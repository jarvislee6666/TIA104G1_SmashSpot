package com.smashspot.product.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.product.model.ProductService;
import com.smashspot.product.model.ProductVO;

import java.sql.Timestamp;
import java.util.List;

@Component
public class AuctionScheduler {
    
    @Autowired
    private ProductService productService;
    
    @Scheduled(fixedRate = 60000) // 每分鐘執行一次
    @Transactional
    public void checkAndUpdateExpiredAuctions() {
        // 獲取所有狀態為1(競標中)的商品
        List<ProductVO> activeProducts = productService.findByBidsta(1);
        
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        
        for (ProductVO product : activeProducts) {
            // 檢查是否已到結標時間
            if (product.getEndtime() != null && product.getEndtime().before(currentTime)) {
                // 更新商品狀態為已結標(2)
                product.setBidstaid(2);
                productService.updateProduct(product);
            }
        }
    }
}
