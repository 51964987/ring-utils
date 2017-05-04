package ringutils.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 用于创建ＤＯＭ，Root结点
 *  
 * @author ring
 * @date 2017年4月16日 上午8:21:51
 * @version V1.0
 */
public class XmlBuilder {
	private Logger logger = LoggerFactory.getLogger(XmlBuilder.class);
	private String path=null;	//xml文件路径
    private Document doc=null;	//xml文件对应的document
    private Element root=null;	//xml文件的根结点

	public XmlBuilder(String path) {
		this.path = path;
		buildDocument();
		buildRoot();
	}

	/**
	 * 将XML文件生成Document  
	 * @author ring
	 * @date 2017年4月16日 上午8:19:51
	 * @version V1.0
	 */
	private void buildDocument() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			logger.debug("Construct document builder success.");
			doc = builder.parse(new File(path));
			logger.debug("Build xml document success.");
		} catch (ParserConfigurationException e) {
			logger.error("Construct document builder error:" + e);
		} catch (SAXException e) {
			logger.error("Parse xml file error:" + e);
		} catch (IOException e) {
			logger.error("Read xml file error:" + e);
		}
	}

	/**
	 * 生成XML的根结点 
	 * @author ring
	 * @date 2017年4月16日 上午8:20:00
	 * @version V1.0
	 */
	private void buildRoot() {
		root = doc.getDocumentElement();
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public Element getRoot() {
		return root;
	}
	public void setRoot(Element root) {
		this.root = root;
	}
	
}
