package x10.constraint;
/**
 * Interface to be implemented by all TypeSystems used by XType. The
 * purpose of this interface is to avoid package dependencies. 
 * @author lshadare
 *
 * @param <T>
 */
public interface XTypeSystem<T extends XType> {
	/**
	 * Return the Boolean type. 
	 * @return
	 */
	T Boolean(); 
	/**
	 * Return the Null Type. 
	 * @return
	 */
	T Null(); 
}
