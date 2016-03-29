package conch2.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import conch2.cdg.CDGLayer;
import conch2.sql.MySQLUtil;
import conch2.xml.XmlUtility;

/**
 * 
 * All helper functions to process each command are grouped in this class. 
 * Each time a command is executed, the result will go to the outputDoc
 * w3c document in XML file format.  
 *
 */
public class CommandProcessor {

	private Statement statement;
	private Document outputDoc;
	private Element root;
	private Connection connection;

	public CommandProcessor(Document outputDoc) {
		this.outputDoc = outputDoc;
		root = outputDoc.getDocumentElement();
		// System.err.println(Server.DB_CONFIG);
		Connection conn = MySQLUtil.connect(Server.DB_CONFIG);
		this.connection = conn;
		try {
			statement = conn.createStatement();
		} catch (SQLException e) {
			XmlUtility.fatalError(outputDoc, "SQLException creating statement");
			e.printStackTrace();
		}
	}
	
 
	/**
	 * This function retrieves metadata about the given vendor from the <b>ComponentVendor</b>
	 * table. 
	 * <p>
	 * @param name vendor name
	 */
	public void getVendor(String name) {
		String query = "SELECT name, note, website FROM ComponentVendor WHERE name='"
				+ name + "';";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();

			if (results.next() == false) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"getVendor", "VendorNotExist");
				// System.out.println("here...");
				root.appendChild(failure);
				// System.out.println("here 2...");
				XmlUtility.addParameter(outputDoc, failure, "name", name);
				return;
			}

			// only one vendor with this name allowed, enforced by database
			Element success = XmlUtility.createSuccess(outputDoc, "getVendor");
			root.appendChild(success);
			XmlUtility.addParameter(outputDoc, success, "name", name);

			Element vendorName = outputDoc.createElement("name");
			vendorName.setTextContent(results.getString(1));
			XmlUtility.addOutput(success, vendorName);

			Element vendorNote = outputDoc.createElement("note");
			vendorNote.setTextContent(results.getString(2));
			XmlUtility.addOutput(success, vendorNote);

			Element vendorWeb = outputDoc.createElement("website");
			vendorWeb.setTextContent(results.getString(3));
			XmlUtility.addOutput(success, vendorWeb);

		} catch (SQLException e) {
			System.err.println("SQLException in getVendor;");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void addVendor(String name, String note, String website) {
		String query = "INSERT INTO ComponentVendor VALUES('";
		query += name + "','" + note + "','" + website + "');";
		// System.out.println(query);
		// System.exit(0);
		try {
			statement.execute(query);
		} catch (SQLException e) {
			String errorMsg = e.toString();
			if (errorMsg.contains("Duplicate entry")) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"addVendor", "VendorExists");
				root.appendChild(failure);
				return;
			}
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		}

		// success
		Element success = XmlUtility.createSuccess(outputDoc, "addVendor");
		root.appendChild(success);
		XmlUtility.addParameter(outputDoc, success, "name", name);
		XmlUtility.addParameter(outputDoc, success, "note", note);
		XmlUtility.addParameter(outputDoc, success, "web", website);

	}

	public void addCategory(String name, String note) {
		String query = "INSERT INTO ComponentCategory VALUES('";
		query += name + "','" + note + "');";
		// System.out.println(query);
		// System.exit(0);
		try {
			statement.execute(query);
		} catch (SQLException e) {
			String errorMsg = e.toString();
			if (errorMsg.contains("Duplicate entry")) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"addCategory", "CategoryExists");
				root.appendChild(failure);
				return;
			}
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		}

		// success
		Element success = XmlUtility.createSuccess(outputDoc, "addCategory");
		root.appendChild(success);
		XmlUtility.addParameter(outputDoc, success, "name", name);
		XmlUtility.addParameter(outputDoc, success, "note", note);

	}

	/**
	 * This function returns knowledge about a knowing category, including its
	 * detailed descriptions.
	 * <p>
	 * @param name category name
	 */
	public void getCategory(String name) {
		String query = "SELECT name, note FROM ComponentCategory WHERE name='"
				+ name + "';";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();

			if (results.next() == false) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"getCategory", "CategoryNotExist");
				// System.out.println("here...");
				root.appendChild(failure);
				// System.out.println("here 2...");
				XmlUtility.addParameter(outputDoc, failure, "name", name);
				return;
			}

			// only one vendor with this name allowed, enforced by database
			Element success = XmlUtility
					.createSuccess(outputDoc, "getCategory");
			root.appendChild(success);
			XmlUtility.addParameter(outputDoc, success, "name", name);

			Element vendorName = outputDoc.createElement("name");
			vendorName.setTextContent(results.getString(1));
			XmlUtility.addOutput(success, vendorName);

			Element vendorNote = outputDoc.createElement("note");
			vendorNote.setTextContent(results.getString(2));
			XmlUtility.addOutput(success, vendorNote);

		} catch (SQLException e) {
			System.err.println("SQLException in getCategory;");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This function retrieves the metadata about the given component
	 * from the <b>ComponentMeta</b> table. 
	 * <p>
	 * @param name name of the component
	 */
	public void getCompMeta(String name) {
		String query = "SELECT ComponentId, ComponentName, ComponentVendor, ComponentCategory, ComponentConstraints FROM ComponentMeta WHERE ComponentName='"
				+ name + "';";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();

			// item not found
			if (results.next() == false) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"getCompName", "CompNotExist");
				root.appendChild(failure);
				XmlUtility.addParameter(outputDoc, failure, "name", name);
				return;
			}

			Element success = XmlUtility
					.createSuccess(outputDoc, "getCompMeta");
			root.appendChild(success);
			XmlUtility.addParameter(outputDoc, success, "name", name);

			Element compName = outputDoc.createElement("name");
			compName.setTextContent(results.getString(2));
			XmlUtility.addOutput(success, compName);

			Element compVendor = outputDoc.createElement("vendor");
			compVendor.setTextContent(results.getString(3));
			XmlUtility.addOutput(success, compVendor);

			Element compCategory = outputDoc.createElement("category");
			compCategory.setTextContent(results.getString(4));
			XmlUtility.addOutput(success, compCategory);

			Element compConsts = outputDoc.createElement("constraints");
			String[] compConstraints = results.getString(5).split(";;");
			for (int i = 0; i < compConstraints.length; i++) {
				Element constraint = outputDoc.createElement("constraint");
				constraint.setTextContent(compConstraints[i]);
				compConsts.appendChild(constraint);
			}
			XmlUtility.addOutput(success, compConsts);

		} catch (SQLException e) {
			System.err.println("SQLException in getcompMeta;");
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * This function inserts an entry into the <b>ComponentMeta</b> table. It
	 * includes component name, its vendor, the category it belongs to, and 
	 * the constraints applied to it in plain text. The componentId is left
	 * as auto-increment.
	 * <p>
	 * @param name component name
	 * @param vendor component vendor
	 * @param category the category it falls in
	 * @param constString constraints applied to this component
	 */
	public void addCompMeta(String name, String vendor, String category,
			String constString) {
	//	System.out.println(constString);
		// String query =
		// "INSERT INTO ComponentMeta(ComponentName, ComponentVendor, ComponentCategory, ComponentConstraints) VALUES('";
		String query = "INSERT INTO ComponentMeta VALUES(null,'";
		query += name + "','" + vendor + "','" + category + "','" + constString
				+ "');";
		// System.out.println(query);
		try {
			statement.execute(query);
		} catch (SQLException e) {
			String errorMsg = e.toString();
			if (errorMsg.contains("Duplicate entry")) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"addComp", "ComponentExists");
				root.appendChild(failure);
				XmlUtility.addParameter(outputDoc, failure, "name", name);
				XmlUtility.addParameter(outputDoc, failure, "vendor", vendor);
				XmlUtility.addParameter(outputDoc, failure, "category",
						category);
				XmlUtility.addParameter(outputDoc, failure, "constraints",
						constString);
				return;
			}
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		}

		// success
		Element success = XmlUtility.createSuccess(outputDoc, "addMeta");
		root.appendChild(success);
		XmlUtility.addParameter(outputDoc, success, "name", name);
		XmlUtility.addParameter(outputDoc, success, "vendor", vendor);
		XmlUtility.addParameter(outputDoc, success, "category", category);
		XmlUtility.addParameter(outputDoc, success, "constraints", constString);

	}

	/**
	 * Given the name of a component, it returns the CDG in text.
	 * <p>
	 * @param name the name of the top-level component
	 */
	public void getCDG(String name) {
		// check whether the component exists, if no, error
		if (!this.compExists(name)) {
			Element failure = XmlUtility.createFailure(outputDoc, "getCDG",
					"TopComponentNotExist");
			root.appendChild(failure);

			XmlUtility.addParameter(outputDoc, failure, "name", name);

			return;
		}

		// success
		Element success = XmlUtility.createSuccess(outputDoc, "getCDG");
		root.appendChild(success);
		XmlUtility.addParameter(outputDoc, success, "name", name);

		// create a CDGLayer and let it expand
		CDGLayer cdgLayer = new CDGLayer(name, this.connection);

		Element cdgLayerElement = outputDoc.createElement("cdgLayer");
		cdgLayerElement.setTextContent(cdgLayer.toString());
		XmlUtility.addOutput(success, cdgLayerElement);

	}

	/**
	 * This function adds a list of other components that a component depends on.
	 * <p>
	 * @param name the component that depends on others
	 * @param depts the components being depended on
	 */
	public void addDeptInstance(String name, ArrayList<String> depts) {
		// check whether each component exists, if no, error
		if (!this.compExists(name)) {
			Element failure = XmlUtility.createFailure(outputDoc,
					"addDeptInstance", "TopComponentNotExist");
			root.appendChild(failure);

			XmlUtility.addParameter(outputDoc, failure, "name", name);
			for (int i = 0; i < depts.size(); i++) {
				XmlUtility.addParameter(outputDoc, failure, "depended",
						depts.get(i));
			}

			return;
		}
		for (int i = 0; i < depts.size(); i++) {
			String deptName = depts.get(i);
			if (!this.compExists(deptName)) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"addDeptInstance", "DeptComponentNotExist");
				root.appendChild(failure);

				XmlUtility.addParameter(outputDoc, failure, "name", name);
				for (int j = 0; j < depts.size(); j++) {
					XmlUtility.addParameter(outputDoc, failure, "depended",
							depts.get(j));
				}

				return;
			}
		}

		// success
		Element success = XmlUtility
				.createSuccess(outputDoc, "addDeptInstance");
		root.appendChild(success);
		XmlUtility.addParameter(outputDoc, success, "name", name);
		for (int i = 0; i < depts.size(); i++) {
			XmlUtility.addParameter(outputDoc, success, "depended",
					depts.get(i));
		}

		// create a CDGLayer basing on existing data
		CDGLayer cdgLayer = new CDGLayer(name, this.connection);
		cdgLayer.addInstance(depts);

		Element cdgLayerElement = outputDoc.createElement("cdgLayer");
		cdgLayerElement.setTextContent(cdgLayer.toString());
		XmlUtility.addOutput(success, cdgLayerElement);

	}
	
	/**
	 *  initialize all the tables to the empty except two tables
	 */
	public void initRepository(ArrayList<String> tableList){
		  for(int i = 0; i < tableList.size(); i++) {
		  String query = "Delete from " + tableList.get(i) + ";";
	//	  query += " ALTER TABLE" + tableList.get(i) + " AUTO_INCREMENT = 300" ;
		  try {
			statement.execute(query);
		} catch (Exception e) {
			System.err.println("SQL exception in initRepository.");
			e.printStackTrace();
			System.exit(-1);
		}
		  
		}
		Element success = XmlUtility.createSuccess(outputDoc, "initRepository");
		root.appendChild(success);
	//	XmlUtility.addParameter(outputDoc, success, "name", name);
	
	}
	
	/**
	 * Add a new TestSuite of a certain Component into the repository
	 */
	public void addTestSuite(String name, String comName) {
		//should comName be name of component or its ID ?  
		if (!this.compExists(comName)) {
			Element failure = XmlUtility.createFailure(outputDoc,
					"addTestSuite", "ComponentNotExist");
			root.appendChild(failure);
			XmlUtility.addParameter(outputDoc, failure, "suiteName", name);
			XmlUtility.addParameter(outputDoc, failure, "componentName", comName);
			return;
		}
		
		String query = "INSERT INTO TestSuites VALUES(null,'";
		query += name + "','" + comName + "');";
		// System.out.println(query);
		try {
			statement.execute(query);
		} catch (SQLException e) {
			String errorMsg = e.toString();
			if (errorMsg.contains("Duplicate entry")) {// but what if the same component 
				Element failure = XmlUtility.createFailure(outputDoc,
						"addTestSuite", "TestSuiteExists");
				root.appendChild(failure);
				XmlUtility.addParameter(outputDoc, failure, "suiteName", name);
				XmlUtility.addParameter(outputDoc, failure, "componentName", comName);
				return;
			}
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		}
		
		// success
				Element success = XmlUtility.createSuccess(outputDoc, "addTestSuite");
				root.appendChild(success);
				XmlUtility.addParameter(outputDoc, success, "suiteName", name);
				XmlUtility.addParameter(outputDoc, success, "componentName", comName);
			
	}
	
	/**
	 * Add a new TestCase of a Test Suite into the repository
	 * @param suiteName
	 * @param localName
	 * @param TestNote
	 */
	
	public void addTestCase(String suiteName, String localName, String testNote) {
		if (!this.testSuiteExists(suiteName)) {
			Element failure = XmlUtility.createFailure(outputDoc,
					"addTestCase", "SuiteNotExist");
			root.appendChild(failure);
		//	System.out.println(suiteName);
		//	System.out.println(localName);
			XmlUtility.addParameter(outputDoc, failure, "suiteName", suiteName);
			XmlUtility.addParameter(outputDoc, failure, "localName", localName);
			XmlUtility.addParameter(outputDoc, failure, "testNote", testNote);
			return;
		}
		
		String query = "INSERT INTO TestCases VALUES(null,'";
		query += suiteName + "','" + testNote + "','" +localName + "');";

		try {
			statement.execute(query);
		} catch (SQLException e) {
			String errorMsg = e.toString();
			if (errorMsg.contains("Duplicate entry")) {
				Element failure = XmlUtility.createFailure(outputDoc,
						"addTestCase", "TestCasesExists");
				root.appendChild(failure);
				XmlUtility.addParameter(outputDoc, failure, "suiteName", suiteName);
				XmlUtility.addParameter(outputDoc, failure, "localName", localName);
				XmlUtility.addParameter(outputDoc, failure, "testNote", testNote);
				return;
			}
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		}
		
	//	 success
			Element success = XmlUtility.createSuccess(outputDoc, "addTestCase");
			root.appendChild(success);
			XmlUtility.addParameter(outputDoc, success, "suiteName", suiteName);
			XmlUtility.addParameter(outputDoc, success, "localName", localName);
			XmlUtility.addParameter(outputDoc, success, "testNote", testNote);
			
	}

	/**
	 * Check whether the component exists in the ComponentMeta table
	 * <p>
	 * @param name
	 *            component name
	 * @return true/false
	 */
	private boolean compExists(String name) {
		String query = "SELECT * FROM ComponentMeta WHERE ComponentName='"
				+ name + "';";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();

			return results.next();
		} catch (SQLException e) {
			System.err.println("SQLException in compExists()");
			e.printStackTrace();
			System.exit(1);
			return true;
		}
	}

	private boolean testSuiteExists(String name) {
		String query = "SELECT * FROM TestSuites WHERE suiteName='"
				+ name + "';";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();

			return results.next();
		} catch (SQLException e) {
			System.err.println("SQLException in testSuiteExists()");
			e.printStackTrace();
			System.exit(1);
			return true;
		}
	}

	/**
	 * This function checks whether the given username and password is
	 * legal according to the <b>Testers</b> table. If yes, it marks a record 
	 * in the <b>Session</b> table, which includes the time this session starts.
	 * Currently, we don't care about the <i>ActiveStatus</i> attribute. 
	 * <p>
	 * @param name username
	 * @param password password of the user
	 */
	public void authenticate(String name, String password) {
		String query = "SELECT TesterId FROM Testers WHERE Name='" + name
				+ "' AND Password='" + password + "';";
		boolean passed= false;
		int testerId = 0;
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();
			passed = results.next();
			if (passed)
				testerId = results.getInt(1);
		} catch (SQLException e) {
			System.err.println("SQLException in authenticate()");
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		if (passed){ // authenticate succeeded
			// create sessionId
			int sessionId = 0;	
			String sessionUpdate = "INSERT INTO Session VALUES(null, '" + String.valueOf(testerId);
			sessionUpdate += "', true, '";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));  
			Date date = new Date();
			String datetime = dateFormat.format(date);
			sessionUpdate += datetime + "');";
			
			//System.out.println(sessionUpdate);
			
			String sessionQuery = "SELECT SessionId FROM Session WHERE TesterId=" + String.valueOf(testerId) + " AND AssignedTime=STR_TO_DATE('" + datetime + "', '%Y-%m-%d %H:%i:%s');";
			
			//System.out.println(sessionQuery);
			
			try {
				statement.execute(sessionUpdate);
				statement.execute(sessionQuery);
				ResultSet results = statement.getResultSet();
				results.next();
				sessionId = results.getInt(1);
				// System.out.println(sessionId);
			} catch (SQLException e) {
				System.err.println("SQLException in updating Session;");
				e.printStackTrace();
				System.exit(1);
			}
			
			
			Element success = XmlUtility.createSuccess(outputDoc, "login");
			root.appendChild(success);
			XmlUtility.addParameter(outputDoc, success, "username", name);
			XmlUtility.addParameter(outputDoc, success, "password", password);
			XmlUtility.addSimpleOutput(success, outputDoc, "sessionId", String.valueOf(sessionId));
		}
		else{ // failed
			Element failure = XmlUtility.createFailure(outputDoc, "login", "FailAuthenticate");
			root.appendChild(failure);
			XmlUtility.addParameter(outputDoc, failure, "username", name);
			XmlUtility.addParameter(outputDoc, failure, "password", password);
			
		}


	}
	


}
