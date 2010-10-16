package x10.types;

import polyglot.frontend.Goal;

public interface LazyRef<T> extends Ref<T> {
    Runnable resolver();
    void setResolver(Runnable resolver);
}
