package x10.lang;

/**
 * The interface to be implemented by classes Foo such that x10.lang.Runtime.asyncArrayCopy can be
 * invoked on objects of type Foo[:dist==dist.UNIQUE]. 
 * 
 * @author vj
 *
 */
public interface RemoteDoubleArrayCopier {
	/**
	 * Returns the array which is to receive the data. Must be invoked on the 
	 * dest object.
	 * @return
	 */
	// double[:rail] getDestArray();
	DoubleReferenceArray getDestArray();
	/**
	 * Returns the array which is to be the source of the data. Must be invoked on the 
	 * source object.  
	 * @return
	 */
	// double value [:rail] getSourceArray();
	DoubleReferenceArray  getSourceArray();
	/**
	 * Method to be invoked on the dest object to perform destination-side notification.
	 * Method is guaranteed to be invoked after the array copy has been completed.
	 *  @arg s --- the offset at which this asyncDoubleArrayCopy started writing.
	 *
	 */
	void postCopyRun(int s);

}
