package com.smashspot.bid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smashspot.bid.model.BidMessage;
import com.smashspot.bid.model.BidVO;
import java.util.List;

@Controller
public class BidWebSocketController {
	
	private static final Logger logger = LoggerFactory.getLogger(BidWebSocketController.class);
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	// 發送競標更新消息
    public void broadcastBidUpdate(Integer productId, Integer currentHighestBid, List<BidVO> bidHistory) {
    	try {
            BidMessage bidMessage = new BidMessage(
                "NEW_BID",
                productId,
                currentHighestBid,
                bidHistory
            );
            
            String destination = "/topic/product/" + productId;
            logger.info("Broadcasting bid update to {}: {}", destination, bidMessage);
            
            messagingTemplate.convertAndSend(destination, bidMessage);
            logger.info("Broadcast successful");
        } catch (Exception e) {
            logger.error("Error broadcasting bid update: {}", e.getMessage(), e);
        }
    }
    
    // 發送錯誤消息
    public void sendErrorMessage(String sessionId, String message) {
    	try {
            BidMessage errorMessage = new BidMessage();
            errorMessage.setType("ERROR");
            errorMessage.setMessage(message);
            
            logger.info("Sending error message to session {}: {}", sessionId, message);
            
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                errorMessage
            );
        } catch (Exception e) {
            logger.error("Error sending error message: {}", e.getMessage(), e);
        }
    }

}
