package com.smashspot.bid.model;

import java.util.*;

public interface ProductsDAO_interface {
	public void insert(ProductsVO productsVO); // 賣家上架

	public void update(ProductsVO productsVO); // 隨時更新最高出價

	public void delete(Integer proid); // 好像用不到?

	public ProductsVO findByPrimaryKey(Integer proid); // 管理員

	public List<ProductsVO> findByBidstaid(Integer bidstaid); // 管理員 & 買家總覽(尚未選擇商品分類時)

	public List<ProductsVO> findByBidstaidAndMemid(Integer bidstaid, Integer memid); // 賣家後台瀏覽(上架中、已生成訂單)

	public List<ProductsVO> findByBidstaidAndProclassid(Integer bidstaid, Integer proclassid); // 買家商品瀏覽

	public List<ProductsVO> findByMemid(Integer memid); // 管理員

	public ProductsVO findByProname(String proname); // 管理員

	public List<ProductsVO> getAll(); // 管理員
	// 管理員應該要用全複合查詢
}
