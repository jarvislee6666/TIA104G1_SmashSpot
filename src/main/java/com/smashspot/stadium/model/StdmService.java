package com.smashspot.stadium.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.smashspot.stadium.model.StdmRepository;
import com.smashspot.stadium.model.StadiumVO;

import util.HibernateUtil_CompositeQuery_Stdm;

public class StdmService {

	@Autowired
	StdmRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addStdm(StadiumVO stdmVO) {
		repository.save(stdmVO);
	}

	public void updateStdm(StadiumVO stdmVO) {
		repository.save(stdmVO);
	}

	public void deleteStdm(Integer stdmId) {
		if (repository.existsById(stdmId))
			repository.deleteByStdmId(stdmId);
//		    repository.deleteById(stdmId);
	}

	public StadiumVO getOneStdm(Integer stdmId) {
		Optional<StadiumVO> optional = repository.findById(stdmId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<StadiumVO> getAll() {
		return repository.findAll();
	}

	public List<StadiumVO> getAll(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_Stdm.getAllC(map,sessionFactory.openSession());
	}

	
}
