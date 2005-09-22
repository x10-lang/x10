package x10.lang;

/* root for all array classes */
abstract public class x10Array implements Indexable, Unsafe {
	// copy src onto dest over regions on the this place
	abstract public dist getDistribution();
}
