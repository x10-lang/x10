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
//OPTIONS: -WORK_STEALING=true

public class WSQuickSortTest {
    public def run():boolean {
    	val N = 1000 * 1000;
    	val r = new x10.util.Random();
    	val data = new Array[int](N, (int)=>r.nextInt(9999));
    	QuickSort.qsort(data, 0, N-1);

    	//check result
    	var a:int = 0;
        var b:int;
        var ok:boolean = true;
        for ([j] in 0..(N-1)) {
            b = data(j);
            ok &= (a <= b);
            a = b;
        }
        return ok;
    }

    public static def main(args:Rail[String]) {
        val r = new WSQuickSortTest().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
}
