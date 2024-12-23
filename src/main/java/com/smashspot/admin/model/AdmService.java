package com.smashspot.admin.model;

import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.smashspot.admin.model.*;

import util.HibernateUtil_CompositeQuery;


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
		return HibernateUtil_CompositeQuery.getAllC(map,sessionFactory.openSession());
	}

}
