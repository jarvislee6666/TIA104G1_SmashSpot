package com.smashspot.reservationtime.model;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationTimeRepository extends JpaRepository<ReservationTimeVO, Integer> {
    @Query(value = 
       "SELECT rt.stdm_id, s.stdm_name, rt.dates, rt.rsv_ava, rt.booked " +
       "FROM reservation_time rt " +
       "JOIN stadium s ON rt.stdm_id = s.stdm_id " +
       "WHERE rt.stdm_id = ?1 " +
       "AND rt.dates BETWEEN " +
       "  DATE_ADD(" +
       "      DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE())-1) DAY), " +
       "      INTERVAL ?2 WEEK" +
       "  ) " +
       "  AND " +
       "  DATE_ADD(" +
       "      DATE_ADD(" +
       "          DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE())-1) DAY), " +
       "          INTERVAL ?3 WEEK" +
       "      ), " +
       "      INTERVAL 6 DAY" +
       "  ) " +
       "ORDER BY rt.dates",
       nativeQuery = true)
   List<Object[]> findReservationByStdmIdAndWeeks(
       Integer stdmId, 
       Integer startWeek, 
       Integer endWeek
   );
   
   
   // --- 方法 2: 動態依照 stadium.capacity 來拼出 rsv_ava ---
   //   假設 stadium 有欄位叫 'capacity'
   //   若你想用 'xxxx' + capacity*7 + 'x' 的方式組出 12 碼
   //   下面範例以 MySQL 語法 CONCAT 與 REPEAT() 函式來實作
   @Modifying
   @Query(value = 
      "INSERT INTO reservation_time (stdm_id, dates, rsv_ava, booked) " +
      "SELECT s.stdm_id, :insertDate, " +
      // 組出 12 碼： 'xxxx' + (capacity 重複 7 次) + 'x'
      "       CONCAT('xxxx', REPEAT(s.court_count, 7), 'x') AS dynamic_rsv_ava, " +
      "       'xxxx0000000x' AS bookedStr " +
      "FROM stadium s " +
      "WHERE NOT EXISTS ( " +
      "    SELECT 1 FROM reservation_time r " +
      "    WHERE r.stdm_id = s.stdm_id " +
      "      AND r.dates = :insertDate " +
      ")", 
      nativeQuery = true)
   void insertTodayReservationDynamicIfNotExists(
       @Param("insertDate") Date insertDate
   );
   
   ReservationTimeVO findByStadium_StdmIdAndDates(Integer stdmId, Date dates);

}
