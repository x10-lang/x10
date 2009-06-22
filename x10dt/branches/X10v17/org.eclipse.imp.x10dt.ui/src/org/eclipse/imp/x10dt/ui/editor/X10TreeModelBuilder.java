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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

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
//import polyglot.ext.x10.ast.ArrayConstructor;          //PORT1.7 --- ask bob, lots here but probably ok initially
//import polyglot.ext.x10.ast.ArrayConstructor_c;        //PORT1.7 remove?
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.util.Position;
//import polyglot.visit.HaltingVisitor;//PORT1.7 remove
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
        node.visit(new HaltingVisitor() {// will override override()  //PORT1.7 HaltingVisitor added below
        	@Override
            public NodeVisitor enter(Node n) {
                if (n instanceof PackageNode) {
                    createSubItem(n, PACKAGE_CATEGORY);   // BRT for things that have no children
                } else if (n instanceof TopLevelDecl) {
                    pushSubItem(n, TYPE_CATEGORY);        // BRT for things that do have children
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
//                } else if (n instanceof ArrayConstructor) {                  //PORT1.7 --- what to do with ArrayConstructor
//                    ArrayConstructor cons = (ArrayConstructor) n;
//                    if (cons.initializer() != null) {
//                        pushSubItem(n, STATEMENT_CATEGORY);
//                    }
                } else if (n instanceof Expr) {
                    return bypassChildren(n);
                } else if (n instanceof Call) {// PORT1.7 do something similar for ArrayConstructor replacement?
                    Call call = (Call) n;                                     // con't: then do something in leave() as well
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
                    n instanceof Next || n instanceof X10Loop  
                    //||(n instanceof ArrayConstructor && ((ArrayConstructor) old).initializer() != null)//PORT1.7 --- what to do for ArrayConstructor?
                    )
                    popSubItem();
                return super.leave(old, n, v);
            }
        });

    }
    public abstract class HaltingVisitor extends NodeVisitor
    {
        protected Node bypassParent;
        protected Collection bypass;

        public HaltingVisitor bypassChildren(Node n) {
            HaltingVisitor v = (HaltingVisitor) copy();
            v.bypassParent = n;
            return v;
        }

        public HaltingVisitor visitChildren() {
            HaltingVisitor v = (HaltingVisitor) copy();
            v.bypassParent = null;
            v.bypass = null;
            return v;
        }

        public HaltingVisitor bypass(Node n) {
            if (n == null) return this;

            HaltingVisitor v = (HaltingVisitor) copy();

            // FIXME: Using a collection is expensive, but is hopefully not
            // often used.
            if (this.bypass == null) {
                v.bypass = Collections.singleton(n);
            }
            else {
                v.bypass = new ArrayList(this.bypass.size()+1);
                v.bypass.addAll(bypass);
                v.bypass.add(n);
            }

            return v;
        }

        public HaltingVisitor bypass(Collection c) {
            if (c == null) return this;

            HaltingVisitor v = (HaltingVisitor) copy();

            // FIXME: Using a collection is expensive, but is hopefully not
            // often used.
            if (this.bypass == null) {
                v.bypass = new ArrayList(c);
            }
            else {
                v.bypass = new ArrayList(this.bypass.size()+c.size());
                v.bypass.addAll(bypass);
                v.bypass.addAll(c);
            }

            return v;
        }

        public Node override(Node parent, Node n) {
            if (bypassParent != null && bypassParent == parent) {
                // System.out.println("bypassing " + n +
                //                    " (child of " + parent + ")");
                return n;
            }

            if (bypass != null) {
                for (Iterator i = bypass.iterator(); i.hasNext(); ) {
                    if (i.next() == n) {
                        // System.out.println("bypassing " + n);
                        return n;
                    }
                }
            }

            return null;
        }
    }
}
