/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks variable name shadowing works correctly.
 * @author vcave
 **/
public class X10DepTypeClassOne(p:int) extends x10Test implements X10InterfaceOne {

   
   public def this(p: int): X10DepTypeClassOne{self.p==p} = {
       property(p);
   }

   public def run(): boolean = {
      val p: int = 1;
      var one: X10DepTypeClassOne = new X10DepTypeClassOne(p);
      return one.p() == p;
   }
   
   public def interfaceMethod(): void = {

   }
   
   public static def main(var args: Rail[String]): void = {
      new X10DepTypeClassOne(0).execute();
   }
   }