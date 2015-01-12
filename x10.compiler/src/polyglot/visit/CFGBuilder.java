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

package polyglot.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.main.Reporter;
import polyglot.types.*;
import polyglot.util.*;
import x10.ast.Async;

/**
 * Class used to construct a CFG.
 */
public class CFGBuilder implements Cloneable
{
    /** The flowgraph under construction. */
    protected FlowGraph graph;

    /** The type system. */
    protected TypeSystem ts;

    /** The reporter */
    protected Reporter reporter;
    
    /**
     * The outer CFGBuilder.  We create a new inner CFGBuilder when entering a
     * loop or try-block and when entering a finally block.
     */
    protected CFGBuilder outer;

    /**
     * The innermost loop or try-block in lexical scope.  We maintain a stack
     * of loops and try-blocks in order to add edges for break and continue
     * statements and for exception throws.  When such a jump is encountered we
     * traverse the stack, searching for the target of the jump.
     */
    protected Stmt innermostTarget;

    /**
     * List of terms on the path to the innermost finally block.  If we are
     * constructing a CFG for a finally block, this is the sequence of terms
     * that caused entry into this and lexically enclosing finally blocks.
     * We construct a unique subgraph for each such path. The list
     * is empty if this CFGBuilder is not constructing the CFG for a finally
     * block.
     */
    protected List<Term> path_to_finally;

    /** The data flow analysis for which we are constructing the graph. */
    protected DataFlow df;

    /**
     * True if we should skip the catch blocks for the innermost try when
     * building edges for an exception throw.
     */
    protected boolean skipInnermostCatches;
    
    /**
     * True if we should add edges for uncaught Errors to the exit node of the
     * graph.  By default, we do not, but subclasses can change this behavior
     * if needed.
     */
    protected boolean errorEdgesToExitNode;

    public CFGBuilder(TypeSystem ts, FlowGraph graph, DataFlow df) {
        this.ts = ts;
        this.reporter = ts.extensionInfo().getOptions().reporter;
        this.graph = graph;
        this.df = df;
        this.path_to_finally = Collections.<Term>emptyList();
        this.outer = null;
        this.innermostTarget = null;
        this.skipInnermostCatches = false;
        this.errorEdgesToExitNode = false;
    }

    public FlowGraph graph() { return graph; }
    public DataFlow dataflow() { return df; }
    public CFGBuilder outer() { return outer; }

    /** Get the type system. */
    public TypeSystem typeSystem() {
        return ts;
    }

    /** Copy the CFGBuilder. */
    private CFGBuilder shallowCopy() {
        try {
            return (CFGBuilder) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("Java clone() weirdness.");
        }
    }

    /**
     * Construct a new CFGBuilder with the a new innermost loop or
     * try-block <code>n</code>.
     */
    public CFGBuilder push(Stmt n) {
        return push(n, false);
    }

    /**
     * Construct a new CFGBuilder with the a new innermost loop or
     * try-block <code>n</code>, optionally skipping innermost catch blocks.
     */
    public CFGBuilder push(Stmt n, boolean skipInnermostCatches) {
        CFGBuilder v = shallowCopy();
        v.outer = this;
        v.innermostTarget = n;
        v.skipInnermostCatches = skipInnermostCatches;
        return v;
    }

    /**
     * Visit edges from a branch.  Simulate breaking/continuing out of
     * the loop, visiting any finally blocks encountered.
     */
    public void visitBranchTarget(Branch b) {
      Term last = b;
      CFGBuilder last_visitor = this;

      for (CFGBuilder v = this; v != null; v = v.outer) {
        Term c = v.innermostTarget;

        if (c instanceof Async) {
            throw new CFGBuildError("Cannot "+b.kind()+" in an async", b.position());
        }
          
        if (c instanceof Try) {
          Try tr = (Try) c;
          if (tr.finallyBlock() != null) {
            last_visitor = tryFinally(v, last, last_visitor, tr.finallyBlock());
            last = tr.finallyBlock();
          }
        }

        if (b.labelNode() != null) {
          if (c instanceof Labeled) {
            Labeled l = (Labeled) c;
            if (l.labelNode().id().equals(b.labelNode().id())) {
              if (b.kind() == Branch.BREAK) {
                edge(last_visitor, last, l, Term.EXIT, FlowGraph.EDGE_KEY_OTHER);
              }
              else {
                Stmt s = l.statement();
                if (s instanceof Loop) {
                  Loop loop = (Loop) s;
                  edge(last_visitor, last, loop.continueTarget(), Term.ENTRY, 
                          FlowGraph.EDGE_KEY_OTHER);
                }
                else {
                  throw new CFGBuildError("Target of continue or break statement must be a loop.", l.position());
                }
              }

              return;
            }
          }
        }
        else {
          if (c instanceof Loop) {
            Loop l = (Loop) c;
            if (b.kind() == Branch.CONTINUE) {
              edge(last_visitor, last, l.continueTarget(), Term.ENTRY, 
                      FlowGraph.EDGE_KEY_OTHER);
            }
            else {
              edge(last_visitor, last, l, Term.EXIT, FlowGraph.EDGE_KEY_OTHER);
            }

            return;
          }
          else if (c instanceof Switch && b.kind() == Branch.BREAK) {
            edge(last_visitor, last, c, Term.EXIT, FlowGraph.EDGE_KEY_OTHER);
            return;
          }
        }
      }

      throw new CFGBuildError("Target of branch statement not found.",
                              b.position());
    }

    /**
     * Visit edges for a return statement.  Simulate the return, visiting any
     * finally blocks encountered.
     */
    public void visitReturn(Return r) {
      Term last = r;
      CFGBuilder last_visitor = this;

      for (CFGBuilder v = this; v != null; v = v.outer) {
        Term c = v.innermostTarget;

        if (c instanceof Try) {
          Try tr = (Try) c;
          if (tr.finallyBlock() != null) {
            last_visitor = tryFinally(v, last, last_visitor, tr.finallyBlock());
            last = tr.finallyBlock();
          }
        }
      }

      // Add an edge to the exit node.
      edge(last_visitor, last, graph.root(), Term.EXIT, FlowGraph.EDGE_KEY_OTHER);
    }

    protected static int counter = 0;

    /** Visit the AST, constructing the CFG. */
    public void visitGraph() {
        String name = StringUtil.getShortNameComponent(df.getClass().getName());
        name += counter++;

	if (reporter.should_report(Reporter.cfg, 2)) {
            String rootName = "";
            if (graph.root() instanceof CodeNode) {
                CodeNode cd = (CodeNode)graph.root();
                rootName = cd.codeDef().toString();
                if (cd.codeDef() instanceof MemberDef) {
                    rootName += " in " + ((MemberDef) cd.codeDef()).container().toString();
                }
            }

            reporter.report(2, "digraph CFGBuild" + name + " {");
            reporter.report(2, "  label=\"CFGBuilder: " + name + "\\n" + rootName +
                "\"; fontsize=20; center=true; ratio=auto; size = \"8.5,11\";");
        }

        // create peers for the entry and exit nodes.
        graph.peer(graph.root(), Collections.<Term>emptyList(), Term.ENTRY);
        graph.peer(graph.root(), Collections.<Term>emptyList(), Term.EXIT);

        this.visitCFG(graph.root(), Collections.<EdgeKeyTermPair>emptyList());

	if (reporter.should_report(Reporter.cfg, 2))
	    reporter.report(2, "}");
    }

    /**
     * Utility function to visit all edges in a list.
     * 
     * If <code>entry</code> is Term.ENTRY, the final successor is
     * <code>after</code>'s entry node; if it's Term.EXIT, it's 
     * <code>after</code>'s exit.
     */
    public void visitCFGList(List<? extends Term> elements, Term after, int entry) {
        Term prev = null;

        for (Term c : elements) {

            if (prev != null) {
                visitCFG(prev, c, Term.ENTRY);
            }

            prev = c;
        }

        if (prev != null) {
            visitCFG(prev, after, entry);
        }
    }

    /**
     * Create an edge for a node <code>a</code> with a single successor
     * <code>succ</code>.
     * 
     * The EdgeKey used for the edge from <code>a</code> to <code>succ</code>
     * will be FlowGraph.EDGE_KEY_OTHER.
     * 
     * If <code>entry</code> is Term.ENTRY, the successor is <code>succ</code>'s
     * entry node; if it's Term.EXIT, it's <code>succ</code>'s exit.
     */
    public void visitCFG(Term a, Term succ, int entry) {
        visitCFG(a, FlowGraph.EDGE_KEY_OTHER, succ, entry);
    }

    /**
     * Create an edge for a node <code>a</code> with a single successor
     * <code>succ</code>, and EdgeKey <code>edgeKey</code>.
     * 
     * If <code>entry</code> is Term.ENTRY, the successor is <code>succ</code>'s
     * entry node; if it's Term.EXIT, it's <code>succ</code>'s exit.
     */
    public void visitCFG(Term a, FlowGraph.EdgeKey edgeKey, Term succ, int entry) {
        visitCFG(a, CollectionUtil.list(new EdgeKeyTermPair(edgeKey, succ, entry)));
    }

    /**
     * Create edges from node <code>a</code> to successors <code>succ1</code> 
     * and <code>succ2</code> with EdgeKeys <code>edgeKey1</code> and
     * <code>edgeKey2</code> respecitvely.
     * 
     * <code>entry1</code> and <code>entry2</code> determine whether the
     * successors are entry or exit nodes. They can be Term.ENTRY or Term.EXIT.
     */
    public void visitCFG(Term a, FlowGraph.EdgeKey edgeKey1, Term succ1,
                                 int entry1, FlowGraph.EdgeKey edgeKey2, Term succ2, int entry2) {
        visitCFG(a, CollectionUtil.list(new EdgeKeyTermPair(edgeKey1, succ1, entry1), 
                                        new EdgeKeyTermPair(edgeKey2, succ2, entry2)));
    }

    /**
     * Create edges from node <code>a</code> to all successors <code>succ</code> 
     * with the EdgeKey <code>edgeKey</code> for all edges created.
     * 
     * If <code>entry</code> is Term.ENTRY, all terms in <code>succ</code> are
     * treated as entry nodes; if it's Term.EXIT, they are treated as exit
     * nodes.
     */
    public void visitCFG(Term a, 
            FlowGraph.EdgeKey edgeKey, List<Term> succ, int entry) {
        List<EdgeKeyTermPair> l = new ArrayList<EdgeKeyTermPair>(succ.size());
        
        for (Term t : succ) {
            l.add(new EdgeKeyTermPair(edgeKey, t, entry));
        }
        
        visitCFG(a, l);
    }

    /**
     * Create edges from node <code>a</code> to all successors
     * <code>succ</code> with the EdgeKey <code>edgeKey</code> for all edges
     * created.
     * 
     * The <code>entry</code> list must have the same size as
     * <code>succ</code>, and each corresponding element determines whether a
     * successor is an entry or exit node (using Term.ENTRY or Term.EXIT).
     */
    public void visitCFG(Term a, 
            FlowGraph.EdgeKey edgeKey, List<Term> succ, List<Integer> entry) {
        if (succ.size() != entry.size()) {
            throw new IllegalArgumentException();
        }
        
        List<EdgeKeyTermPair> l = new ArrayList<EdgeKeyTermPair>(succ.size());
        
        for (int i = 0; i < succ.size(); i++) {
            Term t = succ.get(i);
            l.add(new EdgeKeyTermPair(edgeKey, t, entry.get(i)));
        }
        
        visitCFG(a, l);
    }

    protected static class EdgeKeyTermPair {

        public final FlowGraph.EdgeKey edgeKey;
        public final Term term;
        public final int entry;

        public EdgeKeyTermPair(FlowGraph.EdgeKey edgeKey, Term term, int entry) {
            this.edgeKey = edgeKey;
            this.term = term;
            this.entry = entry;
        }
        
        public String toString() {
            return "{edgeKey=" + edgeKey + ",term=" + term + "," + 
                (entry == Term.ENTRY ? "entry" : "exit") + "}";
        }
        
    }

    /**
     * Create edges for a node <code>a</code> with successors
     * <code>succs</code>.
     * @param a the source node for the edges.
     * @param succs a list of <code>EdgeKeyTermPair</code>s
     */
    protected void visitCFG(Term a, List<EdgeKeyTermPair> succs) {
        Term child = a.firstChild();
        
        if (child == null) {
            edge(this, a, Term.ENTRY, a, Term.EXIT, FlowGraph.EDGE_KEY_OTHER);
        } else {
            edge(this, a, Term.ENTRY, child, Term.ENTRY, FlowGraph.EDGE_KEY_OTHER);
        }

        if (reporter.should_report(Reporter.cfg, 2))
            reporter.report(2, "// node " + a + " -> " + succs);
        
        succs = a.acceptCFG(this, succs);

        for (EdgeKeyTermPair s : succs) {
            edge(a, s.term, s.entry, s.edgeKey);
        }

        visitThrow(a);
    }

    public void visitThrow(Term a) {
        for (Type type : a.del().throwTypes(ts)) {
            visitThrow(a, Term.EXIT, type);
        }

        // Every statement can throw an error.
        // This is probably too inefficient.
        if ((a instanceof Stmt && ! (a instanceof CompoundStmt)) ||
            (a instanceof Block && ((Block) a).statements().isEmpty())) {
            
            visitThrow(a, Term.EXIT, ts.Error());
        }
    }
    
    /**
     * Create edges for an exception thrown from term <code>t</code>.
     */
    public void visitThrow(Term t, int entry, Type type) {
        Term last = t;
        CFGBuilder last_visitor = this;
        Context context = ts.emptyContext();

        for (CFGBuilder v = this; v != null; v = v.outer) {
            Term c = v.innermostTarget;

            if (c instanceof Try) {
                Try tr = (Try) c;

                if (! v.skipInnermostCatches) {                    
                    boolean definiteCatch = false;
                    
                    for (Catch cb : tr.catchBlocks()) {
                        int e = (last == t && entry == Term.ENTRY) ? Term.ENTRY : Term.EXIT;

                        // definite catch
                        if (type.isImplicitCastValid(cb.catchType(), context)) {
                            edge(last_visitor, last, e, cb, Term.ENTRY, 
                                    new FlowGraph.ExceptionEdgeKey(type));
                            definiteCatch = true;
                        }
                        // possible catch
                        else if (cb.catchType().isImplicitCastValid(type, context)) { 
                            edge(last_visitor, last, e, cb, Term.ENTRY, 
                                    new FlowGraph.ExceptionEdgeKey(cb.catchType()));
                        }
                    }
                    if (definiteCatch) {
                        // the exception has definitely been caught.
                        // we can stop recursing to outer try-catch blocks
                        return; 
                    }
                }

                if (tr.finallyBlock() != null) {
                    last_visitor = tryFinally(v, last, last_visitor, tr.finallyBlock());
                    last = tr.finallyBlock();
                }
            }
        }

        int e = (last == t && entry == Term.ENTRY) ? Term.ENTRY : Term.EXIT;
        
        // If not caught, insert a node from the thrower to exit.
        if (errorEdgesToExitNode || !type.isSubtype(ts.Error(), context)) {
            edge(last_visitor, last, e, graph.root(), Term.EXIT, 
                    new FlowGraph.ExceptionEdgeKey(type));
        }
    }

    /**
     * Create edges for the finally block of a try-finally construct. 
     * @param v v.innermostTarget is the Try term that the finallyBlock is assoicated with. @@@XXX
     * @param last the last term visited before the finally block is entered.
     * @param last_visitor @@@XXX
     * @param finallyBlock the finally block associated with a try finally block.
     */
    protected static CFGBuilder tryFinally(CFGBuilder v,
                                 Term last, CFGBuilder last_visitor, Block finallyBlock) {
        //###@@@ I think that we may be using the wrong visitor to perform the
        // enterFinally on; should it maybe be last_visitor? we want to make 
        // sure that the path_to_finally list grows correctly.
        CFGBuilder v_ = v.outer.enterFinally(last);
        
        // @@@XXX
        v_.edge(last_visitor, last, finallyBlock, Term.ENTRY, FlowGraph.EDGE_KEY_OTHER);
        
        // visit the finally block.  
        v_.visitCFG(finallyBlock, Collections.<EdgeKeyTermPair>emptyList());
        return v_;
    }

    /** 
     * Enter a finally block. This method returns a new CFGBuilder
     * with the path_to_finally field pointing to a list that has the
     * Term <code>from</code> appended.
     */
    protected CFGBuilder enterFinally(Term from) {
      CFGBuilder v = this.shallowCopy();
      v.path_to_finally = new ArrayList<Term>(path_to_finally.size()+1);
      v.path_to_finally.addAll(path_to_finally);
      v.path_to_finally.add(from);
      return v;
    }

    /**
     * Add an edge to the CFG from the exit of <code>p</code> to either the
     * entry or exit of <code>q</code>.
     */
    public void edge(Term p, Term q, int qEntry) {
      edge(this, p, q, qEntry, FlowGraph.EDGE_KEY_OTHER);
    }

    /**
     * Add an edge to the CFG from the exit of <code>p</code> to either the
     * entry or exit of <code>q</code>.
     */
    public void edge(Term p, Term q, int qEntry, 
            FlowGraph.EdgeKey edgeKey) {
      edge(this, p, q, qEntry, edgeKey);
    }

    /**
     * Add an edge to the CFG from the exit of <code>p</code> to either the
     * entry or exit of <code>q</code>.
     */
    public void edge(CFGBuilder p_visitor, Term p, 
            Term q, int qEntry, FlowGraph.EdgeKey edgeKey) {
        edge(p_visitor, p, Term.EXIT, q, qEntry, edgeKey);
    }
    
    /**
     * @param p_visitor The visitor used to create p ("this" is the visitor
     *                  that created q) 
     * @param p The predecessor node in the forward graph
     * @param pEntry whether we are working with the entry or exit of p. Can be
     *      Term.ENTRY or Term.EXIT.
     * @param q The successor node in the forward graph
     * @param qEntry whether we are working with the entry or exit of q. Can be
     *      Term.ENTRY or Term.EXIT.
     */
    public void edge(CFGBuilder p_visitor, Term p, int pEntry, 
            Term q, int qEntry, FlowGraph.EdgeKey edgeKey) {
        if (reporter.should_report(Reporter.cfg, 2))
            reporter.report(2, "//     edge " + p + " -> " + q);
        
        FlowGraph.Peer pp = graph.peer(p, p_visitor.path_to_finally, pEntry);
        FlowGraph.Peer pq = graph.peer(q, path_to_finally, qEntry);
        
        if (reporter.should_report(Reporter.cfg, 3)) {
            // at level 3, use Peer.toString() as the label for the nodes
            reporter.report(2,
                          pp.hashCode() + " [ label = \"" +
                          StringUtil.escape(pp.toString()) + "\" ];");
            reporter.report(2,
                          pq.hashCode() + " [ label = \"" +
                          StringUtil.escape(pq.toString()) + "\" ];");
        }
        else if (reporter.should_report(Reporter.cfg, 2)) {
            // at level 2, use Node.toString() as the label for the nodes
            // which is more readable than Peer.toString(), but not as unique.
            reporter.report(2,
                          pp.hashCode() + " [ label = \"" +
                          StringUtil.escape(pp.node.toString()) + "\" ];");
            reporter.report(2,
                          pq.hashCode() + " [ label = \"" +
                          StringUtil.escape(pq.node.toString()) + "\" ];");
        }
        
        if (graph.forward()) {
            if (reporter.should_report(Reporter.cfg, 2)) {
                reporter.report(2, pp.hashCode() + " -> " + pq.hashCode() + " [label=\"" + edgeKey + "\"];");
            }
            pp.succs.add(new FlowGraph.Edge(edgeKey, pq));
            pq.preds.add(new FlowGraph.Edge(edgeKey, pp));
        }
        else {
            if (reporter.should_report(Reporter.cfg, 2)) {
                reporter.report(2, pq.hashCode() + " -> " + pp.hashCode() + " [label=\"" + edgeKey + "\"];");
            }
            pq.succs.add(new FlowGraph.Edge(edgeKey, pp));
            pp.preds.add(new FlowGraph.Edge(edgeKey, pq));
        }
    }
    
}
