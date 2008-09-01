package x10.compiler;


public interface Native
  extends x10.lang.annotations.MethodAnnotation,
          x10.lang.annotations.FieldAnnotation
{
static public class RTT extends x10.types.RuntimeType<x10.compiler.Native> {
public static final RTT it = new RTT();
    
    
    public RTT() {super(x10.compiler.Native.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.compiler.Native)) return false;return true;}}

    
    java.lang.String lang();
    
    java.lang.String code();
}
