package ringutils.poi.write.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
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

import ringutils.poi.write.InsertRowCallback;
import ringutils.poi.write.PoiWriteService;
import ringutils.string.StringUtil;

@SuppressWarnings({"unchecked","rawtypes"})
public class PoiWriteServiceImpl implements PoiWriteService {
	
	private Logger log = LoggerFactory.getLogger(PoiWriteServiceImpl.class);
	private Workbook workbook;
	private int pageSize=1000000;//每一百万数据分页
	private int count;			//总数据，可能包括标题
	private int rownum;
	private int pageOutCount;	//输出数据总数，不包括标题，用于insertPage方法
	private Map<String, JSONObject> sheetMap = new HashMap<String, JSONObject>(); 

	public SXSSFWorkbook getSXSSFWorkbook(){
		if(this.workbook instanceof SXSSFWorkbook){
			return (SXSSFWorkbook)this.workbook;
		}
		return null;
	}
	
	public XSSFWorkbook getXSSFWorkbook(){
		if(this.workbook instanceof SXSSFWorkbook){
			return ((SXSSFWorkbook)this.workbook).getXSSFWorkbook();
		}
		if(this.workbook instanceof XSSFWorkbook){
			return (XSSFWorkbook)this.workbook;
		}
		return null;
	}
	
	public HSSFWorkbook getHSSFWorkbook(){
		if(this.workbook instanceof HSSFWorkbook){
			return (HSSFWorkbook)this.workbook;
		}
		return null;
	}
	
	public DocumentSummaryInformation getDocumentInformationFromHSSF(){
		HSSFWorkbook wb = getHSSFWorkbook();
		wb.createInformationProperties();
		return wb.getDocumentSummaryInformation();
	}
	
	public SummaryInformation getInformationFromHSSF(){
		return getHSSFWorkbook().getSummaryInformation();
	}
	public CoreProperties getCorePropertiesFromXSSF(){
		return getXSSFWorkbook().getProperties().getCoreProperties();
	}
	public CoreProperties getCorePropertiesFromSXSSF(){
		return getSXSSFWorkbook().getXSSFWorkbook().getProperties().getCoreProperties();
	}

		
	private Sheet createOrGetSheet(String sheetname){
		Sheet sheet = null;
		JSONObject sheetJson = sheetMap.get(sheetname);
		if(sheetJson == null){
			sheetJson = new JSONObject();
			sheetJson.put("page", 1);
			sheetJson.put("sheetname", sheetname);
		}
		
		sheet = this.workbook.getSheet(sheetJson.getString("sheetname"))==null?this.workbook.createSheet(sheetJson.getString("sheetname")):this.workbook.getSheet(sheetJson.getString("sheetname"));
		
		if(sheet.getLastRowNum() == this.pageSize){//下一页
			this.rownum=0;
			if(sheetJson.getIntValue("page")==1){
				this.workbook.setSheetName(this.workbook.getSheetIndex(sheetJson.getString("sheetname")), sheetname+"1");
			}
			sheetJson.put("page", sheetJson.getIntValue("page")+1);
			sheetJson.put("sheetname", sheetname+sheetJson.getIntValue("page"));
			sheet = this.workbook.createSheet(sheetJson.getString("sheetname"));
			sheetMap.put(sheetname, sheetJson);
		}
		
		return sheet;
	}
		
	public <T> void insertPage(String sheetname,List<T> list,String[] fields,String[] titls,InsertRowCallback callback) throws Exception{
		if(list != null && list.size() > 0){
			for(int i=0;i<list.size();i++){
				Sheet sheet = createOrGetSheet(sheetname);
				if(this.rownum == 0){
					this.insertRow(sheet, titls, null, callback);//插入标题
				}
				this.insertRow(sheet, list.get(i), fields, callback);//插入行
				this.pageOutCount+=1;
			}
		}
	}
	
	public <T> void insertRow(String sheetname,T data) throws Exception{
		insertRow(createOrGetSheet(sheetname), data, null, null);
	}
	public <T> void insertRow(String sheetname,T data,InsertRowCallback callback) throws Exception{
		insertRow(createOrGetSheet(sheetname), data, null, callback);
	}
	public <T> void insertRow(String sheetname,T data,String[] fields) throws Exception{
		insertRow(createOrGetSheet(sheetname), data, fields, null);
	}
	public <T>  void insertRow(String sheetname, T data, String[] fields,InsertRowCallback callback) throws Exception {
		insertRow(createOrGetSheet(sheetname), data, fields, null);
	}	
	private <T> void insertRow(Sheet sheet, T data, String[] fields,InsertRowCallback callback) throws Exception {
		//创建行
		Row row = sheet.getRow(rownum)==null?sheet.createRow(rownum):sheet.createRow(++rownum);
		if(callback!=null){//行回调
			callback.rowCallback(row);
		}
		Class<?> cls = data.getClass();
		int cells = 0;
		if(fields!=null&&fields.length>0){
			cells = fields.length;
		}else{
			if(cls.isArray()){
				cells = Array.getLength(data);
			}else{
				Method method = cls.getDeclaredMethod("size");
				cells = (Integer) method.invoke(data);
			}
		}
		
		//实现接口
		List<Class<?>> clsList = new ArrayList<Class<?>>();
		Collections.addAll(clsList,data.getClass().getInterfaces());
		
		if( (fields==null || fields.length==0) && clsList.contains(Map.class)){
			Set<String> sets = (Set<String>) cls.getDeclaredMethod("keySet").invoke(data);
			fields = sets.toArray(new String[]{});
		}
		Iterator setIterator = null;
		if(clsList.contains(Set.class)){			
			setIterator = (Iterator) cls.getDeclaredMethod("iterator").invoke(data);
		}
		
		//插入数据
		for(int i=0;i<cells;i++){
			
			String key = null;
			if(fields!=null&&fields.length>0){
				key = fields[i];
			}
			
			Object value = null;
	        if(cls.isArray()){
	        	value = Array.get(data, i);
	        }else if(clsList.contains(Map.class)){
	        	Method get = cls.getDeclaredMethod("get",new Class[]{Object.class});
	        	value = get.invoke(data, new Object[]{key});
	        }else if(clsList.contains(List.class)){
	        	Method get = cls.getDeclaredMethod("get",new Class[]{int.class});
	        	value = get.invoke(data, new Object[]{i});
	        }else if(clsList.contains(Set.class)){
	        	setIterator.hasNext();
	        	value = setIterator.next();
	        }else{
	        	Field f = cls.getDeclaredField(StringUtil.underline2capitalize(value+""));
	        	if(f!=null){
	        		f.setAccessible(true);
	        		value = f.get(data);
	        	}
			}
	        
	        //由回调函数设值
	        /*
	        if(value != null && value instanceof java.sql.Timestamp){
	        	value = DateFormatUtils.format((java.sql.Timestamp)value, "yyyy-MM-dd HH:mm:ss");
	        }else if(value != null && value instanceof java.util.Date){
	        	value = DateFormatUtils.format((java.util.Date)value, "yyyy-MM-dd HH:mm:ss");
	        }*/
	        
	        Cell cell = row.createCell(i);
	        if(callback!=null){	        	
	        	callback.cellCallback(this.rownum,cell, value,data);
	        }else{
	        	cell.setCellValue(value+"");
	        }
		}
		
		//总数
		this.rownum+=1;
		this.count+=1;
		
	}
	
	public void insertRowByStringArray(Sheet sheet, String[] data,InsertRowCallback callback) throws Exception {
		//创建行
		Row row = sheet.getRow(rownum)==null?sheet.createRow(rownum):sheet.createRow(++rownum);
		if(callback!=null){//行回调
			callback.rowCallback(row);
		}
		
		//插入数据
		for(int i=0;i<data.length;i++){
						
			String value = data[i];
						
			Cell cell = row.createCell(i);
			if(callback!=null){	        	
				callback.cellCallback(this.rownum,cell, value,data);
			}else{
				cell.setCellValue(value+"");
			}
		}
		
		//总数
		this.rownum+=1;
		this.count+=1;
		
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
        	    String pattern = "";
        	    if(format == 14 || format == 31 || format == 57 || format == 58){  
        	        //日期  
        	    	pattern = "yyyy-MM-dd";  
        	    }else if (format == 20 || format == 32) {  
        	        //时间  
        	    	pattern = "HH:mm";  
        	    }else if(format == 22){
        	    	pattern = "yyyy-MM-dd HH:mm:ss";  
        	    }
        	    if(pattern.length() > 0){        	    	
        	    	double value = cell.getNumericCellValue();  
        	    	Date date = DateUtil.getJavaDate(value);  
        	    	cellValue = DateFormatUtils.format(date, pattern);
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

	public void output(String filepath) throws IOException{
		FileOutputStream os = null;
		try { 
			if(filepath.toLowerCase().endsWith("xls")){
				if(!(this.workbook instanceof HSSFWorkbook) ){
					throw new IOException("文件扩展名*.xls有误");
				}
			}else if(filepath.toLowerCase().endsWith("xlsx")){
				if((this.workbook instanceof HSSFWorkbook) ){
					throw new IOException("文件扩展名*.xlsx有误");
				}
			}else{
				throw new IOException("输出文件扩展名只能为xls或xlsx");
			}
			log.info("正在生成文件："+filepath);
			File file = new File(filepath);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			long start = System.currentTimeMillis();
			os = new FileOutputStream(filepath);
			this.workbook.write(os);
			log.info("生成文件成功，耗时"+(float)(System.currentTimeMillis()-start)/1000+"秒，"+filepath);
		} catch (Exception e) {
			log.error("生成文件失败："+e.getMessage(),e);
			throw new IOException("生成文件失败："+e.getMessage(), e);
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
			if((this.workbook instanceof SXSSFWorkbook) ){				
				((SXSSFWorkbook)this.workbook).dispose();
			}
		}
	}
	
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCount() {
		return count;
	}
	public int getPageOutCount() {
		return pageOutCount;
	}
	public Map<String, JSONObject> getSheetMap() {
		return sheetMap;
	}

}
