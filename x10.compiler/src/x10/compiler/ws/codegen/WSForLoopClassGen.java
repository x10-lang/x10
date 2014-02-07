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
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.LocalDecl;
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
 * Generate the for loop as one frame class
 *
 */
public class WSForLoopClassGen extends WSRegularFrameClassGen {
    protected final For forStmt;
    
    public WSForLoopClassGen(AbstractWSClassGen parent, For forStmt) {
        super(parent, forStmt.body(),
              WSUtil.getLoopClassName(parent.getClassName()));
        this.forStmt = forStmt;
    }
    
    
    
    /* This method is different to the regular frame's genTreeMethods,
     * since for loop will transform the for into while in slow path
     * @see x10.compiler.ws.codegen.WSRegularFrameClassGen#genThreeMethods()
     */
    @Override
    protected void genMethods() throws SemanticException {
        
        //Firstly process all the initial, update, condition
        List<ForInit> forInits = new ArrayList<ForInit>();
        for(ForInit fi : forStmt.inits()){
            //in fact, the update could be an method call or something;
            if(fi instanceof LocalDecl){
                fi = (ForInit) transLocalDecl((LocalDecl)fi);
            }
            if(fi instanceof Eval){
                fi = (ForInit) this.replaceLocalVarRefWithFieldAccess(fi);
            }
            if(fi != null){
                forInits.add(fi);
            }
        }
        
        Expr forCond = (Expr) this.replaceLocalVarRefWithFieldAccess(forStmt.cond());
        
        List<ForUpdate> forUpdates = new ArrayList<ForUpdate>();
        for(ForUpdate fu : forStmt.iters()){
            //in fact, the update could be an method call or something;
            forUpdates.add((ForUpdate) replaceLocalVarRefWithFieldAccess(fu)); 
        }
        
        
        //then transform the whole body
        Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> bodyCodes = transformMethodBody();
        
        //the results are just the bodies of fast/slow/back
        CodeBlockSynth fastForBlockSynth = bodyCodes.first();
        SwitchSynth resumeSwitchSynth = bodyCodes.second();
        SwitchSynth backSwitchSynth = bodyCodes.third();
        
        //now prepare the body synth
        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        
        Expr pcRef = wsynth.genPCRef(classSynth);
        
        
        //now build the whole bodies
        { //fast
            For fastFS = forStmt.inits(forInits);
            fastFS = fastFS.cond(forCond);
            fastFS = fastFS.iters(forUpdates);
            fastFS = fastFS.body(fastForBlockSynth.close());
            fastBodySynth.addStmt(fastFS);
        }
        {  //slow
            
            //Step 0 - Create the while's body
            CodeBlockSynth whileBodySynth = new CodeBlockSynth(xnf, xct, compilerPos);
            //prepare a boolean breaked Flag; 
            //In after each loop, the breaked will be set to false; And it is not set to false, it will breaked;
            NewLocalVarSynth breakedFlagSynth = whileBodySynth.createLocalVar(compilerPos, synth.booleanValueExpr(true, compilerPos)); 
            
            
            //Step 1 - create the switch statement
            //get the current pcValue
            ArrayList<Integer> switchTable = resumeSwitchSynth.getSwitchTable();
            int pcValue = switchTable.get(switchTable.size() - 1);
            //special process, if the pcValue == 0, means there is no additional statements after the concurrent frame call
            //we need improve the pcValue to 1
            pcValue = pcValue == 0 ? 1 : pcValue;
            
            //slow transform into a while
            //form while's body
            SwitchSynth whileSwitch = new SwitchSynth(xnf, xct, compilerPos, pcRef);
            
            //now insert condition on
            for(ForInit fi : forInits){
                whileSwitch.insertStatementInCondition(0, fi);
            }
            //now change original condition
            //in fact, the condition should be put in -1
            Expr returnCondition = xnf.Unary(compilerPos, forCond, Unary.NOT).type(xts.Boolean());
            Stmt returnCheck = xnf.If(compilerPos, returnCondition, xnf.Return(compilerPos));
            //now switch the original condition, and add a return
            whileSwitch.insertStatementInCondition(-1, returnCheck);
            
            whileSwitch.insertStatementsInCondition(-1, resumeSwitchSynth.getStmtsInCondtion(0));
            //now processing all other cases' codes
            for(int i : resumeSwitchSynth.getSwitchTable()){
                if(i == 0){
                    continue; //has processed
                }
                whileSwitch.insertStatementsInCondition(i, resumeSwitchSynth.getStmtsInCondtion(i));
                pcValue = i; //assign the value to pcValue;
            }
            
            //finally process the original update and condition
            for(ForUpdate fu : forUpdates){
                whileSwitch.insertStatementInCondition(pcValue, fu);
            }

            
            //now assign the pc ref to -1;
            Stmt pcChange = wsynth.genPCAssign(classSynth, -1);
                                     
            whileSwitch.insertStatementInCondition(pcValue, pcChange);
            //And set the breaked flag as false
            Stmt breakedFalse = xnf.Eval(compilerPos, xnf.LocalAssign(compilerPos, breakedFlagSynth.getLocal(), Assign.ASSIGN,
                                                                      synth.booleanValueExpr(false, compilerPos)).type(breakedFlagSynth.getLocal().type()));
            whileSwitch.insertStatementInCondition(pcValue, breakedFalse);
            
            //Step 2: - Create the final break check, and add all into while bodySynth
            whileBodySynth.addStmt(whileSwitch);
            //if(breakedFlag) break;
            //Expr breakedCondition = xnf.Binary(compilerPos, returnFlagRef, Binary.EQ, synth.booleanValueExpr(true, compilerPos)).type(xts.Boolean());
            Stmt breakedCheck = xnf.If(compilerPos, breakedFlagSynth.getLocal().type(xts.Boolean()), xnf.Break(compilerPos));
            whileBodySynth.addStmt(breakedCheck);
            
            While slowWhile = xnf.While(compilerPos, synth.booleanValueExpr(true, compilerPos), whileBodySynth.genStmt());
            resumeBodySynth.addStmt(slowWhile);
        }
        {
            backBodySynth.addStmt(backSwitchSynth.genStmt());
        }
    }

}
