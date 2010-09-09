/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
/**
This produces an infinite loop for the type checker, on 06/25/06
*/

public class ListTest extends x10Test {
	public static class List {
		public val n: int;
		protected var value: Int;
		protected var tail: List!;
  
		public def this(o: Int, t: List!) {
			n=t.n+1;
			tail = t;
			value = o;
		}
		public def this() {
			n=0;
			value=0;
			tail=null;
		}
		public def append(l: List!) = {
			return (n==0)? l : new List(value, tail.append(l)); // this cast should not be needed.
		}
		public def nth(k: int): Int = {
			return k==1 ? value : tail.nth(k-1);
     
		}
 
		public def gen(k: int) = {
			return k==0 ? new List() : new List(k, gen(k-1));
		}
	}
		public def run(): boolean = {
			a: List = new List(1, new List(2, new List()));
			b: Int = a.nth(2);
			return b==2;
		}
	
		public static def main(var args: Rail[String]): void = {
			new ListTest().execute();
		}
}
