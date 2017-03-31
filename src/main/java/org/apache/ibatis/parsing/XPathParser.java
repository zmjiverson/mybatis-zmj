package org.apache.ibatis.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XPathParser {

	private Document document;
	private EntityResolver entityResolver;
	private XPath xpath;
	private Properties variables;
	private boolean validation;

	public XPathParser(InputStream inputstream,boolean validation,Properties prop,EntityResolver entityResolver){
		commonConstructor(validation,prop,entityResolver);
		this.document=createDocument(new InputSource(inputstream));
	}
	
	
	private Document createDocument(InputSource inputSource) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
		  
			return builder.parse(inputSource);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setVariables(Properties variables) {
		this.variables = variables;
	}

	public void commonConstructor(boolean validation,Properties variables,
			EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
		this.variables = variables;
		this.validation = validation;
		
		XPathFactory factory = XPathFactory.newInstance();
		this.xpath = factory.newXPath();
	}
	
	public XNode evalNode(String expression){
		return evalNode(document,expression);
	}


	public XNode evalNode(Object root, String expression) {
		
		Node node=(Node)evaluate(expression,root,XPathConstants.NODE);
		if(node==null){
			return null;
		}
		return new XNode(this, node, variables) ;
	}


	private Object evaluate(String expression, Object root, QName node) {
		
		try {
			return xpath.evaluate(expression, root, node);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<XNode> evalNodes(String expression){
		
		
		return evalNodes(document,expression);
	}


	public List<XNode> evalNodes(Object root, String expression) {

		List<XNode> xnodes=new ArrayList<XNode>();
		NodeList nodes = (NodeList)evaluate(expression,root,XPathConstants.NODE);
		for(int i=0;i<nodes.getLength();i++){
			xnodes.add(new XNode(this,nodes.item(i), variables));
		}
		return xnodes;
	}

}
