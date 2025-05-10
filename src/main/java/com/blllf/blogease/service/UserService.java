package com.blllf.blogease.service;

import com.blllf.blogease.pojo.PageBean;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.pojo.dto.CategoryArticleCount;

import java.util.List;

public interface UserService {
    public List<User> selectAll();
    public User findByUsername(String username);
    User findPeopleInfoById(int id);
    public void register(String username , String password);

    public Boolean update(User user);

    public void updateAvatar(String avatarUrl);

    public void updatePassword(String password);

    public User selectByEmail(String email);

    public void findPassword(String password, String email);

    public boolean deleteUser(Integer id);

    //管理员修改用户信息
    public Boolean updateUserByAdmin(User user);

    public PageBean<User> selectAllByAdmin(Integer pageNum , Integer pageSize , String username , String email ,String nickName);

    //用户点赞排名
    public List<CategoryArticleCount> findRank();

    public String followUser(Integer followerId , Integer followingId);

    public String unfollowUser(Integer followerId, Integer followingId);


}
