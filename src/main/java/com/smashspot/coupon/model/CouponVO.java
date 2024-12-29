package com.smashspot.coupon.model;

//import java.sql.Date;
import java.util.Date;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

@Entity // 要加上@Entity才能成為JPA的一個Entity類別
@Table(name = "coupon") // 代表這個class是對應到資料庫的實體table
public class CouponVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id // @Id代表PK
	@Column(name = "cop_id") // 對應該Table的哪個欄位 (非必要，但當欄位名稱與屬性名稱不同時則一定要用)
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE
	private Integer copid;

	@Column(name = "cop_code")
	@NotEmpty(message = "優惠碼: 請勿空白")
	@Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "優惠碼: 只能是英文字母、數字, 且長度必需在2到20之間")
	private String copcode;

	@Column(name = "crt_date", insertable = false, updatable = false)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date crtdate;

	@Column(name = "end_date")
	@NotNull(message = "使用期限: 請勿空白")
	@Future(message = "結束日期必須大於現在")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
//	@AssertTrue(message = "結束日期不得超過兩個月")
	private Date enddate;

	@NotNull(message = "折扣金額: 請勿空白")
	@Min(value = 10, message = "折扣金額不得小於{value}")
	@Max(value = 5000, message = "折扣金額不得大於{value}")
	private Integer discount;

	// 用於驗證結束日期是否在兩個月內的方法
	@AssertTrue(message = "結束日期不得超過兩個月")
	private boolean isEndDateValid() {
		if (enddate == null) {
			return true;
		}

		Calendar now = Calendar.getInstance();
		Calendar twoMonthsLater = Calendar.getInstance();
		twoMonthsLater.add(Calendar.MONTH, 2);

		return enddate.after(now.getTime()) && enddate.before(twoMonthsLater.getTime());
	}

	public CouponVO() { // 必需有一個不傳參數建構子(JavaBean基本知識)
	}

	public Integer getCopid() {
		return copid;
	}

	public void setCopid(Integer copid) {
		this.copid = copid;
	}

	public String getCopcode() {
		return copcode;
	}

	public void setCopcode(String copcode) {
		this.copcode = copcode;
	}

	public Date getCrtdate() {
		return crtdate;
	}

	public void setCrtdate(Date crtdate) {
		this.crtdate = crtdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	
	
}
