/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2007 IBM Corporation
 *
 */

package polyglot.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.frontend.Compiler;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.FlowGraph.EdgeKey;
import x10.ast.Finish;
import x10.ast.ParExpr;
import x10.ast.X10ClassDecl;
import x10.ast.Async_c;
import x10.ast.Finish_c;
import x10.ast.X10Cast;
import x10.errors.Errors;
import x10.extension.X10Ext_c;
import x10.types.X10LocalDef;
import polyglot.types.TypeSystem;
import x10.visit.Lowerer;
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
public class InitChecker extends DataFlow
{
    public InitChecker(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf,
              true /* forward analysis */,
              false /* perform dataflow when leaving CodeDecls, not when entering */);
    }

    protected ClassBodyInfo currCBI = new ClassBodyInfo();

    /**
     * This class is just a data structure containing relevant information
     * needed for performing initialization checking of a class declaration.
     *
     * These objects form a stack, since class declarations can be nested.
     */
    protected static class ClassBodyInfo {
        /**
         * The info for the outer ClassBody. The <code>ClassBodyInfo</code>s
         * form a stack.
         */
        public ClassBodyInfo outer = null;

        /** The current CodeNode being processed by the dataflow equations */
        public CodeNode currCodeDecl = null;

        /** The current class being processed. */
        public ClassDef currClass = null;


        /**
         * Set of LocalInstances from the outer class body that were used
         * during the declaration of this class. We need to track this
         * in order to correctly populate <code>localsUsedInClassBodies</code>
         */
        public Set<LocalDef> outerLocalsUsed = CollectionFactory.newHashSet();

        /**
         * Map from <code>ClassBody</code>s to <code>Set</code>s of
         * <code>LocalInstance</code>s. If localsUsedInClassBodies(C) = S, then
         * the class body C is an inner class declared in the current code
         * declaration, and S is the set of LocalInstances that are defined
         * in the current code declaration, but are used in the declaration
         * of the class C. We need this information in order to ensure that
         * these local variables are definitely assigned before the class
         * declaration of C.
         */
        public Map<Node, Set<LocalDef>> localsUsedInClassBodies =
            new LinkedHashMap<Node, Set<LocalDef>>();

        /**
         * Set of LocalInstances that we have seen declarations for in this
         * class. This set allows us to determine which local instances
         * are simply being used before they are declared (if they are used in
         * their own initialization) or are locals declared in an enclosing
         * class.
         */
        public Set<LocalDef> localDeclarations = CollectionFactory.newHashSet();
    }


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
     * Initialise the FlowGraph to be used in the dataflow analysis.
     * @return null if no dataflow analysis should be performed for this
     *         code declaration; otherwise, an apropriately initialized
     *         FlowGraph.
     */
    protected FlowGraph initGraph(CodeNode code, Term root) {
        currCBI.currCodeDecl = code;
        return super.initGraph(code,root);
    }

    /**
     * Overridden superclass method.
     *
     * Set up the state that must be tracked during a Class Declaration.
     */
    protected NodeVisitor enterCall(Node parent, Node n) {
        if (n instanceof ClassBody) {
            // we are starting to process a class declaration, but have yet
            // to do any of the dataflow analysis.
            ClassBody cb = (ClassBody) n;

            // Add the properties to the class body when initializing.
            if (parent instanceof X10ClassDecl) {
                List<ClassMember> members;
                members = new ArrayList<ClassMember>();
                members.addAll(cb.members());
                members.addAll(((X10ClassDecl) parent).properties());
                cb = cb.members(members);
            }

            // set up the new ClassBodyInfo, and make sure that it forms
            // a stack.
            ClassDef ct = null;
            if (parent instanceof ClassDecl) {
                ct = ((ClassDecl) parent).classDef();
            }
            else if (parent instanceof New) {
                ct = ((New) parent).anonType();
            }
            if (ct == null) {
                throw new InternalCompilerError("ClassBody found but cannot find the class.", n.position());
            }
            setupClassBody(ct, cb);
        }

        return super.enterCall(n);
    }

    protected Node leaveCall(Node old, Node n, NodeVisitor v) {

        if (n instanceof ClassBody) {
            // Now that we are at the end of the class declaration, and can
            // be sure that all of the initializer blocks have been processed,
            // we can now process the constructors.

            try {
                // todo: is it needed?
                // copy the locals used to the outer scope
                if (currCBI.outer != null) {
                    currCBI.outer.localsUsedInClassBodies.put(n,
                                                  currCBI.outerLocalsUsed);
                }
            }
            finally {
                // pop the stack
                currCBI = currCBI.outer;
            }
        }

        return super.leaveCall(old, n, v);
    }

    protected void setupClassBody(ClassDef ct, ClassBody n) {
        ClassBodyInfo newCDI = new ClassBodyInfo();
        newCDI.outer = currCBI;
        newCDI.currClass = ct;
        currCBI = newCDI;
    }


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

        // record the fact that we have seen the formal declaration
        currCBI.localDeclarations.add(f.localDef());

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
        MinMaxInitCount initCount = res.initStatus.get(ld.localDef());
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
        //}
        //else {
            // the initCount is not null. We now have a problem. Why is the
            // initCount not null? Has this variable been assigned in its own
            // initialization, or is this a declaration inside a loop body?
            // XXX@@@ THIS IS A BUG THAT NEEDS TO BE FIXED.
            // Currently, the declaration "final int i = (i=5);" will
            // not be rejected, as we cannot distinguish between that and
            // "while (true) {final int i = 4;}"
        //}

        // record the fact that we have seen a local declaration
        currCBI.localDeclarations.add(ld.localDef());

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

        Compiler c = job().extensionInfo().compiler();
        c.stats.incrFrequency("InitChecker.check", 1);
        c.stats.startTiming("InitChecker.1","InitChecker.1");

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
            else if (n instanceof ClassBody) {
                checkClassBody(graph, (ClassBody)n, dfIn, dfOut);
            }
        }
        else {
            // this local assign node has not had data flow performed over it.
            // probably a node in a finally block. Just ignore it.
        }

        c.stats.stopTiming();
    }



    private void reportVarNotInit(Name n, Position p) {
        reportError(new Errors.MayNotHaveBeenInitialized(n, p));
    }
    private void reportVarNotInit(NamedVariable f) {
        reportVarNotInit(f.name().id(),f.position());
    }


    /**
     * Check that the local variable <code>l</code> is used correctly.
     */
    protected void checkLocal(LocalDef l,
                              DataFlowItem dfIn,
                              boolean isReachable, Position p) {
        if (!currCBI.localDeclarations.contains(l)) {
            // it's a local variable that has not been declared within
            // this scope. The only way this can arise is from an
            // inner class that is not a member of a class (typically
            // a local class, or an anonymous class declared in a method,
            // constructor or initializer).
            // We need to check that it is a final local, and also
            // keep track of it, to ensure that it has been definitely
            // assigned at this point.
            currCBI.outerLocalsUsed.add(l);
        }
        else {
            MinMaxInitCount initCount = dfIn.initStatus.get(l);
            if (initCount == null // e.g.,  var i:Int = i;
                || initCount.getMin().isZero()) {
                // the local variable may not have been initialized.
                // However, we only want to complain if the local is reachable
                if (isReachable) {
                    reportVarNotInit(l.name(),p);
            	}
            }
        }
    }

    protected void checkLocalInstanceInit(LocalDef li,
                                          DataFlowItem dfIn,
                                          Position pos) {
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
        LocalDef li = ((Local)a.local()).localInstance().def();
        if (!currCBI.localDeclarations.contains(li)) {
            reportError(new Errors.FinalLocalVariableCannotBeAssignedTo(li.name(),a.position()));
        }

        MinMaxInitCount initCount = dfOut.initStatus.get(li);

        if (li.flags().isFinal() && initCount.isIllegalVal()) {
            reportError(new Errors.FinalVariableAlreadyInitialized(li.name(), a.position()));
        }
    }

    /**
     * Check that the set of <code>LocalInstance</code>s
     * <code>localsUsed</code>, which is the set of locals used in the inner
     * class declared by <code>cb</code>
     * are initialized before the class declaration.
     */
    protected void checkClassBody(FlowGraph graph,
                                  ClassBody cb,
                                  DataFlowItem dfIn,
                                  DataFlowItem dfOut) {
        // we need to check that the locals used inside this class body
        // have all been defined at this point.
        Set<LocalDef> localsUsed = currCBI.localsUsedInClassBodies.get(cb);

        if (localsUsed == null) return;

        for (LocalDef li : localsUsed) {
            MinMaxInitCount initCount = dfOut.initStatus.get(li);
            if (!currCBI.localDeclarations.contains(li)) {
                // the local wasn't defined in this scope.
                currCBI.outerLocalsUsed.add(li);
            }
            else if (initCount == null || initCount.getMin().isZero()) {
                // initCount will in general not be null, as the local variable
                // li is declared in the current class; however, if the inner
                // class is declared in the initializer of the local variable
                // declaration, then initCount could in fact be null, as we
                // leave the inner class before we have performed flowLocalDecl
                // for the local variable declaration.

                reportError(new Errors.LocalVariableMustBeInitializedBeforeClassDeclaration(li.name(),cb.position()));
            }
        }
    }
}
