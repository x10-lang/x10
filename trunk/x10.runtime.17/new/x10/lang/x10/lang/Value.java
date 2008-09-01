package x10.lang;


public class Value {
static public class RTT extends x10.types.RuntimeType<x10.lang.Value> {
public static final RTT it = new RTT();
    
    
    public RTT() {super(x10.lang.Value.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.lang.Value)) return false;return true;}}

    
    
//#line 7
native public boolean equals(final java.lang.Object a0);
    
    
//#line 8
native public int hashCode();
    
    
//#line 9
native public java.lang.String toString();
    
    
//#line 6
public Value() {  }
}
