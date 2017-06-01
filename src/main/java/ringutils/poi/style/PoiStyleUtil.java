package ringutils.poi.style;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

public class PoiStyleUtil {
	
	/**
	 * 粗体白色
	 * @param workbook
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午3:33:39
	 * @version V1.0
	 */
	public static Font boldFont(Workbook workbook){
		return font(workbook, (short)1);
	}
	
	/**
	 * 蓝色前景色，居中,白色细线边框
	 * @param workbook
	 * @param font
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午3:33:59
	 * @version V1.0
	 */
	public static CellStyle blueStyle(Workbook workbook,Font font){
		return style(workbook, CellStyle.ALIGN_CENTER, HSSFColor.BLUE_GREY.index, HSSFCellStyle.BORDER_THIN, HSSFColor.WHITE.index, font);
	}
	
	/**
	 * 字段
	 * @param workbook
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午3:33:39
	 * @version V1.0
	 */
	public static Font font(Workbook workbook,Short color){
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		if(color != null){
			font.setColor(color);
		}
		return font;
	}
	
	/**
	 * 样式
	 * @param workbook
	 * @param alignment			如CellStyle.ALIGN_CENTER
	 * @param foregroundColor	如HSSFColor.WHITE.index
	 * @param border			如HSSFCellStyle.BORDER_THIN
	 * @param borderColor		如HSSFColor.WHITE.index
	 * @param font
	 * @return 
	 * @author ring
	 * @date 2017年5月31日 下午6:10:06
	 * @version V1.0
	 */
	public static CellStyle style(Workbook workbook,Short alignment,Short foregroundColor,Short border,Short borderColor,Font font){
		CellStyle style = workbook.createCellStyle();
		
		if(alignment != null){			
			style.setAlignment(alignment);
		}
		
		if(foregroundColor != null){			
			style.setFillForegroundColor(foregroundColor);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		}
		
		if(border != null){				
			style.setBorderTop(border);
			style.setBorderBottom(border);
			style.setBorderLeft(border);
			style.setBorderRight(border);
		}
		
		if(borderColor != null){			
			style.setTopBorderColor(borderColor);
			style.setBottomBorderColor(borderColor);
			style.setLeftBorderColor(borderColor);
			style.setRightBorderColor(borderColor);
		}
		
		if(font!=null){			
			style.setFont(font);
		}
		return style;
	}
}
