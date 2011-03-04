package x10.base;

/* Used by global unshared arrays.  For now, just use the unsafe class to implement and
 * prevent GC from moving the arrays
 * @author cmd
 */
import x10.base.MemoryBlockUnsafe;
public class MemoryBlockPinned extends MemoryBlockUnsafe {
	public MemoryBlockPinned(int count, long size) {
		super(count,null, size); 
	}
}