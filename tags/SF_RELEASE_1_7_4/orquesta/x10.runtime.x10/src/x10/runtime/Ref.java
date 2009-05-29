package x10.runtime;

// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public class Ref {
    private final Object location;
    
    public Ref() {
        location = x10.runtime.Runtime.here();
    }
}
