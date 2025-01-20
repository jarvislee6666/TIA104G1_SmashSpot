package com.smashspot.courtorder.model;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.stadium.model.StadiumVO;

@Repository
public interface CourtOrderRepository extends JpaRepository<CourtOrderVO, Integer> {

	/**
	 * 依照 member.memId 來查詢所有 CourtOrderVO。 這種命名規則方式，Spring Data JPA 會自動生成對應的 SQL。
	 */
	// 目前沒用到
	List<CourtOrderVO> findByMember_Memid(Integer memid);

	/**
	 * 若需要一次將主檔 + 明細 (OneToMany) 全部撈回來， 可以使用 JOIN FETCH 避免
	 * LazyInitializationException
	 */
	@Query("SELECT DISTINCT co FROM CourtOrderVO co " + "JOIN FETCH co.courtOrderDetail "
			+ "WHERE co.member.memid = :memid")
	List<CourtOrderVO> findOrdersWithDetailsByMemberid(@Param("memid") Integer memid);

	// 沃寯添加
	@Query("SELECT co FROM CourtOrderVO co WHERE "
			+ "(:stdmId IS NULL OR co.stadium.stdmId = CAST(:stdmId AS integer)) AND "
			+ "(:memberId IS NULL OR LOWER(co.member.account) LIKE LOWER(CONCAT('%', :memberId, '%'))) AND "
			+ "(:ordsta IS NULL OR " + "(:ordsta = 'true' AND co.ordsta = true) OR "
			+ "(:ordsta = 'false' AND co.ordsta = false))")
	List<CourtOrderVO> findByConditions(@Param("stdmId") String stdmId, @Param("memberId") String memberId,
			@Param("ordsta") String ordsta);

	@Query("SELECT co FROM CourtOrderVO co WHERE co.stadium.stdmId = :stdmId")
	List<CourtOrderVO> findByStadiumId(@Param("stdmId") Integer stdmId);

	@Query("""
		    SELECT DISTINCT co
		    FROM CourtOrderVO co
		    LEFT JOIN co.stadium st
		    LEFT JOIN co.courtOrderDetail dt
		    WHERE 
		        co.member.memid = :memId
		        AND ( COALESCE(:stadiumName, '') = ''
		              OR st.stdmName LIKE CONCAT('%', :stadiumName, '%') )
		        AND ( :ordDate IS NULL
		              OR dt.ordDate = :ordDate )
		        AND ( :ordsta IS NULL
		              OR co.ordsta = :ordsta )
		""")
	List<CourtOrderVO> findByMemberIdComplex(@Param("memId") Integer memId, // 哪位會員
			@Param("stadiumName") String stadiumName, // 場館名稱(可模糊查)
			@Param("ordDate") Date ordDate, // 預約日期(可空)
			@Param("ordsta") Boolean ordsta // 狀態(可空)
	);

}
