package polyglot.ext.x10.types.constr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;

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

	// Map from variables to values for positive bindings.
	protected Map bindings = new HashMap();
	
	// Map from variables to values for negative bindings.
	// protected Map negBindings;
	
	// The place clause for this type.
	protected boolean placePossiblyNull; // true if loc could be null.
	protected boolean placeIsHere; // true if loc could be here.
	protected C_Term_c placeTerm;        // if non null, place could be here or some placeTerm.
	
	// For representation of T(:self == o), selfBinding is o.
	protected C_Term varWhoseTypeThisIs;
	
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
		
		Set keys = bindings.entrySet();
		for (Iterator it = keys.iterator(); it.hasNext();) {
			Map.Entry i = (Map.Entry) it.next();
			c.addBinding((C_Var) i.getKey(), (C_Term) i.getValue());
		
		}
		c.placePossiblyNull = placePossiblyNull;
		c.placeIsHere = placeIsHere;
		c.placeTerm = placeTerm;
		// represent varWhoseTypeThisIs via a self==this constraint.
	
		
		return c;
	}
	public static Constraint makeBinding(C_Var var, C_Term val) {
		Constraint c = new Constraint_c();
		return c.addBinding(var,val);
	}
	public C_Term varWhoseTypeIsThis() {
		return varWhoseTypeThisIs;
	}
	public static Constraint addVarWhoseTypeThisIs(C_Term val, Constraint c) {
		c = (c==null) ? new Constraint_c() : c;
		((Constraint_c) c).varWhoseTypeThisIs = val;
		return c;
	}
	public void setVarWhoseTypeThisIs(C_Term val) {
		varWhoseTypeThisIs = val;
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
	public Map bindings() { return bindings; }
	public boolean isLocal() { return placePossiblyNull || placeIsHere; }
	public boolean isPossiblyRemote() { return ! isLocal();}
	
	/**
	 * Add X=t to the constraint, unless it is inconsistent. 
	 * If there is already a binding X=s, check that s.equals(t), otherwise the constraint
	 * is inconsistent.
	 * Else, check that t is not equal to X before adding it.
	 * @param var
	 * @param val
	 */
	public Constraint addBinding(C_Var var, C_Term val) {
		if (!consistent ) return this; 
		if (bindings == null) bindings = new HashMap();
		C_Term prev = (C_Term) bindings.get(var);
		if (prev != null) {
			consistent &= prev.equals(val);
			return this;
		}
		if (! var.equals(val)) {
			// New information has been added.
			bindings.put(var, val);
			name = name + (name.equals("") ? "" : ",") + var + "=" + val;
			valid = false;
		}
		return this;
	}

	/**
	 * Add a boolean term.
	 * @param term
	 */
	public Constraint addTerm(C_Term term) throws SemanticException {
		return addTerm(term, C_Lit.TRUE);
	}
	public Constraint addTerm(C_Term term, C_Lit val) throws SemanticException {
		if (term instanceof C_Lit) {
			consistent &= ((C_Lit) term).val().equals(val);
			return this;
		}
		if (term instanceof C_Var) {
			addBinding((C_Var) term, val);
			return this;
		}
		if (term instanceof C_UnaryTerm) {
			C_UnaryTerm t = (C_UnaryTerm) term;
			String op = t.op();
			if (op.equals("!")) {
				return addTerm(t.arg(), val.not());
			}
		}
		throw new SemanticException("Cannot add term |" + term
				+ "| to constraint. It must be a literal or a variable, or ! of a literal or variable.");
	}
	/**
	 * If other is not inconsistent, and this is consistent,
	 * checks that each binding X=t in other also exists in this.
	 * @param other
	 * @return
	 */
	public boolean entails(Constraint other) {
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
			C_Term val2 = (C_Term) bindings.get(var);
			//Report.report(1, "Constraint.entails: |" + val + "|" + val2 + "|" + val.equals(val2));
			result = val==val2 || val.equals(val2);
			if ((!result) && (var instanceof C_Special)) {
//				 check the selfbinding
				C_Special s = (C_Special) var;
				if (s.kind().equals(C_Special.SELF)) {
					result = (val==varWhoseTypeThisIs || val.equals(varWhoseTypeThisIs));
				}
			}
		}
	//Report.report(1, "Constraint: " + this + " entails " + other + "? " + result);
		return result;
	}

	public boolean equiv(Constraint other) {
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " );
		boolean result = entails(other);
		if (result) result = (other==null)? valid : other.entails(this);
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " + result);
		return result;
	}
	
	public boolean entails(C_Var var, C_Term val) {
		if (! consistent) return true;
		boolean result = val.equals(bindings.get(var));
		return result;
	}
	public C_Term find(String varName) {
		if ((! consistent) || bindings ==null) return null;
		Set keys = bindings().entrySet();
		//Report.report(1, "Constraint: set is |" + keys + "|");
		for (Iterator it = keys.iterator(); it.hasNext();) {
			Map.Entry i = (Map.Entry) it.next();
			C_Var var = (C_Var) i.getKey();
			if (var.name().equals(varName))
				return (C_Term) i.getValue();
		}
		return null;
		
	}
	public String toString() { return  name;}

}
