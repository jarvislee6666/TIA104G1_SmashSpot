package com.smashspot.orders.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrdersRepository extends JpaRepository<OrdersVO, Integer>{
	
	public List<OrdersVO> findByMemid(Integer memid); // 買家須查詢自己得標的訂單
	
	public List<OrdersVO> findByProductVO_Proid(Integer proid); //賣家須查詢自己接收到的訂單
	
	public List<OrdersVO> findByOrdstaid(Integer ordstaid);
	
    public List<OrdersVO> findByMemidAndOrdstaid(Integer memid, Integer ordstaid);
    
    public List<OrdersVO> findByProductVO_ProidAndOrdstaid(Integer proid, Integer ordstaid);
	
	// 後臺複合查詢
    @Query("SELECT o FROM OrdersVO o " +
    	       "WHERE (:proname IS NULL OR " +
    	             "UPPER(o.productVO.proname) LIKE CONCAT('%', UPPER(:proname), '%')) " +
    	       "AND (:buyerId IS NULL OR o.memid = :buyerId) " +
    	       "AND (:sellerId IS NULL OR o.productVO.memberVO.memid = :sellerId) " +
    	       "AND (:ordstaid IS NULL OR o.ordstaid = :ordstaid)")
    	public List<OrdersVO> findWithFilters(
    	    @Param("proname") String proname,
    	    @Param("buyerId") Integer buyerId,
    	    @Param("sellerId") Integer sellerId,
    	    @Param("ordstaid") Integer ordstaid
    	);
}
