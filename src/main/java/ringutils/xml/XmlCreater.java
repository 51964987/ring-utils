package ringutils.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 创建DOM并生成XML文件  
 * @author ring
 * @date 2017年4月16日 上午8:24:53
 * @version V1.0
 */
public class XmlCreater {
	private Logger logger = LoggerFactory.getLogger(XmlCreater.class);
	private Document doc = null;// 新创建的DOM
	private String path = null;// 生成的XML文件绝对路径

	public XmlCreater(String path) {
		this.path = path;
		init();
	}

	/**
	 * 初始化函数  
	 * @author ring
	 * @date 2017年4月16日 上午8:24:25
	 * @version V1.0
	 */
	private void init() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();// 新建DOM
		} catch (ParserConfigurationException e) {
			logger.error("Parse DOM builder error:" + e);
		}
	}

	/**
	 * 创建根结点，并返回
	 * @param rootTagName
	 * @return 
	 * @author ring
	 * @date 2017年4月16日 上午8:24:12
	 * @version V1.0
	 */
	public Element createRootElement(String rootTagName) {
		if (doc.getDocumentElement() == null) {
			logger.debug("create root element '" + rootTagName + "' success.");
			Element root = doc.createElement(rootTagName);
			doc.appendChild(root);
			return root;
		}
		logger.warn("this dom's root element is exist,create fail.");
		return doc.getDocumentElement();
	}

	/**
	 * 在parent结点下增加子结点tagName
	 * @param parent
	 * @param tagName
	 * @return 
	 * @author ring
	 * @date 2017年4月16日 上午8:24:07
	 * @version V1.0
	 */
	public Element createElement(Element parent, String tagName) {
		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		parent.appendChild(child);
		return child;
	}

	/**
	 * 在parent结点下增加值为value的子结点tabName
	 * @param parent
	 * @param tagName
	 * @param value
	 * @return 
	 * @author ring
	 * @date 2017年4月16日 上午8:23:59
	 * @version V1.0
	 */
	public Element createElement(Element parent, String tagName, String value) {
		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		XmlOper.setElementValue(child, value);
		parent.appendChild(child);
		return child;
	}

	/**
	 * 在parent结点下增加属性
	 * @param parent
	 * @param attrName
	 * @param attrValue 
	 * @author ring
	 * @date 2017年4月16日 上午8:23:51
	 * @version V1.0
	 */
	public void createAttribute(Element parent, String attrName, String attrValue) {
		XmlOper.setElementAttr(parent, attrName, attrValue);
	}

	/**
	 * 根据DOM生成XML文件  
	 * @author ring
	 * @date 2017年4月16日 上午8:23:43
	 * @version V1.0
	 */
	public void buildXmlFile() {
		TransformerFactory tfactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = tfactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			logger.debug("New DOMSource success.");
			StreamResult result = new StreamResult(new File(path));
			logger.debug("New StreamResult success.");
			transformer.setOutputProperty("encoding", "GBK");
			transformer.transform(source, result);
			logger.debug("Build XML File '" + path + "' success.");
		} catch (TransformerConfigurationException e) {
			logger.error("Create Transformer error:" + e);
		} catch (TransformerException e) {
			logger.error("Transformer XML file error:" + e);
		}
	}
}
