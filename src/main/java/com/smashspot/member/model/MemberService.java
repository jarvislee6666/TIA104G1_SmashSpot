package com.smashspot.member.model;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_Member;

@Service("memberService")
public class MemberService {
    
    @Autowired
    MemberRepository repository;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    public void addMember(MemberVO memberVO) {
        repository.save(memberVO);
    }
    
    public void updateMember(MemberVO memberVO) {
        repository.save(memberVO);
    }
    
    public void deleteMember(Integer memid) {
        if (repository.existsById(memid))
            repository.deleteByMemid(memid);
            // repository.deleteById(memid);
    }
    
    public MemberVO getOneMember(Integer memid) {
        Optional<MemberVO> optional = repository.findById(memid);
        // return optional.get();
        return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
    }
    
    public List<MemberVO> getAll() {
        return repository.findAll();
    }
    
//    public List<MemberVO> getAll(Map<String, String[]> map) {
//        return HibernateUtil_CompositeQuery_Member.getAllC(map, sessionFactory.openSession());
//    }
}