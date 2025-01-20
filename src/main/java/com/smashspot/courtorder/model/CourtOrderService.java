package com.smashspot.courtorder.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.admin.model.AdmVO;
import com.smashspot.courtorderdetail.model.CourtOrderDetailRepository;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.reservationtime.model.ReservationTimeRepository;
import com.smashspot.reservationtime.model.ReservationTimeVO;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmRepository;

@Service
@Transactional
public class CourtOrderService {

//    private  CourtOrderRepository courtOrderRepository;
//    private  CourtOrderDetailRepository courtOrderDetailRepository;
//    private  StdmRepository stadiumRepository;
//    
//	public CourtOrderService(CourtOrderRepository courtOrderRepository,
//			CourtOrderDetailRepository courtOrderDetailRepository, StdmRepository stadiumRepository) {
//		this.courtOrderRepository = courtOrderRepository;
//		this.courtOrderDetailRepository = courtOrderDetailRepository;
//		this.stadiumRepository = stadiumRepository;
//	}
//	
	@Autowired
	private CourtOrderRepository courtOrderRepository;
	@Autowired
	private CourtOrderDetailRepository courtOrderDetailRepository;
	@Autowired
	private ReservationTimeRepository reservationTimeRepository;
	@Autowired
	private StdmRepository stadiumRepository;

	/**
	 * 新增訂單 (只要傳 CourtOrderVO 進來即可)
	 */
	public CourtOrderVO createOrderAndUpdateReservationTime(CourtOrderVO requestOrder) {
		

		// 1. 先根據前端傳過來的 stadiumId, 撈出 DB 的 StadiumVO
		Integer stdmId = requestOrder.getStadium().getStdmId();
		StadiumVO stadium = stadiumRepository.findById(stdmId)
				.orElseThrow(() -> new RuntimeException("Stadium not found, id=" + stdmId));

		// 2. 設定訂單資訊
		CourtOrderVO newOrder = new CourtOrderVO();
		newOrder.setMember(requestOrder.getMember());// 沃寯有修改
		newOrder.setStadium(stadium);
		newOrder.setOrdsta(true);
		newOrder.setOrdcrttime(new Timestamp(System.currentTimeMillis()));

		// 3. 處理明細
		Set<CourtOrderDetailVO> details = requestOrder.getCourtOrderDetail();
		if (details != null && !details.isEmpty()) {
			// 把每一筆 detail 都跟 order 綁定
			for (CourtOrderDetailVO detail : details) {
				// 綁定關聯
				detail.setCourtOrder(newOrder);
			}
			// 設到 order
			newOrder.setCourtOrderDetail(details);
		}

		// 4) 計算總金額 (依你規則，這裡示範: 場館價格 * 總時段數)
		int totalTimeSlots = 0;
		if (details != null) {
			for (CourtOrderDetailVO d : details) {
				totalTimeSlots += countNumberOfOnes(d.getOrdTime());
			}
		}
		newOrder.setTotamt(stadium.getCourtPrice() * totalTimeSlots);

		// 5) 在儲存訂單前，先**檢查**對應日期時段的庫存是否足夠
		// 若不足，直接丟異常 => rollback
		if (!checkInventoryAndUpdateIfOK(stdmId, details)) {

			throw new RuntimeException("庫存不足: 場地已被訂滿");
		}

		// 6) 最後存入資料庫 (因為 cascade=ALL，明細也會跟著存)
		return courtOrderRepository.save(newOrder);
	}

	/**
	 * 檢查所有 detail 是否足夠庫存；若足夠，順便更新 reservation_time.booked
	 */
	private boolean checkInventoryAndUpdateIfOK(Integer stdmId, Set<CourtOrderDetailVO> details) {
		// 逐筆 detail
		for (CourtOrderDetailVO detail : details) {
			Date ordDate = detail.getOrdDate(); // e.g. 2024-12-27
			String ordTime = detail.getOrdTime(); // e.g. "xxxx1000100x"
			if (ordTime == null || ordTime.length() != 12 || !ordTime.matches("[0-1x]{12}")) {
				throw new RuntimeException("訂單時段格式不正確: " + ordTime);
			}

			// 找出對應 reservation_time
			ReservationTimeVO rsvTime = reservationTimeRepository.findByStadiumIdAndDatesForUpdate(stdmId, ordDate);
			
//	        // 2) (為了測試) 強制停頓 N 秒
//	        try {
//	            System.out.println("[Tx1] 已取得悲觀鎖，暫停 15 秒...");
//	            Thread.sleep(15_000);
//	        } catch (InterruptedException e) {
//	            e.printStackTrace();
//	        }
//			if (rsvTime == null) {
//				// 代表還沒有對應那天的資料，或查不到
//				return false;
//			}

			// 拿到 rsv_ava & booked
			String rsvAva = rsvTime.getRsvava(); // e.g. "xxxx7777777x"
			String booked = rsvTime.getBooked(); // e.g. "xxxx0000100x"

			if (!isEnough(rsvAva, booked, ordTime)) {
				return false; // 只要有一筆時段不足，就整筆訂單失敗
			}

			// 若足夠，就合併
			String newBooked = mergeBooked(booked, ordTime);
			rsvTime.setBooked(newBooked);
			// 更新 DB
			reservationTimeRepository.save(rsvTime);
		}
		return true;
	}

	/**
	 * 檢查某日/某時段 ordTime 是否在 rsv_ava - booked 後還有足夠的剩餘 (至少 1)
	 */
	private boolean isEnough(String rsvAva, String booked, String ordTime) {
		// 若 rsvAva = "xxxx7777777x", booked= "xxxx0012300x", ordTime= "xxxx0010000x"
		// => 中間 7 碼( index=4..10 ) 分別做檢查
		// 只要 ordTime[i]=='1' 就代表該時段要 +1
		// => leftover = rsvAva[i] - booked[i]
		// => leftover >= 1 => ok
		for (int i = 0; i < 12; i++) {
			char cAva = rsvAva.charAt(i);
			char cBkd = booked.charAt(i);
			char cOrd = ordTime.charAt(i);

			// 遇到 'x' 直接跳過
			if (cAva == 'x')
				continue;
			int avaVal = cAva - '0';
			int bkdVal = cBkd - '0';
			int ordVal = cOrd - '0';

			// 調試輸出
			System.out.printf("時段: %d, 可容納: %d, 已預訂: %d, 需求: %d%n", i, avaVal, bkdVal, ordVal);
			System.out.printf(
					"時段=%d, rsvAva=%c, booked=%c, ordTime=%c => avaVal=%d, bkdVal=%d, ordVal=%d, leftover=%d%n", i,
					cAva, cBkd, cOrd, avaVal, bkdVal, ordVal, (avaVal - bkdVal));

			if (ordVal > 0) {
				// 需求 = ordVal(1) <= leftover ?
				int leftover = avaVal - bkdVal;
				if (leftover < ordVal) {
					// 不足
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 合併: booked + ordTime ordTime 若在 i 位='1' => booked[i] += 1
	 */
	private String mergeBooked(String oldBooked, String ordTime) {
		char[] arr = oldBooked.toCharArray();
		for (int i = 0; i < 12; i++) {
			if (arr[i] == 'x')
				continue; // 不開放
			int oldVal = arr[i] - '0';
			int addVal = ordTime.charAt(i) - '0';
			int sum = oldVal + addVal;
			if (sum > 9)
				sum = 9; // 以防爆字元
			arr[i] = (char) (sum + '0');
		}
		return new String(arr);
	}

	/**
	 * 計算 ordTime 裡 '1' 的數量
	 */
	private int countNumberOfOnes(String ordTime) {
		int cnt = 0;
		if (ordTime != null) {
			for (char c : ordTime.toCharArray()) {
				if (c == '1')
					cnt++;
			}
		}
		return cnt;
	}

	// 沃寯添加
	public List<CourtOrderVO> getAll() {
		return courtOrderRepository.findAll();
	}

	@Transactional(readOnly = true)
	public CourtOrderVO getOneOrder(Integer orderId) {
		CourtOrderVO order = courtOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("找不到訂單"));

		// 觸發關聯對象加載
		if (order.getMember() != null) {
			order.getMember().getName(); // 觸發加載
			order.getMember().getEmail(); // 觸發加載
			order.getMember().getPhone(); // 觸發加載
		}
		return order;
	}

	// 沃寯添加
	public Map<String, Object> calculateReviewStats(Integer stdmId) {
		Map<String, Object> stats = new HashMap<>();

		// 獲取指定場館的所有評價
		List<CourtOrderVO> orders = courtOrderRepository.findByStadiumId(stdmId);

		// 計算評價分布
		Map<Integer, Integer> ratingCounts = new HashMap<>();
		for (int i = 1; i <= 5; i++) {
			ratingCounts.put(i, 0);
		}

		List<Map<String, Object>> reviews = new ArrayList<>();
		double totalRating = 0;
		int reviewCount = 0;

		for (CourtOrderVO order : orders) {
			if (order.getStarrank() != null) {
				int rating = order.getStarrank();
				ratingCounts.put(rating, ratingCounts.get(rating) + 1);
				totalRating += rating;
				reviewCount++;

				// 收集評論詳情
				if (order.getStarrank() != null && order.getMessage() != null) {
					Map<String, Object> review = new HashMap<>();
					review.put("orderId", order.getCourtordid());
					review.put("rating", order.getStarrank());
					review.put("message", order.getMessage());
					review.put("memberAccount", order.getMember().getAccount());
					reviews.add(review);
				}
			}
		}

		// 計算平均評分
		double avgRating = reviewCount > 0 ? totalRating / reviewCount : 0;

		stats.put("ratingDistribution", ratingCounts);
		stats.put("averageRating", Math.round(avgRating * 10) / 10.0);
		stats.put("reviewCount", reviewCount);
		stats.put("reviews", reviews);

		return stats;
	}

	/**
	 * 只用方法命名規則來撈主檔 (Lazy Load)
	 */
	public List<CourtOrderVO> findByMemberId(Integer memid) {
		return courtOrderRepository.findByMember_Memid(memid);
	}

	/**
	 * 用 JOIN FETCH 一次撈主檔 + 明細
	 */
	public List<CourtOrderVO> findOrdersWithDetailsByMemberId(Integer memid) {
		return courtOrderRepository.findOrdersWithDetailsByMemberid(memid);
	}

	// 沃寯添加
	public List<CourtOrderVO> getAll(Map<String, String[]> map) {
		String stdmId = null;
		String memberId = null;
		String ordsta = null;

		if (map != null) {
			// 處理場館 ID
			if (map.containsKey("stdmId") && map.get("stdmId")[0] != null) {
				stdmId = map.get("stdmId")[0].trim();
				if (stdmId.isEmpty())
					stdmId = null;
			}

			// 處理會員帳號
			if (map.containsKey("memberId") && map.get("memberId")[0] != null) {
				memberId = map.get("memberId")[0].trim();
				if (memberId.isEmpty())
					memberId = null;
			}

			// 處理預約狀態
			if (map.containsKey("ordsta") && map.get("ordsta")[0] != null) {
				ordsta = map.get("ordsta")[0].trim();
				if (ordsta.isEmpty())
					ordsta = null;
			}
		}

		System.out.println("Search parameters - stdmId: " + stdmId + ", memberId: " + memberId + ", ordsta: " + ordsta);

		return courtOrderRepository.findByConditions(stdmId, memberId, ordsta);
	}

	/**
	 * 更新評價 (starrank, message)
	 */
	public void updateReview(Integer courtordid, Integer starrank, String message) {
		CourtOrderVO order = courtOrderRepository.findById(courtordid)
				.orElseThrow(() -> new RuntimeException("找不到此訂單"));

		// 可檢查：只有「已完成(ordsta=true)」才允許更新評價
		// 也可檢查：是否該會員本人才可評價...
		// 這裡單純範例直接給予更新
		order.setStarrank(starrank);
		order.setMessage(message);

		courtOrderRepository.save(order);
	}

	/**
	 * 取消訂單 (ordsta = false, canreason)
	 */
	/**
	 * 取消訂單 (ordsta = false, canreason)
	 */
	public void cancelOrder(Integer courtordid, Boolean ordsta, String canreason) {
		// 1) 先撈出訂單主檔
		CourtOrderVO order = courtOrderRepository.findById(courtordid)
				.orElseThrow(() -> new RuntimeException("找不到此訂單"));

		// 2) 檢查原本若已取消，不重複處理
		if (Boolean.FALSE.equals(order.getOrdsta())) {
			throw new RuntimeException("此訂單先前已取消，無法重複取消");
		}

		// 3) 準備檢查是否超過可取消期限
		Set<CourtOrderDetailVO> details = order.getCourtOrderDetail();
		if (details == null || details.isEmpty()) {
			throw new RuntimeException("該訂單沒有明細，無法進行取消");
		}

		// (3.1) 找出該筆訂單「所有明細」中的最早日期
		// CourtOrderDetailVO#getOrdDate() 若是 java.util.Date
		// 直接用 Collections.min() 或 Stream min() 都可以。
		Date earliestDate = details.stream().map(CourtOrderDetailVO::getOrdDate).min(Date::compareTo)
				.orElseThrow(() -> new RuntimeException("訂單明細無日期資訊"));

		// (3.2) 取得「今天」日期
		// 取得當下的毫秒數
		long nowMillis = System.currentTimeMillis();
		// 建立 java.sql.Date
		java.sql.Date today = new java.sql.Date(nowMillis);

		// (3.3) 若 today >= earliestDate，表示已到日期或已超過，不允許取消
		if (!today.before(earliestDate)) {
			throw new RuntimeException("已超過可取消日期，無法取消預約。");
		}

		// 4) 還原(釋放)已訂走的時段
		// (接下來就是你原本的釋放邏輯)
		for (CourtOrderDetailVO detail : details) {
			// (a) 將該筆 detail 的 ordTime 取出
			String ordTime = detail.getOrdTime();
			if (ordTime == null || ordTime.length() != 12 || !ordTime.matches("[0-1x]{12}")) {
				// 可視情況直接跳過或拋異常
				continue;
			}

			// (b) 找到對應的 reservation_time
			Integer stdmId = order.getStadium().getStdmId();
			Date ordDate = detail.getOrdDate();
			ReservationTimeVO rsvTime = reservationTimeRepository.findByStadiumIdAndDatesForUpdate(stdmId, ordDate);
			if (rsvTime == null) {
				// 若找不到對應日期的 reservation_time，可能代表還原失敗，
				// 可以拋異常或記錄 log
				continue;
			}

			// (c) 將 booked 欄位做「減回」處理
			String oldBooked = rsvTime.getBooked(); // e.g. "xxxx0100001x"
			String newBooked = subtractBooked(oldBooked, ordTime);
			rsvTime.setBooked(newBooked);

			// (d) 更新 DB
			reservationTimeRepository.save(rsvTime);

			// (e) 最後也把 Detail 裡的 ordTime 改成 "xxxx0000000x" (看你需求)
			detail.setOrdTime("xxxx0000000x");
		}

		// 5) 最後才更新主檔 (狀態+原因)
		order.setOrdsta(ordsta); // 通常傳入 false
		order.setCanreason(canreason); // 寫入取消原因
		// (若要順便把 total 金額歸 0，依需求自行決定)
		// order.setTotamt(0);

		// 6) 存回資料庫
		courtOrderRepository.save(order);
	}

	/**
	 * 將 oldBooked 與 ordTime 做相減。 - ordTime 若在 i 位='1' => oldBooked[i] -= 1 - 不能小於 0
	 */
	private String subtractBooked(String oldBooked, String ordTime) {
		char[] arr = oldBooked.toCharArray();
		for (int i = 0; i < 12; i++) {
			// 遇到 'x' 直接跳過不處理
			if (arr[i] == 'x') {
				continue;
			}
			int oldVal = arr[i] - '0';
			int subVal = (ordTime.charAt(i) == '1') ? 1 : 0;
			int diff = oldVal - subVal;

			if (diff < 0) {
				diff = 0; // 以防萬一
			}
			arr[i] = (char) (diff + '0');
		}
		return new String(arr);
	}

	public List<CourtOrderVO> findReviewsByStadiumId(Integer stadiumId) {
		// 直接呼叫 Repository
		return courtOrderRepository.findByStadiumId(stadiumId);
	}

	public double calculateAverageRatingForStadium(Integer stdmId) {
		List<CourtOrderVO> reviewList = findReviewsByStadiumId(stdmId);
		if (reviewList.isEmpty()) {
			return 0.0;
		}
		double sum = 0.0;
		int count = 0;
		for (CourtOrderVO review : reviewList) {
			if (review.getStarrank() != null) {
				sum += review.getStarrank();
				count++;
			}
		}
		double average = (count == 0) ? 0.0 : (sum / count);
		// 使用 Math.round，將平均值乘以 10 再除以 10，達到只留一位小數
		double rounded = Math.round(average * 10.0) / 10.0;
		return rounded;
	}

	public int calculateSumMessageForStadium(Integer stdmId) {
		List<CourtOrderVO> reviewList = findReviewsByStadiumId(stdmId);
		if (reviewList.isEmpty()) {
			return 0;
		}

		int messageCount = 0;
		for (CourtOrderVO review : reviewList) {
			// 如果 message 不為 null，代表這筆訂單有留言
			if (review.getMessage() != null && !review.getMessage().trim().isEmpty()) {
				messageCount++;
			}
		}
		return messageCount;

	}

	/**
	 * 根據多條件 (場館名稱 + 日期 + 預約狀態) 查詢訂單
	 */
	public List<CourtOrderVO> searchMyOrders(Integer memId, String stadiumName, Date ordDate, Boolean ordsta) {
		return courtOrderRepository.findByMemberIdComplex(memId, stadiumName, ordDate, ordsta);
	}

}
