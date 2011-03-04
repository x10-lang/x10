// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

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
            throw new UnsupportedOperationException(this.className() + ".xform(" + that.className() + ")");
        }
    }
}
