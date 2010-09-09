// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;

public abstract value class Mat[+T](rows:nat, cols:nat){T <: Row} 
    implements (nat)=>T, Iterable[T] {

    private val mat: ValRail[T];

    public def this(rows: nat, cols: nat, mat:ValRail[T]) {
        property(rows, cols);
        this.mat = mat;
    }

    public def apply(i: nat) = mat(i);

    public def iterator() = mat.iterator();

    public def printInfo(ps: Printer, label: String): void {
        ps.printf("%s\n", label);
        var row:int = 0;
        for (r:Row in this) {
            ps.printf("    ");
            r.printInfo(ps, row++);
        }
    }
}
