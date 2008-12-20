// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

public class PolyXform(V:XformMat, C:PolyMat) extends Xform {

    public def this(V:XformMat, C:PolyMat) {
        property(V, C);
    }

    public def $times(that:Xform):Xform {
        if (that instanceof PolyXform) {
            val p = that to PolyXform;
            return new PolyXform(this.V*p.V, this.C||p.C);
        } else {
            throw new UnsupportedOperationException(this.className() + ".xform(" + that.className() + ")");
        }
    }
}
