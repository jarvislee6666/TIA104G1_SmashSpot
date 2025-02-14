package com.smashspot.member.model;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smashspot.courtorder.model.CourtOrderVO;
import com.smashspot.product.model.ProductVO;

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
    
    private Set<ProductVO> products = new HashSet<ProductVO>();

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
    @Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{5,20}$", 
             message = "帳號: 只能是中、英文字母、數字和_ , 且長度必需在5到20之間")
    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Column(name = "password", length = 20)
    @NotEmpty(message="密碼: 請勿空白")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "密碼: 必須包含英文字母與數字的組合，且長度需在6到20之間")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "name", length = 20)
    @NotEmpty(message="姓名: 請勿空白")
    @Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z)]{2,20}$", message = "姓名: 只能是中、英文字母, 且長度必需在2到20之間")
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

    @Column(name = "crt_time", insertable = false)
    public Timestamp getCrttime() {
        return this.crttime;
    }

    public void setCrttime(Timestamp crttime) {
        this.crttime = crttime;
    }

    @Column(name = "chg_time", updatable = false, insertable = false)
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
    @DateTimeFormat(pattern="yyyy-MM-dd")
    public Date getBday() {
        return this.bday;
    }

    public void setBday(Date bday) {
        this.bday = bday;
    }

    @Column(name = "addr", length = 100)
    @NotEmpty(message="地址: 請勿空白")
    @Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{5,30}$", message = "地址: 只能是中、英文字母、數字和 , 且長度必需在5到30之間")
    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Column(name = "status")
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
    
    @OneToMany(mappedBy = "memberVO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<ProductVO> getProducts() {
        return products;
    }
 
    public void setProducts(Set<ProductVO> products) {
        this.products = products;
    }
    
   
    public void addProduct(ProductVO product) {
        getProducts().add(product);
        product.setMemberVO(this);
    }

    public void removeProduct(ProductVO product) {
        getProducts().remove(product);
        product.setMemberVO(null);
    }
 
    private Set<CourtOrderVO> CourtOrder;
    
    @JsonManagedReference(value = "memberRef")
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	public Set<CourtOrderVO> getCourtOrder() {
		return CourtOrder;
	}

	public void setCourtOrder(Set<CourtOrderVO> courtOrder) {
		CourtOrder = courtOrder;
	}


	
}