/**
 * 
 */
package com.myspringboot.quartz;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


/**
 * @author wangjoun
 *
 */
@Service
public class QuartzInit implements InitializingBean{//ApplicationListener<ContextRefreshedEvent> 
	@Autowired
	@Qualifier("scheduler")
	Scheduler scheduler;
	
	@Autowired
	@Qualifier("quartzDAO")
	private QuartzDAO quartzDAO;

	@Override
	@Lazy(false)
	@SuppressWarnings(value={"unchecked","rawtypes"})
	public void afterPropertiesSet() throws Exception {
		List<QuartzJob> quartzList = quartzDAO.queryList();
		if(!quartzList.isEmpty()){
			for(QuartzJob job : quartzList){
				TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(),job.getJobGroup());
				//获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
	            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
	            //不存在，创建一个
	            if (null == trigger) {
	            	Class clz = Class.forName(job.getClassName());
	                JobDetail jobDetail = JobBuilder.newJob(clz).withIdentity(job.getJobName(),job.getJobGroup()).build();
	                jobDetail.getJobDataMap().put("guartzJob", job);
	                //表达式调度构建器
	                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job
	                    .getCronExpression());
	                //按新的cronExpression表达式构建一个新的trigger
	                trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();
	                scheduler.scheduleJob(jobDetail, trigger);
	            }else{
	            	 // Trigger已存在，那么更新相应的定时设置
	                //表达式调度构建器
	                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job
	                    .getCronExpression());
	                //按新的cronExpression表达式重新构建trigger
	                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
	                    .withSchedule(scheduleBuilder).build();
	                //按新的trigger重新设置job执行
	                scheduler.rescheduleJob(triggerKey, trigger);
	            }
			}
		}
	}

}
