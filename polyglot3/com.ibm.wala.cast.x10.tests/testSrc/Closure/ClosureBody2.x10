/**
 * The body of the closure is evaluated when the closure is invoked by
 * a call expression (§12.8), not at the closure’s place in the
 * program text.
 *
 * @author bdlucas 8/2008
 */

public class ClosureBody2 {

    var x:int = 0;

    def x(x:int):void = {
        this.x=x;
    }


    public def run(): boolean = {
        
        // not evaluated here
        val f = () => {x(1)};
        check("x after defn", x, 0);

        // evaluated here
        f();
        check("x after f()", x, 1);

        return result;
    }
    
    def check(test:String, actual:Int, expected:Int) = {
        var result:boolean = actual == expected;

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureBody2().run();
    }
}
