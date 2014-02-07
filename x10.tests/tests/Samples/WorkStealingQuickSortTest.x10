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
//OPTIONS: -WORK_STEALING=true
// SOURCEPATH: x10.dist/samples/work-stealing

import harness.x10Test;

public class WorkStealingQuickSortTest extends x10Test {
    public def run():boolean {
    	val N = 1000 * 1000;
    	val r = new x10.util.Random();
        //val data = new Array[int](N, (int)=>r.nextInt(9999));
        //Use loop to create data before FIX XTENLANG-2300
        val data:Rail[int] = new Rail[int](N);
        for(var j:Int = 0n; j < N; j++) { data(j) = r.nextInt(9999n);}
    	QuickSort.qsort(data, 0, N-1);

    	//check result
    	var a:int = 0n;
        var b:int;
        var ok:boolean = true;
        for (j in 0..(N-1)) {
            b = data(j);
            ok &= (a <= b);
            a = b;
        }
        return ok;
    }

    public static def main(args:Rail[String]) {
        new WorkStealingQuickSortTest().execute();
    }
}
