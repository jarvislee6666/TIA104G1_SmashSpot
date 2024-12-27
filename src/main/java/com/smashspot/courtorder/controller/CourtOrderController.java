package com.smashspot.courtorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.courtorder.model.CourtOrderService;

@RestController
@RequestMapping("/court-order")
public class CourtOrderController {

    @Autowired
    private CourtOrderService courtOrderSvc;

    /**
     * 新增訂單
     */
    @PostMapping
    public CourtOrderVO createOrder(@RequestBody CourtOrderVO requestOrder) {
        // 呼叫 Service 做新增
        CourtOrderVO saved = courtOrderSvc.createOrder(requestOrder);
        return saved;
    }
}
 