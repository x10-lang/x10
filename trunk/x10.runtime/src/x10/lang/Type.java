/*
 * Created on Oct 14, 2004
 */
package x10.lang;

/**
 * @author Christoph von Praun
 * 
 * Types are instanced when an object of generic type is allocated.
 * Before the actual object, an array with the actual type arguments is created 
 * that is passed as first parameter to the constructur of the new Object.
 */
public class Type implements TypeArgument {
	private final Class class_;
	
	Type(String name) {
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
