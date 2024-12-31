package com.smashspot.stadium.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smashspot.reservationtime.model.ReservationTimeRepository;
import com.smashspot.reservationtime.model.ReservationTimeVO;

import util.HibernateUtil_CompositeQuery_Stdm;

@Service("stdmService")
public class StdmService {

	@Autowired
	StdmRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;
	
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    
    //依據場地數量生成庫存字串//新增by麒安
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

    public void addStdm(StadiumVO stdmVO) {
        repository.save(stdmVO);
        //開放新場館的28天預約庫存字串//新增by麒安
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        for (int i = 0; i < 28; i++) {
            LocalDate targetDay = tomorrow.plusDays(i);

            ReservationTimeVO rsvVO = new ReservationTimeVO();
            rsvVO.setStadium(stdmVO);
            rsvVO.setDates(Date.valueOf(targetDay));
            rsvVO.setBooked("xxx00000000x");
            
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
//		    repository.deleteById(stdmId);
	}

	public StadiumVO getOneStdm(Integer stdmId) {
		Optional<StadiumVO> optional = repository.findById(stdmId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<StadiumVO> getAll() {
		return repository.findAll();
	}

	public List<StadiumVO> getAll(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_Stdm.getAllC(map,sessionFactory.openSession());
	}

	
}
