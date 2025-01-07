package com.smashspot.courtorder.model;

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
     * 依照 member.memId 來查詢所有 CourtOrderVO。
     * 這種命名規則方式，Spring Data JPA 會自動生成對應的 SQL。
     */
	//目前沒用到
    List<CourtOrderVO> findByMember_Memid(Integer memid);

    /**
     * 若需要一次將主檔 + 明細 (OneToMany) 全部撈回來，
     * 可以使用 JOIN FETCH 避免 LazyInitializationException
     */
    @Query("SELECT DISTINCT co FROM CourtOrderVO co "
    	     + "JOIN FETCH co.courtOrderDetail "
    	     + "WHERE co.member.memid = :memid")
    List<CourtOrderVO> findOrdersWithDetailsByMemberid(@Param("memid") Integer memid);
    
    
    
    
    
    
}
