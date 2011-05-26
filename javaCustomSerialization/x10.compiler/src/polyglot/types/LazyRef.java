package polyglot.types;

import polyglot.frontend.Goal;


public interface LazyRef<T> extends Ref<T> {
    Runnable resolver();
    void setResolver(Runnable resolver);
    boolean isResolverSet();
}
