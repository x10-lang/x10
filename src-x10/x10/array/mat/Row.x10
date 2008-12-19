// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

public abstract value class Row(cols:nat) implements (nat)=>int {

    public abstract def apply(i:nat): int;
    public abstract def set(v:int, i:nat): int;

    def this(cols:nat) = property(cols);
}
