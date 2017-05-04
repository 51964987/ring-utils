package ringutils.poi.impl;

import org.apache.poi.ss.usermodel.Workbook;

import ringutils.poi.AbstractWorkBook;
import ringutils.poi.PoiExcelService;

public class HSSFWorkbookImpl extends AbstractWorkBook implements PoiExcelService{

	public HSSFWorkbookImpl(){
		super.workbook = this.getInstance();
	}
	
	@Override
	public Workbook getInstance() {
		return this.getInstanceFactory(PoiExcelService.TYPE_HSSF);
	}

	@Override
	public void dispose() {
		//no doing...
	}
	
}
