package com.smashspot.bid.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class OrdersVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ord_id", updatable = false)
	private Integer ordid;
	
	@Column(name = "ord_sta_id", nullable = false)
	private Integer ordstaid;
	
	@Column(name = "pro_ord_time", nullable = false, updatable = false)
	private Timestamp proordtime;
	
	@Column(name = "before_dis", nullable = false)
	private Integer beforedis;
	
	@Column(name = "after_dis", nullable = false)
	private Integer afterdis;
	
	@Column(name = "send_info", nullable = false, length = 50)
	private String sendinfo;
	
	@Column(name = "cop_id", nullable = false)
	private Integer copid;
	
	@Column(name = "mem_id", nullable = false)
	private Integer memid;
	
	@Column(name = "pro_id", nullable = false)
	private Integer proid;

	
}
