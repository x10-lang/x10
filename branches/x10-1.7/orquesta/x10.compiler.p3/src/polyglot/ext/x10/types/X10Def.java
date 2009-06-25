package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Def;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface X10Def extends Def {
    /** Get the annotations on the definition. */
    List<Ref<? extends Type>> defAnnotations();
    
    /** Set the annotations on the definition. */
    void setDefAnnotations(List<Ref<? extends Type>> ats);

    List<Type> annotations();
    List<Type> annotationsMatching(Type t);
}
