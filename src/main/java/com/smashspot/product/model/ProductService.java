package com.smashspot.product.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smashspot.coupon.model.CouponVO;

@Service
public class ProductService {
	
	@Autowired
	ProductRepository repository;
	
	public ProductVO findById(Integer proclassid) {
		Optional<ProductVO> optional = repository.findById(proclassid);
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
	
	public List<ProductVO> getAll() {
		return repository.findAll();
	}
}
