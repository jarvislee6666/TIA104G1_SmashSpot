package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smashspot.product.model.ProductService;
import com.smashspot.product.model.ProductVO;

//@PropertySource("classpath:application.properties") // 於https://start.spring.io建立Spring Boot專案時, application.properties文件預設已經放在我們的src/main/resources 目錄中，它會被自動檢測到
@Controller
public class IndexController_inSpringBoot {
	
	@Autowired
	ProductService productService;
	
	@GetMapping("/")
    public String showIndex(Model model) {
        List<ProductVO> auctionProducts = productService.getHotAuctionProducts();
        model.addAttribute("auctionProducts", auctionProducts);
        return "index";
    }
}