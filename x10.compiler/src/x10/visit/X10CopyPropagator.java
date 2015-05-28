/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.visit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Catch;
import polyglot.ast.Do;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.If;
import polyglot.ast.Labeled;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Unary;
import polyglot.ast.While;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.LocalDef;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.DataFlow;
import polyglot.visit.FlowGraph;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Closure;
import x10.util.CollectionFactory;

/**
 * Visitor which performs copy propagation. Copied over from
 * polyglot.ast.CopyPropagator.
 */
public class X10CopyPropagator extends DataFlow {

    public X10CopyPropagator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf, true /* forward analysis */, true /* perform dataflow on entry to CodeDecls */);
    }

    protected class DataFlowItem extends Item {
        // Map of LocalInstance -> CopyInfo. The CopyInfo nodes form a forest
        // to represent copy information.
        private Map<LocalDef, CopyInfo> map;

        /**
         * Constructor for creating an empty set.
         */
        protected DataFlowItem() {
            this.map = CollectionFactory.newHashMap();
        }

        /**
         * Deep copy constructor.
         */
        protected DataFlowItem(DataFlowItem dfi) {
            map = CollectionFactory.newHashMap(dfi.map.size());
            for (Map.Entry<LocalDef, CopyInfo> e : dfi.map.entrySet()) {
                LocalDef li = (LocalDef) e.getKey();
                CopyInfo ci = (CopyInfo) e.getValue();
                if (ci.from != null) add(ci.from.li, li);
            }
        }

        protected class CopyInfo {
            final public LocalDef li; // Local instance this node pertains to.
            public CopyInfo from; // In edge.
            public Set<CopyInfo> to; // Out edges.
            public CopyInfo root; // Root CopyInfo node for this tree.

            protected CopyInfo(LocalDef li) {
                if (li == null) {
                    throw new InternalCompilerError("Null local instance " + "encountered during copy propagation.");
                }

                this.li = li;
                this.from = null;
                this.to = CollectionFactory.newHashSet();
                this.root = this;
            }

            protected void setRoot(CopyInfo root) {
                List<CopyInfo> worklist = new ArrayList<CopyInfo>();
                worklist.add(this);
                while (worklist.size() > 0) {
                    CopyInfo ci = (CopyInfo) worklist.remove(worklist.size() - 1);
                    worklist.addAll(ci.to);
                    ci.root = root;
                }
            }

            public boolean equals(Object o) {
                if (!(o instanceof CopyInfo)) return false;
                CopyInfo ci = (CopyInfo) o;

                // Assume both are in consistent data structures, so only check
                // up pointers. Also check root pointers because we can.
                return li == ci.li && (from == null ? ci.from == null : (ci.from != null && from.li == ci.from.li))
                        && root.li == ci.root.li;
            }

            public int hashCode() {
                return li.hashCode() + 31 * (from == null ? 0 : from.li.hashCode() + 31 * root.li.hashCode());
            }
        }

        protected void add(LocalDef from, LocalDef to) {
            // Get the 'to' node.
            boolean newTo = !map.containsKey(to);
            CopyInfo ciTo;
            if (newTo) {
                ciTo = new CopyInfo(to);
                map.put(to, ciTo);
            } else {
                ciTo = (CopyInfo) map.get(to);
            }

            // Get the 'from' node.
            CopyInfo ciFrom;
            if (map.containsKey(from)) {
                ciFrom = (CopyInfo) map.get(from);
            } else {
                ciFrom = new CopyInfo(from);
                map.put(from, ciFrom);
                ciFrom.root = ciFrom;
            }

            // Make sure ciTo doesn't already have a 'from' node.
            if (ciTo.from != null) {
                throw new InternalCompilerError("Error while copying dataflow " + "item during copy propagation.");
            }

            // Link up.
            ciFrom.to.add(ciTo);
            ciTo.from = ciFrom;

            // Consistency fix-up.
            if (newTo) {
                ciTo.root = ciFrom.root;
            } else {
                ciTo.setRoot(ciFrom.root);
            }
        }

        protected void intersect(DataFlowItem dfi) {
            boolean modified = false;

            for (Iterator<Map.Entry<LocalDef, CopyInfo>> it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry<LocalDef, CopyInfo> e = it.next();
                LocalDef li = (LocalDef) e.getKey();
                CopyInfo ci = (CopyInfo) e.getValue();

                if (!dfi.map.containsKey(li)) {
                    modified = true;

                    it.remove();

                    // Surgery. Bypass and remove the node. We'll fix
                    // consistency later.
                    if (ci.from != null) ci.from.to.remove(ci);
                    for (CopyInfo toCI : ci.to) {
                        toCI.from = null;
                    }

                    continue;
                }

                if (ci.from == null) continue;

                // Other DFI contains this key.
                // Make sure that ci and ci.from are also in the same tree in
                // the other DFI. If not, break the link in the intersection
                // result.
                CopyInfo otherCI = (CopyInfo) dfi.map.get(li);
                CopyInfo otherCIfrom = (CopyInfo) dfi.map.get(ci.from.li);

                if (otherCIfrom == null || otherCI.root != otherCIfrom.root) {
                    modified = true;

                    // Remove the uplink.
                    ci.from.to.remove(ci);
                    ci.from = null;
                }
            }

            if (!modified) return;

            // Fix consistency.
            for (Iterator<Map.Entry<LocalDef, CopyInfo>> it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry<LocalDef, CopyInfo> e = it.next();
                CopyInfo ci = (CopyInfo) e.getValue();

                // Only work on roots.
                if (ci.from != null) continue;

                // Cut out singleton nodes.
                if (ci.to.isEmpty()) {
                    it.remove();
                    continue;
                }

                // Fix root.
                ci.setRoot(ci);
            }
        }

        public void kill(LocalDef var) {
            if (!map.containsKey(var)) return;

            CopyInfo ci = (CopyInfo) map.get(var);
            map.remove(var);

            // Splice out 'ci' and fix consistency.
            if (ci.from != null) ci.from.to.remove(ci);
            for (CopyInfo toCI : ci.to) {
                toCI.from = ci.from;
                if (ci.from == null) {
                    toCI.setRoot(toCI);
                } else {
                    ci.from.to.add(toCI);
                }
            }
        }
        
        public void killall() {
            for (LocalDef var : map.keySet()) {
                kill(var);
            }
        }

        public LocalDef getRoot(LocalDef var) {
            if (!map.containsKey(var)) return null;
            return ((CopyInfo) map.get(var)).root.li;
        }

        private void die() {
            throw new InternalCompilerError("Copy propagation dataflow item " + "consistency error.");
        }

        private void consistencyCheck() {
            for (Map.Entry<LocalDef, CopyInfo> e : map.entrySet()) {
                LocalDef li = (LocalDef) e.getKey();
                CopyInfo ci = (CopyInfo) e.getValue();

                if (li != ci.li) die();
                if (!map.containsKey(ci.root.li)) die();
                if (map.get(ci.root.li) != ci.root) die();

                if (ci.from == null) {
                    if (ci.root != ci) die();
                } else {
                    if (!map.containsKey(ci.from.li)) die();
                    if (map.get(ci.from.li) != ci.from) die();
                    if (ci.from.root != ci.root) die();
                    if (!ci.from.to.contains(ci)) die();
                }

                for (CopyInfo toCI : ci.to) {
                    if (!map.containsKey(toCI.li)) die();
                    if (map.get(toCI.li) != toCI) die();
                    if (toCI.root != ci.root) die();
                    if (toCI.from != ci) die();
                }
            }
        }

        public int hashCode() {
            int result = 1;
            for (Map.Entry<LocalDef, CopyInfo> e : map.entrySet()) {
                result = 31 * result + e.getKey().hashCode();
                result = 31 * result + e.getValue().hashCode();
            }

            return result;
        }

        public boolean equals(Object o) {
            if (!(o instanceof DataFlowItem)) return false;

            DataFlowItem dfi = (DataFlowItem) o;
            return map.equals(dfi.map);
        }

        public String toString() {
            String result = "";
            boolean first = true;

            for (CopyInfo ci : map.values()) {
                if (ci.from != null) {
                    if (!first) result += ", ";
                    if (ci.root != ci.from) result += ci.root.li + " ->* ";
                    result += ci.from.li + " -> " + ci.li;
                    first = false;
                }
            }
            return "[" + result + "]";
        }
    }

    public Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
        return new DataFlowItem();
    }

    public Item confluence(List<Item> inItems, Term node, boolean entry, FlowGraph graph) {
        DataFlowItem result = null;
        for (Iterator<Item> it = inItems.iterator(); it.hasNext();) {
            DataFlowItem inItem = (DataFlowItem) it.next();
            if (result == null) {
                result = new DataFlowItem(inItem);
            } else {
                result.intersect(inItem);
            }
        }

        return result;
    }

    private void killDecl(DataFlowItem dfi, Stmt stmt) {
        if (stmt instanceof LocalDecl) {
            dfi.kill(((LocalDecl) stmt).localDef());
        }
    }

    protected DataFlowItem flow(Item in, FlowGraph graph, Term t, boolean entry) {
        DataFlowItem result = new DataFlowItem((DataFlowItem) in);
        if (t instanceof Async) {
            // Kill all variables
            result.killall();
        } else if (t instanceof AtStmt) {
            // Kill all variables
            result.killall();
        } else if (t instanceof Closure) {
            // Kill all variables
            result.killall();
        }
        if (entry) {
            return result;
        }
        if (t instanceof LocalAssign) {
            LocalAssign n = (LocalAssign) t;
            Assign.Operator op = n.operator();
            Expr left = n.local();
            Expr right = n.right();

            if (left instanceof Local) {
                LocalDef to = ((Local) left).localInstance().def();
                result.kill(to);

                if (right instanceof Local && op == Assign.ASSIGN) {
                    LocalDef from = ((Local) right).localInstance().def();
                    result.add(from, to);
                }
            }
        } else if (t instanceof Unary) {
            Unary n = (Unary) t;
            Unary.Operator op = n.operator();
            Expr expr = n.expr();

            if (expr instanceof Local
                    && (op == Unary.POST_INC || op == Unary.POST_DEC || op == Unary.PRE_INC || op == Unary.PRE_DEC)) {

                result.kill(((Local) expr).localInstance().def());
            }
        } else if (t instanceof LocalDecl) {
            LocalDecl n = (LocalDecl) t;

            LocalDef to = n.localDef();
            result.kill(to);

            // It's a copy if we're initializing a non-final local declaration
            // with a value from a local variable. We only care about
            // non-final local declarations because final locals have special
            // use in local classes.
            if (n.init() instanceof Local) {
                LocalDef from = ((Local) n.init()).localInstance().def();
                result.add(from, to);
            }
        } else if (t instanceof Eval_c) {
            Expr exp = ((Eval_c) t).expr();
            if (exp instanceof LocalAssign) {
                LocalAssign n = (LocalAssign) exp;
                Assign.Operator op = n.operator();
                Expr left = n.local();
                Expr right = n.right();
                if (left instanceof Local) {
                    LocalDef to = ((Local) left).localInstance().def();
                    result.kill(to);
                }
            }
        } else if (t instanceof Labeled) {
            Stmt l = ((Labeled) t).statement();
            flow(result, graph, l, entry);
        } else if (t instanceof Block) {
            // Kill locals that were declared in the block.
            Block n = (Block) t;
            // System.out.println(t+"\n");
            for (Stmt s : n.statements()) {
                if (s instanceof Labeled) {
                    flow(result, graph, s, entry);
                }
                killDecl(result, s);
            }
        } else if (t instanceof Loop) {
            if (t instanceof For) {
                // Kill locals that were declared in the initializers.
                For n = (For) t;
                for (Stmt init : n.inits()) {
                    killDecl(result, init);
                }
                killDecl(result, ((Loop) t).body());
            }
            // Kill locals that were declared in the body.
            else if (t instanceof While || t instanceof Do) {
                Stmt m = ((Loop) t).body();
                List<Stmt> st = null;
                if (m instanceof Eval_c)
                    flow(result, graph, m, entry);
                else if (m instanceof Block_c) {
                    st = ((Block_c) m).statements();
                    for (Stmt s : st) {
                        if (s instanceof Labeled || s instanceof Loop || s instanceof Eval_c)
                            flow(result, graph, s, entry);
                        else
                            killDecl(result, s);
                    }
                }
            }
        } else if (t instanceof Catch) {
            // Kill catch's formal.
            result.kill(((Catch) t).formal().localDef());
        } else if (t instanceof If) {
            // Kill locals declared in consequent and alternative.
            If n = (If) t;
            killDecl(result, n.consequent());
            killDecl(result, n.alternative());
        }
        // System.out.println("RESULT: "+ result);
        return result;
    }

    public Map<EdgeKey, Item> flow(Item in, FlowGraph graph, Term t, boolean entry, Set<EdgeKey> succEdgeKeys) {
        return itemToMap(flow(in, graph, t, entry), succEdgeKeys);
    }

    public void post(FlowGraph graph, Term root) {
        // No need to do any checking.
        if (reporter.should_report(Reporter.cfg, 2)) {
            dumpFlowGraph(graph, root);
        }
    }

    public void check(FlowGraph graph, Term n, boolean entry, Item inItem, Map<EdgeKey, Item> outItems) {

        throw new InternalCompilerError("CopyPropagator.check should never be " + "called.");
    }

    @Override
    public NodeVisitor begin() {
        return this;
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        if (n instanceof Local) {
            FlowGraph g = currentFlowGraph();

            if (g == null) return n;

            Local l = (Local) n;
            Collection<FlowGraph.Peer> peers = g.peers(l, Term.EXIT);
            if (peers == null || peers.isEmpty()) return n;

            List<Item> items = new ArrayList<Item>();
            for (FlowGraph.Peer p : peers) {
                if (p.inItem() != null) items.add(p.inItem());
            }

            DataFlowItem in = (DataFlowItem) confluence(items, l, false, g);
            if (in == null) return n;

            LocalDef li = l.localInstance().def();
            LocalDef root = in.getRoot(li);
            if (root == null || root == li) return n;
            return l.name(l.name().id(root.name())).localInstance(root.asInstance());
        }

        if (n instanceof LocalAssign) {
            LocalAssign oldAssign = (LocalAssign) old;
            LocalAssign newAssign = (LocalAssign) n;
            if (oldAssign.local() != newAssign.local()) {
                Node r = newAssign.local(oldAssign.local());
                return r;
            }
        }

        return super.leaveCall(old, n, v);
    }
}
