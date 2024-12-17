
package com.smashspot.reservation.model;

import java.io.Serializable;
import java.sql.Date;

public class ReservationTimeVO {
	
    private Integer rsvtimeid;    // 預約時段編號
    private Integer stdmid;       // 場館編號
    private Date dates;           // 日期
    private String booked;        // 已預約數量
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
