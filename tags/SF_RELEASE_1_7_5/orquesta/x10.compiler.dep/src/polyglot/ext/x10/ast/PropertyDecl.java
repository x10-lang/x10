/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;

public interface PropertyDecl extends FieldDecl {
    /**
     * Return the synthetic getter metod for this property.
     * @return -- the getter method for this property.
     */
    MethodDecl getter();
    
    /**
     * For Interfaces with properties, an abstract method signature for each property 
     * is generated in the interface body
     * <RAJ> 
     */
    MethodDecl abstractGetter();
}
