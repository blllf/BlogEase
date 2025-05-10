package com.blllf.blogease.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blllf.blogease.anno.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@TableName("article")
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @NotNull(groups = {update.class})
    private Integer id;//主键ID

    @NotEmpty
    @Pattern(regexp = "^.{1,40}$")
    private String title;//文章标题
    @NotEmpty
    private String content;//文章内容

    //@NotEmpty
    @URL
    private String coverImg;//封面图像

    @State
    private String state;//发布状态 已发布|草稿
    @NotNull
    private Integer categoryId;//文章分类id
    private Integer createUser;//创建人ID
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

    //注解告诉 MyBatis-Plus 忽略该属性在数据库中的映射
    @TableField(exist = false)
    private LocalDateTime collectionTime;

    //@TableLogic
    private Integer deleted;

    public interface update extends Default {}
}
