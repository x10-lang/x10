package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.Type_c;
import polyglot.util.Position;
import polyglot.util.TypedList;

public class AnnotatedType_c extends Type_c implements AnnotatedType {
    Type baseType;
    List<Type> annotations;

    public AnnotatedType_c(X10TypeSystem ts, Position pos, Type baseType, List<Type> annotations) {
	super(ts, pos);
	this.baseType = baseType;
	this.annotations = TypedList.copyAndCheck(annotations, Type.class, true);
    }

    public Type baseType() {
        return baseType;
    }

    public AnnotatedType baseType(Type baseType) {
        AnnotatedType_c t = (AnnotatedType_c) copy();
        t.baseType = baseType;
        return t;        
    }

    public List<Type> annotations() {
        return annotations;
    }

    public AnnotatedType annotations(List<Type> annotations) {
        AnnotatedType_c t = (AnnotatedType_c) copy();
        t.annotations = TypedList.copyAndCheck(annotations, Type.class, true);
        return t;
    }
    
    public String translate(Resolver c) {
        return baseType.translate(c);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Type ct : annotations) {
            sb.append("@");
            sb.append(ct);
            sb.append(" ");
        }
        sb.append(baseType);
        return sb.toString();
    }
}
