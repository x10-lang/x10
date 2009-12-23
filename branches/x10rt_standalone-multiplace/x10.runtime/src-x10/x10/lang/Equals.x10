package x10.lang;

/**
 * An interface that should be implemented by any struct with a user-defined notion of equality.
 * 
 * This interface is implemented by x10.lang.Object. It is not implemented by functions (which do not
 * implement == either.)
 * 
 * @author vj 12/16/2009
 */
public interface Equals {
	
	def equals(Any):Boolean;
	
	def hashCode():Int;
}