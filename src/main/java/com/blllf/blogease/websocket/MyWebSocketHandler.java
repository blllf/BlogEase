package com.blllf.blogease.websocket;

import com.blllf.blogease.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getUri().getQuery().replace("userId=", "");
        WsSessionManager.add(userId , session);
    }

    //处理客户端发送的文本消息。
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理接收到的消息
        // 例如，可以将消息广播给所有连接的客户端
        System.out.println("Received message: " + message.getPayload());
        session.sendMessage(new TextMessage(message.getPayload() + " 发送时间 " + LocalDateTime.now()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = session.getUri().getQuery().replace("userId=", "");
        System.out.println(WsSessionManager.SESSION_POOL.get(userId));
        WsSessionManager.removeAndClose(userId);
    }

    public void sendMessageToUser(String userId, String message) {
        WebSocketSession session = WsSessionManager.get(userId);
        if (session == null) {
            System.err.println("Error: No session found for user ID: " + userId);
        } else if (!session.isOpen()) {
            System.err.println("Error: Session is not open for user ID: " + userId);
        } else {
            try {
                //通过 WebSocket 会话将消息发送给特定的用户
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
