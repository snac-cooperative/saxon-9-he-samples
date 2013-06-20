

This code contains complete, working examples of both ways to add extension functions to Saxon HE (Home
Edition). Version 9.5 is required since dramatic changes were made after 9.4.

Requirements:

Java

SaxonHE9 (version 9.5)

This has been tested on Linux. It probably works with MacOS and will need minor modifications to work with MS
Windows. If you are using Windows, I suggest that you get cygwin.

Let us assume you have installed saxon9he.jar in the the bin directory in your home directory, in other words:
~/bin or $HOME/bin.

Java is installed somewhere in a standard path. 

This session transcript snippet should explain the location of Java and the PATH environment variable:

    > which java
    /usr/bin/java

    > which javac
    /usr/bin/javac

    > echo $PATH
    /usr/lib64/qt-3.3/bin:/home/twl8n/perl5/perlbrew/bin:/home/twl8n/perl5/perlbrew/perls/perl-5.10.1/bin:/usr/local/bin:/bin:/usr/bin:/home/twl8n/bin:/usr/local/sbin:/usr/sbin:/sbin:/usr/java/latest/bin:/home/twl8n/bin:.



ext_full.java reads ext_full.xsl and dummy.xml. It has an extension function eg:add-two() where the eg
namespace is http://example.com/saxon-extension.

I added an export for CLASSPATH to my .zshrc (bash users would use .bashrc), but I'll explicitly show the
export command below.

This is how I run the program:

    export CLASSPATH=$HOME/bin/saxon9he.jar:$CLASSPATH
    javac ext_full.java
    java ext_full

A session transcript:

    > java ext_full
    format-dateTime: 2013-06-20 09:58:30
    date: 2013-06-20T09:58:30.626-04:00
    add-two: 77



ext_simple.java reads ext_simple.xsl and dummy.xml. It has an extension function eg:sqrt() where the eg
namespace is http://example.com/saxon-extension just as above.

    export CLASSPATH=$HOME/bin/saxon9he.jar:$CLASSPATH
    javac ext_simple.java
    java ext_simple


A session transcript:

    > java ext_simple
    sqrt: 7.0
    format-dateTime: 2013-06-20 09:59:01
    date: 2013-06-20T09:59:01.144-04:00
    sqrt: 7

The hierarchy of inheritance of Java classes and methods is often difficult to navigate, and this has been a
big problem for me with Saxon, so I find it useful to be able to print out a concise list of all available
methods for a given class. generic_demo.java shows how this is done.

    export CLASSPATH=$HOME/bin/saxon9he.jar:$CLASSPATH
    javac generic_demo.java
    java generic_demo

Here is a session transcript with an elipsis (...) where I removed some of the output in the interest of
brevity:

    > java generic_demo
    Class: java.lang.Class
    Package: package java.lang, Java Platform API Specification, version 1.6

    access$100: static boolean java.lang.Class.access$100(java.lang.Object[],java.lang.Object[])
    access$202: static boolean java.lang.Class.access$202(boolean)
    access$302: static boolean java.lang.Class.access$302(boolean)
    addAll: private static void java.lang.Class.addAll(java.util.Collection,java.lang.reflect.Field[])
    argumentTypesToString: private static java.lang.String java.lang.Class.argumentTypesToString(java.lang.Class[])
    arrayContentsEq: private static boolean java.lang.Class.arrayContentsEq(java.lang.Object[],java.lang.Object[])
    asSubclass: public <U> java.lang.Class<? extends U> java.lang.Class.asSubclass(java.lang.Class<U>)
    ...
    toClass: private static java.lang.Class java.lang.Class.toClass(java.lang.reflect.Type)
    toString: public java.lang.String java.lang.Object.toString()
    wait: public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException


