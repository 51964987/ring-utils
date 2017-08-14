package ringutils.result;

import java.io.Serializable;

/**
 * 返回对象  
 * @author ring
 * @date 2017年7月20日 上午9:50:00
 * @version V1.0
 */
public class ResultData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Object param;		//请求的参数
	private Object data;		//返回的数据
	private boolean success;	//是否成功
	private String msg;			//提示信息
	
	public Object getParam() {
		return param;
	}
	public void setParam(Object param) {
		this.param = param;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
