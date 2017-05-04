package ringutils.number;

import java.text.NumberFormat;

public class ProgrssUtil {
	
	/**
	 * 百分比，保留2位数
	 * @param cur	当前数
	 * @param counts	总数
	 * @return 
	 * @author ring
	 * @date 2017年3月16日 上午10:10:28
	 * @version V1.0
	 */
	public static String progrss(double cur,double counts){
		return progrss(cur, counts, 2);
	}
	
	/**
	 * 百分比，保留2位数
	 * @param cur	当前数
	 * @param counts	总数
	 * @return 
	 * @author ring
	 * @date 2017年3月16日 上午10:10:28
	 * @version V1.0
	 */
	public static String progrss(double cur,double counts,int newValue){
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(newValue);
		return nf.format(cur/counts);
	}
}
