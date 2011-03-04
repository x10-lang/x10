/**
 * Test point comparison.
 */

class Test16 extends TestArray {

    public void run() {

        final Point p = Point.make(new int [] {1, 2, 3, 4, 5});
        final Point q = Point.make(new int [] {2, 3, 4, 5, 6});
        final Point r = Point.make(new int [] {6, 5, 4, 3, 2});
        final Point s = Point.make(new int [] {1, 2, 3, 4, 5});

        comp(p,q);
        comp(p,r);
        comp(p,s);

        comp(q,p);
        comp(r,p);
        comp(s,p);
    }

    void comp(Point a, Point b) {
        pr(a + "> " + b + " " + a.$gt(b));
        pr(a + "< " + b + " " + a.$lt(b));
        pr(a + ">=" + b + " " + a.$ge(b));
        pr(a + "<=" + b + " " + a.$le(b));
        pr(a + "==" + b + " " + a.$eq(b));
        pr(a + "!=" + b + " " + a.$ne(b));
    }
}

