package com.smashspot.admin.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface AdmRepository extends JpaRepository<AdmVO, Integer> {

    AdmVO findByAdmphone(String admphone);
    AdmVO findByAdmemail(String admemail);
    AdmVO findByAdmpassword(String admpassword);
   
}
