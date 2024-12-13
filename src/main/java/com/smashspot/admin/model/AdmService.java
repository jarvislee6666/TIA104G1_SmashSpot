package com.smashspot.admin.model;

import java.util.List;
import java.util.Map;
import com.smashspot.admin.model.*;



public interface AdmService {
	
	AdmVO addAdm(AdmVO adm);
	
	AdmVO updateAdm(AdmVO adm);
		
	AdmVO getAdmByAdmid(Integer admid);
	
	List<AdmVO> getAllAdms(int currentPage);
	
	int getPageTotal();
	
	List<AdmVO> getAdmsByCompositeQuery(Map<String, String[]> map);

}
