package x10.lang;


@x10.generics.Parameters({"T"})
public interface Set<T> {
static public class RTT extends x10.types.RuntimeType<x10.
  lang.
  Set> {
public final x10.types.Type T;
    
    public RTT(x10.types.Type T) {super(x10.lang.Set.class);
                                      this.T = T;
                                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.lang.Set)) return false;if (! this.T.equals(((x10.
          lang.Set) o).rtt_x10$lang$Set_T())) return false;
        return true;}}
public x10.types.Type<?> rtt_x10$lang$Set_T();

    }
