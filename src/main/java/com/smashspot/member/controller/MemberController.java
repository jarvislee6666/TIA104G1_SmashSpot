package com.smashspot.member.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smashspot.member.model.EmailService;
import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private EmailService emailService;

    /**
     * 顯示註冊頁面
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register.html";
    }

    /**
     * 發送驗證碼到指定email
     */
    @PostMapping("/send-verification")
    public String sendVerification(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            // 檢查Email是否已存在
            if (memberService.getRepository().findByEmail(email) != null) {
                redirectAttributes.addFlashAttribute("error", "Email已被使用");
                return "register.html";
            }
            
            memberService.sendVerificationEmail(email);
            redirectAttributes.addFlashAttribute("message", "驗證碼已發送至您的信箱");
            return "register.html";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "驗證碼發送失敗: " + e.getMessage());
            return "register.html";
        }
    }

    /**
     * 會員註冊處理
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute MemberVO memberVO,
            @RequestParam String verificationCode,
            RedirectAttributes redirectAttributes) {
        try {
            // 檢查帳號是否已存在
            if (memberService.getRepository().findByAccount(memberVO.getAccount()) != null) {
                redirectAttributes.addFlashAttribute("error", "帳號已存在");
                return "login.html";
            }
            
            // 檢查手機號碼是否已存在
            if (memberService.getRepository().findByPhone(memberVO.getPhone()) != null) {
                redirectAttributes.addFlashAttribute("error", "手機號碼已被使用");
                return "login.html";
            }

            // 驗證驗證碼
            if (!memberService.verifyCode(memberVO.getEmail(), verificationCode)) {
                redirectAttributes.addFlashAttribute("error", "驗證碼不正確或已過期");
                return "login.html";
            }

            // 設置註冊時間和修改時間
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            memberVO.setCrttime(currentTime);
            memberVO.setChgtime(currentTime);
            
            // 設置帳號狀態為啟用
            memberVO.setStatus(true);
            
            // 儲存會員資料
            memberService.addMember(memberVO);
            
            // 發送註冊確認信
            emailService.sendRegistrationEmail(memberVO.getEmail(), memberVO.getName());
            
            redirectAttributes.addFlashAttribute("message", "註冊成功，已寄送確認信至您的信箱");
            return "login.html";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "註冊失敗: " + e.getMessage());
            return "login.html";
        }
    }

    /**
     * 顯示登入頁面
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login.html";
    }

    /**
     * 會員登入處理
     */
    @PostMapping("/login")
    public String login(@RequestParam String account, 
                       @RequestParam String password, 
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        try {
            MemberVO member = memberService.getRepository().findByAccount(account);
            
            if (member == null) {
                redirectAttributes.addFlashAttribute("error", "帳號不存在");
                return "login.html";
            }
            
            if (!member.getPassword().equals(password)) {
                redirectAttributes.addFlashAttribute("error", "密碼錯誤");
                return "login.html";
            }
            
            if (!member.getStatus()) {
                redirectAttributes.addFlashAttribute("error", "帳號已被停用");
                return "login.html";
            }
            
            // 登入成功，將會員資訊存入session
            session.setAttribute("memberId", member.getMemid());
            
            redirectAttributes.addFlashAttribute("message", "登入成功");
            return "login.html";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登入失敗: " + e.getMessage());
            return "login.html";
        }
    }

    /**
     * 顯示會員資料頁面
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 從session取得會員ID
            Integer memberId = (Integer) session.getAttribute("memberId");
            
            if (memberId == null) {
                redirectAttributes.addFlashAttribute("error", "請先登入");
                return "basicInfo.html";
            }
            
            MemberVO member = memberService.getOneMember(memberId);
            
            if (member == null) {
                redirectAttributes.addFlashAttribute("error", "找不到會員資料");
                return "basicInfo.html";
            }
            
            // 移除敏感資料
            member.setPassword(null);
            
            // 將會員資料傳給view
            model.addAttribute("member", member);
            
            return "basicInfo.html";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "查詢失敗: " + e.getMessage());
            return "basicInfo.html";
        }
    }

    /**
     * 會員登出處理
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            session.invalidate();
            redirectAttributes.addFlashAttribute("message", "登出成功");
            return "back-end/member/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登出失敗: " + e.getMessage());
            return "login.html";
        }
    }
}