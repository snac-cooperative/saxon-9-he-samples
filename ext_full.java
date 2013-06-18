import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.*;
import net.sf.saxon.tree.iter.*;

import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.IntegerValue;

import net.sf.saxon.om.StructuredQName; 
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

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

  Simple transformation method.
  @param sourcePath - Absolute path to source xml file.
  @param xsltPath - Absolute path to xslt file.
  @param resultDir - Directory where you want to put resulting files.
  
  This requires Saxon 9.5 Works with HE. Will not work with Saxon 9.4 or earlier.
  
  Requires XSL file ext_full.xsl. Creates ext_full_out.xml.
  
  export CLASSPATH=$HOME/bin/saxon9he.jar:$CLASSPATH
    
  javac ext_full.java
  java ext_full

  Loosely derived from code at:
  http://codingwithpassion.blogspot.com/2011/03/saxon-xslt-java-example.html
    
*/

public class ext_full
{
  private static class ShiftLeft extends ExtensionFunctionDefinition
    {
        @Override
        public StructuredQName getFunctionQName()
        {
            return new StructuredQName("eg", "http://example.com/saxon-extension", "shift-left");
        }
    
        @Override
        public SequenceType[] getArgumentTypes()
        {
            return new SequenceType[] {SequenceType.SINGLE_INTEGER, SequenceType.SINGLE_INTEGER};
        }

        @Override
        public SequenceType getResultType(net.sf.saxon.value.SequenceType[] suppliedArgumentTypes)
        {
            return SequenceType.SINGLE_INTEGER;
        }

        @Override
        public ExtensionFunctionCall makeCallExpression()
        {
            return new sl_call();
        }
        
        private static class sl_call extends ExtensionFunctionCall
        {
            @Override
            public net.sf.saxon.om.Sequence call(XPathContext context, net.sf.saxon.om.Sequence[] arguments) throws XPathException
            {
                net.sf.saxon.om.Sequence seq  = null;
                try
                    {
                        XdmItem new_item = XdmItem.newAtomicValue("4242",
                                                                  ItemType.INTEGER);
                        seq = new_item.getUnderlyingValue();
                    }
                catch (SaxonApiException sae)
                  {
                    sae.printStackTrace();
                  }

                // Works!
                return seq;

                // Works also, but not very useful.
                // return arguments[0];
            }
        }
  }

    public static void simpleTransform(String sourcePath,
                                       String xsltPath,
                                       String resultDir)
    {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) tFactory;
        net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();
        saxonConfig.registerExtensionFunction(new ShiftLeft());

        try
            {
                Transformer transformer =
                    tFactory.newTransformer(new StreamSource(new File(xsltPath)));
                /*
                  Send output to stdout. If you want to write a file use new StreamResult(new File("foo.xml"))
                  or some variation of StreamResult.
                */
                transformer.transform(new StreamSource(new File(sourcePath)),
                                      new StreamResult(System.out));
            }
        catch (Exception e)
            {
                e.printStackTrace();
            }
    }
  
    public static void main(String[] args) 
    {
        //Set saxon as transformer.
        System.setProperty("javax.xml.transform.TransformerFactory",
                           "net.sf.saxon.TransformerFactoryImpl");

        simpleTransform("dummy.xml", "ext_full.xsl");
    }

}


