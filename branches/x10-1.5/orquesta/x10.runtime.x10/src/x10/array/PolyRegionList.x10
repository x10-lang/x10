package x10.array;

import x10.util.ArrayList_PolyRegion;

class PolyRegionList(int rank) extends ArrayList_PolyRegion {

    public PolyRegionList(int rank) {
        this.rank = rank;
    }

    void add(Region r) {
        if (r instanceof PolyRegion) {
            if (!r.isEmpty())
                super.add((PolyRegion) r);
        } else if (r instanceof UnionRegion) {
            UnionRegion u = (UnionRegion) r;
            for (int j=0; j<u.regions.length; j++)
                super.add(u.regions[j]);
        } else
            throw new Error("unknown region type " + r);
    }

}
