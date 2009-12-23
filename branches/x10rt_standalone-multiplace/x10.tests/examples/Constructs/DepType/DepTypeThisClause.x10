/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;

/**
 * Checks that an arg i from the param list of one method does not stray into
 * body of another.
 *
 * @author vj, 5/17/2006
 */

public class DepTypeThisClause(i:int, j:int) extends x10Test {
    def this(ii:int, jj:int):DepTypeThisClause{self.i==ii,self.j==jj} = { property(ii,jj); }

    // i is a param for this method and also a property
    def make(i:int(3)){this.i==3}:DepTypeThisClause = new DepTypeThisClause(i,i);

    // a method whose return type is a deptype
    public def run(){this.i==3}:boolean(true) = {
        x10.io.Console.OUT.println("i (=3?) = " + i); //property ref.
        return true;
    }

    public static def main(Rail[String]) = {
        new DepTypeThisClause(3,9).execute();
    }

}
