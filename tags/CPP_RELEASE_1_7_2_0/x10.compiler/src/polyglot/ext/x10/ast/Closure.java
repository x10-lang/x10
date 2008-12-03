/*
 * Created on Feb 26, 2007
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.CodeBlock;
import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.MethodInstance;

public interface Closure extends Expr, CodeBlock {
    /** The closure's formal parameters.
     * @return A list of {@link polyglot.ast.Formal Formal}
     */
    List formals();

    /** The closure's exception throw types.
     * @return A list of {@link polyglot.ast.TypeNode TypeNode}
     */
    List throwTypes();

    /**
     * @return the closure's return type
     */
    TypeNode returnType();

    /**
     * @return the closure's containing method
     */
    MethodInstance methodContainer();

    /** Set the closure's containing method
     * @param methodInstance
     * @return the new {@link Closure}
     */
    Closure methodContainer(MethodInstance methodInstance);

    /**
     * @return the closure's containing type
     */
    ClassType typeContainer();

    /** Set the closure's containing type
     * @param typeContainer
     * @return the new {@link Closure}
     */
    Closure typeContainer(ClassType typeContainer);
}
