package com.smashspot.stadium.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;
//import com.smashspot.location.model.LocationVO;
//import com.smashspot.location.model.LocationService;
import com.smashspot.admin.model.AdmVO;
import com.smashspot.admin.model.AdmService;

@Controller
@RequestMapping("/stdm")
public class StdmController {

	@Autowired
	StdmService stdmSvc;

//	@Autowired
//	LocationService locSvc;
//	
	@Autowired
	AdmService admSvc;

	/*
	 * This method will serve as addStdm.html handler.
	 */
	@GetMapping("addStdm")
	public String addStdm(ModelMap model) {
		StadiumVO stdmVO = new StadiumVO();
		model.addAttribute("stdmVO", stdmVO);
		return "back-end/stdm/addStdm";
	}

	/*
	 * This method will be called on addStdm.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("insert")
	public String insert(@Valid StadiumVO stdmVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(stdmVO, result, "upFiles");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "場館照片: 請上傳照片");
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				stdmVO.setStdmPic(buf);
			}
		}
		if (result.hasErrors() || parts[0].isEmpty()) {
			return "back-end/stdm/addStdm";
		}
		/*************************** 2.開始新增資料 *****************************************/
		stdmSvc.addStdm(stdmVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<StadiumVO> list = stdmSvc.getAll();
		model.addAttribute("stdmListData", list);
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/stdm/listAllStdm"; // 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/stdm/listAllStdm")
	}

	/*
	 * This method will be called on listAllStdm.html form submission, handling POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("stdmId") String stdmId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		StadiumVO stdmVO = stdmSvc.getOneStdm(Integer.valueOf(stdmId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("stdmVO", stdmVO);
		return "back-end/stdm/update_stdm_input"; // 查詢完成後轉交update_stdm_input.html
	}

	/*
	 * This method will be called on update_stdm_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid StadiumVO stdmVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(stdmVO, result, "upFiles");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
			byte[] upFiles = stdmSvc.getOneStdm(stdmVO.getStdmId()).getStdmPic();
			stdmVO.setStdmPic(upFiles);
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] upFiles = multipartFile.getBytes();
				stdmVO.setStdmPic(upFiles);
			}
		}
		if (result.hasErrors()) {
			return "back-end/stdm/update_stdm_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		stdmSvc.updateStdm(stdmVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		stdmVO = stdmSvc.getOneStdm(Integer.valueOf(stdmVO.getStdmId()));
		model.addAttribute("stdmVO", stdmVO);
		return "back-end/stdm/listOneStdm"; // 修改成功後轉交listOneStdm.html
	}

	/*
	 * This method will be called on listAllStdm.html form submission, handling POST request
	 */
	@PostMapping("delete")
	public String delete(@RequestParam("stdmId") String stdmId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始刪除資料 *****************************************/
		stdmSvc.deleteStdm(Integer.valueOf(stdmId));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<StadiumVO> list = stdmSvc.getAll();
		model.addAttribute("stdmListData", list);
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/stdm/listAllStdm"; // 刪除完成後轉交listAllStdm.html
	}

	/*
	 * 第一種作法 Method used to populate the List Data in view. 如 : 
	 * <form:select path="admid" id="admid" items="${admListData}" itemValue="admid" itemLabel="admname" />
	 */
	@ModelAttribute("admListData")
	protected List<AdmVO> referenceListData() {
		// AdmService admSvc = new AdmService();
		List<AdmVO> list = admSvc.getAll();
		return list;
	}
	
//	@ModelAttribute("locListData")
//	protected List<LocationVO> referenceListData() {
//		// LocService locSvc = new LocService();
//		List<Location> list = locSvc.getAll();
//		return list;
//	}

//	/*
//	 * 【 第二種作法 】 Method used to populate the Map Data in view. 如 : 
//	 * <form:select path="deptno" id="deptno" items="${depMapData}" />
//	 */
//	@ModelAttribute("deptMapData") //
//	protected Map<Integer, String> referenceMapData() {
//		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
//		map.put(10, "財務部");
//		map.put(20, "研發部");
//		map.put(30, "業務部");
//		map.put(40, "生管部");
//		return map;
//	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(StadiumVO stdmVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(stdmVO, "stdmVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	/*
	 * This method will be called on select_page.html form submission, handling POST request
	 */
	@PostMapping("listEmps_ByCompositeQuery")
	public String listAllEmp(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<StadiumVO> list = stdmSvc.getAll(map);
		model.addAttribute("empListData", list); // for listAllStdm.html 第85行用
		return "back-end/emp/listAllEmp";
	}

}