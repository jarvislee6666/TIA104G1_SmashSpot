// 定義套件路徑，表明此控制器屬於會員管理相關功能
package com.smashspot.member.controller;

// 引入必要的Java和Spring Framework相關類別
import java.sql.Timestamp;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
// 引入會員相關的服務類別
import com.smashspot.member.model.EmailService;
import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;
import com.smashspot.member.model.RedisService;

// 使用@Controller註解標記這是一個Spring MVC控制器
// @RequestMapping("/member")設定此控制器的基礎URL路徑
@Controller
@RequestMapping("/member")
public class MemberController {

    // 注入會員服務層元件，處理會員相關的業務邏輯
    @Autowired
    private MemberService memberService;
    
    // 注入郵件服務元件，處理郵件發送功能
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    // 建立日誌記錄器，用於記錄控制器中的重要操作和錯誤信息
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private static final String MEMBER_TEMP_PREFIX = "member:temp:";

    /**
     * 顯示會員註冊頁面
     * 當訪問 GET /member/register 時觸發
     * 
     * @param model Spring的Model物件，用於向視圖傳遞數據
     * @return 返回註冊頁面的視圖名稱
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // 向模型中添加一個空的MemberVO物件，但不進行驗證
        if (!model.containsAttribute("memberVO")) {
            model.addAttribute("memberVO", new MemberVO());
        }
        return "back-end/member/register";
    }

    @PostMapping("/register")
    public String insert(@Valid MemberVO memberVO, BindingResult result, Model model,
                        @RequestParam("confirmPassword") String confirmPassword) {
        
        // 驗證邏輯
        boolean hasErrors = false;

        // 檢查是否有重複的帳號、Email或電話號碼
        if (memberService.findByAccount(memberVO.getAccount()) != null) {
            result.rejectValue("account", "error.memberVO", "此帳號已存在");
            hasErrors = true;
        }
        if (memberService.findByEmail(memberVO.getEmail()) != null) {
            result.rejectValue("email", "error.memberVO", "此 Email 已存在");
            hasErrors = true;
        }
        if (memberService.findByPhone(memberVO.getPhone()) != null) {
            result.rejectValue("phone", "error.memberVO", "此電話號碼已存在");
            hasErrors = true;
        }
               
        // 確認密碼是否匹配
        if (!memberVO.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.memberVO", "密碼與確認密碼不符");
            hasErrors = true;
        }

        // 如果有任何驗證錯誤，返回註冊頁面
        if (result.hasErrors() || hasErrors) {
            return "back-end/member/register";
        }
        
        try {
            // 生成驗證 token
            String token = redisService.generateVerificationToken(memberVO);
            
            // 將會員資料暫存到 Redis，設定 30 分鐘過期
            // 使用 Jackson 或其他 JSON 工具將物件序列化
            ObjectMapper mapper = new ObjectMapper();
            String memberJson = mapper.writeValueAsString(memberVO);
            redisTemplate.opsForValue().set(
                MEMBER_TEMP_PREFIX + token,
                memberJson,
                30,
                TimeUnit.MINUTES
            );

            // 生成驗證連結
            String verificationLink = redisService.getVerificationLink(token);

            // 發送驗證郵件
            emailService.sendVerificationEmail(memberVO.getEmail(), verificationLink);

            // 導向等待驗證頁面
            model.addAttribute("email", memberVO.getEmail());
            return "back-end/member/VerificationPage";
        } catch (Exception e) {
            logger.error("註冊過程發生錯誤", e);
            model.addAttribute("error", "註冊失敗: " + e.getMessage());
            model.addAttribute("memberVO", memberVO);
            return "back-end/member/register";
        }
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) {
        try {
            // 從 Redis 取得暫存的會員資料
            String memberJson = (String) redisTemplate.opsForValue().get(MEMBER_TEMP_PREFIX + token);
            
            if (memberJson != null) {
                // 將 JSON 轉回 MemberVO 物件
                ObjectMapper mapper = new ObjectMapper();
                MemberVO memberVO = mapper.readValue(memberJson, MemberVO.class);
                
                // 設定會員狀態為已驗證
                memberVO.setStatus(true);
                
                // 正式將會員資料寫入資料庫
                memberService.addMember(memberVO);
                
                // 刪除 Redis 中的暫存資料
                redisTemplate.delete(MEMBER_TEMP_PREFIX + token);
                
                // 發送歡迎郵件
                sendWelcomeEmail(memberVO);
                
                model.addAttribute("success", "註冊成功！歡迎加入SmashSpot，請登入開始使用我們的服務。");
                return "back-end/member/login";
            }
            
            model.addAttribute("error", "驗證連結已失效或不正確");
            return "back-end/member/VerificationPage";
            
        } catch (Exception e) {
            logger.error("驗證過程發生錯誤", e);
            model.addAttribute("error", "驗證過程發生錯誤，請稍後再試");
            return "back-end/member/VerificationPage";
        }
    }
    //重新寄送驗證信
    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, Model model) {
        try {
            // 檢查是否有該email的暫存會員資料
            String memberJson = (String) redisTemplate.opsForValue().get(MEMBER_TEMP_PREFIX + email);
            
            if (memberJson != null) {
                // 重新生成驗證token
                ObjectMapper mapper = new ObjectMapper();
                MemberVO memberVO = mapper.readValue(memberJson, MemberVO.class);
                String token = redisService.generateVerificationToken(memberVO);
                
                // 重新發送驗證郵件
                String verificationLink = redisService.getVerificationLink(token);
                emailService.sendVerificationEmail(email, verificationLink);
                
                model.addAttribute("success", "驗證信已重新發送，請查收您的信箱");
            } else {
                model.addAttribute("error", "無法找到註冊資料，請重新註冊");
            }
        } catch (Exception e) {
            logger.error("重新發送驗證信失敗", e);
            model.addAttribute("error", "發送驗證信失敗，請稍後再試");
        }
        
        model.addAttribute("email", email);
        return "back-end/member/VerificationPage";
    }
    
    
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, 
                               RedirectAttributes redirectAttributes) {
        try {
            // 檢查email是否存在
            MemberVO member = memberService.findByEmail(email);
            if (member == null) {
                // 如果找不到對應的email，添加錯誤消息
                redirectAttributes.addFlashAttribute("error", "找不到此電子郵件地址的帳號");
                return "redirect:/member/login";  // 返回登入頁面
            }

            // 生成重設密碼的token
            String resetToken = redisService.generatePasswordResetToken(member);
            String resetLink = redisService.getPasswordResetLink(resetToken);

            // 發送重設密碼郵件
            emailService.sendPasswordResetEmail(email, resetLink);

            // 添加成功消息
            redirectAttributes.addFlashAttribute("success", 
                "重設密碼連結已發送到您的信箱，請在30分鐘內完成密碼重設");
            
        } catch (Exception e) {
            logger.error("密碼重設過程發生錯誤", e);
            redirectAttributes.addFlashAttribute("error", 
                "發送重設密碼郵件時發生錯誤，請稍後再試");
        }
        
        return "redirect:/member/login";  // 重定向回登入頁面
    }
    
    /**
     * 顯示密碼重設頁面
     * 當用戶從郵件中點擊重設連結時，會先到達這個頁面
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        // 檢查 token 是否有效
        if (!redisService.isPasswordResetTokenValid(token)) {
            model.addAttribute("error", "密碼重設連結已失效或不正確，請重新申請");
            return "back-end/member/login";
        }
        
        // 如果 token 有效，將其傳遞給重設密碼表單
        model.addAttribute("token", token);
        return "back-end/member/resetPasswordPage";
    }

    /**
     * 處理密碼重設請求
     * 當用戶提交新密碼時處理更新邏輯
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                              @RequestParam String newPassword,
                              @RequestParam String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        try {
            // 再次驗證 token
            if (!redisService.isPasswordResetTokenValid(token)) {
                redirectAttributes.addFlashAttribute("error", "密碼重設連結已失效，請重新申請");
                return "redirect:/member/login";
            }
            
            // 驗證密碼
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "兩次輸入的密碼不相同");
                return "redirect:/member/reset-password?token=" + token;
            }
            
            // 密碼長度驗證
            if (newPassword.length() < 6 || newPassword.length() > 20) {
                redirectAttributes.addFlashAttribute("error", "密碼長度必須在6到20個字元之間");
                return "redirect:/member/reset-password?token=" + token;
            }
            
            // 從 Redis 中獲取對應的會員信息
            MemberVO member = redisService.getMemberFromPasswordResetToken(token);
            if (member == null) {
                redirectAttributes.addFlashAttribute("error", "無法找到會員資訊，請重新申請密碼重設");
                return "redirect:/member/login";
            }
            
            // 更新密碼
            member.setPassword(newPassword);
            memberService.updateMember(member);
            
            // 清除 Redis 中的重設 token
            redisService.clearPasswordResetToken(token);
            
            // 設置成功消息
            redirectAttributes.addFlashAttribute("success", "密碼已成功重設，請使用新密碼登入");
            
            return "redirect:/member/login";
            
        } catch (Exception e) {
            logger.error("密碼重設過程發生錯誤", e);
            redirectAttributes.addFlashAttribute("error", "密碼重設失敗，請稍後再試");
            return "redirect:/member/login";
        }
    }
    
    
    /**
     * 發送歡迎信的私有方法
     */
    private void sendWelcomeEmail(MemberVO member) throws MessagingException {
        String welcomeSubject = "歡迎加入SmashSpot！";
        String welcomeContent = String.format(
            "<div style='font-family: 微軟正黑體, sans-serif; padding: 20px;'>" +
            "<h2>親愛的 %s 您好：</h2>" +
            "<p>感謝您完成電子郵件驗證！您現在已經是SmashSpot的正式會員了。</p>" +
            "<p>作為我們的會員，您可以立即享有以下專屬權益：</p>" +
            "<ul style='line-height: 1.6;'>" +
            "<li>瀏覽和預約場地</li>" +
            "<li>參與會員專屬活動</li>" +
            "<li>累積消費點數</li>" +
            "<li>享受會員專屬優惠</li>" +
            "</ul>" +
            "<p>現在就登入開始探索吧！</p>" +
            "<p>如果您有任何問題或需要協助，歡迎隨時與我們聯繫。</p>" +
            "<p>祝您有愉快的使用體驗！</p>" +
            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            "<p style='color: #666; font-size: 14px;'>此為系統自動發送的郵件，請勿直接回覆</p>" +
            "</div>",
            member.getName()
        );
        
        emailService.sendHtmlEmail(member.getEmail(), welcomeSubject, welcomeContent);
    }
    
    /**
     * 顯示登入頁面
     * GET /member/login
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "back-end/member/login";
    }

    /**
     * 處理會員登入請求
     * POST /member/login
     * 
     * @param account 會員帳號
     * @param password 會員密碼
     * @param session HTTP會話物件，用於儲存登入狀態
     * @param model 用於向視圖傳遞數據
     * @return 登入成功則重定向到會員資料頁，失敗則返回登入頁面
     */
    @PostMapping("/login")
    public String login(@RequestParam String account, @RequestParam String password,
                       HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // 驗證會員帳號密碼
        MemberVO mem = memberService.login(account, password);
        if (mem != null) {
        	
        	// 檢查會員狀態
            if (!mem.getStatus()) {
            	redirectAttributes.addFlashAttribute("error", "您的帳號已被停用");
                return "redirect:/member/login";
            }
            
            // 登入成功，將會員資訊存入session
            session.setAttribute("login", mem);
            
            // 檢查是否有之前被攔截的請求路徑
            String redirectURL = (String) session.getAttribute("redirectURL");
            if (redirectURL != null) {
                // 清除暫存的路徑並重定向到該路徑
                session.removeAttribute("redirectURL");
                return "redirect:" + redirectURL;
            } else {
                // 無特定路徑則導向會員基本資料頁
                return "redirect:/member/basic-info";
            }
        }
        // 登入失敗，返回登入頁面並顯示錯誤訊息
        model.addAttribute("error", "帳號或密碼錯誤");
        return "back-end/member/login";
    }

    /**
     * 顯示會員基本資料頁面
     * GET /member/basic-info
     * 
     * @param session 用於檢查登入狀態
     * @param model 用於向視圖傳遞數據
     * @return 已登入則顯示資料頁，未登入則重定向到登入頁
     */
    @GetMapping("/basic-info")
    public String showBasicInfo(HttpSession session, Model model) {
        // 從session中獲取登入會員資訊
        MemberVO member = (MemberVO) session.getAttribute("login");
        if (member == null) {
            return "redirect:/member/login";
        }
        
        // 將會員資料添加到模型中
        model.addAttribute("memberForm", member);
        
        // 處理會員頭像
        if (member.getMempic() != null) {
            // 將二進制圖片數據轉換為Base64字符串
            String base64Image = "data:image/jpeg;base64," + 
                Base64.getEncoder().encodeToString(member.getMempic());
            model.addAttribute("memberImage", base64Image);
        } else {
            model.addAttribute("memberImage", null);
        }

        return "back-end/member/basicInfo";
    }

    
    @PostMapping("/update-info")
    public String updateInfo(
            @Valid @ModelAttribute("memberForm") MemberVO memberVO,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 從 session 中獲取當前登入的會員資料
            MemberVO currentMember = (MemberVO) session.getAttribute("login");
            if (currentMember == null) {
                return "redirect:/member/login";
            }

            // 保留原有的重要資料
            memberVO.setMemid(currentMember.getMemid());
            memberVO.setAccount(currentMember.getAccount());  // 重要：保留原有帳號
            memberVO.setStatus(currentMember.getStatus());
            memberVO.setMempic(currentMember.getMempic());
            memberVO.setCrttime(currentMember.getCrttime());
            
            // 如果密碼欄位為空，保留原密碼
            if (memberVO.getPassword() == null || memberVO.getPassword().trim().isEmpty()) {
                memberVO.setPassword(currentMember.getPassword());
            }

            // 設置更新時間
            memberVO.setChgtime(new Timestamp(System.currentTimeMillis()));

            // 在更新之前，記錄要更新的資料，方便除錯
            logger.info("準備更新會員資料: ID={}, Account={}, Name={}, Email={}", 
                memberVO.getMemid(), 
                memberVO.getAccount(),
                memberVO.getName(),
                memberVO.getEmail());

            // 更新資料
            memberService.updateMember(memberVO);
            
            // 更新 session 中的會員資料
            session.setAttribute("login", memberVO);
            
            redirectAttributes.addFlashAttribute("message", "資料更新成功！");
            redirectAttributes.addFlashAttribute("messageType", "success");
            
        } catch (Exception e) {
            logger.error("更新失敗", e);
            redirectAttributes.addFlashAttribute("message", "更新失敗：" + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        
        return "redirect:/member/basic-info";
    }  
    /**
     * 處理會員登出請求
     * POST /member/logout
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // 清除session中的所有資料
        session.invalidate();
        return "redirect:/member/login";
    }

    /**
     * 處理會員頭像上傳
     * POST /member/upload-avatar
     */
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            // 檢查登入狀態
            MemberVO member = (MemberVO) session.getAttribute("login");
            if (member == null) {
                return "redirect:/member/login";
            }

            // 驗證上傳的文件
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "請選擇要上傳的檔案");
                return "redirect:/member/basic-info";
            }

            // 檢查文件類型
            if (!file.getContentType().startsWith("image/")) {
                redirectAttributes.addFlashAttribute("message", "只能上傳圖片檔案");
                return "redirect:/member/basic-info";
            }

            // 更新會員頭像
            member.setMempic(file.getBytes());
            memberService.updateMember(member);
            
            // 更新session中的會員資料
            session.setAttribute("login", member);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "頭像上傳失敗：" + e.getMessage());
        }
        
        return "redirect:/member/basic-info";
    }

    /**
     * 獲取會員頭像
     * GET /member/avatar/{id}
     */
    @ResponseBody
    @GetMapping("/avatar/{id}")
    public String getAvatar(@PathVariable Integer id) {
        try {
            // 查詢會員資料
            MemberVO member = memberService.getOneMember(id);
            if (member != null && member.getMempic() != null) {
                // 將頭像圖片轉換為Base64字符串
                return "data:image/jpeg;base64," + 
                    Base64.getEncoder().encodeToString(member.getMempic());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}