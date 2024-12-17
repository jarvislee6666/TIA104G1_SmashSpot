package com.smashspot.courtorder.model;

import java.sql.Timestamp; 

public class CourtOrderVO implements java.io.Serializable {
    private Integer stadiumOrderId;    // stadium_order_id
    private Integer memberId;          // member_id
    private Integer stadiumId;         // stadium_id
    private Boolean reservationStatus; // reservation_status
    private Timestamp createdAt;          // created_at
    private Integer totalAmount;       // total_amount
    private String cancelReason;       // cancel_reason
    private String commentText;        // comment_text
    private Integer rating;            // rating

    // Getters and Setters
    public Integer getStadiumOrderId() {
        return stadiumOrderId;
    }

    public void setStadiumOrderId(Integer stadiumOrderId) {
        this.stadiumOrderId = stadiumOrderId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(Integer stadiumId) {
        this.stadiumId = stadiumId;
    }

    public Boolean getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(Boolean reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}