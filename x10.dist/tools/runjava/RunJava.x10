/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import x10.interop.Java;


public class RunJava {
    public static def main(args:Rail[String]):void {
        try {
            if (args.size < 1) {
                Console.ERR.println("Please specify the Java Main class");
                return;
            }
                
            val className = args(0);
            val numRestArgs = args.size - 1L;
            val restArgs =  new Rail[String](numRestArgs);
            Rail.copy(args, 1L, restArgs, 0L, numRestArgs);
            val javaArgs = Java.convert(restArgs);
                
            val mainClass = java.lang.Class.forName(className);
            val stringArrayClass = java.lang.Class.forName("[Ljava.lang.String;");
            val argsClasses = Java.convert([stringArrayClass]);
            val javaArgsArray = Java.convert([javaArgs]);
            val mainMethod = mainClass.getMethod("main", argsClasses);

            mainMethod.invoke(null, javaArgsArray);
        } catch (e:java.lang.reflect.InvocationTargetException) {
            var cause:CheckedThrowable = e.getCheckedCause();
            while (cause instanceof x10.lang.WrappedThrowable) {
                cause = cause.getCheckedCause();
            }
            cause.printStackTrace();
        } catch (e:CheckedThrowable) {
            e.printStackTrace();
        }
    }
}
