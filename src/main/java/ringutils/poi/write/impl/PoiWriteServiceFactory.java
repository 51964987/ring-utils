package ringutils.poi.write.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ringutils.poi.write.PoiWriteService;

/**
 * 创建PoiExcelService实例,Workbook有HSSFWorkbook,XSSFWorkbook实例。
 * @author ring
 * @date 2017年3月30日 上午9:31:36
 * @version V1.0
 */
public class PoiWriteServiceFactory{
	
	private static Logger log = LoggerFactory.getLogger(PoiWriteServiceFactory.class);
	
	/**
	 * 获取Workbook实例
	 * @param type
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午2:53:34
	 * @version V1.0
	 */
	public static PoiWriteService getInstance(){
		PoiWriteService service = new PoiWriteServiceImpl(); 
		service.setWorkbook(new SXSSFWorkbook(PoiWriteService.ROW_ACCESS));
		return service;
	}
	
	/**
	 * 获取Workbook实例
	 * @param type
	 * @return 
	 * @author ring
	 * @date 2017年3月14日 下午2:53:34
	 * @version V1.0
	 */
	public static PoiWriteService getInstance(String type){
		PoiWriteService service = new PoiWriteServiceImpl(); 
		//根据类型获取实例
		if(type==null||type.equalsIgnoreCase(PoiWriteService.TYPE_HSSF)){
			service.setWorkbook(new HSSFWorkbook());
		}else if(type.equalsIgnoreCase(PoiWriteService.TYPE_XSSF)){
			service.setWorkbook(new XSSFWorkbook());
		}else if(type.equalsIgnoreCase(PoiWriteService.TYPE_SXSSF)){
			service.setWorkbook(new SXSSFWorkbook(PoiWriteService.ROW_ACCESS));
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
	public static List<PoiWriteService> getPoiExcel(HttpServletRequest request) {
		List<PoiWriteService> list = new ArrayList<PoiWriteService>();
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
					
					list.add(getPoiExcelService(item.getName(), item.getInputStream()));
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		return list;
	}
	
	/**
	 * 获取工作薄
	 * @param filename	文件路径
	 * @return 
	 * @author ring
	 * @date 2017年3月15日 上午11:24:34
	 * @version V1.0
	 * @throws IOException 
	 */
	public static PoiWriteService getPoiExcelService(String filename) throws IOException {
		PoiWriteService service = null; 
		InputStream is = null;
		try {
			is = new FileInputStream(filename);  //建立输入流
			service = getPoiExcelService(filename, is);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		}finally{
			if(is!=null){
				is.close();
			}
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
	public static PoiWriteService getPoiExcelService(String filename,InputStream is) throws IOException{
		PoiWriteService service = new PoiWriteServiceImpl(); 
		filename = filename.toLowerCase();
		if(filename.endsWith("xls")) {
			service.setWorkbook(new HSSFWorkbook(is));
		} else if(filename.endsWith("xlsx")) {
			service.setWorkbook(new XSSFWorkbook(is));
		}else {
	    	throw new IOException("读取的不是excel文件");
	    }
		return service;
	}

}
