/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.TypeNode;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.X10Context;
import polyglot.frontend.Job;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.MethodInstance;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.LocalClassRemover;
import polyglot.ext.x10.types.X10TypeSystem;

public class X10LocalClassRemover extends LocalClassRemover {
	public X10LocalClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	

    protected boolean isLocal(Context c, String name) {
    	CodeInstance ci = ((X10Context) c).definingCodeDef(name);
    	if (ci == null) return false;
    	while (c != null) {
    		CodeInstance curr = c.currentCode();
    		if (curr == ci) return true;
    		// Allow closures, asyncs
    		if (curr instanceof MethodInstance && ((MethodInstance) curr).name().equals("$dummyAsync$"))
    			;
    		else
    			return false;
    		c = c.pop();
    	}
    	return false;
    }

    protected TypeNode defaultSuperType(Position pos) {
	return nf.CanonicalTypeNode(pos, ((X10TypeSystem)ts).X10Object());
    }
}
