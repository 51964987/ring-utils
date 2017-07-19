package ringutils.poi.read.impl;

import java.lang.reflect.Field;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadSheetHandler implements SheetContentsHandler{
	private Logger log = LoggerFactory.getLogger(ReadSheetHandler.class);
	
	private int rownum;		//当前行
	private String[] row;	//当前行数据
	private Object entity;	//POJO对象
	private Class<?> clz;	//POJO类型
	private int dataCount;	//输入数据总数，不包括标题
	
	private String sheetname;
	private String[] fields;
	private ReadSheetHandlerCallback callback;
	
	public ReadSheetHandler(Class<?> clz, String[] fields, ReadSheetHandlerCallback callback) {
		super();
		this.clz = clz;
		this.fields = fields;
		this.callback = callback;
	}
	
	public ReadSheetHandler(ReadSheetHandlerCallback callback) {
		super();
		this.callback = callback;
	}

	@Override
	public void startRow(int rownum) {
		log.info("正在读取("+(sheetname==null?"":sheetname)+")"+(rownum+1)+"行数据");
		this.rownum = rownum;
		this.row = new String[this.fields.length];
		try {
			if(clz!=null){				
				entity = clz.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	@Override
	public void endRow(int rownum) {
		if(rownum>0){
			dataCount++;
		}
		try {
			callback.callback(rownum, row, entity);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public void cell(String cellReference, String formattedValue,XSSFComment comment) {
		try {
			short col = new CellReference(cellReference).getCol();
			row[col]=formattedValue;
			callback.before(rownum, col, formattedValue);
			if(rownum>0&&entity!=null){//数据
				Field f = null;
				try {
					f = entity.getClass().getDeclaredField(fields[col]);
				} catch (NoSuchFieldException | SecurityException e) {
					log.warn(e.getMessage(),e);
				}
				if(f!=null){
					f.setAccessible(true);
					Object value = formattedValue;
					if(f.getType().isAssignableFrom(String.class)){
						value = formattedValue;
					}else if(f.getType().isAssignableFrom(Long.class)){
						value = Long.valueOf(value+"");
					}else if(f.getType().isAssignableFrom(Double.class)){
						value = Double.valueOf(value+"");
					}else if(f.getType().isAssignableFrom(Integer.class)){
						value = Integer.valueOf(value+"");
					}else if(f.getType().isAssignableFrom(Float.class)){
						value = Float.valueOf(value+"");
					}
					f.set(entity, value);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public void headerFooter(String text, boolean isHeader, String tagName) {
		
	}

	public int getDataCount() {
		return dataCount;
	}
	public String getSheetname() {
		return sheetname;
	}
	public void setSheetname(String sheetname) {
		this.sheetname = sheetname;
	}

	
}
