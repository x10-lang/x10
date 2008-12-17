// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

// {T<:Row} needs fix to XTENLANG-300
public value class Mat[T](rows:nat, cols:nat) /*{T<:Row}*/ implements (nat)=>T {
    
    private val r: ValRail[T];
    
    public def this(rows: nat, cols: nat, init:(nat)=>T) {
        property(rows, cols);
        r = Rail.makeVal[T](rows, init);
    }
    
    public def this(cols: nat, r:ValRail[T]) {
        property(r.length, cols);
        this.r = r;
    }

    public def apply(i:nat) = r(i);

    public def iterator(): Iterator[T] = r.iterator();
}


