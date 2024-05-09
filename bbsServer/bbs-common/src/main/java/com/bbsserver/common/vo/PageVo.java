package com.bbsserver.common.vo;

import lombok.Data;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {

    private int page;

    private int pageSize;

    private int total;

    private List<T> list;

    //构造函数
    public PageVo() {
    }

    public PageVo(long page, long pageSize, long total, List<T> list) {
        this.page = (int) page;
        this.pageSize = (int) pageSize;
        this.total = (int) total;
        this.list = list;
    }

    public PageVo(IPage<T> page) {
        //当前页数
        this.page = (int) page.getCurrent();
        //每页大小
        this.pageSize = (int) page.getSize();
        //总数据数
        this.total = (int) page.getTotal();
        //当前页的数据
        this.list = page.getRecords();
    }
}
