package com.smashspot.admin.model;

import static util.Constants.PAGE_MAX_RESULT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.smashspot.admin.model.*;


public class AdmServiceImpl implements AdmService {
	
	private AdmDAO dao;
	
	public AdmServiceImpl() {
		dao = new AdmDAOImpl();
	}

	@Override
	public AdmVO addAdm(AdmVO adm) {
		
		return null;
	}

	@Override
	public AdmVO updateAdm(AdmVO adm) {
	
		return null;
	}

	@Override
	public AdmVO getAdmByAdmid(Integer admid) {
	
		return null;
	}

	@Override
	public List<AdmVO> getAllAdms(int currentPage) {

		return dao.getAll(currentPage);
	}

	@Override
	public int getPageTotal() {
		long total = dao.getTotal();
		int pageQty = (int)(total % PAGE_MAX_RESULT == 0 ? (total / PAGE_MAX_RESULT) : (total / PAGE_MAX_RESULT + 1));
		return pageQty;
	}

	@Override
	public List<AdmVO> getAdmsByCompositeQuery(Map<String, String[]> map) {
		Map<String, String> query = new HashMap<>();
		Set<Map.Entry<String, String[]>> entry = map.entrySet();
		
		for (Map.Entry<String, String[]> row : entry) {
			String key = row.getKey();
			if ("action".equals(key)) {
				continue;
			}
			String value = row.getValue()[0]; 
			if (value == null || value.isEmpty()) {
				continue;
			}
			query.put(key, value);
		}
		
		System.out.println(query);
		
		return dao.getByCompositeQuery(query);
	}

}
