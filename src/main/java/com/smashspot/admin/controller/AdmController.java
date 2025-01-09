package com.smashspot.admin.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smashspot.admin.model.AdmService;
import com.smashspot.admin.model.AdmVO;
import com.smashspot.courtorder.model.CourtOrderService;
import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.member.model.MemberService;
import com.smashspot.member.model.MemberVO;
import com.smashspot.reservationtime.model.ReservationTimeService;
import com.smashspot.reservationtime.model.ReservationTimeVO;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;



@Controller
@RequestMapping("/adm")
public class AdmController {
	
	@Autowired
	AdmService admSvc;
	
	@Autowired
    private MemberService memberSvc;
	
	@Autowired
    private CourtOrderService courtOrderService;
	
	@Autowired
    private StdmService stadiumService;
	
	@Autowired
    private ReservationTimeService reservationTimeService;
	
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
	public String getOne_For_Update(@RequestParam("admid") String admid, ModelMap model, HttpSession session) {
	    // 檢查登入狀態和權限
	    AdmVO loginAdm = (AdmVO) session.getAttribute("loginAdm");
	    
	    // 如果未登入或不是高級管理員，導回列表頁
	    if (loginAdm == null || !loginAdm.getSupvsr()) {
	        return "redirect:/adm/listAllAdm";
	    }
	    
	    AdmVO admVO = admSvc.getOneAdm(Integer.valueOf(admid));
	    model.addAttribute("admVO", admVO);
	    return "back-end/adm/updateAdm";
	}
	
	@PostMapping("update")
	public String update(@Valid AdmVO admVO, BindingResult result, ModelMap model, HttpSession session) {
	    // 檢查登入狀態和權限
	    AdmVO loginAdm = (AdmVO) session.getAttribute("loginAdm");
	    
	    // 如果未登入或不是高級管理員，導回列表頁
	    if (loginAdm == null || !loginAdm.getSupvsr()) {
	        return "redirect:/adm/listAllAdm";
	    }

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
	                   RedirectAttributes redirectAttrs) {
	    AdmVO adm = admSvc.login(admemail, admpassword);
	    if (adm != null) {
	        // 檢查帳號狀態
	        if (!adm.getAdmsta()) {
	            redirectAttrs.addFlashAttribute("error", "此帳號已被停用");
	            return "redirect:/adm/login";
	        }
	        session.setAttribute("loginAdm", adm);
	        return "redirect:/adm/listAllAdm";
	    }
	    redirectAttrs.addFlashAttribute("error", "帳號或密碼錯誤");
	    return "redirect:/adm/login";
	}
	
	@PostMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate();
	    return "redirect:/adm/loginAdm";
	}
	
	@GetMapping("/listAllMember")
	public String listAllMember(
		    @RequestParam(required = false) String name,
		    @RequestParam(required = false) String status,
		    Model model) {
		    
		    Map<String, String[]> map = new HashMap<>();
		    if (name != null && !name.trim().isEmpty()) {
		        map.put("name", new String[]{name});
		    }
		    if (status != null && !status.trim().isEmpty()) {
		        map.put("status", new String[]{status});
		    }
		    
		    List<MemberVO> memberList;
		    if (map.isEmpty()) {
		        memberList = memberSvc.getAll();
		    } else {
		        memberList = memberSvc.getAll(map);
		    }
		    
		    model.addAttribute("memberList", memberList);
		    return "back-end/adm/listAllMember";
		}



	@GetMapping("/updateMember")
    public String getOneForUpdate(@RequestParam("memid") Integer memid, Model model) {
        MemberVO memberVO = memberSvc.getOneMember(memid);
        model.addAttribute("memberVO", memberVO);
        return "back-end/adm/updateMember";
    }

	@PostMapping("updateMem")
	@ResponseBody
	public ResponseEntity<?> updateMemberStatus(@RequestBody Map<String, Object> body) {
	    try {
	        Integer memid = Integer.parseInt(body.get("memid").toString());
	        Boolean status = (Boolean) body.get("status");
	        
	        MemberVO memberVO = memberSvc.getOneMember(memid);
	        memberVO.setStatus(status);
	        memberVO.setChgtime(new Timestamp(System.currentTimeMillis()));
	        
	        memberSvc.updateMember(memberVO);
	        
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	private BindingResult removeFieldErrors(MemberVO memberVO, BindingResult result, String... fieldNames) {
	   List<FieldError> errorsToKeep = result.getFieldErrors().stream()
	       .filter(err 	-> !Arrays.asList(fieldNames).contains(err.getField()))
	       .collect(Collectors.toList());
	   
	   BindingResult newResult = new BeanPropertyBindingResult(memberVO, "memberVO");
	   errorsToKeep.forEach(newResult::addError);
	   return newResult;
	}
	
	@GetMapping("/listAllCourtOrders")
	public String listAllCourtOrders(
	        @RequestParam(required = false) String stdmId,  // 改為 String 型別
	        @RequestParam(required = false) String memberId,
	        @RequestParam(required = false) String ordsta,
	        Model model) {
	    
	    Map<String, String[]> map = new HashMap<>();
	    
	    // 處理場館 ID
	    if (stdmId != null && !stdmId.trim().isEmpty()) {
	        map.put("stdmId", new String[]{stdmId});
	    }
	    
	    // 處理會員帳號
	    if (memberId != null && !memberId.trim().isEmpty()) {
	        map.put("memberId", new String[]{memberId});
	    }
	    
	    // 處理預約狀態
	    if (ordsta != null && !ordsta.trim().isEmpty()) {
	        map.put("ordsta", new String[]{ordsta});
	    }

	    List<CourtOrderVO> orderList;
	    if (map.isEmpty()) {
	        orderList = courtOrderService.getAll();
	    } else {
	        orderList = courtOrderService.getAll(map);
	    }
	    
	    // 取得場館列表
	    List<StadiumVO> stadiumList = stadiumService.getAll();
	    
	    model.addAttribute("orderList", orderList);
	    model.addAttribute("stadiumList", stadiumList);
	    return "back-end/adm/listAllCourtOrders";
	}
	
	@GetMapping("/getOrderDetail/{orderId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable Integer orderId) {
	    try {
	        CourtOrderVO order = courtOrderService.getOneOrder(orderId);
	        
	        Map<String, Object> response = new HashMap<>();
	        response.put("courtordid", order.getCourtordid());
	        response.put("member", Map.of(
	            "name", order.getMember().getName(),
	            "email", order.getMember().getEmail(),
	            "phone", order.getMember().getPhone()
	        ));
	        response.put("stadium", Map.of(
	            "stdmName", order.getStadium().getStdmName()
	        ));
	        
	        // 處理所有訂單詳情
	        List<Map<String, Object>> details = new ArrayList<>();
	        for (CourtOrderDetailVO detail : order.getCourtOrderDetail()) {
	            Map<String, Object> detailMap = new HashMap<>();
	            detailMap.put("ordDate", detail.getOrdDate());
	            detailMap.put("ordTime", detail.getOrdTime());
	            details.add(detailMap);
	        }
	        response.put("details", details);
	        
	        response.put("totamt", order.getTotamt());

	        response.put("ordsta", order.getOrdsta());
	        
	        response.put("canreason", order.getCanreason());
	        
	        response.put("starrank", order.getStarrank());
	        
	        response.put("message", order.getMessage());
	        
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}	
	
	@GetMapping("/listAllChart")
	public String showStadiumStats(Model model) {
	    List<StadiumVO> stadiumList = stadiumService.getAll();
	    model.addAttribute("stadiumList", stadiumList);
	    return "back-end/adm/listAllChart";
	}

	@GetMapping("/getStadiumStats")
	@ResponseBody
	public Map<String, Object> getStadiumStats(
	    @RequestParam Integer stdmId,
	    @RequestParam String month,
	    @RequestParam String type) {
	    
		if ("review".equals(type)) {
		    Map<String, Object> stats = courtOrderService.calculateReviewStats(stdmId);
		    
		    // 修改 reviews 資料結構，加入會員帳號
		    List<Map<String, Object>> reviews = (List<Map<String, Object>>) stats.get("reviews");
		    reviews.forEach(review -> {
		        CourtOrderVO order = courtOrderService.getOneOrder((Integer) review.get("orderId"));
		        review.put("memberAccount", order.getMember().getAccount());
		    });
		    
		    return stats;
		}
		
	    if ("usage".equals(type)) {
	        // 使用率統計仍需要時間區間
	        YearMonth ym = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
	        Date startDate = Date.valueOf(ym.atDay(1).toString());
	        Date endDate = Date.valueOf(ym.atEndOfMonth().toString());
	        
	        List<ReservationTimeVO> reservations = reservationTimeService
	            .findByStadiumIdAndDatesBetween(stdmId, startDate, endDate);
	        return Map.of("usageStats", 
	            reservationTimeService.calculateTimeSlotStats(reservations));
	    } else {
	        // 評論統計不需要時間區間
	        return courtOrderService.calculateReviewStats(stdmId);
	    }
	}
	
	
}
