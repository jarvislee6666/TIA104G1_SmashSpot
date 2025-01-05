package com.smashspot.orders.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.smashspot.coupon.model.CouponService;
import com.smashspot.coupon.model.CouponVO;
import com.smashspot.member.model.MemberVO;
import com.smashspot.orders.model.OrdersService;
import com.smashspot.orders.model.OrdersVO;
import com.smashspot.product.model.ProductService;
import com.smashspot.product.model.ProductVO;

@Controller
@SessionAttributes("orderData") // 使用 @SessionAttributes 來指定需要存在 session 的屬性
public class OrdersController {
	@Autowired
	OrdersService odrsvc;
	
	@Autowired
	ProductService proSvc;
	
	@Autowired
	CouponService copSvc;
    
    // 創建一個 Model 屬性來存儲訂單數據
    @ModelAttribute("orderData")
    public Map<String, Object> orderData() {
        return new HashMap<>();
    }
	
	@GetMapping("/adm/listAllOrders")
	public String listAllProduct(Model model) {
		List<OrdersVO> list = odrsvc.getAll();
    	model.addAttribute("ordersListData",list);
		return "back-end/adm/listAllOrders";
	}
	
	@GetMapping("/client/orders/DPstep1/{proid}")
	public String DPstep1(@PathVariable Integer proid, Model model, HttpSession session) {
		// 獲取當前登入的會員
	    MemberVO loginMember = (MemberVO) session.getAttribute("login");
	    
		// 獲取商品資訊
	    ProductVO productVO = proSvc.getOneProduct(proid);
	    
	    // 檢查是否是商品擁有者
	    if (loginMember.getMemid().equals(productVO.getMemberVO().getMemid())) {
	        // 是商品擁有者，重定向回商品詳情頁面
	        return "redirect:/client/getOneProduct/" + proid;
	    }
	    
	    model.addAttribute("productVO", productVO);
	    return "back-end/client/orders/DPstep1";
	}
	
	@GetMapping("/client/orders/DPstep2")
	public String DPstep1(Model model) {
		// 獲取商品資訊

	    return "back-end/client/orders/DPstep2";
	}
	
	 // 步驟一：驗證優惠券並保存數據到 Session
    @PostMapping("/client/orders/saveStep1")
    @ResponseBody
    public ResponseEntity<?> saveStep1(@RequestParam String payment,
                                     @RequestParam(required = false) Integer copid,
                                     @RequestParam Integer finalPrice,
                                     @RequestParam Integer productId,
                                     @ModelAttribute("orderData") Map<String, Object> orderData) {
        // 將步驟一的數據存入 session
        orderData.put("payment", payment);
        orderData.put("copid", copid);
        orderData.put("finalPrice", finalPrice);
        orderData.put("productId", productId);
        
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    // 步驟二：保存收貨資訊到 Session
    @PostMapping("/client/orders/saveStep2")
    @ResponseBody
    public ResponseEntity<?> saveStep2(@RequestParam String address,
                                     @RequestParam String recipient,
                                     @RequestParam String phone,
                                     @ModelAttribute("orderData") Map<String, Object> orderData) {
        // 將步驟二的數據存入 session
        orderData.put("address", address);
        orderData.put("recipient", recipient);
        orderData.put("phone", phone);
        
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    // 步驟三：最終提交訂單
    @PostMapping("/client/orders/submitOrder")
    @ResponseBody
    public ResponseEntity<?> submitOrder(@ModelAttribute("orderData") Map<String, Object> orderData, HttpSession session) {
        try {
            // 從 session 中取出所有數據
            OrdersVO order = new OrdersVO();
            // 設置訂單資訊
            MemberVO mem = (MemberVO) session.getAttribute("login");
            order.setMemid(mem.getMemid()); // 獲取當前會員ID
            order.setOrdstaid(2); // 2 代表付款完成
            order.setBeforedis((Integer) orderData.get("originalPrice"));
            order.setAfterdis((Integer) orderData.get("finalPrice"));
            
            // 設置收貨資訊
            String sendInfo = String.format("收件人：%s，電話：%s，地址：%s", 
                orderData.get("recipient"),
                orderData.get("phone"),
                orderData.get("address"));
            order.setSendinfo(sendInfo);
            
            // 設置商品
            ProductVO product = proSvc.getOneProduct((Integer) orderData.get("productId"));
            order.setProductVO(product);
            
            // 設置優惠券（如果有）
            Integer copid = (Integer) orderData.get("copid");
            if (copid != null) {
                CouponVO coupon = copSvc.getOneCoupon(copid);
                order.setCouponVO(coupon);
            }
            
            // 保存訂單
            odrsvc.addOrder(order);
            
            // 清除 session 中的訂單數據
            orderData.clear();
            
            return ResponseEntity.ok(Map.of("success", true, "orderId", order.getOrdid()));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
	
}
