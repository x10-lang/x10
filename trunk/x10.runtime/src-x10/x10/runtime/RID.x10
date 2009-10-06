package x10.runtime;

public final struct RID(place:Place, id:Int) {
    public def this(place:Place, id:Int) = property(place, id);

    public def hashCode():Int = id;
}
