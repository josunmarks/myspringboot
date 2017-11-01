/**
 * 
 */
package com.myspringboot.mina;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangjoun
 *
 */
@Configuration
public class MinaConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(MinaConfiguration.class);
	@Value("${mina.port}")
	private int port;
	//日志过滤器
	@Bean
	public LoggingFilter loggingFilter() {
		return new LoggingFilter();
	}
	//自定义处理器
	@Bean
	public IoHandler ioHandler() {
		return new MyMinaServerHandler();
	}
	//开放端口
	@Bean
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(port);
    }
	//编码过滤器
	@Bean
	public ProtocolCodecFilter codec(){
		return new ProtocolCodecFilter(new HCoderFactory());
	}
	//添加过滤器
	@Bean
	public DefaultIoFilterChainBuilder filterChainBuilder(){
		DefaultIoFilterChainBuilder filterChainBuilder = new DefaultIoFilterChainBuilder();
		LinkedHashMap filters = new LinkedHashMap();
		filters.put("codec", codec());
		filters.put("logger", loggingFilter());
		filterChainBuilder.setFilters(filters);
		return filterChainBuilder;
	}
	@Bean(initMethod = "bind", destroyMethod = "unbind")
	public NioSocketAcceptor ioAcceptor(){
		NioSocketAcceptor ioAcceptor = new NioSocketAcceptor();
		ioAcceptor.setDefaultLocalAddress(inetSocketAddress());
		ioAcceptor.setHandler(ioHandler());
		ioAcceptor.setFilterChainBuilder(filterChainBuilder());
		ioAcceptor.setReuseAddress(true);
		return ioAcceptor;
	}
	/*
    public NioSocketAcceptor nioSocketAcceptor(ReceiveMinaHandle receiveMinaHandle,DefaultIoFilterChainBuilder defaultIoFilterChainBuilder) {

        NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
        nioSocketAcceptor.setDefaultLocalAddress(new InetSocketAddress(port));
        nioSocketAcceptor.setReuseAddress(true);
        nioSocketAcceptor.setFilterChainBuilder(defaultIoFilterChainBuilder);
        nioSocketAcceptor.setHandler(receiveMinaHandle);
        return nioSocketAcceptor;
    }*/
	
}
