package com.smashspot.courtorder.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smashspot.courtorder.model.CourtOrderService;
import com.smashspot.courtorder.model.CourtOrderVO;

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
		System.out.println("接收到 requestOrder: " + requestOrder); 
		// 呼叫 Service 做新增
		CourtOrderVO saved = courtOrderSvc.createOrderAndUpdateReservationTime(requestOrder);
		return saved;
	}


}
