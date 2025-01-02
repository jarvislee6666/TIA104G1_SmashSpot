package com.smashspot.member.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smashspot.admin.model.AdmVO;
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
        return "back-end/member/login";
    }

    /**
     * 會員登入處理
     */
    @PostMapping("/login")
    public String login(@RequestParam String account, 
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        MemberVO mem = memberService.login(account, password);
        if (mem != null) {
            session.setAttribute("login", mem);
            return "redirect:/member/basic-info";
        }
        model.addAttribute("error", true);
        return "back-end/member/login";
    }

    /**
     * 顯示會員資料頁面
     */
    @GetMapping("/basic-info")
    public String showBasicInfo(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("login");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        model.addAttribute("memberForm", member);
        return "back-end/member/basicInfo";
    }
    
    @PostMapping("/update-info")
    public String updateInfo(@Valid @ModelAttribute("memberForm") MemberVO memberVO,
                            BindingResult result,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "back-end/member/basicInfo";
        }
        
        MemberVO original = memberService.getOneMember(memberVO.getMemid());
        original.setName(memberVO.getName());
        original.setEmail(memberVO.getEmail());
        original.setPhone(memberVO.getPhone());
        original.setBday(memberVO.getBday());
        original.setAddr(memberVO.getAddr());
        original.setChgtime(new Timestamp(System.currentTimeMillis()));
        
        if (memberVO.getPassword() != null && !memberVO.getPassword().isEmpty()) {
            original.setPassword(memberVO.getPassword());
        }
        
        memberService.updateMember(original);
        session.setAttribute("login", original);
        redirectAttributes.addFlashAttribute("message", "更新成功");
        return "redirect:/member/basic-info";
    }

    @PostMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate();
	    return "redirect:/member/login";
	}
}