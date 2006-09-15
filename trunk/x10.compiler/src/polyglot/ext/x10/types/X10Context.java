package polyglot.ext.x10.types;

import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;

public interface X10Context extends Context {
	
	// Use addVariable to add a PropertyInstance to the context.
	
	/**
	 * Looks up a property in the current scope.
	 * @param name
	 * @return
	 * @throws SemanticException
	 */
	PropertyInstance findProperty(String name) throws SemanticException;
	
	/**
     * Finds the type which added a property to the scope.
     * This is usually a subclass of <code>findProperty(name).container()</code>.
     */
    ClassType findPropertyScope(String name) throws SemanticException;
    
    /** Enter the scope of a deptype. */
    Context pushDepType(X10ParsedClassType t);
    
    /** Return the current deptype, null if there is none. */
    X10ParsedClassType currentDepType();
    boolean isDepType();

}
