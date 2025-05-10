package com.blllf.blogease.pojo.dto;

import com.blllf.blogease.pojo.Category;
import com.blllf.blogease.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * dto 数据传输对象
 * 封装数据并在不同的层级或组件之间传递的对象层
 */

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryArticleCount extends Category{
    /*private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String categoryAlias;
    private String categoryPic;*/

    private String username;
    private Integer totalCount;
    //粉丝数量
    private Integer followerCount;
    private String userPic;

    private Integer userCount;
    private Integer articleCount;
    private Integer categoryCount;
    private Integer accountDay;

}
