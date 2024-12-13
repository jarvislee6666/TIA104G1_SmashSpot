package com.smashspot.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smashspot.admin.model.*;


@WebServlet("/adm/adm.do")
public class AdmServlet extends HttpServlet {
	
	private AdmService admService;
	
	@Override
	public void init() throws ServletException {
		admService = new AdmServiceImpl();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");
		String forwardPath = "";
		switch (action) {
			case "getAll":
				forwardPath = getAllAdms(req, res);
				break;
			case "compositeQuery":
				forwardPath = getCompositeAdmsQuery(req, res);
				break;
			default:
				forwardPath = "/index.jsp";
		}
		
		res.setContentType("text/html; charset=UTF-8");
		RequestDispatcher dispatcher = req.getRequestDispatcher(forwardPath);
		dispatcher.forward(req, res);
	}

	private String getAllAdms(HttpServletRequest req, HttpServletResponse res) {
		String page = req.getParameter("page");
		int currentPage = (page == null) ? 1 : Integer.parseInt(page);
		
		List<AdmVO> admList = admService.getAllAdms(currentPage);

		if (req.getSession().getAttribute("admPageQty") == null) {
			int admPageQty = admService.getPageTotal();
			req.getSession().setAttribute("admPageQty", admPageQty);
		}
		
		req.setAttribute("admList", admList);
		req.setAttribute("currentPage", currentPage);
		
		return "/back_end/adm/listAllAdms.jsp";
	}
	
	private String getCompositeAdmsQuery(HttpServletRequest req, HttpServletResponse res) {
		Map<String, String[]> map = req.getParameterMap();
		
		if (map != null) {
			String page = req.getParameter("page");
			int currentPage = (page == null) ? 1 : Integer.parseInt(page);
			
			List<AdmVO> admList = admService.getAdmsByCompositeQuery(map);
			req.setAttribute("admList", admList);
			req.setAttribute("currentPage", currentPage);
		} else {
			return "/back_end/adm/listAllAdms.jsp";
		}
		return "/back_end/adm/listAllAdms.jsp";
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

}
