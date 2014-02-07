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
import polyglot.ast.Binary;
import polyglot.ast.Branch;
import polyglot.ast.Case;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.SwitchBlock;
import polyglot.ast.SwitchElement;
import polyglot.types.SemanticException;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSUtil;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.util.synthesizer.SwitchSynth;

/**
 * @author Haichuan
 * 
 * Generate the switch class
 *
 */
public class WSSwitchClassGen extends WSRegularFrameClassGen {
    protected final Switch switchStmt;
    
    public WSSwitchClassGen(AbstractWSClassGen parent, Switch switchStmt) {
        super(parent, switchStmt,
              WSUtil.getSwitchClassName(parent.getClassName()));
        this.switchStmt = switchStmt;
    }
    
    
    
    /* This method is different to the regular frame's genTreeMethods,
     * The switch's transformation is very complex. Please refer the ppt design doc in X10 wiki
     * @see x10.compiler.ws.codegen.WSRegularFrameClassGen#genThreeMethods()
     */
    @Override
    protected void genMethods() throws SemanticException {        
        //now prepare the body synth
        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(switchStmt.position());
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(switchStmt.position());
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(switchStmt.position());
        
        //First get the original switch's expr
        Expr orgSwitchExpr = (Expr) this.replaceLocalVarRefWithFieldAccess(switchStmt.expr());
        
        //prepare fast/resume's first switch table
        Switch fastSwitch = switchStmt.expr(orgSwitchExpr);
        ArrayList<SwitchElement> fastSwitchElements = new ArrayList<SwitchElement>();
        //sPC = pc;
        Expr pcRef = wsynth.genPCRef(classSynth);
        NewLocalVarSynth sPCLocalSynth = resumeBodySynth.createLocalVar(switchStmt.position(), pcRef);
        Local sPCRef = sPCLocalSynth.getLocal();
        Switch resumePCSetSwitch = switchStmt.expr(orgSwitchExpr); 
        ArrayList<SwitchElement> resumePCSetSwitchElements = new ArrayList<SwitchElement>();
        
        //now prepare resume/back's switch synthesizer
        SwitchSynth resumeSwitchSynth = new SwitchSynth(xnf, xct, switchStmt.position(), sPCRef);
        SwitchSynth backSwitchSynth = new SwitchSynth(xnf, xct, switchStmt.position(), pcRef);        
        
        //start to go over all the switch's conditions, and process them
        int pcValue = -1;
        for(SwitchElement se : switchStmt.elements()){            
            if(se instanceof Case){ //add case to the switch directly
                fastSwitchElements.add(se);
                resumePCSetSwitchElements.add(se);
                continue;
            }
            SwitchBlock sb = (SwitchBlock)se;
            if(sb.statements().size() == 0){
                continue; //no statements, just continue;
            }
            
            //break processing;
            int sbStmtSize = sb.statements().size();
            Stmt lastStmt = sb.statements().get(sbStmtSize - 1);
            boolean containsBreak = false;
            if(lastStmt instanceof Branch
                    && ((Branch)lastStmt).kind() == Branch.BREAK){
                containsBreak = true;
                ArrayList<Stmt> newSBStmts = new ArrayList<Stmt>(sb.statements());
                newSBStmts.remove(sbStmtSize - 1);
                sb = xnf.SwitchBlock(sb.position(), newSBStmts); //new block without break;
            }
            
            //in this case, pc value should change
            pcValue++;
            //and need set one in the slowPCSetSwitchElements;
            Assign sPCAssign = (Assign) xnf.LocalAssign(compilerPos, sPCRef, Assign.ASSIGN, synth.intValueExpr(pcValue, compilerPos)).type(xts.Int());
            Stmt sPCAssignS = xnf.Eval(compilerPos, sPCAssign);
            ArrayList<Stmt> sPCChangeStmts = new ArrayList<Stmt>();
            sPCChangeStmts.add(sPCAssignS);
            //always need a break; other wise the sPC is not correct
            sPCChangeStmts.add(xnf.Break(compilerPos));
            resumePCSetSwitchElements.add(xnf.SwitchBlock(compilerPos, sPCChangeStmts));
            
            if(!WSUtil.isComplexCodeNode(sb, wts)){
                //simple codes, just do local to 
                sb = (SwitchBlock) replaceLocalVarRefWithFieldAccess(sb);
                List<Stmt> stmts = new ArrayList<Stmt>(sb.statements());
                if(containsBreak) {
                    stmts.add(xnf.Break(lastStmt.position()));
                }
                fastSwitchElements.add(xnf.SwitchBlock(sb.position(), stmts));
                resumeSwitchSynth.insertStatementInCondition(pcValue, xnf.SwitchBlock(sb.position(), stmts));
            }
            else{
                TransCodes transCodes = transBlock(sb, pcValue, WSUtil
                                                   .getBlockFrameClassName(className));
                //fast add to fast switch
                List<Stmt> fastSS = transCodes.getFastStmts();
                if(containsBreak) {
                    fastSS.add(xnf.Break(lastStmt.position()));
                }
                fastSwitchElements.add(xnf.SwitchBlock(sb.position(), fastSS));

                //now change the pc
                pcValue = transCodes.pcValue(); //should increase one;
                //slow add to slow
                resumeSwitchSynth.insertStatementsInCondition(pcValue, transCodes.getResumeStmts());

                
                //the second part of the slow
                if(containsBreak){
                    resumeSwitchSynth.insertStatementInCondition(pcValue, xnf.Break(lastStmt.position()));
                }
                else{ //Definitely need add a ";" in the next case condition, so that it could switch to there;
                    resumeSwitchSynth.insertStatementInCondition(pcValue, xnf.Empty(lastStmt.position()));
                }
                
                //back add to back
                {
                  //nothing
                } 
            }

        }
        
        //now formulate the fast switch and resumePCSetSwitch
        fastSwitch = fastSwitch.elements(fastSwitchElements);
        fastBodySynth.addStmt(fastSwitch);
        
        //then the resume
        resumePCSetSwitch = resumePCSetSwitch.elements(resumePCSetSwitchElements);
        //formulate the if;
        Expr checkSPCCond = xnf.Binary(compilerPos, sPCRef, Binary.EQ,
                   synth.intValueExpr(0, compilerPos)).type(xts.Boolean());
        
        Stmt ifStmt = xnf.If(compilerPos, checkSPCCond, resumePCSetSwitch);
        resumeBodySynth.addStmt(ifStmt);
        resumeBodySynth.addStmt(resumeSwitchSynth); //the switch 
        
        //finally the back
        backBodySynth.addStmt(backSwitchSynth);
    }
}
