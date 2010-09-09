/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Classes implementing interfaces with properties must also 
 * define the same properties, or else an error is thrown
 *
 * @author raj
 */
public class InterfaceProp_MustFailCompile extends x10Test {
  
	interface  I(i:int) {
      public def a():void;
	}
	interface  J(k:int) extends I{
      public def a():void;
	}
	class E(k:int) implements J{
      public def this(kk:int):E= { k = kk;}
      public def a():void {
        val x:int;
      }
	}
	
	public def run(): boolean = {
        new E(1);
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new InterfaceProp_MustFailCompile().execute();
	}
}
