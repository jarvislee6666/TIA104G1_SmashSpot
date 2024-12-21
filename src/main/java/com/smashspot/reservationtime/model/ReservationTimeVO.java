package com.smashspot.reservationtime.model;

import java.io.Serializable;
import java.sql.Date;


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
@Table(name = "reservation_time")
public class ReservationTimeVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rsv_time_id", updatable = false)	
    private Integer rsvtimeid;    // 預約時段編號
	
	@Column(name = "stm_id")
    private Integer stdmid;       // 場館編號
	
	@Column(name = "dates")
    private Date dates;           // 日期
	
	@Column(name = "booked")
    private String booked;        // 已預約數量
	
	@Column(name = "rsv_ava")
    private String rsvava;        // 可預約數量
    
	public Integer getRsvtimeid() {
		return rsvtimeid;
	}
	public void setRsvtimeid(Integer rsvtimeid) {
		this.rsvtimeid = rsvtimeid;
	}
	public Integer getStdmid() {
		return stdmid;
	}
	public void setStdmid(Integer stdmid) {
		this.stdmid = stdmid;
	}
	public Date getDates() {
		return dates;
	}
	public void setDates(Date dates) {
		this.dates = dates;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public String getRsvava() {
		return rsvava;
	}
	public void setRsvava(String rsvava) {
		this.rsvava = rsvava;
	}

}
