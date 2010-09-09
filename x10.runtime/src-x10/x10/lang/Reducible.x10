package x10.lang;

/**
 * The interface that must be implemented by reduction operations.
 * 
 * Implementations of Reducible[T] must ensure that the apply(T,T) method is associative
 * and commutative and stateless, and that zero() is an identity.
 * @author vj
 */
public interface Reducible[T]  {
	
	/**
	 * The identity for this reducer operation. It must be the case
	 * that apply(zero(),f)=f.
	 */
	global safe def zero():T;
	
	global safe def apply(T,T):T;
	
}