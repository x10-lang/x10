/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a constrained cast leading to primitive unboxing works 
 *          actually checks the unboxed primitive.
 * @author vcave
 * @author vj  -- Moved to X10 2.0 and renamed
 **/
 public class CastInt3ToAnyAndBack extends x10Test {

   public def run() {
      val x = mth() as Int(3);
      return true;
   }
   
   public def mth()=3 as Any;
   public static def main(Rail[String]) {
      new CastInt3ToAnyAndBack().execute();
   }
}
