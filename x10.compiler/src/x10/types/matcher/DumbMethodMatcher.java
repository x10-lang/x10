package x10.types.matcher;

import java.util.List;

import polyglot.types.Context;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.types.X10Context;
import x10.types.X10MethodInstance;

/** A method matcher that only checks name and arity. */
public class DumbMethodMatcher extends X10MethodMatcher {
    public DumbMethodMatcher(Type container, Name name, List<Type> argTypes, Context context) {
        super(container, name, argTypes, context);
    }

    public DumbMethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        super(container, name, typeArgs, argTypes, context);
    }

    @Override
    public MethodInstance instantiate(MethodInstance mi) throws SemanticException {
        if (mi instanceof X10MethodInstance) {
            X10MethodInstance xmi = (X10MethodInstance) mi;

            if (!xmi.name().equals(name))
                return null;
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
                X10MethodInstance newXmi = Matcher.instantiate((X10Context) context, xmi, c, typeArgs, argTypes);
                return newXmi;
            }
        }
        return null;
    }
}