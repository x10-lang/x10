/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a deptype can be extended, and a constructor propagates
 * constraints on super's properties.
 *
 * @author vj
 */
public class Extends extends x10Test {
    class Test(i:int, j:int) {
        def this(i:int, j:int):Test{self.i==i&&self.j==j} = {
            property(i,j);
        }
    }

    class Test2(k:int) extends Test{
        def this(k:int):Test2{self.k==k&&self.i==k&&self.j==k}{
            super(k,k);
            property(k);
        }
    }
    public def run(): boolean = {
        var a: Test2{self.k==1 && self.i==self.j} = new Test2(1);
        var b: Test{self.i==self.j} = a;
        return true;
    }
    public static def main(var args: Rail[String]): void = {
        new Extends().execute();
    }
}
