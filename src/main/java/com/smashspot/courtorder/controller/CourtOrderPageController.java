package com.smashspot.courtorder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smashspot.courtorder.model.CourtOrderService;
import com.smashspot.courtorder.model.CourtOrderVO;

@Controller
@RequestMapping("/court-order")
public class CourtOrderPageController {

    @Autowired
    private CourtOrderService courtOrderSvc;

    @GetMapping("/appointmentRecord")
    public String showAppointmentRecord(Model model) {
        // 1) 從 Service 查詢資料 (假設會員ID固定測試 = 2，或動態取得)
        List<CourtOrderVO> orders = courtOrderSvc.findOrdersWithDetailsByMemberId(2);

        // 2) 放入 model
        model.addAttribute("orders", orders);

        // 3) 返回 thymeleaf 模板名稱(不含 .html 副檔名)
        return "/back-end/member/appointmentRecord";
    }
}

