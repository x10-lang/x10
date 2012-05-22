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

import x10.interop.java.Throws;

public class XClass extends JClass {
    static def testJThrows():void @Throws[java.lang.Throwable] {
        val x = new XClass();
        x.jthrows();
    }
    public static def main(args:Array[String](1)):void {
        val x = new XClass();
        val o = Console.OUT;
        o.println(x.jint());
        o.println(x.jobject());
        o.println(x.jstring());
        o.println(x.jinteger());
        o.println(x.jclass());
	o.println(x.typeName());
	o.println(x.equals(x));
	o.println(x.hashCode());
	o.println(x.toString());
	o.println(x instanceof Any);
    }
}
