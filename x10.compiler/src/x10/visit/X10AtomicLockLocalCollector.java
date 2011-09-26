package x10.visit;

import java.util.LinkedHashSet;
import java.util.Set;

import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Atomic_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.types.X10LocalDef;

/**
 * A visitor to fetch all local lock variables used inside an atomic
 * section.
 * 
 * It also maintains a set of all locally declared variables, for
 * computing lock variables that are referred but are not defined
 * in the visited block.
 * 
 * An example:
 * 
 * def foo() {
 *   local_lock_var1 = new OrderedLock();
 *   async {
 *      atomic(var1, var2) {  local_lock_var1,  local_lock_var2     }
 *   }
 * }
 * 
 * Suppose the visitor is going to visit the method block foo().
 * Clearly, the atomic section uses two local locks: local_lock_var1, and
 * local_lock_var2. However, only local_lock_var1 is defined locally. Thus,
 * the escaped lock local { local_lock_var2 }. 
 *
 * @author Sai Zhang (szhang@cs.washington.edu)
 *
 */
public class X10AtomicLockLocalCollector extends NodeVisitor {
	//all local locks referred inside an atomic block
	private Set<LocalDef> lockLocals = new LinkedHashSet<LocalDef>(); 
	//all local locks defined inside the visited block
	private Set<LocalDef> localDecls = new LinkedHashSet<LocalDef>();
	
	@Override
	public Node leave(Node old, Node n, NodeVisitor v)  {
		//get all referred lock locals inside each atomic block
		if(n instanceof Atomic_c) {
			AtomicLockLocalVisitor visitor = new AtomicLockLocalVisitor();
			n.visit(visitor);
			lockLocals.addAll(visitor.lockLocals);
		}
		//also get all locally declared vars
		if(n instanceof X10LocalDecl_c) {
		    X10LocalDecl_c decls = (X10LocalDecl_c)n;
		    localDecls.add(decls.localDef());
		    
		}
		return n;
	}
	//Returns all lock locals referred in the atomic set
	public Set<LocalDef> getLockLocals() {
		return this.lockLocals;
	}
	//Returns all lock locals that are referred in an atomic set, but
	//are not locally defined.
	public Set<LocalDef> getEscapedLockLocals() {
		Set<LocalDef> ret = new LinkedHashSet<LocalDef>();
		for(LocalDef lockDef : this.lockLocals) {
			boolean isDecl = false;
			for(LocalDef localDef : this.localDecls) {
				if(lockDef.name().toString().
						indexOf(X10LockMapAtomicityTranslator.getLocalVarLockName(localDef.name().toString())) != -1) {
					isDecl = true;
					break;
				}
			}
			if(!isDecl) {
				ret.add(lockDef);
			}
		}
		return ret;
	}

}

/**
 * A simple visit class to fetch all local defs corresponding
 * to an allocated local lock variable.
 * 
 * Note, the local lock variable has a fixed name pattern, so
 * the code just uses a name comparison.
 * */
class AtomicLockLocalVisitor extends NodeVisitor {
	Set<LocalDef> lockLocals = new LinkedHashSet<LocalDef>();
	
	Set<LocalDef> escapedLocals = new LinkedHashSet<LocalDef>();
	
	@Override
	public Node leave(Node old, Node n, NodeVisitor v)  {
	   if (n instanceof X10Local_c) {
		  X10Local_c localc = (X10Local_c)n;
		  if(X10LockMapAtomicityTranslator.isLocalLock(localc.name().toString())) {
			 lockLocals.add(localc.localInstance().def());
			 escapedLocals.add(localc.localInstance().def());
		  }
	    }
	   

	   
	   return n;
	}
}
