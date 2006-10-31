package polyglot.ext.x10.types.constr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;

/**
 * A representation of constraints of the form X1=t1 && ... Xk == tk.
 * Note that there is no unification, only checking. So it is possible
 * to represent such a constraint directly as a mapping from Xi to ti.
 * The constraint is implemented as a Map from variables to terms.
 * 
 * @author vj
 *
 */
public class Constraint_c implements Constraint {

	//Maps C_Var's to nodes.
	protected HashMap/*<C_Var,Promise>*/ roots;
	
	// Map from variables to values for negative bindings.
	// protected Map negBindings;
	
	// The place clause for this type.
	protected boolean placePossiblyNull; // true if loc could be null.
	protected boolean placeIsHere; // true if loc could be here.
	protected C_Term_c placeTerm;        // if non null, place could be here or some placeTerm.
	
	// For representation of T(:self == o), selfBinding is o.
	protected C_Var varWhoseTypeThisIs;
	
	boolean consistent = true;
	boolean valid = true;
	public Constraint_c() {
		super();
	}
	/** Copy this constraint. */
	
	public Constraint copy() {
		return copyInto(new Constraint_c());
	}
	/**
	 * Return the result of copying this into c. Assume that c will be 
	 * the depclause of the same base type as this, hence it is ok to 
	 * copy self-clauses as is. 
	 * @param c
	 * @return
	 */
	private Constraint copyInto(Constraint_c c) {
		c.addIn(this);
		c.placePossiblyNull = placePossiblyNull;
		c.placeIsHere = placeIsHere;
		c.placeTerm = placeTerm;
		// represent varWhoseTypeThisIs via a self==this constraint.
		return c;
	}
	public Constraint addIn(Constraint c) {
		HashMap result = c.constraints();
		addBindings(result);
		return this;
	}
	public Constraint addBindings(HashMap result) {
		if (result==null || result.isEmpty())
			return this;
		for (Iterator it = result.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			C_Term t1 = (C_Term) entry.getKey();
			C_Term t2 = (C_Term) entry.getValue();
			addBinding(t1,t2);
		}
		return this;
	}
	public static Constraint makeBinding(C_Var var, C_Term val)  {
		
		Constraint c = new Constraint_c();
		return c.addBinding(var,val);
		
	}
	public C_Var varWhoseTypeIsThis() {
		return varWhoseTypeThisIs;
	}
	/**
	 * Add the constraint self==val.
	 * @param val
	 * @param c
	 * @return
	 */
	public static Constraint addSelfBinding(C_Term val, Constraint c)  {
		c = (c==null) ? new Constraint_c() : c;
		if (val instanceof C_Var) {
			c.setVarWhoseTypeThisIs((C_Var) val);
			return c;
		}
		
		c =  c.addBinding(C_Special.Self, val);
		
		return c;
	}
	public void setVarWhoseTypeThisIs(C_Var var) {
		varWhoseTypeThisIs = var;
	}
	String name = "";

	public static final  transient X10TypeSystem typeSystem = X10TypeSystem_c.getTypeSystem();
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
	
	public Promise intern(C_Term term) {
		if (term instanceof Promise )
			// this is the case for literals, for here
			return (Promise) term;
		// otherwise it must be a C_Var.
		if (! (term instanceof C_Var))
			throw new InternalCompilerError("Cannot intern  term  " + term
					+ "; it must be a promise or a C_Var");
		C_Var var = (C_Var) term;
		C_Var[] vars = var.vars();
		
		C_Var baseVar = vars[0];
		if (roots == null) roots = new HashMap();
		Promise p = (Promise) roots.get(baseVar);
		if (p == null) {
			p = new Promise_c(baseVar);
			// Report.report(1, "Constraint_c.intern: adding " + var + " to roots for (:" + this+").");
			roots.put(baseVar, p);
		}
		return p.intern(vars, 1);
	}
	public Promise lookup(C_Term term) {
		if (term instanceof Promise )
			// this is the case for literals, for here
			return (Promise) term;
		// otherwise it must be a C_Var.
		if (! (term instanceof C_Var))
			throw new InternalCompilerError("Cannot lookup  term  " + term
					+ "; it must be a promise or a C_Var");
		C_Var var = (C_Var) term;
		C_Var[] vars = var.vars();
		
		C_Var baseVar = vars[0];
		if (roots ==null) return null;
		
		Promise p = (Promise) roots.get(baseVar);
		if (p == null) return null;
		return p.lookup(vars, 1);
	}
	
	/**
	 * Add t1=t2 to the constraint, unless it is inconsistent. 
	 * @param var -- t1
	 * @param val -- t2
	 */
	public Constraint addBinding(C_Term t1, C_Term t2)  {
		Constraint result = null;
	String oldString = this.toString();
		try { 
			
			if (!consistent ) return result=this; 
			if (roots == null) roots = new HashMap();
			if (t1==null) t1 = C_Lit.NULL;
			if (t2==null) t2 = C_Lit.NULL;
			if (varWhoseTypeThisIs !=null) {
				t1 = t1.substitute(C_Special_c.Self, varWhoseTypeThisIs);
				t2 = t2.substitute(C_Special_c.Self, varWhoseTypeThisIs);
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
			name += modified ? (name.equals("") ? "" : ", ") + t1 + "=" + t2 : "";
			valid &= ! modified;
			result=this;
		} catch (Failure z) {
			consistent=false;
			throw new InternalCompilerError("Adding binding " + t1 + "=" + t2 + " to " + this 
					+ " has made it inconsistent.");
		} finally {
			Report.report(1, "Constraint_c: Adding " + t1 + "=" + t2 + " to (:" + oldString +") yields (:" + result+").");
		}
		return result;
	}
	public Constraint addBindingPromise(C_Term t1, Promise p)  {
		try { 
			
			if (!consistent ) return this; 
			if (roots == null) roots = new HashMap();
			if (t1==null) t1 = C_Lit.NULL;
			if (varWhoseTypeThisIs !=null) {
				t1 = t1.substitute(C_Special_c.Self, varWhoseTypeThisIs);
			}
			Promise p1 = intern(t1);
			boolean modified = p1.bind(p);
			
		} catch (Failure z) {
			
			consistent=false;
			throw new InternalCompilerError("Adding binding " + t1 + "=" + p + " to " + this 
					+ " has made it inconsistent.");
		}
		return this;
	}

	private void possiblyAddTypeConstraint(C_Var t1, Promise target) {
		//Report.report(1, "Constraint_c.possiblyAddTypeConstraint " + t1 + " " + target);
		// Check if t1's type forces t1 to be equal to something (t3). If so, add
		// t2=t3 in there.
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
					C_Term cVar = xd.varWhoseTypeIsThis();
					if (t1Root.equals(cVar)) {
						t1 = (C_Var) t1.substitute(C_Special_c.Self, cVar);
					}
				}
				Promise p = c.lookup(t1);
				if (p != null) {
					// aha there really is a term that t1's roottype binds t1 to!
					C_Term t3 = p.term();
					if (t3 instanceof C_Var && ((C_Var) t3).rootVar().equals(C_Special.Self)) {
						if (xd != null) {
							C_Var sVar = xd.varWhoseTypeIsThis();
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
	public Constraint addTerm(C_Term term) throws Failure {
		C_Lit val = C_Lit.TRUE;
		if (term instanceof C_UnaryTerm) {
			C_UnaryTerm t = (C_UnaryTerm) term;
			String op = t.op();
			if (op.equals("!")) {
				return addBinding(t.arg(), val.not());
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
	public HashMap/*<C_Term, C_Term>*/ constraints() {
		return constraints(new HashMap());
	}
	public HashMap/*<C_Term, C_Term>*/ constraints(HashMap result) {
		return constraints(result, null, null);
	}
	
	public HashMap/*<C_Term, C_Term>*/ constraints(HashMap result, C_Term newSelf, C_Term newThis) {
		if (roots==null) return result;
		for (Iterator it = roots.values().iterator(); it.hasNext();) {
			Promise p = (Promise) it.next();
			if (newSelf==null && newThis==null)
				// no point in propagating the fields through if they wont be used.
				p.dump(result);
			else 
				p.dump(result, newSelf, newThis);
		}
		// Report.report(1, "Constraint_c: The constraints associated with (:" + this + ") are " + result + ".");
		return result;
	}
	public boolean entailedBy(Constraint other) {
		if ((! other.consistent()) || valid() )
			return true;
		assert (roots !=null);
		HashMap result = constraints();
		for (Iterator it = result.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (! other.entails((C_Var) entry.getKey(), (C_Term) entry.getValue()))
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

	public boolean entails(C_Term t1, C_Term t2) {
		
		boolean result = false;
		try {
			
			if (! consistent) return result = true;
			if (varWhoseTypeThisIs !=null) {
				t1 = t1.substitute(C_Special_c.Self, varWhoseTypeThisIs);
				t2 = t2.substitute(C_Special_c.Self, varWhoseTypeThisIs);
			}
			Promise p1 = lookup(t1);
			if (p1 == null) return result=false;
			
			int r1Count=0;
			C_Var[] vars1 = null;
			if (p1 instanceof Promise_c ){
				r1Count = ((Promise_c) p1).lookupReturnValue();
				vars1 = ((C_Var) t1).vars();
			}
			Promise p2 = lookup(t2);
			int r2Count=0;
			C_Var[] vars2 = null;
			if (p1 instanceof Promise_c ){
				r1Count = ((Promise_c) p1).lookupReturnValue();
				vars1 = ((C_Var) t1).vars();
			}
			if (p1 == null || p2==null) {
				//one of these terms does not have even a partial match in the constraint.
				return result=false;
			}
			if ((r1Count == 0 || r1Count == vars1.length-1) && (r2Count==0 || r2Count == vars2.length-1)) {
				// exact lookups
				return result=p1.equals(p2);
			} 
			// at least one of them had a suffix left over
			
			if (! p1.equals(p2)) return false;
			//	Now ensure that they have the same suffix left over. 
			int residual1 = vars1.length-r1Count-1, residual2=vars2.length-r2Count-1;
			if (residual1 != residual2) return false;
			for (int i=1; i < residual1; i++) {
				if (! vars1[residual1+i].name().equals(vars2[residual2+i].name()))
					return result=false;
			}
			return result=true;
	} finally {
		Report.report(1, "Constraint_c: (:" + this + (result? ") entails " : ") does not entail ") 
				+ t1 + "=" + t2+ ".");
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
		if (varWhoseTypeThisIs == null) return false;
		C_Var var1 = (C_Var) var.substitute(varWhoseTypeThisIs, C_Special.Self);
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
	
	 public C_Term find(String varName) {
		 if ((! consistent) || roots ==null) return null;
		// Report.report(1, "Constraint_c.find: roots are " + roots);
		 Promise self = (Promise) roots.get(C_Special_c.Self);
		 Promise result = self.lookup(varName);
		 return result==null ? null : result.term();
	 }
	
	
	public String toString() { return   name ;}

}
