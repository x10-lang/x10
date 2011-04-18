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



package x10.compiler.ws;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Do;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Term;
import polyglot.ast.While;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ExtensionInfo;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.Closure;
import x10.ast.FinishExpr;
import x10.ast.ForLoop;
import x10.ast.Offer;
import x10.ast.StmtSeq;
import x10.compiler.ws.WSTransformState.MethodType;
import x10.compiler.ws.util.WSTransformationContent;
import x10.compiler.ws.util.WSUtil;
import x10.compiler.ws.util.CodePatternDetector.Pattern;
import x10.optimizations.ForLoopOptimizer;
import x10.visit.ExpressionFlattener;

/**
*
 * ContextVisitor that pre-process AST code for work stealing.
 * @author Haichuan
 * 
 * Some code cannot be transformed by WS Code Generator directly. They should be preprocessed in some way
 * 
 * Four goals of code pre-processing
 * I: transform compound stmts into simple stmts by ExpressionFlattener
 * II: transform forloop (for(x in domain)) into standard for by forloopunrolloer
 * III: transform ateach by lowerer
 * IV: transform simple for (e.g. for(var i:int=0; i < 1000; i++ ) async) into divide-and-conquer style


 */

public class WSCodePreprocessor extends ContextVisitor {
    static final boolean debug = true;

    // Single static WSTransformState shared by all visitors (FIXME)
    public static WSTransformState wts; 
    
    public WSCodePreprocessor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public static void setWALATransTarget(ExtensionInfo extensionInfo, WSTransformationContent target){
        //DEBUG
        if(debug){
            WSUtil.debug("Use WALA CallGraph Data...");    
        }
        wts = new WSTransformStateWALA(extensionInfo, target);
        WSCodeGenerator.wts = wts;
    }
    
    public static void buildCallGraph(ExtensionInfo extensionInfo) {
        //DEBUG
        if(debug){
            WSUtil.debug("Build Simple Graph Graph...");    
        }
        wts = new WSTransformStateSimple(extensionInfo);
        WSCodeGenerator.wts = wts;
    }
    
    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if(n instanceof For){
            return visitFor((For)n);
        }
        if(n instanceof ForLoop){
            return visitForLoop((ForLoop)n);
        }
        if(n instanceof AtEach){
            return visitAtEach((AtEach)n);
        }
        if(n instanceof Eval){
            return visitEval((Eval)n);
        }
        if(n instanceof Do){
            return visitDo((Do)n);
        }
        if(n instanceof While){
            return visitWhile((While)n);
        }
        if(n instanceof Switch){
            return visitSwitch((Switch)n);
        }
        if(n instanceof If){
            return visitIf((If)n);
        }
        if(n instanceof Return){
            return visitReturn((Return)n);
        }
        if(n instanceof LocalDecl){
            return visitLocalDecl((LocalDecl)n);
        }
        if(n instanceof ConstructorDecl){
            return visitConstructorDecl((ConstructorDecl)n);
        }
        if(n instanceof Closure){
            return visitClosure((Closure)n);
        }
        if(n instanceof Offer){
            return visitOffer((Offer)n);
        }
        
        return n;
    }
    
    private Node visitOffer(Offer n) throws SemanticException {
        WSUtil.err("X10 Work-Stealing doesn's support Collecting-Finish", n);
        return n;
    }

    private Node visitClosure(Closure n) throws SemanticException {        
        if(wts.getMethodType(n) != MethodType.NORMAL){
            WSUtil.err("X10 Work Stealing doesn't support concurrent closure", n);
        }
        return n;
    }

    private Node visitConstructorDecl(ConstructorDecl n) throws SemanticException {
        if(wts.getMethodType(n) != MethodType.NORMAL){
            WSUtil.err("X10 Work-Stealing doesn's support concurrent constructor", n);
        }
        return n;
    }

    /**
     * @param call
     * @return true if the call is "foo(...)" style. The "..." has no any concurrent call
     */
    private boolean isSimpleConcurrentCall(Call call){
        int concurrentCallNum = WSUtil.calcConcurrentCallNums(call, wts);
        if(concurrentCallNum == 1 && wts.isConcurrentCallSite(call)){
            return true;
        }
        else{
            return false;
        }
    }

    private Node visitLocalDecl(LocalDecl n) {
        if(n.init() == null){
            return n;
        }
        // a local decl's initializer 
        Expr expr = n.init();
        if(!WSUtil.isComplexCodeNode(expr, wts)){
            return n;
        }
        if(expr instanceof Call
                && isSimpleConcurrentCall((Call)expr)){
                return n;
        }
        return flattenStmt(n);
    }

    private Node visitReturn(Return n) {
        if(WSUtil.isComplexCodeNode(n.expr(), wts)){
            return flattenStmt(n);
        }
        
        return n;
    }

    private Node visitIf(If n) {
        if(WSUtil.isComplexCodeNode(n.cond(), wts)){
            return flattenStmt(n);
        }
        return n;
    }

    private Node visitSwitch(Switch n) {
        if(WSUtil.isComplexCodeNode(n.expr(), wts)){
            return flattenStmt(n);
        }
        return n;
    }

    private Node visitWhile(While n) {
        if(WSUtil.isComplexCodeNode(n.cond(), wts)){
            return flattenStmt(n);
        }
        return n;
    }

    private Node visitDo(Do n) {
        if(WSUtil.isComplexCodeNode(n.cond(), wts)){
            return flattenStmt(n);
        }
        return n;
    }
    
    private Node visitEval(Eval n) {
        
        //if the node is simple, no need any flatten.
        if(! WSUtil.isComplexCodeNode(n, wts)){
            return n;
        }
        
        Expr expr = n.expr();
        
        //Simple case 1: directly concurrent call
        if(expr instanceof Call
                && isSimpleConcurrentCall((Call)expr)){
                return n;
        }
        else if(expr instanceof LocalAssign || expr instanceof FieldAssign){
            Assign assign = (Assign)expr;
            Expr rightExpr = assign.right();
            if(rightExpr instanceof Call 
                    && isSimpleConcurrentCall((Call)rightExpr)){
                    return n;
            }
            if(rightExpr instanceof FinishExpr){
                return n; //collecting-finish
            }
        }
        return flattenStmt(n);
    }

    private Node visitAtEach(AtEach n) throws SemanticException {
        WSUtil.err("X10 Work-Stealing doesn's support AtEach", n);
        return null;
    }

    /**
     * Need transform ForLoop to For
     * @param n
     * @return
     */
    private Node visitForLoop(ForLoop n) {
        //Step 1: translate the forloop by forloopoptimizer
        ForLoopOptimizer flo = new ForLoopOptimizer(job, ts, nf);
        flo.begin();
        Node floOut = n.visit(flo); //floOut could be a block or a for statement
        // there are two cases from ForLoopOptimizer
        // 1. only for; 2. some local declarations and for
        // for cases 2, we need add these local declarations into for's init

        For forStmt = null;
        if (floOut instanceof Block) { // case 2
            List<ForInit> forInits = new ArrayList<ForInit>();
            for (Stmt s : ((Block) floOut).statements()) {
                if (s instanceof For) {
                    forStmt = (For) s;
                } else {
                    assert (s instanceof ForInit);// otherwise the ForLoopOptimizer is wrong
                    forInits.add((ForInit) s);
                }
            }
            forInits.addAll(forStmt.inits());
            forStmt = forStmt.inits(forInits); // replace inits
        } else {
            forStmt = (For) floOut;
        }

        // second step, it's highly possible the body of a for stmt contains
        // additional block
        // { stmt1
        // { ... //remove this level
        // } //remove this level
        // }
        // just remove the first level
        Stmt forBodyStmt = forStmt.body();
        if (forBodyStmt instanceof Block) {
            ArrayList<Stmt> stmts = new ArrayList<Stmt>();
            for (Stmt s : ((Block) forBodyStmt).statements()) {
                if (s instanceof Block) {
                    stmts.addAll(((Block) s).statements());
                } else {
                    stmts.add(s);
                }
            }
            // now insert it into the forBodyStmt again;
            forStmt = forStmt.body(nf.Block(forStmt.position(), stmts));
        }
        //Final step: still need check the for loop
        return visitFor(forStmt);
    }

    /**
     * We suppose the stmt is flattened. And only transform 
     *  for() async() style.
     * @param n
     * @return
     */
    public Node visitFor(For n) {
        
        //Detect the init/iterator/condition
        List<Term> condTerms = new ArrayList<Term>();
        condTerms.addAll(n.inits());
        condTerms.addAll(n.iters());
        condTerms.add(n.cond());
        
        boolean needFlattern = false;
        for(Term t : condTerms){
            if(WSUtil.isComplexCodeNode(t, wts)){
                needFlattern = true;
                break;
            }
        }
        
        if(needFlattern){
            return flattenStmt(n);
        }
        
        //for a simple For, we could try to transform it into divide and conquer style.
        
        //do a simple code pattern detector
//        Stmt forBody = n.body();
//        if(forBody instanceof Async) {
//            //Sytle 1
//            return null;
//        }
//        
//        if(forBody instanceof Block){
//            List<Stmt> stmts = ((Block)forBody).statements();
//            if(stmts.size() == 2
//                    )
//            
//        }
//        return n; //not transform
//        
//        
//        
//        
//        
        return n;
    }
    
    private Node flattenStmt(Stmt n){
        ExpressionFlattener ef = new ExpressionFlattener(job, ts, nf);
        ef.begin();
        Stmt outS = (Stmt) n.visit(ef);
        if(debug){
            WSUtil.debug("Flatten Input:", n);
            if(outS instanceof Block){
                if(outS instanceof StmtSeq){
                    WSUtil.debug("Flatten Output is OK:StmtSeq");
                }
                else{
                    WSUtil.debug("Flatten Output is WRONG:" + outS.getClass());
                }
            }
            WSUtil.debug("Flatten Output:", outS);
        }
        return outS;
    }

    
    
}
