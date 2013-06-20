
import java.lang.reflect.Method;
import java.util.*;

// imports from ext_simple
import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.*;
// import net.sf.saxon.functions.*;

// Need these additional imports:
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.om.*;
import net.sf.saxon.TransformerFactoryImpl;

// Used by the proc builder

import java.io.BufferedReader;
// import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.util.ArrayList;
// import java.util.List;


// The signature for SequenceType conflicts with s9api, and value.SequenceType is required so we can get
// SINGLE_INTEGER so we must use the full classpath for SequenceType in the code.
import net.sf.saxon.value.*;

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

public class ext_shell
{
  
    private static class ExecShell extends ExtensionFunctionDefinition
    {
        @Override
        public StructuredQName getFunctionQName()
        {
            return new StructuredQName("eg", "http://example.com/saxon-extension", "shell");
        }
    
        @Override
        public int getMaximumNumberOfArguments()
        {
            // For most things, 10 or 20 is probably enough. Shells like bash are limited more by the buffer
            // size than the actual number of args.
            return 100;
        }
        @Override
        public net.sf.saxon.value.SequenceType[] getArgumentTypes()
        {
            return new net.sf.saxon.value.SequenceType[] {net.sf.saxon.value.SequenceType.SINGLE_STRING};
        }

        // I wonder which SequenceType is newest/correct? net.sf.saxon.value or net.sf.saxon.s91pi?
        // public net.sf.saxon.value.SequenceType getResultType(net.sf.saxon.value.SequenceType[] suppliedArgumentTypes)

        @Override
        public net.sf.saxon.value.SequenceType getResultType(net.sf.saxon.value.SequenceType[] suppliedArgumentTypes)
        {
            return net.sf.saxon.value.SequenceType.SINGLE_STRING;
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
                        // This works. It is fragile since it depends on fairly strict data types.
                        /* double arg_zero = ((Int64Value)arguments[0]).getDoubleValue(); */
                        /* double arg_one = ((Int64Value)arguments[1]).getDoubleValue(); */
                        // XdmItem new_item = new XdmAtomicValue((int)(arg_zero + arg_one));
                        // seq = new_item.getUnderlyingValue();

                        //Build command 
                        List<String> commands = new ArrayList<String>();

                        // Warning: Note: StringValue.toString() returns a string with double quotes around
                        // it. '/bin/cat' becomes "/bin/cat" where asString() returns /bin/cat.

                        //Add the arguments to the commands list.
                        for (net.sf.saxon.om.Sequence narg: arguments)
                            {
                                commands.add(((StringValue)narg).asString());
                            }
                        // System.out.println("Commands: " + commands);
                      
                        //Run macro on target
                        ProcessBuilder pb = new ProcessBuilder(commands);
                        pb.directory(new File("./"));
                        pb.redirectErrorStream(true);
                        Process process = pb.start();
                      
                        //Read output
                        StringBuilder out = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line = null, previous = null;
                        while ((line = br.readLine()) != null)
                            {
                                if (!line.equals(previous))
                                    {
                                        previous = line;
                                        out.append(line).append('\n');
                                        // System.out.println(line);
                                    }
                            }
                      
                        // We don't really handle success or failure. 
                        if (process.waitFor() == 0)
                            {
                                // System.out.println("Success!");
                            }
                        else
                            {
                                // Abnormal termination: Log command parameters and output and throw ExecutionException
                                System.err.println(commands);
                                System.err.println(out.toString());
                            }

                        XdmItem new_item = new XdmAtomicValue(out.toString());
                        seq = new_item.getUnderlyingValue();
                    }
                catch (Exception sae)
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
                                       String xsltPath)
    {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) tFactory;
        net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();
        saxonConfig.registerExtensionFunction(new ExecShell());

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

    public static class SortByString implements Comparator<Method>
    {
        public int compare(Method n1, Method n2)
        {
            // The easy sort is just toString(), but we could use a combination of getName() and other
            // methods. toString and toGenericString() seem to be identical.

            // return n1.toGenericString().compareTo(n2.toGenericString());
            return n1.getName().compareTo(n2.getName());
        }
    }


    /*
      This is here for debugging. It can be a challenge to follow the dizzying heirarchy of classes and method
      signatures. Docs are often somewhat hard to read as well. Now and then, it simplifies things to cut to
      the chase and just print out all available methods for a given object.

      Gets an array of all methods in a class hierarchy by recursing up to parent classes.
      @param objectClass the class
      @return the methods array

      Method[] arr = getAllMethodsInHierarchy(Int64Value.class);

      The "Class<?>" syntax is wildcard parameterized type aka generic type parameters.
    */
    public static Method[] getAllMethodsInHierarchy(Class<?> objectClass)
    {
        // The original example used a HashSet, but HashSet does not sort.
        // Set<Method> allMethods = new HashSet<Method>();

        // Accumulate the results in a TreeSet and use SortByString() as a comparator.
        TreeSet<Method> allMethods = new TreeSet<Method>(new SortByString());
        Method[] declaredMethods = objectClass.getDeclaredMethods();
        Method[] methods = objectClass.getMethods();
        if (objectClass.getSuperclass() != null)
            {
                Class<?> superClass = objectClass.getSuperclass();
                Method[] superClassMethods = getAllMethodsInHierarchy(superClass);
                allMethods.addAll(Arrays.asList(superClassMethods));
            }
        allMethods.addAll(Arrays.asList(declaredMethods));
        allMethods.addAll(Arrays.asList(methods));

        Method[] allm = allMethods.toArray(new Method[allMethods.size()]);

        // Due to the recursive nature of this func, our only choice is to return the list (as opposed to
        // printing, or something).
        return allm;
    }

    public static void main(final String[] args) throws IOException, InterruptedException
    {

        // Normally disabled. Enable for debugging and info.
        if (1 == 0)
            {
                // Doing this in two lines with a instantiated object works.
                // XdmAtomicValue xav = new XdmAtomicValue("stuff");
                // Method[] arr = getAllMethodsInHierarchy(xav.getClass());
                
                // Directly getting the class of the object's class is simpler.
                // Method[] arr = getAllMethodsInHierarchy(XdmAtomicValue.class);

                Class check = Int64Value.class;
                System.out.println("Class: " + check.getName() + "\n" + "Package: " + check.getPackage() +"\n");

                Method[] arr = getAllMethodsInHierarchy(check);

                for (Method met: arr)
                    {
                        // System.out.println(met.toString());
                        System.out.println(met.getName() + ": " + met.toGenericString());
                    }
                System.out.println();
        
            }

        simpleTransform("dummy.xml", "ext_shell.xsl");
    }

}


