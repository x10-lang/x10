// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;
import x10.io.StringWriter;

abstract value class Row(cols:nat) implements (nat)=>int {

    public abstract def apply(i:nat): int;
    public abstract def set(v:int, i:nat): int;

    protected def this(cols:nat) = property(cols);

    /**
     * print a row in both matrix and equation form
     */

    public def printInfo(ps: Printer, row:int) {
        ps.printf("[");
        for (var i: int = 0; i<cols; i++) {
            ps.printf("%4d", this(i));
            if (i==cols-2) ps.printf(" |");
        }
        ps.printf(" ]   ");
        printEqn(ps, " ", row);
        ps.printf("\n");
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
        if (c!=0||first) ps.printf((c>=0&&!first?"+":"") + c);
    }

    public def toString(): String {
        val os = new StringWriter();
        val ps = new Printer(os);
        printEqn(ps, "", 0);
        return os.toString();
    }


}
