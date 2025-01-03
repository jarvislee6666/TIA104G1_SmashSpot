package com.smashspot.member.model;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.admin.model.AdmVO;

//import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_Member;

@Service("memberService")
public class MemberService {
    
    @Autowired
    MemberRepository repository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    
    @Transactional
    public void addMember(MemberVO memberVO) {
        logger.info("開始新增會員: {}", memberVO.getAccount());
        try {
        	repository.save(memberVO);
            logger.info("會員新增成功: {}", memberVO.getAccount());
        } catch (Exception e) {
            logger.error("會員新增失敗", e);
            throw e;
        }
    }
    
    public void updateMember(MemberVO memberVO) {
        repository.save(memberVO);
    }
    
    public MemberVO getOneMember(Integer memid) {
        Optional<MemberVO> optional = repository.findById(memid);
        // return optional.get();
        return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
    }
    
    public List<MemberVO> getAll() {
        return repository.findAll();
    }

	public MemberVO getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	 public MemberVO findByAccount(String account) {
	        return repository.findByAccount(account);
	    }

	 public MemberVO findByEmail(String email) {
	        return repository.findByEmail(email);
	    }
    
	 public MemberVO findByPhone(String phone) {
	        return repository.findByPhone(phone);
	    }
	
//    public List<MemberVO> getAll(Map<String, String[]> map) {
//        return HibernateUtil_CompositeQuery_Member.getAllC(map, sessionFactory.openSession());
//    }
	public void sendVerificationEmail(String email) {
        // 生成驗證碼
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);
        
        // 儲存驗證碼到 Redis
        redisService.saveVerificationCode(email, verificationCode);
        
        // 發送驗證郵件
        emailService.sendVerificationEmail(email, verificationCode);
    }
    
    public boolean verifyEmail(String email, String code) {
        String storedCode = redisService.getVerificationCode(email);
        if (storedCode != null && storedCode.equals(code)) {
            redisService.deleteVerificationCode(email);
            return true;
        }
        return false;
    }
    
    public void registerMember(MemberVO memberVO, String verificationCode) {
        if (verifyEmail(memberVO.getEmail(), verificationCode)) {
            // 儲存會員資料
            repository.save(memberVO);
            
            // 發送歡迎郵件
            emailService.sendWelcomeEmail(memberVO.getEmail(), memberVO.getName());
        } else {
            throw new RuntimeException("驗證碼無效或已過期");
        }
    }

	public boolean verifyCode(@NotEmpty(message = "Email: 請勿空白") @Email(message = "Email格式不正確") String email,
			String verificationCode) {
		// TODO Auto-generated method stub
		return false;
	}


	public MemberVO login(String account, String password) {
        MemberVO mem = repository.findByAccount(account);
        if (mem != null && mem.getPassword().equals(password)) {
            return mem;
        }
        return null;
    }







}


