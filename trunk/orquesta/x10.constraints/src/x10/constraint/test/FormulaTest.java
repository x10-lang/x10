/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class FormulaTest extends TestCase {
	public FormulaTest() {
		super("FormulaTest");
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
		
		XConstraint c = new XConstraint_c();

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
	
	public Pair<Integer,XName> parseId(char[] buf, int i) {
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
		XName xid = new XNameWrapper<String>(id);
		return new Pair<Integer, XName>(i, xid);
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
		
		XName op = null;
		
		if (Character.isLetter(c)) {
			Pair<Integer,XName> p = parseId(buf, i);
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
				op = new XNameWrapper<String>("==");
			}
		}
		else if (c == '!') {
			i++;
			c = buf[i];
			op = new XNameWrapper<String>("!");
		}
		else if (c == '&') {
			i++;
			c = buf[i];
			if (c == '&') {
				i++;
				c = buf[i];
				op = new XNameWrapper<String>("&&");
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
				Pair<Integer,XName> p = parseId(buf, i);
				i = p.fst;
				c = buf[i];
				XName id = p.snd;

				if (left != null) {
					left = XTerms.makeField(left, id);
				}
				else {
					left = XTerms.makeLocal(id);
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
				left = XTerms.makeEquals(left, terms.get(k));
			}
			return new Pair<Integer,XTerm>(i, left);
		}
		if (op.toString().equals("&&")) {
			XTerm left = terms.get(0);
			for (int k = 1; k < terms.size(); k++) {
				left = XTerms.makeAnd(left, terms.get(k));
			}
			return new Pair<Integer,XTerm>(i, left);
		}
		if (op.toString().equals("!")) {
			return new Pair<Integer,XTerm>(i, XTerms.makeNot(terms.get(0)));
		}

		return new Pair<Integer,XTerm>(i, XTerms.makeAtom(op, terms.toArray(new XTerm[0])));
	}

	@Override
	protected void runTest() throws Throwable {
		XConstraint c1 = parse("(== x y)");
		XConstraint c2 = parse("(== y x)");
		XConstraint c3 = parse("(== y x)");
		XConstraint c4 = parse("(&& (== y x) (== x.f z))");
		XConstraint c5 = parse("(== y.f z)");
		XConstraint c6 = parse("(&& (F x.f z) (== x.f z) (== y x))");
		XConstraint c7 = parse("(F y.f z)");
		XConstraint c8 = parse("(F z x.f)");
		XConstraint c9 = parse("(! (! (== x y)))");
//		Constraint c9 = parse("(! (== x y))");
		
		System.out.println("c1 = " + c1);
		System.out.println("c2 = " + c2);
		System.out.println("c3 = " + c3);
		System.out.println("c4 = " + c4);
		System.out.println("c5 = " + c5);
		System.out.println("c6 = " + c6);
		System.out.println("c7 = " + c7);
		System.out.println("c8 = " + c8);
		
		boolean ea = c1.entails(c2);
		System.out.println(c1 + " ==> " + c2 + " = " + ea);
		
		boolean eb = c2.entails(c1);
		System.out.println(c2 + " ==> " + c1 + " = " + eb);

		boolean ec = c3.entails(c1);
		System.out.println(c3 + " ==> " + c1 + " = " + ec);

		boolean ed = c4.entails(c5);
		System.out.println(c4 + " ==> " + c5 + " = " + ed);
		
		boolean ee = c5.entails(c4);
		System.out.println(c5 + " ==> " + c4 + " = " + ee);
		
		boolean ef = c6.entails(c7);
		System.out.println(c6 + " ==> " + c7 + " = " + ef);
		
		boolean eg = c6.entails(c8);
		System.out.println(c6 + " ==> " + c8 + " = " + eg);

		boolean eh = c8.entails(c6);
		System.out.println(c8 + " ==> " + c6 + " = " + eh);
		
		boolean ei = c9.entails(c1);
		System.out.println(c9 + " ==> " + c1 + " = " + ei);
	
		boolean ej = c1.entails(c9);
		System.out.println(c1 + " ==> " + c9 + " = " + ej);
	}
}
