# ring-utils 阿环工具类
## 快速生成JavaBean文件

```
ProcessFile processFile = new ProcessFile();
processFile.initConfig("utf-8");

//模板参数
Map<String, Object> data = new HashMap<String, Object>();
data.put("tableName", "rsys_user");		//表名
data.put("clsName", StringUtils.capitalize(StringUtil.underline2capitalize(data.get("tableName")+"")));			//JavaBean名称
data.put("package", "ring.system.entity.test");	//生成JavaBean文件所在目录

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

//根据模板生成JavaBean文件,带注解
processFile.templateProcess(data , "/ftl/annotationBean.ftl",outputPath);
//根据模板生成JavaBean文件
//processFile.templateProcess(data , "/ftl/bean.ftl",outputPath);
```


