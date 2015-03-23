/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Purpose: Checks variable name shadowing works correctly.
 * @author vcave
 **/
public class X10DepTypeClassOne(p:int) extends x10Test implements X10InterfaceOne {
   public property p():int = p;
   
   public def this(p: int): X10DepTypeClassOne{self.p==p} {
       property(p);
   }

   public def run(): boolean {
      val p: int = 1n;
      var one: X10DepTypeClassOne = new X10DepTypeClassOne(p);
      return one.p() == p;
   }
   
   public def interfaceMethod(): void {

   }
   
   public static def main(var args: Rail[String]): void {
      new X10DepTypeClassOne(0n).execute();
   }
   }
