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
 * The classic hello world program, with a twist - lists each place
 * Converted to 2.1 9/1/2010
 */
class HelloWholeWorld {
  public static def main(Array[String]):void {
     finish for (p in Place.places()) {
     	async at (p) Console.OUT.println("Hello World from place "+p.id);
     }
  }
}


