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

public class StructCall extends x10Test  {
    
    public def run():boolean {
      val a = S1(3,4);
      return a.sum() == 7;
    }

    public static def main(var args: Rail[String]): void = {
        new StructCall().execute();
    }
}
