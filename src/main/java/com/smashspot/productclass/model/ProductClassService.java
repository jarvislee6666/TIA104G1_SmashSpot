package com.smashspot.productclass.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smashspot.coupon.model.CouponVO;

@Service
public class ProductClassService {
	
	@Autowired
	ProductClassRepository repository;
	
	public ProductClassVO findById(Integer proclassid) {
		Optional<ProductClassVO> optional = repository.findById(proclassid);
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
	
	public List<ProductClassVO> getAll() {
		return repository.findAll();
	}
}
