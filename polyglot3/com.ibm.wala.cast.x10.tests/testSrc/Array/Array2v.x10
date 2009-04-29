/**
 * Testing 3D arrays.
 */
public class Array2v {

    public def run(): boolean = {

        val e = 0..9;
        val r = [e, e, e] as Region;
        val d = Dist.makeConstant(r, here);

        chk(d.equals(Dist.makeConstant([0..9, 0..9, 0..9], here)));

        val ia = Array.make[int](d, (Point)=>0);

        for (val (i,j,k): Point in d) {
            chk(ia(i, j, k) == 0);
            ia(i, j, k) = 100*i + 10*j + k;
        }

        for (val (i,j,k): Point in d) {
            chk(ia(i, j, k) == 100*i + 10*j + k);
        }

        return true;
    }
    
    public static def chk(b: boolean, s: String): void = {
        if (!b) throw new Error(s);
    }

    public static def main(var args: Rail[String]): void = {
        new Array2v().run();
    }
}
