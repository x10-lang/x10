/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that two guarded fields may not have the same name, even if their guard
 * conditions are disjoint.
 *
 * @author pvarma
 */
public class SameNameGuardedFieldAccess_MustFailCompile extends x10Test {

    static class Test(i: int, j:int) {
        public var v{i==5}: int = 5;
        public var v{i==6}: int = 6;
        def this(i:int, j:int):Test = {
            this.i=i;
            this.j=j;
        }
    }

    public def run(): boolean = {
        var t: Test{self.i==5} = new Test(5, 5);
        t.value = t.value + 10;
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new SameNameGuardedFieldAccess_MustFailCompile().execute();
    }
}
