package com.smashspot.product.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.smashspot.bid.model.BidService;
import com.smashspot.bid.model.BidVO;
import com.smashspot.coupon.model.CouponVO;
import com.smashspot.member.model.MemberVO;
import com.smashspot.product.model.*;
import com.smashspot.productclass.model.ProductClassService;
import com.smashspot.productclass.model.ProductClassVO;

@Controller
public class ProductController {
	@Autowired
	ProductService proSvc;
	
	@Autowired
	ProductClassService proClassSvc;
	
	@Autowired
    private BidService bidService;
	
	@ModelAttribute("proClassList")  // 買家首頁 迴圈顯示資料用
	protected List<ProductClassVO> referenceProClassList(Model model) {
		
    	List<ProductClassVO> list = proClassSvc.getAll();
		return list;
	}
	
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
	    
	    binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
	        @Override
	        public void setAsText(String text) {
	            try {
	                if (text != null && !text.trim().isEmpty()) {
	                    LocalDateTime dateTime = LocalDateTime.parse(text);
	                    setValue(Timestamp.valueOf(dateTime));
	                } else {
	                    setValue(null);
	                }
	            } catch (Exception e) {
	                setValue(null);
	            }
	        }
	    });
	}

	@GetMapping("/adm/listAllProduct")
	public String listAllProduct(
	        @RequestParam(required = false) String proname,
	        @RequestParam(required = false) Integer sellerId,
	        @RequestParam(required = false) Integer bidstaid,
	        Model model) {
	        
	    List<ProductVO> list;
	    
	    // 判斷是否有查詢條件
	    if (proname != null || sellerId != null || bidstaid != null) {
	        list = proSvc.searchProducts(proname, sellerId, bidstaid);
	    } else {
	        list = proSvc.getAll();
	    }
	    
	    // 加入資料到 Model
	    model.addAttribute("productListData", list);
	    model.addAttribute("searchProname", proname);
	    model.addAttribute("searchSellerId", sellerId);
	    model.addAttribute("searchBidstaid", bidstaid);
	    
	    return "back-end/adm/listAllProduct";
	}
    
    @GetMapping("/client/listAllProductING")
    public String listAllProductING(
            @RequestParam(required = false) Integer proclassid,
            @RequestParam(defaultValue = "default") String sort,
            Model model) {
        
        List<ProductVO> list;
        if (proclassid != null) {
            // 如果有分類 ID，則按分類篩選
            list = proSvc.findByBidstaAndProclass(1, proclassid);
        } else {
            // 否則獲取所有商品
            list = proSvc.findByBidsta(1);
        }
        
        // 根據排序參數進行排序
        switch (sort) {
            case "latest":
                list.sort((a, b) -> b.getProid().compareTo(a.getProid()));
                break;
            case "endingSoon":
                list.sort((a, b) -> {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    boolean aEnded = a.getEndtime().before(now);
                    boolean bEnded = b.getEndtime().before(now);
                    
                    if (aEnded && !bEnded) return 1;
                    if (!aEnded && bEnded) return -1;
                    if (aEnded && bEnded) return 0;
                    
                    return a.getEndtime().compareTo(b.getEndtime());
                });
                break;
            default:
                // 預設排序邏輯
                break;
        }
        
        model.addAttribute("productListDataING", list);
        model.addAttribute("totalActiveProducts", proSvc.findByBidsta(1).size());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentProclassid", proclassid);
        
        return "back-end/client/product/listAllProductING";
    }
    
    @GetMapping("/client/getOneProduct/{proid}")
    public String getOneProduct(@PathVariable Integer proid, Model model, HttpSession session) {
        ProductVO productVO = proSvc.getOneProduct(proid);
        model.addAttribute("productVO", productVO);
        // 將結標時間轉換為毫秒數傳給前端
        model.addAttribute("endTimeMillis", productVO.getEndtime().getTime());
        
        // 獲取競標歷史記錄
        List<BidVO> bidHistory = bidService.getProductBidHistory(proid);
        model.addAttribute("bidHistory", bidHistory);
        
        // 添加當前登入會員資訊的判斷
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        boolean isOwner = loginMember != null &&                        
                          loginMember.getMemid().equals(productVO.getMemberVO().getMemid());
        model.addAttribute("isOwner", isOwner);

        return "back-end/client/product/getOneProduct";
    }
    
    @GetMapping("/client/memProductList")
    public String memProductList(Model model, HttpSession session) {
        MemberVO mem = (MemberVO) session.getAttribute("login");
        
        List<ProductVO> list = proSvc.findMemUnsoldProducts(mem.getMemid());
        model.addAttribute("memProductListData", list);
        return "back-end/client/product/memProductList";
    }
	
	@GetMapping("/client/addProduct")
	public String addProduct(ModelMap model, HttpSession session) {
		MemberVO mem = (MemberVO) session.getAttribute("login");
		ProductVO productVO = new ProductVO();
		productVO.setMemberVO(mem);
	    productVO.setBidstaid(1); // 剛上架的狀態一定是 1.上架中
		model.addAttribute("productVO", productVO);
		return "back-end/client/product/addProduct";
	}
	
	@PostMapping("/client/insertProduct") // 避免圖片因頁面錯誤驗證刷新而丟失，改用ajax
	@ResponseBody  // 這個註解很重要
	public Map<String, Object> insert(@Valid @ModelAttribute ProductVO productVO, 
	                                BindingResult result,
	                                @RequestParam("propic") MultipartFile file) {
	    Map<String, Object> response = new HashMap<>();
	    Map<String, String> errors = new HashMap<>();
	    
	    // 如果有 memberVO.memid，則設置完整的 MemberVO
	    if (productVO.getMemberVO() != null && productVO.getMemberVO().getMemid() != null) {
	        MemberVO memberVO = new MemberVO();
	        memberVO.setMemid(productVO.getMemberVO().getMemid());
	        productVO.setMemberVO(memberVO);
	    }
	    
	    // 驗證結標時間
	    if (productVO.getEndtime() == null) {
	        errors.put("endtime", "請選擇結標時間");
	    } else if (productVO.getEndtime().before(new Timestamp(System.currentTimeMillis()))) {
	        errors.put("endtime", "結標時間必須在現在時間之後");
	    }
	    
	    // 處理圖片上傳
	    if (!file.isEmpty()) {
	        try {
	            productVO.setPropic(file.getBytes());
	        } catch (IOException e) {
	            errors.put("propic", "圖片上傳失敗");
	        }
	    } else {
	        errors.put("propic", "請選擇圖片");
	    }
	    
	    // 價格比較驗證
	    if (productVO.getBaseprice() != null && productVO.getPurprice() != null) {
	        if (productVO.getBaseprice() >= productVO.getPurprice()) {
	            errors.put("validPriceRange", "底標價必須小於直購價");
	        }
	    }
	    
	    // 處理其他驗證錯誤
	    if (result.hasErrors()) {
	        result.getFieldErrors().forEach(error -> {
	            // 檢查是否是價格比較的錯誤
	            if ("validPriceRange".equals(error.getField())) {
	                errors.put("validPriceRange", error.getDefaultMessage());
	            } else {
	                errors.put(error.getField(), error.getDefaultMessage());
	            }
	        });
	    }
	    
	    // 如果有任何錯誤
	    if (!errors.isEmpty()) {
	        response.put("success", false);
	        response.put("errors", errors);
	        return response;
	    }
	    
	    // 嘗試保存數據
	    try {
	        proSvc.addProduct(productVO);
	        response.put("success", true);
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "新增失敗: " + e.getMessage());
	    }
	    
	    return response;
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
	public String listProductByClass(
	        @PathVariable Integer proclassid,
	        @RequestParam(defaultValue = "default") String sort,
	        Model model) {
	    
	    // 重定向到主列表頁面，保留分類和排序參數
	    return "redirect:/client/listAllProductING?proclassid=" + proclassid + "&sort=" + sort;
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
