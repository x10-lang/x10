package x10.compiler.ws.codegen;

import x10.ast.Block;
import x10.ast.Expr;
import x10.ast.Finish;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.types.ClassDef;
import x10.types.Flags;
import x10.types.SemanticException;
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
    public WSFinishStmtClassGen(AbstractWSClassGen parent, Finish finishStmt) {
        super(parent, parent,
                WSCodeGenUtility.getFinishStmtClassName(parent.getClassName()),
                parent.wts.finishFrameType, finishStmt.body());
    }

    protected void genClassConstructor() throws SemanticException {
        Expr upRef = conSynth.addFormal(compilerPos, Flags.FINAL, wts.frameType, "up"); //up:Frame!
        
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        SuperCallSynth superCallSynth = codeBlockSynth.createSuperCall(compilerPos, classSynth.getClassDef());
        superCallSynth.addArgument(wts.frameType, upRef);
    }

    @Override
    protected void genMethods() throws SemanticException {
        
        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        
        Expr pcRef = synth.makeFieldAccess(compilerPos, getThisRef(), PC, xct);
        
        SwitchSynth resumeSwitchSynth = resumeBodySynth.createSwitchStmt(compilerPos, pcRef);
        SwitchSynth backSwitchSynth = backBodySynth.createSwitchStmt(compilerPos, pcRef);        
        
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

}
