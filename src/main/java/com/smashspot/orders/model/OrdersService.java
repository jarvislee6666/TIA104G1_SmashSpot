package com.smashspot.orders.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {
    
    @Autowired
    OrdersRepository repository;
    
    public void addOrder(OrdersVO ordersVO) {
        repository.save(ordersVO);
    }
    
    public void updateOrder(OrdersVO ordersVO) {
        repository.save(ordersVO);
    }
    
    public OrdersVO getOneOrder(Integer ordid) {
        Optional<OrdersVO> optional = repository.findById(ordid);
        return optional.orElse(null);
    }
    
    // 買家查詢自己的訂單
    public List<OrdersVO> findByMem(Integer memid) {
        return repository.findByMemid(memid);
    }
    
    // 賣家查詢自己收到的訂單
    public List<OrdersVO> findByProduct(Integer proid) {
        return repository.findByProductVO_Proid(proid);
    }
    
    // 根據訂單狀態查詢
    public List<OrdersVO> findByOrdsta(Integer ordstaid) {
        return repository.findByOrdstaid(ordstaid);
    }
    
    // 買家依訂單狀態查詢
    public List<OrdersVO> findByMemAndOrdsta(Integer memid, Integer ordstaid) {
        return repository.findByMemidAndOrdstaid(memid, ordstaid);
    }
    
    // 賣家依商品和訂單狀態查詢
    public List<OrdersVO> findByProductAndOrdsta(Integer proid, Integer ordstaid) {
        return repository.findByProductVO_ProidAndOrdstaid(proid, ordstaid);
    }
    
    public List<OrdersVO> getAll() {
        return repository.findAll();
    }
}
