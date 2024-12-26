package com.smashspot.admin.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.smashspot.admin.model.*;

import util.HibernateUtil_CompositeQuery_Adm;


@Service("admService")
public class AdmService {
	
	@Autowired
	AdmRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	public void addAdm(AdmVO admVO) {
		repository.save(admVO);
	}
		
	public void updateAdm(AdmVO admVO) {
		repository.save(admVO);
	}
	
	public List<AdmVO> getAll() {
		return repository.findAll();
	}
	
	public List<AdmVO> getAll(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_Adm.getAllC(map,sessionFactory.openSession());
	}
	
	public AdmVO getOneAdm(Integer admid) {
		Optional<AdmVO> optional = repository.findById(admid);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
	
	public AdmVO findByEmail(String email) {
        return repository.findByAdmemail(email);
    }
    
    public AdmVO findByPhone(String phone) {
        return repository.findByAdmphone(phone);
    }
    
    public AdmVO findByPassword(String password) {
        return repository.findByAdmpassword(password);
    }
    
    public AdmVO login(String email, String password) {
        AdmVO adm = repository.findByAdmemail(email);
        if (adm != null && adm.getAdmpassword().equals(password)) {
            return adm;
        }
        return null;
    }
	

}
