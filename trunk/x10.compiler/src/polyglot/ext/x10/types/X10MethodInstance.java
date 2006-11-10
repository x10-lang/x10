/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Formal;
import polyglot.types.MethodInstance;

/**
 * @author vj
 *
 */
public interface X10MethodInstance extends MethodInstance {
	/**
	 * Does this method instance represent a method on a java class?
	 * @return
	 */
	boolean isJavaMethod();
	
	/**
	 * Is this a method in a safe class, or a method marked as safe?
	 * @return
	 */
	boolean isSafe();
	
	List<Formal> formals();

}
