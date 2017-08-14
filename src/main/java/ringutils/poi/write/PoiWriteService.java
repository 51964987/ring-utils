package ringutils.poi.write;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONObject;

/**
 * POI读、写EXCEL接口，
 * @author ring 
 * @date 2017年3月14日 上午9:44:53
 * @version V1.0
 */
public interface PoiWriteService {
	
	/** HSSFWorkbook */
	public final static String TYPE_HSSF = "HSSF";
	/** XSSFWorkbook */
	public final static String TYPE_XSSF = "XSSF";
	/** SXSSFWorkbook */
	public final static String TYPE_SXSSF = "SXSSF";
	
	/**
	 * 用于TYPE_SXSSF，保持在内存中10000行,超过行会刷新到磁盘,-1关掉auto-flushing和积累在内存中所有行
	 */
	public final static int ROW_ACCESS = 10000;
	
	SXSSFWorkbook getSXSSFWorkbook();
	XSSFWorkbook getXSSFWorkbook();
	HSSFWorkbook getHSSFWorkbook();
	
	/**
	 * 用于获取和设置xls文件自定义属性信息
	 * @return 
	 * @author ring
	 * @date 2017年6月1日 下午2:33:57
	 * @version V1.0
	 */
	DocumentSummaryInformation getDocumentInformationFromHSSF();
	
	/**
	 * 用于获取和设置xls文件属性信息
	 * @return 
	 * @author ring
	 * @date 2017年6月1日 下午2:33:57
	 * @version V1.0
	 */
	SummaryInformation getInformationFromHSSF();
	/**
	 * 用于获取和设置xlsx文件属性信息
	 * @return 
	 * @author ring
	 * @date 2017年6月1日 下午2:34:42
	 * @version V1.0
	 */
	CoreProperties getCorePropertiesFromXSSF();
	/**
	 * 用于获取和设置xlsx文件属性信息
	 * @return 
	 * @author ring
	 * @date 2017年6月1日 下午2:34:42
	 * @version V1.0
	 */
	CoreProperties getCorePropertiesFromSXSSF();
	
	<T> void insertPage(String sheetname,List<T> list,String[] fields,String[] titls,InsertRowCallback callback) throws Exception;
	
	<T> void insertRow(String sheetname,T data) throws Exception;
	<T> void insertRow(String sheetname,T data,InsertRowCallback callback) throws Exception;
	<T> void insertRow(String sheetname,T data,String[] fields) throws Exception;
	<T> void insertRow(String sheetname,T data,String[] fields,InsertRowCallback callback) throws Exception;
	
	void output(String filepath) throws IOException;
	
	/**
	 * 读取单元格数据
	 * @param cell
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 下午1:48:29
	 * @version V1.0
	 */
	Object getCellStringValue(Cell cell);
	
	//...get,set
	Workbook getWorkbook();
	void setWorkbook(Workbook workbook);
	public int getPageSize();
	public void setPageSize(int pageSize);
	public int getCount();
	int getPageOutCount();
	public Map<String, JSONObject> getSheetMap();

}
