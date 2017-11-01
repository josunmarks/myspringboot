/**
 * 
 */
package com.myspringboot.quartz;


/**
 * @author wangjoun
 *
 */
public class QuartzJob {
	//任务ID
	private String jobId;
	//任务名称
	private String jobName;
	private String className;
	//任务组号
	private String jobGroup;
	//任务状态 0停用 1启用 2删除
	private String jobStatus;
	//时间表达式
	private String cronExpression;
	//描述
	private String desc;
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
}
