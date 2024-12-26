package com.smashspot.admin.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(java.sql.Date.class, new CustomDateEditor(dateFormat, true) {
	        @Override
	        public void setValue(Object value) {
	            if (value instanceof java.util.Date) {
	                super.setValue(new java.sql.Date(((java.util.Date) value).getTime()));
	            } else {
	                super.setValue(value);
	            }
	        }
	    });
	}
	
	@PostMapping("insert")
	public String insert(@Valid AdmVO admVO, BindingResult result, ModelMap model,
	                    @RequestParam("confirmPassword") String confirmPassword) {
	    
		if (admSvc.findByEmail(admVO.getAdmemail()) != null) {
	        result.rejectValue("admemail", "error.admVO", "此 Email 已存在");
	    }
	    
	    if (admSvc.findByPhone(admVO.getAdmphone()) != null) {
	        result.rejectValue("admphone", "error.admVO", "此電話號碼已存在");
	    }
	    
	    if (admSvc.findByPassword(admVO.getAdmpassword()) != null) {
	        result.rejectValue("admpassword", "error.admVO", "此密碼已存在");
	    }
	    
	    if (!admVO.getAdmpassword().equals(confirmPassword)) {
	        result.rejectValue("admpassword", "error.admVO", "密碼與確認密碼不符");
	    }
	    
	    admVO.setAdmsta(true);
	    
	    if (result.hasErrors()) {
	        return "back-end/adm/addAdm";
	    }
	    
	    try {
	        admSvc.addAdm(admVO);
	        model.addAttribute("success", "新增成功");
	        return "redirect:/adm/listAllAdm";
	    } catch (Exception e) {
	        model.addAttribute("error", "新增失敗: " + e.getMessage());
	        return "back-end/adm/addAdm";
	    }
	}
	
	@GetMapping("/updateAdm")
	public String getOne_For_Update(@RequestParam("admid") String admid, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// EmpService empSvc = new EmpService();
		AdmVO admVO = admSvc.getOneAdm(Integer.valueOf(admid));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("admVO", admVO);
		return "back-end/adm/updateAdm"; // 查詢完成後轉交update_emp_input.html
	}
	
	@PostMapping("update")
	public String update(@Valid AdmVO admVO, BindingResult result, ModelMap model){

		if (result.hasErrors()) {
	        return "back-end/adm/updateAdm";
	    }
	    
		AdmVO original = admSvc.getOneAdm(admVO.getAdmid());
	    original.setAdmsta(admVO.getAdmsta());
	    original.setSupvsr(admVO.getSupvsr());
	    
	    admSvc.updateAdm(original);
	    return "redirect:/adm/listAllAdm";
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
	
	@GetMapping("/login")
    public String loginPage() {
        return "back-end/adm/loginAdm";
    }
	
	@PostMapping("/login")
    public String login(@RequestParam String admemail, 
                       @RequestParam String admpassword,
                       HttpSession session,
                       Model model) {
        AdmVO adm = admSvc.login(admemail, admpassword);
        if (adm != null) {
            session.setAttribute("loginAdm", adm);
            return "redirect:/adm/listAllAdm";
        }
        model.addAttribute("error", "帳號或密碼錯誤");
        return "back-end/adm/loginAdm";
    }

}
