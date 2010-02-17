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
