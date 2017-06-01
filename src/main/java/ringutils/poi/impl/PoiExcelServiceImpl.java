package ringutils.poi.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
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

import ringutils.bean.BeanUtil;
import ringutils.poi.InsertRowCallback;
import ringutils.poi.PoiExcelService;
import ringutils.poi.ReadRowCallback;
import ringutils.string.StringUtil;

@SuppressWarnings({"unchecked","rawtypes"})
public class PoiExcelServiceImpl implements PoiExcelService {
	
	private Logger log = LoggerFactory.getLogger(PoiExcelServiceImpl.class);
	private Workbook workbook;
	private int pageSize=1000000;
	private int count;
	private Map<String, JSONObject> sheetMap = new HashMap<String, JSONObject>(); 
	
	public DocumentSummaryInformation getHssfDocumentInformation(){
		((HSSFWorkbook)this.workbook).createInformationProperties();
		return ((HSSFWorkbook)this.workbook).getDocumentSummaryInformation();
	}
	
	public SummaryInformation getHssfInformation(){
		return ((HSSFWorkbook)this.workbook).getSummaryInformation();
	}
	public CoreProperties getXssfInformation(){
		return ((XSSFWorkbook)this.workbook).getProperties().getCoreProperties();
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
				if(sheet.getLastRowNum() == 0){
					this.insertRow(sheet, titls, null, callback);//插入标题
				}
				this.insertRow(sheet, list.get(i), fields, callback);//插入行
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
		int rownum = sheet.getLastRowNum();
		Row row = sheet.getRow(rownum)==null?sheet.createRow(rownum):sheet.createRow(rownum+1);
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
	        
	        if(value != null && value instanceof java.sql.Timestamp){
	        	value = DateFormatUtils.format((java.sql.Timestamp)value, "yyyy-MM-dd HH:mm:ss");
	        }else if(value != null && value instanceof java.util.Date){
	        	value = DateFormatUtils.format((java.util.Date)value, "yyyy-MM-dd HH:mm:ss");
	        }
	        
	        Cell cell = row.createCell(i);
	        if(callback!=null){	        	
	        	callback.cellCallback(cell, value,data);
	        }else{
	        	cell.setCellValue(value+"");
	        }
		}
		
		//总数
		this.count+=1;
		
	}
	
	public <T> List<T> read(int sheetindex, int[] cells, String[] fields, Class<T> cls)  throws Exception{
		return read(this.getWorkbook().getSheetAt(sheetindex), cells, fields,cls, null);
	}

	public <T> List<T> read(String sheetname, int[] cells, String[] fields, Class<T> cls)  throws Exception{
		return read(this.getWorkbook().getSheet(sheetname), cells, fields,cls, null);
	}

	public <T> List<T> read(Sheet sheet, int[] cells, String[] fields, Class<T> cls)  throws Exception{
		return read(sheet, cells, fields,cls, null);
	}

	public <T> List<T> read(int sheetindex, int[] cells, String[] fields, ReadRowCallback<T> callback) throws Exception {
		return read(this.getWorkbook().getSheetAt(sheetindex), cells, fields,null, callback);
	}

	public <T> List<T> read(String sheetname, int[] cells,String[] fields, ReadRowCallback<T> callback) throws Exception {
		return read(this.getWorkbook().getSheet(sheetname), cells, fields,null, callback);
	}
	
	public <T> List<T> read(Sheet sheet, int[] cells,String[] fields, ReadRowCallback<T> callback) throws Exception {
		return read(sheet, cells, fields,null, callback);
	}

	private <T> List<T> read(Sheet sheet, int[] cells, String[] fields, Class<T> cls, ReadRowCallback<T> callback) throws Exception {
		List<T> result = new ArrayList<T>();
		Iterator<Row> rows = sheet.rowIterator();
		try {
			while(rows.hasNext()){
				Row row = rows.next();
				
				if(callback != null){
					if(!callback.cellBefore(row)){
						continue;
					}
				}
				
				if(cls == null){
					ParameterizedType parameterizedType = (ParameterizedType) callback.getClass().getGenericInterfaces()[0];
					cls = (Class<T>) parameterizedType.getActualTypeArguments()[0];
				}

				T o = BeanUtil.newInstance(cls, cells.length);
				
				for(int i=0;i<cells.length;i++){
					Cell cell = row.getCell(i);
					if(cell==null){
						continue;
					}
					String key = null;
					if(fields!=null&&fields.length>0){						
						key = fields[i];
					}
					Object value = getCellStringValue(cell);
					BeanUtil.setT(o, key, value, i);
				}
				
				boolean isAdd = true;
				if(callback != null){
					isAdd = callback.cellAfter(row, o);
				}
				
				if(isAdd){					
					result.add(o);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
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
		//long start = System.currentTimeMillis();
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
			os = new FileOutputStream(filepath);
			this.workbook.write(os);
			//printLogTime(log,start, "生成文件成功");
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
	public Map<String, JSONObject> getSheetMap() {
		return sheetMap;
	}
}
