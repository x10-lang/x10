/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraint;

import java.util.Arrays;
import java.util.List;


/**
   Collection of static methods to create different kinds of XTerms
   (names, vars, atomic formulas, literals, local variables, fields
   etc).
   
   @author njnystrom
   (Comments by vj 09/06/08)

 */
public class XTerms {

	public static final XLit ZERO_FLOAT = new XLit(new Float(0.0f));
	public static final XLit ZERO_DOUBLE = new XLit(new Double(0.0));
	public static final XLit ZERO_INT = new XLit(0);
	public static final XLit ZERO_LONG = new XLit(new Long(0));
	public static final XLit ZERO_CHAR = new XLit(Character.valueOf('\0'));
	public static final XLit NULL = new XLit(null);
	public static final XLit TRUE = new XLit(true);
	public static final XLit FALSE = new XLit(false);
    public static boolean isBoolean(XTerm x) { return x==TRUE || x==FALSE; } // because we intern
	public static final XLit OPERATOR = new XLit(new Object()) { 
		public String toString() { return "o"; } 
	};

	public static final String equalsName = "===";
	public static final String disEqualsName = "!==";
	public static final String andName = "&&&";
	public static final String notName = "!!!";

	public static final String asExprEqualsName = "==";
	public static final String asExprDisEqualsName = "!=";
	public static final String asExprAndName = "&&";
	public static final String asExprNotName = "!";

    // used in generating a new name or variable
	static int nextId = 0;
	
	/**
	 * Make a fresh EQV with a system chosen name. 
	 * @return
	 */
	public static XEQV makeEQV() {
		return new XEQV(nextId++);
	}
	/**
	 * Make a fresh UQV with a system chosen name. 
	 * @return
	 */
	public static XUQV makeUQV() {
		return new XUQV(nextId++);
	}

	/**
	 * Make a fresh UQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned UQV
	 * @return
	 */
	public static XUQV makeUQV(String prefix) {
		return new XUQV(prefix, nextId++);
	}

	
	/**
	 * Make and return <code>receiver.field</code>.
	 * @param receiver
	 * @param field
	 * @return
	 */
	public static final <T> XField<T> makeField(XVar receiver, T field) {
		return new XField<T>(receiver, field);
	}
	public static final XField<Object> makeFakeField(XVar receiver, Object field) {
		return new XField<Object>(receiver, field, true);
	}
	

    /** Make and return a literal containing o. null, true and false are
     * interned.
     */
	public static final XLit makeLit(Object o) {
		if (o == null) return NULL;
		if (o.equals(true)) return TRUE;
		if (o.equals(false)) return FALSE;
		return new XLit(o);
	}
	
	/**
    Make and return op(terms1,..., termsn) -- an atomic formula
    with operator op and terms terms. Uses varargs.
	 */
	public static XTerm makeAtom(Object op, XTerm... terms) {
		return makeAtom(op, true, Arrays.asList(terms));
	}

	/**
    Make and return op(terms1,..., termsn) -- a function application 
    with function name op and arguments terms. Uses varargs.
	 */
	public static XTerm makeAtom(Object op, List<XTerm> terms) {
		return makeAtom(op, true, terms);
	}
 
    /**
       Make and return left == right.
     */
	public static XTerm makeEquals(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		if (left instanceof XLit && right instanceof XLit) {
		        if (left.equals(right))
		            return XTerms.TRUE;
		        else
		            return XTerms.FALSE;
		}
		return new XEquals(left, right);
	}
	
	/**
	 * Make and return left != right.
	 * @param left
	 * @param right
	 * @return
	 */
	public static XTerm makeDisEquals(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		if (left instanceof XLit && right instanceof XLit) {
			
		        if (left.equals(right))
		            return XTerms.FALSE;
		        else
		            return XTerms.TRUE;
		}
		return new XDisEquals(left, right);
	}

    /**
       Make and return left,right -- the logical conjunction.
       left and right should be boolean terms. (not checked.)
     */
	public static XTerm makeAnd(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		return new XAnd(left, right);
	}
	public static XTerm makeNot(XTerm arg) {
		assert arg != null;
		return new XNot(arg);
	}

	/**
	 * Return the constraint true.
	 * @return
	 */
	public static XConstraint makeTrueConstraint() {
		return new XConstraint();
	}
	
	//*************************************** Implementation
	/**
    Make and return op(terms1,..., termsn) -- an expression 
    with operator op and arguments terms. If atomicFormula is true
    then this is marked as an atomicFormula, else it is considered a term 
    (a function application term).
	 */

	static XTerm makeAtom(Object op, boolean isAtomicFormula, List<XTerm> terms) {
		assert op != null;
		assert terms != null;
		XFormula<Object> f = new XFormula<Object>(op, op, terms, isAtomicFormula);
		return f;
	}

}
