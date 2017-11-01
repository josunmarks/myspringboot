/**
 * 
 */
package com.myspringboot.websocket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author wangjoun
 *
 */
public class WebsocketHandler extends TextWebSocketHandler {
	//用于保存客户端在线session
	private static final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);
	@Override
	//建立连接后
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//		super.afterConnectionEstablished(session);
		String uid = (String) session.getId();
		SessionMap sessionMap = SessionMap.newInstance();
		if(sessionMap.getSession(uid) == null){
			sessionMap.addSession(uid, session);
		}
		logger.debug(session.getRemoteAddress()+"连接成功!");
	}

	@Override
	//接收文本消息并发送.
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//		super.handleTextMessage(session, message);
		String echomsg = message.getPayload()+",hello!";
		SessionMap sessionMap = SessionMap.newInstance();
		sessionMap.sendMessageToUser(session.getId(), new TextMessage(echomsg));
	}

	@Override
	//消息传送失败时
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//		super.handleTransportError(session, exception);
		if(session.isOpen()){
			session.close();
		}
		removeSession(session);
		logger.debug(session.getRemoteAddress()+"移除连接!");
	}

	@Override
	//连接关闭时触发
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//		super.afterConnectionClosed(session, status);
		logger.debug(session.getRemoteAddress()+"连接关闭!");
		removeSession(session);
	}
	//从缓存中移除session
	private void removeSession(WebSocketSession session){
		SessionMap sessionMap = SessionMap.newInstance();
		sessionMap.removeSession(session.getId());
	}
}
