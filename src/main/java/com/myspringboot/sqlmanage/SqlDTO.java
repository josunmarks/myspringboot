/**
 * 
 */
package com.myspringboot.sqlmanage;

import java.util.List;

/**
 * @author wangjoun
 *
 */
public class SqlDTO {
	private String id;
	private String sqlGroup;
	private String sqlType;
	private String sqlContext;
	private String nextContext;
	private String property; 
	private List<SqlDTO> whereList;
	private List<SqlDTO> setList;
	
	public String getNextContext() {
		return nextContext;
	}
	public void setNextContext(String nextContext) {
		this.nextContext = nextContext;
	}
	public List<SqlDTO> getWhereList() {
		return whereList;
	}
	public void setWhereList(List<SqlDTO> whereList) {
		this.whereList = whereList;
	}
	public List<SqlDTO> getSetList() {
		return setList;
	}
	public void setSetList(List<SqlDTO> setList) {
		this.setList = setList;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSqlGroup() {
		return sqlGroup;
	}
	public void setSqlGroup(String sqlGroup) {
		this.sqlGroup = sqlGroup;
	}
	public String getSqlType() {
		return sqlType;
	}
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	public String getSqlContext() {
		return sqlContext;
	}
	public void setSqlContext(String sqlContext) {
		this.sqlContext = sqlContext;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	
}
