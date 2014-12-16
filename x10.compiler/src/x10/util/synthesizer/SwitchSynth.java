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
package x10.util.synthesizer;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Case;
import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.SwitchBlock;
import polyglot.ast.SwitchElement;
import polyglot.util.Position;
import polyglot.types.Context;

/**
 * The first try a state synthesizer for Switch statements
 * Currently, it only supports IntLit as case condition.
 * Later version should support all NumLit case condition.
 */
public class SwitchSynth extends AbstractStateSynth implements IStmtSynth{
    Expr switchCond;
    
    
    ArrayList<Integer> switchTable; //store the switch condition
    ArrayList<List<Stmt>> switchBlockTable; //store the statements under the switch table
    
    List<Stmt> defaultStmts;
    
    public SwitchSynth(NodeFactory xnf, Context context, Position pos, Expr switchCond){
        super(xnf, context, pos);
        this.switchCond = switchCond;
        
        
        switchTable = new ArrayList<Integer>();
        switchBlockTable = new ArrayList<List<Stmt>>();
    }
    
    public void insertStatementsInCondition(int cond, List<Stmt> stmts){
        for(Stmt s : stmts){
            insertStatementInCondition(cond, s);
        }
    }
    
    /**
     * Will insert the statement in the condition( case cond:)
     * If there is no such a condition, just create it, and insert it according to the order
     * All statements will be inserted one by one
     * @param cond
     * @param stmt
     */
    public void insertStatementInCondition(int cond, Stmt stmt){
        //first look for the switch block
        List<Stmt> stmts = null;   
        for(int i = 0; i < switchTable.size(); i++){
            int c = switchTable.get(i);
            if(c == cond){
                stmts = switchBlockTable.get(i);
                break;
            }
//            if( c > cond){
//                //in order to make it clear, the condition should be inserted here
//                stmts = new ArrayList<Stmt>();
//                switchTable.add(i, cond);
//                switchBlockTable.add(i, stmts);
//                break;
//            }
        }
        
        if(stmts == null){
            stmts = new ArrayList<Stmt>();
            switchTable.add(cond);
            switchBlockTable.add(stmts);
        }
        stmts.add(stmt);
    }
    
    /**
     * Insert stmts into the switch's default condition
     * @param stmts
     */
    public void insertStatementsInDefault(List<Stmt> stmts){

        for(Stmt stmt : stmts){
            insertStatementInDefault(stmt);
        }
    }
    
    /**
     * Insert one stmt into the switch's default condition
     * @param stmt
     */
    public void insertStatementInDefault(Stmt stmt){
        if(defaultStmts == null){
            defaultStmts = new ArrayList<Stmt>();
        }
        
        defaultStmts.add(stmt);
    }
    
    
    
    
    public ArrayList<Integer> getSwitchTable() {
        return switchTable;
    }

    public List<Stmt> getStmtsInCondtion(int i) {
        if(switchBlockTable.size() > i){
            return switchBlockTable.get(i);            
        }
        else{
            return new ArrayList<Stmt>(); //return null;
        }
    }

    public Switch genStmt(){
        ArrayList<SwitchElement> switchElements = new ArrayList<SwitchElement>();
        //iterate all switch block and statements table to insert it
        
        for(int i = 0; i < switchTable.size(); i++){
            int cond = switchTable.get(i);
            List<Stmt> stmts = switchBlockTable.get(i);
            
            Expr caseExpr = xnf.IntLit(pos, IntLit.INT, cond).type(xts.Int());
            Case ca = xnf.Case(pos, caseExpr);
            SwitchBlock swb =xnf.SwitchBlock(pos, stmts);
            switchElements.add(ca);
            switchElements.add(swb);
        }
        
        if(defaultStmts != null && defaultStmts.size() > 0){
            switchElements.add(xnf.Default(pos));
            switchElements.add(xnf.SwitchBlock(pos, defaultStmts));
        }
        
        Switch sw = xnf.Switch(pos,
                               switchCond,  switchElements);
        return sw;
    }
}
