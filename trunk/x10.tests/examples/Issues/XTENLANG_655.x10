// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author hhorii 02/2010
 */
public class XTENLANG_655 extends x10Test {
    public def thispop[X](x:X){X <: Vossol} : Int = at(x)(x.x());
    public def thisint[X](x:X){X <: Int} {x.toOctalString();}
    
    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_655().execute();
    }
}

interface Vossol{
  def x():Int;
}

class Chakrrire implements Vossol {
  public def x()  = 1;
  public def this() {}
}
