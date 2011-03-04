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
package polyglot.ext.x10.types.constr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.util.InternalCompilerError;

/**
 * An implementation of a Promise. The nodes in a graph maintained by a constraint
 * are either Promise_c's or other implementations of Promise, such as 
 * C_LIt and C_Here. 
 * @author vj
 *
 */
public class Promise_c implements Promise, Serializable {

	/**
	 * The externally visible C_Var that this node represents in the constraint graph.
	 * May be null, if this promise corresponds to an internal node.
	 */
	protected  C_Var var;
	
	/**
	 * This node may have been equated to another node, n. If so, value contains
	 * the reference to n. Can be null.
	 */
	protected Promise value;
	
	/** fields captures constraints on fields of this variable, if any.
	 * Invariant: if value is not null, then fields must be null,
	 * the constraints are translated to the target of the value.
	 * 
	 */
	
	protected HashMap<String, Promise> fields;
	
	public Promise_c(C_Var c) {
		super();
		value = null;
		var = c;
		
	}
	
	public Promise_c(C_Var var, Promise value, Map<String,Promise> fields) {
		this.var = var;
		this.value = value;
		this.fields = new HashMap<String,Promise>(fields);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see polyglot.ext.x10.types.constr.Promise#term()
	 */
	public C_Var term() {
		
		return var;
	}
	public void setTerm(C_Var term) {
		var = term;
		/*if (value != null) {
			value.setTerm(term);
			return;
		}*/
		if (var != null && fields != null) 
			for (Iterator<Map.Entry<String,Promise>> it = fields.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String,Promise> entry = it.next();
				String key = entry.getKey();
				Promise p = entry.getValue();
				assert p != this;
				FieldInstance f = ((C_Field) p.term()).fieldInstance();
				p.setTerm(new C_Field_c(f, term));
			}
	}
	public boolean forwarded() {
		return value != null;
	}
	public boolean hasChildren() {
		return fields !=null;
	}
	public void setVar(C_Var v) {
		var =v;
	}
	protected Promise_c clone() {
		Promise_c result = null;
		try {
			result = (Promise_c) super.clone();
		} catch (CloneNotSupportedException z) {
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
		if ( this.fields != null) {
			HashMap<String, Promise> cloneFields = new HashMap<String, Promise>();
			for (Iterator<Map.Entry<String,Promise>> it = fields.entrySet().iterator(); 
			it.hasNext();) {
				Map.Entry<String,Promise> entry = it.next();
				String key = entry.getKey();
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
	public Promise lookup( C_Var[] vars, int index) {
		// follow the eq link if there is one.
		if (value != null) return value.lookup(vars, index);
		if (index==vars.length) {
			lookupReturnValue = index;
			return this;
		}
		if (fields == null) {
			lookupReturnValue = index;
			return this;
		}
		String s = vars[index].name();
		// check this edge already exists.
		Promise p =  fields.get(s);
		if (p == null) {
			lookupReturnValue = index;
			return this;
		}
		return p.lookup(vars, index+1);
	}
	public Promise lookup() {
		if (value !=null)
			return value.lookup();
		return this;
	}
	public Promise lookup(String s) {
		// follow the eq link if there is one.
		if (value != null) return value.lookup(s);
		if (fields == null) return null;
		// check this edge already exists.
		Promise p = fields.get(s);
		return p == null ? null : p.lookup();
	}
	public Promise intern( C_Var[] vars, int index) {
		return intern(vars, index, null);
	}
	public Promise intern( C_Var[] vars, int index, Promise last) {
		// follow the eq link if there is one.
		if (value != null) return value.intern(vars, index, last);
		if (index==vars.length) 
			return this;
		// if not, we need to add this path here. Ensure fields is initialized.
		if (fields == null) fields = new HashMap<String,Promise>();
		String s = vars[index].name();
		// check this edge already exists.
		Promise p = (Promise) fields.get(s);
		if (p == null) {
			// no edge. Create a new promise and add this edge.
			p = (index ==vars.length-1 && last != null) ? last : new Promise_c(vars[index]);
			fields.put(s, p);
		}
		// recursively, intern the rest of the path on the child.
		return p.intern(vars, index+1, last);
	}
	
	public void addIn(String s, Promise orphan) throws Failure {
	    assert orphan != this;
		if (value !=null)
			//	Alternative is to fwd it blindly, that would be correct, but i want to know
			// if this is happening. It should not happen.
			throw new InternalCompilerError("The node " + this + " is forwarded to " +
					value + "; the " + s + " child, " + orphan+ ", cannot be added to it.");
		
		if (fields == null) fields = new HashMap<String, Promise>();
		Promise child = (Promise) fields.get(s);
		if (child != null) {
			orphan.bind(child);
			return;
		}
		fields.put(s, orphan);
	}
	
	/*public void updateSubterms() {
		if (value !=null)
			return;
			
		if (fields == null) return;
		
		C_Field oldTerm = (C_Field) term();
		FieldInstance oldfi = oldTerm.fieldInstance();
		
		C_Field newTerm = new C_Field_c(oldfi, (C_Var) term());
		orphan.setTerm(newTerm);
		fields.put(s, orphan);
		orphan.updateSubterms();
		
	}*/

	public boolean bind(/*@nonnull*/Promise target) throws Failure {
		if (forwarded())
			throw new Failure("The promise " + this + " is already bound to "
					+ value + "; cannot bind it to " + target + ".");
		if (this==target) // nothing to do!
			return false;
		
		// Check for cycles!
		if (canReach(target) || target.canReach(this))
			throw new Failure("Binding " + this + " to " + target + " creates a cycle.");
		if (! term().prefersBeingBound() && target.term().prefersBeingBound()) {
				return target.bind(this);
		}
		value = target;
		if ( fields !=null) 
			for (Iterator<Map.Entry<String,Promise>> it = fields.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String,Promise> i =  it.next();
				target.addIn(i.getKey(), i.getValue());
			}
		fields = null;
		return true;
	}
	
	/** Can this promise reach p in the directed graph representing
	 * the constraints?
	 */
	public boolean canReach(Promise p) {
		if (p == this ) return true;
		if (value != null) return value.canReach(p);
		if (fields != null) 
			for (Iterator it = fields.values().iterator(); it.hasNext();) {
				Promise q = (Promise) it.next();
				if (q.canReach(p)) return true;
			}
		return false;
	}
	public void dump(HashMap<C_Var, C_Var> result, C_Term prefix) {
		dump(result, prefix, null, null);
	}
	public void  dump(HashMap<C_Var, C_Var> result, C_Term prefix, C_Var newSelf, C_Var newThis) {
		if (value != null) {
			C_Var t1 = term();
			if (t1==null || t1.isEQV())  // nothing to dump!
				return;
			C_Var t2=value.term();
			if (prefix != null && ! (prefix.prefixes(t1) || prefix.prefixes(t2)))
					return;
			if (newSelf != null) {
				t1 = t1.substitute(newSelf, C_Special.Self);
				t2 = t2.substitute(newSelf, C_Special.Self);
			}
			if (newThis != null) {
				t1 = t1.substitute(newThis, C_Special.This);
				t2 = t2.substitute(newThis, C_Special.This);
			}
		//	Report.report(1, "Promise_c: dumping " + t1 + "=" + t2);
			result.put(t1,t2);
			return;
		}
		if (fields != null) 
			for (Iterator<Promise> it = fields.values().iterator(); it.hasNext();) 
				it.next().dump(result, prefix, newSelf, newThis);
	}
	public String toString() {
		return var.toString() 
		+ ((value != null) ? "-> " + value : ((fields != null) ? fields.toString() : ""));
	}
	
	public void replaceDescendant(Promise y, Promise x) {
		var = var.substitute(y.term(), x.term());
		if (value!= null) {
			if (value.equals(x)) {
				value = y;
			}
			else {
				value.replaceDescendant(y, x);
			}
		}
		if (fields != null) 
			for (Iterator<Entry<String,Promise>> it = fields.entrySet().iterator(); it.hasNext();) {
				Entry<String,Promise> p = it.next();
				String key = p.getKey();
				Promise value = p.getValue();
				if (value.equals(x)) {
					p.setValue(y);
				} else {
					value.replaceDescendant(y,x);
				}
			}
	}
	
		
	public Promise value() { return value;}
	public HashMap<String, Promise> fields() { return fields;}
}
