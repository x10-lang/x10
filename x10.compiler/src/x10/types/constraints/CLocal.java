package x10.types.constraints;

import polyglot.types.Def;
import polyglot.types.Type;
import x10.constraint.XVar;
import x10.types.X10LocalDef;

/**
 * Representation of a local variable in a constraint and its associated 
 * definition. 
 * @author lshadare
 *
 */
public interface CLocal extends XVar<Type> {
	/**
	 * Returns the definition associated with this local variable
	 * @return
	 */
	X10LocalDef def(); 
}
