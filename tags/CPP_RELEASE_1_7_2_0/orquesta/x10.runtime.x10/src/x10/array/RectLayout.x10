package x10.array;

import x10.lang.Region;
import x10.lang.Point;


//
// XXX also need RectLayout0 w/ mins all 0? can that be done with
// generics??
//

final value class RectLayout(int rank) extends Layout {

    private final int size;

    private final int [] min;
    private final int min0;
    private final int min1;
    private final int min2;

    private final int [] delta;
    private final int delta0;
    private final int delta1;
    private final int delta2;

    RectLayout(int [] min, int [] max) {
        
        if (max.length!=min.length)
            throw U.illegal("min and max must have same length");

        this.rank = min.length;

        this.min = min;

        int size = 1;
        delta = new int[rank];
        for (int i=0; i<rank; i++) {
            delta[i] = max[i] - min[i] + 1;
            size *= delta[i];
        }
        this.size = size;

        min0 = rank>=1? min[0] : 0;
        min1 = rank>=2? min[1] : 0;
        min2 = rank>=3? min[2] : 0;

        delta0 = rank>=1? delta[0] : 0;
        delta1 = rank>=2? delta[1] : 0;
        delta2 = rank>=3? delta[2] : 0;
    }


    //
    // Layout
    //

    final int size() {
        return size;
    }

    /* obsolete, but keep for reference
    final Point coord(int offset) {
        int [] cs = new int[rank];
        for (int i=rank-1; i>0; i--) {
            cs[i] = offset%delta[i] + min[i];
            offset /= delta[i];
        }
        cs[0] = offset + min[0];
        return Point.make(cs);
    }
    */

    final int offset(Point coord) {
        assert coord.rank==rank;
        int [] cs = coord.coords();
        int offset = cs[0] - min[0];
        for (int i=1; i<rank; i++)
            offset = offset*delta[i] + cs[i] - min[i];
        return offset;
    }

    final int offset(int i0) {
        int offset = i0 - min0;
        return offset;
    }

    final int offset(int i0, int i1) {
        int offset = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        return offset;
    }

    final int offset(int i0, int i1, int i2) {
        int offset = i0 - min0;
        offset = offset*delta1 + i1 - min1;
        offset = offset*delta2 + i2 - min2;
        return offset;
    }
}
