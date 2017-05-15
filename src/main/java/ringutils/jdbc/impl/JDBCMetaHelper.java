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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import ringutils.jdbc.JDBCUtil;

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
	 * 当前表元数据
	 * @param cls
	 * @param tableName
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
			rs = metaData.getTables(conn.getCatalog(), conn.getSchema(), tableName,new String[]{"TABLE"});
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
	 * 当前表的列元数据
	 * @param cls
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:07:18
	 * @version V1.0
	 */
	public static <T> List<T> listColumns(Class<T> cls,String tableName) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getColumns(null, "%", tableName, "%");
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
	 * 当前表的主键元数据
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
	 * 当前表的外键元数据
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
	 * 获取全部数据表元数据（不包含列元数据）
	 * @param cls	指定返回类型
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年5月12日 上午12:05:11
	 * @version V1.0
	 */
	public static <T> List<T> listTables(Class<T> cls) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			conn = JDBCUtil.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getTables(null, "%", "%", new String[] { "TABLE" });
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
		decimal.put("type", "float");
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
		date.put("class", "java.sql.Date");
		map.put("date", date);
		map.put("year", date);

		JSONObject time = new JSONObject();
		time.put("type", "Time");
		time.put("class", "java.sql.Time");
		map.put("time", time);
		
		JSONObject datetime = new JSONObject();
		datetime.put("type", "Timestamp");
		datetime.put("class", "java.sql.Timestamp");
		map.put("datetime", datetime);
		map.put("timestamp", datetime);
		map.put("smalldatetime", datetime);
		map.put("datetime2", datetime);
		
		return map;
	}
}