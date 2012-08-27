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

import harness.x10Test;

/*
 *     author: r lesniak 10/27/10
 *     extracted from SelfArrayReference_7.x10 by by Dave C
 */

public class XTENLANG_3103[T] extends x10Test {
    class C[U](a:Array[U]{rank==1}) {
    }

    public def run():Boolean {
        val myArTestIntNo123 : C[Int{self!=0}] = null;
        val arr /*: Array[Int{self!=0}]{rank==1}*/ = myArTestIntNo123.a;
        arr(Point.make(0));
        return true;
    }

    public static def main(args: Array[String](1)) {
    	new XTENLANG_3103[Int]().execute();
    }
}
