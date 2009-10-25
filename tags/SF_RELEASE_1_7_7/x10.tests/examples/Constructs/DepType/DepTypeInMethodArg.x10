/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
 public class DepTypeInMethodArg extends x10Test {
     class Test(i:int, j:int) {
	 public def this(i:int, j:int):Test{self.i==i&&self.j==j} = {
	     property(i,j);
	 }
     }
     def m(t1:Test, t2:Test{i==t1.i})=true;
     public def run():boolean = {
	 val x:Test{i==j} = new Test(1,1); 
	 return true;
     }
     public static def main(var args: Rail[String]): void = {
	 new DepTypeInMethodArg().execute();
     }
 }
