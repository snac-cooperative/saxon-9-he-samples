import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.*;
import net.sf.saxon.tree.iter.*;

// import net.sf.saxon.s9api.SequenceType;
// import net.sf.saxon.functions.*;
// import net.sf.saxon.lib.*;
// import net.sf.saxon.om.*;

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
  http://codingwithpassion.blogspot.com/2011/03/saxon-xslt-java-example.html
    
  Simple transformation method.
  @param sourcePath - Absolute path to source xml file.
  @param xsltPath - Absolute path to xslt file.
  @param resultDir - Directory where you want to put resulting files.
  
  This requires Saxon 9.5 Works with HE. Will not work with Saxon 9.4 or earlier.
  
  Requires XSL file ext_full.xsl. Creates ext_full_out.xml.
  
  export CLASSPATH=$HOME/bin/saxon9he.jar:$CLASSPATH
    
  javac ext_full.java
  java ext_full
*/

public class ext_full
{
    // private TransformerFactory getTransformerFactory() throws net.sf.saxon.trans.XPathException
    // {
    //   TransformerFactory tFactory = TransformerFactory.newInstance();
    //   if (tFactory instanceof TransformerFactoryImpl)
    //     {
    //       TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) tFactory;
    //       net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();
    //       saxonConfig.registerExtensionFunction(new dump_string());
    //     }
    //   return tFactory;
    // }


    // The extension must be registered with the configuration:

    // configuration.registerExtensionFunction(new ShiftLeft())
    // and it can then be called like this:

    // declare namespace eg="http://example.com/saxon-extension";
    //           for $i in 1 to 10 return eg:shift-left(2, $i)"

  // final?
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
        
        // call(net.sf.saxon.expr.XPathContext,net.sf.saxon.om.Sequence[])
        
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
                catch (SaxonApiException e) {}

                // print(arguments[0].getUnderlyingValue());

                // Works!
                return seq;

                // works, but not very useful
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


