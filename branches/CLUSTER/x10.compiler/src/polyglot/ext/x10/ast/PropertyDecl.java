package polyglot.ext.x10.ast;

import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;

public interface PropertyDecl extends FieldDecl {
    /**
     * Return the synthetic getter metod for this property.
     * @return -- the getter method for this property.
     */
    MethodDecl getter();
}
