package com.smashspot.bid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smashspot.bid.model.*;
import com.smashspot.member.model.MemberVO;
import com.smashspot.product.model.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bid")
public class BidController {
    
    @Autowired
    private BidService bidService;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/check-login")
    public ResponseEntity<?> checkLoginStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        MemberVO member = (MemberVO) session.getAttribute("login");
        response.put("isLoggedIn", member != null);
        
        if (member == null) {
            response.put("redirect", "/member/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/place")
    public ResponseEntity<?> placeBid(
            @RequestParam Integer productId,
            @RequestParam Integer bidAmount,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 檢查用戶是否登入
        MemberVO member = (MemberVO) session.getAttribute("login");
        if (member == null) {
            response.put("success", false);
            response.put("error", "請先登入");
            response.put("redirect", "/member/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // 獲取商品信息
        ProductVO product = productService.getOneProduct(productId);
        if (product == null) {
            response.put("success", false);
            response.put("error", "商品不存在");
            return ResponseEntity.ok(response);
        }
        
        // 檢查是否為商品擁有者
        if (member.getMemid().equals(product.getMemberVO().getMemid())) {
            response.put("success", false);
            response.put("error", "不能對自己的商品出價");
            return ResponseEntity.ok(response);
        }
        
        // 檢查出價是否有效
        if (!bidService.isValidBid(productId, bidAmount)) {
            response.put("success", false);
            response.put("error", "出價必須高於目前最高價");
            return ResponseEntity.ok(response);
        }
        
        try {
            // 創建競標記錄
            BidVO bid = bidService.createBid(member.getMemid(), productId, bidAmount);
            
            // 更新商品最高價
            product.setMaxprice(bidAmount);
            productService.updateProduct(product);
            
            // 獲取更新後的競標歷史
            List<BidVO> bidHistory = bidService.getProductBidHistory(productId);
            
            response.put("success", true);
            response.put("currentHighestBid", bidAmount);
            response.put("bidHistory", bidHistory);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "競標失敗：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/history/{productId}")
    public ResponseEntity<?> getBidHistory(@PathVariable Integer productId) {
        try {
            List<BidVO> bidHistory = bidService.getProductBidHistory(productId);
            return ResponseEntity.ok(bidHistory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("獲取競標歷史失敗：" + e.getMessage());
        }
    }
}
