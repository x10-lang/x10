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

import harness.x10Test;

/**
 * Test that the closures initializing each array element are called
 * within separate asyncs.
 */

public class XTENLANG_587  extends x10Test {
    public static def main(Array[String](1)) {
        new XTENLANG_587().execute();
    }
    public def run()  {
    	class Cell {
    		var f:Int=1;
    	}
    	val cell = new Cell();
    	val cell2 = new Cell();
    	val  x = new Array[Int](0..1, ([i]:Point) =>
            {
                when (cell.f==i) {
                    cell.f = (i+1)%2;
                    cell2.f = i;
                }
                i
            }
            );
    	return cell2.f == 0;
    }
}