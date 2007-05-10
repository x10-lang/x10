/*
 * Created on Feb 26, 2007
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.Declaration;
import polyglot.types.FunctionInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;

/**
 * Represents a closure.<br>
 * You'd think it wouldn't be necessary to have a representation of (nameless) closures in the
 * type system outside of <code>ClosureTypes</code>, but the control-/data-flow framework requires
 * that all code bodies have an associated instance. See <code>CodeNode.codeInstance()</code>, and
 * <code>Closure.enterScope()</code>, which needs to push something on the Context stack, but the
 * only reasonable thing to push is some form of <code>CodeInstance</code>. Perhaps there should be
 * a more general kind of "code context" class, with a corresponding push method on
 * <code>Context</code>.
 * @author rfuhrer
 */
public interface ClosureInstance extends FunctionInstance, Declaration {
    MethodInstance methodContainer();

    void setMethodContainer(MethodInstance container);

    /**
     * Return true if this closure can be called with 
     * actual parameters of types <code>actualTypes</code>.
     * @param actualTypes A list of argument types of type <code>Type</code>.
     * @see polyglot.types.Type
     */
    boolean closureCallValid(List<Type> actualTypes);
}
