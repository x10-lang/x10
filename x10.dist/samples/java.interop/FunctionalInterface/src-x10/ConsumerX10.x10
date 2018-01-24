public class ConsumerX10[T] implements java.util.function.Consumer/*[T]*/ {
    /*
     * HACK: X10 sees Java generic type as its erasure.
     * In order to override Java generic method in X10, we eliminate type parameters from X10 method parameters.
     * It changes X10 method signature, but it works in some cases because this is covariant override.
     * This is the limitation comes from current design.
     */
    public def accept(value:Any/*T*/):void {}
}
