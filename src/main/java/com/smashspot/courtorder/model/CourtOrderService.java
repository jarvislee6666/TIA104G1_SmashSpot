package com.smashspot.courtorder.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.smashspot.courtorderdetail.model.CourtOrderDetailRepository;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmRepository;

@Service("CourtOrderService")
public class CourtOrderService {

	
    private  CourtOrderRepository courtOrderRepository;
    private  CourtOrderDetailRepository courtOrderDetailRepository;
    private  StdmRepository stadiumRepository;
    
	public CourtOrderService(CourtOrderRepository courtOrderRepository,
			CourtOrderDetailRepository courtOrderDetailRepository, StdmRepository stadiumRepository) {
		this.courtOrderRepository = courtOrderRepository;
		this.courtOrderDetailRepository = courtOrderDetailRepository;
		this.stadiumRepository = stadiumRepository;
	}
	
    /**
     * 新增訂單 (只要傳 CourtOrderVO 進來即可)
     */
    public CourtOrderVO createOrder(CourtOrderVO requestOrder) {
        // 1. 先根據前端傳過來的 stadiumId, 撈出 DB 的 StadiumVO
        Integer stadiumId = requestOrder.getStadium().getStdmId();
        StadiumVO stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new RuntimeException("Stadium not found, id=" + stadiumId));

        // 2. 設定訂單資訊
        CourtOrderVO order = new CourtOrderVO();
        order.setMemid(requestOrder.getMemid());

        // 場館
        order.setStadium(stadium);

        // 訂單狀態一律為 true
        order.setOrdsta(true);

        // 訂單成立時間=現在時間
        order.setOrdcrttime(Timestamp.from(Instant.now()));

        // 取消原因、評論留言、星等先不存
        order.setCanreason(null);
        order.setMesssag(null);
        order.setStarrank(null);

        // 3. 處理明細
        Set<CourtOrderDetailVO> details = requestOrder.getCourtOrderDetail();
        if (details != null && !details.isEmpty()) {
            // 把每一筆 detail 都跟 order 綁定
            for (CourtOrderDetailVO detail : details) {
                // 綁定關聯
                detail.setCourtOrder(order);
            }
            // 設到 order
            order.setCourtOrderDetail(details);
        }

        // 4. 計算 totamt：1個 '1' 表示一個時段，先統計所有明細中 '1' 的總數，再 * 場館價格
        int totalSlots = 0;
        if (details != null) {
            for (CourtOrderDetailVO detail : details) {
                String ordTime = detail.getOrdTime(); // e.g. "xxx11000000x"
                if (ordTime != null) {
                    // 數每個 '1'
                    for (char c : ordTime.toCharArray()) {
                        if (c == '1') {
                            totalSlots++;
                        }
                    }
                }
            }
        }
        // 場館單價 * 佔用的時段數
        order.setTotamt(stadium.getCourtPrice() * totalSlots);

        // 5. 存入 DB (因為 cascade = ALL，會一併存明細)
        return courtOrderRepository.save(order);
    }
    


}
