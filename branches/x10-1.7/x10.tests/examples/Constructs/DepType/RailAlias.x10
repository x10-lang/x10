/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 
 *
 * @author igor
 */
public class RailAlias extends x10Test {
     public def this(): RailAlias = { }
     public def run(): boolean = {
          var r: Region{rail} = [0..10];
          var a: Array[double]{rail} = Array.make[double](r, (x:Point)=>0.0);
          var d: double = a(1);
          for (val (p) in a.region) a(p) = 1.0; 
          return true;
     }
     public static def main(var a: Rail[String]): void = {
    	 new RailAlias().run();
     }
}
