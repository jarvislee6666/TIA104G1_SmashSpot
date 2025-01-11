package com.smashspot.member.model;


import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.smashspot.admin.model.AdmVO;

public interface MemberRepository extends JpaRepository<MemberVO, Integer> {
    
    MemberVO findByAccount(String account);
    MemberVO findByEmail(String email);
    MemberVO findByPhone(String phone);
    Optional<MemberVO> findById(Integer memberId);
    
}