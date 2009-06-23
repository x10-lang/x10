
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Type;

public interface X10Use<T extends X10Def> {
    public T x10Def();
    public List<Type> annotations();
    public List<Type> annotationsMatching(Type t);
}
