/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.runtime.impl.java;

public class TestClassLoading {
    public static void main(String[] args) {
        /* check java version */
        String javaversionstring = System.getProperty("java.specification.version");
        if (javaversionstring == null) {
            System.out.println("Error: Unknow Java version.  Use Java 6 or newer.");
            System.exit(1);
        }
        double javaversion = Double.parseDouble(javaversionstring);
        if (javaversion < 1.6) {
            System.out.println("Error: Unsupported Java version.  Use Java 6 or newer.");
            System.exit(1);
        }

        /* check application class */
        try {
            String name = args[0];
            Class.forName(name);
        } catch (Throwable e) {
            System.exit(1);
        }

        System.exit(0);
    }
}
