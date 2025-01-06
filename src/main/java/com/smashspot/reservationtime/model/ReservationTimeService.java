package com.smashspot.reservationtime.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.reservationtime.model.ReservationTimeVO;

import util.HibernateUtil_CompositeQuery_Adm;

@Service("ReservationTimeService")
public class ReservationTimeService {
	
    @Autowired
    private ReservationTimeRepository repository;

	
	public void addRsv(ReservationTimeVO reservationTimeVO) {
		repository.save(reservationTimeVO);
	}
		
	public void updateRsv(ReservationTimeVO reservationTimeVO) {
		repository.save(reservationTimeVO);
	}
	
	public List<ReservationTimeVO> getAll() {
		return repository.findAll();
	}
	
	public ReservationTimeVO getOneRsv(Integer rsvid) {
		Optional<ReservationTimeVO> optional = repository.findById(rsvid);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

    // 這裡專門給查詢條件用
    public List<Object[]> findReservationByStdmIdAndWeeks(Integer stdmId, Integer startWeek, Integer endWeek) {
        return repository.findReservationByStdmIdAndWeeks(stdmId, startWeek, endWeek);
    }
    
    @Transactional
    public void addDailyReservationDynamic() {
        // 1. 先取得當前時間
        long now = System.currentTimeMillis();
        
        // 2. 用 Calendar 來加 29 天
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);
        cal.add(Calendar.DAY_OF_MONTH, 28);
        
        // 3. 轉成 java.sql.Date
        Date futureDate = new Date(cal.getTimeInMillis());
        
        // 4. 傳進 repository，插入「today + 28 天」的資料
        repository.insertTodayReservationDynamicIfNotExists(futureDate);
    }

//沃寯添加====================================================================================================
    public Map<String, Integer> calculateTimeSlotStats(List<ReservationTimeVO> reservations) {
        Map<String, Integer> slotStats = new LinkedHashMap<>();
        String[] slots = {
            "00:00-02:00", "02:00-04:00", "04:00-06:00", "06:00-08:00", 
            "08:00-10:00", "10:00-12:00", "12:00-14:00", "14:00-16:00",
            "16:00-18:00", "18:00-20:00", "20:00-22:00", "22:00-24:00"
        };
        
        // 初始化每個時段的計數為0
        for (String slot : slots) {
            slotStats.put(slot, 0);
        }
        
        // 計算每個時段的預約數
        for (ReservationTimeVO rsv : reservations) {
            String booked = rsv.getBooked(); // 例如 "xxxx330000x"
            for (int i = 0; i < booked.length() - 1; i++) { // 最後一個x不計算
                char c = booked.charAt(i);
                if (Character.isDigit(c)) {
                    int count = Character.getNumericValue(c);
                    String slot = slots[i];
                    slotStats.put(slot, slotStats.get(slot) + count);
                }
                // x代表該時段沒有預約，不需要特別處理，因為已經初始化為0
            }
        }
        
        return slotStats;
    }

    private int countBookings(String slot) {
        int count = 0;
        for (char c : slot.toCharArray()) {
            if (c >= '0' && c <= '9') {
                count += (c - '0');
            }
        }
        return count;
    }
    
    public List<ReservationTimeVO> findByStadiumIdAndDatesBetween(
    	    Integer stdmId, Date startDate, Date endDate) {
    	    return repository.findByStadiumIdAndDatesBetween(stdmId, startDate, endDate);
    	}
//=============================================================================================================    

}
