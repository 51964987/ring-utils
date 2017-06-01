package ringutils.poi;

import org.apache.poi.ss.usermodel.Row;

public interface ReadRowCallback<T> {
	
	/**
	 * 读取单元格数据前的回调函数
	 * true:可以读取单元格数据	false:跳过
	 * @param row
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年5月26日 下午6:11:15
	 * @version V1.0
	 */
	boolean cellBefore(Row row)throws Exception;
	
	/**
	 * 读取所有单元格后的回调函数
	 * @param row
	 * @param t
	 * @throws Exception 
	 * @author ring
	 * @date 2017年5月26日 下午6:10:31
	 * @version V1.0
	 * @return 
	 */
	boolean cellAfter(Row row,T t)throws Exception;
}
