/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that final local variables can be accessed in a depclause.
 *
 * @author vj
 */
public class VariableMustBeFinalInDepClause extends x10Test {
    static class Test(i:int) {
        public def this(ii:int):Test{self.i==ii} {
            property(ii);
        }
    }
    public def m(var t: Test{i==52}): Test{i==52} = {
        val j: int{self==52} = 52;
        var a: Test{i==j} = t;
        return a;
    }
    public def run(): boolean = {
        var t: Test{i==52} = new Test(52);
        return m(t).i==52;
    }
    public static def main(var args: Rail[String]): void = {
        new VariableMustBeFinalInDepClause().execute();
    }

}
