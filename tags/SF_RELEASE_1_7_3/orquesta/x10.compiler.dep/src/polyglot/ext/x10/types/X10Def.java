package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Def;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface X10Def extends Def {
    /** Get the annotations on the definition. */
    List<Ref<? extends X10ClassType>> defAnnotations();
    
    /** Set the annotations on the definition. */
    void setDefAnnotations(List<Ref<? extends X10ClassType>> annotations);

    List<X10ClassType> annotations();
    List<X10ClassType> annotationsMatching(Type t);
}
