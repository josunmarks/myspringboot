/**
 * 
 */
package com.myspringboot.sqlmanage;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author wangjoun
 *
 */
@Service
public class SqlManageService implements InitializingBean{
	@Value("${sqlmapper.locations}")
	private Resource[] mapperLocations;
	
	private void readDocument(Document document){
		Element rootElement = document.getDocumentElement(); 
		NodeList nodes = rootElement.getChildNodes(); 
        for (int i=0; i < nodes.getLength(); i++) 
        { 
           Node node = nodes.item(i); 
           if (node.getNodeType() == Node.ELEMENT_NODE) {   
        	   SqlDTO sqlDTO = readElement((Element)node);
        	   SqlFactory.addSql(sqlDTO.getId(), sqlDTO.getSqlGroup(), sqlDTO);
           } 
        } 
	}
	
	private SqlDTO readElement(Element element){
		if (!isEmpty(element.getTextContent())) {
			SqlDTO sqlDTO = new SqlDTO();
			NamedNodeMap accrMap = element.getAttributes();
			sqlDTO.setSqlType(element.getTagName());
			for (int i = 0; i < accrMap.getLength(); i++) {
				Node node = accrMap.item(i);
				if ("id".toUpperCase().equals(node.getNodeName().toUpperCase())) {
					sqlDTO.setId(node.getNodeValue());
				}
				if ("sqlgroup".toUpperCase().equals(node.getNodeName().toUpperCase())) {
					sqlDTO.setSqlGroup(node.getNodeValue());
				}
				if ("property".toUpperCase().equals(node.getNodeName().toUpperCase())) {
					sqlDTO.setProperty(node.getNodeValue());
				}
			}
			sqlDTO.setSqlContext(element.getFirstChild().getTextContent());
			sqlDTO.setNextContext(element.getPreviousSibling().getTextContent());
			switch (element.getTagName().toUpperCase()) {
			case "SELECT":
			case "INSERT":
			case "DELETE":
			case "EXECUTE":
			case "UPDATE":
				NodeList nodes = element.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						if ("where".equals(node.getNodeName())) {
							List<SqlDTO> whereList = new ArrayList<SqlDTO>();
							NodeList subnodes = node.getChildNodes();
							for (int j = 0; j < subnodes.getLength(); j++) {
								Node subnode = subnodes.item(j);
								if (subnode.getNodeType() == Node.ELEMENT_NODE) {
									SqlDTO subSqlDTO = readElement((Element) subnode);
									whereList.add(subSqlDTO);
								}
							}
							sqlDTO.setWhereList(whereList);
						}
						if ("set".equals(node.getNodeName())) {
							List<SqlDTO> setList = new ArrayList<SqlDTO>();
							NodeList subnodes = node.getChildNodes();
							for (int j = 0; j < subnodes.getLength(); j++) {
								Node subnode = subnodes.item(j);
								if (subnode.getNodeType() == Node.ELEMENT_NODE) {
									SqlDTO subSqlDTO = readElement((Element) subnode);
									setList.add(subSqlDTO);
								}
							}
							sqlDTO.setSetList(setList);
						}

					}
				}
				break;
			}
			return sqlDTO;
		}else{
			return null;
		}
		
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!isEmpty(this.mapperLocations)) {
			for (Resource mapperLocation : this.mapperLocations) {
				if (mapperLocation == null) {
					continue;
				}
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setNamespaceAware(false);
					factory.setIgnoringComments(true);
					factory.setIgnoringElementContentWhitespace(false);
					factory.setCoalescing(false);
					factory.setExpandEntityReferences(true);

					DocumentBuilder builder;

					builder = factory.newDocumentBuilder();

					builder.setErrorHandler(new ErrorHandler() {
						@Override
						public void error(SAXParseException exception) throws SAXException {
							throw exception;
						}

						@Override
						public void fatalError(SAXParseException exception) throws SAXException {
							throw exception;
						}

						@Override
						public void warning(SAXParseException exception) throws SAXException {
						}
					});
					Document document = builder.parse(new InputSource(mapperLocation.getInputStream()));
					readDocument(document);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
