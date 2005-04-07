/*
 * Created on Oct 14, 2004
 */
package x10.base;

import x10.lang.Runtime;

/**
 * @author Christoph von Praun
 * 
 * Types are instanced when an object of generic type is allocated.
 * Before the actual object, an array with the actual type arguments is created 
 * that is passed as first parameter to the constructur of the new Object.
 * 
 * Instances of this class can be used by the runtime system, e.g., to 
 * allocate instances of template arguments that are of ordinary value or 
 * reference types and for dynamic type checking on template type arguments. 
 */
public class Type implements TypeArgument {
	private final Class class_;
	
	public Type(String name) {
		Class tmp = null;
		try {
			tmp = Class.forName(name);
		} catch (Throwable e) {
			Runtime.java.error("Failed to instance class.", e);
		} finally {
			class_ = tmp;
		}
	}
}
