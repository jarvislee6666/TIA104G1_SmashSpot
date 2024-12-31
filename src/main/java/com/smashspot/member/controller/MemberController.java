package com.smashspot.member.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smashspot.member.model.EmailService;
import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private EmailService emailService;

    /**
     * 發送驗證碼到指定email
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestParam String email) {
        try {
            // 檢查Email是否已存在
            if (memberService.getRepository().findByEmail(email) != null) {
                return ResponseEntity.badRequest().body("Email已被使用");
            }
            
            memberService.sendVerificationEmail(email);
            return ResponseEntity.ok("驗證碼已發送至您的信箱");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("驗證碼發送失敗: " + e.getMessage());
        }
    }

    /**
     * 會員註冊
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody MemberVO memberVO,
            @RequestParam String verificationCode) {
        try {
            // 檢查帳號是否已存在
            if (memberService.getRepository().findByAccount(memberVO.getAccount()) != null) {
                return ResponseEntity.badRequest().body("帳號已存在");
            }
            
            // 檢查手機號碼是否已存在
            if (memberService.getRepository().findByPhone(memberVO.getPhone()) != null) {
                return ResponseEntity.badRequest().body("手機號碼已被使用");
            }

            // 驗證驗證碼
            if (!memberService.verifyCode(memberVO.getEmail(), verificationCode)) {
                return ResponseEntity.badRequest().body("驗證碼不正確或已過期");
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
            
            return ResponseEntity.ok("註冊成功，已寄送確認信至您的信箱");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("註冊失敗: " + e.getMessage());
        }
    }

    /**
     * 會員登入
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        try {
            String account = loginData.get("account");
            String password = loginData.get("password");
            
            MemberVO member = memberService.getRepository().findByAccount(account);
            
            if (member == null) {
                return ResponseEntity.badRequest().body("帳號不存在");
            }
            
            if (!member.getPassword().equals(password)) {
                return ResponseEntity.badRequest().body("密碼錯誤");
            }
            
            if (!member.getStatus()) {
                return ResponseEntity.badRequest().body("帳號已被停用");
            }
            
            // 登入成功，將會員資訊存入session
            session.setAttribute("memberId", member.getMemid());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登入成功");
            response.put("memberId", member.getMemid());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("登入失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢會員資料
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        try {
            // 從session取得會員ID
            Integer memberId = (Integer) session.getAttribute("memberId");
            
            if (memberId == null) {
                return ResponseEntity.badRequest().body("請先登入");
            }
            
            MemberVO member = memberService.getOneMember(memberId);
            
            if (member == null) {
                return ResponseEntity.badRequest().body("找不到會員資料");
            }
            
            // 移除敏感資料
            member.setPassword(null);
            
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 會員登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            session.invalidate();
            return ResponseEntity.ok("登出成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("登出失敗: " + e.getMessage());
        }
    }
}