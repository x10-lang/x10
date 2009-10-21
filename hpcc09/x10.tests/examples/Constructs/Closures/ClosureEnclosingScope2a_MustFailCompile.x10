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

public class ClosureEnclosingScope2a_MustFailCompile extends ClosureTest {

    var a:int = 1;

    public def run(): boolean = {
        
        check("(()=>a)()", (()=>a)(), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureEnclosingScope2a_MustFailCompile().execute();
    }
}
