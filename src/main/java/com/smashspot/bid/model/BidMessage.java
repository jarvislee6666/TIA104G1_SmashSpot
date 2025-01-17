package com.smashspot.bid.model;

import java.io.Serializable;
import java.util.List;

public class BidMessage implements Serializable{
	private String type; // 消息類型：例如 "NEW_BID"
    private Integer productId; // 商品ID
    private Integer currentHighestBid; // 當前最高價
    private List<BidVO> bidHistory; // 競標歷史
    private String message; // 額外消息

    // Constructors
    public BidMessage() {}

    public BidMessage(String type, Integer productId, Integer currentHighestBid, List<BidVO> bidHistory) {
        this.type = type;
        this.productId = productId;
        this.currentHighestBid = currentHighestBid;
        this.bidHistory = bidHistory;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(Integer currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public List<BidVO> getBidHistory() {
        return bidHistory;
    }

    public void setBidHistory(List<BidVO> bidHistory) {
        this.bidHistory = bidHistory;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
