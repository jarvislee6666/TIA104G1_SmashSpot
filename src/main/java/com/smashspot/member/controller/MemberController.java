package com.smashspot.member.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
    
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    /**
     * 顯示註冊頁面
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("memberVO", new MemberVO());
        return "back-end/member/register";
    }

    /**
     * 發送驗證碼到指定email
     */
//    @PostMapping("/send-verification")
//    public String sendVerification(@RequestParam String email, RedirectAttributes redirectAttributes) {
//        try {
//            // 檢查Email是否已存在
//            if (memberService.getRepository().findByEmail(email) != null) {
//                redirectAttributes.addFlashAttribute("error", "Email已被使用");
//                return "register.html";
//            }
            
//            memberService.sendVerificationEmail(email);
//            redirectAttributes.addFlashAttribute("message", "驗證碼已發送至您的信箱");
//            return "register.html";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "驗證碼發送失敗: " + e.getMessage());
//            return "register.html";
//        }
//    }

    /**
     * 會員註冊處理
     */
    @PostMapping("/register")
    public String insert(@Valid MemberVO memberVO, BindingResult result, ModelMap model,
            @RequestParam("confirmPassword") String confirmPassword) {
    	
    	// 設置必要欄位
        memberVO.setCrttime(new Timestamp(System.currentTimeMillis()));
        memberVO.setChgtime(new Timestamp(System.currentTimeMillis()));
        memberVO.setStatus(true);
        
    	logger.info("Account: {}", memberVO.getAccount());
    	logger.info("Email: {}", memberVO.getEmail());
    	logger.info("Name: {}", memberVO.getName());
    	logger.info("Phone: {}", memberVO.getPhone());
    	logger.info("Bday: {}", memberVO.getBday());
    	logger.info("Addr: {}", memberVO.getAddr());
    	logger.info("Password: {}", memberVO.getPassword());
    	logger.info("Confirm Password: {}", confirmPassword);
    	
        // 檢查帳號是否存在
        if (memberService.findByAccount(memberVO.getAccount()) != null) {
            result.rejectValue("account", "error.memberVO", "此帳號已存在");
        }
        // 檢查 Email 是否存在 
        if (memberService.findByEmail(memberVO.getEmail()) != null) {
            result.rejectValue("email", "error.memberVO", "此 Email 已存在");
        }
        // 檢查電話是否存在
        if (memberService.findByPhone(memberVO.getPhone()) != null) {
            result.rejectValue("phone", "error.memberVO", "此電話號碼已存在");
        }
        // 如果有錯誤，返回註冊頁面
        if (result.hasErrors()) {
        	System.out.println(result.toString());
            return "back-end/member/register";
        }

        

        try {
            memberService.addMember(memberVO);
            logger.info("Successfully registered member: {}", memberVO.getAccount());
            return "redirect:/member/login";
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            model.addAttribute("error", "註冊失敗: " + e.getMessage());
            return "back-end/member/register";
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