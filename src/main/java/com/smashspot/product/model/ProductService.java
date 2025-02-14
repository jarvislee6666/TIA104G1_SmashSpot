package com.smashspot.product.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smashspot.coupon.model.CouponVO;

@Service
public class ProductService {

	@Autowired
	ProductRepository repository;

	public void addProduct(ProductVO productVO) {
		repository.save(productVO);
	}

	public void updateProduct(ProductVO productVO) {
		repository.save(productVO);
	}

	public ProductVO getOneProduct(Integer proid) {
		Optional<ProductVO> optional = repository.findById(proid);
		return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<ProductVO> findByBidsta(Integer bidstaid) {
		return repository.findByBidstaid(bidstaid);
	}

	public List<ProductVO> findMem(Integer memid) {
		return repository.findByMemberVO_Memid(memid);
	}
	
	public List<ProductVO> findMemUnsoldProducts(Integer memid) {
	    List<ProductVO> allProducts = repository.findByMemberVO_Memid(memid);
	    // 過濾掉已有訂單的商品
	    return allProducts.stream()
	            .filter(product -> product.getOrders().isEmpty())
	            .collect(Collectors.toList());
	}

	public List<ProductVO> findByBidstaAndProclass(Integer bidstaid, Integer proclassid) {
		return repository.findByBidstaidAndProductClassVO_Proclassid(bidstaid, proclassid);
	}
	
	public List<ProductVO> getAll() {
		return repository.findAll();
	}
	
	// 添加複合查詢方法
    public List<ProductVO> searchProducts(String proname, Integer sellerId, Integer bidstaid) {
        String searchName = (proname != null && !proname.trim().isEmpty()) ? proname.trim() : null;
        return repository.findWithFilters(searchName, sellerId, bidstaid);
    }
    
    public List<ProductVO> getHotAuctionProducts() {
        return repository.findHotAuctionProducts();
    }
}
