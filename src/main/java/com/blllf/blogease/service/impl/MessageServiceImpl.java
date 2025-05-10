package com.blllf.blogease.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blllf.blogease.mapper.MessageMapper;
import com.blllf.blogease.pojo.Message;
import com.blllf.blogease.pojo.PageBean;
import com.blllf.blogease.service.MessageService;
import com.blllf.blogease.util.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper , Message> implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public PageBean<Message> findMsgList(Integer pageNum, Integer pageSize, Integer isRead, String title) {
        PageBean<Message> pb = new PageBean<>();
        PageHelper.startPage(pageNum , pageSize);
        Map<String , Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Integer isBroadcast = 0;
        Page<Message> msgList = messageMapper.findMsgList(isBroadcast, username, isRead, title);
        pb.setTotal(msgList.getTotal());
        pb.setItems(msgList.getResult());
        return pb;
    }

    @Override
    public void deleteMsgByIds(List<Integer> ids) {
        if (ids.get(0).equals(1)){
            List<Integer> newIds = ids.stream().skip(1).toList();
            messageMapper.updateMsgByIds(newIds);
        }else {
            messageMapper.deleteMsgByIds(ids);
        }
    }
}
