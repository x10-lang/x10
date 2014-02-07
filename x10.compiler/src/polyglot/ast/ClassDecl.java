/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.ClassDef;
import polyglot.types.Flags;
import x10.ast.DepParameterExpr;
import x10.ast.PropertyDecl;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.types.X10ClassDef;

/**
 * A <code>ClassDecl</code> represents a top-level, member, or local class
 * declaration.
 */
public interface ClassDecl extends Term, TopLevelDecl, ClassMember
{
	DepParameterExpr classInvariant();
	X10ClassDecl classInvariant(DepParameterExpr classInvariant);
	
	List<TypeParamNode> typeParameters();
	X10ClassDecl typeParameters(List<TypeParamNode> typeParameters);
	
	List<PropertyDecl> properties();
	X10ClassDecl properties(List<PropertyDecl> ps);
	
    /**
     * The type of the class declaration.
     */
    X10ClassDef classDef();

    /**
     * Set the type of the class declaration.
     */
    X10ClassDecl classDef(X10ClassDef type);

    /**
     * The class declaration's flags.
     */
    FlagsNode flags();

    /**
     * Set the class declaration's flags.
     */
    X10ClassDecl flags(FlagsNode flags);

    /**
     * The class declaration's name.
     */
    Id name();
    
    /**
     * Set the class declaration's name.
     */
    X10ClassDecl name(Id name);

    /**
     * The class's super class.
     */
    TypeNode superClass();

    /**
     * Set the class's super class.
     */
    X10ClassDecl superClass(TypeNode superClass);

    /**
     * The class's interface list.
     * @return A list of {@link polyglot.ast.TypeNode TypeNode}.
     */
    List<TypeNode> interfaces();

    /**
     * Set the class's interface list.
     * @param interfaces A list of {@link polyglot.ast.TypeNode TypeNode}.
     */
    X10ClassDecl interfaces(List<TypeNode> interfaces);

    /**
     * The class's body.
     */
    ClassBody body();

    /**
     * Set the class's body.
     */
    X10ClassDecl body(ClassBody body);
}
