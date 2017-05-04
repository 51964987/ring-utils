package ringutils.jdbc.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import ringutils.jdbc.JDBCUtil;
import ringutils.jdbc.callback.BatchDataCallback;

public class JDBCUpdateHelper {
	private static Logger log = LoggerFactory.getLogger(JDBCUpdateHelper.class);
	
	/**
	 * 批量插入数据
	 * JDBC连接加上参数rewriteBatchedStatements=true提升性能
	 * @param sql	插入SQ语句L
	 * @param fields	SQL对应字段
	 * @param batchCallback	源数据回调
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月31日 下午2:04:12
	 * @version V1.0
	 */
	public static void insertByBatchDataCallback(String sql,String[] fields,BatchDataCallback callback) throws SQLException{
        Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			ps=conn.prepareStatement(sql);
			callback.batch(ps);            
			ps.executeBatch();
           conn.commit();
        } catch (Exception e) {
        	if(conn!=null){				
				conn.rollback();
			}
			e.printStackTrace();
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(null, conn, ps);
		}
	}
	
	/**
	 * 批量插入数据
	 * JDBC连接加上参数rewriteBatchedStatements=true提升性能
	 * @param sql	插入SQ语句L
	 * @param fields	SQL对应字段
	 * @param data	源数据
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月31日 下午2:04:12
	 * @version V1.0
	 */
	public static void insertBatch(String sql,String[] fields,List<JSONObject> data) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int max = 10000;
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			ps=conn.prepareStatement(sql);
			for(int i=0;i<data.size();i++){
				JSONObject vals = data.get(i);				
				for(int j=0;j<fields.length;j++){
					ps.setObject(j+1,vals.get(fields[j]));
				}
				ps.addBatch();
				if((i+1)%max==0){
					ps.executeBatch();
				}
			}            
			ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			if(conn!=null){				
				conn.rollback();
			}
			e.printStackTrace();
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(null, conn, ps);
		}
	}
	
	/**
	 * 插入值后返回（自动生成的）主键值 
	 * @param sql	插入SQL语句
	 * @param vals	字段对应值
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午8:08:50
	 * @version V1.0
	 */
    public static Object insertWithReturnPrimeKey(String sql,Object... vals) throws SQLException {  
        ResultSet rs = null;  
        Object result = null;  
        Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JDBCUtil.getConnection();
			ps=conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            JDBCUtil.setPSObject(ps, vals);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();  
            if (rs.next()) {  
                result = rs.getObject(1);  
            }
        } catch (Exception e) {
			e.printStackTrace();
			log.error("JDBC操作出错",e);
			throw new SQLException("JDBC操作出错",e);
		}finally{
			JDBCUtil.free(rs, conn, ps);
		}
		return result;  
    }  
	
	/**
	 * 用于增删改
	 * @param sql	增删改SQL语句
	 * @param vals	字段对应值
	 * @return
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午1:51:41
	 * @version V1.0
	 */
	public static int update(String sql,Object... vals) throws SQLException{
		synchronized (JDBCUpdateHelper.class) {			
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = JDBCUtil.getConnection();
				conn.setAutoCommit(false);
				ps=conn.prepareStatement(sql);
				JDBCUtil.setPSObject(ps, vals);
				int rs = ps.executeUpdate();
				conn.commit();
				return rs;
			} catch (Exception e) {
				if(conn!=null){				
					conn.rollback();
				}
				e.printStackTrace();
				log.error("JDBC操作出错",e);
				throw new SQLException("JDBC操作出错",e);
			}finally{
				JDBCUtil.free(null, conn, ps);
			}
		}
	}
}
