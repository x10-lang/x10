package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.types.DerefTransform;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Use;
import polyglot.util.TransformingList;

public class X10TypeObjectMixin {
    
    public static List<X10ClassType> annotations(X10Def def) {
        return new TransformingList<Ref<? extends X10ClassType>, X10ClassType>(def.defAnnotations(), new DerefTransform<X10ClassType>());
    }
    
    public static List<X10ClassType> annotationsMatching(X10Def o, Type t) {
        return annotationsMatching(annotations(o), t);
    }
    
    public static List<X10ClassType> annotationsNamed(X10Def o, String fullName) {
        return annotationsNamed(annotations(o), fullName);
    }
    
    public static List<X10ClassType> annotations(X10Use<? extends X10Def> o) {
        return annotations(o.x10Def());
    }

    public static List<X10ClassType> annotationsMatching(X10Use<? extends X10Def> o, Type t) {
        return annotationsMatching(annotations(o), t);
    }
    
    public static List<X10ClassType> annotationsNamed(X10Use<? extends X10Def> o, String fullName) {
        return annotationsNamed(annotations(o), fullName);
    }

    public static List<X10ClassType> annotationsMatching(List<X10ClassType> annotations, Type t) {
        List<X10ClassType> l = new ArrayList<X10ClassType>();
        for (Iterator<X10ClassType> i = annotations.iterator(); i.hasNext(); ) {
            X10ClassType ct = i.next();
            if (ct.isSubtype(t)) {
                l.add(ct);
            }
        }
        return l;
    }

    public static List<X10ClassType> annotationsNamed(List<X10ClassType> annotations, String fullName) {
        List<X10ClassType> l = new ArrayList<X10ClassType>();
        for (Iterator<X10ClassType> i = annotations.iterator(); i.hasNext(); ) {
            X10ClassType ct = i.next();
            if (ct.fullName().equals(fullName)) {
                l.add(ct);
            }
        }
        return l;
    }

}
