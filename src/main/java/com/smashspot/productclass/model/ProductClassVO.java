package com.smashspot.productclass.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.*;

import com.smashspot.product.model.ProductVO;

@Entity
@Table(name = "product_classification")
public class ProductClassVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pro_class_id")
	private Integer proclassid;

	@Column(name = "cate_name", nullable = false, length = 20)
	@NotBlank(message = "分類名稱不能為空")
    @Size(max = 20, message = "分類名稱長度不得超過20個字符")
	private String catename;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="productClassVO")
	@OrderBy("proid asc")
	private Set<ProductVO> products = new HashSet<ProductVO>();

	public long getActiveProductCount() {
        return products.stream()
                      .filter(product -> product.getBidstaid() == 1)
                      .count();
    }
	
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

	public Set<ProductVO> getProducts() {
		return products;
	}

	public void setProducts(Set<ProductVO> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "ProductClassificationVO [proclassid=" + proclassid + ", catename=" + catename + "]";
	}

}
