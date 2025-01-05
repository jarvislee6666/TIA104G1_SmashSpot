package com.smashspot.coupon.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.smashspot.coupon.model.CouponService;
import com.smashspot.coupon.model.CouponVO;

@RestController
@RequestMapping("/api/public/coupon")
public class PublicCouponController {
    
    @Autowired
    CouponService copSvc;
    
    //結帳時驗證優惠碼
    @PostMapping("/validate")
  	@ResponseBody
  	public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam String code, @RequestParam Integer originalPrice) {
  	    
  		Map<String, Object> response = new HashMap<>();
  	    
  		try {
              CouponVO coupon = copSvc.findByCopcode(code);
              
              if (coupon == null) {
                  response.put("valid", false);
                  response.put("message", "優惠碼不存在");
                  return ResponseEntity.ok(response);
              }
              
              Date currentDate = new Date();
              if (currentDate.after(coupon.getEnddate())) {
                  response.put("valid", false);
                  response.put("message", "優惠碼已過期");
                  return ResponseEntity.ok(response);
              }
              
              // 計算折扣後金額
              int discountedPrice = originalPrice - coupon.getDiscount();
              if (discountedPrice < 0) {
                  discountedPrice = 0;
              }
              
              response.put("valid", true);
              response.put("message", "優惠碼套用成功");
              response.put("discount", coupon.getDiscount());
              response.put("discountedPrice", discountedPrice);
              response.put("copid", coupon.getCopid());
              
              return ResponseEntity.ok(response);
              
          } catch (Exception e) {
              response.put("valid", false);
              response.put("message", "系統錯誤，請稍後再試");
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
          }
  	}
}