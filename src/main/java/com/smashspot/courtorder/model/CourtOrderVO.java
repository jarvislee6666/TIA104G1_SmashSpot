package com.smashspot.courtorder.model;

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
@Table(name = "court_order")
public class CourtOrderVO implements java.io.Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "court_ord_id", updatable = false)	
    private Integer courtordid;
	
	@Column(name = "mem_id")
    private Integer memid;          
	
	@Column(name = "stdm_id")
    private Integer stdmid;        
	
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
	
	@Column(name = "messsage")
    private String messsag;   
	
	public Integer getCourtordid() {
		return courtordid;
	}

	public void setCourtordid(Integer courtordid) {
		this.courtordid = courtordid;
	}

	public Integer getMemid() {
		return memid;
	}

	public void setMemid(Integer memid) {
		this.memid = memid;
	}

	public Integer getStdmid() {
		return stdmid;
	}

	public void setStdmid(Integer stdmid) {
		this.stdmid = stdmid;
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

	public String getMesssag() {
		return messsag;
	}

	public void setMesssag(String messsag) {
		this.messsag = messsag;
	}
         


}