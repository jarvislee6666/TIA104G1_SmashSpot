package com.smashspot.stadiumlike.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smashspot.stadium.model.StadiumVO;

@Repository
public interface StadiumLikeRepository extends JpaRepository<StadiumLikeVO, Integer> {

    // 找該會員所有收藏清單
    List<StadiumLikeVO> findByMemId(Integer memId);

    // 檢查該會員是否已收藏此場館
    boolean existsByMemIdAndStdmId(Integer memId, Integer stdmId);

    @Query("SELECT s FROM StadiumVO s WHERE s.stdmId IN :ids")
    List<StadiumVO> findByStdmIds(@Param("ids") Set<Integer> ids);
    
    // 找到一筆對應 memId, stdmId 的收藏記錄
    Optional<StadiumLikeVO> findByMemIdAndStdmId(Integer memId, Integer stdmId);

}

	
