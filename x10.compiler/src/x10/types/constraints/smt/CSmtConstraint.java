package x10.types.constraints.smt;

import java.util.Map;

import polyglot.types.Def;
import polyglot.types.Type;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.smt.XSmtConstraint;
import x10.constraint.smt.XSmtVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CSelf;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;

public class CSmtConstraint extends XSmtConstraint<Type> implements CConstraint {
	XVar<Type> self; 
	XVar<Type> thisVar;
	
	CSmtConstraint(Type t) {
		super(); 
		this.self = ConstraintManager.getConstraintSystem().makeSelf(t); 
		this.thisVar = null; 
		
	}
	
	CSmtConstraint(CSelf self) {
		
	}

	@Override
	public XVar<Type> self() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XVar<Type> thisVar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XVar<Type> selfVarBinding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPlaceTerm() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addIn(CConstraint c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addIn(XTerm<Type> newSelf, CConstraint c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelfEquality(XTerm<Type> term) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelfDisEquality(XTerm<Type> term) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelfEquality(XConstrainedTerm var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addThisEquality(XTerm<Type> term) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setThisVar(XTerm<Type> var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEquality(XTerm<Type> s, XConstrainedTerm t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEquality(XConstrainedTerm s, XTerm<Type> t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEquality(XConstrainedTerm s, XConstrainedTerm t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CConstraint substitute(XTerm<Type> y, XTerm<Type> x) throws XFailure {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint instantiateSelf(XTerm<Type> newSelf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint residue(CConstraint other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTerm<Type> getThisVar(CConstraint t1, CConstraint t2)
			throws XFailure {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint substitute(XTerm<Type>[] ys, XTerm<Type>[] xs)
			throws XFailure {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean entails(CConstraint other, ConstraintMaker sigma) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public XTerm<Type> bindingForSelfProjection(Def fd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint leastUpperBound(CConstraint c2, Type t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint project(XTerm<Type> v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint exists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSigma(CConstraint c, Map<XTerm<Type>, CConstraint> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSigma(XConstrainedTerm ct, Type t,
			Map<XTerm<Type>, CConstraint> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CConstraint constraintProjection(Map<XTerm<Type>, CConstraint> m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTerm<Type> bindingForVar(XTerm<Type> local_self) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CSmtConstraint copy() {
		// TODO Auto-generated method stub
		return null;
	}	
}
