package ringutils.xml;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 用于操作ＸＭＬ文件，包括查找、新增、删除、修改结点  
 * @author ring
 * @date 2017年4月16日 上午8:22:10
 * @version V1.0
 */
public class XmlOper {
	private static Logger logger = LoggerFactory.getLogger(XmlOper.class);
	/**
	 * 获取父结点parent的所有子结点
	 * @param parent
	 * @return
	 * @author ring
	 * @date 2017年4月16日 上午8:11:10
	 * @version V1.0
	 */
	public static NodeList getNodeList(Element parent) {
		return parent.getChildNodes();
	}

	/**
	 * 在父结点中查询指定名称的结点集
	 * @param parent
	 * @param name
	 * @return 
	 * @author ring
	 * @date 2017年4月16日 上午8:13:31
	 * @version V1.0
	 */
	public static Element[] getElementsByName(Element parent, String name) {
		ArrayList<Node> resList = new ArrayList<Node>();
		NodeList nl = getNodeList(parent);
		for (int i = 0; i < nl.getLength(); i++) {
			Node nd = nl.item(i);
			if (nd.getNodeName().equals(name)) {
				resList.add(nd);
			}
		}
		Element[] res = new Element[resList.size()];
		for (int i = 0; i < resList.size(); i++) {
			res[0] = (Element) resList.get(i);
		}
		logger.debug(parent.getNodeName() + "'s children of " + name + "'s num:" + res.length);
		return res;
	}

	/**
	 * 获取指定Element的名称
	 * @param element
	 * @return 
	 * @author ring
	 * @date 2017年4月16日 上午8:13:39
	 * @version V1.0
	 */
	public static String getElementName(Element element) {
		return element.getNodeName();
	}

	/**
	 * 获取指定Element的值
	 * @param element
	 * @return 
	 * @author ring
	 * @date 2017年4月16日 上午8:13:45
	 * @version V1.0
	 */
	public static String getElementValue(Element element) {
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE)// 是一个Text Node
			{
				logger.debug(element.getNodeName() + " has a Text Node.");
				return element.getFirstChild().getNodeValue();
			}
		}
		logger.error(element.getNodeName() + " hasn't a Text Node.");
		return null;
	}

	/**
	 * 获取指定Element的属性attr的值
	 * @param element
	 * @param attr
	 * @return 
	 * @author ring
	 * @date 2017年4月16日 上午8:13:51
	 * @version V1.0
	 */
	public static String getElementAttr(Element element, String attr) {
		return element.getAttribute(attr);
	}

	/**
	 * 设置指定Element的值
	 * @param element
	 * @param val 
	 * @author ring
	 * @date 2017年4月16日 上午8:13:57
	 * @version V1.0
	 */
	public static void setElementValue(Element element, String val) {
		Node node = element.getOwnerDocument().createTextNode(val);
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node nd = nl.item(i);
			if (nd.getNodeType() == Node.TEXT_NODE)// 是一个Text Node
			{
				nd.setNodeValue(val);
				logger.debug("modify " + element.getNodeName() + "'s node value succe.");
				return;
			}
		}
		logger.debug("new " + element.getNodeName() + "'s node value succe.");
		element.appendChild(node);
	}

	/**
	 * 设置结点Element的属性
	 * @param element
	 * @param attr
	 * @param attrVal 
	 * @author ring
	 * @date 2017年4月16日 上午8:14:04
	 * @version V1.0
	 */
	public static void setElementAttr(Element element, String attr, String attrVal) {
		element.setAttribute(attr, attrVal);
	}

	/**
	 * 在parent下增加结点child
	 * @param parent
	 * @param child 
	 * @author ring
	 * @date 2017年4月16日 上午8:14:11
	 * @version V1.0
	 */
	public static void addElement(Element parent, Element child) {
		parent.appendChild(child);
	}

	/**
	 * 在parent下增加字符串tagName生成的结点
	 * @param parent
	 * @param tagName 
	 * @author ring
	 * @date 2017年4月16日 上午8:14:17
	 * @version V1.0
	 */
	public static void addElement(Element parent, String tagName) {
		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		parent.appendChild(child);
	}

	/**
	 * 在parent下增加tagName的Text结点，且值为text
	 * @param parent
	 * @param tagName
	 * @param text 
	 * @author ring
	 * @date 2017年4月16日 上午8:14:33
	 * @version V1.0
	 */
	public static void addElement(Element parent, String tagName, String text) {
		Document doc = parent.getOwnerDocument();
		Element child = doc.createElement(tagName);
		setElementValue(child, text);
		parent.appendChild(child);
	}

	/**
	 * 将父结点parent下的名称为tagName的结点移除
	 * @param parent
	 * @param tagName 
	 * @author ring
	 * @date 2017年4月16日 上午8:14:40
	 * @version V1.0
	 */
	public static void removeElement(Element parent, String tagName) {
		logger.debug("remove " + parent.getNodeName() + "'s children by tagName " + tagName + " begin...");
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node nd = nl.item(i);
			if (nd.getNodeName().equals(tagName)) {
				parent.removeChild(nd);
				logger.debug("remove child '" + nd + "' success.");
			}
		}
		logger.debug("remove " + parent.getNodeName() + "'s children by tagName " + tagName + " end.");
	}

}
