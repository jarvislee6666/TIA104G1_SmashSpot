package com.smashspot.bid.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "product")
public class ProductsVO {
	// pr test
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pro_id", updatable = false)
	private Integer proid;

	@Column(name = "mem_id", nullable = false)
	private Integer memid;

	@Column(name = "pro_class_id", nullable = false)
	private Integer proclassid;

	@Column(name = "bid_sta_id", nullable = false)
	private Integer bidstaid;

	@Column(name = "base_price", nullable = false)
	private Integer baseprice;

	@Column(name = "pur_price", nullable = false)
	private Integer purprice;
	
	@Column(name = "intro", nullable = false, length = 255)
	private String intro;
	
	@Column(name = "pro_start_time", nullable = false, updatable = false)
	@CreationTimestamp
	private Timestamp prostarttime;
	
	@Column(name = "end_time", nullable = false)
	private Timestamp endtime;
	
	@Column(name = "mod_time", nullable = false, updatable = false)
	private Timestamp modtime;
	
	@Column(name = "min_incr", nullable = false)
	private Integer minincr; 

	@Column(name = "pro_name", nullable = false, length = 30)
	private String proname;
	
	@Column(name = "pro_pic")
	private byte[] propic;
	
	@Column(name = "max_price")
	private Integer maxprice;

	public Integer getProid() {
		return proid;
	}

	public void setProid(Integer proid) {
		this.proid = proid;
	}

	public Integer getMemid() {
		return memid;
	}

	public void setMemid(Integer memid) {
		this.memid = memid;
	}

	public Integer getProclassid() {
		return proclassid;
	}

	public void setProclassid(Integer proclassid) {
		this.proclassid = proclassid;
	}

	public Integer getBidstaid() {
		return bidstaid;
	}

	public void setBidstaid(Integer bidstaid) {
		this.bidstaid = bidstaid;
	}

	public Integer getBaseprice() {
		return baseprice;
	}

	public void setBaseprice(Integer baseprice) {
		this.baseprice = baseprice;
	}

	public Integer getPurprice() {
		return purprice;
	}

	public void setPurprice(Integer purprice) {
		this.purprice = purprice;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Timestamp getProstarttime() {
		return prostarttime;
	}

	public void setProstarttime(Timestamp prostarttime) {
		this.prostarttime = prostarttime;
	}

	public Timestamp getEndtime() {
		return endtime;
	}

	public void setEndtime(Timestamp endtime) {
		this.endtime = endtime;
	}

	public Timestamp getModtime() {
		return modtime;
	}

	public void setModtime(Timestamp modtime) {
		this.modtime = modtime;
	}

	public Integer getMinincr() {
		return minincr;
	}

	public void setMinincr(Integer minincr) {
		this.minincr = minincr;
	}

	public String getProname() {
		return proname;
	}

	public void setProname(String proname) {
		this.proname = proname;
	}

	public byte[] getPropic() {
		return propic;
	}

	public void setPropic(byte[] propic) {
		this.propic = propic;
	}

	public Integer getMaxprice() {
		return maxprice;
	}

	public void setMaxprice(Integer maxprice) {
		this.maxprice = maxprice;
	}

	@Override
	public String toString() {
		return "ProductsVo [proid=" + proid + ", memid=" + memid + ", proclassid=" + proclassid + ", bidstaid="
				+ bidstaid + ", baseprice=" + baseprice + ", purprice=" + purprice + ", intro=" + intro
				+ ", prostarttime=" + prostarttime + ", endtime=" + endtime + ", modtime=" + modtime + ", minincr="
				+ minincr + ", proname=" + proname + ", propic=" + Arrays.toString(propic) + ", maxprice=" + maxprice
				+ "]";
	}

}
