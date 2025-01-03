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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;
//import com.smashspot.location.model.LocationVO;
//import com.smashspot.location.model.LocationService;
import com.smashspot.admin.model.AdmVO;
import com.smashspot.coupon.model.CouponVO;
import com.smashspot.location.model.LocationVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smashspot.admin.model.AdmService;

@Controller
@RequestMapping("/adm")
public class StdmController {

	@Autowired
	StdmService stdmSvc;

//	@Autowired
//	LocationService locSvc;
	
	@Autowired
	AdmService admSvc;
	
	@GetMapping("/getHolidays")//by麒安
	@ResponseBody
	public List<LocalDate> getHolidays(@RequestParam Integer stdmId) {
	    // 取出已設定的休館日
	    return stdmSvc.findAllHolidays(stdmId);
	}
	
	@PostMapping("/updateHolidays")//by麒安
	@ResponseBody
	public Map<String, Object> updateHolidays(@RequestParam Integer stdmId,
	                                          @RequestParam String closedDatesJson) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        // 轉成 List<LocalDate>
	        ObjectMapper mapper = new ObjectMapper();
	        List<String> closedDateStrs = mapper.readValue(closedDatesJson, new TypeReference<List<String>>() {});
	        List<LocalDate> closedDates = closedDateStrs.stream()
	                .map(LocalDate::parse)
	                .collect(Collectors.toList());

	        stdmSvc.updateHolidays(stdmId, closedDates);
	        result.put("success", true);
	        result.put("message", "成功更新休館日設定");
	    } catch(Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}



	@GetMapping("/listAllStdm")
	public String listAllStdm(
	        @RequestParam(required = false) String stdmName,
	        @RequestParam(required = false) String oprSta,
	        @RequestParam(required = false) String admname,
	        Model model) {
    
    Map<String, String[]> map = new HashMap<>();
    if (stdmName != null && !stdmName.trim().isEmpty()) {
        map.put("stdmName", new String[]{stdmName});
    }
    if (oprSta != null && !oprSta.trim().isEmpty()) {
        map.put("oprSta", new String[]{oprSta});
    }
    if (admname != null && !admname.trim().isEmpty()) {
        map.put("admname", new String[]{admname});
    }

    
    List<StadiumVO> stdmList;
    if (map.isEmpty()) {
        stdmList = stdmSvc.getAll();
    } else {
        stdmList = stdmSvc.getAll(map);
    }
    
    // 確保管理員列表資料有被加載
    if (!model.containsAttribute("admListData")) {
        model.addAttribute("admListData", admSvc.getAll());
    }
    
    model.addAttribute("stadiumVO", new StadiumVO());
    model.addAttribute("stdmListData", stdmList);
    return "back-end/adm/listAllStdm";
}

	
	@ModelAttribute("admListData")
	protected List<AdmVO> referenceListData() {
		List<AdmVO> list = admSvc.getAll();
		return list;
	}

	@GetMapping("/addStdm")
	public String addStdm(ModelMap model) {

	    if (!model.containsAttribute("stadiumVO")) {
	        model.addAttribute("stadiumVO", new StadiumVO());
	    }
	    
	    return "back-end/adm/addStdm";
	}

	
	@PostMapping("/insertStdm")
	public String insert(@Valid StadiumVO stdmVO, BindingResult result, ModelMap model,
			@RequestParam("stdmPic") MultipartFile[] parts) throws IOException {

		// result儲存驗證的結果,並去除中upFiles欄位的FieldError紀錄，以防影響最終驗證結果
		result = removeFieldError(stdmVO, result, "stdmPic");

		// 使用者未選擇要上傳的圖片時
		if (parts[0].isEmpty()) { 
			model.addAttribute("errorMessage", "場館照片: 請上傳照片");
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				stdmVO.setStdmPic(buf);
			}
		}
		stdmVO.setOprSta(true);
		stdmVO.setOpenTime(8);
		stdmVO.setCloseTime(22);
		
		if (result.hasErrors() || parts[0].isEmpty()) {
			return "back-end/adm/addStdm";
		}
		try {
	        stdmSvc.addStdm(stdmVO);
	        model.addAttribute("success", "新增成功");
	        return "redirect:/adm/listAllStdm";
	    } catch (Exception e) {
	        model.addAttribute("error", "新增失敗: " + e.getMessage());
	        return "back-end/adm/addStdm";
	    }

	}

	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("stdmId") String stdmId, ModelMap model) {
		StadiumVO stdmVO = stdmSvc.getOneStdm(Integer.valueOf(stdmId));
		model.addAttribute("stadiumVO", stdmVO);
		return "back-end/adm/update_stdm_input"; // 查詢完成後轉交update_stdm_input.html
	}

	
	/*
	 * This method will be called on update_stdm_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("updateStdm")
	public String update(@Valid StadiumVO stdmVO, BindingResult result, ModelMap model,
			@RequestParam("stdmPic") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(stdmVO, result, "stdmPic");

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
			return "back-end/adm/update_stdm_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		stdmSvc.updateStdm(stdmVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		stdmVO = stdmSvc.getOneStdm(Integer.valueOf(stdmVO.getStdmId()));
		model.addAttribute("stadiumVO", stdmVO);
		return "back-end/adm/listOneStdm"; // 修改成功後轉交listOneStdm.html
	}




	
//	@ModelAttribute("locListData")
//	protected List<LocationVO> referenceListData() {
//		// LocService locSvc = new LocService();
//		List<Location> list = locSvc.getAll();
//		return list;
//	}
	
	@ModelAttribute("locMapData") //
	protected Map<Integer, String> referenceMapData() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(1, "台北市");
		map.put(2, "新北市");
		map.put(3, "桃園市");
		map.put(4, "台中市");
		map.put(5, "高雄市");
		map.put(6, "台南市");
		map.put(7, "基隆市");
		map.put(8, "新竹市");
		map.put(9, "嘉義市");
		return map;
	}


	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(StadiumVO stdmVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(stdmVO, "stadiumVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	/*
	 * This method will be called on select_page.html form submission, handling POST request
	 */
//	@PostMapping("listEmps_ByCompositeQuery")
//	public String listAllEmp(HttpServletRequest req, Model model) {
//		Map<String, String[]> map = req.getParameterMap();
//		List<StadiumVO> list = stdmSvc.getAll(map);
//		model.addAttribute("empListData", list); // for listAllStdm.html 第85行用
//		return "back-end/emp/listAllEmp";
//	}

}