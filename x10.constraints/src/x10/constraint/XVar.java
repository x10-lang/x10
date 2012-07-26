package x10.constraint;
/**
 * Interface to represent XTerm variables with their associated 
 * type information T. Two XVar are equal iff their names are equal.
 * @author lshadare
 *
 * @param <T>
 */
public interface XVar<T extends XType> extends XTerm<T> {
	String name(); 
}
