package com.blllf.blogease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blllf.blogease.mapper.ArticleMapper;
import com.blllf.blogease.mapper.FollowMapper;
import com.blllf.blogease.mapper.LikeArticlesMapper;
import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.*;
import com.blllf.blogease.pojo.dto.CategoryArticleCount;
import com.blllf.blogease.service.UserService;
import com.blllf.blogease.util.Md5Util;
import com.blllf.blogease.util.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper , User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private LikeArticlesMapper likeArticlesMapper;
    @Autowired
    private FollowMapper followMapper;


    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public User findByUsername(String username) {
        User u = userMapper.findByUsername(username);
        return u;
    }

    //个人中心-个人信息
    @Override
    public User findPeopleInfoById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public void register(String username, String password) {
        /*//对密码加密
        String md5String = Md5Util.getMD5String(password);

        userMapper.add(username,md5String);*/

        /*暂时不进行加密 开发阶段*/
        userMapper.add(username,password);
    }

    @Override
    public Boolean update(User user) {

        if (user.getEmail() != null){
            User user1 = userMapper.selectByEmail3(user.getEmail() , user.getUsername());
            if (user1 != null){
                return false;
            }else {
                user.setUpdateTime(LocalDateTime.now());
                userMapper.update(user);
                return true;
            }
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        return true;

    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String , Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl , id);
    }

    @Override
    public void updatePassword(String password) {
        Map<String , Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        //对密码进行加密
        //userMapper.updatePassword(Md5Util.getMD5String(password) , id);
        userMapper.updatePassword(password,id);
    }

    @Override
    public User selectByEmail(String email) {
        User user = userMapper.selectByEmail(email);
        return user;
    }

    @Override
    public void findPassword(String password , String email) {

        userMapper.findPasswordByEmail(Md5Util.getMD5String(password),email);
    }

    //管理员
    // 删除用户需要删除与用户有关的数据
    @Override
    public boolean deleteUser(Integer id) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user", id);

        QueryWrapper<ArticleILike> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("create_user" , id);

        int delete = articleMapper.delete(wrapper);
        int delete1 = likeArticlesMapper.delete(wrapper1);
        int delete2 = userMapper.deleteById(id);

        return delete > 0 || delete1 > 0 || delete2 > 0;
    }

    @Override
    public Boolean updateUserByAdmin(@RequestBody User user) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ne("username" , user.getUsername())
                .eq("email" , user.getEmail());
        List<User> user1 = userMapper.selectList(wrapper);
        //说明该用户的邮箱 ， 密码 ， 昵称 已存在
        if (!user1.isEmpty()){
            return false;
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateUserAdmin(user);
        return true;
    }

    @Override
    public PageBean<User> selectAllByAdmin(Integer pageNum, Integer pageSize, String username, String email , String nickName) {
        PageBean<User> pb = new PageBean<>();
        PageHelper.startPage(pageNum,pageSize);
        if (username != null && !username.isEmpty()){
            username = "%" + username + "%";
        }
        if (email != null && !email.isEmpty()){
            email = "%" + email + "%";
        }
        if (nickName != null && !nickName.isEmpty()){
            nickName = "%" + nickName + "%";
        }

        Page<User> users = userMapper.findAllUsersByAdmin(username , email , nickName);
        pb.setTotal(users.getTotal());
        pb.setItems(users.getResult());
        return pb;
    }

    @Override
    public List<CategoryArticleCount> findRank() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("startTime" , "2024-04-02");
        hashMap.put("endTime" , "2024-07-31");
        List<CategoryArticleCount> rank = userMapper.findRank(hashMap);
        return rank;
    }

    @Override
    @Transactional
    public String followUser(Integer followerId, Integer followingId) {
        if (followerId.equals(followingId)){
            return "禁止给自己点关注";
        }
        if (followMapper.existsFollow(followerId , followingId)){
            return "不能重复关注";
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        //写入数据库
        int i = followMapper.insertFollow(follow);
        if (i > 0){
            return "yes";
        }
        return "error";
    }

    @Override
    public String unfollowUser(Integer followerId, Integer followingId) {
        //1. 清楚数据库
        int affected = followMapper.deleteFollow(followerId, followingId);
        if (affected > 0) {
            return "yes";
        }
        return "关注关系不存在";
    }



}
