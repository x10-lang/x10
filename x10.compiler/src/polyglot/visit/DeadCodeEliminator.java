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

package polyglot.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.DataFlow.Item;
import polyglot.visit.FlowGraph.EdgeKey;

/**
 * Visitor which performs dead code elimination.  (Note that "dead code" is not
 * unreachable code, but is actually code that has no effect.)
 */
public class DeadCodeEliminator extends DataFlow {
    public DeadCodeEliminator(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf,
	      false /* backward analysis */,
	      true  /* perform dataflow on entry to CodeDecls */);
    }

    protected static class DataFlowItem extends Item {
	// Set of LocalInstances of live variables.
	private Set<LocalDef> liveVars;

	// Set of LocalInstances of live declarations.  A LocalDecl is live if
	// the declared local is ever live.
	private Set<LocalDef> liveDecls;

	/**
	 * Constructor for creating an empty set.
	 */
	protected DataFlowItem() {
	    this.liveVars = CollectionFactory.newHashSet();
	    this.liveDecls = CollectionFactory.newHashSet();
	}

	/**
	 * Deep copy constructor.
	 */
	protected DataFlowItem(DataFlowItem dfi) {
	    liveVars = CollectionFactory.newHashSet(dfi.liveVars);
	    liveDecls = CollectionFactory.newHashSet(dfi.liveDecls);
	}

	public void add(LocalDef li) {
	    liveVars.add(li);
	    liveDecls.add(li);
	}

	public void addAll(Set<LocalDef> lis) {
	    liveVars.addAll(lis);
	    liveDecls.addAll(lis);
	}

	public void remove(LocalDef li) {
	    liveVars.remove(li);
	}

	public void removeAll(Set<LocalDef> lis) {
	    liveVars.removeAll(lis);
	}

	public void removeDecl(LocalDef li) {
	    liveVars.remove(li);
	    liveDecls.remove(li);
	}

	public void union(DataFlowItem dfi) {
	    liveVars.addAll(dfi.liveVars);
	    liveDecls.addAll(dfi.liveDecls);
	}

	protected boolean needDecl(LocalDef li) {
	    return liveDecls.contains(li);
	}

	protected boolean needDef(LocalDef li) {
	    return liveVars.contains(li);
	}

	public int hashCode() {
	    int result = 1;
	    for (LocalDef ld : liveVars) {
		result = 31*result + ld.hashCode();
	    }

	    for (LocalDef ld : liveDecls) {
		result = 31*result + ld.hashCode();
	    }

	    return result;
	}

	public boolean equals(Object o) {
	    if (!(o instanceof DataFlowItem)) return false;

	    DataFlowItem dfi = (DataFlowItem)o;
	    return liveVars.equals(dfi.liveVars)
		&& liveDecls.equals(dfi.liveDecls);
	}

	public String toString() {
	    return "<vars=" + liveVars + " ; decls=" + liveDecls + ">";
	}
    }

    public Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
	return new DataFlowItem();
    }

    public Item confluence(List<Item> inItems, Term node, boolean entry, FlowGraph graph) {
	DataFlowItem result = null;
	for (Item item : inItems) {
	    DataFlowItem inItem = (DataFlowItem)item;
	    if (result == null) {
		result = new DataFlowItem(inItem);
	    } else {
		result.union(inItem);
	    }
	}

	return result;
    }

    public Map<EdgeKey, Item> flow(Item in, FlowGraph graph, Term t, boolean entry, Set<EdgeKey> succEdgeKeys) {
	return itemToMap(flow(in, graph, t, entry), succEdgeKeys);
    }

    protected DataFlowItem flow(Item in, FlowGraph graph, Term t, boolean entry) {
	DataFlowItem result = new DataFlowItem((DataFlowItem)in);
    
    if (entry) {
        return result;
    }

	Pair<Set<LocalDef>, Set<LocalDef>> du = null;

	if (t instanceof LocalDecl) {
	    LocalDecl n = (LocalDecl)t;

	    LocalDef to = n.localDef();
	    result.removeDecl(to);

	    du = getDefUse(n.init());
	} else if (t instanceof Stmt && !(t instanceof CompoundStmt)) {
	    du = getDefUse((Stmt)t);
	} else if (t instanceof CompoundStmt) {
	    if (t instanceof If) {
		du = getDefUse(((If)t).cond());
	    } else if (t instanceof Switch) {
		du = getDefUse(((Switch)t).expr());
	    } else if (t instanceof Do) {
		du = getDefUse(((Do)t).cond());
	    } else if (t instanceof For) {
		du = getDefUse(((For)t).cond());
	    } else if (t instanceof While) {
		du = getDefUse(((While)t).cond());
	    }
	}

	if (du != null) {
	    result.removeAll(du.fst());
	    result.addAll(du.snd());
	}

	return result;
    }

    public void post(FlowGraph graph, Term root) {
	// No need to do any checking.
	if (reporter.should_report(Reporter.cfg, 2)) {
	    dumpFlowGraph(graph, root);
	}
    }

    public void check(FlowGraph graph, Term n, boolean entry, Item inItem, Map<EdgeKey, Item> outItems) {

	throw new InternalCompilerError("DeadCodeEliminator.check should "
	    + "never be called.");
    }

    private DataFlowItem getItem(Term n) {
	FlowGraph g = currentFlowGraph();
	if (g == null) return null;

	Collection<FlowGraph.Peer> peers = g.peers(n, Term.EXIT);
	if (peers == null || peers.isEmpty()) return null;

	List<Item> items = new ArrayList<Item>();
	for (FlowGraph.Peer p : peers) {
	    if (p.inItem() != null) items.add(p.inItem());
	}

	return (DataFlowItem)confluence(items, n, false, g);
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) {

	if (n instanceof LocalDecl) {
	    LocalDecl ld = (LocalDecl)n;
	    DataFlowItem in = getItem(ld);
	    if (in == null || in.needDecl(ld.localDef())) return n;
	    return getEffects(ld.init());
	}

	if (n instanceof Eval) {
	    Eval eval = (Eval)n;
	    Expr expr = eval.expr();
	    Local local;
	    Expr right = null;

	    if (expr instanceof LocalAssign) {
		LocalAssign assign = (LocalAssign)expr;
		Expr left = assign.local();
		right = assign.right();
		if (!(left instanceof Local)) return n;
		local = (Local)left;
	    } else if (expr instanceof Assign) {
		return n;
	    } else if (expr instanceof Unary) {
		Unary unary = (Unary)expr;
		expr = unary.expr();
		if (!(expr instanceof Local)) return n;
		local = (Local)expr;
	    } else {
		return n;
	    }

	    DataFlowItem in = getItem(eval);
	    if (in == null || in.needDef(local.localInstance().def())) return n;

	    if (right != null) {
		return getEffects(right);
	    }

	    return nf.Empty(Position.COMPILER_GENERATED);
	}

	if (n instanceof Block) {
	    // Get rid of empty statements.
	    Block b = (Block)n;
	    List<Stmt> stmts = new ArrayList<Stmt>(b.statements());
	    for (Iterator<Stmt> it = stmts.iterator(); it.hasNext(); ) {
		if (it.next() instanceof Empty) it.remove();
	    }

	    return b.statements(stmts);
	}

	return n;
    }

    /**
     * Returns array of sets of local instances.
     * Element 0 is the set of local instances DEFined by the node.
     * Element 1 is the set of local instances USEd by the node.
     */
    protected Pair<Set<LocalDef>, Set<LocalDef>> getDefUse(Node n) {
	final Set<LocalDef> def = CollectionFactory.newHashSet();
	final Set<LocalDef> use = CollectionFactory.newHashSet();

	if (n != null) {
	    n.visit(createDefUseFinder(def, use));
	}

	return new Pair<Set<LocalDef>, Set<LocalDef>>(def, use);
    }

    protected NodeVisitor createDefUseFinder(Set<LocalDef> def, Set<LocalDef> use) {
	return new DefUseFinder(def, use);
    }

    protected static class DefUseFinder extends NodeVisitor {
	protected Set<LocalDef> def;
	protected Set<LocalDef> use;

	public DefUseFinder(Set<LocalDef> def, Set<LocalDef> use) {
	    this.def = def;
	    this.use = use;
	}
	
    @Override
	public Node override(Node parent, Node n) {
		if (parent instanceof LocalAssign) {
			LocalAssign a = (LocalAssign) parent;
			if (n == a.local()) {
				return n;
			}
		}
		
		return null;
	}

    @Override
	public Node leave(Node old, Node n, NodeVisitor v) {
	    if (n instanceof Local) {
		use.add(((Local)n).localInstance().def());
	    } else if (n instanceof LocalAssign) {
		Expr left = ((LocalAssign)n).local();
		if (left instanceof Local) {
		    def.add(((Local)left).localInstance().def());
		}
	    }

	    return n;
	}
    }

    /**
     * Returns a statement that is side-effect-equivalent to the given
     * expression.
     */
    protected Stmt getEffects(Expr expr) {
	Stmt empty = nf.Empty(Position.COMPILER_GENERATED);
	if (expr == null) return empty;

	final List<Stmt> result = new LinkedList<Stmt>();
	final Position pos = Position.COMPILER_GENERATED;

	NodeVisitor v = new NodeVisitor() {
		@Override
		public Node override(Node parent, Node n) {
			if (n instanceof Assign || n instanceof ProcedureCall) {
				return leave(parent, n, n, this);
			}

		// XXX Cast

		if (n instanceof Unary) {
		    Unary.Operator op = ((Unary)n).operator();
		    if (op == Unary.POST_INC || op == Unary.POST_DEC
			|| op == Unary.PRE_INC || op == Unary.PRE_INC) {

		    	return leave(parent, n, n, this);
		    }
		}

		return n;
	    }

	    @Override
	    public Node leave(Node old, Node n, NodeVisitor v) {
		if (n instanceof Assign || n instanceof ProcedureCall) {
		    result.add(nf.Eval(pos, (Expr)n));
		} else if (n instanceof Unary) {
		    Unary.Operator op = ((Unary)n).operator();
		    if (op == Unary.POST_INC || op == Unary.POST_DEC
			|| op == Unary.PRE_INC || op == Unary.PRE_INC) {

			result.add(nf.Eval(pos, (Expr)n));
		    }
		}

		// XXX Cast

		return n;
	    }
	};

	expr.visit(v);

	if (result.isEmpty()) return empty;
	if (result.size() == 1) return result.get(0);
	return nf.Block(Position.COMPILER_GENERATED, result);
    }
}

