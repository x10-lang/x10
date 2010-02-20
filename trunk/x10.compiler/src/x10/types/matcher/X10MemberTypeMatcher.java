/**
 * 
 */
package x10.types.matcher;

import polyglot.types.Context;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c.MemberTypeMatcher;
import x10.types.MacroType;

public class X10MemberTypeMatcher extends MemberTypeMatcher {
    public X10MemberTypeMatcher(Type container, Name name, Context context) {
        super(container, name, context);
    }

    @Override
    public Named instantiate(Named t) throws SemanticException {
        Named n = super.instantiate(t);
        // Also check that the name is simple.
        if (n instanceof MacroType) {
            MacroType mt = (MacroType) n;
            if (mt.formalTypes().size() != 0)
                return null;
            if (mt.typeParameters().size() != 0)
                return null;
        }
        return n;
    }
}