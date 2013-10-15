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

package x10.regionarray;

final class VarRow extends Row {

    private val row:Rail[int];

    public def this(cols: Int, init: (Int)=>int) {
        super(cols);
        row = new Rail[int](cols, (i:long)=>init(i as int));
    }
    
    public def this(cols: Int) {
        super(cols);
        row = new Rail[int](cols);
    }
    
    def row():Rail[int] = row;
    public operator this(i:Int) = row()(i);

    public operator this(i:Int)=(v:int) = (row()(i) = v);
}
