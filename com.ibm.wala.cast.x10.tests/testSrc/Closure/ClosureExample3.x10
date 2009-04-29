import x10.util.Collection;

/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */
public class ClosureExample3 extends x10Test {
    var result: Boolean = true;
    def allPositive(c: Collection): Boolean {
        c.applyToAll((x: Int) => { if (x < result) atomic {result=false;}});
        return result;
    }

    public def run(): boolean = {
        
        // XXX just syntax and type check for now

        return result;
    }



    public static def main(var args: Rail[String]): void = {
        new ClosureExample3().run();
    }
}
