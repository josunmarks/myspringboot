/**
 * 
 */
package com.myspringboot.mina;

import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * @author wangjoun
 *
 */
public class MyMinaServerHandler extends IoHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MyMinaServerHandler.class);

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.debug("连接创建："+session.getLocalAddress());  
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.debug("连接打开："+session.getLocalAddress());  
		super.sessionOpened(session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.debug("客户端和服务器端失去链接"+session.getRemoteAddress());
		super.sessionClosed(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error(cause.getMessage());
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		logger.debug("服务端收到信息-------------");  
		String str = message.toString();
		if (str.trim().equalsIgnoreCase("quit")) {  
            session.closeNow();
            return;  
        }  
        //获取客户端发过来的key  
        Map mes = (Map) JSON.parse(message.toString());
        String key = mes.get("key").toString();
        System.out.println("key :"+key);
        //保存客户端的会话session  
        SessionMap sessionMap = SessionMap.newInstance();  
        sessionMap.addSession(key, session);  
        session.write("i am recived");
		//super.messageReceived(session, message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
//		super.messageSent(session, message);
		logger.debug("------------服务端发消息到客户端---");
	}

}
