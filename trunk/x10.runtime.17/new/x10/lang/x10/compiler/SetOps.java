package x10.compiler;


@x10.generics.Parameters({"T"})
public interface SetOps<T> {
static public class RTT extends x10.types.RuntimeType<x10.
  compiler.
  SetOps> {
public final x10.types.Type T;
    
    public RTT(x10.types.Type T) {super(x10.compiler.SetOps.class);
                                      this.T = T;
                                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.compiler.
          SetOps)) return false;if (! this.T.equals(((x10.compiler.
          SetOps) o).rtt_x10$compiler$SetOps_T())) return false;
        return true;}}
public x10.types.Type<?> rtt_x10$compiler$SetOps_T();

    
    
    
//#line 4
T $not();
    
    
//#line 5
T $and(final T that);
    
    
//#line 6
T $or(final T that);
    
    
//#line 7
T $minus(final T that);
}
