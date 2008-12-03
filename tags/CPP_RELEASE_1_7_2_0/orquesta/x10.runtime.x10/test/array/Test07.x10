/**
 * Form a cross product of three 1-d rectangles, construct an array
 * from it, and observe it.
 */

class Test07 extends TestArray {

    public void run() {
        Region r1 = Region.makeRectangular(1, 2);
        Region r2 = Region.makeRectangular(3, 4);
        Region r3 = Region.makeRectangular(5, 6);
        Region r4 = Region.make(new Region [] {r1, r2, r3});
        prArray("rectangular product", r4);
    }
}
