package com.smashspot.favoriteproduct.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smashspot.favoriteproduct.model.FavoriteProductService;
import com.smashspot.favoriteproduct.model.FavoriteProductVO;
import com.smashspot.member.model.MemberVO;
import com.smashspot.product.model.ProductService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Controller
@RequestMapping("/client/favorite")
public class FavoriteProductController {
	
	@Autowired
    private FavoriteProductService favoriteProductService;
    
    @Autowired
    private ProductService productService;
    
    // 獲取收藏列表頁面
    @GetMapping("/list")
    public String listFavorites(Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        
        Set<FavoriteProductVO> favorites = favoriteProductService.getFavorites(loginMember.getMemid());
        model.addAttribute("favorites", favorites);
        return "back-end/client/product/favoriteList";
    }
    
    // 添加收藏
    @PostMapping("/add/{productId}")
    @ResponseBody
    public ResponseEntity<?> addFavorite(@PathVariable Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return ResponseEntity.ok(response);
        }

        boolean result = favoriteProductService.addFavorite(loginMember.getMemid(), productId);
        response.put("success", result);
        response.put("message", result ? "成功加入收藏" : "加入收藏失敗");
        
        return ResponseEntity.ok(response);
    }
    
    // 移除收藏
    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<?> removeFavorite(@PathVariable Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return ResponseEntity.ok(response);
        }

        boolean result = favoriteProductService.removeFavorite(loginMember.getMemid(), productId);
        response.put("success", result);
        response.put("message", result ? "成功移除收藏" : "移除收藏失敗");
        
        return ResponseEntity.ok(response);
    }
    
    // 檢查商品是否已收藏
    @GetMapping("/check/{productId}")
    @ResponseBody
    public ResponseEntity<?> checkFavorite(@PathVariable Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            response.put("isFavorite", false);
            return ResponseEntity.ok(response);
        }

        boolean isFavorite = favoriteProductService.isFavorite(loginMember.getMemid(), productId);
        response.put("isFavorite", isFavorite);
        
        return ResponseEntity.ok(response);
    }

}
