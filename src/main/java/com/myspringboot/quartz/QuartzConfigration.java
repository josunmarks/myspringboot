/**
 * 
 */
package com.myspringboot.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author wangjoun
 *
 */
@Configuration
public class QuartzConfigration {
	
	private static final Logger logger = LoggerFactory.getLogger(QuartzConfigration.class);
	
	/** 
     * attention: 
     * Details：定义quartz调度工厂 
     */  
    @Bean(name = "scheduler")  
    public SchedulerFactoryBean schedulerFactory() {  
        SchedulerFactoryBean bean = new SchedulerFactoryBean();  
        return bean;  
    } 
}
