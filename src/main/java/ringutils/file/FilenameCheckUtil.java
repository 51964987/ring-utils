package ringutils.file;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameCheckUtil {
	
	/**
	 * 过滤文件名乱码，用于保障生成文件成功
	 * @param filename
	 * @return 
	 * @author ring
	 * @date 2017年3月22日 上午10:47:37
	 * @version V1.0
	 */
	public static String filterMessycode(String filename){
		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile("([^\\\\\\/\\:\\*\\?\"<>| 　])");
		Matcher m = p.matcher(filename);
		while(m.find()){
			sb.append(m.group(1));
		}
		return sb.toString();
	}
}
