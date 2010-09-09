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

import x10.io.Console;
/**
 * The classic hello world program, shows how to define a
 * method on an object, using the def syntax.
 * @author ??
 * @author vj 
 */

public class Hello {
    public static def main(Rail[String])  {
         Console.OUT.println("Hello, X10 world!");
         val h= new Hello();
         val myBool= h.myMethod();
         Console.OUT.println("The answer is: "+myBool);
    }
    def myMethod()=true;
}