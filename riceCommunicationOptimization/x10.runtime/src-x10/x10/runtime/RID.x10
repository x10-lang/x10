package x10.runtime;

// TODO: This should be a struct, but struct codegen not functional enough yet.
public final value RID(place:Place, id:Int) {
	public def this(place:Place, id:Int) = property(place, id);
	
	public def hashCode():Int = id;
}
