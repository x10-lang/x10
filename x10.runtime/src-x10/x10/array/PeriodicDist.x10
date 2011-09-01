/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2010-2011.
 */

package x10.array;

import x10.compiler.Inline;

/**
 * A periodic dist decorates a standard X10 dist by implementing 
 * periodic boundary conditions, in which elements at each edge of
 * the region are considered to be neighbours, and indexes that fall
 * outside the "home" region in any dimension are wrapped around modulo 
 * the size of the region in that dimension.
 */
public final class PeriodicDist extends Dist {
    val baseDist:Dist{rank==this.rank};

    val min:Array[Int](1); /* will be null if rank<5 */
    val min0:Int;
    val min1:Int;
    val min2:Int;
    val min3:Int;

    val delta:Array[Int](1); /* will be null if rank<5 */
    val delta0:Int;
    val delta1:Int;
    val delta2:Int;
    val delta3:Int;

   public def this(base : Dist) : PeriodicDist{self.rank==base.rank} {
        super(base.region);
        baseDist = base;
        val reg = base.region;
        if (reg.isEmpty()) {
            min0 = min1 = min2 = min3 = 0;
            delta0 = delta1 = delta2 = delta3 = 0;
            if (rank>4) {
                min = new Array[Int](rank, (Int)=>0);
                delta = new Array[Int](rank, (Int)=>0);
            } else {
                min = delta = null;
            }
        } else {
            if (rank>4) {
                val tmpMin = new Array[Int](rank, (i:Int) => reg.min(i));
                min = tmpMin;
                delta = new Array[Int](rank, (i:Int) => reg.max(i) - tmpMin(i) +1);
            } else {
                min = null;
                delta = null;
            }

            min0 = reg.min(0);
            delta0 = reg.max(0) - min0 + 1;

            if (rank > 1) {
                min1 = reg.min(1);
                delta1 = reg.max(1) - min1 + 1;
            } else {
                min1 = delta1 = 0;
            }

            if (rank > 2) {
                min2 = reg.min(2);
                delta2 = reg.max(2) - min2 + 1;
            } else {
                min2 = delta2 = 0;
            }

            if (rank > 3) {
                min3 = reg.min(3);
                delta3 = reg.max(3) - min3 + 1;
            } else {
                min3 = delta3 = 0;
            }
        }
    }

    private @Inline def getPeriodicIndex(index : Int, dim : Int) : Int {
        var regionMin : Int = 0;
        if (rank < 5) {
            switch (dim) {
                case 0:
                    regionMin = min0;
                    break;
                case 1:
                    regionMin = min1;
                    break;
                case 2:
                    regionMin = min2;
                    break;
                case 3:
                    regionMin = min3;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
            regionMin = min(dim);
        }
        var regionDelta : Int = 0;
        if (rank < 5) {
            switch (dim) {
                case 0:
                    regionDelta = delta0;
                    break;
                case 1:
                    regionDelta = delta1;
                    break;
                case 2:
                    regionDelta = delta2;
                    break;
                case 3:
                    regionDelta = delta3;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
            regionDelta = delta(dim);
        }
        var actualIndex : Int = index;
        while (actualIndex < regionMin) actualIndex += regionDelta;
        while (actualIndex >= regionMin+regionDelta) actualIndex -= regionDelta;
        return actualIndex;
    }

    public @Inline def places():PlaceGroup = baseDist.places();
    public @Inline def numPlaces():Int = baseDist.numPlaces();
    public @Inline def regions():Iterable[Region(rank)] = baseDist.regions();
    public def get(p:Place):Region(rank) = baseDist.get(p);

    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(p:Place):Region(rank) = get(p);

    public @Inline operator this(pt:Point(rank)):Place {
        val actualPt = Point.make(rank, (i : Int) => getPeriodicIndex(pt(i), i));
        return baseDist(actualPt);
    }
    public @Inline operator this(i0:Int){rank==1}:Place {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        return baseDist(a0);
    }
    public @Inline operator this(i0:Int, i1:Int){rank==2}:Place {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        var a1 : Int = i1;
        while (a1 < min1) a1 += delta1;
        while (a1 >= (min1 + delta1)) a1 -= delta1;
        return baseDist(a0, a1);
    }
    public @Inline operator this(i0:Int, i1:Int, i2:Int){rank==3}:Place {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        var a1 : Int = i1;
        while (a1 < min1) a1 += delta1;
        while (a1 >= (min1 + delta1)) a1 -= delta1;
        var a2 : Int = i2;
        while (a2 < min2) a2 += delta2;
        while (a2 >= (min2 + delta2)) a2 -= delta2;
        return baseDist(a0, a1, a2);
    }
    public @Inline operator this(i0:Int, i1:Int, i2:Int, i3:Int){rank==4}:Place {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        var a1 : Int = i1;
        while (a1 < min1) a1 += delta1;
        while (a1 >= (min1 + delta1)) a1 -= delta1;
        var a2 : Int = i2;
        while (a2 < min2) a2 += delta2;
        while (a2 >= (min2 + delta2)) a2 -= delta2;
        var a3 : Int = i3;
        while (a3 < min3) a3 += delta3;
        while (a3 >= (min3 + delta3)) a3 -= delta3;
        return baseDist(a0, a1, a2, a3);
    }

    public @Inline def offset(pt:Point(rank)):Int {
        val actualPt = Point.make(rank, (i : Int) => getPeriodicIndex(pt(i), i));
        return baseDist.offset(actualPt);
    }
    public @Inline def offset(i0:Int){rank==1}:Int {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        return baseDist.offset(a0);
    }
    public @Inline def offset(i0:Int, i1:Int){rank==2}:Int {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        var a1 : Int = i1;
        while (a1 < min1) a1 += delta1;
        while (a1 >= (min1 + delta1)) a1 -= delta1;
        return baseDist.offset(a0, a1);
    }
    public @Inline def offset(i0:Int, i1:Int, i2:Int){rank==3}:Int {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        var a1 : Int = i1;
        while (a1 < min1) a1 += delta1;
        while (a1 >= (min1 + delta1)) a1 -= delta1;
        var a2 : Int = i2;
        while (a2 < min2) a2 += delta2;
        while (a2 >= (min2 + delta2)) a2 -= delta2;
        return baseDist.offset(a0, a1, a2);
    }
    public @Inline def offset(i0:Int, i1:Int, i2:Int, i3:Int){rank==4}:Int {
        var a0 : Int = i0;
        while (a0 < min0) a0 += delta0;
        while (a0 >= (min0 + delta0)) a0 -= delta0;
        var a1 : Int = i1;
        while (a1 < min1) a1 += delta1;
        while (a1 >= (min1 + delta1)) a1 -= delta1;
        var a2 : Int = i2;
        while (a2 < min2) a2 += delta2;
        while (a2 >= (min2 + delta2)) a2 -= delta2;
        var a3 : Int = i3;
        while (a3 < min3) a3 += delta3;
        while (a3 >= (min3 + delta3)) a3 -= delta3;
        return baseDist.offset(a0, a1, a2, a3);
    }

    public @Inline def maxOffset():Int = baseDist.maxOffset();
    public @Inline def restriction(r:Region(rank)):Dist(rank) = new PeriodicDist(baseDist.restriction(r));
    public @Inline def restriction(p:Place):Dist(rank) = new PeriodicDist(baseDist.restriction(p));

    public def toString():String {
        return "Periodic: " + baseDist.toString();
    }
}

