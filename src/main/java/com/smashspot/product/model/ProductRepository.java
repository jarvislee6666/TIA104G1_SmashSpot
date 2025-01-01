package com.smashspot.product.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<ProductVO, Integer>{
	
	public List<ProductVO> findByBidstaid(Integer bidstaid); // 買家總覽(尚未選擇商品分類時)
	
	public List<ProductVO> findByMemid(Integer memid); // 賣家後台瀏覽(所有該賣家刊登的商品)
	
	public List<ProductVO> findByBidstaidAndProductClassVO_Proclassid(Integer bidstaid, Integer proclassid); // 買家依商品分類瀏覽
	
	// 下架 & 結標 更新商品狀態 => 使用內建save處理即可
	
	// 管理員後台複合查詢
}
