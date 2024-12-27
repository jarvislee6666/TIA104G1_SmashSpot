package com.smashspot.reservationtime.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

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

}
