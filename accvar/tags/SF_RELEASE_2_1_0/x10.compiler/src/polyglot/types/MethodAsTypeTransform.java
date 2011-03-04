/**
 * 
 */
package polyglot.types;

import polyglot.util.Transformation;

public class MethodAsTypeTransform implements
        Transformation<MethodDef, MethodInstance> {
    public MethodInstance transform(MethodDef def) {
        return def.asInstance();
    }
}
