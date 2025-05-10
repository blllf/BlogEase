package com.blllf.blogease.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blllf.blogease.pojo.dto.ArticleAndUser;
import com.blllf.blogease.pojo.dto.CategoryArticleCount;
import com.blllf.blogease.pojo.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user")
    List<User> selectAll();
    List<User> findUsersByIds(List<Integer> ids);
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    @Insert("insert into user(username, password, create_time, update_time) VALUES " +
            "(#{username} , #{password} , now() , now())")
    void add(String username , String password);


    @Update("update user set nickname = #{nickname} , email = #{email} , update_time = #{updateTime} where id = #{id}")
    void update(User user);

    //更新头像
    @Update("update user set user_pic = #{avatarUrl} , update_time = now() where id = #{id}")
    void updateAvatar(String avatarUrl , Integer id);

    //更新密码
    @Update("update user set password = #{password} , update_time = now() where id = #{id}")
    void updatePassword(String password , Integer id);

    //更新昵称
    @Update("update user set nickname = #{nickname} , update_time = now() where id = #{id} ")
    void updateNickname(String nickname , Integer id);

    //更新邮箱
    @Update("update user set email = #{email} , update_time = now() where id = #{id} ")
    void updateEmail(String email , Integer id);

    //根据用户邮箱查询用户
    //1.
    @Select("select * from user where email = #{email}")
    User selectByEmail(String email);
    //2.
    @Select("select * from user where email = #{email} or username = #{username}")
    List<User> selectByEmail2(String email , String username);
    //3.
    @Select("select * from user where username <> #{username} and email = #{email}")
    User selectByEmail3(String email,String username);

    @Update("update user set password = #{password} , update_time = now() where email = #{email}")
    void findPasswordByEmail(String password , String email);

    //管理员操作修改
    @Update("update user set nickname = #{nickname} , email = #{email} , update_time = #{updateTime} , password = #{password} where id = #{id}")
    void updateUserAdmin(User user);

    @Insert("insert into user(username , nickname, email , password, create_time, update_time) VALUES " +
            "(#{username} ,#{nickname} , #{email} , #{password} , now() , now())")
    void addUserAdmin(User user);


    Page<User> findAllUsersByAdmin(String username, String email, String nickname);


    //树状图
    @Select("select user.username, count(*) as total_count from user , article " +
            "where user.id = article.create_user and article.state = '已发布' and  article.deleted = 0  GROUP BY user.id")
    List<CategoryArticleCount> findUsernameAndArticles();


    //点赞rank
    List<CategoryArticleCount> findRank(HashMap<String,String> map);


    @Select("SELECT id FROM user WHERE username LIKE #{username}")
    List<Integer> selectUserIdsByUsername(String username);
    //同步user表粉丝数
    @Update("UPDATE user SET follower_count = #{count} WHERE id = #{userId}")
    int updateUserFollowerCount(@Param("userId") int userId , @Param("count") int count);
    //同步user表关注数
    @Update("UPDATE user SET attention_count = #{count} WHERE id = #{userId}")
    int updateUserAttentionCount(@Param("userId") int userId ,@Param("count") int count);


}
