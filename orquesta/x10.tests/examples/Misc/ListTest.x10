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

import x10.lang.Object;
import x10.lang.Integer;;
public class ListTest extends x10Test {
	public static class List {
		public val n: int;
		protected var value: Integer;
		protected var tail: List;
  
		public def this(o: Integer, t: List): List = {
			n=t.n+1;
			tail = t;
			value = o;
		}
		public def this(): List = {
			n=0;
			value=null;
			tail=null;
		}
		public def append(l: List): List = {
			return (n==0)? l : new List(value, tail.append(l)); // this cast should not be needed.
		}
		public def nth(k: int): Integer = {
			// vj: Replacing the line below with the line after that removes the
			// infinite loop in the compiler.
			//return k==1 ? value : tail.nth(k-1);
			return k==1 ? value : tail.nth(k-1);
     
		}
 
		public def gen(k: int): List = {
			return k==0 ? new List() : new List(new Integer(k), gen(k-1));
		}
	}
		public def run(): boolean = {
			a: List = new List(new Integer(1), new List(new Integer(2), new List()));
			b: Integer = a.nth(2);
			return b.val==2;
		}
	
		public static def main(var args: Rail[String]): void = {
			new ListTest().execute();
		}
}
