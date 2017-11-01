/**
 * 
 */
package com.myspringboot.sqlmanage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangjoun
 *
 */
@SuppressWarnings(value={"rawtypes"})
public class SqlFactory {
	
	private final static HashMap<String,SqlDTO> sqlCacheMap = new HashMap<String,SqlDTO>();
	
	public static void addSql(String id,String sqlgroup,SqlDTO sqlDTO){
		sqlCacheMap.put(getSqlKey(id,sqlgroup), sqlDTO);
	}
	
	private static String getSqlKey(String id,String sqlgroup){
		return sqlgroup+"-"+id;
	}
	
	public static String getSqlContext(String id,String sqlgroup){
		SqlDTO sqlDTO = sqlCacheMap.get(getSqlKey(id,sqlgroup));
		if(sqlDTO != null){
			return sqlDTO.getSqlContext();
		}else{
			return null;
		}
	}
	
	public static String getSqlContext(String id,String sqlgroup,Map map){
		SqlDTO sqlDTO = sqlCacheMap.get(getSqlKey(id,sqlgroup));
		StringBuffer sql = new StringBuffer();
		if(sqlDTO != null){
			sql.append(sqlDTO.getSqlContext());
			if("UPDATE".equals(sqlDTO.getSqlType().toUpperCase()) || "SELECT".equals(sqlDTO.getSqlType().toUpperCase())){ 
				if(sqlDTO.getSetList() != null && !sqlDTO.getSetList().isEmpty()){
					String setsql = getSetsql(sqlDTO, map);
					if(sql.indexOf(" set ") >= 0 && setsql.length() > 0){
						sql.append(setsql);
					}
					if(sql.indexOf(" set ") < 0 && setsql.length() > 0){
						sql.append(" set ").append(setsql);
					}
				}
				if(sqlDTO.getWhereList() != null && !sqlDTO.getWhereList().isEmpty()){
					String wheresql = getWheresql(sqlDTO,map);
					
					if(sql.indexOf(" where ") >= 0 && wheresql.length() > 0){
						sql.append(wheresql);
					}
					if(sql.indexOf(" where ") < 0 && wheresql.length() > 0){
						sql.append(" where ").append(wheresql);
					}
				}
			}
			return sql.toString();
		}else{
			return null;
		}
	}
	
	private static String getWheresql(SqlDTO sqlDTO,Map map){
		StringBuffer sbfwhere = new StringBuffer();
		if(sqlDTO.getWhereList() != null && !sqlDTO.getWhereList().isEmpty()){
			for(SqlDTO whereDTO : sqlDTO.getWhereList()){
				switch(whereDTO.getSqlType().toUpperCase()){
				case "ISNOTEMPTY":
					if(map.get(whereDTO.getProperty()) != null && !"".equals(map.get(whereDTO.getProperty()))){
						if(sbfwhere.length() > 0 && whereDTO.getSqlContext().indexOf(" and ")<0){
							sbfwhere.append(" and ");
						}
						sbfwhere.append(whereDTO.getSqlContext());
					}
					break;
				case "ISEMPTY":
					if(map.get(whereDTO.getProperty()) == null || "".equals(map.get(whereDTO.getProperty()))){
						if(sbfwhere.length() > 0 && whereDTO.getSqlContext().indexOf(" and ")<0){
							sbfwhere.append(" and ");
						}
						sbfwhere.append(whereDTO.getSqlContext());
					}
					break;
				}
			}
		}
		return sbfwhere.toString();
	}
	
	private static String getSetsql(SqlDTO sqlDTO,Map map){
		StringBuffer sbfset = new StringBuffer();
		if(sqlDTO.getSetList() != null && !sqlDTO.getSetList().isEmpty()){
			for(SqlDTO setDTO : sqlDTO.getSetList()){
				switch(setDTO.getSqlType().toUpperCase()){
				case "ISNOTEMPTY":
					if(map.get(setDTO.getProperty()) != null && !"".equals(map.get(setDTO.getProperty()))){
						if(sbfset.length() > 0 && setDTO.getSqlContext().indexOf(",")<0){
							sbfset.append(" , ");
						}
						sbfset.append(setDTO.getSqlContext());
					}
					break;
				case "ISEMPTY":
					if(map.get(setDTO.getProperty()) == null || "".equals(map.get(setDTO.getProperty()))){
						if(sbfset.length() > 0 && setDTO.getSqlContext().indexOf(",")<0){
							sbfset.append(" , ");
						}
						sbfset.append(setDTO.getSqlContext());
					}
					break;
				}
			}
		}
		return sbfset.toString();
	}
	
}
