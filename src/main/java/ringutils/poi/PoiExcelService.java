package ringutils.poi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.fastjson.JSONObject;

/**
 * POI读、写EXCEL接口，
 * @author ring 
 * @date 2017年3月14日 上午9:44:53
 * @version V1.0
 */
public interface PoiExcelService {
	
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
	
	/**
	 * 用于获取和设置xls文件自定义属性信息
	 * @return 
	 * @author ring
	 * @date 2017年6月1日 下午2:33:57
	 * @version V1.0
	 */
	DocumentSummaryInformation getHssfDocumentInformation();
	
	/**
	 * 用于获取和设置xls文件属性信息
	 * @return 
	 * @author ring
	 * @date 2017年6月1日 下午2:33:57
	 * @version V1.0
	 */
	SummaryInformation getHssfInformation();
	/**
	 * 用于获取和设置xlsx文件属性信息
	 * @return 
	 * @author ring
	 * @date 2017年6月1日 下午2:34:42
	 * @version V1.0
	 */
	CoreProperties getXssfInformation();
	
	<T> void insertPage(String sheetname,List<T> list,String[] fields,String[] titls,InsertRowCallback callback) throws Exception;
	
	<T> void insertRow(String sheetname,T data) throws Exception;
	<T> void insertRow(String sheetname,T data,InsertRowCallback callback) throws Exception;
	<T> void insertRow(String sheetname,T data,String[] fields) throws Exception;
	<T> void insertRow(String sheetname,T data,String[] fields,InsertRowCallback callback) throws Exception;
	
	void output(String filepath) throws IOException;
	
	<T> List<T> read(int sheetindex,int[] cells,String[] fields,Class<T> cls) throws Exception;
	<T> List<T> read(String sheetname,int[] cells,String[] fields,Class<T> cls) throws Exception;
	<T> List<T> read(Sheet sheet,int[] cells,String[] fields,Class<T> cls) throws Exception;
	
	<T> List<T> read(int sheetindex,int[] cells,String[] fields,ReadRowCallback<T> callback) throws Exception;
	<T> List<T> read(String sheetname,int[] cells,String[] fields,ReadRowCallback<T> callback) throws Exception;
	<T> List<T> read(Sheet sheet,int[] cells,String[] fields,ReadRowCallback<T> callback) throws Exception;
	
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
	public Map<String, JSONObject> getSheetMap();
}
