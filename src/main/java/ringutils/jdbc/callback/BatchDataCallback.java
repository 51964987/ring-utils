package ringutils.jdbc.callback;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 用于批量插入数据
 * @author ring
 * @date 2017年3月31日 下午2:18:22
 * @version V1.0
 */
public interface BatchDataCallback {
	
	/**
	 * 用于一个连接批量插入数据<br/>
	 * 例如：<br/>
	 * final String[] fields = {"name","pric"};<br/>
		String sql = SQLHelper.getInsertSql("test_table_xx",fields);<br/>
		BatchDataCallback batchCallback = new BatchDataCallback() {<br/>
			@Override<br/>
			public void batch(PreparedStatement ps) throws SQLException {<br/>
				int max = 10000;<br/>
				double count = 1000000;<br/>
				int cur = 0;<br/>
				for(int i=0;i<count;i++){<br/>
					ps.setObject(1, new Date().toLocaleString());<br/>
					ps.setObject(2, 1.0);<br/>
					ps.addBatch();<br/>
					if((i+1)%max==0){<br/>
						ps.executeBatch();<br/>
						System.out.println(ProgrssUtil.progrss(cur, count));<br/>
					}<br/>
					cur+=1;<br/>
				}<br/>
			}<br/>
		};<br/>
		JDBCUpdateHelper.insertBatchCallback(sql, fields, batchCallback);
	 * @param ps
	 * @throws SQLException 
	 * @author ring
	 * @date 2017年3月31日 下午2:41:57
	 * @version V1.0
	 */
	void batch(PreparedStatement ps)throws SQLException;
}
