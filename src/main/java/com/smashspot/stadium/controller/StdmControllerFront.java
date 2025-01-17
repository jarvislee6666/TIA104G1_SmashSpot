package com.smashspot.stadium.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smashspot.courtorder.model.CourtOrderService;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;


@Controller
@RequestMapping("/stadium")
public class StdmControllerFront {

	@Autowired
	StdmService stdmSvc;
	
	@Autowired
	private CourtOrderService courtOrderSvc;

	
	@GetMapping("/listAllStadium")
	public String listAllStdm(
	        @RequestParam(required = false) String stdmName,
	        @RequestParam(required = false) String locationVO,
	        @RequestParam(defaultValue = "1") int page, // 當前頁數
	        @RequestParam(defaultValue = "6") int size, // 每頁顯示數量
	        Model model) {

	    
	    Map<String, String[]> map = new HashMap<>();
	    if (stdmName != null && !stdmName.trim().isEmpty()) {
	        map.put("stdmName", new String[]{stdmName});
	    }
	    if (locationVO != null && !locationVO.isEmpty()) {
	        map.put("locationVO", new String[]{locationVO});
	    }

	    
	    List<StadiumVO> stdmList;
	    if (map.isEmpty()) {
	        stdmList = stdmSvc.getAll();
	    } else {
	        stdmList = stdmSvc.getAll(map);
	    }

	    
	    int totalRecords = stdmList.size(); 
	    int totalPages = (int) Math.ceil((double) totalRecords / size);
	    int startIndex = (page - 1) * size; 
	    int endIndex = Math.min(startIndex + size, totalRecords); 

	    if (startIndex > totalRecords) {
	        startIndex = Math.max((totalPages - 1) * size, 0);
	        endIndex = totalRecords;
	    }

	    List<StadiumVO> paginatedList = stdmList.subList(startIndex, endIndex);

	   
	    model.addAttribute("stadiumVO", new StadiumVO());
	    model.addAttribute("stdmListData", paginatedList); // 分頁後數據
	    model.addAttribute("currentPage", page); // 當前頁數
	    model.addAttribute("totalPages", totalPages); // 總頁數
	    model.addAttribute("pageSize", size); // 每頁數量
	    model.addAttribute("totalRecords", totalRecords); // 總記錄數

	    // 評分與評論計算
	    Map<Integer, Double> averageMap = new HashMap<>();
	    Map<Integer, Integer> reviewCountMap = new HashMap<>();

	    for (StadiumVO stadium : stdmList) {
	        Integer stdmId = stadium.getStdmId();
	        double avgRating = courtOrderSvc.calculateAverageRatingForStadium(stdmId);
	        int totalMsg = courtOrderSvc.calculateSumMessageForStadium(stdmId);
	        averageMap.put(stdmId, avgRating);
	        reviewCountMap.put(stdmId, totalMsg);
	    }

	    model.addAttribute("averageMap", averageMap);
	    model.addAttribute("reviewCountMap", reviewCountMap);

	    return "back-end/stdm/listAllStdmFront";
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


	@GetMapping("/getImage/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		StadiumVO stdmVO = stdmSvc.getOneStdm(id);
		byte[] image = stdmVO.getStdmPic();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG); // 或其他適當的媒體類型

		return new ResponseEntity<>(image, headers, HttpStatus.OK);
	}
}
