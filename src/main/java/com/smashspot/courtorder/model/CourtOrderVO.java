package com.smashspot.courtorder.model;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smashspot.courtorderdetail.model.CourtOrderDetailVO;
import com.smashspot.member.model.MemberVO;
import com.smashspot.stadium.model.StadiumVO;

@Entity
@Table(name = "court_order")
public class CourtOrderVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "court_ord_id", updatable = false)
	private Integer courtordid;

//	@JsonBackReference(value = "stadiumRef")
	@JsonIgnoreProperties({"courtOrder", "reservationTime"})
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "stdm_id", referencedColumnName = "stdm_id")
	private StadiumVO stadium;

	@JsonManagedReference
	@OneToMany(mappedBy = "courtOrder", cascade = CascadeType.ALL)
	private Set<CourtOrderDetailVO> courtOrderDetail;

	public Set<CourtOrderDetailVO> getCourtOrderDetail() {
		return courtOrderDetail;
	}

	public void setCourtOrderDetail(Set<CourtOrderDetailVO> courtOrderDetail) {
		this.courtOrderDetail = courtOrderDetail;
	}

	public StadiumVO getStadium() {
		return stadium;
	}

	public void setStadium(StadiumVO stadium) {
		this.stadium = stadium;
	}

	@JsonBackReference(value = "memberRef")
	@ManyToOne
	@JoinColumn(name = "mem_id", referencedColumnName = "mem_id")
	private MemberVO member;

	@Column(name = "rsv_sta")
	private Boolean ordsta;

	@Column(name = "crt_time")
	private Timestamp ordcrttime;

	@Column(name = "tot_amt")
	private Integer totamt;

	@Column(name = "can_reason")
	private String canreason;

	@Column(name = "star_rank")
	private Integer starrank;

	@Column(name = "message")
	private String message;

	public Integer getCourtordid() {
		return courtordid;
	}

	public void setCourtordid(Integer courtordid) {
		this.courtordid = courtordid;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

	public Boolean getOrdsta() {
		return ordsta;
	}

	public void setOrdsta(Boolean ordsta) {
		this.ordsta = ordsta;
	}

	public Timestamp getOrdcrttime() {
		return ordcrttime;
	}

	public void setOrdcrttime(Timestamp ordcrttime) {
		this.ordcrttime = ordcrttime;
	}

	public Integer getTotamt() {
		return totamt;
	}

	public void setTotamt(Integer totamt) {
		this.totamt = totamt;
	}

	public String getCanreason() {
		return canreason;
	}

	public void setCanreason(String canreason) {
		this.canreason = canreason;
	}

	public Integer getStarrank() {
		return starrank;
	}

	public void setStarrank(Integer starrank) {
		this.starrank = starrank;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}