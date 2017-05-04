package ringutils.file.upload;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import ringutils.file.FileDeleteUtil;

/**
 * 用于上传文件到本地
 * @author ring
 * @date 2017年3月29日 下午6:55:56
 * @version V1.0
 */
public class UploadUtil {

	private static Logger log = LoggerFactory.getLogger(UploadUtil.class);
	
	/**
	 * 不包含的类型，多个以｜分隔
	 * @param excludeFileType
	 * @return 
	 * @author ring
	 * @date 2017年3月29日 下午11:27:13
	 * @version V1.0
	 */
	public static final String REGEX_EXCLUDE(String excludeFileType){
		return "(?i).*\\.((?!"+excludeFileType+").)*";
	}
	
	/**
	 * 文档类型
	 */
	public static final String REGEX_DOC = "(?i).*\\.(?:doc|docx)";
	
	/**
	 * EXCEL类型
	 */
	public static final String REGEX_EXCEL = "(?i).*\\.(?:xls|xlsx)";
	
	/**
	 * 图片类型
	 */
	public static final String REGEX_IMG = "(?i).*\\.(?:jpg|jpeg|png|gif|bmp)";
	
	/**
	 * 文件最大值
	 */
	private long maxFileSize;
	/**
	 * 文件类型过滤
	 */
	private String  fileNameRegex;
	
	/**
	 * 获取文件
	 * @param request
	 * @return 
	 * @author ring
	 * @date 2017年4月1日 下午7:12:35
	 * @version V1.0
	 */
	public List<InputStream> listFile(HttpServletRequest request){
		List<InputStream> list = null;
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			
//			//设置是否将上传文件已临时文件的形式保存在磁盘的临界值（以字节为单位的int值），如果从没有调用该方法设置此临界值，将会采用系统默认值10KB
//			factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
//			//设置当上传文件尺寸大于setSizeThreshold方法设置的临界值时，将文件以临时文件形式保存在磁盘上的存放目录。
//			factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
			
			ServletFileUpload fu = new ServletFileUpload(factory);
			fu.setHeaderEncoding("utf-8");//解决中文问题
			List<?> items = fu.parseRequest(request);
			Iterator<?> it = items.iterator();
			list = new ArrayList<InputStream>();
			
			while(it.hasNext()){
				FileItem item = (FileItem) it.next();
				
				if(!item.isFormField()){//表单字段
					
					//过滤空文件
					if(item.getName()==null||"".equals(item.getName().trim())){
						continue;
					}
					
					//检验
					check(item);
					
					//输入流
					list.add(item.getInputStream());
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	/**
	 * 校验
	 * @param item
	 * @throws Exception 
	 * @author ring
	 * @date 2017年4月1日 下午7:14:14
	 * @version V1.0
	 */
	private void check(FileItem item) throws Exception{
		//验证文件大小
		if(this.maxFileSize>0&&this.maxFileSize<item.getSize()){
			throw new Exception("超过文件最大值");
		}
		
		//验证文件类型
		if(this.fileNameRegex!=null&&this.fileNameRegex.length()>0){
			if(!Pattern.matches(this.fileNameRegex, item.getName())){
				throw new Exception("文件类型不符合");
			}
		}
	}
	
	/**
	 * 上传文件
	 * @param request	请求
	 * @param uploadDir 目标上传的文件夹
	 * @author ring
	 * @date 2017年3月29日 下午3:20:03
	 * @version V1.0
	 * @return 
	 */
	public JSONObject upload(HttpServletRequest request,String uploadDir){
		
		//文件地址，用于异常时删除刚上传的文件
		List<String> paths = new ArrayList<String>();
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload fu = new ServletFileUpload(factory);
			fu.setHeaderEncoding("utf-8");//解决中文问题
			List<?> items = fu.parseRequest(request);
			Iterator<?> it = items.iterator();
			
			Map<String, List<Object>> rs = new HashMap<String, List<Object>>();
			
			while(it.hasNext()){
				FileItem item = (FileItem) it.next();
				
				//input name值
				String fieldName = item.getFieldName();
				List<Object> vals = rs.get(fieldName);
				if(vals==null){
					vals = new ArrayList<Object>();
				}
				
				if(item.isFormField()){//表单字段
					vals.add(item.getString("utf-8"));
				}else{//文件
					
					//过滤空文件
					if(item.getName()==null||"".equals(item.getName().trim())){
						continue;
					}
					
					//检验
					check(item);
					
					//文件信息					
					JSONObject fileinfo = new JSONObject();
					fileinfo.put("contentType", item.getContentType());
					fileinfo.put("size", item.getSize());//单位：字节
					fileinfo.put("filename", item.getName());
					String uuidname = UUID.randomUUID().toString()+item.getName().substring(item.getName().lastIndexOf("."));
					fileinfo.put("uuidname", uuidname);
					
					//创建目录
					File dirFile = new File(uploadDir);
					if(!dirFile.exists()){
						dirFile.mkdirs();
					}
					
					//写入本地
					item.write(new File(uploadDir+"\\"+uuidname));
					
					paths.add(uploadDir+"\\"+uuidname);
					vals.add(fileinfo);
				}
				
				//保存结果
				rs.put(fieldName, vals);
			}
			return JSONObject.parseObject(JSON.toJSONString(rs));
		} catch (Exception e) {
			e.printStackTrace();
			//上传失败，删除上传文件
			FileDeleteUtil.deleteByDir(paths);
			log.error("上传失败",e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	
	public long getMaxFileSize() {
		return maxFileSize;
	}
	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public String getFileNameRegex() {
		return fileNameRegex;
	}
	public void setFileNameRegex(String fileNameRegex) {
		this.fileNameRegex = fileNameRegex;
	}
	
	
}
