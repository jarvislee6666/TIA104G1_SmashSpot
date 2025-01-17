package com.smashspot.stadium.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.smashspot.admin.model.AdmService;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;

@Controller
@RequestMapping("/stadium")
public class StdmControllerFront {

	@Autowired
	StdmService stdmSvc;

	
	@GetMapping("/listAllStadium")
	public String listAllStdm(
	        @RequestParam(required = false) String stdmName,
	        @RequestParam(required = false) String locationVO,
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
    
    
//    stdmList = stdmList.stream()
//    	    .filter(stdm -> stdm.getOprSta())
//    	    .collect(Collectors.toList());
    
    
    model.addAttribute("stadiumVO", new StadiumVO());
    model.addAttribute("stdmListData", stdmList);
    return "back-end/stdm/listAllStdmFront";
}
	
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


	@GetMapping("/getImage/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		StadiumVO stdmVO = stdmSvc.getOneStdm(id);
		byte[] image = stdmVO.getStdmPic();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG); // 或其他適當的媒體類型

		return new ResponseEntity<>(image, headers, HttpStatus.OK);
	}
}
