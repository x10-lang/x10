

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
import x10.util.*;

public class FunInference1 extends x10Test {
    public static def map[A,C](L: List[A], f:(A)=>C) : List[C] {
        val ret = new ArrayList[C](L.size());
        for(x in L) ret.add(f(x));
        return ret;
        }
     static class F(s:String){}
     static def b(L : List[F])  = map(L, (x:F)=>x.s);
    public def run()=true;

    public static def main(Rail[String]){
        new FunInference1().execute();
    }
}
