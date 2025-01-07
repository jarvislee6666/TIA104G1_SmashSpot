package com.smashspot.courtorder.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smashspot.courtorder.model.CourtOrderService;
import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.member.model.MemberVO;

@Controller
@RequestMapping("/member")
public class CourtOrderPageController {

    @Autowired
    private CourtOrderService courtOrderSvc;

    @GetMapping("/appointment-records")
    public String showAppointmentRecord(HttpSession session, Model model) {
        // 1) 從 Session 取出目前登入的會員物件
        MemberVO loginUser = (MemberVO) session.getAttribute("login");
        if (loginUser == null) {
            // 尚未登入 -> 你可以選擇導回登入頁
            return "redirect:/member/login"; 
        }
        
        // 2) 拿會員ID (假設 MemberVO 有一個 getMemid() )
        Integer memId = loginUser.getMemid();
        model.addAttribute("memId", memId);
        // 1) 從 Service 查詢資料 (假設會員ID固定測試 = 2，或動態取得)
        List<CourtOrderVO> orders = courtOrderSvc.findOrdersWithDetailsByMemberId(memId);

        // 2) 放入 model
        model.addAttribute("orders", orders);
        

        // 3) 返回 thymeleaf 模板名稱(不含 .html 副檔名)
        return "/back-end/member/AppointmentRecord";
    }
}

