package com.smashspot.product.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.smashspot.coupon.model.CouponService;
import com.smashspot.coupon.model.CouponVO;
import com.smashspot.product.model.*;
import com.smashspot.productclass.model.ProductClassService;
import com.smashspot.productclass.model.ProductClassVO;

@Controller
public class ProductController {
	@Autowired
	ProductService proSvc;
	
	@Autowired
	ProductClassService proClassSvc;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    // 增加 MultipartFile 到 byte[] 的轉換器
	    binder.registerCustomEditor(byte[].class, new PropertyEditorSupport() {
	        @Override
	        public void setAsText(String text) {}
	        
	        @Override
	        public void setValue(Object value) {
	            if (value instanceof MultipartFile) {
	                try {
	                    super.setValue(((MultipartFile) value).getBytes());
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    });
	    
	    // 保留原有的 Timestamp 轉換器
	    binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
	        @Override
	        public void setAsText(String text) {
	            LocalDateTime dateTime = LocalDateTime.parse(text);
	            setValue(Timestamp.valueOf(dateTime));
	        }
	    });
	}

    @GetMapping("/adm/listAllProduct")
	public String listAllProduct(Model model) {
		return "back-end/adm/listAllProduct";
	}
    
    @GetMapping("/client/listAllProductING")
	public String listAllProductING(Model model) {
		return "back-end/client/product/listAllProductING";
	}
    
    @GetMapping("/client/memProductList")
	public String memProductList(Model model) {
		return "back-end/client/product/memProductList";
	}
    
    @ModelAttribute("memProductListData")  // 賣家後台 迴圈顯示資料用
    protected List<ProductVO> referenceMemProductListData(Model model) {
        Integer memid = 2; // 暫時模擬會員ID
        List<ProductVO> list = proSvc.findMem(memid);
        return list;
    }
    
    @ModelAttribute("productListData")  // 管理員後台 迴圈顯示資料用
	protected List<ProductVO> referenceListData(Model model) {
		
    	List<ProductVO> list = proSvc.getAll();
		return list;
	}
    
    @ModelAttribute("productListDataING")  // 買家首頁 迴圈顯示資料用
	protected List<ProductVO> referenceListDataING(Model model) {
		
    	List<ProductVO> allActiveProducts = proSvc.findByBidsta(1);
	    model.addAttribute("totalActiveProducts", allActiveProducts.size()); // 每次都可正確顯示全部商品數量
	    
    	List<ProductVO> list = proSvc.findByBidsta(1); // 根據所選分類更新頁面
		return list;
	}
    
    @ModelAttribute("proClassList")  // 買家首頁 迴圈顯示資料用
	protected List<ProductClassVO> referenceProClassList(Model model) {
		
    	List<ProductClassVO> list = proClassSvc.getAll();
		return list;
	}
	
	@GetMapping("/client/addProduct")
	public String addProduct(ModelMap model) {
		ProductVO productVO = new ProductVO();
		productVO.setMemid(2);    // 設定預設值
	    productVO.setBidstaid(1); // 設定預設值
		model.addAttribute("productVO", productVO);
		return "back-end/client/product/addProduct";
	}

	@PostMapping("/client/insertProduct")
	public String insert(@Valid @ModelAttribute ProductVO productVO, BindingResult result, @RequestParam("propic") MultipartFile file, ModelMap model) throws IOException {
        
        if (!file.isEmpty()) {
	        productVO.setPropic(file.getBytes());
	    }
	    
		if (result.hasErrors()) {
			System.out.println("Validation errors: " + result.getAllErrors());
	        return "back-end/client/product/addProduct";
	    }
		
	    
	    try {
	    	proSvc.addProduct(productVO);
	    	return "redirect:/client/listAllProductING";
	    } catch (Exception e) {
	    	e.printStackTrace();
	        model.addAttribute("error", "新增失敗: " + e.getMessage());
	        return "back-end/client/product/addProduct";
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
	
	@GetMapping("/client/listProductByClass/{proclassid}")
	public String listProductByClass(@PathVariable Integer proclassid, Model model) {
	    List<ProductVO> list = proSvc.findByBidstaAndProclass(1, proclassid);
	    model.addAttribute("productListDataING", list);
	    return "back-end/client/product/listAllProductING";
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
	
	@GetMapping("/product/image/{proid}")
	public ResponseEntity<byte[]> getProductImage(@PathVariable Integer proid) {
	    ProductVO productVO = proSvc.getOneProduct(proid);
	    if (productVO != null && productVO.getPropic() != null) {
	        return ResponseEntity.ok()
	                .contentType(MediaType.IMAGE_JPEG)
	                .body(productVO.getPropic());
	    }
	    return ResponseEntity.notFound().build();
	}
	
	

}
