// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

public class ValRow extends Row {

    global private val row: ValRail[int];

    public def this(row: ValRail[int]) {
        super(row.length);
        this.row = row;
    }

    public def this(row: Rail[int]!) {
        this(row.length, (i:Int) => row(i));
    }

    public def this(cols: Int, init: (Int)=>int) {
        super(cols);
        row = ValRail.make[int](cols, init);
    }
    
    public safe global def apply(i:Int) = row(i);
    
    public safe global def set(v:int, i:Int):int {
        throw new IllegalOperationException("ValRow.set");
    }
}


