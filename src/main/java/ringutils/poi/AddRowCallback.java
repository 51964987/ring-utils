package ringutils.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public interface AddRowCallback {
	/**
	 * 创建行后回调
	 * @param row 
	 * @author ring
	 * @date 2017年3月15日 上午9:05:59
	 * @version V1.0
	 */
	void rowCallback(Row row);
	
	/**
	 * 创建列回调
	 * @param cell
	 * @param row 
	 * @author ring
	 * @date 2017年3月15日 上午9:06:19
	 * @version V1.0
	 */
	void cellCallback(Cell cell,Row row);
}
