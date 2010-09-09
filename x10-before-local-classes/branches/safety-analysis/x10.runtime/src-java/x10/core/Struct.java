package x10.core;

import x10.types.RuntimeType;
import x10.types.Type;

// Base class for all X10 structs
public abstract class Struct {

    public static class RTT extends RuntimeType<Struct> {
    	public static final RTT it = new RTT();

    	public RTT() {
            super(Struct.class);
        }

        @Override
        public boolean instanceof$(Object o) {
	    return o instanceof Struct;
        }

    }

    public Struct() {}

}
