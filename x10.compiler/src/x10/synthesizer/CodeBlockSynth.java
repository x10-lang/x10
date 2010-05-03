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
package x10.synthesizer;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.types.ClassDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import x10.ast.X10NodeFactory;
import x10.types.X10Context;

/**
 * Synthesizer to construct a code block
 *
 */
public class CodeBlockSynth extends AbstractStateSynth{

    
    //inner class for directly inserted stmt
    class SimpleStmtSynth implements IStmtSynth{
        Stmt stmt;
        SimpleStmtSynth(Stmt stmt){
            this.stmt = stmt;
        }
        public Stmt genStmt(Position pos) throws SemanticException {
            return (Stmt) stmt.position(pos);
        }
        public Stmt genStmt() throws SemanticException {
            return stmt;
        }
    }
    
    List<IStmtSynth> stmtSythns; //all synthesizers for generate the code block
    
    public CodeBlockSynth(X10NodeFactory xnf, X10Context xct, Position pos) {
        super(xnf, xct, pos);
        this.pos = pos;
        stmtSythns = new ArrayList<IStmtSynth>();
    }

    public void addStmt(Stmt stmt) {
        stmtSythns.add(new SimpleStmtSynth(stmt));
    }

    public void addStmts(List<Stmt> stmts) {
        for(Stmt stmt : stmts){
            addStmt(stmt);
        }
    }
    
    public void addStmtSynth(IStmtSynth iss) {
        stmtSythns.add(iss);
    }


    public InstanceCallSynth createInstanceCallStmt(Position pos, Receiver insRef, String methodName) {
        InstanceCallSynth synth = new InstanceCallSynth(xnf, xct, pos, insRef, methodName);
        addStmtSynth(synth);
        return synth;
    }

    public NewInstanceSynth createNewInstaceStmt(Position pos, Type classType) {
        NewInstanceSynth synth = new NewInstanceSynth(xnf, xct, pos, classType);
        addStmtSynth(synth);
        return synth;
    }

    public SwitchSynth createSwitchStmt(Position pos, Expr switchCond) {
        SwitchSynth synth = new SwitchSynth(xnf, xct, pos, switchCond);
        addStmtSynth(synth);
        return synth;
    }

    public NewLocalVarSynth createLocalVar(Position pos, Expr initializer) {
        NewLocalVarSynth synth = new NewLocalVarSynth(xnf, xct, pos, initializer);
        addStmtSynth(synth);
        return synth;
    }
    
    public SuperCallSynth createSuperCall(Position pos, ClassDef classDef){
        SuperCallSynth synth = new SuperCallSynth(xnf, xct, pos, classDef);
        addStmtSynth(synth);
        return synth;
    }

    
    public Block genCodeGen() throws SemanticException {
        ArrayList<Stmt> stmts = new ArrayList<Stmt>();
        for(IStmtSynth iss : stmtSythns){
            stmts.add(iss.genStmt());
        }        
        return xnf.Block(pos, stmts);
    }

    
}
