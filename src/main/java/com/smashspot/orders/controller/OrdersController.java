package com.smashspot.orders.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.smashspot.bid.model.BidService;
import com.smashspot.bid.model.BidVO;
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
	
	@Autowired
    BidService bidService; // 注入 BidService
    
    // 創建一個 Model 屬性來存儲訂單數據
    @ModelAttribute("orderData")
    public Map<String, Object> orderData() {
        return new HashMap<>();
    }
	
    @GetMapping("/adm/listAllOrders")
    public String listAllProduct(
            @RequestParam(required = false) String proname,
            @RequestParam(required = false) Integer buyerId,
            @RequestParam(required = false) Integer sellerId,
            @RequestParam(required = false) Integer ordstaid,
            Model model) {
            
        List<OrdersVO> list;
        // 如果所有參數都是 null，則獲取全部訂單
        if (proname == null && buyerId == null && sellerId == null && ordstaid == null) {
            list = odrsvc.getAll();
        } else {
            list = odrsvc.searchOrders(proname, buyerId, sellerId, ordstaid);
        }
        
        // 加入資料到 Model
        model.addAttribute("ordersListData", list);
        // 保存搜尋條件
        model.addAttribute("searchProname", proname);
        model.addAttribute("searchBuyerId", buyerId);
        model.addAttribute("searchSellerId", sellerId);
        model.addAttribute("searchOrdstaid", ordstaid);
        
        return "back-end/adm/listAllOrders";
    }
	
	@GetMapping("/member/buyingList")
	public String listMemBuyingList(Model model, HttpSession session) {
	    // 從 session 獲取當前登入會員
	    MemberVO loginMember = (MemberVO) session.getAttribute("login");
	    if (loginMember == null) {
	        return "redirect:/login";
	    }
	    
	    // 獲取該會員的所有訂單
	    List<OrdersVO> list = odrsvc.findByMem(loginMember.getMemid());
	    model.addAttribute("ordersListData", list);
	    
	    return "back-end/member/buyingList";
	}
	
	@GetMapping("/client/orders/DPstep1/{proid}")
	public String DPstep1(@PathVariable Integer proid, Model model, HttpSession session) {
		// 獲取當前登入的會員
	    MemberVO loginMember = (MemberVO) session.getAttribute("login");
	    if (loginMember == null) {
            return "redirect:/member/login";
        }
	    
		// 獲取商品資訊
	    ProductVO productVO = proSvc.getOneProduct(proid);
	    if (productVO == null) {
            // 商品不存在的處理
            return "redirect:/client/listAllProductING";
        }
	    
	    // 檢查是否是商品擁有者
	    if (loginMember.getMemid().equals(productVO.getMemberVO().getMemid())) {
	        // 是商品擁有者，重定向回商品詳情頁面
	        return "redirect:/client/getOneProduct/" + proid;
	    }
	    
	    // 確定商品價格
        Integer finalPrice;
        String purchaseType;
        
        if (productVO.getBidstaid() == 2) {
            // 競標結束的情況
            List<BidVO> memberBids = bidService.getMemberBidsForProduct(loginMember.getMemid(), proid);
            Integer highestBid = memberBids.stream()
                    .map(BidVO::getBidamt)
                    .max(Integer::compareTo)
                    .orElse(0);

            // 檢查是否為最高出價者
            if (!highestBid.equals(productVO.getMaxprice())) {
                return "redirect:/client/getOneProduct/" + proid;
            }
            
            finalPrice = productVO.getMaxprice(); // 使用最終競標價
            purchaseType = "auction";
        } else {
            // 直購的情況
            finalPrice = productVO.getPurprice(); // 使用直購價
            purchaseType = "direct";
        }
	    
        model.addAttribute("productVO", productVO);
        model.addAttribute("finalPrice", finalPrice);
        model.addAttribute("purchaseType", purchaseType);
	    return "back-end/client/orders/DPstep1";
	}
	
	@GetMapping("/client/orders/DPstep2")
	public String DPstep2(@ModelAttribute("orderData") Map<String, Object> orderData, 
				            Model model, HttpSession session) {
		
		// 檢查是否已完成步驟一
	    if (!orderData.containsKey("productId") || !orderData.containsKey("payment")) {
	        // 如果沒有必要的數據，重定向到商品列表
	        return "redirect:/client/listAllProductING";
	    }

	    // 檢查用戶是否登入
	    MemberVO loginMember = (MemberVO) session.getAttribute("login");
	    if (loginMember == null) {
	        return "redirect:/member/login";
	    }

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
    try {
    	// 先獲取商品資訊以取得原始價格
        ProductVO product = proSvc.getOneProduct(productId);
        if (product == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "商品不存在"));
        }
        
        // 確定實際價格（競標或直購）
        Integer originalPrice;
        if (product.getBidstaid() == 2) {
            // 競標結束的情況
            originalPrice = product.getMaxprice();
        } else {
            // 直購的情況
            originalPrice = product.getPurprice();
        }
        
        // 如果有使用優惠券，驗證優惠券並計算折扣後金額
        if (copid != null) {
            CouponVO coupon = copSvc.getOneCoupon(copid);
            
            // 計算折扣後金額
            int expectedDiscountedPrice = originalPrice - coupon.getDiscount();
            if (expectedDiscountedPrice < 0) {
                expectedDiscountedPrice = 0;
            }
            
            // 驗證提交的折扣後價格是否正確
            if (finalPrice != expectedDiscountedPrice) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "折扣金額計算不正確"));
            }
        } else {
            // 如果沒有使用優惠券，則驗證價格是否等於原始價格
            if (!originalPrice.equals(finalPrice)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "價格資訊不正確"));
            }
        }
    	
    	// 將步驟一的數據存入 session
        orderData.put("payment", payment);
        orderData.put("copid", copid);
        orderData.put("finalPrice", finalPrice);
        orderData.put("originalPrice", originalPrice);
        orderData.put("productId", productId);
        
        return ResponseEntity.ok(Map.of("success", true));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of("success", false, "message", e.getMessage()));
	    }
    }
    
    // 步驟二：保存收貨資訊到 Session
    @PostMapping("/client/orders/saveStep2")
    @ResponseBody
    public ResponseEntity<?> saveStep2(@RequestParam String address,
                                     @RequestParam String recipient,
                                     @RequestParam String phone,
                                     @RequestParam String email,
                                     @RequestParam(required = false) String notes,
                                     @RequestParam String delivery,
                                     @ModelAttribute("orderData") Map<String, Object> orderData) {
    	
    	try {
            // 檢查是否有步驟一的數據
            if (!orderData.containsKey("payment") || !orderData.containsKey("productId")) {
                return ResponseEntity.badRequest().body(Map.of("success", false, 
                    "message", "請先完成步驟一"));
            }
    	
	        // 將步驟二的數據存入 session
	        orderData.put("address", address);
	        orderData.put("recipient", recipient);
	        orderData.put("phone", phone);
	        orderData.put("email", email);
	        orderData.put("notes", notes);
	        orderData.put("delivery", delivery);
        
            return ResponseEntity.ok(Map.of("success", true));
    	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Map.of("success", false, 
                                   "message", "系統錯誤：" + e.getMessage()));
        }
    }
    
    // 步驟三：最終提交訂單
    @GetMapping("/client/orders/DPstep3")
    public String DPstep3(@ModelAttribute("orderData") Map<String, Object> orderData, HttpSession session, Model model) {
    	// 從 session 中取出會員資訊
        MemberVO mem = (MemberVO) session.getAttribute("login");
        if (mem == null) {
            return "redirect:/login";
        }
    	
    	try {
            // 從 session 中取出所有數據
            OrdersVO order = new OrdersVO();
            // 設置訂單資訊
            order.setMemid(mem.getMemid()); // 獲取當前會員ID
            order.setOrdstaid(2); // 2 代表付款完成
            
            
            // 設置商品資訊
            Integer productId = (Integer) orderData.get("productId");
            List<OrdersVO> exsitOrder = odrsvc.findByProduct(productId);
	        if (exsitOrder.size() >= 1) {
	            return "redirect:/client/listAllProductING"; 
	        }
            ProductVO product = proSvc.getOneProduct(productId);
            order.setProductVO(product);
            
            // 設置價格資訊
            order.setBeforedis((Integer) orderData.get("originalPrice"));
            order.setAfterdis((Integer) orderData.get("finalPrice"));
            
            // 設置優惠券（如果有使用）
            Integer copid = (Integer) orderData.get("copid");
            if (copid != null) {
                CouponVO coupon = copSvc.getOneCoupon(copid);
                order.setCouponVO(coupon);
            }
            
            // 組合收件資訊
            String sendInfo = String.format("收件人：%s，電話：+886%s，地址：%s", 
                orderData.get("recipient"),
                orderData.get("phone"),
                orderData.get("address"));
            order.setSendinfo(sendInfo);
            
            // 保存訂單
            odrsvc.addOrder(order);
            
            // 更新商品狀態為已結標/直購完成(2)
            product.setBidstaid(2);
            proSvc.updateProduct(product);
            
            // 將訂單資訊傳遞給頁面
            model.addAttribute("order", order);
            model.addAttribute("payment", orderData.get("payment"));
            model.addAttribute("finalPrice", orderData.get("finalPrice"));
            model.addAttribute("recipient", orderData.get("recipient"));
            model.addAttribute("phone", orderData.get("phone"));
            model.addAttribute("address", orderData.get("address"));
            
            // 清除 session 中的訂單資料
            orderData.clear();
            
            return "back-end/client/orders/DPstep3";
            
        } catch (Exception e) {
        	e.printStackTrace();
            return "redirect:/client/orders/DPstep2";
        }
    }
    
    @GetMapping("/adm/getProductImage/{ordId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProductImage(@PathVariable Integer ordId) {
        try {
            OrdersVO order = odrsvc.getOneOrder(ordId);
            if (order != null && order.getProductVO() != null) {
                byte[] image = order.getProductVO().getPropic();
                if (image != null && image.length > 0) {
                    return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(image);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/adm/cancelOrder/{ordId}")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable Integer ordId) {
        try {
            OrdersVO order = odrsvc.getOneOrder(ordId);
            if (order == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "訂單不存在"));
            }
            
            // 更新訂單狀態為取消(5)
            order.setOrdstaid(5);
            odrsvc.updateOrder(order);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PostMapping("/member/confirmReceipt/{ordId}")
    @ResponseBody
    public ResponseEntity<?> confirmReceipt(@PathVariable Integer ordId, HttpSession session) {
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("login");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "請先登入"));
            }
            
            OrdersVO order = odrsvc.getOneOrder(ordId);
            if (order == null || !order.getMemid().equals(loginMember.getMemid())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "訂單不存在或無權限"));
            }
            
            // 更新訂單狀態為完成(4)
            order.setOrdstaid(4);
            odrsvc.updateOrder(order);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/client/memOrdersList")
    public String listMemOrdersList(Model model, HttpSession session) {
        // 從 session 獲取當前登入會員
        MemberVO loginMember = (MemberVO) session.getAttribute("login");
        if (loginMember == null) {
            return "redirect:/login";
        }
        
        // 獲取該賣家的所有商品的訂單
        List<ProductVO> sellerProducts = proSvc.findMem(loginMember.getMemid());
        List<OrdersVO> allSellerOrders = new ArrayList<>();
        
        for (ProductVO product : sellerProducts) {
            List<OrdersVO> productOrders = odrsvc.findByProduct(product.getProid());
            allSellerOrders.addAll(productOrders);
        }
        
        model.addAttribute("ordersListData", allSellerOrders);
        
        return "back-end/client/product/memOrdersList";
    }
    
    @PostMapping("/client/shipOrder/{ordId}")
    @ResponseBody
    public ResponseEntity<?> shipOrder(@PathVariable Integer ordId, HttpSession session) {
        try {
            MemberVO loginMember = (MemberVO) session.getAttribute("login");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "請先登入"));
            }
            
            OrdersVO order = odrsvc.getOneOrder(ordId);
            if (order == null || !order.getProductVO().getMemberVO().getMemid().equals(loginMember.getMemid())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "訂單不存在或無權限"));
            }
            
            // 更新訂單狀態為已出貨(3)
            order.setOrdstaid(3);
            odrsvc.updateOrder(order);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
