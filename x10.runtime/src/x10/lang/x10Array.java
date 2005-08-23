package x10.lang;

/* root for all array classes */
abstract public class x10Array implements Indexable, Unsafe {
	// copy src onto dest over regions on the this place
	abstract public void copyLocalSection(x10Array dest,x10Array src,region localRegion);
	abstract public dist getDistribution();
	
	abstract public void copyDisjoint(x10Array include,x10Array exclude,region localRegion);
	
	
}
