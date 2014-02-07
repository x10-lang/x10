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

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.ast.Finish;
import x10.ast.FinishExpr;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSUtil;
import x10.types.ConstrainedType;
import x10.types.X10ParsedClassType;
import x10.util.synthesizer.CodeBlockSynth;
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
    
    Type reducerBaseType; //only used for collecting-finish frame
    boolean isCollectingFinishFrame;
    //For FinishStmt
    public WSFinishStmtClassGen(AbstractWSClassGen parent, Finish finishStmt) {
        super(parent, parent,
                WSUtil.getFinishStmtClassName(parent.getClassName()),
                parent.xts.FinishFrame(), finishStmt.body());
        
        if(!wts.OPT_PC_FIELD){
            wsynth.createPCField(classSynth);
        }
    }
    
    //For FinishExpr
    public WSFinishStmtClassGen(AbstractWSClassGen parent, Type reducerBaseType, FinishExpr finishExpr) {
        super(parent, parent,
                WSUtil.getFinishStmtClassName(parent.getClassName()),
                parent.xts.CollectingFinish().typeArguments(Collections.singletonList(reducerBaseType)),
                finishExpr.body());
        
        if(!wts.OPT_PC_FIELD){
            wsynth.createPCField(classSynth);
        }
        //now start extract the collecting finish's type.
        this.reducerBaseType = reducerBaseType;
        assert(reducerBaseType != null);
        isCollectingFinishFrame = true;
    }

    protected void genClassConstructor() throws SemanticException {
        if(isCollectingFinishFrame){
            wsynth.genClassConstructorType3Base(classSynth, getReducerBaseType());            
        }
        else{
            wsynth.genClassConstructorType1Base(classSynth);            
        }
    }
    
    public Type getReducerBaseType() {
        return reducerBaseType;
    }

    @Override
    protected void genMethods() throws SemanticException {
        
        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        
        AbstractWSClassGen childFrameGen = genChildFrame(xts.RegularFrame(), codeBlock, WSUtil.getBlockFrameClassName(getClassName()));
        List<Stmt> fastCallCodes = wsynth.genInvocateFrameStmts(1, classSynth, fastMSynth, childFrameGen);
        
        if(wts.DISABLE_EXCEPTION_HANDLE){
            fastBodySynth.addStmts(fastCallCodes);
        }
        else{
            fastBodySynth.addStmt(wsynth.genExceptionHandler(fastCallCodes, classSynth));
            fastBodySynth.addStmt(wsynth.genRethrowStmt(classSynth));
        }
        
        //resume/back path
        if(!wts.OPT_PC_FIELD){
            Expr pcRef = wsynth.genPCRef(classSynth);
            CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
            SwitchSynth resumeSwitchSynth = resumeBodySynth.createSwitchStmt(compilerPos, pcRef);     
            List<Stmt> resumeCallCodes = wsynth.genInvocateFrameStmts(1, classSynth, resumeMSynth, childFrameGen);
            
            if(wts.DISABLE_EXCEPTION_HANDLE){
                resumeSwitchSynth.insertStatementsInCondition(0, resumeCallCodes);
            }
            else{
                resumeSwitchSynth.insertStatementInCondition(0, wsynth.genExceptionHandler(resumeCallCodes, classSynth));
                resumeSwitchSynth.insertStatementInCondition(0, wsynth.genRethrowStmt(classSynth));
            }
        }
    }

    
}
