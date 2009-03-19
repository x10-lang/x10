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
import x10.lang.Integer;
public class ListTest extends x10Test {
	public static class List {
		public final int n;
		protected nullable<Integer> value;
		protected nullable<List> tail;
  
		public List(Integer o, List t) {
			n=t.n+1;
			tail = t;
			value = o;
		}
		public List () {
			n=0;
			value=null;
			tail=null;
		}
		public List append(List l) {
			return (n==0)? l : new List((Integer) value, tail.append(l)); // this cast should not be needed.
		}
		public Integer nth(int k) {
			// vj: Replacing the line below with the line after that removes the
			// infinite loop in the compiler.
			//return k==1 ? value : tail.nth(k-1);
			return k==1 ? (Integer) value : tail.nth(k-1);
     
		}
 
		public List gen(int  k) {
			return k==0 ? new List() : new List(new Integer(k), gen(k-1));
		}
	}
		public boolean run() {
			
			List a = new List(new Integer(1), new List(new Integer(2), new List()));
			Integer b = a.nth(2);
			
			return b.val==2;
		}
	
		public static void main(String[] args) {
			new ListTest().execute();
		}
}