package com.smashspot.stadium.model;

import java.util.*;

import com.smashspot.stadium.model.*;

public interface StadiumDAO_interface {
	int insert(StadiumVO stdmVO);
	
    int update(StadiumVO stdmVO);
    
    int delete(Integer stdmId);
    
    //單一查詢
//    StadiumVO findByStdmName(String stdmName);
//    StadiumVO findByLocId(Integer locId);
    
    List<StadiumVO> getAll();
    
    //萬用複合查詢(傳入參數型態Map)(回傳 List) 查詢: 球館名稱、地區，最後再加上麒安ㄉ預約日期查詢~
    List<StadiumVO> getAll(Map<String, String> map); 
    
	//視前端需求
    //List<StadiumVO> getAll(int currentPage);
	
	//long getTotal();
}
