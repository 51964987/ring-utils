package ringutils.ip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: IPUtil
 * @version 1.0
 * @Desc: Ip工具类
 * @author huaping hu
 * @date 2016年6月1日下午5:26:56
 * @history v1.0
 *
 */
public class IPUtil {

	public final static Logger LOGGER = Logger.getLogger(IPUtil.class);

	/**
	 * 
	 * 描述：获取IP地址
	 * 
	 * @author huaping hu
	 * @date 2016年6月1日下午5:25:44
	 * @param request
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "nuknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "nuknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "nuknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 
	 * 描述：获取IP+[IP所属地址]
	 * 
	 * @author huaping hu
	 * @date 2016年6月1日下午6:01:09
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public static String getIpBelongAddress(HttpServletRequest request) throws Exception {
		String ip = getIpAddress(request);
		String belongIp = getIPbelongAddress(ip);
		return ip + belongIp;
	}

	/**
	 * 
	 * 描述：获取IP所属地址
	 * 
	 * @author huaping hu
	 * @date 2016年6月1日下午5:59:43
	 * @param ip
	 * @return
	 * @throws Exception 
	 */
	public static String getIPbelongAddress(String ip) throws Exception {
		String ipAddress = "[]";
		try {
			// 淘宝提供的服务地址
			String context = call("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
			JSONObject fromObject = JSONObject.parseObject(context);
			String code = fromObject.getString("code");
			if (code.equals("0")) {
				JSONObject jsonObject = fromObject.getJSONObject("data");
				ipAddress = "[" + jsonObject.get("country") + "/" + jsonObject.get("city") + "]";
			}
		} catch (Exception e) {
			LOGGER.error("获取IP所属地址出错", e);
			throw e;
		}
		return ipAddress;
	}

	/**
	 * 
	 * 描述：获取Ip所属地址
	 * 
	 * @author huaping hu
	 * @date 2016年6月1日下午5:38:55
	 * @param urlStr
	 * @return
	 * @throws Exception 
	 */
	public static String call(String urlStr) throws Exception {

		try {

			URL url = new URL(urlStr);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

			httpCon.setConnectTimeout(3000);
			httpCon.setDoInput(true);
			httpCon.setRequestMethod("GET");

			int code = httpCon.getResponseCode();

			if (code == 200) {
				return streamConvertToSting(httpCon.getInputStream());
			}
		} catch (Exception e) {
			LOGGER.error("获取IP所属地址出错", e);
			throw e;
		}
		return null;
	}

	/**
	 * 
	 * 描述：将InputStream转换成String
	 * 
	 * @author huaping hu
	 * @date 2016年6月1日下午5:51:53
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public static String streamConvertToSting(InputStream is) throws IOException {

		String tempStr = "";
		try {

			if (is == null)
				return null;
			ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
			byte[] by = new byte[1024];
			int len = 0;
			while ((len = is.read(by)) != -1) {
				arrayOut.write(by, 0, len);
			}
			tempStr = new String(arrayOut.toByteArray());

		} catch (IOException e) {
			LOGGER.error("将InputStream转换成String出错", e);
			throw e;
		}
		return tempStr;
	}

	public static void main(String[] args) throws Exception {

		String context = call("http://ip.taobao.com/service/getIpInfo.php?ip=120.192.182.1");

		JSONObject fromObject = JSONObject.parseObject(context);
		JSONObject jsonObject = fromObject.getJSONObject("data");
		System.out.println(fromObject);
		System.err.println(jsonObject.get("city"));
	}
}