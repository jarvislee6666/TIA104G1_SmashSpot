package com.smashspot.stadiumlike.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StadiumLikeService {

    @Autowired
    private StadiumLikeRepository stadiumLikeRepo;

    /**
     * 新增收藏
     */
    public boolean addLike(Integer memId, Integer stdmId) {
        // 1) 檢查是否已收藏
        boolean alreadyExists = stadiumLikeRepo.existsByMemIdAndStdmId(memId, stdmId);
        if (alreadyExists) {
            // 已存在 => 回傳 false / 或拋出例外
            return false;
        }
        // 2) 不存在 => 建立並存
        StadiumLikeVO likeVO = new StadiumLikeVO(memId, stdmId);
        stadiumLikeRepo.save(likeVO);
        return true;
    }

    /**
     * 取消收藏
     */
    public boolean removeLike(Integer memId, Integer stdmId) {
        Optional<StadiumLikeVO> existing = stadiumLikeRepo.findByMemIdAndStdmId(memId, stdmId);
        if (existing.isPresent()) {
            stadiumLikeRepo.delete(existing.get());
            return true;
        }
        return false;
    }

    /**
     * 檢查是否已收藏
     */
    public boolean isLiked(Integer memId, Integer stdmId) {
        return stadiumLikeRepo.existsByMemIdAndStdmId(memId, stdmId);
    }

    /**
     * 取得該會員的所有收藏清單
     */
    public List<StadiumLikeVO> getUserLikes(Integer memId) {
        return stadiumLikeRepo.findByMemId(memId);
    }

    /**
     * 取得收藏數量
     */
    public long getLikeCount(Integer memId) {
        // 也可以用 stadiumLikeRepo.countByMemId(memId) (若自己在 Repository 寫了對應方法)
        return stadiumLikeRepo.findByMemId(memId).size();
    }
}
