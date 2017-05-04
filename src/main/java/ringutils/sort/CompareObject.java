package ringutils.sort;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompareObject {
	
	private static Logger log = LoggerFactory.getLogger(CompareObject.class);
	
	public static int compare(Object v1,Object v2){
		int result = 0;
		if(v1 instanceof String){
			result = v1.toString().compareTo(v2.toString());
		}else if(v1 instanceof Date){
			long time = ((Date)v1).getTime() - ((Date)v2).getTime();
			if(time > 0){
				result = 1;
			}else if(time < 0){
				result = -1;
			}else{
				result = 0;
			}
		}else if(v1 instanceof Integer){
			result = ((Integer)v1) - ((Integer)v2);
		}else{
			result = v1.toString().compareTo(v2.toString());
			log.warn("不可识别的对象类型，转换字符串比较返回...");
		}
		return result;
	}
	
}
