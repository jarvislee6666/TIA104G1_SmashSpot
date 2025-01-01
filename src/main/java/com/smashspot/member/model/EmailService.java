package com.smashspot.member.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("您的驗證碼是: " + verificationCode + "\n請在30分鐘內完成驗證。");
        mailSender.send(message);
    }
    
    public void sendWelcomeEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to SmashSpot!");
        message.setText("親愛的 " + name + "，\n\n歡迎加入我們的平台！");
        mailSender.send(message);
    }

	public void sendRegistrationEmail(@NotEmpty(message = "Email: 請勿空白") @Email(message = "Email格式不正確") String email,
			@NotEmpty(message = "姓名: 請勿空白") @Pattern(regexp = "^[(一-龥)(a-zA-Z)]{2,20}$", message = "姓名: 只能是中、英文字母, 且長度必需在2到20之間") String name) {
		// TODO Auto-generated method stub
		
	}
}