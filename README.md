# 阿环工具类
## 1.快速生成JavaBean文件
```
JavaBeanHelper.process("utf-8", "ring.system.entity.user", "/ftl/annotationBean.ftl", "rsys_user_info", "rsys_user_login_log");
```
## 2.导出EXCEL文件
### 2.1根据自己需要选择TYPE_HSSF，TYPE_XSSF，TYPE_SXSSF
```
PoiExcelService excelService = PoiExcelServiceFactory.getInstance();
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
####EXAMPLE1:
	CoreProperties cp = excelService.getCorePropertiesFromXSSF();
	或
	CoreProperties cp = excelService.getXSSFWorkbook().getProperties().getCoreProperties();
	//--属性
	cp.setTitle("title");						//标题
	cp.setSubjectProperty("SubjectProperty");	//主题
	cp.setKeywords("keywords");					//标记
	cp.setCategory("category");					//类别
	cp.setDescription("description");			//备注
	//--来源
	Nullable<Date> date = new Nullable<Date>(DateUtil.parseDatetime("2017-08-08 12:12:23"));//yyyy-MM-dd HH:mm:dd
	cp.setCreated(date);						//创建内容的时间
	cp.setModified(date);						//最后一次保存的日期
	cp.setLastPrinted(date);					//最后一次打印的时间
	//--内容
	cp.setContentStatus("内容状态");				//内容状态
...
####EXAMPLE2:
	//扩展属性 
	CTProperties ctp = excelService.getXSSFWorkbook().getProperties().getExtendedProperties().getUnderlyingProperties();
	PackagePropertiesPart ppp = excelService.getXSSFWorkbook().getProperties().getCoreProperties().getUnderlyingProperties();
	//--属性
	ppp.setTitleProperty("title");				//标题
	ppp.setSubjectProperty("SubjectProperty");	//主题
	ppp.setKeywordsProperty("keywords");		//标记
	ppp.setCategoryProperty("category");		//类别
	ppp.setDescriptionProperty("description");	//备注
	
	//--来源
	ppp.setCreatorProperty("ring");				//作者
	ctp.setCompany("xxx有限公司");				//公司
	ctp.setApplication("XXXXXX系统");			//程序名称
	ppp.setVersionProperty("1");				//版本号
	ppp.setRevisionProperty("2");				//修改号
	
	Nullable<Date> date = new Nullable<Date>(DateUtil.parseDatetime("2017-08-08 12:12:23"));//yyyy-MM-dd HH:mm:dd
	ppp.setCreatedProperty(date);				//创建内容的时间
	ppp.setModifiedProperty(date);				//最后一次保存的日期
	ppp.setLastPrintedProperty(date);			//最后一次打印的时间
			
	//--内容
	ppp.setContentStatusProperty("内容状态");	//内容状态
	ppp.setLanguageProperty("java");			//语言
...		
```

### 2.6获取或更改导出的XLS文件属性
```
SummaryInformation suminfoInformation = excelService.getInformationFromHSSF();
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

PoiExcelService excelService = PoiExcelServiceFactory.getInstance();
JDBCUtil.initPorperties(JDBCUtil.getPropertiesInputStream("jdbc.properties"));

excelService.insertPage("日志", 
		JDBCMetaHelper.listColumns("rsys_log"), 
		Table2ExcelCallback.fields, Table2ExcelCallback.titls, 
		new Table2ExcelCallback(excelService, tableMap));

excelService.output("d:/table.xlsx");
```

## 读取EXCEL文件
```
String path = "e://test.xlsx";
		PoiReadService poiReadService = new PoiReadServiceImpl();
		String[] fields = {"aa","bb","cc","...","dd"};
		poiReadService.read(path, ".*", new ReadSheetHandler(POJO.class,fields,new ReadSheetHandlerCallback() {
			@Override
			public void callback(int rownum, String[] row, Object entity) throws Exception {
				//...
			}
			
			@Override
			public void before(int rownum, short col, String formattedValue) throws Exception {
				//...
			}
		}));
```