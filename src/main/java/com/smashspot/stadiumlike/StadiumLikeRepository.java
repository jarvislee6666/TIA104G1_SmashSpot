package com.smashspot.stadiumlike;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StadiumLikeRepository {
    // 自定義前綴，避免 key 混用
    private static final String LIKE_KEY_PREFIX = "stadium:like:user:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增收藏（存放在 Set 裡）
     * @param stadiumLikeVO
     * @return 新增是否成功
     */
    public boolean save(StadiumLikeVO stadiumLikeVO) {
        String key = LIKE_KEY_PREFIX + stadiumLikeVO.getMemId(); 
        // 將 stadiumLikeVO 加進該用戶的 Set
        Long result = redisTemplate.opsForSet().add(key, stadiumLikeVO);
        return result != null && result > 0;
    }

    /**
     * 刪除收藏
     */
    public boolean delete(Integer memId, Integer stdmId) {
        String key = LIKE_KEY_PREFIX + memId;
        // 取出該使用者所有的收藏物件，再尋找要刪除的那一筆
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members != null) {
            for (Object member : members) {
                if (member instanceof StadiumLikeVO) {
                    StadiumLikeVO likeVO = (StadiumLikeVO) member;
                    if (likeVO.getStadium() != null 
                        && likeVO.getStadium().getStdmId().equals(stdmId)) {
                        // 找到要刪除的場館 → remove
                        Long removed = redisTemplate.opsForSet().remove(key, likeVO);
                        return removed != null && removed > 0;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判斷某用戶是否已收藏某場館
     */
    public boolean exists(Integer memId, Integer stdmId) {
        String key = LIKE_KEY_PREFIX + memId;
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members != null) {
            for (Object member : members) {
                if (member instanceof StadiumLikeVO) {
                    StadiumLikeVO likeVO = (StadiumLikeVO) member;
                    if (likeVO.getStadium() != null 
                        && likeVO.getStadium().getStdmId().equals(stdmId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 取得該用戶所有收藏
     */
    public Set<StadiumLikeVO> findByUserId(Integer memId) {
        String key = LIKE_KEY_PREFIX + memId;
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members == null) {
            return Set.of();
        }
        return members.stream()
                .filter(obj -> obj instanceof StadiumLikeVO)
                .map(obj -> (StadiumLikeVO) obj)
                .collect(Collectors.toSet());
    }

    /**
     * 取得收藏數量
     */
    public long count(Integer memId) {
        String key = LIKE_KEY_PREFIX + memId;
        Long size = redisTemplate.opsForSet().size(key);
        return size == null ? 0 : size;
    }
}
