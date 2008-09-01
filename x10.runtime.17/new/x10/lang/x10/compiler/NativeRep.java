package x10.compiler;


public interface NativeRep extends x10.lang.annotations.ClassAnnotation {
static public class RTT extends x10.types.RuntimeType<x10.compiler.NativeRep> {
public static final RTT it = new RTT();
    
    
    public RTT() {super(x10.compiler.NativeRep.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.compiler.NativeRep)) return false;return true;}}

    
    java.lang.String lang();
    
    java.lang.String type();
}
