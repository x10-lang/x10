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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.*;

import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.ast.X10ClassDecl;
import x10.errors.Errors;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ParsedClassType_c;


/**
 * A <code>ClassDecl</code> is the definition of a class, abstract class,
 * or interface. It may be a public or other top-level class, or an inner
 * named class, or an anonymous class.
 */
public abstract class ClassDecl_c extends Term_c implements ClassDecl
{
    public ClassDecl_c(Position pos, FlagsNode flags, Id name,
            TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
        super(pos);
        // superClass may be null, interfaces may be empty
    }

    public abstract X10ClassDef classDef();

    public abstract X10ClassDecl classDef(X10ClassDef type);

    public abstract X10ClassDecl flags(FlagsNode flags);

    public abstract X10ClassDecl name(Id name);

    public abstract X10ClassDecl superClass(TypeNode superClass);

    public abstract X10ClassDecl interfaces(List<TypeNode> interfaces);

    public abstract X10ClassDecl body(ClassBody body);

    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public abstract Term firstChild();

    /**
     * Visit this term in evaluation order.
     */
    public abstract <S> List<S> acceptCFG(CFGBuilder v, List<S> succs);
    
    public abstract Node visitSignature(NodeVisitor v);
    
    public abstract ClassDecl_c preBuildTypes(TypeBuilder tb);
    
    public abstract ClassDecl_c postBuildTypes(TypeBuilder tb);

    /*public Context enterChildScope(Node child, Context c) {
        if (child == this.body) {
            TypeSystem ts = c.typeSystem();
            c = c.pushClass(type, type.asType());
        }
        else if (child == this.superClass || this.interfaces.contains(child)) {
            // Add this class to the context, but don't push a class scope.
            // This allows us to detect loops in the inheritance
            // hierarchy, but avoids an infinite loop.
            c = c.pushBlock();
            c.addNamed(this.type.asType());
        }
        return super.enterChildScope(child, c);
    }*/

//    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
//        if (type == null) {
//            throw new InternalCompilerError("Missing type.", position());
//        }
//
//        checkSupertypeCycles(ar.typeSystem());
//
//        ClassDef type = classDef();
//
//        // Make sure that the inStaticContext flag of the class is correct.
//        Context ctxt = ar.context();
//        type.inStaticContext(ctxt.inStaticContext());
//
//        return this;
//    }

    protected abstract void checkSupertypeCycles(TypeSystem ts) throws SemanticException;

    protected abstract void setSuperClass(TypeSystem ts, ClassDef thisType);

    protected abstract void setInterfaces(TypeSystem ts, ClassDef thisType);

    protected abstract ConstructorDecl createDefaultConstructor(ClassDef thisType, TypeSystem ts, NodeFactory nf);
    
    public abstract Node typeCheckOverride(Node parent, ContextVisitor tc);
    
    public abstract Node conformanceCheck(ContextVisitor tc);

    /**
     * Returns true if the given type is valid and we should report errors on it.
     */
    protected abstract boolean isValidType(Type type);


    public abstract String toString();

    public abstract void prettyPrintHeader(CodeWriter w, PrettyPrinter tr);
}
