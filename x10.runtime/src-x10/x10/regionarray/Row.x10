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
import x10.io.StringWriter;


abstract class Row(cols:Int) implements (Int)=>Int {

    public abstract operator this(i:Int):Int;
    public abstract operator this(i:Int)=(v:Int):Int;

    protected def this(cols:Int) = property(cols);

    /**
     * print a row in both matrix and equation form
     */
    public def printInfo(ps:Printer, row:Int) {
        ps.print("[");
        for (var i:Int = 0n; i<cols; i++) {
            ps.print(this(i));
            if (i==cols-2n) ps.print(" |");
        }
        ps.print(" ]   ");
        printEqn(ps, " ", row);
        ps.println();
    }

    /**
     * print a row in equation form
     */
    def printEqn(ps:Printer, spc:String, row:Int) {
        var first:Boolean = true;
        ps.print("y" + row + " = ");
        for (var i:Int = 0n; i<cols-1n; i++) {
            val c = this(i);
            if (c==1n) {
                if (first)
                    ps.print("x" + i);
                else
                    ps.print("+x" + i);
            } else if (c==-1n)
                ps.print("-x" + i);
            else if (c!=0n)
                ps.print((c>=0n&&!first?"+":"") + c + "*x" + i + " ");
            if (c!=0n)
                first = false;
        }
        val c = this(cols-1n);
        if (c!=0n||first) ps.print((c>=0n&&!first?"+":"") + c);
    }

    public def toString():String {
        val os = new StringWriter();
        val ps = new Printer(os);
        printEqn(ps, "", 0n);
        return os.result();
    }

}
