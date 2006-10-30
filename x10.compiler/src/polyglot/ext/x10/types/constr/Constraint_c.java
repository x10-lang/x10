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
	
	public Constraint_c copy() {
		return copyInto(new Constraint_c());
	}
	/**
	 * Return the result of copying this into c.
	 * @param c
	 * @return
	 */
	public Constraint_c copyInto(Constraint_c c) {
		HashMap result = constraints();
		for (Iterator it = result.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			C_Term t1 = (C_Term) entry.getKey();
			C_Term t2 = (C_Term) entry.getValue();
			
			c.addBinding(t1,t2);
			
		}
		
		c.placePossiblyNull = placePossiblyNull;
		c.placeIsHere = placeIsHere;
		c.placeTerm = placeTerm;
		// represent varWhoseTypeThisIs via a self==this constraint.
		return c;
	}
	public static Constraint makeBinding(C_Var var, C_Term val)  {
		try {
		Constraint c = new Constraint_c();
		return c.addBinding(var,val);
		} catch (Failure z) {
			throw new InternalCompilerError("Unexpected failure when adding " + var 
					+ "->" + val + " to an empty constraint.");
		}
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
		try {
		c =  c.addBinding(C_Special.self, val);
		} catch (Failure z) {
			throw new InternalCompilerError("Caught failure " + z + 
					" when adding self-binding " + val + " to " + c+".");
		}
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
	
	/**
	 * Add t1=t2 to the constraint, unless it is inconsistent. 
	 * @param var -- t1
	 * @param val -- t2
	 */
	public Constraint addBinding(C_Term t1, C_Term t2)  {
		try { 
			Report.report(1, "Adding " + t1 + "->" + t2 + " to " + this +".");
			
			if (!consistent ) return this; 
			if (roots == null) roots = new HashMap();
			if (t1==null)
				t1 = C_Lit.NULL;
			if (t2==null)
				t2 = C_Lit.NULL;
			if (varWhoseTypeThisIs !=null) {
				t1 = t1.substitute(C_Special_c.self, varWhoseTypeThisIs);
				t2 = t2.substitute(C_Special_c.self, varWhoseTypeThisIs);
			}
			Promise p1 = intern(t1);
			Promise p2 = intern(t2);
			boolean modified = p1.bind(p2);
			name += modified ? (name.equals("") ? "" : ", ") + t1 + "=" + t2 : "";
			valid &= ! modified;
			
		} catch (Failure z) {
			Report.report(1, "Adding binding " + t1 + "=" + t2 + " to " + this 
					+ " has made it inconsistent.");
			consistent=false;
		}
		return this;
	}

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
		HashMap result = new HashMap();
		if (roots==null) return result;
		for (Iterator it = roots.values().iterator(); it.hasNext();) {
			Promise p = (Promise) it.next();
			p.dump(result);
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
		if (! consistent) return true;
		if (varWhoseTypeThisIs !=null) {
			t1 = t1.substitute(C_Special_c.self, varWhoseTypeThisIs);
			t2 = t2.substitute(C_Special_c.self, varWhoseTypeThisIs);
		}
		Promise p1 = intern(t1);
		Promise p2 = intern(t2);
		boolean result = p1.equals(p2);
		if ( Report.should_report(Report.types,1))
			Report.report(1, this + (result? " entails " : " does not entail ") + t1 + "=" + t2+".");
		return result;
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
//			 check the selfbinding
			C_Special s = (C_Special) var;
			if (s.kind().equals(C_Special.SELF)) {
				result = (val==varWhoseTypeThisIs || val.equals(varWhoseTypeThisIs));
			}
			
		}*/
		
	}
	
	protected boolean checkSelfEntails(C_Var var, C_Term val) {
		if (varWhoseTypeThisIs == null) return false;
		C_Var var1 = (C_Var) var.substitute(varWhoseTypeThisIs, C_Special.self);
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
		 Promise self = (Promise) roots.get(C_Special_c.self);
		 Promise result = self.lookup(varName);
		 return result==null ? null : result.term();
	 }
	
	
	public String toString() { return  "#" +hashCode() + " " +  name ;}

}
