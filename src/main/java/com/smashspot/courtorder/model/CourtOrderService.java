package com.smashspot.courtorder.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.courtorderdetail.model.CourtOrderDetailRepository;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.reservationtime.model.ReservationTimeRepository;
import com.smashspot.reservationtime.model.ReservationTimeVO;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmRepository;

@Service
@Transactional
public class CourtOrderService {

	
//    private  CourtOrderRepository courtOrderRepository;
//    private  CourtOrderDetailRepository courtOrderDetailRepository;
//    private  StdmRepository stadiumRepository;
//    
//	public CourtOrderService(CourtOrderRepository courtOrderRepository,
//			CourtOrderDetailRepository courtOrderDetailRepository, StdmRepository stadiumRepository) {
//		this.courtOrderRepository = courtOrderRepository;
//		this.courtOrderDetailRepository = courtOrderDetailRepository;
//		this.stadiumRepository = stadiumRepository;
//	}
//	
    @Autowired
    private CourtOrderRepository courtOrderRepository;
    @Autowired
    private CourtOrderDetailRepository courtOrderDetailRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private StdmRepository stadiumRepository;
    /**
     * 新增訂單 (只要傳 CourtOrderVO 進來即可)
     */
	
    public CourtOrderVO createOrderAndUpdateReservationTime(CourtOrderVO requestOrder) {
    	
        // 1. 先根據前端傳過來的 stadiumId, 撈出 DB 的 StadiumVO
        Integer stdmId = requestOrder.getStadium().getStdmId();
        StadiumVO stadium = stadiumRepository.findById(stdmId)
                .orElseThrow(() -> new RuntimeException("Stadium not found, id=" + stdmId));

        // 2. 設定訂單資訊
        CourtOrderVO newOrder = new CourtOrderVO();
        newOrder.setMemid(requestOrder.getMemid());
        newOrder.setStadium(stadium);
        newOrder.setOrdsta(true);
        newOrder.setOrdcrttime(new Timestamp(System.currentTimeMillis()));

        // 3. 處理明細
        Set<CourtOrderDetailVO> details = requestOrder.getCourtOrderDetail();
        if (details != null && !details.isEmpty()) {
            // 把每一筆 detail 都跟 order 綁定
            for (CourtOrderDetailVO detail : details) {
                // 綁定關聯
                detail.setCourtOrder(newOrder);
            }
            // 設到 order
            newOrder.setCourtOrderDetail(details);
        }

//        if (details != null && !details.isEmpty()) {
//            // 把每一筆 detail 都跟 order 綁定
//            for (CourtOrderDetailVO detail : details) {
//                // 綁定關聯
//                detail.setCourtOrder(newOrder);
//            }
//            // 設到 order
//            newOrder.setCourtOrderDetail(details);
//        }

        // 4) 計算總金額 (依你規則，這裡示範: 場館價格 * 總時段數)
        int totalTimeSlots = 0;
        if (details != null) {
            for (CourtOrderDetailVO d : details) {
                totalTimeSlots += countNumberOfOnes(d.getOrdTime());
            }
        }
        newOrder.setTotamt(stadium.getCourtPrice() * totalTimeSlots);
        
        // 5) 在儲存訂單前，先**檢查**對應日期時段的庫存是否足夠
        //    若不足，直接丟異常 => rollback
        if (!checkInventoryAndUpdateIfOK(stdmId, details)) {

            throw new RuntimeException("庫存不足: 場地已被訂滿");
        }

        // 6) 最後存入資料庫 (因為 cascade=ALL，明細也會跟著存)
        return courtOrderRepository.save(newOrder);
    }
    /**
     * 檢查所有 detail 是否足夠庫存；若足夠，順便更新 reservation_time.booked
     */
    private boolean checkInventoryAndUpdateIfOK(
            Integer stdmId, 
            Set<CourtOrderDetailVO> details
    ) {
        // 逐筆 detail
        for (CourtOrderDetailVO detail : details) {
            Date ordDate = detail.getOrdDate(); // e.g. 2024-12-27
            String ordTime = detail.getOrdTime(); // e.g. "xxxx1000100x"
            if (ordTime == null || ordTime.length() != 12 || !ordTime.matches("[0-1x]{12}")) {
                throw new RuntimeException("訂單時段格式不正確: " + ordTime);
            }

            // 找出對應 reservation_time
            ReservationTimeVO rsvTime = reservationTimeRepository
                .findByStadium_StdmIdAndDates(stdmId, ordDate);
            if (rsvTime == null) {
                // 代表還沒有對應那天的資料，或查不到
                return false;
            }

            // 拿到 rsv_ava & booked
            String rsvAva = rsvTime.getRsvava();  // e.g. "xxxx7777777x"
            String booked = rsvTime.getBooked();  // e.g. "xxxx0000100x"

            if (!isEnough(rsvAva, booked, ordTime)) {
                return false; // 只要有一筆時段不足，就整筆訂單失敗
            }

            // 若足夠，就合併
            String newBooked = mergeBooked(booked, ordTime);
            rsvTime.setBooked(newBooked);
            // 更新 DB
            reservationTimeRepository.save(rsvTime);
        }
        return true;
    }

    /**
     * 檢查某日/某時段 ordTime 是否在 rsv_ava - booked 後還有足夠的剩餘 (至少 1)
     */
    private boolean isEnough(String rsvAva, String booked, String ordTime) {
        // 若 rsvAva = "xxxx7777777x", booked= "xxxx0012300x", ordTime= "xxxx0010000x"
        // => 中間 7 碼( index=4..10 ) 分別做檢查
        // 只要 ordTime[i]=='1' 就代表該時段要 +1
        // => leftover = rsvAva[i] - booked[i]
        // => leftover >= 1 => ok
        for (int i = 0; i < 12; i++) {
            char cAva = rsvAva.charAt(i);
            char cBkd = booked.charAt(i);
            char cOrd = ordTime.charAt(i);

            // 遇到 'x' 直接跳過
            if (cAva == 'x') continue; 
            int avaVal = cAva - '0';
            int bkdVal = cBkd - '0';
            int ordVal = cOrd - '0';
            
            // 調試輸出
            System.out.printf("時段: %d, 可容納: %d, 已預訂: %d, 需求: %d%n", i, avaVal, bkdVal, ordVal);
            System.out.printf(
            		  "時段=%d, rsvAva=%c, booked=%c, ordTime=%c => avaVal=%d, bkdVal=%d, ordVal=%d, leftover=%d%n",
            		  i, cAva, cBkd, cOrd, avaVal, bkdVal, ordVal, (avaVal-bkdVal)
            		);


            if (ordVal > 0) {
                // 需求 = ordVal(1) <= leftover ?
                int leftover = avaVal - bkdVal;
                if (leftover < ordVal) {
                    // 不足
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 合併: booked + ordTime
     * ordTime 若在 i 位='1' => booked[i] += 1
     */
    private String mergeBooked(String oldBooked, String ordTime) {
        char[] arr = oldBooked.toCharArray();
        for (int i = 0; i < 12; i++) {
            if (arr[i] == 'x') continue; // 不開放
            int oldVal = arr[i] - '0';
            int addVal = ordTime.charAt(i) - '0';
            int sum = oldVal + addVal;
            if (sum > 9) sum = 9; // 以防爆字元
            arr[i] = (char) (sum + '0');
        }
        return new String(arr);
    }

    /**
     * 計算 ordTime 裡 '1' 的數量
     */
    private int countNumberOfOnes(String ordTime) {
        int cnt = 0;
        if (ordTime != null) {
            for (char c : ordTime.toCharArray()) {
                if (c == '1') cnt++;
            }
        }
        return cnt;
    }
    


}
