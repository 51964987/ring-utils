package ringutils.jdbc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import ringutils.jdbc.JDBCUtil;
import ringutils.jdbc.callback.SQLCallback;

/**
 * 用于JDBC查询<br/>
 * 分页查询：调用SQLHelper.getPageSql(...)获取SQL语句，再调用JDBCQueryHelper.query(...)
 *  
 * @author ring
 * @date 2017年4月1日 下午3:21:44
 * @version V1.0
 */
@SuppressWarnings("unchecked")
public class JDBCQueryHelper {
	private static Logger log = LoggerFactory.getLogger(JDBCQueryHelper.class);

	/**
	 * 查询SQL并返回结果集
	 * @param sql	SQL语句
	 * @param vals	SQL语句参数值
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午5:23:59
	 * @version V1.0
	 */
	public static List<Object[]> list(String sql,Object... vals) throws SQLException{
		return list(Object[].class,sql, vals);
	}
	
	/**
	 * 查询SQL并返回结果集
	 * @param cls	类
	 * @param sql	SQL语句
	 * @param vals	SQL语句参数值
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午5:23:59
	 * @version V1.0
	 */
	public static <T> List<T> list(Class<T> cls,String sql,Object... vals) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			ps=conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
			ps.setFetchSize(10000);
			JDBCUtil.setPSObject(ps, vals);
			rs = ps.executeQuery();
			
			list = new ArrayList<T>();
			while(rs.next()){
				list.add(setT(cls,rs));
			}
			conn.commit();
		} catch (Exception e) {
			if(conn!=null){				
				conn.rollback();
			}
			e.printStackTrace();
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return list;
	}

	/**
	 * 查询SQL并调用回调方法进行处理数据
	 * @param sql	SQL语句
	 * @param sqlCallback	泛型回调方法，用于处理返回的数据，需指定泛型类型
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午5:23:46
	 * @version V1.0
	 */
	public static <T> void listWithCallback(String sql,SQLCallback<T> sqlCallback) throws SQLException{
		listWithCallback(sql, null,sqlCallback);
	}
	
	
	/**
	 * 查询SQL并调用回调方法进行处理数据
	 * @param sql	SQL语句
	 * @param vals	SQL语句参数值
	 * @param sqlCallback	泛型回调方法，用于处理返回的数据，需指定泛型类型
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午5:23:46
	 * @version V1.0
	 * @param <T>
	 */
	public static <T> void listWithCallback(String sql,Object[] vals,SQLCallback<T> sqlCallback) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int max = 10000;
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			ps=conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
			ps.setFetchSize(max);
			JDBCUtil.setPSObject(ps, vals);
			rs = ps.executeQuery();
			
			List<T> list = new ArrayList<T>();
			//获取泛型类型
			Type type = sqlCallback.getClass().getGenericInterfaces()[0];
			Class<T> cls = (Class<T>)((ParameterizedType)type).getActualTypeArguments()[0];
			
			while(rs.next()){
				list.add(setT(cls,rs));
				if(list.size()==max){//一个批次回调方法
					sqlCallback.run(list);
					list.clear();
				}
			}
			if(list.size()>0){//最后一个批次回调方法
				sqlCallback.run(list);
				list.clear();
			}
			conn.commit();
		} catch (Exception e) {
			if(conn!=null){				
				conn.rollback();
			}
			e.printStackTrace();
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
	}

	/**
	 * 返回单个结果集，如count|min|max等
	 * @param sql
	 * @param vals
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午3:45:09
	 * @version V1.0
	 */
	public static Object uniqueResult(String sql,Object... vals) throws SQLException{
		return uniqueResult(null, sql,vals);
	}
	
	/**
	 * 查询单个结果集
	 * @param cls
	 * @param sql
	 * @param vals
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年4月1日 下午2:56:46
	 * @version V1.0
	 */
	public static <T> T uniqueResult(Class<T> cls,String sql,Object... vals) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Object o = null;
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			ps=conn.prepareStatement(sql);
			JDBCUtil.setPSObject(ps, vals);
			rs = ps.executeQuery();
			if(rs.next()){
				if(cls!=null){					
					o = setT(cls,rs);
				}else{
					o = rs.getObject(1);
				}
			}
			if(rs.next()){
				rs.last();//移到最后一行
				throw new Exception("query did not return a unique result:"+rs.getRow());
			}
			conn.commit();
		} catch (Exception e) {
			if(conn!=null){				
				conn.rollback();
			}
			e.printStackTrace();
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return (T) o;
	}
	
	/**
	 * 设置返回对象
	 * @param cls	类
	 * @param rs	返回对象
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年4月1日 上午9:38:16
	 * @version V1.0
	 */
	private static <T> T setT(Class<T> cls,ResultSet rs) throws Exception{
		int count = rs.getMetaData().getColumnCount();
		Object o = null;
		if(cls.isAssignableFrom(Object[].class)){//数组
			o = new Object[count];
		}else{//单个对象
			o = cls.newInstance();
		}
		for(int i=0;i<count;i++){
			
			//获取值
			String colsName = rs.getMetaData().getColumnName(i+1);  
            Object colsValue = rs.getObject(colsName);  
            
            //赋值
            if(cls.isAssignableFrom(Object[].class)){//数组
            	((Object[])o)[i] = colsValue;
            }else if(o instanceof JSONObject || o instanceof Map){//用于调用put方法赋值
            	Method put = cls.getDeclaredMethod("put",new Class[]{Object.class,Object.class});
            	put.invoke(o, new Object[]{colsName,colsValue});
            }else{//JavaBean赋值           	
            	Field field = cls.getDeclaredField(colsName);
            	if(field!=null){
            		field.setAccessible(true); //打开javabean的访问权限  
            		field.set(o, colsValue);
            	}
            }            
		}
		return (T) o;
	}
		
}
