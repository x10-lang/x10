/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.X10Context;
import polyglot.frontend.Job;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.visit.LocalClassRemover;

public class X10LocalClassRemover extends LocalClassRemover {
	public X10LocalClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	

	@Override
    protected boolean isLocal(Context c, Name name) {
    	CodeDef ci = ((X10Context) c).definingCodeDef(name);
    	if (ci == null) return false;
    	while (c != null) {
    		CodeDef curr = c.currentCode();
    		if (curr == ci) return true;
    		// Allow closures, asyncs
    		if (curr instanceof MethodDef && ((MethodDef) curr).name().equals(Name.make("$dummyAsync$")))
    			;
    		else
    			return false;
    		c = c.pop();
    	}
    	return false;
    }
}