package com.blllf.blogease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blllf.blogease.pojo.Message;
import com.blllf.blogease.pojo.PageBean;

import java.util.List;

public interface MessageService extends IService<Message> {
    PageBean<Message> findMsgList(Integer pageNum, Integer pageSize, Integer isRead, String title);

    //批量删除
    void deleteMsgByIds(List<Integer> ids);
}
