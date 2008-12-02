/**
 * Accessing p[3] in a rank-2 point should cause an array index out of
 * bounds exception.
 */

class Test15 extends TestArray {

    public void run() {

        final Point p = Point.make(new int [] {1,2});

        new E("p[-1]") {void run() {p.get(-1);}};
        new E("p[3]") {void run() {p.get(3);}};
    }

}

