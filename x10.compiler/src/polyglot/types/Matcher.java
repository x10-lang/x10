/**
 * 
 */
package polyglot.types;

public interface Matcher<T> {
    T instantiate(T matched) throws SemanticException;

    boolean visit(Type t);
   
    Name name();
    String signature();

    /** Key used for cache lookups, or null if cannot be cached. */
    Object key();
}