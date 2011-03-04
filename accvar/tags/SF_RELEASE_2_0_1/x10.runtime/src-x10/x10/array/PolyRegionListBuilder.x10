// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.util.ArrayList;

/**
 * A list of PolyRegions, based on ArrayList. We override add to do
 * some special processing on the way in. Used as the basis of
 * UnionRegion.
 *
 * @author bdlucas
 */

public class PolyRegionListBuilder(rank: int) extends ArrayList[PolyRegion{self.rank==this.rank}] {

    // XTENLANG-49
    static type PolyRegion(rank:Int) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:Int) = PolyRegionListBuilder{self.rank==rank};
    static type PolyRow(rank:Int) = PolyRow{self.rank==rank};
    static type PolyMat(rank:Int) = PolyMat{self.rank==rank};
    static type UnionRegion(rank:Int) = UnionRegion{self.rank==rank};

    public def this(rank: int): PolyRegionListBuilder(rank) {
        super(); // XTENLANG-31
        property(rank);
    }

    public def add(r:Region(rank)) {
        if (r instanceof PolyRegion) {
            if (!r.isEmpty())
                super.add(r as PolyRegion(r.rank));
        } else if (r instanceof UnionRegion(r.rank)) {
            u: UnionRegion(rank) = r as UnionRegion(r.rank);
            for (var j:int=0; j<u.regions.length; j++)
                add(u.regions(j) as Region(r.rank)); // XXXX why?
        } else
            throw new Error("unknown region type " + r);
    }


}
