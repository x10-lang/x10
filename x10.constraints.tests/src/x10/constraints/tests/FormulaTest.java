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

package x10.constraints.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import x10.constraint.XConstraint;
import x10.constraint.XConstraintManager;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;

public class FormulaTest extends TestCase {
	public FormulaTest() {
		super("FormulaTest");
	}
		
	HashMap<String, XVar> vars = new HashMap<String, XVar>();
	XVar makeUQV(String id) {
		XVar v = vars.get(id);
		if (v !=null) return v;
		v = XConstraintManager.getConstraintSystem().makeUQV(id);
		vars.put(id, v);
		return v;
	}
	public XConstraint parse(String s) {
		char[] buf = s.toCharArray();
		int i = 0;
		
		while (Character.isWhitespace(buf[i])) {
			i++;
		}
		
		if (buf[i] == '(') {
			i++;
		}
		else {
			assert false : "cannot parse " + s;
		}
		
		XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();

		Pair<Integer,XTerm> p = parse(buf, i);
		XTerm t = p.snd;
	
		if (t != null) {
			try {
				if (t instanceof XEquals) {
					c.addBinding(((XEquals) t).left(), ((XEquals) t).right());
				}
				else {
					c.addTerm(t);
				}
			}
			catch (XFailure e) {
			}
		}

		return c;
	}
	
	public Pair<Integer,String> parseId(char[] buf, int i) {
		int n = buf.length;
		char c = buf[i];
		
		int begin = i;
		for (; i < n; i++) {
			c = buf[i];
			if (! Character.isLetter(c)) {
				break;
			}
		}

		String id = new String(buf, begin, i-begin);
		return new Pair<Integer, String>(i, id);
	}
	
	public Pair<Integer,XTerm> parse(char[] buf, int i) {
		int n = buf.length;
		
		if (i >= n) {
			return new Pair<Integer,XTerm>(n, null);
		}
		
		char c = buf[i];
		
		while (Character.isWhitespace(c)) {
			i++;
			c = buf[i];
		}
		
		String op = null;
		
		if (Character.isLetter(c)) {
			Pair<Integer,String> p = parseId(buf, i);
			i = p.fst;
			c = buf[i];
			op = p.snd;
		}
		else if (c == '=') {
			i++;
			c = buf[i];
			if (c == '=') {
				i++;
				c = buf[i];
				op = "==";
			}
		}
		else if (c == '!') {
			i++;
			c = buf[i];
			op =  "!";
		}
		else if (c == '&') {
			i++;
			c = buf[i];
			if (c == '&') {
				i++;
				c = buf[i];
				op = "&&";
			}
		}
		
		if (op == null) {
			assert false : "bad op";
			return null;
		}
		
		List<XTerm> terms = new ArrayList<XTerm>();

		while (true) {
			XTerm t = null;
			
			while (Character.isWhitespace(c)) {
				i++;
				c = buf[i];
			}
		
			if (c == '(') {
				Pair<Integer,XTerm> p = parse(buf, i+1);
				t = p.snd;
				i = p.fst;
				c = buf[i];
			}

			XVar left = null;

			while (Character.isLetter(c)) {
				Pair<Integer,String> p = parseId(buf, i);
				i = p.fst;
				c = buf[i];
				String id = p.snd;

				if (left != null) {
					left = XConstraintManager.getConstraintSystem().makeField(left, id);
				}
				else {
					left = makeUQV(id);
				}

				if (c == '.') {
					i++;
					c = buf[i];
				}
				else {
					break;
				}
			}
			
			if (left != null) {
				t = left;
			}
			
			if (t != null) {
				terms.add(t);
			}
			
			if (c == ')') {
				i++;
				break;
			}
		}
		
		if (op.toString().equals("==")) {
			XTerm left = terms.get(0);
			for (int k = 1; k < terms.size(); k++) {
				left = XConstraintManager.getConstraintSystem().makeEquals(left, terms.get(k));
			}
			return new Pair<Integer,XTerm>(i, left);
		}
		if (op.toString().equals("&&")) {
			XTerm left = terms.get(0);
			for (int k = 1; k < terms.size(); k++) {
				left = XConstraintManager.getConstraintSystem().makeAnd(left, terms.get(k));
			}
			return new Pair<Integer,XTerm>(i, left);
		}
		if (op.toString().equals("!")) {
			return new Pair<Integer,XTerm>(i, XConstraintManager.getConstraintSystem().makeNot(terms.get(0)));
		}

		return new Pair<Integer,XTerm>(i, XConstraintManager.getConstraintSystem().makeAtom(op, terms.toArray(new XTerm[0])));
	}

	XConstraint c1 = parse("(== x y)");
	XConstraint c2 = parse("(== y x)");
	XConstraint c3 = parse("(== y x)");
	XConstraint c4 = parse("(&& (== y x) (== x.f z))");
	XConstraint c5 = parse("(== y.f z)");
	XConstraint c6 = parse("(&& (F x.f z) (== x.f z) (== y x))");
	XConstraint c7 = parse("(F y.f z)");
	XConstraint c8 = parse("(F z x.f)");
	
	XConstraint c9 = parse("(! (! (== x y)))");
	public void test1() throws Throwable {
		assertTrue(c1.entails(c2));
	}
	public void test2() throws Throwable {
		assertTrue(c2.entails(c1));
	}
	public void test3() throws Throwable {
		assertTrue(c3.entails(c1));
	}
	public void test4() throws Throwable {
		assertTrue(c4.entails(c5));
	}
	public void test5() throws Throwable {
		assertFalse(c5.entails(c4));
	}
	public void test6() throws Throwable {
		assertTrue(c6.entails(c7));
	}
	public void test7() throws Throwable {
		assertTrue(c6.entails(c8));
	}
	public void test8() throws Throwable {
		assertFalse(c8.entails(c6));
	}
	public void test9() throws Throwable {
		assertTrue(c9.entails(c9));
	}
	
	public void test10() throws Throwable {
		assertTrue(c1.entails(c9));
	}
}
