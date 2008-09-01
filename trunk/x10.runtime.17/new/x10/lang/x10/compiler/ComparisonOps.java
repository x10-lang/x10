package x10.compiler;


@x10.generics.Parameters({"T"})
public interface ComparisonOps<T> {
static public class RTT extends x10.types.RuntimeType<x10.
  compiler.
  ComparisonOps> {
public final x10.types.Type T;
    
    public RTT(x10.types.Type T) {super(x10.compiler.ComparisonOps.class);
                                      this.T = T;
                                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.compiler.
          ComparisonOps)) return false;if (! this.T.equals(((x10.compiler.
          ComparisonOps) o).rtt_x10$compiler$ComparisonOps_T())) return false;
        return true;}}
public x10.types.Type<?> rtt_x10$compiler$ComparisonOps_T();

    
    
    
//#line 4
boolean $eq(final T that);
    
    
//#line 5
boolean $lt(final T that);
    
    
//#line 6
boolean $gt(final T that);
    
    
//#line 7
boolean $le(final T that);
    
    
//#line 8
boolean $ge(final T that);
    
    
//#line 9
boolean $ne(final T that);
}
