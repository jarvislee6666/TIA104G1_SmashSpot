package com.smashspot.bid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "favorites_list")
public class FavoritesListVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "favor_list_id", updatable = false)
	private Integer favorlistid;
	
	@Column(name = "mem_id", nullable = false)
	private Integer memid;
	
	@Column(name = "pro_id", nullable = false)
	private Integer proid;
	
}
