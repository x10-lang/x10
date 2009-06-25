package x10.types;

import x10.core.fun.Fun_0_1;

// new List[int{self==x}]();
// ->
// new List(Types.constrained(Type.INT, (self) => self==x));    // this works for instanceof T, but not for <:
public class ConstrainedType<T> extends RuntimeType<T> {
    Type<T> base;
    Fun_0_1<T,Boolean> tester;

    public ConstrainedType(Type<T> base, Fun_0_1<T, Boolean> t) {
        super(base.getJavaClass());
        this.base = base;
        this.tester = t;
    }
    
    public boolean instanceof$(Object o) {
        return base.instanceof$(o) && (tester == null || tester.apply((T) o));
    }
}
