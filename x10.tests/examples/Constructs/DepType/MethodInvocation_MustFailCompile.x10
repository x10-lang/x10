/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that a method invocation satisfies the parameter types and constraint
 * of the method declaration.
 *
 * @author pvarma
 */
public class MethodInvocation_MustFailCompile extends x10Test {

    static class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii,self.j==jj} = { property(ii,jj); }
        public def tester(k:int, l:int(k)) = k + l;
    }

    public def run(): boolean = {
        var t: Test = new Test(1, 2);
        // the following call should fail to type check
        t.tester(3, 4);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new MethodInvocation_MustFailCompile().execute();
    }
}
