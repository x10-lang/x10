package x10.constraint;
/**
 * Representation of a local variable in a constraint and its associated 
 * definition. 
 * @author lshadare
 *
 * @param <T> the type of the XTerm
 * @param <D> the definition of the local variable
 */
public interface XLocal<T extends XType, D extends XDef<T>> extends XVar<T> {
	/**
	 * Returns the definition associated with this local variable
	 * @return
	 */
	D def(); 
}
