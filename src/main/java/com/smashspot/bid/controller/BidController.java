package com.smashspot.bid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.smashspot.bid.model.*;
import com.smashspot.member.model.MemberVO;
import com.smashspot.orders.model.OrdersService;
import com.smashspot.orders.model.OrdersVO;
import com.smashspot.product.model.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BidController {
    
    @Autowired
    private BidService bidService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private BidWebSocketController bidWebSocketController;
    
    @GetMapping("/member/biddingList")
    public String listBiddingProduct(Model model, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("login");
        if (member == null) {
            return "redirect:/member/login";
        }

        // 獲取狀態為1(競標中)和2(已結標)的商品
        List<ProductVO> activeAndEndedProducts = new ArrayList<>();
        activeAndEndedProducts.addAll(productService.findByBidsta(1)); // 競標中的商品
        activeAndEndedProducts.addAll(productService.findByBidsta(2)); // 已結標的商品
        
        // 建立結果列表
        List<Map<String, Object>> biddingList = new ArrayList<>();
        
        // 遍歷每個商品,檢查會員是否有出價
        for (ProductVO product : activeAndEndedProducts) {
        	// 檢查該商品是否已有完成付款的訂單
            List<OrdersVO> orders = ordersService.findByProduct(product.getProid());
            boolean hasCompletedOrder = orders.stream()
                    .anyMatch(order -> order.getOrdstaid() == 2 || order.getOrdstaid() == 3 || order.getOrdstaid() == 4);
                
            // 如果商品已經完成付款，則跳過不顯示
            if (hasCompletedOrder) {
                continue;
            }
        	
        	
        	
            List<BidVO> memberBids = bidService.getMemberBidsForProduct(member.getMemid(), product.getProid());
            
            if (!memberBids.isEmpty()) {
                // 取得該會員對此商品的最高出價
                Integer highestBid = memberBids.stream()
                        .map(BidVO::getBidamt)
                        .max(Integer::compareTo)
                        .orElse(0);
                        
                // 取得商品當前最高價
                Integer currentHighestBid = product.getMaxprice();
                
                // 檢查是否為最高出價者
                boolean isHighestBidder = highestBid.equals(currentHighestBid);
                
                Map<String, Object> bidInfo = new HashMap<>();
                bidInfo.put("product", product);
                bidInfo.put("myHighestBid", highestBid);
                bidInfo.put("currentHighestBid", currentHighestBid);
                bidInfo.put("isHighestBidder", isHighestBidder);
                bidInfo.put("bidStatus", product.getBidstaid()); // 添加商品狀態
                
                biddingList.add(bidInfo);
            }
        }
        
        model.addAttribute("biddingList", biddingList);
        return "back-end/member/biddingList";
    }
    
    @GetMapping("/client/bid/check-login")
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
    
    @PostMapping("/client/bid/place")
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
            
            // 使用WebSocket廣播更新
            bidWebSocketController.broadcastBidUpdate(productId, bidAmount, bidHistory);
            
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
    
    @GetMapping("/client/bid/history/{productId}")
    public ResponseEntity<?> getBidHistory(@PathVariable Integer productId) {
        try {
            List<BidVO> bidHistory = bidService.getProductBidHistory(productId);
            return ResponseEntity.ok(bidHistory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("獲取競標歷史失敗：" + e.getMessage());
        }
    }
}
