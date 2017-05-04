package ringutils.poi.impl;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import ringutils.poi.AbstractWorkBook;
import ringutils.poi.PoiExcelService;

public class SXSSFWorkbookImpl extends AbstractWorkBook implements PoiExcelService{
	
	public SXSSFWorkbookImpl(){
		super.workbook = this.getInstance();
	}
	
	@Override
	public Workbook getInstance() {
		return this.getInstanceFactory(PoiExcelService.TYPE_SXSSF);
	}

	@Override
	public void dispose() {
		((SXSSFWorkbook)this.getWorkbook()).dispose();
	}
	
}
