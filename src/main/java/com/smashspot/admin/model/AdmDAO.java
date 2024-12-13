package com.smashspot.admin.model;

import java.util.List;

import com.smashspot.admin.model.AdmVO;

public interface AdmDAO {

	int add(AdmVO admVO);
	int update(AdmVO admVO);
	AdmVO findByPK(Integer admid);
	List<AdmVO> getAll();
}
