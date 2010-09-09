// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * The function type [X1, . . ., Xk](x1: T1 . . ., xn: Tn) => S may be
 * considered equivalent to an interface type with a method: def
 * apply[X1, . . ., Xk](x1: Y1, . . ., xn: Yn): Z;
 *
 * A closure call e(. . .) is shorthand for a method call e.apply(. . .).
 *
 * @author bdlucas 8/2008
 */

public class ClosureObject3 extends ClosureTest {

    public def run(): boolean = {
        
        val f:(int)=>int = (i:int) => i+1;
        check("f(1)", f(1), 2);
        check("f.apply(1)", f(1), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureObject3().execute();
    }
}
