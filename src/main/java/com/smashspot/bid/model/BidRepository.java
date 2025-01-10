package com.smashspot.bid.model;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class BidRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis key pattern for bid history
    private static final String BID_HISTORY_KEY = "bid:history:%d";  // bid:history:{productId}

    public BidRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 儲存競標出價紀錄
    public void saveBid(BidVO bid) {
        String historyKey = String.format(BID_HISTORY_KEY, bid.getProid());
        redisTemplate.opsForList().rightPush(historyKey, bid);
    }

    // 獲取商品的所有出價記錄
    public List<BidVO> getProductBidHistory(Integer productId) {
        String historyKey = String.format(BID_HISTORY_KEY, productId);
        List<Object> bids = redisTemplate.opsForList().range(historyKey, 0, -1);
        return bids != null ? new ArrayList<>((Collection<? extends BidVO>) (Collection<?>) bids) : new ArrayList<>();
    }

    // 獲取會員對特定商品的出價記錄
    public List<BidVO> getMemberProductBids(Integer memberId, Integer productId) {
        String historyKey = String.format(BID_HISTORY_KEY, productId);
        List<Object> allBids = redisTemplate.opsForList().range(historyKey, 0, -1);
        
        List<BidVO> memberBids = new ArrayList<>();
        if (allBids != null) {
            for (Object bid : allBids) {
                BidVO bidVO = (BidVO) bid;
                if (bidVO.getMemid().equals(memberId)) {
                    memberBids.add(bidVO);
                }
            }
        }
        return memberBids;
    }

    // 清除商品的競標歷史記錄（在商品結標或下架時調用）
    public void clearBidHistory(Integer productId) {
        String historyKey = String.format(BID_HISTORY_KEY, productId);
        redisTemplate.delete(historyKey);
    }
}
