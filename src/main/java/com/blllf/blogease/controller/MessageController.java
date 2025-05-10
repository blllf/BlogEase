package com.blllf.blogease.controller;

import com.blllf.blogease.mapper.ArticleMapper;
import com.blllf.blogease.mapper.MessageMapper;
import com.blllf.blogease.pojo.Article;
import com.blllf.blogease.pojo.Message;
import com.blllf.blogease.pojo.PageBean;
import com.blllf.blogease.pojo.Result;
import com.blllf.blogease.service.MessageService;
import com.blllf.blogease.service.RedisService;
import com.blllf.blogease.websocket.MyWebSocketHandler;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private MyWebSocketHandler myWebSocketHandler;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private MessageService messageService;

    //用户接受消息
    @GetMapping("/receiveMsg")
    public Result receiveMsg(String userId){
        List<Message> messages = redisService.receiveMsg(userId);
        StringBuilder sendMsgBuilder = new StringBuilder();
        for (Message message : messages) {
            if (!sendMsgBuilder.isEmpty()) { // 确保不是第一次添加内容时不会添加多余的换行符
                sendMsgBuilder.append("\n");
            }
            Article article = articleMapper.selectById(message.getArticleId());
            sendMsgBuilder.append(article.getTitle() + "::" + message.getContent()); // 追加每个消息的内容
        }
        String sendMsg = sendMsgBuilder.toString(); // 最后转换为String
        myWebSocketHandler.sendMessageToUser(userId , sendMsg);
        return Result.success();
    }

    //获取所有消息列表
    @GetMapping("/showMsgList")
    public Result<PageBean<Message>> showMsgList(Integer pageNum , Integer pageSize ,
                                                 @RequestParam(required = false) Integer isRead,
                                                 @RequestParam(required = false) String title){
        PageBean<Message> msgList = messageService.findMsgList(pageNum, pageSize, isRead, title);
        return Result.success(msgList);
    }

    //批量删除
    @PostMapping("/deleteMsgByIds")
    public Result deleteMsgByIds(@NotNull @RequestBody List<Integer> ids){
        messageService.deleteMsgByIds(ids);
        return Result.success();
    }
}
