package ringutils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Util {
	private static Logger log = LoggerFactory.getLogger(MD5Util.class);
	
	/**
	 * 加密，位移3位，即后三位移到前三位
	 * @param inStr
	 * @return 
	 * @author ring
	 * @date 2017年3月16日 上午10:20:43
	 * @version V1.0
	 */
	public static String encryptStr3Move(String inStr){
		if(inStr==null||"".equals(inStr)){
			return "";
		}
		for(int i=0;i<3;i++){
			inStr = inStr.substring(inStr.length()-1, inStr.length()) + inStr.substring(0,inStr.length()-1);
		}
		return encrypt(inStr);
	}
		
	/**
	 * 加密
	 * @param inStr
	 * @return 
	 * @author ring
	 * @date 2017年3月16日 上午10:20:43
	 * @version V1.0
	 */
	public static String encrypt(String inStr){
		MessageDigest md5 = null;
		try {
			
			if(inStr==null||"".equals(inStr)){
				return "";
			}
			
			md5 = MessageDigest.getInstance("MD5");
			
			char[] charArray = inStr.toCharArray();
			byte[] byteArray = new byte[charArray.length];
			
			for(int i=0;i<charArray.length;i++){
				byteArray[i] = (byte)charArray[i];
			}
			byte[] md5Bytes = md5.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();
			for(int i=0;i<md5Bytes.length;i++){
				int val = ((int)md5Bytes[i]) & 0xff;
				if(val < 16){
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			throw new RuntimeException("MD5加密失败", e);
		}
	}
}
