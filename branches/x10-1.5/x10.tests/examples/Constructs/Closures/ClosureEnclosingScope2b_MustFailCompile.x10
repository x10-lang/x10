// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * STATIC SEMANTICS RULE: In an expression (x1: T1, . . ., xn: Tn) =>
 * e, any outer local variable accessed by e must be final or must be
 * declared as shared (§14.9).
 *
 * @author bdlucas 8/2008
 */

public class ClosureEnclosingScope2b_MustFailCompile extends ClosureTest {

    val a = 1;

    public def run(): boolean = {
        
        var b:int = 1;

        check("(()=>a+b)()", (()=>a+b)(), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureEnclosingScope2b_MustFailCompile().execute();
    }
}
