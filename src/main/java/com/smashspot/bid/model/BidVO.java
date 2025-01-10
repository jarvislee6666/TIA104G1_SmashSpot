package com.smashspot.bid.model;

import java.io.Serializable;
import java.sql.Timestamp;


public class BidVO implements Serializable{

	private Integer bidid;
	
	private Integer memid;
	
	private Integer proid;
	
	private Timestamp bidtime;
	
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

	public Timestamp getBidtime() {
		return bidtime;
	}

	public void setBidtime(Timestamp bidtime) {
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
