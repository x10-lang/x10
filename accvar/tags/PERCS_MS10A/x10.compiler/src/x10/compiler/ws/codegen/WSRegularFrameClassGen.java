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
import java.util.HashSet;
import java.util.List;

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
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import x10.ast.Async;
import x10.ast.Finish;
import x10.ast.ForLoop;
import x10.ast.When;
import x10.ast.X10Formal;
import x10.ast.X10Loop;
import x10.compiler.ws.WSCodeGenerator;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.CodePatternDetector;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.Triple;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.optimizations.ForLoopOptimizer;
import polyglot.types.Context;
import x10.util.synthesizer.ClassSynth;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
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
                WSCodeGenUtility.setSpeicalQualifier(stmt, outer, xnf));
        
        addPCField();
    }

    // nested frames
    protected WSRegularFrameClassGen(AbstractWSClassGen parent, Stmt stmt, String classNamePrefix) {
        super(parent, parent, classNamePrefix, parent.wts.regularFrameType, stmt);
        
        addPCField();
    }

    @Override
    protected void genMethods() throws SemanticException {

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

        HashSet<Name> localDeclaredVar = new HashSet<Name>(); //all locals with these names will not be replaced
        
        while (bodyStmts.size() > 0) {

            Stmt s = bodyStmts.remove(0); //always remove the first one
            isReturnPathChanged = false; //have one statement, and should have return path
            TransCodes codes;

            // need process local declare first
            if (s instanceof LocalDecl) { // Pre-processing
                s = transLocalDecl((LocalDecl) s);
                if(s == null) continue;
                if (s instanceof LocalDecl)
                    localDeclaredVar.add(((LocalDecl) s).name().id());
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
            case Compound:
                //call for help flattener help, and add results back to 
                codes = transCompoundStmt(s);
                bodyStmts.addAll(0, codes.getFlattenedCodes()); //put them into target
                continue;
            case Finish:
                codes = transFinish((Finish)s, prePcValue);
                break;
            case Async:
                codes = transAsync((Async)s, prePcValue);
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
            case ForLoop:
                codes = transForLoop((ForLoop) s, prePcValue);
                break;
            case While:
            case DoWhile:
                codes = transWhileDoLoop((Loop) s, prePcValue);
                break;
            case Switch:
                codes = transSwitch((Switch) s, prePcValue);
                break;
            case Block:
                codes = transBlock((Block) s, prePcValue, WSCodeGenUtility
                                   .getBlockFrameClassName(className));
                break;
            case Try:
                codes = transTry((Try) s, prePcValue);
                break;
            case FinishAssign:
            case Unsupport:
            default:
                System.err.println("[WS_ERR]Found un-support code patterns");
                s.prettyPrint(System.err);
                System.err.println();
                throw new SemanticException("Work-Stealing Compiling doesn't support : " + s, s.position());
            }
            pcValue = codes.getPcValue();
            fastBodySynth.addStmts(codes.first());
            resumeSwitchSynth.insertStatementsInCondition(prePcValue, codes.second());
            if(codes.third().size() > 0){ //only assign call has back
                backSwitchSynth.insertStatementsInCondition(pcValue, codes.third());
                backSwitchSynth.insertStatementInCondition(pcValue, xnf.Break(compilerPos));
            }
            prePcValue = pcValue;
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
        if (WSCodeGenUtility.isComplexCodeNode(conS, wts)) {
            Block ifConBlock = (conS instanceof Block) ? (Block) conS : synth.toBlock(conS);
            TransCodes conCodes = transBlock(ifConBlock, prePcValue,
                                             WSCodeGenUtility.getIFBlockClassName(className, true));          
            fastIf = ifS.consequent(xnf.Block(conS.position(), conCodes.first()));
            slowIf = ifS.consequent(xnf.Block(conS.position(), conCodes.second()));
        }
        else{
            //should use transNormal, not use directly replace
            TransCodes ifTCodes = transNormalStmt(conS, prePcValue, Collections.EMPTY_SET);
            fastIf = ifS.consequent(WSCodeGenUtility.seqStmtsToBlock(xnf, ifTCodes.first().get(0)));
            slowIf = ifS.consequent(WSCodeGenUtility.seqStmtsToBlock(xnf, ifTCodes.second().get(0)));
        }

        Stmt altS = ifS.alternative();
        if (altS != null) {
            if (WSCodeGenUtility.isComplexCodeNode(altS, wts)) {
                Block ifAltBlock = (altS instanceof Block) ? (Block) altS : synth.toBlock(altS);
                TransCodes altCodes = transBlock(ifAltBlock, prePcValue,
                                                 WSCodeGenUtility.getIFBlockClassName(className, false));
                fastIf = fastIf.alternative(xnf.Block(altS.position(), altCodes.first()));
                slowIf = slowIf.alternative(xnf.Block(altS.position(), altCodes.second()));
            }
            else{
                //should use transNormal, not use directly replace
                TransCodes ifFCodes = transNormalStmt(altS, prePcValue, Collections.EMPTY_SET);
                fastIf = fastIf.alternative(WSCodeGenUtility.seqStmtsToBlock(xnf, ifFCodes.first().get(0)));
                slowIf = slowIf.alternative(WSCodeGenUtility.seqStmtsToBlock(xnf, ifFCodes.second().get(0)));
            }
        }
        transCodes.addFirst(fastIf);
        transCodes.addSecond(slowIf);
        { //back
        }
        
        return transCodes;
    }

    /**
     * Transform for(p in domain) to traditional for loop and unroll unnecessary block
     * @param floop for ( i : domain) loop
     * @return transformed for(init; condition; statement);
     */
    protected For processForLoop(X10Loop floop) {
        // the next step, translate the forloop by forloopoptimizer
        ForLoopOptimizer flo = new ForLoopOptimizer(job, xts, xnf);
        flo.begin();
        Node floOut = floop.visit(flo); //floOut could be a block or a for statement
        // there are two cases from ForLoopOptimizer
        // 1. only for; 2. some local declarations and for
        // for cases 2, we need add these local declarations into for's init

        For forStmt = null;
        if (floOut instanceof Block) { // case 2
            List<ForInit> forInits = new ArrayList<ForInit>();
            for (Stmt s : ((Block) floOut).statements()) {
                if (s instanceof For) {
                    forStmt = (For) s;
                } else {
                    assert (s instanceof ForInit);// otherwise the ForLoopOptimizer is wrong
                    forInits.add((ForInit) s);
                }
            }
            forInits.addAll(forStmt.inits());
            forStmt = forStmt.inits(forInits); // replace inits
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
            forStmt = forStmt.body(xnf.Block(forStmt.position(), stmts));
        }
        return forStmt;
    }

    /**
     * Transform for ( i : domain) style 
     */
    protected TransCodes transForLoop(ForLoop floop, int prePcValue) throws SemanticException {
        For f = processForLoop(floop);
        return transFor(f, prePcValue);
    }

    protected TransCodes transWhileDoLoop(Loop loop, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen loopClassGen = genChildFrame(wts.regularFrameType, loop, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), loopClassGen);

        boolean hasReturnInBlock = WSCodeGenUtility.hasReturnStatement(loop);
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

        AbstractWSClassGen forClassGen = genChildFrame(wts.regularFrameType, f, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), forClassGen);

        boolean hasReturnInBlock = WSCodeGenUtility.hasReturnStatement(f);
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

        AbstractWSClassGen finishClassGen = genChildFrame(wts.finishFrameType, f, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(prePcValue + 1, finishClassGen);
        
        return childCallCodes;
    }
    
    protected TransCodes transWhen(When w, int prePcValue) throws SemanticException {

        AbstractWSClassGen finishClassType = genChildFrame(wts.regularFrameType, w, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(prePcValue + 1, finishClassType);
        
        return childCallCodes;
    }



    protected TransCodes transAsync(Stmt a, int prePcValue) throws SemanticException {

        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen asyncClassGen = genChildFrame(wts.asyncFrameType, a, null);
        
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
     * Gen Stmt: ff.asyncs ++ or --, with/without atomic
     * @param increase true: ++, false --
     * @param atomic true, add atomic 
     * @return ff.asyncs++ or ff.asyncs-- or atomic{...}
     * @throws SemanticException 
     */
    protected Stmt genFFChangeStmt(boolean increase, boolean atomic) throws SemanticException{
        
        Expr ffRef = synth.makeFieldAccess(compilerPos, getThisRef(), FF, xct);
        Expr asyncRef = synth.makeFieldAccess(compilerPos, ffRef, ASYNCS, xct);
        Expr changeExpr;
        if(increase){
            changeExpr = synth.makeFieldAssign(compilerPos, ffRef, ASYNCS, xnf.Binary(compilerPos, asyncRef, Binary.ADD, synth.intValueExpr(1, compilerPos)).type(xts.Int()), xct);
//            changeExpr = xnf.Unary(compilerPos, asyncRef, Unary.POST_INC).type(xts.Int());  
        }
        else{
            changeExpr = synth.makeFieldAssign(compilerPos, ffRef, ASYNCS, xnf.Binary(compilerPos, asyncRef, Binary.ADD, synth.intValueExpr(-1, compilerPos)).type(xts.Int()), xct);
//            changeExpr = xnf.Unary(compilerPos, asyncRef, Unary.POST_DEC).type(xts.Int());  
        }
        Eval changeEval = xnf.Eval(compilerPos, changeExpr);
        
        if(atomic){
            return xnf.Atomic(compilerPos, xnf.Here(compilerPos).type(xts.Place()), changeEval); 
        }
        else{
            return changeEval;
        }
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

        AbstractWSClassGen blockClassGen = genChildFrame(wts.regularFrameType, block, frameNamePrefix);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), blockClassGen);

        boolean hasReturnInBlock = WSCodeGenUtility.hasReturnStatement(block);
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
     * TryStmt only has tryBlock as concurrent block. Then just call transBlock to transform this part
     */
    protected TransCodes transTry(Try tryStmt, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1);

        TransCodes tryBlockCodes = transBlock(tryStmt.tryBlock(), prePcValue,
                                         WSCodeGenUtility.getBlockFrameClassName(className));          
        
        Try fastTry, slowTry;
        { // fast
            fastTry = tryStmt.tryBlock(xnf.Block(tryStmt.position(), tryBlockCodes.first()));
        }
        { // resume
            slowTry = tryStmt.tryBlock(xnf.Block(tryStmt.position(), tryBlockCodes.second()));
        }
        { //back, nothing
        }
        transCodes.addFirst(fastTry);
        transCodes.addSecond(slowTry);
        
        return transCodes;
    }
    
    
    protected TransCodes transSwitch(Switch switchStmt, int prePcValue) throws SemanticException {
        TransCodes transCodes = new TransCodes(prePcValue + 1);

        AbstractWSClassGen switchClassGen = genChildFrame(wts.regularFrameType, switchStmt, null);
        
        //prepare child frame call;
        TransCodes childCallCodes = genInvocateFrameStmts(transCodes.getPcValue(), switchClassGen);

        boolean hasReturnInBlock = WSCodeGenUtility.hasReturnStatement(switchStmt);
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

        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, wts.frameType, "up");
        Expr ffRef = conSynth.addFormal(compilerPos, Flags.FINAL, wts.finishFrameType, "ff");
        SuperCallSynth superCallSynth = conCodeSynth.createSuperCall(compilerPos, classSynth.getDef());
        superCallSynth.addArgument(wts.frameType, upRef);
        superCallSynth.addArgument(wts.finishFrameType, ffRef);
    }
}
