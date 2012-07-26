package x10.constraint;
/**
 * Representation of the Def associated with some XTerms. This interface
 * is used instead of polyglot.types.Def to avoid package dependencies. 
 * 
 * @author lshadare
 *
 */
public interface XDef<T extends XType> {
	T resultType(); 
	//int hashCode();
	//boolean equals(); 
}
