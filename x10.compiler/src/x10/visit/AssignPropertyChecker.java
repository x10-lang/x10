/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.CodeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Term;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.DataFlow;
import polyglot.visit.FlowGraph;
import polyglot.visit.DataFlow.Item;
import x10.ast.AssignPropertyBody;
import x10.ast.AssignPropertyCall;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;

public class AssignPropertyChecker extends DataFlow {
	ConstructorDecl cd;
	
	// InitChecker will check that all properties are assigned exactly once.
	// TypeChecker will check that a property statement assigns to all defined properties.
	//
	// We just need to check that:
	// 1. There is exactly one property() statement or this() constructor call on each normal control-flow path to the exit node.
	// 2. The property statement implies the return type.
	
	public AssignPropertyChecker(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf, false /* backward analysis */);
	}
	
	protected FlowGraph initGraph(CodeNode code, Term root) {
		if (code instanceof ConstructorDecl) {
			ConstructorDecl d = (ConstructorDecl) code;
			this.cd = d;
			if (d.constructorDef() instanceof X10ConstructorDef) {
			    X10ConstructorDef xci = (X10ConstructorDef) d.constructorDef();
				if (xci.container().get() instanceof X10ClassType) {
					X10ClassType xt = (X10ClassType) xci.container().get();
					if (! xt.definedProperties().isEmpty()) {
						return super.initGraph(code, root);
					}
				}
			}
		}
		
		return null;
	}
	
	public Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
		return createItem(DataFlowItem.ZERO, DataFlowItem.ZERO);
	}
	
	public boolean needsProperty() {
	    X10ConstructorInstance xci = (X10ConstructorInstance) cd.constructorDef().asInstance();
	    return X10TypeMixin.isConstrained(xci.container()) || X10TypeMixin.isConstrained(xci.returnType());
	}
		
	protected static class DataFlowItem extends Item {
		/** set of properties on all non-exceptional paths from this node to an exit */
		public final int min, max;
		
		/** Create a data flow item with the given properties assigned. */
		protected DataFlowItem(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		public final static int ZERO = 0;
		public final static int ONE = 1;
		public final static int MANY = 2;
		public final static int DONT_CARE = 3;
		
		public String toString() {
			return "propertyAssignCount=" + min + ".." + max;
		}
		public boolean equals(Object o) {
			if (o instanceof DataFlowItem) {
				DataFlowItem i = (DataFlowItem) o;
				return this.min == i.min && this.max == i.max;
			}
			return false;
		}
		public int hashCode() {
			return Integer.valueOf(min).hashCode() + Integer.valueOf(max << 2).hashCode();
		}
	}
	
	public DataFlowItem increment(DataFlowItem in) {
		int min, max;
		if (in != null && (in.min == DataFlowItem.ONE || in.min == DataFlowItem.MANY))
			min = DataFlowItem.MANY;
		else
			min = DataFlowItem.ONE;
		if (in != null && (in.max == DataFlowItem.ONE || in.max == DataFlowItem.MANY))
			max = DataFlowItem.MANY;
		else
			max = DataFlowItem.ONE;
		return createItem(min, max);
	}

	DataFlowItem[][] cache = new DataFlowItem[4][4];
	
	protected DataFlowItem createItem(int min, int max) {
		DataFlowItem i = cache[min][max];
		if (i == null) {
			i = new DataFlowItem(min, max);
			cache[min][max] = i;
		}
		return i;
	}
	
	public Map flow(Item in, FlowGraph graph, Term n, boolean entry, Set succEdgeKeys) {
		// If every path from the exit node to the entry goes through a property
		// assign, we're okay. So make the propAssign bit false at exit and true
		// at every property assignment; the confluence operation is &&.
		// We deal with exceptions specially, and assume that any exception
		// edge to the exit node is OK.


                // Only flow from exit.
                if (entry)
                    return itemToMap(in, succEdgeKeys);
		
		DataFlowItem inItem = (DataFlowItem) in;
		
		if (n instanceof Return) {
			return itemToMap(createItem(DataFlowItem.ZERO, DataFlowItem.ZERO), succEdgeKeys);
		}
		
		if (graph.exitPeers().contains(n)) {
			// Exception edges are assumed to be safe
			if (needsProperty()) {
				Map m = itemToMap(createItem(DataFlowItem.DONT_CARE, DataFlowItem.DONT_CARE), succEdgeKeys);
				if (succEdgeKeys.contains(FlowGraph.EDGE_KEY_OTHER)) {
					m.put(FlowGraph.EDGE_KEY_OTHER, createItem(DataFlowItem.ZERO, DataFlowItem.ZERO));
				}
				if (succEdgeKeys.contains(FlowGraph.EDGE_KEY_TRUE)) {
					m.put(FlowGraph.EDGE_KEY_TRUE, createItem(DataFlowItem.ZERO, DataFlowItem.ZERO));
				}
				if (succEdgeKeys.contains(FlowGraph.EDGE_KEY_FALSE)) {
					m.put(FlowGraph.EDGE_KEY_FALSE, createItem(DataFlowItem.ZERO, DataFlowItem.ZERO));
				}
				
				return m;
			}
			else {
				return itemToMap(createItem(DataFlowItem.ZERO, DataFlowItem.ZERO), succEdgeKeys);
			}
		}
		
		if (n instanceof AssignPropertyBody) {
			return itemToMap(increment(inItem), succEdgeKeys);
		}

		if (n instanceof ConstructorCall) {
			ConstructorCall cc = (ConstructorCall) n;
			if (cc.kind() == ConstructorCall.THIS) {
				return itemToMap(increment(inItem), succEdgeKeys);
			}
		}
		
		return itemToMap(in, succEdgeKeys);
	}
	
//	 protected Item confluence(List items, List itemKeys, Term node, boolean entry, FlowGraph graph) {
//		 List l1 = new ArrayList(3);
//		 List l2 = new ArrayList(3);
//		 Iterator i = items.iterator();
//		 Iterator j = itemKeys.iterator();
//		 while (i.hasNext() && j.hasNext()) {
//			 DataFlowItem dfi = (DataFlowItem) i.next();
//			 FlowGraph.EdgeKey key = (FlowGraph.EdgeKey) j.next();
//			 if (key instanceof FlowGraph.ExceptionEdgeKey) continue;
//			 l1.add(dfi);
//			 l2.add(key);
//		 }
//		 if (l1.size() == 0) {
//			 return new DataFlowItem(0, 0);
//		 }
//		 else if (l1.size() == 1) {
//			 return ((DataFlowItem) l1.get(0));
//		 }
//		 return super.confluence(l1, l2, node, graph);
//	 }
	 
	 protected Item confluence(List items, Term node, boolean entry, FlowGraph graph) {
		// intersect the items
		int min = DataFlowItem.DONT_CARE;
		int max = DataFlowItem.DONT_CARE;
		Iterator i = items.iterator();
		while (i.hasNext()) {
			DataFlowItem dfi = (DataFlowItem) i.next();
			if (min == DataFlowItem.DONT_CARE || (dfi.min != DataFlowItem.DONT_CARE && dfi.min < min))
				min = dfi.min;
			if (max == DataFlowItem.DONT_CARE || (dfi.max != DataFlowItem.DONT_CARE && dfi.max > max))
				max = dfi.max;
		}
		return createItem(min, max); 
	}
	
	public void check(FlowGraph graph, Term n, boolean entry, Item inItem, Map outItems) throws SemanticException {
                if (entry)
                    return;

		// Check that all paths to exit have exactly one property() statement.
		if (graph.entryPeers().contains(n)) {
			if (outItems != null && !outItems.isEmpty()) {
				// due to the flow equations, all DataFlowItems in the outItems map
				// are the same, so just take the first one.
				DataFlowItem outItem = (DataFlowItem)outItems.values().iterator().next(); 
				if (outItem != null) {
					if (outItem.max == DataFlowItem.MANY) {
						// This should be caught by InitChecker, but report it here just in case.
						throw new SemanticException("The constructor may have initialized properties more than once.  There is a path with more than one property(...) statement or this(...) call.",
								cd.position());
					}
					
					if (!needsProperty() || outItem.min != DataFlowItem.ZERO) {
						return;
					}
					
					// fall through to report error
				}
			}

			throw new SemanticException("The constructor incorrectly initializes properties.  There is a path without a property(...) statement or this(...) call.",
					cd.position());
		}
	}
}
