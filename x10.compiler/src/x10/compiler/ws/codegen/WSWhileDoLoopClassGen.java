/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */


package x10.compiler.ws.codegen;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Do;
import polyglot.ast.Expr;
import polyglot.ast.Loop;
import polyglot.ast.Stmt;
import polyglot.ast.Unary;
import polyglot.ast.While;
import polyglot.types.SemanticException;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.Triple;
import x10.compiler.ws.util.WSUtil;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.util.synthesizer.SwitchSynth;

/**
 * @author Haichuan
 * 
 * Generate the while & do loop as one frame class
 *
 */
public class WSWhileDoLoopClassGen extends WSRegularFrameClassGen {
    protected final Loop loopStmt;
    
    public WSWhileDoLoopClassGen(AbstractWSClassGen parent, Loop loopStmt) {
        super(parent, loopStmt.body(),
              WSUtil.getLoopClassName(parent.getClassName()));
        this.loopStmt = loopStmt;
    }
    
    
    
    /* This method is different to the regular frame's genTreeMethods,
     * since loop will transform the loop into while in slow path
     * @see x10.compiler.ws.codegen.WSRegularFrameClassGen#genThreeMethods()
     */
    @Override
    protected void genMethods() throws SemanticException {
        Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> bodyCodes = transformMethodBody();
        
        //the results are just the bodies of fast/slow/back
        CodeBlockSynth fastLoopBlockSynth = bodyCodes.first();
        SwitchSynth resumeSwitchSynth = bodyCodes.second();
        SwitchSynth backSwitchSynth = bodyCodes.third();
        
        //now prepare the body synth
        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        
        //now processing all others about the loop
        boolean isCondInFront = loopStmt instanceof While; //while's condition at the beginning,
        Expr loopCondExpr = (Expr)this.replaceLocalVarRefWithFieldAccess(loopStmt.cond());
        
        Expr pcRef = wsynth.genPCRef(classSynth);
 
        
        //now build the whole bodies
        { //fast
            if(isCondInFront){ //
                While fastWhile =  (While)loopStmt;
                fastWhile = fastWhile.cond(loopCondExpr);
                fastWhile = fastWhile.body(fastLoopBlockSynth.close());
                fastBodySynth.addStmt(fastWhile);
            }
            else{
                Do fastDo = (Do)loopStmt;
                fastDo = fastDo.cond(loopCondExpr);
                fastDo = fastDo.body(fastLoopBlockSynth.close());
                fastBodySynth.addStmt(fastDo);
            }
        }
        {   //resume
            //just maintain as is switch condition,
            //if while: condition check is in pc = 1; 
            //if do: condition check at the final
            
            //Step 0 - Create the while's body
            CodeBlockSynth whileBodySynth = new CodeBlockSynth(xnf, xct, compilerPos);
            //prepare a boolean breaked Flag; 
            //In after each loop, the breaked will be set to false; And it is not set to false, it will breaked;
            NewLocalVarSynth breakedFlagSynth = whileBodySynth.createLocalVar(compilerPos, synth.booleanValueExpr(true, compilerPos)); 
 
            //Step 1 - prepare the return conditional check
            Expr returnCondition = xnf.Unary(compilerPos, loopCondExpr, Unary.NOT).type(xts.Boolean());
            Stmt returnCheck = xnf.If(compilerPos, returnCondition, xnf.Return(compilerPos));
            
            //slow transform into a while
            //form while's body
            SwitchSynth whileSwitch = new SwitchSynth(xnf, xct, compilerPos, pcRef);
            
            if(isCondInFront){
                whileSwitch.insertStatementInCondition(0, returnCheck);
            }
            //copy all switch table;
            int pcValue = 0;
            for(int i : resumeSwitchSynth.getSwitchTable()){
                whileSwitch.insertStatementsInCondition(i, resumeSwitchSynth.getStmtsInCondtion(i));
                pcValue = i; //assign the value to pcValue;
            }
            
            //special process, in case only one switch table item
            pcValue = (pcValue == 0) ? 1 : pcValue;
            
            if(!isCondInFront){
                whileSwitch.insertStatementInCondition(pcValue, returnCheck);
            }
            Stmt pcChange = wsynth.genPCAssign(classSynth, 0);
            whileSwitch.insertStatementInCondition(pcValue, pcChange);
            //And set the breaked flag as false
            Stmt breakedFalse = xnf.Eval(compilerPos, xnf.LocalAssign(compilerPos, breakedFlagSynth.getLocal(), Assign.ASSIGN,
                                                                      synth.booleanValueExpr(false, compilerPos).type(breakedFlagSynth.getLocal().type())));
            whileSwitch.insertStatementInCondition(pcValue, breakedFalse);
            
            //Step 2: - Create the final breaked check, and add all into while bodySynth
            whileBodySynth.addStmt(whileSwitch);
            //if(breakedFlag) break;
            //Expr breakedCondition = xnf.Binary(compilerPos, returnFlagRef, Binary.EQ, synth.booleanValueExpr(true, compilerPos)).type(xts.Boolean());
            Stmt breakedCheck = xnf.If(compilerPos, breakedFlagSynth.getLocal().type(xts.Boolean()), xnf.Break(compilerPos));
            whileBodySynth.addStmt(breakedCheck);
            
            While resumeWhile = xnf.While(compilerPos, synth.booleanValueExpr(true, compilerPos), whileBodySynth.genStmt());
            resumeBodySynth.addStmt(resumeWhile);
        }
        {
            backBodySynth.addStmt(backSwitchSynth.genStmt());
        }        
    }

}
