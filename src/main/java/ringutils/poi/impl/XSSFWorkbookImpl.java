package ringutils.poi.impl;

import org.apache.poi.ss.usermodel.Workbook;

import ringutils.poi.AbstractWorkBook;
import ringutils.poi.PoiExcelService;

public class XSSFWorkbookImpl extends AbstractWorkBook implements PoiExcelService{

	public XSSFWorkbookImpl(){
		super.workbook = this.getInstance();
	}
	
	@Override
	public Workbook getInstance() {
		return this.getInstanceFactory(PoiExcelService.TYPE_XSSF);
	}

	@Override
	public void dispose() {
		//no doing...
	}
	
}
