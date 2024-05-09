package com.bbsserver.web;

import com.bbsserver.common.vo.DataResult;
import org.springframework.web.bind.annotation.*;


@RestController
public class TestController {
    @PostMapping("/test")
    public DataResult test(@RequestBody Te t,Integer id) {
        return DataResult.success(t);
    }
}
