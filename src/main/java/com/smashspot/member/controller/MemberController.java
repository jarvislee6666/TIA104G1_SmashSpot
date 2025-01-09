// 定義套件路徑，表明此控制器屬於會員管理相關功能
package com.smashspot.member.controller;

// 引入必要的Java和Spring Framework相關類別
import java.sql.Timestamp;
import java.util.Base64;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 引入會員相關的服務類別
import com.smashspot.member.model.EmailService;
import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;

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
    
    // 建立日誌記錄器，用於記錄控制器中的重要操作和錯誤信息
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    /**
     * 顯示會員註冊頁面
     * 當訪問 GET /member/register 時觸發
     * 
     * @param model Spring的Model物件，用於向視圖傳遞數據
     * @return 返回註冊頁面的視圖名稱
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // 向模型中添加一個空的MemberVO物件，供表單綁定使用
        model.addAttribute("memberVO", new MemberVO());
        return "back-end/member/register";
    }

    /**
     * 處理會員註冊請求
     * 當提交 POST /member/register 時觸發
     * 
     * @param memberVO 包含會員註冊信息的物件（經過表單驗證）
     * @param result 包含驗證結果的BindingResult物件
     * @param model 用於向視圖傳遞數據的ModelMap物件
     * @param confirmPassword 確認密碼欄位
     * @return 註冊成功則轉向登入頁面，失敗則返回註冊頁面
     */
    @PostMapping("/register")
    public String insert(@Valid MemberVO memberVO, BindingResult result, ModelMap model,
                        @RequestParam("confirmPassword") String confirmPassword) {
        
        // 檢查是否有重複的帳號、Email或電話號碼
        if (memberService.findByAccount(memberVO.getAccount()) != null) {
            result.rejectValue("account", "error.memberVO", "此帳號已存在");
        }
        if (memberService.findByEmail(memberVO.getEmail()) != null) {
            result.rejectValue("email", "error.memberVO", "此 Email 已存在");
        }
        if (memberService.findByPhone(memberVO.getPhone()) != null) {
            result.rejectValue("phone", "error.memberVO", "此電話號碼已存在");
        }
               
        // 確認密碼是否匹配
        if (!memberVO.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.memberVO", "密碼與確認密碼不符");
        }
        
        // 設定會員狀態為有效
        memberVO.setStatus(true);

        // 如果有任何驗證錯誤，返回註冊頁面
        if (result.hasErrors()) {
            return "back-end/member/register";
        }
        
        try {
            // 儲存會員資料
            memberService.addMember(memberVO);

            // 發送歡迎郵件
            String subject = "恭喜註冊成功！";
            String content = String.format(
                "<p>親愛的 %s 您好，</p>"
                + "<p>感謝您註冊我們的服務！現在您可以登入並開始使用我們的平台。</p>"
                + "<p>祝您使用愉快！</p><p>敬上，<br>您的團隊</p>",
                memberVO.getName()
            );
            emailService.sendHtmlEmail(memberVO.getEmail(), subject, content);

            // 註冊成功，轉向登入頁面
            model.addAttribute("success", "新增成功");
            return "back-end/member/login";
        } catch (Exception e) {
            // 發生錯誤時返回驗證頁面並顯示錯誤信息
            model.addAttribute("error", "新增失敗: " + e.getMessage());
            return "back-end/member/VerificationPage";
        }
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

    /**
     * 處理會員資料更新請求
     * POST /member/update-info
     */
    @PostMapping("/update-info")
    public String updateInfo(
            @Valid @ModelAttribute("memberForm") MemberVO memberVO,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // 驗證表單數據
        if (result.hasErrors()) {
            return "back-end/member/basicInfo";
        }

        try {
            // 獲取原始會員資料
            MemberVO original = memberService.getOneMember(memberVO.getMemid());
            if (original == null) {
                redirectAttributes.addFlashAttribute("error", "會員資料不存在");
                return "back-end/member/basic-info";
            }

            // 更新會員資料
            original.setName(memberVO.getName());
            original.setEmail(memberVO.getEmail());
            original.setPhone(memberVO.getPhone());
            original.setBday(memberVO.getBday());
            original.setAddr(memberVO.getAddr());
            original.setChgtime(new Timestamp(System.currentTimeMillis()));

            // 僅在有新密碼輸入時更新密碼
            if (memberVO.getPassword() != null && !memberVO.getPassword().isEmpty()) {
                original.setPassword(memberVO.getPassword());
            }

            // 保存更新後的資料
            memberService.updateMember(original);

            // 更新session中的會員資料
            session.setAttribute("login", original);

            // 設定成功消息
            redirectAttributes.addFlashAttribute("message", "資料更新成功");
            redirectAttributes.addFlashAttribute("messageType", "success");
            
            return "back-end/member/basic-info";
        } catch (Exception e) {
            // 設定錯誤消息
            redirectAttributes.addFlashAttribute("message", "更新失敗：" + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "back-end/member/basic-info";
        }
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
            
            redirectAttributes.addFlashAttribute("message", "頭像上傳成功");
            
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