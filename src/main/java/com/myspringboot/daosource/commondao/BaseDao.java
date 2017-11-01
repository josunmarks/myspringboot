/**
 * 
 */
package com.myspringboot.daosource.commondao;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wangjoun
 *
 */
@Repository("baseDAO")
@SuppressWarnings(value={"rawtypes","unchecked"})
public class BaseDao {

	@Autowired
	@Qualifier("primaryJdbcTemplate")
	private JdbcTemplate jdbcTempleate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTempleate;
	}

	public Pagination query(String sql, Object[] args, RowMapper rowMapper, int numPerPage, int currentPage) {
		Pagination page = new Pagination();
		if (numPerPage == 0) {
			numPerPage = Pagination.NUMBERS_PER_PAGE;
		}
		if (sql == null || sql.equals("")) {
			throw new IllegalArgumentException("Pagination.sql is empty,please initial it first. ");
		}
		// 设置每页显示记录数
		page.setNumPerPage(numPerPage);
		// 设置要显示的页数
		page.setCurrentPage(currentPage);
		// 计算总记录数
		StringBuffer totalSQL = new StringBuffer(" SELECT count(1) FROM ( ");
		totalSQL.append(sql);
		totalSQL.append(" ) totalTable ");
		// 总记录数
		page.setTotalRows(

				getJdbcTemplate().queryForObject(totalSQL.toString(), args, Integer.class));
		// 计算总页数
		page.setTotalPages();
		// 计算起始行数
		page.setStartIndex();
		// 计算结束行数
		page.setLastIndex();
		String querysql = getMySQLPageSQL(sql, page.getStartIndex(), numPerPage);
		List relist = new ArrayList();
		if (rowMapper == null) {
			relist = this.getJdbcTemplate().query(querysql, args, new RowMapper() {
				@Override
				public Map mapRow(ResultSet rs, int index) throws SQLException {
					Map remap = new HashMap();
					int cnum = rs.getMetaData().getColumnCount();
					for (int i = 1; i <= cnum; i++) {
						String colname = rs.getMetaData().getColumnName(i);
						remap.put(colname.toLowerCase(), rs.getObject(i));
					}
					return remap;
				}
			});
			// relist = this.getJdbcTemplate().queryForList(querysql, args);
		} else {
			relist = this.getJdbcTemplate().queryForList(querysql, args, rowMapper);
		}
		// 装入结果集
		page.setResultList(relist);
		return page;
	}

	public Pagination query(String sql, List argList, RowMapper rowMapper) {
		return this.query(sql, argList.toArray(), rowMapper, Pagination.NUMBERS_PER_PAGE, 0);
	}

	/**
	 * 查询出map的key为小写.
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public List queryForList(String sql, Object[] args) {
		return this.getJdbcTemplate().query(sql, args, new RowMapper() {
			@Override
			public Map mapRow(ResultSet rs, int index) throws SQLException {
				Map remap = new HashMap();
				int cnum = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= cnum; i++) {
					String colname = rs.getMetaData().getColumnName(i);
					remap.put(colname.toLowerCase(), rs.getObject(i));
				}
				return remap;
			}
		});
	}

	public Map queryForMap(String sql, Object[] args) {
		List relist = this.queryForList(sql, args);
		return (Map) relist.get(0);
	}

	public List queryForList2Object(String sql, Object[] args, Class classname) {
		return this.getJdbcTemplate().query(sql, args, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int index) throws SQLException {
				try {
					Object clazz = classname.newInstance();
					setObjectValue(clazz, rs);
					return clazz;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	
	public Object query2Object(String sql, Object[] args, Class classname) {
		List relist = this.getJdbcTemplate().query(sql, args, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int index) throws SQLException {
				try {
					Object clazz = classname.newInstance();
					setObjectValue(clazz, rs);
					return clazz;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		if(relist == null || relist.isEmpty()){
			return null;
		}
		 return relist.get(0);
	}
	

	public void setObjectValue(Object obj, ResultSet rs)
			throws SQLException, IllegalArgumentException, IllegalAccessException {
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		int cnum = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= cnum; i++) {
			String colname = rs.getMetaData().getColumnLabel(i);
			for (int j = 0; j < fields.length; j++) {
				Field field = fields[j];
				if (colname.toUpperCase().equals(field.getName().toUpperCase())) {
					field.setAccessible(true);
					if (field.getType().getName().equals("int")) {
						field.setInt(obj, rs.getInt(i));
					} else if (field.getType().getName().equals("float")) {
						field.setFloat(obj, rs.getFloat(i));
					} else if (rs.getObject(i) instanceof java.util.Date) {
						java.util.Date date = (java.util.Date) rs.getObject(i);
						field.set(obj, date);
					}else if (rs.getObject(i) instanceof java.sql.Date) {
						java.sql.Date date = (Date) rs.getObject(i);
						field.set(obj, date.toString());
					} else if (rs.getObject(i) instanceof java.sql.Timestamp) {
						java.sql.Timestamp time = (Timestamp) rs.getObject(i);
						field.set(obj, time.toString());
					} else {
						field.set(obj, rs.getObject(i));
					}
					break;
				}
			}
		}
	}

	public void batchSingnalUpdate(String tablecode, List<Map<String, Object>> list, List<String> columns) {
		StringBuffer sql = new StringBuffer("insert into " + tablecode + " (");
		StringBuffer valuessql = new StringBuffer("values(");
		int i = 0;
		for (String col : columns) {
			if (i > 0) {
				sql.append(",");
				valuessql.append(",");
			}
			sql.append(col);
			valuessql.append("?");
			i++;
		}
		sql.append(")").append(valuessql).append(")");
		this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return list.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map map = list.get(i);
				for (int j = 0; j < columns.size(); j++) {
					Object o = map.get(columns.get(j));
					setValues2Type(ps, o, j);
				}
			}
		});
	}

	public void batchSingnalUpdateByID(String tablecode, List<Map<String, Object>> list, List<String> columns) {
		StringBuffer sql = new StringBuffer("update " + tablecode + " set ");
		int i = 0;
		for (String col : columns) {
			if (i > 0 && i < columns.size()) {
				sql.append(", ");
			}
			sql.append(col).append("=?");
			i++;
		}
		sql.append(" where id=?");
		this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return list.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map map = list.get(i);
				for (int j = 0; j < columns.size(); j++) {
					Object o = map.get(columns.get(j));
					setValues2Type(ps, o, j);
				}
				setValues2Type(ps, map.get("id"), columns.size());
			}
		});
	}

	public void batchSingnalUpdateByID(String tablecode, List<Map<String, Object>> list, List<String> columns,
			List<String> pkids) {
		StringBuffer sql = new StringBuffer("update " + tablecode + " set ");
		int i = 0;
		for (String col : columns) {
			if (i > 0 && i < columns.size()) {
				sql.append(", ");
			}
			sql.append(col).append("=?");
			i++;
		}
		sql.append(" where ");
		int a = 0;
		for (String key : pkids) {
			if (a > 0) {
				sql.append(" and ");
			}
			sql.append(key + "=?  ");
			a++;
		}
		this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return list.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map map = list.get(i);
				for (int j = 0; j < columns.size(); j++) {
					Object o = map.get(columns.get(j));
					setValues2Type(ps, o, j);
				}
				for (String key : pkids) {
					setValues2Type(ps, map.get(key), columns.size());
				}
			}
		});
	}

	public void UbatchSingnalUpdate(String tablecode, List<Map<String, Object>> list, List<String> columns) {
		StringBuffer sql = new StringBuffer("insert into " + tablecode + " (");
		StringBuffer valuessql = new StringBuffer("values(");
		int i = 0;
		int j = 0;
		for (String col : columns) {
			if (i > 0) {
				sql.append(",");
				valuessql.append(",");
			}
			sql.append(col);
			valuessql.append("?");
			i++;
		}
		sql.append(")").append(valuessql).append(") ON DUPLICATE KEY UPDATE ");

		for (String col : columns) {
			if (j > 0) {
				sql.append(",");

			}
			sql.append(col + " = values(" + col + ")");

			j++;
		}

		this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return list.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map map = list.get(i);
				for (int j = 0; j < columns.size(); j++) {
					Object o = map.get(columns.get(j));
					setValues2Type(ps, o, j);
				}
			}
		});
	}

	/**
	 * 用于设置sql语句中?传参的值
	 * 
	 * @author wxj
	 * @time 2017-1-11
	 * @param ps
	 *            PreparedStatement
	 * @param o
	 *            需要设置的值
	 * @param index
	 *            需要设置值的位置
	 * @throws SQLException
	 */
	private void setValues2Type(PreparedStatement ps, Object o, int index) throws SQLException {
		if (null == o) {
			ps.setNull(index + 1, 0);
		} else if (o instanceof String) {
			ps.setString(index + 1, (o == null) ? null : String.valueOf(o));
		} else if (o instanceof Date) {
			ps.setDate(index + 1, (Date) o);
		} else if (o instanceof Integer) {
			ps.setInt(index + 1, ((Integer) o).intValue());
		} else if (o instanceof BigDecimal) {
			ps.setBigDecimal(index + 1, (BigDecimal) o);
		} else if (o instanceof Number) {
			ps.setBigDecimal(index + 1, new BigDecimal(o.toString()));
		} else if (o instanceof Clob) {
			ps.setClob(index + 1, (Clob) o);
		} else if (o instanceof Blob) {
			ps.setBlob(index + 1, (Blob) o);
		} else if (o instanceof java.sql.Timestamp) {
			ps.setTimestamp(index + 1, (java.sql.Timestamp) o);
		} else if (o instanceof JSONObject) {
			ps.setString(index + 1, (o == null) ? null : String.valueOf(o));
		} else if (o instanceof java.sql.Time) {
			ps.setTime(index + 1, (java.sql.Time) o);
		} else {
			throw new RuntimeException("参数" + index + "类型未知：" + o.getClass());
		}
	}

	/**
	 * 构造MySQL数据分页SQL
	 * 
	 * @param queryString
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	public String getMySQLPageSQL(String queryString, Integer startIndex, Integer pageSize) {
		String result = "";
		if (null != startIndex && null != pageSize) {
			result = queryString + " limit " + startIndex + "," + pageSize;
		} else if (null != startIndex && null == pageSize) {
			result = queryString + " limit " + startIndex;
		} else {
			result = queryString;
		}
		return result;
	}
}
