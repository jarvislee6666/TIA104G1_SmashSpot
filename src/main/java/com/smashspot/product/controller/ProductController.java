package com.smashspot.product.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smashspot.coupon.model.CouponService;
import com.smashspot.coupon.model.CouponVO;
import com.smashspot.product.model.*;

@Controller
public class ProductController {
	@Autowired
	ProductService proSvc;
	
//	@InitBinder
//    public void initBinder(WebDataBinder binder) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        dateFormat.setLenient(false);
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
//    }

    @GetMapping("/adm/listAllProduct")
	public String listAllProduct(Model model) {
		return "back-end/adm/listAllProduct";
	}
    
    @GetMapping("/client/listAllProductING")
	public String listAllProductING(Model model) {
		return "back-end/client/product/listAllProductING";
	}
    
    @ModelAttribute("productListData")  // 管理員後台 迴圈顯示資料用
	protected List<ProductVO> referenceListData(Model model) {
		
    	List<ProductVO> list = proSvc.getAll();
		return list;
	}
    
    @ModelAttribute("productListDataING")  // 買家首頁 迴圈顯示資料用
	protected List<ProductVO> referenceListDataING(Model model) {
		
    	List<ProductVO> list = proSvc.findByBidsta(1);
		return list;
	}
	
	@GetMapping("addProduct")
	public String addProduct(ModelMap model) {
		ProductVO productVO = new ProductVO();
		model.addAttribute("productVO", productVO);
		return "back-end/product/addProduct";
	}

	@PostMapping("insertProduct")
	public String insert(@Valid ProductVO productVO, BindingResult result, ModelMap model) throws IOException {
		
//		if (proSvc.findByCopcode(couponVO.getCopcode()) != null) {
//	        result.rejectValue("copcode", "error.couponVO", "此 優惠碼 已存在");
//	    }
	    
		if (result.hasErrors()) {
	        return "back-end/product/addProduct";
	    }
	    
	    try {
	    	proSvc.addProduct(productVO);
	    	List<ProductVO> list = proSvc.findByBidsta(1); // 先模擬跳轉前台首頁，實際上應該要在賣家後台顯示其上架的產品
			model.addAttribute("productListDataING", list);
	        model.addAttribute("success", "新增成功");
	        return "redirect:/product/listAllProductING";
	    } catch (Exception e) {
	        model.addAttribute("error", "新增失敗: " + e.getMessage());
	        return "back-end/product/addProduct";
	    }
	}

	@PostMapping("getOneProduct_For_Bid") // 點擊單一商品頁面
	public String getOne_For_Bid(@RequestParam("proid") String proid, ModelMap model) {
		/*************************** 2.開始查詢資料 *****************************************/
		ProductVO productVO = proSvc.getOneProduct(Integer.valueOf(proid));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("productVO", productVO);
		return "back-end/prodcut/getOneProduct"; // 查詢完成後轉交getOneProduct.html
	}

	@PostMapping("/adm/updateProductSta")  // 更改商品狀態，1.後臺下架 2.前台結標
	public String update(@RequestParam("proid") String proid, @RequestParam("bidstaid") Integer bidstaid, ModelMap model) throws IOException {

		/*************************** 2.開始修改資料 *****************************************/
		ProductVO productVO = proSvc.getOneProduct(Integer.valueOf(proid));
		productVO.setBidstaid(bidstaid);
		proSvc.updateProduct(productVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		if(bidstaid == 2) { //模擬前台結標時間到
			model.addAttribute("success", "- (商品已結標)");
			
			return "back-end/product/listAllProductING";
		}
		
		model.addAttribute("success", "- (下架成功)");
		return "back-end/adm/listAllProduct"; //後台手動下架
	}

	public BindingResult removeFieldError(CouponVO couponVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(couponVO, "couponVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}

}
