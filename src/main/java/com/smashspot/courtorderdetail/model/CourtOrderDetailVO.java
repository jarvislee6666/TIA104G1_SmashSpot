package com.smashspot.courtorderdetail.model;

import java.sql.Date;
import java.sql.Timestamp; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "court_order_detail")
public class CourtOrderDetailVO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ord_dtl_id", nullable = false, updatable = false)
    private Integer ordDtlId; // 流水號 (PK)

    @Column(name = "court_ord_id", nullable = false)
    private Integer courtOrdId; // 場地訂單編號 (FK)

	@Column(name = "ord_date", nullable = false)
    private Date ordDate; // 預約日期

    @Column(name = "ord_time", nullable = false, length = 12)
    private String ordTime; // 預約時段
    
    public Integer getOrdDtlId() {
		return ordDtlId;
	}

	public void setOrdDtlId(Integer ordDtlId) {
		this.ordDtlId = ordDtlId;
	}

	public Integer getCourtOrdId() {
		return courtOrdId;
	}

	public void setCourtOrdId(Integer courtOrdId) {
		this.courtOrdId = courtOrdId;
	}

	public Date getOrdDate() {
		return ordDate;
	}

	public void setOrdDate(Date ordDate) {
		this.ordDate = ordDate;
	}

	public String getOrdTime() {
		return ordTime;
	}

	public void setOrdTime(String ordTime) {
		this.ordTime = ordTime;
	}
}
