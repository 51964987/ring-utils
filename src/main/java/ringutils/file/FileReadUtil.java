package ringutils.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReadUtil {
	
	private static Logger log = LoggerFactory.getLogger(FileReadUtil.class);
	
	/**
	 * 读取文件
	 * @param filename	文件路径名
	 * @param charset	字符集
	 * @return 
	 * @author ring
	 * @date 2017年3月17日 上午11:12:15
	 * @version V1.0
	 */
	public String read(String filename,String charset){
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		String tmpStr = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charset));
			while( (tmpStr = reader.readLine()) != null ){
				sb.append(tmpStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			throw new RuntimeException("读取文件失败", e);
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(),e);
				}
			}
		}
		return sb.toString();
	}
}
