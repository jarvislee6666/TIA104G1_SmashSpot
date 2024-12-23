package com.smashspot.stadiumlike;

import java.sql.Timestamp; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "stadium_like")
public class StadiumLikeVO {
	//test
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stdm_like_id", nullable = false, updatable = false)
    private Integer stdmLikeId; // 收藏清單編號 (PK)

    @Column(name = "mem_id", nullable = false)
    private Integer memId; // 會員編號 (FK)

    @Column(name = "stdm_id", nullable = false)
    private Integer stdmId; // 場館編號 (FK)

    // 無參數建構子
    public StadiumLikeVO() {
    }

    // Getter 和 Setter 方法
    public Integer getStdmLikeId() {
        return stdmLikeId;
    }

    public void setStdmLikeId(Integer stdmLikeId) {
        this.stdmLikeId = stdmLikeId;
    }

    public Integer getMemId() {
        return memId;
    }

    public void setMemId(Integer memId) {
        this.memId = memId;
    }

    public Integer getStdmId() {
        return stdmId;
    }

    public void setStdmId(Integer stdmId) {
        this.stdmId = stdmId;
    }

    @Override
    public String toString() {
        return "StadiumLikeVO{" +
                "stdmLikeId=" + stdmLikeId +
                ", memId=" + memId +
                ", stdmId=" + stdmId +
                '}';
    }

}
