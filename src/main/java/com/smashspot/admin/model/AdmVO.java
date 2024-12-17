package com.smashspot.admin.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.smashspot.stadium.model.StadiumVO;



@Entity
@Table(name = "admin")
@NamedQuery(name = "getAllEmps", query = "from AdmVO where adm_id > :adm_id order by adm_id desc")
public class AdmVO{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_id", updatable = false)	
	private Integer admid;//admin_id
	
	@OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
	
	private Set<StadiumVO> stadium;
	
	@Column(name = "adm_email")
	private String admemail;//admin_email
	
	@Column(name = "adm_password")
	private String admpassword;//admin_password
	
	@Column(name = "adm_name")
	private String admname;//admin_name
	
	@Column(name = "adm_phone")
	private String admphone;//admin_phone
	
	@Column(name = "hr_date")
	private Date hrdate;//hire_date
	
	@Column(name = "upd_time", updatable = false, insertable = false)
	private Timestamp updtime;//update_time
	
	@Column(name = "adm_sta")
	private Boolean admsta;//admin_state
	
	@Column(name = "adm_bday")
	private Date admbday;//admin_birthday
	
	@Column(name = "supvsr")
	private Boolean supvsr;//supervisor
	
	public AdmVO() {
		super();
	}
	
	public Integer getAdmid() {
		return admid;
	}
	public void setAdmid(Integer admid) {
		this.admid = admid;
	}
	public String getAdmemail() {
		return admemail;
	}
	public void setAdmemail(String admemail) {
		this.admemail = admemail;
	}
	public String getAdmpassword() {
		return admpassword;
	}
	public void setAdmpassword(String admpassword) {
		this.admpassword = admpassword;
	}
	public String getAdmname() {
		return admname;
	}
	public void setAdmname(String admname) {
		this.admname = admname;
	}
	public String getAdmphone() {
		return admphone;
	}
	public void setAdmphone(String admphone) {
		this.admphone = admphone;
	}
	public Date getHrdate() {
		return hrdate;
	}
	public void setHrdate(Date hrdate) {
		this.hrdate = hrdate;
	}
	public Timestamp getUpdtime() {
		return updtime;
	}
	public void setUpdtime(Timestamp updtime) {
		this.updtime = updtime;
	}
	public Boolean getAdmsta() {
		return admsta;
	}
	public void setAdmsta(Boolean admsta) {
		this.admsta = admsta;
	}
	public Date getAdmbday() {
		return admbday;
	}
	public void setAdmbday(Date admbday) {
		this.admbday = admbday;
	}
	public Boolean getSupvsr() {
		return supvsr;
	}
	public void setSupvsr(Boolean supvsr) {
		this.supvsr = supvsr;
	}

	public Set<StadiumVO> getStadium() {
		return stadium;
	}

	public void setStadium(Set<StadiumVO> stadium) {
		this.stadium = stadium;
	}
	
	
	
	
	
	
	
	
}
