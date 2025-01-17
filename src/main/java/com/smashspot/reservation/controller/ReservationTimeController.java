package com.smashspot.reservation.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.member.model.MemberVO;
import com.smashspot.member.model.MemberService;
import com.smashspot.reservationtime.model.ReservationTimeService;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;
import com.smashspot.courtorder.model.CourtOrderService;

@Controller
@RequestMapping("/reservation")
public class ReservationTimeController {

    @Autowired
    private ReservationTimeService reservationTimeService;
    
    @Autowired
    private StdmService stdmService;
    
	@Autowired
	private CourtOrderService courtOrderSvc;
	
	@Autowired
	private MemberService memberSvc;

    @GetMapping("/week")
    public String getWeeklyReservation(
        @RequestParam("stdmId") Integer stdmId,
        @RequestParam(value = "week", defaultValue = "0") Integer week,
        Model model, HttpSession session
    ) {

    	
        // 1) 查詢對應的場館資料
        StadiumVO stadium = stdmService.getOneStdm(stdmId);
        
        model.addAttribute("stadiumVO", stadium);

        // (C) 檢查場館是否已啟用 (選擇性)
        /*
        if (stadium == null || !Boolean.TRUE.equals(stadium.getOprSta())) {
            model.addAttribute("error", "該場館不存在或目前不開放營運");
            return "back-end/client/reservationtime/courtReservation";
        }
        */

        // (D) 查詢「每個場館的最後營業日」，拿到 Map<Integer, LocalDate>
        Map<Integer, LocalDate> lastDatesMap = reservationTimeService.getLastDatesForEachStadium();
        // 拿到本場館對應的最後營業日
        LocalDate closingDate = lastDatesMap.get(stdmId);
        // 丟到 Model 裡
        model.addAttribute("closingDate", closingDate);
        


        
        model.addAttribute("stadiumVO", stadium);
        
        // 濾器/攔截器已確保使用者必須登入才能進到這裡
        MemberVO member = (MemberVO) session.getAttribute("login");
        if (member == null) {
            return "redirect:/member/login";  // 再防一下，萬一還是沒登入
        }

        // 也可以直接放到 model
        model.addAttribute("member", member);
        
        
        
        // 檢查 week 範圍
        if (week < 0) week = 0;
        if (week > 4) week = 4;

        // 設置今天日期
        Date today = new Date();
        model.addAttribute("today", today);
        
        // 設置28天後的日期限制
        Calendar futureLimit = Calendar.getInstance();
        futureLimit.setTime(today);
        futureLimit.add(Calendar.DAY_OF_MONTH, 28);
        model.addAttribute("futureLimit", futureLimit.getTime());
        
        // 計算本週日期
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(today);
        startCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        // 調整到指定週
        startCal.add(Calendar.WEEK_OF_YEAR, week);
        Date startDate = startCal.getTime();
        model.addAttribute("startDate", startDate);
        
        // 計算週結束日期
        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DAY_OF_WEEK, 6);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        model.addAttribute("endDate", endCal.getTime());

        // 查詢預約資料
        List<Object[]> rawList = reservationTimeService.findReservationByStdmIdAndWeeks(
            stdmId, 
            week,
            week
        );
        
     // (1) 如果 rawList 不空，從第一筆拿到場館名稱
        //先保留但不使用
        if (rawList != null && !rawList.isEmpty()) {
            Object[] firstRow = rawList.get(0);
            // 索引 1 處就是您在 raw SQL/JPQL Query 裡 SELECT 的第二個欄位 (matchingRow[1])
            String stadiumName = (String) firstRow[1]; 
            model.addAttribute("stdmName", stadiumName);
        }

        // 確保有完整的七天資料
        List<Map<String, Object>> reservationList = new ArrayList<>();
        Calendar tempCal = (Calendar) startCal.clone();
        
        for (int i = 0; i < 7; i++) {
            // 尋找對應日期的資料
            Date currentDate = tempCal.getTime();
            Object[] matchingRow = findMatchingRow(rawList, currentDate);
            
            Map<String, Object> map = new HashMap<>();
            map.put("date", currentDate);
            
            if (matchingRow != null) {
                String rsvAvaStr = (String) matchingRow[3];
                String bookedStr = (String) matchingRow[4];
                map.put("rsvAva", rsvAvaStr);
                map.put("booked", bookedStr);
                map.put("leftover", computeLeftover(rsvAvaStr, bookedStr));
                map.put("stdmId", matchingRow[0]);
                map.put("stdmname", matchingRow[1]);
            } else {
                map.put("rsvAva", "xxxxxxxxxxxx");
                map.put("booked", "xxxxxxxxxxxx");
                map.put("leftover", "xxxxxxxxxxxx");
            }
            
            reservationList.add(map);
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // 3. 撈該場館的評價列表
        List<CourtOrderVO> reviewList = courtOrderSvc.findReviewsByStadiumId(stdmId);

        // 直接呼叫 Service 計算平均星等
        double averageRating = courtOrderSvc.calculateAverageRatingForStadium(stdmId);
        // 直接呼叫 Service 計算總留言數
        int sumMessage = courtOrderSvc.calculateSumMessageForStadium(stdmId);


        // 4. 放到 model，給 Thymeleaf 用
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalReviews", sumMessage);
        model.addAttribute("stdmId", stdmId);
        model.addAttribute("week", week);
        model.addAttribute("reservationList", reservationList);
        


        return "back-end/client/reservationtime/courtReservation";
    }

    private Object[] findMatchingRow(List<Object[]> rawList, Date targetDate) {
        if (rawList == null || rawList.isEmpty()) return null;
        
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        
        for (Object[] row : rawList) {
            Date rowDate = (Date) row[2];
            cal1.setTime(targetDate);
            cal2.setTime(rowDate);
            
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                return row;
            }
        }
        
        return null;
    }

    private String computeLeftover(String rsvAvaStr, String bookedStr) {
        if (rsvAvaStr == null || bookedStr == null) {
            return null;
        }
        if (rsvAvaStr.length() != 12 || bookedStr.length() != 12) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            char ava = rsvAvaStr.charAt(i);
            char bkd = bookedStr.charAt(i);

            if (ava == 'x') {
                sb.append('x');
            } else {
                int valAva = ava - '0';
                int valBkd = bkd - '0';
                int leftover = valAva - valBkd;
                if (leftover < 0) leftover = 0;
                sb.append(leftover);
            }
        }
        return sb.toString();
    }
    


    
}