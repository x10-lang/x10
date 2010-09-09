/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checking that constructor calls for structs work.
 */
struct S1 {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }

  public final def sum() = x + y;
}

struct S2 extends struct S1 {
  val z:int;
  public def this(a:int, b:int, c:int) {
    super(a, b);
    z =c;
  }
  
  public final def sum2() = sum() + z;
}

public class StructCall extends x10Test  {
	
	public def run():boolean {
		  val a:struct S1 = S1(3,4);
          val b:struct S2 = S2(1, 2,3);
  return true;
	}
	public static def main(var args: Rail[String]): void = {
		new StructCall().execute();
	}
}
