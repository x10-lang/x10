package x10.constraint;
/**
 * Interface representing a field/method access. 
 * @author lshadare
 *
 * @param <T> 
 * @param <F>
 */
public interface XField<T extends XType, F extends XDef<T>> extends XExpr<T> {
	F field(); 
	XTerm<T> receiver(); 
}
