package com.smashspot.stadium.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface StdmRepository extends JpaRepository<StadiumVO, Integer>{

	@Transactional
	@Modifying
	@Query(value = "delete from stdm where stdmId =?1", nativeQuery = true)
	void deleteByStdmId(int stdmId);

	//● (自訂)條件查詢
	@Query(value = "from StdmVO where stdmId=?1 and stdmName like?2 and locId=?3 order by stdmId")
	List<StadiumVO> findByOthers(int stdmId , String stdmName , int locId);
}
