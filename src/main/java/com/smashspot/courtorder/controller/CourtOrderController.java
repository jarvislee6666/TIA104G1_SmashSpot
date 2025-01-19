package com.smashspot.courtorder.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.smashspot.courtorder.model.CourtOrderService;
import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.member.model.MemberVO;
import com.smashspot.member.model.MemberService;

@RestController
@RequestMapping("/court-order")
public class CourtOrderController {

	@Autowired
	private CourtOrderService courtOrderSvc;
	
	@Autowired
	private MemberService memberSvc;

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
	    
	    Map<String, String> fieldErrors = new HashMap<>(); // 用來收集錯誤訊息

	    // 前端送來的 JSON 裡，key 就是 "cardNumber", "cardExpiry", "cardCvv", "cardHolder"
	    String cardNumber = formData.get("cardNumber");
	    String cardExpiry = formData.get("cardExpiry");
	    String cardCvv = formData.get("cardCvv");
	    String cardHolder = formData.get("cardHolder");

	    // 驗證卡號
	    if (!isValidCardNumber(cardNumber)) {
	    	fieldErrors.put("cardNumber","信用卡號格式錯誤！");
	    }
	    // 驗證有效期限
	    if (!isValidExpiry(cardExpiry)) {
	    	fieldErrors.put("cardExpiry","信用卡有效期限錯誤！");
	    }
	    // 驗證 CVV
	    if (!isValidCvv(cardCvv)) {
	    	fieldErrors.put("cardCvv","CVV 格式錯誤！");
	    }
	    // 驗證持卡人姓名 (如需)
	    if (cardHolder == null || cardHolder.trim().isEmpty()) {
	    	fieldErrors.put("cardHolder","持卡人姓名不可空白！");
	    }

	    if (!fieldErrors.isEmpty()) {
	        // 有錯誤 => 回傳所有錯誤訊息
	        result.put("success", false);
	        result.put("fieldErrors", fieldErrors);
	    } else {
	        // 若皆通過
	        result.put("success", true);
	        result.put("message", "信用卡驗證通過");
	    }

	    return result;
	}

	private boolean isValidCardNumber(String cardNo) {
	    return cardNo != null && cardNo.matches("\\d{16}");
	}
	private boolean isValidExpiry(String expiry) {
	    return expiry != null && expiry.matches("\\d{2}/\\d{2}");
	}
	private boolean isValidCvv(String cvv) {
	    return cvv != null && cvv.matches("\\d{3}");
	}
	
    // 取得該會員的所有訂單 (含明細)
    @GetMapping("/my-orders/{memid}")
    public List<CourtOrderVO> getMyOrders(@PathVariable("memid") Integer memid) {
        // 呼叫 Service 拿資料
        List<CourtOrderVO> list = courtOrderSvc.findOrdersWithDetailsByMemberId(memid);
        // 直接回傳 CourtOrderVO，其中有 Set<CourtOrderDetailVO>
        
        // 對每一筆訂單的明細進行日期排序
        for (CourtOrderVO order : list) {
            if (order.getCourtOrderDetail() != null) {
            	// 假設原本是 Set<CourtOrderDetailVO>
            	List<CourtOrderDetailVO> sortedDetails = new ArrayList<>(order.getCourtOrderDetail());

            	// 排序
            	sortedDetails.sort((d1, d2) -> {
            	    // 如果 ordDate 是字串且能用 compareTo() 比較 (例如 yyyy-MM-dd 格式)
            	    return d1.getOrdDate().compareTo(d2.getOrdDate());
            	});

            	// 轉回 Set，使用 LinkedHashSet 保留排序後的插入順序
            	Set<CourtOrderDetailVO> sortedSet = new LinkedHashSet<>(sortedDetails);

            	// 再塞回去 entity
            	order.setCourtOrderDetail(sortedSet);
            }
        }
        return list;
    }
    
    /**
     * 1) 更新評價與留言
     */
    @PatchMapping("/review/{courtordid}")
    public Map<String, Object> updateReview(
        @PathVariable("courtordid") Integer courtordid, 
        @RequestBody CourtOrderVO requestBody
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 取得前端傳入的評價、留言
            Integer starrank = requestBody.getStarrank();  
            String message   = requestBody.getMessage();

            // 呼叫 Service 做更新
            courtOrderSvc.updateReview(courtordid, starrank, message);

            result.put("success", true);
            result.put("message", "評價更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 2) 取消預約 (編輯狀態 + 取消原因)
     */
    @PatchMapping("/cancel/{courtordid}")
    public Map<String, Object> cancelOrder(
        @PathVariable("courtordid") Integer courtordid, 
        @RequestBody CourtOrderVO requestBody
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean ordsta    = requestBody.getOrdsta();     // false
            String canreason  = requestBody.getCanreason();  // 取消原因

            // 呼叫 Service 做更新
            courtOrderSvc.cancelOrder(courtordid, ordsta, canreason);

            result.put("success", true);
            result.put("message", "訂單已取消");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/avatar/{memid}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Integer memid) {
        MemberVO memberVO = memberSvc.getOneMember(memid);
        if (memberVO != null && memberVO.getMempic() != null) {
            byte[] avatarBytes = memberVO.getMempic();

            // 檢查圖片格式 (假設都存 JPEG，就直接用 image/jpeg)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            
            return new ResponseEntity<>(avatarBytes, headers, HttpStatus.OK);
        }
        // 如果找不到，回傳 404
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/my-orders-search")
    public List<CourtOrderVO> searchMyOrders(
        @RequestParam Integer memId,
        @RequestParam(required=false) String stadiumName,
        @RequestParam(required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date ordDate,
        @RequestParam(required=false) Boolean ordsta
    ){
        if (stadiumName != null && stadiumName.trim().isEmpty()) {
            stadiumName = null;
        }
        System.out.println("檢查參數: memId="+memId+", stadiumName="+stadiumName+", ordDate="+ordDate+", ordsta="+ordsta);
        // 同理 date, status 如果想手動處理，也可判斷
        // 但 date 若是空字串，Spring 可能會 parse 失敗 => 進不來
        // ordsta 若是空字串，也可能 parse 失敗 => 進不來

        return courtOrderSvc.searchMyOrders(memId, stadiumName, ordDate, ordsta);
    }


    
}
