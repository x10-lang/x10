/**
 * Test the shorthand syntax for an array initializer.
 */
public class IntArrayInitializerShorthand {

    public def run(): boolean = {

        val d = Dist.makeConstant([1..10, 1..10], here);
        val ia = Array.make[int](d, ((i,j):Point) => i+j);

        for (val p(i,j): Point(2) in ([1..10, 1..10] as Region))
            chk(ia(p) == i+j);

        return true;
    }
    
    public static def chk(b: boolean, s: String): void = {
        if (!b) throw new Error(s);
    }

    public static def main(var args: Rail[String]): void = {
        new IntArrayInitializerShorthand().run();
    }
}
