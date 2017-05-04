package ringutils.poi.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ringutils.poi.PoiExcelService;

/**
 * 创建PoiExcelService实例,Workbook有HSSFWorkbook,XSSFWorkbook实例。
 * @author ring
 * @date 2017年3月30日 上午9:31:36
 * @version V1.0
 */
public class PoiExcelFactory{
	
	private static Logger log = LoggerFactory.getLogger(PoiExcelFactory.class);
	
	/**
	 * 获取工作薄
	 * @param filename	文件路径
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 上午11:24:34
	 * @version V1.0
	 */
	public static PoiExcelService getPoiExcel(String filename) {
		PoiExcelService service = null; 
		InputStream is = null;
		try {
			is = new FileInputStream(filename);  //建立输入流
			service = getPoiExcel(filename, is);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return service;
	}
	
	/**
	 * 获取工作薄
	 * @param filename	文件路径
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 上午11:24:34
	 * @version V1.0
	 */
	public static PoiExcelService getPoiExcel(HttpServletRequest request) {
		PoiExcelService service = null; 
		try {
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload fu = new ServletFileUpload(factory);
			fu.setHeaderEncoding("utf-8");//解决中文问题
			List<?> items = fu.parseRequest(request);
			Iterator<?> it = items.iterator();
			
			while(it.hasNext()){
				FileItem item = (FileItem) it.next();
								
				if(!item.isFormField()){//文件

					//过滤空文件
					if(item.getName()==null||"".equals(item.getName().trim())){
						continue;
					}
					
					//验证文件类型
					if(!Pattern.matches("(?i).*\\.(?:xls|xlsx)", item.getName())){
						throw new Exception("仅支持xls或xlsx文件");
					}
					service = getPoiExcel(item.getName(), item.getInputStream());
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		return service;
	}
	
	/**
	 * 根据文件名生成对应的Workbook服务实例
	 * @param filename	文件名
	 * @param is		输入流
	 * @return
	 * @throws IOException 
	 * @author ring
	 * @date 2017年3月30日 上午11:03:52
	 * @version V1.0
	 */
	public static PoiExcelService getPoiExcel(String filename,InputStream is) throws IOException{
		PoiExcelService service = null;
		if(filename.endsWith("xls")) {
			service = new HSSFWorkbookImpl();
			service.setWorkbook(new HSSFWorkbook(is));
		} else if(filename.endsWith("xlsx")) {
			service = new XSSFWorkbookImpl();
			service.setWorkbook(new XSSFWorkbook(is));
		}else {
	    	throw new IOException("读取的不是excel文件");
	    }
		return service;
	}

}
