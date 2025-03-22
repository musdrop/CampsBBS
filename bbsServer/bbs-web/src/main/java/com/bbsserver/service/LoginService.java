package com.bbsserver.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbsserver.common.dto.LoginDTO;
import com.bbsserver.common.entity.bbsUser;
import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.mapper.UserMapper;
import com.bbsserver.common.service.VerificationCodeService;
import com.bbsserver.common.vo.LoginVo;
import com.bbsserver.dto.RegisterDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;


@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerificationCodeService verificationCodeService;

    //处理登录表单，判断是否能够登录
    public LoginVo login(LoginDTO loginDTO) {

        if (!verificationCodeService.checkCode(loginDTO.getCodeId(), loginDTO.getCode())) {
            throw new CommonException("验证码不正确");
        }
        //创建查询条件
        QueryWrapper<bbsUser> queryWrapper = new QueryWrapper<>();
        //查找前端传过来的账号
        queryWrapper.lambda().eq(bbsUser::getAccount, loginDTO.getAccount());
        bbsUser user = userMapper.selectOne(queryWrapper);

        if (null == user) {
            throw new CommonException("账号不存在");
        }
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new CommonException("密码不正确");
        }
        //登录成功，将查询到的user拷贝到loginVo中，返回
        LoginVo loginVo = new LoginVo();
        BeanUtils.copyProperties(user, loginVo);
        return loginVo;
    }

    //处理注册表单
    public void register(RegisterDTO registerDTO) {
        //创建查询提交
        QueryWrapper<bbsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(bbsUser::getAccount, registerDTO.getAccount());
        //查询要注册的账号是否已存在
        bbsUser user = userMapper.selectOne(queryWrapper);
        if (null != user) {
            throw new CommonException("账号已存在");
        }
        //创建用户账号
        user = new bbsUser();
        BeanUtils.copyProperties(registerDTO, user);
        user.setHead(registerDTO.getHeadPath());
        user.setAuth(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insert(user);
    }

}