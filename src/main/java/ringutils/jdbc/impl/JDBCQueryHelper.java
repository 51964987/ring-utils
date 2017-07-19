package ringutils.jdbc.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ringutils.bean.BeanUtil;
import ringutils.jdbc.JDBCUtil;
import ringutils.jdbc.callback.SQLCallback;
import ringutils.number.ProgrssUtil;

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
		PreparedStatement countps = null;
		PreparedStatement ps = null;
		ResultSet countrs = null;
		ResultSet rs = null;
		int max = 10000;
		double counts = 0; 
		try {
			
			List<T> list = new ArrayList<T>();
			//获取泛型类型
			Type type = sqlCallback.getClass().getGenericInterfaces()[0];
			Class<T> cls = (Class<T>)((ParameterizedType)type).getActualTypeArguments()[0];
			
			String countSql = "select count(*) from "+sql.toLowerCase().split("from")[1];
			log.info("JDBC查询:"+countSql);
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			
			countps = conn.prepareStatement(countSql);
			JDBCUtil.setPSObject(countps,vals);
			long countlong = System.currentTimeMillis();
			countrs = countps.executeQuery();
			if(countrs.next()){
				counts = countrs.getDouble(1);
			}
			log.info("COUNT:"+counts+"，耗时"+(float)(System.currentTimeMillis()-countlong)/1000+"秒JDBC查询:"+countSql);
			
			log.info("JDBC查询:"+sql);
			ps=conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
			ps.setFetchSize(max);
			JDBCUtil.setPSObject(ps, vals);
			long qlong = System.currentTimeMillis();
			rs = ps.executeQuery();
			log.info("耗时"+(float)(System.currentTimeMillis()-qlong)/1000+"秒JDBC查询:"+sql);
			
			long opstart = System.currentTimeMillis();
			long start = System.currentTimeMillis();
			double cur=0;
			while(rs.next()){
				list.add(setT(cls,rs));
				if(list.size()==max){//一个批次回调方法
					float s1 = (float)(System.currentTimeMillis()-start)/1000;
					long s2 = System.currentTimeMillis();
					sqlCallback.run(list);
					float scb = (float)(System.currentTimeMillis()-s2)/1000;
					log.info(ProgrssUtil.progrss(cur, counts)+"获取"+list.size()+"条数据耗时"+s1+"秒，处理"+list.size()+"条数据耗时"+scb+"秒");
					list.clear();
					start = System.currentTimeMillis();
				}
				cur++;
			}
			if(list.size()>0){//最后一个批次回调方法
				float s1 = (float)(System.currentTimeMillis()-start)/1000;
				long s2 = System.currentTimeMillis();
				sqlCallback.run(list);
				float scb = (float)(System.currentTimeMillis()-s2)/1000;
				log.info(ProgrssUtil.progrss(cur, counts)+"获取"+list.size()+"条数据耗时"+s1+"秒，处理"+list.size()+"条数据耗时"+scb+"秒");
				list.clear();
				start = System.currentTimeMillis();
			}
			log.info("处理"+cur+"条数据耗时"+(float)(System.currentTimeMillis()-opstart)/1000+"秒");
			conn.commit();
		} catch (Exception e) {
			if(conn!=null){				
				conn.rollback();
			}
			e.printStackTrace();
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(countrs, null, countps);
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
	public static <T> T setT(Class<T> cls,ResultSet rs) throws Exception{
		int count = rs.getMetaData().getColumnCount();
		T o = BeanUtil.newInstance(cls, count);
		for(int i=0;i<count;i++){
			//获取值
			String colsName = rs.getMetaData().getColumnName(i+1);  
            Object colsValue = rs.getObject(colsName);  
            BeanUtil.setT(o, colsName, colsValue, i);            
		}
		return (T) o;
	}
	
}
