import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.*;
import net.sf.saxon.functions.*;

public class ext_simple
{
    /*
      http://codingwithpassion.blogspot.com/2011/03/saxon-xslt-java-example.html
    
      Simple transformation method.
      @param sourcePath - Absolute path to source xml file.
      @param xsltPath - Absolute path to xslt file.
      @param resultDir - Directory where you want to put resulting files.
    
      This requires Saxon 9.5 Works with HE. Will not work with Saxon 9.4 or earlier.

      Requires XSL file ext_simple.xsl.

      export CLASSPATH=$HOME/bin/saxon9he.jar:$CLASSPATH
      javac ext_simple.java
      java ext_simple
    */
    public static void simpleTransform(String sourcePath,
                                       String xsltPath)
    {
        TransformerFactory tFactory = TransformerFactory.newInstance();
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
    
        //Set saxon as transformer.
        System.setProperty("javax.xml.transform.TransformerFactory",
                           "net.sf.saxon.TransformerFactoryImpl");

        simpleTransform("dummy.xml", "ext_simple.xsl");
    }
}


