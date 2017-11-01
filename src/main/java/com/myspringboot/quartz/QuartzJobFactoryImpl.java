/**
 * 
 */
package com.myspringboot.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangjoun
 * 任务的实现类
 */
public class QuartzJobFactoryImpl implements Job {
	private Logger logger = LoggerFactory.getLogger(QuartzJobFactoryImpl.class);
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		QuartzJob scheduleJob = (QuartzJob)context.getMergedJobDataMap().get("guartzJob");
		System.out.println("---------------------------");
		logger.debug("任务开始:"+scheduleJob.getJobName()+","+System.currentTimeMillis());
	}
}
