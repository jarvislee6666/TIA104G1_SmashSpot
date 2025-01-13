package com.smashspot.coupon.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.smashspot.coupon.model.*;

@Controller
@RequestMapping("/adm")
public class CouponController {

	@Autowired
	CouponService copSvc;
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

	@GetMapping("listAllCoupon")
	public String listAllCoupon(
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        Model model) {
	    
	    try {
	        List<CouponVO> coupons;
	        
	        if ((keyword == null || keyword.trim().isEmpty()) && 
	            (startDate == null || startDate.trim().isEmpty()) && 
	            (endDate == null || endDate.trim().isEmpty())) {
	            
	            coupons = copSvc.getAll();
	        } else {
	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	            Date start = (startDate != null && !startDate.trim().isEmpty()) ? dateFormat.parse(startDate) : null;
	            Date end = (endDate != null && !endDate.trim().isEmpty()) ? dateFormat.parse(endDate) : null;
	            
	            coupons = copSvc.searchCoupons(keyword, start, end);
	        }
	        
	        model.addAttribute("couponListData", coupons);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("startDate", startDate);
	        model.addAttribute("endDate", endDate);
	        
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", "查詢發生錯誤");
	        model.addAttribute("couponListData", copSvc.getAll());
	    }
	    
	    return "back-end/adm/listAllCoupon";
	}
    
    @ModelAttribute("couponListData")  // for listAllCoupon.html 迴圈顯示資料用
	protected List<CouponVO> referenceListData(Model model) {
		
    	List<CouponVO> list = copSvc.getAll();
		return list;
	}
	
	@GetMapping("addCoupon")
	public String addCoupon(ModelMap model) {
		CouponVO couponVO = new CouponVO();
		model.addAttribute("couponVO", couponVO);
		return "back-end/adm/addCoupon";
	}

	@PostMapping("insertCoupon")
	public String insert(@Valid CouponVO couponVO, BindingResult result, ModelMap model) {
	    if (result.hasErrors()) {
	        return "back-end/adm/addCoupon";
	    }
	    
	    try {
	        copSvc.addCoupon(couponVO);
	        return "redirect:/adm/listAllCoupon";
	    } catch (Exception e) {
	        model.addAttribute("message", "新增失敗: " + e.getMessage());
	        return "back-end/adm/addCoupon";
	    }
	}

	@PostMapping("getOneCoupon_For_Update")
	public String getOne_For_Update(@RequestParam("copid") String copid, ModelMap model) {
		/*************************** 2.開始查詢資料 *****************************************/
		CouponVO couponVO = copSvc.getOneCoupon(Integer.valueOf(copid));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("couponVO", couponVO);
		return "back-end/adm/updateCoupon"; // 查詢完成後轉交update_coupon_input.html
	}

	@PostMapping("updateCoupon")
	public String update(@Valid CouponVO couponVO, BindingResult result, ModelMap model) throws IOException {
	    if (result.hasErrors()) {
	        return "back-end/adm/updateCoupon";
	    }

	    try {
	        copSvc.updateCoupon(couponVO);
	        model.addAttribute("success", "修改成功");
	        return "redirect:/adm/listAllCoupon";
	    } catch (Exception e) {
	        model.addAttribute("error", "修改失敗: " + e.getMessage());
	        return "back-end/adm/updateCoupon";
	    }
	}
	
	
	
	//結帳時驗證優惠碼
	@PostMapping("/validateCoupon")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam String code, @RequestParam Integer originalPrice) {
	    
		Map<String, Object> response = new HashMap<>();
	    
		try {
            CouponVO coupon = copSvc.findByCopcode(code);
            
            if (coupon == null) {
                response.put("valid", false);
                response.put("message", "優惠碼不存在");
                return ResponseEntity.ok(response);
            }
            
            Date currentDate = new Date();
            if (currentDate.after(coupon.getEnddate())) {
                response.put("valid", false);
                response.put("message", "優惠碼已過期");
                return ResponseEntity.ok(response);
            }
            
            // 計算折扣後金額
            int discountedPrice = originalPrice - coupon.getDiscount();
            if (discountedPrice < 0) {
                discountedPrice = 0;
            }
            
            response.put("valid", true);
            response.put("message", "優惠碼套用成功");
            response.put("discount", coupon.getDiscount());
            response.put("discountedPrice", discountedPrice);
            response.put("copid", coupon.getCopid());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "系統錯誤，請稍後再試");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
	}

}
