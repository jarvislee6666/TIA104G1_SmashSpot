package com.smashspot.courtorder.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
//	@PostMapping
//	public CourtOrderVO createOrder(@RequestBody CourtOrderVO requestOrder) {
//		System.out.println("接收到 requestOrder: " + requestOrder); 
//		// 呼叫 Service 做新增
//		CourtOrderVO saved = courtOrderSvc.createOrderAndUpdateReservationTime(requestOrder);
//		return saved;
//	}
	@PostMapping
	public Map<String, Object> createOrder(@RequestBody CourtOrderVO requestOrder) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        CourtOrderVO saved = courtOrderSvc.createOrderAndUpdateReservationTime(requestOrder);
	        // 成功
	        result.put("success", true);
	        result.put("courtordid", saved.getCourtordid());
	        result.put("totamt", saved.getTotamt());
	    } catch (RuntimeException e) {
	        // 失敗 (包含庫存不足或其他)
	        result.put("success", false);
	        result.put("message", e.getMessage());  // "庫存不足: 場地已被訂滿"
	    }
	    return result;
	}
	
	@PostMapping("/payment-check")
	@ResponseBody
	public Map<String, Object> verifyPayment(@RequestBody Map<String, String> formData) {
	    Map<String, Object> result = new HashMap<>();
	    
	    List<String> errors = new ArrayList<>(); // 用來收集錯誤訊息

	    // 前端送來的 JSON 裡，key 就是 "cardNumber", "cardExpiry", "cardCvv", "cardHolder"
	    String cardNumber = formData.get("cardNumber");
	    String cardExpiry = formData.get("cardExpiry");
	    String cardCvv = formData.get("cardCvv");
	    String cardHolder = formData.get("cardHolder");

	    // 驗證卡號
	    if (!isValidCardNumber(cardNumber)) {
	        errors.add("信用卡號格式錯誤！");
	    }
	    // 驗證有效期限
	    if (!isValidExpiry(cardExpiry)) {
	        errors.add("信用卡有效期限錯誤！");
	    }
	    // 驗證 CVV
	    if (!isValidCvv(cardCvv)) {
	        errors.add("CVV 格式錯誤！");
	    }
	    // 驗證持卡人姓名 (如需)
	    if (cardHolder == null || cardHolder.trim().isEmpty()) {
	        errors.add("持卡人姓名不可空白！");
	    }

	    if (!errors.isEmpty()) {
	        // 有錯誤 => 回傳所有錯誤訊息
	        result.put("success", false);
	        result.put("messages", errors);
	    } else {
	        // 若皆通過
	        result.put("success", true);
	        result.put("message", "信用卡驗證通過");
	    }

	    return result;
	}

	// 簡單示範
	private boolean isValidCardNumber(String cardNo) {
	    return cardNo != null && cardNo.matches("\\d{16}");
	}
	private boolean isValidExpiry(String expiry) {
	    return expiry != null && expiry.matches("\\d{2}/\\d{2}");
	}
	private boolean isValidCvv(String cvv) {
	    return cvv != null && cvv.matches("\\d{3}");
	}



}
