package ringutils.poi.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

public class PoiStyleUtil {
	
	/**
	 * 默认字体，加粗
	 * @param workbook
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午3:33:39
	 * @version V1.0
	 */
	public static Font defaultFont(Workbook workbook){
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setColor((short)1);
		return font;
	}
	
	/**
	 * 默认单元格风格，前景色为蓝色，居中
	 * @param workbook
	 * @param font
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午3:33:59
	 * @version V1.0
	 */
	public static CellStyle defaultStyle(Workbook workbook,Font font){
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor((short)54);//HSSFColor.BLUE_GREY.index
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		if(font!=null){			
			style.setFont(font);
		}
		return style;
	}
}
