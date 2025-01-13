package com.smashspot.product.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends JpaRepository<ProductVO, Integer>{
	
	public List<ProductVO> findByBidstaid(Integer bidstaid); // 買家總覽(尚未選擇商品分類時)
	
	public List<ProductVO> findByMemberVO_Memid(Integer memid); // 賣家後台瀏覽(所有該賣家刊登的商品)
	
	public List<ProductVO> findByBidstaidAndProductClassVO_Proclassid(Integer bidstaid, Integer proclassid); // 買家依商品分類瀏覽
	
	// 下架 & 結標 更新商品狀態 => 使用內建save處理即可
	
	// 添加後台複合查詢
    @Query("SELECT p FROM ProductVO p " +
           "WHERE (:proname IS NULL OR " +
                 "UPPER(p.proname) LIKE CONCAT('%', UPPER(:proname), '%')) " +
           "AND (:sellerId IS NULL OR p.memberVO.memid = :sellerId) " +
           "AND (:bidstaid IS NULL OR p.bidstaid = :bidstaid)")
    public List<ProductVO> findWithFilters(
        @Param("proname") String proname,
        @Param("sellerId") Integer sellerId,
        @Param("bidstaid") Integer bidstaid
    );
    
 // 查詢熱門競標商品，根據最高出價排序
    @Query(value = "SELECT * FROM product " +
           "WHERE bid_sta_id = 1 " +  // 1代表競標中
           "ORDER BY max_price DESC " +
           "LIMIT 6", nativeQuery = true)
    List<ProductVO> findHotAuctionProducts();
}
