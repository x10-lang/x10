package x10.lang;


public interface Signed extends x10.lang.Integer {
static public class RTT extends x10.types.RuntimeType<x10.lang.Signed> {
public static final RTT it = new RTT();
    
    
    public RTT() {super(x10.lang.Signed.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.lang.Signed)) return false;return true;}}
}
