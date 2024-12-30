package com.smashspot.product.model;

import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.*;
import javax.validation.constraints.*;


import com.smashspot.productclass.model.ProductClassVO;

@Entity
@Table(name = "product")
public class ProductVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pro_id")
	private Integer proid;

	@Column(name = "mem_id", nullable = false)
	@NotNull(message = "會員ID不能為空")
	private Integer memid;
	
	// 與商品分類的多對一關係
    @ManyToOne
    @JoinColumn(name = "pro_class_id", nullable = false)
    private ProductClassVO productClassVO;

	@Column(name = "bid_sta_id", nullable = false)
	@NotNull(message = "競標狀態不能為空")
	private Integer bidstaid;

	@Column(name = "base_price", nullable = false)
	@NotNull(message = "商品底價不能為空")
	private Integer baseprice;

	@Column(name = "pur_price", nullable = false)
	@NotNull(message = "商品直購價不能為空")
	private Integer purprice;
	
	@Column(name = "intro", nullable = false)
	@NotBlank(message = "商品介紹不能為空")
    @Size(max = 255, message = "商品介紹最大長度為255個字符")
	private String intro;
	
	@Column(name = "pro_start_time", insertable = false, updatable = false)
	private Timestamp prostarttime;
	
	@Column(name = "end_time", nullable = false)
	@NotNull(message = "商品結束時間不能為空")
	private Timestamp endtime;
	
	@Column(name = "mod_time", insertable = false, updatable = false)
	private Timestamp modtime;
	
	@Column(name = "min_incr", nullable = false)
	@NotNull(message = "最小增額不能為空")
	@Min(value = 100, message = "最小增額不得小於{value}")
	private Integer minincr;

	@Column(name = "pro_name", nullable = false, length = 30)
	@NotBlank(message = "商品名稱不能為空")
    @Size(max = 30, message = "商品名稱最大長度為30個字符")
	private String proname;
	
	@Lob
	@Column(name = "pro_pic")
	private byte[] propic;
	
	@Column(name = "max_price")
	private Integer maxprice; // 最高金額(競標中、結標)

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

	public ProductClassVO getProductClassVO() {
		return productClassVO;
	}

	public void setProductClassVO(ProductClassVO productClassVO) {
		this.productClassVO = productClassVO;
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
		return "ProductVO [proid=" + proid + ", memid=" + memid + ", productClassVO=" + productClassVO + ", bidstaid="
				+ bidstaid + ", baseprice=" + baseprice + ", purprice=" + purprice + ", intro=" + intro
				+ ", prostarttime=" + prostarttime + ", endtime=" + endtime + ", modtime=" + modtime + ", minincr="
				+ minincr + ", proname=" + proname + ", propic=" + Arrays.toString(propic) + ", maxprice=" + maxprice
				+ "]";
	}


}
