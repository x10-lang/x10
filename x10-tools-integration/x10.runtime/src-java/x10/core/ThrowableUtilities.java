/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import x10.rtt.RuntimeType;

public class ThrowableUtilities {

    public static ValRail<String> getStackTrace(Throwable t) {
        StackTraceElement[] elements = t.getStackTrace();
        String str[] = new String[elements.length];
        for (int i=0 ; i<elements.length ; ++i) {
            str[i] = elements[i].toString();
        }
        return new ValRail<String>(new RuntimeType<String>(String.class),str.length,(Object)str);
    }
    
    public static void printStackTrace(Throwable t, Object/*x10.io.Printer*/ p) {
        Class<?> x10_io_Printer = null;
        try {
            x10_io_Printer = Class.forName("x10.io.Printer");
        } catch (Exception e) {
        }
        Method printStackTrace = null;
        if (x10_io_Printer != null) {
            try {
                printStackTrace = t.getClass().getMethod("printStackTrace", x10_io_Printer);
            } catch (Exception e) {
            }
        }
        if (printStackTrace != null) {
            try {
                printStackTrace.invoke(t, p);
            } catch (Exception e) {
            }
        } else {
            try {
                Method getNativeOutputStream = x10_io_Printer.getMethod("getNativeOutputStream");
                PrintWriter printWriter = new java.io.PrintWriter((OutputStream) getNativeOutputStream.invoke(p));
                t.printStackTrace(printWriter);
            } catch (Exception e) {
            }
        }
    }
}
