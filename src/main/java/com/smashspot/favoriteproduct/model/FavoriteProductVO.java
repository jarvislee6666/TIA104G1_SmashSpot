package com.smashspot.favoriteproduct.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.smashspot.product.model.ProductVO;

public class FavoriteProductVO {
private static final long serialVersionUID = 1L;
    
    private Integer userId;
    private Integer productId;
    private Timestamp favoriteTime;  // 收藏時間
    private ProductVO productVO;

    public FavoriteProductVO() {
    }

    public FavoriteProductVO(Integer userId, Integer productId) {
        this.userId = userId;
        this.productId = productId;
        this.favoriteTime = new Timestamp(System.currentTimeMillis());
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Timestamp getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(Timestamp favoriteTime) {
        this.favoriteTime = favoriteTime;
    }

    public ProductVO getProductVO() {
        return productVO;
    }

    public void setProductVO(ProductVO productVO) {
        this.productVO = productVO;
    }

    @Override
    public String toString() {
        return "FavoriteVO [userId=" + userId + ", productId=" + productId + 
               ", favoriteTime=" + favoriteTime + ", productVO=" + productVO + "]";
    }
}
