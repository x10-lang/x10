package x10.types.constraints.smt;

import java.util.Map;

import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.constraint.XConstraint;
import x10.constraint.XDef;
import x10.constraint.XEQV;
import x10.constraint.XExpr;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.smt.XSmtConstraint;
import x10.constraint.smt.XSmtPrinter;
import x10.constraint.smt.XSmtTerm;
import x10.constraint.smt.XSmtVar;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;

public class CSmtConstraint extends XSmtConstraint<Type> implements CConstraint {
	XSmtVar<Type> self; 
	XSmtVar<Type> thisVar;
	
	@Override
	public CSmtConstraintSystem sys() { return (CSmtConstraintSystem) super.sys(); }

	@Override
	public TypeSystem ts() { return (TypeSystem) super.ts(); }
	
	CSmtConstraint(CSmtConstraintSystem sys, Type t, TypeSystem ts) {
		super(sys, ts);
		// FIXME: sometimes the null type is passed in. We assume that if we do not know the
		// type associated with this constraint, self will be substituted. 
		if (t == null) {
			this.self = null;
		} else {
			this.self = (XSmtVar<Type>)ConstraintManager.getConstraintSystem().makeSelf(t);
		}
		this.thisVar = null; 
	}
	
	CSmtConstraint(CSmtConstraintSystem sys, XSmtVar<Type> self, TypeSystem ts) {
		super(sys, ts);
		this.self = self; 
		this.thisVar = null; 
	}
	
	CSmtConstraint(CSmtConstraintSystem sys, TypeSystem ts) {
		super(sys,ts);
		this.self = null; 
		this.thisVar = null; 
	}
	
	CSmtConstraint(CSmtConstraint other) {
		super(other);
		this.self = other.self(); 
		this.thisVar = other.thisVar(); 
	}

	@Override
	public XSmtVar<Type> self() {
		return self; 
	}

	@Override
	public XSmtVar<Type> thisVar() {
		return thisVar; 
	}

	@Override
	public XTerm<Type> selfVarBinding() {
		return bindingForVar(self());
	}

	/*
	@Override
	public boolean hasPlaceTerm() {
		// a global place term has to be a variable so it suffices
		// to look at vars and projections
		for (XTerm<Type> t : getVarsAndFields()) {
			if (PlaceChecker.isGlobalPlace(t))
				return true; 
		}
		return false;
	}
	*/

	@Override
	public void addIn(CConstraint c) {
		// nothing to do
		if (c == null)
			return; 
		//FIXME: sketchy hack for when we don't know the self of a constraint?
		if (self() == null && c.self() != null) {
			XVar<Type> newself = sys().makeSelf(c.self().type());
			setSelf(newself); 
		}
		// if both are null we don't need to substitute anything so we're good
		addIn(self(), c);
	}

	@Override
	public void addIn(XTerm<Type> newSelf, CConstraint c) {
		if (c == null) return; 
		if (c == this || c.terms() == terms()) {
			try {
				substitute(newSelf, self());
			} catch(XFailure e) {
				assert false; // should not happen
			}
		}
		if (! c.consistent()) {setInconsistent(); return;}
		if (c.valid()) return; 
		
		for (XTerm<Type> term : c.terms()) {
			XTerm<Type> newterm = term.subst(sys(), newSelf, c.self());
			assert newterm != null; 
			conjuncts.add((XSmtTerm<Type>)newterm);
		}
	}

	@Override
	public void addSelfEquality(XTerm<Type> term) {
		addEquality(self(), term);
	}

	@Override
	public void addSelfDisEquality(XTerm<Type> term) {
		addDisEquality(self(), term);
	}

	@Override
	public void addSelfEquality(XConstrainedTerm term) {
		addEquality(self(), term);
	}

	@Override
	public void addThisEquality(XTerm<Type> term) {
		addEquality(thisVar(), term);
	}

	@Override
	public void setThisVar(XVar<Type> var) {
		this.thisVar = (XSmtVar<Type>)var; 
	}
	@Override
	public void setSelf(XVar<Type> self) {
		assert this.self == null;
		this.self = (XSmtVar<Type>)self; 
	}

	@Override
	public void addEquality(XTerm<Type> s, XConstrainedTerm t) {
		addEquality(s, t.term());
		addIn(s, t.constraint());
	}

	@Override
	public void addEquality(XConstrainedTerm s, XTerm<Type> t) {
		addEquality(t, s);
	}

	@Override
	public void addEquality(XConstrainedTerm s, XConstrainedTerm t) {
		addEquality(s.term(), t.term());
		addIn(s.term(), s.constraint());
		addIn(t.term(), t.constraint());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CSmtConstraint substitute(XTerm<Type> y, XTerm<Type> x) throws XFailure {
		return (CSmtConstraint)substitute(new XTerm[] {y}, new XTerm[] {x});
	}

	@Override
	public CSmtConstraint instantiateSelf(XTerm<Type> newSelf) {
		assert newSelf != null;
		//FIXME: sketchy hack until we rework isOldSubtype
		if (self() != null) {
			String type1 = XSmtPrinter.smt2(self().type());
			String type2 = XSmtPrinter.smt2(newSelf.type()); 
			if (!type1.equals(type2)) {
				//System.out.println("Types are not the same, cannot instantiate.");
				return this.copy(); 
			}
		}
		CSmtConstraint result = new CSmtConstraint(sys(), newSelf.type(),ts()); 
		try {
			for (XSmtTerm<Type> c : conjuncts) {
				XSmtTerm<Type> newc = c.subst(sys(),newSelf, self);
				result.addTerm(newc); 
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
	public CSmtConstraint residue(XConstraint<Type> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTerm<Type> getThisVar(CConstraint t1, CConstraint t2) 
			throws XFailure {
		XTerm<Type> thisVar = t1 == null ? null : t1.thisVar();
        if (thisVar == null) return t2==null ? null : t2.thisVar();
        if (t2 != null && ! thisVar.equals( t2.thisVar()))
            throw new XFailure("Inconsistent this vars " + thisVar + " and "
                               + t2.thisVar());
        return thisVar;
	}

	@Override
	public CConstraint substitute(XTerm<Type>[] ys, XTerm<Type>[] xs)
			throws XFailure {
		assert (ys!= null && xs != null);
		assert xs.length == ys.length;
		
		boolean eq = false; 
		// check if we are substituting the x for x
		for (int i = 0; i < ys.length; ++i) {
			XTerm<Type> y = ys[i];
			XTerm<Type> x = xs[i];
			if (! y.equals(x)) 
				eq = false; 
		}
		
		if (eq) return this; 
		if (!consistent())
			return this; 
		// when we cannot figure out the type of the constraint, we just have a null self
		// the assumption is that self will be substituted in
		CSmtConstraint result = self() == null ? new CSmtConstraint(sys(),self(),ts()) :
												 new CSmtConstraint(sys(),self().type(),ts()); 
		for (XTerm<Type> term : terms()) {
			XTerm<Type> t = term; 
			for (int i = 0; i < ys.length; ++i) {
				XTerm<Type> y= ys[i];
				XTerm<Type> x = xs[i];
				t = t.subst(sys(), y, x);
			}
			t = t.subst(sys(), result.self(), self());
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
		CConstraint newthis = this.copy(); 
		try {
			CConstraint contextConstraints = sigma.make();
			newthis.addIn(contextConstraints);
			res = newthis.entails(other);
		} catch(XFailure x) {
			return false;
		}
		return res; 
	}

	@Override
	public XTerm<Type> bindingForSelfField(XDef<Type> fd) {
		XTerm<Type> field = sys().makeField(self(), fd);
		return bindingForVar(field);
	}

	@Override
	public CConstraint leastUpperBound(CConstraint other, Type t) {
		//FIXME: this is a temporary sketchy solution to see how it gets called
		XTerm<Type> otherSelf = other.self(); 
		CSmtConstraint result = new CSmtConstraint(sys(),t,ts()); 
		XTerm<Type> resultSelf = result.self(); 
		try {
			for (XTerm<Type> term : other.terms()) {
				term = term.subst(sys(), otherSelf, self()); 
				if (entails(term)) {
					term = term.subst(sys(), self(), resultSelf);
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
	public CConstraint project(XVar<Type> v) {
		XVar<Type> eqv = ConstraintManager.getConstraintSystem().makeEQV(v.type());
		CSmtConstraint result = null;
		try {
			result = substitute(eqv, v);
		} catch(XFailure x) {
			throw new AssertionError("Subsituting for a fresh variable in " +
					"should not cause inconsistency"); 
		}
		return result; 
	}

	@Override
	public CConstraint exists() {
		return instantiateSelf(ConstraintManager.getConstraintSystem().makeEQV(self().type()));
	}

	@Override
	public void addSigma(CConstraint c, Map<XTerm<Type>, CConstraint> m) {
		if (c != null) {
			addIn(c); 
			addIn(c.constraintProjection(m));
		}
	}

	@Override
	public void addSigma(XConstrainedTerm ct, Type t, Map<XTerm<Type>, CConstraint> m) {
		if ( ct!= null)
			addSigma(ct.xconstraint(t), m);
	}

	@Override
	public CSmtConstraint constraintProjection(Map<XTerm<Type>, CConstraint> m) {
		CSmtConstraint result = constraintProjection(m, 0);
		//FIXME do we need to check for this?
		if (!result.consistent())
			result.setInconsistent(); 
		return result; 
	}

	private CSmtConstraint constraintProjection(Map<XTerm<Type>, CConstraint> m, int depth) {
		CSmtConstraint result = self() == null? new CSmtConstraint(sys(),(XSmtVar<Type>)null,ts()) :
												new CSmtConstraint(sys(),self().type(),ts()); 
		for (XTerm<Type> t : terms()) {
			CSmtConstraint termConstraint = constraintProjection(t, m, depth); 
			if (termConstraint != null)
				result.addIn(termConstraint);
		}
		return result;
	}
	
	private static int MAX_DEPTH = 15;
	
	private CSmtConstraint constraintProjection(XTerm<Type> t, Map<XTerm<Type>, CConstraint> m, int depth) {
        if (t == null) return null;
        if (depth > MAX_DEPTH) {
        	// because the constraint returned by this method is in a sense "temporary"
        	// (it will be added to another constraint and its self will be substituted)
        	// we do not care what baseType it is associated with
            return new CSmtConstraint(sys(),(XSmtVar<Type>)null,ts());
        }
        CSmtConstraint res = (CSmtConstraint)m.get(t);
        if (res != null) 
        	return res;
        
        m.put(t, new CSmtConstraint(sys(),(XSmtVar<Type>)null,ts()));
        
        if (t instanceof CLocal) {
			CLocal v = (CLocal) t;
            X10LocalDef ld = v.def();
            if (ld != null) {
                Type ty = Types.get(ld.type());
                ty = PlaceChecker.ReplaceHereByPlaceTerm(ty, ld.placeTerm());
                CSmtConstraint ci = (CSmtConstraint)Types.realX(ty,ts());
                res = new CSmtConstraint(sys(),(XSmtVar<Type>)null,ts());
                ci = ci.instantiateSelf(v);
                res.addIn(ci);
                res.addIn(ci.constraintProjection(m, depth+1));
            }
        } else if (t instanceof XLit) { // no new info to contribute
        } else if (t instanceof XEQV) { // no new info to contribute
        } else if (t instanceof XUQV) { // no new info to contribute
        } else if (t instanceof XVar) { // no new info to contribute
        } else if (t instanceof XField<?,?>) { // no new info to contribute
        } else if (t instanceof XField<?,?>){
        	@SuppressWarnings("unchecked")
			XField<Type,XDef<Type>> f = (XField<Type,XDef<Type>>) t;
            XTerm<Type> target = f.receiver();
            CSmtConstraint targetConstraint = constraintProjection(target, m, depth+1); 
            
            Type baseType = f.type();
            CSmtConstraint ci = null;
            if (baseType == null) 
            	res = targetConstraint;
            else {
                ci = (CSmtConstraint)Types.realX(baseType,ts());
                XTerm<Type> v = thisVar(f);
                res = new CSmtConstraint(sys(),(XSmtVar<Type>)null,ts());
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
        } else if (t instanceof XExpr) {
            XExpr<Type> f = (XExpr<Type>)t; 
            for (XTerm<Type> a : f.children()) {
                CSmtConstraint argConstraint = constraintProjection(a, m, depth+1); //ancestors);
                if (argConstraint != null) {
                    if (res == null) 
                    	res = new CSmtConstraint(sys(),(XSmtVar<Type>)null,ts());
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
	
	private static XTerm<Type> thisVar(XField<Type,XDef<Type>> f) {
        if (f.field() instanceof X10FieldDef)
            return ((X10ClassDef) Types.get(((X10FieldDef) f.field()).container()).toClass().def()).thisVar();
        return null;
	}

	@Override
	public CSmtConstraint copy() {
		return new CSmtConstraint(this);
	}
	
//	@Override
//	public String toString() {
//		return "(self="+self() + ", " + "this=" + thisVar() +")" + super.toString(); 
//	}

}
