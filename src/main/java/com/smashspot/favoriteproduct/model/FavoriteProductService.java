package com.smashspot.favoriteproduct.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smashspot.product.model.ProductService;
import com.smashspot.product.model.ProductVO;

import java.util.Set;

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
     */
    public Set<FavoriteProductVO> getFavorites(Integer userId) {
        Set<FavoriteProductVO> favorites = favoriteProductRepository.findByUserId(userId);
        // 為每個收藏加載對應的商品信息
        for (FavoriteProductVO favorite : favorites) {
            ProductVO product = productService.getOneProduct(favorite.getProductId());
            favorite.setProductVO(product);
        }
        return favorites;
    }
    
    /**
     * 獲取用戶收藏數量
     */
    public long getFavoriteCount(Integer userId) {
        return favoriteProductRepository.count(userId);
    }
}
