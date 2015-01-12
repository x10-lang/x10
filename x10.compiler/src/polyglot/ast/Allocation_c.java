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

import java.util.List;

import polyglot.ast.Expr_c;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

/**
 * @author Bowen Alpern
 *
 */
public class Allocation_c extends Expr_c implements Allocation {
    
    TypeNode objectType;
    List<TypeNode> typeArguments;

    /**
     * @param pos
     */
    public Allocation_c(Position pos, TypeNode objType, List<TypeNode> typeArgs) {
        super(pos);
        objectType = objType;
        typeArguments = typeArgs;
    }

    /* (non-Javadoc)
     * @see x10.ast.X10Allocation#typeArguments()
     */
    public List<TypeNode> typeArguments() {
        return typeArguments;
    }

    /* (non-Javadoc)
     * @see x10.ast.X10Allocation#typeArguments(java.util.List)
     */
    public Allocation typeArguments(List<TypeNode> typeArgs) {
        if (typeArgs == typeArguments) return this;
        Allocation_c c  = (Allocation_c) copy();
        c.typeArguments = typeArgs;
        return c;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Allocation#objectType()
     */
    public TypeNode objectType() {
        return objectType;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Allocation#objectType(polyglot.ast.TypeNode)
     */
    public Allocation objectType(TypeNode objType) {
        if (objType == objectType) return this;
        Allocation_c a = (Allocation_c) copy();
        a.objectType   = objType;
        return a;
    }

    /**
     * @param objType
     * @param typeArgs
     * @return
     */
    private Node reconstruct(TypeNode objType, List<TypeNode> typeArgs) {
        if (objType == objectType && typeArgs == typeArguments) return this;
        Allocation_c a  = (Allocation_c) copy();
        a.objectType    = objType;
        a.typeArguments = typeArgs;
        return a;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Term_c#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     */
    @Override
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        return succs;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Node_c#visitChildren(polyglot.visit.NodeVisitor)
     */
    @Override
    public Node visitChildren(NodeVisitor v) {
        TypeNode objType = (TypeNode) visitChild(objectType, v);
        List<TypeNode> typeArgs = visitList(typeArguments, v);
        return reconstruct(objType, typeArgs);
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Term#firstChild()
     */
    public Term firstChild() {
        return null;
    }

    public String toString() {
        return "$allocate$[" + objectType +"]()";
    }
    
    /** Write the call to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("$allocate$[");
        print(objectType, w, tr);
        w.write("]()");
    }

}
