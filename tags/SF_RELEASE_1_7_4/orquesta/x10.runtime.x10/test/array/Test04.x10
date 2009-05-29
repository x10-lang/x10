/**
 * Construct an array from a native rail and examine it.
 */

class Test04 extends TestArray {

    public void run() {
        double [] foo = new double[] {5,4,3,2,1};
        Array_double a = Array_double.make(foo);
        prArray("from native rail", a);
    }
}
