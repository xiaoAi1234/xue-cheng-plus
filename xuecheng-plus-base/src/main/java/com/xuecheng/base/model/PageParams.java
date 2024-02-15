package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * ClassName: PageParams
 * Package: com.xuecheng.base.model
 * Description: 分页查询-分页参数
 *
 * @Author 艾子睿
 * @Create 2024/2/15 14:23
 * @Version 1.0
 */
//get set方法
@Data
//为了打印日志方便
@ToString
public class PageParams {
    //此处用Long为了契合mybatis的分页查询
    @ApiModelProperty("页码")
    private Long pageNo = 1L;
    private Long pageSize = 10L;

    public PageParams(){

    }

    public PageParams(long pageNo,long pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

}
