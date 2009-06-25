/**
 * Tests bounding box, ==.
 *
 * (Was originally ConvexHull test, but in spite of name operated on
 * bounding box, not convex hull)
 */

import x10.array.HalfspaceList;
import x10.array.PolyRegion;

class Test22 extends TestArray {


    public void run() {

        Region r1 = r(0,1,0,7);
        prArray("r1", r1, true);
        pr("r1.boundingBox().$eq(r1) checks " + r1.boundingBox().$eq(r1));

        Region r2 = r(4,5,0,7);
        prArray("r2", r2, true);
        pr("r2.boundingBox().$eq(r2) checks " + r2.boundingBox().$eq(r2));

        Region r3 = r(0,7,4,5);
        prArray("r3", r3, true);
        pr("r3.boundingBox().$eq(r3) checks " + r3.boundingBox().$eq(r3));

        Region r12 = r1.$or(r2);
        prArray("r12", r12, true);

        Region r12b = r12.boundingBox();
        prArray("r12b", r12b, true);
        pr("r12b.$eq(r(0,5,0,7)) checks " + r12b.$eq(r(0,5,0,7)));

        Region r12a3 = r12.$and(r3);
        prArray("r12a3", r12a3, true);
        Region r12a3x = r(0,1,4,5).$or(r(4,5,4,5));
        prArray("r12a3x", r12a3x, true);
        pr("r12a3.$eq(r12a3x) checks " + r12a3.$eq(r12a3x));
        pr("r12.contains(r12a3) checks " + r12.contains(r12a3));
        pr("r3.contains(r12a3) checks " + r3.contains(r12a3));

        Region r12a3b = r12a3.boundingBox();
        prArray("r12a3b", r12a3b, true);
        pr("r12a3b.$eq(r(0,5,4,5)) checks " + r12a3b.$eq(r(0,5,4,5)));

        Region r123 = r1.$or(r2).$or(r3);
        prArray("r123", r123, true);
        Region r123x = r(0,1,0,7).$or(r(4,5,0,7)).$or(r(2,3,4,5)).$or(r(6,7,4,5));
        prArray("r123x", r123x, true);
        pr("r123.$eq(r123x) checks " + r123.$eq(r123x));
        pr("r123.contains(r1) checks " + r123.contains(r1));
        pr("r123.contains(r2) checks " + r123.contains(r2));
        pr("r123.contains(r3) checks " + r123.contains(r3));

        Region r123b = r123.boundingBox();
        prArray("r123b", r123b, true);
        pr("r123b.$eq(r(0,7,0,7)) checks " + r123b.$eq(r(0,7,0,7)));

        Region r12m3 = r12.$minus(r3);
        prArray("r12m3", r12m3, true);
        Region r12m3x = r(0,1,0,3).$or(r(0,1,6,7)).$or(r(4,5,0,3)).$or(r(4,5,6,7));
        pr("r12m3.$eq(r12m3x) checks " + r12m3.$eq(r12m3x));
        pr("r12.contains(r12m3) checks " + r12.contains(r12m3));
        pr("r12m3.disjoint(r3) checks " + r12m3.disjoint(r3));

        Region r12m3b = r12m3.boundingBox();
        prArray("r12m3b", r12m3b, true);
        pr("r12m3b.$eq(r(0,5,0,7)) checks " + r12m3b.$eq(r(0,5,0,7)));

        Region r4 = r(0,0,4,4).$or(r(1,1,3,3)).$or(r(5,5,2,2)).$or(r(3,3,6,6));
        prArray("r4", r4, true);

        Region r4b = r4.boundingBox();
        prArray("r4b", r4b, true);
        pr("r4b.$eq(r(0,5,2,6)) checks " + r4b.$eq(r(0,5,2,6)));

        Region r1a2 = r1.$and(r2);
        prArray("r1a2", r1a2, true);
        pr("r1a2.boundingBox().$eq(r1a2) checks " + r1a2.boundingBox().$eq(r1a2));
    }

}

