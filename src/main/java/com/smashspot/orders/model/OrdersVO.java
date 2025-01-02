package com.smashspot.orders.model;

import java.sql.Timestamp;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.smashspot.product.model.ProductVO;
import com.smashspot.coupon.model.CouponVO;

@Entity
@Table(name = "orders")
public class OrdersVO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ord_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ordid;

    @Column(name = "ord_sta_id", nullable = false)
    @NotNull(message = "訂單狀態不能為空")
    private Integer ordstaid;

    @Column(name = "pro_ord_time", insertable = false, updatable = false)
    private Timestamp proordtime;

    @Column(name = "before_dis", nullable = false)
    @NotNull(message = "折扣前金額不能為空")
    @Min(value = 1, message = "折扣前金額不得小於{value}")
    private Integer beforedis;

    @Column(name = "after_dis", nullable = false)
    @NotNull(message = "折扣後金額不能為空")
    @Min(value = 1, message = "折扣後金額不得小於{value}")
    private Integer afterdis;

    @Column(name = "send_info", nullable = false)
    @NotBlank(message = "寄送資訊不能為空")
    @Size(max = 50, message = "寄送資訊最大長度為50個字符")
    private String sendinfo;

    @Column(name = "mem_id", nullable = false)
    @NotNull(message = "會員ID不能為空")
    private Integer memid;

    @ManyToOne
    @JoinColumn(name = "pro_id", nullable = false)
    @NotNull(message = "商品不能為空")
    private ProductVO productVO;

    @ManyToOne
    @JoinColumn(name = "cop_id")
    private CouponVO couponVO;

    @AssertTrue(message = "折扣後金額必須小於折扣前金額")
    private boolean isValidDiscountAmount() {
        if (beforedis != null && afterdis != null) {
            return afterdis <= beforedis;
        }
        return true;
    }

    public OrdersVO() {
    }

    public Integer getOrdid() {
        return ordid;
    }

    public void setOrdid(Integer ordid) {
        this.ordid = ordid;
    }

    public Integer getOrdstaid() {
        return ordstaid;
    }

    public void setOrdstaid(Integer ordstaid) {
        this.ordstaid = ordstaid;
    }

    public Timestamp getProordtime() {
        return proordtime;
    }

    public void setProordtime(Timestamp proordtime) {
        this.proordtime = proordtime;
    }

    public Integer getBeforedis() {
        return beforedis;
    }

    public void setBeforedis(Integer beforedis) {
        this.beforedis = beforedis;
    }

    public Integer getAfterdis() {
        return afterdis;
    }

    public void setAfterdis(Integer afterdis) {
        this.afterdis = afterdis;
    }

    public String getSendinfo() {
        return sendinfo;
    }

    public void setSendinfo(String sendinfo) {
        this.sendinfo = sendinfo;
    }

    public Integer getMemid() {
        return memid;
    }

    public void setMemid(Integer memid) {
        this.memid = memid;
    }

    public ProductVO getProductVO() {
        return productVO;
    }

    public void setProductVO(ProductVO productVO) {
        this.productVO = productVO;
    }

    public CouponVO getCouponVO() {
        return couponVO;
    }

    public void setCouponVO(CouponVO couponVO) {
        this.couponVO = couponVO;
    }

    @Override
    public String toString() {
        return "OrdersVO [ordid=" + ordid + ", ordstaid=" + ordstaid + ", proordtime=" + proordtime 
               + ", beforedis=" + beforedis + ", afterdis=" + afterdis + ", sendinfo=" + sendinfo 
               + ", memid=" + memid + ", productVO=" + productVO + ", couponVO=" + couponVO + "]";
    }
}
