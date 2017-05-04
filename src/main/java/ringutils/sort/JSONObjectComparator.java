package ringutils.sort;

import java.util.Comparator;

import com.alibaba.fastjson.JSONObject;

public class JSONObjectComparator implements Comparator<JSONObject> {
	/**
	 * 字段
	 */
	private String field;
	/**
	 * 是否升序
	 */
	private boolean asc;
	
	public JSONObjectComparator(String field, boolean asc) {
		super();
		this.field = field;
		this.asc = asc;
	}

	public int compare(JSONObject o1, JSONObject o2) {
		Object v1 = o1.get(this.field);
		Object v2 = o2.get(this.field);
		int result = CompareObject.compare(v1, v2);
		if(!this.asc){
			result = -result;
		}
		return result;
	}

}
