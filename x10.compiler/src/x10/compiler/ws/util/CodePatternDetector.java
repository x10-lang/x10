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
import x10.ast.Offer;
import x10.ast.StmtSeq;
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
                  FinishExprAssign, //used in collecting finish
                  Async,
                  At, // at(p) S
                  AsyncAt, // async at(p) S
                  When,
                  LocalDecl, //local declare with the initializer is concurrent call
                  Call, //only the first level call is target call;
                  AssignCall, //only the first level call is target call;
                  If,
                  For,
                  ForLoop, //should not happen after ExpressionFlattener
                  While,
                  DoWhile,
                  Switch,
                  Try,
                  Simple, 
                  Block,
                  StmtSeq, //need flatten
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
        
        if(stmt instanceof StmtSeq){
            return Pattern.StmtSeq; //the stmt should be unwrapped
        }
        
        if(!WSUtil.isComplexCodeNode(stmt, wts)){
            return Pattern.Simple; // no need any transformation
        }
        
        if(stmt instanceof LocalDecl){
            return Pattern.LocalDecl; //local decl with concurrent call - should not happen
        }
        
        if(stmt instanceof Async){
            Stmt asyncBodyStmt = WSUtil.unwrapToOneStmt(((Async)stmt).body());
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
            return Pattern.When;
        }
        
        if(stmt instanceof Eval){
            //After code flatten, the eval should be call/assigncall/finishAssign
            Expr expr = ((Eval)stmt).expr();
            if(expr instanceof Call){
                return Pattern.Call;
            }
            else if(expr instanceof Assign){
                Expr rightExpr = ((Assign)expr).right();
                if(rightExpr instanceof Call){
                    return Pattern.AssignCall;
                }
                else if(rightExpr instanceof FinishExpr){
                    return Pattern.FinishExprAssign;
                }
                else {
                    return Pattern.Unsupport;
                }
            }
            else {
                return Pattern.Unsupport;
            }
        }
        
        //next if
        if(stmt instanceof If){
             return Pattern.If;
        }
        
        //next For
        if(stmt instanceof For){
            return Pattern.For;
        }
        
        if(stmt instanceof ForLoop){
            return Pattern.ForLoop; //Should not happen
        }
        
        if(stmt instanceof While){
            return Pattern.While;
        }
        
        if(stmt instanceof Do){
            return Pattern.DoWhile;
        }
        
        if(stmt instanceof Switch){
            return Pattern.Switch;
        }
        
        if(stmt instanceof Block){
            return Pattern.Block;
        }
        
        if(stmt instanceof Try){
            //we only support try's block is complex, right now
            Try tryStmt = (Try)stmt;
            for(Catch c : tryStmt.catchBlocks()){
                if(WSUtil.isComplexCodeNode(c.body(), wts)){
                    return Pattern.Unsupport;
                }
            }
            if(WSUtil.isComplexCodeNode(tryStmt.finallyBlock(), wts)){
                return Pattern.Unsupport;
            }
            return Pattern.Try;
        }
        
        //other statements no support right now
        return Pattern.Unsupport;
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
                || pattern == Pattern.Switch
                || pattern == Pattern.FinishExprAssign){
            return true;
        }
        return false;
    }
    
}
