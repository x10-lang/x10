/**
 * 
 */
package polyglot.types;

import polyglot.util.Transformation;

public class ConstructorAsTypeTransform implements
        Transformation<ConstructorDef, ConstructorInstance> {
    public ConstructorInstance transform(ConstructorDef def) {
        return def.asInstance();
    }
}
