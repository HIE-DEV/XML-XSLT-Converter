# XML-XSLT-Converter
Converts XML into .html with an XSLT. Outputs validation/transformation error messages into .val files. Intended for use with FHIR.

To call from the command line, run:
    java -jar <absolute location of the .jar> <absolute file location of XML> <absolute file location of XSLT> <directory to return files to> <enable transformer/validation error capture [optional]>
  
For example (Windows):

    java -jar C:/FHIR/FHIRValidator_v1.jar C:/FHIR/Example.xml C:/FHIR/Example.xsl C:/FHIR/Return true

For example (Linux):

    java -jar /user/Documents/FHIR/FHIRValidator_v1.jar /user/Documents/FHIR/Example.xml /user/Documents/FHIR/Example.xsl /FHIR/Return true

You will need to create the directory structure before making the calls.

The output .html and .val files will have the same name as the incoming .xml. So for example:

    example.xml

Would appear as:

    example.html
    example.val
    
Errors in .val are plain-text new line delimited.

# Source code compilation

Instructions on how to 'compile' the Compiler.java source code into a 'Runnable JAR file' can be found in the /Source/ folder in the .readme file. Compiled on Eclipse Jee 2018-09.
