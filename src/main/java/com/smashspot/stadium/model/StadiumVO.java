package com.smashspot.stadium.model;


import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.math.BigDecimal;

public class StadiumVO implements Serializable {
    private Integer stdmid;          // 場館編號
    private String stdmname;         // 場館名稱
    private String stdmaddr;         // 場館地址
    private Integer locid;           // 地區編號
    private BigDecimal longitude;    // 經度
    private BigDecimal latitude;     // 緯度
    private String stdmintro;        // 場館介紹
    private Integer courtcount;      // 場地數量
    private Integer courtprice;      // 場地價格
    private Boolean oprsta;          // 營業狀態
    private byte[] stdmpic;          // 場館圖片
    private Integer admid;           // 管理員編號
    private Timestamp stdmstarttime; // 開始營業時間
    private Time opentime;           // 開放時間
    private Time closetime;          // 關閉時間
	public Integer getStdmid() {
		return stdmid;
	}
	public void setStdmid(Integer stdmid) {
		this.stdmid = stdmid;
	}
	public String getStdmname() {
		return stdmname;
	}
	public void setStdmname(String stdmname) {
		this.stdmname = stdmname;
	}
	public String getStdmaddr() {
		return stdmaddr;
	}
	public void setStdmaddr(String stdmaddr) {
		this.stdmaddr = stdmaddr;
	}
	public Integer getLocid() {
		return locid;
	}
	public void setLocid(Integer locid) {
		this.locid = locid;
	}
	public BigDecimal getLongitude() {
		return longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	public String getStdmintro() {
		return stdmintro;
	}
	public void setStdmintro(String stdmintro) {
		this.stdmintro = stdmintro;
	}
	public Integer getCourtcount() {
		return courtcount;
	}
	public void setCourtcount(Integer courtcount) {
		this.courtcount = courtcount;
	}
	public Integer getCourtprice() {
		return courtprice;
	}
	public void setCourtprice(Integer courtprice) {
		this.courtprice = courtprice;
	}
	public Boolean getOprsta() {
		return oprsta;
	}
	public void setOprsta(Boolean oprsta) {
		this.oprsta = oprsta;
	}
	public byte[] getStdmpic() {
		return stdmpic;
	}
	public void setStdmpic(byte[] stdmpic) {
		this.stdmpic = stdmpic;
	}
	public Integer getAdmid() {
		return admid;
	}
	public void setAdmid(Integer admid) {
		this.admid = admid;
	}
	public Timestamp getStdmstarttime() {
		return stdmstarttime;
	}
	public void setStdmstarttime(Timestamp stdmstarttime) {
		this.stdmstarttime = stdmstarttime;
	}
	public Time getOpentime() {
		return opentime;
	}
	public void setOpentime(Time opentime) {
		this.opentime = opentime;
	}
	public Time getClosetime() {
		return closetime;
	}
	public void setClosetime(Time closetime) {
		this.closetime = closetime;
	}

}