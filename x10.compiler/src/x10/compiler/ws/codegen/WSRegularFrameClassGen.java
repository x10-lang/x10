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
import java.util.List;
import java.util.Set;

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
import x10.ast.ForLoop;
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
           String className, Stmt stmt, ClassDef outer, Flags flags, ClassType superType) {
        super(job, xnf, xct, wts, className, superType, flags, outer,
                WSUtil.setSpeicalQualifier(stmt, outer, xnf));
        
        addPCField();
    }

    // nested frames
    protected WSRegularFrameClassGen(AbstractWSClassGen parent, Stmt stmt, String classNamePrefix) {
        super(parent, parent, classNamePrefix, parent.xts.RegularFrame(), stmt);
        
        addPCField();
    }
    
    // remote frames: at(p) & async at(p)
    protected WSRegularFrameClassGen(AbstractWSClassGen parent, AbstractWSClassGen up, Stmt stmt, String classNamePrefix, ClassType frameType) {
        super(parent, up, classNamePrefix, frameType, stmt);
        
        addPCField();
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


        // need final process closure issues
        fastBodySynth.addCodeProcessingJob(new ClosureDefReinstantiator(xts, xct, this.getClassDef(), fastMSynth.getDef()));

        resumeBodySynth.addCodeProcessingJob(new ClosureDefReinstantiator(xts, xct, this.getClassDef(), resumeMSynth.getDef()));

        // add all references
        fastBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));
        resumeBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));
        backBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));

    }

    protected Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> transformMethodBody() throws SemanticException {

        CodeBlockSynth fastBodySynth = new CodeBlockSynth(xnf, xct, compilerPos);
        Expr pcRef = synth.makeFieldAccess(compilerPos, getThisRef(), PC, xct);
        SwitchSynth resumeSwitchSynth = new SwitchSynth(xnf, xct, compilerPos, pcRef);
        SwitchSynth backSwitchSynth = new SwitchSynth(xnf, xct, compilerPos, pcRef);

        ArrayList<Stmt> bodyStmts = new ArrayList<Stmt>(codeBlock.statements());
        int pcValue = 0; // The current pc value. Will increase every time an
                            // inner class is created
        int prePcValue = 0; // The current pc value. Will increase every time an
                            // inner class is created
        boolean isFrameOnHeap = false; //For both c++ & java path, "at" & "async at" transform need the frame on heap first
        
        Set<Name> localDeclaredVar = CollectionFactory.newHashSet(); //all locals with these names will not be replaced
        
        while (bodyStmts.size() > 0) {

            Stmt s = bodyStmts.remove(0); //always remove the first one
            isReturnPathChanged = false; //have one statement, and should have return path
            TransCodes codes; //the main codes
            TransCodes codes2 = null; //only used in at stmt transformation, which need additional check

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
                if(!isFrameOnHeap){
                    codes = genMoveFrameToHeapCodes(prePcValue);
                    isFrameOnHeap = true;
                    bodyStmts.add(0, s); //transform the statement next loop
                }
                else{
                    Pair<TransCodes, TransCodes> codesPair = transAtAsyncAt((AtStmt)s, prePcValue, false, null, localDeclaredVar);
                    codes = codesPair.fst();
                    codes2 = codesPair.snd();
                    //At stmt need additional block check
                }
                break;
            case AsyncAt:
                if(!isFrameOnHeap){
                    codes = genMoveFrameToHeapCodes(prePcValue);
                    isFrameOnHeap = true;
                    bodyStmts.add(0, s); //transform the statement next loop
                }
                else{
                    Async async = (Async)s;
                    Pair<TransCodes, TransCodes> codesPair = transAtAsyncAt(s, prePcValue, true, async.clocks(), localDeclaredVar);
                    codes = codesPair.fst();
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
            case FinishAssign:
            case Unsupport:
            default:
                WSUtil.err("X10 WorkStealing cannot support:", s);
                continue;
            }
            pcValue = codes.getPcValue();
            fastBodySynth.addStmts(codes.first());
            resumeSwitchSynth.insertStatementsInCondition(prePcValue, codes.second());
            if(codes.third().size() > 0){ //only assign call has back
                backSwitchSynth.insertStatementsInCondition(pcValue, codes.third());
                backSwitchSynth.insertStatementInCondition(pcValue, xnf.Break(compilerPos));
            }
            prePcValue = pcValue;
            
            if(codes2 != null){
                //Process atstmt's condition check
                pcValue = codes2.getPcValue();
                fastBodySynth.addStmts(codes2.first());
                resumeSwitchSynth.insertStatementsInCondition(pcValue, codes2.second());
                prePcValue = pcValue;
                codes2 = null;
            }
        } // for loop end

        //processing the return if the return was impacted by return check stmt;
        if(isReturnPathChanged){
            Type returnType = fastMSynth.getDef().returnType().get();
            if(returnType != null && returnType != xts.Void()){
                //add return statement although it should not be exectued
                Name returnFlagName = this.queryMethodReturnFlagName();
                Expr methodFrameRef = this.getFieldContainerRef(returnFlagName, xts.Boolean());
                Name resultName = this.queryMethodResultFieldName();
                Expr resultRef = synth.makeFieldAccess(compilerPos, methodFrameRef, resultName, xct);
                fastBodySynth.addStmt(xnf.Return(compilerPos, resultRef));
            }
        }
        
        return new Triple<CodeBlockSynth, SwitchSynth, SwitchSynth>(fastBodySynth, resumeSwitchSynth, backSwitchSynth);
    }
    
    /**
     * At least one path is complex path.  But condition is not (otherwise it is a compound stmt)
     */
    protected TransCodes transIf(If ifS, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1);
        
        // condition need replace
        ifS = ifS.cond((Expr) this.replaceLocalVarRefWithFieldAccess(ifS.cond()));

        If fastIf, slowIf; //two path;
        // true condition block
        Stmt conS = ifS.consequent();
        if (WSUtil.isComplexCodeNode(conS, wts)) {
            Block ifConBlock = (conS instanceof Block) ? (Block) conS : synth.toBlock(conS);
            TransCodes conCodes = transBlock(ifConBlock, prePcValue,
                                             WSUtil.getIFBlockClassName(className, true));          
            fastIf = ifS.consequent(xnf.Block(conS.position(), conCodes.first()));
            slowIf = ifS.consequent(xnf.Block(conS.position(), conCodes.second()));
        }
        else{
            //should use transNormal, not use directly replace
            TransCodes ifTCodes = transNormalStmt(conS, prePcValue, Collections.EMPTY_SET);
            fastIf = ifS.consequent(WSUtil.seqStmtsToOneStmt(xnf, ifTCodes.first().get(0)));
            slowIf = ifS.consequent(WSUtil.seqStmtsToOneStmt(xnf, ifTCodes.second().get(0)));
        }

        Stmt altS = ifS.alternative();
        if (altS != null) {
            if (WSUtil.isComplexCodeNode(altS, wts)) {
                Block ifAltBlock = (altS instanceof Block) ? (Block) altS : synth.toBlock(altS);
                TransCodes altCodes = transBlock(ifAltBlock, prePcValue,
                                                 WSUtil.getIFBlockClassName(className, false));
                fastIf = fastIf.alternative(xnf.Block(altS.position(), altCodes.first()));
                slowIf = slowIf.alternative(xnf.Block(altS.position(), altCodes.second()));
            }
            else{
                //should use transNormal, not use directly replace
                TransCodes ifFCodes = transNormalStmt(altS, prePcValue, Collections.EMPTY_SET);
                fastIf = fastIf.alternative(WSUtil.seqStmtsToOneStmt(xnf, ifFCodes.first().get(0)));
                slowIf = slowIf.alternative(WSUtil.seqStmtsToOneStmt(xnf, ifFCodes.second().get(0)));
            }
        }
        transCodes.addFirst(fastIf);
        transCodes.addSecond(slowIf);
        { //back
        }
        
        return transCodes;
    }

    protected TransCodes transWhileDoLoop(Loop loop, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen loopClassGen = genChildFrame(xts.RegularFrame(), loop, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), loopClassGen);

        boolean hasReturnInBlock = WSUtil.hasReturnStatement(loop);
        { // fast
            transCodes.addFirst(childCallCodes.first());
            if (hasReturnInBlock) {
                transCodes.addFirst(genReturnCheckStmt(true));
            }
        }
        { // resume
            transCodes.addSecond(childCallCodes.second());                                 // 0
            if (hasReturnInBlock) {
                transCodes.addSecond(genReturnCheckStmt(false));
            }
        }
        { //back, nothing
        }
        return transCodes;
    }

    protected TransCodes transFor(For f, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen forClassGen = genChildFrame(xts.RegularFrame(), f, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), forClassGen);

        boolean hasReturnInBlock = WSUtil.hasReturnStatement(f);
        { // fast
            transCodes.addFirst(childCallCodes.first());
            if (hasReturnInBlock) {
                transCodes.addFirst(genReturnCheckStmt(true));
            }
        }
        { // resume
            transCodes.addSecond(childCallCodes.second());                                 // 0
            if (hasReturnInBlock) {
                transCodes.addSecond(genReturnCheckStmt(false));
            }
        }
        { //back, nothing
        }
        return transCodes;
    }

    protected TransCodes transFinish(Finish f, int prePcValue) throws SemanticException {

        AbstractWSClassGen finishClassGen = genChildFrame(xts.FinishFrame(), f, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(prePcValue + 1, finishClassGen);
        
        return childCallCodes;
    }
    
    protected TransCodes transWhen(When w, int prePcValue) throws SemanticException {

        AbstractWSClassGen finishClassType = genChildFrame(xts.RegularFrame(), w, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(prePcValue + 1, finishClassType);
        
        return childCallCodes;
    }
    
    //Generate move to heap codes
    //_pc = x;
    //if(ff.redirect == null){
    //    moveToHeap(worker);
    //}
    protected TransCodes genMoveFrameToHeapCodes(int prePcValue) throws SemanticException{
        TransCodes transCodes = new TransCodes(prePcValue + 1);
        
        //_pc = x;
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
        
        Expr thisRef = genUpcastCall(getClassType(), xts.RegularFrame(), getThisRef());        
        InstanceCallSynth fastRedoCallSynth = new InstanceCallSynth(xnf, xct, compilerPos, thisRef, CONTINUE_NOW.toString());
        Expr fastWorkerRef = fastMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        fastRedoCallSynth.addArgument(xts.Worker(), fastWorkerRef);
        Stmt fastRedoCallStmt = fastRedoCallSynth.genStmt();

        if(!wts.__CPP__){
            //java path, always redo() in fast path
            transCodes.addFirst(fastRedoCallStmt);
        }
        else{
            //c++ path, only redo() if ff.redirect == null. make sure ff is migrated
            // if statement with redo call
            Expr ffRef = synth.makeFieldAccess(compilerPos, getThisRef(), FF, xct);
            Expr redirectRef = synth.makeFieldAccess(compilerPos, ffRef, REDIRECT, xct);
            Expr redoCheck = xnf.Binary(compilerPos, redirectRef, Binary.EQ,
                                        xnf.NullLit(compilerPos).type(xts.FinishFrame())).type(xts.Boolean());
            Stmt fastIfRedoStmt = xnf.If(compilerPos, redoCheck, fastRedoCallStmt);     
            transCodes.addFirst(fastRedoCallStmt/*fastIfRedoStmt*/);            
        }        
        
        //resume path no need the redo
//        InstanceCallSynth resumeRedoCallSynth = new InstanceCallSynth(xnf, xct, compilerPos, thisRef, REDO.toString());
//        Expr resumeWorkerRef = resumeMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
//        resumeRedoCallSynth.addArgument(xts.Worker(), resumeWorkerRef);
//        Stmt resumeIfRedoStmt = xnf.If(compilerPos, redoCheck, resumeRedoCallSynth.genStmt());        
//        transCodes.addSecond(resumeIfRedoStmt);
        
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
    protected Pair<TransCodes, TransCodes> transAtAsyncAt(Stmt stmt, int prePcValue,
                                                          boolean isAsync, 
                                                          List<Expr> clocks, //not use clocks now
                                                          Set<Name> declaredLocals) throws SemanticException{
        TransCodes transCodes = new TransCodes(prePcValue); //remote call doesn't need increase pc
        
        //transformation step by step:
        
        Expr ffRef = synth.makeFieldAccess(compilerPos, getThisRef(), FF, xct);
        
        //create the frame class gen
        WSRemoteMainFrameClassGen remoteClassGen = (WSRemoteMainFrameClassGen) genChildFrame(xts.RegularFrame(), stmt, null); //no need prefix gen here
        
        //prepare val rRootFinish:RemoteRootFinish = new RemoteRootFinish(ff).init();
        NewInstanceSynth remoteRootFFSynth = new NewInstanceSynth(xnf, xct, compilerPos, xts.RemoteFinish());
        remoteRootFFSynth.addAnnotation(genStackAllocateAnnotation());
        remoteRootFFSynth.addArgument(xts.FinishFrame(), ffRef);
        NewLocalVarSynth remoteRootFFRefLocalSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, remoteRootFFSynth.genExpr());
        remoteRootFFRefLocalSynth.addAnnotation(genStackAllocateAnnotation());
        Expr remoteRootFFRef = remoteRootFFRefLocalSynth.getLocal();
        transCodes.addFirst(remoteRootFFRefLocalSynth.genStmt());
        transCodes.addSecond(remoteRootFFRefLocalSynth.genStmt());
        
        //Prepare the instance
        //val rFrame = new _mainR0(rRootFinish, rRootFinish, n1);
        NewInstanceSynth remoteMainSynth = new NewInstanceSynth(xnf, xct, compilerPos, remoteClassGen.getClassType());
        remoteMainSynth.addAnnotation(genStackAllocateAnnotation());

        if (isAsync) {
            remoteMainSynth.addArgument(xts.RemoteFinish(), remoteRootFFRef);
        } else {
            NewInstanceSynth remoteAtFrameSynth = new NewInstanceSynth(xnf, xct, compilerPos, xts.AtFrame());
            remoteAtFrameSynth.addAnnotation(genStackAllocateAnnotation());
            remoteAtFrameSynth.addArgument(xts.Frame(), getThisRef());
            remoteAtFrameSynth.addArgument(xts.RemoteFinish(), remoteRootFFRef);
            NewLocalVarSynth remoteAtFrameLocalSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, remoteAtFrameSynth.genExpr());
            remoteAtFrameLocalSynth.addAnnotation(genStackAllocateAnnotation());
            transCodes.addFirst(remoteAtFrameLocalSynth.genStmt());
            transCodes.addSecond(remoteAtFrameLocalSynth.genStmt());
            Expr remoteAtFrame = remoteAtFrameLocalSynth.getLocal();
            remoteMainSynth.addArgument(xts.Frame(), remoteAtFrame);
        }
        remoteMainSynth.addArgument(xts.RemoteFinish(), remoteRootFFRef);
        
        //iterate add all formals required
        for(Pair<Name, Type> formal : remoteClassGen.formals){
            //make an access to the value;
            Expr fieldContainerRef = this.getFieldContainerRef(formal.fst(), formal.snd());
            Expr fieldRef = synth.makeFieldAccess(compilerPos, fieldContainerRef, formal.fst(), xct).type(formal.snd());
            remoteMainSynth.addArgument(formal.snd(), fieldRef);
        }
        
        NewLocalVarSynth remoteMainLocalSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, remoteMainSynth.genExpr());
        remoteMainLocalSynth.addAnnotation(genStackAllocateAnnotation());
        transCodes.addFirst(remoteMainLocalSynth.genStmt());
        transCodes.addSecond(remoteMainLocalSynth.genStmt());
        Expr remoteMainRef = remoteMainLocalSynth.getLocal();
        
        
        //Prepare worker call
        //worker.remoteRunFrame(here.next(), rFrame, ff);
        //need change place to place.home if place is a GlobalRef
        Expr place = (Expr) this.replaceLocalVarRefWithFieldAccess(remoteClassGen.getPlace(), declaredLocals);
        if (xts.hasSameClassDef(Types.baseType(place.type()), xts.GlobalRef())) {
                place = synth.makeFieldAccess(stmt.position(),place, xts.homeName(), xct);
        }
        String method = isAsync ? RUN_ASYNC_AT.toString() : RUN_AT.toString();
        
        TransCodes transCodes2 = null;
        if(isAsync) {
            { //fast
               InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, compilerPos, xnf.CanonicalTypeNode(compilerPos, xts.Worker()), method);
               callSynth.addArgument(xts.Place(), place);
               callSynth.addArgument(remoteClassGen.getClassType(), remoteMainRef);
               transCodes.addFirst(callSynth.genStmt());
           }
           { //resume
               InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, compilerPos, xnf.CanonicalTypeNode(compilerPos, xts.Worker()), method);
               callSynth.addArgument(xts.Place(), place);
               callSynth.addArgument(remoteClassGen.getClassType(), remoteMainRef);
               transCodes.addSecond(callSynth.genStmt());
           }
           } else {
               transCodes2 = new TransCodes(prePcValue + 1);
               //block on the flag 
               //_pc = x; increase pc first
               try{
                   //check whether the frame contains pc field. For optimized finish frame and async frame, there is no pc field
                   Expr pcAssgn = synth.makeFieldAssign(compilerPos, getThisRef(), PC,
                                         synth.intValueExpr(transCodes2.getPcValue(), compilerPos), xct).type(xts.Int());
                   transCodes.addFirst(xnf.Eval(compilerPos, pcAssgn));
                   transCodes.addSecond(xnf.Eval(compilerPos, pcAssgn));   
               }
               catch(polyglot.types.NoMemberException e){
                   //Just ignore the pc assign statement if there is no pc field in the frame
               }
               
               { //fast
                   InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, compilerPos, xnf.CanonicalTypeNode(compilerPos, xts.Worker()), method);
                   callSynth.addArgument(xts.Place(), place);
                   callSynth.addArgument(remoteClassGen.getClassType(), remoteMainRef);
                   transCodes.addFirst(callSynth.genStmt());
               }
               { //resume
                   InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, compilerPos, xnf.CanonicalTypeNode(compilerPos, xts.Worker()), method);
                   callSynth.addArgument(xts.Place(), place);
                   callSynth.addArgument(remoteClassGen.getClassType(), remoteMainRef);
                   transCodes.addSecond(callSynth.genStmt());
               }
           }
        return new Pair<TransCodes, TransCodes>(transCodes, transCodes2);
    }
    
    protected TransCodes transAsync(Stmt a, int prePcValue) throws SemanticException {

        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen asyncClassGen = genChildFrame(xts.AsyncFrame(), a, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), asyncClassGen);
        {// fast
            //ff.asyncs++
            // child frame call with push()
            transCodes.addFirst(childCallCodes.first());
            // ff.asyncs--
        }
        {// resume
            //atomic ff.asyncs++
            // child frame call with push()
            transCodes.addSecond(childCallCodes.second());
            //atomic ff.asyncs--
        }
        {// back - nothing? Really nothing?
        }
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
        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen blockClassGen = genChildFrame(xts.RegularFrame(), block, frameNamePrefix);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), blockClassGen);

        boolean hasReturnInBlock = WSUtil.hasReturnStatement(block);
        { // fast
            transCodes.addFirst(childCallCodes.first());
            if (hasReturnInBlock) {
                transCodes.addFirst(genReturnCheckStmt(true));
            }
        }
        { // resume
            transCodes.addSecond(childCallCodes.second());                                 // 0
            if (hasReturnInBlock) {
                transCodes.addSecond(genReturnCheckStmt(false));
            }
        }
        { //back, nothing
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
        
        TransCodes transCodes = new TransCodes(prePcValue + 1);
        AbstractWSClassGen tryClassGen = genChildFrame(xts.RegularFrame(), tryStmt, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), tryClassGen);

        //FIXME: need understand the situation tht a "return" appears in try's block
        boolean hasReturnInBlock = WSUtil.hasReturnStatement(tryStmt.tryBlock());
        { // fast
            transCodes.addFirst(childCallCodes.first());
            if (hasReturnInBlock) {
                transCodes.addFirst(genReturnCheckStmt(true));
            }
        }
        { // resume
            transCodes.addSecond(childCallCodes.second());                                 // 0
            if (hasReturnInBlock) {
                transCodes.addSecond(genReturnCheckStmt(false));
            }
        }
        { //back, nothing
        }        
        return transCodes;
    }
    
    
    protected TransCodes transSwitch(Switch switchStmt, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen switchClassGen = genChildFrame(xts.RegularFrame(), switchStmt, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), switchClassGen);

        boolean hasReturnInBlock = WSUtil.hasReturnStatement(switchStmt);
        { // fast
            transCodes.addFirst(childCallCodes.first());
            if (hasReturnInBlock) {
                transCodes.addFirst(genReturnCheckStmt(true));
            }
        }
        { // resume
            transCodes.addSecond(childCallCodes.second());                                 // 0
            if (hasReturnInBlock) {
                transCodes.addSecond(genReturnCheckStmt(false));
            }
        }
        { //back, nothing
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
        Name returnFlagName = this.queryMethodReturnFlagName();
        Expr methodFrameRef = this.getFieldContainerRef(returnFlagName, xts.Boolean());
        Expr returnFlagRef = synth.makeFieldAccess(compilerPos, methodFrameRef, returnFlagName, xct)
                .type(xts.Boolean());

        Name resultName = this.queryMethodResultFieldName();
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
        // add all constructors
        CodeBlockSynth conCodeSynth = conSynth.createConstructorBody(compilerPos);

        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, xts.Frame(), UP.toString());
        Expr ffRef = conSynth.addFormal(compilerPos, Flags.FINAL, xts.FinishFrame(), FF.toString());
        SuperCallSynth superCallSynth = conCodeSynth.createSuperCall(compilerPos, classSynth.getDef());
        superCallSynth.addArgument(xts.Frame(), upRef);
        superCallSynth.addArgument(xts.FinishFrame(), ffRef);
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
