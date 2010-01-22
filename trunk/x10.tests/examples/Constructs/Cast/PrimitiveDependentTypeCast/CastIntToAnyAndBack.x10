/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks cast leading to primitive unboxing works.
 * @author vcave
 **/
 public class CastIntToAnyAndBack extends x10Test {

   public def run()  {
      val i  = mth() as Int;
      return true;
   }
   
   public def mth() = 3 as Any;
   public static def main(Rail[String]) {
      new CastIntToAnyAndBack().execute();
   }
}
