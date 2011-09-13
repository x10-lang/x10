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

public class X10AtomicLockLocalCollector extends NodeVisitor {
	
	private Set<LocalDef> lockLocals = new LinkedHashSet<LocalDef>(); 
	
	private Set<LocalDef> localDecls = new LinkedHashSet<LocalDef>();
	
	@Override
	public Node leave(Node old, Node n, NodeVisitor v)  {
		
		//System.out.println("visiting n: " + n.getClass());
		if(n instanceof Atomic_c) {
			//System.out.println("  entering  atomic section...");
			AtomicLockLocalVisitor visitor = new AtomicLockLocalVisitor();
			n.visit(visitor);
			lockLocals.addAll(visitor.lockLocals);
		}
		
		if(n instanceof X10LocalDecl_c) {
		    X10LocalDecl_c decls = (X10LocalDecl_c)n;
		    localDecls.add(decls.localDef());
		    
		}
		
		return n;
		
	}
	

//}
	
	public Set<LocalDef> getLockLocals() {
		return this.lockLocals;
	}
	
	public Set<LocalDef> getEscapedLockLocals() {
		Set<LocalDef> ret = new LinkedHashSet<LocalDef>();
		for(LocalDef lockDef : this.lockLocals) {
			boolean isDecl = false;
			for(LocalDef localDef : this.localDecls) {
				if(lockDef.name().toString().
						indexOf(X10LinkedAtomicityTranslator.getLocalVarLockName(localDef.name().toString())) != -1) {
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

class AtomicLockLocalVisitor extends NodeVisitor {
	Set<LocalDef> lockLocals = new LinkedHashSet<LocalDef>();
	
	Set<LocalDef> escapedLocals = new LinkedHashSet<LocalDef>();
	
	@Override
	public Node leave(Node old, Node n, NodeVisitor v)  {
	   if (n instanceof X10Local_c) {
		  X10Local_c localc = (X10Local_c)n;
		  if(X10LinkedAtomicityTranslator.isLocalLock(localc.name().toString())) {
			 lockLocals.add(localc.localInstance().def());
			 escapedLocals.add(localc.localInstance().def());
		  }
	    }
	   

	   
	   return n;
	}
}
