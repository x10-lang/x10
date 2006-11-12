package polyglot.ext.x10.ast;

/**
 * Immutable representation of a local variable access. 
 * Introduced to add X10 specific type checks. A local variable accessed
 * in a deptype must be final.
 * 
 * @author vj
 */
import polyglot.ast.Node;
import polyglot.ext.jl.ast.Local_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Local_c extends Local_c {

	public X10Local_c(Position pos, String name) {
		super(pos, name);
		
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		 Context c = tc.context();
		 try {
		    LocalInstance li = c.findLocal(name);
		    
		    // if the local is defined in an outer class, then it must be final
		    if (!c.isLocal(li.name())) {
		    	// this local is defined in an outer class
		    	if (!li.flags().isFinal()) {
		    		throw new SemanticException("Local variable \"" + li.name() + 
		    				"\" is accessed from an inner class, and must be declared " +
		    				"final.",
		    				this.position());                     
		    	}
		    }
		    
		    
		    X10Local_c result = (X10Local_c) localInstance(li).type(li.type());
		    X10Context xtc = (X10Context) tc.context();
		    if (xtc.inDepType()) {
		    	li = result.localInstance();
		    	if (! li.flags().isFinal()) {
		    		throw new SemanticException("Local variable " + li.name() 
		    				+ " must be final in a depclause.", 
		    				position());
		    	}
		    }
		
	    return result;
		 } catch (SemanticException z) {
			 if (tc instanceof TypeElaborator) {
				 // Ignore semantic exceptions that may arise during TypeElaboration. The
				 // field being referenced may not exist because of an MDE -- e.g. its type
				 // does not yet have its signature resolved. 
				 return this;
			 } else {
				 throw z;
			 }
		 }
	}
}
