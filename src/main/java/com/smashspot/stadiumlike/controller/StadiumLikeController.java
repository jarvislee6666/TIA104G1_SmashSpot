package com.smashspot.stadiumlike.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.smashspot.member.model.MemberVO;
import com.smashspot.stadiumlike.model.StadiumLikeService;
import com.smashspot.stadiumlike.model.StadiumLikeVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stadium-like")
public class StadiumLikeController {

    @Autowired
    private StadiumLikeService stadiumLikeService;

    // (A) 列出會員收藏
    @GetMapping("/list")
    public String listLikes(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        List<StadiumLikeVO> userLikes = stadiumLikeService.getUserLikes(loginMember.getMemid());
        model.addAttribute("userLikes", userLikes);
        return "stadiumLikeList";
    }

    // (B) 新增收藏 (AJAX)
    @PostMapping("/add/{stdmId}")
    @ResponseBody
    public Map<String, Object> addLike(@PathVariable Integer stdmId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return response;
        }
        boolean result = stadiumLikeService.addLike(loginMember.getMemid(), stdmId);
        response.put("success", result);
        response.put("message", result ? "成功收藏" : "可能已收藏或場館不存在");
        return response;
    }

    // (C) 取消收藏 (AJAX)
    @DeleteMapping("/remove/{stdmId}")
    @ResponseBody
    public Map<String, Object> removeLike(@PathVariable Integer stdmId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return response;
        }
        boolean result = stadiumLikeService.removeLike(loginMember.getMemid(), stdmId);
        response.put("success", result);
        response.put("message", result ? "成功取消收藏" : "刪除失敗 (可能不存在)");
        return response;
    }

    // (D) 檢查是否已收藏
    @GetMapping("/check/{stdmId}")
    @ResponseBody
    public Map<String, Object> checkLike(@PathVariable Integer stdmId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        boolean isLiked = false;
        if (loginMember != null) {
            isLiked = stadiumLikeService.isLiked(loginMember.getMemid(), stdmId);
        }
        response.put("isLiked", isLiked);
        return response;
    }
    
    @GetMapping("/my-list")
    public String myLikeList(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            // 如果尚未登入，跳去登入頁
            return "redirect:/member/login";
        }
        
        // 1) 查出該會員的收藏 (回傳 List<StadiumLikeVO>)
        List<StadiumLikeVO> likeList = stadiumLikeService.getUserLikes(loginMember.getMemid());

        // 2) 可能還要把 `stdmId` -> `StadiumVO` 轉成一個可顯示名稱的資料結構
        //    這裡示範簡單做法：直接在 service 做 left join，或 
        //    只存 stdmId，再手動去 StadiumService 查出 stadiumName
        //    => 依你的系統實作不同而定

        model.addAttribute("likeList", likeList);
        return "myFavoriteList";  // Thymeleaf 頁面
    }
}
