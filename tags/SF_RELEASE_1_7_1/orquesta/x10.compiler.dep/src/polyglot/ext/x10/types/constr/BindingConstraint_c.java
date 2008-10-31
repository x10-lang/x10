/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Receiver;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject_c;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;


/**
 * A representation of constraints of the form X1=t1 && ... Xk == tk.
 * Note that there is no unification, only checking. So it is possible
 * to represent such a constraint directly as a mapping from Xi to ti.
 * The constraint is implemented as a Map from variables to terms.
 * 
 * @author vj
 *
 */
public class BindingConstraint_c extends TypeObject_c implements BindingConstraint, Cloneable {

    ConstraintSystem sys;
    
    public ConstraintSystem constraintSystem() {
        return sys;
    }
    
    public X10TypeSystem typeSystem() {
        return (X10TypeSystem) super.typeSystem();
    }
    
        //Maps C_Var's to nodes.
	protected HashMap<C_Var,Promise> roots;
	public HashMap<C_Var, Promise> roots() { return roots; }
	
	// Map from variables to values for negative bindings.
	// protected Map negBindings;
	
	// For representation of T(:self == o), selfBinding is o.
	protected C_Var selfVar;
	
	boolean consistent = true;
	boolean valid = true;
	
	protected final X10TypeSystem typeSystem;

	public BindingConstraint_c(X10TypeSystem xts, ConstraintSystem sys) {
		super(xts, null);
		typeSystem= xts;
		this.sys = sys;
	}
	public BindingConstraint_c(X10TypeSystem ts, ConstraintSystem sys, int eqvCount, boolean consistent, boolean placeIsHere, C_Term placeTerm, boolean placePossiblyNull, Map<C_Var, Promise> roots, boolean valid, C_Var selfVar) {
		this(ts, sys);
		this.consistent = consistent;
		this.roots = new HashMap<C_Var,Promise>(roots);
		this.valid = valid;
		this.selfVar = selfVar;
	}


    /** Copy this constraint logically -- that is create a new constraint which contains
	 * the same equalities (as any) as the current one.
	 * */
	
	public BindingConstraint copy() {
		try {
			return copyInto(new BindingConstraint_c(typeSystem, sys));
		}
		catch (Failure f) {
			throw new InternalCompilerError("Copying constraint made it inconsistent.", f);
		}
	}
	/** 
	 * Clone this constraint, physically. Note that this is the usual default shallow copy.
	 */
	public BindingConstraint_c clone() {
	    try {
	        return (BindingConstraint_c) super.clone();
	    }
	    catch (CloneNotSupportedException e) {
	        throw new InternalCompilerError("clone failed");
	    }
	}
	
	/**
	 * Return the result of copying this into c. Assume that c will be 
	 * the depclause of the same base type as this, hence it is ok to 
	 * copy self-clauses as is. 
	 * @param c
	 * @return
	 * @throws Failure 
	 */
	private BindingConstraint copyInto(BindingConstraint_c c) throws Failure {
                        if (roots != null) {
                                c.roots = new HashMap<C_Var, Promise>();
                                HashMap<Promise, Promise> renaming = new HashMap<Promise,Promise>();
                                for (Iterator<Map.Entry<C_Var,Promise>> it = roots.entrySet().iterator();
                                it.hasNext(); ) {
                                    Map.Entry<C_Var, Promise> m = it.next();
                                    C_Var var = m.getKey();
                                    Promise p = m.getValue();
                                    Promise q = p.cloneRecursively(renaming);
                                    c.roots.put(var, q);
                                }
                        }
                        return c;
	}


	public void addIn(SimpleConstraint c) throws Failure {
	    if (c == null)
	        return;
	    assert c instanceof BindingConstraint;
	    addIn((BindingConstraint) c);
	}
	    
	public void addIn(BindingConstraint c) throws Failure {
	    if (c == null)
	        return;
	    assert c.constraintSystem() == sys;

	    HashMap<C_Var, C_Var> result = c.constraints();
	    addBindings(result);
	}
	
        public Set<C_Var> vars() {
            Set<C_Var> vars = new LinkedHashSet<C_Var>();
            
            for (Map.Entry<C_Var, C_Var> e : constraints().entrySet()) {
                vars.add(e.getKey());
                vars.add(e.getValue());
            }
            
            return vars;
        }

	public void addBindings(HashMap<C_Var,C_Var> result) throws Failure {
		if (result==null || result.isEmpty())
			return;
		for (Iterator<Map.Entry<C_Var,C_Var>> it = result.entrySet().iterator(); it.hasNext();) {
			Map.Entry<C_Var,C_Var> entry =  it.next();
			C_Var t1 = entry.getKey();
			C_Var t2 =  entry.getValue();
			addBinding(t1,t2);
		}
	}

	/**
	 * Is the constraint consistent? i.e. X=s and X=t have not been added to it,
	 * where s and t are not equal.
	 */
	public boolean consistent() {return consistent;}
	public void setInconsistent() {consistent = false;}
	
	/** Is the constraint valid? i.e. vacuous.
	 * 
	 */
	public boolean valid() { return valid;}
	
	public Promise intern(C_Var term) {
		try {
			return intern(term, null);
		}
		catch (Failure f) {
			// Failure should only happen if last != null, which it isn't.
			throw new InternalCompilerError("Could not intern a " + term + " into " + this + ".", f);
		}
	}
	/**
	 * Used to implement substitution:  if last != null, term, is substituted for 
	 * the term that was interned previously to produce the promise last. This is accomplished by
	 * returning last as the promise obtained by interning term, unless term is a literal, in which
	 * case last is forwarded to term, and term is returned. This way incoming and outgoing edges 
	 * (from fields) from last are preserved, but term now "becomes" last.
	 * Required: on entry, last.value == null.
	 * The code will work even if we have literals that are at types where properties are permitted.
	 * @param term
	 * @param last
	 * @return
	 * @throws Failure 
	 */
	public Promise intern(C_Term term, Promise last) throws Failure  {
		if (term instanceof Promise ) {
			Promise q = (Promise) term;
			// this is the case for literals, for here
			if (last != null) {
				last.bind(q);
			}
			return q;
		}
			
		// otherwise it must be a C_Var.
		if (! (term instanceof C_Var))
			throw new InternalCompilerError("Cannot intern  term  " + term
					+ "; it must be a promise or a C_Var, not " + term.getClass()+ ".");
		C_Var var = (C_Var) term;
		C_Var[] vars = var.vars();
		
		C_Var baseVar = vars[0];
		if (roots == null) roots = new HashMap<C_Var, Promise>();
		Promise p = (Promise) roots.get(baseVar);
		if (p == null) {
			p = (vars.length==1 && last != null) ? last : new Promise_c(baseVar);
			// Report.report(1, "Constraint_c.intern: adding " + var + " to roots for (:" + this+").");
			roots.put(baseVar, p);
		}
		return p.intern(vars, 1, last);
	}
	public void internRecursively(C_Var v, Constraint container) throws Failure {
		intern(v);
		propagateRecursively(v, container);
	}
	/**
	 * Return the promise obtained by looking up term in this. Does not create new
	 * nodes in the constraint graph. Does not return a forwarded promise.
	 */
	public Promise lookup(C_Term term) {
		Promise result = lookupPartialOk(term);
		if (! (result instanceof Promise_c))
			return result;
		// it must be the case that term is a C_Var.
		C_Var var = (C_Var) term;
		C_Var[] vars = var.vars();
		Promise_c resultC = (Promise_c) result;
		int index = resultC.lookupReturnValue();
		return (index==vars.length) ? result : null;
	}
	public Promise lookupPartialOk(C_Term term) {
		if (term == null) return null;
		if (term instanceof Promise )
			// this is the case for literals, for here
			return (Promise) term;
		// otherwise it must be a C_Var.
		if (! (term instanceof C_Var))
			throw new InternalCompilerError("Cannot lookup  term  " + term
					+ "; it must be a promise or a C_Var");
		if (roots ==null) return null;
		C_Var var = (C_Var) term;
		C_Var[] vars = var.vars();
		C_Var baseVar = vars[0];
		//Report.report(1, "Constraint_c: c=" + this + " looking up |"  + term + "| var=" + baseVar);
		Promise p = (Promise) roots.get(baseVar);
		//Report.report(1, "Constraint_c: p=" + p+ " : " + p.getClass());
		if (p == null) return null;
		return p.lookup(vars, 1);
	}
	
	/**
	 * Add t1=t2 to the constraint, unless it is inconsistent. 
	 * @param var -- t1
	 * @param val -- t2
	 */
	public void addBinding(C_Var t1, C_Var t2) throws Failure  {
		Constraint result = null;
		String oldString = this.toString();
		try { 
			
			if (!consistent)
			    return;

			if (roots == null) roots = new HashMap<C_Var, Promise>();
			if (t1==null) 
				t1 = this.typeSystem.NULL();
			if (t2==null) 
				t2 = this.typeSystem.NULL();
			if (selfVar !=null) {
				t1 = t1.substitute(new C_Special_c(X10Special.SELF, selfVar.type()), selfVar);
				t2 = t2.substitute(new C_Special_c(X10Special.SELF, selfVar.type()), selfVar);
			}
			Promise p1 = intern(t1);
			Promise p2 = intern(t2);
			boolean modified = p1.bind(p2);
			if (t1 instanceof C_Var) {
				possiblyAddTypeConstraint((C_Var) t1, p2);
			}
			if (t2 instanceof C_Var) {
				possiblyAddTypeConstraint((C_Var) t2, p2);
			}
			valid &= ! modified;
		} catch (Failure z) {
			setInconsistent();
			throw new Failure("Adding binding " + t1 + "=" + t2 + " to " + this 
					+ " has made it inconsistent.", z);
		}
	}
	public void addBindingPromise(C_Var t1, Promise p)  {
		try { 
			
			if (!consistent) 
			    return;
			if (roots == null) roots = new HashMap<C_Var, Promise>();
			if (t1==null) t1 = this.typeSystem.NULL();
			if (selfVar !=null) {
				t1 = t1.substitute(new C_Special_c(X10Special.SELF, selfVar.type()), selfVar);
			}
			Promise p1 = intern(t1);
			boolean modified = p1.bind(p);
			
		} catch (Failure z) {
			consistent=false;
			if (true)
			    return;
			throw new InternalCompilerError("Adding binding " + t1 + "=" + p + " to " + this 
					+ " has made it inconsistent.");
		}
	}

	
	private C_Var rootBindingForTerm(C_Var t1) {
		//Report.report(1, "Constraint_c.possiblyAddTypeConstraint " + t1 + " " + target);
		// Check if t1's type forces t1 to be equal to something (t3). If so, add
		// t2=t3 in there.
		C_Var result = null;
		C_Var t1Root = t1.rootVar();
		
		BindingConstraint c = getRealClauseBindingConstraint(t1Root);
		if (c != null) {
		    X10Type xType = (X10Type) t1Root.type();
		    Constraint xd = xType == null ? null : X10TypeMixin.depClause(xType);

		    if (xd!= null) {
		        C_Var cVar = xd.selfVar();
		        if (t1Root.equals(cVar)) {
		            t1 = (C_Var) t1.substitute(new C_Special_c(X10Special.SELF, cVar.type()), cVar);
		        }
		    }

		    Promise p = c.lookup(t1);
		    if (p != null) {
		        // aha there really is a term that t1's roottype binds t1 to!
		        result = p.term();
		        if (result instanceof C_Var && ((C_Var) result).rootVar().equals(C_Special.Self)) {
		            if (xd != null) {
		                C_Var sVar = xd.selfVar();
		                if (sVar != null) {
		                    result = result.substitute(sVar, C_Special_c.Self);
		                } // else nothing
		            } // else nothing.
		        } 
		    }
		}
		return result;
	}
	
	private BindingConstraint getRealClauseBindingConstraint(C_Var t1Root) {
	    X10Type xType = (X10Type) t1Root.type();
	    
	    if (t1Root.equals(C_Special_c.Self)) {
	        return this;
	    }
	    
	    return getRealClauseBindingConstraint(xType);
	}
	
    private BindingConstraint getRealClauseBindingConstraint(X10Type xType) {
        if (xType != null) {
            Constraint xc = X10TypeMixin.realClause(xType);
            return getBindingConstraint(xc);
        }
        return null;
    }
    
    private BindingConstraint getBindingConstraint(Constraint xc) {
        BindingConstraint c = null;
        for (SimpleConstraint sc : xc.conjuncts()) {
            if (sc instanceof BindingConstraint) {
                c = (BindingConstraint) sc;
                break;
            }
        }
        return c;
    }
	private void possiblyAddTypeConstraint(C_Var t1, Promise target) {
		//Report.report(1, "Constraint_c.possiblyAddTypeConstraint " + t1 + " " + target);
		// Check if t1's type forces t1 to be equal to something (t3). If so, add
		// target=t3 in there.
		C_Var p1Root = t1.rootVar();
		Type type = p1Root.type();
		if (type != null) {
			X10Type xType = (X10Type) type;
			if (p1Root.equals(C_Special.Self))
				return;
			    
			BindingConstraint c = getRealClauseBindingConstraint(p1Root);

			// Constraint c = p1Root.equals(C_Special.Self) ? this : xType.realClause();
		//	Report.report(1, "Constraint_c.possiblyAddTypeConstraint c=" + c);
			if (c != null) {
				C_Var t1Root = t1.rootVar();
				Constraint xd = X10TypeMixin.depClause(xType);
				if (xd!= null) {
					C_Var cVar = xd.selfVar();
					if (t1Root.equals(cVar)) {
						t1 = (C_Var) t1.substitute(new C_Special_c(X10Special.SELF, cVar.type()), cVar);
					}
				}
				Promise p = c.lookup(t1);
				if (p != null) {
					// aha there really is a term that t1's roottype binds t1 to!
					C_Var t3 = p.term();
					if (t3 instanceof C_Var && ((C_Var) t3).rootVar().equals(C_Special.Self)) {
						if (xd != null) {
							C_Var sVar = xd.selfVar();
							if (sVar != null) {
								t3 = t3.substitute(sVar, C_Special_c.Self);
								addBindingPromise(t3, target);
							} // else nothing
						} // else nothing.
					} else {
						addBindingPromise(t3, target);
					}
				}
			}
		}
		
	}
		
	
	/*if ((! result) && (val2 instanceof C_Var)) {
		C_Var indirect = (C_Var) val2;
		C_Var rootVar = indirect.rootVar();
		X10Type type = (X10Type) rootVar.type();
		Constraint c = rootVar.equals(C_Special.self) ? this : type.depClause();
		if (c!=null) {
			C_Var val2self = (C_Var) val2.substitute(C_Special.self, rootVar);
			result = c.entails(val2self, val);
			if ((!result) && (var instanceof C_Special)) {
//				check the selfbinding
				C_Special s = (C_Special) var;
				if (s.kind().equals(C_Special.SELF)) {
					result = (val==varWhoseTypeThisIs || val.equals(varWhoseTypeThisIs));
				}
			}
		}
	}
	*/

	public HashMap<C_Var, C_Var> constraints() {
		return constraints(new HashMap<C_Var,C_Var>());
	}
	public HashMap<C_Var, C_Var> constraints(HashMap<C_Var, C_Var> result) {
		return constraints(result, null, null);
	}
	public HashMap<C_Var, C_Var> constraints(HashMap<C_Var, C_Var> result, C_Var newSelf, C_Var newThis) {
		return constraints(result, null, newSelf, newThis);
	}
	
	public HashMap<C_Var, C_Var> constraints(HashMap<C_Var,C_Var> result, C_Term prefix, 
			C_Var newSelf, C_Var newThis) {
		if (roots==null) return result;
		for (Iterator<Promise> it = roots.values().iterator(); it.hasNext();) 
			it.next().dump(result, prefix, newSelf, newThis);
		return result;
	}
	public HashMap<C_Var, C_Var> constraints(C_Var y) {
		Promise p = lookup(y);
		if (p == null) return new HashMap<C_Var,C_Var>();
		C_Var rep = p.term();
		return constraints(rep, C_Special.Self);
	}
	
	public HashMap<C_Var, C_Var> constraints(C_Var y, C_Var newSelf) {
		HashMap<C_Var,C_Var> result = new HashMap<C_Var,C_Var>();
		return constraints(result, y, newSelf, C_Special.This);
	}
	
	public boolean entailedBy(Constraint xother, Constraint me) {
	    BindingConstraint other = getBindingConstraint(xother);
	    if (other == null || (! other.consistent()) || valid() )
			return true;
		if (roots != null) {
		    HashMap<C_Var, C_Var> result = constraints();
		    try {
		        other.saturate(me);
		    }
		    catch (Failure f) {
		        // inconsistent implies true
		        return true;
		    }
		    for (Iterator it = result.entrySet().iterator(); it.hasNext();) {
		        Map.Entry entry = (Map.Entry) it.next();
		        if (! other.entailsBinding((C_Var) entry.getKey(), (C_Var) entry.getValue(), me))
		            return false;
		    }

		}
		return true;
	}

    /** Does this entail that t1=t2? */
	public boolean entailsBinding(C_Var t1, C_Var t2, Constraint container) {
		boolean result = entailsImmed(t1, t2);
		if (result) return result;

		result = checkSelfEntails(t1, t2);
		if (result) return result;

		C_Var t1r = rootBindingForTerm(t1);
		if (t1r != null && (! t1r.equals(t1)) && entailsBinding(t1r, t2, container)) return true;

		C_Var t2r = rootBindingForTerm(t2);
		if (t2r != null && (! t2r.equals(t2)) && entailsBinding(t1, t2r, container)) return true;

		// \exists q. x=q is true since x=x.
                if (t1 instanceof C_EQV) {
                    if (roots == null || ! roots.containsKey(t1)) return true;
                }
                if (t2 instanceof C_EQV) {
                    if (roots == null || ! roots.containsKey(t2)) return true;
                }
		return false;
		
	}
	public boolean entailsImmed(C_Var t1, C_Var t2) {
		
		boolean result = false;
		try {
			//Report.report(1, "Constraint_c: Does (:" + this +  ") entail " 
			//		+ t1 + "=" + t2+ "?");
			if (! consistent) return result = true;
			if (selfVar !=null) {
				t1 = t1.substitute(new C_Special_c(X10Special.SELF, selfVar.type()), selfVar);
				t2 = t2.substitute(new C_Special_c(X10Special.SELF, selfVar.type()), selfVar);
			}
			Promise p1 = lookupPartialOk(t1);
			if (p1 == null) // No match, the term t1 is not equated to anything by this.
				return result=false;
			
			int r1Count=0;
			C_Var[] vars1 = null;
			if (p1 instanceof Promise_c ){
				r1Count = ((Promise_c) p1).lookupReturnValue();
				vars1 = ((C_Var) t1).vars();
			}
			Promise p2 = lookupPartialOk(t2);
			if (p2==null) // No match, the term t2 is not equated to anything by this.
				return result=false;
			
			int r2Count=0;
			C_Var[] vars2 = null;
			if (p2 instanceof Promise_c ){
				r2Count = ((Promise_c) p2).lookupReturnValue();
				vars2 = ((C_Var) t2).vars();
			}
			
			if ((r1Count==0 || r1Count == vars1.length) && (r2Count==0 || r2Count == vars2.length)) {
				// exact lookups
				result = p1.equals(p2);
				return result;
			} 
			// at least one of them had a suffix left over
			// Now the returned promises must match, and they must have the same suffix.
			if (! p1.equals(p2)) return false;
			//	Now ensure that they have the same suffix left over. 
			int residual1 = vars1.length-r1Count, residual2=vars2.length-r2Count;
			if (residual1 != residual2) return false;
			for (int i=0; i < residual1; i++) {
				if (! vars1[r1Count+i].name().equals(vars2[r2Count+i].name()))
					return result=false;
			}
			return result=true;
	} finally {
	//	Report.report(1, "Constraint_c: (:" + this + (result? ") entails " : ") does not entail ") 
		//		+ t1 + "=" + t2+ ".");
	}
		
		
	/*
		if ((! result) && (val2 instanceof C_Var)) {
			C_Var indirect = (C_Var) val2;
			C_Var rootVar = indirect.rootVar();
			X10Type type = (X10Type) rootVar.type();
			Constraint c = rootVar.equals(C_Special.self) ? this : type.depClause();
			if (c!=null) {
				C_Var val2self = (C_Var) val2.substitute(C_Special.self, rootVar);
				result = c.entails(val2self, val);
				if ((!result) && (var instanceof C_Special)) {
//					check the selfbinding
					C_Special s = (C_Special) var;
					if (s.kind().equals(C_Special.SELF)) {
						result = (val==varWhoseTypeThisIs || val.equals(varWhoseTypeThisIs));
					}
				}
			}
		}
		*/
	}
	
	protected boolean checkSelfEntails(C_Var var, C_Term val) {
		if (selfVar == null) return false;
		C_Var var1 = (C_Var) var.substitute(selfVar, C_Special.Self);
		boolean result = var1.equals(val);
		return result;
	}
	
	 public C_Var find(String varName) {
		 if ((! consistent) || roots ==null) return null;
		// Report.report(1, "Constraint_c.find: roots are " + roots);
		 Promise self = (Promise) roots.get(C_Special_c.Self);
		 if (self == null) return null;
		 Promise result = self.lookup(varName);
		 return result==null ? null : result.term();
	 }
	
	
	public String toString() { 
	        String str = constraints().toString();
		return  "(:" + str.substring(1, str.length()-1) + ")";
	}

	public BindingConstraint substitute(C_Var y, C_Root x, boolean propagate, Constraint container, HashSet<C_Term> visited) throws Failure {
		assert (y != null && x !=null);
		if (y.equals(x)) return this;
		Promise last = lookupPartialOk(x);
		if (last == null) return this; 	// x does not occur in this
		BindingConstraint_c result = clone();
		result.applySubstitution(y,x, propagate, container);
		return result;
	}
	public void applySubstitution( HashMap<C_Root, C_Var> subs, boolean propagate, Constraint container) throws Failure {
		for (Iterator<Map.Entry<C_Root, C_Var>> it = subs.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<C_Root, C_Var> e = it.next();
			C_Root x = e.getKey();
			C_Var y = e.getValue();
			applySubstitution(y,x, propagate, container);
		}
	}
	public void applySubstitution(C_Var y, C_Root x, boolean propagate, Constraint container) throws Failure {
		if (roots == null)
			// nothing to substitute
			return;
		Promise p = (Promise) roots.get(x);
		if (p == null)
			// nothing to substitute
			return;
		// Remove this now so that you avoid alpha capture issues. y may be the same as x. 
		roots.remove(x);
		Promise q = intern(y);
		if (q.term().equals(x)) {
			// Cannot replace x with x.  Instead,
                        // introduce an EQV and substitute that for x.
			
			C_EQV v = container.genEQV(x.type(), false);

			// Clone the root map, with the renaming map primed
                        // with x -> v
			HashMap<Promise, Promise> renaming = new HashMap<Promise,Promise>();
			renaming.put(q, new Promise_c(v));
			
			for (Iterator<Map.Entry<C_Var,Promise>> it = roots.entrySet().iterator();
			it.hasNext(); ) {
				Map.Entry<C_Var, Promise> m = it.next();
				C_Var var = m.getKey();
				Promise p2 = m.getValue();
				Promise q2 = p2.cloneRecursively(renaming);
				m.setValue(q2);
			}

			return;
		}
		replace(q, p);
		if (p instanceof C_Lit) {
//			try {
				q.bind(p);
//			} catch (Failure f) {
//				throw new InternalCompilerError("Error in replacing " + x 
//						+ " with " + y + " in " + this + ": binding failure with "  + p);
//			}
			if (propagate) 
				propagate(y, container);
			return;
		}
		Promise xf = p.value();
		if (xf != null) {
			//addBinding(y, xf.term());
//			try {
				q.bind(xf);
//			} catch (Failure f) {
//				throw new InternalCompilerError("Error in replacing " + x 
//						+ " with " + y + " in " + this + ": binding failure with "  + xf, f);
//			}
		} else {
			
			HashMap<String, Promise> fields = p.fields(); 
			if (fields != null)
				for (Iterator<Entry<String,Promise>> it = fields.entrySet().iterator(); it.hasNext();) {
					Entry<String, Promise> entry = it.next();
					String s = entry.getKey();
					Promise orphan = entry.getValue();
					if (q == orphan) {
					    throw new Failure("Cannot substitute " + y + " for " + x + " in " + this + "; cycle found with field: " + s + "->" + orphan);
					}
//					try {
						q.addIn(s, orphan);
						C_Field oldTerm = (C_Field) orphan.term();
						FieldInstance oldfi = oldTerm.fieldInstance();
						C_Field newTerm = new C_Field_c(oldfi, (C_Var) q.term());
						orphan.setTerm(newTerm);
//					} catch (Failure f) {
//						throw new InternalCompilerError("Error in replacing " + x 
//								+ " with " + y + " in " + this + ": failure in forwarding " + entry);
//					}
				}
		}
		
		
		// Now move all the terms over.
		if (propagate) 
			propagate(y, container);
		
		// Report.report(1, "Constraint_c: applySubstitution:" + thisString +  " = " + this);
	}
	/** Replace all pointers entering x in this constraint with pointers entering y.
	 * 
	 * @param y
	 * @param x
	 */
	public void replace(Promise y, Promise x) {
		Collection<Promise> rootPs = roots.values();
		for (Iterator<Promise> it = rootPs.iterator(); it.hasNext();) {
			Promise p = it.next();
			if (! p.equals(x)) {
				p.replaceDescendant(y, x);
			}
		}
	}
	
	public void propagate(C_Var y, Constraint container) throws Failure {
		X10Type yType = (X10Type) y.type();
		if (yType == null) return;
		BindingConstraint yTypeRC = getRealClauseBindingConstraint(yType);
		if (yTypeRC == null) return;
		yTypeRC = yTypeRC.clone();
		yTypeRC = yTypeRC.substitute(y,C_Special.Self, false, container, new HashSet<C_Term>());
		addIn(yTypeRC);
		
	}
	public void propagateRecursively(C_Var y, Constraint container) throws Failure {
		X10Type yType = (X10Type) y.type();
		if (yType == null) return;
		
		// This constraint is a copy of a class dep clause, with stuff added in.
		// y.type is the root type and has class dep clause.
		// So, yTypeRC is the class dep clause; which is not necessarily smaller than
		// this constraint ==> infinite loop.
        // Example: this == self{i=self.i, j=self.j->self.i}
		// where self : ValueProp1(:self.i=self.j)
		BindingConstraint yTypeRC = getRealClauseBindingConstraint(yType);
		if (yTypeRC == null) return;
		yTypeRC = yTypeRC.clone();
		yTypeRC.saturate(container);
		yTypeRC = yTypeRC.substitute(y,C_Special.Self, false, container, new HashSet<C_Term>());
		addIn(yTypeRC);
	}
	
	public void saturate(Constraint container) throws Failure {
		if (roots != null) {
			final HashMap<C_Var,Promise> rootSnapshot = (HashMap<C_Var,Promise>) roots.clone();
			for (Iterator<C_Var> it = rootSnapshot.keySet().iterator(); it.hasNext();) {
				C_Var var = it.next();
				propagateRecursively(var, container);
			}
		}
	}
	public boolean hasVar(C_Root v) {
		if (roots == null) return false;
		return roots.keySet().contains(v);
	}
	
    public SimpleConstraint binaryOp(Binary.Operator op, Constraint other, Constraint me) {
        return null;
    }

    public SimpleConstraint unaryOp(Unary.Operator op) {
        return null;
    }

}
