package com.smashspot.orders.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.smashspot.orders.model.OrdersService;
import com.smashspot.orders.model.OrdersVO;
import com.smashspot.product.model.ProductService;
import com.smashspot.product.model.ProductVO;

@Controller
public class OrdersController {
	@Autowired
	OrdersService odrsvc;
	
	@Autowired
	ProductService proSvc;
	
	@GetMapping("/adm/listAllOrders")
	public String listAllProduct(Model model) {
		List<OrdersVO> list = odrsvc.getAll();
    	model.addAttribute("ordersListData",list);
		return "back-end/adm/listAllOrders";
	}
	
	@GetMapping("/client/orders/DPstep1/{proid}")
	public String DPstep1(@PathVariable Integer proid, Model model) {
		// 獲取商品資訊
	    ProductVO productVO = proSvc.getOneProduct(proid);
	    model.addAttribute("productVO", productVO);
	    return "back-end/client/orders/DPstep1";
	}
	
}
