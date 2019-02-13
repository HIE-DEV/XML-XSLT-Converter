package Compiler;

import java.io.*;
import java.lang.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.util.regex.*;

public class Compiler {
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("FHIR Converter and XSLT validator. Version 1.");
		/* Argument definitions
		 * 
		 *  Arg[0] = Location of the XML file
		 *  Arg[1] = Location of the XSLT file
		 *  Arg[2] = Directory to output the HTML and validation script to
		 *  Arg[3] = (Optional) argument specifying whether or not to use error validation (default: true) */
		
		if(args.length >= 3 && args.length <= 4)
		{
			System.out.println("Beginning XML transformation.");
			String[] Response = null;
			boolean OptionalErrorMode = true;
			
			if(args.length == 3)
			{
				Response = TransformXMLWithXSLTWithFilenames(args[0],args[1],OptionalErrorMode);
			}
			else
			{
				String Lower = args[3].toLowerCase();
				
				if(Lower == "false" || Lower == "f" || Lower == "0" || Lower == "n" || Lower == "no")
				{
					OptionalErrorMode = false;
				}
				
				Response = TransformXMLWithXSLTWithFilenames(args[0],args[1],OptionalErrorMode);
			}
			
			System.out.println("Transformation processed.");
			System.out.println("Writing to file.");
			
			System.out.println("GetEndFilename.");
			String EndFilename = GetEndFilename(args[0]);
			System.out.println("GetFilenameWithoutExtension.");
			String EndFilenameWithoutExt = GetFilenameWithoutExtension(EndFilename);
			System.out.println("FullFilename.");
			String FullFilename = new String(args[2]+"/"+EndFilenameWithoutExt);
			
			System.out.println("Writing out HTML file.");
			
			String FullFilenameHTML = new String(FullFilename+".html");
			FileOutputStream TargetHTMLOutput = new FileOutputStream(FullFilenameHTML);
			TargetHTMLOutput.write(Response[0].getBytes("UTF-8"));
			TargetHTMLOutput.close();
			
			System.out.println("HTML file written.");
			
			if(OptionalErrorMode)
			{
				System.out.println("Writing out validation file.");
				String FullFilenameValidation = new String(FullFilename+".val");
				FileOutputStream TargetValidationOutput = new FileOutputStream(FullFilenameValidation);
				TargetValidationOutput.write(Response[1].getBytes("UTF-8"));
				TargetValidationOutput.close();
				System.out.println("Validation written.");
				
				FullFilenameValidation = null;
				TargetValidationOutput = null;
			}
			
			System.out.println("Setting variables to null in preparation for clean-up.");
			Response = null;
			EndFilename = null;
			EndFilenameWithoutExt = null;
			FullFilename = null;
			FullFilenameHTML = null;
			TargetHTMLOutput = null;
			System.out.println("Calling garbage collector to clean-up.");
			System.gc();
			System.out.println("Done.");
			System.exit(0);
		}
		else
		{
			/*Invalid number of arguments provided*/
			System.out.println("Incorrect number of arguments. Minimum is 3, maximum is 4.");
			System.out.println("args[0] = File location of XML to convert to HTML/Validate");
			System.out.println("args[1] = File location of the XSLT to use in conversion");
			System.out.println("args[2] = Directory to return the .html and .val files to");
			System.out.println("args[3] = (Optional) specify whether to enable validation (default: true)");
			System.exit(1);
		}
    }
	
	public static String GetFilenameWithoutExtension(String IncomingString) throws Exception
	{
		if(IncomingString.indexOf(".") < 0)
		{
			return IncomingString;
		}
		return IncomingString.substring(0,IncomingString.lastIndexOf("."));
	}
	
	public static int DetectSlashes(String IncomingString) throws Exception
	{
		
		int DetectType = 0;
		
		if(IncomingString.indexOf("/") > -1)
		{
			DetectType = 1;
		}
		
		if(IncomingString.indexOf("\\") > -1)
		{
			DetectType += 2;
		}
		
		//0 - no slashes
		//1 - Forward slash
		//2 - Backslash
		//3 - Both forward and backslash
		return DetectType;
	}
	
	public static String NormaliseSlashes(String IncomingString,boolean OptionalForwardSlashToBack) throws Exception
	{
		String Temp = IncomingString;
		
		if(!OptionalForwardSlashToBack)
		{
			while(Temp.indexOf("\\") > -1){Temp = Temp.replace("\\","/");}
		}
		else
		{
			while(Temp.indexOf("/") > -1){Temp = Temp.replace("/","\\");}
		}
		return Temp;
	}
	
	public static String NormaliseSlashes(String IncomingString) throws Exception
	{ 
		return NormaliseSlashes(IncomingString,false);
	}
		
	public static String GetEndFilename(String IncomingString) throws Exception
	{
		
		String TemporaryFilename = null;	
		int DetectSlash = DetectSlashes(IncomingString);
		String[] SplitNames = null;
		
		if(DetectSlash == 3)
		{
			//Both types of slashes detected, normalise
			TemporaryFilename = NormaliseSlashes(IncomingString);
			SplitNames = TemporaryFilename.split(Pattern.quote("\\"));
		}
		else if(DetectSlash == 1)
		{
			//Forward slash "/" only
			SplitNames = IncomingString.split(Pattern.quote("/"));
		}
		else if(DetectSlash == 2)
		{
			//Backslash "\\" only
			SplitNames = IncomingString.split(Pattern.quote("\\"));
		}
		else
		{
			//No slash of any type detected
			return IncomingString;
		}
		
		return SplitNames[SplitNames.length-1];
		
	}
	
	public static String[] TransformXMLWithXSLTWithFilenames(String XMLFilename, String XSLTFilename, boolean OptionalEnableErrorCapture) throws Exception
	{
		File XMLFile = new File(XMLFilename);
		File XSLTFile = new File(XSLTFilename);

		return TransformXMLWithXSLT(new StreamSource(XMLFile), new StreamSource(XSLTFile),OptionalEnableErrorCapture);	
	}
	
	public static String[] TransformXMLWithXSLTWithStrings(String IncomingXMLAsString, String IncomingXSLTAsString, boolean OptionalEnableErrorCapture) throws Exception
	{
		StringReader XMLSR = new java.io.StringReader(IncomingXMLAsString);
		StringReader XSLTSR = new java.io.StringReader(IncomingXSLTAsString);
		
		return TransformXMLWithXSLT(new StreamSource(XMLSR), new StreamSource(XSLTSR),OptionalEnableErrorCapture);
	}
	
	public static String[] TransformXMLWithXSLT(Source XMLStream, Source XSLTStream, boolean OptionalEnableErrorCapture) throws Exception
	{
		//We declare all the error handling variable externally so they're accessible from any point in this function
		ByteArrayOutputStream BAOS = null;
		PrintStream CapturePrintStream = null;
		PrintStream OldErr = null;
		String BAOSData = null;
		
		StringWriter TransformationResult = new java.io.StringWriter();
		
		if(!!OptionalEnableErrorCapture)
		{
			BAOS = new java.io.ByteArrayOutputStream();
			CapturePrintStream = new java.io.PrintStream(BAOS, true, "UTF-8");
			
			OldErr = System.err;
			System.setErr(CapturePrintStream);
		}
		
		TransformerFactory TransformerNewInstance = TransformerFactory.newInstance();
		Transformer InDisguise = TransformerNewInstance.newTransformer(XSLTStream);
		InDisguise.transform(XMLStream, new StreamResult(TransformationResult));
		
		if(!!OptionalEnableErrorCapture)
		{
			System.setErr(OldErr);
			BAOSData = new String(BAOS.toByteArray(), "UTF-8");
		}
		
		String[] JavaStringArray = new String[2];
		
		StringBuffer StringBuffered = TransformationResult.getBuffer(); 

		JavaStringArray[0] = StringBuffered.toString();
		JavaStringArray[1] = BAOSData;
		
		return JavaStringArray;
	}

}
