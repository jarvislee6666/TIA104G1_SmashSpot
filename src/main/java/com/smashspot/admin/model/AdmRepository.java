package com.smashspot.admin.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;




public interface AdmRepository extends JpaRepository<AdmVO, Integer> {

	@Query(value = "from AdmVO where admid=?1 and admname like?2 and hrdate=?3 order by admid")
	List<AdmVO> findByOthers(int admid , String admname , java.sql.Date hrdate);
}
