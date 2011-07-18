/**
 * 
 */
package polyglot.types;

import polyglot.util.Transformation;
import x10.types.MethodInstance;

public class MethodAsTypeTransform implements
        Transformation<MethodDef, MethodInstance> {
    public MethodInstance transform(MethodDef def) {
        return def.asInstance();
    }
}
