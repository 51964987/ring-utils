# 阿环工具类
## 1.快速生成JavaBean文件
```
JavaBeanHelper.process("utf-8", "ring.system.entity.user", "/ftl/annotationBean.ftl", "rsys_user_info", "rsys_user_login_log");
```
## 2.导出EXCEL文件
### 2.1根据自己需要选择TYPE_HSSF，TYPE_XSSF，TYPE_SXSSF
```
PoiExcelService excelService = PoiExcelServiceFactory.getInstance(PoiExcelService.TYPE_XSSF);
```
### 2.2分页导出，即根据pagesize分成多个sheet页
```
excelService.insertPage(...);
```
### 2.3插入一行数据
```
excelService.insertRow(...);
```
### 2.4导出，根据TYPE_HSSF，TYPE_XSSF，TYPE_SXSSF不同，输出的扩展名是不一样的哦
```
excelService.output(...);
```
### 2.5获取或更改导出的XLSX文件属性
```
CoreProperties cP = excelService.getXssfInformation();
cP.setCreator("ggi");
cP.setDescription("备注");
cP.setKeywords("标记");
...
```

### 2.6获取或更改导出的XLS文件属性
```
SummaryInformation suminfoInformation = excelService.getHssfInformation();
suminfoInformation.setComments("备注");// 备注
suminfoInformation.setAuthor("java");// 作者
suminfoInformation.setKeywords("12345678");
...
```
### 2.7获取或更改导出的XLS文件自定义属性
```
DocumentSummaryInformation docmentIfo = excelService.getHssfDocumentInformation();
CustomProperties customProperties = new CustomProperties();
customProperties.put("IP", "127.0.0.1");
docmentIfo.setCustomProperties(customProperties);
...
```

## 数据表生成EXCEL文件，作为数据结构文件
```
JSONObject tableMap = new JSONObject();
tableMap.put("rsys", "环环工作室");
tableMap.put("rsys_log", "系统日志");

PoiExcelService excelService = PoiExcelServiceFactory.getInstance(PoiExcelService.TYPE_XSSF);
JDBCUtil.initPorperties(JDBCUtil.getPropertiesInputStream("jdbc.properties"));

excelService.insertPage("日志", 
		JDBCMetaHelper.listColumns("rsys_log"), 
		Table2ExcelCallback.fields, Table2ExcelCallback.titls, 
		new Table2ExcelCallback(excelService, tableMap));

excelService.output("d:/table.xlsx");
```