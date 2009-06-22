/*
 *
 * (C) Copyright IBM Corporation 2006-2008
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * An implementation of a Promise. The nodes in a graph maintained by a
 * constraint are either Promise_c's or other implementations of Promise, such
 * as C_Lit and C_Here.
 * 
 * @author vj
 * 
 */
public class XPromise_c implements XPromise, Serializable {
	/**
	 * The externally visible C_Term that this node represents in the constraint
	 * graph. May be null, if this promise corresponds to an internal node.
	 */
	protected XTerm var;

	/**
	 * This node may have been equated to another node, n. If so, value contains
	 * the reference to n. Can be null.
	 */
	protected XPromise value;

	/**
	 * fields captures constraints on fields of this variable, if any.
	 * Invariant: if value is not null, then fields must be null, the
	 * constraints are translated to the target of the value.
	 * 
	 * We represent composite terms (e.g., binary operators, unary operators,
	 * calls) as promises with fields representing the operator and the
	 * operands.
	 */
	protected HashMap<XName, XPromise> fields;

	public XPromise_c(XTerm c) {
		super();
		value = null;
		var = c;
	}

	public XPromise_c(Map<XName, XPromise> fields) {
		this(fields, null);
	}

	public XPromise_c(Map<XName, XPromise> fields, XTerm term) {
		this(term, null, fields);
	}

	public XPromise_c(XTerm var, XPromise value, Map<XName, XPromise> fields) {
		this.var = var;
		this.value = value;
		if (fields != null)
			this.fields = new HashMap<XName, XPromise>(fields);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see polyglot.ext.x10.types.constr.Promise#term()
	 */
	public XTerm term() {
		return var;
	}

	public void setTerm(XTerm term) {
	    HashSet<XPromise> visited = new HashSet<XPromise>();
	    visited.add(this);
	    setTerm(term, visited);
	}
	
	public void setTerm(XTerm term, Set<XPromise> visited) {
		var = term;
		if (var != null && fields != null) {
			for (Map.Entry<XName, XPromise> entry : fields.entrySet()) {
				XName key = entry.getKey();
				XPromise p = entry.getValue();
				if (visited.contains(p))
				    continue;
				visited.add(p);
				if (p.term() instanceof XField) {
				    XField f = (XField) p.term();
				    XName field = f.field();
				    if (field.equals(key))
					p.setTerm(XConstraint_c.makeField(term, field), visited);
				    else
					System.out.println(term + "." + key + " = " + p + " (different field)");
				}
				else {
				    System.out.println(term + "." + key + " = " + p + " (not a field)");
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

	public void setVar(XVar v) {
		var = v;
	}

	protected XPromise_c clone() {
		XPromise_c result = null;
		try {
			result = (XPromise_c) super.clone();
		}
		catch (CloneNotSupportedException z) {
			// But it is!
		}
		return result;
	}

	public XPromise cloneRecursively(HashMap<XPromise, XPromise> env) {
		XPromise q = env.get(this);
		if (q != null)
			return q;
		XPromise_c clone = clone();
		env.put(this, clone);
		if (this.value != null) {
			XPromise valueClone = value.cloneRecursively(env);
			clone.value = valueClone;
		}
		if (this.fields != null) {
			HashMap<XName, XPromise> cloneFields = new HashMap<XName, XPromise>();
			for (Iterator<Map.Entry<XName, XPromise>> it = fields.entrySet().iterator(); it.hasNext();) {
				Map.Entry<XName, XPromise> entry = it.next();
				XName key = entry.getKey();
				XPromise p = entry.getValue();
				XPromise cloneP = p.cloneRecursively(env);
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

	public XPromise lookup(XVar[] vars, int index) throws XFailure {
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
		assert vars[index] instanceof XField;
		XField f = (XField) vars[index];
		XName field = f.field();
		// check this edge already exists.
		XPromise p = fields.get(field);
		if (p == null) {
			lookupReturnValue = index;
			return this;
		}
		return p.lookup(vars, index + 1);
	}

	public XPromise lookup() throws XFailure {
		if (value != null)
			return value.lookup();
		return this;
	}

	public XPromise lookup(XName s) throws XFailure {
		// follow the eq link if there is one.
		if (value != null)
			return value.lookup(s);
		if (fields == null)
			return null;
		// check this edge already exists.
		XPromise p = fields.get(s);
		return p == null ? null : p.lookup();
	}

	public XPromise intern(XVar[] vars, int index) throws XFailure {
		return intern(vars, index, null);
	}

	public XPromise intern(XVar[] vars, int index, XPromise last) throws XFailure {
		assert index >= 1;

		// follow the eq link if there is one.
		if (value != null)
			return value.intern(vars, index, last);
		if (index == vars.length)
			return this;
		// if not, we need to add this path here. Ensure fields is initialized.
		if (fields == null)
			fields = new HashMap<XName, XPromise>();
		assert vars[index] instanceof XField;
		XField f = (XField) vars[index];
		XName s = f.field();
		// check this edge already exists.
		XPromise p = (XPromise) fields.get(s);
		if (p == null) {
			// no edge. Create a new promise and add this edge.
			p = (index == vars.length - 1 && last != null) ? last : new XPromise_c(f);
			fields.put(s, p);
		}
		// recursively, intern the rest of the path on the child.
		return p.intern(vars, index + 1, last);
	}

	public void addIn(XName s, XPromise orphan) throws XFailure {
		if (value != null) {
			// Alternative is to fwd it blindly, that would be correct, but i
			// want to know
			// if this is happening. It should not happen.
			throw new XFailure("The node " + this + " is forwarded to " + value + "; the " + s + " child, " + orphan + ", cannot be added to it.");
		}

		if (fields == null)
			fields = new HashMap<XName, XPromise>();
		XPromise child = (XPromise) fields.get(s);
		if (child != null) {
			orphan.bind(child);
			return;
		}
		fields.put(s, orphan);
	}

	public boolean bind(/* @nonnull */XPromise target) throws XFailure {
		if (!target.equals(value) && !target.equals(var)) {
			if (forwarded())
				throw new XFailure("The promise " + this + " is already bound to " + value + "; cannot bind it to " + target + ".");
			if (this == target) // nothing to do!
				return false;

			// Check for cycles!
			if (canReach(target) || target.canReach(this))
				throw new XFailure("Binding " + this + " to " + target + " creates a cycle.");
			if (!term().prefersBeingBound() && target.term().prefersBeingBound()) {
				return target.bind(this);
			}
			value = target;
		}
		if (fields != null) {
			for (Map.Entry<XName, XPromise> i : fields.entrySet()) {
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
	public boolean canReach(XPromise p) {
		if (p == this)
			return true;
		if (value != null)
			return value.canReach(p);
		if (fields != null)
			for (Iterator it = fields.values().iterator(); it.hasNext();) {
				XPromise q = (XPromise) it.next();
				if (q.canReach(p))
					return true;
			}
		return false;
	}

	public void dump(List<XTerm> result, XTerm prefix) {
		XTerm t1 = term();
		if (t1 == null)
			return;
		if (t1.isAtomicFormula()) {
			if (prefix != null && !(prefix.prefixes(t1)))
				return;
			result.add(t1);
		}

		if (value != null) {
			XTerm t2 = value.term();
			if (prefix != null && !(prefix.prefixes(t1) || prefix.prefixes(t2)))
				return;
			// Report.report(1, "Promise_c: dumping " + t1 + "=" + t2);
			result.add(XTerms.makeEquals(t1, t2));
			return;
		}
		if (fields != null)
			for (XPromise p : fields.values()) {
				p.dump(result, prefix);
			}
	}

	public String toString() {
		return var + ((value != null) ? "->" + value : ((fields != null) ? fields.toString() : ""));
	}

	public void replaceDescendant(XPromise y, XPromise x, XConstraint c) {
		if (value != null) {
			if (value.equals(x)) {
			        if (this.equals(y)) {
			            // don't create a self-cycle; it's redundant!
			            value = null;
			        }
			        else {
			            value = y;
			        }
			}
			else {
				value.replaceDescendant(y, x, c);
			}
		}
		
		if (fields != null) {
		    for (Map.Entry<XName, XPromise> p : fields.entrySet()) {
			XName key = p.getKey();
			XPromise val = p.getValue();
			// doing this / self, and val == self.location -> this.location
			// add this.location / self.location to the replacements to perform
			if (val.equals(x)) {
			    p.setValue(y);
			}
			else {
			    val.replaceDescendant(y, x, c);
			}
		    }
		}
	}

	public XPromise value() {
		return value;
	}

	public HashMap<XName, XPromise> fields() {
		return fields;
	}
}
