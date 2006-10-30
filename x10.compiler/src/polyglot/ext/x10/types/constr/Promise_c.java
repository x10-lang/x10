/**
 * 
 */
package polyglot.ext.x10.types.constr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import polyglot.main.Report;
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
	 * The C_Var that this node represents in the constraint graph.
	 */
	protected final C_Var var;
	
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
	
	protected HashMap fields;
	
	public Promise_c(C_Var c) {
		super();
		value = null;
		var = c;
		
	}
	public C_Term term() {
		return var;
	}
	public boolean forwarded() {
		return value != null;
	}
	public boolean hasChildren() {
		return fields !=null;
	}
	public Promise lookup( C_Var[] vars, int index) {
		// follow the eq link if there is one.
		if (value != null) return value.lookup(vars, index);
		if (index==vars.length) 
			return this;
		if (fields == null)
			return null;
		String s = vars[index].name();
		// check this edge already exists.
		Promise p = (Promise) fields.get(s);
		if (p == null) 
			return null;
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
		Promise p = (Promise) fields.get(s);
		return p == null ? null : p.lookup();
	}
	public Promise intern( C_Var[] vars, int index) {
		// follow the eq link if there is one.
		if (value != null) return value.intern(vars, index);
		if (index==vars.length) 
			return this;
		// if not, we need to add this path here. Ensure fields is initialized.
		if (fields == null) fields = new HashMap();
		String s = vars[index].name();
		// check this edge already exists.
		Promise p = (Promise) fields.get(s);
		if (p == null) {
			// no edge. Create a new promise and add this edge.
			p = new Promise_c(vars[index]);
			fields.put(s, p);
		}
		// recursively, intern the rest of the path on the child.
		return p.intern(vars, index+1);
	}
	
	public void addIn(String s, Promise orphan) throws Failure {
		if (value !=null)
			//	Alternative is to fwd it blindly, that would be correct, but i want to know
			// if this is happening. It should not happen.
			throw new InternalCompilerError("The node " + this + " is forwarded to " +
					value + "; the " + s + " child, " + orphan+ ", cannot be added to it.");
		
		if (fields == null) fields = new HashMap();
		Promise child = (Promise) fields.get(s);
		if (child != null) {
			orphan.bind(child);
			return;
		}
		fields.put(s, orphan);
	}

	public boolean bind(/*@nonnull*/Promise target) throws Failure {
		if (forwarded())
			throw new InternalCompilerError("The promise " + this + " is already bound to "
					+ value + "; cannot bind it to " + target + ".");
		if (this==target) // nothing to do!
			return false;
		// Check for cycles!
		if (canReach(target) || target.canReach(this))
			throw new Failure("Binding " + this + " to " + target + " creates a cycle.");
		value = target;
		if ( fields !=null) 
			for (Iterator it = fields.entrySet().iterator(); it.hasNext();) {
				Map.Entry i = (Map.Entry) it.next();
				target.addIn((String) i.getKey(), (Promise) i.getValue());
			}
		fields = null;
		return true;
	}
	
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
	public void  dump(HashMap/*<C_Term,C_Term>*/ result) {
		if (value != null) {
			C_Term t1 = term(), t2=value.term();
		//	Report.report(1, "Promise_c: dumping " + t1 + "=" + t2);
			result.put(t1,t2);
			return;
		}
		if (fields != null) 
			for (Iterator it = fields.values().iterator(); it.hasNext();) {
				Promise q = (Promise) it.next();
				q.dump(result);
			}
	}
	public String toString() {
		return var.toString() 
		+ ((value != null) ? "-> " + value : ((fields != null) ? fields.toString() : ""));
	}
}
