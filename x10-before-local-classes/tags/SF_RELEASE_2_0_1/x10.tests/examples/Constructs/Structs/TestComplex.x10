import harness.x10Test;

/**
 * @author milthorpe
 */
class TestComplex extends x10Test {
    public def run(): boolean {
        val a = Complex(2.0, 2.0);
        chk ((-(-a)) == a);
        chk (a.abs() == Math.sqrt(8.0));

        val b = a.conjugate();
        chk (b.conjugate() == a);

        val c = Complex(1.0, 4.0);
        chk (a + c - c == a, "a + c - c = a");
        /* Note: this identity does not always hold, given peculiarities of Smith's algorithm for division */
        chk ((a * c) / c == a, "a * c / c = a");
        

        val d = Complex(4.0, -1.0);
        chk (a + d - d == a, "a + d - d = a");
        
        chk ((a * d) / d == a, "a * d / d = a");

        val e = a / Complex(0.0, 0.0);
        chk (e.isNaN());

        return true;
    }

    public static def main(Rail[String]) {
        new TestComplex().execute();
    }

}
