package com.smashspot.favoriteproduct.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smashspot.product.model.ProductService;
import com.smashspot.product.model.ProductVO;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteProductService {
	@Autowired
    private FavoriteProductRepository favoriteProductRepository;
	
	@Autowired
    private ProductService productService;
    
    /**
     * 添加收藏
     */
    public boolean addFavorite(Integer userId, Integer productId) {
    	FavoriteProductVO favoriteVO = new FavoriteProductVO(userId, productId);
        return favoriteProductRepository.save(favoriteVO);
    }
    
    /**
     * 取消收藏
     */
    public boolean removeFavorite(Integer userId, Integer productId) {
        return favoriteProductRepository.delete(userId, productId);
    }
    
    /**
     * 檢查商品是否已收藏
     */
    public boolean isFavorite(Integer userId, Integer productId) {
        return favoriteProductRepository.exists(userId, productId);
    }
    
    /**
     * 獲取用戶的所有收藏
     * 只返回狀態為上架中(bidstaid=1)的商品
     */
    public Set<FavoriteProductVO> getFavorites(Integer userId) {
        Set<FavoriteProductVO> favorites = favoriteProductRepository.findByUserId(userId);
        
        // 過濾並只保留狀態為上架中(bidstaid=1)的商品
        return favorites.stream()
            .map(favorite -> {
                ProductVO product = productService.getOneProduct(favorite.getProductId());
                favorite.setProductVO(product);
                return favorite;
            })
            .filter(favorite -> {
                ProductVO product = favorite.getProductVO();
                // 確保商品存在且狀態為上架中(1)
                return product != null && product.getBidstaid() != null && product.getBidstaid() == 1;
            })
            .collect(Collectors.toSet());
    }
    
    /**
     * 獲取用戶收藏數量
     * 只計算狀態為上架中的商品
     */
    public long getFavoriteCount(Integer userId) {
        Set<FavoriteProductVO> favorites = getFavorites(userId);
        return favorites.size();
    }
}
