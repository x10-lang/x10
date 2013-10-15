/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.visit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 *                                Dead Variable Eliminator
 * 
 * Purpose: to remove useless variable declarations.
 * 
 * A local variable declaration is said to be:
 *   "dead" -- if the variable is never used and its initializer is clearly side effect free,
 *   "weak" -- if the variable is never used but its initializer might have side effects, and
 *   "lame" -- if the variable is used exactly once as the initializer of another variable.
 * 
 * Dead variable declarations are removed (replaced by empty statements).  Weak variable 
 * declarations are replaced by the evaluation of the initializer.
 * 
 * Possible improvement: currently "uses" of a variable on the left hand side of an ordinary
 * (non updating) assignment are not distinguished from actual uses.  This distinction might be
 * exploited but it might be better to put the AST in SSA form before dead variable elimination.
 * 
 * @author Bowen Alpern
 *
 */
public class DeadVariableEliminator extends ContextVisitor {

    private static final boolean DEBUG = false;
//  private static final boolean DEBUG = true;

    private static final boolean VERBOSE = false;
//  private static final boolean VERBOSE = true;

    /**
     * State of the dead variable eliminator
     */

    private TypeSystem xts;
    private NodeFactory xnf;
    private SideEffectDetector sed;

    private Node root;
    private VariableState state;

    public DeadVariableEliminator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        xnf = nf;
        state = new VariableState();
    }

    @Override
    public NodeVisitor begin() {
        sed = new SideEffectDetector(job, ts, nf);
        sed = (SideEffectDetector) sed.begin();
        return super.begin();
    }

    /*
    // unused code
    @Override
    public DeadVariableEliminator copy() {
        DeadVariableEliminator copy = (DeadVariableEliminator) super.copy();
        copy.state = state;
        copy.root  = root;
        return copy;
    }
    */

    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#override(polyglot.ast.Node)
     */
    @Override
    public Node override(Node n) {
        if (ExpressionFlattener.cannotFlatten(n, job)) {
            return n;
        }
        return super.override(n);
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#enterCall(polyglot.ast.Node, polyglot.ast.Node)
     */
    @Override
    protected NodeVisitor enterCall(Node parent, Node n)throws SemanticException {
        if (null == parent) { // n is a root AST
            debug("DeadVariableEliminator entering root " +n, n);
            root = n; // DEBUG
            state.clear();
        }
        if (n instanceof LocalDecl) {
            state.addDecl((LocalDecl) n);
        } else if (n instanceof Formal) {
            state.ignoreFormal((Formal) n);
        } else if (n instanceof Local) {
            state.recordUse((Local) n);
        } else if (n instanceof LocalAssign && ((LocalAssign) n).operator() == Assign.ASSIGN) {
 //         state.recodeStore((LocalAssign) n);  // TODO: handle dead assignments
        }
        return super.enterCall(parent, n);
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
        if (null != parent) return n; // Wait for the root of the ast
        assert(((DeadVariableEliminator) v).root == old);
        Map<Node, Node> substitutions = state.makeReplacementMap();
        if (!substitutions.isEmpty()) {
            n = n.visit(new SubstitutionVisitor(substitutions));
                assert substitutions.isEmpty();
  //        n = n.visit(new ConstantPropagator(job, ts, nf).context(context()));
        }
        debug("DeadVariableEliminator leaving " + n, n);
        return n;
    }

    private final class VariableState {
        private final Set<LocalDef>                   formals  = CollectionFactory.newHashSet();                   // defs that are formal params
        private final Map<LocalDef, LocalDecl>        declMap  = CollectionFactory.newHashMap();        // the declaration of a def
        private final Map<LocalDef, Set<Local>>       useMap   = CollectionFactory.newHashMap();       // the uses of a def
 //     private final Map<LocalDef, Set<LocalAssign>> storeMap = CollectionFactory.newHashMap(); // the pure stores of a def

        /**
         * 
         */
        private void clear() {
            formals.clear();
            declMap.clear();
            useMap.clear();
  //        storeMap.clear();
        }

        /**
         * @param decl
         */
        private void addDecl(LocalDecl decl) {
            LocalDef def = decl.localDef();
            declMap.put(def, decl);
            if (null == useMap.get(def)) {
                useMap.put(def, CollectionFactory.<Local>newHashSet());
            }/*
            if (null == storeMap.get(def)) {
                storeMap.put(def, CollectionFactory.<LocalAssign>newHashSet());
            }*/
        }

        /**
         * @param n
         */
        private void ignoreFormal(Formal n) {
            formals.add(n.localDef());
        }
        
        /**
         * @param local
         */
        private void recordUse(Local local) {
            LocalDef def = local.localInstance().def();
            if (formals.contains(def)) return;
            Set<Local> uses = useMap.get(def);
            if (null == uses) {
                uses = CollectionFactory.newHashSet();
                useMap.put(def, uses);
            }
            uses.add(local);
        }

        /**
         * @param assign
         *//*
        public void recodeStore(LocalAssign assign) {
            LocalDef def = assign.local().localInstance().def();
            Set<LocalAssign> stores = storeMap.get(def);
            if (null == stores) {
                stores = CollectionFactory.newHashSet();
                storeMap.put(def, stores);
            }
            stores.add(assign);
        }*/

        public Map<Node, Node> makeReplacementMap() {
            Set<LocalDecl> dead = CollectionFactory.newHashSet();
            for (LocalDef def : useMap.keySet()) {
                if (isDead(def))
                    dead.add(declMap.get(def));
            }
            Map<Node, Node> map = CollectionFactory.newHashMap();
            while (!dead.isEmpty()) {
                LocalDecl decl = removeDecl(dead);
                Expr init = decl.init();
                if (sed.hasSideEffects(init)) {
                    map.put(decl, xnf.Eval(decl.position(), init));
                } else {
                    map.put(decl, xnf.Empty(decl.position()));
                    dead.addAll(ignoreUses(init));
                } /* TODO handle dead assignments
                for (LocalAssign assign : storeMap.get(decl.localDef())) {
                    map.put(assign, assign.right().type(assign.type()));
                } */
            }
            
            return map;
        }

        /**
         * @param def
         * @return
         */
        private boolean isDead(LocalDef def) {
            if (useMap.containsKey(def)) {
                Set<Local> uses = useMap.get(def);
                if (uses.isEmpty()) return true;
            }
  //        if (true) 
                return false; // TODO: remove dead LocalAssign's
  //        Set<LocalAssign> stores = storeMap.get(def);
  //        return uses.size() == stores.size();
        }

        /**
         * @param set
         * @return
         */
        private LocalDecl removeDecl(Set<LocalDecl> set) {
            if (set.isEmpty()) return null;
            LocalDecl decl = set.iterator().next();
            set.remove(decl);
            return decl;
        }

        /**
         * @param init
         * @return
         */
        private Set<LocalDecl> ignoreUses(Expr init) {
            Set<LocalDecl> dead = CollectionFactory.newHashSet();
            for (Local use : uses(init)) {
                LocalDef def = use.localInstance().def();
                Set<Local> uses = useMap.get(def);
                if (null != uses)
                    uses.remove(use);
                if (isDead(def)) {
                    dead.add(declMap.get(def));
                }
            }
            return dead;
        }

        /**
         * 
         * @param expr
         * @return
         */
        private Set<Local> uses(Expr expr) {
            final Set<Local> result = CollectionFactory.newHashSet();
            if (null == expr)
                return result;
            expr.visit(new NodeVisitor() {
                /* (non-Javadoc)
                 * @see polyglot.visit.NodeVisitor#override(polyglot.ast.Node)
                 */
                @Override
                public Node override(Node n) {
                    if (n instanceof Local & !formals.contains(n)) 
                        result.add((Local) n);
                    return null;
                }
            });
            return result;
        }

    }

    private final class SubstitutionVisitor extends NodeVisitor {
        private final Map<Node, Node> subst;

        /**
         * @param substitutions
         */
        public SubstitutionVisitor(Map<Node, Node> substitutions) {
            super();
            this.subst = substitutions;
        }

        /* (non-Javadoc)
         * @see polyglot.visit.NodeVisitor#leave(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
         */
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (old instanceof Stmt && !(n instanceof Stmt)) {
                Position pos = n.position();
                Expr e = (Expr) n;
                if (!sed.hasSideEffects(e)) {
                    debug("DVE eliminating side-effect free expr: " +n, n);
                    n = xnf.Empty(pos);
                } else if (e instanceof ProcedureCall || e instanceof Assign || e instanceof Unary) {
                    n = xnf.Eval(pos, e);
                } else if (!e.type().typeEquals(xts.Void(), context())) {
                    FlagsNode fn = xnf.FlagsNode(pos, Flags.FINAL);
                    TypeNode tn  = xnf.X10CanonicalTypeNode(pos, e.type());
                    Id id        = xnf.Id(pos, Name.makeFresh("dummy"));
                    n            = xnf.LocalDecl(pos, fn, tn, id, e);
                } else {
                    String msg = "Error: unexpected void expr: " +e+ " replacing stmt: " +old+ " at " +e.position();
                    System.err.println(msg);
                    throw new InternalCompilerError(msg);
                }
            }
            if (n instanceof LocalDecl || n instanceof LocalAssign) {
                Node s = subst.remove(old);
                if (null != s) { 
                    verbose("DVE replacing " +old+ " by " +s, n);
                    if (n instanceof LocalDecl) { // TODO: handle LocalAssign's (perhaps by replacing every "expr" by "Type dummy = expr" before Java back end
                        n = s;
                    } else { // TODO: handle n instanceof LocalAssign 
                        debug("DVE NOT replacing " +old+ " by " +s, n);
                        if (false) n = s;
                    }
                }
            }
            return n;
        }
    }

    private static void verbose(String msg, Node node) {
        if (!VERBOSE) return;
        try {
            Thread.sleep(10);
            System.out.print("  ");
            if (null != node)
                System.out.print(node.position() + ":  ");
            System.out.println(msg);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore exception (we are just trying to avoid stepping on writes to STDERR
        }
    }

    private static void debug(String msg, Node node) {
        if (!DEBUG) return;
        try {
            Thread.sleep(10);
            System.err.print("  DEBUG ");
            if (null != node)
                System.err.print(node.position() + ":  ");
            System.err.println(msg);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore exception (we are just trying to avoid stepping on writes to STDOUT
        }
    }

}
