package ringutils.poi.read;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ringutils.poi.read.impl.ReadSheetHandler;

public interface PoiReadService {
	
	/**
	 * 读取EXCEL文件
	 * @param path
	 * @param sheetnamePattern
	 * @param sheetContentsHandler
	 * @throws Exception 
	 * @author ring
	 * @date 2017年7月18日 上午11:17:53
	 * @version V1.0
	 */
	void read(String path,String sheetnamePattern,ReadSheetHandler sheetContentsHandler)throws Exception;
	
	/**
	 * 删除文件
	 * @param listFilePath
	 * @throws Exception 
	 * @author ring
	 * @date 2017年7月18日 上午11:23:15
	 * @version V1.0
	 */
	void deleteListFilePath(List<String> listFilePath)throws Exception;
	
	/**
	 * 装上传的文件保存在（临时）路径
	 * @param request
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年7月18日 上午11:23:24
	 * @version V1.0
	 */
	List<String> listFilePath(HttpServletRequest request)throws Exception;
	
	/**
	 * （临时）路径
	 * @return 
	 * @author ring
	 * @date 2017年7月18日 上午11:23:51
	 * @version V1.0
	 */
	List<String> getListFilePath();
}
