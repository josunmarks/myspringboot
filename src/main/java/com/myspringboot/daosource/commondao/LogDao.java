/**
 * 
 */
package com.myspringboot.daosource.commondao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author wangjoun
 *
 */
@Repository("logDAO")
public class LogDao extends BaseDao {
	@Autowired
	@Qualifier("secondaryJdbcTemplate")
	private JdbcTemplate jdbcTempleate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTempleate;
	}
}
