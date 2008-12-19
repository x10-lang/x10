// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

// {T<:Row} needs fix to XTENLANG-300
public abstract value class Mat[+T](rows:nat, cols:nat) implements (nat)=>T {
    public def this(rows: nat, cols: nat) = property(rows, cols);
    abstract public def apply(i:nat): T;
    abstract public def iterator(): Iterator[T];
}
