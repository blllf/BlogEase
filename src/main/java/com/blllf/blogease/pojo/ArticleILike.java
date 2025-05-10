package com.blllf.blogease.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("articlecollection")
public class ArticleILike {

    private Integer id;
    private Integer userId;
    private LocalDateTime collectionTime;
    private Integer articleId;

    @TableField(exist = false)
    private List<Article> articleList;      //用户关联的文章
}
