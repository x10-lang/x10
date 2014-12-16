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
import java.util.List;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.If;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Try;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Pair;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Finish;
import x10.ast.FinishExpr;
import x10.ast.ForLoop;
import x10.ast.Offer;
import x10.ast.StmtSeq;
import x10.ast.When;
import x10.ast.X10Loop;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.CodePatternDetector;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.Triple;
import x10.compiler.ws.util.WSUtil;
import x10.optimizations.ForLoopOptimizer;
import x10.types.X10ClassDef;
import x10.util.CollectionFactory;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.NewInstanceSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.util.synthesizer.SuperCallSynth;
import x10.util.synthesizer.SwitchSynth;

/**
 * @author Haichuan
 * 
 * Used to generate a normal code block's fast/slow/back It can process finish
 * statement as well as simple method invocation. Finish -> translate to finish
 * stmt class Method invoke -> if the target method is a parallel method,
 * translate to fast/slow/back call
 * 
 * The inner class extends RegularFrame
 * 
 */
public class WSRegularFrameClassGen extends AbstractWSClassGen {
    //this flag is set to true when genReturnCheckStmt() is called.
    //And set to false every time a new statement is processed.
    //In this cause, the original full coverage return was limited.
    //And If there is on any additional statements after the returnCheckPath,
    //we need add an additional return to prevent the java path has no "return x" at the end of the method
    boolean isReturnPathChanged;

    // method frames
    protected WSRegularFrameClassGen(Job job, NodeFactory xnf, Context xct, WSTransformState wts,
           String className, Stmt stmt, X10ClassDef outer, Flags flags, ClassType superType) {
        super(job, xnf, xct, wts, className, superType, flags, outer,
                WSUtil.setSpeicalQualifier(stmt, outer, xnf));
        wsynth.createPCField(classSynth);
    }

    // nested frames
    protected WSRegularFrameClassGen(AbstractWSClassGen parent, Stmt stmt, String classNamePrefix) {
        super(parent, parent, classNamePrefix, parent.xts.RegularFrame(), stmt);
        wsynth.createPCField(classSynth);
    }
    
    // remote frames: at(p) & async at(p)
    protected WSRegularFrameClassGen(AbstractWSClassGen parent, AbstractWSClassGen up, Stmt stmt, String classNamePrefix, ClassType frameType) {
        super(parent, up, classNamePrefix, frameType, stmt);
        wsynth.createPCField(classSynth);
    }

    @Override
    protected void genMethods() throws SemanticException {

        if (codeBlock == null) {
            fastMSynth.setFlag(Flags.ABSTRACT);
            resumeMSynth.setFlag(Flags.ABSTRACT);
            backMSynth.setFlag(Flags.ABSTRACT);
            classSynth.setFlags(Flags.ABSTRACT);
            return;
        }

        Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> bodyCodes = transformMethodBody();

        CodeBlockSynth fastBodySynth = bodyCodes.first();
        fastMSynth.setMethodBodySynth(fastBodySynth);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        resumeBodySynth.addStmt(bodyCodes.second().genStmt());

        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        backBodySynth.addStmt(bodyCodes.third().genStmt());
    }

    protected Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> transformMethodBody() throws SemanticException {

        CodeBlockSynth fastBodySynth = new CodeBlockSynth(xnf, xct, compilerPos);
        Expr pcRef = wsynth.genPCRef(classSynth);
        SwitchSynth resumeSwitchSynth = new SwitchSynth(xnf, xct, compilerPos, pcRef);
        SwitchSynth backSwitchSynth = new SwitchSynth(xnf, xct, compilerPos, pcRef);

        ArrayList<Stmt> bodyStmts = new ArrayList<Stmt>(codeBlock.statements());
        int pcValue = 0; // The current pc value. Will increase every time an
                            // inner class is created
        int prePcValue = 0; // The current pc value. Will increase every time an
                            // inner class is created
        boolean isInResumePath = false; //For both c++ & java path, "at" & "async at" should be executed in resume path

        Set<Name> localDeclaredVar = CollectionFactory.newHashSet(); //all locals with these names will not be replaced
        
        while (bodyStmts.size() > 0) {

            Stmt s = bodyStmts.remove(0); //always remove the first one
            isReturnPathChanged = false; //have one statement, and should have return path
            TransCodes codes; //the main codes

            // need process local declare first
            if (s instanceof LocalDecl) { // Pre-processing
                s = transLocalDecl((LocalDecl) s);
                if(s == null) continue;
                if (s instanceof LocalDecl){
                    //this local is not transformed as a field. Record it
                    localDeclaredVar.add(((LocalDecl) s).name().id());
                }
           }
            // need analyze out-finish scope local assign
            if(s instanceof Eval){
                localAssignEscapeProcess((Eval)s);
            }

            //use code pattern detector to detect
            CodePatternDetector.Pattern pattern = CodePatternDetector.detectAndTransform(s, wts);
            switch(pattern){
            case Simple:
                codes = transNormalStmt(s, prePcValue, localDeclaredVar);
                break;
            case StmtSeq:
                //Unwrapp the stmts, and add them back
                bodyStmts.addAll(0, WSUtil.unwrapToStmtList(s)); //put them into target
                continue;
            case Finish:
                codes = transFinish((Finish)s, prePcValue);
                break;
            case Async:
                codes = transAsync((Async)s, prePcValue);
                break;
            case At:
                if(!isInResumePath){
                    codes = genSwitchToResumePathCodes(prePcValue);
                    isInResumePath = true;
                    bodyStmts.add(0, s); //transform the statement next loop
                }
                else{
                    codes = transAtAsyncAt((AtStmt)s, prePcValue, false, null, localDeclaredVar);
                }
                break;
            case AsyncAt:
                if(!isInResumePath){
                    codes = genSwitchToResumePathCodes(prePcValue);
                    isInResumePath = true;
                    bodyStmts.add(0, s); //transform the statement next loop
                }
                else{
                    Async async = (Async)s;
                    codes = transAtAsyncAt(s, prePcValue, true, async.clocks(), localDeclaredVar);
                }
                break;
            case When:
                codes = transWhen((When)s, prePcValue);
                break;
            case Call:
                codes = transCall((Call)((Eval)s).expr(), prePcValue, localDeclaredVar);
                break;
            case AssignCall:
                codes = transAssignCall(((Eval)s), prePcValue, localDeclaredVar);
                break;
            case If:
                codes = transIf((If) s, prePcValue);
                break;
            case For:
                codes = transFor((For) s, prePcValue);
                break;
            case While:
            case DoWhile:
                codes = transWhileDoLoop((Loop) s, prePcValue);
                break;
            case Switch:
                codes = transSwitch((Switch) s, prePcValue);
                break;
            case Block:
                codes = transBlock((Block) s, prePcValue, WSUtil
                                   .getBlockFrameClassName(className));
                break;
            case Try:
                codes = transTry((Try) s, prePcValue);
                break;
            case FinishExprAssign:
                codes = transFinishExprAssign((Eval)s, prePcValue, localDeclaredVar);
                break;
            case Unsupport:
            default:
                WSUtil.err("X10 WorkStealing cannot support:", s);
                continue;
            }
            pcValue = codes.pcValue();
            fastBodySynth.addStmts(codes.getFastStmts());
            resumeSwitchSynth.insertStatementsInCondition(prePcValue, codes.getResumeStmts());
            if(codes.getResumePostStmts().size() > 0){
                resumeSwitchSynth.insertStatementsInCondition(pcValue, codes.getResumePostStmts());
            }
            if(codes.getBackStmts().size() > 0){ //only assign call has back
                backSwitchSynth.insertStatementsInCondition(pcValue, codes.getBackStmts());
                backSwitchSynth.insertStatementInCondition(pcValue, xnf.Break(compilerPos));
            }
            prePcValue = pcValue;
        } // for loop end

        //processing the return if the return was impacted by return check stmt;
        //should only be executed in method frame
        if(isReturnPathChanged){
            Type returnType = fastMSynth.getDef().returnType().get();
            if(returnType != null && returnType != xts.Void()){
                //add return statement although it should not be exectued
                fastBodySynth.addStmt(wsynth.genReturnResultStmt(classSynth));
            }
        }
        
        return new Triple<CodeBlockSynth, SwitchSynth, SwitchSynth>(fastBodySynth, resumeSwitchSynth, backSwitchSynth);
    }

    /**
     * At least one path is complex path.  But condition is not (otherwise it is a compound stmt)
     */
    protected TransCodes transIf(If ifS, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue);
        
        // condition need replace
        ifS = ifS.cond((Expr) this.replaceLocalVarRefWithFieldAccess(ifS.cond()));

        If fastIf, slowIf; //two path;
        // true condition block
        Stmt conS = ifS.consequent();
        if (WSUtil.isComplexCodeNode(conS, wts)) {
            Block ifConBlock = (conS instanceof Block) ? (Block) conS : synth.toBlock(conS);
            TransCodes conCodes = transBlock(ifConBlock, prePcValue,
                                             WSUtil.getIFBlockClassName(className, true));          
            fastIf = ifS.consequent(xnf.Block(conS.position(), conCodes.getFastStmts()));
            //note the resume path codes has no return check
            slowIf = ifS.consequent(xnf.Block(conS.position(), conCodes.getResumeStmts()));
        }
        else{
            //should use transNormal, not use directly replace
            TransCodes ifTCodes = transNormalStmt(conS, prePcValue, Collections.EMPTY_SET);
            fastIf = ifS.consequent(WSUtil.seqStmtsToOneStmt(xnf, ifTCodes.getFastStmts().get(0)));
          //note the resume path codes has no return check
            slowIf = ifS.consequent(WSUtil.seqStmtsToOneStmt(xnf, ifTCodes.getResumeStmts().get(0)));
        }

        Stmt altS = ifS.alternative();
        if (altS != null) {
            if (WSUtil.isComplexCodeNode(altS, wts)) {
                Block ifAltBlock = (altS instanceof Block) ? (Block) altS : synth.toBlock(altS);
                TransCodes altCodes = transBlock(ifAltBlock, prePcValue,
                                                 WSUtil.getIFBlockClassName(className, false));
                fastIf = fastIf.alternative(xnf.Block(altS.position(), altCodes.getFastStmts()));
                slowIf = slowIf.alternative(xnf.Block(altS.position(), altCodes.getResumeStmts()));
            }
            else{
                //should use transNormal, not use directly replace
                TransCodes ifFCodes = transNormalStmt(altS, prePcValue, Collections.EMPTY_SET);
                fastIf = fastIf.alternative(WSUtil.seqStmtsToOneStmt(xnf, ifFCodes.getFastStmts().get(0)));
                slowIf = slowIf.alternative(WSUtil.seqStmtsToOneStmt(xnf, ifFCodes.getResumeStmts().get(0)));
            }
        }
        transCodes.addFast(fastIf);
        transCodes.addResume(slowIf);
        transCodes.increasePC();

        //Add add the return check code
        if(WSUtil.hasReturnStatement(ifS)){
            //The code is after pc change
            transCodes.addFast(genReturnCheckStmt(true));
            transCodes.addPostResume(genReturnCheckStmt(false));
        }
        
        return transCodes;
    }

    protected TransCodes transWhileDoLoop(Loop loop, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue);

        AbstractWSClassGen loopClassGen = genChildFrame(xts.RegularFrame(), loop, null);
        
        //prepare child frame call;
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, loopClassGen);
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, loopClassGen);
        transCodes.addFast(fastStmts);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();
        
        if(WSUtil.hasReturnStatement(loop)){
            //The code is after pc change
            transCodes.addFast(genReturnCheckStmt(true));
            transCodes.addPostResume(genReturnCheckStmt(false));
        }
        return transCodes;
    }

    protected TransCodes transFor(For f, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue);
        AbstractWSClassGen forClassGen = genChildFrame(xts.RegularFrame(), f, null);
        
        //prepare child frame call;
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, forClassGen);
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, forClassGen);
        transCodes.addFast(fastStmts);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();
        
        if(WSUtil.hasReturnStatement(f)){
            //The code is after pc change
            transCodes.addFast(genReturnCheckStmt(true));
            transCodes.addPostResume(genReturnCheckStmt(false));
        }
        return transCodes;
    }

    protected TransCodes transFinish(Finish f, int prePcValue) throws SemanticException {

        TransCodes transCodes = new TransCodes(prePcValue);
        AbstractWSClassGen finishClassGen = genChildFrame(xts.FinishFrame(), f, null);
        
        //prepare child frame call;
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, finishClassGen);
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, finishClassGen);
        transCodes.addFast(fastStmts);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();

        return transCodes;
    }
    
    protected TransCodes transFinishExprAssign(Eval s, int prePcValue, Set<Name> declaredLocals) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue);
        
        Assign assign = (Assign) s.expr();
        
        FinishExpr finishExpr = (FinishExpr) assign.right();
        Expr reducer = finishExpr.reducer();
        Type reducerBaseType = Types.reducerType(reducer.type()); //get the base type
        reducer = (Expr) replaceLocalVarRefWithFieldAccess(reducer, declaredLocals);
        
        AbstractWSClassGen collectingFinishFrameGen = new WSFinishStmtClassGen(this, reducerBaseType, finishExpr);
        collectingFinishFrameGen.genClass();
        
        //now start invocating the class's fast/resume/
        //pc = x;
        try{
            //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
            Stmt pcAssign = wsynth.genPCAssign(classSynth, prePcValue + 1);
            transCodes.addFast(pcAssign);
            transCodes.addResume(pcAssign);
        }
        catch(polyglot.types.NoMemberException e){
            //Just ignore the pc assign statement 
        }
        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, collectingFinishFrameGen.getClassType());
        Expr parentRef = wsynth.genUpcastCall(getClassType(), xts.Frame(), wsynth.genThisRef(classSynth));
        niSynth.addArgument(xts.Frame(), parentRef);
        
        //process the second one, reducer
        niSynth.addArgument(reducer.type(), reducer);
        niSynth.addAnnotation(wsynth.genStackAllocateAnnotation());
        NewLocalVarSynth localSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, niSynth.genExpr());
        localSynth.addAnnotation(wsynth.genStackAllocateAnnotation());
        Stmt niS = localSynth.genStmt();
        transCodes.addFast(niS);
        transCodes.addResume(niS);
        //now the instance's fast/slow method call
        Expr localRef = localSynth.getLocal(); 
        Expr fastWorkerRef = fastMSynth.getMethodBodySynth(compilerPos).getLocal(WSSynthesizer.WORKER.toString());
        Expr resumeWorkerRef = resumeMSynth.getMethodBodySynth(compilerPos).getLocal(WSSynthesizer.WORKER.toString());
        
        InstanceCallSynth fastPathCallSynth = new InstanceCallSynth(xnf, xct, compilerPos, localRef, WSSynthesizer.FAST.toString());
        fastPathCallSynth.addArgument(xts.Worker(), fastWorkerRef);
        transCodes.addFast(fastPathCallSynth.genStmt());
        
        InstanceCallSynth resumePathCallSynth = new InstanceCallSynth(xnf, xct, compilerPos, localRef, WSSynthesizer.FAST.toString());
        resumePathCallSynth.addArgument(xts.Worker(), resumeWorkerRef);
        transCodes.addResume(resumePathCallSynth.genStmt());
        
        //finally - the assign
        assign = (Assign) replaceLocalVarRefWithFieldAccess(assign, declaredLocals);
        //fast result assign;
        InstanceCallSynth fastPathResultCallSynth = new InstanceCallSynth(xnf, xct, compilerPos, localRef, WSSynthesizer.CF_RESULT_FAST.toString());
        fastPathResultCallSynth.addArgument(xts.Worker(), fastWorkerRef);
        Stmt fastAssign = xnf.Eval(assign.position(), assign.right(fastPathResultCallSynth.genExpr()));
        transCodes.addFast(fastAssign);
        //resume        
        InstanceCallSynth resumePathResultCallSynth = new InstanceCallSynth(xnf, xct, compilerPos, localRef, WSSynthesizer.CF_RESULT_FAST.toString());
        resumePathResultCallSynth.addArgument(xts.Worker(), resumeWorkerRef);
        Stmt resumeAssign = xnf.Eval(assign.position(), assign.right(resumePathResultCallSynth.genExpr()));
        transCodes.addResume(fastAssign);
        //back
        transCodes.increasePC();
        Expr backFrameRef = backMSynth.getMethodBodySynth(compilerPos).getLocal(WSSynthesizer.FRAME.toString());
        Expr castExp = wsynth.genCastCall(xts.Frame(), 
                                          xts.CollectingFinish().typeArguments(Collections.singletonList(reducerBaseType)),
                                   backFrameRef, xct);
        InstanceCallSynth backResultCallSynth = new InstanceCallSynth(xnf, xct, compilerPos, castExp, WSSynthesizer.CF_RESULT.toString());
        Stmt backAssign = xnf.Eval(assign.position(), assign.right(backResultCallSynth.genExpr()));
        transCodes.addBack(backAssign);

        //and process the back
        return transCodes;
    }
    
    protected TransCodes transWhen(When w, int prePcValue) throws SemanticException {

        TransCodes transCodes = new TransCodes(prePcValue);
        AbstractWSClassGen whenClassGen = genChildFrame(xts.RegularFrame(), w, null);
        
        //prepare child frame call;
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, whenClassGen);
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, whenClassGen);
        transCodes.addFast(fastStmts);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();

        return transCodes;
    }
    

    /**
     * Generate switch to resume path statements. In fast path;
     * _pc = x;
     * continueNow(worker)
     * @param prePcValue
     * @return
     * @throws SemanticException
     */
    protected TransCodes genSwitchToResumePathCodes(int prePcValue) throws SemanticException{
        TransCodes transCodes = new TransCodes(prePcValue);
        //_pc = x;
        try{
            //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
            Stmt pcAssign = wsynth.genPCAssign(classSynth, prePcValue + 1); 
            transCodes.addFast(pcAssign);
            transCodes.addResume(pcAssign);   
        }
        catch(polyglot.types.NoMemberException e){
            //Just ignore the pc assign statement if there is no pc field in the frame
        }
        
        Stmt fastRedoCallStmt = wsynth.genContinueNowStmt(classSynth, fastMSynth);  
        transCodes.addFast(fastRedoCallStmt);
        
        transCodes.increasePC();
        return transCodes;
    }

    
    /**
     * @param atStmt the at stmt or the async at stmt
     * @param prePcValue
     * @param isAsync true: async at(P) S; false: at(P)S
     * @param clocks: only useful when isAsync == true
     * @return two transcodes, because the pc will change more than once
     * @throws SemanticException
     */
    protected TransCodes transAtAsyncAt(Stmt stmt, int prePcValue,
                                                          boolean isAsync, 
                                                          List<Expr> clocks, //not use clocks now
                                                          Set<Name> declaredLocals) throws SemanticException{
        TransCodes transCodes = new TransCodes(prePcValue); //remote call doesn't need increase pc
        
        //transformation step by step:
        //prepare val rRootFinish:RemoteRootFinish = new RemoteRootFinish(ff);
        Pair<Stmt, Expr> remoteFFPair = wsynth.genRemoteRootFinishInitializer(classSynth);
        transCodes.addFast(remoteFFPair.fst());
        transCodes.addResume(remoteFFPair.fst());
        
        //create the frame class gen
        WSRemoteMainFrameClassGen remoteClassGen = (WSRemoteMainFrameClassGen) genChildFrame(xts.RegularFrame(), stmt, null); //no need prefix gen here
        
        //FIXME: should be merged into gen class invocation stmts
        
        //Prepare the instance
        //val rFrame = new _mainR0(rRootFinish, rRootFinish, n1);
        NewInstanceSynth remoteMainSynth = new NewInstanceSynth(xnf, xct, compilerPos, remoteClassGen.getClassType());
        remoteMainSynth.addAnnotation(wsynth.genStackAllocateAnnotation());

        if (isAsync) {
            remoteMainSynth.addArgument(xts.RemoteFinish(), remoteFFPair.snd());
        } else {
            //Create the at frame wrapper
            NewInstanceSynth remoteAtFrameSynth = new NewInstanceSynth(xnf, xct, compilerPos, xts.AtFrame());
            remoteAtFrameSynth.addAnnotation(wsynth.genStackAllocateAnnotation());
            remoteAtFrameSynth.addArgument(xts.Frame(), getThisRef());
            remoteAtFrameSynth.addArgument(xts.RemoteFinish(), remoteFFPair.snd());
            NewLocalVarSynth remoteAtFrameLocalSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, remoteAtFrameSynth.genExpr());
            remoteAtFrameLocalSynth.addAnnotation(wsynth.genStackAllocateAnnotation());
            transCodes.addFast(remoteAtFrameLocalSynth.genStmt());
            transCodes.addResume(remoteAtFrameLocalSynth.genStmt());
            Expr remoteAtFrame = remoteAtFrameLocalSynth.getLocal();
            remoteMainSynth.addArgument(xts.Frame(), remoteAtFrame);
        }
        remoteMainSynth.addArgument(xts.RemoteFinish(), remoteFFPair.snd());
        
        //iterate add all formals required
        for(Pair<Name, Type> formal : remoteClassGen.formals){
            //make an access to the value;
            Expr fieldContainerRef = this.getFieldContainerRef(formal.fst(), formal.snd());
            Expr fieldRef = synth.makeFieldAccess(compilerPos, fieldContainerRef, formal.fst(), xct).type(formal.snd());
            remoteMainSynth.addArgument(formal.snd(), fieldRef);
        }
        
        NewLocalVarSynth remoteMainLocalSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, remoteMainSynth.genExpr());
        remoteMainLocalSynth.addAnnotation(wsynth.genStackAllocateAnnotation());
        transCodes.addFast(remoteMainLocalSynth.genStmt());
        transCodes.addResume(remoteMainLocalSynth.genStmt());
        Expr remoteMainRef = remoteMainLocalSynth.getLocal();
        
        
        //Prepare worker call
        //worker.remoteRunFrame(here.next(), rFrame, ff);
        //need change place to place.home if place is a GlobalRef
        Expr place = (Expr) this.replaceLocalVarRefWithFieldAccess(remoteClassGen.getPlace(), declaredLocals);
        
        if(isAsync) {
            Stmt asyncCall = wsynth.genRunAsyncAt(classSynth, place, remoteClassGen.getClassType(), remoteMainRef);
            transCodes.addFast(asyncCall);
            transCodes.addResume(asyncCall);
        } else {
               //_pc = x; increase pc first
            try{
                //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
                Stmt pcAssign = wsynth.genPCAssign(classSynth, prePcValue + 1);
                transCodes.addFast(pcAssign);
                transCodes.addResume(pcAssign);   
            }
            catch(polyglot.types.NoMemberException e){
                //Just ignore the pc assign statement if there is no pc field in the frame
            }
            Stmt atCall = wsynth.genRunAt(classSynth, place, remoteClassGen.getClassType(), remoteMainRef);
            transCodes.addFast(atCall);
            transCodes.addResume(atCall);
            transCodes.increasePC();
        }
        return transCodes;
    }
    
    protected TransCodes transAsync(Stmt a, int prePcValue) throws SemanticException {

        TransCodes transCodes = new TransCodes(prePcValue);

        AbstractWSClassGen asyncClassGen = genChildFrame(xts.AsyncFrame(), a, null);
        
        //fast
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, asyncClassGen);
        transCodes.addFast(fastStmts);
        // resume
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, asyncClassGen);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();
        return transCodes;
    }
    
    /**
     * Transform one block code. If the code block is a complex code block,
     * translate it into a regular frame, and add corresponding codes into a new
     * block
     * 
     * Else just only replace the local access to field access
     */
    protected TransCodes transBlock(Block block, int prePcValue, String frameNamePrefix) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue);

        AbstractWSClassGen blockClassGen = genChildFrame(xts.RegularFrame(), block, frameNamePrefix);
        
        //prepare child frame call;
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, blockClassGen);
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, blockClassGen);
        transCodes.addFast(fastStmts);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();
        
        if(WSUtil.hasReturnStatement(block)){
            //The code is after pc change
            transCodes.addFast(genReturnCheckStmt(true));
            transCodes.addPostResume(genReturnCheckStmt(false));
        }
        return transCodes;
    }
    
    /**
     * TryStmt:
     * Transform the whole try as one separate frame right now.
     * Try as one frame "E" frame
     * Try's body as a lower "B" frame
     * TryFrame
     *   -->TransBodyFrame
     *  
     *  only has tryBlock as concurrent block. Then just call transBlock to transform this part
     */
    protected TransCodes transTry(Try tryStmt, int prePcValue) throws SemanticException {
        
        TransCodes transCodes = new TransCodes(prePcValue);
        AbstractWSClassGen tryClassGen = genChildFrame(xts.RegularFrame(), tryStmt, null);
        
        //FIXME: need understand the situation that a "return" appears in try's block
        //prepare child frame call;
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, tryClassGen);
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, tryClassGen);
        transCodes.addFast(fastStmts);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();
        
        if(WSUtil.hasReturnStatement(tryStmt.tryBlock())){
            //The code is after pc change
            transCodes.addFast(genReturnCheckStmt(true));
            transCodes.addPostResume(genReturnCheckStmt(false));
        }
        return transCodes;
    }
    
    
    protected TransCodes transSwitch(Switch switchStmt, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue);
        AbstractWSClassGen switchClassGen = genChildFrame(xts.RegularFrame(), switchStmt, null);
        
        //prepare child frame call;
        List<Stmt> fastStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, fastMSynth, switchClassGen);
        List<Stmt> resumeStmts = wsynth.genInvocateFrameStmts(prePcValue + 1, classSynth, resumeMSynth, switchClassGen);
        transCodes.addFast(fastStmts);
        transCodes.addResume(resumeStmts);
        transCodes.increasePC();
        
        if(WSUtil.hasReturnStatement(switchStmt)){
            //The code is after pc change
            transCodes.addFast(genReturnCheckStmt(true));
            transCodes.addPostResume(genReturnCheckStmt(false));
        }
        return transCodes;
    }
    

    /**
     * Generate this stmt " if(returnFlag == true){ return result; }
     * 
     * If a block contains return, and the block need to be transformed into a
     * separate frame, after the fast/slow call to the block, an additional
     * return check need to be added, to make sure if the return is executed in
     * the block frame, the caller need return Immediately.
     * 
     * Fast: return in non method frame, and return result in method frame
     * Resume: always just return;
     * 
     * @return
     * @throws SemanticException
     */
    protected Stmt genReturnCheckStmt(boolean isFastPath) throws SemanticException {
        isReturnPathChanged = true; //has changed the original return path
        // return and reference
        Name returnFlagName = WSSynthesizer.RETURN_FLAG;
        Expr methodFrameRef = this.getFieldContainerRef(returnFlagName, xts.Boolean());
        Expr returnFlagRef = synth.makeFieldAccess(compilerPos, methodFrameRef, returnFlagName, xct)
                .type(xts.Boolean());

        Name resultName = WSSynthesizer.RETURN_VALUE;
        Expr resultRef = null;
        if (isFastPath && resultName != null && this instanceof WSMethodFrameClassGen) {
            // has return value && the current frame is method frame
            resultRef = synth.makeFieldAccess(compilerPos, methodFrameRef, resultName, xct);
        }

        // if(returnFlag == true){
        // return result; //fast path in method frame
        // return; //resume path and non method frame
        // }
        // xnf.If(pos, cond, consequent, alternative)
        Expr returnCondition = xnf.Binary(compilerPos, returnFlagRef, Binary.EQ,
                                          synth.booleanValueExpr(true, compilerPos)).type(xts.Boolean());
        Stmt returnCheck = xnf.If(compilerPos, returnCondition, resultRef == null ? xnf.Return(compilerPos) : xnf
                .Return(compilerPos, resultRef));
        
        return returnCheck;
    }

    protected void genClassConstructor() throws SemanticException {
        wsynth.genClassConstructorType2Base(classSynth);
    }
    
    /* 
     * Cannot inline RemoteMainFrame's fast path
     * @see x10.compiler.ws.codegen.AbstractWSClassGen#isFastPathInline()
     */
    public boolean isFastPathInline(ClassType frameType){
        if(xts.isSubtype(frameType, xts.MainFrame())){
            return false; //cannot inline main frame
        }
        else{
            return super.isFastPathInline(frameType); //default true;   
        }
    }
    
}
