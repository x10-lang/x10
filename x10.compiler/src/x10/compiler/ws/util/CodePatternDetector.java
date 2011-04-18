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


package x10.compiler.ws.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Catch;
import polyglot.ast.Do;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.If;
import polyglot.ast.LocalDecl;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Term;
import polyglot.ast.Try;
import polyglot.ast.While;
import polyglot.types.MethodDef;

import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Finish;
import x10.ast.FinishExpr;
import x10.ast.ForLoop;
import x10.ast.When;
import x10.compiler.ws.WSTransformState;
import polyglot.types.Context;
import polyglot.types.TypeSystem;

/**
 * @author Haichuan
 * 
 * X10 Work-Stealing can only translate the following patterns directly
 *    finish S  => finish frame
 *    async S => async frame
 *    aVar = async S => translate to async{ aVar = S }, and need move aVar in async frame
 *    foo() or a.foo() => invocation to the foo()'s WS wrapper method
 *    aVar = foo() or aVar = a.foo(); => invocation to the foo()'s WS wrapper, 
 *                                       and move results
 * 
 * And for local decl, 
 *    val s; => Just create an inner class field
 *    val s = foo(); => create a inner class field and return trans assign call;
 * 
 * Besides the previous directly translation, other statements if they contain one of above 
 * code pattern need to be translated.
 * 
 * Category I: control flows, such as return, if, for forloop, while, do while, switch
 *    These statements will be processed by specific frame generation code
 *    
 * Category II: compound statements, 
 *     e.g. Eval: a = foo(abc()); a = foo() + abc();
 *     e.g. If: if's condition
 *     e.g. for forloop, while, swtich's condition, updates, and initial codes contains target code
 *     e.g. return's result contains target content
 * 
 */
public class CodePatternDetector {
    public static enum Pattern{ Finish,
                  FinishAssign, //used in collecting finish
                  Async,
                  At, // at(p) S
                  AsyncAt, // async at(p) S
                  When,
                  LocalDecl, //local declare with the initializer is concurrent call
                  Call, //only the first level call is target call;
                  AssignCall, //only the first level call is target call;
                  If,
                  For,
                  ForLoop,
                  While,
                  DoWhile,
                  Switch,
                  Block,
                  Try,
                  Compound, //need flatten
                  Simple, 
                  Unsupport, // async, future's place is not here
                  };
                  
    /**
     * Check an input statement and return the corresponding restored statement
     * 
     * 
     * @param stmt
     * @return
     */
    public static Pattern detectAndTransform(final Stmt stmt, final WSTransformState wts){
        
        if(!WSUtil.isComplexCodeNode(stmt, wts)){
            return Pattern.Simple;
        }
        
        if(stmt instanceof LocalDecl){
            return Pattern.LocalDecl;
        }
        
        if(stmt instanceof Async){
            Stmt asyncBodyStmt = WSUtil.unrollToOneStmt(((Async)stmt).body());
            if(asyncBodyStmt instanceof AtStmt){
                return Pattern.AsyncAt; //async at one place
            }
            else{           
                return Pattern.Async;
            }
        }
        
        if(stmt instanceof AtStmt){
            return Pattern.At;
        }
        
        if(stmt instanceof Finish){
            return Pattern.Finish;
        }
        
        if(stmt instanceof When){
            When w = (When)stmt;
            if(WSUtil.isComplexCodeNode(w.expr(), wts)){
                //need flatten
                return Pattern.Compound;
            }
            else{
                //just normal when
                return Pattern.When;
            }
        }
        
        if(stmt instanceof Eval){
            return detectEval(stmt, wts);
        }
        
        //next if
        if(stmt instanceof If){
            If ifStmt = (If)stmt;
            if(WSUtil.isComplexCodeNode(ifStmt.cond(), wts)){
                //need flatten
                return Pattern.Compound;
            }
            else{
                //just normal if
                return Pattern.If;
            }
        }
        
        //next For
        if(stmt instanceof For){
            For forStmt = (For)stmt;
            List<Term> condTerms = new ArrayList<Term>();
            condTerms.addAll(forStmt.inits());
            condTerms.addAll(forStmt.iters());
            condTerms.add(forStmt.cond());
            
            boolean compoundS = false;
            for(Term t : condTerms){
                if(WSUtil.isComplexCodeNode(t, wts)){
                    compoundS = true;
                    break;
                }
            }
            
            if(compoundS){
                return Pattern.Compound;
            }
            else{
                return Pattern.For;
            }
        }
        
        if(stmt instanceof ForLoop){
            ForLoop forloopStmt = (ForLoop)stmt;
            
            if(WSUtil.isComplexCodeNode(forloopStmt.domain(), wts)){
                return Pattern.Compound;
            }
            else{
                return Pattern.ForLoop;
            }
        }
        
        if(stmt instanceof While){
            While whileStmt = (While)stmt;
            if(WSUtil.isComplexCodeNode(whileStmt.cond(), wts)){
                return Pattern.Compound;
            }
            else{
                return Pattern.While;
            }
        }
        
        if(stmt instanceof Do){
            Do doStmt = (Do)stmt;
            if(WSUtil.isComplexCodeNode(doStmt.cond(), wts)){
                return Pattern.Compound;
            }
            else{
                return Pattern.DoWhile;
            }
        }
        
        if(stmt instanceof Switch){
            Switch ss = (Switch)stmt;
            if(WSUtil.isComplexCodeNode(ss.expr(), wts)){
                return Pattern.Compound;
            }
            else{
                return Pattern.Switch;
            }
        }

        if(stmt instanceof Return){ //the return's expr must be complex
            Return rStmt = (Return)stmt;
            if(WSUtil.isComplexCodeNode(rStmt.expr(), wts)){
                return Pattern.Compound;
            }
            else{
                //not possible. otherwise should return simple;
                assert(false);
            }
        }
        
        if(stmt instanceof Block){
            return Pattern.Block;
        }
        
        if(stmt instanceof Try){
            //we only support try's block is complex, right now
            
            Try tryStmt = (Try)stmt;
            for(Catch c : tryStmt.catchBlocks()){
                if(WSUtil.isComplexCodeNode(c.body(), wts)){
                    System.out.println("----------> catch error");
                    return Pattern.Unsupport;
                }
            }
            if(WSUtil.isComplexCodeNode(tryStmt.finallyBlock(), wts)){
                System.out.println("----------> final error");
                return Pattern.Unsupport;
            }
            
            return Pattern.Try;
        }
        
        //other statements no support right now
        return Pattern.Unsupport;
    }

    private static Pattern detectEval(final Stmt stmt, final WSTransformState wts) {
        //should > 0. Other wise will not be sent to here for pattern detection
        int concurrentCallNum = WSUtil.calcConcurrentCallNums(stmt, wts);
        assert(concurrentCallNum > 0);
        Expr expr = ((Eval)stmt).expr();

        if(expr instanceof Call){
            Call aCall = (Call)expr;
            if(wts.isConcurrentCallSite(aCall)
                    && concurrentCallNum == 1){ //only this call is concurrent call
                return Pattern.Call;
            }
            else{ //call==1, not in first level; or call > 1
                return Pattern.Compound;
            }
        }
        else if(expr instanceof Assign){
            Assign assign = (Assign)expr;
            Expr rightExpr = assign.right();
            if(rightExpr instanceof Call){
                Call aCall = (Call)rightExpr;
                if(wts.isConcurrentCallSite(aCall)
                        && concurrentCallNum == 1){ //only this call is concurrent call
                    return Pattern.AssignCall;
                }
                else{ //call==1, not in first level; or call > 1
                    return Pattern.Compound;
                }
            }
            else if( rightExpr instanceof FinishExpr){
                return Pattern.FinishAssign;
            }
            else{
                //if right is not a call, must be a compound one
                return Pattern.Compound;
            }
        }
        else {
            //other eval with concurrent call, must be compound
            return Pattern.Compound;
        }
    }
    
    /**
     * Detect whether this pattern is a control flow pattern,
     * such as block, if, forloop, for, do...while, while, switch
     * 
     * @param pattern
     * @return
     */
    public static boolean isControlFlowPattern(Pattern pattern){
        if(pattern == Pattern.Block
                || pattern == Pattern.If
                || pattern == Pattern.For
                || pattern == Pattern.ForLoop
                || pattern == Pattern.DoWhile
                || pattern == Pattern.While
                || pattern == Pattern.Switch){
            return true;
        }
        return false;
    }
    
}
