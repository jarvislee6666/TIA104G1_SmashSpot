package com.smashspot.orders.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface OrdersRepository extends JpaRepository<OrdersVO, Integer>{
	
	public List<OrdersVO> findByMemid(Integer memid); // 買家須查詢自己得標的訂單
	
	public List<OrdersVO> findByProductVO_Proid(Integer proid); //賣家須查詢自己接收到的訂單
	
	public List<OrdersVO> findByOrdstaid(Integer ordstaid);
	
    public List<OrdersVO> findByMemidAndOrdstaid(Integer memid, Integer ordstaid);
    
    public List<OrdersVO> findByProductVO_ProidAndOrdstaid(Integer proid, Integer ordstaid);
	
	// 後臺複合查詢??
}
