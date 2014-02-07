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

import harness.x10Test;
import x10.util.Box;
class Test4[T] {T haszero} {
    val test:Test4[T] = null;
    val root = new Box[Test4[T]](test); // this lookup should be fine. 
}
class Test5[T] {
    val test:Test5[T] = null;
    val root = new Box[Test5[T]](test);
}
public class XTENLANG_2711 extends x10Test {

    public def run()=true;

    public static def main(Rail[String]) {
        new XTENLANG_2711().execute();
    }
}
