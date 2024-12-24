package com.smashspot.admin.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.smashspot.admin.model.AdmService;
import com.smashspot.admin.model.AdmVO;



@Controller
@RequestMapping("/adm")
public class AdmController {
	
	@Autowired
	AdmService admSvc;
	
	@GetMapping("/listAllAdm")
		public String listAllAdm(
		        @RequestParam(required = false) String admname,
		        @RequestParam(required = false) String admsta,
		        @RequestParam(required = false) String supvsr,
		        Model model) {
	    
	    Map<String, String[]> map = new HashMap<>();
	    if (admname != null && !admname.trim().isEmpty()) {
	        map.put("admname", new String[]{admname});
	    }
	    if (admsta != null && !admsta.trim().isEmpty()) {
	        map.put("admsta", new String[]{admsta});
	    }
	    if (supvsr != null && !supvsr.trim().isEmpty()) {
	        map.put("supvsr", new String[]{supvsr});
	    }
	    
	    List<AdmVO> admList;
	    if (map.isEmpty()) {
	        admList = admSvc.getAll();
	    } else {
	        admList = admSvc.getAll(map);
	    }
	    
	    model.addAttribute("admVO", new AdmVO());
	    model.addAttribute("admList", admList);
	    return "back-end/adm/listAllAdm";
	}
	@GetMapping("/addAdm")
	public String addAdm(ModelMap model) {
		AdmVO admVO = new AdmVO();
		model.addAttribute("admVO", admVO);
		return "back-end/adm/addAdm";
	}
	
	@PostMapping("insert")
	public String insert(@Valid AdmVO admVO, BindingResult result, ModelMap model
			) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		
		/*************************** 2.開始新增資料 *****************************************/
		// EmpService empSvc = new EmpService();
		admSvc.addAdm(admVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<AdmVO> list = admSvc.getAll();
		model.addAttribute("admListData", list);
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/emp/listAllAdm"; // 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/emp/listAllEmp")
	}
	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("admid") String admid, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// EmpService empSvc = new EmpService();
		AdmVO admVO = admSvc.getOneAdm(Integer.valueOf(admid));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("admVO", admVO);
		return "back-end/adm/update_adm_input"; // 查詢完成後轉交update_emp_input.html
	}
	
	@PostMapping("update")
	public String update(@Valid AdmVO admVO, BindingResult result, ModelMap model)
			throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		if (result.hasErrors()) {
			return "back-end/adm/update_adm_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		// EmpService empSvc = new EmpService();
		admSvc.updateAdm(admVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		admVO = admSvc.getOneAdm(Integer.valueOf(admVO.getAdmid()));
		model.addAttribute("admVO", admVO);
		return "back-end/adm/listAllAdm"; // 修改成功後轉交listAllAdm.html
	}

	public BindingResult removeFieldError(AdmVO admVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(admVO, "admVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	@PostMapping("listAdms_ByCompositeQuery")
	public String listAllEmp(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<AdmVO> list = admSvc.getAll(map);
		model.addAttribute("admListData", list); // for listAllAdm.html 第85行用
		return "back-end/emp/listAllAdm";
	}
	
	

}
