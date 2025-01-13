package com.smashspot.coupon.model;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CouponRepository extends JpaRepository<CouponVO, Integer>{
	CouponVO findByCopcode(String copcode);
	
	//複合查詢
	 @Query("SELECT c FROM CouponVO c WHERE " +
	           "(:copcode IS NULL OR LOWER(c.copcode) LIKE LOWER(CONCAT('%', :copcode, '%'))) AND " +
	           "(:startDate IS NULL OR c.crtdate >= :startDate) AND " +
	           "(:endDate IS NULL OR c.enddate <= :endDate)")
	    List<CouponVO> findByCopcodeAndDateRange(
	        @Param("copcode") String copcode,
	        @Param("startDate") Date startDate,
	        @Param("endDate") Date endDate
	    );
}
