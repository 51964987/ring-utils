package ringutils.code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

/**
 * 根据模板生成文件
 *  
 * @author ring
 * @date 2017年5月13日 下午3:36:53
 * @version V1.0
 */
public class ProcessFile {

	private static Logger log = LoggerFactory.getLogger(ProcessFile.class);
	private Configuration cfg = null;  
    private Template template = null; 
    
	/** ROOT路径 */
	private String rootPath;
	/** JAVA路径 */
	private String javaPath;
	/** WEB路径 */
	private String webappPath;
	/** RESOURCES路径 */
	private String resourcesPath;

	public ProcessFile(){
		super();
		//工程路径
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		File targetFile = new File(path).getParentFile();
		this.init(targetFile);
	}
	
	public ProcessFile(File targetFile){
		super();
		this.init(targetFile);
	}
	
	private void init(File targetFile){
		this.rootPath = targetFile.getParent();
		if("target".equalsIgnoreCase(targetFile.getName())){//Maven工程
			this.javaPath = this.rootPath + File.separator + "src" + File.separator + "main"  + File.separator + "java" ;
			this.webappPath = this.rootPath + File.separator + "src" + File.separator + "main"  + File.separator + "webapp" ;
			this.resourcesPath = this.rootPath + File.separator + "src" + File.separator + "main"  + File.separator + "resources" ;
		}else{
			this.javaPath = this.rootPath + File.separator + "src";
			this.webappPath = targetFile.getAbsolutePath();
			this.resourcesPath = this.rootPath + File.separator + "resources";
		}
	}
	
	/**
	 * 初始化配置
	 * @param templatePath
	 * @param charset
	 * @throws IOException 
	 * @author ring
	 * @date 2017年5月16日 上午9:00:36
	 * @version V1.0
	 */
	public void initConfig(String templatePath,String charset) throws IOException{
		//加载模板
		this.cfg = new Configuration(Configuration.VERSION_2_3_23);
		//配置
		cfg.setDirectoryForTemplateLoading(new File(templatePath)); 
		//设置编码
		cfg.setDefaultEncoding(charset);
		//设置对象的包装器
		//cfg.setObjectWrapper(new DefaultObjectWrapper());
		
		//设置异常处理器 开发阶段使用DEBUG_HANDLER,生产阶段使用:RETHROW_HANDLER
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		////通过setSharedVariable可以将一些变量设置为公共的共享的变量，在任何使用该configuration获取template的模版中都可以访问共享变量  
        //cfg.setSharedVariable("author", "Earl"); 
	}
	/**
	 * 初始化配置--默认resources为模板路径
	 * @param charset
	 * @throws IOException 
	 * @author ring
	 * @date 2017年5月16日 上午9:00:36
	 * @version V1.0
	 */
	public void initConfig(String charset) throws IOException{
		this.initConfig(this.resourcesPath, charset);
	}
	
	 /** 
     * 获取模板 
     * @param templatePath 
     * @return 
     * @throws TemplateNotFoundException 
     * @throws MalformedTemplateNameException 
     * @throws ParseException 
     * @throws IOException 
     */  
    private Template getTemplate(String templatePath) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException{  
        template=cfg.getTemplate(templatePath,cfg.getDefaultEncoding());  
        return template;  
    } 
    
    /** 
     * 将生成的内容写入字符串 
	 * @param data			数据集
	 * @param templateName	模版名称
     * @return 
     * @author ring
     * @date 2017年5月13日 下午3:28:57
     * @version V1.0
     */
    public StringWriter templateProcess(Map<String, Object> data,String templateName) {  
        StringWriter sw=new StringWriter();  
        try {  
            template=getTemplate(templateName);  
            template.process(data, sw);  
        } catch (Exception e) {  
            e.printStackTrace();  
			log.error(e.getMessage(),e);
        }finally{  
            try {  
                sw.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return sw;  
    } 
    
	/**
	 * 将数据集与模版结合，生成文件
	 * @param data			数据集
	 * @param templateName	模版名称
	 * @param outputPath	生成文件输出路径
	 * @return 
	 * @author ring
	 * @date 2017年5月13日 下午3:28:05
	 * @version V1.0
	 */
	public boolean templateProcess(Map<String,Object> data,String templateName,String outputPath){
		Writer out = null;
		try{
			File f = new File(outputPath);
			if(f.exists()){
				log.info("已存在文件："+f.getAbsolutePath());
				return false;
			}
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), cfg.getDefaultEncoding()));
			template=getTemplate(templateName);  
			template.process(data, out);
			out.flush();
			log.info("生成文件："+outputPath);
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage(),e);
			new File(outputPath).delete();
			return false;
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(),e);
				}
			}
		}
		return true;
	}
	
	public String getWebappPath() {
		return webappPath;
	}
	public void setWebappPath(String webappPath) {
		this.webappPath = webappPath;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public String getJavaPath() {
		return javaPath;
	}
	public void setJavaPath(String javaPath) {
		this.javaPath = javaPath;
	}

	public String getResourcesPath() {
		return resourcesPath;
	}

	public void setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
	}
			
}
