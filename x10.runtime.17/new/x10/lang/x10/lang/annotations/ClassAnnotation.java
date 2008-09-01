package x10.lang.annotations;


public interface ClassAnnotation extends x10.lang.annotations.Annotation {
static public class RTT extends x10.types.RuntimeType<x10.
  lang.
  annotations.
  ClassAnnotation> {
public static final RTT it = new RTT();
    
    
    public RTT() {super(x10.lang.annotations.ClassAnnotation.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.lang.annotations.
          ClassAnnotation)) return false;return true;}}
}
