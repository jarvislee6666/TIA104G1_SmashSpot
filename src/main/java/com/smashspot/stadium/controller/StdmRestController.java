package com.smashspot.stadium.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smashspot.stadium.model.StadiumVO;
import com.smashspot.stadium.model.StdmService;

@RestController
@RequestMapping("/stdm/api")
public class StdmRestController {

    @Autowired
    private StdmService stdmSvc;

    @GetMapping("/{stdmId}")
    public StadiumVO getStadiumData(@PathVariable("stdmId") Integer stdmId) {
        return stdmSvc.getOneStdm(stdmId);
    }
}

