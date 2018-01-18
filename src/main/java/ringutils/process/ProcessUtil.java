package ringutils.process;

import java.io.InputStreamReader;
import java.io.LineNumberReader;


public class ProcessUtil {

	public static String action(String value,String charset) throws Exception{
		Process ps = Runtime.getRuntime().exec(value);
		LineNumberReader br = new LineNumberReader(new InputStreamReader(ps.getInputStream(), charset));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while( (line = br.readLine()) != null ){
			sb.append(line).append("\n");
		}
		sb.deleteCharAt(sb.length()-1);
		int extVal = ps.waitFor();
		if(extVal != 0){
			sb.append("异常退出！！！");
		}
		return sb.toString();
	}
}
