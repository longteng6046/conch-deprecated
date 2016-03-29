
package conch2.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import conch2.server.Server;
import conch2.xml.XmlUtility;



/**
 * This is the main method that runs a set of test cases locally. It has the main
 * method that invokes the <code>process</code> method in the <b>Server</b> class.
 * 
 * @author Teng Long(tlong@cs.umd.edu)
 *
 */
public class TestRunner {
	/**
	 * 
	 * @param args all test suites(.list files) to be run
	 */
	public static void main(String[] args) {
		for (int i=0; i<args.length; i++){		
			String testList = args[i];
			System.out.println("Running test suite: " + testList);
			
			ArrayList<String> testCases = TestRunner.getTestList(testList);
			
			for (int j=0; j<testCases.size(); j++){
				
				boolean result = TestRunner.RunTestCase(testCases.get(j));
				System.out.print("\tRunning test case: " + testCases.get(j) + " ... ");
				if (result == true)
					System.out.println("Success.");
				else
					System.out.println("Failure.");
			}
			
			
		}
	}
	
	
	/**
	 * Retriving all test paths in a test suite.
	 * @param testSuite the path to the test suite file
	 * @return An arrayList that contains all test paths (excluding _in.xml or _out.xml)
	 */
	private static ArrayList<String> getTestList(String testSuite){
		ArrayList<String> result = new ArrayList<String>();
		
		String separator = System.getProperty("file.separator");
		int idx = testSuite.lastIndexOf(separator);
		String suitePath = null;
		if (idx == -1)
			suitePath = "." + separator;
		else{
			suitePath = testSuite.substring(0, idx);
			suitePath += separator;
		}
		
		//System.out.println(testSuite);
		//System.out.println(separator);
		
		try{
			File suiteFile = new File(testSuite);
			BufferedReader bf = new BufferedReader(new FileReader(suiteFile));
			String line = null;
			
			while ((line = bf.readLine()) != null && 
					line.trim().length() != 0 &&
					!line.trim().startsWith("#")
					){
				result.add(suitePath + line.trim());
			}
			
			
		} catch (Exception e) {
			System.err.println("Error in reading the test suite file.");
			e.printStackTrace();
		}

		return result;	
	}
	

	/**
	 * 
	 * @param testCase name of the test case(without the "_in.xml" part) to be run
	 * @return true/false, pass/fail
	 */
	private static boolean RunTestCase(String testCase){
		String inXmlFile = testCase + "_in.xml";
		String outXmlFile = testCase + "_out.xml";
		try{
			// read inout xml and oracle
			BufferedReader bfIn = new BufferedReader(new FileReader(new File(inXmlFile)));
			BufferedReader bfOut = new BufferedReader(new FileReader(new File(outXmlFile)));
			String line = null;
			String separator = System.getProperty("line.separator");
			StringBuilder sbIn = new StringBuilder();
			StringBuilder sbOut = new StringBuilder();
			
			while ((line = bfIn.readLine()) != null){
				sbIn.append(line);
				sbIn.append(separator);
			}
			
			while ((line = bfOut.readLine()) != null){
				sbOut.append(line);
				sbOut.append(separator);
			}
			
			String inXmlContent = sbIn.toString();
			String outXmlContent = sbOut.toString();
			
			// run test and obtain actual output
			String actualOut = (new Server()).process(inXmlContent);
			
			
			
			
			// compare output with oracle
			// return XmlUtility.compareXMLDocString(outXmlContent, actualOut);
			return XmlUtility.compareTestOutput(outXmlContent, actualOut, false);
		}
		catch (IOException e) {
			System.err.println("Problem openning test case: " + testCase);
			e.printStackTrace();
		}
		
		
		return false;
	}
}
