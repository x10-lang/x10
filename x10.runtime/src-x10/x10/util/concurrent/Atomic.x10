package x10.util.concurrent;

/**
 * The interface that each compiled class (or interface) must
 * implement (inherit) in data-centric sychronization.
 * @author Sai Zhang (szhang@cs.washington.edu)
 */
public interface Atomic {
	public def getOrderedLock():OrderedLock;
}