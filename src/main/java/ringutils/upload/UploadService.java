package ringutils.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ringutils.result.ResultData;

public interface UploadService {
	
	/**
	 * 上传
	 * @param request
	 * @param response
	 * @return 
	 * @author ring
	 * @date 2017年7月20日 上午9:59:20
	 * @version V1.0
	 */
	ResultData upload(HttpServletRequest request,HttpServletResponse response)throws Exception;
}
