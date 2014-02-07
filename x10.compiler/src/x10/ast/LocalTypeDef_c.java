/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import polyglot.ast.Node;
import polyglot.ast.NodeList;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.types.Context;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

public class LocalTypeDef_c extends Stmt_c implements LocalTypeDef {
    TypeDecl typeDef;

    public LocalTypeDef_c(Position pos, TypeDecl typeDef) {
        super(pos);
        assert (typeDef != null);
        this.typeDef = typeDef;
    }

    public TypeDecl typeDef() {
        return typeDef;
    }

    public LocalTypeDef typeDef(TypeDecl typeDef) {
        LocalTypeDef_c n = (LocalTypeDef_c) copy();
        n.typeDef = typeDef;
        return n;
    }

    /** Reconstruct the statement. */
    protected LocalTypeDef_c reconstruct(TypeDecl typeDef) {
        if (typeDef != this.typeDef) {
            LocalTypeDef_c n = (LocalTypeDef_c) copy();
            n.typeDef = typeDef;
            return n;
        }

        return this;
    }

    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term firstChild() {
        return typeDef();
    }

    /**
     * Visit this term in evaluation order.
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(typeDef(), this, EXIT);
        return succs;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
        Node typeDef = visitChild(this.typeDef, v);
        if (typeDef instanceof NodeList) {
            // Return a NodeList of LocalClassDecls.
            NodeList nl = (NodeList) typeDef;
            List<Node> decls = new ArrayList<Node>(nl.nodes());
            for (ListIterator<Node> it = decls.listIterator(); it.hasNext();) {
                TypeDecl cd = (TypeDecl) it.next();
                it.set(reconstruct(cd));
            }
            return nl.nodes(decls);
        }

        return reconstruct((TypeDecl) typeDef);
    }

    public void addDecls(Context c) {
            // We should now be back in the scope of the enclosing block.
            // Add the type.
            c.addNamed(typeDef.typeDef().asType());
        }

    public String toString() {
        return typeDef.toString();
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        printBlock(typeDef, w, tr);
        w.write(";");
    }

}
