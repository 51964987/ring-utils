package ringutils.poi.read.impl;

public interface ReadSheetHandlerCallback {
	
	/**
	 * 为POJO对象字段赋值之前方法
	 * @param rownum
	 * @param col
	 * @param formattedValue
	 * @throws Exception 
	 * @author ring
	 * @date 2017年7月18日 下午4:27:54
	 * @version V1.0
	 */
	void before(int rownum,short col,String formattedValue)throws Exception;
	
	/**
	 * 读取数据回调方法
	 * @param rownum
	 * @param row
	 * @param entity
	 * @throws Exception 
	 * @author ring
	 * @date 2017年7月18日 下午4:27:24
	 * @version V1.0
	 */
	void callback(int rownum,String[] row,Object entity)throws Exception;
}
