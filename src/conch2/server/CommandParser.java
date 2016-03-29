package conch2.server;

import java.sql.SQLException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import conch2.xml.XmlUtility;

/**
 * 
 * A CommandParser parses the command XML file, get the commands 
 * from the children list of the root node as well as their parameters,
 * and invoke the corresponding helper function to process them.  
 *
 */
public class CommandParser {
	/**
	 *
	 * @param inDoc Input XML document that includes a set of supported commands.
	 * @param outDoc Output XML document that includes the results.
	 *<p>
	 * Input XML doc format:
	 * <pre>
	 * {@code 
	 * <commands> 
	 *   <command1...> 
	 *   <command2...> 
	 *   ... 
	 *   <commandn...> 
	 * </commands> 
	 *}</pre>
	 * <br>
	 * {@code <command1>} to {@code <commandn>} and their results' format:
	 * <br>
	 * --------------------------------------------------------------------------------<br>
	 * Login to the repository:
	 *<pre>{@code 
	 *  <login>
	 *   <username>username</username>
	 *   <password>passwd</password>
	 * </login>

	 * <success>
	 *   <command name="login"/>
	 *	  <parameters>
	 *	    <username value="username"/>
	 *	    <password value="passwd"/>
	 *	  </parameters>
	 *	  <output>
	 *	    <sessionId>1</sessionId>
	 *	  </output>
	 * </success>
	 *
	 * <failure type="FailAuthenticate">
	 *	 <command name="login"/>
	 *	 <parameters>
	 *	   <username value="username"/>
	 *	   <password value="passwd"/>
	 *	  </parameters>
	 * </failure>
	 * }</pre>
	 * 
	 * --------------------------------------------------------------------------------<br>
	 * Logout from a session
	 *<pre>{@code 
	 *  <logout>
	 *   <sessionId>sessionId</sessionId>
	 *   <username>username</username>
	 * </logout>

	 * <success>
	 *   <command name="logout"/>
	 *	  <parameters>
	 *		<sessionId value="sessionId" />
	 *	    <username value="username" />
	 *	  </parameters>
	 *	  <output \>
	 * </success>
	 *
	 * <failure type="NoSessionExists">
	 *	 <command name="logout"/>
	 *	 <parameters>
	 *		<sessionId value="sessionId" />
	 *	    <username value="username" />
	 *	  </parameters>
	 * </failure>
	 * }</pre>
	 *
	 * --------------------------------------------------------------------------------<br>	
	 * Initialize all tables in <code>conch</code> schema, except table: Testers and Session.
	 * <pre>{@code
	 *   <initRepository>
	 *     <sessionId>sessionId</sessionId>
	 *   </initRepository>
	 * 
	 * <success>
	 *   <command name="initRepository" />
	 *     <parameters>
	 *       <sessionId>sessionId</sessionId>
	 *     </parameters>
	 *   <output />
	 * </success>
	 * 
	 * <failure type="PermissionDenied">
	 *   <command name="initRepository" />
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *   </parameters>
	 * </failure>
	 * 
	 * }</pre>
	 * --------------------------------------------------------------------------------<br>	
	 * Retrieve Metadata of a given component:
	 * <pre>{@code
	 *   <getCompMeta>
	 *    <sessionId>sessionId</sessionId>
	 *    <name>sqlite3</name>
	 *  </getCompMeta>
	 *  
	 *  <success>
	 *    <command name="getCompMeta" />
	 *	  <parameters>
	 *      <sessionId>sessionId</sessionId>
	 *      <name value="sqlite3" />
	 *	  </parameters>
	 *	  <output>
	 *	    <name>sqlite3</name>
	 *		<vendor>sqlite</vendor>
	 *		<category>library</category>
	 *		<constraints>
	 *		  <constraint>"version > 3"</constraint>
	 *		  <constraint>"os in [unix, linux]"</constraint>
	 *		</constraints>
	 *	  </output>
	 *  </success>
	 * 
	 *  <failure type="CompNotExist">
	 *	  <command name="getCompMeta" />
	 *	  <parameters>
	 *		<sessionId>sessionId</sessionId>
	 *		<compName value="sqlite3" />
	 *	  </parameters>
	 *  </failure>
	 * }
	 *</pre>
	 * --------------------------------------------------------------------------------<br>	
	 * Retrieve information of a given component vendor:
	 * <pre>{@code
	 * <getVendor>
	 *	 <sessionId>sessionId</sessionId>
	 *	 <name>sqlite</name>
	 * </getVendor>
	 * 
	 * <success>
	 *	 <command name="getVendor" />
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>sqlite</name>
	 *	 </parameters>
	 *	 <output>
	 *	   <name>sqlite</name>
	 *	   <note>"SQLite development team"</note>
	 *	   <website>"http://www.sqlite.org/"</website>
	 *	 </output>
	 * </success>
	 *
	 * <failure type="VendorNotExist">
	 *	 <command name="getVendor" />
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>sqlite</name>
	 *	 </parameters>
	 * </failure>
	 * }
	 * </pre>
	 * --------------------------------------------------------------------------------<br>	
	 * Retrieve information of a given category
	 * <pre>{@code
	 * <getCategory>
	 *   <sessionId>sessionId</sessionId>
	 *	 <name>library</name>
	 * </getCategory>
	 *
	 * <success>
	 *	 <command name="getCategory" />
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>library</name>
	 *	 </parameters>
	 *	 <output>
	 *	   <name>library</name>
	 *	   <note>"A library component includes all kinds of runtime libraries."
	 *	   </note>
	 *	 </output>
	 * </success>
	 *
	 * <failure type="CategoryNotExist">
	 *	 <command name="getCategory" />
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>library</name>
	 *	 </parameters>
	 * </failure>
	 * }</pre>
	 * --------------------------------------------------------------------------------<br>	
	 * Add a new component and its metadata into Conch
	 * <pre>{@code
	 * <addComp>
	 *   <sessionId>sessionId</sessionId>
	 *	 <name>sqlite3</name>
	 *	 <vendor>sqlite</vendor>
	 *	 <category>library</category>
	 *	 <constraints>
	 *	   <constraint>"version > 3"</constraint>
	 *	   <constraint>"os in [unix, linux]"</constraint>
	 *	 </constraints>
	 * </addComp>
	 *	
	 * <success>
	 *	 <command>addComp</command>
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>sqlite3</name>
	 *	   <vendor>sqlite</vendor>
	 *	   <category>library</category>
	 *	   <constraints>
	 *		 <constraint>"version > 3"</constraint>
	 *		 <constraint>"os in [unix, linux]"</constraint>
	 *	   </constraints>
	 *	 </parameters>
	 *	 <output />
	 * </success>
	 *
	 * <!-- other failure: VendorNotExist, CategoryNotExist -->
	 *
	 * <failure type="CompAlreadyExist">
	 *	 <command>addComp</command>
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>sqlite3</name>
	 *	   <vendor>sqlite</vendor>
	 *	   <category>library</category>
	 *	   <constraints>
	 *		 <constraint>"version > 3"</constraint>
	 *		 <constraint>"os in [unix, linux]"</constraint>
	 *	   </constraints>
	 *	 </parameters>
	 * </failure>
	 * }
	 * </pre>
	 * --------------------------------------------------------------------------------<br>
	 * Retrieve existing CDG of a component:
	 * <pre>{@code
	 * <getCDG>
	 *   <sessionId>sessionId</sessionId>
	 *	 <name>sqlite3</name>
	 * </getCDG>
	 *
	 * <success>
	 *	 <command>getCDG</command>
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>sqlite3</name>
	 *	 </parameters>
	 *	 <output>
	 *	   <cdg>"* [+ gcc pgcc intelc] ncurses tclsh"</cdg>
	 *	 </output>
	 * </success>
	 *
 	 * <failure type="CompNotExist">
	 *	 <command>getCDG</command>
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>sqlite3</name>
	 *   </parameters>
	 * </failure>
	 *
	 * }</pre>
	 *  	
	 * --------------------------------------------------------------------------------<br>
	 * Add a dependency instance to Conch:
	 * <pre>{@code
	 * <addDeptInstance>
	 *   <sessionId>sessionId</sessionId>
	 *	 <name>sqlite3</name>
	 *	 <depended>gcc</depended>
	 *	 <depended>ncurses</depended>
	 *	 <depended>tclsh</depended>
	 * </addDeptInstance>
	 *
	 * <success>
	 *	 <command>addDeptInstance</command>
	 *	 <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *	   <name>sqlite3</name>
	 *	   <depended>gcc</depended>
	 *	   <depended>ncurses</depended>
	 *     <depended>tclsh</depended>
	 *   </parameters>
	 *	 <output />
	 * </success>
	 *
	 * <!-- Other type: DeptNotExist, InstConflict -->
	 * <failure type="CompNotExist">
	 *   <command>addDeptInstance</command>
	 *	   <parameters>
	 *       <sessionId>sessionId</sessionId>
	 *       <name>sqlite3</name>
	 *       <depended>gcc</depended>
	 *       <depended>ncurses</depended>
	 *       <depended>tclsh</depended>
	 *     </parameters>
	 * </failure>
	 * }</pre>
	 * --------------------------------------------------------------------------------<br>
	 * Retrieve package information of a given component:
	 * <pre>{@code
	 * <getPkgInfo>
	 *   <sessionId>sessionId</sessionId>
	 *   <name>sqlite</name>
	 * </getPkgInfo>
	 *
	 * <success>
	 *	 <command>getPkgInfo</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <name>sqlite3</name>
	 *   </parameters>
	 *   <output>
	 *     <package>
	 *       <name>sqlite3</name>
	 *       <version>"3.7.14"</version>
	 *       <os>linux</os>
	 *       <arch>x86</arch>
	 *       <url>"x86_url"</url>
	 *     </package>
	 *     <package>
	 *       <name>sqlite3</name>
	 *       <version>"3.7.14"</version>
	 *       <os>linux</os>
	 *       <arch>amd64</arch>
	 *       <url>"amd64_url"</url>
	 *     </package>
	 *   </output>
	 * </success>
	 *
	 * <failure type="CompNotExist">
	 *   <command>getPkgInfo</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <name>sqlite3</name>
	 * </parameters>
	 * </failure>
	 * }</pre>
	 *
	 * --------------------------------------------------------------------------------<br>	
	 * Add a new record of a package into the repository:
	 * <pre>{@code
	 * <addPkgInfo>
	 *   <sessionId>sessionId</sessionId>
	 *   <name>sqlite3</name>
	 *   <version>"3.7.14"</version>
	 *   <os>linux</os>
	 *   <arch>x86</arch>
	 *   <url>"www.idontknow.com"</url>
	 * </addPkgInfo>
	 *
	 * <success>
	 *   <command>addPkgInfo</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <name>sqlite3</name>
	 *     <version>"3.7.14"</version>
	 *     <os>linux</os>
	 *     <arch>x86</arch>
	 *     <url>"x86_url</url>
	 *   </parameters>
	 *   <output />
	 * </success>
	 *
	 * <failure type="CompNotExist">
	 *   <command>addPkgInfo</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <name>sqlite3</name>
	 *     <version>"3.7.14"</version>
	 *     <os>linux</os>
	 *     <arch>x86</arch>
	 *     <url>"x86_url</url>
	 *   </parameters>
	 * </failure>
	 * }</pre>
	 * --------------------------------------------------------------------------------<br>
	 * Retrieve all HARD constraints of a given component:
	 * <pre>
	 * <font color=red> not implemented.</font>
	 * </pre>
	 * --------------------------------------------------------------------------------<br>	
	 * Add a new TestSuite of a certain Component into the repository. We suppose suites are
	 * associated with components, not packages. Oh, test suite name is Unique.
	 * <pre>{@code
	 * <addTestSuite>
	 *   <sessionId>sessionId</sessionId>
	 *   <name>sqlite3UnitTests</name>
	 *   <componentName>sqlite3</componentName>
	 * </addTestSuite>
	 *
	 * <success>
	 *   <command>addTestSuite</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <name>sqlite3UnitTests</name>
	 *     <componentName>sqlite3</componentName>
	 *   </parameters>
	 *   <output />
	 * </success>
	 *
	 * <failure type="CompNotExist">
	 *   <command>addTestSuite</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <name>sqlite3UnitTests</name>
	 *     <componentName>sqlite3</componentName>
	 *     <version>"3.7.14"</version>
	 *   </parameters>
	 * </failure>
	 * }</pre>
	 * --------------------------------------------------------------------------------<br>
	 * Add a new TestCase of a Test Suite into the repository. We suppose each test case
	 * will have a unique local name, which can be used as a key identification.
	 * <pre>{@code
	 * <addTestCase>
	 *   <sessionId>sessionId</sessionId>
	 *   <localName>sqlite3UnitTests_tc1</localName>
	 *   <suiteName>sqlite3UnitTests</suiteName>
	 * </addTestCase>
	 *
	 * <success>
	 *   <command>addTestCase</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <localName>sqlite3UnitTests_tc1</localName>
	 *     <suiteName>sqlite3UnitTests</suiteName>
	 *   </parameters>
	 *   <output />
	 * </success>
	 *
	 * <failure type="SuiteNotExist">
	 *   <command>addTestCase</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <localName>sqlite3UnitTests_tc1</localName>
	 *     <suiteName>sqlite3UnitTests</suiteName>
	 *   </parameters>
	 * </failure>
	 * }</pre>
	 * --------------------------------------------------------------------------------<br>	
	 * Add a new Test record to the repository. Given the sessionId, tester data should
	 * be found automatically; and the time (EST) should be also automatically created. 
	 * Please refer to the authenticate method.
	 * <pre>{@code
	 * <addTestRecord>
	 *   <sessionId>sessionId</sessionId>
	 *   <testType>functional</testType>
	 *   <configuration>a string to represent configuration</configuration>
	 * </addTestRecord>
	 *
	 * <success>
	 *   <command>addTestRecord</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <testType>functional</testType>
	 *     <configuration>a string to represent configuration</configuration>
	 *   </parameters>
	 *   <output>
	 *   	<recordId>1234</recordId>
	 *   </output>
	 * </success>
	 * 
	 * <failure type="TypeError">
	 *   <command>addTestRecord</command>
	 *   <parameters>
	 *     <sessionId>sessionId</sessionId>
	 *     <testType>functional</testType>
	 *     <configuration>a string to represent configuration</configuration>
	 *   </parameters>
	 * </failure> 
	 * }</pre>
	 * --------------------------------------------------------------------------------<br>	
	 * @throws SQLException 
	 */
	 
	public void parseCommand(Document inDoc, Document outDoc){
		Element root = inDoc.getDocumentElement();
	//	System.out.println("+++++++++++");
	//	 System.out.println("root: " + root.getNodeName());

		CommandProcessor processor = new CommandProcessor(outDoc);
		
		
		NodeList cmdList = root.getChildNodes();
		for (int i=0; i<cmdList.getLength(); i++){
			Node cmd = cmdList.item(i);
			if (!(cmd instanceof Element))
				continue;
			Element cmdElement = (Element)cmd;
			String cmdName = cmdElement.getNodeName();
			
	//		System.out.println(cmdName);
			//process different commands
			if (cmdName == "login"){
				String name = cmdElement.getElementsByTagName("username").item(0).getTextContent();
	//			System.out.println("=================");
	//			System.out.println(name);
				String password = cmdElement.getElementsByTagName("password").item(0).getTextContent();
				processor.authenticate(name, password);
			}
			else if (cmdName == "getVendor"){
				String name = cmdElement.getElementsByTagName("name").item(0).getTextContent();
				processor.getVendor(name);
			}
			else if (cmdName == "addVendor"){
				String name = cmdElement.getElementsByTagName("name").item(0).getTextContent();
				String note= cmdElement.getElementsByTagName("note").item(0).getTextContent();
				String website= cmdElement.getElementsByTagName("website").item(0).getTextContent();
				processor.addVendor(name, note, website);
			}
			else if (cmdName == "addCategory"){
				String name = cmdElement.getElementsByTagName("name").item(0).getTextContent();
				String note= cmdElement.getElementsByTagName("note").item(0).getTextContent();
				processor.addCategory(name, note);
			}
			else if (cmdName == "getCategory"){
				String name = cmdElement.getElementsByTagName("name").item(0).getTextContent();
				processor.getCategory(name);
			}
			else if (cmdName == "getCompMeta"){
				//String name = cmdElement.getChildNodes().item(0).getNodeName();
				String name = cmdElement.getElementsByTagName("name").item(0).getTextContent();
				processor.getCompMeta(name);
			}
			else if (cmdName == "addComp"){
				String name = XmlUtility.getUniqueElementByTagName(cmdElement, "name").getTextContent();
			//	System.out.println(name);
				String vendor= XmlUtility.getUniqueElementByTagName(cmdElement, "vendor").getTextContent();
			//	System.out.println(vendor);
				String category= XmlUtility.getUniqueElementByTagName(cmdElement, "category").getTextContent();
			//	System.out.println(category);
				Element constraints = XmlUtility.getUniqueElementByTagName(cmdElement, "constraints");
				String constString = "";
				NodeList nodes = constraints.getChildNodes();
				for (int j=0; j<nodes.getLength(); j++){
					Node kid = nodes.item(j);
					if (!(kid instanceof Element))
						continue;
					constString += ((Element)kid).getTextContent();
					constString += ";;";
				}
			//	System.out.println(constString + "+++++++++");
				constString = constString.substring(0, constString.length()-2);
				processor.addCompMeta(name, vendor, category, constString);
			}
			else if (cmdName == "getCDG"){
				String name = XmlUtility.getUniqueElementByTagName(cmdElement, "name").getTextContent();
				processor.getCDG(name);
				
			}
			
			else if (cmdName == "addDeptInstance"){
				String name = XmlUtility.getUniqueElementByTagName(cmdElement, "name").getTextContent();
				ArrayList<String> depts = new ArrayList<String>();
				NodeList nodes = cmdElement.getElementsByTagName("depended");
				for (int j=0; j<nodes.getLength(); j++){
					String deptName = nodes.item(j).getTextContent();
					depts.add(deptName);
				}
				processor.addDeptInstance(name, depts);
			}
			
			else if (cmdName == "initRepository") {
				
			//	String name = cmdElement.getElementsByTagName("initRepository").item(0).getTextContent();
				ArrayList<String> list = new ArrayList<String>();
				list = updateTable();
				processor.initRepository(list);
			}
			
			else if (cmdName == "addTestSuite") {
		//		String id = XmlUtility.getUniqueElementByTagName(cmdElement, "sessionId").getTextContent();
				String name = XmlUtility.getUniqueElementByTagName(cmdElement, "suiteName").getTextContent();
				String comName = XmlUtility.getUniqueElementByTagName(cmdElement, "comName").getTextContent();
				processor.addTestSuite(name, comName);
			}
			else if(cmdName == "addTestCase") {
				String suiteName = XmlUtility.getUniqueElementByTagName(cmdElement, "suiteName").getTextContent();
				String localName = XmlUtility.getUniqueElementByTagName(cmdElement, "localName").getTextContent();
				String testNote = XmlUtility.getUniqueElementByTagName(cmdElement, "testNote").getTextContent();
				processor.addTestCase(suiteName, localName, testNote);
			}
			else{
				System.err.println("Command " + cmdName + " not supported.");
				continue;
			}
					
		}
	}
	
	private ArrayList<String> updateTable() {
		ArrayList<String> tableList = new ArrayList<String>();
		tableList.add("BuildtestResults");
		tableList.add("RecordRuntimeFlag_test");
		tableList.add("RecordRuntimeFlag_suite");
		tableList.add("FuncTestResults");
		tableList.add("TestRecord");
	//	tableList.add("testers (no deletion, but when insert data, opposite way");
		tableList.add("RuntimeFlags_test");
		tableList.add("TestCases");
		tableList.add("RuntimeFlags_suite");
		tableList.add("TestSuites");
	//	tableList.add("BuildFlags_univ");
		tableList.add("BuildFlags_pkg");
		tableList.add("Edges");
		tableList.add("Nodes");
		tableList.add("PackageInfo");
		tableList.add("ComponentMeta");
		tableList.add("ComponentCategory");
		tableList.add("ComponentVendor"); 
		return tableList;
	}

}
