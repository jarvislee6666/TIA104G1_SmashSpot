package com.smashspot.bid.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
public class BidService {
    
    @Autowired
    private BidRepository bidRepository;

    /**
     * 建立新的競標出價
     * @param memid 會員ID
     * @param proid 商品ID
     * @param bidamt 出價金額
     * @return BidVO 競標紀錄
     */
    public BidVO createBid(Integer memid, Integer proid, Integer bidamt) {
        BidVO bid = new BidVO();
        bid.setMemid(memid);
        bid.setProid(proid);
        bid.setBidamt(bidamt);
        bid.setBidtime(new Timestamp(System.currentTimeMillis()));
        
        // 儲存競標紀錄
        bidRepository.saveBid(bid);
        
        return bid;
    }

    /**
     * 獲取商品的所有競標歷史
     * @param productId 商品ID
     * @return List<BidVO> 競標紀錄列表
     */
    public List<BidVO> getProductBidHistory(Integer productId) {
        return bidRepository.getProductBidHistory(productId);
    }

    /**
     * 獲取特定會員對特定商品的競標紀錄
     * @param memberId 會員ID
     * @param productId 商品ID
     * @return List<BidVO> 競標紀錄列表
     */
    public List<BidVO> getMemberBidsForProduct(Integer memberId, Integer productId) {
        return bidRepository.getMemberProductBids(memberId, productId);
    }

    /**
     * 清除商品的競標紀錄
     * @param productId 商品ID
     */
    public void clearProductBidHistory(Integer productId) {
        bidRepository.clearBidHistory(productId);
    }

    /**
     * 獲取商品的最高出價
     * @param productId 商品ID
     * @return Integer 最高出價金額，如果沒有出價紀錄則返回null
     */
    public Integer getHighestBid(Integer productId) {
        List<BidVO> bidHistory = bidRepository.getProductBidHistory(productId);
        if (bidHistory.isEmpty()) {
            return null;
        }
        
        return bidHistory.stream()
                .map(BidVO::getBidamt)
                .max(Integer::compareTo)
                .orElse(null);
    }

    /**
     * 檢查出價是否有效（高於目前最高價）
     * @param productId 商品ID
     * @param bidAmount 欲出價金額
     * @return boolean 是否為有效出價
     */
    public boolean isValidBid(Integer productId, Integer bidAmount) {
        Integer highestBid = getHighestBid(productId);
        return highestBid == null || bidAmount > highestBid;
    }
}
