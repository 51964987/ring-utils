package ringutils.file;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.alibaba.fastjson.JSONObject;

/**
 * 可用于生成CSV等文件  
 * @author ring
 * @date 2017年3月17日 上午9:23:23
 * @version V1.0
 */
public class FileWriteUtil {
	
	/**
	 * 输出对象
	 */
	private BufferedWriter fileWriter;
	/**
	 * 字符集
	 */
	private String charset;
	/**
	 * 文件地址
	 */
	private String fullpath;
	/**
	 * 分割符
	 */
	private String symbol;
	/**
	 * 字符大小
	 */
	private int outputBufferSize = 1024;
	/**
	 * 保持在内存中10000行,超过行会刷新到磁盘
	 */
	private int rowaccess = 10000;
	
	private int curRowNum = 0;
	
	/**
	 * 
	 * @param fullpath	输出的文件地址
	 * @param charset	字符集
	 * @param symbol	分割符
	 * @throws Exception
	 */
	public FileWriteUtil(String fullpath, String charset, String symbol) throws Exception {
		super();
		this.fullpath = fullpath;
		this.charset = charset;
		this.symbol = symbol;
		this.fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullpath), charset), outputBufferSize);
	}
	
	/**
	 * 写数据
	 * @param data
	 * @throws IOException 
	 * @author ring
	 * @date 2017年3月16日 下午3:48:08
	 * @version V1.0
	 */
	public void write(String[] data) throws IOException{
		writeObject(data, data);
	}
	
	/**
	 * 写入数据
	 * @param data		String[]｜JSONObject
	 * @param target	字段或其他
	 * @throws IOException 
	 * @author ring
	 * @date 2017年3月17日 上午9:17:33
	 * @version V1.0
	 */
	private void writeObject(Object data,String[] target) throws IOException{
		if(target==null||target.length<=0){
			return;
		}
		StringBuffer dataStr = new StringBuffer();
		for(int i=0;i<target.length;i++){
			if(i>0){
				dataStr.append(this.symbol);
			}
			if(data instanceof JSONObject){				
				dataStr.append(((JSONObject)data).get(target[i]));
			}else if(data instanceof String[]){
				dataStr.append(((String[])data)[i]);
			}else{
				throw new IOException("未知类型");
			}
		}
		this.fileWriter.write(dataStr.toString());
		this.fileWriter.newLine();
		curRowNum+=1;
		if(curRowNum%rowaccess==0){
			this.fileWriter.flush();
		}
	}
	
	/**
	 * 写数据
	 * @param data
	 * @param dataField
	 * @throws IOException 
	 * @author ring
	 * @date 2017年3月16日 下午3:48:08
	 * @version V1.0
	 */
	public void write(JSONObject data,String[] dataField) throws IOException{
		writeObject(data, dataField);
	}
		
	/**
	 * 关闭
	 * @throws IOException 
	 * @author ring
	 * @date 2017年3月16日 下午3:54:27
	 * @version V1.0
	 */
	public void close() throws IOException{
		if(this.fileWriter!=null){
			this.fileWriter.close();
		}
	}
	public BufferedWriter getFileWriter() {
		return fileWriter;
	}
	public void setFileWriter(BufferedWriter fileWriter) {
		this.fileWriter = fileWriter;
	}
	public String getCharset() {
		return charset;
	}
	public String getFullpath() {
		return fullpath;
	}
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public int getOutputBufferSize() {
		return outputBufferSize;
	}
	public void setOutputBufferSize(int outputBufferSize) {
		this.outputBufferSize = outputBufferSize;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getRowaccess() {
		return rowaccess;
	}
	public void setRowaccess(int rowaccess) {
		this.rowaccess = rowaccess;
	}
	public int getCurRowNum() {
		return curRowNum;
	}
	public void setCurRowNum(int curRowNum) {
		this.curRowNum = curRowNum;
	}
	
}
