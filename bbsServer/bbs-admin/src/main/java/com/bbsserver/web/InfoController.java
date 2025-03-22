package com.bbsserver.web;

import com.bbsserver.common.vo.DataResult;
import com.bbsserver.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {
    @Autowired
    private InfoService infoService;

    @GetMapping("/all")
    public DataResult list() {
        return DataResult.success(infoService.getInfos());
    }
}
