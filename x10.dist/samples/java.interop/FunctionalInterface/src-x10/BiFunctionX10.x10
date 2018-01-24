public class BiFunctionX10[T,U,R]{R haszero} implements java.util.function.BiFunction/*[T,U,R]*/ {
    /*
     * HACK: X10 sees Java generic type as its erasure.
     * In order to override Java generic method in X10, we eliminate type parameters from X10 method parameters.
     * It changes X10 method signature, but it works in some cases because this is covariant override.
     * This is the limitation comes from current design.
     */
    public def apply(t:Any/*T*/,u:Any/*U*/):R = Zero.get[R]();
}
