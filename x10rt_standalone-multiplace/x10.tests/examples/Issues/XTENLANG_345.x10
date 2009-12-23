// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author vj 03/2009
 */

class XTENLANG_345 extends x10Test {

    interface Set[T] {
	def add(x:Set[T]):Set[T];
    }
    
    public def run()=true;
    public  incomplete static def all[T](x:Array[T]):Set[Object];
    public static def main(Rail[String]) {
        new XTENLANG_345().execute();
    }
}
