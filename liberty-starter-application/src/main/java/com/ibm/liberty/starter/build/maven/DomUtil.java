/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.ibm.liberty.starter.build.maven;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DomUtil {
	/**
	 * Determine if the parent node contains a child node with matching name
	 * @param parentNode - the parent node
	 * @param nodeName - name of child node to match 
	 * @return boolean value to indicate whether the parent node contains matching child node
	 */
	public static boolean hasChildNode(Node parentNode, String nodeName){
		return getChildNode(parentNode, nodeName, null) != null ? true : false;
	}

	/**
	 * Get the matching child node
	 * @param parentNode - the parent node
	 * @param name - name of child node to match 
	 * @param value - value of child node to match, specify null to not match value
	 * @return the child node if a match was found, null otherwise
	 */
	public static Node getChildNode(Node parentNode, String name, String value){
		List<Node> matchingChildren = getChildren(parentNode, name, value);
		return (matchingChildren.size() > 0) ? matchingChildren.get(0) : null;
	}

	/**
	 * Get the matching grand child node
	 * @param parentNode - the parent node
	 * @param childNodeName - name of child node to match
	 * @param grandChildNodeName - name of grand child node to match 
	 * @param grandChildNodeValue - value of grand child node to match
	 * @return the grand child node if a match was found, null otherwise
	 */
	public static Node getGrandchildNode(Node parentNode, String childNodeName, String grandChildNodeName, String grandChildNodeValue){
		List<Node> matchingChildren = getChildren(parentNode, childNodeName, null);
		for(Node child : matchingChildren){
			Node matchingGrandChild = getChildNode(child, grandChildNodeName, grandChildNodeValue);
			if(matchingGrandChild != null){
				return matchingGrandChild;
			}
		}
		return null;
	}

	/**
	 * Get all matching child nodes
	 * @param parentNode - the parent node
	 * @param name - name of child node to match 
	 * @param value - value of child node to match, specify null to not match value
	 * @return matching child nodes
	 */
	private static List<Node> getChildren(Node parentNode, String name, String value){
		List<Node> childNodes = new ArrayList<Node>();
		if(parentNode == null || name == null){
			return childNodes;
		}

		if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasChildNodes()) {
			NodeList children = parentNode.getChildNodes();
			for(int i=0; i < children.getLength(); i++){
				Node child = children.item(i);
				if(child != null && name.equals(child.getNodeName()) && (value == null || value.equals(child.getTextContent()))){
					childNodes.add(child);
				}
			}
		}

		return childNodes;
	}

	/**
	 * Add a child node
	 * @param parentNode - the parent node
	 * @param childName - name of child node to create
	 * @param childValue - value of child node
	 * @return the child node
	 */
	public static Node addChildNode(Document doc, Node parentNode, String childName, String childValue){
		if(doc == null || parentNode == null || childName == null){
			return null;
		}
		Node childNode = doc.createElement(childName);
		if(childValue != null){
			childNode.setTextContent(childValue);
		}
		parentNode.appendChild(childNode);
		return childNode;
	}

	/**
	 * Get the matching child node. Create a node if it doens't already exist
	 * @param parentNode - the parent node
	 * @param childName - name of child node to match
	 * @param childValue - value of child node to match, specify null to not match value
	 * @return the child node
	 */
	public static Node findOrAddChildNode(Document doc, Node parentNode, String childName, String childValue){
		Node matchingChild = getChildNode(parentNode, childName, childValue);
		return matchingChild != null ? matchingChild : addChildNode(doc, parentNode, childName, childValue);
	}

	public static Document getDocument(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = domFactory.newDocumentBuilder();
		return db.parse(inputStream);
	}
	
	public static boolean nodeHasId(Node node, String id) {
		// This will ignore white space nodes
		if (node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			int length = nodeList.getLength();
			for (int i = 0; i < length; i++) {
				if (id.equals(nodeList.item(i).getTextContent())) {
					return true;
				}
			}
		}
		return false;
	}
}
