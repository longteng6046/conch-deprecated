package conch2.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import conch2.server.Server;

/**
 * It runs the <code>process</code> method in Server locally.
 * 
 * @author Teng Long(tlong@cs.umd.edu)
 *
 */
public class LocalRunner {
	public static void main(String[] args) {
		String inputString, outputString;
		try{
		//	File inFile = new File("./TestCases/ts1/tc1_login_in.xml");
			File inFile = new File("./TestCases/ts7/tc7_addTestCases_in.xml");
		//	File inFile = new File("./TestCases/ts6/tc6_addTestSuites_in.xml");
		//	File inFile = new File("./TestCases/ts3/tc3_addComVendor_in.xml");
		//	File inFile = new File("./TestCases/ts4/tc4_getComp_in.xml");
		//	File inFile = new File("./TestCases/ts5/tc5_getCDG_in.xml");
		//	File inFile = new File("./TestCases/ts2/tc2_inirepository_in.xml");
			BufferedReader bfReader = new BufferedReader(new FileReader(inFile));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String separator = System.getProperty("line.separator");
						
			while ((line = bfReader.readLine()) != null){
				stringBuilder.append(line);
				stringBuilder.append(separator);
			}
			
			inputString = stringBuilder.toString();
			
			
			Server server = new Server();
			outputString = server.process(inputString);
			System.out.println(outputString);
			
		}
		catch (IOException e) {
			System.err.println("IO error: failure reading input XML file.");
		}
		
		
	}

}
