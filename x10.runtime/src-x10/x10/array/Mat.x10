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

package x10.array;

import x10.io.Printer;

public abstract class Mat[+T](rows:Int, cols:Int){T <: Row} 
    implements (Int)=>T, Iterable[T] {

    private global val mat: ValRail[T];

    public def this(rows: Int, cols: Int, mat:ValRail[T]) {
        property(rows, cols);
        this.mat = mat;
    }

    public global def apply(i: Int) = mat(i);

    public global def iterator() = mat.iterator();

    public global def printInfo(ps: Printer, label: String): void {
        ps.printf("%s\n", label);
        var row:int = 0;
        for (r:Row in this) {
            ps.printf("    ");
            r.printInfo(ps, row++);
        }
    }
}
