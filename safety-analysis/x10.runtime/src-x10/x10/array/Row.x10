// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;
import x10.io.StringWriter;


abstract class Row(cols:nat) implements (nat)=>int {

    public abstract global def apply(i:nat): int;
    public abstract global def set(v:int, i:nat): int;

    protected def this(cols:nat) = property(cols);

    /**
     * print a row in both matrix and equation form
     */

    public global def printInfo(ps: Printer, row:int) {
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

    global def printEqn(ps: Printer, spc: String, row: int) {
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

    public global def toString(): String {
        val os = new StringWriter();
        val ps = new Printer(os);
        printEqn(ps, "", 0);
        return os.toString();
    }


}
