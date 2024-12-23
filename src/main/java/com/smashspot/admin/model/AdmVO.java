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
import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import com.smashspot.stadium.model.StadiumVO;



@Entity
@Table(name = "admin")
@NamedQuery(name = "getAllEmps", query = "from AdmVO where adm_id > :adm_id order by adm_id desc")
public class AdmVO{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_id", updatable = false)	
	private Integer admid;//admin_id
	
	@OneToMany(mappedBy = "admVO", cascade = CascadeType.ALL)
	
	private Set<StadiumVO> stadiumVO;
	
	@Column(name = "adm_email")
	@NotEmpty(message="管理員email: 請勿空白")
	@Email(message = "請輸入有效的電子郵件地址")
	private String admemail;//admin_email
	
	@Column(name = "adm_password")
	@NotEmpty(message="管理員密碼: 請勿空白")
	@Pattern(regexp = "^[(a-zA-Z0-9)]{5,15}$", message = "管理員密碼: 只能是英文字母、數字, 且長度必需在5到15之間")
	private String admpassword;//admin_password
	
	@Column(name = "adm_name")
	@NotEmpty(message="員工姓名: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "管理員姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String admname;//admin_name
	
	@Column(name = "adm_phone")
	@NotEmpty(message="管理員電話: 請勿空白")
	@Pattern(regexp = "^09\\d{8}$",message = "請輸入正確手機號碼格式")
	private String admphone;//admin_phone
	
	@Column(name = "hr_date")
	@NotEmpty(message="雇用日期: 請勿空白")	
	@FutureOrPresent(message = "日期必須是今日(含)或之後")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date hrdate;//hire_date
	
	@Column(name = "upd_time", updatable = false, insertable = false)
	private Timestamp updtime;//update_time
	
	@Column(name = "adm_sta")
	private Boolean admsta;//admin_state
	
	@Column(name = "adm_bday")
	@NotEmpty(message="雇用日期: 請勿空白")	
	@Past(message="日期必須是在今日(含)之前")
	@DateTimeFormat(pattern="yyyy-MM-dd")
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

//	public Set<StadiumVO> getStadium() {
//		return stadiumVO;
//	}
//
//	public void setStadium(Set<StadiumVO> stadium) {
//		this.stadiumVO = stadiumVO;
//	}

	
	
	
	
	
	
	
	
}
