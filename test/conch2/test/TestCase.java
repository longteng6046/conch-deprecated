package conch2.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.mysql.jdbc.BufferRow;

public class TestCase {
	// the path to the xml file of the test, without the _in.xml or _out.xml part.
	private String xmlPath; 
	private String inputXML;
	private String oracleXML;
	
	public TestCase(String path) throws IOException {
		this.xmlPath = path;
		File inFile=new File(this.xmlPath + "_in.xml");
		File outFile= new File(this.xmlPath + "_out.xml");
		if (!inFile.exists()){
			throw new FileNotFoundException("Cannot find test case!");	
		}
		else if (!outFile.exists()){
			throw new FileNotFoundException("Cannot find test oracle!");	
		}
		
		String separator = System.getProperty("line.separator");
		
		BufferedReader bfReaderIn = new BufferedReader(new FileReader(inFile));
		BufferedReader bfReaderOut= new BufferedReader(new FileReader(outFile));
		StringBuilder sbIn = new StringBuilder();
		StringBuilder sbOut = new StringBuilder();
		
		String line = null;
		
		while ((line = bfReaderIn.readLine()) != null){
			sbIn.append(line);
			sbIn.append(separator);
		}
		
		while ((line = bfReaderOut.readLine()) != null){
			sbOut.append(line);
			sbOut.append(separator);
		}
		
		this.inputXML = sbIn.toString();
		this.oracleXML = sbOut.toString();
		
	}
	
	public String getInputString(){
		return this.inputXML;
	}
	
	public String getOracleString(){
		return this.oracleXML;
	}
	
}
