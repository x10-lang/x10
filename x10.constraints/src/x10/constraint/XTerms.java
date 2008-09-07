/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */

package x10.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
   Collection of static methods to create different kinds of XTerms
   (names, vars, atomic formulas, literals, local variables, fields
   etc).
   
   @author njnystrom
   (Comments by vj 09/06/08)

 */
public class XTerms {
    // true and false literals are interned.
	public static final XLit NULL = new XLit_c(null);
	public static final XLit TRUE = new XLit_c(true);
	public static final XLit FALSE = new XLit_c(false);
	public static final XLit OPERATOR = new XLit_c(new Object()) { public String toString() { return "o"; } };

	static final XName equalsName = new XNameWrapper<String>("===");
	static final XName andName = new XNameWrapper<String>("&&&");
	static final XName notName = new XNameWrapper<String>("!!!");

    // used in generating a new name.
	static int nextId = 0;
	
	public static final XName makeFreshName() {
	    return makeFreshName("_");
	}
	public static final XName makeFreshName(String prefix) {
	    return new XNameWrapper<Object>(new Object(), prefix + (nextId++));
	}
	
	public static final <T> XName makeName(T name) {
		return new XNameWrapper<T>(name);
	}
	
	public static final <T> XName makeName(T name, String s) {
		return new XNameWrapper<T>(name, s);
	}
	
	public static final XLocal makeLocal(XName name) {
		return new XLocal_c(name);
	}

	public static final XField makeField(XVar receiver, XName field) {
		return new XField_c(receiver, field);
	}

    /** Make and return a literal containing o. true and false are
     * interned.
     */
	public static final XLit makeLit(Object o) {
		if (o == null) return NULL;
		if (o.equals(true)) return TRUE;
		if (o.equals(false)) return FALSE;
		return new XLit_c(o);
	}
	
    /**
       Make and return op(terms1,..., termsn) -- an atomic formula
       with operator op and terms terms. Uses varargs.
     */
	public static XTerm makeAtom(XName op, XTerm... terms) {
	    return makeAtom(op, terms);
	    /*	    assert op != null;
	    assert terms != null;
	    XFormula f = new XFormula_c(op, terms);
	    f.markAsAtomicFormula();
	    return f;*/
	}

    /**
       Make and return op(terms1,..., termsn) -- an atomic formula
       with operator op and terms terms.
     */

	public static XTerm makeAtom(XName op, List<XTerm> terms) {
		assert op != null;
		assert terms != null;
		XFormula f = new XFormula_c(op, terms);
		f.markAsAtomicFormula();
		return f;
	}
    /**
       Make and return left == right.
     */
	public static XTerm makeEquals(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		return new XEquals_c(left, right);
	}

    /**
       Make and return left,right -- the logical conjunction.
       left and right should be boolean terms. (not checked.)
     */
	public static XTerm makeAnd(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		return new XAnd_c(left, right);
	}

    /** Make and return not (arg). arg should be a boolean term. (not
     * checked.)
     */
	public static XTerm makeNot(XTerm arg) {
		assert arg != null;
		return new XNot_c(arg);
	}

	// HACK: should be non-static
	public static List<Solver> externalSolvers;
	public static List<Solver> externalSolvers() {
		if (externalSolvers == null)
			return Collections.EMPTY_LIST;
		return externalSolvers;
	}
	public static void addExternalSolvers(Solver s) {
		if (externalSolvers == null)
			externalSolvers = new ArrayList<Solver>();
		externalSolvers.add(s);
	}
	
}
