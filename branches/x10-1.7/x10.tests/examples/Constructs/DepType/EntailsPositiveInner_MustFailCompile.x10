/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A class with parameters, Test, is defined as an inner class. Check
 * that a type Test(:i==j) can be defined.
 *
 * @author vj
 */
public class EntailsPositiveInner_MustFailCompile extends x10Test {
    class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii,self.j==jj} = { property(ii,jj);}
    }

    public def run(): boolean = {
        var x: Test{self.i==self.j} = new Test(1,2); // should fail
        return true;
    }
    public static def main(var args: Rail[String]): void = {
        new EntailsPositiveInner_MustFailCompile().execute();
    }
}
