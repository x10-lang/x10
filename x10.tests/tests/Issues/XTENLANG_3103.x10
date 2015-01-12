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

import harness.x10Test;
import x10.regionarray.*;

/**
 *     author: r lesniak 10/27/10
 *     extracted from SelfArrayReference_7.x10 by by Dave C
 */
public class XTENLANG_3103[T] extends x10Test {
    class C[U](a:Array[U]{rank==1}) {
    }

    public static def runIt() {
        val myArTestIntNo123 : XTENLANG_3103[String].C[Int{self!=0n}] = null;
        val arr = myArTestIntNo123.a;
        // Used to ICE here
        arr(Point.make(0));
    }

    public def run():Boolean {
        try {
            runIt();
        } catch (e:NullPointerException) {
        }
        return true;
    }

    public static def main(args: Rail[String]) {
    	new XTENLANG_3103[Int]().execute();
    }
}
