package com.smashspot.bid.model;

import java.util.List;

public interface OrdersDAO_interface {
	public void update(OrdersVO ordersVO);// 訂單狀態更新(付款、訂單完成、取消)

	public void insert(OrdersVO ordersVO);// 新增訂單

	public List<OrdersVO> findByMemid(Integer memid);// 查詢賣家訂單

	public List<OrdersVO> getAll();// 查詢所有訂單(後台)
}
