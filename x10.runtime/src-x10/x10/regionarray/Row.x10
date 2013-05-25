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

package x10.regionarray;

import x10.io.Printer;
import x10.io.StringWriter;


abstract class Row(cols:Int) implements (Int)=>int {

    public abstract operator this(i:Int): int;
    public abstract operator this(i:Int)=(v:int): int;

    protected def this(cols:Int) = property(cols);

    /**
     * print a row in both matrix and equation form
     */

    public def printInfo(ps: Printer, row:int) {
        ps.print("[");
        for (var i: int = 0; i<cols; i++) {
            ps.print(this(i));
            if (i==cols-2) ps.print(" |");
        }
        ps.print(" ]   ");
        printEqn(ps, " ", row);
        ps.println();
    }

    /**
     * print a row in equation form
     */

    def printEqn(ps: Printer, spc: String, row: int) {
        var first: boolean = true;
        ps.print("y" + row + " = ");
        for (var i: int = 0; i<cols-1; i++) {
            val c = this(i);
            if (c==1) {
                if (first)
                    ps.print("x" + i);
                else
                    ps.print("+x" + i);
            } else if (c==-1)
                ps.print("-x" + i);
            else if (c!=0)
                ps.print((c>=0&&!first?"+":"") + c + "*x" + i + " ");
            if (c!=0)
                first = false;
        }
        val c = this(cols-1);
        if (c!=0||first) ps.print((c>=0&&!first?"+":"") + c);
    }

    public def toString(): String {
        val os = new StringWriter();
        val ps = new Printer(os);
        printEqn(ps, "", 0);
        return os.result();
    }


}
