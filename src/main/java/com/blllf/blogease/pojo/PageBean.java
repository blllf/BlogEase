package com.blllf.blogease.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装接收分页后的数据
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageBean<T> {
    private long total;
    private List<T> items;
}
