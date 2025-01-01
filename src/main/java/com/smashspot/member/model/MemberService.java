package com.smashspot.member.model;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    public void addMember(MemberVO memberVO) {
        repository.save(memberVO);
    }
    
    public void updateMember(MemberVO memberVO) {
        repository.save(memberVO);
    }
    
    public void deleteMember(Integer memid) {
        if (repository.existsById(memid))
            repository.deleteByMemid(memid);
            // repository.deleteById(memid);
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

	public MemberVO findByAccount(
			@NotEmpty(message = "帳號: 請勿空白") @Pattern(regexp = "^[(一-龥)(a-zA-Z0-9_)]{2,100}$", message = "帳號: 只能是中、英文字母、數字和_ , 且長度必需在2到100之間") String account) {
		// TODO Auto-generated method stub
		return null;
	}

	public MemberVO findByEmail(@NotEmpty(message = "Email: 請勿空白") @Email(message = "Email格式不正確") String email) {
		// TODO Auto-generated method stub
		return null;
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










}


