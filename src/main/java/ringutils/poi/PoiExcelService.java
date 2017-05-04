package ringutils.poi;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;

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
	
	/** 当前行号 */
	public final static String INFO_ROWNUM = "rowNum";
	/** 当前工作薄下标 */
	public final static String INFO_SHEETNAME_INDEX = "sheetnameIndex";
	
	/**
	 * 获取Workbook实例
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 上午10:41:40
	 * @version V1.0
	 */
	Workbook getInstance();
	
	/**
	 * 读取EXCEL工作薄数据
	 * @param sheetindex
	 * @param cells		列数下标
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 上午10:47:50
	 * @version V1.0
	 */
	List<Object[]> read(int sheetindex,int[] cells);
	
	/**
	 * 读取单元格数据
	 * @param cell
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 下午1:48:29
	 * @version V1.0
	 */
	Object getCellStringValue(Cell cell);
	
	/**
	 * 生成工作薄
	 * @param sheetname
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 上午11:13:18
	 * @version V1.0
	 */
	int createSheet(String sheetname);
	
	/**
	 * 分页增加
	 * @param data			源数据
	 * @param cellField		列字段
	 * @param titleData		标题源数据，首行
	 * @param sheetname		工作薄名称，分页后显示sheetname+数字1,2,...
	 * @param maxPageSize	每页最大输出数据
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午11:25:43
	 * @version V1.0
	 */
	void addRowByPage(JSONObject data,String[] cellField,String[] titleData,String sheetname,int maxPageSize,AddRowCallback callback);
	
	/**
	 * 分页增加
	 * @param data			源数据
	 * @param cellField		列字段
	 * @param sheetname		工作薄名称，分页后显示sheetname+数字1,2,...
	 * @param maxPageSize	每页最大输出数据
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午11:25:43
	 * @version V1.0
	 */
	void addRowByPage(JSONObject data,String[] cellField,String sheetname,int maxPageSize,AddRowCallback callback);
	
	/**
	 * 分页增加
	 * @param data			源数据
	 * @param titleData		标题源数据，首行
	 * @param sheetname		工作薄名称，分页后显示sheetname+数字1,2,...
	 * @param maxPageSize	每页最大输出数据
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午11:25:43
	 * @version V1.0
	 */
	void addRowByPage(String[] data,String[] titleData,String sheetname,int maxPageSize,AddRowCallback callback);
	
	/**
	 * 分页增加
	 * @param data			源数据
	 * @param sheetname		工作薄名称，分页后显示sheetname+数字1,2,...
	 * @param maxPageSize	每页最大输出数据
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午11:25:43
	 * @version V1.0
	 */
	void addRowByPage(String[] data,String sheetname,int maxPageSize,AddRowCallback callback);
	
	/**
	 * 增加行
	 * @param data			源数据
	 * @param cellField		列字段
	 * @param sheetindex	当前页
	 * @param rownum		当前行
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午9:59:37
	 * @version V1.0
	 */
	void addRow(JSONObject data,String[] cellField,int sheetindex,int rownum,AddRowCallback callback);
	
	/**
	 * 增加行
	 * @param data			源数据
	 * @param cellField		列字段
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午9:59:37
	 * @version V1.0
	 */
	void addRow(JSONObject data,String[] cellField,AddRowCallback callback);
	
	/**
	 * 增加行
	 * @param data			源数据
	 * @param sheetindex	当前页
	 * @param rownum		当前行
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午9:59:37
	 * @version V1.0
	 */
	void addRow(String[] data,int sheetindex,int rownum,AddRowCallback callback);
	
	/**
	 * 增加行
	 * @param data			源数据
	 * @param sheetindex	当前页
	 * @param rownum		当前行
	 * @param callback 		行回调
	 * @author ring
	 * @date 2017年3月14日 上午9:59:37
	 * @version V1.0
	 */
	void addRow(String[] data,AddRowCallback callback);
	
	/**
	 * 移到下一行
	 * @param sheetname 
	 * @author ring
	 * @date 2017年3月14日 下午6:06:36
	 * @version V1.0
	 */
	void nextRow(String sheetname);
	
	/**
	 * 移到下一行
	 * @author ring
	 * @date 2017年3月14日 下午6:06:36
	 * @version V1.0
	 */
	void nextRow();
	
	/**
	 * 生成文件
	 * @param outputFullPath 
	 * @author ring
	 * @date 2017年3月14日 上午10:55:01
	 * @version V1.0
	 */
	void write(String outputFullPath);

	/**
	 * 删除缓存文件，用于SXSSFWorkbook
	 * @author ring
	 * @date 2017年3月14日 下午3:55:56
	 * @version V1.0
	 */
	void dispose();
	
	/**
	 * 打印日志
	 * @param log
	 * @param start
	 * @param tip 
	 * @author ring
	 * @date 2017年3月14日 下午6:20:15
	 * @version V1.0
	 */
	void printLogTime(Logger log,long start,String tip);
	//...get,set
	Workbook getWorkbook();
	void setWorkbook(Workbook workbook) ;
	int getCurSheetIndex() ;
	void setCurSheetIndex(int curSheetIndex);
	int getCurRowNum();
	void setCurRowNum(int curRowNum) ;
	Map<String, JSONObject> getSheetInfoMaps() ;
	void setSheetInfoMaps(Map<String, JSONObject> sheetInfoMaps) ;
	public int getRowaccess();
	public void setRowaccess(int rowaccess) ;
}
