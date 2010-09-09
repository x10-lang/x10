// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: closure type params

import harness.x10Test;


/**
 * Function types are defined via the => type constructor. Closures
 * (§12.5) and method selectors (§12.6) are of function type. The general
 * form of a function type is:
 * 
 * [X1, . . ., Xm](x1: T1, . . ., xn: Tn){c} => T
 * throws S1, . . ., Sk
 *
 * @author bdlucas 8/2008
 */

public class ClosureFunctionType1 extends ClosureTest {

    var f: [T,U] (x:String, y:T) {y<0 && T<:X && U<:Exception} => T throws U, Exception;

    def set(ff: [V,W] (a:String, b:V) {b<0 && V<:X && W<:Exception} => V throws W, Exception) {
        f = ff;
    }

    public def run(): boolean {
        
        // syntax and type checking test - nothing to run

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new ClosureFunctionType1().execute();
    }
}
