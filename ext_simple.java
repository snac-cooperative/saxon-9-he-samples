import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.*;
import net.sf.saxon.functions.*;

/*
  Author: Tom Laudeman
  The Institute for Advanced Technology in the Humanities
        
  Copyright 2013 University of Virginia. Licensed under the Educational Community License, Version 2.0 (the
  "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
  License at
        
  http://opensource.org/licenses/ECL-2.0
  http://www.osedu.org/licenses/ECL-2.0
        
  Unless required by applicable law or agreed to in writing, software distributed under the License is
  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
  the License for the specific language governing permissions and limitations under the License.

  This requires Saxon 9.5 Works with HE. Will not work with Saxon 9.4 or earlier.

  Requires XSL file ext_simple.xsl.

  export CLASSPATH=$HOME/bin/saxon9he.jar:$CLASSPATH
  javac ext_simple.java
  java ext_simple

  Loosely derived from code at:
  http://codingwithpassion.blogspot.com/2011/03/saxon-xslt-java-example.html
*/

public class ext_simple
{
  // Simple transformation method. Relative and absolute paths seem to work.
  // @param sourcePath - path to source xml file.
  // @param xsltPath - path to xslt script file.
    
    public static void simpleTransform(String sourcePath,
                                       String xsltPath)
    {
        try
            {
                ExtensionFunction sqrt = new ExtensionFunction()
                    {
                        public QName getName()
                        {
                            return new QName("http://example.com/saxon-extension", "sqrt");
                        }
                        
                        public SequenceType getResultType()
                        {
                            return SequenceType.makeSequenceType(ItemType.DOUBLE, OccurrenceIndicator.ONE);
                        }
                        
                        public net.sf.saxon.s9api.SequenceType[] getArgumentTypes()
                        {
                            return new SequenceType[]
                                {
                                    SequenceType.makeSequenceType(ItemType.DOUBLE, OccurrenceIndicator.ONE)
                                };
                        }
      
                        public XdmValue call(XdmValue[] arguments) throws SaxonApiException
                        {
                            double arg = ((XdmAtomicValue)arguments[0].itemAt(0)).getDoubleValue();
                            double result = Math.sqrt(arg);
                            System.out.println( "sqrt: " + result );
                            return new XdmAtomicValue(result);
                        }
                    };

                try
                    {
                        Processor proc = new Processor(false);
                        proc.registerExtensionFunction(sqrt);
                        XsltCompiler comp = proc.newXsltCompiler();
                        XsltExecutable exp = comp.compile(new StreamSource(new File(xsltPath)));
                        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File(sourcePath)));
                        Serializer out = new Serializer();
                        out.setOutputProperty(Serializer.Property.METHOD, "text");
                        out.setOutputProperty(Serializer.Property.INDENT, "no");
                        /*
                          We will write out to stream stdout. If you want to write to a file instead (and not
                          use result-document() from inside the xsl) then use out.setOutputFile(new File("foo.xml"));
                        */
                        out.setOutputStream(System.out);
                        XsltTransformer trans = exp.load();
                        trans.setInitialContextNode(source);
                        trans.setDestination(out);
                        trans.transform();
                    }
                catch (SaxonApiException sae)
                    {
                        sae.printStackTrace();
                        // throw sae;
                    }
            }
        catch (Exception e)
            {
                e.printStackTrace();
            }
    }
  
    public static void main(String[] args) 
    {

        simpleTransform("dummy.xml", "ext_simple.xsl");
    }
}


