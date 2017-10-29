package ringutils.poi.read.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ringutils.poi.read.MapSheetHandlerCallback;
import ringutils.poi.read.ReadSheetContentsHandler;

public class ReadSheetHandlerMapImpl implements ReadSheetContentsHandler{
	private Logger log = LoggerFactory.getLogger(ReadSheetHandlerMapImpl.class);
	
	private int rownum;			//当前行
	private int dataCount;		//输入数据总数，不包括标题
	private String sheetname;	//SHEET名称
	
	private Map<Short,String> rowMap = null;		//当前行数据
	
	private MapSheetHandlerCallback callback;	//回调
		
	public ReadSheetHandlerMapImpl(MapSheetHandlerCallback callback) {
		super();
		this.callback = callback;
	}

	@Override
	public void startRow(int rownum) {
		log.info("正在读取("+(sheetname==null?"":sheetname)+")"+(rownum+1)+"行数据");
		try {
			this.rownum = rownum;
			rowMap = new HashMap<>();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	@Override
	public void endRow(int rownum) {
		try {
			if(rownum>0){//第一行为标题
				dataCount++;
			}
			callback.readRowafter(rownum, rowMap);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public void cell(String cellReference, String formattedValue,XSSFComment comment) {
		try {
			short col = new CellReference(cellReference).getCol();
			rowMap.put(col, formattedValue);
			callback.readCellAfter(rownum, col, formattedValue);
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
