package com.blllf.blogease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blllf.blogease.pojo.ArticleILike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeArticlesMapper extends BaseMapper<ArticleILike> {
}
