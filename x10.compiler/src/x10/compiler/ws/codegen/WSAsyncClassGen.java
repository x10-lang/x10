package x10.compiler.ws.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import polyglot.util.Pair;
import x10.ast.Async;
import x10.ast.Block;
import x10.ast.Call;
import x10.ast.Eval;
import x10.ast.Expr;
import x10.ast.FieldAssign;
import x10.ast.LocalAssign;
import x10.ast.LocalDecl;
import x10.ast.Stmt;
import x10.compiler.ws.WSCodeGenerator;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.CodePatternDetector;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.types.ClassDef;
import x10.types.Flags;
import x10.types.Name;
import x10.types.SemanticException;
import x10.types.Type;
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
    
    public WSAsyncClassGen(AbstractWSClassGen parent, Async a) {
        //Note in building the tree, we use parentFinish as async frame's up frame
        super(parent, getFinishFrameOfAsyncFrame(parent),
                WSCodeGenUtility.getFAsyncStmtClassName(parent.getClassName()),
                parent.wts.asyncFrameType, a.body());
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

        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);

        Expr pcRef = synth.makeFieldAccess(compilerPos, getThisRef(), PC, xct);
        
        //resume & back's switch
        SwitchSynth resumeSwitchSynth = resumeBodySynth.createSwitchStmt(compilerPos, pcRef);
        SwitchSynth backSwitchSynth = backBodySynth.createSwitchStmt(compilerPos, pcRef);
        //Move method
        MethodSynth moveMSynth = classSynth.createMethod(compilerPos, MOVE.toString());
        moveMSynth.setFlag(Flags.PUBLIC);
        Expr moveFfRef = moveMSynth.addFormal(compilerPos, Flags.FINAL, wts.finishFrameType, "ff");
        CodeBlockSynth moveBodySynth = moveMSynth.getMethodBodySynth(compilerPos);

        HashSet<Name> localDeclaredVar = new HashSet<Name>(); //all locals with these names will not be replaced
        
        //first check whether the block contains concurrent construct, if it is, transform the whole as a regular frame
        boolean containsConcurrent = WSCodeGenUtility.containsConcurrentConstruct(codeBlock);
        int concurrentCallNum = WSCodeGenUtility.calcConcurrentCallNums(codeBlock, wts);
        
        //FIXME: still have problems, if there is a loop.
        //So only one situation; only have one top level call or assign call, need use pattern detector
        if(containsConcurrent || concurrentCallNum > 1){
            //if contains a async, finish, just create a new frame
            //if the concurrent calls' num > 1, just creat a new regular frame to handle
            
            AbstractWSClassGen childFrameGen = genChildFrame(wts.regularFrameType, codeBlock, WSCodeGenUtility.getBlockFrameClassName(getClassName()));
            TransCodes callCodes = this.genInvocateFrameStmts(1, childFrameGen);
            
            //now add codes to three path;
            fastBodySynth.addStmts(callCodes.first());
            resumeSwitchSynth.insertStatementsInCondition(0, callCodes.second());
            if(callCodes.third().size() > 0){ //only assign call has back
                backSwitchSynth.insertStatementsInCondition(0, callCodes.third());
                backSwitchSynth.insertStatementInCondition(0, xnf.Break(compilerPos));
            }
        }
        else{
            //transform code one by one
            //in this case, no more frame will be generated.
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
                case Call:
                    codes = transCall((Call)((Eval)s).expr(), prePcValue, localDeclaredVar);
                    break;
                case AssignCall:
                    codes = transAssignCall(((Eval)s), prePcValue, localDeclaredVar);
                    break;
                default:
                    System.err.println("[WS_ERR]Not support the following statements:");
                    s.prettyPrint(System.err);
                    System.err.println();
                    System.err.println("[WS_ERR]Please turn off WS Compilation");
                    System.exit(1);
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
            }
        }

        //After fast body, there should be a poll
        //upcast[_async,AsyncFrame](this).poll(worker);
        fastBodySynth.addStmt(genPollStmt());
               
        
      //move
      //get the assign expression, the left will be the right, and 
      //the new left will be finish frame's parent's field, the name of the field should be the same
      
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
                 
        fastBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));
        resumeBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));
        backBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));     
        moveBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));
    } 
    
    protected Stmt genPollStmt() throws SemanticException{
        //fast path: //upcast[_async,AsyncFrame](this).poll(worker);
        
        Expr upThisExpr = genUpcastCall(getClassType(), wts.asyncFrameType, getThisRef());
        
        InstanceCallSynth icSynth = new InstanceCallSynth(xnf, xct, compilerPos, upThisExpr, POLL.toString());
        Expr fastWorkerRef = fastMSynth.getMethodBodySynth(compilerPos).getLocal(WORKER.toString());
        icSynth.addArgument(wts.workerType, fastWorkerRef);        
        return icSynth.genStmt();
    }

    protected void genClassConstructor() throws SemanticException {        
        //now generate another constructor
        /* 
           @Inline def this(up:Frame!) {
               super(up, up);
           }
        */
        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, wts.frameType, "up"); //up:Frame!
        
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        SuperCallSynth superCallSynth = codeBlockSynth.createSuperCall(compilerPos, classSynth.getClassDef());
        superCallSynth.addArgument(wts.frameType, upRef);
        
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
    
}
