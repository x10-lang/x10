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

import polyglot.ast.Block;
import polyglot.ast.Catch;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
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
    public WSFinishStmtClassGen(AbstractWSClassGen parent, Finish finishStmt) {
        super(parent, parent,
                WSCodeGenUtility.getFinishStmtClassName(parent.getClassName()),
                parent.wts.finishFrameType, finishStmt.body());
        
        if(WSOptimizeConfig.OPT_PC_FIELD == 0){
            addPCField();
        }
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
        

        AbstractWSClassGen childFrameGen = genChildFrame(wts.regularFrameType, codeBlock, WSCodeGenUtility.getBlockFrameClassName(getClassName()));
        TransCodes callCodes = this.genInvocateFrameStmts(1, childFrameGen);
        
        //now add codes to three path;
        //fast path
        Name formalName = xct.getNewVarName();
        
        Formal fa = synth.createFormal(compilerPos, wts.stolenType, formalName, Flags.NONE);
        Stmt ea = xnf.Throw(compilerPos, xnf.Local(compilerPos, xnf.Id(compilerPos, formalName)).localInstance(fa.localDef().asInstance()).type(wts.stolenType));
        Catch ca = xnf.Catch(compilerPos, fa, xnf.Block(compilerPos, ea));
        
        Formal f = synth.createFormal(compilerPos, xts.Throwable(), formalName, Flags.NONE);
        Expr caught = synth.makeInstanceCall(compilerPos, synth.thisRef(wts.finishFrameType, compilerPos),
                CAUGHT, Collections.<TypeNode>emptyList(), Collections.<Expr>singletonList(
                        xnf.Local(compilerPos, xnf.Id(compilerPos, formalName)).localInstance(f.localDef().asInstance()).type(xts.Throwable())), xts.Void(),
                Collections.<Type>singletonList(xts.Throwable()), xct);
        Catch c = xnf.Catch(compilerPos, f, xnf.Block(compilerPos,
                xnf.Eval(compilerPos, caught)));
        
        List<Catch> handlers = new ArrayList<Catch>(2);
        handlers.add(ca);
        handlers.add(c);
        
        Try t = xnf.Try(compilerPos, xnf.Block(compilerPos, callCodes.first()), handlers);
        fastBodySynth.addStmt(t);
        fastBodySynth.addStmt(genRethrowStmt());
        
        //resume/back path
        if(WSOptimizeConfig.OPT_PC_FIELD == 0){
            Expr pcRef = synth.makeFieldAccess(compilerPos, getThisRef(), PC, xct);
            
            SwitchSynth resumeSwitchSynth = resumeBodySynth.createSwitchStmt(compilerPos, pcRef);
            SwitchSynth backSwitchSynth = backBodySynth.createSwitchStmt(compilerPos, pcRef);      
            resumeSwitchSynth.insertStatementsInCondition(0, callCodes.second());
            if(callCodes.third().size() > 0){ //only assign call has back
                backSwitchSynth.insertStatementsInCondition(callCodes.getPcValue(), callCodes.third());
                backSwitchSynth.insertStatementInCondition(callCodes.getPcValue(), xnf.Break(compilerPos));
            }
        }
   }

    protected Stmt genRethrowStmt() throws SemanticException{
        //fast path: //upcast[_async,AsyncFrame](this).poll(worker);
        
        InstanceCallSynth icSynth = new InstanceCallSynth(xnf, xct, compilerPos, getThisRef(), RETHROW.toString());
        return icSynth.genStmt();
    }
}
