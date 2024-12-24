package com.smashspot.admin.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.smashspot.admin.model.AdmService;
import com.smashspot.admin.model.AdmVO;

@Controller
public class IndexController_Admin {

	// @Autowired (●自動裝配)(Spring ORM 課程)
	// 目前自動裝配了EmpService --> 供第66使用
	@Autowired
	AdmService admSvc;

	// inject(注入資料) via application.properties
	@Value("${welcome.message}")
	private String message;

	private List<String> myList = Arrays.asList("Spring Boot Quickstart 官網 : https://start.spring.io", "IDE 開發工具",
			"直接使用(匯入)官方的 Maven Spring-Boot-demo Project + pom.xml",
			"直接使用官方現成的 @SpringBootApplication + SpringBootServletInitializer 組態檔",
			"依賴注入(DI) HikariDataSource (官方建議的連線池)", "Thymeleaf",
			"Java WebApp (<font color=red>快速完成 Spring Boot Web MVC</font>)");

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("message", message);
		model.addAttribute("myList", myList);
		return "index"; // view
	}

	// http://......../hello?name=peter1
	@GetMapping("/hello")
	public String indexWithParam(@RequestParam(name = "name", required = false, defaultValue = "") String name,
			Model model) {
		model.addAttribute("message", name);
		return "index"; // view
	}

	// =========== 以下第63~75行是提供給
	// /src/main/resources/templates/back-end/emp/select_page.html 與 listAllEmp.html
	// 要使用的資料 ===================
	@GetMapping("/adm/select_page")
	public String select_page(Model model) {
		return "back-end/adm/select_page";
	}

	@GetMapping("/adm/listAllAdm")
	public String listAllAdm(@RequestParam(required = false) String admname,
			@RequestParam(required = false) String admsta, @RequestParam(required = false) String supvsr, Model model) {
		List<AdmVO> admList = admSvc.getAll();
		List<AdmVO> filteredAdms = admList.stream().filter(adm -> {
			boolean match = true;
			
			// 依帳號狀態過濾
			if (admsta != null && !admsta.isEmpty()) {
				match = match && adm.getAdmsta() == Boolean.parseBoolean(admsta);
			}
			// 依權限等級過濾
			if (supvsr != null && !supvsr.isEmpty()) {
				match = match && adm.getSupvsr() == Boolean.parseBoolean(supvsr);
			}
			return match;
		}).collect(Collectors.toList());
		if (filteredAdms.isEmpty() && (admname != null || admsta != null || supvsr != null)) {
			model.addAttribute("admList", admList); // 顯示所有資料
		} else {
			model.addAttribute("admList", filteredAdms);
		}

		return "back-end/adm/listAllAdm";
	}

	@ModelAttribute("admListData") // for select_page.html 第97 109行用 // for listAllEmp.html 第85行用
	protected List<AdmVO> referenceListData(Model model) {

		List<AdmVO> list = admSvc.getAll();
		return list;
	}

}
