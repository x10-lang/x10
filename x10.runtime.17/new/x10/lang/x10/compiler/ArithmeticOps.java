package x10.compiler;


@x10.generics.Parameters({"T"})
public interface ArithmeticOps<T> {
static public class RTT extends x10.types.RuntimeType<x10.
  compiler.
  ArithmeticOps> {
public final x10.types.Type T;
    
    public RTT(x10.types.Type T) {super(x10.compiler.ArithmeticOps.class);
                                      this.T = T;
                                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.compiler.
          ArithmeticOps)) return false;if (! this.T.equals(((x10.compiler.
          ArithmeticOps) o).rtt_x10$compiler$ArithmeticOps_T())) return false;
        return true;}}
public x10.types.Type<?> rtt_x10$compiler$ArithmeticOps_T();

    
    
    
//#line 4
T $plus();
    
    
//#line 5
T $minus();
    
    
//#line 7
T $plus(final T that);
    
    
//#line 8
T $minus(final T that);
    
    
//#line 9
T $times(final T that);
    
    
//#line 10
T $over(final T that);
    
    
//#line 11
T $percent(final T that);
}
