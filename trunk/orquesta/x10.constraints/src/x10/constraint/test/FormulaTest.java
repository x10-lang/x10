package x10.constraint.test;

import java.util.ArrayList;
import java.util.List;

import com.sun.tools.javac.util.Pair;

import junit.framework.TestCase;
import x10.constraint.C_Equals;
import x10.constraint.C_Field;
import x10.constraint.C_Field_c;
import x10.constraint.C_Formula;
import x10.constraint.C_Local;
import x10.constraint.C_Name;
import x10.constraint.C_NameWrapper;
import x10.constraint.C_Term;
import x10.constraint.C_Terms;
import x10.constraint.C_Var;
import x10.constraint.Constraint;
import x10.constraint.Constraint_c;
import x10.constraint.Failure;

public class FormulaTest extends TestCase {
	public FormulaTest() {
		super("FormulaTest");
	}
	
	public Constraint parse(String s) {
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
		
		Constraint c = new Constraint_c();

		Pair<Integer,C_Term> p = parse(buf, i);
		C_Term t = p.snd;
	
		if (t != null) {
			try {
				if (t instanceof C_Equals) {
					c.addBinding(((C_Equals) t).left(), ((C_Equals) t).right());
				}
				else {
					c.addTerm(t);
				}
			}
			catch (Failure e) {
			}
		}

		return c;
	}
	
	public Pair<Integer,C_Name> parseId(char[] buf, int i) {
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
		C_Name xid = new C_NameWrapper<String>(id);
		return new Pair<Integer, C_Name>(i, xid);
	}
	
	public Pair<Integer,C_Term> parse(char[] buf, int i) {
		int n = buf.length;
		
		if (i >= n) {
			return new Pair<Integer,C_Term>(n, null);
		}
		
		char c = buf[i];
		
		while (Character.isWhitespace(c)) {
			i++;
			c = buf[i];
		}
		
		C_Name op = null;
		
		if (Character.isLetter(c)) {
			Pair<Integer,C_Name> p = parseId(buf, i);
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
				op = new C_NameWrapper<String>("==");
			}
		}
		else if (c == '!') {
			i++;
			c = buf[i];
			op = new C_NameWrapper<String>("!");
		}
		else if (c == '&') {
			i++;
			c = buf[i];
			if (c == '&') {
				i++;
				c = buf[i];
				op = new C_NameWrapper<String>("&&");
			}
		}
		
		if (op == null) {
			assert false : "bad op";
			return null;
		}
		
		List<C_Term> terms = new ArrayList<C_Term>();

		while (true) {
			C_Term t = null;
			
			while (Character.isWhitespace(c)) {
				i++;
				c = buf[i];
			}
		
			if (c == '(') {
				Pair<Integer,C_Term> p = parse(buf, i+1);
				t = p.snd;
				i = p.fst;
				c = buf[i];
			}

			C_Var left = null;

			while (Character.isLetter(c)) {
				Pair<Integer,C_Name> p = parseId(buf, i);
				i = p.fst;
				c = buf[i];
				C_Name id = p.snd;

				if (left != null) {
					left = C_Terms.makeField(left, id);
				}
				else {
					left = C_Terms.makeLocal(id);
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
			C_Term left = terms.get(0);
			for (int k = 1; k < terms.size(); k++) {
				left = C_Terms.makeEquals(left, terms.get(k));
			}
			return new Pair<Integer,C_Term>(i, left);
		}
		if (op.toString().equals("&&")) {
			C_Term left = terms.get(0);
			for (int k = 1; k < terms.size(); k++) {
				left = C_Terms.makeAnd(left, terms.get(k));
			}
			return new Pair<Integer,C_Term>(i, left);
		}
		if (op.toString().equals("!")) {
			return new Pair<Integer,C_Term>(i, C_Terms.makeNot(terms.get(0)));
		}

		return new Pair<Integer,C_Term>(i, C_Terms.makeAtom(op, terms.toArray(new C_Term[0])));
	}

	@Override
	protected void runTest() throws Throwable {
		Constraint c1 = parse("(== x y)");
		Constraint c2 = parse("(== y x)");
		Constraint c3 = parse("(== y x)");
		Constraint c4 = parse("(&& (== y x) (== x.f z))");
		Constraint c5 = parse("(== y.f z)");
		Constraint c6 = parse("(&& (F x.f z) (== x.f z) (== y x))");
		Constraint c7 = parse("(F y.f z)");
		Constraint c8 = parse("(F z x.f)");
		Constraint c9 = parse("(! (! (== x y)))");
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
