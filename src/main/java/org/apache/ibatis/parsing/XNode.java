package org.apache.ibatis.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.CharacterData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XNode {

	private Node node;
	private String name;
	private String body;
	private Properties attributes;
	private Properties variables;
	private XPathParser xpathParser;
	
	public XNode(XPathParser xpathParser,Node node,Properties prop){
		
		this.xpathParser=xpathParser;
		this.node=node;
		this.variables=prop;
		
		this.name=node.getNodeName();
		this.attributes=parseAttributes(node);
		this.body=parseBody(node);
	}
	
	
	public XNode newXNode(Node node){
		return new XNode(xpathParser, node, variables);
	}
	
	public List<XNode> getChildren(){
		List<XNode> children=new ArrayList<XNode>();
		NodeList nodeList = node.getChildNodes();
		if(nodeList!=null){
			for(int i=0;i<nodeList.getLength();i++){
				Node item = nodeList.item(i);
				if(item.getNodeType()==Node.ELEMENT_NODE){
					children.add(new XNode(xpathParser, item, variables));
				}
			}
		}
		return children;
	}

	private String parseBody(Node node) {
		
		String data=getBodyData(node);
		if(data==null){
			NodeList childNodes = node.getChildNodes();
			for(int i=0;i<childNodes.getLength();i++){
				Node child = childNodes.item(i);
				data = getBodyData(child);
				if(data!=null)break;
			}
		}
		
		return data;
	}
	
	private Properties parseAttributes(Node n) {
		
		Properties attributes = new Properties();
		NamedNodeMap attributeNodeMap = n.getAttributes();
		for(int i=0;i<attributeNodeMap.getLength();i++){
			Node attribute = attributeNodeMap.item(i);
			String nodename=attribute.getNodeName();
			String nodevalue=attribute.getNodeValue();
			attributes.put(nodename, nodevalue);
		}
		return attributes;
	}
	
	private String getBodyData(Node child){
		
		if(child.getNodeType()==Node.CDATA_SECTION_NODE
				||child.getNodeType()==Node.TEXT_NODE){
			String data = ((CharacterData) child).getData();
			return data;
		}
		return null;
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
	    builder.append("<");
	    builder.append(name);
	    for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
	      builder.append(" ");
	      builder.append(entry.getKey());
	      builder.append("=\"");
	      builder.append(entry.getValue());
	      builder.append("\"");
	    }
	    List<XNode> children = getChildren();
	    if (children.size() > 0) {
	      builder.append(">\n");
	      for (XNode node : children) {
	        builder.append(node.toString());
	      }
	      builder.append("</");
	      builder.append(name);
	      builder.append(">");
	    } else if (body != null) {
	      builder.append(">");
	      builder.append(body);
	      builder.append("</");
	      builder.append(name);
	      builder.append(">");
	    } else {
	      builder.append("/>");
	    }
	    builder.append("\n");
	    return builder.toString();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		InputStream is=new FileInputStream(new File("E://MetaSearchInfoDao.xml"));
		
		XPathParser parser = new XPathParser(is, false, null, null);
		
		System.out.println(parser);
		
		XNode xnode = parser.evalNode("/mapper");
		
		System.out.println(xnode.body);
		System.out.println(xnode.toString());
	}
}
