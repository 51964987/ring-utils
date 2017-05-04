package ringutils.number;

import java.math.BigDecimal;

public class NumberUtil {
	
	/**
	 * 四舍五入，保留2位数
	 * @param obj
	 * @return 
	 * @author ring
	 * @date 2017年3月17日 上午11:20:05
	 * @version V1.0
	 */
	public static String round(Object obj){
		return round(obj,2);
	}
	
	/**
	 * 四舍五入
	 * @param obj
	 * @param newScale	保留位数
	 * @return 
	 * @author ring
	 * @date 2017年3月17日 上午11:20:05
	 * @version V1.0
	 */
	public static String round(Object obj,int newScale){
		if(obj!=null&&obj.toString().trim().equals("")){
			return "";
		}
		BigDecimal bd = new BigDecimal(obj+"");
		BigDecimal bds = bd.setScale(newScale, BigDecimal.ROUND_HALF_UP);
		return bds.toPlainString();
	}
}
