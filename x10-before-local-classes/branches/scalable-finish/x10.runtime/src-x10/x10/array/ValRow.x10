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


