package ringutils.jdbc.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import ringutils.jdbc.JDBCUtil;
import ringutils.string.StringUtil;

/**
 * 用于获取数据库、数据表等元数据信息
 *  
 * @author ring
 * @date 2017年5月11日 下午11:24:57
 * @version V1.0
 */
public class JDBCMetaHelper {
	private static Logger log = LoggerFactory.getLogger(JDBCMetaHelper.class);


	/**
	 * 当前表字段
	 * @param cls
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:07:18
	 * @version V1.0
	 */
	public static String[] fields(String tableName) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getColumns(null, "%", tableName, "%");
			while (rs.next()) {
				list.add(rs.getString("COLUMN_NAME"));
			}
		} catch (Exception e) {
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return list.toArray(new String[]{});
	}
	
	
	/**
	 * 数据库信息
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月18日 下午5:37:43
	 * @version V1.0
	 */
	public static JSONObject tableInfo() throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		JSONObject info = new JSONObject();
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			info.put("user_name",dbmd.getUserName());    
			info.put("system_functions",dbmd.getSystemFunctions());    
			info.put("time_date_functions",dbmd.getTimeDateFunctions());    
			info.put("string_functions",dbmd.getStringFunctions());    
			info.put("schema_term",dbmd.getSchemaTerm());    
			info.put("url",dbmd.getURL());    
			info.put("is_readonly",dbmd.isReadOnly());    
			info.put("database_product_name",dbmd.getDatabaseProductName());    
			info.put("database_product_version",dbmd.getDatabaseProductVersion());    
			info.put("driver_name",dbmd.getDriverName());    
			info.put("driver_version",dbmd.getDriverVersion()); 
		} catch (Exception e) {
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(null, conn, ps);
		}
		return info;
	}
	
	/**
	 * 全部表元数据
	 * @param cls	指定返回类型
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:05:11
	 * @version V1.0
	 */
	public static <T> List<T> listTables(Class<T> cls) throws SQLException{
		return listTables(cls, null);
	}
	
	/**
	 * 表元数据
	 * @param cls
	 * @param tableName	為空時查全部表
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:07:18
	 * @version V1.0
	 */
	public static <T> List<T> listTables(Class<T> cls,String tableName) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getTables(conn.getCatalog(), "%", StringUtils.isNotEmpty(tableName)?tableName:"%", new String[] { "TABLE" });
			list = new ArrayList<T>();
			while (rs.next()) {
				list.add(JDBCQueryHelper.setT(cls, rs));
			}
		} catch (Exception e) {
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return list;
	}
	
	/**
	 * 列元数据
	 * @param cls
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:07:18
	 * @version V1.0
	 */
	public static List<JSONObject> listColumns(String... tableNames) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet pkrs = null;
		List<JSONObject> list = null;
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			Map<String, JSONObject> types = mysqlType2Map();
			for(String tableName : tableNames){				
				pkrs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName);
				Map<String, List<JSONObject>> pkMap = new HashMap<String, List<JSONObject>>();
				while(pkrs.next()){
					JSONObject o = JDBCQueryHelper.setT(JSONObject.class, pkrs);
					String key = o.getString("COLUMN_NAME")+tableName;
					List<JSONObject> olist = pkMap.get(key);
					if(olist == null){
						olist = new ArrayList<JSONObject>();
					}
					olist.add(o);
					pkMap.put(key, olist);
				}
				
				rs = metaData.getColumns(null, "%", tableName, "%");
				list = new ArrayList<JSONObject>();
				int index = 1;
				while (rs.next()) {
					JSONObject o = JDBCQueryHelper.setT(JSONObject.class, rs);
					o.put("COLUMN_INDEX",index++);
					o.put("COLUMN_PK", pkMap.get(o.getString("COLUMN_NAME")+tableName)!=null?"YES":"");
					o.put("FIELD_NAME", StringUtil.underline2capitalize(o.getString("COLUMN_NAME").toLowerCase()));
					o.put("FIELD_TYPE", types.get(o.getString("TYPE_NAME").toLowerCase()).getString("type"));
					o.put("FIELD_CLASS", types.get(o.getString("TYPE_NAME").toLowerCase()).getString("class"));
					list.add(o);
				}
			}
		} catch (Exception e) {
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return list;
	}
	
	/**
	 * 主键元数据
	 * @param cls	指定返回类型
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:06:00
	 * @version V1.0
	 */
	public static <T> List<T> listPromaryKeys(Class<T> cls,String tableName) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName);
			list = new ArrayList<T>();
			while (rs.next()) {
				list.add(JDBCQueryHelper.setT(cls, rs));
			}
		} catch (Exception e) {
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return list;
	}
	
	/**
	 * 外键元数据
	 * @param cls	指定返回类型
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:06:00
	 * @version V1.0
	 */
	public static <T> List<T> listImportedKeys(Class<T> cls,String tableName) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getImportedKeys(conn.getCatalog(), conn.getSchema(), tableName);
			list = new ArrayList<T>();
			while (rs.next()) {
				list.add(JDBCQueryHelper.setT(cls, rs));
			}
		} catch (Exception e) {
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return list;
	}
	
	/**
	 * MYSQL数据类型对应JAVA类型
	 * @return 
	 * @author ring
	 * @date 2017年5月12日 上午8:54:00
	 * @version V1.0
	 */
	public static Map<String, JSONObject> mysqlType2Map(){
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		
		JSONObject bit = new JSONObject();
		bit.put("type", "boolean");
		map.put("bit", bit);
		
		JSONObject binary = new JSONObject();
		binary.put("type", "byte[]");
		map.put("binary", binary);
		map.put("image", binary);
		map.put("udt", binary);
		map.put("varbinary", binary);
		map.put("blob", binary);
		
		JSONObject smallint = new JSONObject();
		smallint.put("type", "short");
		
		JSONObject intint = new JSONObject();
		smallint.put("type", "int");
		map.put("int", intint);
		map.put("tinyint", intint);
		map.put("smallint", intint);
		map.put("mediumint", intint);
		map.put("boolean", intint);

		JSONObject bigint = new JSONObject();
		bigint.put("type", "BigInteger");
		bigint.put("class", "java.math.BigInteger");
		map.put("bigint", bigint);
		
		JSONObject bigintint = new JSONObject();
		bigintint.put("type", "long");
		map.put("bigint", bigintint);
		map.put("integer", bigintint);
		
		JSONObject floatint = new JSONObject();
		floatint.put("type", "float");
		map.put("float", floatint);
		
		JSONObject doubleint = new JSONObject();
		doubleint.put("type", "double");
		map.put("double", doubleint);
		
		JSONObject decimal = new JSONObject();
		decimal.put("type", "BigDecimal");
		decimal.put("class", "java.math.BigDecimal");
		map.put("decimal", decimal);
		map.put("money", decimal);
		map.put("smallmoney", decimal);
		map.put("numeric", decimal);
		
		JSONObject real = new JSONObject();
		decimal.put("type", "BigDecimal");
		map.put("real", real);

		JSONObject varchar = new JSONObject();
		varchar.put("type", "String");
		varchar.put("class", "java.lang.String");
		map.put("varchar", varchar);
		map.put("char", varchar);
		map.put("nvarchar", varchar);
		map.put("nchar", varchar);
		map.put("text", varchar);
		map.put("ntext", varchar);
		map.put("uniqueidentifier", varchar);
		map.put("xml", varchar);

		JSONObject date = new JSONObject();
		date.put("type", "Date");
		date.put("class", "java.util.Date");
		map.put("date", date);
		map.put("year", date);

		JSONObject time = new JSONObject();
		time.put("type", "Time");
		time.put("class", "java.sql.Time");
		map.put("time", time);
		
		JSONObject datetime = new JSONObject();
		datetime.put("type", "Date");
		datetime.put("class", "java.util.Date");
		map.put("datetime", datetime);
		map.put("timestamp", datetime);
		map.put("smalldatetime", datetime);
		map.put("datetime2", datetime);
		
		return map;
	}
}
