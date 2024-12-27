package com.smashspot.courtorder.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.stadium.model.StadiumVO;

@Repository
public interface CourtOrderRepository extends JpaRepository<CourtOrderVO, Integer> {


}
