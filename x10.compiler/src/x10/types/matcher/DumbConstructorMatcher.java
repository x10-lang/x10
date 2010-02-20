package x10.types.matcher;

import java.util.List;

import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;

/** A constructor matcher that only checks name and arity. */
public class DumbConstructorMatcher extends X10ConstructorMatcher {
    public DumbConstructorMatcher(Type container, List<Type> argTypes, Context context) {
        super(container, argTypes, context);
    }

    public DumbConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes, Context context) {
        super(container, typeArgs, argTypes, context);
    }

    @Override
    public ConstructorInstance instantiate(ConstructorInstance mi) throws SemanticException {
        if (mi instanceof X10ConstructorInstance) {
            X10ConstructorInstance xmi = (X10ConstructorInstance) mi;

            if (xmi.formalTypes().size() != argTypes.size())
                return null;

            List<Type> typeArgs = this.typeArgs;
            // don't infer type arguments when doing conversions
            // if (typeArgs.size() != 0 && typeArgs.size() !=
            // xmi.typeParameters().size())
            // return null;
            if (typeArgs.size() != xmi.typeParameters().size())
                return null;

            Type c = container != null ? container : xmi.container();
            // if (typeArgs.isEmpty() && ! xmi.typeParameters().isEmpty()) {
            // Type[] Y = X10MethodInstance_c.inferTypeArguments(xmi, c,
            // argTypes, xmi.formalTypes(), xmi.typeParameters(),
            // (X10Context) context);
            // typeArgs = Arrays.asList(Y);
            // }
            if (typeArgs.size() == xmi.typeParameters().size()) {
                X10ConstructorInstance newXmi = Matcher.instantiate((X10Context) context, xmi, c, typeArgs, argTypes);
                return newXmi;
            }
        }
        return null;
    }
}