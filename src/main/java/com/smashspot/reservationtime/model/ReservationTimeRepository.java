package com.smashspot.reservationtime.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
