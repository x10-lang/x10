/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Purpose: Checks dynamic cast should fail at runtime.
 * Issue: Constraint is not meet.
 * Note: The following code will use java reflexion to dynamically checks constraints. 
 * @author vcave
 **/
public class DynamicCast4_MethodReturn extends x10Test {
   public def run(): boolean = {      
      try {                  
         // constraint not meet
         var convertedObject: X10DepTypeClassTwo{p==0&&q==2} = 
         this.objectReturner() as X10DepTypeClassTwo{p==0&&q==2};
         
      }catch(var e: ClassCastException) {
         return true;
      }

      return false;
   }
   
   public def objectReturner(): Any = {
      return new X10DepTypeClassTwo(0,1);
   }

   public static def main(var args: Array[String](1)): void = {
      new DynamicCast4_MethodReturn().execute();
   }
}
