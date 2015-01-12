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

// SOURCEPATH: x10.dist/samples

public class QSortTest extends x10Test {
    public def run():boolean {
        val r = new x10.util.Random();
        val N = 10000;
        val data = new Rail[int](N, (long)=>r.nextInt(9999n));
        QSort.qsort(data, 0, N-1);
        for (i in 1..(N-1)) {
            if (data(i-1) > data(i)) {
                Console.OUT.println("Failed to sort: index "+i);
                return false;
            }
        }
        return true;
    }

    public static def main(args:Rail[String]) {
	    new QSortTest().execute();
    }
}
