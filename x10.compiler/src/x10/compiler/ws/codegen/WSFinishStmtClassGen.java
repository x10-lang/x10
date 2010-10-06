package x10.compiler.ws.codegen;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import x10.ast.Finish;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.types.X10ClassType;
import x10.util.synthesizer.ClassSynth;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.SuperCallSynth;
import x10.util.synthesizer.SwitchSynth;

/**
 * @author Haichuan
 * 
 * Transform a finish stmt to a inner class
 * 
 */
public class WSFinishStmtClassGen extends AbstractWSClassGen {

    protected Finish finish;

    public WSFinishStmtClassGen(AbstractWSClassGen parent, Finish finish) {
        super(parent.job, parent.getX10NodeFactory(), parent.getX10Context(), parent.getWSTransformState(),
              parent);

        this.finish = finish;

        // className like "_fib_finish_1";
        className = WSCodeGenUtility.getFinishStmtClassName(parent.getClassName())
                    + parent.assignChildId();
        classSynth = new ClassSynth(job, xnf, xct, wts.finishFrameType, className);
        classSynth.setKind(ClassDef.MEMBER);
        //note the flag should according to the method's type
        ClassDef classDef = parent.classSynth.getClassDef();
        classSynth.setFlags(classDef.flags());    
        classSynth.setKind(classDef.kind());
        classSynth.setOuter(parent.classSynth.getOuter());
        this.frameDepth = parent.frameDepth + 1;
        
        addPCFieldToClass();        
        //now prepare all kinds of method synthesizer
        prepareMethodSynths();
    }

    @Override
    public void genClass() throws SemanticException {

        genClassConstructors();

        if (wts.realloc) genRemapMethod();
        
        genThreeMethods(); //fast, resume, back
    }

    protected void genClassConstructors() throws SemanticException {
        //finishstmt class doesn't have fields
        //gen copy constructor
        if (wts.realloc) genCopyConstructor(compilerPos);
        
        //now generate another con
        ConstructorSynth conSynth = classSynth.createConstructor(compilerPos);
        conSynth.addAnnotation(genHeaderAnnotation());
        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, wts.frameType, "up"); //up:Frame!
        
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        SuperCallSynth superCallSynth = codeBlockSynth.createSuperCall(compilerPos, classSynth.getClassDef());
        superCallSynth.addArgument(wts.frameType, upRef);
    }

    protected void genThreeMethods() throws SemanticException {
        
        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        
        Expr pcRef = synth.makeFieldAccess(compilerPos, getThisRef(), PC, xct);
        
        SwitchSynth resumeSwitchSynth = resumeBodySynth.createSwitchStmt(compilerPos, pcRef);
        SwitchSynth backSwitchSynth = backBodySynth.createSwitchStmt(compilerPos, pcRef);        
        
        //
        Block finishBody;
        if(finish.body() instanceof Block){
            finishBody = (Block) finish.body();
        }
        else{
            finishBody = xnf.Block(finish.body().position(), finish.body());
        }
        
        AbstractWSClassGen childFrameGen = genChildFrame(wts.regularFrameType, finishBody, WSCodeGenUtility.getBlockFrameClassName(getClassName()));
        TransCodes callCodes = this.genInvocateFrameStmts(1, childFrameGen);
        
        //now add codes to three path;
        fastBodySynth.addStmts(callCodes.first());
        resumeSwitchSynth.insertStatementsInCondition(0, callCodes.second());
        if(callCodes.third().size() > 0){ //only assign call has back
            backSwitchSynth.insertStatementsInCondition(0, callCodes.third());
            backSwitchSynth.insertStatementInCondition(0, xnf.Break(compilerPos));
        }
   }

}
