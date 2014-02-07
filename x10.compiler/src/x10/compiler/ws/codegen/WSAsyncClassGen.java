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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Catch;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10.ast.Async;
import x10.ast.Offer;
import x10.ast.StmtSeq;
import x10.compiler.ws.WSCodeGenerator;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.CodePatternDetector;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSUtil;
import x10.compiler.ws.util.CodePatternDetector.Pattern;
import x10.types.X10ClassType;
import x10.util.synthesizer.ClassSynth;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.MethodSynth;
import x10.util.synthesizer.SuperCallSynth;
import x10.util.synthesizer.SwitchSynth;

/**
 * @author Haichuan
 * 
 * Generate the async frame class
 * 
 * Process
 * pre-process the stmt, if it is a block, transform it into List<Stmt>
 * If the stmts contains concurrent construct,
 *   all the statements will be transformed into a regular frame.
 *   
 * If the stmts doesn't contain concurrent construct, transform all statements one by one
 * 
 * 
 *
 */
public class WSAsyncClassGen extends AbstractWSClassGen {
    protected final AbstractWSClassGen parentK; //used to store its parent continuation    
    protected final List<Pair<Name,Type>> formals; //the formals are not real formals, but local var copied from parent frames;
    protected final List<LocalAssign> outFinishScopeLocalAssign;//all the locals in this scope need be processed in move
    
    protected boolean inFrameTransform; //record whether the code block can be transformed in the async frame
    
    
    public WSAsyncClassGen(AbstractWSClassGen parent, Async a) {
        //Note in building the tree, we use parentFinish as async frame's up frame
        super(parent, getFinishFrameOfAsyncFrame(parent),
                WSUtil.getAsyncStmtClassName(parent.getClassName()),
                parent.xts.AsyncFrame(), a.body());
        inFrameTransform = canInFrameTransform(codeBlock);
        
        if(!wts.OPT_PC_FIELD){
            wsynth.createPCField(classSynth);
        }
        parentK = parent; //record parent continuation
        formals = new ArrayList<Pair<Name, Type>>();
        outFinishScopeLocalAssign = new ArrayList<LocalAssign>();
    }

    
    /**
     * Will generate fast, back and move method
     * 
     * @throws SemanticException
     */
    @Override
    protected void genMethods() throws SemanticException {

        CodeBlockSynth fastBodySynth;
        CodeBlockSynth resumeBodySynth; 
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        
        if(wts.DISABLE_EXCEPTION_HANDLE){
            fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
            resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        }
        else{
            fastBodySynth = new CodeBlockSynth(xnf, xct, compilerPos);
            resumeBodySynth = new CodeBlockSynth(xnf, xct, compilerPos);
        }
        //the pc and switch table are only set value if we turn off pc field optimizatoin
        Expr pcRef = null;
        SwitchSynth resumeSwitchSynth = null;
        SwitchSynth backSwitchSynth = null;
        if(!wts.OPT_PC_FIELD){
            pcRef = wsynth.genPCRef(classSynth);
            resumeSwitchSynth = resumeBodySynth.createSwitchStmt(compilerPos, pcRef);
            backSwitchSynth = backBodySynth.createSwitchStmt(compilerPos, pcRef);
        }
        
        //Used for in frame transformation: record local variables that are not transformed as fields
        Set<Name> localDeclaredVars = CollectionFactory.newHashSet(); //all locals with these names will not be replaced
                
        if(!inFrameTransform){
            //we create a new frame to transform the async's body
            AbstractWSClassGen childFrameGen = genChildFrame(xts.RegularFrame(), codeBlock, WSUtil.getBlockFrameClassName(getClassName()));
            List<Stmt> callCodes = wsynth.genInvocateFrameStmts(1, classSynth, fastMSynth, childFrameGen);
            fastBodySynth.addStmts(callCodes);       
            //no codes in resume path here
        }
        else{
            //transform code one by one in the async frame. No more deeper frames will be generated
            ArrayList<Stmt> bodyStmts = new ArrayList<Stmt>(codeBlock.statements());
            
            int pcValue = 0; //The current pc value. Will increase every time an inner class is created
            int prePcValue = 0; //the last time's pc value. If pc value is changed, need generate a switch case
            
            while (bodyStmts.size() > 0) {
                Stmt s = bodyStmts.remove(0); //always remove the first one
                TransCodes codes;
                
                // need process local declare first
                if (s instanceof LocalDecl) { // Pre-processing
                    s = transLocalDecl((LocalDecl) s);
                    if(s == null) continue;
                    if (s instanceof LocalDecl)
                        localDeclaredVars.add(((LocalDecl) s).name().id());
                }
                // need analyze out-finish scope local assign                
                if(s instanceof Eval){
                    localAssignEscapeProcess((Eval)s);
                }

                //use code pattern detector to detect
                CodePatternDetector.Pattern pattern = CodePatternDetector.detectAndTransform(s, wts);
                switch(pattern){
                case Simple:
                    codes = transNormalStmt(s, prePcValue, localDeclaredVars);
                    break;
                case StmtSeq:
                    //Unwrapp the stmts, and add them back
                    bodyStmts.addAll(0, ((StmtSeq)s).statements()); //put them into target
                    continue;
                case Call:
                    codes = transCall((Call)((Eval)s).expr(), prePcValue, localDeclaredVars);
                    break;
                case AssignCall:
                    codes = transAssignCall(((Eval)s), prePcValue, localDeclaredVars);
                    break;
                default:
                    WSUtil.err("X10 WorkStealing cannot support:", s);
                    continue;
                }
                fastBodySynth.addStmts(codes.getFastStmts());
                pcValue = codes.pcValue();
                if(!wts.OPT_PC_FIELD){
                    resumeSwitchSynth.insertStatementsInCondition(prePcValue, codes.getResumeStmts());
                    if(codes.getBackStmts().size() > 0){ //only assign call has back
                        backSwitchSynth.insertStatementsInCondition(pcValue, codes.getBackStmts());
                        backSwitchSynth.insertStatementInCondition(pcValue, xnf.Break(compilerPos));
                    }
                }
                else{
                    if(prePcValue == 0 && codes.getResumePostStmts().size() > 0){
                        resumeBodySynth.addStmts(codes.getResumePostStmts());
                    }
                    if(prePcValue == 1){
                        resumeBodySynth.addStmts(codes.getResumeStmts());
                    }
                    //because there is only one possible assign call, its safe to add the statement to back path
                    if(codes.getBackStmts().size() > 0){ //only assign call has back
                        backBodySynth.addStmts(codes.getBackStmts());
                    }
                }
                prePcValue = pcValue;
            }//while end
        } //in frame transform end
        
        //Put the codes into a try block
        if(!wts.DISABLE_EXCEPTION_HANDLE){
            Block fastBlock = (Block) fastBodySynth.genStmt();
            Block resumeBlock = (Block) resumeBodySynth.genStmt();
            fastMSynth.getMethodBodySynth(compilerPos).addStmt(wsynth.genExceptionHandler(fastBlock.statements(), classSynth));
            if(resumeBlock.statements().size() > 0){
                resumeMSynth.getMethodBodySynth(compilerPos).addStmt(wsynth.genExceptionHandler(resumeBlock.statements(), classSynth));            
            }
        }
        

        //After fast body, there should be a poll 
        fastMSynth.getMethodBodySynth(compilerPos).addStmt(wsynth.genPollStmt(classSynth, fastMSynth));
        genMoveMethod(localDeclaredVars);

    }
    
    private void genMoveMethod(Set<Name> localDeclaredVar) throws SemanticException {
        //Move method - Used to move data for all out scope assign statements
        MethodSynth moveMSynth = classSynth.createMethod(classSynth.pos(), WSSynthesizer.MOVE.toString());
        moveMSynth.setFlag(Flags.PUBLIC);
        Expr moveFfRef = moveMSynth.addFormal(compilerPos, Flags.FINAL, xts.FinishFrame(), WSSynthesizer.FF.toString());
        CodeBlockSynth moveBodySynth = moveMSynth.getMethodBodySynth(compilerPos);
        //How to move
        //  Get the assign expression, the left will be the right, and 
        //  The new left will be finish frame's parent's field, the name of the field should be the same
      
        for(LocalAssign assign : outFinishScopeLocalAssign){
            //left redirected ff frame's parent
            //right this current frame's parent
            Name name = assign.local().name().id();
            //then process the original local, and infact the reference is built
            FieldAssign fAssign = (FieldAssign) this.replaceLocalVarRefWithFieldAccess(assign, localDeclaredVar);
              
            Expr leftContainerRef = getFieldContainerRef(name, getUpFrame(), moveFfRef); //search the field from async's parent(finish)
            Expr moveAssign = synth.makeFieldToFieldAssign(compilerPos, leftContainerRef, name, fAssign.target(), name, xct);                    
            moveBodySynth.addStmt(xnf.Eval(compilerPos, moveAssign));  
        }
        
        //final processing 
        moveBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(job, this.getRefToDeclMap()).context(xct));
    }

    protected void genClassConstructor() throws SemanticException {        
        ConstructorSynth conSynth = wsynth.genClassConstructorType1Base(classSynth);
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        
        //process all the formals. Assign fields with formals
        Expr thisRef = getThisRef();
        for(Pair<Name, Type> formal: formals){
            Name formalName = formal.fst();
            Type formalType = formal.snd();
            
            Expr fRef = conSynth.addFormal(compilerPos, Flags.FINAL, formalType, formalName);
            //make a field access
            Stmt s = xnf.Eval(compilerPos, 
                              synth.makeFieldAssign(compilerPos, thisRef, formalName, fRef, xct));
            codeBlockSynth.addStmt(s);
        }
    }
    
    
    /**
     * This method will be called by localvartofieldaccess replacer
     * If a local var is found in async frame, but defined above async and within finish
     * it should be copied into the async frame as a formal
     * 
     * For a local var, it should be called only once. This is enforced by the cache in localvartofieldaccess
     * @param name
     * @param type
     */
    protected void addFormal(Name name, Type type){
        formals.add(new Pair<Name, Type>(name, type));
        //now create a field 
        classSynth.createField(compilerPos, name.toString(), type);;
        fieldNames.add(name);
        
    }
    
    
    
    /**
     * For all local assigns, need detect whether this local assign is to
     * a local var that is not in the finish scope.
     * If in this case, the local var need to be moved in the async frame
     * 
     * @param localAssign
     */
    public void addOutFinishScopeLocals(LocalAssign localAssign){
        outFinishScopeLocalAssign.add(localAssign);
    }
    
    
    /**
     * Detect whether the stmts in the block can be transformed in just the async frame.
     * If the stmts satisfy the following conditions, they could be transformed in the async frame
     *   1) all stmts are simple stmts,
     *   2) no concurrent construct, such as finish/async/when
     *   3) only contains one concurrent method call, and the call is not in a control flow, such as if, loop, block
     * 
     * Other wise, WS code gen will create a new regular frame to transform the stmts.
     * 
     * If the frame can be transformed in async frame, we could use pc field optimization tech.
     * 
     * @param block the code block to be analyzed
     * @return
     */
    protected boolean canInFrameTransform(Block block){
        boolean containsConcurrent = WSUtil.containsConcurrentConstruct(block);
        int concurrentCallNum = WSUtil.calcConcurrentCallNums(block, wts);
        
        if(containsConcurrent || concurrentCallNum > 1){
            return false;
        }
        
        if(concurrentCallNum == 0){
            return true; //no concurrent call and no concurrent construts;
        }
        
        //now it contains only one concurrent call, we must make sure it is not in a loop body or other control flows.
        for(Stmt s : block.statements()){
            CodePatternDetector.Pattern pattern = CodePatternDetector.detectAndTransform(s, wts);
            
            if(CodePatternDetector.isControlFlowPattern(pattern)){
                return false;
            }
        }
        return true; //the only concurrent call is not in a control flow.
    }
    
    
}
