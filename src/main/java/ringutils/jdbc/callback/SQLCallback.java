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
	 * 10000条为一批次返回数据
	 * @param list 
	 * @author ring
	 * @date 2017年3月31日 下午3:07:09
	 * @version V1.0
	 */
	void run(List<T> list);
}
