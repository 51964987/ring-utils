package ringutils.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	
	/**
	 * 下划线转驼峰
	 * @param input
	 * @return 
	 * @author ring
	 * @date 2017年5月13日 00:30:38
	 * @version V1.0
	 */
	public static String underline2capitalize(String input){
		String regex = "(.*?)_(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input.toLowerCase());
		while(m.find()){
			input = m.group(1)+StringUtils.capitalize(m.group(2));
			m = p.matcher(input);
		}
		return input;
	}
	
	/**
	 * 驼峰转下划线
	 * @param input
	 * @return 
	 * @author ring
	 * @date 2017年5月13日 00:35:23
	 * @version V1.0
	 */
	public static String capitalize2underline(String input){
		String regex = "(.*?)([A-Z].*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		while(m.find()){
			input = m.group(1)+"_"+StringUtils.uncapitalize(m.group(2));
			m = p.matcher(input);
		}
		return input;
	}
}
