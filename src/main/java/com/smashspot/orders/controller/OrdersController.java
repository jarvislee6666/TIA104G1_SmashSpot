package com.smashspot.orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smashspot.orders.model.OrdersService;

@Controller
public class OrdersController {
	@Autowired
	OrdersService odrsvc;
	
	@GetMapping("/adm/listAllOrders")
	public String listAllProduct(Model model) {
		return "back-end/adm/listAllOrders";
	}
}
