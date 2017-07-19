package ringutils.poi.write.impl;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

import com.alibaba.fastjson.JSONObject;

import ringutils.poi.write.InsertRowCallback;
import ringutils.poi.write.PoiWriteService;
import ringutils.poi.write.style.PoiStyleUtil;

/**
 * 用于数据结构导出
 *  
 * @author ring
 * @date 2017年7月16日 下午1:44:28
 * @version V1.0
 */
public class Table2ExcelCallback implements InsertRowCallback{

	public static String[] titls = {"数据库中文名","数据库英文名","表中文名","表英文名","栏位数","字段名称","字段注释","字段类型","字段长度","主键","备注"};
	public static String[] fields = {"TABLE_CAT_CHN","TABLE_CAT","TABLE_NAME_CHN","TABLE_NAME","COLUMN_INDEX","COLUMN_NAME","REMARKS","TYPE_NAME","COLUMN_SIZE","COLUMN_PK","备注"};
	private PoiWriteService excelService;
	/**
	 * {TABLE_CAT:xxx,tablename1:xxx,tablename2:xxx}
	 */
	private JSONObject tableMap;
	public Table2ExcelCallback(PoiWriteService excelService, JSONObject tableMap) {
		super();
		this.excelService = excelService;
		this.tableMap = tableMap;
	}
	public Table2ExcelCallback(PoiWriteService excelService) {
		super();
		this.excelService = excelService;
	}

	public void rowCallback(Row row) {
		
	}

	public void cellCallback(int rownum,Cell cell, Object value, Object data) {
		Font font = PoiStyleUtil.font(excelService.getWorkbook(), HSSFColor.BLACK.index);
		if(rownum == 0 ){//标题
			cell.setCellStyle(PoiStyleUtil.style(excelService.getWorkbook(), CellStyle.ALIGN_CENTER, HSSFColor.YELLOW.index, CellStyle.BORDER_THIN, HSSFColor.BLACK.index, font));
			cell.getSheet().createFreezePane(0, 1, 0, 1);//首行冻结
		}else{
			JSONObject o = (JSONObject) data;
			if(o.getIntValue("COLUMN_INDEX") == 1){//栏位 
				cell.setCellStyle(PoiStyleUtil.style(excelService.getWorkbook(), CellStyle.ALIGN_LEFT, HSSFColor.GREEN.index, CellStyle.BORDER_THIN, HSSFColor.BLACK.index, null));
			}else{
				cell.setCellStyle(PoiStyleUtil.style(excelService.getWorkbook(), CellStyle.ALIGN_LEFT, null, CellStyle.BORDER_THIN, HSSFColor.BLACK.index, null));
			}
			if(cell.getColumnIndex() == 0 && tableMap != null){//数据库中文名
				value = tableMap.get(o.getString("TABLE_CAT"));
			}
			if(cell.getColumnIndex() == 2 && tableMap != null){//表中文名
				value = tableMap.get(o.getString("TABLE_NAME"));
			}
		}
		value = value==null?"":value;
		cell.setCellValue(value+"");
	}

}
