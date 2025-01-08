package com.smashspot.member.model;


import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.admin.model.AdmVO;

public interface MemberRepository extends JpaRepository<MemberVO, Integer> {
    
    MemberVO findByAccount(String account);
    MemberVO findByEmail(String email);
    MemberVO findByPhone(String phone);
    
 // 添加複合查詢方法
    @Query("SELECT m FROM MemberVO m WHERE " +
            "(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:status IS NULL OR " +
            "    (:status = 'true' AND m.status = true) OR " +
            "    (:status = 'false' AND m.status = false))")
     List<MemberVO> findByConditions(
         @Param("name") String name,
         @Param("status") String status
     );
    
}
