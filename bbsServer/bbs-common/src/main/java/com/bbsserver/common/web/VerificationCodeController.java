package com.bbsserver.common.web;

import com.bbsserver.common.service.VerificationCodeService;
import com.bbsserver.common.vo.DataResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/login-code")
    public DataResult loginCode() {
        return DataResult.success(verificationCodeService.genCode());
    }
}