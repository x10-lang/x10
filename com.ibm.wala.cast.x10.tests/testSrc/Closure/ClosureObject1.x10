/**
 * Closures, like all first-class functions in X10 are objects
 * (§4.6.1).
 *
 * @author bdlucas 8/2008
 */
public class ClosureObject1 {

    public def run(): boolean = {
        
        f:Object = ()=>1;
        check("(f as ()=>int)()", (f as ()=>int)(), 1);

        return result;
    }
    
    def check(test:String, actual:Object, expected:Object) = {
        var result:boolean = actual.equals(expected);

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureObject1().run();
    }
}
