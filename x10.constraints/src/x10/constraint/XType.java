package x10.constraint;
/**
 * All types of an XTerm should implement this interface. The main
 * purpose of the interface rather than using polyglot.types.Type 
 * directly is to avoid package dependencies. 
 *  
 * @author lshadare
 *
 */
public interface XType {
	boolean isBoolean(); 
}
