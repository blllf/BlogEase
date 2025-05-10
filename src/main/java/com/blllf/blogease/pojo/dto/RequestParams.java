package com.blllf.blogease.pojo.dto;

import lombok.Data;

//es搜索属性
@Data
public class RequestParams {
    private String key;
    private Integer categoryId;
    private String updateTime;
    private Integer page;
    private Integer size;
    private String sortBy;
}
