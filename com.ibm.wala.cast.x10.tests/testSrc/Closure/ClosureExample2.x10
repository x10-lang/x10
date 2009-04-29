/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */
public class ClosureExample2 {

    def incr(A: Array[Int]): Array[Int] = {
        val f = (x: Int) => x+1; // e.g., f(1) == 2
        return A.lift(f);
    }


    public def run(): boolean = {
        
        // XXX just syntax and type check for now

        return result;
    }



    public static def main(var args: Rail[String]): void = {
        new ClosureExample2().run();
    }
}
