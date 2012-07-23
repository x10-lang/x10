package x10.types.constraints.xsmt;

import java.util.Map;

import polyglot.ast.Field;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.xsmt.XSmtConstraint;
import x10.constraint.xsmt.XSmtTerm;
import x10.types.X10LocalDef;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CField;
import x10.types.constraints.CLocal;
import x10.types.constraints.CSelf;
import x10.types.constraints.CThis;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;

public class CSmtConstraint extends XSmtConstraint implements CConstraint {
	XVar<Type> self;
	XVar<Type> thisVar;
	Type baseType;
	
	public CSmtConstraint(XVar<Type> self, Type t) {
		this.self = self;
		this.baseType = t; 
	}
	
	public CSmtConstraint(Type t) {
		this(ConstraintManager.getConstraintSystem().makeSelf(t), t);
	}
	
	@Override
	public XVar<Type> self() {
		return self;
	}
	
	@Override
	public Type baseType() {
		return baseType; 
	}
	@Override
	public XVar<Type> selfVarBinding() {
		return bindingForVar(self()); 
	}

	@Override
	public XVar<Type> thisVar() {
		return thisVar; 
	}

	@Override
	public boolean hasPlaceTerm() {
		for (XTerm<Type> t : formula) {
			if(hasPlaceTermHelper(t))
				return true;
		}
		return false;
	}
	
	/**
	 * Recursively traverses the term and checks whether it has a 
	 * place term (here). 
	 * @param term
	 * @return
	 */
	private boolean hasPlaceTermHelper(XTerm<Type> term) {
		if (term instanceof XFormula<?>) {
			XFormula<?> form = (XFormula<?>) term; 
			for (XTerm<Type> t : form.arguments()) {
				if (hasPlaceTermHelper(term))
					return true; 
			}
		}
		
		if (term instanceof XField<?>) {
			XField<?> field = (XField<?>) term;
			if (hasPlaceTermHelper(field.receiver()))
				return true;
		}
		
		return PlaceChecker.isGlobalPlace(term); 
	}

	@Override
	public void addIn(CConstraint c) {
		addIn(self(), c);
	}

	@Override
	public void addIn(XTerm<Type> newSelf, CConstraint c) {
		if (c == null) return; 
		// with smt it's probably more efficient not to check for consistency now
		if (! c.consistent()) {setInconsistent(); return;}
		if (c.valid()) return; 
		
		for (XTerm<Type> term : c.constraints()) {
			XTerm<Type> newterm = term.subst(newSelf, c.self());
			assert newterm != null; 
			this.formula.add((XSmtTerm)newterm);
		}
	}

	@Override
	public void addSelfEquality(XTerm<Type> var) {
		addEquality(self(), var);
	}

	@Override
	public void addSelfDisEquality(XTerm<Type> term) {
		addDisBinding(self(), term);
	}

	@Override
	public void addSelfEquality(XConstrainedTerm var) {
		addEquality(self(), var);
		
	}

	@Override
	public void addThisEquality(XTerm<Type> term) {
		addEquality(thisVar(), term);
		
	}

	@Override
	public void setThisVar(XVar<Type> var) {
		if (var == null)
			return;
		thisVar = var; 
	}

	@Override
	public void addEquality(XTerm<Type> s, XConstrainedTerm t) {
		addEquality(s, t.term());
		addIn(s, t.constraint());
	}

	@Override
	public void addEquality(XConstrainedTerm s, XTerm<Type> t) {
		addEquality(t,s);
	}

	@Override
	public void addEquality(XConstrainedTerm s, XConstrainedTerm t) {
		addEquality(s.term(), t.term());
		addIn(s.term(), s.constraint());
		addIn(t.term(), t.constraint());
	}

	@Override
	public CSmtConstraint substitute(XTerm<Type> y, XVar<Type> x) throws XFailure {
		return (CSmtConstraint)substitute(new XTerm[] {y}, new XVar[] {x});
	}

	@Override
	public CSmtConstraint instantiateSelf(XTerm<Type> newSelf) {
		CSmtConstraint result = new CSmtConstraint(baseType()); 
		try {
			for (XTerm<Type> f : formula) {
				XTerm<Type> newf = f.subst(newSelf, self);
				result.addTerm(newf); 
			}
		} catch (XFailure x) {
			result.setInconsistent();
			return result; 
		}
		// FIXME: this will probably be a costly call
		if (!result.consistent())
			result.setInconsistent();
		
		return result;
	}

	@Override
	public CConstraint residue(CConstraint other) {
		other = other.instantiateSelf(self());
		assert other.consistent(); 
		
		CSmtConstraint result = new CSmtConstraint(baseType()); 
		
		for (XTerm<Type> term : other.constraints()) {
			if (!entails(term))
				try {
					result.addTerm(term);
				} catch (XFailure x) {
					result.setInconsistent(); 
					return result; 
				}
		}
		// FIXME: this will probably be a costly call
		if (!result.consistent())
			result.setInconsistent();
		
		return result;
	}

	@Override
	public XVar<Type> getThisVar(CConstraint t1, CConstraint t2) throws XFailure {
		XVar<Type> thisVar = t1 == null ? null : t1.thisVar();
        if (thisVar == null) return t2==null ? null : t2.thisVar();
        if (t2 != null && ! thisVar.equals( t2.thisVar()))
            throw new XFailure("Inconsistent this vars " + thisVar + " and "
                               + t2.thisVar());
        return thisVar;
	}

	@Override
	public CConstraint substitute(XTerm[] ys, XVar[] xs) throws XFailure {
		assert (ys!= null && xs != null);
		assert xs.length == ys.length;
		
		boolean eq = false; 
		// check if we are substituting the x for x
		for (int i = 0; i < ys.length; ++i) {
			XTerm<Type> y = ys[i];
			XVar<Type> x = xs[i];
			if (! y.equals(x)) 
				eq = false; 
		}
		
		if (eq) return this; 
		if (!consistent())
			return this; 
		
		CSmtConstraint result = new CSmtConstraint(baseType()); 
		for (XTerm<Type> term : formula) {
			XTerm<Type> t = term; 
			for (int i = 0; i < ys.length; ++i) {
				XTerm<Type> y= ys[i];
				XVar<Type> x = xs[i];
				t = t.subst(y, x);
			}
			t = t.subst(result.self(), self());
			result.addTerm(t);
		}
		
		//FIXME: expensive call with SMT
		if (!result.consistent())
			throw new XFailure(); 
		return result;
	}

	@Override
	public boolean entails(CConstraint other, ConstraintMaker sigma) {
		if (!consistent()) return true;
		if (other == null || other.valid()) return true; 
		boolean res; 
		try {
			CConstraint contextConstraints = sigma.make();
			// Is it ok to change the constraint in place?
			this.addIn(contextConstraints);
			res = this.entails(other);
		} catch(XFailure x) {
			return false;
		}
		return res; 
	}

	@Override
	public XTerm<Type> bindingForSelfField(FieldDef fd) {
		XVar<Type> field = ConstraintManager.getConstraintSystem().makeField(self(), fd);
		return bindingForVar(field);
	}

	@Override
	public XTerm<Type> bindingForSelfField(MethodDef fd) {
		XVar<Type> field = ConstraintManager.getConstraintSystem().makeField(self(), fd);
		return bindingForVar(field);
	}

	@Override
	public XTerm<Type> bindingForSelfField(Field f) {
		XVar<Type> field = ConstraintManager.getConstraintSystem().makeField(self(), f.fieldInstance().def());
		return bindingForVar(field);
	}

	@Override
	public XTerm<Type> bindingForSelfField(FieldInstance f) {
		XVar<Type> field = ConstraintManager.getConstraintSystem().makeField(self(), f.def());
		return bindingForVar(field);
	}

	@Override
	public CConstraint leastUpperBound(CConstraint other, Type t) {
		//FIXME: this is a temporary sketchy solution to see how it gets called
		XVar<Type> otherSelf = other.self(); 
		CSmtConstraint result = new CSmtConstraint(t); 
		XVar<Type> resultSelf = result.self(); 
		try {
			for (XTerm<Type> term : other.constraints()) {
				if (entails(term, otherSelf)) {
					term = term.subst(resultSelf, otherSelf);
					result.addTerm(term);
				}
			}
		}
		catch (XFailure x) {
			result.setInconsistent();
			return result; 
		}
		return result; 
	}

	@Override
	public CSmtConstraint project(XVar<Type> v) {
		XVar<Type> eqv = ConstraintManager.getConstraintSystem().makeEQV();
		CSmtConstraint result = null;
		try {
			result = substitute(eqv, v);
		} catch(XFailure x) {
			// indeed this should never happen
		}
		return result; 
	}

	@Override
	public CConstraint exists() {
		return instantiateSelf(ConstraintManager.getConstraintSystem().makeEQV());
	}

	@Override
	public void addSigma(CConstraint c, Map<XTerm, CConstraint> m) {
		if (c != null) {
			addIn(c); 
			addIn(c.constraintProjection(m));
		}
	}

	@Override
	public void addSigma(XConstrainedTerm ct, Type t, Map<XTerm, CConstraint> m) {
		if ( ct!= null)
			addSigma(ct.xconstraint(t), m);
	}

	@Override
	public CSmtConstraint constraintProjection(Map<XTerm, CConstraint> m) {
		CSmtConstraint result = constraintProjection(m, 0);
		//FIXME do we need to check for this?
		if (!result.consistent())
			result.setInconsistent(); 
		return result; 
	}

	private CSmtConstraint constraintProjection(Map<XTerm, CConstraint> m, int depth) {
		CSmtConstraint result = new CSmtConstraint(baseType()); 
		for (XTerm<Type> t : constraints()) {
			// note that the baseType field of termConstraint will be null
        	// but this does not matter as the constraint will be added in r, 
			// and self will be substituted out
			CSmtConstraint termConstraint = constraintProjection(t, m, depth); 
			if (termConstraint != null)
				result.addIn(termConstraint);
		}
		return result;
	}
	
	private static int MAX_DEPTH = 15;
	
	private static CSmtConstraint constraintProjection(XTerm<Type> t, Map<XTerm, CConstraint> m, int depth) {
        if (t == null) return null;
        if (depth > MAX_DEPTH) {
        	// because the constraint returned by this method is in a sense "temporary"
        	// (it will be added to another constraint and its self will be substituted)
        	// we do not care what baseType it is associated with
            return new CSmtConstraint(null);
        }
        CSmtConstraint res = (CSmtConstraint)m.get(t);
        if (res != null) 
        	return res;
        
        m.put(t, new CSmtConstraint(null));
        
        if (t instanceof CLocal) {
            CLocal v = (CLocal) t;
            X10LocalDef ld = v.localDef();
            if (ld != null) {
                Type ty = Types.get(ld.type());
                ty = PlaceChecker.ReplaceHereByPlaceTerm(ty, ld.placeTerm());
                CSmtConstraint ci = (CSmtConstraint)Types.realX(ty);
                res = new CSmtConstraint(null);
                ci = ci.instantiateSelf(v);
                res.addIn(ci);
                res.addIn(ci.constraintProjection(m, depth+1));
            }
        } else if (t instanceof XLit) { // no new info to contribute
        } else if (t instanceof CSelf){ // no new info to contribute
        } else if (t instanceof CThis){ // no new info to contribute
        } else if (t instanceof XEQV) { // no new info to contribute
        } else if (t instanceof XUQV) { // no new info to contribute
        } else if (t instanceof XField<?>) { // no new info to contribute
        } else if (t instanceof CField){
            CField f = (CField) t;
            XTerm<Type> target = f.receiver();
            CSmtConstraint targetConstraint = constraintProjection(target, m, depth+1); 
            
            Type baseType = f.type();
            CSmtConstraint ci = null;
            if (baseType == null) 
            	res = targetConstraint;
            else {
                ci = (CSmtConstraint)Types.realX(baseType);
                XVar<Type> v = f.thisVar();
                res = new CSmtConstraint(null);
                if (v != null) {
                	try {
                		ci = ci.substitute(target, v);
                	} catch (XFailure x) {
                		res.setInconsistent(); 
                		return res; 
                	}
                }
                ci = ci.instantiateSelf(f);
                res.addIn(ci);

                CSmtConstraint ciInferred = ci.constraintProjection(m, depth+1); 
                res.addIn(ciInferred);
                if (targetConstraint != null) res.addIn(targetConstraint);
            } 
        } else if (t instanceof XFormula<?>) {
            XFormula<?> f = (XFormula<?>) t;
            for (XTerm<Type> a : f.arguments()) {
                CSmtConstraint argConstraint = constraintProjection(a, m, depth+1); //ancestors);
                if (argConstraint != null) {
                    if (res == null) 
                    	res = new CSmtConstraint(null);
                    res.addIn(argConstraint);
                }
            }
        } 
        else 
        	assert false : "unexpected " + t;
        
        if (res != null) 
        	m.put(t, res); // update the entry
        return res;
	}
	
	@Override
	public CConstraint copy() {
		CSmtConstraint result = new CSmtConstraint(this.self(), baseType()); 
		result.thisVar = this.thisVar();
		result.addIn(this); 
		return result; 
	}

	@Override
	public void setBaseType(Type t) {
		this.baseType = t; 
		if (self instanceof CSelf) 
			((CSelf)self).setType(t);

	}
}
