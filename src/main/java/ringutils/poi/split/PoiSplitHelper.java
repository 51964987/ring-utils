package ringutils.poi.split;

import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ringutils.poi.read.MapSheetHandlerCallback;
import ringutils.poi.read.PoiReadService;
import ringutils.poi.read.impl.PoiReadServiceImpl;
import ringutils.poi.read.impl.ReadSheetHandlerMapImpl;
import ringutils.poi.write.InsertRowCallback;
import ringutils.poi.write.PoiWriteService;
import ringutils.poi.write.impl.PoiWriteServiceFactory;
import ringutils.poi.write.style.PoiStyleUtil;

public class PoiSplitHelper {
	
	private Logger log = LoggerFactory.getLogger(PoiSplitHelper.class);
	
	private String sourcePath;
	private int pagesize;
	
	public PoiSplitHelper(String sourcePath, int pagesize) {
		super();
		this.sourcePath = sourcePath;
		this.pagesize = pagesize;
	}

	/**
	 * 分割
	 * @param sheetnamePattern 
	 * @author ring
	 * @date 2017年9月9日 下午2:00:25
	 * @version V1.0
	 */
	public void split(final String sheetnamePattern){
		
		PoiReadService readService = new PoiReadServiceImpl();
		final int pagesize = this.getPagesize();
		final String outputpath = sourcePath.substring(0,sourcePath.lastIndexOf("."))+"_%s"+sourcePath.substring(sourcePath.lastIndexOf("."));
		
		MapSheetHandlerCallback callback = new MapSheetHandlerCallback() {

			@Override
			public void readCellAfter(int rownum, short col, String formattedValue) throws Exception {
				
			}
			
			private int currRow = 0;
			private int splitNum = 1;
			private PoiWriteService writeService = null;
			private Map<Short, String> titleMap = null;
			private InsertRowCallback callback = new InsertRowCallback() {
				
				@Override
				public void rowCallback(Row row) {
					
				}
				
				@Override
				public void cellCallback(int rownum, Cell cell, Object value, Object data) {
					if(rownum == 0 ){//标题
						Font font = PoiStyleUtil.font(writeService.getWorkbook(), HSSFColor.BLACK.index);
						cell.setCellStyle(PoiStyleUtil.style(writeService.getWorkbook(), CellStyle.ALIGN_CENTER, HSSFColor.YELLOW.index, CellStyle.BORDER_THIN, HSSFColor.BLACK.index, font));
					}
					if(value != null){								
						cell.setCellValue(value+"");
					}
				}
			};
			@Override
			public void readRowafter(int rownum, Map<Short, String> rowMap) throws Exception {
				if(currRow==0){					
					writeService = PoiWriteServiceFactory.getInstance();
					if(titleMap==null&&rownum==0){
						titleMap = rowMap;
					}
					if(titleMap !=null && splitNum > 1){
						insertRow(titleMap);
						currRow++;
					}
				}
				
				insertRow(rowMap);
				
				if(currRow == pagesize){					
					writeService.output(String.format(outputpath, splitNum++));
					currRow=-1;
				}
				currRow++;
			}
			
			private void insertRow(Map<Short, String> rowMap) throws Exception{
				Iterator<Short> its = rowMap.keySet().iterator();
				while(its.hasNext()){
					short col = its.next();
					String formattedValue = rowMap.get(col);
					writeService.insertCell(sheetnamePattern, currRow, col, formattedValue,callback);
				}
			}

			@Override
			public int getCurrRow() {
				return currRow;
			}
			@Override
			public int getSplitNum() {
				return splitNum;
			}
			@Override
			public PoiWriteService getWriteService() {
				return writeService;
			}
		};
		
		try {
			readService.read(sourcePath, sheetnamePattern, new ReadSheetHandlerMapImpl(callback));
			
			if(callback.getWriteService() != null && callback.getCurrRow() > 0 && callback.getCurrRow() <= this.getPagesize()){
				callback.getWriteService().output(String.format(outputpath, callback.getSplitNum()));
			}
						
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	
}
