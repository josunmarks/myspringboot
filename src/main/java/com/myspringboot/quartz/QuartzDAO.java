/**
 * 
 */
package com.myspringboot.quartz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author wangjoun
 *
 */
@Repository("quartzDAO")
public class QuartzDAO {
	@Autowired
	@Qualifier("primaryJdbcTemplate")
	private JdbcTemplate jdbcTempleate;
	
	public List<QuartzJob> queryList(){
		List<QuartzJob> list = jdbcTempleate.query("select * from sys_t_quartzs where jobstatus = 0", new RowMapper<QuartzJob>(){
			@Override
			public QuartzJob mapRow(ResultSet rs, int rowNum) throws SQLException {
				QuartzJob job = new QuartzJob();
				job.setClassName(rs.getString("classname"));
				job.setCronExpression(rs.getString("cronexpression"));
				job.setDesc(rs.getString("desc"));
				job.setJobGroup(rs.getString("jobgroup"));
				job.setJobId(rs.getString("jobid"));
				job.setJobName(rs.getString("jobname"));
				job.setJobStatus(rs.getString("jobstatus"));
				return job;
			}
		});
		return list;
	}
	
}
