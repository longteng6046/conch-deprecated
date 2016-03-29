package conch2.ts1;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import java.io.IOException;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import conch2.server.Server;
import conch2.test.TestCase;

public class Tc2_inirepository {
	Server server;
	TestCase testCase;
	@Test
	public void test() throws IOException, SAXException {
		
			server = new Server();
			
	    	testCase = new TestCase("./TestCases/ts2/tc2_inirepository");
		
			XMLUnit.setIgnoreAttributeOrder(true);
			XMLUnit.setIgnoreComments(true);
			XMLUnit.setIgnoreWhitespace(true);
			XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
			
			String input = this.testCase.getInputString();
			String oracle = this.testCase.getOracleString();
			String output = server.process(input);
			assertXMLEqual("Result", output, oracle);
		}
	}


