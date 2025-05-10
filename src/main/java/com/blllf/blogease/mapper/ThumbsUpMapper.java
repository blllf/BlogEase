package com.blllf.blogease.mapper;

import com.blllf.blogease.pojo.LikedCount;
import com.blllf.blogease.pojo.UserLike;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ThumbsUpMapper {

    // ------------------------------------  userlike 数据库表 ---------------------------
    @Insert("insert into userlike(liked_user_id, liked_post_id, status, create_time, update_time) values" +
            " (#{likedUserId} , #{likedPostId} , #{status} , now() ,now())")
    public void save(UserLike userLike);

    //批量保存或修改
    //void saveAll(List<UserLike> list);

    //修改
    @Update("update userlike set status = #{status} " +
            "where liked_user_id = #{likedUserId} and liked_post_id = #{likedPostId}")
    void updateLike(UserLike userLike);

    //根据 likedUserId  likedPostId 在本地数据库查询
    @Select("select * from userlike where liked_user_id = #{likedUserId} and liked_post_id = #{likedPostId}")
    UserLike getByLikedUserIdAndLikedPostId(String likedUserId , String likedPostId);


    //查询所欲数据
    @Select("select * from userlike")
    List<UserLike> selectAll();

    //删除用户 状态 = 0
    @Delete("delete from userlike where liked_user_id = #{likedUserId};")
    public void deleteLike(String likedUserId);

    // ------------------------------------  userlikecount 数据库表 ---------------------------

    //根据用户ID查询db中的用户信息
    @Select("select * from userlikecount where likedUserId = #{likedUserId}")
    public LikedCount findById(String likedUserId);

    @Select("select * from userlikecount")
    public List<LikedCount> findAll();

    //修改用户信息 --> 点赞数量
    @Update("update userlikecount set number = #{likeNum} where likedUserId = #{likedUserId}")
    public void updateLikeCount(Integer likeNum , String likedUserId);


    //保存到DB中
    @Insert("insert into userlikecount(likedUserId, number, update_time) VALUES " +
            "(#{likedUserId} , #{number} , now())")
    public void addLikeCountToDB(LikedCount likedCount);

    //删除用户 数量 = 0
    @Delete("delete from userlikecount where likedUserId = #{likedUserId}")
    public void deleteLikeCount(String likedUserId);




}
