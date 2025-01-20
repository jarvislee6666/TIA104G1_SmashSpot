package com.smashspot.reservationtime.model;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import javax.persistence.LockModeType;


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

   //   stadium 有欄位叫 'court_count'
   //   用 'xxxx' + court_count*7 + 'x' 的方式組出 12 碼
   //   下面範例以 MySQL 語法 CONCAT 與 REPEAT() 函式來實作
   @Modifying
   @Query(value = 
      "INSERT INTO reservation_time (stdm_id, dates, rsv_ava, booked) " +
      "SELECT s.stdm_id, :insertDate, " +
      // 組出 12 碼： 'xxxx' + (court_count 重複 7 次) + 'x'
      "       CONCAT('xxxx', REPEAT(s.court_count, 7), 'x') AS dynamic_rsv_ava, " +
      "       'xxxx0000000x' AS bookedStr " +
      "FROM stadium s " +
      "WHERE s.opr_sta = 1 "+
      "AND NOT EXISTS ( " +
      "    SELECT 1 FROM reservation_time r " +
      "    WHERE r.stdm_id = s.stdm_id " +
      "      AND r.dates = :insertDate " +
      ")", 
      nativeQuery = true)
   void insertTodayReservationDynamicIfNotExists(
       @Param("insertDate") Date insertDate
   );
   
//   // 查出「同場館、同日期」的 ReservationTimeVO 
//   @Query("SELECT r FROM ReservationTimeVO r "
//        + "WHERE r.stadium.stdmId = :stdmId AND r.dates = :dates")
//   ReservationTimeVO findByStadiumIdAndDates(@Param("stdmId") Integer stdmId,
//                                             @Param("dates") Date dates);

   // 查出「同場館、且日期 >= :dates」的多筆
   @Query("SELECT r FROM ReservationTimeVO r "
        + "WHERE r.stadium.stdmId = :stdmId AND r.dates >= :dates")
   List<ReservationTimeVO> findByStadiumIdAndDatesGreaterThanEqual(
                   @Param("stdmId") Integer stdmId,
                   @Param("dates") Date dates);

   // 直接查「已經是休館日(rsvava=xxxxxxxxxxxx)」的清單
   @Query("SELECT r FROM ReservationTimeVO r "
        + "WHERE r.stadium.stdmId = :stdmId "
        + "  AND r.dates >= :dates "
        + "  AND r.rsvava = 'xxxxxxxxxxxx'")
   List<ReservationTimeVO> findHolidayByStadiumIdAndDatesGreaterThanEqual(
                   @Param("stdmId") Integer stdmId,
                   @Param("dates") Date dates);
   
   //沃寯添加
   @Query("SELECT r FROM ReservationTimeVO r " +
	       "WHERE r.stadium.stdmId = :stdmId " +
	       "AND r.dates BETWEEN :startDate AND :endDate")
	List<ReservationTimeVO> findByStadiumIdAndDatesBetween(
	    @Param("stdmId") Integer stdmId,
	    @Param("startDate") Date startDate,
	    @Param("endDate") Date endDate
	);
   
   @Query("SELECT r.stadium.stdmId, MAX(r.dates) " +
	       "FROM ReservationTimeVO r " +
	       "GROUP BY r.stadium.stdmId")
	List<Object[]> findLastDatesForEachStadium();
	
    // 新增帶悲觀鎖的方法
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationTimeVO r "
         + "WHERE r.stadium.stdmId = :stdmId AND r.dates = :dates")
    ReservationTimeVO findByStadiumIdAndDatesForUpdate(
            @Param("stdmId") Integer stdmId, 
            @Param("dates") java.sql.Date dates
    );


   
   
}
