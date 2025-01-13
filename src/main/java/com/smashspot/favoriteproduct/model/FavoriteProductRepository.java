package com.smashspot.favoriteproduct.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FavoriteProductRepository {
	 private static final String FAVORITE_KEY_PREFIX = "user:favorite:";
	    
	    @Autowired
	    private RedisTemplate<String, Object> redisTemplate;
	    
	    /**
	     * 保存收藏
	     */
	    public boolean save(FavoriteProductVO favoriteProductVO) {
	        String key = FAVORITE_KEY_PREFIX + favoriteProductVO.getUserId();
	        return redisTemplate.opsForSet().add(key, favoriteProductVO) > 0;
	    }
	    
	    /**
	     * 刪除收藏
	     */
	    public boolean delete(Integer userId, Integer productId) {
	        String key = FAVORITE_KEY_PREFIX + userId;
	        Set<Object> members = redisTemplate.opsForSet().members(key);
	        if (members != null) {
	            for (Object member : members) {
	                if (member instanceof FavoriteProductVO) {
	                	FavoriteProductVO favorite = (FavoriteProductVO) member;
	                    if (favorite.getProductId().equals(productId)) {
	                        return redisTemplate.opsForSet().remove(key, member) > 0;
	                    }
	                }
	            }
	        }
	        return false;
	    }
	    
	    /**
	     * 檢查是否已收藏
	     */
	    public boolean exists(Integer userId, Integer productId) {
	        String key = FAVORITE_KEY_PREFIX + userId;
	        Set<Object> members = redisTemplate.opsForSet().members(key);
	        if (members != null) {
	            return members.stream()
	                    .filter(member -> member instanceof FavoriteProductVO)
	                    .map(member -> (FavoriteProductVO) member)
	                    .anyMatch(favorite -> favorite.getProductId().equals(productId));
	        }
	        return false;
	    }
	    
	    /**
	     * 獲取用戶所有收藏
	     */
	    public Set<FavoriteProductVO> findByUserId(Integer userId) {
	        String key = FAVORITE_KEY_PREFIX + userId;
	        Set<Object> members = redisTemplate.opsForSet().members(key);
	        if (members != null) {
	            return members.stream()
	                    .filter(member -> member instanceof FavoriteProductVO)
	                    .map(member -> (FavoriteProductVO) member)
	                    .collect(Collectors.toSet());
	        }
	        return Set.of();
	    }
	    
	    /**
	     * 獲取收藏數量
	     */
	    public long count(Integer userId) {
	        String key = FAVORITE_KEY_PREFIX + userId;
	        return redisTemplate.opsForSet().size(key);
	    }
}
