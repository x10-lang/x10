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
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.Position;
import polyglot.util.InternalCompilerError;
import x10.util.CollectionFactory;
import polyglot.visit.FlowGraph.EdgeKey;
import x10.ast.Finish;
import x10.ast.ParExpr;
import x10.ast.Async_c;
import x10.ast.Finish_c;
import x10.ast.X10Cast;
import x10.ast.Closure;
import x10.ast.X10Formal_c;
import x10.ast.X10Formal;
import x10.ast.X10ClassBody_c;
import x10.errors.Errors;
import x10.extension.X10Ext_c;
import x10.types.X10LocalDef;
import x10.types.X10LocalDef_c;
import x10.types.X10LocalInstance;
import polyglot.types.TypeSystem;
import x10.visit.Lowerer;
import x10.visit.CheckEscapingThis;
import x10.util.Synthesizer;

/**
 * Visitor which checks that all local variables must be defined before use,
 * Fields (instance and static) are checked by CheckEscapingThis.
 *
 * The checking of the rules is implemented in the methods leaveCall(Node)
 * and check(FlowGraph, Term, Item, Item).
 *
 Yoav Zibin added:

  Adding finish-async initialization of var/val:
  See XTENLANG-1565.
  I kept the CFG without changes.
  So, an Async_c exit has two incoming edges: from the exit of the body and
  from the exit of the place.
  I changed in InitChecker's data structure:
  for each variable we now keep:
  [minSeq,maxSeq,minAsync,maxAsync]
  minSeq - the minimal number of times the variable is assigned in sequential code.
  maxSeq - the maximal number of times the variable is assigned in sequential code.
  minAsync - the minimal number of times the variable is assigned (in async or sequential code).
  maxAsync - the maximal number of times the variable is assigned (in async or sequential code).
  We keep the invariant that: minSeq<=minAsync && maxSeq<=maxAsync
  You can read from a variable only if minSeq>=1.
  A val at the end of a ctor must have: [1,1,1,1]

  Note that for our purposes, [0,0,1,1] and [0,1,1,1] carry the same information and restriction.
  (After a finish, both will become the same: [1,1,0,0].)

  Just having a boolean flag (i.e, [min,max,isAsync]) is ok, but I think it is less clear
  because of this example:
  var k:Int;
  k=1;
  async { k=2; }
  // can read from "k". The representation with a flag must be: [2,2,false]
  So, the meaning of the flag is: isAsync=false means that at least one assignment was
  in sequential code.
  Therefore, I prefer the representation as a quartet: [minSeq,maxSeq,minAsync,maxAsync]

  For example, in this simple program the flow is as follows:
  val i:Int;
  // i=[0,0,0,0]
  finish {
    // i=[0,0,0,0]
    async {
      i=42;
      // i=[1,1,1,1]
    }
    // i=[0,0,1,1]
  }
  // i=[1,1,1,1]

  Here is a more complicated example:
         var i:Int, j:Int, k:Int;
         val m:Int, n:Int, q:Int;

         i=1;
         // i:[1,1,1,1]
         finish {
             m=2;
             // m:[1,1,1,1]
             if (true) {
                 async {
                     n=3; i=4; j=5; k=6; q=7;
                     // n:[1,1,1,1] i:[2,2,2,2] j:[1,1,1,1] k:[1,1,1,1] q:[1,1,1,1]
                 }
                 // n:[0,0,1,1] i:[1,1,2,2] j:[0,0,1,1] k:[0,0,1,1] q:[0,0,1,1]
                 k=8;
                 // k:[1,1,2,2]
             } else {
                 // n:[0,0,0,0] m:[1,1,1,1] i:[1,1,1,1] j:[0,0,0,0] k:[0,0,0,0] q:[0,0,0,0]
                 n=9; m=10;
                 // n:[1,1,1,1] m:[2,2,2,2]
             }
             // k:[0,1,0,2] n:[0,1,1,1] m:[1,2,1,2] i:[1,1,1,2] j:[0,0,0,1] q:[0,0,0,1]
             k=11;
             // k:[1,2,1,3]
         }
         // k:[1,3,1,3] n:[1,1,1,1] m:[1,2,1,2] i:[1,2,1,2] j:[0,1,0,1] q:[0,1,0,1]
         j=12;
         // j:[1,2,1,2]
         // all (except q) are definitely-assigned now. "m" was assigned too many times.


  The exact rules are:
  Consider a join between two elements: [a,b,c,d] and [a',b',c',d'].
  1) If join (like at the end of an IF): [min(a,a'), max(b,b'), min(c,c'), max(d,d')]
  2) join at the end of an Async_c(PLACE,BODY):  suppose that the first element has flown from
     the BODY and the second has flown from the PLACE (so a>=a', b>=b', ...).
     Then, the result is: [a',b', c, d]
  3) join at the end of a Finish_c: you have only one incoming edge, and the result is: [c,d,c,d]

  Another way to write down these rules:
  S [a,b,c,d]
  S' [a',b',c',d']
  0) S; S'  [a+a', b+b', c+c', d+d']
  1) if S else S [min(a,a'), max(b,b'), min(c,c'), max(d,d')]
  2) async S [0,0,c,d]
  3) finish S [c,d,c,d]

  For example (see variables "n", "i"):
  IfJoin([0,0,1,1],[1,1,1,1]) = [0,1,1,1]
  AsyncJoin([2,2,2,2],[1,1,1,1]) = [1,1,2,2]
  FinishJoin([0,1,1,1]) = [1,1,1,1]
 */
public final class InitChecker extends DataFlow
{
    public static long TIME = 0;
    public static int ASYNC_INIT_COUNT = 0;

    public InitChecker(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf,
              true /* forward analysis */,
              true /* perform dataflow when when entering */);
    }

    private Map<Object, Set<LocalDef>> initLocals = new HashMap<Object, Set<LocalDef>>();

    /**
     * Class representing the initialization counts of variables. The
     * different values of the counts that we are interested in are ZERO,
     * ONE and MANY.
     */
    public enum InitCount {
        ZERO(0,"0"), ONE(1,"1"), MANY(2,"many");

        public final int count;
        private final String name;
        private InitCount(int i, String name) {
            count = i;
            this.name = name;
        }
        public boolean isZero() { return this==ZERO; }
        public boolean isOne() { return this==ONE; }

        public String toString() {
            return name;
        }

        public InitCount increment() {
            if (count == 0) {
                return ONE;
            }
            return MANY;
        }
        public InitCount min(InitCount b) {
            return fromNum(Math.min(count,b.count));
        }
        public InitCount max(InitCount b) {
            return fromNum(Math.max(count,b.count));
        }
        public InitCount add(InitCount b) {
            return fromNum(count+b.count);
        }
        private static InitCount fromNum(int i) {
            assert i>=0 : i;
            switch (i) {
                case 0: return ZERO;
                case 1: return ONE;
                default: return MANY;
            }
        }
    }

    /**
     * Class to record counts of the minimum and maximum number of times
     * a local has been initialized or assigned to.
     */
    public static class MinMaxInitCount {
        public final static MinMaxInitCount ZERO =
            new MinMaxInitCount(InitCount.ZERO,InitCount.ZERO,InitCount.ZERO,InitCount.ZERO);
        public final static MinMaxInitCount ONE =
            new MinMaxInitCount(InitCount.ONE,InitCount.ONE,InitCount.ONE,InitCount.ONE);

        private final boolean wasRead; // used in CheckEscapingThis for fields read on NonEscaping methods (not used for locals in InitChecker)
        private final InitCount minSeq, maxSeq, minAsync, maxAsync;

        public MinMaxInitCount(InitCount minSeq, InitCount maxSeq,InitCount minAsync, InitCount maxAsync) {
            this(minSeq, maxSeq,minAsync,maxAsync,false);
        }
        public MinMaxInitCount(InitCount minSeq, InitCount maxSeq,InitCount minAsync, InitCount maxAsync, boolean wasRead) {
            this.minSeq = minSeq;
            this.maxSeq = maxSeq;
            this.minAsync = minAsync;
            this.maxAsync = maxAsync;
            this.wasRead = wasRead;
            assert minSeq.count<=minAsync.count && maxSeq.count<=maxAsync.count;
        }
        public MinMaxInitCount increment() { // when a variable is sequentially assigned
            return new MinMaxInitCount(minSeq.increment(),maxSeq.increment(),minAsync.increment(),maxAsync.increment(), wasRead);
        }
        public InitCount getMin() { return minSeq; }
        public boolean isZero() {
            return equals(ZERO);
        }
        public boolean isOne() {
            return equals(ONE);
        }
        public boolean isIllegalVal() {
            return !isOne();
        }
        public int hashCode() {
            return (wasRead?256:0) + minSeq.hashCode() * 64 + maxSeq.hashCode() * 16 + minAsync.hashCode() * 4 + maxAsync.hashCode();
        }
        public String toString() {
            return "[ min: " + minSeq + "; max: " + maxSeq + "; minAsync: " + minAsync + "; maxAsync: " + maxAsync + "; wasRead="+wasRead+"]";
        }
        public boolean equals(MinMaxInitCount maxInitCount) {
            return     this.wasRead==maxInitCount.wasRead &&
                       this.minSeq==maxInitCount.minSeq &&
                       this.maxSeq==maxInitCount.maxSeq &&
                       this.minAsync==maxInitCount.minAsync &&
                       this.maxAsync==maxInitCount.maxAsync;
        }
        public boolean equals(Object o) {
            if (o instanceof MinMaxInitCount) {
                return equals((MinMaxInitCount) o);
            }
            return false;
        }
        public boolean isAsynInit() {
            return minSeq!=minAsync || maxSeq!=maxAsync;
        }
        public MinMaxInitCount finish() {
            return new MinMaxInitCount(minAsync,maxAsync,minAsync,maxAsync, wasRead);//[c,d,c,d]
        }
        public boolean isRead() { return wasRead; }
        public boolean isWrite() { return minAsync!=InitCount.ZERO; }
        public boolean isSeqWrite() { return minSeq!=InitCount.ZERO; }
        public static MinMaxInitCount build(boolean read,boolean write,boolean seqWrite) {
            final InitCount seqW = seqWrite ? InitCount.ONE : InitCount.ZERO;
            final InitCount w = write ? InitCount.ONE : InitCount.ZERO;
            return new MinMaxInitCount(seqW,seqW, w,w, read); 
        }
        public MinMaxInitCount afterAssign() { return increment(); }
        public MinMaxInitCount afterRead() {
            return new MinMaxInitCount(minSeq, maxSeq,minAsync,maxAsync,wasRead || minSeq==InitCount.ZERO);
        }
        public MinMaxInitCount afterSeqBlock(MinMaxInitCount after) {
            return new MinMaxInitCount(
                    minSeq.add(after.minSeq),
                    maxSeq.add(after.maxSeq),
                    minAsync.add(after.minAsync),
                    maxAsync.add(after.maxAsync),
                    wasRead || (!isSeqWrite() && after.wasRead));
        }
        public MinMaxInitCount afterAsync(MinMaxInitCount initCount2, boolean isUncounted) {
            MinMaxInitCount initCount1 = this;

            // one flow must be smaller than the other (the one coming from the entry of the Async is
            // smaller or equal to the one coming after the BODY)
            MinMaxInitCount small =
                            // is initCount1 the min?
                    initCount1.minSeq.count<initCount2.minSeq.count ? initCount1 :
                    initCount1.maxSeq.count<initCount2.maxSeq.count ? initCount1 :
                    initCount1.minAsync.count<initCount2.minAsync.count ? initCount1 :
                    initCount1.maxAsync.count<initCount2.maxAsync.count ? initCount1 :
                            // is initCount2 the min?
                    initCount1.minSeq.count>initCount2.minSeq.count ? initCount2 :
                    initCount1.maxSeq.count>initCount2.maxSeq.count ? initCount2 :
                    initCount1.minAsync.count>initCount2.minAsync.count ? initCount2 :
                    initCount1.maxAsync.count>initCount2.maxAsync.count ? initCount2 :
                            initCount1; // both are equal, so we just choose initCount1
            MinMaxInitCount big = small==initCount1 ? initCount2 : initCount1;
            assert small.minSeq.count<=big.minSeq.count &&
                    small.maxSeq.count<=big.maxSeq.count &&
                    small.minAsync.count<=big.minAsync.count &&
                    small.maxAsync.count<=big.maxAsync.count;
            final MinMaxInitCount res = new MinMaxInitCount(
                    small.minSeq, small.maxSeq,
                    isUncounted ? small.minAsync : big.minAsync, big.maxAsync,
                    small.wasRead || big.wasRead);
            return res; // [a',b', c, d]
        }
        public MinMaxInitCount afterIf(MinMaxInitCount o) {
            // normal join: [min(a,a'), max(b,b'), min(c,c'), max(d,d')]
            return new MinMaxInitCount(
                    minSeq.min(o.minSeq),
                    maxSeq.max(o.maxSeq),
                    minAsync.min(o.minAsync),
                    maxAsync.max(o.maxAsync),
                    wasRead || o.wasRead);
        }

        public static MinMaxInitCount join(TypeSystem xts, VarDef v, Term node, boolean entry, MinMaxInitCount initCount1, MinMaxInitCount initCount2) {
            assert !(node instanceof Finish);
            if (initCount1 == null) return initCount2;
            if (initCount2 == null) return initCount1;

            if (!entry && node instanceof Async_c) {
                Async_c async = (Async_c) node;

                boolean isUncounted = Lowerer.isUncountedAsync(xts,async);
                //@Uncounted async S
                //is treated like this:
                //async if (flag) S
                //so the statement in S might or might not get executed.
                //Therefore even after a "finish" we still can't use anything assigned in S.

                if (!initCount1.equals(initCount2) && v instanceof X10LocalDef) {
                    ((X10LocalDef)v).setAsyncInit();
                }

                return initCount1.afterAsync(initCount2,isUncounted);
            }
            // normal join
            return initCount1.afterIf(initCount2);

        }
    }

    /**
     * Dataflow items for this dataflow are maps of VarInstances to counts
     * of the min and max number of times those locals have
     * been initialized. These min and max counts are then used to determine
     * if variables have been initialized before use, and that final variables
     * are not initialized too many times.
     *
     * This class is immutable.
     */
    public static class BaseDataFlowItem<T extends VarDef> extends Item {
        public final Map<T, MinMaxInitCount> initStatus = CollectionFactory.newHashMap(); // map of VarDef to MinMaxInitCount

        public String toString() {
            return initStatus.toString();
        }

        public boolean equals(Object o) {
            if (o instanceof BaseDataFlowItem) {
                return this.initStatus.equals(((BaseDataFlowItem)o).initStatus);
            }
            return false;
        }

        public int hashCode() {
            return (initStatus.hashCode());
        }

    }
    public static class DataFlowItem extends BaseDataFlowItem<LocalDef> {}

    /**
     * Construct a flow graph for the <code>Expr</code> provided, and call
     * <code>dataflow(FlowGraph)</code>. Is also responsible for calling
     * <code>post(FlowGraph, Term)</code> after
     * <code>dataflow(FlowGraph)</code> has been called.
     * There is no need to push a CFG onto the stack, as dataflow is not
     * performed on entry in this analysis.
     */
    protected void dataflow(Expr root) {
        // Build the control flow graph.
        FlowGraph g = new FlowGraph(root, forward);
        CFGBuilder v = createCFGBuilder(ts, g);
        v.visitGraph();
        dataflow(g);
        post(g, root);
    }

    public Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
        return createInitDFI();
    }

    private DataFlowItem createInitDFI() {
        return new DataFlowItem();
    }

    /**
     * The confluence operator for <code>Initializer</code>s and
     * <code>Constructor</code>s needs to be a
     * little special, as we are only concerned with non-exceptional flows in
     * these cases.
     * This method ensures that a slightly different confluence is performed
     * for these <code>Term</code>s, otherwise
     * <code>confluence(List, Term)</code> is called instead.
     */
    protected Item confluence(List<Item> items, List<EdgeKey> itemKeys,
            Term node, boolean entry, FlowGraph graph) {
        if (node instanceof Initializer || node instanceof ConstructorDecl) {
            List<Item> filtered = filterItemsNonException(items, itemKeys);
            if (filtered.isEmpty()) {
                return createInitDFI();
            }
            else if (filtered.size() == 1) {
                return (Item)filtered.get(0);
            }
            else {
               return confluence(filtered, node, entry, graph);
            }
        }
        return confluence(items, node, entry, graph);
    }

    /**
     * The confluence operator is essentially the union of all of the
     * inItems. However, if two or more of the initCount maps from
     * the inItems each have a MinMaxInitCounts entry for the same
     * VarInstance, the conflict must be resolved, by using the
     * minimum of all mins and the maximum of all maxs.
     */
    public Item confluence(List<Item> inItems, Term node, boolean entry, FlowGraph graph) {
        // Resolve any conflicts pairwise.
        final DataFlowItem res = new DataFlowItem();
        Map<LocalDef, MinMaxInitCount> m = null;
        for (Item itm : inItems) {
            if (m == null) {
                m = res.initStatus;
                m.putAll(((DataFlowItem)itm).initStatus);
            }
            else {
                Map<LocalDef, MinMaxInitCount> n = ((DataFlowItem)itm).initStatus;
                for (Map.Entry<LocalDef, MinMaxInitCount> e : n.entrySet()) {
                    LocalDef v = e.getKey();
                    MinMaxInitCount initCount1 = m.get(v);
                    MinMaxInitCount initCount2 = (MinMaxInitCount)e.getValue();
                    m.put(v, MinMaxInitCount.join((TypeSystem)ts,v,node,entry,initCount1, initCount2));
                }
            }
        }
        return res;
    }

    protected Map<EdgeKey, Item> flow(List<Item> inItems, List<EdgeKey> inItemKeys,
            FlowGraph graph, Term n, boolean entry, Set<EdgeKey> edgeKeys) {
        return this.flowToBooleanFlow(inItems, inItemKeys, graph, n, entry, edgeKeys);
    }

    /**
     * Perform the appropriate flow operations for the Terms. This method
     * delegates to other appropriate methods in this class, for modularity.
     *
     * To summarize:
     * - Formals: declaration of a Formal param, just insert a new
     *              MinMaxInitCount for the LocalInstance.
     * - LocalDecl: a declaration of a local variable, just insert a new
     *              MinMaxInitCount for the LocalInstance as appropriate
     *              based on whether the declaration has an initializer or not.
     * - Assign: if the LHS of the assign is a local var, then increment the min and max counts
     */
    public Map<EdgeKey, Item> flow(Item trueItem, Item falseItem, Item otherItem,
            FlowGraph graph, Term n, boolean entry, Set<EdgeKey> succEdgeKeys) {

        Map<EdgeKey, Item> res = flow_(trueItem,falseItem,otherItem,graph,n,entry, succEdgeKeys);
        if (entry) return res;

        // We also need to mark the variable as "initialized in async" to allow
        // the backend to implement such initializations.
        final FlowGraph.Peer peer = graph.peer(n, Term.ENTRY);
        final Item inItem = peer.inItem();
        if (inItem==null) return res;
        final DataFlowItem in = (DataFlowItem) inItem;
        final X10Ext_c ext = (X10Ext_c) n.ext();
        for (Map.Entry<LocalDef, MinMaxInitCount> e : in.initStatus.entrySet()) {
            final MinMaxInitCount before = e.getValue();
            final LocalDef v = e.getKey();
            for (Item outItem : res.values()) {
                if (outItem==null) continue;
                final DataFlowItem out = (DataFlowItem) outItem;
                final MinMaxInitCount after = out.initStatus.get(v);
                if (after==null) continue;
                final Flags flags = v.flags();
                if (!before.equals(after) && after.isOne() && flags !=null && flags.isFinal()) {
                    if (ext.initVals ==null) ext.initVals = CollectionFactory.newHashSet();
                    ext.initVals.add(v);
                    break; // optimization, cause we already added "v"
                }
            }
        }
        return res;
    }
    public Map<EdgeKey, Item> flow_(Item trueItem, Item falseItem, Item otherItem,
            FlowGraph graph, Term n, boolean entry, Set<EdgeKey> succEdgeKeys) {
        Item inItem = safeConfluence(trueItem, FlowGraph.EDGE_KEY_TRUE,
                                     falseItem, FlowGraph.EDGE_KEY_FALSE,
                                     otherItem, FlowGraph.EDGE_KEY_OTHER,
                                     n, entry, graph);
        if (entry) {
            return itemToMap(inItem, succEdgeKeys);
        }

        DataFlowItem inDFItem = ((DataFlowItem)inItem);
        Map<EdgeKey, Item> ret = null;
        if (n instanceof Formal) {
            // formal argument declaration.
            ret = flowFormal(inDFItem, graph, (Formal)n, succEdgeKeys);
        }
        else if (n instanceof LocalDecl) {
            // local variable declaration.
            ret = flowLocalDecl(inDFItem, graph, (LocalDecl)n, succEdgeKeys);
        }
        else if (n instanceof LocalAssign) {
            // assignment to a local variable
            ret = flowLocalAssign(inDFItem, graph, (LocalAssign)n, succEdgeKeys);
        }
        else if (n instanceof Expr && ((Expr)n).type().isBoolean() &&
                    (n instanceof Binary || n instanceof Unary)) {
            if (trueItem == null) trueItem = inDFItem;
            if (falseItem == null) falseItem = inDFItem;
            ret = flowBooleanConditions(trueItem, falseItem, inDFItem, graph, (Expr)n, succEdgeKeys);
        } else if (n instanceof ParExpr && ((ParExpr)n).type().isBoolean()) {
            if (trueItem == null) trueItem = inDFItem;
            if (falseItem == null) falseItem = inDFItem;
            return itemsToMap(trueItem, falseItem, inDFItem, succEdgeKeys);
        } else if (n instanceof Finish_c) {
            final DataFlowItem res = new DataFlowItem();
            for (Map.Entry<LocalDef, MinMaxInitCount> e : inDFItem.initStatus.entrySet()) {
                final MinMaxInitCount before = e.getValue();
                final MinMaxInitCount after = before.finish();
                final LocalDef v = e.getKey();
                res.initStatus.put(v, after);
                if (CheckEscapingThis.GATHER_STATS && before.isAsynInit() && v.flags().isFinal()) {
                    System.out.println("Async local init="+v.position());
                    ASYNC_INIT_COUNT++;
                }
            }
            return itemToMap(res, succEdgeKeys);
        }
        if (ret != null) {
            return ret;
        }
        return itemToMap(inItem, succEdgeKeys);
    }

    /**
     * Perform the appropriate flow operations for declaration of a formal
     * parameter
     */
    protected Map<EdgeKey, Item> flowFormal(DataFlowItem inItem, FlowGraph graph, Formal f,
            Set<EdgeKey> succEdgeKeys) {
        final DataFlowItem res = new DataFlowItem();
        res.initStatus.putAll(inItem.initStatus);
        // a formal argument is always defined.
        res.initStatus.put(f.localDef(), MinMaxInitCount.ONE);
        for (Formal ff : ((X10Formal)f).vars())
            res.initStatus.put(ff.localDef(), MinMaxInitCount.ONE);            
        return itemToMap(res, succEdgeKeys);
    }

    /**
     * Perform the appropriate flow operations for declaration of a local
     * variable
     */
    protected Map<EdgeKey, Item> flowLocalDecl(DataFlowItem inItem,
                                FlowGraph graph,
                                LocalDecl ld,
                                Set<EdgeKey> succEdgeKeys) {
        final DataFlowItem res = new DataFlowItem();
        res.initStatus.putAll(inItem.initStatus);
        MinMaxInitCount initCount;
        //if (initCount == null) {
            if (ld.init() != null) {
                // declaration of local var with initialization.
                initCount = MinMaxInitCount.ONE;
            }
            else {
                // declaration of local var with no initialization.
                initCount = MinMaxInitCount.ZERO;
            }

            res.initStatus.put(ld.localDef(), initCount);

        return itemToMap(res, succEdgeKeys);
    }

    /**
     * Perform the appropriate flow operations for assignment to a local
     * variable
     */
    protected Map<EdgeKey, Item> flowLocalAssign(DataFlowItem inItem,
                                  FlowGraph graph,
                                  LocalAssign a,
                                  Set<EdgeKey> succEdgeKeys) {
          Local l = (Local) a.local();
          final DataFlowItem res = new DataFlowItem();
          res.initStatus.putAll(inItem.initStatus);
          MinMaxInitCount initCount = res.initStatus.get(l.localInstance().def());

          // initcount could be null if the local is defined in the outer
          // class, or if we have not yet seen its declaration (i.e. the
          // local is used in its own initialization)
          if (initCount == null) {
              initCount = MinMaxInitCount.ZERO;
          }

          initCount = initCount.increment();

          res.initStatus.put(l.localInstance().def(), initCount);
          return itemToMap(res, succEdgeKeys);
    }


    /**
     * Check that the conditions of initialization are not broken.
     *
     * To summarize the conditions:
     * - Local variables must be initialized before use, (i.e. min count > 0)
     * - Final local variables (including Formals) cannot be assigned to more
     *               than once (i.e. max count <= 1)
     */
    public void check(final FlowGraph graph, Term n, boolean entry, Item inItem,
            Map<EdgeKey, Item> outItems) {
        long start = System.currentTimeMillis();
        DataFlowItem dfIn = (DataFlowItem)inItem;
        if (dfIn == null) {
            // There is no input data flow item. This can happen if we are
            // checking an unreachable term, and so no Items have flowed
            // through the term. For example, in the code fragment:
            //     a: do { break a; } while (++i < 10);
            // the expression "++i < 10" is unreachable, but the as there is
            // no unreachable statement, the Java Language Spec permits it.

            // Set inItem to a default Item
            dfIn = createInitDFI();
        }

        x10.ExtensionInfo x10Info = (x10.ExtensionInfo) job().extensionInfo();
        x10Info.stats.incrFrequency("InitChecker.check", 1);
        x10Info.stats.startTiming("InitChecker.1","InitChecker.1");

        DataFlowItem dfOut;
        if (!entry && outItems != null && !outItems.isEmpty()) {
            // due to the flow equations, all DataFlowItems in the outItems map
            // are the same, so just take the first one.
            dfOut = (DataFlowItem)outItems.values().iterator().next();

            if (n instanceof Local) {
                final Local l = (Local) n;
                checkLocal(l.localInstance().def(), dfIn, l.reachable(), n.position());
            } else if (n instanceof X10Cast) {
                X10Cast cast = (X10Cast)n;
                // convert constraint to Expr
                final Set<VarDef> exprs = Synthesizer.getLocals(cast.castType());
                for (VarDef e : exprs)
                    if (e instanceof LocalDef)
                        checkLocal((LocalDef)e, dfIn, true, n.position());
            }
            else if (n instanceof LocalAssign) {
                checkLocalAssign(graph, (LocalAssign)n, dfIn, dfOut);
            }
            else if (n instanceof Closure) {
                checkCode(getCurrKey(n), dfIn);

            } else if (n instanceof ClassBody) {
                ClassBody cb = (ClassBody) n;
                if (cb.members().size()>0) {
                    checkCode(getCurrKey(cb.members().get(0)), dfIn);
                }
            }
        }
        else {
            // this local assign node has not had data flow performed over it.
            // probably a node in a finally block. Just ignore it.
        }

        x10Info.stats.stopTiming();

        TIME += Math.abs(System.currentTimeMillis()-start);
    }



    private void reportVarNotInit(Name n, Position p) {
        reportError(new Errors.MayNotHaveBeenInitialized(n, p));
    }


    /**
     * Check that the local variable <code>l</code> is used correctly.
     */
    protected void checkLocal(LocalDef l,
                              DataFlowItem dfIn,
                              boolean isReachable, Position p) {
        if (((X10LocalInstance)l.asInstance()).error()!=null) return;
        if (l instanceof X10LocalDef_c && ((X10LocalDef_c) l).hidden()) return;
        MinMaxInitCount initCount = dfIn.initStatus.get(l);
        if (initCount == null) {
            // check the outer local was init
            Set<LocalDef> initDefs = getInitDefs();
            if ((l.flags().isFinal()) && // "var" fields are already reported: "Local variable is accessed from an inner class or a closure, and must be declared final."
                (initDefs==null || !initDefs.contains(l))) {
                reportError(new Errors.LocalVariableMustBeInitializedBeforeClassDeclaration(l.name(),p));                
            }
        } else if (initCount.getMin().isZero()) {
            // the local variable may not have been initialized.
            // However, we only want to complain if the local is reachable
            if (isReachable) {
                reportVarNotInit(l.name(),p);
            }
        }
    }

    protected void checkLocalInstanceInit(LocalDef li,
                                          DataFlowItem dfIn,
                                          Position pos) {
        if (((X10LocalInstance)li.asInstance()).error()!=null) return;
        MinMaxInitCount initCount = dfIn.initStatus.get(li);
        if (initCount != null && initCount.getMin().isZero()) {
            // the local variable may not have been initialized.
            reportVarNotInit(li.name(), pos);
	    }
    }

    /**
     * Check that the assignment to a local variable is correct.
     */
    protected void checkLocalAssign(FlowGraph graph,
                                    LocalAssign a,
                                    DataFlowItem dfIn,
                                    DataFlowItem dfOut) {
        X10LocalInstance instance = (X10LocalInstance) ((Local) a.local()).localInstance();
        if (instance.error()!=null) return;
        LocalDef li = instance.def();
        MinMaxInitCount initCount = dfIn.initStatus.get(li);
        if (initCount==null) {
            reportError(new Errors.FinalLocalVariableCannotBeAssignedTo(li.name(),a.position()));
        } else if (li.flags().isFinal() && !initCount.isZero()) {
            reportError(new Errors.FinalVariableAlreadyInitialized(li.name(), a.position()));
        }
    }

    /**
     * Check that the set of <code>LocalInstance</code>s
     * <code>localsUsed</code>, which is the set of locals used in the inner
     * class declared by <code>cb</code>
     * are initialized before the class declaration.
     */
    protected void checkCode(Object key,DataFlowItem dfIn) {
        Set<LocalDef> initDefs = getInitDefs();
        if (initDefs==null) initDefs = Collections.EMPTY_SET;
        Set<LocalDef> cbInitDefs = new HashSet<LocalDef>(initDefs);
        for (Map.Entry<LocalDef, MinMaxInitCount> en : dfIn.initStatus.entrySet()) {
            final LocalDef def = en.getKey();
            if (def.flags().isFinal() && en.getValue().isOne()) {
                cbInitDefs.add(def);
            }
        }
        initLocals.put(key,cbInitDefs);
    }

    private CodeNode currCode = null;
    private Set<LocalDef> getInitDefs() {
        if (currCode==null) return null;
        Object key = getCurrKey(currCode);
        Set<LocalDef> res = initLocals.get(key);
        if (res!=null) return res;
        if (key instanceof ClassDef) {
            ClassDef def = (ClassDef) key;
            while (true) {
                def = Types.get(def.outer());
                if (def==null) return null;
                res = initLocals.get(def);
                if (res!=null) return res;
            }
        }
        return null;
    }
    private Object getCurrKey(Node currCode) { // either ClassDef or Closure
        if (currCode instanceof ClassMember) {
            return ((ClassType)Types.get(((ClassMember)currCode).memberDef().container())).def();
        }
        if (currCode instanceof Closure)
            return currCode;
        throw new InternalCompilerError("Unexpected currCode="+currCode);
    }
    protected FlowGraph initGraph(CodeNode code, Term root) {
        currCode = code;
        return super.initGraph(code, root);
    }
}
