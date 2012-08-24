package x10.types.constraints;

import x10.constraint.XDef;
import x10.constraint.XType;
import x10.constraint.XVar;

/**
 * Representation of a local variable in a constraint and its associated 
 * definition. 
 * @author lshadare
 *
 * @param <T> the type of the XTerm
 * @param <D> the definition of the local variable
 */
public interface CLocal<T extends XType, D extends XDef<T>> extends XVar<T> {
	/**
	 * Returns the definition associated with this local variable
	 * @return
	 */
	D def(); 
}
