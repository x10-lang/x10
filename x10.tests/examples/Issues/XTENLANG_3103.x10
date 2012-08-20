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

/*
 *     author: r lesniak 10/27/10
 */

public class XTENLANG_3103[T]
{
    class C[U](a:Array[U]{rank==1}) {            

    }

    public static def run()
    {
        val myArTestIntNo123 : C[Int{self!=0}] = null;
        val arr /*: Array[Int{self!=0}]{rank==1}*/ = myArTestIntNo123.a;
        arr(Point.make(0));
    }
}
