package com.smashspot.stadium.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.reservationtime.model.ReservationTimeRepository;
import com.smashspot.reservationtime.model.ReservationTimeVO;
import com.smashspot.stadiumlike.model.StadiumLikeVO;

import util.HibernateUtil_CompositeQuery_Stdm;

@Service("stdmService")
@Transactional // by麒安
public class StdmService {

	@Autowired
	StdmRepository repository;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ReservationTimeRepository reservationTimeRepository;
	

	// 休館日的標記
	private static final String HOLIDAY_RSVAVA = "xxxxxxxxxxxx";// by麒安

	// 依據場地數量生成庫存字串//新增by麒安
	private String buildRsvAvaByCourtCount(Integer courtCount) {
		// 確保 courtCount 是一位數，如果有可能兩位數，就得考慮字串長度超過 12 碼問題
		StringBuilder sb = new StringBuilder();
		sb.append("xxxx");
		for (int i = 0; i < 7; i++) {
			sb.append(courtCount);
		}
		sb.append("x");
		return sb.toString(); // e.g. "xxxx7777777x"
	}

	/**
	 * 讓前端一次送出「最終的休館日清單 (ClosedDates)」。 1) 先查出一個月後(含)所有 ReservationTimeVO 2) 把在
	 * closedDates 裡的 -> 設為 HOLIDAY_RSVAVA 3) 不在 closedDates 裡的 -> 還原成可預約
	 */
	public void updateHolidays(Integer stdmId, List<LocalDate> closedDates) {
		// A) 查找該場館
		StadiumVO stadium = repository.findById(stdmId).orElseThrow(() -> new RuntimeException("找不到場館ID=" + stdmId));
		int courtCount = stadium.getCourtCount();

		// B) 限制只能設定在「一個月後(含)」
		LocalDate minAllowedDate = LocalDate.now().plusDays(1);
		for (LocalDate ld : closedDates) {
			if (ld.isBefore(minAllowedDate)) {
				throw new RuntimeException("休館日不可設定在一個月內: " + ld);
			}
		}

		// C) 查出「該場館且日期 >= 一個月後」的所有 ReservationTimeVO
		Date startDate = Date.valueOf(minAllowedDate);
		List<ReservationTimeVO> futureRsvList = reservationTimeRepository
				.findByStadiumIdAndDatesGreaterThanEqual(stdmId, startDate);

		// D) 把 closedDates 做成 set, 方便比對
		Set<LocalDate> closedSet = new HashSet<>(closedDates);

		// E) 遍歷 futureRsvList：
		// - 在closedSet裡 => 設成 "xxxxxxxxxxxx"
		// - 不在closedSet => 還原成可預約
		for (ReservationTimeVO r : futureRsvList) {
			LocalDate rDate = r.getDates().toLocalDate();

			// 1) 先看這天是不是要設定休館 (closedSet.contains(rDate))
			boolean isClosedDay = closedSet.contains(rDate);

			if (isClosedDay) {

				// 如果 booked != "xxxx0000000x" ，表示已經有人預約
				if (!"xxxx0000000x".equals(r.getBooked())) {
					// 這邊你可以：
					// (A) 直接擋下，不允許休館 → 拋例外 or 忽略
					throw new RuntimeException("該日已有預約，無法設定為休館日: " + rDate + " (booked=" + r.getBooked() + ")");

				}

				// 設為休館
				r.setRsvava(HOLIDAY_RSVAVA);
			} else {
				// 還原成可預約
				String revertStr = buildRsvAvaByCourtCount(courtCount);
				r.setRsvava(revertStr);
			}
			reservationTimeRepository.save(r);
		}

	}

	/**
	 * 取得該場館「已設定為休館」的日期清單 (一個月後)
	 */
	public List<LocalDate> findAllHolidays(Integer stdmId) {
		LocalDate oneMonthLater = LocalDate.now().plusDays(1);
		Date startDate = Date.valueOf(oneMonthLater);

		List<ReservationTimeVO> holidayList = reservationTimeRepository
				.findHolidayByStadiumIdAndDatesGreaterThanEqual(stdmId, startDate);
		// 只取 r.rsvava = 'xxxxxxxxxxxx'
		return holidayList.stream().map(r -> r.getDates().toLocalDate()).collect(Collectors.toList());
	}

	public void addStdm(StadiumVO stdmVO) {
		repository.save(stdmVO);
		// 開放新場館的28天預約庫存字串//新增by麒安
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		for (int i = 0; i < 28; i++) {
			LocalDate targetDay = tomorrow.plusDays(i);

			ReservationTimeVO rsvVO = new ReservationTimeVO();
			rsvVO.setStadium(stdmVO);
			rsvVO.setDates(Date.valueOf(targetDay));
			rsvVO.setBooked("xxxx0000000x");

			// 動態產生
			String rsvAvaStr = buildRsvAvaByCourtCount(stdmVO.getCourtCount());
			rsvVO.setRsvava(rsvAvaStr);

			reservationTimeRepository.save(rsvVO);
		}
	}

	public void updateStdm(StadiumVO stdmVO) {
		repository.save(stdmVO);
	}

	public void deleteStdm(Integer stdmId) {
		if (repository.existsById(stdmId))
			repository.deleteByStdmId(stdmId);
	}

	public StadiumVO getOneStdm(Integer stdmId) {
		Optional<StadiumVO> optional = repository.findById(stdmId);
		return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<StadiumVO> getAll() {
		return repository.findAll();
	}

	public List<StadiumVO> getAll(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_Stdm.getAllC(map, sessionFactory.openSession());
	}


	
    public List<StadiumVO> findByStdmIds(Set<Integer> ids) {
        // 如果 set 為空，直接回傳空
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.findByStdmIdIn(ids);
    }
	

	
}
