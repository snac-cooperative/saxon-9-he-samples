import java.lang.reflect.Method;
import java.util.*;

public class generic_demo
{

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
      This is here for debugging. It can be a challenge to follow the dizzying hierarchy of classes and method
      signatures. Docs are often somewhat hard to read as well. Now and then, it simplifies things to cut to
      the chase and just print out all available methods for a given object.

      Gets an array of all methods in a class hierarchy by recursing up to parent classes.
      @param objectClass the class
      @return the methods array

      Method[] arr = getAllMethodsInHierarchy(String.class);

      The "Class<?>" syntax is wildcard parameterized type aka generic type parameters.
    */
    public static Method[] getAllMethodsInHierarchy(Class<?> objectClass)
    {
        // The original example used a HashSet, but they don't sort well.
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

    public static void main(String[] args) 
    {
        // Doing this in two lines with a instantiated object works.
        // XdmAtomicValue xav = new XdmAtomicValue("stuff");
        // Method[] arr = getAllMethodsInHierarchy(xav.getClass());
                
        // Directly getting the class of the object's class is simpler.
        // Method[] arr = getAllMethodsInHierarchy(XdmAtomicValue.class);

        Class check = Class.class;

        Method[] arr = getAllMethodsInHierarchy(check);

        System.out.println("Class: " + check.getName() + "\n" + "Package: " + check.getPackage() +"\n");

        for (Method met: arr)
            {
                System.out.println(met.getName() + ": " + met.toGenericString());
            }
        System.out.println();
    }

}


