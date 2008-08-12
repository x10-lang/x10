/**
 * Basic region algebra tests.
 *
 * (Was RegionAlgebra)
 */

class Test23 extends TestArray {

    public void run() {

        Region r1 = r(0,1,0,7);
        prArray("r1", r1, true);

        Region r2 = r(4,5,0,7);
        prArray("r2", r2, true);

        Region r3 = r(0,7,4,5);
        prArray("r3", r3, true);

        Region r4 = (r1.$or(r2)).$and(r3);
        prArray("r4=(r1||r2)&&r3", r4, true);

        Region r4x = r(0,1,4,5).$or(r(4,5,4,5));
        prArray("r4x", r4x, true);
        pr("r4.equals(r4x) checks " + r4.equals(r4x));
        pr("(r1.$or(r2)).contains(r4) checks " + (r1.$or(r2)).contains(r4));
        pr("r3.contains(r4) checks " + r3.contains(r4));

        Region r5 = r1.$or(r2).$or(r3);
        prArray("r5=r1||r2||r3", r5, true);
        Region r5x = r(0,1,0,7).$or(r(4,5,0,7)).$or(r(2,3,4,5)).$or(r(6,7,4,5));
        prArray("r5x", r5x, true);
        pr("r5.equals(r5x) checks " + r5.equals(r5x));
        pr("r5.contains(r1) checks " + r5.contains(r1));
        pr("r5.contains(r2) checks " + r5.contains(r2));
        pr("r5.contains(r3) checks " + r5.contains(r3));

        Region r6 = (r1.$or(r2)).$minus(r3);
        prArray("r6=(r1||r2)-r3", r6);
        Region r6x = r(0,1,0,3).$or(r(0,1,6,7)).$or(r(4,5,0,3)).$or(r(4,5,6,7));
        prArray("r6x", r6x, true);
        pr("r6.equals(r6x) checks " + r6.equals(r6x));
        pr("(r1.$or(r2)).contains(r6) checks " + (r1.$or(r2)).contains(r6));
        pr("r6.disjoint(r3) checks " + r6.disjoint(r3));
    }

}

