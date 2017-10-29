package ringutils.poi.read;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;

public interface ReadSheetContentsHandler extends SheetContentsHandler{
	int getDataCount();
	String getSheetname();
	void setSheetname(String sheetname);
}
