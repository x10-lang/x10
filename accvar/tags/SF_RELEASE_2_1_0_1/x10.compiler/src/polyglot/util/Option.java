/**
 * 
 */
package polyglot.util;


public abstract class Option<T> {
    public abstract T get();

    public static final None<?> NONE = new None<Object>();
    @SuppressWarnings("unchecked") // Casting to a generic type
    public static <S> None<S> None() { return (None<S>) NONE; }
    public static <S> Some<S> Some(S s) { return new Some<S>(s); }
    
    public static class Some<T> extends Option<T> {
        T t;

        public Some(T t) {
            this.t = t;
        }

        public T get() {
            return t;
        }

        public String toString() { return "Some(" + t + ")"; }
    }

    public static class None<T> extends Option<T> {
        private None() {}

        public T get() {
            return null;
        }
        
        public String toString() { return "None"; }
    }
}