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

package x10.regionarray;

import x10.io.Printer;

abstract class Mat[T](rows:Int, cols:Int){T <: Row} 
    implements (Int)=>T, Iterable[T] {

    private val mat:Rail[T];

    public def this(rows:Int, cols:Int, mat:Rail[T]) {
        property(rows, cols);
        this.mat = mat;
    }

    public operator this(i:Int) = mat(i);

    public def iterator() = mat.iterator();

    public def printInfo(ps:Printer, label:String): void {
        ps.printf("%s\n", label);
        var row:Int = 0n;
        for (r:Row in this) {
            ps.printf("    ");
            r.printInfo(ps, row++);
        }
    }
}
