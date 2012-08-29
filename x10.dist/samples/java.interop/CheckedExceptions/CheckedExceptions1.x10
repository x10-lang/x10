/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import x10.io.Console;

class CheckedExceptions1 {

  public static def main(Array[String]):void {

    val ea1 = new CheckedThrowable("hi");
    Console.OUT.println(ea1);
    Console.OUT.println(ea1.toString());
    Console.OUT.println(ea1.typeName());
    Console.OUT.println(ea1 instanceof CheckedThrowable);

    val eb1 = new CheckedException("hi");
    Console.OUT.println(eb1);
    Console.OUT.println(eb1.toString());
    Console.OUT.println(eb1.typeName());
    Console.OUT.println(eb1 instanceof CheckedException);
    Console.OUT.println(eb1 instanceof CheckedThrowable);

    val ec1 = new Exception23("hi");
    Console.OUT.println(ec1);
    Console.OUT.println(ec1.toString());
    Console.OUT.println(ec1.typeName());
    Console.OUT.println(ec1 instanceof Exception23);
    Console.OUT.println(ec1 instanceof CheckedException);
    Console.OUT.println(ec1 instanceof CheckedThrowable);

    val ed1 = new Error23("hi");
    Console.OUT.println(ed1);
    Console.OUT.println(ed1.toString());
    Console.OUT.println(ed1.typeName());
    Console.OUT.println(ed1 instanceof Error23);
    Console.OUT.println(ed1 instanceof CheckedThrowable);

  }

}
