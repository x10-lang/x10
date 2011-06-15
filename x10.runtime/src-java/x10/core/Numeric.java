package x10.core;

/**
 * Represents a supertype of all boxed numeric types.
 */
public abstract class Numeric extends Number implements StructI {

    public Numeric $init() {return this;}
	
    public Numeric() {}

    @Override
    public boolean equals(Object o) {
        return _struct_equals$O(o);
    }
}
