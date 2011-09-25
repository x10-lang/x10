package x10.visit;

import polyglot.ast.*;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.NodeVisitor;
import polyglot.visit.DataFlow;
import polyglot.visit.FlowGraph;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InitChecker;
import polyglot.visit.DataFlow.Item;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.FieldInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.ClassDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Context;
import polyglot.types.ProcedureDef_c;
import polyglot.types.Types;
import x10.ast.*;
import x10.errors.Errors;
import polyglot.types.TypeSystem;
import polyglot.types.VarDef;
import polyglot.types.LocalDef;
import polyglot.types.ClassType;
import polyglot.types.ContainerType;
import x10.types.X10FieldDef;

import x10.types.MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10MethodDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldDef_c;
import x10.types.checker.ThisChecker;
import x10.util.Synthesizer;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Arrays;

import static polyglot.visit.InitChecker.*;

/**
 * Checks correct object initialization, i.e.,
 * - this cannot escape during construction
 * - correct usages of @NonEscaping and @NoThisAccess
 * - fields are not read before they're assigned.
 * - property(...) is called exactly once
 *
 * We do not track:
 * - static fields (see FwdReferenceChecker, and other checks are done in X10FieldAssign_c and X10FieldDecl_c)
 * - locals (see InitChecker)
 */

public class CheckEscapingThis extends NodeVisitor
{
    public static long TIME = 0;
    public final static boolean GATHER_STATS = false;  // Gather statistics for the initialization paper
    private static int ASYNC_INIT_COUNT = 0;
    private static HashSet<X10ProcedureDef> ALL_CTORS = new HashSet<X10ProcedureDef>();
    private static HashSet<X10ProcedureDef> ALL_METHODS = new HashSet<X10ProcedureDef>();
    private static HashSet<X10ProcedureDef> ALL_EXPLICIT_NON_ESCAPING_METHODS = new HashSet<X10ProcedureDef>();
    private static HashSet<X10ProcedureDef> ALL_NON_ESCAPING_METHODS = new HashSet<X10ProcedureDef>();
    private static HashSet<X10ProcedureDef> ALL_NO_THIS_ACCESS = new HashSet<X10ProcedureDef>();

    private static class DataFlowItem extends BaseDataFlowItem<FieldDef> {}

    class FieldChecker extends DataFlow {
        private ProcedureDecl currDecl;
        private DataFlowItem init;
        private DataFlowItem finalResult;
        public FieldChecker(DataFlowItem init) {
            super(CheckEscapingThis.this.job, CheckEscapingThis.this.ts, CheckEscapingThis.this.nf,
                  true /* forward analysis */,
                  false /* perform dataflow when leaving CodeDecls, not when entering */);
            this.init = init;
        }
        protected Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
            return init;
        }
        protected Item confluence(List<Item> items, List<EdgeKey> itemKeys,
            Term node, boolean entry, FlowGraph graph) {
            if (node instanceof ProcedureDecl) {
                List<Item> filtered = filterItemsNonException(items, itemKeys);
                if (filtered.isEmpty()) {
                    // see XTENLANG-1851
                    DataFlowItem res = new DataFlowItem();
                    for (FieldDef d : init.initStatus.keySet())
                        res.initStatus.put(d, MinMaxInitCount.ONE);
                    return res;
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
        protected Item confluence(List<Item> items, Term node, boolean entry, FlowGraph graph) {
            //if (items.size()==0) return INIT;
            //if (items.size()==1) return (DataFlowItem)items.get(0);
            assert items.size()>=2;

            boolean isAsync = !entry && node instanceof Async;
            boolean isUncounted = isAsync ? Lowerer.isUncountedAsync((TypeSystem)ts,(Async) node) : false;
            DataFlowItem res = new DataFlowItem();
            res.initStatus.putAll(((DataFlowItem) items.get(0)).initStatus);

            for (int i=1; i<items.size(); i++) {
                DataFlowItem item = (DataFlowItem) items.get(i);
                for (Map.Entry<FieldDef, MinMaxInitCount> pair : item.initStatus.entrySet()) {
                    final FieldDef key = pair.getKey();
                    MinMaxInitCount i1 = res.initStatus.get(key);
                    MinMaxInitCount i2 = pair.getValue();
                    // Async must be handled here, because DataFlow first calls safeConfluence, and only then it calls flow:
                    //p.inItem = this.safeConfluence(...
                    //p.outItems = this.flow(...

                    MinMaxInitCount i_res = isAsync ? i1.afterAsync(i2,isUncounted) : i1.afterIf(i2);
                    res.initStatus.put(key,i_res);
                }
            }
            return res;
        }
        protected FlowGraph initGraph(CodeNode code, Term root) {
            currDecl = (ProcedureDecl)code; // we do not analyze closures (Closure_c) since we require that "this" does not escape there
            return super.initGraph(code,root);
        }

        protected Map<EdgeKey, Item> flow(List<Item> inItems, List<EdgeKey> inItemKeys, FlowGraph graph,
                Term n, boolean entry, Set<EdgeKey> edgeKeys) {
            return this.flowToBooleanFlow(inItems, inItemKeys, graph, n, entry, edgeKeys);
        }
        private DataFlowItem fieldReadWrite(boolean isAssign, FieldDef field, DataFlowItem inItem) {
            DataFlowItem res = new DataFlowItem();
            res.initStatus.putAll(inItem.initStatus);
            final MinMaxInitCount valueBefore = inItem.initStatus.get(field);
            if (valueBefore!=null) { // can happen for fake fields, e.g., class Bar { val q= this.f; }
                MinMaxInitCount valueAfter = isAssign ? valueBefore.afterAssign() : valueBefore.afterRead();
                res.initStatus.put(field,valueAfter);
            }
            return res;
        }
        public Map<EdgeKey, Item> flow(Item trueItem, Item falseItem, Item otherItem,
                    FlowGraph graph, Term n, boolean entry, Set<EdgeKey> succEdgeKeys) {
            final DataFlowItem inItem = (DataFlowItem) safeConfluence(trueItem, FlowGraph.EDGE_KEY_TRUE,
                                         falseItem, FlowGraph.EDGE_KEY_FALSE,
                                         otherItem, FlowGraph.EDGE_KEY_OTHER,
                                         n, entry, graph);
            DataFlowItem res = inItem;
            if (trueItem == null) trueItem = inItem;
            if (falseItem == null) falseItem = inItem;
            if (entry) {
                return itemToMap(inItem, succEdgeKeys);
            }

            boolean isAssign = n instanceof FieldAssign;
            if (isAssign || n instanceof Field) {
                final FieldInstance fi = isAssign ? ((FieldAssign) n).fieldInstance() : ((Field) n).fieldInstance();
                FieldDef field = fi.def();
                if (field!=null && (isAssign ? isTargetThis((FieldAssign) n) : isTargetThis((Field) n))) {
                    res = fieldReadWrite(isAssign, field, inItem);
                    if (isAssign) {
                        // make sure we assign to val fields at most once
                        if (field.flags().isFinal()) {
                            // check it is assigned exactly once
                            final MinMaxInitCount maxInitCount = res.initStatus.get(field);
                            if (maxInitCount.isIllegalVal())
                                reportError(new Errors.FinalFieldAlreadyInitialized(field.name(),n.position()));
                        }
                    }
                }

            } else if (n instanceof AssignPropertyCall) {
                res = fieldReadWrite(true, propertyRepresentative,inItem);

            // I don't need to recurse into the constraint of a X10CanonicalTypeNode because:
            // - in STATIC_CHECKS it doesn't generate any code for them, therefore there is nothing to check.
            // e.g. the following is legal: class A { val f1:A{this.f2==f1} = null; val f2:A = null; }
            // - in DYNAMIC_CHECKS it generates a cast, and we recurse into that cast next:
            } else if (n instanceof X10Cast) {
                X10Cast cast = (X10Cast)n;
                // convert constraint to Expr
                final Set<VarDef> exprs = Synthesizer.getLocals(cast.castType());
                for (VarDef e : exprs)
                    if (e instanceof FieldDef) {
                        FieldDef field = (FieldDef) e;
                        if (isTargetThis(field)) {
                            res = fieldReadWrite(false, field, inItem);
                        }
                    }

            } else if (n instanceof ConstructorCall) {
                ConstructorCall ctorCall = (ConstructorCall) n;
                assert (ctorCall.kind()==ConstructorCall.SUPER) : "We do not analyze ctors with 'this()' calls, because they're always correct - everything must be initialized after the 'this()' call";
                //assert res==INIT : "It must be the first statement in the ctor";
                // Note that the super call should be the first statement, but I inline the field-inits before it.

            } else if (n instanceof X10Call) {
                MethodInfo info = getInfo((X10Call) n);
                if (info!=null) {
                    res = new DataFlowItem();
                    res.initStatus.putAll(inItem.initStatus);
                    for (FieldDef field : fields) {
                        boolean isRead = info.read.contains(field);
                        boolean isWrite = info.write.contains(field);
                        boolean isWriteSeq = info.seqWrite.contains(field);
                        final MinMaxInitCount before = res.initStatus.get(field);
                        res.initStatus.put(field, before.afterSeqBlock(MinMaxInitCount.build(isRead,isWrite, isWriteSeq)));
                    }
                }
            } else if (n instanceof Expr && ((Expr)n).type().isBoolean() &&
                    (n instanceof Binary || n instanceof Unary)) {
                final Map<EdgeKey, Item> map = flowBooleanConditions(trueItem, falseItem, inItem, graph, (Expr) n, succEdgeKeys);
                if (map!=null) return map;
            } else if (n instanceof ParExpr && ((ParExpr)n).type().isBoolean()) {
                return itemsToMap(trueItem, falseItem, inItem, succEdgeKeys);
            } else if (n instanceof Finish) {
                res = new DataFlowItem();
                for (Map.Entry<FieldDef, MinMaxInitCount> pair : inItem.initStatus.entrySet()) {
                    MinMaxInitCount before = pair.getValue();
                    res.initStatus.put(pair.getKey(), before.finish());
                    if (GATHER_STATS && before.isAsynInit()) {
                        System.out.println("Async field init="+pair.getKey().position());
                        ASYNC_INIT_COUNT++;
                    }
                }
            }
            if (res!=inItem) {
                MethodInfo info = allMethods.get(currDecl.procedureInstance());
                final boolean ctor = isCtor();
                if (!ctor) assert info!=null : currDecl;
                // can't read from an un-init var in the ctors (I want to catch it here, so I can give the exact position info)
                // can't read from any var if @NoThisAccess
                if (ctor) {
                    for (FieldDef f : fields) {
                        boolean readBefore = inItem.initStatus.get(f).isRead();
                        final MinMaxInitCount fRes = res.initStatus.get(f);
                        if (!readBefore && fRes.isRead()) {
                            // wasn't read before, and we read it now (either because of Field access, or X10Call)
                            reportError(new Errors.CannotReadFromFieldBeforeDefiniteAssignment(f.name(),n.position()));
                            // I want to report more errors with this field, so I remove the read status
                            res.initStatus.put(f,MinMaxInitCount.build(false, fRes.isWrite(),fRes.isSeqWrite()));
                            wasError = true;
                        }
                    }
                }
            }
            return itemToMap(res, succEdgeKeys);
        }
        private boolean isCtor() { return currDecl instanceof ConstructorDecl; }

        @Override
        protected void check(FlowGraph graph, Term n, boolean entry, Item inItem, Map<EdgeKey, Item> outItems) {
            DataFlowItem dfIn = (DataFlowItem)inItem;
            if (dfIn == null) dfIn = init;
            if (n == graph.root() && !entry) {
                assert n==currDecl : n;
                finalResult = dfIn;
            }
        }
        public void checkResult() {
            // finish method/ctor
            ProcedureDecl decl = currDecl;
            assert decl!=null;
            final ProcedureDef procDef = decl.procedureInstance();

            // everything must be assigned in a ctor (and nothing read)
            if (isCtor()) {
                for (FieldDef f : fields) {
                    // a VAR marked with @Uninitialized is not tracked
                    if (!finalResult.initStatus.get(f).isSeqWrite() && !Types.isUninitializedField((X10FieldDef)f,(TypeSystem)ts)) {
                        final Position pos = currDecl.position();
                        // could be an auto-generated ctor
                        wasError = true;
                        reportError(new Errors.FieldNameWasNotDefinitelyAssigned(f.flags().isProperty(), f.name(), pos.isCompilerGenerated() ? f.position() : pos));
                    }
                }
            } else {
                MethodInfo oldInfo = allMethods.get(procDef);
                assert oldInfo!=null : currDecl;
                assert !Types.isNoThisAccess((X10ProcedureDef)procDef,(TypeSystem)ts);


                MethodInfo newInfo = new MethodInfo();
                for (Map.Entry<FieldDef, MinMaxInitCount> pair : finalResult.initStatus.entrySet()) {
                    MinMaxInitCount val = pair.getValue();
                    final FieldDef f = pair.getKey();
                    if (val.isRead()) newInfo.read.add(f);
                    if (!f.flags().isFinal()) {
                        // NonEscaping methods can only write to VAR (not VAL)
                        if (val.isWrite()) newInfo.write.add(f);
                        if (val.isSeqWrite()) newInfo.seqWrite.add(f);
                    }
                }
                // proof that the fix-point terminates: write set decreases while the read set increases
                assert oldInfo.write.containsAll(newInfo.write);
                assert oldInfo.seqWrite.containsAll(newInfo.seqWrite);
                assert newInfo.read.containsAll(oldInfo.read);


                // fixed-point reached?
                if (newInfo.read.equals(oldInfo.read) &&
                    newInfo.write.equals(oldInfo.write) &&
                    newInfo.seqWrite.equals(oldInfo.seqWrite)) {
                    // no change!
                } else {
                    wasChange = true;
                    allMethods.put(procDef,newInfo);
                }
            }
        }
    }
    private MethodInfo getInfo(X10Call call) {
        final X10MethodDef def = (X10MethodDef) call.methodInstance().def();
        if (isTargetThis(call) && findMethod(call)!=null && !Types.isNoThisAccess(def,ts)) {
            final MethodInfo info = allMethods.get(def);
            assert info!=null;
            return info;
        }
        return null;
    }
    private static boolean isProperty(FieldDef def) {
        return def.flags().isProperty();
    }
    private static boolean isProperty(ProcedureDef def) {
        return ((ProcedureDef_c)def).flags().isProperty();
    }
    private static boolean isPrivate(ProcedureDef def) {
        return ((ProcedureDef_c)def).flags().isPrivate();
    }
    private boolean isPrivateOrFinal(ProcedureDef def) {
        if (isXlassFinal) return true;
        final Flags flags = ((ProcedureDef_c)def).flags();
        return flags.isPrivate() || flags.isFinal();
    }

    // Main visits all the classes, and type-checks them (verify that "this" doesn't escape, etc)
    public static class Main extends NodeVisitor {
        private final Job job;
        public Main(Job job) {
            this.job = job;
        }
        @Override public NodeVisitor enter(Node n) {
            if (n instanceof X10ClassDecl_c) {
                final X10ClassDecl_c classDecl_c = (X10ClassDecl_c) n;
                if (!classDecl_c.flags().flags().isInterface()) // I have nothing to analyze in an interface
                    new CheckEscapingThis(classDecl_c,job,
                        job.extensionInfo().typeSystem());
            }
            return this;
        }
    }

    static enum CtorState { Start, SawCtor, SawProperty };
    public class CheckCtor extends NodeVisitor {
        private CtorState state = CtorState.Start;
        private boolean wasSuperCall = false;

        private CheckCtor(X10ConstructorDecl_c ctor) {
            if (getConstructorCall(ctor)==null) {
                // There is no 'this(...)' or 'super(...)' calls, so we implicitly start after a 'super()' call
                state = CtorState.SawCtor;
                wasSuperCall = true;
            }
        }
        private void postCheck() {
            switch (state) {
                case Start:
                    assert false : "There must be a super call (either explicit or implicit)";
                case SawCtor:
                    // We already report if: property(...) might not have been called
                    //if (hasProperties && wasSuperCall)
                    //    reportError("You must call 'property(...)' at least once",ctor.position());
                    break;
            }
        }

        @Override public Node visitEdgeNoOverride(Node parent, Node n) {
            Position pos = n.position();
            if (getMsg(n)!=null) {
                return n; // already checked
            }
            if (!canUseThis() && n instanceof X10Call) {
                final X10Call call = (X10Call) n;
                if (isTargetThis(call)) {
                    if (Types.isNoThisAccess((X10MethodDef)call.methodInstance().def(),ts)) {
                        // && X10TypeMixin.getNonEscapingReadsFrom((X10MethodDef)call.methodInstance().def(),ts)==null) { // @NonEscaping methods cannot write to any fields
                        // even though we use "this.call(...)", this is legal
                        // because the call doesn't read nor write to "this"
                    } else {
                        MethodInfo info = getInfo(call);
                        if (info!=null && info.read.size()==0 && info.write.size()==0) {
                            // ok
                        } else {
                            reportError("You can use 'this' before 'property(...)' to call only @NoThisAccess methods or NonEscaping methods that do not read nor write any fields.",pos);
                        }
                    }
                    for (Expr e : call.arguments())
                        e.visit(this);
                    return n;
                }
            }

            n.del().visitChildren(this);

            if (n instanceof ConstructorCall) {
                ConstructorCall constructorCall = (ConstructorCall) n;
                switch (state) {
                case Start:
                    state = CtorState.SawCtor;
                    wasSuperCall = constructorCall.kind()==ConstructorCall.SUPER;
                    break;
                case SawCtor:
                    reportError("Can only have a single 'this(...)' or 'super(...)' in a constructor",pos);
                    break;
                case SawProperty:
                    reportError("'this(...)' or 'super(...)' must come before 'property(...)'",pos);
                    break;
                }
            } else if (n instanceof AssignPropertyCall) {
                switch (state) {
                case Start:
                    assert false : "implicit super() call is handled at the beginning, see getConstructorCall";
                case SawCtor:
                    if (!wasSuperCall)
                        reportError("You cannot call 'property(...)' after 'this(...)'",pos);
                    else if (!hasProperties) {
                        // This error is already reported: "The property initializer must have the same number of arguments as properties for the class."
                        //reportError("You can call 'property(...)' only if the class defined properties",pos);
                    } else {
                        state = CtorState.SawProperty;
                    }
                    break;
                case SawProperty:
                    reportError("You can call 'property(...)' at most once",pos);
                    break;
                }

            } else if (isThis(n)) {
                Special special = (Special) n;
                if (special.kind()==Special.SUPER) {
                    if (state==CtorState.Start)
                        reportError("You can use 'super' only after 'super(...)'",pos);
                } else if (!canUseThis())
                    reportError((hasProperties ? "Can use 'this' only after 'property(...)'" : "Can use 'this' only after 'this(...)' or 'super(...)'")+" in "+parent, pos);
            }
            return n;
        }
        private boolean canUseThis() {
            return state==CtorState.SawProperty || // after call to 'property(...)'
                (!hasProperties && state==CtorState.SawCtor) || // after call to 'this(...)' or 'super(...)' if there are no properties
                (state==CtorState.SawCtor && !wasSuperCall); // after call to 'this(...)'
        }

    }


    // we gather info on every private/final/@NonEscaping method called during construction (@NoThisAccess do not access "this", so no need to analyze them)
    private static class MethodInfo {
        private final Set<FieldDef> read = CollectionFactory.newHashSet();
        private final Set<FieldDef> write = CollectionFactory.newHashSet();
        private final Set<FieldDef> seqWrite = CollectionFactory.newHashSet();
    }
    private final Job job;
    private final NodeFactory nf;
    private final TypeSystem ts;
    private final X10ClassDecl_c xlass;
    private final boolean hasProperties; // this this class defined properties (excluding properties of the sueprclass). if so, there must be exactly one "property(...)"
    private final FieldDef propertyRepresentative; // tracking a single property field is enough (all of them are assigned together)
    private final boolean isXlassFinal;
    private final Type xlassType;
    // the keys are either X10ConstructorDecl_c or X10MethodDecl_c
    private final HashMap<ProcedureDef,MethodInfo> allMethods = new LinkedHashMap<ProcedureDef, MethodInfo>(); // all ctors and methods recursively called from allMethods on receiver "this"
    private final ArrayList<ProcedureDecl> dfsMethods = new ArrayList<ProcedureDecl>(); // to accelerate the fix-point alg
    // the set of all VAR and VAL fields (including one property representative), including those in the superclass because of super() call
    // (we need to check that VAL are read properly, and that VAR are written and read properly.)
    private final Set<FieldDef> fields = CollectionFactory.newHashSet();
    private final Set<FieldDef> superFields = CollectionFactory.newHashSet(); // after the "super()" call, these fields are initialized
    private final DataFlowItem INIT = new DataFlowItem();
    private final DataFlowItem CTOR_INIT = new DataFlowItem();

    private boolean wasChange = true, wasError = false; // for fixed point alg
    private Set<FieldDef> globalRef = CollectionFactory.newHashSet();// There is one exception to the "this cannot escape" rule:  val root = GlobalRef[...](this)

    private void checkGlobalRef(Node n) {
        // you cannot access a globalRef field via this  (but you can assign to them)
        if (n instanceof Field) {
            Field f = (Field) n;
            FieldDef def = f.fieldInstance().def();
            if (isTargetThis(f) && globalRef.contains(def))
                reportError("Cannot use '"+def.name()+"' because a GlobalRef[...](this) cannot be used in a field initializer, constructor, or methods called from a constructor.",n.position());
        }
    }
    public CheckEscapingThis(X10ClassDecl_c xlass, Job job, TypeSystem ts) {
        long start = System.currentTimeMillis();
        this.job = job;
        this.ts = ts;
        nf = (NodeFactory)ts.extensionInfo().nodeFactory();
        this.xlass = xlass;
        final List<PropertyDecl> props = xlass.properties();
        hasProperties = props!=null && props.size()>0;
        propertyRepresentative = hasProperties ? props.get(0).fieldDef() : null;
        if (hasProperties) fields.add(propertyRepresentative); // adding one property representative (for our data flow, to make sure it property(...) is always called
        isXlassFinal = xlass.flags().flags().isFinal();
        this.xlassType = Types.baseType(xlass.classDef().asType());
        // calculate the set of all fields (including inherited fields)
        calcFields();
        MinMaxInitCount notInited = MinMaxInitCount.build(false, false,false);
        MinMaxInitCount inited = MinMaxInitCount.build(false, true,true);
        for (FieldDef f : fields) {
            INIT.initStatus.put(f,notInited);
            CTOR_INIT.initStatus.put(f,notInited);
        }
        for (FieldDef field : superFields) {
            CTOR_INIT.initStatus.put(field, inited);
        }
        typeCheck();
        TIME += Math.abs(System.currentTimeMillis()-start);
    }
    private static ArrayList<FieldDef> getInstanceFields(ClassDef currClass) {
        List<FieldDef> list = currClass.fields();
        ArrayList<FieldDef> init = new ArrayList<FieldDef>(list.size());
        for (FieldDef f : list) {
            if (!isProperty(f) && // not tracking property fields (checking property() call was done elsewhere)
                !f.flags().isStatic()) { // static fields are checked in FwdReferenceChecker
                init.add(f);
            }
        }
        return init;
    }

    private void calcFields() {
        final ClassDef myClassDef = xlass.classDef();
        ClassDef currClass = myClassDef;
        while (currClass!=null) {
            final ArrayList<FieldDef> init = getInstanceFields(currClass);
            fields.addAll(init);
            if (myClassDef!=currClass) superFields.addAll(init);
            final Ref<? extends Type> superType = currClass.superType();
            if (superType==null) break;
            currClass = superType.get().toClass().def();
        }
    }
    private boolean isGlobalRefNewExpr(X10New_c new_c) {
        final TypeNode typeNode = new_c.objectType();
        final List<Expr> args = new_c.arguments();
        if (args.size()==1 && isThis(args.get(0)) && // the first and only argument is "this"
            typeNode instanceof X10CanonicalTypeNode_c) { // now checking the ctor is of GlobalRef
            X10CanonicalTypeNode_c tn = (X10CanonicalTypeNode_c) typeNode;
            final Type type = Types.baseType(tn.type());
            if (type instanceof X10ParsedClassType_c) {
                X10ParsedClassType_c classType_c = (X10ParsedClassType_c) type;
                final QName qName = classType_c.def().fullName();
                if (qName.equals(QName.make("x10.lang","GlobalRef"))) {
                    // found the pattern!
                    return true;
                }
            }
        }
        return false;        
    }
    private void calcGlobalRefs(ArrayList<X10FieldDecl_c> nonStaticFields) {
        for (X10FieldDecl_c field : nonStaticFields) {
            boolean isGlobalRef = false;
            X10FieldDef fieldDef = field.fieldDef();
            if (Types.isNonEscaping(fieldDef,ts))
                isGlobalRef = true;
            final Expr init = field.init();
            // check for the pattern: val/var someField = GlobalRef[...](this)
            if (init instanceof X10New_c) {
                X10New_c new_c = (X10New_c) init;
                if (isGlobalRefNewExpr(new_c)) isGlobalRef=true;
            }
            if (isGlobalRef) {
                // must be private
                if (!isXlassFinal && !field.flags().flags().isPrivate())
                    reportError("In order to use the pattern GlobalRef[...](this) the field must be private.",field.position());
                globalRef.add(fieldDef);
            }
        }
    }
    private void typeCheck() {
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        // visit all (non-static) field initializers and check that they do not have forward references nor that "this" escapes
        ArrayList<X10FieldDecl_c> nonStaticFields = new ArrayList<X10FieldDecl_c>();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10FieldDecl_c) {
                X10FieldDecl_c field = (X10FieldDecl_c) classMember;
                if (field.flags().flags().isStatic()) continue;
                nonStaticFields.add(field);
            }
        }
        // Find globalRefs
        calcGlobalRefs(nonStaticFields);

        // inline the field-initializers in every ctor
        ArrayList<Stmt> fieldInits = new ArrayList<Stmt>();
        final Position pos = Position.COMPILER_GENERATED;
        for (X10FieldDecl_c field : nonStaticFields) {
            final Expr init = field.init();
            final X10FieldDef def = (X10FieldDef) field.fieldDef();
            if (init==null) continue;
            final Special This = (Special) nf.Special(pos, Special_c.THIS).type(def.container().get().toType());
            final FieldAssign fieldAssign = (FieldAssign) nf.FieldAssign(pos, This, field.name(), Assign_c.ASSIGN, init).
                    fieldInstance(def.asInstance()).
                    type(init.type());
            fieldInits.add(nf.Eval(pos, fieldAssign));
            if (!globalRef.contains(def)) init.visit(this); // field init are like a ctor
        }
        Block newInit = nf.Block(pos,fieldInits);


        // visit every ctor, every @NoThisAccess/@NonEscaping method, and every method recursively called from them, and check that this and super do not escape
        ArrayList<X10ConstructorDecl_c> allCtors = new ArrayList<X10ConstructorDecl_c>();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof ProcedureDecl) {
                final ProcedureDecl proc = (ProcedureDecl) classMember;
                final X10ProcedureDef def = (X10ProcedureDef)proc.procedureInstance();
                final Block procBody = proc.body();

                if (def instanceof X10MethodDef) {
                    X10MethodDef x10def = (X10MethodDef) def;
                    boolean isNoThisAccess = Types.isNoThisAccess(x10def,ts);
                    boolean isNonEscaping = Types.isNonEscaping(x10def,ts);
                    if (GATHER_STATS) {
                        ALL_METHODS.add(x10def);
                        if (isNoThisAccess) ALL_NO_THIS_ACCESS.add(x10def);
                        if (isNonEscaping) {
                            ALL_NON_ESCAPING_METHODS.add(x10def);
                            ALL_EXPLICIT_NON_ESCAPING_METHODS.add(x10def);
                        }
                    }

                    // if we overrode a method with @NoThisAccess, then we must be annotated with @NoThisAccess
                    // (NonEscaping is private/final, so cannot be overriden)
                    if (!isNoThisAccess) {
                        final MethodInstance instance = x10def.asInstance();
                        final Context emptyContext = ts.emptyContext();
                        final List<MethodInstance> overriddenMethods = instance.overrides(emptyContext); // Yoav and Igor thought hard and couldn't find an example where using an empty context vs. the correct context gives a different result.
                        for (MethodInstance overriddenMI : overriddenMethods) {
                            MethodDef overriddenDef = overriddenMI.def();
                            if (overriddenDef==def) continue; // me
                            boolean overriddenIsNoThisAccess = Types.isNoThisAccess((X10MethodDef)overriddenDef,ts);
                            if (overriddenIsNoThisAccess) {
                                reportError("You must annotate "+proc+" with @NoThisAccess because it overrides a method annotated with that.", proc.position());
                                break; // one such error msg is enough
                            }
                        }
                    }


                    if (isNoThisAccess) { // NoThisAccess is stronger than NonEscaping so we check it first (in case someone wrote both annotations)
                        // check "this" is not accessed at all
                        if (procBody != null) { // native/abstract methods
                            checkNoThis(procBody,"You cannot use 'this' or 'super' in a method annotated with @NoThisAccess");
                        }
                        // No need to do procBody.visit(this)  because "this"/"super" are not used in a NoThisAccess method.
                    } else if (isNonEscaping) {
                        if (!isPrivateOrFinal(x10def))
                            reportError("A @NonEscaping method must be private or final.", proc.position());
                        if (procBody!=null && !allMethods.containsKey(def)) { // for native methods/ctors, we don't have a body
                            final MethodInfo info = new MethodInfo();
                            allMethods.put(def, info);
                            procBody.visit(this);
                            dfsMethods.add(proc);
                        }
                    }
                } else {
                    if (procBody==null) continue; // native ctors
                    assert proc instanceof X10ConstructorDecl_c : proc;
                    final X10ConstructorDecl_c ctor = (X10ConstructorDecl_c) proc;
                    allCtors.add(ctor);
                    if (GATHER_STATS) ALL_CTORS.add(ctor.constructorDef());
                    procBody.visit(this);
                }
            }
        }
        // we still need to CheckCtor (make sure super, this and property is correct)
        //if (fields.size()==0) return; // done! all fields have an init, thus all reads are legal (and no writes must be done).

        // do init for the fixed point alg
        for (Map.Entry<ProcedureDef, MethodInfo> entry : allMethods.entrySet()) {
            MethodInfo info = entry.getValue();
            info.write.addAll(fields);
            info.seqWrite.addAll(fields);
        }
        // run fix point alg: ctors do not need to be in the fixed point alg because nobody can call them directly
        final FieldChecker fieldChecker = new FieldChecker(INIT);
        while (wasChange && !wasError) {
            wasChange =false;
            // do a DFS: starting from private/final methods, and then the ctors (this will reach a fixed-point fastest)
            for (ProcedureDecl p : dfsMethods) {
                fieldChecker.dataflow(p);
                fieldChecker.checkResult();
            }
        }
        // handle ctors and field initializers
        // make a new special ctor for field-init, and the ctors will use its data-flow for their INIT
        if (allCtors.size()>0) { // there should be at least one auto-generated ctor
            fieldChecker.init = CTOR_INIT;
            X10ConstructorDecl_c fieldInitCtor = (X10ConstructorDecl_c) allCtors.get(0).body(newInit);
            fieldChecker.dataflow(fieldInitCtor);
            fieldChecker.init = fieldChecker.finalResult;
            for (X10ConstructorDecl_c ctor : allCtors) {
                // check super, this, and property calls
                final CheckCtor checkCtor = new CheckCtor(ctor);
                ctor.visit(checkCtor); // we check both the body and signature because we want to make sure that "this" is not used in the method guard
                checkCtor.postCheck();

                final ConstructorCall cc = getConstructorCall(ctor);
                if (cc!=null && cc.kind() == ConstructorCall.THIS) {
                    // ignore in dataflow ctors that call other ctors (using "this(...)").
                    continue;
                }
                // X10ConstructorDecl_c newCtor = (X10ConstructorDecl_c) ctor.body( nf.Block(pos,newInit,ctor.body()) ); //reports errors in the field-inits multiple times (if we have multiple ctors)
                fieldChecker.dataflow(ctor);
                fieldChecker.checkResult();
            }
        }
        if (GATHER_STATS) {
            System.out.println(
                " ASYNC_LOCAL_INIT_COUNT="+InitChecker.ASYNC_INIT_COUNT+
                " ASYNC_FIELD_INIT_COUNT="+ASYNC_INIT_COUNT +
                " ALL_CTORS="+ALL_CTORS.size()+
                " ALL_METHODS="+ALL_METHODS.size()+
                " ALL_NON_ESCAPING_METHODS="+ALL_NON_ESCAPING_METHODS.size()+
                " ALL_EXPLICIT_NON_ESCAPING_METHODS="+ALL_EXPLICIT_NON_ESCAPING_METHODS.size()+
                " ALL_NO_THIS_ACCESS="+ALL_NO_THIS_ACCESS.size());
            TreeMap<String,int[]> byFile = new TreeMap<String,int[]>();
            HashSet[] ALL = {ALL_CTORS, ALL_METHODS, ALL_NON_ESCAPING_METHODS, ALL_EXPLICIT_NON_ESCAPING_METHODS, ALL_NO_THIS_ACCESS};
            for (int i=0; i<ALL.length; i++)
                for (Object defO : ALL[i]) {
                    X10ProcedureDef def = (X10ProcedureDef) defO;
                    Position defPos = def.position();
                    if (defPos.isCompilerGenerated()) continue;
                    String file = defPos.file();
                    if (!byFile.containsKey(file)) byFile.put(file, new int[ALL.length] );
                    byFile.get(file)[i]++;
                }
            for (String file : byFile.keySet()) {
                System.out.println(file+": "+ Arrays.toString(byFile.get(file)));                
            }
            System.out.println();
        }
    }
    public static ConstructorCall getConstructorCall(X10ConstructorDecl_c ctor) {
        // We can reuse ConstructorCallChecker, but for better efficiency, we just check it directly
        final Block ctorBody = ctor.body();
        assert ctorBody!=null;
        final List<Stmt> stmts = ctorBody.statements();
        for (Stmt s : stmts) {
            if (s instanceof ConstructorCall) {
                return (ConstructorCall) s;
            }
        }
        return null;
    }
    private X10MethodDecl_c findMethod(X10Call call) {
        MethodInstance mi2 = call.methodInstance();
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10MethodDecl_c) {
                X10MethodDecl_c mdecl = (X10MethodDecl_c) classMember;
                if (mdecl.body()==null) continue; // for native methods (like typeName in structs)
                final MethodDef md = mdecl.methodDef();
                if (mi2.def().equals(md)) return mdecl;
            }
        }
        return null;
    }
    private void checkNoThis(Node n, String msg) {
        n.visit(new ThisCheckerIgnoringTypes(msg));
    }
    private String getMsg(Node n) {
        if (n instanceof AtEach)
            return "'this' or 'super' cannot escape via an 'ateach' statement during construction.";
        if (n instanceof AtStmt)
            return "'this' or 'super' cannot escape via an 'at' statement during construction.";
        if (n instanceof AtExpr)
            return "'this' or 'super' cannot escape via an 'at' expression during construction.";
        if (n instanceof Closure)
            return "'this' or 'super' cannot escape via a closure during construction.";
        return null;        
    }

    @Override public Node visitEdgeNoOverride(Node parent, Node n) {
        checkGlobalRef(n); // check globalRef usage in ctors and methods called from ctors

        String msg = getMsg(n);
        if (msg!=null) {
            // "this" cannot escape into an AT or closure
            checkNoThis(n,msg);
            return n;
        }

        if (n instanceof New) {
            New aNew = (New) n;
            if (aNew.qualifier()==null && aNew.body()!=null) {
                reportError("'this' cannot escape via an anonymous class during construction", n.position());
            }
        }
        // You can access "this" for field access and field assignment.
        // field assignment:
        if (n instanceof FieldAssign) {
            FieldAssign f = (FieldAssign) n;
            if (isThis(f.target())) {
                Expr right = f.right();
                if (right instanceof X10New_c && globalRef.contains(f.fieldInstance().def()) && isGlobalRefNewExpr((X10New_c)right))
                    return n;
                right.visit(this);
                return n;
            }
        }
        // field access:
        if (n instanceof Field) {
            final Field f = (Field) n;
            if (isThis(f.target())) return n;
        }
        // You can also access "this" as the receiver of property calls (because they are MACROS that are expanded to field access)
        // and as the receiver of private/final calls
        if (n instanceof X10Call) {
            final X10Call call = (X10Call) n;
            final MethodInstance methodInstance = call.methodInstance();
            final X10ProcedureDef procDef = (X10ProcedureDef) methodInstance.def();
            if (isThis(call.target())) {
                boolean hasNoThisAccess = Types.isNoThisAccess(procDef,ts);
                if (isProperty(procDef) || hasNoThisAccess) {
                    // property-method calls and calls to @NoThisAccess are ok
                } else {
                    // the method must be final or private (or @NoThisAccess)
                    final Position callPos = call.position();
                    boolean isNonEscaping = Types.isNonEscaping(procDef,ts);
                    X10MethodDecl_c method = findMethod(call);
                    if (method==null) {
                        // in the future: we could infer nonescaping from the superclass. The problem is that it is hard to understand the error messages that result from such inference
                        // Igor: I think we should disallow the call to foo() when we infer that foo() escapes this.  The error message may mention @NonEscaping -- once the user annotates foo() with @NonEscaping, the compiler will tell him/her where the potential points of escape are.
                        if (!isNonEscaping)
                            reportError("The call "+call+" is illegal because you can only call a superclass method during construction only if it is annotated with @NonEscaping.", callPos);
                    } else {
                        if (!isPrivateOrFinal(procDef) && !isNonEscaping) // if it is NonEscaping, we will already report the error: "A @NonEscaping method must be private or final."
                            reportError("The call "+call+" is illegal because you can only call private/final @NonEscaping methods or @NoThisAccess methods during construction.", callPos);

                        ProcedureDef pd = method.procedureInstance();
                        if (allMethods.containsKey(pd)) {
                            // we already analyzed this method (or it is an error method)
                        } else {
                            if (!isNonEscaping && !isXlassFinal && !isPrivate(procDef))
                                job.compiler().errorQueue().enqueue(ErrorInfo.WARNING,"Method '"+procDef.signature()+"' is called during construction and therefore should be marked as @NonEscaping.", method.position());
                            final Block body = method.body();
                            if (body!=null) {
                                allMethods.put(pd,new MethodInfo()); // prevent infinite recursion
                                if (GATHER_STATS)
                                    ALL_NON_ESCAPING_METHODS.add(method.methodDef());
                                body.visit(this);
                                dfsMethods.add(method);
                            }
                        }
                    }
                }

                // it is enough to just recurse into the arguments (because the receiver is either this or super)
                for (Expr e : call.arguments())
                    e.visit(this);
                return n;
            }
        }

        // You cannot use "this" for anything else!
        if (isThis(n)) {
            reportError("'this' and 'super' cannot escape from a constructor or from methods called from a constructor",n.position());
        }
        n.del().visitChildren(this);
        return n;
    }
    private void reportError(String s, Position p) {
        job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,s,p);
    }


    private boolean isTargetThis(X10Call call) {
        final MethodInstance methodInstance = call.methodInstance();
        final MethodDef def = methodInstance.def();
        return !isProperty(def) && !def.flags().isStatic() && isThis(call.target());
    }
    private boolean isTargetThis(FieldAssign f) {
        FieldDef def = f.fieldInstance().def();
        return !isProperty(def) && !def.flags().isStatic() && isThis(f.target());
    }
    private boolean isTargetThis(Field f) {
        FieldDef def = f.fieldInstance().def();
        return isTargetThis(def) && isThis(f.target());
    }
    private boolean isTargetThis(FieldDef def) {
        return !isProperty(def) && !def.flags().isStatic();
    }
    private boolean isThis(Node n) {
        if (n==null || !(n instanceof Special)) return false;
        final Special special = (Special) n;
        final Type tt = Types.baseType(special.type());
        if (!tt.isClass()) return false;
        ClassType type = tt.toClass();

        // both this and super cannot escape
        // for "super.", it resolves to the superclass, so I need to go up the superclasses
        Type thisClass = xlassType;
        while (thisClass!=null) {
            if (!thisClass.isClass()) return false;
            final ClassType classType = thisClass.toClass();
            if (type.def()==classType.def()) return true;
            final Type superClass = classType.superClass();
            if (superClass==null) return false;
            thisClass = Types.baseType(superClass);
        }
        return false;
    }
    class ThisCheckerIgnoringTypes extends NodeVisitor {
        private final String errMsg;

        public ThisCheckerIgnoringTypes(String errMsg) {
            this.errMsg = errMsg;
        }
        @Override public Node override(Node n) {
            if (isThis(n)) {
                job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,errMsg,n.position());
            }
            return null;
        }
    }
}
