package com.bbsserver.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.entity.bbsUser;
import com.bbsserver.common.mapper.UserMapper;
import com.bbsserver.common.vo.PageVo;
import com.bbsserver.dto.UserListDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public PageVo<bbsUser> listUser(UserListDTO userListDTO) {
        //创建分页对象，包含当前需要的页和每页大小
        Page<bbsUser> queryPage = new Page<>(userListDTO.getPageNum(), userListDTO.getPageSize());
        //创建查询条件
        QueryWrapper<bbsUser> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userListDTO.getAccount())) {
            queryWrapper.like("account", userListDTO.getAccount());
        }
        if (StringUtils.isNotBlank(userListDTO.getName())) {
            queryWrapper.like("name", userListDTO.getName());
        }
        //执行查询，返回分页对象，包含总页数和当前分页的数据
        IPage<bbsUser> page = userMapper.selectPage(queryPage, queryWrapper);
        //将分页对象转换为PageVo对象，返回
        return new PageVo<bbsUser>(page);
    }

    public bbsUser getUser(int id) {
        bbsUser user = userMapper.selectById(id);
        user.setPassword(null);
        return user;
    }

    public void deleteUser(int id) {
        int count = userMapper.deleteById(id);
        if(count != 1){
            throw new RuntimeException("删除失败");
        }
    }

}
