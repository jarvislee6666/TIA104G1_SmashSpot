package com.smashspot.reservationtime.model;

import java.io.Serializable;
import java.sql.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.smashspot.stadium.model.StadiumVO;

@Entity
@Table(name = "reservation_time")
public class ReservationTimeVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rsv_time_id", updatable = false)	
    private Integer rsvTimeId;    // 預約時段編號
	
	@ManyToOne
	@JoinColumn(name = "stdm_id", referencedColumnName = "stdm_id") 
	private StadiumVO stadium;

	
	public StadiumVO getStadium() {
		return stadium;
	}
	public void setStadium(StadiumVO stadium) {
		this.stadium = stadium;
	}
	
	
	@Column(name = "dates")
    private Date dates;           // 日期
	
	@Column(name = "booked")
    private String booked;        // 已預約數量
	
	@Column(name = "rsv_ava")
    private String rsvava;        // 可預約數量
    
	public Integer getRsvtimeid() {
		return rsvTimeId;
	}
	public void setRsvtimeid(Integer rsvtimeid) {
		this.rsvTimeId = rsvtimeid;
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