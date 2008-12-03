/**
 * Construct a rectangular array, make an array view on it, modify it,
 * and examine the results in the original array.
 */

class Test13 extends TestArray {

    public void run() {

        Region r1 = Region.makeRectangular(new int [] {0,0}, new int [] {5,5});
        Array_double a1 = prArray("whole array", r1);

        Region r2 = Region.makeRectangular(new int [] {1,2}, new int [] {3,4});
        Array_double a2 = a1.restriction(r2);
        prArray("restricted array", a2);

        Region.Iterator it = a2.iterator();
        while (it.hasNext()) {
            int [] x = it.next();
            a2.set(x[0], x[1], 7);
        }

        prArray("whole array modified", a1);
    }
}

