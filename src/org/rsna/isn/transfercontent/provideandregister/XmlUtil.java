/*---------------------------------------------------------------
*  Copyright 2005 by the Radiological Society of North America
*
*  This source software is released under the terms of the
*  RSNA Public License (http://mirc.rsna.org/rsnapubliclicense)
*----------------------------------------------------------------*/

package org.rsna.isn.transfercontent.provideandregister;

import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlUtil {


	/**
	 * Creates a new empty XML DOM document.
	 * @return the XML DOM document.
	 */
	public static Document getNewDocument() throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return db.newDocument();
	}

	/**
	 * Creates a new XML DOM document with a root element.
	 * @param name the root element name.
	 * @return the XML DOM document.
	 */
	public static Document getNewDocument(String name) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement(name);
		doc.appendChild(root);
		return doc;
	}

	/**
	 * Parses an XML file.
	 * @param file the file containing the XML to parse.
	 * @return the XML DOM document.
	 */
	public static Document getDocument(File file) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return db.parse(file);
	}

	/**
	 * Transforms an XML file using an XSL file and an array of parameters.
	 *<p>
	 * The parameter array consists of a sequence of pairs of (String parametername)
	 * followed by (Object parametervalue) in an Object[].
	 * @param docFile the file containing the XML to transform.
	 * @param xslFile the file containing the XSL transformation program.
	 * @param params the array of transformation parameters.
	 * @return the transformed DOM Document.
	 */
	public static Document getTransformedDocument(File docFile, File xslFile, Object[] params) throws Exception {
		return getTransformedDocument(new StreamSource(docFile), new StreamSource(xslFile), params);
	}

	/**
	 * Transforms an XML DOM Document using an XSL file and an array of parameters.
	 *<p>
	 * The parameter array consists of a sequence of pairs of (String parametername)
	 * followed by (Object parametervalue) in an Object[].
	 * @param doc the Document to transform.
	 * @param xslFile the file containing the XSL transformation program.
	 * @param params the array of transformation parameters.
	 * @return the transformed DOM Document.
	 */
	public static Document getTransformedDocument(Document doc, File xslFile, Object[] params) throws Exception {
		return getTransformedDocument(new DOMSource(doc), new StreamSource(xslFile), params);
	}

	/**
	 * General method for transformation to a DOM Document. Transforms a Source
	 * document using a Source XSL document and an array of parameters.
	 *<p>
	 * The parameter array consists of a sequence of pairs of (String parametername)
	 * followed by (Object parametervalue) in an Object[].
	 * @param doc the Source XML document to transform.
	 * @param xsl the Source XSL transformation program.
	 * @param params the array of transformation parameters.
	 * @return the transformed text.
	 */
	public static Document getTransformedDocument(Source doc, Source xsl, Object[] params) throws Exception {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xsl);
		if ((params != null) && (params.length > 1)) {
			for (int i=0; i<params.length; i=i+2) {
				transformer.setParameter((String)params[i],params[i+1]);
			}
		}
		DOMResult domResult = new DOMResult();
		transformer.transform(doc,domResult);
		return (Document) domResult.getNode();
	}

	/**
	 * Recursively walk the tree from a node down and convert to a text string.
	 */
	public static String toString(Node node) {
		StringBuffer sb = new StringBuffer();
		renderNode(sb,node);
		return sb.toString();
	}

	static void renderNode(StringBuffer sb, Node node) {
		switch (node.getNodeType()) {

			case Node.DOCUMENT_NODE:
				//sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				NodeList nodes = node.getChildNodes();
				if (nodes != null) {
					for (int i=0; i<nodes.getLength(); i++) {
						renderNode(sb,nodes.item(i));
					}
				}
				break;

			case Node.ELEMENT_NODE:
				String name = node.getNodeName();
				NamedNodeMap attributes = node.getAttributes();
				if (attributes.getLength() == 0) {
					sb.append("<" + name + ">");
				}
				else {
					sb.append("<" + name + " ");
					int attrlen = attributes.getLength();
					for (int i=0; i<attrlen; i++) {
						Node current = attributes.item(i);
						sb.append(current.getNodeName() + "=\"" +
							escapeChars(current.getNodeValue()));
						if (i < attrlen-1)
							sb.append("\" ");
						else
							sb.append("\">");
					}
				}
				NodeList children = node.getChildNodes();
				if (children != null) {
					for (int i=0; i<children.getLength(); i++) {
						renderNode(sb,children.item(i));
					}
				}
				sb.append("</" + name + ">");
				break;

			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				sb.append(escapeChars(node.getNodeValue()));
				break;

			case Node.PROCESSING_INSTRUCTION_NODE:
				sb.append("<?" + node.getNodeName() + " " +
					escapeChars(node.getNodeValue()) + "?>");
				break;

			case Node.ENTITY_REFERENCE_NODE:
				sb.append("&" + node.getNodeName() + ";");
				break;

			case Node.DOCUMENT_TYPE_NODE:
				// Ignore document type nodes
				break;

			case Node.COMMENT_NODE:
				sb.append("<!--" + node.getNodeValue() + "-->");
				break;
		}
		return;
	}

	static String escapeChars(String theString) {
		return theString.replaceAll("\\&","&amp;")
						.replaceAll("\\>","&gt;")
						.replaceAll("\\<","&lt;")
						.replaceAll("\"","&quot;")
						.replaceAll("'","&apos;");
	}
}