package com.blllf.blogease.mapper;

import com.blllf.blogease.pojo.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {
    // 插入关注关系
    int insertFollow(Follow follow);

    // 检查是否存在关注关系
    boolean existsFollow(@Param("followerId") Integer followerId,
                         @Param("followingId") Integer followingId);

    // 删除关注关系
    int deleteFollow(@Param("followerId") Integer followerId,
                     @Param("followingId") Integer followingId);

    // 查询关注列表（分页）
    List<Long> selectFollowingIds(@Param("userId") Integer userId);

    // 查询粉丝列表（分页）
    List<Long> selectFollowerIds(@Param("userId") Integer userId);

    // 查询关注总数
    int countFollowing(@Param("userId") Integer userId);

    // 查询粉丝总数
    int countFollowers(@Param("userId") Integer userId);
}
