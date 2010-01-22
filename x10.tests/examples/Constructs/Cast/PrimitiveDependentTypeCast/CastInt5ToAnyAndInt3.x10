/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Check you can cast a primitive to Any and back to a constrained type, 
 *  and the constraint is checked.
 * @author vcave
 *  @author vj  -- Moved to X10 2.0 and renamed.
 **/
 public class CastInt5ToAnyAndInt3 extends x10Test {

   public def run() {
      try {
         val i = mth() as Int(3);
      } catch(e: ClassCastException) {
         return true;
      }
      return false;
   }
   
   public def mth() = 5 as Any;
   
   public static def main(Rail[String]) {
      new CastInt5ToAnyAndInt3().execute();
   }
}
