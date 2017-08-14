package ringutils.jdbc.callback;

import java.util.List;

/**
 * 用于查询本地数据库时回调
 * @author ring
 * @date 2017年3月31日 下午2:12:00
 * @version V1.0
 */
public interface SQLCallback<T> {
	
	/**
	 * 查询SQL列字段
	 * @param fields 
	 * @author ring
	 * @date 2017年8月9日 下午4:18:19
	 * @version V1.0
	 */
	void setFields(String[] fields);
	
	/**
	 * 10000条为一批次返回数据
	 * @param rowstart	开始行 
	 * @param list 		数据集合
	 * @author ring
	 * @date 2017年3月31日 下午3:07:09
	 * @version V1.0
	 * @throws Exception 
	 */
	void run(List<T> list) throws Exception;
	
}
