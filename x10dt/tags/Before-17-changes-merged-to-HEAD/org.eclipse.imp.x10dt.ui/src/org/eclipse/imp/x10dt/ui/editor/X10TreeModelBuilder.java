/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.x10dt.ui.editor;

import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.services.base.TreeModelBuilderBase;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TopLevelDecl;
import polyglot.ext.x10.ast.ArrayConstructor;
import polyglot.ext.x10.ast.ArrayConstructor_c;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.util.Position;
import polyglot.visit.HaltingVisitor;
import polyglot.visit.NodeVisitor;

public class X10TreeModelBuilder extends TreeModelBuilderBase {
    private static final int PACKAGE_CATEGORY= 0;
    private static final int TYPE_CATEGORY= 1;
    private static final int FIELD_CATEGORY= 2;
    private static final int METHOD_CATEGORY= 3;
    private static final int STATEMENT_CATEGORY= ModelTreeNode.DEFAULT_CATEGORY;

    @Override
    protected void visitTree(Object root) {
    	if (root == null) return;
        Node node= (Node) root;
        if (root == null)
            return;
        node.visit(new HaltingVisitor() {
            public NodeVisitor enter(Node n) {
                if (n instanceof PackageNode) {
                    createSubItem(n, PACKAGE_CATEGORY);
                } else if (n instanceof TopLevelDecl) {
                    pushSubItem(n, TYPE_CATEGORY);
                } else if (n instanceof ClassDecl) {
                    pushSubItem(n, TYPE_CATEGORY);
                } else if (n instanceof ProcedureDecl) {
                    if (((ProcedureDecl) n).position() != Position.COMPILER_GENERATED) {
                        pushSubItem(n, METHOD_CATEGORY);
                    }
                } else if (n instanceof FieldDecl) {
                    createSubItem(n, FIELD_CATEGORY);
                } else if (n instanceof Async || n instanceof AtEach || n instanceof ForEach ||
                           n instanceof Future || n instanceof Finish || n instanceof Atomic ||
                           n instanceof Next) {
                    pushSubItem(n, STATEMENT_CATEGORY);
                } else if (n instanceof X10Loop) {
                    pushSubItem(n, STATEMENT_CATEGORY);
                } else if (n instanceof ArrayConstructor) {
                    ArrayConstructor cons = (ArrayConstructor) n;
                    if (cons.initializer() != null) {
                        pushSubItem(n, STATEMENT_CATEGORY);
                    }
                } else if (n instanceof Expr) {
                    return bypassChildren(n);
                } else if (n instanceof Call) {
                    Call call = (Call) n;
                    if (call.name().equals("force") && call.arguments().size() == 0) {
                        createSubItem(n, STATEMENT_CATEGORY);
                    }
                }
                return this;
            }
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof TopLevelDecl ||
                    n instanceof ClassDecl ||
                    (n instanceof ProcedureDecl && ((ProcedureDecl) old).position() != Position.COMPILER_GENERATED) ||
                    n instanceof Async || n instanceof AtEach || n instanceof ForEach ||
                    n instanceof Future || n instanceof Finish || n instanceof Atomic ||
                    n instanceof Next || n instanceof X10Loop ||
                    (n instanceof ArrayConstructor && ((ArrayConstructor) old).initializer() != null))
                    popSubItem();
                return super.leave(old, n, v);
            }
        });
    }
}
