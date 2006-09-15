package polyglot.ext.x10.types.constr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

	private Map<C_Var,C_Term> bindings;
	
	boolean consistent = true;
	boolean valid = true;
	public Constraint_c() {
		super();
	}
	String name = "";
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
		if (bindings == null) bindings = new HashMap<C_Var,C_Term>();
		C_Term prev = (C_Term) bindings.get(var);
		if (prev != null) {
			consistent &= prev.equals(val);
			return this;
		}
		if (! var.equals(val)) {
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
		Set<Map.Entry<C_Var, C_Term>> keys = other.bindings().entrySet();
		//Report.report(1, "Constraint: set is |" + keys + "|");
		boolean result = true;
		for (Iterator<Map.Entry<C_Var,C_Term>> it = keys.iterator(); result && it.hasNext();) {
			Map.Entry<C_Var,C_Term> i = it.next();
			C_Term val = i.getValue();
			C_Var var = i.getKey();
			C_Term val2 = bindings.get(var);
			//Report.report(1, "Constraint.entails: |" + val + "|" + val2 + "|" + val.equals(val2));
			result &=val.equals(val2);
		}
		//Report.report(1, "Constraint: " + this + " entails " + other + "? " + result);
		return result;
	}

	public boolean equiv(Constraint other) {
		boolean result = entails(other);
		if (result) result = (other==null)? valid : other.entails(this);
		return result;
	}
	
	public boolean entails(C_Var var, C_Term val) {
		if (! consistent) return true;
		boolean result = val.equals(bindings.get(var));
		return result;
	}
	public C_Term find(String varName) {
		if ((! consistent) || bindings ==null) return null;
		Set<Map.Entry<C_Var, C_Term>> keys = bindings().entrySet();
		//Report.report(1, "Constraint: set is |" + keys + "|");
		for (Iterator<Map.Entry<C_Var,C_Term>> it = keys.iterator(); it.hasNext();) {
			Map.Entry<C_Var,C_Term> i = it.next();
			C_Var var = i.getKey();
			if (var.name().equals(varName))
				return i.getValue();
		}
		return null;
		
	}
	public String toString() { return  name;}

}
