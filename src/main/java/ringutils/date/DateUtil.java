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
	public static Date yyyyMMddHHmmss(String yyyyMMddHHmmss) throws ParseException{
		return DateUtils.parseDate(yyyyMMddHHmmss, "yyyyMMddHHmmss");
	}

	/**
	 * 将日期类型转换为yyyyMMddHHmmss字符串
	 * @param date
	 * @return 
	 * @author ring
	 * @date 2017年4月6日 下午3:24:54
	 * @version V1.0
	 */
	public static String yyyyMMddHHmmss() {
		return DateFormatUtils.format(new Date(),"yyyyMMddHHmmss");
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
	public static Date yyyyMMdd(String yyyyMMdd) throws ParseException{
		return DateUtils.parseDate(yyyyMMdd, "yyyyMMdd");
	}
	
	/**
	 * 将日期类型转换为 yyyyMMdd字符串
	 * @param date
	 * @return 
	 * @author ring
	 * @date 2017年4月6日 下午3:24:54
	 * @version V1.0
	 */
	public static String yyyyMMdd() {
		return DateFormatUtils.format(new Date(),"yyyyMMdd");
	}
	
	/**
	 * 将字符串转换为日期类型
	 * @param dateStr	yyyyMMddHHmmss|yyyyMMdd
	 * @return
	 * @throws ParseException 
	 * @author ring
	 * @date 2017年10月25日 下午8:17:44
	 * @version V1.0
	 */
	public static Date parseDate(String dateStr) throws ParseException{
		return DateUtils.parseDate(dateStr, new String[]{"yyyyMMddHHmmss","yyyyMMdd"});
	}
}