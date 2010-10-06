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
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Term;
import polyglot.ast.Try;
import polyglot.ast.While;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import x10.ast.Async;
import x10.ast.Finish;
import x10.ast.FinishExpr;
import x10.ast.ForLoop;
import x10.ast.Future;
import x10.ast.When;
import x10.ast.X10NodeFactory;
import x10.compiler.ws.WSTransformState;
import x10.types.X10Context;
import x10.types.X10TypeSystem;

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
                  When,
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
                  
                  
    static Name FORCE = Name.make("force");
        
    X10NodeFactory xnf;
    X10TypeSystem xts;
    X10Context xct;
    WSTransformState wts;
    public CodePatternDetector(X10NodeFactory xnf, X10Context xct, WSTransformState wts){
        this.xnf = xnf;
        this.xct = xct;
        this.xts = (X10TypeSystem) xct.typeSystem();
        this.wts = wts;
    }
    
    /**
     * Check an input statement and return the corresponding restored statement
     * 
     * 
     * @param stmt
     * @return
     */
    public Pair<Pattern, Stmt> detectAndTransform(Stmt stmt){
        
        if(!WSCodeGenUtility.isComplexCodeNode(stmt, wts)){
            return new Pair<Pattern, Stmt>(Pattern.Simple, stmt);
        }
        
        //TODO: Check home == here;
        if(stmt instanceof Async){
            return new Pair<Pattern, Stmt>(Pattern.Async, stmt);
        }
        
        if(stmt instanceof Finish){
            return new Pair<Pattern, Stmt>(Pattern.Finish, stmt);
        }
        
        if(stmt instanceof When){
            When w = (When)stmt;
            if(WSCodeGenUtility.isComplexCodeNode(w.expr(), wts)){
                //need flatten
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                //just normal when
                return new Pair<Pattern, Stmt>(Pattern.When, stmt);
            }
        }
        
        if(stmt instanceof Eval){
            return detectEval(stmt);
        }
        
        //next if
        if(stmt instanceof If){
            If ifStmt = (If)stmt;
            if(WSCodeGenUtility.isComplexCodeNode(ifStmt.cond(), wts)){
                //need flatten
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                //just normal if
                return new Pair<Pattern, Stmt>(Pattern.If, stmt);
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
                if(WSCodeGenUtility.isComplexCodeNode(t, wts)){
                    compoundS = true;
                    break;
                }
            }
            
            if(compoundS){
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                return new Pair<Pattern, Stmt>(Pattern.For, stmt);
            }
        }
        
        if(stmt instanceof ForLoop){
            ForLoop forloopStmt = (ForLoop)stmt;
            
            if(WSCodeGenUtility.isComplexCodeNode(forloopStmt.domain(), wts)){
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                return new Pair<Pattern, Stmt>(Pattern.ForLoop, stmt);
            }
        }
        
        if(stmt instanceof While){
            While whileStmt = (While)stmt;
            if(WSCodeGenUtility.isComplexCodeNode(whileStmt.cond(), wts)){
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                return new Pair<Pattern, Stmt>(Pattern.While, stmt);
            }
        }
        
        if(stmt instanceof Do){
            Do doStmt = (Do)stmt;
            if(WSCodeGenUtility.isComplexCodeNode(doStmt.cond(), wts)){
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                return new Pair<Pattern, Stmt>(Pattern.DoWhile, stmt);
            }
        }
        
        if(stmt instanceof Switch){
            Switch ss = (Switch)stmt;
            if(WSCodeGenUtility.isComplexCodeNode(ss.expr(), wts)){
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                return new Pair<Pattern, Stmt>(Pattern.Switch, stmt);
            }
        }

        if(stmt instanceof Return){ //the return's expr must be complex
            Return rStmt = (Return)stmt;
            if(WSCodeGenUtility.isComplexCodeNode(rStmt.expr(), wts)){
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
            else{
                //not possible. otherwise should return simple;
                assert(false);
            }
        }
        
        if(stmt instanceof Block){
            return new Pair<Pattern, Stmt>(Pattern.Block, stmt);
        }
        
        if(stmt instanceof Try){
            //we only support try's block is complex, right now
            
            Try tryStmt = (Try)stmt;
            for(Catch c : tryStmt.catchBlocks()){
                if(WSCodeGenUtility.isComplexCodeNode(c.body(), wts)){
                    System.out.println("----------> catch error");
                    return new Pair<Pattern, Stmt>(Pattern.Unsupport, stmt);
                }
            }
            if(WSCodeGenUtility.isComplexCodeNode(tryStmt.finallyBlock(), wts)){
                System.out.println("----------> final error");
                return new Pair<Pattern, Stmt>(Pattern.Unsupport, stmt);
            }
            
            return new Pair<Pattern, Stmt>(Pattern.Try, stmt);
        }
        
        //other statements no support right now
        return new Pair<Pattern, Stmt>(Pattern.Unsupport, stmt);
    }

    private Pair<Pattern, Stmt> detectEval(Stmt stmt) {
        //should > 0. Other wise will not be sent to here for pattern detection
        int concurrentCallNum = WSCodeGenUtility.calcConcurrentCallNums(stmt, wts);
        assert(concurrentCallNum > 0);
        Expr expr = ((Eval)stmt).expr();

        if(expr instanceof Call){
            Call aCall = (Call)expr;
            if(wts.isTargetProcedure(aCall.methodInstance().def())
                    && concurrentCallNum == 1){ //only this call is concurrent call
                return new Pair<Pattern, Stmt>(Pattern.Call, stmt);
            }
            else{ //call==1, not in first level; or call > 1
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
        }
        else if(expr instanceof Assign){
            Assign assign = (Assign)expr;
            Expr rightExpr = assign.right();
            if(rightExpr instanceof Call){
                Call aCall = (Call)rightExpr;
                if(wts.isTargetProcedure(aCall.methodInstance().def())
                        && concurrentCallNum == 1){ //only this call is concurrent call
                    return new Pair<Pattern, Stmt>(Pattern.AssignCall, stmt);
                }
                else{ //call==1, not in first level; or call > 1
                    return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
                }
            }
            else if( rightExpr instanceof FinishExpr){
                return new Pair<Pattern, Stmt>(Pattern.FinishAssign, stmt);
            }
            else{
                //if right is not a call, must be a compound one
                return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
            }
        }
        else {
            //other eval with concurrent call, must be compound
            return new Pair<Pattern, Stmt>(Pattern.Compound, stmt);
        }
    }
}
