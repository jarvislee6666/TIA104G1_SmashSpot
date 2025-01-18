package com.smashspot.stadium.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	        @RequestParam(defaultValue = "1") int page, // Current page
	        @RequestParam(defaultValue = "6") int size, // Page size
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

	    List<StadiumVO> stdmList = map.isEmpty() ? stdmSvc.getAll() : stdmSvc.getAll(map);

	    // Pagination logic
	    int totalRecords = stdmList.size();
	    int totalPages = (int) Math.ceil((double) totalRecords / size);
	    int startIndex = (page - 1) * size;
	    int endIndex = Math.min(startIndex + size, totalRecords);

	    // Handle boundary cases
	    if (startIndex > totalRecords) {
	        startIndex = totalRecords - size;
	        endIndex = totalRecords;
	    }

	    List<StadiumVO> paginatedList = stdmList.subList(startIndex, endIndex);

	    model.addAttribute("stadiumVO", new StadiumVO());
	    model.addAttribute("stdmListData", paginatedList);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("pageSize", size);
	    model.addAttribute("totalRecords", totalRecords);

	    if (!model.containsAttribute("admListData")) {
	        model.addAttribute("admListData", admSvc.getAll());
	    }

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

	
	@ModelAttribute("locMapData")
	protected Map<Integer, String> referenceMapData() {
	    Map<Integer, String> map = new LinkedHashMap<Integer, String>();
	    map.put(1,  "台北市");
	    map.put(2,  "新北市");
	    map.put(3,  "桃園市");
	    map.put(4,  "台中市");
	    map.put(5,  "台南市");
	    map.put(6,  "高雄市");
	    map.put(7,  "基隆市");
	    map.put(8,  "新竹市");
	    map.put(9,  "嘉義市");
	    map.put(10, "宜蘭縣");
	    map.put(11, "新竹縣");
	    map.put(12, "苗栗縣");
	    map.put(13, "彰化縣");
	    map.put(14, "南投縣");
	    map.put(15, "雲林縣");
	    map.put(16, "嘉義縣");
	    map.put(17, "屏東縣");
	    map.put(18, "台東縣");
	    map.put(19, "花蓮縣");
	    map.put(20, "澎湖縣");
	    map.put(21, "金門縣");
	    map.put(22, "連江縣");
	    return map;
	}
	
	@GetMapping("/updateStdm")
	public String updateStdmForm(@RequestParam("stdmId") Integer stdmId, ModelMap model) {
	    // 1. 查詢指定場館資料
	    StadiumVO stdmVO = stdmSvc.getOneStdm(stdmId);
	    if (stdmVO == null) {
	        model.addAttribute("errorMessage", "查無此場館資料");
	        return "redirect:/adm/listAllStdm";
	    }

	    // 2. 如果需要顯示舊圖片，可在這裡轉成 Base64 (若 StadiumVO 有 imageBase64 屬性)
	    /*
	    if (stdmVO.getStdmPic() != null) {
	        String base64Str = Base64.getEncoder().encodeToString(stdmVO.getStdmPic());
	        stdmVO.setImageBase64(base64Str);
	    }
	    */

	    // 3. 放進 model
	    model.addAttribute("stadiumVO", stdmVO);

	    // 4. 回傳到「編輯表單」的 Thymeleaf 頁面 (您想用哪個都行)
	    return "back-end/adm/updateStdm";
	}

	@PostMapping("/updateStdm")
	public String updateStdmProcess(
	        @Valid @ModelAttribute("stadiumVO") StadiumVO stdmVO, 
	        BindingResult result, 
	        ModelMap model,
	        @RequestParam("stdmPic") MultipartFile[] parts
	) throws IOException {

	    /*************************** 1.移除圖片欄位的驗證錯誤(若有) ***************************/
	    result = removeFieldError(stdmVO, result, "stdmPic");

	    /*************************** 2.處理圖片上傳與舊圖保留 ***************************/
	    if (parts == null || parts.length == 0 || parts[0].isEmpty()) {
	        // 沒有上傳新圖 -> 保留舊圖
	        byte[] oldPic = stdmSvc.getOneStdm(stdmVO.getStdmId()).getStdmPic();
	        stdmVO.setStdmPic(oldPic);
	    } else {
	        // 有上傳新圖 -> 取第一張(或多張)
	        for (MultipartFile multipartFile : parts) {
	            byte[] upFiles = multipartFile.getBytes();
	            stdmVO.setStdmPic(upFiles);
	        }
	    }

	    /*************************** 3.若有其他欄位驗證錯誤，回到表單 ***************************/
	    if (result.hasErrors()) {
	        return "back-end/adm/updateStdm";
	    }

	    /*************************** 4.進行更新 ***************************/
//	    stdmVO.setOprSta(true);
	    stdmSvc.updateStdm(stdmVO);

	    /*************************** 5.更新完成，導向或轉交到成功頁面 ***************************/
	    model.addAttribute("success", "- (修改成功)");
	    StadiumVO updatedVO = stdmSvc.getOneStdm(stdmVO.getStdmId());
	    model.addAttribute("stadiumVO", updatedVO);

	    // 您可以選擇回「顯示單筆資料」或「列表頁」等
	    return "redirect:/adm/listAllStdm";
	}

	/** 移除指定欄位錯誤的工具方法 **/
	private BindingResult removeFieldError(StadiumVO stdmVO, BindingResult result, String removedField) {
	    List<FieldError> errorsToKeep = result.getFieldErrors().stream()
	            .filter(fieldError -> !fieldError.getField().equals(removedField))
	            .collect(Collectors.toList());
	    BindingResult newResult = new BeanPropertyBindingResult(stdmVO, "stadiumVO");
	    for (FieldError err : errorsToKeep) {
	        newResult.addError(err);
	    }
	    return newResult;
	}
	
	
	
	
	@GetMapping("/getImage/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		StadiumVO stdmVO = stdmSvc.getOneStdm(id);
		byte[] image = stdmVO.getStdmPic();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG); // 或其他適當的媒體類型

		return new ResponseEntity<>(image, headers, HttpStatus.OK);
	}

	

}