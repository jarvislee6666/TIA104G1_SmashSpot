package com.smashspot.stadium.model;

import java.util.*;

import com.stadium.model.StadiumVO;

public interface StadiumDAO_interface {
	public void insert(StadiumVO stadiumVO);
    public void update(StadiumVO stadiumVO);
    public void delete(Integer stdmId);
    public StadiumVO findByPrimaryKey(Integer stdmId);
    public List<StadiumVO> getAll();
    //萬用複合查詢(傳入參數型態Map)(回傳 List)
//  public List<StadiumVO> getAll(Map<String, String[]> map); 
}
