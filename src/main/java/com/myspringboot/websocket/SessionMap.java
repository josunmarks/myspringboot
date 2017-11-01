/**
 * 
 */
package com.myspringboot.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author wangjoun
 *
 */
public class SessionMap {
	private static final Logger logger = LoggerFactory.getLogger(SessionMap.class);
	private static SessionMap sessionMap = null;  
	private Map<Object, WebSocketSession> userSocketSessionMap  = new HashMap<Object, WebSocketSession>();

	 /**  
    * @Description: 获取唯一实例  
    * @author whl  
    * @date 2014-9-29 下午1:29:33  
    */  
   public static SessionMap newInstance(){  
   	logger.debug("SessionMap单例获取---");  
       if(sessionMap == null){  
           sessionMap = new SessionMap();  
       }  
       return sessionMap;  
   } 
   
   public void addSession(String key,WebSocketSession session){
	   userSocketSessionMap.put(key, session);
   }
   
   public WebSocketSession getSession(String key){
	  return userSocketSessionMap.get(key);
   }
   
   public void removeSession(String key){
	   for (Iterator iterator = userSocketSessionMap.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			if(entry.getKey().toString().equals(key)){
				iterator.remove();
			}
		}
   }
   //给特跌用户发送消息
   public void sendMessageToUser(String key,TextMessage message) throws IOException{
	   WebSocketSession session = getSession(key);
	   session.sendMessage(message);
   }
   //给几个用户同时发送消息
   public void sendMessageToUser(String[] keys,TextMessage message) throws IOException{
	   for(String key : keys){
		   WebSocketSession session = getSession(key);
		   if(session.isOpen()){
				new Thread(new Runnable() {
                   public void run() {
                       try {
                       	session.sendMessage(message);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               }).start();
			}
	   }
   }
   //给所有用户同时发送消息
   public void sendMessageToAllUser(TextMessage message) throws IOException{
	   for (Iterator iterator = userSocketSessionMap.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			WebSocketSession session = (WebSocketSession) entry.getValue();
			if(session.isOpen()){
				new Thread(new Runnable() {
                    public void run() {
                        try {
                        	session.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
			}
		}
   }
   
}
