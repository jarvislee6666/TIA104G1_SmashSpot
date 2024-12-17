package com.smashspot.bid.model;

import java.util.List;

public interface BidDAO_interface {
	public void insert(BidVO bidVO);// 出價新增

	public List<BidVO> findByMemid(Integer memid);// 查詢單獨會員出價

	public List<BidVO> getAll();// 查詢全部出價(單一商品頁面、賣家後台)
}
