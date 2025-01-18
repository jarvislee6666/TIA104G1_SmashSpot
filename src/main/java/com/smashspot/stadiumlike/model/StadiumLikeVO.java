package com.smashspot.stadiumlike.model;

import javax.persistence.*;

@Entity
@Table(name = "stadium_like")
public class StadiumLikeVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stdm_like_id")
    private Integer stdmLikeId; // 收藏清單編號 (PK)

    @Column(name = "mem_id", nullable = false)
    private Integer memId; // 會員編號 (FK)

    @Column(name = "stdm_id", nullable = false)
    private Integer stdmId; // 場館編號 (FK)

    // --- Constructors ---
    public StadiumLikeVO() {
    }

    public StadiumLikeVO(Integer memId, Integer stdmId) {
        this.memId = memId;
        this.stdmId = stdmId;
    }

    // --- Getters & Setters ---
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
