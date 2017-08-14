package ringutils.download;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ringutils.result.ResultData;

public interface DownloadService {
	
	/**
	 * 下载
	 * @param request
	 * @param response
	 * @return 
	 * @author ring
	 * @date 2017年7月20日 上午9:59:20
	 * @version V1.0
	 */
	ResultData download(HttpServletRequest request,HttpServletResponse response)throws Exception;
}
