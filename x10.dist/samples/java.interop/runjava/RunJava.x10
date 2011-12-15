/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

import x10.interop.Java;
import x10.interop.java.Throws;
import x10.runtime.impl.java.InteropFuns;

public class RunJava {
    public static def main(args: Array[String](1)) @Throws[java.lang.Throwable] {
        if (args.size < 1) {
            Console.ERR.println("Please specify the Java Main class");
            return;
        }
                
        val className:java.lang.String = Java.convert(args(0));
        val numRestArgs = args.size - 1;
        val javaArgs:Java.array[java.lang.String] = Java.newArray[java.lang.String](numRestArgs);
                
        for (i in 0..(numRestArgs - 1)) {
            javaArgs(i) = Java.convert(args(i+1));
        }
                
        val mainClass = java.lang.Class.forName(className);
        val stringArrayClass = java.lang.Class.forName("[Ljava.lang.String;");
        val argsClasses = Java.convert([stringArrayClass]);
        val javaArgsArray = Java.convert([javaArgs]);
        val mainMethod = mainClass.getMethod("main", argsClasses);

        InteropFuns.invokeAndUnwrapExceptions(mainMethod, null, javaArgsArray);
    }
}
