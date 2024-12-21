package com.smashspot.bid.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
@Entity
@Table(name = "bid")
public class BidVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bid_id", updatable = false)
	private Integer bidid;
	
	@Column(name = "mem_id", updatable = false)
	private Integer memid;
	
	@Column(name = "pro_id", updatable = false)
	private Integer proid;
	
	@Column(name = "bid_time", updatable = false)
	@CreationTimestamp
	private LocalDateTime bidtime;
	
	@Column(name = "bid_amt", updatable = false)
	private Integer bidamt;

	public Integer getBidid() {
		return bidid;
	}

	public void setBidid(Integer bidid) {
		this.bidid = bidid;
	}

	public Integer getMemid() {
		return memid;
	}

	public void setMemid(Integer memid) {
		this.memid = memid;
	}

	public Integer getProid() {
		return proid;
	}

	public void setProid(Integer proid) {
		this.proid = proid;
	}

	public LocalDateTime getBidtime() {
		return bidtime;
	}

	public void setBidtime(LocalDateTime bidtime) {
		this.bidtime = bidtime;
	}

	public Integer getBidamt() {
		return bidamt;
	}

	public void setBidamt(Integer bidamt) {
		this.bidamt = bidamt;
	}

	@Override
	public String toString() {
		return "BidVO [bidid=" + bidid + ", memid=" + memid + ", proid=" + proid + ", bidtime=" + bidtime + ", bidamt="
				+ bidamt + "]";
	}
	
	
}
