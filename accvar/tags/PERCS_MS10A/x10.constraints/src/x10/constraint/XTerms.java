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
	public static final XLit OPERATOR = new XLit(new Object()) { 
		public String toString() { return "o"; } 
	};

	public static final XName equalsName = new XNameWrapper<String>("===");
	public static final XName disEqualsName = new XNameWrapper<String>("!==");
	public static final XName andName = new XNameWrapper<String>("&&&");
	public static final XName notName = new XNameWrapper<String>("!!!");

	public static final XName asExprEqualsName = new XNameWrapper<String>("==");
	public static final XName asExprDisEqualsName = new XNameWrapper<String>("!=");
	public static final XName asExprAndName = new XNameWrapper<String>("&&");
	public static final XName asExprNotName = new XNameWrapper<String>("!");

    // used in generating a new name.
	static int nextId = 0;
	
	/**
	 * Make a fresh name, guaranteed to be not equal to any
	 * other name.
	 * @return
	 */
	public static final XName makeFreshName() {
	    return makeFreshName("_");
	}
	/**
	 * Make a fresh EQV with a system chosen name. 
	 * @return
	 */
	public static XEQV makeEQV() {
		return makeEQV(makeFreshName());
	}
	/**
	 * Make a fresh EQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned EQV
	 * @return
	 */
	public static final XEQV makeEQV(String prefix) {
		return new XEQV(makeFreshName(prefix), true);
	}
	/**
	 * Make a fresh UQV with a system chosen name. 
	 * @return
	 */
	public static XEQV makeUQV() {
		return makeUQV(makeFreshName());
	}

	/**
	 * Make a fresh UQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned UQV
	 * @return
	 */
	public static XEQV makeUQV(String prefix) {
		return new XEQV(makeFreshName(prefix), false);
	}

	/**
	 * Make a fresh local variable with a system chosen name. 
	 * @return
	 */
	public static XLocal makeFreshLocal() {
		return makeLocal(XTerms.makeFreshName());
	}
	/**
	 * Make a fresh local variable whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned XLocal
	 */
	public static XLocal makeFreshLocal(String prefix) {
		return makeLocal(XTerms.makeFreshName(prefix));
	}
	/**
	 * Make an EQV with the given name
	 * @return
	 */
	public static XEQV makeEQV(XName name) {
		return new XEQV(name, true);
	}
	/**
	 * Make a UQV with the given name
	 * @return
	 */
	public static XEQV makeUQV(XName name) {
		return new XEQV(name, false);
	}
	

	/**
	 * Make a fresh name -- guaranteed to be not equal to 
	 * any other name created before.
	 * @param prefix
	 * @return
	 */
	public static final XName makeFreshName(String prefix) {
	    return new XNameWrapper<Object>(new Object(), prefix + (nextId++));
	}
	
	/**
	 * Make an XName with the given object. The string for
	 * the XName will be taken to be name.toString().
	 * @param <T>
	 * @param name -- must be non-null
	 * @return
	 */
	public static final <T> XName makeName(T name) {
		return new XNameWrapper<T>(name);
	}
	
	/**
	 * Make an XName with the given object and the given
	 * string. 
	 * @param <T>
	 * @param typeName -- the type of the entity, must be non-null
	 * @param s  -- the string used to name the entity.
	 * @return
	 */
	public static final <T> XName makeName(T typeName, String s) {
		return new XNameWrapper<T>(typeName, s);
	}
	
	/**
	 * Make a local variable with the given name. Note this
	 * will be <code>equal</code> to another local variable
	 * if both have <code>equal</code> name's.
	 * @param name
	 * @return
	 */
	public static final XLocal makeLocal(XName name) {
		return new XLocal(name);
	}
	
	/**
	 * Make and return <code>receiver.field</code>.
	 * @param receiver
	 * @param field
	 * @return
	 */
	public static final XField makeField(XVar receiver, XName field) {
		return new XField(receiver, field);
	}
	public static final XField makeFakeField(XVar receiver, XName field) {
		return new XField(receiver, field, true);
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
	public static XTerm makeAtom(XName op, XTerm... terms) {
		return makeAtom(op, true, Arrays.asList(terms));
	}

	/**
    Make and return op(terms1,..., termsn) -- a function application 
    with function name op and arguments terms. Uses varargs.
	 */
	public static XTerm makeAtom(XName op, List<XTerm> terms) {
		return makeAtom(op, true, terms);
	}


	/**
    Make and return op(terms1,..., termsn) -- a function application 
    with function name op and arguments terms. Uses varargs.
	 */
	public static XTerm makeTerm(XName op, XTerm... terms) {
		/*if (op.equals(plusName)) {
			return new XPlus(terms);
		}
		if (op.equals(minusName)) {
			return new XMinus(terms);
		}*/
		
		return makeAtom(op, false, Arrays.asList(terms));
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

	static XTerm makeAtom(XName op, boolean atomicFormula, List<XTerm> terms) {
		assert op != null;
		assert terms != null;
		XFormula f = new XFormula(op, op, terms);
		if (atomicFormula) {
			f.markAsAtomicFormula();
		}
		return f;
	}
	 static XEQV makeEQV(XName name, boolean hidden) {
			return new XEQV(name, hidden);
		}
		    

	
}
