/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package x10.constraint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An implementation of a Promise. The nodes in a graph maintained by a
 * constraint are either Promise_c's or other implementations of Promise, such
 * as C_Lit and C_Here.
 * 
 * @author vj
 * 
 */
public class Promise_c implements Promise, Serializable {
	/**
	 * The externally visible C_Term that this node represents in the constraint
	 * graph. May be null, if this promise corresponds to an internal node.
	 */
	protected C_Term var;

	/**
	 * This node may have been equated to another node, n. If so, value contains
	 * the reference to n. Can be null.
	 */
	protected Promise value;

	/**
	 * fields captures constraints on fields of this variable, if any.
	 * Invariant: if value is not null, then fields must be null, the
	 * constraints are translated to the target of the value.
	 * 
	 * We represent composite terms (e.g., binary operators, unary operators,
	 * calls) as promises with fields representing the operator and the
	 * operands.
	 */
	protected HashMap<C_Name, Promise> fields;

	public Promise_c(C_Term c) {
		super();
		value = null;
		var = c;
	}

	public Promise_c(Map<C_Name, Promise> fields) {
		this(fields, null);
	}

	public Promise_c(Map<C_Name, Promise> fields, C_Term term) {
		this(term, null, fields);
	}

	public Promise_c(C_Term var, Promise value, Map<C_Name, Promise> fields) {
		this.var = var;
		this.value = value;
		if (fields != null)
			this.fields = new HashMap<C_Name, Promise>(fields);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see polyglot.ext.x10.types.constr.Promise#term()
	 */
	public C_Term term() {
		return var;
	}

	public void setTerm(C_Var term) {
		var = term;
		if (var != null && fields != null) {
			for (Map.Entry<C_Name, Promise> entry : fields.entrySet()) {
				C_Name key = entry.getKey();
				Promise p = entry.getValue();
				if (p.term() != null) {
					C_Name field = ((C_Field) p.term()).field();
					p.setTerm(new C_Field_c(term, field));
				}
			}
		}
	}

	public boolean forwarded() {
		return value != null;
	}

	public boolean hasChildren() {
		return fields != null;
	}

	public void setVar(C_Var v) {
		var = v;
	}

	protected Promise_c clone() {
		Promise_c result = null;
		try {
			result = (Promise_c) super.clone();
		}
		catch (CloneNotSupportedException z) {
			// But it is!
		}
		return result;
	}

	public Promise cloneRecursively(HashMap<Promise, Promise> env) {
		Promise q = env.get(this);
		if (q != null)
			return q;
		Promise_c clone = clone();
		env.put(this, clone);
		if (this.value != null) {
			Promise valueClone = value.cloneRecursively(env);
			clone.value = valueClone;
		}
		if (this.fields != null) {
			HashMap<C_Name, Promise> cloneFields = new HashMap<C_Name, Promise>();
			for (Iterator<Map.Entry<C_Name, Promise>> it = fields.entrySet().iterator(); it.hasNext();) {
				Map.Entry<C_Name, Promise> entry = it.next();
				C_Name key = entry.getKey();
				Promise p = entry.getValue();
				Promise cloneP = p.cloneRecursively(env);
				cloneFields.put(key, cloneP);
			}
			clone.fields = cloneFields;
		}
		return clone;
	}

	int lookupReturnValue;

	public int lookupReturnValue() {
		return lookupReturnValue;
	}

	public Promise lookup(C_Var[] vars, int index) throws Failure {
		assert index >= 1;

		// follow the eq link if there is one.
		if (value != null)
			return value.lookup(vars, index);
		if (index == vars.length) {
			lookupReturnValue = index;
			return this;
		}
		if (fields == null) {
			lookupReturnValue = index;
			return this;
		}
		assert vars[index] instanceof C_Field;
		C_Field f = (C_Field) vars[index];
		C_Name field = f.field();
		// check this edge already exists.
		Promise p = fields.get(field);
		if (p == null) {
			lookupReturnValue = index;
			return this;
		}
		return p.lookup(vars, index + 1);
	}

	public Promise lookup() throws Failure {
		if (value != null)
			return value.lookup();
		return this;
	}

	public Promise lookup(C_Name s) throws Failure {
		// follow the eq link if there is one.
		if (value != null)
			return value.lookup(s);
		if (fields == null)
			return null;
		// check this edge already exists.
		Promise p = fields.get(s);
		return p == null ? null : p.lookup();
	}

	public Promise intern(C_Var[] vars, int index) throws Failure {
		return intern(vars, index, null);
	}

	public Promise intern(C_Var[] vars, int index, Promise last) throws Failure {
		assert index >= 1;

		// follow the eq link if there is one.
		if (value != null)
			return value.intern(vars, index, last);
		if (index == vars.length)
			return this;
		// if not, we need to add this path here. Ensure fields is initialized.
		if (fields == null)
			fields = new HashMap<C_Name, Promise>();
		assert vars[index] instanceof C_Field;
		C_Field f = (C_Field) vars[index];
		C_Name s = f.field();
		// check this edge already exists.
		Promise p = (Promise) fields.get(s);
		if (p == null) {
			// no edge. Create a new promise and add this edge.
			p = (index == vars.length - 1 && last != null) ? last : new Promise_c(f);
			fields.put(s, p);
		}
		// recursively, intern the rest of the path on the child.
		return p.intern(vars, index + 1, last);
	}

	public void addIn(C_Name s, Promise orphan) throws Failure {
		if (value != null) {
			// Alternative is to fwd it blindly, that would be correct, but i
			// want to know
			// if this is happening. It should not happen.
			throw new Failure("The node " + this + " is forwarded to " + value + "; the " + s + " child, " + orphan + ", cannot be added to it.");
		}

		if (fields == null)
			fields = new HashMap<C_Name, Promise>();
		Promise child = (Promise) fields.get(s);
		if (child != null) {
			orphan.bind(child);
			return;
		}
		fields.put(s, orphan);
	}

	public boolean bind(/* @nonnull */Promise target) throws Failure {
		if (!target.equals(value)) {
			if (forwarded())
				throw new Failure("The promise " + this + " is already bound to " + value + "; cannot bind it to " + target + ".");
			if (this == target) // nothing to do!
				return false;

			// Check for cycles!
			if (canReach(target) || target.canReach(this))
				throw new Failure("Binding " + this + " to " + target + " creates a cycle.");
			if (!term().prefersBeingBound() && target.term().prefersBeingBound()) {
				return target.bind(this);
			}
			value = target;
		}
		if (fields != null) {
			for (Map.Entry<C_Name, Promise> i : fields.entrySet()) {
				target.addIn(i.getKey(), i.getValue());
			}
			fields = null;
		}
		return true;
	}

	/**
	 * Can this promise reach p in the directed graph representing the
	 * constraints?
	 */
	public boolean canReach(Promise p) {
		if (p == this)
			return true;
		if (value != null)
			return value.canReach(p);
		if (fields != null)
			for (Iterator it = fields.values().iterator(); it.hasNext();) {
				Promise q = (Promise) it.next();
				if (q.canReach(p))
					return true;
			}
		return false;
	}

	public void dump(List<C_Term> result, C_Term prefix) {
		dump(result, prefix, null);
	}

	public void dump(List<C_Term> result, C_Term prefix, C_Var newSelf) {
		C_Term t1 = term();
		if (t1 == null || t1.isEQV())
			return;
		if (t1.isAtomicFormula()) {
			if (prefix != null && !(prefix.prefixes(t1)))
				return;
			if (newSelf != null)
				t1 = t1.substitute(newSelf, C_Self.Self);
			result.add(t1);
		}

		if (value != null) {
			C_Term t2 = value.term();
			if (prefix != null && !(prefix.prefixes(t1) || prefix.prefixes(t2)))
				return;
			if (newSelf != null) {
				t1 = t1.substitute(newSelf, C_Self.Self);
			}
			// Report.report(1, "Promise_c: dumping " + t1 + "=" + t2);
			result.add(C_Terms.makeEquals(t1, t2));
			return;
		}
		if (fields != null)
			for (Promise p : fields.values()) {
				p.dump(result, prefix, newSelf);
			}
	}

	public String toString() {
		return var + ((value != null) ? "->" + value : ((fields != null) ? fields.toString() : ""));
	}

	public void replaceDescendant(Promise y, Promise x) {
		// var = var.substitute(y.term(), x.term());
		if (value != null) {
			if (value.equals(x)) {
				value = y;
			}
			else {
				value.replaceDescendant(y, x);
			}
		}
		if (fields != null)
			for (Iterator<Entry<C_Name, Promise>> it = fields.entrySet().iterator(); it.hasNext();) {
				Entry<C_Name, Promise> p = it.next();
				C_Name key = p.getKey();
				Promise value = p.getValue();
				if (value.equals(x)) {
					p.setValue(y);
				}
				else {
					value.replaceDescendant(y, x);
				}
			}
	}

	public Promise value() {
		return value;
	}

	public HashMap<C_Name, Promise> fields() {
		return fields;
	}
}
