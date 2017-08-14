package ringutils.jdbc.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SQLHelper{
	
	/**
	 * 获取分页SQL语句
	 * @param sql
	 * @param page
	 * @param pageSize
	 * @return 
	 * @author ring
	 * @date 2017年4月1日 下午3:17:56
	 * @version V1.0
	 */
	public static String getPageSql(String sql,int page,int pageSize){
		int beginIndex = (page - 1)*pageSize;
		return sql+" limit "+beginIndex+","+pageSize;
	}
	
	/**
	 * 获得删除SQL语句
	 * @param tablename
	 * @param fields
	 * @return 
	 * @author ring
	 * @date 2017年3月30日 下午3:27:32
	 * @version V1.0
	 */
	public static String getDeleteSql(String tablename,String[] whereFields){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM ").append(tablename).append(" WHERE ");
		for(int i=0;i<whereFields.length;i++){
			if(i>0){
				sql.append("and");
			}
			sql.append(whereFields[i]).append("=").append("?");
		}
		return sql.toString();
	}
	
	/**
	 * 获得修改SQL语句
	 * @param tablename
	 * @param fields
	 * @return 
	 * @author ring
	 * @date 2017年3月30日 下午3:27:32
	 * @version V1.0
	 */
	public static String getUpdateSql(String tablename,String[] fields,String... whereFields){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ").append(tablename).append(" SET ");
		for(int i=0;i<fields.length;i++){
			if(i>0){
				sql.append(",");
			}
			sql.append(fields[i]).append("=").append("?");
		}
		if(whereFields!=null&&whereFields.length>0){
			sql.append(" WHERE ");
			for(int i=0;i<whereFields.length;i++){
				if(i>0){
					sql.append("and");
				}
				sql.append(whereFields[i]).append("=").append("?");
			}
		}
		return sql.toString();
	}
	
	/**
	 * 获得插入SQL语句
	 * @param tablename
	 * @param fields
	 * @return 
	 * @author ring
	 * @date 2017年3月30日 下午3:27:32
	 * @version V1.0
	 */
	public static String getInsertSql(String tablename,String[] fields){
		StringBuffer sql = new StringBuffer();
		StringBuffer params = new StringBuffer();
		sql.append("INSERT INTO ").append(tablename).append("(");
		for(int i=0;i<fields.length;i++){
			if(i>0){
				sql.append(",");
				params.append(",");
			}
			sql.append(fields[i]);
			params.append("?");
		}
		sql.append(")VALUES(");
		sql.append(params.toString()).append(")");
		return sql.toString();
	}
	
	/**
	 * 获得索引SQL语句
	 * @param tablename
	 * @param field
	 * @return 
	 * @author ring
	 * @date 2017年3月30日 下午3:36:24
	 * @version V1.0
	 */
	public static String getAlterSql(String tablename,String... field){
		StringBuffer sql = new StringBuffer();
		sql.append("ALTER TABLE "+tablename+" add index index_").append(UUID.randomUUID().toString().substring(0, 8)).append("(");
		for(int i=0;i<field.length;i++){
			if(i>0){
				sql.append(",");
			}
			sql.append(field[i]);
		}
		sql.append(")");
		return sql.toString();
	}
	
	/**
	 * 获得删除表SQL语句
	 * @param tablename
	 * @return 
	 * @author ring
	 * @date 2017年3月30日 下午3:30:53
	 * @version V1.0
	 */
	public static String getDropTableSql(String tablename){
		return "DROP TABLE IF EXISTS "+tablename;
	}
	
	/**
	 * 获得创建表SQL语句
	 * @param tablename
	 * @param meta	[{field:'字段',type:'类型',length:'长度',pbc:'约束条件，如PRIMARY KEY NOT NULL AUTO_INCREMENT',comment:'备注'}]
	 * @return 
	 * @author ring
	 * @date 2017年3月30日 下午1:56:02
	 * @version V1.0
	 */
	public static String getCreateTableSql(String tablename,JSONArray meta){
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE ").append(tablename).append("(");
		for(int i=0;i<meta.size();i++){
			JSONObject m = meta.getJSONObject(i);
			if(i>0){
				sql.append(",");
			}
			sql.append(m.getString("field")).append(" ").append(m.getString("type"));
			String lenStr = m.getString("length");
			if(lenStr==null||"".equals(lenStr.trim())){
				lenStr = "";
			}else{
				lenStr = "("+lenStr+")";
			}
			sql.append(lenStr).append(" ");
			
			String pbc = m.getString("pbc");
			sql.append(pbc!=null?pbc:"").append(" ");
			String comment = m.getString("comment");
			sql.append(comment!=null?"COMMENT '"+comment+"'":"").append(" ");
			
		}
		sql.append(") ENGINE = MYISAM DEFAULT CHARACTER SET UTF8");
		return sql.toString();
	}
	
	/**
	 * 临时表名称
	 * @return 
	 * @author ring
	 * @date 2017年8月9日 下午3:53:52
	 * @version V1.0
	 */
	public static String tmpTableName(){
		return "temp_"+UUID.randomUUID().toString().substring(0, 8);
	}
	
	/**
	 * 复制表结构到新表，不带主键
	 * @param targetTable
	 * @param srcTable
	 * @return 
	 * @author ring
	 * @date 2017年8月9日 下午3:56:01
	 * @version V1.0
	 */
	public static String getCreateTableSql(String targetTable,String srcTable){
		StringBuffer sb = new StringBuffer();
		sb.append(" CREATE TABLE").append(targetTable);
		sb.append(" SELECT * FROM  ").append(srcTable).append(" WHERE 1 = 2");
		return sb.toString();
	}
	
	/**
	 * 复制表结构到新表，带主键
	 * @param targetTable
	 * @param srcTable
	 * @return 
	 * @author ring
	 * @date 2017年8月9日 下午3:56:01
	 * @version V1.0
	 */
	public static String getCreateLikeTableSql(String targetTable,String srcTable){
		StringBuffer sb = new StringBuffer();
		sb.append(" CREATE TABLE").append(targetTable);
		sb.append(" LIKE  ").append(srcTable);
		return sb.toString();
	}
	
	/**
	 * 更新表
	 * @param targetTable	目标表
	 * @param srcTable		源表
	 * @param set			目标表更新的字段
	 * @param otherSet		目标表更新的默认值字段
	 * @param where			目标表与源表主键条件
	 * @return 
	 * @author ring
	 * @date 2017年8月9日 下午4:08:35
	 * @version V1.0
	 */
	public static String updateTableFromTable(String targetTable,String srcTable,String[] set,Map<String, Object> otherSet,String... where){
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE ").append(targetTable+" t,").append(srcTable+" s SET ");
		if(set!=null&&set.length>0){
			for(int i=0;i<set.length;i++){
				if(i>0){
					sb.append(",");
				}
				sb.append(" t.").append(set[i]).append(" = ").append(" s.").append(set[i]);
			}
		}
		if(otherSet!=null){
			Iterator<String> keys = otherSet.keySet().iterator();
			while(keys.hasNext()){
				if(set!=null&&set.length>0){
					sb.append(",");
				}
				String key = keys.next();
				Object value = otherSet.get(key);
				sb.append(" t.").append(key).append(" = ").append(" s.").append(value);
			}
		}
		sb.append(" WHERE 1=1 ");
		if(where!=null&&where.length>0){
			for(int i=0;i<where.length;i++){
				sb.append(" AND ").append(" t.").append(where[i]).append(" = ").append(" s.").append(where[i]);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 插入表
	 * @param targetTable	目标表
	 * @param srcTable		源表
	 * @param on			主键条件
	 * @return 
	 * @author ring
	 * @date 2017年8月9日 下午4:14:34
	 * @version V1.0
	 */
	public static String insertTableFromTable(String targetTable,String srcTable,String[] on){
		StringBuffer sb = new StringBuffer(" INSERT INTO "+targetTable);
		sb.append(" SELECT * FROM ").append(srcTable+" s ");
		sb.append(" LEFT JOIN ").append(targetTable+" t ON ");
		StringBuffer wheresb = new StringBuffer(" WHERE 1=1 ");
		if(on!=null&&on.length>0){
			for(int i=0;i<on.length;i++){
				if(i>0){
					sb.append("AND");
				}
				sb.append(" s.").append(on[i]).append(" = ").append(" t.").append(on[i]);
				wheresb.append(" AND t.").append(on[i]).append(" IS NULL ");
			}
		}	
		sb.append(wheresb);
		return sb.toString();
	}
	
}
