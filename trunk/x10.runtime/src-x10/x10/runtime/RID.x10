package x10.runtime;

public final class RID(place:Place, id:Int) {
	public def this(place:Place, id:Int) = property(place, id);
	
	public global def hashCode():Int = id;
}
