package com.bbsserver.web;


import com.bbsserver.common.vo.DataResult;
import com.bbsserver.dto.UserListDTO;
import com.bbsserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/list")
    public DataResult list(@RequestBody UserListDTO userListDTO) {
        return DataResult.success(userService.listUser(userListDTO));
    }

    @GetMapping("/detail")
    public DataResult detail(@RequestParam int id) {
        return DataResult.success(userService.getUser(id));
    }

    @GetMapping("/delete")
    public DataResult delete(@RequestParam int id) {
        userService.deleteUser(id);
        return DataResult.success();
    }

}
