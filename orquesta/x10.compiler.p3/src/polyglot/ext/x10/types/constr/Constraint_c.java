/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import polyglot.types.LocalInstance_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ast.Binary.Operator;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.ext.x10.types.X10LocalInstance_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject_c;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;


/**
 * A representation of constraints of the form X1=t1 && ... Xk == tk.
 * Note that there is no unification, only checking. So it is possible
 * to represent such a constraint directly as a mapping from Xi to ti.
 * The constraint is implemented as a Map from variables to terms.
 * 
 * @author vj
 *
 */
public class Constraint_c extends TypeObject_c implements Constraint, Cloneable {

	//Maps C_Var's to nodes.
	protected HashMap<C_Var,Promise> roots;
	public HashMap<C_Var,Promise> roots() { return roots;}
	
	// Map from variables to values for negative bindings.
	// protected Map negBindings;
	
	// The place clause for this type.
	protected boolean placePossiblyNull; // true if loc could be null.
	protected boolean placeIsHere; // true if loc could be here.
	protected C_Term_c placeTerm;        // if non null, place could be here or some placeTerm.
	
	
	// For representation of T(:self == o), selfBinding is o.
	protected C_Var selfVar;
	
	public int eqvCount() { return eqvCount; }
	public boolean placeIsHere() { return placeIsHere; }
	public boolean placePossiblyNull() { return placePossiblyNull; }
	public C_Term placeTerm() { return placeTerm; }
	
	boolean consistent = true;
	boolean valid = true;

	protected final X10TypeSystem typeSystem;

	public Constraint_c(X10TypeSystem xts) {
		super(xts);
		typeSystem= xts;
	}
	public Constraint_c(X10TypeSystem ts, int eqvCount, boolean consistent, boolean placeIsHere, C_Term placeTerm, boolean placePossiblyNull, Map<C_Var, Promise> roots, boolean valid, C_Var selfVar) {
		this(ts);
		this.eqvCount = eqvCount;
		this.consistent = consistent;
		this.placeIsHere = placeIsHere;
		this.placeTerm = (C_Term_c) placeTerm;
		this.placePossiblyNull = placePossiblyNull;
		this.roots = new HashMap<C_Var,Promise>(roots);
		this.valid = valid;
		this.selfVar = selfVar;
	}
	
	/** Copy this constraint logically -- that is create a new constraint which contains
	 * the same equalities (as any) as the current one.
	 * */
	
	public Constraint copy() {
		Constraint_c c = new Constraint_c(typeSystem);
		try {
			return copyInto(c);
		}
		catch (Failure f) {
			c.setInconsistent();
			return c;
		}
	}
	
	/** 
	 * Clone this constraint, physically. Note that this is the usual default shallow copy.
	 */
	public Constraint clone() {
		try {
			Constraint_c clone = (Constraint_c) super.clone();
			if (roots != null) {
				clone.roots = new HashMap<C_Var, Promise>();
				HashMap<Promise, Promise> renaming = new HashMap<Promise,Promise>();
				for (Iterator<Map.Entry<C_Var,Promise>> it = roots.entrySet().iterator();
				it.hasNext(); ) {
					Map.Entry<C_Var, Promise> m = it.next();
					C_Var var = m.getKey();
					Promise p = m.getValue();
					Promise q = p.cloneRecursively(renaming);
					clone.roots.put(var, q);
				}
			}
			return clone;
		} catch (CloneNotSupportedException z) {
//			  But it is!!
			return null;
		}
	}
	/**
	 * Return the result of copying this into c. Assume that c will be 
	 * the depclause of the same base type as this, hence it is ok to 
	 * copy self-clauses as is. 
	 * @param c
	 * @return
	 */
	private Constraint copyInto(Constraint_c c) throws Failure {
		c.addIn(this);
		c.placePossiblyNull = placePossiblyNull;
		c.placeIsHere = placeIsHere;
		c.placeTerm = placeTerm;
		// represent varWhoseTypeThisIs via a self==this constraint.
		return c;
	}
	public Constraint addIn(Constraint c)  throws Failure {
		if (c !=null) {
			HashMap result = c.constraints();
			addBindings(result);
			C_Var v = c.selfVar();
			if (v != null ) {
				if (selfVar == null) {
				selfVar = v;
				} else {
					addSelfBinding(v, this, typeSystem);
				}
			}
		}
		return this;
	}
	public Constraint addBindings(HashMap<C_Var,C_Var> result) throws Failure {
		if (result==null || result.isEmpty())
			return this;
		for (Iterator<Map.Entry<C_Var,C_Var>> it = result.entrySet().iterator(); it.hasNext();) {
			Map.Entry<C_Var,C_Var> entry =  it.next();
			C_Var t1 = entry.getKey();
			C_Var t2 = entry.getValue();
			addBinding(t1,t2);
		}
		return this;
	}
	public static Constraint makeBinding(C_Var var, C_Var val, X10TypeSystem xts) throws Failure {
		Constraint c = new Constraint_c(xts);
		return c.addBinding(var,val);
	}
	public C_Var selfVar() {
		return selfVar;
	}
	/**
	 * Add the constraint self==val.
	 * @param val
	 * @param c
	 * @return
	 */
	public static Constraint addSelfBinding(C_Var val, Constraint c, X10TypeSystem xts) throws Failure {
		c = (c==null) ? new Constraint_c(xts) : c;
//		if (c.selfVar() == null) {
		if (val instanceof C_Var && ! (val instanceof C_Lit)) {
			c.setSelfVar((C_Var) val);
			return c;
		}
		
		c =  c.addBinding(C_Special.Self, val);
		
		return c;
	}
	public void setSelfVar(C_Var var) {
		selfVar = var;
	}
	

//	public static final transient X10TypeSystem typeSystem = X10TypeSystem_c.getTypeSystem();

	/**
	 * Is the constraint consistent? i.e. X=s and X=t have not been added to it,
	 * where s and t are not equal.
	 */
	public boolean consistent() {return consistent;}
	
	/** Is the constraint valid? i.e. vacuous.
	 * 
	 */
	public boolean valid() { return valid;}
	public boolean isLocal() { return placePossiblyNull || placeIsHere; }
	public boolean isPossiblyRemote() { return ! isLocal();}
	
	public Promise intern(C_Var term)  {
		return intern(term, null);
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
	 */
	public Promise intern(C_Term term, Promise last)  {
		if (term instanceof Promise ) {
			Promise q = (Promise) term;
			// this is the case for literals, for here
			if (last != null) {
				try {
					last.bind(q);
				} catch (Failure f) {
					throw new InternalCompilerError("A term ( " + term 
							+ ") cannot be interned to a promise (" + last + ") whose value is not null.");
				}
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
	public void internRecursively(C_Var v) throws Failure {
		intern(v);
		propagateRecursively(v);
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
	 * @param asVar -- t1
	 * @param val -- t2
	 */
	public Constraint addBinding(C_Var t1, C_Var t2) throws Failure {
		Constraint result = null;
		String oldString = this.toString();
		try { 
			if (!consistent ) return result=this; 
			if (roots == null) roots = new HashMap<C_Var, Promise>();
			if (t1==null) 
				t1 = this.typeSystem.NULL();
			if (t2==null) 
				t2 = this.typeSystem.NULL();
			if (selfVar !=null) {
				t1 = t1.substitute(C_Special_c.Self, selfVar);
				t2 = t2.substitute(C_Special_c.Self, selfVar);
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
			result=this;
		} catch (Failure z) {
			consistent=false;
			if (true)
				return this;
//			throw new InternalCompilerError("Adding binding " + t1 + "=" + t2 + " to " + this 
//					+ " has made it inconsistent.");
		}
		return result;
	}
	public Constraint addBindingPromise(C_Var t1, Promise p)  {
		try { 
			
			if (!consistent ) return this; 
			if (roots == null) roots = new HashMap<C_Var, Promise>();
			if (t1==null) t1 = this.typeSystem.NULL();
			if (selfVar !=null) {
				t1 = t1.substitute(C_Special_c.Self, selfVar);
			}
			Promise p1 = intern(t1);
			boolean modified = p1.bind(p);
			
		} catch (Failure z) {
			consistent=false;
//			throw new InternalCompilerError("Adding binding " + t1 + "=" + p + " to " + this 
//					+ " has made it inconsistent.");
		}
		return this;
	}

	private C_Var rootBindingForTerm(C_Var t1) {
		//Report.report(1, "Constraint_c.possiblyAddTypeConstraint " + t1 + " " + target);
		// Check if t1's type forces t1 to be equal to something (t3). If so, add
		// t2=t3 in there.
		C_Var result = null;
		C_Var t1Root = t1.rootVar();
		
		X10Type xType = (X10Type) t1Root.type();
		if (t1Root.equals(C_Special_c.Self) || xType != null) {
			Constraint c = t1Root.equals(C_Special_c.Self) ? this 
					: ((X10Type) t1Root.type()).realClause();
			
			// Constraint c = p1Root.equals(C_Special.Self) ? this : xType.realClause();
		//	Report.report(1, "Constraint_c.possiblyAddTypeConstraint c=" + c);
			if (c != null) {
				Constraint xd = xType == null ? null : xType.depClause();
				
					if (xd!= null) {
						C_Var cVar = xd.selfVar();
						if (t1Root.equals(cVar)) {
							t1 = (C_Var) t1.substitute(C_Special_c.Self, cVar);
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
		}
		return result;
	}
	private void possiblyAddTypeConstraint(C_Var t1, Promise target) {
		//Report.report(1, "Constraint_c.possiblyAddTypeConstraint " + t1 + " " + target);
		// Check if t1's type forces t1 to be equal to something (t3). If so, add
		// target=t3 in there.
		C_Var p1Root = t1.rootVar();
		Type type = p1Root.type();
		if (type != null) {
			X10Type xType = (X10Type) type;
			Constraint c;
			if (p1Root.equals(C_Special.Self))
				return;
			else 
				c = xType.realClause();
			// Constraint c = p1Root.equals(C_Special.Self) ? this : xType.realClause();
		//	Report.report(1, "Constraint_c.possiblyAddTypeConstraint c=" + c);
			if (c != null) {
				C_Var t1Root = t1.rootVar();
				Constraint xd = xType.depClause();
				if (xd!= null) {
					C_Var cVar = xd.selfVar();
					if (t1Root.equals(cVar)) {
						t1 = (C_Var) t1.substitute(C_Special_c.Self, cVar);
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
	/**
	 * Add a boolean term.
	 * @param term
	 */
	public Constraint addTerm(C_Var term) throws Failure {
		C_Lit val = this.typeSystem.TRUE();
		if (term instanceof C_UnaryTerm) {
			C_UnaryTerm t = (C_UnaryTerm) term;
			String op = t.op();
			if (op.equals("!")) {
				return addBinding((C_Var) t.arg(), val.not());
			}
		}
		return addBinding(term, val);
	//	throw new Failure("Cannot add term |" + term
	//			+ "| to constraint. It must be a literal or a variable, or ! of a literal or variable.");
	}
	
	
	/**
	 * If other is not inconsistent, and this is consistent,
	 * checks that each binding X=t in other also exists in this.
	 * @param other
	 * @return
	 */
	public boolean entails(Constraint other) {
		if (! consistent()) return true;
		if (other == null || other.valid()) return true;
		boolean result = other.entailedBy(this);
		if ( Report.should_report(Report.types,1))
			Report.report(1, this + (result? " entails " : " does not entail ") + other);
		return  result;
	}
	
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
	
	public boolean entailedBy(Constraint other) {
		if ((! other.consistent()) || valid() )
			return true;
		assert (roots !=null);
		HashMap result = constraints();
		try {
			other.saturate();
		}
		catch (Failure f) {
			// other is inconsistent
			return true;
		}
		for (Iterator it = result.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (! other.entails((C_Var) entry.getKey(), (C_Var) entry.getValue()))
				return false;
		}
		return true;
	}
	
	
	/*public boolean entails(Constraint other) {
		//Report.report(1, "Constraint: " + this + " entails " + other + "?");
		if (other == null) return true;
		if (! consistent) return true;
		if (! other.consistent()) return false;
		// now both are consistent
		Set keys = other.bindings().entrySet();
		//Report.report(1, "Constraint: set is |" + keys + "|");
		boolean result = true;
		for (Iterator it = keys.iterator(); result && it.hasNext();) {
			Map.Entry i = (Map.Entry) it.next();
			C_Term val = (C_Term) i.getValue();
			C_Var var = (C_Var) i.getKey();
			result = entails(var, val);
		}
	//Report.report(1, "Constraint: " + this + " entails " + other + "? " + result);
		return result;
	}*/

	public boolean entails(C_Var t1, C_Var t2) {
		boolean result = entailsImmed(t1, t2);
		if (result) return result;
		
		result = checkSelfEntails(t1, t2);
		if (result) return result;

		C_Var t1r = rootBindingForTerm(t1);
		if (t1r != null && (! t1r.equals(t1)) && entails(t1r, t2)) return true;

		C_Var t2r = rootBindingForTerm(t2);
		if (t2r != null && (! t2r.equals(t2)) && entails(t1, t2r)) return true;

		// \exists q. x=q is true since x=x.
		if (t1 instanceof C_EQV) {
			if (roots == null || ! roots.containsKey(t1)) return true;
		}
		if (t2 instanceof C_EQV) {
			if (roots == null || ! roots.containsKey(t2)) return true;
		}

//		if (t1 instanceof C_Var) {
//			result =checkSelfEntails((C_Var) t1,t2);
//			if (result) return result;
//		}
//		if (t1 instanceof C_Var) {
//			C_Var t1r = rootBindingForTerm((C_Var)t1);
//			if (t1r != null && (! t1r.equals(t1)) && entails(t1r, t2)) return true;
//		}
//		if (t2 instanceof C_Var) {
//			C_Var t2r = rootBindingForTerm((C_Var)t2);
//			if (t2r != null && (! t2r.equals(t2)) && entails(t1, t2r)) return true;
//		}
		
		return false;
		
	}
	public boolean entailsImmed(C_Var t1, C_Var t2) {
		
		boolean result = false;
		try {
			//Report.report(1, "Constraint_c: Does (:" + this +  ") entail " 
			//		+ t1 + "=" + t2+ "?");
			if (! consistent) return result = true;
			if (selfVar !=null) {
				t1 = t1.substitute(C_Special_c.Self, selfVar);
				t2 = t2.substitute(C_Special_c.Self, selfVar);
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
	public boolean equiv(Constraint other) {
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " );
		boolean result = entails(other);
		if (result) result = (other==null)? valid : other.entails(this);
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " + result);
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
		str = str.substring(1, str.length()-1);
		if (selfVar != null)
			str = "self=" + selfVar + (str.equals("") ? "" : ", " + str);
		return  "(:" + str + ")" ;
		}

	protected int eqvCount;
	public C_EQV genEQV(Type type, boolean isSelfVar) {
		return genEQV(type, isSelfVar, true);
	}
	public C_EQV genEQV(Type type, boolean isSelfVar, boolean hidden) {
		String name = "_" + eqvCount++;
		X10TypeSystem xts = (X10TypeSystem) type.typeSystem();
		LocalInstance li = xts.localDef(Position.COMPILER_GENERATED, Flags.FINAL, Types.ref(type), name).asInstance();
		C_EQV result = new C_EQV_c(li, isSelfVar, hidden);
		return result;
	}
	public Constraint substitute(C_Var y, C_Root x) throws Failure {
		return substitute(y, x, true);
	}
	public Constraint substitute(C_Var y, C_Root x, boolean propagate) throws Failure {
		assert (y != null && x !=null);
		if (y.equals(x)) return this;
		Promise last = lookupPartialOk(x);
		if (last == null) return this; 	// x does not occur in this
		Constraint result = clone();
		result.applySubstitution(y,x, propagate);
		return result;
	}
	public Constraint substitute(HashMap<C_Root, C_Var> subs) throws Failure {
		return substitute(subs, true);
	}
	public Constraint substitute(HashMap<C_Root, C_Var> subs, boolean propagate) throws Failure {
		if (subs==null || subs.isEmpty()) return this;
		boolean notneeded = true;
		for (Iterator<Map.Entry<C_Root, C_Var>> it = subs.entrySet().iterator();
		notneeded && it.hasNext(); ) {
			Map.Entry<C_Root, C_Var> e = it.next();
			C_Root x = e.getKey();
			C_Var y = e.getValue();
			
			notneeded = (y.equals(x)) || lookupPartialOk(x) == null;
		}
		if (notneeded) return this;
		Constraint result = clone();
		result.applySubstitution(subs, propagate);
		return result;
	}
	public Constraint substitute(C_Var[] ys, C_Root[] xs) throws Failure {
		return substitute(ys, xs, true);
	}
	public Constraint substitute(C_Var[] ys, C_Root[] xs, boolean propagate) throws Failure {
		assert xs.length == ys.length;
		final int n = xs.length;
		Constraint result = this;
		for (int i=0; i < n; i++) {
			C_Root x = xs[i];
			C_Var y = ys[i];
			if (! (y.equals(x) || lookupPartialOk(x) == null)) {
				if (result==this)
					result = clone();
				result.applySubstitution(y, x, propagate);
			}
		}
		return result==null ? this : result;
	}
	public void applySubstitution( HashMap<C_Root, C_Var> subs, boolean propagate) throws Failure {
		for (Iterator<Map.Entry<C_Root, C_Var>> it = subs.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<C_Root, C_Var> e = it.next();
			C_Root x = e.getKey();
			C_Var y = e.getValue();
			applySubstitution(y,x, propagate);
		}
	}
	public void applySubstitution(C_Var y, C_Root x, boolean propagate) throws Failure {
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
		replace(q, p);
		if (q.term().equals(x)) {
			// Cannot replace x with x.  Instead,
			// introduce an EQV and substitute that for x.

			C_EQV v = genEQV(x.type(), false);

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
		if (p instanceof C_Lit) {
//			try {
				q.bind(p);
//			} catch (Failure f) {
//				throw new InternalCompilerError("Error in replacing " + x 
//						+ " with " + y + " in " + this + ": binding failure with "  + p);
//			}
			if (propagate) 
				propagate(y);
			return;
		}
		Promise xf = p.value();
		if (xf != null) {
			//addBinding(y, xf.term());
//			try {
				q.bind(xf);
//			} catch (Failure f) {
//				throw new InternalCompilerError("Error in replacing " + x 
//						+ " with " + y + " in " + this + ": binding failure with "  + xf);
//			}
		} else {
			
			HashMap<String,Promise> fields = p.fields(); 
			if (fields != null)
				for (Iterator<Entry<String,Promise>> it = fields.entrySet().iterator(); it.hasNext();) {
					Entry<String, Promise> entry = it.next();
					String s = entry.getKey();
					Promise orphan = entry.getValue();
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
			propagate(y);
		
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
	
	public void propagate(C_Var y) throws Failure {
		X10Type yType = (X10Type) y.type();
		if (yType == null) return;
		Constraint yTypeRC = yType.realClause();
		if (yTypeRC == null) return;
		yTypeRC = yTypeRC.clone();
		yTypeRC.setSelfVar(null);
		yTypeRC = yTypeRC.substitute(y,C_Special.Self, false);
		addIn(yTypeRC);
		
	}
	public void propagateRecursively(C_Var y) throws Failure {
		if (y instanceof C_Special && ((C_Special) y).kind() == C_Special.SELF) {
			return;
		}
		X10Type yType = (X10Type) y.type();
		if (yType == null) return;
		Constraint yTypeRC = yType.realClause();
		if (yTypeRC == null) return;
		yTypeRC = yTypeRC.clone();
		yTypeRC.setSelfVar(null);
		yTypeRC.saturate();
		yTypeRC = yTypeRC.substitute(y,C_Special.Self, false);
		addIn(yTypeRC);
	}
	public void saturate() throws Failure {
		if (roots != null) {
			final HashMap<C_Var,Promise> rootSnapshot = (HashMap<C_Var,Promise>) roots.clone();
			for (Iterator<C_Var> it = rootSnapshot.keySet().iterator(); it.hasNext();) {
				C_Var var = it.next();
				propagateRecursively(var);
			}
		}
	}
	
	public boolean entailsType(C_Var y) {
		assert(y != null);
		
		X10Type yType = (X10Type) y.type();
		Constraint yTypeRC = yType.realClause();
		
		HashMap<C_Var, C_Var> yTypeRCSubtermBindings = yTypeRC.constraints(C_Special.Self, y);
		boolean result = true;
		for (Iterator<Map.Entry<C_Var, C_Var>> 
		it2 = yTypeRCSubtermBindings.entrySet().iterator(); 
		result && it2.hasNext();) {
			Map.Entry<C_Var, C_Var> e2 = it2.next();
			C_Var yp = e2.getKey();
			C_Var t1 = e2.getValue();
			result = entails(yp, t1);
		}	
		
		//Report.report(1, "Constraint_c: " + this.toString() 
		//		+ (result ? " entails " : " does not entail ") + yType + " " + y);
		return result;
	}
	public Constraint instantiate(List<X10Type> list)  throws Failure {
		String thisString = this.toString();
		HashMap<C_Root, C_Var> subs = null;
		Set<C_Var> roots =  roots().keySet();
		for (Iterator<C_Var> it = roots.iterator(); it.hasNext(); ) {
			C_Var var = it.next();
			if (var instanceof C_Local) {
				C_Local local = (C_Local) var;
				X10LocalInstance li = (X10LocalInstance) local.localInstance();
				int p = li.positionInArgList();
				if (p > list.size())
					throw new InternalCompilerError("The argument index " + p +
							" in constraint " + this + " is out of bounds for "
							+ " argument types " + li + ".");
				if (p >= 0) {
					X10Type type = list.get(p);
					C_Var selfVar = type.selfVar();
					if (selfVar == null) {
						selfVar = genEQV(li.type(),true);
					}
					if (subs ==null) subs= new HashMap<C_Root,C_Var>();
					subs.put(local, selfVar);
				}
				
			}
		}
		Constraint result = this;
		if (subs != null && ! subs.isEmpty()) 
			result= this.substitute(subs, false);
		//Report.report(1, "Constraint_c: " + thisString + ".instantiate(" + list + ")==>" + result);
		return result;
	}
	public boolean hasVar(C_Root v) {
		if (roots == null) return false;
		return roots.keySet().contains(v);
	}
	 public C_Var selfVar(Expr arg) {
		 return selfVar(arg, true);
	 }
	 public C_Var selfVar(Expr arg, boolean hidden)  {
	    	C_Var result = null;
	    	if (rigid(arg)) {
	    		try {
	    		result = (C_Var) new TypeTranslator(typeSystem).trans(arg);
	    		} catch (SemanticException z) {
	    			throw new InternalCompilerError("Unexpected error " + z 
	    					+ " while trying to convert the rigid term " + arg 
	    					+ " to a C_Var.");
	    		}
	    		return result;
	    	}
	    	//result = (C_Root) type.selfVar();
	    	if (result == null) {
	    		result = genEQV(arg.type(),true, hidden);
	    	}
	    	return result;
	    }
	    public static boolean rigid(Receiver arg) {
	    	if (arg instanceof TypeNode) {
	    		return true;
	    	}
	    	if (arg instanceof Expr) {
	    		return rigid((Expr) arg);
	    	}
	    	assert false;
	    	return false;
	    }
	    public static boolean rigid(Expr arg) {
	    	boolean result = false;
	    	if (arg instanceof Field) {
	    		Field f = (Field) arg;
	    		return result = rigid(f.target()) && f.flags().isFinal();
	    	}
	    	if (arg instanceof Local) {
	    		return result = ((Local) arg).flags().isFinal();
	    	}
	    	if (arg instanceof Lit || arg instanceof X10Special || arg instanceof Here) {
	    		return result = true;
	    	}
	    
	    	return result;
	    }
		public void addSelfBinding(C_Var var) throws Failure {
			addBinding(selfVar(), var);
		}
		public Constraint addTerm(C_Term term) throws Failure {
			if (term instanceof C_Var) {
				return addTerm((C_Var) term);
			}
			return this;
		}
		public Constraint binaryOp(Operator op, Constraint other) {
			return new Constraint_c(typeSystem);
		}
		public void setInconsistent() {
			this.consistent = false;
		}
		public Constraint unaryOp(polyglot.ast.Unary.Operator op) {
			return new Constraint_c(typeSystem);
		}
}
