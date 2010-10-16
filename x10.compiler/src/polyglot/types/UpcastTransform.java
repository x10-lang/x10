/**
 * 
 */
package polyglot.types;

import polyglot.util.Transformation;

public class UpcastTransform<T, S extends T> implements Transformation<S, T> {
    public T transform(S v) {
        return v;
    }
}
