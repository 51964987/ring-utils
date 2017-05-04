package ringutils.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SortUtil {
	/**
	 * 排序
	 * @param list
	 * @param comparators 
	 * @author ring
	 * @date 2017年3月16日 上午11:05:04
	 * @version V1.0
	 */
	public static<T> void sort(List<T> list,final List<Comparator<T>> comparators){
		if(comparators.isEmpty()){
			throw new RuntimeException("comparators is empty.");
		}
		Comparator<T> comparator = new Comparator<T>() {
			public int compare(T o1, T o2) {
				for(Comparator c : comparators){
					if(c.compare(o1, o2)>0){
						return 1;
					}else if(c.compare(o1, o2)<0){
						return -1;
					}
				}
				return 0;
			}
		};
		Collections.sort(list, comparator);
	}
}
