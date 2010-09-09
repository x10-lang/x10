/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.TypeSystem;

/**
 * @author vj
 *
 */
public interface X10ProcedureInstance {
	boolean callValidImplNoClauses(List argTypes);
	
	List<X10Type> formalTypes();
	
	TypeSystem typeSystem();

}
