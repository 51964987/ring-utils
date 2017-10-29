package ringutils.poi.read;

import java.util.Map;

import ringutils.poi.write.PoiWriteService;

public interface MapSheetHandlerCallback {
	
	/**
	 * 读取单元格之后
	 * @param rownum
	 * @param col
	 * @param formattedValue
	 * @throws Exception 
	 * @author ring
	 * @date 2017年7月18日 下午4:27:54
	 * @version V1.0
	 */
	void readCellAfter(int rownum,short col,String formattedValue)throws Exception;
	
	/**
	 * 读取一行数据之后
	 * @param rownum
	 * @param rowMap
	 * @throws Exception 
	 * @author ring
	 * @date 2017年7月18日 下午4:27:24
	 * @version V1.0
	 */
	void readRowafter(int rownum,Map<Short, String> rowMap)throws Exception;
	
	int getCurrRow();
	int getSplitNum();
	PoiWriteService getWriteService();
}
