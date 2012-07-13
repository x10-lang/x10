package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintSystem;
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;



public class XSmtConstraintSystem implements XConstraintSystem {

	private static XLit TRUE = new XSmtLit<Boolean>(true);
	private static XLit FALSE = new XSmtLit<Boolean>(false);
	private static XLit NULL = new XSmtLit<Object>(null);

	private static int idCount = 0; 
	
	public XConstraint makeConstraint() {
		return new XSmtConstraint();
	}

	public XLit xtrue() {
		return TRUE; 
	}

	public XLit xfalse() {
		return FALSE; 
 	}

	public XLit xnull() {
		return NULL;
	}

	public XEQV makeEQV() {
		return new XSmtEQV(idCount++);
	}

	public XUQV makeUQV() {
		return new XSmtUQV(idCount++);
	}

	public XUQV makeUQV(String prefix) {
		return new XSmtUQV(prefix, idCount++);
	}

	public <T> XField<T> makeField(XVar receiver, T field) {
		return new XSmtField<T>((XSmtVar)receiver, field);
	}

	public XField<Object> makeFakeField(XVar receiver, Object field) {
		return new XSmtField<Object>((XSmtVar)receiver, field, true);
	}

	public XLit makeLit(Object o) {
		return new XSmtLit<Object>(o); 
	}

	public XFormula<Object> makeAtom(Object op, boolean isAtomicFormula, XTerm... terms) {
		List<XSmtTerm> args = new ArrayList<XSmtTerm>(); 
		for (XTerm t: terms) 
			args.add((XSmtTerm)t); 
		
		return new XSmtFormula<Object>(op, isAtomicFormula, args);
	}

	public XSmtTerm makeEquals(XTerm left, XTerm right) {
		return new XSmtEquals((XSmtTerm)left, (XSmtTerm)right);
	}

	public XSmtTerm makeDisEquals(XTerm left, XTerm right) {
		return new XSmtDisEquals((XSmtTerm)left, (XSmtTerm)right);
	}

	public XSmtTerm makeAnd(XTerm left, XTerm right) {
		return new XSmtAnd((XSmtTerm)left, (XSmtTerm)right);
	}

	public XSmtTerm makeNot(XTerm arg) {
		return new XSmtNot((XSmtTerm)arg);
	}

	public XFormula<Object> makeAtom(Object op, XTerm... terms) {
		return new XSmtFormula<Object>(op, true, terms);
	}

	public <T> XLocal<T> makeLocal(T name) {
		return new XSmtLocal<T>(name); 
	}

	public XSmtTerm makeOpaque(Object op, XTerm... terms) {
		throw new UnsupportedOperationException("No opaque yet!");
	}

	/**
	 * Factory methods specific to the SmtConstraint System 
	 */

	public XSmtTerm makeAnd(List<XSmtTerm> conjuncts) {
		return new XSmtAnd(conjuncts);
	}

	public XSmtTerm makeAnd(XSmtTerm... conjuncts) {
		return new XSmtAnd(conjuncts);
	}
	
	public XSmtTerm makeImpl(XTerm a, XTerm b) {
		return new XSmtFormula<String>("=>", true, a, b);
	}
	
	public XSmtTerm makeOr(XTerm a, XTerm b) {
		return new XSmtFormula<String>("||", true, a, b);
	}
	
	public XSmtTerm makeOr(XSmtTerm... disjuncts) {
		return new XSmtFormula<String>("||", true, disjuncts);
	}
	
}
