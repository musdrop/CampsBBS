package com.bbsserver.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbsserver.common.dto.LoginDTO;
import com.bbsserver.common.entity.User;
import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.mapper.UserMapper;
import com.bbsserver.common.service.VerificationCodeService;
import com.bbsserver.common.vo.LoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LoginService
 *
 * @author yangjiajia
 * @createdAt 2024/4/2 11:49
 */
@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerificationCodeService verificationCodeService;

    //处理登录表单，判断是否能够登录
    public LoginVo login(LoginDTO loginDTO) {

        //先校验验证码
        if (!verificationCodeService.checkCode(loginDTO.getCodeId(), loginDTO.getCode())) {
            throw new CommonException("验证码不正确");
        }
        //创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //查找前端传过来的账号
        queryWrapper.lambda().eq(User::getAccount, loginDTO.getAccount());
        User user = userMapper.selectOne(queryWrapper);

        if (null == user) {
            throw new CommonException("账号不存在");
        }
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new CommonException("密码不正确");
        }
        if (user.getAuth() != 1) {
            throw new CommonException("没有权限");
        }
        //登录成功，将查询到的user拷贝到loginVo中，返回
        LoginVo loginVo = new LoginVo();
        BeanUtils.copyProperties(user, loginVo);
        return loginVo;
    }
}