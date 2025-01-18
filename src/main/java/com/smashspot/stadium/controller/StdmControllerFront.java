package com.smashspot.stadium.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

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
import com.smashspot.courtorder.model.CourtOrderService;
import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.member.model.MemberVO;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;
import com.smashspot.stadiumlike.model.StadiumLikeService;
import com.smashspot.stadiumlike.model.StadiumLikeVO;


@Controller
@RequestMapping("/stadium")
public class StdmControllerFront {

	@Autowired
	StdmService stdmSvc;
	
	@Autowired
	private CourtOrderService courtOrderSvc;

    @Autowired
    private StadiumLikeService stadiumLikeService; // 負責收藏功能
	
	@GetMapping("/listAllStadium")
	public String listAllStdm(
	        @RequestParam(required = false) String stdmName,
	        @RequestParam(required = false) String locationVO,
	        HttpSession session,
	        Model model) {
    // 1) 查全部場館
    List<StadiumVO> stdmListData = stdmSvc.getAll();
    model.addAttribute("stdmListData", stdmListData);
    
    // 2) 取得當前登入會員ID (若未登入，可能 = null)
    MemberVO loginMember = (MemberVO) session.getAttribute("login");
    Set<Integer> isLikedSet = new HashSet<>();
    if (loginMember != null) {
        // 3) 查該會員所有收藏(以 StadiumLikeVO 或你設計好的結構)
        List<StadiumLikeVO> userLikes = stadiumLikeService.getUserLikes(loginMember.getMemid());
        // 4) 建立 Set<Integer> 裝 stdmId
        for (StadiumLikeVO likeVO : userLikes) {
            isLikedSet.add(likeVO.getStdmId());
        }
    }

        // 5) 放到 model，給頁面 Thymeleaf 判斷使用
        model.addAttribute("isLikedSet", isLikedSet);
    
		
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
   

    // 建立兩個 Map
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
	
    @GetMapping("/listLiked")
    public String listLiked(Model model, HttpSession session) {
        // 1) 取得登入會員
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            // 尚未登入就轉回登入頁
            return "redirect:/member/login";
        }
        // 1) 印出登入會員的 memId
        System.out.println("【DEBUG】loginMember memId = " + loginMember.getMemid());
        
        // 2) 查該會員所有收藏紀錄 => List<StadiumLikeVO>
        List<StadiumLikeVO> userLikes = stadiumLikeService.getUserLikes(loginMember.getMemid());
        // 3) 把其中的 stdmId 拉出來放進一個 Set
        Set<Integer> likedStdmIds = userLikes.stream()
                .map(StadiumLikeVO::getStdmId)
                .collect(Collectors.toSet());
        
        // 3) 印出 likedStdmIds 看看
        System.out.println("【DEBUG】likedStdmIds = " + likedStdmIds);

        // 4) 用這個 Set 去查對應的場館 List<StadiumVO>
        //    這邊提供多種做法 (看你的 StadiumService 是否支援 findByStdmIds)
        List<StadiumVO> onlyLikedStadiums = stdmSvc.findByStdmIds(likedStdmIds);
        // ↑ 你可以在 stadiumService 中自行撰寫 
        //   public List<StadiumVO> findByStdmIds(Set<Integer> ids) { ... }

        // 5) 同時我們也需要 isLikedSet (用來在 thymeleaf 上判斷 active)
        //    其實這就是 likedStdmIds
        Set<Integer> isLikedSet = likedStdmIds;
        
        // 補上計算 averageMap & reviewCountMap
        Map<Integer, Double> averageMap = new HashMap<>();
        Map<Integer, Integer> reviewCountMap = new HashMap<>();
        for (StadiumVO stadium : onlyLikedStadiums) {
            Integer stdmId = stadium.getStdmId();
            double avgRating = courtOrderSvc.calculateAverageRatingForStadium(stdmId);
            int totalMsg = courtOrderSvc.calculateSumMessageForStadium(stdmId);
            averageMap.put(stdmId, avgRating);
            reviewCountMap.put(stdmId, totalMsg);
        }
        model.addAttribute("averageMap", averageMap);
        model.addAttribute("reviewCountMap", reviewCountMap);

        // 6) 把這些放進 model
        model.addAttribute("stdmListData", onlyLikedStadiums); 
        model.addAttribute("isLikedSet", isLikedSet);
        

        // 7) 跳到跟「全部場館列表」同一個模板，但裡面只會顯示已收藏的場館
        return "back-end/stdm/listAllStdmFront";
    }
}
