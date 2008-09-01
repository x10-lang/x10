package x10.lang;


@x10.generics.Parameters({"K", "V"})
public interface Map<K, V> extends x10.core.fun.Fun_0_1<K,V> {
static public class RTT extends x10.types.RuntimeType<x10.
  lang.
  Map> {
public final x10.types.Type K;
    public final x10.types.Type V;
    
    public RTT(x10.types.Type K, x10.types.Type V) {super(x10.lang.Map.class);
                                                        this.K = K;
                                                        this.V = V;
                                                        }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.lang.Map)) return false;if (! this.K.equals(((x10.
          lang.Map) o).rtt_x10$lang$Map_K())) return false;
        if (! this.V.equals(((x10.lang.
          Map) o).rtt_x10$lang$Map_V())) return false;
        return true;}}
public x10.types.Type<?> rtt_x10$lang$Map_K();
public x10.types.Type<?> rtt_x10$lang$Map_V();

    }
