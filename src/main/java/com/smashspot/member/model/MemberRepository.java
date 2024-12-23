package com.smashspot.member.model;


import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<MemberVO, Integer> {
    
    @Transactional
    @Modifying
    @Query(value = "delete from member where memid =?1", nativeQuery = true)
    void deleteByMemid(int memid);
    
    //● (自訂)條件查詢
    @Query(value = "from MemberVO where memid=?1 and name like ?2 and status=?3 order by memid")
    List<MemberVO> findByOthers(int memid, String name, Boolean status);
    
    //● 根據帳號查詢會員
    @Query(value = "from MemberVO where account = ?1")
    MemberVO findByAccount(String account);
    
    //● 根據電子郵件查詢會員
    @Query(value = "from MemberVO where email = ?1")
    MemberVO findByEmail(String email);
    
    //● 根據手機號碼查詢會員
    @Query(value = "from MemberVO where phone = ?1")
    MemberVO findByPhone(String phone);
    
    //● 查詢指定日期範圍內註冊的會員
    @Query(value = "from MemberVO where crttime between ?1 and ?2 order by crttime desc")
    List<MemberVO> findByCreateDateBetween(java.sql.Timestamp startDate, java.sql.Timestamp endDate);
}