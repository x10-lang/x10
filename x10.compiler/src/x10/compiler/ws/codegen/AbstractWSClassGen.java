/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */


package x10.compiler.ws.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.Do;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Term;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.While;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;

import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Closure;
import x10.ast.Finish;
import x10.ast.When;
import x10.ast.X10Call;
import x10.ast.X10ClassDecl;
import x10.extension.X10Ext_c;
import x10.compiler.ws.WSCodeGenerator;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.AdvLocalAccessToFieldAccessReplacer;
import x10.compiler.ws.util.CodePatternDetector;
import x10.compiler.ws.util.ILocalToFieldContainerMap;
import x10.compiler.ws.util.ReferenceContainer;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.MethodInstance;
import polyglot.types.Context;
import x10.types.X10LocalDef;
import x10.types.X10MethodDef;
import polyglot.types.TypeSystem;
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
import x10.visit.ExpressionFlattener;

/**
 * @author Haichuan
 * 
 * The base of different kinds of WS class generator
 * 
 * Because one method will be divided into different segments (control flows)
 * and each segment will be transformed into one inner class,
 * these inner classes are chained together in a tree structure.
 * 
 * In order to represent the tree structure, the AbstractWSClassGen
 * is a tree node, too. It has parent and child
 *
 */
public abstract class AbstractWSClassGen implements ILocalToFieldContainerMap{
    static final protected Position compilerPos = Position.COMPILER_GENERATED;
    static final protected Name FAST = Name.make("fast");
    static final protected Name PUSH = Name.make("push");
    static final protected Name POLL = Name.make("poll");
    static final protected Name RESUME = Name.make("resume");
    static final protected Name BACK = Name.make("back");
    static final protected Name MOVE = Name.make("move");
    static final protected Name FINALIZE = Name.make("finalize");
    static final protected Name CAUGHT = Name.make("caught");
    static final protected Name REMOTE_RUN_FRAME = Name.make("remoteRunFrame");
    static final protected Name REMOTE_AT_NOTIFY = Name.make("remoteAtNotify");    
    static final protected Name WORKER = Name.make("worker");
    static final protected Name FRAME = Name.make("frame");
    static final protected Name PC = Name.make("_pc");
    static final protected Name FF = Name.make("ff");
    static final protected Name UP = Name.make("up");
    static final protected Name BLOCK_FLAG = Name.make("_bf");
    static final protected Name ASYNCS = Name.make("asyncs");
    static final protected Name REDIRECT = Name.make("redirect");
    static final protected Name REDO = Name.make("redo");
    static final protected Name INIT = Name.make("init");
    static final protected Name OPERATOR = Name.make("operator()");
    static final protected Name ENTER_ATOMIC = Name.make("enterAtomic");
    static final protected Name EXIT_WHEN = Name.make("exitWSWhen");
    

    final protected Job job;
    final protected NodeFactory xnf;
    final protected TypeSystem xts;
    final protected Context xct;
    final protected WSTransformState wts;
    final protected Synthesizer synth; //stateless synthesizer

    final protected Block codeBlock; // store all code block

    final protected Set<Name> fieldNames; //store names of all fields in current frame
    final protected String className; //used for child to query, and form child's name
    final protected ClassSynth classSynth; //stateful synthesizer for class gen
    final protected int frameDepth; //the depth to parent;

    final protected MethodSynth fastMSynth;
    final protected MethodSynth resumeMSynth;
    final protected MethodSynth backMSynth;
    final protected ConstructorSynth conSynth;

    //Fields to maintain the tree
    final private AbstractWSClassGen up;
    private List<AbstractWSClassGen> children; //lazy initialization


    private AbstractWSClassGen(Job job, Context xct, WSTransformState wts, AbstractWSClassGen up,
            String className, ClassType frameType, int frameDepth, Flags flags, ClassDef outer, Stmt stmt) {
        this.job = job;
        xnf = (NodeFactory) job.extensionInfo().nodeFactory();
        xts = (TypeSystem) job.extensionInfo().typeSystem();
        synth = new Synthesizer(xnf, xts);
        this.xct = xct;
        this.wts = wts;
        this.up = up;
        
        this.codeBlock = stmt == null ? null : synth.toBlock(stmt); // switch statement has null codeBlock
        
        this.className = className;
        classSynth = new ClassSynth(job, xnf, xct, frameType, className);
        classSynth.setKind(ClassDef.MEMBER);
        classSynth.setFlags(flags);
        classSynth.setOuter(outer);
        
        fieldNames = CollectionFactory.newHashSet(); //used to store all other fields' names
        
        this.frameDepth = frameDepth;
        
        fastMSynth = classSynth.createMethod(compilerPos, FAST.toString());
        fastMSynth.setFlag(Flags.PUBLIC);
        if (isFastPathInline(frameType)){
            //only set inline to inner frames, not top frames
            fastMSynth.addAnnotation(genInlineAnnotation());
        }
        fastMSynth.addFormal(compilerPos, Flags.FINAL, wts.workerType, WORKER.toString());
        
        resumeMSynth = classSynth.createMethod(compilerPos, RESUME.toString());
        resumeMSynth.setFlag(Flags.PUBLIC);
        resumeMSynth.addFormal(compilerPos, Flags.FINAL, wts.workerType, WORKER.toString());
        
        backMSynth = classSynth.createMethod(compilerPos, BACK.toString());
        backMSynth.setFlag(Flags.PUBLIC);
        backMSynth.addFormal(compilerPos, Flags.FINAL, wts.workerType, WORKER.toString());
        backMSynth.addFormal(compilerPos, Flags.FINAL, wts.frameType, FRAME.toString());

        conSynth = classSynth.createConstructor(compilerPos);
        conSynth.addAnnotation(genHeaderAnnotation());
    }

    // method frames
    protected AbstractWSClassGen(Job job, NodeFactory xnf, Context xct, WSTransformState wts,
            String className, ClassType frameType, Flags flags, ClassDef outer, Stmt stmt) {
        this(job, xct, wts, null, className, frameType, 0, flags, outer, stmt);
    }

    // nested frames
    protected AbstractWSClassGen(AbstractWSClassGen parent, AbstractWSClassGen up,
            String classNamePrefix, ClassType frameType, Stmt stmt) {
        this(parent.job, parent.xct, parent.wts, up, classNamePrefix + parent.assignChildId(), frameType,
                parent.frameDepth + 1, parent.classSynth.getClassDef().flags(),
                parent.classSynth.getOuter(), stmt);
        parent.addChild(this);
        //here we process the type parameters. For a target method that contains type parameters,
        //all the generated inner classes should contain the type parameters;
        X10ClassDef parentDef = parent.classSynth.getClassDef();
        classSynth.addTypeParameters(parentDef.typeParameters(), parentDef.variances());
    }


    
    /**
     * Return direct children
     * @return
     */
    public AbstractWSClassGen[] getChildren() {
        if(children == null){
            return new AbstractWSClassGen[0];
        }
        else{
            return children.toArray(new AbstractWSClassGen[children.size()]);
        }
    }
    
    /**
     * Used to pretty print
     * @param indent the indent before the output
     * @return
     */
    public String getFrameStructureDesc(int indent){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < indent; i++){
            sb.append(' ');
        }
        
        sb.append(this.className);
        if(this instanceof WSAsyncClassGen){
            WSAsyncClassGen aFrame = (WSAsyncClassGen)this;
            
            sb.append(" (k = ").append(aFrame.parentK.className).append(')');
        }
        if(this instanceof WSRemoteMainFrameClassGen){
            WSRemoteMainFrameClassGen rFrame = (WSRemoteMainFrameClassGen)this;
            
            sb.append(" (r = ").append(rFrame.parentR.className).append(')');
        }
        sb.append(System.getProperty("line.separator"));

        AbstractWSClassGen[] children = getChildren();
        for(AbstractWSClassGen child : children){
            sb.append(child.getFrameStructureDesc(indent + 2));
        }
        return sb.toString();
    }
    
    
    /**
     * Close and return all the inner classes rooted by the current class as one list
     * @return
     */
    public List<X10ClassDecl> close() throws SemanticException {
        ArrayList<X10ClassDecl> classes = new ArrayList<X10ClassDecl>();
        recursiveAddClasses(classes, this);
        return classes;
    }
    

    public int getFrameDepth() {
        return frameDepth;
    }

    private void recursiveAddClasses(ArrayList<X10ClassDecl> classes, AbstractWSClassGen parent) throws SemanticException {
        // This method is only used by public AbstractWSClassGen[] genAllOffString()
        classes.add(parent.classSynth.close());
        AbstractWSClassGen[] children = parent.getChildren();
        for(AbstractWSClassGen child : children){
            recursiveAddClasses(classes, child);
        }
    }
    
    /**
     * And one inner class into the current class to build the frame tree
     * @param child
     * @return the child's sequence id used form the child frame's name
     */
    public void addChild(AbstractWSClassGen child) {
        if(children == null){
            children = new ArrayList<AbstractWSClassGen>();
        }
        children.add(child);
    }
    
    
    int childrenNameCount; //start from 0. not the same as children's number 
                           //since finish frame has more children classFrame
    /**
     * Query the child's sequence id in parent
     * @param child
     * @return the id num;
     */
    public synchronized int assignChildId() {
        int retId = childrenNameCount;
        childrenNameCount ++;
        return retId;  
    }
    
    public AbstractWSClassGen getUpFrame() {
        return up;
    }
    
    public X10ClassDef getClassDef(){
        return classSynth.getClassDef();
    }
    
    //TODO: interfaces to query one specific parent    
    //From the list, we could query all added inner classes

    public String getClassName() {
        return className;
    }
    
    public NodeFactory getX10NodeFactory() {
        return xnf;
    }

    public Context getX10Context() {
        return xct;
    }

    public WSTransformState getWSTransformState() {
        return wts;
    }

    /**
     * Use the generator to generate all kinds of classes.
     * Need add the new inner class to its parent frame class
     * @throws SemanticException
     */
    public final void genClass() throws SemanticException {
        genMethods(); //fast/resume/move
        genClassConstructor();
        if (wts.realloc) genCopyConstructor(compilerPos);
        if (wts.realloc) genRemapMethod();
    }

    protected abstract void genMethods() throws SemanticException;

    protected abstract void genClassConstructor() throws SemanticException;
    
    
    /**
     * Trigger class generation, and close the class synth
     * @return the generated class decl
     * @throws SemanticException 
     */
    
    public X10ClassType getClassType(){
        return (X10ClassType) classSynth.getClassDef().asType();
    }
    
    public Expr getThisRef(){
        return synth.thisRef(getClassType(), compilerPos);
    }
    
    /**
     * Create "@Uninitialized var pc:Int;"
     */
    protected void addPCField(){
        FieldSynth pcFieldSynth = classSynth.createField(compilerPos, PC.toString(), xts.Int());
        pcFieldSynth.addAnnotation(genUninitializedAnnotation());
    }
    
    
    /**
     * Return statement throw new RuntimeException(msg + appendInfo);
     * @param msg
     * @param appendInfo
     * @return
     * @throws SemanticException 
     */
    protected Stmt genThrowRuntimeExceptionStmt(String msg, Expr appendInfo) throws SemanticException {
        NewInstanceSynth nis = new NewInstanceSynth(xnf, xct, compilerPos, (ClassType) xts.RuntimeException());

        Expr msgRef = synth.stringValueExpr(msg, compilerPos);
        
        Expr exceptionMsgRef = xnf.Binary(compilerPos, msgRef, Binary.ADD, appendInfo).type(xts.String());
        nis.addArgument(xts.String(), exceptionMsgRef);
        
        return xnf.Throw(compilerPos, nis.genExpr());
    }
    
    /**
     * It will decide the right child frame's class gen, and return the right type to caller
     * childSuperType could be only: asyncFrame, finishFrame, regularFrame
     * asyncFrame: WSAsyncClassGen
     * finishFrame: WSFinishStmtClassGen
     * regularFrame:WSRegularFrameClassGen or ForLoopClassGen or DoLoopClassGen or SwitchClassGen
     * @param childSuperType: could be only three: async, finish, regular
     * @param stmt
     * @param namePrefix: only useful for regularFrame type
     * @return
     * @throws SemanticException 
     */
    protected AbstractWSClassGen genChildFrame(Type childSuperType, Stmt stmt, String namePrefix) throws SemanticException{
        
        AbstractWSClassGen childClassGen = null;
        
        if(childSuperType == wts.finishFrameType){
            //stmt is Finish
            childClassGen = new WSFinishStmtClassGen(this, (Finish)stmt);
        }
        else{
            //first unroll to one stmt; //remove additional no use stmt
            stmt = WSCodeGenUtility.unrollToOneStmt(stmt); 
            if(stmt instanceof For){
                childClassGen = new WSForLoopClassGen(this, (For)stmt);
            }
            else if(stmt instanceof While || stmt instanceof Do){
                childClassGen = new WSWhileDoLoopClassGen(this, (Loop)stmt);
            }
            else if(stmt instanceof Switch){
                childClassGen = new WSSwitchClassGen(this, (Switch)stmt);
            }
            else if(stmt instanceof When){
                childClassGen = new WSWhenFrameClassGen(this, (When)stmt);
            }
            else if(stmt instanceof Try){
                childClassGen = new WSTryStmtClassGen(this, (Try)stmt);
            }
            else if(stmt instanceof Async){
                //Two situations:
                //1) current frame is aysnc frame stmt or finish frame, we need wrap async into a block, 
                //   no matter the async is async or async at, and use the current name as prefix
                //2) current frame is normal stmt, just transform it directly
                if(xts.isSubtype(getClassType(), wts.asyncFrameType)
                        || xts.isSubtype(getClassType(), wts.finishFrameType)){
                    childClassGen = new WSRegularFrameClassGen(this, synth.toBlock(stmt),
                                                               WSCodeGenUtility.getBlockFrameClassName(this.getClassName()));
                }
                else{
                    Async async = (Async)stmt;
                    Stmt asyncBody = WSCodeGenUtility.unrollToOneStmt(async.body());
                    //need check the frame is pure async or async at(p)
                    if(asyncBody instanceof AtStmt){
                        //async at(p), transform it as a remote frame
                        childClassGen = new WSRemoteMainFrameClassGen(this, (AtStmt)asyncBody, true);
                    }
                    else{
                        //pure async
                        childClassGen = new WSAsyncClassGen(this, (Async)stmt); 
                    }
                   
                }
            }
            else if(stmt instanceof AtStmt){
                //Transform it as at remoteframe
                childClassGen = new WSRemoteMainFrameClassGen(this, (AtStmt)stmt, false);
            }
            else{
                //stmt.prettyPrint(System.out);               
                //TODO: optimization point: if from finish frame, the stmt is a loop
                //could unloop it and call for frame directly
                //assert(stmt instanceof Block); //definetly need to be block
                childClassGen = new WSRegularFrameClassGen(this, stmt, namePrefix);
            }
        }
        childClassGen.genClass(); //
        return childClassGen;
    }
    
    
    /**
     * Generate this type of codes
     *       @StackAllocate val _m_finish_body = @StackAllocate new _finish_body(upcast[_finish,Frame](this), upcast[_finish,FinishFrame](this));
     *       _m_finish_body.fast(worker);
     * @return transcoces, first: fast; second resume();
     * @throws SemanticException 
     */
    protected TransCodes genInvocateFrameStmts(int pc, AbstractWSClassGen newClassGen) throws SemanticException{
        TransCodes transCodes = new TransCodes(pc);
        
        ClassType newClassType = newClassGen.getClassType();
        //pc = x;
        try{
            //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
            
            Expr pcAssgn = synth.makeFieldAssign(compilerPos, getThisRef(), PC,
                                                 synth.intValueExpr(pc, compilerPos), xct).type(xts.Int());
            transCodes.addFirst(xnf.Eval(compilerPos, pcAssgn));
            transCodes.addSecond(xnf.Eval(compilerPos, pcAssgn));              
        }
        catch(polyglot.types.NoMemberException e){
            //Just ignore the pc assign statement
        }
      
        
        
        //if the new frame is an async frame, push() instructions should be inserted here
        if(xts.isSubtype(newClassType, wts.asyncFrameType)){
            Expr fastWorkerRef = fastMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
            transCodes.addFirst(genContinuationPush(fastWorkerRef));
            
            Expr resumeWorkerRef = resumeMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
            transCodes.addSecond(genContinuationPush(resumeWorkerRef));
        }
        
        
        //create the instance
        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, newClassType);
        
        //first parameter in constructor, parent
        if(xts.isSubtype(newClassType, wts.asyncFrameType)){
            Expr ffRef = synth.makeFieldAccess(compilerPos, getThisRef(), FF, xct);
            Expr parentRef = genUpcastCall(wts.finishFrameType, wts.frameType, ffRef);
            niSynth.addArgument(wts.frameType, parentRef);
        }
        else{ //upcast[_selfType,Frame](this);
            Expr parentRef = genUpcastCall(getClassType(), wts.frameType, getThisRef());
            niSynth.addArgument(wts.frameType, parentRef);
        }
        
        //process the 2nd parameter, ff.
        if(!xts.isSubtype(newClassType, wts.finishFrameType) && !xts.isSubtype(newClassType, wts.asyncFrameType)){
            //if target is not finish frame, need add ff as reference
            Expr ffRef;
            if(xts.isSubtype(getClassType(), wts.finishFrameType)){
                //if itself is ff type, upcast it self
                ffRef = genUpcastCall(getClassType(), wts.finishFrameType, getThisRef());
            }
            else if(xts.isSubtype(getClassType(), wts.asyncFrameType)){
                //if itself is ff type, upcast it self
                Expr upRef = synth.makeFieldAccess(compilerPos, getThisRef(), UP, xct);
                ffRef = genCastCall(wts.frameType, wts.finishFrameType, upRef);
            } else {
                //non finish frame will have ff field as ff 
                ffRef = synth.makeFieldAccess(compilerPos, getThisRef(), FF, xct);
            }
            niSynth.addArgument(wts.finishFrameType, ffRef);
        }
        
        //Finally, if the newClassType is an async frame, may consider the adding formal
        if(xts.isSubtype(newClassType, wts.asyncFrameType)){
            WSAsyncClassGen asyncGen = (WSAsyncClassGen)newClassGen;
            //need access each formal, and add them into the parameters
            for(Pair<Name, Type> formal : asyncGen.formals){
                //make an access to the value;
                Expr fieldContainerRef = this.getFieldContainerRef(formal.fst(), formal.snd());
                Expr fieldRef = synth.makeFieldAccess(compilerPos, fieldContainerRef, formal.fst(), xct).type(formal.snd());
                niSynth.addArgument(formal.snd(), fieldRef);
            }
        }

        niSynth.addAnnotation(genStackAllocateAnnotation());
        NewLocalVarSynth localSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, niSynth.genExpr());
        localSynth.addAnnotation(genStackAllocateAnnotation());
        transCodes.addFirst(localSynth.genStmt());
        transCodes.addSecond(localSynth.genStmt());
        
        //now the instance's fast/slow method call
        Expr localRef = localSynth.getLocal(); // point to this inner class's instance
       
        { //fast
            Expr fastWorkerRef = fastMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
            InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, compilerPos, localRef, FAST.toString());
            callSynth.addArgument(wts.workerType, fastWorkerRef);
            transCodes.addFirst(callSynth.genStmt());
        }
        { //resume
            Expr resumeWorkerRef = resumeMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
            InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, compilerPos, localRef, FAST.toString());
            callSynth.addArgument(wts.workerType, resumeWorkerRef);
            transCodes.addSecond(callSynth.genStmt());
        }
        return transCodes;
    }
    
    /**
     * Generate upcast[_finish_body,Continuation](this).push(worker);
     * The worker ref is different for fast/resume path
     * @param workerRef
     * @return
     * @throws SemanticException 
     */
    protected Stmt genContinuationPush(Expr workerRef) throws SemanticException{
        
        // push(worker)
        Expr castThisRef = genUpcastCall(getClassType(), wts.regularFrameType, getThisRef());
        
        InstanceCallSynth icSynth = new InstanceCallSynth(xnf, xct, compilerPos, castThisRef, PUSH.toString());
        // continuationFrame
        icSynth.addArgument(wts.workerType, workerRef);
        return icSynth.genStmt();
    }
    
    
    /**
     * Normal codes, no concurrent method/construct
     * Just replace local refs as field access
     */
    protected TransCodes transNormalStmt(Stmt s, int pcValue, Set<Name> declaredLocals) throws SemanticException {
        assert(!WSCodeGenUtility.isComplexCodeNode(s, wts));

        TransCodes transCodes = new TransCodes(pcValue);
        s = (Stmt) this.replaceLocalVarRefWithFieldAccess(s, declaredLocals);
        
        //need process the return's issue;
        TransReturnVisitor fastRV = new TransReturnVisitor(true);
        transCodes.addFirst((Stmt)s.visit(fastRV));
        TransReturnVisitor resumeRV = new TransReturnVisitor(false);
        transCodes.addSecond((Stmt)s.visit(resumeRV));
        return transCodes;
    }
    
    class TransReturnVisitor extends NodeVisitor{
        private boolean fastPath; //fast or resume path
        private int noTransformDepth; //not transform "return" in Closure or anonymous class body
        TransReturnVisitor(boolean fastPath){
            this.fastPath = fastPath;
        }

        @Override
        public NodeVisitor enter(Node n) {
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noTransformDepth++;
            }
            return super.enter(n);
        }

        public Node leave(Node old, Node n, NodeVisitor v) {

            if(n instanceof Closure
                    || n instanceof ClassBody){
                noTransformDepth--;
                return n;
            }
            
            if(n instanceof Return && noTransformDepth == 0){ //return not in closure
                Return r = (Return)n;
                try {
                    if(AbstractWSClassGen.this instanceof WSMethodFrameClassGen){
                        if(fastPath){ //directly return, no others
                            return n; 
                        }
                        else{  //slow path: result = expr(); return;
                            List<Stmt> stmts = new ArrayList<Stmt>();
                            if (r.expr() != null && r.expr().type() != xts.Void()) {
                                WSMethodFrameClassGen methodFrameGen = (WSMethodFrameClassGen)AbstractWSClassGen.this;
                                
                                Expr returnValueAssign;
                                returnValueAssign = synth.makeFieldAssign(r.position(),
                                                                          getThisRef(),
                                                                          methodFrameGen.getReturnFieldName(), 
                                                                          r.expr(), xct);
                                stmts.add(xnf.Eval(r.position(), returnValueAssign));
                                
                            }
                            stmts.add(xnf.Return(r.position())); //return
                            return xnf.StmtSeq(r.position(), stmts);
                        }
                    }
                    else{ //non method frame
                        List<Stmt> stmts = new ArrayList<Stmt>();
                        Name resultName = queryMethodResultFieldName();
                        Name returnFlagName = queryMethodReturnFlagName();
                        Expr methodFrameRef = getFieldContainerRef(returnFlagName, xts.Boolean());
    
                        // parent.returnFlag = true;
                        Expr returnFlagAssign = synth.makeFieldAssign(r.position(), methodFrameRef, 
                                                                      returnFlagName,
                                                                      synth.booleanValueExpr(true, r.position()), xct);
                        stmts.add(xnf.Eval(compilerPos, returnFlagAssign));
                        // parent.result = expr();
                        if (r.expr() != null && r.expr().type() != xts.Void()) {
                            Expr returnValueAssign = synth.makeFieldAssign(r.position(),
                                                                           methodFrameRef, 
                                                                           resultName,
                                                                           r.expr(), xct);
                            stmts.add(xnf.Eval(compilerPos, returnValueAssign));
                        }
                        stmts.add(xnf.Return(r.position())); //return
                        return xnf.StmtSeq(r.position(), stmts);
                    }
                } catch (SemanticException e) {
                    System.err.println("[WS_ERR]Return transformation error:" + n.toString());
                    e.printStackTrace();
                    return n;
                }
            }
            else{ //non return others
                return n;                
            }
        }
    }
    
    /**
     * Used to query the method's return field from any frames
     * 
     * @return
     */
    protected Name queryMethodResultFieldName() {
        // start from this
        AbstractWSClassGen classGen = this;
        while (classGen != null) {
            if (classGen instanceof WSMethodFrameClassGen) {
                return ((WSMethodFrameClassGen) classGen).getReturnFieldName();
            }
            classGen = (AbstractWSClassGen) classGen.getUpFrame();
        }
        return null; // Not found, should throw exception
    }

    /**
     * Used to query the method's return flag from any frames
     * 
     * @return
     */
    protected Name queryMethodReturnFlagName() {
        // start from this
        AbstractWSClassGen classGen = this;
        while (classGen != null) {
            if (classGen instanceof WSMethodFrameClassGen) {
                return ((WSMethodFrameClassGen) classGen).getReturnFlagName();
            }
            classGen = (AbstractWSClassGen) classGen.getUpFrame();
        }
        return null; // Not found, should throw exception
    }
    
    /**
     * Change local declare with initializer e.g. "var n:Int = 10" as only local initialize "n = 10"
     * And create an inner class fields "n";
     * Here, the assign right local access will is still local access, not replaced by field access
     * And then the stmt need to be processed by other transformations,
     * e.g. replace local access with field access.
     * @param ld
     * @return null if just a decl or stmt of the intializor
     */
    protected Stmt transLocalDecl(LocalDecl ld){
        //Create fields
        if (((X10LocalDef) ld.localDef()).annotationsMatching(wts.transientType).isEmpty()) {
            //The local is not annotated as "@Transient", and should be transformed as field
            Name fieldName = ld.name().id();
            FieldSynth localFieldSynth = classSynth.createField(ld.position(), fieldName.toString(), ld.type().type());
            localFieldSynth.addAnnotation(genUninitializedAnnotation()); 
            fieldNames.add(fieldName); //put it into current frame's fields name lists
            
            //and check the intializor
            Expr localInit = ld.init();
            if(localInit == null){
                return null; //just a pure declare, no initialization;
            }
            
            //now create a local ref to this localDecl
            Local local = xnf.Local(ld.position(), xnf.Id(ld.position(), ld.name().id())).localInstance(ld.localDef().asInstance());
            Expr assign = xnf.LocalAssign(localInit.position(), local, Assign.ASSIGN, localInit).type(localInit.type());
            return xnf.Eval(ld.position(), assign);      
        } else {
            //the local is annoated as "@Transient", no need transformation
            return ld;
        }
    }
    
    /**
     * This one is direct call and assign call transformation
     * Transform a call to fast/resume/back path 
     * This callee is a concurrent method
     */
    protected TransCodes transCall(Call call, int prePcValue, Set<Name> declaredLocals) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1); //increase the pc value;

        //pc = x;
        try{
            //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
            Expr pcAssgn = synth.makeFieldAssign(compilerPos, getThisRef(), PC,
                                  synth.intValueExpr(transCodes.getPcValue(), compilerPos), xct).type(xts.Int());
            transCodes.addFirst(xnf.Eval(compilerPos, pcAssgn));
            transCodes.addSecond(xnf.Eval(compilerPos, pcAssgn));   
        }
        catch(polyglot.types.NoMemberException e){
            //Just ignore the pc assign statement if there is no pc field in the frame
        }
        
        //replace local access with field access
        //FIXME: async frame's local decl specific processing
        Call aCall = (Call) this.replaceLocalVarRefWithFieldAccess(call, declaredLocals); 
        
        //find out the wrapper method
        MethodDef methodDef = aCall.methodInstance().def();
//        MethodSynth fastSlowMethodPair = wts.getFastAndSlowMethod(methodDef);
        
        X10MethodDef mDef = WSCodeGenUtility.createWSCallMethodDef(methodDef, wts);

        //preparing the references for invocation the call
        Expr parentRef = genUpcastCall(getClassType(), wts.frameType, getThisRef());
        Expr ffRef;
        if(xts.isSubtype(getClassType(), wts.finishFrameType)){
            //if it self is ff type, upcast it self
            ffRef = genUpcastCall(getClassType(), wts.finishFrameType, getThisRef());
        }
        else if(xts.isSubtype(getClassType(), wts.asyncFrameType)){
            //if it self is ff type, upcast it self
            Expr upRef = synth.makeFieldAccess(compilerPos, getThisRef(), UP, xct);
            ffRef = genCastCall(wts.frameType, wts.finishFrameType, upRef);
        }
        else{
            //non finish frame will have ff field as ff 
            ffRef = synth.makeFieldAccess(compilerPos, getThisRef(), FF, xct);
        }
            
        { // fast
            ArrayList<Expr> newArgs = new ArrayList<Expr>();
            Expr fastWorkerRef = fastMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
            newArgs.add(fastWorkerRef);
            newArgs.add(parentRef);
            newArgs.add(ffRef);
            
            X10Call fastMCall = WSCodeGenUtility.replaceMethodCallWithWSMethodCall(xnf,(X10Call) aCall, mDef, newArgs);
            transCodes.addFirst(xnf.Eval(aCall.position(), fastMCall));
        }
        { // resume
            ArrayList<Expr> newArgs = new ArrayList<Expr>();
            Expr resumeWorkerRef = resumeMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
            newArgs.add(resumeWorkerRef);
            newArgs.add(parentRef);
            newArgs.add(ffRef);
            
            X10Call slowMCall = WSCodeGenUtility.replaceMethodCallWithWSMethodCall(xnf, (X10Call) aCall, mDef, newArgs);
            transCodes.addSecond(xnf.Eval(aCall.position(), slowMCall));
        }
        { // back              
            //nothing in back path;
        }
        return transCodes;
    }
    
    /**
     * Transform aVar = foo(...) style. The foo() is a target method
     */
    protected TransCodes transAssignCall(Eval eval, int prePcValue, Set<Name> declaredLocals) throws SemanticException {

        Assign aAssign = (Assign)eval.expr();
        Call call = (Call) aAssign.right();
        aAssign = (Assign) this.replaceLocalVarRefWithFieldAccess(aAssign, declaredLocals);

        // trigger trans call
        TransCodes callTransCodes = transCall(call, prePcValue, declaredLocals);
        TransCodes transCodes = new TransCodes(callTransCodes.getPcValue());
        
        //If the pc_field optimization is turned on, only return 1 statement (call)
        //other wise, two stmts, 1st, the pc assign; 2nd, the call
        int codesSize = callTransCodes.first().size(); 
        if(codesSize == 2){
            //add pc change statement
            transCodes.addFirst(callTransCodes.first().get(0));
            transCodes.addSecond(callTransCodes.second().get(0));
        }
        //the call is the last stmt
        Call fastCall = (Call) ((Eval) callTransCodes.first().get(codesSize - 1)).expr();
        Call slowCall = (Call) ((Eval) callTransCodes.second().get(codesSize - 1)).expr();
        transCodes.addFirst(xnf.Eval(compilerPos, aAssign.right(fastCall)));
        transCodes.addSecond(xnf.Eval(compilerPos, aAssign.right(slowCall)));
        
        // back path
        // construct move content statements
        // _m_fib.t1 = cast[Frame,()=>Int](frame)();
        Expr backFrameRef = backMSynth.getMethodBodySynth(compilerPos).getLocal(FRAME.toString());
        Expr castExp = genCastCall(wts.frameType, 
                                   synth.simpleFunctionType(slowCall.type(), compilerPos),
                                   backFrameRef);

//        //now prepare the closure
//        Block body = xnf.Block(compilerPos, xnf.Return(compilerPos, castExp));
//        System.out.println(xct.currentCode()+"-->currentNod");
//        Closure c = synth.makeClosure(compilerPos, slowCall.type(), Collections.EMPTY_LIST, body, xct);
//        X10MethodInstance ci = c.closureDef().asType().applyMethod();
//        ClosureCall functionCall = (ClosureCall) xnf.ClosureCall(compilerPos, c, Collections.EMPTY_LIST).closureInstance(ci).type(slowCall.type());
//        
//        System.out.println("-----------" + functionCall.closureInstance());
//        functionCall.dump(System.out);
//        functionCall.dump(System.out);

        //FIXME: it should be a closure call. However, an instance call here is simpler.
        InstanceCallSynth niSynth = new InstanceCallSynth(xnf, xct, compilerPos, castExp, OPERATOR.toString());
        
        
        
        aAssign = aAssign.right(niSynth.genExpr());

        transCodes.addThird(callTransCodes.third()); //in fact nothing
        transCodes.addThird(xnf.Eval(compilerPos, aAssign));

        return transCodes;
    }
    
    
    /**
     * Flatten compound statements by ExpressionFlattener
     */
    protected TransCodes transCompoundStmt(Stmt s){
        TransCodes transCodes = new TransCodes();
        ExpressionFlattener ef = new ExpressionFlattener(job, xts, xnf);
        ef.begin();
        Stmt sFOut = (Stmt) s.visit(ef); // the floOut could be a block or a for
        transCodes.addFlattened(sFOut);
        return transCodes;
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
    protected void genCopyConstructor(Position pos) throws SemanticException{

        ClassDef cDef = getClassDef();
        Type cType = getClassType();
        Type ccType = PlaceChecker.AddIsHereClause(cType, xct);
        Type sType = cDef.superType().get();
        Type scType = PlaceChecker.AddIsHereClause(sType, xct);

        ConstructorSynth conSynth = classSynth.createConstructor(pos);
        conSynth.setFlags(Flags.PROTECTED);
        conSynth.addFormal(compilerPos, Flags.FINAL, xts.Int(), "x"); //a dummy int formal
        Expr otherRef = conSynth.addFormal(compilerPos, Flags.FINAL, ccType, "o");
        CodeBlockSynth conStmtsSynth = conSynth.createConstructorBody(compilerPos);
        SuperCallSynth superCallSynth = conStmtsSynth.createSuperCall(compilerPos, getClassDef());
        superCallSynth.addArgument(xts.Int(), synth.intValueExpr(-1, compilerPos));
        Expr upCastOtherRef = genUpcastCall(cType, sType, otherRef);
        superCallSynth.addArgument(scType, upCastOtherRef);

        // now try to add fields copy directly
        Receiver leftReceiver = getThisRef();
        for (FieldDef df : cDef.fields()) {
            Name fieldName = df.name();
            
            //method frame not move return fields
            if(this instanceof WSMethodFrameClassGen){
                WSMethodFrameClassGen methodFrame = (WSMethodFrameClassGen)this;
                if(methodFrame.getReturnFlagName().toString().equals(fieldName.toString())
                        || (methodFrame.getReturnFieldName() != null
                            && methodFrame.getReturnFieldName().toString().equals(fieldName.toString())
                            )
                      ){
                    continue; //not copy return and return flag
                }
            }
            
            Stmt s = xnf.Eval(compilerPos, synth.makeFieldToFieldAssign(compilerPos, leftReceiver, fieldName, otherRef,
                                                                        fieldName, xct));
            conStmtsSynth.addStmt(s);
        }
    }
        
    /**
     * Generate a remap method: "def remap() = new <the method type>(-1, this);"
     * @param cDecl The glass
     * @return the class with "remap" method inserted
     * @throws SemanticException
     */
    protected void genRemapMethod() throws SemanticException {
        ClassType cType = classSynth.getDef().asType();
        Type scType = PlaceChecker.AddIsHereClause(cType, xct);
        MethodSynth methodSynth = classSynth.createMethod(compilerPos, "remap");
        methodSynth.setFlag(Flags.PUBLIC);

        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, cType);
        niSynth.addArgument(xts.Int(), synth.intValueExpr(-1, compilerPos) );
        niSynth.addArgument(scType, synth.thisRef(cType, compilerPos));
                
        //upcast new instance
        Type upCastTargetType;
        if(xts.isSubtype(cType, wts.finishFrameType)){
            upCastTargetType = wts.finishFrameType;
        }
        else if(xts.isSubtype(cType, wts.regularFrameType)){
            upCastTargetType = wts.regularFrameType;
        }
        else{
            upCastTargetType = wts.frameType;
        }
        methodSynth.setReturnType(upCastTargetType);
        Expr castReturn = genUpcastCall(cType, upCastTargetType, niSynth.genExpr());
        
        Stmt ret = xnf.Return(compilerPos, castReturn);
        CodeBlockSynth blockSynth = methodSynth.getMethodBodySynth(compilerPos);
        blockSynth.addStmt(ret);

    }
    
    /**
     * Generate an annotation node of type annotationType
     * WS will use the following types: Inline, StackAllocate, Uninitialized, Header
     * 
     * @param annotationType
     * @return
     */
    AnnotationNode genAnnotationNode(Type annotationType){
        TypeNode annoTN = xnf.CanonicalTypeNode(compilerPos, annotationType).typeRef(Types.ref(annotationType));
        return xnf.AnnotationNode(compilerPos, annoTN);
    }
    
    AnnotationNode genStackAllocateAnnotation(){
        return genAnnotationNode(wts.stackAllocateType);
    }

    AnnotationNode genHeaderAnnotation(){
        return genAnnotationNode(wts.headerType);
    }
    
    AnnotationNode genInlineAnnotation(){
        return genAnnotationNode(wts.inlineType);
    }
    
    AnnotationNode genUninitializedAnnotation(){
        return genAnnotationNode(wts.uninitializedType);
    }
    
//    /**
//     * Generate @StackAllocate annotation for a local variable declaration
//     * @return the annotation
//     */
//    List<AnnotationNode> genStackAllocateLeftAnnotations(){
//     // StackAllocate: left hand annotation for local decl
//        /* According to Olivier, we could be able to use only one kind of annotation for both
//         * left hand (for stmt) and right hand (for expr) node. I'm stilling generating two 
//         * kinds of annotations, conforming to the compiler generated AST. 
//         *  
//         */
//        TypeNode annoTN = xnf.CanonicalTypeNode(compilerPos, wts.stackAllocateType).typeRef(Types.ref(wts.stackAllocateType));
//        AnnotationNode leftAnnoNode = xnf.AnnotationNode(compilerPos, annoTN);
//        return Collections.<AnnotationNode>singletonList(leftAnnoNode);
//    }
//    
//    /**
//     * Generate @StackAllocate annotation for a new statement
//     * @return annotation
//     */
//    List<AnnotationNode> genStackAllocateRightAnnotations(){
//        // StackAllocate: right hand annotation for new
//        AmbTypeNode ambTN = xnf.AmbTypeNode(compilerPos, xnf.Id(compilerPos, Name.make("StackAllocate")));
//        ambTN = (AmbTypeNode) ambTN.typeRef(Types.ref(wts.stackAllocateType));
//        AnnotationNode anNode = xnf.AnnotationNode(compilerPos, ambTN).annotationType(ambTN);
//        return Collections.<AnnotationNode>singletonList(anNode);
//    }
    
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
     * Generate cast[type1, type2](expr)
     * @param type1
     * @param type2
     * @param expr
     * @return cast[type1, type2](expr)
     * @throws SemanticException
     */
    Expr genCastCall(Type type1, Type type2, Expr expr) throws SemanticException{
        return genCastCall("cast", type1, type2, expr);
    }
    
    Expr genCastCall(String name, Type type1, Type type2, Expr expr) throws SemanticException{
        List<TypeNode> genericTN = new ArrayList<TypeNode>();
        genericTN.add(xnf.CanonicalTypeNode(compilerPos, Types.ref(type1)));
        genericTN.add(xnf.CanonicalTypeNode(compilerPos, Types.ref(type2)));
        
        Call aCall = synth.makeStaticCall(compilerPos, 
                                          wts.frameType,
                                          Name.make(name),
                                          genericTN, // This is for generic type, if any 
                                          Collections.singletonList(expr),
                                          PlaceChecker.AddIsHereClause(type2, xct),
                                          Collections.singletonList(type1),
                                          xct);
        
        return aCall;
    }
    
    
//    /**
//     * For a method gen, originally, there are local variables access
//     * However, all these local variables are stored as method body class's fields
//     * We need replace local as field ref
//     * E.g. n ==> instanceRef.n
//     * @param s the statement
//     * @param instanceRef reference to the class's instance
//     * @return
//     */
//    protected Term replaceLocalVarRefWithFieldAccess(Term s, Expr instanceRef){
//        LocalAccessToFieldAccessReplacer rep = new LocalAccessToFieldAccessReplacer(instanceRef, synth, context);
//        s = (Term) s.visit(rep);
//        return s;
//    }
    
    /**
     * Replace local var access with field access.
     * @param s the statement
     * @return
     */
    protected Term replaceLocalVarRefWithFieldAccess(Term s){
        return replaceLocalVarRefWithFieldAccess(s, CollectionFactory.<Name>newHashSet());
    }
    
    /**
     * Replace local var access with field access.
     * @param s the statement
     * @param declaredLocals: an ignore list. All these vars have been declared in the scope
     * @return
     */
    protected Term replaceLocalVarRefWithFieldAccess(Term s, Set<Name> declaredLocals){
        
        AdvLocalAccessToFieldAccessReplacer rep = new AdvLocalAccessToFieldAccessReplacer(this, synth, xct, declaredLocals);
        Term newS = (Term) s.visit(rep);
        
        if(rep.isReplaceError()){
            System.err.println("[WS_WARNING] ReplaceLocalVarRefWithFieldAccess:");
            s.prettyPrint(System.err);
            Exception e = new Exception();
            e.printStackTrace(System.err);
            System.err.println();
        }
        
        return newS;
    }
    
    /**
     * Check whether the current frame contains the input field's name
     * @param fieldName
     * @return
     */
    public boolean containField(Name fieldName){
        return fieldNames.contains(fieldName);
    }
    

//    /**
//     * For any method generate, need provide a reference to methodBodyInnerClass
//     * So that any other inner class could reference to that instance, and access fields
//     * 
//     * @return
//     * @throws SemanticException 
//     */
//    protected Pair<LocalDecl, Local> genMethodBodyClassRefAsLocalVariable() throws SemanticException{
//        //It will use innerClass as start
//        AbstractWSClassGen selfClass = this;
//        AbstractWSClassGen parentClass = selfClass.getParent();
//        X10ClassDef parentDef;
//        X10ClassDef selfDef; 
//        Expr selfRef = xnf.This(compilerPos);
//        Name upName = Name.make("up");
//        while(parentClass != null){
//            parentDef =  parentClass.getClassDef();
//            selfDef = selfClass.getClassDef();
//            selfRef = selfRef.type(selfDef.asType());            
//            //need cast current's up to parent's reference
//            Expr upField = synth.makeFieldAccess(compilerPos, selfRef, upName, xct);
//            selfRef = genCastCall(wts.frameType, parentDef.asType(), upField);
//            selfClass = parentClass;
//            parentClass = selfClass.getParent(); //here it is possible parent == null;
//        }
//        //now  selfRef should point to the method's frame
//        return synth.makeLocalVarWithAnnotation(compilerPos, Flags.NONE, selfRef, null, xct);
//
//    }    
    
    /**
     * Change local declare with initializer e.g. "var n:Int = 10" as only local initialize "n = 10"
     * Here, the assign right local access will is still local access, not replaced by field access
     * And then the stmt need to be processed by other transformations,
     * e.g. replace local access with field access.
     * @param ld local declare
     * @return null if it is a pure local declare, or a local assign
     */
    public Stmt removeLocalDeclare(LocalDecl ld){
        Expr localInit = ld.init();
        if(localInit == null){
            return null; //just a pure declare, no initialization;
        }
        
        //now create a local ref to this localDecl
        Local local = xnf.Local(ld.position(), xnf.Id(ld.position(), ld.name().id())).localInstance(ld.localDef().asInstance());
        Expr assign = xnf.LocalAssign(localInit.position(), local, Assign.ASSIGN, localInit).type(localInit.type());
        return xnf.Eval(ld.position(), assign);
    }
    
    
    
    /**
     * 
     * Replace original call, e.g. fib(n) with generated WS call
     *  --> fib_fast(worker, this, this, 1, n);
     * The newArgs are worker/this/this/1
     * @param aCall
     * @param methodDef
     * @param newArgTypes
     * @param newArgs
     * @return
     */
    public X10Call replaceMethodCallWithWSMethodCall(X10Call aCall, X10MethodDef methodDef, 
                                                  List<Expr> newArgs){
    	
        //for arguments & new method instance's formal types
        ArrayList<Expr> args = new ArrayList<Expr>(newArgs);
        args.addAll(aCall.arguments());
        ArrayList<Type> argTypes = new ArrayList<Type>();
        for(Expr e : newArgs){
        	argTypes.add(e.type());
        }
        argTypes.addAll(aCall.methodInstance().formalTypes());
        
        //for the name
        Name name = methodDef.name(); //new name
        
        //new method instance with original properties
        MethodInstance mi = methodDef.asInstance();
        mi = (MethodInstance) mi.formalTypes(argTypes);
        mi = mi.returnType(aCall.methodInstance().returnType());
        mi =  (MethodInstance) mi.container(aCall.methodInstance().container());
        
        //build new call
        aCall = (X10Call) aCall.methodInstance(mi);
        aCall = (X10Call) aCall.name(xnf.Id(aCall.name().position(), name));
        aCall = (X10Call) aCall.arguments(args);
        aCall.type(methodDef.returnType().get());
        return aCall;
    }

    
    /**
     * Looking for an async frame's finish frame as parent frame
     * If it found an finish frame, the parent frame is a finish frame.
     * If the async has no direct finish frame, only null
     * Since an async frame is a finish frame look for both WSFinishStmtClassGen and WSAsyncClassGen
     * @param frameClass the start location to look for
     * @return AbstractWSClassGen from the chain of frameClass or null
     */
    static protected AbstractWSClassGen getFinishFrameOfAsyncFrame(AbstractWSClassGen directParentFrame){
        AbstractWSClassGen parentFrame = directParentFrame;
        
        
        while(parentFrame != null){
            if(parentFrame instanceof WSFinishStmtClassGen){
                return parentFrame;
            }
            parentFrame = parentFrame.getUpFrame();
        }
        return null; //not found, return null;
    }
    
    
    //------------ Below codes are used to implement ILocalToFieldContainerMap
    
    //Map relationship
    // (1..n)field --> (1)frame --> (1)frameRef
    // (1)frameRef will has only one local decl
    protected Map<Name, AbstractWSClassGen> localToFieldFrameMap;
    protected Map<AbstractWSClassGen, Expr> fieldFrameToRefMap;
    protected Map<Expr, Stmt> refToDeclMap ;
    
        
    /**
     * Return the whole local expr to local decl map
     * @return
     */
    public Map<Expr, Stmt> getRefToDeclMap(){
        return refToDeclMap;
    }

    //search the fields from a start classFrame, and store it into a path structure
    //a path structure is a arraylist
    //[[fieldName, classType], [fieldName, classType] , ...]
    protected void lookForField(Name fieldName, ReferenceContainer refContainer, AbstractWSClassGen classFrame){
        
        if(classFrame.containField(fieldName)){
            refContainer.setFound(); //current is a found one;
        }
        //now still need do full search
        //typical frames have only up field to point to its' parent
        //but async frames have up to point its finish, and k points to its continuation
        //look for up field first, then continuation
        
        AbstractWSClassGen parentClassFrame = classFrame.getUpFrame();
        if(parentClassFrame != null){ //it's possible the parent is null, for an async has no direct finish, and remote main frame
            refContainer.push(UP.toString(), parentClassFrame);
            lookForField(fieldName, refContainer, parentClassFrame);
            refContainer.pop();
        }
        
        //async frame type could search continuation frame to
        if(classFrame instanceof WSAsyncClassGen){
            WSAsyncClassGen asyncFrame = (WSAsyncClassGen)classFrame;
            parentClassFrame = asyncFrame.parentK;
            
            if(parentClassFrame != null){
                
                refContainer.push("k", parentClassFrame);
                lookForField(fieldName, refContainer, parentClassFrame);
                refContainer.pop();
            }
        }
        
        //remote frame type should search it's remote parent frame
        if(classFrame instanceof WSRemoteMainFrameClassGen){
            WSRemoteMainFrameClassGen remoteFrame = (WSRemoteMainFrameClassGen)classFrame;
            parentClassFrame = remoteFrame.parentR;
            if(parentClassFrame != null){
                refContainer.push("r", parentClassFrame);
                lookForField(fieldName, refContainer, parentClassFrame);
                refContainer.pop();
            }
        }
        
    }
    
    /**
     * Analyze an eval, if it is a local assign,
     * the method will check whether the target local is an out finish scope local assign
     * If it is, WS code gen will look for all the upper async frames to move the local var
     * 
     * @param e
     */
    protected void localAssignEscapeProcess(Eval e){
        LocalAssign localAssign = WSCodeGenUtility.identifyLocalAssign(e);
        if(localAssign == null){
            return; //it is not a local assign, just ignore it
        }
        
        Name localName = localAssign.local().name().id();
        
        ReferenceContainer refContainer = new ReferenceContainer(this);
        lookForField(localName, refContainer, this); //start from here
        List<Pair<String, Type>> refStructure = refContainer.getBestRefStructure(); 
        
        if(refStructure == null){
            System.err.println("[WS_ERR] Cannot find reference to local variable name:" + localName.toString());
            return; //should not happen
        }
        
        //now check whether this one is outfinish scope
        ClassType finishFrameType = null;
        for(Pair<String, Type> p : refStructure){
            ClassType cType = (ClassType) p.snd();
            if(cType.superClass() == wts.finishFrameType){
                //the local is out of the finish scope because finish frame doesn't contain locals
                finishFrameType = cType; //not break, and find out the last finish
            }
        }
        
        if(finishFrameType == null){
            //System.out.print("[WS_INFO] \"");
            //localAssign.prettyPrint(System.out);
            //System.out.println("\" is out-finish scope assign. But it is not in an async frame, and no move");
        }
        else{ 
            //an out finish assign, need find all async frames between the current frame and the finish frame
            //and add the move statements
            List<WSAsyncClassGen> asyncFrames = new ArrayList<WSAsyncClassGen>();
            AbstractWSClassGen curFrame = this;
            while(curFrame != null && curFrame.getClassType() != finishFrameType){
                if(curFrame instanceof WSAsyncClassGen){
                    ((WSAsyncClassGen)curFrame).addOutFinishScopeLocals(localAssign);
                    asyncFrames.add((WSAsyncClassGen) curFrame);
                    curFrame = ((WSAsyncClassGen)curFrame).parentK;
                }
                else{
                    curFrame = curFrame.up;
                }
            }
            
            //below just for debug info
            if(asyncFrames.size() > 0){
                StringBuffer sb = new StringBuffer();
                for(WSAsyncClassGen aFrame : asyncFrames){
                    sb.append(aFrame.getClassName()).append(", ");
                }
                
                System.out.print("[WS_INFO] \"");
                localAssign.prettyPrint(System.out);
                System.out.println("\" is an out-finish scope assign expr, and will be moved into " + sb.toString());                
            }
        }
    }
    
    
    
    /**
     * A field (fieldName) may be in any upper frame of current frame
     * Search a field from a start location (startRef, startContainer),
     * and generate a reference to the frame that contains the field
     * 
     * startRef is a FinishFrame
     * 
     * Used in move method of async frame
     * 
     * @param fieldName
     * @param startContainer
     * @param startRef, should be a finish frame type
     * @return
     * @throws SemanticException 
     */
    public Expr getFieldContainerRef(Name fieldName, AbstractWSClassGen startFrame, Expr startRef) throws SemanticException{
        ReferenceContainer refContainer = new ReferenceContainer(startFrame);
        lookForField(fieldName, refContainer, startFrame); //start from here
        List<Pair<String, Type>> refStructure = refContainer.getBestRefStructure();

        if(refStructure != null){
            //first generate the cast [FinishFrame, startFrame.type]startRef
            Type castedType = startFrame.getClassDef().asType();
            Expr castedRef = genCastCall(wts.finishFrameType, castedType, startRef); //cast[Frame, _finish](up)
            //now gothourgh the refStructure
            AbstractWSClassGen classFrame = startFrame;
            for(Pair<String, Type> p : refStructure){
                assert(p.fst().equals(UP.toString()));
                classFrame = classFrame.getUpFrame();
                Expr upField = synth.makeFieldAccess(compilerPos, castedRef, Name.make(p.fst()), xct);
                castedRef = genCastCall(wts.frameType, p.snd(), upField);   
            }
            return castedRef;
        }
        
        System.err.println("[WS_ERR] Cannot find reference to local variable name:" + fieldName.toString());
        return null;
    }
    
    
    /* Gen field container ref. All ref is a local var. 
     * If it is new, create the local
     * @see x10.compiler.ws.util.ILocalToFieldContainerMap#getFieldContainerRef(polyglot.types.Name)
     */
    public Expr getFieldContainerRef(Name fieldName, Type type) throws SemanticException{
        if(localToFieldFrameMap == null){ //first time initialize
            localToFieldFrameMap = CollectionFactory.newHashMap();
            fieldFrameToRefMap = CollectionFactory.newHashMap();
            refToDeclMap = CollectionFactory.newHashMap();
        }
        AbstractWSClassGen frame = localToFieldFrameMap.get(fieldName);     //use cache    
        if(frame != null){ //found it in cache, no need build the reference again
            return fieldFrameToRefMap.get(frame);
        }
        else {
            ReferenceContainer refContainer = new ReferenceContainer(this);
            lookForField(fieldName, refContainer, this); //start from here
            //here the refStructure should be both the shortest, but also has no "k/r"
            //So we use some other logical to process the refStructure, remove "k/r" and create local in async or remote frames 
            List<Pair<String, Type>> refStructure = refContainer.getBestRefStructure(); 
            refStructure = processRefStructureForAsyncAndRemoteFrame(refStructure, fieldName, type);
            
            Expr ref = xnf.This(compilerPos).type(this.getClassDef().asType());
            if(refStructure != null){ //found the ref
                if(refStructure.size() == 0){ //found in current frame
                    localToFieldFrameMap.put(fieldName, this);
                    fieldFrameToRefMap.put(this, ref);
                    return ref; //just this
                }
                else{
                    AbstractWSClassGen classFrame = this;
                    
                    for(Pair<String, Type> p : refStructure){
                        assert(p.fst().equals(UP.toString()));
                        classFrame = classFrame.getUpFrame();
                        Expr upField = synth.makeFieldAccess(compilerPos, ref, Name.make(p.fst()), xct);
                        ref = genCastCall(wts.frameType, p.snd(), upField);
                    }
                    
                    localToFieldFrameMap.put(fieldName, classFrame);
                    
                    //now have found the classFrame and the ref
                    if(fieldFrameToRefMap.containsKey(classFrame)){
                        return fieldFrameToRefMap.get(classFrame);
                    }
                    else{
                        //need create the new local
                        Pair<LocalDecl, Local> tmpPair = synth.makeLocalVarWithAnnotation(compilerPos, Flags.FINAL, ref, null, xct);
                        ref = tmpPair.snd();
                        refToDeclMap.put(ref, tmpPair.fst());
                        fieldFrameToRefMap.put(classFrame, ref);
                        return ref;
                    }
                }
            }

        }
        System.err.println("[WS_ERR] Cannot find reference to local variable name:" + fieldName.toString());
        return null;
    }  
    
    
    //Async frame's "up" field point to the finish frame
    //And the local variables between finish frame and itself should be copied into the async frame as formals
    //Remote frame has no up, all ref structure contains up should be copied into the remote frame as formals
    //The return structure will only contains "up". No "k" or "r"
    List<Pair<String, Type>> processRefStructureForAsyncAndRemoteFrame(List<Pair<String, Type>> refStructure, Name name, Type type){
        
        if(refStructure == null || refStructure.size() == 0){
            return refStructure; // no need process
        }
        
        ArrayList<Pair<String, Type>> result = new ArrayList<Pair<String, Type>>();
       
        //identify the field container frame according to the refstructure
        boolean foundKRInRef = false; //if found, no need additional search
        AbstractWSClassGen classFrame = this;
        for(Pair<String, Type> pair : refStructure){
            if(pair.fst().equals(UP.toString())){
                classFrame = classFrame.up;
            }
            else{ //"from parentK" or from "parentR"
                foundKRInRef = true;
                
                if(pair.fst().equals("k")){
                    WSAsyncClassGen asyncFrame = (WSAsyncClassGen)classFrame;
                    classFrame = asyncFrame.parentK;
                    //and we need create the fields in the async frame
                    asyncFrame.addFormal(name, type);
                }
                else{ //"r"
                    WSRemoteMainFrameClassGen remoteFrame = (WSRemoteMainFrameClassGen)classFrame;
                    classFrame = remoteFrame.parentR;
                    //and we need create the fields in the async frame
                    remoteFrame.addFormal(name, type);
                }
                //cannot break yet, still do more search
            }
            if(!foundKRInRef){ //as soon as found "k/r", we stop copy the ref to result.
                result.add(pair);                    
            }
        }        
        return result;
    }
    
    
    //=============================== This Section is used to control optimizaiton config
    //Sub class gen should override the method to control the opimization parameter.
    public boolean isFastPathInline(ClassType frameType){
        return true; //default true;
    }
    
}
