package com.bbsserver.common.vo;

import lombok.Data;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

@Data
public class PageVo<T> {

    private int current;

    private int pages;

    private int total;

    private List<T> list;

    //构造函数
    public PageVo() {
    }

    public PageVo(long current, long pages, long total, List<T> list) {
        this.current = (int) current;
        this.pages = (int) pages;
        this.total = (int) total;
        this.list = list;
    }

    public PageVo(IPage<T> page) {
        //当前页数
        this.current = (int) page.getCurrent();
        //页数
        this.pages = (int) page.getPages();
        //总数据数
        this.total = (int) page.getTotal();
        //当前页的数据
        this.list = page.getRecords();
    }
}
