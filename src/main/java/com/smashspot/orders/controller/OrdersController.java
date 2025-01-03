package com.smashspot.orders.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smashspot.orders.model.OrdersService;
import com.smashspot.orders.model.OrdersVO;
import com.smashspot.product.model.ProductVO;

@Controller
public class OrdersController {
	@Autowired
	OrdersService odrsvc;
	
	@GetMapping("/adm/listAllOrders")
	public String listAllProduct(Model model) {
		List<OrdersVO> list = odrsvc.getAll();
    	model.addAttribute("ordersListData",list);
		return "back-end/adm/listAllOrders";
	}
}
