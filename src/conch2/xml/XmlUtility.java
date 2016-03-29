package conch2.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtility {

	/**
	 * Append a <code>fatalError</code> tag to the result xml document.
	 * 
	 * @param output
	 *            the output w3c XML Document object
	 */
	public static void fatalError(Document output, String note) {
		Element fatalError = output.createElement("fatalError");
		fatalError.setAttribute("note", note);
		output.appendChild(fatalError);
	}

	/**
	 * Process a w3c XML document into a Java String.
	 * 
	 * @param outputDoc
	 * @return
	 */
	public static String printToString(Document doc) {
		try {
			doc.normalizeDocument();
			DOMSource source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(source, result);
			return writer.toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create a success element, and append the correct command name.
	 * 
	 * @param outDoc
	 *            the document for output xml
	 * @param commandName
	 *            name of the successfully executed command
	 * @return a <success><commmand name=$commandname /></success> Element
	 */
	public static Element createSuccess(Document outDoc, String commandName) {
		Element success = outDoc.createElement("success");
		Element cmd = outDoc.createElement("command");
		cmd.setAttribute("name", commandName);
		success.appendChild(cmd);
		
		Element parameter = outDoc.createElement("parameters");
		success.appendChild(parameter);
		
		Element output = outDoc.createElement("output");
		success.appendChild(output);
		return success;
	}
	
	public static Element createFailure(Document outputDoc, String commandName, String type){
		Element failure= outputDoc.createElement("failure");
		failure.setAttribute("type", type);
		Element cmd = outputDoc.createElement("command");
		cmd.setAttribute("name", commandName);
		failure.appendChild(cmd);
		
		Element parameter = outputDoc.createElement("parameters");
		failure.appendChild(parameter);
		
		// Element output = outputDoc.createElement("output");
		// failure.appendChild(output);
		return failure;
	}
	
	/**
	 * Return the child element whose name is as specified. 
	 * <p>
	 * Note: it's up to the user to guarantee that there EXISTS and is only 
	 * ONE child element.
	 * @param parent
	 * @param name
	 * @return
	 */
	public static Element getUniqueElementByTagName(Element parent, String name){
		Element elm = (Element) parent.getElementsByTagName(name).item(0);
		return elm;
	}
	
	public static void addParameter(Document outDoc, Element succFail, String name, String value){
		//System.out.println("value:" + value);
		Element parameters = (Element) succFail.getElementsByTagName("parameters").item(0);
		Element parameter = outDoc.createElement(name);
		parameter.setAttribute("value", value);
		parameters.appendChild(parameter);
	}
	
	public static void addOutput(Element success, Element outItem){
		Element output = (Element) success.getElementsByTagName("output").item(0);
		output.appendChild(outItem);
	}
	
	public static void addSimpleOutput(Element success, Document outDoc, String tagName, String value){
		Element output = (Element) success.getElementsByTagName("output").item(0);
		Element outItem = outDoc.createElement(tagName);
		outItem.setTextContent(value);
		output.appendChild(outItem);
	}
	
	/**
	 * Parse string1 and string2 as XML files, and compare their content. 
	 * <p>
	 * @param string1 the content of XML file one
	 * @param string2 the content of XML file two
	 * @return true/false, whether their contents are identical.
	 */
	public static boolean compareXMLDocString(String string1, String string2){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setIgnoringComments(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc1 = db.parse(new InputSource(new StringReader(string1)));
			Document doc2 = db.parse(new InputSource(new StringReader(string2)));
			doc1.normalizeDocument();
			doc2.normalizeDocument();
			if (doc1.isEqualNode(doc2) == false){
				System.out.println("different docs:");
				XmlUtility.printToString(doc1);
				XmlUtility.printToString(doc2);
			}
			else
				return true;
		} catch (ParserConfigurationException e) {
			System.err.println("ParserConfigurationException in compareXMLDocString.");
			e.printStackTrace();
		} catch (SAXException e) {
			System.err.println("SAXException in compareXMLDocString.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException in compareXMLDocString.");
			e.printStackTrace();
		}
		
		
		return false;
	}
	

	

	
	/**
	 * Parse string1 and string2 as XML files, and compare their content. 
	 * <p>
	 * @param string1 the content of XML file one
	 * @param string2 the content of XML file two
	 * @param whiteList name of the fields to ignore when comparing the contents
	 * @return true/false, whether their contents are identical.
	 */
	public static boolean compareXMLDocString(String string1, String string2, ArrayList<String> whiteList){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setIgnoringComments(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc1 = db.parse(new InputSource(new StringReader(string1)));
			Document doc2 = db.parse(new InputSource(new StringReader(string2)));
			doc1.normalizeDocument();
			doc2.normalizeDocument();
			XmlUtility.cleanWhiteList(doc1, whiteList);
			XmlUtility.cleanWhiteList(doc2, whiteList);
			if (((Node)doc1).isEqualNode(((Node)doc2)) == false){
				System.out.println("different docs 2:");
				System.out.println(XmlUtility.printToString(doc1));
				System.out.println(XmlUtility.printToString(doc2));
			}
			else
				return true;
		} catch (ParserConfigurationException e) {
			System.err.println("ParserConfigurationException in compareXMLDocString.");
			e.printStackTrace();
		} catch (SAXException e) {
			System.err.println("SAXException in compareXMLDocString.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException in compareXMLDocString.");
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	

	public static String clearXMLString(String origin, ArrayList<String> whiteList){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setIgnoringComments(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(origin)));
			doc.normalizeDocument();
			XmlUtility.cleanWhiteList(doc, whiteList);
			return XmlUtility.printToString(doc);
		} catch (Exception e) {
			System.err.println("Exception parsing the string into XML.");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	

	private static void cleanWhiteList(Node node,
				ArrayList<String> whiteList) {
		if (whiteList.contains(node.getLocalName())){
			node.setNodeValue(null);
			node.setTextContent(null);
			// System.err.println("haha");
		}
		NodeList children = node.getChildNodes();
		if (children.getLength() != 0){
			for (int i=0; i<children.getLength(); i++)
				cleanWhiteList(children.item(i), whiteList);
		}
		
	}

	/**
	 * 
	 * @param string1
	 * @param string2
	 * @param checkSessionId whether to compare the value of sessionId when comparing two outputs
	 * @return true/false
	 */
	public static boolean compareTestOutput(String string1, String string2, boolean checkSessionId){
		if (checkSessionId == false){
			ArrayList<String> whiteList = new ArrayList<String>();
			whiteList.add("sessionId");
			return XmlUtility.compareXMLDocString(string1, string2, whiteList);
		}
		else
			return XmlUtility.compareXMLDocString(string1, string2);
	}

}
