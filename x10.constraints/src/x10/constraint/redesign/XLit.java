package x10.constraint.redesign;
/**
 * Interface to represent a literal with its associated value. A literal
 * is essentially a constant in that two literals are equal if their values
 * are equal. 
 * 
 * @author lshadare
 *
 * @param <T> type of XTerms
 * @param <V> value contained in this literal
 */
public interface XLit<T extends XType,V> extends XTerm<T>  {
	V val(); 
}
