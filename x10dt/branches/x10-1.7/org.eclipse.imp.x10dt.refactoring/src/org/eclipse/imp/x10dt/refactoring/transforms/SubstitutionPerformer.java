/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package org.eclipse.imp.x10dt.refactoring.transforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.VarDecl;
import polyglot.types.LocalInstance;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;

/**
 * Produces an AST like the given tree T, but with a set S = { <v,e> } of var-to-expr
 * substitutions performed. That is, references to a variable v in keys(S) are
 * substituted by the corresponding expression e = S(v). Any free variables in e
 * must not be captured by bindings in T, or else an error results.<br>
 * For now, only substitutions of local variables and formal argument references are
 * permitted. Attempts to substitute for other types of nodes, e.g., class names,
 * will silently do nothing.
 */
public class SubstitutionPerformer {
    private class VarContext {
        public final VarContext fParent;
        public final Set<VarDecl> fVarDecls= new HashSet<VarDecl>();
        public VarContext() {
            this(null);
        }
        public VarContext(VarContext parent) {
            fParent= parent;
        }
        public boolean hasBinding(String name) {
            for(VarDecl d: fVarDecls) {
                if (d.name().equals(name)) {
                    return true;
                }
            }
            if (fParent != null) {
                return fParent.hasBinding(name);
            }
            return false;
        }
    }

    private abstract class ContextVisitor extends NodeVisitor {
        protected VarContext fVarContext= new VarContext();
        
        @Override
        public NodeVisitor enter(Node n) {
            if (n instanceof Block) {
                pushContext();
            } else if (n instanceof LocalDecl) {
                addVarDecl((LocalDecl) n);
            }
            return super.enter(n);
        }
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Block) {
                popContext();
            }
            return super.leave(old, n, v);
        }
        protected void pushContext() {
            fVarContext= new VarContext(fVarContext);
        }
        protected void popContext() {
            if (fVarContext != null) {
                fVarContext= fVarContext.fParent;
            }
        }
        protected void addVarDecl(VarDecl vd) {
            if (fVarContext == null) {
                throw new IllegalStateException("Declaring a var without a context???");
            }
            fVarContext.fVarDecls.add(vd);
        }
    }

    private class FreeVarsVisitor extends ContextVisitor {
        private final VarInstance<VarDef> fVar;

        public FreeVarsVisitor(VarInstance<VarDef> v) {
            fVar= v;
            fFreeVarMap.put(v, new HashSet<Local>());
        }
        @Override
        public NodeVisitor enter(Node n) {
            if (n instanceof Local) {
                Local local= (Local) n;
                String localName= local.name().toString();

                if (!fVarContext.hasBinding(localName)) {
                    Set<Local> freeVars= fFreeVarMap.get(fVar);
                    freeVars.add(local);
                }
            }
            return super.enter(n);
        }
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            return super.leave(old, n, v);
        }
    }

    private class SubstitutionVisitor extends ContextVisitor {
        public SubstitutionVisitor() { }

        @Override
        public NodeVisitor enter(Node n) {
            if (n instanceof Local) {
                Local local= (Local) n;
                String localName= local.name().toString();

                if (fVarContext.hasBinding(localName)) {
                    // TODO Do something more sensible than just throwing an exception
                    // This info needs to get back to the user in consumable form
                    throw new IllegalStateException("Inlining failed: unintended name capture for " + localName);
                }
            } else if (n instanceof MethodDecl) {
                throw new IllegalStateException("Unable to inline code containing a method decl");
            }
            return super.enter(n);
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Local) {
                Local local= (Local) n;
                LocalInstance localInstance= local.localInstance();
                if (fSubs.containsKey(localInstance)) {
                    return fSubs.get(localInstance);
                }
                return n;
            }
            return super.leave(old, n, v);
        }
    }

    /**
     * The set of substitutions passed in from the client
     */
    private final Map<VarInstance<VarDef>, Node> fSubs;

    /**
     * This maps a Local (something to be substituted) to the set of free variables
     * in its substitution, so that a quick check can be performed to see whether any
     * of these free vars will be captured by a binding in the surrounding context.
     */
    private final Map<VarInstance<VarDef>,Set<Local>> fFreeVarMap= new HashMap<VarInstance<VarDef>,Set<Local>>();

    public SubstitutionPerformer(Map<VarInstance<VarDef>,Node> subs) {
        fSubs= subs;
    }

    private void collectFreeVars() {
        for(VarInstance<VarDef> v: fSubs.keySet()) {
            Expr e= (Expr) fSubs.get(v);
            e.visit(new FreeVarsVisitor(v));
        }
    }

    public Node perform(Node n, Node root) {
        collectFreeVars();

        Node result= n.visit(new SubstitutionVisitor());

        return result;
    }
}

