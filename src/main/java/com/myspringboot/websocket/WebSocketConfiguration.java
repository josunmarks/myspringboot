/**
 * 
 */
package com.myspringboot.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @author wangjoun
 *
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(websocketHandler(), "/ws").addInterceptors(new HttpSessionHandshakeInterceptor());
		registry.addHandler(websocketHandler(), "/ws/sockjs").addInterceptors(new HttpSessionHandshakeInterceptor()).withSockJS();
	}
	@Bean
	//自定义过滤器
	public WebsocketHandler websocketHandler(){
		return new WebsocketHandler();
	}

}
