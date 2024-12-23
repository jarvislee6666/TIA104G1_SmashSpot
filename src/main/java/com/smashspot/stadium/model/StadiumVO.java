package com.smashspot.stadium.model;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.smashspot.admin.model.AdmVO;
import com.smashspot.reservationtime.model.ReservationTimeVO;
import com.smashspot.stadiumlike.StadiumLikeVO;

@Entity
@Table(name = "stadium")
public class StadiumVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer stdmId;
	private String stdmName;
	private String stdmAddr;
	private Integer locId;
	private Double longitude;
	private Double latitude;
	private String stdmIntro;
	private Integer courtCount;
	private Integer courtPrice;
	private Boolean oprSta;
	private byte[] stdmPic;
	private AdmVO admin;
	private Integer openTime;
	private Integer closeTime;
	private Timestamp stdmStartTime;
	private String stdmPicBase64;
	
	// 新增的欄位//add by 麒安
	@OneToMany(mappedBy = "stadium", cascade = CascadeType.ALL)   // "stadium" 必須對應 ReservationTimeVO 中的 @ManyToOne 的變數名稱
	private Set<ReservationTimeVO> reservationTimes; 
	
	
	public Set<ReservationTimeVO> getReservationTimes() {
		return reservationTimes;
	}
	public void setReservationTimes(Set<ReservationTimeVO> reservationTimes) {
		this.reservationTimes = reservationTimes;
	}
	
	
	@OneToMany(mappedBy = "stadium", cascade = CascadeType.ALL)   
	private Set<StadiumLikeVO> StadiumLike; 
	
	public Set<StadiumLikeVO> getStadiumLike() {
		return StadiumLike;
	}
	public void setStadiumLike(Set<StadiumLikeVO> stadiumLike) {
		StadiumLike = stadiumLike;
	}
	
	//改了念芸的第117行
	//	@JoinColumn(name = "loc_id", referencedColumnName="loc_id")
	//	@ManyToOne
	
	
	
	
	
	//add 結束 by 麒安

	public StadiumVO() { 
	}
	
	
	@Id 
	@Column(name = "stdm_id")  
	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】 
	public Integer getStdmId() {
		return this.stdmId;
	}
	public void setStdmId(Integer stdmId) {
		this.stdmId = stdmId;
	}
	
	
	@Column(name = "stdm_name")
	@NotEmpty(message="場館名稱: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$", message = "場館名稱: 只能是中、英文字母、數字和_ , 且長度必需在2到20之間")
	public String getStdmName() {
		return stdmName;
	}
	public void setStdmName(String stdmName) {
		this.stdmName = stdmName;
	}
	
	
	@Column(name = "stdm_addr")
	@NotEmpty(message="場館地址: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,50}$", message = "場館地址: 只能是中、英文字母、數字和_ , 且長度必需在2到50之間")
	public String getStdmAddr() {
		return stdmAddr;
	}
	public void setStdmAddr(String stdmAddr) {
		this.stdmAddr = stdmAddr;
	}
	
	
	//【此處預設為 @ManyToOne(fetch=FetchType.EAGER) --> 是指 lazy="false"之意】【注意: 此處的預設值與XML版 (p.127及p.132) 的預設值相反】
	//【如果修改為 @ManyToOne(fetch=FetchType.LAZY)  --> 則指 lazy="true" 之意】
	// @JoinColumn(name = "") 場館PK待加
	@JoinColumn(name = "loc_id", referencedColumnName="loc_id")
	@ManyToOne
	public Integer getLocId() {
		return locId;
	}
	public void setLocId(Integer locId) {
		this.locId = locId;
	}
	
	
	@Column(name = "longitude")
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	
	@Column(name = "latitude")
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	
	@Column(name = "stdm_intro")
	@NotEmpty(message="場館簡介: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]$", message = "場館簡介: 只能是中、英文字母、數字和_ ,")
	public String getStdmIntro() {
		return stdmIntro;
	}
	public void setStdmIntro(String stdmIntro) {
		this.stdmIntro = stdmIntro;
	}
	
	
	@Column(name = "court_count")
	@NotNull(message="球場數量請填數字")
	public Integer getCourtCount() {
		return courtCount;
	}
	public void setCourtCount(Integer courtCount) {
		this.courtCount = courtCount;
	}
	
	
	@Column(name = "court_price")
	@NotNull(message="球場價格請填數字")
	@Min(10)
	public Integer getCourtPrice() {
		return courtPrice;
	}
	public void setCourtPrice(Integer courtPrice) {
		this.courtPrice = courtPrice;
	}
	
	
	@Column(name = "opr_sta")
	public Boolean getOprSta() {
		return oprSta;
	}
	public void setOprSta(Boolean oprSta) {
		this.oprSta = oprSta;
	}
	
	
	@Column(name = "stdm_pic")
	@NotEmpty(message="場館照片: 請上傳照片") 
	//--> 由Controller.java 處理錯誤信息
	public byte[] getStdmPic() {
		return stdmPic;
	}
	public void setStdmPic(byte[] stdmPic) {
		this.stdmPic = stdmPic;
	}
	
	
	//【此處預設為 @ManyToOne(fetch=FetchType.EAGER) --> 是指 lazy="false"之意】【注意: 此處的預設值與XML版 (p.127及p.132) 的預設值相反】
	//【如果修改為 @ManyToOne(fetch=FetchType.LAZY)  --> 則指 lazy="true" 之意】
	@JoinColumn(name = "adm_id", referencedColumnName="adm_id")
	//@Column(name = "adm_id")
	@ManyToOne
	public AdmVO getAdmVO() {
		return admin;
	}
	public void setAdmVO(AdmVO admin) {
		this.admin = admin;
	}
	
	//開閉館時間的驗證邏輯待修~~~
	@Column(name = "open_time")
	@NotNull(message="開館時間請填數字")
	@DecimalMin(value = "0", message = "開館時間: 不能小於{value}點")
	@DecimalMax(value = "24", message = "開館時間: 不能超過{value}點")
	public Integer getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Integer openTime) {
		this.openTime = openTime;
	}


	@Column(name = "close_time")
	@NotNull(message="閉館時間請填數字")
	@DecimalMin(value = "0", message = "閉館時間: 不能小於{value}點")
	@DecimalMax(value = "24", message = "閉館時間: 不能超過{value}點")
	public Integer getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Integer closeTime) {
		this.closeTime = closeTime;
	}
	
	
	@Column(name = "stdm_start_time")
	public Timestamp getStdmStartTime() {
		return stdmStartTime;
	}
	public void setStdmStartTime(Timestamp stdmStartTime) {
		this.stdmStartTime = stdmStartTime;
	}
	
	
	// 圖片格式先以Base64處理，0205版本無下面getter/setter
	public String getStdmPicBase64() {
		return stdmPicBase64;
	}
	public void setStdmPicBase64(byte[] stdmPic) {
		if(stdmPic != null) {
			this.stdmPicBase64 = Base64.getEncoder().encodeToString(stdmPic);
		}
		
	}
	
	
}
