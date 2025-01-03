package com.smashspot.member.model;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "member")
public class MemberVO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer memid;
    private String account;
    private String password;
    private String name;
    private String email;
    private Timestamp crttime;
    private Timestamp chgtime;
    private String phone;
    private Date bday;
    private String addr;
    private Boolean status;
    private byte[] mempic;

    public MemberVO() {
    }

    @Id
    @Column(name = "mem_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getMemid() {
        return this.memid;
    }

    public void setMemid(Integer memid) {
        this.memid = memid;
    }

    @Column(name = "account", length = 100)
    @NotEmpty(message="帳號: 請勿空白")
    @Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,100}$", 
             message = "帳號: 只能是中、英文字母、數字和_ , 且長度必需在2到100之間")
    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Column(name = "password", length = 20)
    @NotEmpty(message="密碼: 請勿空白")
    @Size(min = 6, max = 20, message="密碼長度必需在{min}到{max}之間")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "name", length = 20)
    @NotEmpty(message="姓名: 請勿空白")
    @Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z)]{2,20}$", 
             message = "姓名: 只能是中、英文字母, 且長度必需在2到20之間")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "email", length = 100)
    @NotEmpty(message="Email: 請勿空白")
    @Email(message = "Email格式不正確")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "crt_time")
    @NotNull(message="建立時間: 請勿空白")
    public Timestamp getCrttime() {
        return this.crttime;
    }

    public void setCrttime(Timestamp crttime) {
        this.crttime = crttime;
    }

    @Column(name = "chg_time")
    @NotNull(message="修改時間: 請勿空白")
    public Timestamp getChgtime() {
        return this.chgtime;
    }

    public void setChgtime(Timestamp chgtime) {
        this.chgtime = chgtime;
    }

    @Column(name = "phone", length = 15)
    @NotEmpty(message="電話: 請勿空白")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "bday")
    @NotNull(message="生日: 請勿空白")
    @Past(message="生日必須是在今日(含)之前")
    public Date getBday() {
        return this.bday;
    }

    public void setBday(Date bday) {
        this.bday = bday;
    }

    @Column(name = "addr", length = 100)
    @NotEmpty(message="地址: 請勿空白")
    @Size(min = 5, max = 100, message="地址長度必需在{min}到{max}之間")
    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Column(name = "status")
    @NotNull(message="帳號狀態: 請勿空白")
    public Boolean getStatus() {
        return this.status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Column(name = "mem_pic")
    @Lob
    public byte[] getMempic() {
        return this.mempic;
    }

    public void setMempic(byte[] mempic) {
        this.mempic = mempic;
    }

	
	

	
}