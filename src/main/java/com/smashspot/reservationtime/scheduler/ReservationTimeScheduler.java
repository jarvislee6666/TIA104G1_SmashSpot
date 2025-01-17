package com.smashspot.reservationtime.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smashspot.reservationtime.model.ReservationTimeService;
import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;

@Component
public class ReservationTimeScheduler {

    @Autowired
    private ReservationTimeService reservationTimeService;
    @Autowired
    private StdmService stdmService;


    /**
     * cron = "0 0 0 * * ?"：代表「每天 00:00:00 執行一次」
     * （你也可以自行調整成要卡在哪一分鐘執行）
     */
    @Scheduled(cron = "0 22 * * * ?")
    public void createDailyReservationTime() {
        // 這裡呼叫 Service 裡的邏輯，為每個 Stadium 新增一筆 reservationTimeVO
    	 reservationTimeService.addDailyReservationDynamic();
    }
}
