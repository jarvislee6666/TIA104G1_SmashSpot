package com.smashspot.courtorderdetail.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;

@Repository
public interface CourtOrderDetailRepository extends JpaRepository<CourtOrderDetailVO, Integer> {

}
