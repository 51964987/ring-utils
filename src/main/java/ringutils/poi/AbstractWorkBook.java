package ringutils.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public abstract class AbstractWorkBook {
	
	private Logger log = LoggerFactory.getLogger(AbstractWorkBook.class);
	AbstractWorkBook(Workbook workbook){}
	public Workbook workbook;
	
	/**
	 * 当前工作薄下标
	 */
	public int curSheetIndex;
	/**
	 * 当前行号
	 */
	public int curRowNum;
	
	/**
	 * 用于TYPE_SXSSF，保持在内存中10000行,超过行会刷新到磁盘,-1关掉auto-flushing和积累在内存中所有行
	 */
	public int rowaccess = 10000;

	/**
	 * 工作薄信息集合
	 * @param rowNum_sheetnameIndex	工作薄当前行号
	 * @param sheetnameIndex	工作薄名称下标，从1开始,大于1时sheetname=sheetname+sheetnameIndex
	 * {sheetname:{rowNum_sheetnameIndex:0,sheetnameIndex:1}} 
	 */
	public Map<String, JSONObject> sheetInfoMaps;
	public AbstractWorkBook() {
		super();
		//初始化
		if(sheetInfoMaps==null){			
			sheetInfoMaps = new HashMap<String, JSONObject>();
		}
	}
	
	/**
	 * 读取EXCEL工作薄数据
	 * @param sheetindex
	 * @param cells		列数下标
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 上午10:47:50
	 * @version V1.0
	 */
	public List<Object[]> read(int sheetindex,int[] cells) {
		Sheet sheet = this.workbook.getSheetAt(sheetindex);
		List<Object[]> result = new ArrayList<Object[]>();
		Iterator<Row> rows = sheet.rowIterator();
		while(rows.hasNext()){
			Row row = rows.next();
			Object[] vals = new Object[cells.length];
			for(int i=0;i<cells.length;i++){
				Cell cell = row.getCell(i);
				if(cell==null){
					continue;
				}
				vals[i] = getCellStringValue(cell);
			}
			result.add(vals);
		}
		return result;
	}
	
	/**
	 * 读取单元格数据
	 * 可根据需要重写
	 * @param cell
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 下午1:49:00
	 * @version V1.0
	 */
	public Object getCellStringValue(Cell cell) {        
        Object cellValue = null;        
        switch (cell.getCellType()) {        
        case Cell.CELL_TYPE_STRING://字符串类型
        	cellValue = cell.getStringCellValue();        
            break;        
        case Cell.CELL_TYPE_NUMERIC: //数值类型     
        	short format = cell.getCellStyle().getDataFormat();
        	/**
        	 * 所有日期格式都可以通过getDataFormat()值来判断
				yyyy-MM-dd-----	14
				yyyy年m月d日---	31
				yyyy年m月-------	57
				m月d日  ----------	58
				HH:mm-----------	20
				h时mm分  -------	32
        	 */
        	if (DateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式  
        	    SimpleDateFormat sdf = null;  
        	    if(format == 14 || format == 31 || format == 57 || format == 58){  
        	        //日期  
        	        sdf = new SimpleDateFormat("yyyy-MM-dd");  
        	    }else if (format == 20 || format == 32) {  
        	        //时间  
        	        sdf = new SimpleDateFormat("HH:mm");  
        	    }else if(format == 22){
        	    	 sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        	    }
        	    if(sdf!=null){        	    	
        	    	double value = cell.getNumericCellValue();  
        	    	Date date = DateUtil.getJavaDate(value);  
        	    	cellValue = sdf.format(date);  
        	    }
            } else { 
            	double doubleVal = cell.getNumericCellValue();
            	long longVal = Math.round(doubleVal);  
                if (Double.parseDouble(longVal + ".0") == doubleVal) {
                	cellValue = longVal;
                }else{
                	cellValue = doubleVal;
                }
                //科学计数法的数据则由调用者处理
                //DecimalFormat df = new DecimalFormat("0");  
                //String whatYourWant = df.format(cell.getNumericCellValue()); 
            }
            break;        
        case Cell.CELL_TYPE_FORMULA: //公式型     
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);        
            cellValue = String.valueOf(cell.getNumericCellValue());        
            break;        
        case Cell.CELL_TYPE_BLANK://空值        
            cellValue="";        
            break;    
        case Cell.CELL_TYPE_BOOLEAN://布尔型
        	cellValue = cell.getBooleanCellValue();
           break;  
        case Cell.CELL_TYPE_ERROR://错误
        	cellValue = "非法字符";
            break;        
        default:     
        	cellValue = "未知类型";
            break;        
        }        
        return cellValue;        
    }

	/**
	 * 创建工作薄
	 * @param sheetname
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午9:09:25
	 * @version V1.0
	 */
	public int createSheet(String sheetname){
		Sheet sheet = this.workbook.createSheet(sheetname);
		this.curSheetIndex = this.workbook.getSheetIndex(sheet);
		return this.curSheetIndex;
	}
	
	/**
	 * 获取工作薄信息
	 * @param sheetname
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午6:11:49
	 * @version V1.0
	 */
	private JSONObject getSheetInfo(String sheetname){
		JSONObject sheetInfo = sheetInfoMaps.get(sheetname);
		if(sheetInfoMaps.get(sheetname)==null){			
			sheetInfo = new JSONObject();
			sheetInfo.put(PoiExcelService.INFO_SHEETNAME_INDEX, 1);
			this.curRowNum = 0;
			sheetInfoMaps.put(sheetname, sheetInfo);
		}
		return sheetInfo;
	}

	//############################################## 分页增加行 ##################################################################//
	
	public void addRowByPage(JSONObject data,String[] cellField,String[] titleData,String sheetname,int maxPageSize,AddRowCallback callback){
		addObjectRowByPage(data, cellField, titleData, sheetname, maxPageSize, callback);
	}
	
	public void addRowByPage(String[] data,String[] titleData,String sheetname,int maxPageSize,AddRowCallback callback){
		addObjectRowByPage(data, null, titleData, sheetname, maxPageSize, callback);
	}
	
	public void addRowByPage(String[] data,String sheetname,int maxPageSize,AddRowCallback callback){
		addRowByPage(data, null, sheetname, maxPageSize, callback);
	}
	
	public void addRowByPage(JSONObject data,String[] cellField,String sheetname,int maxPageSize,AddRowCallback callback){
		addRowByPage(data, cellField, null, sheetname, maxPageSize, callback);
	}
	
	private void addObjectRowByPage(Object data,String[] cellField,String[] titleData,String sheetname,int maxPageSize,AddRowCallback callback){
		//工作薄信息
		JSONObject sheetInfo = getSheetInfo(sheetname);
		int curSheetnameIndex = sheetInfo.getIntValue(PoiExcelService.INFO_SHEETNAME_INDEX);
		
		//达到最大行数时，生成第下一页
		if(this.curRowNum == maxPageSize+(titleData!=null?1:0)){
			this.curRowNum = 0;//第一行从0开始
			//下一页
			sheetInfo.put(PoiExcelService.INFO_SHEETNAME_INDEX, curSheetnameIndex+1);
			if(curSheetnameIndex==1){				
				//更改sheetname为sheetname1
				this.workbook.setSheetName(this.curSheetIndex, sheetname+"1");
			}
		}
		
		//工作薄实例
		curSheetnameIndex = sheetInfo.getIntValue(PoiExcelService.INFO_SHEETNAME_INDEX);
		String strSheetname = curSheetnameIndex>1?sheetname+curSheetnameIndex:sheetname;
		
		Sheet sheet = this.workbook.getSheet(strSheetname);
		if(sheet==null){
			createSheet(strSheetname);
			//标题源数据
			if(titleData!=null){
				this.addRow(titleData, this.curSheetIndex, this.curRowNum, callback);
				//下一行
				this.curRowNum+=1;
			}
		}
		
		//增加行
		if(data instanceof JSONObject){			
			this.addRow((JSONObject)data,cellField, this.curSheetIndex, this.curRowNum, callback);
		}else if(data instanceof String[]){
			this.addRow((String[])data, this.curSheetIndex, this.curRowNum, callback);
		}else{
			throw new RuntimeException("not found "+data.getClass()+" data method 'addRow'");
		}
		//下一行
		nextRow(sheetname);
	}

	/**
	 * 移到下一行
	 * @param sheetname 
	 * @author ring
	 * @date 2017年3月14日 下午6:00:58
	 * @version V1.0
	 */
	public void nextRow(String sheetname){
		JSONObject sheetInfo = getSheetInfo(sheetname);
		//下一行
		this.curRowNum+=1;
		//当前工作薄行号
		sheetInfo.put(PoiExcelService.INFO_ROWNUM+"_"+sheetInfo.getIntValue(PoiExcelService.INFO_SHEETNAME_INDEX), this.curRowNum);
		sheetInfoMaps.put(sheetname, sheetInfo);
	}
	
	/**
	 * 移到下一行
	 * @param sheetname 
	 * @author ring
	 * @date 2017年3月14日 下午6:00:58
	 * @version V1.0
	 */
	public void nextRow(){
		String sheetname = this.workbook.getSheetAt(this.curSheetIndex).getSheetName();
		//工作薄信息
		getSheetInfo(sheetname);
		for(Iterator<String> keys = sheetInfoMaps.keySet().iterator();keys.hasNext();){
			String key = keys.next();
			JSONObject sheetInfo = sheetInfoMaps.get(key);
			int sheetnameindex = sheetInfo.getIntValue(PoiExcelService.INFO_SHEETNAME_INDEX);
			String strSheetname = sheetnameindex>1?key+sheetnameindex:key;
			if(strSheetname.equals(sheetname)){				
				nextRow(key);
				break;
			}
		}
	}
	
	//############################################## 增加行 ##################################################################//
	
	public void addRow(JSONObject data,String[] cellField,int sheetindex,int rownum,AddRowCallback callback){
		addObjectRow(data, cellField, sheetindex, rownum, callback);
	}
	
	public void addRow(JSONObject data,String[] cellField,AddRowCallback callback){
		addRow(data, cellField, this.curSheetIndex, this.curRowNum, callback);
	}
	
	public void addRow(String[] data,int sheetindex,int rownum,AddRowCallback callback){
		addObjectRow(data, data, sheetindex, rownum, callback);
	}
	
	public void addRow(String[] data,AddRowCallback callback){
		addRow(data, this.curSheetIndex, this.curRowNum, callback);
	}

	/**
	 * 增加行
	 * @param data		源代码
	 * @param target	列字段数据
	 * @param sheetindex	当前工作薄下标
	 * @param rownum		当前行
	 * @param callback 
	 * @author ring
	 * @date 2017年3月14日 下午8:53:00
	 * @version V1.0
	 */
	private void addObjectRow(Object data,String[] target,int sheetindex,int rownum,AddRowCallback callback){
		//创建行
		Row row = this.workbook.getSheetAt(sheetindex).createRow(rownum);
		if(callback!=null){//行回调
			callback.rowCallback(row);
		}
		//插入数据
		for(int i=0;i<target.length;i++){
			Cell cell = row.createCell(i);
			
			if(data instanceof String[]){
				cell.setCellValue(((String[])data)[i]);
			}else if(data instanceof JSONObject){				
				cell.setCellValue(((JSONObject)data).getString(target[i]));
			}
			
			if(callback!=null){//列回调
				callback.cellCallback(cell,row);
			}
		}
	}
	
	/**
	 * 生成文件
	 * @param outputFullPath 
	 * @author ring
	 * @date 2017年3月14日 下午4:51:17
	 * @version V1.0
	 */
	public void write(String outputFullPath){
		long start = System.currentTimeMillis();
		FileOutputStream os = null;
		try { 
			if(outputFullPath.endsWith("xls")){
				if(!(this.workbook instanceof HSSFWorkbook) ){
					throw new RuntimeException("文件扩展名*.xls有误");
				}
			}else{
				if((this.workbook instanceof HSSFWorkbook) ){
					throw new RuntimeException("文件扩展名*.xlsx有误");
				}
			}
			log.info("正在生成文件："+outputFullPath);
			//File file = new File(outputFullPath.substring(0,outputFullPath.lastIndexOf("/")));
			File file = new File(outputFullPath);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			os = new FileOutputStream(outputFullPath);
			this.workbook.write(os);
			printLogTime(log,start, "生成文件成功");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("生成文件失败："+e.getMessage(),e);
			throw new RuntimeException("生成文件失败："+e.getMessage(), e);
		} finally {
			if(os!=null){				
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(),e);
				}
			}
			//删除缓存文件
			this.dispose();
		}
	}
	
	/**
	 * 获取Workbook实例
	 * @param type
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午2:53:34
	 * @version V1.0
	 */
	protected Workbook getInstanceFactory(String type){
		//根据类型获取实例
		if(type==null||type.equalsIgnoreCase(PoiExcelService.TYPE_HSSF)){
			return workbook = new HSSFWorkbook();
		}else if(type.equalsIgnoreCase(PoiExcelService.TYPE_XSSF)){
			return workbook = new XSSFWorkbook();
		}else if(type.equalsIgnoreCase(PoiExcelService.TYPE_SXSSF)){
			return workbook = new SXSSFWorkbook(this.getRowaccess());
		}
		return null;
	}
	
	/**
	 * 打印日志
	 * @param log
	 * @param start
	 * @param tip 
	 * @author ring
	 * @date 2017年3月14日 上午10:54:05
	 * @version V1.0
	 */
	public void printLogTime(Logger log,long start,String tip){
		long end = System.currentTimeMillis();
		String s = String.format("%s 耗时：%s%s", tip,(float)(end-start)/1000,"秒");
		log.info(s);
	}
	
	//############################################## 子类必须重写 ##################################################################//
	
	//abstract
	public abstract Workbook getInstance();
	public abstract void dispose();

	//############################################## ...get,set... ##################################################################//
	
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public int getCurSheetIndex() {
		return curSheetIndex;
	}
	public void setCurSheetIndex(int curSheetIndex) {
		this.curSheetIndex = curSheetIndex;
	}
	public int getCurRowNum() {
		return curRowNum;
	}
	public void setCurRowNum(int curRowNum) {
		this.curRowNum = curRowNum;
	}
	public Map<String, JSONObject> getSheetInfoMaps() {
		return sheetInfoMaps;
	}
	public void setSheetInfoMaps(Map<String, JSONObject> sheetInfoMaps) {
		this.sheetInfoMaps = sheetInfoMaps;
	}
	public int getRowaccess() {
		return rowaccess;
	}
	public void setRowaccess(int rowaccess) {
		this.rowaccess = rowaccess;
	}
	 
}
