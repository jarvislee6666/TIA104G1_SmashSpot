package com.smashspot.bid.model;

import java.util.List;

public interface FavoritesListDAO_interface {
	public FavoritesListVO insert(Integer memid, Integer proid); // 加入收藏清單

	public List<FavoritesListVO> findByMemid(Integer memid); // 買家收藏清單列表
}
