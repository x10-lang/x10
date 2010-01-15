package x10.core;

import x10.rtt.*;

public class ThrowableUtilities {

    public static ValRail<String> getStackTrace(Throwable t) {
        StackTraceElement[] elements = t.getStackTrace();
        String str[] = new String[elements.length];
        for (int i=0 ; i<elements.length ; ++i) {
            str[i] = elements[i].toString();
        }
        return new ValRail<String>(new RuntimeType<String>(String.class),str.length,(Object)str);
    }
}
