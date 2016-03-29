package conch2.ts1;

import static org.custommonkey.xmlunit.XMLAssert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import conch2.server.Server;
import conch2.test.TestCase;
import conch2.xml.XmlUtility;

public class Tc1_login {

	Server server;
	TestCase testCase;
	@Before
	public void setUp() throws Exception {
		server = new Server();

		
		/******* Set TestCase ******/
		testCase = new TestCase("./TestCases/ts1/tc1_login");
	//	testCase = new TestCase("./TestCases/ts2/tc2_inirepository");
		
	//	testCase = new TestCase("./TestCases/ts3/tc3_addComVendor");
		// setup XMLUnit options
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		
	}

	

	@Test 
	public void test() throws SAXException, IOException {
		String input = this.testCase.getInputString();
		String oracle = this.testCase.getOracleString();
		String output = server.process(input);

		ArrayList<String> whiteList = new ArrayList<String>();
		whiteList.add("sessionId");
		output = XmlUtility.clearXMLString(output, whiteList);
		oracle = XmlUtility.clearXMLString(oracle, whiteList);

		//oracle = oracle.replace("\n", "");
		//oracle = oracle.replace("\t", "");
		
		
		
		// Diff d = new Diff(output, oracle);
		// DetailedDiff dd = new DetailedDiff(d);
		assertXMLEqual("Result", output, oracle);
		

		
		// to find the differences
		// System.out.println(output);
		// System.out.println(oracle);
		// List l = dd.getAllDifferences();
		// System.out.println(l);
	}

}
