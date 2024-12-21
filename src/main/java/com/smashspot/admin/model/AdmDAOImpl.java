package com.smashspot.admin.model;

import static util.Constants.PAGE_MAX_RESULT;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.smashspot.admin.model.*;


import util.HibernateUtil;



public class AdmDAOImpl implements AdmDAO{
	
	private SessionFactory factory;

	public AdmDAOImpl() {
		factory = HibernateUtil.getSessionFactory();
	}
	
	private Session getSession() {
		return factory.getCurrentSession();
	}

	@Override
	public int add(AdmVO adm) {
		return (Integer) getSession().save(adm);
	}

	@Override
	public int update(AdmVO adm) {
		try {
			getSession().update(adm);
			return 1;
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public AdmVO findByPK(Integer admid) {
		return getSession().get(AdmVO.class, admid);
	}

	@Override
	public List<AdmVO> getAll() {
		return getSession().createQuery("from Adm", AdmVO.class).list();
	}

	@Override
	public List<AdmVO> getByCompositeQuery(Map<String, String> map) {
		if (map.size() == 0)
		return null;
		
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<AdmVO> criteria = builder.createQuery(AdmVO.class);
		Root<AdmVO> root = criteria.from(AdmVO.class);

		List<Predicate> predicates = new ArrayList<>();

		for (Map.Entry<String, String> row : map.entrySet()) {
			if ("admname".equals(row.getKey())) {
				predicates.add(builder.like(root.get("admname"), "%" + row.getValue() + "%"));
			}

			if ("admsta".equals(row.getKey()) && row.getValue() != null && !row.getValue().isEmpty()) {
                boolean statusValue = Boolean.parseBoolean(row.getValue());
                predicates.add(builder.equal(root.get("admsta"), statusValue));
            }
			
			if ("supvsr".equals(row.getKey()) && row.getValue() != null && !row.getValue().isEmpty()) {
                boolean supervisorValue = Boolean.parseBoolean(row.getValue());
                predicates.add(builder.equal(root.get("supvsr"), supervisorValue));
            }

		}

		criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
		criteria.orderBy(builder.asc(root.get("admid")));
		TypedQuery<AdmVO> query = getSession().createQuery(criteria);

		return query.getResultList();
	}

	@Override
	public List<AdmVO> getAll(int currentPage) {
		int first = (currentPage - 1) * PAGE_MAX_RESULT;
		return getSession().createQuery("from AdmVO", AdmVO.class)
				.setFirstResult(first)
				.setMaxResults(PAGE_MAX_RESULT)
				.list();
	}

	@Override
	public long getTotal() {
		return getSession().createQuery("select count(*) from AdmVO", Long.class).uniqueResult();
	}
	

}
