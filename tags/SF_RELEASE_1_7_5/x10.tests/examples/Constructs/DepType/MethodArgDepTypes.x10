/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method arg can have a deptype and it is propagated into the body.
 *
 * @author vj
 */
public class MethodArgDepTypes extends x10Test {
    class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii,self.j==jj} = { property(ii,jj); }
    }

    public def m(var t: Test{self.i==self.j}): boolean = {
        var tt: Test{self.i==self.j} = t;
        return true;
    }
    public def run(): boolean = {
        return m(new Test(2,2));
    }
    public static def main(var args: Rail[String]): void = {
        new MethodArgDepTypes().execute();
    }
}
