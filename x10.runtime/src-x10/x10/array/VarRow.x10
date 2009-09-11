// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

//  why is this a value class?
// Now we cant statically describe the place at which row is located.
final value class VarRow extends Row {

    private val row: Rail[int];

    public def this(cols: nat, init: (nat)=>int) {
        super(cols);
        row = Rail.makeVar[int](cols, init);
    }
    
    public def this(cols: nat) {
        super(cols);
        row = Rail.makeVar[int](cols);
    }
    
    def row() = row as Rail[int]{self.at(here)};
    public safe def apply(i:nat) = row()(i);

    public safe def set(v:int, i:nat) = (row()(i) = v);
}
