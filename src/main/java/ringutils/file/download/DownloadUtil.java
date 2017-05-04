package ringutils.file.download;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadUtil {
	private static Logger log = LoggerFactory.getLogger(DownloadUtil.class);
	
	/**
	 * 文件下载<br/>
	 * 若报错：getOutputStream() has already been called for this response<br/>	
	 * 则需要添加以下两行代码：<br/>
	 	out.clear();<br/>
		out = pageContext.pushBody();<br/>
	 * @param response
	 * @param workbook	POI工作薄
	 * @param filename	文件名
	 * @throws Exception 
	 * @author ring
	 * @date 2017年3月31日 下午4:11:24
	 * @version V1.0
	 */
	public void download(HttpServletResponse response,Workbook workbook,String filename) throws Exception{
		try {
			//1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
			response.setContentType("multipart/form-data");  
			//response.setContentType("application/x-msdownload");  
			//2.设置文件头：最后一个参数是设置下载文件名(new String(filename.getBytes("utf-8"), "ISO-8859-1")解决中文被过滤问题)  
			response.setHeader("Content-Disposition", "attachment;fileName="+new String(filename.getBytes("utf-8"), "ISO-8859-1"));
			response.setCharacterEncoding("utf-8");
			ServletOutputStream os = response.getOutputStream();
			workbook.write(os);
			
			//注意看以下几句的使用  
			os.flush();  
			os.close();  
			os=null;  
			response.flushBuffer();  
			log.info("file download success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("下载失败："+e.getMessage(),e);
			throw new Exception("下载失败",e);
		}		
	}
	
	/**
	 * 文件下载
	 * @param response
	 * @param pathname	文件全路径
	 * @param filename	文件名
	 * @author ring
	 * @date 2017年3月31日 下午4:06:16
	 * @version V1.0
	 * @throws Exception 
	 */
	public void download(HttpServletResponse response,String pathname,String filename) throws Exception{
		try {
			//1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
			response.setContentType("multipart/form-data");  
			//2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)  
			response.setHeader("Content-Disposition", "attachment;fileName="+filename);
			response.setCharacterEncoding("utf-8");
			
			File file = new File(pathname);
			ServletOutputStream out = null;  
			if(file.exists()){
				log.error("file not fond!");
			}else{
				FileInputStream inputStream = new FileInputStream(file);  
				  
			    //3.通过response获取ServletOutputStream对象(out)  
			    out = response.getOutputStream();  
  
			    int b = 0;  
			    byte[] buffer = new byte[512];  
			    while (b != -1){  
			        b = inputStream.read(buffer);  
			        //4.写到输出流(out)中  
			        out.write(buffer,0,b);  
			    }  
			    inputStream.close();  
			    out.close();  
			    out.flush();  
				log.info("file download success");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("下载失败："+e.getMessage(),e);
			throw new Exception("下载失败",e);
		}
	}
}
