/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */
public class ClosureExample4 {

    def fib(n: Int): Int {
        val f = (n: Int) : Point => {
            if (n < 1) return new Point(1,0);
            val (n,n1): Point = this(n-1);
            new Point(n+n1,n)
        }
        val (n,n1): Point = f(n);
        n
    }

    public def run(): boolean = {
        
        // XXX just syntax and type check for now

        return true;
    }



    public static def main(var args: Rail[String]): void = {
        new ClosureExample4().run();
    }
}
