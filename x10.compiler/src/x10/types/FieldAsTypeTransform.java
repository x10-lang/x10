/**
 * 
 */
package x10.types;

import polyglot.util.Transformation;

public class FieldAsTypeTransform implements
        Transformation<FieldDef, FieldInstance> {
    public FieldInstance transform(FieldDef def) {
        return def.asInstance();
    }
}
