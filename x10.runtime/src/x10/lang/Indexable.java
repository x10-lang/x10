/*
 * Created by vj on Jan 13, 2005
 *
 * 
 */
package x10.lang;

/** A package protected interface implemented by x10.lang classes which can be accessed as arrays.
 * @author vj Jan 13, 2005
 * 
 */
interface Indexable extends IndexableGet, IndexableSet {
	public long getUnsafeAddress();
}
