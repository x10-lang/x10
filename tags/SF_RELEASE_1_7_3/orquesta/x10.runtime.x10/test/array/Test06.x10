/**
 * Take a cross product of a 1-d rectange with a 2-d triangular
 * region, forming an extrusion, construct an array from it, and
 * examine it.
 */

class Test06 extends TestArray {

    public void run() {
        Region r1 = Region.makeRectangular(1, 2);
        Region r2 = Region.makeLowerTriangular(3);
        Region r3 = r1.product(r2);
        prArray("extrusion", r3);
    }
}

