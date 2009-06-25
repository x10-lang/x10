/**
 * Test point arithmetic.
 *
 * (was PointArithmetic)
 */

// existing tests indicate 1.5 supported Point op int and int op Point
// where op is + - * /.
//
// Does 1.7 still support this and does it require anything in the
// Point API in addition to $plus $minus $times $over?
//
// Perhaps related - Vijay's version of Arithmetic had cosub and codiv
// - do we need those?


class Test14 extends TestArray {

    public void run() {

        final Point p = Point.make(new int [] {2, 3, 4, 5, 6});
        final Point q = Point.make(new int [] {6, 5, 4, 3, 2});
        final Point r = Point.make(new int [] {1, 2, 3, 4});
        final int c = 2;

        // points
        prPoint("p", p);
        prPoint("q", q);;
        prPoint("r", r);;

        // unary ops
        prPoint("+p", p.$plus());
        prPoint("-p", p.$minus());

        // binary ops
        prPoint("p+q", p.$plus(q));
        prPoint("p-q", p.$minus(q));
        prPoint("p*q", p.$times(q));
        prPoint("p/q", p.$over(q));

        // XXX
        // point/const binary ops
        //prPoint("p+c", p.$plus(c));
        //prPoint("p-c", p.$minus(c));
        //prPoint("p*c", p.$times(c));
        //prPoint("p/c", p.$over(c));

        // XXX
        // const/point binary ops
        // prPoint("c+p", c.$plus(p)));
        // prPoint("c-p", c.$minus(p));
        // prPoint("c*p", c.$times(p));
        // prPoint("c/p", c.$over(p));

        // test exceptions for rank mismatch
        new E("p+r") {void run() {p.$plus(r);}};
        new E("p-r") {void run() {p.$minus(r);}};
        new E("p*r") {void run() {p.$times(r);}};
        new E("p/r") {void run() {p.$over(r);}};
    }
}

