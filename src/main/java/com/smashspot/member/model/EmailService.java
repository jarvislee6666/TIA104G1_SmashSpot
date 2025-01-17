
// 定義套件路徑，指明此類別所屬的功能模組
package com.smashspot.member.model;

// 引入必要的Spring Framework和JavaMail相關類別
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

// 使用@Service註解，將此類別標記為Spring的服務層元件
// Spring會自動掃描並建立此類別的實例
@Service
public class EmailService {

    // 注入Spring提供的JavaMailSender介面
    // 這個介面負責實際的郵件發送操作，支援SMTP協議
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 發送註冊驗證信的方法
     * @param to 收件者的電子郵件地址
     * @param verificationLink 驗證連結URL
     * @throws MessagingException 當郵件發送過程中發生錯誤時拋出
     */
    public void sendVerificationEmail(String to, String verificationLink) throws MessagingException {
        // 設定郵件主旨
        String subject = "請驗證您的會員帳號";
        
        // 使用String.format構建HTML格式的郵件內容
        // 包含了適當的CSS樣式設定，確保郵件在各種郵件客戶端都能正確顯示
        String content = String.format(
            // 外層div設定字體和內距
            "<div style='font-family: 微軟正黑體, sans-serif; padding: 20px;'>" +
            // 標題部分
            "<h2>親愛的會員您好：</h2>" +
            // 說明文字
            "<p>感謝您註冊成為我們的會員！請點擊下方按鈕來驗證您的帳號：</p>" +
            // 驗證按鈕容器
            "<div style='margin: 20px 0;'>" +
            // 設計美觀的按鈕樣式
            "<a href='%s' style='background-color: #4CAF50; color: white; padding: 12px 24px; " +
            "text-decoration: none; border-radius: 4px; display: inline-block;'>點擊驗證帳號</a>" +
            "</div>" +
            // 備用連結說明
            "<p>如果按鈕無法點擊，請複製以下連結到瀏覽器開啟：</p>" +
            // 顯示完整的驗證連結
            "<p style='color: #666; word-break: break-all;'>%s</p>" +
            // 重要提醒事項
            "<p>※ 此驗證連結將在30分鐘後失效</p>" +
            "<p>※ 如果這不是您本人的操作，請忽略此信件</p>" +
            // 分隔線
            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            // 郵件底部說明
            "<p style='color: #666; font-size: 14px;'>此為系統自動發送的郵件，請勿直接回覆</p>" +
            "</div>",
            verificationLink, // 第一個%s的替換值
            verificationLink  // 第二個%s的替換值
        );

        // 調用共用的HTML郵件發送方法
        sendHtmlEmail(to, subject, content);
    }

    /**
     * 發送歡迎信的方法
     * @param to 收件者的電子郵件地址
     * @param name 會員名稱
     * @throws MessagingException 當郵件發送過程中發生錯誤時拋出
     */
    public void sendWelcomeEmail(String to, String name) throws MessagingException {
        // 設定郵件主旨
        String subject = "歡迎加入會員！";
        
        // 使用String.format構建HTML格式的歡迎信內容
        String content = String.format(
            // 外層容器設定
            "<div style='font-family: 微軟正黑體, sans-serif; padding: 20px;'>" +
            // 個人化的歡迎標題
            "<h2>親愛的 %s 您好：</h2>" +
            // 歡迎訊息
            "<p>恭喜您已成功完成會員註冊！</p>" +
            // 會員權益說明
            "<p>現在您可以享有以下會員權益：</p>" +
            // 使用無序列表呈現權益內容
            "<ul style='color: #444;'>" +
            "<li style='margin: 10px 0;'>完整瀏覽所有商品資訊</li>" +
            "<li style='margin: 10px 0;'>追蹤商品價格變動</li>" +
            "<li style='margin: 10px 0;'>參與會員專屬活動</li>" +
            "<li style='margin: 10px 0;'>累積購物點數</li>" +
            "</ul>" +
            // 結尾文字
            "<p>如果您有任何問題，歡迎隨時與我們聯繫！</p>" +
            // 分隔線
            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            // 系統郵件說明
            "<p style='color: #666; font-size: 14px;'>此為系統自動發送的郵件，請勿直接回覆</p>" +
            "</div>",
            name  // 替換%s為會員名稱
        );

        // 調用共用的HTML郵件發送方法
        sendHtmlEmail(to, subject, content);
    }

    /**
     * 共用的HTML郵件發送方法
     * @param to 收件者郵件地址
     * @param subject 郵件主旨
     * @param htmlContent HTML格式的郵件內容
     * @throws MessagingException 當郵件發送過程中發生錯誤時拋出
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        // 創建一個MimeMessage實例，這是JavaMail API中表示電子郵件的類別
        MimeMessage message = mailSender.createMimeMessage();
        
        // 創建MimeMessageHelper實例來協助設定郵件內容
        // 參數說明：
        // - message: 要設定的MimeMessage實例
        // - true: 表示這是一個multipart訊息，支援附件等多媒體內容
        // - "UTF-8": 指定字符編碼，確保中文等字符能正確顯示
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");     
        helper.setFrom("tiasmashspot@gmail.com");
        // 設定收件人地址
        helper.setTo(to);
        // 設定郵件主旨
        helper.setSubject(subject);
        // 設定郵件內容，第二個參數true表示內容是HTML格式
        helper.setText(htmlContent, true);
        
        // 發送郵件
//        mailSender.send(message);
        new Thread(()->mailSender.send(message)).start();
    }
 
    public void sendPasswordResetEmail(String to, String resetLink) throws MessagingException {
        String subject = "SmashSpot 密碼重設";
        
        String content = String.format(
            "<div style='font-family: 微軟正黑體, sans-serif; padding: 20px;'>" +
            "<h2>密碼重設請求</h2>" +
            "<p>我們收到了您的密碼重設請求。請點擊下方連結重設您的密碼：</p>" +
            "<div style='margin: 20px 0;'>" +
            "<a href='%s' style='background-color: #4CAF50; color: white; padding: 12px 24px; " +
            "text-decoration: none; border-radius: 4px; display: inline-block;'>重設密碼</a>" +
            "</div>" +
            "<p>如果按鈕無法點擊，請複製以下連結到瀏覽器開啟：</p>" +
            "<p style='color: #666; word-break: break-all;'>%s</p>" +
            "<p>※ 此連結將在30分鐘後失效</p>" +
            "<p>※ 如果這不是您本人的操作，請忽略此信件</p>" +
            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            "<p style='color: #666; font-size: 14px;'>此為系統自動發送的郵件，請勿直接回覆</p>" +
            "</div>",
            resetLink,
            resetLink
        );        
        sendHtmlEmail(to, subject, content);
    }
 
}