package ringutils.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCUtil {
	
	private static Logger log = LoggerFactory.getLogger(JDBCUtil.class);
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	/**
	 * 根据properties文件路径获取输入流
	 * @param propertiesPath <br/>
	 * driver = <br/>
	 * url = <br/>
	 * username = <br/>
	 * password = <br/>
	 * @return 
	 * @author ring
	 * @date 2017年5月11日 下午11:19:09
	 * @version V1.0
	 */
	public static InputStream getPropertiesInputStream(String propertiesPath){
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesPath);
	}
	
	/**
	 * 初始化
	 * @param propertiesPath	properties文件路径
	 * @param driverMapping		driver映射名称
	 * @param urlMapping		url映射名称
	 * @param usernameMapping	username映射名称
	 * @param passwordMapping 	password映射名称
	 * @author ring
	 * @date 2017年12月21日 上午10:11:26
	 * @version V1.0
	 */
	public static void init(String propertiesPath,String driverMapping,
			String urlMapping,String usernameMapping,String passwordMapping){
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesPath);
		init(inStream, driverMapping, urlMapping, usernameMapping, passwordMapping);
	}
	
	/**
	 * 初始化
	 * @param inStream			properties输入流
	 * @param driverMapping		driver映射名称
	 * @param urlMapping		url映射名称
	 * @param usernameMapping	username映射名称
	 * @param passwordMapping 	password映射名称
	 * @author ring
	 * @date 2017年12月21日 上午10:14:20
	 * @version V1.0
	 */
	public static void init(InputStream inStream,String driverMapping,
			String urlMapping,String usernameMapping,String passwordMapping){
		try {
			Properties p = new Properties();
			p.load(inStream);
			driver = p.getProperty(driverMapping);
			url = p.getProperty(urlMapping);
			username = p.getProperty(usernameMapping);
			password = p.getProperty(passwordMapping);
			init(driver, url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 初始化
	 * @param driverstr
	 * @param urlstr
	 * @param usernamestr
	 * @param passwordstr 
	 * @author ring
	 * @date 2017年12月21日 上午10:14:50
	 * @version V1.0
	 */
	public static void init(String driverstr,String urlstr,String usernamestr,String passwordstr){
		try {
			driver = driverstr;
			url = urlstr;
			username = usernamestr;
			password = passwordstr;
			Class.forName(driver);//装载驱动
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 参数赋值
	 * @param ps
	 * @param vals
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 下午1:49:00
	 * @version V1.0
	 */
	public static void setPSObject(PreparedStatement ps,Object[] vals) throws SQLException{
		if(vals!=null&&vals.length>0){
			for(int i=0;i<vals.length;i++){
				ps.setObject(i+1, vals[i]);
			}
		}
	}
	
	/**
	 * 获取连接
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月30日 上午11:25:14
	 * @version V1.0
	 */
	public static Connection getConnection() throws Exception{
		return DriverManager.getConnection(url,username, password);
	}
	
	/**
	 * 释放资源
	 * @param rs
	 * @param conn
	 * @param ps 
	 * @author ring
	 * @date 2017年3月30日 下午3:56:54
	 * @version V1.0
	 */
	public static void free(ResultSet rs,Connection conn,PreparedStatement ps){
		freeResultSet(rs);
		freePreparedStatement(ps);
		freeConnection(conn);
	}
	
	/**
	 * 释放连接
	 * @param conn 
	 * @author ring
	 * @date 2017年3月30日 上午11:26:15
	 * @version V1.0
	 * @throws SQLException 
	 */
	public static void freeConnection(Connection conn){
		try {
			if(conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("关闭JDBC连接出错",e);
		}
	}
	
	/**
	 * 释放结果集
	 * @param conn 
	 * @author ring
	 * @date 2017年3月30日 上午11:26:15
	 * @version V1.0
	 * @throws SQLException 
	 */
	public static void freeResultSet(ResultSet rs){
		try {
			if(rs!=null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("关闭JDBC连接出错",e);
		}
	}
	
	/**
	 * 释放PS
	 * @param conn 
	 * @author ring
	 * @date 2017年3月30日 上午11:26:15
	 * @version V1.0
	 * @throws SQLException 
	 */
	public static void freePreparedStatement(PreparedStatement ps){
		try {
			if(ps!=null){
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("关闭JDBC.PreparedStatement出错",e);
		}
	}
	
}
