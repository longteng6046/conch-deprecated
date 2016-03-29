package conch2.server;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import conch2.sql.MySQLUtil;
import conch2.xml.XmlUtility;


/**
 * 
 * A server contains the functions which are going to be provided service interfaces.
 * Currently only the "process()" function is available to users; all commands are supposed 
 * to pass in as text string to it. 
 *
 */
public class Server {

	// public static String DB_CONFIG = "/home/tlong/workspace/conch2server/WebContent/db.cfg";
	public static String DB_CONFIG = new java.io.File(".").getAbsolutePath() + "/WebContent/db.cfg";
	
	private DocumentBuilderFactory factory = DocumentBuilderFactory
			.newInstance();

	
	/**
	 * It takes in an XML file in text, parse it accordingly, and invode corresponding
	 * helper functions.
	 * 
	 * @param input the content of an input xml file
	 * @return the content of another xml file containing expected results of the input
	 */
	public String process(String input) {
		// System.out.println("XML command(s) received...");

		
		
		/* create the input document and corresponding result output document */
		Document inputDoc = null;
		Document outputDoc = null;

		DocumentBuilder db = null;
		
		/* initial fatal error occasion */ 
		// XML library does not work
		try {
			db = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			System.err.println("FatalError: cannot build XML DocumentBuilder on server ");
			e1.printStackTrace();
			System.exit(-1);
		}
		
		// database connection error
		if (MySQLUtil.dbServerCheck(DB_CONFIG) == false){
			System.err.println("FatalError: database connection error.");
			XmlUtility.fatalError(outputDoc, "Database Connection Error");
			return XmlUtility.printToString(outputDoc);
		}
		
		outputDoc = db.newDocument();
		
		try {
			inputDoc = db.parse(new ByteArrayInputStream(input.getBytes()));
		} catch (Exception e1) {
			XmlUtility.fatalError(outputDoc, "Error parsing input XML");
			//e1.printStackTrace();
			return XmlUtility.printToString(outputDoc);
		} 
		
		
		// System.out.println("begin to process commands...");
		
		// process and execute commands
		Element results = outputDoc.createElement("results");
		outputDoc.appendChild(results);
		CommandParser parser = new CommandParser();
		parser.parseCommand(inputDoc, outputDoc);


		/* transfer output document to string */
		return XmlUtility.printToString(outputDoc);

	}
	
	/**
	 * For basic service client test only ...
	 * @param echo
	 * @return
	 */
	public String test(String echo){
		//System.out.println(haha);
		return "You have reached conch2!\n" + echo;
	}
	
	/**
	 * Submit a service command with a file attachment.
	 */
/*	public String processAttachment(String command, File attachment){
		return "Get your file...";
		
	}*/
}
