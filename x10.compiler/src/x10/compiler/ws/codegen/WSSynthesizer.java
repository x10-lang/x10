/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */



package x10.compiler.ws.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Catch;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Pair;
import polyglot.util.Position;
import x10.ast.AnnotationNode;
import x10.ast.ClosureCall;
import x10.ast.X10Call;
import x10.ast.X10MethodDecl;
import x10.compiler.ws.util.WSUtil;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.checker.PlaceChecker;
import x10.util.Synthesizer;
import x10.util.synthesizer.ClassSynth;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
import x10.util.synthesizer.FieldSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.MethodSynth;
import x10.util.synthesizer.NewInstanceSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.util.synthesizer.SuperCallSynth;

/**
 * @author Haichuan
 * 
 * All Work-Stealing Code stateless synthesizer
 * 
 * createXXX : create field/method/class
 * genXXX: generate expression, stmt
 * transXXX: transform a stmt to a code block
 * 
 *
 */
public class WSSynthesizer {
    static final protected Position compilerPos = Position.COMPILER_GENERATED;
    static final protected Name REMAP = Name.make("remap");
    static final protected Name FAST = Name.make("fast");
    static final protected Name PUSH = Name.make("push");
    static final protected Name POLL = Name.make("poll");
    static final protected Name RESUME = Name.make("resume");
    static final protected Name BACK = Name.make("back");
    static final protected Name MOVE = Name.make("move");
    static final protected Name RETHROW = Name.make("rethrow");
    static final protected Name CAUGHT = Name.make("caught");
    static final protected Name RUN_ASYNC_AT = Name.make("runAsyncAt");
    static final protected Name RUN_AT = Name.make("runAt");
    static final protected Name WORKER = Name.make("worker");
    static final protected Name FRAME = Name.make("frame");
    static final protected Name PC = Name.make("_pc");
    static final protected Name FF = Name.make("ff");
    static final protected Name UP = Name.make("up");
    static final protected Name ROOT_FINISH = Name.make("rootFinish");    
    static final protected Name RETURN_VALUE = Name.make("_returnV");
    static final protected Name RETURN_FLAG = Name.make("_returnF");
    static final protected Name ASYNCS = Name.make("asyncs");
    static final protected Name REDIRECT = Name.make("redirect");
    static final protected Name CONTINUE_LATER = Name.make("continueLater");
    static final protected Name CONTINUE_NOW = Name.make("continueNow");
    static final protected Name OPERATOR = ClosureCall.APPLY;
    static final protected Name ENTER_ATOMIC = Name.make("enterAtomic");
    static final protected Name EXIT_WHEN = Name.make("exitWSWhen");
    static final protected Name ACCEPT = Name.make("accept");    
    static final protected Name RD = Name.make("rd");  //used in collecting-finish frame constructor
    static final protected Name CF_RESULT_FAST = Name.make("fastResult");
    static final protected Name CF_RESULT = Name.make("result");
    
    //private IWSClassFrame classFrame;
    private Synthesizer synth;
    private NodeFactory nf;
    private TypeSystem ts;
    
    public WSSynthesizer(NodeFactory nf, TypeSystem ts){
        //this.classFrame = classFrame;
        this.nf = nf;
        this.ts = ts;
        this.synth = new Synthesizer(nf, ts);
    }
    
    public Position position(Position pos, String s) {
        return new Position(pos.path(), pos.nameAndLineString() + s);
    }
    
    public ClassSynth createClassSynth(Job job, Context ct, Type superClassType,
                                       X10ClassDef outClassDef, Flags flags, String className){
        ClassSynth classSynth = new ClassSynth(job, nf, ct, position(outClassDef.position(), className), superClassType, className);
        classSynth.setKind(ClassDef.MEMBER);
        classSynth.setFlags(flags);
        classSynth.setOuter(outClassDef);
        return classSynth;
    }
    
    public MethodSynth createFastMethodSynth(ClassSynth classSynth, boolean isInline){
        MethodSynth fastMSynth = classSynth.createMethod(classSynth.pos(), FAST.toString());
        fastMSynth.setFlag(Flags.PUBLIC);
        if (isInline){
            //only set inline to inner frames, not top frames
            fastMSynth.addAnnotation(genInlineAnnotation());
        }
        fastMSynth.addFormal(compilerPos, Flags.FINAL, ts.Worker(), WORKER.toString());
        return fastMSynth;
    }
    
    public MethodSynth createResumeMethodSynth(ClassSynth classSynth){
        MethodSynth resumeMSynth = classSynth.createMethod(classSynth.pos(), RESUME.toString());
        resumeMSynth.setFlag(Flags.PUBLIC);
        resumeMSynth.addFormal(compilerPos, Flags.FINAL, ts.Worker(), WORKER.toString());
        return resumeMSynth;
    }
    
    public MethodSynth createBackMethodSynth(ClassSynth classSynth){
        MethodSynth backMSynth = classSynth.createMethod(classSynth.pos(), BACK.toString());
        backMSynth.setFlag(Flags.PUBLIC);
        backMSynth.addFormal(compilerPos, Flags.FINAL, ts.Worker(), WORKER.toString());
        backMSynth.addFormal(compilerPos, Flags.FINAL, ts.Frame(), FRAME.toString());
        return backMSynth;
    }
    public ConstructorSynth createConstructorSynth(ClassSynth classSynth){
        ConstructorSynth conSynth = classSynth.createConstructor(classSynth.pos());
        conSynth.addAnnotation(genHeaderAnnotation());
        return conSynth;
    }
    
    
    public void genCopyConstructors(ClassSynth classSynth){
        
    }
    
    /**
     * Generate a remap method: "def remap() = new <the method type>(-1, this);"
     * @param cDecl The glass
     * @return the class with "remap" method inserted
     * @throws SemanticException
     */
    protected void genRemapMethod(ClassSynth classSynth, boolean isEmptyMethod) throws SemanticException {
        Context ct = classSynth.getContext();
        ClassType cType = classSynth.getDef().asType();
        Type scType = PlaceChecker.AddIsHereClause(cType, ct);
        MethodSynth methodSynth = classSynth.createMethod(classSynth.pos(), REMAP.toString());
                
        //upcast new instance
        Type upCastTargetType;
        if(ts.isSubtype(cType, ts.FinishFrame())){
            upCastTargetType = ts.FinishFrame();
        }
        else if(ts.isSubtype(cType, ts.RegularFrame())){
            upCastTargetType = ts.RegularFrame();
        }
        else{
            upCastTargetType = ts.Frame();
        }
        methodSynth.setReturnType(upCastTargetType);

        if (isEmptyMethod) {
            methodSynth.setFlag(Flags.PUBLIC.Abstract());
            return;
        }
        methodSynth.setFlag(Flags.PUBLIC);

        NewInstanceSynth niSynth = new NewInstanceSynth(nf, ct, compilerPos, cType);
        niSynth.addArgument(ts.Int(), synth.intValueExpr(-1, compilerPos) );
        niSynth.addArgument(scType, synth.thisRef(cType, compilerPos));
        
        Expr castReturn = genUpcastCall(cType, upCastTargetType, niSynth.genExpr());
        
        Stmt ret = nf.Return(compilerPos, castReturn);
        CodeBlockSynth blockSynth = methodSynth.getMethodBodySynth(compilerPos);
        blockSynth.addStmt(ret);
    }
    
    
    /**
     * Generate a copy constructor: 
     * def this(Int, o:<this_type>!){
     *    super(-1, upcast[this_Type, superType]o);
     *    ...//all fiedls' copy
     * }
     * @param cDecl The class to add this copy constructor
     * @param copyStmts statements besides super() call to copy fields. Could be null
     * @return the classDecl with the copy constructor
     * @throws SemanticException
     */
    public void genCopyConstructor(ClassSynth classSynth) throws SemanticException{

        Context ct = classSynth.getContext();
        ClassDef cDef = classSynth.getClassDef();
        Type cType = classSynth.getClassDef().asType();
        Type ccType = PlaceChecker.AddIsHereClause(cType, ct);
        Type sType = cDef.superType().get();
        Type scType = PlaceChecker.AddIsHereClause(sType, ct);

        ConstructorSynth conSynth = classSynth.createConstructor(compilerPos);
        conSynth.setFlags(Flags.PROTECTED);
        conSynth.addFormal(compilerPos, Flags.FINAL, ts.Int(), "x"); //a dummy int formal
        Expr otherRef = conSynth.addFormal(compilerPos, Flags.FINAL, ccType, "o");
        CodeBlockSynth conStmtsSynth = conSynth.createConstructorBody(compilerPos);
        SuperCallSynth superCallSynth = conStmtsSynth.createSuperCall(compilerPos, cDef);
        superCallSynth.addArgument(ts.Int(), synth.intValueExpr(-1, compilerPos));
        Expr upCastOtherRef = genUpcastCall(cType, sType, otherRef);
        superCallSynth.addArgument(scType, upCastOtherRef);

        // now try to add fields copy directly
        Receiver leftReceiver = synth.thisRef(cType, compilerPos); //this

        for (FieldDef df : cDef.fields()) {
            Name fieldName = df.name();
            //Ingore RETURN_VALUE copy
            if(fieldName.toString().equals(RETURN_VALUE.toString())
               || fieldName.toString().equals(RETURN_FLAG.toString())){
                continue; 
            }
            Stmt s = nf.Eval(compilerPos, synth.makeFieldToFieldAssign(compilerPos, leftReceiver, fieldName, otherRef,
                                                                       fieldName, ct));
            conStmtsSynth.addStmt(s);
        }
    }
    
    /**
     * Generate @Header def this(up:Frame) {  super(up); }
     * Used by Async Frame and Finish Frame
     * @param classSynth
     * @param conSynth
     * @return a constructor block synth to add more stmts
     */
    public ConstructorSynth genClassConstructorType1Base(ClassSynth classSynth){
        ConstructorSynth conSynth = createConstructorSynth(classSynth);
        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, ts.Frame(), UP.toString()); //up:Frame!
        
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        SuperCallSynth superCallSynth = codeBlockSynth.createSuperCall(compilerPos, classSynth.getClassDef());
        superCallSynth.addArgument(ts.Frame(), upRef);
        return conSynth;
    }
    
    /**
     * Generate @Header def this(up:Frame, ff:FinishFrame) {  super(up, ff); }
     * Used by RegularFrame & TryFrame
     * @param classSynth
     * @param conSynth
     * @return a constructor synth to add more stmts
     */
    public ConstructorSynth genClassConstructorType2Base(ClassSynth classSynth){
        ConstructorSynth conSynth = createConstructorSynth(classSynth);
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);

        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, ts.Frame(), UP.toString());
        Expr ffRef = conSynth.addFormal(compilerPos, Flags.FINAL, ts.FinishFrame(), FF.toString());
        SuperCallSynth superCallSynth = codeBlockSynth.createSuperCall(compilerPos, classSynth.getDef());
        superCallSynth.addArgument(ts.Frame(), upRef);
        superCallSynth.addArgument(ts.FinishFrame(), ffRef);
        return conSynth;
    }
    
    /**
     * Generate @Header def this(up:Frame, rd:Reducible[T]) {  super(up, rd); }
     * Used by FinishFrame (FinishExpr)
     * @param classSynth
     * @param conSynth
     * @return a constructor synth to add more stmts
     */
    public ConstructorSynth genClassConstructorType3Base(ClassSynth classSynth, Type reducerBaseType){
        ConstructorSynth conSynth = createConstructorSynth(classSynth);
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        Type reducerType = ts.Reducible().typeArguments(Collections.singletonList(reducerBaseType));

        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, ts.Frame(), UP.toString());
        Expr rdRef = conSynth.addFormal(compilerPos, Flags.FINAL, reducerType, RD.toString());
        SuperCallSynth superCallSynth = codeBlockSynth.createSuperCall(compilerPos, classSynth.getDef());
        superCallSynth.addArgument(ts.Frame(), upRef);
        superCallSynth.addArgument(reducerType, rdRef);
        return conSynth;
    }
    
    
    public Name createFieldFromLocal(ClassSynth classSynth, LocalDecl ld){
        Name fieldName = ld.name().id();
        FieldSynth localFieldSynth = classSynth.createField(ld.position(), fieldName.toString(), ld.type().type());
        localFieldSynth.addAnnotation(genUninitializedAnnotation());
        localFieldSynth.addAnnotation(genSuppressTransientErrorAnnotation());
        localFieldSynth.setFlags(Flags.TRANSIENT);
        return fieldName;
    }
    
    public Name createReturnFlagField(ClassSynth classSynth){
        FieldSynth returnFlagSynth = classSynth.createField(compilerPos, RETURN_FLAG.toString(), ts.Boolean());
        returnFlagSynth.addAnnotation(genUninitializedAnnotation());
        return RETURN_FLAG;
    }
    
    public Name createReturnValueField(ClassSynth classSynth, Type returnType){
        FieldSynth resurnFieldSynth = classSynth.createField(compilerPos, RETURN_VALUE.toString(), returnType);
        resurnFieldSynth.addAnnotation(genUninitializedAnnotation());
        return RETURN_VALUE;
    }
    
    /**
     * Create "@Uninitialized var pc:Int;"
     */
    protected void createPCField(ClassSynth classSynth){
        FieldSynth pcFieldSynth = classSynth.createField(compilerPos, PC.toString(), ts.Int());
        pcFieldSynth.addAnnotation(genUninitializedAnnotation());
    }
    
   /*
    * Generate cast[type1, type2](expr)
    * @param type1
    * @param type2
    * @param expr
    * @return cast[type1, type2](expr)
    * @throws SemanticException
    */
   Expr genCastCall(Type type1, Type type2, Expr expr, Context ct) throws SemanticException{
       List<TypeNode> genericTN = new ArrayList<TypeNode>();
       genericTN.add(nf.CanonicalTypeNode(compilerPos, Types.ref(type1)));
       genericTN.add(nf.CanonicalTypeNode(compilerPos, Types.ref(type2)));
       
       Call aCall = synth.makeStaticCall(compilerPos, 
                                         ts.Frame(),
                                         Name.make("cast"),
                                         genericTN, // This is for generic type, if any 
                                         Collections.singletonList(expr),
                                         PlaceChecker.AddIsHereClause(type2, ct),
                                         Collections.singletonList(type1),
                                         ct);
       
       return aCall;
   }
    
    
    /**
     * Generate upcast[type1, type2](expr)
     * @param type1
     * @param type2
     * @param expr
     * @return upcast[type1, type2](expr)
     * @throws SemanticException
     */
    Expr genUpcastCall(Type type1, Type type2, Expr expr)throws SemanticException{
        return expr;
    }
    
    /**
     * Return statement throw new RuntimeException(msg + appendInfo);
     * @param msg
     * @param appendInfo
     * @return
     * @throws SemanticException 
     */
    protected Stmt genThrowRuntimeExceptionStmt(String msg, Expr appendInfo, Context ct) throws SemanticException {
        NewInstanceSynth nis = new NewInstanceSynth(nf, ct, compilerPos, (ClassType) ts.Exception());

        Expr msgRef = synth.stringValueExpr(msg, compilerPos);
        
        Expr exceptionMsgRef = nf.Binary(compilerPos, msgRef, Binary.ADD, appendInfo).type(ts.String());
        nis.addArgument(ts.String(), exceptionMsgRef);
        
        return nf.Throw(compilerPos, nis.genExpr());
    }
    
    /**
     * Transform the original main method into a new main method
     * The new main method create the ws method instance, and call fast
     * @param classSynth the 
     * @param mainMethodDecl
     * @return
     * @throws SemanticException
     */
    public MethodDecl genNewMainMethod(ClassSynth classSynth, MethodDecl mainMethodDecl) throws SemanticException{
        
        Context ct = classSynth.getContext();
        NewInstanceSynth rSynth = new NewInstanceSynth(nf, ct, compilerPos, ts.RootFinish());
        NewLocalVarSynth nvSynth = new NewLocalVarSynth(nf, ct, compilerPos, ROOT_FINISH, Flags.FINAL, 
                                                        rSynth.genExpr(), ts.RootFinish(), Collections.EMPTY_LIST);

        //new _main(args)
        NewInstanceSynth niSynth = new NewInstanceSynth(nf, ct, compilerPos, classSynth.getClassDef().asType());
        niSynth.addAnnotation(genStackAllocateAnnotation());
        niSynth.addArgument(ts.Frame(), nvSynth.getLocal());
        niSynth.addArgument(ts.FinishFrame(), nvSynth.getLocal());
        for(Formal f : mainMethodDecl.formals()){
            //now add the type
            Type fType = f.localDef().type().get(); 
            Expr formalRef = nf.Local(compilerPos, nf.Id(compilerPos, f.name().id())).localInstance(f.localDef().asInstance()).type(fType);
            niSynth.addArgument(fType, formalRef);
        }
        Expr newMainExpr = niSynth.genExpr();
        // new _main(args).run();
        NewLocalVarSynth localSynth = new NewLocalVarSynth(nf, ct, compilerPos, Flags.FINAL, newMainExpr);
        localSynth.addAnnotation(genStackAllocateAnnotation());

        Expr mainCall = synth.makeStaticCall(compilerPos, ts.Worker(), Name.make("main"), Collections.<Expr>singletonList(localSynth.getLocal()), ts.Void(), ct);
        CodeBlockSynth cbSynth = new CodeBlockSynth(nf, ct, compilerPos);
        cbSynth.addStmt(nvSynth.genStmt());
        cbSynth.addStmt(localSynth.genStmt());
        cbSynth.addStmt(nf.Eval(compilerPos, mainCall));

        return (MethodDecl) mainMethodDecl.body(cbSynth.close());
    }
    
    /**
     * Generate one wrapper method 
     *   static def fib_fast(worker:Worker!, up:Frame!, ff:FinishFrame!, pc:Int, n:Int):Int {
     *       @StackAllocate val _tmp = @StackAllocate new _fib(up, ff, pc, n);
     *       return _tmp.fast(worker);
     *   }
     * @param containerClassDef the method's container class's def
     * @param methodName the newly method's name
     * @param targetClassDef the target class's def
     * @param targetMethodName the target method's name, fast or slow
     * @return the method synthesizer
     * @throws SemanticException 
     */
    public X10MethodDecl genWSMethod(ClassSynth classSynth, X10MethodDecl methodDecl) throws SemanticException{
        

        X10MethodDef methodDef =methodDecl.methodDef();
        Context ct = classSynth.getContext();
        X10ClassType containerClassType = (X10ClassType) methodDef.container().get();
        X10ClassDef containerClassDef = containerClassType.x10Def();
        
        String fastPathName = WSUtil.getMethodFastPathName(methodDef);
        MethodSynth methodSynth;
        methodSynth = new MethodSynth(nf, ct, methodDef.position(), containerClassDef, fastPathName);
        methodSynth.setFlag(methodDef.flags());
        methodSynth.setReturnType(methodDef.returnType().get());
       
        String targetMethodName = FAST.toString();
        
        //now process the formals
        Expr workerRef = methodSynth.addFormal(compilerPos, Flags.FINAL, ts.Worker(), WORKER.toString());
        Expr upRef = methodSynth.addFormal(compilerPos, Flags.FINAL, ts.Frame(), UP.toString());
        Expr ffRef = methodSynth.addFormal(compilerPos, Flags.FINAL, ts.FinishFrame(), FF.toString());
        
        //all other formals
        List<Expr> orgFormalRefs = new ArrayList<Expr>();
        List<Type> orgFormalTypes = new ArrayList<Type>();
        for(Formal f : methodDecl.formals()){
            orgFormalTypes.add(f.type().type());
            orgFormalRefs.add(methodSynth.addFormal(f)); //all formals are added in
        }
        //add all type parameters (template)
        X10MethodDecl mDecl = (X10MethodDecl)methodDecl;
        X10MethodDef mDef = mDecl.methodDef();
        int paramSize = mDef.typeParameters().size();
        for(int i = 0; i < paramSize; i++){
            methodSynth.addTypeParameter(mDef.typeParameters().get(i),
                                        mDecl.typeParameters().get(i).variance());          
        }

        //now create the body
        CodeBlockSynth mBodySynth = methodSynth.getMethodBodySynth(compilerPos);        
        NewInstanceSynth niSynth = new NewInstanceSynth(nf, ct, classSynth.pos(), classSynth.getClassDef().asType());
        niSynth.addArgument(ts.Frame(), upRef);
        niSynth.addArgument(ts.FinishFrame(), ffRef);
        niSynth.addArguments(orgFormalTypes, orgFormalRefs);
        niSynth.addAnnotation(genStackAllocateAnnotation());
        //special process for the new statement
        New newE = (New) niSynth.genExpr();        
        Special s = (Special) newE.qualifier();
        if(s != null){
            s = s.qualifier(null); //clear the type node            
            newE = newE.qualifier(s); //this method is outer, no need qualifier typenode
        }

        //special process for the new statement end
        
        NewLocalVarSynth localSynth = mBodySynth.createLocalVar(compilerPos, Flags.FINAL, newE);
        localSynth.addAnnotation(genStackAllocateAnnotation());
        Expr localRef = localSynth.getLocal(); //point to this inner class instance

        //_tmp.fast(worker) or _temp.slow(worker)
        InstanceCallSynth callSynth = new InstanceCallSynth(nf, ct, localRef, FAST.toString());
        callSynth.addArgument(ts.Worker(), workerRef);
        Expr callExpr = callSynth.genExpr();
        //if the method has return type, insert return, others, just call them
        if(callExpr.type() != null && callExpr.type() != ts.Void()){
            mBodySynth.addStmt(nf.Return(compilerPos, callExpr));
        }
        else{
            mBodySynth.addStmt(nf.Eval(callExpr.position(), callExpr));
        }
               
        return methodSynth.close();
    }
    
    public Expr genFFRef(ClassSynth classSynth) throws SemanticException {
        Type cType = classSynth.getDef().asType();
        Context ct = classSynth.getContext();
        Expr ffRef;
        if(ts.isSubtype(cType, ts.FinishFrame())){
            //if it self is ff type, upcast it self
            ffRef = genUpcastCall(cType, ts.FinishFrame(), genThisRef(classSynth));
        }
        else if(ts.isSubtype(cType, ts.AsyncFrame())){
            //if it self is ff type, upcast it self
            Expr upRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), UP, ct);
            ffRef = genCastCall(ts.Frame(), ts.FinishFrame(), upRef, ct);
        }
        else{
              //non finish frame will have ff field as ff 
              ffRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), FF, ct);
        }
        return ffRef;
    }
    
    /**
     * Gen (Frame.cast[XXXFrameType, CollectingFinish[int]](ffRef)).accept(this.t2, worker);
     * @param classSynth
     * @param methodSynth
     * @param offerExpr
     * @param reducerType e.g. int
     * @return
     * @throws SemanticException
     */
    public Stmt genOfferCallStmt(ClassSynth classSynth, MethodSynth methodSynth, Expr offerExpr, Type reducerType)throws SemanticException{
       
        X10ClassType cfType = ts.CollectingFinish();
        cfType = cfType.typeArguments(Collections.singletonList(reducerType));
        
        Type cType = classSynth.getDef().asType();
        Context ct = classSynth.getContext();
        Expr ffRef;
        Type ffRefType;
        if(ts.isSubtype(cType, ts.FinishFrame())){
            //if it self is ff type, upcast it self
            ffRef = genThisRef(classSynth);
            ffRefType = ts.FinishFrame();
        }
        else if(ts.isSubtype(cType, ts.AsyncFrame())){
            //if it self is ff type, upcast it self
            ffRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), UP, ct);
            ffRefType = ts.Frame();
        }
        else{
            //non finish frame will have ff field as ff 
            ffRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), FF, ct);
            ffRefType = ts.Frame(); 
        }
        Expr cfRef = genCastCall(ffRefType, cfType, ffRef, ct);
        Expr workerRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        InstanceCallSynth callSynth = new InstanceCallSynth(nf, ct, cfRef, ACCEPT.toString());
        callSynth.addArgument(reducerType, offerExpr);
        callSynth.addArgument(ts.Worker(), workerRef);
        return callSynth.genStmt();
    }
    
    /**
     * Transform org call to ws call, which means call the wrapper, and add ff/parent refs.
     * @param orgCall
     * @param classSynth
     * @param methodSynth
     * @return
     * @throws SemanticException
     */
    public Stmt genWSCallStmt(Call orgCall, ClassSynth classSynth, MethodSynth methodSynth) throws SemanticException{
        Context ct = classSynth.getContext();
      
        MethodInstance mi = WSUtil.createWSMethodInstance(orgCall.methodInstance(), ts);

        //preparing the references for invocation the call
        Type cType = classSynth.getDef().asType();
        Expr parentRef = genUpcastCall(cType, ts.Frame(), genThisRef(classSynth));
        Expr ffRef;
        if(ts.isSubtype(cType, ts.FinishFrame())){
          //if it self is ff type, upcast it self
          ffRef = genUpcastCall(cType, ts.FinishFrame(), genThisRef(classSynth));
        }
        else if(ts.isSubtype(cType, ts.AsyncFrame())){
          //if it self is ff type, upcast it self
          Expr upRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), UP, ct);
          ffRef = genCastCall(ts.Frame(), ts.FinishFrame(), upRef, ct);
        }
        else{
            //non finish frame will have ff field as ff 
            ffRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), FF, ct);
        }
        
        ArrayList<Expr> newArgs = new ArrayList<Expr>();
        Expr fastWorkerRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        newArgs.add(fastWorkerRef);
        newArgs.add(parentRef);
        newArgs.add(ffRef);
        
        X10Call fastMCall = WSUtil.replaceMethodCallWithWSMethodCall(nf,(X10Call) orgCall, mi, newArgs);
        
        return nf.Eval(orgCall.position(), fastMCall);
    }
    
    /**
     * Used by back method to generate aVar = cast[Frame,()=>xType](frame)();
     * @param methodSynth
     * @param type
     * @param assign
     * @return
     * @throws SemanticException
     */
    public Stmt genBackAssignStmt(MethodSynth methodSynth, Type type, Assign assign) throws SemanticException{
        // _m_fib.t1 = cast[Frame,()=>Int](frame)();
        Context ct = methodSynth.getContext();
        Expr backFrameRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(FRAME.toString());
        Expr castExp = genCastCall(ts.Frame(), 
                                   synth.simpleFunctionType(type, compilerPos),
                                   backFrameRef, ct);


        //FIXME: it should be a closure call. However, an instance call here is simpler.
        InstanceCallSynth niSynth = new InstanceCallSynth(nf, ct, compilerPos, castExp, OPERATOR.toString());
        assign = assign.right(niSynth.genExpr());
        return nf.Eval(assign.position(), assign);
    }
    
    /**
     * Generate invocation stmts to sub frames
     * @param pc the target pc after the call
     * @param classSynth the current class's synth
     * @param newClassGen
     * @return
     * @throws SemanticException 
     */
    public List<Stmt> genInvocateFrameStmts(int pc, ClassSynth classSynth, MethodSynth methodSynth, AbstractWSClassGen newClassGen) throws SemanticException{
        List<Stmt> stmts = new ArrayList<Stmt>();
        ClassType classType = classSynth.getClassDef().asType();
        ClassType newClassType = newClassGen.getClassType();
        Context ct = classSynth.getContext();
        //pc = x;
        try{
            //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
            stmts.add(genPCAssign(classSynth, pc));           
        }
        catch(polyglot.types.NoMemberException e){
            //Just ignore the pc assign statement
        }
      
        Expr workerRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        
        //if the new frame is an async frame, push() instructions should be inserted here
        if(ts.isSubtype(newClassType, ts.AsyncFrame())){
            stmts.add(genContinuationPush(classSynth, workerRef));
        }
        
        
        //create the instance
        NewInstanceSynth niSynth = new NewInstanceSynth(nf, ct, compilerPos, newClassType);
        
        //first parameter in constructor, parent
        if(ts.isSubtype(newClassType, ts.AsyncFrame())){
            Expr ffRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), FF, ct);
            Expr parentRef = genUpcastCall(ts.FinishFrame(), ts.Frame(), ffRef);
            niSynth.addArgument(ts.Frame(), parentRef);
        }
        else{ //upcast[_selfType,Frame](this);
            Expr parentRef = genUpcastCall(classType, ts.Frame(), genThisRef(classSynth));
            niSynth.addArgument(ts.Frame(), parentRef);
        }
        
        //process the 2nd parameter, ff.
        if(!ts.isSubtype(newClassType, ts.FinishFrame()) && !ts.isSubtype(newClassType, ts.AsyncFrame())){
            //if target is not finish frame, need add ff as reference
            Expr ffRef;
            if(ts.isSubtype(classType, ts.FinishFrame())){
                //if itself is ff type, upcast it self
                ffRef = genUpcastCall(classType, ts.FinishFrame(), genThisRef(classSynth));
            }
            else if(ts.isSubtype(classType, ts.AsyncFrame())){
                //if itself is async type, upcast it's up field
                Expr upRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), UP, ct);
                ffRef = genCastCall(ts.Frame(), ts.FinishFrame(), upRef, ct);
            } else {
                //non finish frame will have ff field as ff 
                ffRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), FF, ct);
            }
            niSynth.addArgument(ts.FinishFrame(), ffRef);
        }
        
        
        //Finally, if the newClassType is an async frame, may consider the adding formal
        if(ts.isSubtype(newClassType, ts.AsyncFrame())){
            WSAsyncClassGen asyncGen = (WSAsyncClassGen)newClassGen;
            
            //need access each formal, and add them into the parameters
            for(Pair<Name, Type> formal : asyncGen.formals){
                //make an access to the value; The value is referenced from async's parent continuation frame
                Expr fieldContainerRef = asyncGen.parentK.getFieldContainerRef(formal.fst(), formal.snd());
                Expr fieldRef = synth.makeFieldAccess(compilerPos, fieldContainerRef, formal.fst(), ct).type(formal.snd());
                niSynth.addArgument(formal.snd(), fieldRef);
            }
        }

        niSynth.addAnnotation(genStackAllocateAnnotation());
        NewLocalVarSynth localSynth = new NewLocalVarSynth(nf, ct, compilerPos, Flags.FINAL, niSynth.genExpr());
        localSynth.addAnnotation(genStackAllocateAnnotation());
        stmts.add(localSynth.genStmt());
        
        //now the instance's fast/slow method call
        Expr localRef = localSynth.getLocal(); // point to this inner class's instance
        InstanceCallSynth callSynth = new InstanceCallSynth(nf, ct, compilerPos, localRef, FAST.toString());
        callSynth.addArgument(ts.Worker(), workerRef);
        stmts.add(callSynth.genStmt());

        return stmts;
    }
    /*
     * Used by TryStmt transformation
     * worker.rethrow();
     */
    protected Stmt genRethrowStmt(MethodSynth methodSynth) throws SemanticException{
        Context ct = methodSynth.getContext();
        Expr workerRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        InstanceCallSynth icSynth = new InstanceCallSynth(nf, ct, compilerPos, workerRef, RETHROW.toString());
        return icSynth.genStmt();
    }
    
    /**
     * Generate an annotation node of type annotationType
     * WS will use the following types: Inline, StackAllocate, Uninitialized, Header
     * 
     * @param annotationType
     * @return
     */
    AnnotationNode genAnnotationNode(Type annotationType){
        TypeNode annoTN = nf.CanonicalTypeNode(compilerPos, annotationType).typeRef(Types.ref(annotationType));
        return nf.AnnotationNode(compilerPos, annoTN);
    }
    
    AnnotationNode genStackAllocateAnnotation(){
        return genAnnotationNode(ts.StackAllocate());
    }

    AnnotationNode genHeaderAnnotation(){
        return genAnnotationNode(ts.Header());
    }
    
    AnnotationNode genInlineAnnotation(){
        return genAnnotationNode(ts.InlineOnly());
    }
    
    AnnotationNode genUninitializedAnnotation(){
        return genAnnotationNode(ts.Uninitialized());
    }
    
    AnnotationNode genSuppressTransientErrorAnnotation(){
        return genAnnotationNode(ts.SuppressTransientError());
    }
    
    public Stmt genPCAssign(ClassSynth classSynth, int pc) throws SemanticException{
        Context ct = classSynth.getContext();
        Expr pcAssgn = synth.makeFieldAssign(compilerPos, genThisRef(classSynth), PC,
                                             synth.intValueExpr(pc, compilerPos), ct).type(ts.Int());
        return nf.Eval(compilerPos, pcAssgn);
    }
    
    
    /**
     * Generate  val rRootFinish:RemoteRootFinish = new RemoteRootFinish(ff);
     * @param classSynth
     * @return
     * @throws SemanticException
     */
    public Pair<Stmt, Expr> genRemoteRootFinishInitializer(ClassSynth classSynth) throws SemanticException{
        Expr ffRef = genFFFieldRef(classSynth);
        
        Context ct = classSynth.getContext();
        //prepare val rRootFinish:RemoteRootFinish = new RemoteRootFinish(ff).init();
        NewInstanceSynth remoteRootFFSynth = new NewInstanceSynth(nf, ct, compilerPos, ts.RemoteFinish());
        remoteRootFFSynth.addAnnotation(genStackAllocateAnnotation());
        remoteRootFFSynth.addArgument(ts.FinishFrame(), ffRef);
        NewLocalVarSynth remoteRootFFRefLocalSynth = new NewLocalVarSynth(nf, ct, compilerPos, Flags.FINAL, remoteRootFFSynth.genExpr());
        remoteRootFFRefLocalSynth.addAnnotation(genStackAllocateAnnotation());
        Expr remoteRootFFRef = remoteRootFFRefLocalSynth.getLocal();
        return new Pair<Stmt, Expr>(remoteRootFFRefLocalSynth.genStmt(), remoteRootFFRef);
    }
    
    public Stmt genRunAsyncAt(ClassSynth classSynth, Expr place, Type remoteMainFrameType, Expr remoteMainRef)  throws SemanticException{
        Context ct = classSynth.getContext();
        if (ts.hasSameClassDef(Types.baseType(place.type()), ts.GlobalRef())) {
            place = synth.makeFieldAccess(compilerPos ,place, ts.homeName(), ct);
        }
        
        InstanceCallSynth callSynth = new InstanceCallSynth(nf, ct, compilerPos, nf.CanonicalTypeNode(compilerPos, ts.Worker()), RUN_ASYNC_AT.toString());
        callSynth.addArgument(ts.Place(), place);
        callSynth.addArgument(remoteMainFrameType, remoteMainRef);
        return callSynth.genStmt();
    }
    
    public Stmt genRunAt(ClassSynth classSynth, Expr place, Type remoteMainFrameType, Expr remoteMainRef)  throws SemanticException{
        Context ct = classSynth.getContext();
        if (ts.hasSameClassDef(Types.baseType(place.type()), ts.GlobalRef())) {
            place = synth.makeFieldAccess(compilerPos ,place, ts.homeName(), ct);
        }
        
        InstanceCallSynth callSynth = new InstanceCallSynth(nf, ct, compilerPos, nf.CanonicalTypeNode(compilerPos, ts.Worker()), RUN_AT.toString());
        callSynth.addArgument(ts.Place(), place);
        callSynth.addArgument(remoteMainFrameType, remoteMainRef);
        return callSynth.genStmt();
    }
    
    /**
     * Generate upcast[_finish_body,Continuation](this).push(worker);
     * The worker ref is different for fast/resume path
     * @param workerRef
     * @return
     * @throws SemanticException 
     */
    protected Stmt genContinuationPush(ClassSynth classSynth, Expr workerRef) throws SemanticException{
        Type classType =  classSynth.getClassDef().asType();
        Context ct = classSynth.getContext();
        // push(worker)
        Expr castThisRef = genUpcastCall(classType, ts.RegularFrame(), genThisRef(classSynth));
        
        InstanceCallSynth icSynth = new InstanceCallSynth(nf, ct, compilerPos, castThisRef, PUSH.toString());
        // continuationFrame
        icSynth.addArgument(ts.Worker(), workerRef);
        return icSynth.genStmt();
    }
    
    public Expr genThisRef(ClassSynth classSynth){
        Type type =  classSynth.getClassDef().asType();
        return synth.thisRef(type, compilerPos);
    }
    
    public Expr genFFFieldRef(ClassSynth classSynth) throws SemanticException{
        return synth.makeFieldAccess(compilerPos, genThisRef(classSynth), FF, classSynth.getContext());
    }
    
    public Expr genPCRef(ClassSynth classSynth) throws SemanticException{
        Context ct = classSynth.getContext();
        Expr pcRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), PC, ct);
        return pcRef;
    }
    
    
    /**
     * Generate
     *   try { tryBodyStmts}
     *   catch (t:x10.compiler.Abort) { throw t; }
     *   catch (t:x10.lang.Throwable) { this.caught(t); }
     * Used by Async frame and finish frame. The catch Abort will not be generated here.
     * It will be added in later compilation pass
     * @param tryBodyStmts
     * @return
     * @throws SemanticException
     */
    public Try genExceptionHandler(List<Stmt> tryBodyStmts, ClassSynth classSynth) throws SemanticException {
        Context ct = classSynth.getContext();
        Name formalName = ct.getNewVarName();
        
        Formal f = synth.createFormal(compilerPos, ts.Exception(), formalName, Flags.NONE);
        Expr caught = synth.makeInstanceCall(compilerPos, genThisRef(classSynth),
                CAUGHT, Collections.<TypeNode>emptyList(), Collections.<Expr>singletonList(
                        nf.Local(compilerPos, nf.Id(compilerPos, formalName)).localInstance(f.localDef().asInstance()).type(ts.Exception())), ts.Void(),
                Collections.<Type>singletonList(ts.Exception()), ct);
        Catch c = nf.Catch(compilerPos, f, nf.Block(compilerPos,
                nf.Eval(compilerPos, caught)));
        
        Try t = nf.Try(compilerPos, nf.Block(compilerPos, tryBodyStmts), Collections.<Catch>singletonList(c));
        return t;
    }
    
    /**
     * Used by Finish frame's rethrow
     * @param classSynth
     * @return
     * @throws SemanticException
     */
    public Stmt genRethrowStmt(ClassSynth classSynth) throws SemanticException{
        //fast path: //upcast[_async,AsyncFrame](this).rethrow(worker);
        Context ct = classSynth.getContext();
        InstanceCallSynth icSynth = new InstanceCallSynth(nf, ct, compilerPos, genThisRef(classSynth), RETHROW.toString());
        return icSynth.genStmt();
    }
    
    public Stmt genReturnResultStmt(ClassSynth classSynth) throws SemanticException{
        Context ct = classSynth.getContext();
        Expr resultRef = synth.makeFieldAccess(compilerPos, genThisRef(classSynth), RETURN_VALUE, ct);
        return nf.Return(compilerPos, resultRef);
    }
    
    /**
     * Generate this.continueLater(worker); Used by blocking constructs, such as when
     * @param classSynth
     * @param methodSynth
     * @return
     * @throws SemanticException
     */
    public Stmt genContinueLaterStmt(ClassSynth classSynth, MethodSynth methodSynth) throws SemanticException{
        Type ctype = classSynth.getClassDef().asType();
        Context ct = classSynth.getContext();
        Expr thisRef = genUpcastCall(ctype, ts.RegularFrame(), genThisRef(classSynth));
        InstanceCallSynth redoCallSynth = new InstanceCallSynth(nf, ct, compilerPos, thisRef, CONTINUE_LATER.toString());
        Expr workerRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        redoCallSynth.addArgument(ts.Worker(), workerRef);
        return redoCallSynth.genStmt();
    }
    
    
    /**
     * Generate this.continueNow(worker); Used to move frames to heap in remote frame transform
     * @param classSynth
     * @param methodSynth
     * @return
     * @throws SemanticException
     */
    public Stmt genContinueNowStmt(ClassSynth classSynth, MethodSynth methodSynth) throws SemanticException{
        Type ctype = classSynth.getClassDef().asType();
        Context ct = classSynth.getContext();
        Expr thisRef = genUpcastCall(ctype, ts.RegularFrame(), genThisRef(classSynth));
        InstanceCallSynth redoCallSynth = new InstanceCallSynth(nf, ct, compilerPos, thisRef, CONTINUE_NOW.toString());
        Expr workerRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        redoCallSynth.addArgument(ts.Worker(), workerRef);
        return redoCallSynth.genStmt();
    }
    
    
    /**
     * Used by async frame, gen "this.poll(worker)"
     * @param classSynth
     * @param methodSynth
     * @return
     * @throws SemanticException
     */
    public Stmt genPollStmt(ClassSynth classSynth, MethodSynth methodSynth) throws SemanticException{
        //fast path: //upcast[_async,AsyncFrame](this).poll(worker);
        Type cType = classSynth.getClassDef().asType();
        Context ct = classSynth.getContext();
        Expr upThisExpr = genUpcastCall(cType, ts.AsyncFrame(), genThisRef(classSynth));
        
        InstanceCallSynth icSynth = new InstanceCallSynth(nf, ct, compilerPos, upThisExpr, POLL.toString());
        Expr fastWorkerRef = methodSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        icSynth.addArgument(ts.Worker(), fastWorkerRef);        
        return icSynth.genStmt();
    }
    
    
    
    
//    public Expr getFastWorkerRef() {
//        return classFrame.getFastMethodSynth().getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
//    }
//    
//    public Expr getResumeWorkerRef() {
//        return classFrame.getResumeMethodSynth().getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
//    }
//
//    public Expr getBackWorkerRef() {
//        return classFrame.getBackMethodSynth().getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
//    }
//    public Expr getBackFrameRef() {
//        return classFrame.getResumeMethodSynth().getMethodBodySynth(compilerPos).getLocal(FRAME.toString());
//    }
}
