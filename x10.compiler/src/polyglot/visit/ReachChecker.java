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
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.DataFlow.Item;
import polyglot.visit.FlowGraph.EdgeKey;
import x10.errors.Errors;

/**
 * Visitor which checks that all statements must be reachable
 */
public class ReachChecker extends DataFlow
{
    public ReachChecker(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf, 
              true /* forward analysis */, 
              true /* perform dataflow on entry to CodeDecls */);
        reportCFG_Errors = true; // this is always the first dataflow analysis
    }

    protected static class DataFlowItem extends Item {
        public final boolean reachable;
        public final boolean normalReachable;

        protected DataFlowItem(boolean reachable, boolean normalReachable) {
            this.reachable = reachable;
            this.normalReachable = normalReachable;
        }
        
        // terms that are reachable through normal control flow
        public static final DataFlowItem REACHABLE = new DataFlowItem(true, true);
        
        // terms that are reachable only through exception control flow, but
        // not by normal control flow. 
        public static final DataFlowItem REACHABLE_EX_ONLY = new DataFlowItem(true, false);

        // terms that are not reachable 
        public static final DataFlowItem NOT_REACHABLE = new DataFlowItem(false, false);

        public String toString() {
            return (reachable?"":"not ") + "reachable" +
                   (normalReachable?"":" by exceptions only");
        }
        
        public boolean equals(Object o) {
            if (o instanceof DataFlowItem) {
                return this.reachable == ((DataFlowItem)o).reachable &&
                       this.normalReachable == ((DataFlowItem)o).normalReachable;
            }
            return false;
        }
        public int hashCode() {
            return (reachable ? 5423 : 5753) + (normalReachable ? 31 : -2);
        }
    }

    public Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
        if (node == graph.root() && entry) {
            return DataFlowItem.REACHABLE;
        }
        else {
            return DataFlowItem.NOT_REACHABLE;
        }
    }
    
    public Map<EdgeKey, Item> flow(Item in, FlowGraph graph, Term n, boolean entry, Set<EdgeKey> succEdgeKeys) {
        if (in == DataFlowItem.NOT_REACHABLE) {
            return itemToMap(in, succEdgeKeys);
        }
        
        // in is either REACHABLE or REACHABLE_EX_ONLY.
        // return a map where all exception edges are REACHABLE_EX_ONLY,
        // and all non-exception edges are REACHABLE.
        Map<EdgeKey, Item> m = itemToMap(DataFlowItem.REACHABLE_EX_ONLY, succEdgeKeys);

        if (succEdgeKeys.contains(FlowGraph.EDGE_KEY_OTHER)) {
            m.put(FlowGraph.EDGE_KEY_OTHER, DataFlowItem.REACHABLE);
        }
        if (succEdgeKeys.contains(FlowGraph.EDGE_KEY_TRUE)) {
            m.put(FlowGraph.EDGE_KEY_TRUE, DataFlowItem.REACHABLE);
        }
        if (succEdgeKeys.contains(FlowGraph.EDGE_KEY_FALSE)) {
            m.put(FlowGraph.EDGE_KEY_FALSE, DataFlowItem.REACHABLE);
        }
        
        return m;
    }

    public Item confluence(List<Item> inItems, Term node, boolean entry, FlowGraph graph) {
        throw new InternalCompilerError("Should never be called.");
    }

    public Item confluence(List<Item> inItems, List<EdgeKey> itemKeys, 
            Term node, boolean entry, FlowGraph graph) {
        // if any predecessor is reachable, so is this one, and if any
        // predecessor is normal reachable, and the edge key is not an 
        // exception edge key, then so is this one.
        
        
        List<Item> l = this.filterItemsNonException(inItems, itemKeys);
        for (Item item : l) {
            if (item == DataFlowItem.REACHABLE) {
                // this term is reachable via a non-exception edge
                return DataFlowItem.REACHABLE;
            }
        }

        // If we fall through to here, then there were
        // no non-exception edges that were normally reachable.        
        // We now need to determine if this node is
        // reachable via an exception edge key, or if 
        // it is not reachable at all.
        for (Item item : inItems) {
            if (((DataFlowItem)item).reachable) {
                // this term is reachable, but only through an
                // exception edge.
                return DataFlowItem.REACHABLE_EX_ONLY;
            }
        }

        return DataFlowItem.NOT_REACHABLE;
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        // check for reachability.
        if (n instanceof Term) {
           n = checkReachability((Term)n);
            Boolean reachable = ((Term) n).reachable();
            if (!hadCFG_Error && (reachable==null || !reachable)) {
               // Do we throw an exception or not?
               
               // Compound statements are allowed to be unreachable
               // (e.g., "{ return; }" or "while (true) S").  If a compound
               // statement is truly unreachable, one of its sub-statements
               // will be also and we will report an error there.

               if ((n instanceof Block && ((Block) n).statements().isEmpty()) ||
                   (n instanceof Stmt && ! (n instanceof CompoundStmt))) {
                   reportError(new Errors.UnreachableStatement(n.position()));
               }
           }
           
        }
         
        return super.leaveCall(old, n, v);
    }

    protected Node checkReachability(Term n) {
        FlowGraph g = currentFlowGraph();
        if (g != null) {   
            Collection<FlowGraph.Peer> peers = g.peers(n, Term.EXIT);
            if (peers != null && !peers.isEmpty()) {
                boolean isInitializer = (n instanceof Initializer);
                
                for (FlowGraph.Peer p : peers) {
                    // the peer is reachable if at least one of its out items
                    // is reachable. This would cover all cases, except that some
                    // peers may have no successors (e.g. peers that throw an
                    // an exception that is not caught by the method). So we need 
                    // to also check the inItem.
                    if (p.inItem() != null) {
                        DataFlowItem dfi = (DataFlowItem)p.inItem();
                        // there will only be one peer for an initializer,
                        // as it cannot occur in a finally block.
                        if (isInitializer && !dfi.normalReachable) {
                            reportError(new Errors.InitializersMustCompleteNormally(n.position()));
                        }

                        if (dfi.reachable) {
                            return n.reachable(true);
                        }
                    }
                    
                    if (p.outItems != null) {
                        for (Item v : p.outItems.values()) {
                            DataFlowItem item = (DataFlowItem) v;
                        
                            if (item != null && item.reachable) {
                                // n is reachable.
                                return n.reachable(true);
                            }                    
                        }
                    }
                }
                
                // if we fall through to here, then no peer for n was reachable.
                n = n.reachable(false);                
            }
        }        
        return n;
    }
    
    public void post(FlowGraph graph, Term root) {
        // There is no need to do any checking in this method, as this will
        // be handled by leaveCall and checkReachability.
        if (reporter.should_report(Reporter.cfg, 2)) {
            dumpFlowGraph(graph, root);
        }
    }

    public void check(FlowGraph graph, Term n, boolean entry, 
            Item inItem, Map<EdgeKey, Item> outItems) {
        throw new InternalCompilerError("ReachChecker.check should " +
                "never be called.");
    }
    
}
