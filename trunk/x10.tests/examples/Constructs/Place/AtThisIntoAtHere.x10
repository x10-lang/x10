/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 
 * Testing that a this field marked at(this) can be sent into a method requiring an at here, provided that
 * there has been no place shift between invocation of the method and the method call.

 * @author vj
 */
public class AtThisIntoAtHere extends x10Test {
    class Test {
      var x:Test{self.at(this)}=null;
    
     def m(b:Test{self.at(here)}) {}
     def n() { 
	 // n() is a method not marked global, hence on method entry one can assume that this.home==here.
	 // therefore this.x also satisfies at(here) (since its type satisfies at(this)). 
	 // Hence this is a legal call.
       m(x);
     }
    }

    public def run() = true;

    public static def main(Rail[String]) {
	  new AtThisIntoAtHere().execute();
    }

}
