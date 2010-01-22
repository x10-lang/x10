/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks boxing/unboxing works properly.
 * @author vcave
 * @author vj -- Changed to X10 2.0
 **/
 public class CastInt3ToAny3AndBack extends x10Test {

   public def run(): boolean = {      
      var obj: Any(3) = 3;
      val i = obj as Int(3);
      return true;
   }

   public static def main(var args: Rail[String]): void = {
      new CastInt3ToAny3AndBack().execute();
   }
}
