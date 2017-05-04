package ringutils.date;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 时间操作工具  
 * @author ring
 * @date 2017年4月6日 下午3:18:28
 * @version V1.0
 */
public class DateUtil {
	
	/**
	 * 将yyyy-MM-dd HH:mm:dd字符串转换为日期类型
	 * @param dateStr
	 * @return
	 * @throws ParseException 
	 * @author ring
	 * @date 2017年4月18日 下午11:22:57
	 * @version V1.0
	 */
	public static Date parseDatetime(String dateStr) throws ParseException{
		return DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm:dd");
	}
	
	/**
	 * 将yyyyMMdd字符串转换为日期类型
	 * @param dateStr
	 * @return
	 * @throws ParseException 
	 * @author ring
	 * @date 2017年4月18日 下午11:22:57
	 * @version V1.0
	 */
	public static Date parseYyyyMMdd(String dateStr) throws ParseException{
		return DateUtils.parseDate(dateStr, "yyyyMMdd");
	}
	
	/**
	 * 将日期类型转换为yyyyMMdd字符串
	 * @param date
	 * @return 
	 * @author ring
	 * @date 2017年4月6日 下午3:24:54
	 * @version V1.0
	 */
	public static String formatYyyyMMdd() {
		return formatYyyyMMdd(new Date());
	}
	
	/**
	 * 将日期类型转换为 yyyyMMdd字符串
	 * @param date
	 * @return 
	 * @author ring
	 * @date 2017年4月6日 下午3:24:54
	 * @version V1.0
	 */
	public static String formatYyyyMMdd(Date date) {
		if(date == null) {return "";}
		return DateFormatUtils.format(date,"yyyyMMdd");
	}
	
	/**
	 * 将日期类型转换为yyyy-MM-dd HH:mm:ss字符串
	 * @param date
	 * @return 
	 * @author ring
	 * @date 2017年4月6日 下午3:24:54
	 * @version V1.0
	 */
	public static String formatDatetime() {
		return formatDatetime(new Date());
	}
	
	/**
	 * 将日期类型转换为yyyy-MM-dd HH:mm:ss字符串
	 * @param date
	 * @return 
	 * @author ring
	 * @date 2017年4月6日 下午3:24:54
	 * @version V1.0
	 */
	public static String formatDatetime(Date date) {
		if(date == null) {return "";}
		return DateFormatUtils.format(date,"yyyy-MM-dd HH:mm:ss");
	}
	
	
}