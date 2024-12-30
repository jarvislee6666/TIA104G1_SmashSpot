package com.smashspot.coupon.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
	public String listAllCoupon(Model model) {
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
	public String insert(@Valid CouponVO couponVO, BindingResult result,ModelMap model) throws IOException {
		
		if (copSvc.findByCopcode(couponVO.getCopcode()) != null) {
	        result.rejectValue("copcode", "error.couponVO", "此 優惠碼 已存在");
	    }
	    
		if (result.hasErrors()) {
	        return "back-end/adm/addCoupon";
	    }
	    
	    try {
	    	copSvc.addCoupon(couponVO);
	    	List<CouponVO> list = copSvc.getAll();
			model.addAttribute("couponListData", list);
	        model.addAttribute("success", "新增成功");
	        return "redirect:/adm/listAllCoupon";
	    } catch (Exception e) {
	        model.addAttribute("error", "新增失敗: " + e.getMessage());
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
	public String update(@Valid CouponVO couponVO, ModelMap model) throws IOException {

		/*************************** 2.開始修改資料 *****************************************/
		copSvc.updateCoupon(couponVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		
		return "back-end/adm/listAllCoupon";
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
	
	@PostMapping("deleteCoupon")
	public String delete(@RequestParam("copid") String copid, ModelMap model) {
		/*************************** 2.開始刪除資料 *****************************************/
		copSvc.deleteCoupon(Integer.valueOf(copid));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<CouponVO> list = copSvc.getAll();
		model.addAttribute("couponListData", list);
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/adm/listAllCoupon";
	}

}
