package com.smashspot.bid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_classification")
public class ProductClassificationVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pro_class_id", updatable = false)
	private Integer proclassid;

	@Column(name = "cate_name", nullable = false, length = 20)
	private String catename;

	public Integer getProclassid() {
		return proclassid;
	}

	public void setProclassid(Integer proclassid) {
		this.proclassid = proclassid;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	@Override
	public String toString() {
		return "ProductClassificationVO [proclassid=" + proclassid + ", catename=" + catename + "]";
	}

}
