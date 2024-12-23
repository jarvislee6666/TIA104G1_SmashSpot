package com.smashspot.stadium.model;

import static util.Constants.PAGE_MAX_RESULT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.smashspot.stadium.model.*;
import util.*;

//測試測試
public class StadiumDAO_impl implements StadiumDAO_interface {
	// SessionFactory 為 thread-safe，可宣告為屬性讓請求執行緒們共用
	private SessionFactory factory;

	public StadiumDAO_impl() {
		factory = HibernateUtil_CompositeQuery.getSessionFactory();
	}
	
	// Session 為 not thread-safe，所以此方法在各個增刪改查方法裡呼叫
	// 以避免請求執行緒共用了同個 Session
	private Session getSession() {
		return factory.getCurrentSession();
	}

	@Override
	public int insert(StadiumVO stdmVO) {
		// 回傳給 service 剛新增成功的自增主鍵值
		return (Integer) getSession().save(stdmVO);
	}

	@Override
	public int update(StadiumVO stdmVO) {
		try {
			getSession().update(stdmVO);
			return 1;
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public int delete(Integer stdmId) {
		StadiumVO stdmVO = getSession().get(StadiumVO.class, stdmId);
		if (stdmVO != null) {
			getSession().delete(stdmVO);
			// 回傳給 service，1代表刪除成功
			return 1;
		} else {
			// 回傳給 service，-1代表刪除失敗
			return -1;
		}
	}

//	@Override
//	public StadiumVO findByStdmName(String stdmName) {
//		return getSession().get(StadiumVO.class, stdmName);
//	}
//	
//	@Override
//	public StadiumVO findByLocId(Integer locId) {
//		return getSession().get(StadiumVO.class, locId);
//	}

	@Override
	public List<StadiumVO> getAll() {
		return getSession().createQuery("from Stdm", StadiumVO.class).list();
	}

	@Override
	public List<StadiumVO> getAll(Map<String, String> map) {
		if (map.size() == 0)
			return getAll();

		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<StadiumVO> criteria = builder.createQuery(StadiumVO.class);
		Root<StadiumVO> root = criteria.from(StadiumVO.class);

		List<Predicate> predicates = new ArrayList<>();

		for (Entry<String, String> row : map.entrySet()) {
			if ("stdmName".equals(row.getKey())) {
				predicates.add(builder.like(root.get("stdmName"), "%" + row.getValue() + "%"));
			}

			if ("locId".equals(row.getKey())) {
				predicates.add(builder.equal(root.get("locId"), row.getValue()));
			}

		}

		criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
		criteria.orderBy(builder.asc(root.get("stdmId")));
		TypedQuery<StadiumVO> query = getSession().createQuery(criteria);

		return query.getResultList();
	}

//	@Override
//	public List<Emp> getAll(int currentPage) {
//		int first = (currentPage - 1) * PAGE_MAX_RESULT;
//		return getSession().createQuery("from Emp", Emp.class)
//				.setFirstResult(first)
//				.setMaxResults(PAGE_MAX_RESULT)
//				.list();
//	}

//	@Override
//	public long getTotal() {
//		return getSession().createQuery("select count(*) from Emp", Long.class).uniqueResult();
//	}

}
