package com.smashspot.admin.model;

import java.util.List;
import java.util.Map;

import com.smashspot.admin.model.*;



public interface AdmDAO {

	int add(AdmVO adm);
	
	int update(AdmVO adm);
	
	AdmVO findByPK(Integer admid);
	
	List<AdmVO> getAll();
	
	List<AdmVO> getByCompositeQuery(Map<String, String> map);
	
	List<AdmVO> getAll(int currentPage);
	
	long getTotal();
}
