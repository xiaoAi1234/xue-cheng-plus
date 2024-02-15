package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: PageResult
 * Package: com.xuecheng.base.model
 * Description: 分页查询结果模型类
 *
 * @Author 艾子睿
 * @Create 2024/2/15 14:39
 * @Version 1.0
 */
@Data
@ToString
public class PageResult<T> implements Serializable {
    private List<T> items;
    private long page;
    private long pageSize;
    private long counts;

    public PageResult(List<T> items, long page, long pageSize, long counts) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.counts = counts;
    }
}
