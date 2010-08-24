/**
 * 
 */
package polyglot.types;

import polyglot.util.Transformation;

public class DerefTransform<T extends TypeObject> implements
        Transformation<Ref<? extends T>, T> {
    public T transform(Ref<? extends T> ref) {
        return Types.get(ref);
    }
}
