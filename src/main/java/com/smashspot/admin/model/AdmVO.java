package com.smashspot.admin.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



@Entity
@Table(name = "admin")
@NamedQuery(name = "getAllEmps", query = "from Adm where adm_id > :adm_id order by adm_id desc")
public class AdmVO{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_id", updatable = false)	
	private Integer admid;//admin_id
	
	@Column(name = "adm_email")
	private String admemail;//admin_email
	
	@Column(name = "adm_email")
	private String admpassword;//admin_password
	
	private String admname;//admin_name
	
	private String admphone;//admin_phone
	private Date hrdate;//hire_date
	private Timestamp updtime;//update_time
	private Boolean admsta;//admin_state
	private Date admbday;//admin_birthday
	private Boolean supvsr;//supervisor
	
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
	
	
	
	
	
	
	
	
}
