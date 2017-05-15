package ringutils.jdbc.impl;

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
}
