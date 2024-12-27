package com.smashspot.coupon.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponVO, Integer>{
	
}
