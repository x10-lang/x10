/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the type Test(:self.j==1) is a subtype of Test(:self.j==j) when j is of type int(:self==1).
 *
 * @author vj
 */
public class EntailsPositiveInnerMustPass extends x10Test {
    class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii&&self.j==jj} = {
            property(ii,jj);
        }
    }

    public def run(): boolean = {
        val j: int{self==1} = 1;
        var x: Test{self.j==j} = new Test(1,1);
        return true;
    }
    public static def main(var args: Rail[String]): void = {
        new EntailsPositiveInnerMustPass().execute();
    }
}
