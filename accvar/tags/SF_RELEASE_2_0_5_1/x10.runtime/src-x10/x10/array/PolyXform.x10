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

public class PolyXform(E:PolyMat, T:XformMat) extends Xform {

    public def this(E:PolyMat, T:XformMat) {
        property(E, T);
    }

    public operator this * (that:Xform!):Xform {
        if (that instanceof PolyXform) {
            val p = that as PolyXform!;
            return new PolyXform(this.E||p.E, this.T*p.T);
        } else {
            throw new UnsupportedOperationException(this.typeName() + ".xform(" + that.typeName() + ")");
        }
    }
}
