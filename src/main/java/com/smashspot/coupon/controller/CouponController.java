package com.smashspot.coupon.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.smashspot.coupon.model.*;

@Controller
@RequestMapping("/coupon")
public class CouponController {

	@Autowired
	CouponService copSvc;

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
	public String addEmp(ModelMap model) {
		CouponVO couponVO = new CouponVO();
		model.addAttribute("couponVO", couponVO);
		return "back-end/coupon/addCoupon";
	}

	@PostMapping("insert")
	public String insert(@Valid CouponVO couponVO, ModelMap model) throws IOException {

		/*************************** 2.開始新增資料 *****************************************/
		copSvc.addCoupon(couponVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<CouponVO> list = copSvc.getAll();
		model.addAttribute("couponListData", list);
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/coupon/listAllCoupon"; // 新增成功後重導
	}

	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("copid") String copid, ModelMap model) {
		/*************************** 2.開始查詢資料 *****************************************/
		CouponVO couponVO = copSvc.getOneCoupon(Integer.valueOf(copid));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("couponVO", couponVO);
		return "back-end/coupon/update_coupon_input"; // 查詢完成後轉交update_coupon_input.html
	}

	@PostMapping("update")
	public String update(@Valid CouponVO couponVO, ModelMap model) throws IOException {

		/*************************** 2.開始修改資料 *****************************************/
		copSvc.updateCoupon(couponVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		couponVO = copSvc.getOneCoupon(Integer.valueOf(couponVO.getCopid()));
		model.addAttribute("couponVO", couponVO);
		return "back-end/coupon/listAllCoupon";
	}

	@PostMapping("delete")
	public String delete(@RequestParam("copid") String copid, ModelMap model) {
		/*************************** 2.開始刪除資料 *****************************************/
		copSvc.deleteCoupon(Integer.valueOf(copid));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<CouponVO> list = copSvc.getAll();
		model.addAttribute("couponListData", list);
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/coupon/listAllCoupon";
	}

}
