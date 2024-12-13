package com.smashspot.admin.model;


import static util.Constants.PAGE_MAX_RESULT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import util.HibernateUtil;
import org.hibernate.Session;
import com.smashspot.admin.model.*;









public class AdmServiceImpl2 implements AdmService {
	
	private AdmDAO dao;
	
	public AdmServiceImpl2() {
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
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			List<AdmVO> list = dao.getAll(currentPage);
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
			return null;
		}
	
	}

	@Override
	public int getPageTotal() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		int pageQty;
		try {
			session.beginTransaction();
			long total = dao.getTotal();
			pageQty = (int)(total % PAGE_MAX_RESULT == 0 ? (total / PAGE_MAX_RESULT) : (total / PAGE_MAX_RESULT + 1));
			session.getTransaction().commit();
		} catch (Exception e) {
			pageQty = 1;
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return pageQty;
	}

	@Override
	public List<AdmVO> getAdmsByCompositeQuery(Map<String, String[]> map) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Map<String, String> query = new HashMap<>();
		// Map.Entry即代表一組key-value
		Set<Map.Entry<String, String[]>> entry = map.entrySet();
		
		for (Map.Entry<String, String[]> row : entry) {
			String key = row.getKey();
			// 因為請求參數裡包含了action，做個去除動作
			if ("action".equals(key)) {
				continue;
			}
			// 若是value為空即代表沒有查詢條件，做個去除動作
			String value = row.getValue()[0];
			if (value.isEmpty() || value == null) {
				continue;
			}
			query.put(key, value);
		}
		
		System.out.println(query);
		
		try {
			session.beginTransaction();
			List<AdmVO> list = dao.getByCompositeQuery(query);
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
			return null;
		}
	}



}
