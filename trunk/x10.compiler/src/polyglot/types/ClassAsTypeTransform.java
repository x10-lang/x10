/**
 * 
 */
package polyglot.types;

import polyglot.util.Transformation;

public class ClassAsTypeTransform implements
        Transformation<ClassDef, ClassType> {
    public ClassType transform(ClassDef def) {
        return def.asType();
    }
}
