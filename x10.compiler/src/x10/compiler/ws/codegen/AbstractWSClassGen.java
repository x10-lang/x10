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
import polyglot.ast.Catch;
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
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Closure;
import x10.ast.Finish;
import x10.ast.Offer;
import x10.ast.When;
import x10.ast.X10Call;
import x10.ast.X10ClassDecl;
import x10.extension.X10Ext_c;
import x10.compiler.ws.WSCodeGenerator;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.AdvLocalAccessToFieldAccessReplacer;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.CodePatternDetector;
import x10.compiler.ws.util.ILocalToFieldContainerMap;
import x10.compiler.ws.util.ReferenceContainer;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSUtil;
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
 * The base of different kinds of WS class generator
 * 
 * Because one method will be divided into different segments (control flows)
 * and each segment will be transformed into one inner class,
 * these inner classes are chained together in a tree structure.
 * 
 * In order to represent the tree structure, the AbstractWSClassGen
 * is a tree node, too. It has parent and child
 *
 * @author Haichuan
 */
public abstract class AbstractWSClassGen implements ILocalToFieldContainerMap {   

    static final protected Position compilerPos = Position.COMPILER_GENERATED;
    
    final protected Job job;
    final protected NodeFactory xnf;
    final protected TypeSystem xts;
    final protected Context xct;
    final protected WSTransformState wts;
    final protected Synthesizer synth; //stateless synthesizer
    final protected WSSynthesizer wsynth; //synthesizer for work-stealing

    final protected Block codeBlock; // store all code block

    final protected Set<Name> fieldNames; //store names of all fields in current frame
    final protected String className; //used for child to query, and form child's name
    final protected ClassSynth classSynth; //stateful synthesizer for class gen
    final protected int frameDepth; //the depth to parent;

    final protected MethodSynth fastMSynth;
    final protected MethodSynth resumeMSynth;
    final protected MethodSynth backMSynth;
    //Fields to maintain the tree
    final private AbstractWSClassGen up;
    private List<AbstractWSClassGen> children; //lazy initialization


    private AbstractWSClassGen(Job job, Context xct, WSTransformState wts, AbstractWSClassGen up,
            String className, ClassType frameType, int frameDepth, Flags flags, X10ClassDef outer, Stmt stmt) {
        this.job = job;
        xnf = (NodeFactory) job.extensionInfo().nodeFactory();
        xts = (TypeSystem) job.extensionInfo().typeSystem();
        synth = new Synthesizer(xnf, xts);
        wsynth = new WSSynthesizer(xnf, xts);
        this.xct = xct;
        this.wts = wts;
        this.up = up;

        fieldNames = CollectionFactory.newHashSet(); //used to store all other fields' names    
        this.frameDepth = frameDepth;
        
        this.codeBlock = stmt == null ? null : synth.toBlock(stmt); // switch statement has null codeBlock
        this.className = className;
        classSynth = wsynth.createClassSynth(job, xct, frameType, outer, flags, className);
        fastMSynth = wsynth.createFastMethodSynth(classSynth, isFastPathInline(frameType));
        resumeMSynth = wsynth.createResumeMethodSynth(classSynth);
        backMSynth = wsynth.createBackMethodSynth(classSynth);
    }

    // method frames
    protected AbstractWSClassGen(Job job, NodeFactory xnf, Context xct, WSTransformState wts,
            String className, ClassType frameType, Flags flags, X10ClassDef outer, Stmt stmt) {
        this(job, xct, wts, null, className, frameType, 0, flags, outer, stmt);
    }

    // nested frames
    protected AbstractWSClassGen(AbstractWSClassGen parent, AbstractWSClassGen up,
            String classNamePrefix, ClassType frameType, Stmt stmt) {
        this(parent.job, parent.xct, parent.wts, up, classNamePrefix + parent.assignChildId(), frameType,
                parent.frameDepth + 1, parent.classSynth.getFlags(),
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
    private void addChild(AbstractWSClassGen child) {
        if(children == null){
            children = new ArrayList<AbstractWSClassGen>();
        }
        children.add(child);
    }
    
    
    int childrenNameCount; //start from 0. not the same as children's number 
                           //since finish frame has more child classFrames
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
    
    public WSFinishStmtClassGen getDirectFinishFrameClassGen(){
        AbstractWSClassGen frame = this;
        while(frame != null) {
            if(frame instanceof WSFinishStmtClassGen){
                return (WSFinishStmtClassGen)frame;
            }
            frame = frame.getUpFrame();
        }
        return null;
    }

    /**
     * Use the generator to generate all kinds of classes.
     * Need add the new inner class to its parent frame class
     * @throws SemanticException
     */
    public final void genClass() throws SemanticException {
        genMethods(); //fast/resume/back, and async frame's move methods
        methodCodeProcessing(); //process the codes
        genClassConstructor();
        if (wts.__CPP__) {
            wsynth.genCopyConstructor(classSynth);
            wsynth.genRemapMethod(classSynth, codeBlock == null);
        }
    }
    
    /**
     * Final code processing for fast/resume/back path
     */
    private void methodCodeProcessing(){

        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        
        // add change locals to fields
        fastBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(job, this.getRefToDeclMap()).context(xct));
        resumeBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(job, this.getRefToDeclMap()).context(xct));
        backBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(job, this.getRefToDeclMap()).context(xct));
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
        
        if(childSuperType == xts.FinishFrame()){
            //stmt is Finish
            childClassGen = new WSFinishStmtClassGen(this, (Finish)stmt);
        }
        else{
            //first unroll to one stmt; //remove additional no use stmt
            stmt = WSUtil.unwrapToOneStmt(stmt); 
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
                if(xts.isSubtype(getClassType(), xts.AsyncFrame())
                        || xts.isSubtype(getClassType(), xts.FinishFrame())){
                    childClassGen = new WSRegularFrameClassGen(this, synth.toBlock(stmt),
                                                               WSUtil.getBlockFrameClassName(this.getClassName()));
                }
                else{
                    Async async = (Async)stmt;
                    Stmt asyncBody = WSUtil.unwrapToOneStmt(async.body());
                    //need check the frame is pure async or async at(p)
                    if(asyncBody instanceof AtStmt){
                        //async at(p), transform it as a remote frame
                        childClassGen = new WSRemoteMainFrameClassGen(this, (AtStmt)asyncBody);
                    }
                    else{
                        //pure async
                        childClassGen = new WSAsyncClassGen(this, (Async)stmt); 
                    }
                   
                }
            }
            else if(stmt instanceof AtStmt){
                //Transform it as at remoteframe
                childClassGen = new WSRemoteMainFrameClassGen(this, (AtStmt)stmt);
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
     * Normal codes, no concurrent method/construct
     * Just replace local refs as field access
     */
    protected TransCodes transNormalStmt(Stmt s, int pcValue, Set<Name> declaredLocals) throws SemanticException {
        assert(!WSUtil.isComplexCodeNode(s, wts));

        TransCodes transCodes = new TransCodes(pcValue);
        s = (Stmt) this.replaceLocalVarRefWithFieldAccess(s, declaredLocals);
        
        //need process the return's & offer issue;
        TransOfferVisitor fastOV = new TransOfferVisitor(true);
        Stmt fStmt = (Stmt) s.visit(fastOV);
        TransOfferVisitor resumeOV = new TransOfferVisitor(false);
        Stmt rStmt = (Stmt) s.visit(resumeOV);        
        
        TransReturnVisitor fastRV = new TransReturnVisitor(true);
        transCodes.addFast((Stmt)fStmt.visit(fastRV));
        TransReturnVisitor resumeRV = new TransReturnVisitor(false);
        transCodes.addResume((Stmt)rStmt.visit(resumeRV));
        return transCodes;
    }
    
    //Process offer in normal stmts
    class TransOfferVisitor extends ErrorHandlingVisitor{
        private boolean fastPath; //fast:true or resume path:false
        Type reducerType;
        
        TransOfferVisitor(boolean fastPath){
            super(AbstractWSClassGen.this.job, xts, xnf);
            this.fastPath = fastPath;
            WSFinishStmtClassGen finishFrameGen = getDirectFinishFrameClassGen();
            if(finishFrameGen != null){
                reducerType = finishFrameGen.getReducerBaseType();                
            }
        }
        
        public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
            if(n instanceof Offer){
                Offer offer = (Offer)n;
                if(reducerType == null){
                    WSUtil.debug("Cannot find Collecting-Finish type in Finish-Expr. Use offer's type", offer);
                    reducerType = offer.expr().type();
                }
                MethodSynth mSynth = fastPath ? fastMSynth : resumeMSynth;
                return wsynth.genOfferCallStmt(classSynth, mSynth, offer.expr(), reducerType);
            }
            return n;   
        }
    }
    
    
    //Process return in normal stmts
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

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {

            if(n instanceof Closure || n instanceof ClassBody){
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
                        Name resultName = WSSynthesizer.RETURN_VALUE;
                        Name returnFlagName = WSSynthesizer.RETURN_FLAG;
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
                    try {
                        e.printStackTrace();
                        WSUtil.err("Return transformation error:", n);
                    } catch (SemanticException e1) {
                    }
                    return n;
                }
            }
            else{ //non return others
                return n;                
            }
        }
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
        if (((X10LocalDef) ld.localDef()).annotationsMatching(xts.Ephemeral()).isEmpty()) {
            //The local is not annotated as "@Transient", and should be transformed as field
            Name fieldName = wsynth.createFieldFromLocal(classSynth, ld) ;
            fieldNames.add(fieldName); //put it into current frame's fields name lists
            
            //and check the intializor
            Expr localInit = ld.init();
            if(localInit == null){
                return null; //just a pure declare, no initialization;
            }
            //now create a tmp local ref to this localDecl
            Local local = xnf.Local(ld.position(), xnf.Id(ld.position(), ld.name().id())).localInstance(ld.localDef().asInstance());
            Expr assign = xnf.LocalAssign(localInit.position(), local, Assign.ASSIGN, localInit).type(localInit.type());
            return xnf.Eval(ld.position(), assign);      
        } else {
            //the local is annotated as "@Transient", no need transformation
            return ld;
        }
    }
    
    /**
     * This one is direct call and assign call transformation
     * Transform a call to fast/resume/back path 
     * This callee is a concurrent method
     */
    protected TransCodes transCall(Call call, int prePcValue, Set<Name> declaredLocals) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue); //increase the pc value;

        //pc = x;
        try{
            //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
            Stmt pcAssign = wsynth.genPCAssign(classSynth, prePcValue + 1); 
            transCodes.addFast(pcAssign);
            transCodes.addResume(pcAssign);   
        }
        catch(polyglot.types.NoMemberException e){
            //Just ignore the pc assign statement if there is no pc field in the frame
        }
        
        //replace local access with field access
        //FIXME: async frame's local decl specific processing
        Call aCall = (Call) this.replaceLocalVarRefWithFieldAccess(call, declaredLocals); 
        
        Stmt fastCall = wsynth.genWSCallStmt(aCall, classSynth, fastMSynth);
        Stmt resumeCall = wsynth.genWSCallStmt(aCall, classSynth, resumeMSynth);        
        transCodes.addFast(fastCall);
        transCodes.addResume(resumeCall);
        transCodes.increasePC();
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
        TransCodes transCodes = new TransCodes(prePcValue);
        
        //Extract the call evaluation 
        //If the pc_field optimization is turned on, only return 1 statement (call)
        //other wise, two stmts, 1st, the pc assign; 2nd, the call
        int codesSize = callTransCodes.getFastStmts().size(); 
        if(codesSize == 2){
            //add pc change statement
            transCodes.addFast(callTransCodes.getFastStmts().get(0));
            transCodes.addResume(callTransCodes.getResumeStmts().get(0));
        }
        //the call is the last stmt
        Call fastCall = (Call) ((Eval) callTransCodes.getFastStmts().get(codesSize - 1)).expr();
        Call slowCall = (Call) ((Eval) callTransCodes.getResumeStmts().get(codesSize - 1)).expr();
        transCodes.addFast(xnf.Eval(compilerPos, aAssign.right(fastCall)));
        transCodes.addResume(xnf.Eval(compilerPos, aAssign.right(slowCall)));
        transCodes.increasePC();
        // back path
        // construct move content statements
        // _m_fib.t1 = cast[Frame,()=>Int](frame)();
        Stmt backAssign = wsynth.genBackAssignStmt(backMSynth, slowCall.type(), aAssign);
        transCodes.addBack(backAssign);
        return transCodes;
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
                                          xts.Frame(),
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
//            selfRef = genCastCall(xts.Frame(), parentDef.asType(), upField);
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
    
    
    //================Below codes are used to implement ILocalToFieldContainerMap ===========
    
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
            refContainer.push(UP_REF, parentClassFrame);
            lookForField(fieldName, refContainer, parentClassFrame);
            refContainer.pop();
        }
        
        //async frame type could search continuation frame to
        if(classFrame instanceof WSAsyncClassGen){
            WSAsyncClassGen asyncFrame = (WSAsyncClassGen)classFrame;
            parentClassFrame = asyncFrame.parentK;
            assert(parentClassFrame != null);
            refContainer.push(K_REF, parentClassFrame);
            lookForField(fieldName, refContainer, parentClassFrame);
            refContainer.pop();
        }
        
        //remote frame type should search it's remote parent frame
        if(classFrame instanceof WSRemoteMainFrameClassGen){
            WSRemoteMainFrameClassGen remoteFrame = (WSRemoteMainFrameClassGen)classFrame;
            parentClassFrame = remoteFrame.parentR;
            assert(parentClassFrame != null);
            refContainer.push(R_REF, parentClassFrame);
            lookForField(fieldName, refContainer, parentClassFrame);
            refContainer.pop();
        }
    }
    
    /**
     * Analyze an eval, if it is a local assign,
     * the method will check whether the target local is an out finish scope local assign
     * If it is, WS code gen will look for all the upper async frames to move the local var
     * @param e
     * @throws SemanticException 
     */
    protected void localAssignEscapeProcess(Eval e) throws SemanticException{
        LocalAssign localAssign = WSUtil.identifyLocalAssign(e);
        if(localAssign == null){
            return; //it is not a local assign, just ignore it
        }
        
        Name localName = localAssign.local().name().id();
        
        ReferenceContainer refContainer = new ReferenceContainer(this);
        lookForField(localName, refContainer, this); //start from here
        List<Pair<String, Type>> refStructure = refContainer.getBestRefStructure(); 
        
        if(refStructure == null){
            WSUtil.err("Cannot find the reference to a local variable:" + localName.toString(), e);
        }
        
        //now check whether this one is outfinish scope
        ClassType lastFinishFrameType = null;
        for(Pair<String, Type> p : refStructure){
            ClassType cType = (ClassType) p.snd();
            if(cType.superClass() == xts.FinishFrame()){
                //the local is out of the finish scope because finish frame doesn't contain locals
                lastFinishFrameType = cType; //not break, and find out the last finish
            }
        }
        
        if(lastFinishFrameType == null){
            //WSUtil.debug("Find out-finish scope assign. But it is not in an async frame, and no move needed.", localAssign);
        }
        else{ 
            //an out finish assign, need find all async frames between the current frame and the finish frame
            //and add the move statements
            List<WSAsyncClassGen> asyncFrames = new ArrayList<WSAsyncClassGen>();
            AbstractWSClassGen curFrame = this;
            while(curFrame != null && curFrame.getClassType() != lastFinishFrameType){
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
                WSUtil.debug("Find out-finish assign expr. The value will be moved into " + sb.toString(), localAssign);             
            }
        }
    }

    /**
     * A field (fieldName) may be in any upper frame of current frame
     * Search a field from a start location (startRef, startContainer),
     * and generate a reference to the frame that contains the field
     * 
     * This method is only used in move() of async frame, where the startRef is a FinishFrame
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
            Expr castedRef = genCastCall(xts.FinishFrame(), castedType, startRef); //cast[Frame, _finish](up)
            //now gothourgh the refStructure
            AbstractWSClassGen classFrame = startFrame;
            for(Pair<String, Type> p : refStructure){
                assert(p.fst().equals(UP_REF));//the out-finish assign's ref should only contains "up"
                classFrame = classFrame.getUpFrame();
                Expr upField = synth.makeFieldAccess(compilerPos, castedRef, Name.make(p.fst()), xct);
                castedRef = genCastCall(xts.Frame(), p.snd(), upField);   
            }
            return castedRef;
        }
        WSUtil.err("Cannot find reference to local variable name: "+ fieldName.toString(), null);
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
                        assert(p.fst().equals(UP_REF)); //after processing async & remote frame. the only ref is "up"
                        classFrame = classFrame.getUpFrame();
                        Expr upField = synth.makeFieldAccess(compilerPos, ref, Name.make(p.fst()), xct);
                        ref = genCastCall(xts.Frame(), p.snd(), upField);
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
        WSUtil.err("Cannot find reference to local variable name:" + fieldName.toString(), null);
        return null;
    }  
    
    
    //Async frame's "up" field point to the finish frame
    //And the local variables between finish frame and itself should be copied into the async frame as formals
    //Remote frame has no up, all ref structure contains up should be copied into the remote frame as formals
    //The return structure will only contains "up". No "k" or "r"
    private List<Pair<String, Type>> processRefStructureForAsyncAndRemoteFrame(List<Pair<String, Type>> refStructure, Name name, Type type){
        
        if(refStructure == null || refStructure.size() == 0){
            return refStructure; // no need process
        }
        
        ArrayList<Pair<String, Type>> result = new ArrayList<Pair<String, Type>>();
       
        //identify the field container frame according to the refstructure
        boolean foundKRInRef = false; //if found, no need additional search
        AbstractWSClassGen classFrame = this;
        for(Pair<String, Type> pair : refStructure){
            if(pair.fst().equals(UP_REF)){
                classFrame = classFrame.up;
            }
            else{ //"from parentK" or from "parentR"
                foundKRInRef = true;
                if(pair.fst().equals(K_REF)){
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
