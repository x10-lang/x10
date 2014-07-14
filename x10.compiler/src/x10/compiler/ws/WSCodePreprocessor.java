/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */



package x10.compiler.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary.Operator;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Do;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.While;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ExtensionInfo;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtStmt_c;
import x10.ast.Closure;
import x10.ast.FinishExpr;
import x10.ast.ForLoop;
import x10.ast.Offer;
import x10.ast.StmtSeq;
import x10.ast.X10Binary_c;
import x10.ast.X10ClassDecl;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.ast.X10Special;
import x10.compiler.ws.WSTransformState.MethodType;
import x10.compiler.ws.util.Triple;
import x10.compiler.ws.util.WSTransformationContent;
import x10.compiler.ws.util.WSUtil;
import x10.compiler.ws.util.CodePatternDetector.Pattern;
import x10.optimizations.ForLoopOptimizer;
import x10.types.AsyncDef;
import x10.types.AtDef;
import x10.types.EnvironmentCapture;
import x10.types.MethodInstance;
import x10.types.ParameterType;
import x10.types.ThisDef;
import x10.types.X10MemberDef;
import x10.types.X10MethodDef;
import x10.util.AltSynthesizer;
import x10.util.CollectionFactory;
import x10.util.Synthesizer;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.MethodSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.visit.Desugarer;
import x10.visit.ExpressionFlattener;
import x10.visit.X10InnerClassRemover;

/**
*
 * ContextVisitor that pre-process AST code for work stealing.
 * @author Haichuan
 * 
 * Some code cannot be transformed by WS Code Generator directly.
 * 1) Concurrent call in If condition, When/Do/While/Switch expr, 
 *    For's init/iterator/condition, Compound expressions.
 * 2) Concurrent call in ForLoop
 * 3) AtEach
 * Jobs performed in this pass
 * I: Transform Un-supported stmts
 *   1) transform compound stmts into simple stmts by ExpressionFlattener
 *   2) transform forloop (for(x in domain)) into standard for by forloopunrolloer
 *   3) transform AtEach by lowerer's code
 * II: Advanced optimization
 *   1) Transform simple for (e.g. for(var i:int=0; i < 1000; i++ ) async) 
 *      into divide-and-conquer style


 */

public class WSCodePreprocessor extends ContextVisitor {
    static final boolean debug = true;
    static final Position compilerPos = Position.COMPILER_GENERATED;

    /**
     * A small container to store the diviable for's information
     */
    protected class DividableFor{
        Type boundType;
        Expr lowerRef;
        Expr upperRef;
        Expr condLeft;
        Binary.Operator condOperator;
        LocalDecl iteratorDecl;
        Stmt iteratorAssign;
        Async forAsync;
    }
    
    
    protected class IteratorFinderVisitor extends NodeVisitor {
        private boolean found = false;
        private final LocalDef iDef;
        
        public IteratorFinderVisitor(LocalDef iDef){
            this.iDef = iDef;
        }
        
        public boolean isFound(){
            return found;
        }
        
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if(n instanceof Local){
                LocalInstance li = ((Local) n).localInstance();
                if(li.def() == iDef){
                    found = true;
                }
            }
            return n;
        }
        
    }
    
    
    /**
     * A visitor to locate all local vars referenced in for's body
     */
    public static class LocalVarCaptureVisitor extends NodeVisitor {
        private final Context context;
        private final Set<Local> locals;
        public LocalVarCaptureVisitor(Context context) {
            this.context = context;
            this.locals = CollectionFactory.newHashSet();
        }
        
        /**
         * @return non-duplicated locals
         */
        public List<Local> getLocals(){
            //the locals may contains duplicate locals. we need filter them out by name
            List<Local> ls = new ArrayList<Local>();
            Set<String> names = CollectionFactory.newHashSet();
            for(Local l : locals){
                String name = l.name().id().toString();
                if(names.contains(name)){  continue;  }
                names.add(name);
                ls.add(l);
            }
            return ls;
        }
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Local) {
                LocalInstance li = ((Local) n).localInstance();
                VarInstance<?> o = context.findVariableSilent(li.name());
                if (li == o || (o != null && li.def() == o.def())) {
                    locals.add((Local)n);
                }
            }
            return n;
        }
    }
    
    // Single static WSTransformState shared by all visitors (FIXME)
    public static WSTransformState wts; 
    private final Set<X10MethodDecl> genMethodDecls;
    private final Synthesizer synth;
    private final AltSynthesizer altsynth;
    
    public WSCodePreprocessor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        genMethodDecls = CollectionFactory.newHashSet();
        synth = new Synthesizer(nf, ts);
        altsynth = new AltSynthesizer(ts, nf);
    }

    public static void setWALATransTarget(ExtensionInfo extensionInfo, WSTransformationContent target){
        //DEBUG
        if(debug){
            WSUtil.debug("Use WALA CallGraph Data...");    
        }
        wts = new WSTransformStateWALA(extensionInfo, target);
        WSCodeGenerator.wts = wts;
    }
    
    public static void buildCallGraph(ExtensionInfo extensionInfo) {
        //DEBUG
        if(debug){
            WSUtil.debug("Build Simple Graph Graph...");    
        }
        wts = new WSTransformStateSimple(extensionInfo);
        WSCodeGenerator.wts = wts;
    }
    
    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if(n instanceof For){
            return visitFor((For)n);
        }
        if(n instanceof ForLoop){
            return visitForLoop((ForLoop)n);
        }
        if(n instanceof AtEach){
            return visitAtEach((AtEach)n);
        }
        if(n instanceof Eval){
            return visitEval((Eval)n);
        }
        if(n instanceof Do){
            return visitDo((Do)n);
        }
        if(n instanceof While){
            return visitWhile((While)n);
        }
        if(n instanceof Switch){
            return visitSwitch((Switch)n);
        }
        if(n instanceof If){
            return visitIf((If)n);
        }
        if(n instanceof Return){
            return visitReturn((Return)n);
        }
        if(n instanceof LocalDecl){
            return visitLocalDecl((LocalDecl)n);
        }
        if(n instanceof ConstructorDecl){
            return visitConstructorDecl((ConstructorDecl)n);
        }
        if(n instanceof Closure){
            return visitClosure((Closure)n);
        }
        if(n instanceof Offer){
            return visitOffer((Offer)n);
        }
        if (n instanceof X10ClassDecl) {
            return visitClass((X10ClassDecl)n);
        }
        
        return n;
    }
    
    private Node visitClass(X10ClassDecl cDecl) throws SemanticException {
        ClassDef cDef = cDecl.classDef();
        List<X10MethodDecl> methods = getMethodDecls(cDef);    
        if(methods.isEmpty()){
            return cDecl;
        }
        cDecl = Synthesizer.addMethods(cDecl, methods);
        
        Desugarer desugarer = ((x10.ExtensionInfo) job.extensionInfo()).makeDesugarer(job);
        desugarer.begin();
        desugarer = (Desugarer) desugarer.context(context()); //copy current context
        
        X10InnerClassRemover innerclassRemover = new X10InnerClassRemover(job, ts, nf);
        innerclassRemover.begin();
        innerclassRemover = (X10InnerClassRemover) innerclassRemover.context(context()); //copy current context
        
        cDecl = (X10ClassDecl) cDecl.visit(desugarer);
        cDecl = (X10ClassDecl) cDecl.visit(innerclassRemover);
        
        return cDecl;
    }

    protected List<X10MethodDecl> getMethodDecls(ClassDef cDef) throws SemanticException {
        List<X10MethodDecl> mDecls = new ArrayList<X10MethodDecl>();
        
        for(X10MethodDecl mDecl : genMethodDecls){
            ClassDef containerDef = ((ClassType) mDecl.methodDef().container().get()).def();
            if(containerDef == cDef){
                mDecls.add(mDecl);
            }
        }
        return mDecls;
    }
    
    private Node visitOffer(Offer n) throws SemanticException {
        if(WSUtil.isComplexCodeNode(n.expr(), wts)){
            return flattenStmt(n);
        }
        return n;
    }

    private Node visitClosure(Closure n) throws SemanticException {        
        if(wts.getMethodType(n) != MethodType.NORMAL){
            WSUtil.err("X10 Work Stealing doesn't support concurrent closure", n);
        }
        return n;
    }

    private Node visitConstructorDecl(ConstructorDecl n) throws SemanticException {
        if(wts.getMethodType(n) != MethodType.NORMAL){
            WSUtil.err("X10 Work-Stealing doesn's support concurrent constructor", n);
        }
        return n;
    }

    /**
     * @param call
     * @return true if the call is "foo(...)" style. The "..." has no any concurrent call
     */
    private boolean isSimpleConcurrentCall(Call call){
        int concurrentCallNum = WSUtil.calcConcurrentCallNums(call, wts);
        if(concurrentCallNum == 1 && wts.isConcurrentCallSite(call)){
            return true;
        }
        else{
            return false;
        }
    }

    private Node visitLocalDecl(LocalDecl n) {
        if(n.init() == null){
            return n;
        }
        // a local decl's initializer 
        Expr expr = n.init();
        if(!WSUtil.isComplexCodeNode(expr, wts)){
            return n;
        }
        if(expr instanceof Call
                && isSimpleConcurrentCall((Call)expr)){
                return n;
        }
        if(expr instanceof FinishExpr){
            return n;
        }
        return flattenStmt(n);
    }

    private Node visitReturn(Return n) {
        if(WSUtil.isComplexCodeNode(n.expr(), wts)){
            return flattenStmt(n);
        }
        
        return n;
    }

    private Node visitIf(If n) {
        if(WSUtil.isComplexCodeNode(n.cond(), wts)){
            return flattenStmt(n);
        }
        return n;
    }

    private Node visitSwitch(Switch n) {
        if(WSUtil.isComplexCodeNode(n.expr(), wts)){
            return flattenStmt(n);
        }
        return n;
    }

    private Node visitWhile(While n) {
        if(WSUtil.isComplexCodeNode(n.cond(), wts)){
            return flattenStmt(n);
        }
        return n;
    }

    private Node visitDo(Do n) {
        if(WSUtil.isComplexCodeNode(n.cond(), wts)){
            return flattenStmt(n);
        }
        return n;
    }
    
    private Node visitEval(Eval n) {
        
        //if the node is simple, no need any flatten.
        if(! WSUtil.isComplexCodeNode(n, wts)){
            return n;
        }
        
        Expr expr = n.expr();
        
        //Simple case 1: directly concurrent call
        if(expr instanceof Call
                && isSimpleConcurrentCall((Call)expr)){
                return n;
        }
        else if(expr instanceof LocalAssign || expr instanceof FieldAssign){
            Assign assign = (Assign)expr;
            Expr rightExpr = assign.right();
            if(rightExpr instanceof Call 
                    && isSimpleConcurrentCall((Call)rightExpr)){
                    return n;
            }
            if(rightExpr instanceof FinishExpr){
                return n; //collecting-finish
            }
        }
        return flattenStmt(n);
    }

    
    private static final Name DIST = Name.make("dist");
    private static final Name RESTRICTION = Name.make("restriction");
    private static final Name PLACES = Name.make("places");
    
    /**
     * @param a ateach (p in D) S;
     * @return val d = D.dist; for (p in d.places()) async (p) for (pt in d|here) async S;
     * @throws SemanticException
     * The code is similar to the visitAtEach in lowerer. But we create async stmt directly
     * Not lower it
     */
    private Node visitAtEach(AtEach a) throws SemanticException {
        Position pos = a.position();
        Position bpos = a.body().position();
        AsyncDef asyncDef = (AsyncDef)AtStmt_c.createDummyAsync(bpos, ts, 
                                                      context.currentClass(), 
                                                      context.currentCode(), 
                                                      context.currentCode().staticContext(), true);
        AtDef atDef = a.atDef();
        Name tmp = Context.getNewVarName(); //for the dist
        Expr domain = a.domain();
        Type dType = domain.type();
        if (ts.isX10RegionDistArray(dType)) {
            domain = altsynth.createFieldRef(pos, domain, DIST);
            dType = domain.type();
        }
        LocalDef lDef = ts.localDef(pos, ts.Final(), Types.ref(dType), tmp);
        LocalDecl local = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
                nf.CanonicalTypeNode(pos, dType), nf.Id(pos, tmp), domain).localDef(lDef);
        X10Formal formal = (X10Formal) a.formal();
        Type fType = formal.type().type();
        assert (ts.isPoint(fType));
        assert (ts.isDistribution(dType));
        // Have to desugar some newly-created nodes
        Type pType = ts.Place();
        MethodInstance rmi = ts.findMethod(dType,
                ts.MethodMatcher(dType, RESTRICTION, Collections.singletonList(pType), context()));
        Expr here = nf.Here(bpos); //org:visitHere(nf.Here(bpos));
        Expr dAtPlace = nf.Call(bpos,
                nf.Local(pos, nf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                nf.Id(bpos, RESTRICTION),
                here).methodInstance(rmi).type(rmi.returnType());
        Expr here1 = nf.Here(bpos); //org:visitHere(nf.Here(bpos));
        List<VarInstance<? extends VarDef>> env = a.atDef().capturedEnvironment();
        //Stmt body = async(a.body().position(), a.body(), a.clocks(), here1, null, env);
        Stmt body = nf.Async(bpos, a.clocks(), a.body()).asyncDef(asyncDef);
        
        Stmt inner = nf.ForLoop(pos, formal, dAtPlace, body).locals(formal.explode(this));
        MethodInstance pmi = ts.findMethod(dType,
                ts.MethodMatcher(dType, PLACES, Collections.<Type>emptyList(), context()));
        Expr places = nf.Call(bpos,
                nf.Local(pos, nf.Id(pos, tmp)).localInstance(lDef.asInstance()).type(dType),
                nf.Id(bpos, PLACES)).methodInstance(pmi).type(pmi.returnType());
        Name pTmp = Context.getNewVarName();;
        LocalDef pDef = ts.localDef(pos, ts.Final(), Types.ref(pType), pTmp);
        Formal pFormal = nf.Formal(pos, nf.FlagsNode(pos, ts.Final()),
                nf.CanonicalTypeNode(pos, pType), nf.Id(pos, pTmp)).localDef(pDef);
        List<VarInstance<? extends VarDef>> env1 = new ArrayList<VarInstance<? extends VarDef>>(env);
        env1.remove(formal.localDef().asInstance());
        for (int i = 0; i < formal.localInstances().length; i++) {
            env1.remove(formal.localInstances()[i].asInstance());
        }
        env1.add(lDef.asInstance());
        //outter async: async at(p) body
        Stmt body1 = nf.AtStmt(bpos, 
                               nf.Local(bpos, nf.Id(bpos, pTmp)).localInstance(pDef.asInstance()).type(pType), 
                               inner).atDef(atDef);
        Stmt outAsync = nf.Async(bpos, a.clocks(), body1).asyncDef(asyncDef);

        
        Stmt outer = nf.ForLoop(pos, pFormal, places, outAsync);

        // TODO: Instead of creating ForLoop's and then removing them, 
        //       change the code above to create simple For's in the first place.
        ForLoopOptimizer flo = new ForLoopOptimizer(job, ts, nf);
        For newLoop = (For)outer.visit(((ContextVisitor)flo.begin()).context(context()));
        
        List<Stmt> stmts = new ArrayList<Stmt>();
        stmts.add(local);
        stmts.add(newLoop);
        return nf.StmtSeq(pos, stmts);
        //WSUtil.err("X10 Work-Stealing doesn's support AtEach", n);
    }

    /**
     * Need transform ForLoop to For
     * @param n
     * @return
     * @throws SemanticException 
     */
    private Node visitForLoop(ForLoop n) throws SemanticException {
        //Only process concurrent for
        if(! WSUtil.isComplexCodeNode(n, wts)){
            return n; 
        }
        
        //Step 1: translate the forloop by forloopoptimizer
        ForLoopOptimizer flo = new ForLoopOptimizer(job, ts, nf);
        flo.begin();
        flo.context(context());
        Node floOut = n.visit(flo); //floOut could be a block or a for statement
        // there are two cases from ForLoopOptimizer
        // 1. only for; 2. some local declarations and for
        // for cases 2, we need add these local declarations into for's init

        For forStmt = null;
        List<Stmt> forBlockStmts = new ArrayList<Stmt>();
        int forStmtLoc = -1; //the place the for stmt in the block
        if (floOut instanceof Block) { // case 2, re-wrap it into a stmtseq
            forBlockStmts.addAll(((Block)floOut).statements());
            for (int i = 0; i < forBlockStmts.size(); i++) {
                Stmt s = forBlockStmts.get(i);
                if (s instanceof For) {
                    forStmt = (For) s;
                    forStmtLoc = i;
                    break;
                }
            }
            assert(forStmtLoc >= 0);
        } else {
            forStmt = (For) floOut;
        }

        // second step, it's highly possible the body of a for stmt contains
        // additional block
        // { stmt1
        // { ... //remove this level
        // } //remove this level
        // }
        // just remove the first level
        Stmt forBodyStmt = forStmt.body();
        if (forBodyStmt instanceof Block) {
            ArrayList<Stmt> stmts = new ArrayList<Stmt>();
            for (Stmt s : ((Block) forBodyStmt).statements()) {
                if (s instanceof Block) {
                    stmts.addAll(((Block) s).statements());
                } else {
                    stmts.add(s);
                }
            }
            // now insert it into the forBodyStmt again;
            forStmt = forStmt.body(nf.Block(forStmt.position(), stmts));
        }
        //Final step: still need check the for loop
        Stmt newStmt = (Stmt) visitFor(forStmt);
        if(forStmtLoc >= 0){
            forBlockStmts.set(forStmtLoc, newStmt);
            newStmt = nf.StmtSeq(n.position(), forBlockStmts);
        }
        
        return newStmt;
    }

    /**
     * We suppose the stmt is flattened. And only transform 
     *  for() async() style.
     * @param n
     * @return
     * @throws SemanticException 
     */
    public Node visitFor(For n) throws SemanticException {
        //Only process concurrent for
        if(! WSUtil.isComplexCodeNode(n, wts)){
            return n; 
        }
        
        //Detect the init/iterator/condition
        List<Term> condTerms = new ArrayList<Term>();
        condTerms.addAll(n.inits());
        condTerms.addAll(n.iters());
        condTerms.add(n.cond());
        
        boolean needFlattern = false;
        for(Term t : condTerms){
            if(WSUtil.isComplexCodeNode(t, wts)){
                needFlattern = true;
                break;
            }
        }
        
        if(needFlattern){
            return flattenStmt(n);
        }
        
        
        if(wts.OPT_FOR_ASYNC){
          //for a simple For, we could try to transform it into divide and conquer style.
            DividableFor df = isDividableFor(n);
            if(df != null){
                return transformDividableFor(df, n);
            }
        }
        return n;

    }
    
    private Stmt transformDividableFor(DividableFor df, For n) throws SemanticException {
        if(debug){
            WSUtil.debug("Found dividable for:", n);
        }
        //First need detect all locals for copy
        LocalVarCaptureVisitor visitor = new LocalVarCaptureVisitor(context);
        //the only part need not check locals is for's upper ref & init decl
        n.body().visit(visitor);
        for(ForUpdate fu: n.iters()){
            fu.visit(visitor);
        }
        df.condLeft.visit(visitor);
        List<Local> locals = visitor.getLocals();

        String mName = WSUtil.getDividableForMethodName(n) + genMethodDecls.size();
        X10MethodDecl mDecl = synthDividableForMethod(n, mName, context, df, locals);
        
        X10MethodDef mDef = mDecl.methodDef();
        
        //now generate the in-place call
        Call call = synthDividableForMethodCall(n.position(), context, mDef,
                                                 df.lowerRef, df.upperRef, synth.intValueExpr(1, compilerPos),
                                                 df, visitor.getLocals());
        wts.addSynthesizedConcurrentMethod(mDecl);
        genMethodDecls.add(mDecl);
        if(debug){
            WSUtil.debug("Transform to divid-and-conquer call", call);
            WSUtil.debug("Method Body:", mDecl);
        }
        return nf.Eval(n.position(), call);
    }
    
    private X10MethodDecl synthDividableForMethod(For n, String methodName, Context c,
                                               DividableFor df, List<Local> locals) throws SemanticException{
        //define the synthesized method's type
        Flags flag = Flags.FINAL;
        if(c.inStaticContext()){
            flag = flag.set(Flags.STATIC);
        }
        
        MethodSynth mSynth = new MethodSynth(nf, WSUtil.popToClassContext(c), 
                                             n.position(), c.currentClassDef(),
                                             methodName);
        mSynth.setFlag(flag);
        for(ParameterType pt : c.currentCode().typeParameters()){
            mSynth.addTypeParameter(pt, pt.getVariance());
        }

        //Formals: 
        Expr lowerRef = mSynth.addFormal(compilerPos, df.boundType, "_$lower");
        Expr upperRef = mSynth.addFormal(compilerPos, df.boundType, "_$upper");
        Expr sliceNumRef = mSynth.addFormal(compilerPos, ts.Int(), "_$sliceNum");
        List<Local> formalLocals = new ArrayList<Local>();
        for(Local local : locals){
            Local formalLocal = mSynth.addFormal(local.position(), Flags.FINAL, local.type(), local.name().id());
            formalLocals.add(formalLocal);
        }
        
        //the method's body
        CodeBlockSynth methodBodySynth = mSynth.getMethodBodySynth(n.body().position());
        Context mContext = methodBodySynth.getContext();
        //if's condition

        //if((sliceNum >> 2) < Runtime.NTHREADS 
        Expr runtimeThreadsRef = synth.makeFieldAccess(compilerPos,
                              nf.CanonicalTypeNode(compilerPos, ts.Runtime()),
                              Name.make("NTHREADS"), mContext);
        Expr sliceNum4 = nf.Binary(compilerPos, sliceNumRef, Binary.SHR, synth.intValueExpr(2, compilerPos)).type(ts.Int());
        Expr cond = nf.Binary(compilerPos, sliceNum4, Binary.LT, runtimeThreadsRef).type(ts.Boolean());
        
        
        //val m:int = (s+e)/2;
        //val nSliceNum = sliceNum << 1;
        Expr totalBoundExpr = nf.Binary(compilerPos, lowerRef, Binary.ADD, upperRef).type(df.boundType);
        Expr middleBoundExpr = nf.Binary(compilerPos, totalBoundExpr, Binary.SHR, synth.intValueExpr(1, compilerPos)).type(df.boundType);
        NewLocalVarSynth mLocalSynth = new NewLocalVarSynth(nf, mContext, compilerPos, Flags.FINAL, middleBoundExpr);
        Expr middleBoundRef = mLocalSynth.getLocal();
        Expr newSliceNumExpr = nf.Binary(compilerPos, sliceNumRef, Binary.SHL, synth.intValueExpr(1, compilerPos)).type(ts.Int());
        NewLocalVarSynth nSliceLocalSynth = new NewLocalVarSynth(nf, mContext, compilerPos, Flags.FINAL, newSliceNumExpr);
        Expr newSliceNumRef = nSliceLocalSynth.getLocal();
        
        //divide-and-conquer
        //async forR0(s,m, nSliceNum, a, load);        
        Call call1 = synthDividableForMethodCall(n.position(), mContext, mSynth.getDef(),
                                       lowerRef, middleBoundRef, newSliceNumRef,
                                       df, formalLocals);

        Async async = df.forAsync.body(nf.Eval(compilerPos, call1));
        //forR0(m,e, nSliceNum, a, load);
        Call call2 = synthDividableForMethodCall(n.position(), mContext, mSynth.getDef(),
                                       middleBoundRef, upperRef, newSliceNumRef,
                                       df, formalLocals);

        
        Block ifTrueBlock = nf.Block(compilerPos, mLocalSynth.genStmt(), 
                                     nSliceLocalSynth.genStmt(), 
                                     async, nf.Eval(compilerPos, call2));
        //synth the 
        //for(var i:int=s; i<e; i++){
        //    a(i) = dowork(a(i), load);
        //}
        List<ForInit> newForInits = new ArrayList<ForInit>();
        newForInits.add(df.iteratorDecl.init(lowerRef));
        For newFor = n.inits(newForInits);
        
        Expr condBinary = nf.Binary(compilerPos, df.condLeft, df.condOperator, upperRef).type(ts.Boolean());
        newFor = newFor.cond(condBinary);
        
        //prepare for body
        List<Stmt> stmts = new ArrayList<Stmt>();
        if(df.iteratorAssign != null){
            stmts.add(df.iteratorAssign);
        }
        stmts.addAll(WSUtil.unwrapBodyBlockToStmtList(df.forAsync.body()));
        newFor = newFor.body(nf.Block(compilerPos, stmts));
        
        //Final if
        If ifStmt = nf.If(n.position(), cond, ifTrueBlock, newFor);
        methodBodySynth.addStmt(ifStmt);
        return mSynth.close();
    }
    
    private Call synthDividableForMethodCall(Position pos, Context ct, X10MethodDef mDef, 
                                             Expr lowerRef, Expr upperRef, Expr sliceNumRef,
                                             DividableFor df, List<Local> locals){

        Id callId = nf.Id(pos, mDef.name());
        ArrayList<Expr> paras = new ArrayList<Expr>();
        ArrayList<Type> formalTypes = new ArrayList<Type>();
        ArrayList<TypeNode> typeArgs = new ArrayList<TypeNode>();
        ArrayList<Type> typeActuals = new ArrayList<Type>();
        for (ParameterType pt : mDef.typeParameters()) {
            typeActuals.add(pt);
            typeArgs.add(nf.CanonicalTypeNode(pos, pt));
        }
        paras.add(lowerRef); formalTypes.add(df.boundType);
        paras.add(upperRef); formalTypes.add(df.boundType);
        paras.add(sliceNumRef); formalTypes.add(ts.Int());
        for(Local local : locals){
            paras.add(local);
            formalTypes.add(local.type());
        }
        Receiver r; 
        //decide the right method: instance of static
        
        if (mDef.flags().isStatic()) {
            r = nf.CanonicalTypeNode(compilerPos, ct.currentClass());
        }
        else {
            r = synth.thisRef(ct.currentClass(), compilerPos);
        }
        Call call = (Call) nf.X10Call(pos, r, callId, typeArgs, paras).type(ts.Void());
        MethodInstance mi = mDef.asInstance();
        mi = (MethodInstance) mi.flags(mDef.flags());
        mi = mi.name(mDef.name());
        mi = mi.returnType( mDef.returnType().get());
        mi = mi.formalTypes(formalTypes);
        mi = (MethodInstance) mi.typeParameters(typeActuals);
        
        return call.methodInstance(mi);
    }
    
    
    
    /**
     * Identify For patterns, like
     * 1) for(var i:int = lowerBound; i condition (<, <=, >, >=); i updater) { async {...}}
     * 2) for(var i:int = lowerBound; i condition (<, <=, >, >=); i updater) { val ii = i; async {...}}
     * @param n
     * @return <lowerBound, UpperBound, iterator type>
     */
    private DividableFor isDividableFor(For n){
        //Four condition
        //1: init has only one variables and the variable'
        //2: iterator is only related this single var's change
        //3: condition is larger or smaller
        //4: the body has only one stmt(aysnc) or two stmts(val assign, right is the iterator
        
        DividableFor df = new DividableFor();
        
        //Condition 1:
        List<ForInit> forInits = n.inits();
        if(forInits.size() != 1
                || !(forInits.get(0) instanceof LocalDecl)) {
            return null;
        }
        LocalDecl ld = (LocalDecl) forInits.get(0);
        if(ld.init() == null){
            return null;
        }
        df.iteratorDecl = ld;
        df.lowerRef = ld.init();
        df.boundType = ld.type().type();;

        //Condition 2: seems no need
        
        //Condition 3: 
        if(!(n.cond() instanceof Call)){
            return null;
        }
        Call call = (Call) n.cond();
        Name callName = call.name().id();
        Name nameLE = X10Binary_c.binaryMethodName(Binary.LE);
        Name nameGE = X10Binary_c.binaryMethodName(Binary.GE);
        Name nameLT = X10Binary_c.binaryMethodName(Binary.LT);
        Name nameGT = X10Binary_c.binaryMethodName(Binary.GT);
        
        //The condition call should be compare operator
        if(callName.equals(nameLE)){
            df.condOperator = Binary.LE;
        }
        else if(callName.equals(nameGE)){
            df.condOperator = Binary.GE;
        }
        else if(callName.equals(nameLT)){
            df.condOperator = Binary.LT;
        }
        else if(callName.equals(nameGT)){
            df.condOperator = Binary.GT;
        }
        else {
            return null;
        }

        //Now detect iterator is in the left or right. Cannot be in either side
        Expr condLeftExpr = (Expr) call.target();
        Expr condRightExpr = call.arguments().get(0);
        IteratorFinderVisitor iFinder = new IteratorFinderVisitor(ld.localDef());
        condLeftExpr.visit(iFinder);
        boolean leftFound = iFinder.isFound();
        iFinder = new IteratorFinderVisitor(ld.localDef());
        condRightExpr.visit(iFinder);
        boolean rightFound = iFinder.isFound();
        if(rightFound && leftFound || !rightFound && !leftFound){
            //both found or not found
            return null;
        }
        else if(leftFound){
            df.condLeft = condLeftExpr;
            df.upperRef = condRightExpr;
        }
        else { //right found - Switch left and right
            df.condLeft = condRightExpr;
            df.upperRef = condLeftExpr;
            //and need change operator
            if(df.condOperator == Binary.LE) {df.condOperator = Binary.GE; }
            else if(df.condOperator == Binary.GE) {df.condOperator = Binary.LE; }
            else if(df.condOperator == Binary.LT) {df.condOperator = Binary.GT; }
            else if(df.condOperator == Binary.GT) {df.condOperator = Binary.LT; }
        }
        //and the bound could be only integer type, and we need prepare a "1" for "GE"/"LE" case
        Expr uni;
        if(df.boundType == ts.Int()){
            uni = nf.IntLit(compilerPos, IntLit.INT, 1).type(ts.Int());
        }
        else if(df.boundType == ts.UInt()){
            uni = nf.IntLit(compilerPos, IntLit.UINT, 1).type(ts.UInt());
        }
        else if(df.boundType == ts.Long()){
            uni = nf.IntLit(compilerPos, IntLit.LONG, 1).type(ts.Long());
        }
        else if(df.boundType == ts.ULong()){
            uni = nf.IntLit(compilerPos, IntLit.ULONG, 1).type(ts.ULong());
        }
        else if(df.boundType == ts.Short()){
            uni = nf.IntLit(compilerPos, IntLit.SHORT, 1).type(ts.Short());
        }
        else if(df.boundType == ts.UShort()){
            uni = nf.IntLit(compilerPos, IntLit.USHORT, 1).type(ts.UShort());
        }
        else if(df.boundType == ts.Byte()){
            uni = nf.IntLit(compilerPos, IntLit.BYTE, 1).type(ts.Byte());
        }
        else if(df.boundType == ts.UByte()){
            uni = nf.IntLit(compilerPos, IntLit.UBYTE, 1).type(ts.UByte());
        }
        else{
            return null;
        }
        //Need process GE or LE. Change to GT or LT, and change upperRef
        if(df.condOperator == Binary.GE){
            df.condOperator = Binary.GT;
            df.upperRef = nf.Binary(compilerPos, df.upperRef, Binary.SUB, uni).type(df.boundType);
        }
        if(df.condOperator == Binary.LE){
            df.condOperator = Binary.LT;
            df.upperRef = nf.Binary(compilerPos, df.upperRef, Binary.ADD, uni).type(df.boundType);
        }
        
        //Condition 4:
        Stmt body = WSUtil.unwrapToOneStmt(n.body());
        //first case only one async
        if(body instanceof Async){
            df.forAsync = (Async) body;
            return df;
        }
        
        //second case
        if(body instanceof Block){
            List<Stmt> stmts = WSUtil.unwrapBodyBlockToStmtList(body);
            if(stmts.size() == 2 && stmts.get(1) instanceof Async){
                df.forAsync = (Async) stmts.get(1);
                if(stmts.get(0) instanceof LocalDecl){
                    LocalDecl ld2 = (LocalDecl)stmts.get(0);
                    if(ld2.init() != null
                            && ld2.init() instanceof Local){
                        Local local = (Local)ld2.init();
                        if(local.localInstance().def() == ld.localDef()){
                            //just the iterator assignment
                            df.iteratorAssign = ld2;
                            return df;
                        }
                    }
                }
            }
        }        
        return null;
    }
    
    
    private Node flattenStmt(Stmt n){
        ExpressionFlattener ef = new ExpressionFlattener(job, ts, nf);
        ef.begin();
        Stmt outS = (Stmt) n.visit(ef);
        if(debug){
            WSUtil.debug("Flatten Input:", n);
            if(outS instanceof Block){
                if(outS instanceof StmtSeq){
                    WSUtil.debug("Flatten Output is OK:StmtSeq");
                }
                else{
                    WSUtil.debug("Flatten Output is WRONG:" + outS.getClass());
                }
            }
            WSUtil.debug("Flatten Output:", outS);
        }
        return outS;
    }

    
    
}
