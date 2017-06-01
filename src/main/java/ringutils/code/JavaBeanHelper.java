package ringutils.code;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import ringutils.jdbc.JDBCUtil;
import ringutils.jdbc.impl.JDBCMetaHelper;
import ringutils.string.StringUtil;

public class JavaBeanHelper {
	
	/**
	 * 生成javaBean文件
	 * @param charset	字符编码
	 * @param packageStr	目录路径
	 * @param templateName	模板路径名称，相对于src/java/resource目录下
	 * @param tableNames	数据表名
	 * @throws Exception 
	 * @author ring
	 * @date 2017年5月17日 下午10:40:35
	 * @version V1.0
	 */
	public static void process(String charset,String packageStr,String templateName,String... tableNames) throws Exception{
		
		//ProcessFile对象
		ProcessFile processFile = new ProcessFile();
		processFile.initConfig(charset);
		for(String tableName : tableNames){
			
			//模板参数
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("tableName", tableName);		//表名
			data.put("clsName", StringUtils.capitalize(StringUtil.underline2capitalize(tableName)));			//JavaBean名称
			data.put("package", packageStr);	//生成JavaBean文件所在目录
			
			//JDBC初始化参数配置
			JDBCUtil.initPorperties(JDBCUtil.getPropertiesInputStream("jdbc.properties"));
			
			//表注釋
			List<JSONObject> listTables = JDBCMetaHelper.listTables(JSONObject.class, data.get("tableName")+"");
			data.put("tableRemark", listTables.get(0).getString("REMARKS"));
			
			//主鍵
			List<JSONObject> listPromaryKeys = JDBCMetaHelper.listPromaryKeys(JSONObject.class, data.get("tableName")+"");
			if(listPromaryKeys!=null&&listPromaryKeys.size()>0){
				for(JSONObject col : listPromaryKeys){//下划线转驼峰
					col.put("name", StringUtil.underline2capitalize(col.getString("COLUMN_NAME")));
				}
			}
			data.put("pkeys", listPromaryKeys);
			
			//列
			List<JSONObject> listColumns = JDBCMetaHelper.listColumns(JSONObject.class, data.get("tableName")+"");
			if(listColumns!=null&&listColumns.size()>0){
				for(JSONObject col : listColumns){//下划线转驼峰
					col.put("name", StringUtil.underline2capitalize(col.getString("COLUMN_NAME")));
				}
			}
			data.put("cols", listColumns);
			
			//对应类型
			data.put("types", JDBCMetaHelper.mysqlType2Map());
			
			String packageFold = data.get("package").toString().replace(".", File.separator);
			String outputPath =processFile.getJavaPath()+File.separator+packageFold+File.separator+data.get("clsName")+".java";
			
			//根据模板生成JavaBean文件
			processFile.templateProcess(data , templateName,outputPath);
		}
	}
}
