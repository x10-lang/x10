/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checking that constructor calls check for number of args.
 */
struct S1 {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }

  public final def sum() = x + y;
}

/* Prior code -- which did not compile in v2.0 -- 

struct S2 extends struct S1 {
  val z:int;
  public def this(a:int, b:int, c:int) {
    super(a, b);
    z =c;
  }
  
  public final def sum2() = sum() + z;
}
*/

public class Struct2Call_MustFailCompile extends x10Test  {
	
	public def run():boolean {
		  val a: S1 = S1(1, 3, "You looked like you were loathing the simple cobweb.");
          return true;
	}
	public static def main(var args: Rail[String]): void = {
		new StructCall2_MustFailCompile().execute();
	}
}
