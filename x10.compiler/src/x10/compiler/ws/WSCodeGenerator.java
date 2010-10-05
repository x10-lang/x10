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

import java.util.List;

import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.ConstructorDef;
import polyglot.types.MethodDef;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.Closure;
import x10.ast.Here;
import x10.ast.Offer;
import x10.ast.PlacedClosure;
import x10.ast.RemoteActivityInvocation;
import x10.ast.X10ClassDecl;
import x10.ast.X10NodeFactory;
import x10.compiler.ws.codegen.WSMainMethodClassGen;
import x10.compiler.ws.codegen.WSMethodFrameClassGen;
import x10.compiler.ws.util.WSCallGraph;
import x10.types.ClosureDef;
import x10.types.X10Context;
import x10.types.X10TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.util.Synthesizer;


/**
 * ContextVisitor that generates code for work stealing.
 * @author Haibo
 * @author Haichuan
 * 
 * In work-stealing code transformation, all methods with finish-async statements,
 * and all methods that invoke directly or indirectly the above methods,
 * need rewriting.
 * 
 * So it needs to build a static call-graph, and have a DFS on the reverse call-graph
 *  edges and mark all methods reachable. 
 * 
 * In the first step, we only mark all methods that contain finish-async as the target.
 */
public class WSCodeGenerator extends ContextVisitor {
    private static final int debugLevel = 4;
    
    //Although there are different WSVisitor, each one has the same WSTransformState
    //FIXME: get rid of the static field
    static public WSTransformState wts; 
        
    /** 
     * @param job
     * @param ts
     * @param nf
     */
    public WSCodeGenerator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    public static void buildCallGraph(X10TypeSystem xts, X10NodeFactory xnf, String theLanguage) {
        wts = new WSTransformState(xts, theLanguage);
        wts.buildCallGraph(xts, xnf);
    }
    
    /* 
     * This method will check an AST node, and decide whether transform or not.
     * MethodDecl --> if it is a target method, transform it into an inner class
     * X10ClassDecl --> if it contains some newly added inner classes, add these classes to the container class
     * 
     * ConstructorDecl --> if it contains concurrent, throw error
     * Closure (not place procedure) --> if it contains concurrent, throw error
     * 
     * 
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     */
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Node result = n;
        
        //Need check whether some constructs are concurrent
        if(n instanceof ConstructorDecl){
            ConstructorDecl cDecl = (ConstructorDecl)n;
            ConstructorDef cDef = cDecl.constructorDef();
            if(wts.isTargetProcedure(cDef)){
                throw new SemanticException("Work-Stealing Compiling doesn't support concurrent constructor: " + cDef,cDecl.position());
            }
        }
        
        if(n instanceof Closure){
            if(n instanceof PlacedClosure){
                //Future & AtExpr
                checkRemoteActivityPlace((PlacedClosure)n);
            }
            else{
                Closure closure = (Closure)n;           
                ClosureDef cDef = closure.closureDef();
                if(wts.isTargetProcedure(cDef)){
                    throw new SemanticException("Work-Stealing Compiling doesn't support concurrent closure: " + cDef,closure.position());
                }
            }
        }
        
        if(n instanceof RemoteActivityInvocation){
            //Other RemoteActivityInvocation, such as async, AtStmt
            checkRemoteActivityPlace((RemoteActivityInvocation)n);
        }
        
        if(n instanceof AtEach){
            throw new SemanticException("Work-Stealing Compiling only supports single place applications: " + n,n.position());
        }
        
        if(n instanceof Offer){
            throw new SemanticException("Work-Stealing Compiling doesn't support collecting finish: " + n,n.position());
        }
        
        if (n instanceof MethodDecl) {
            MethodDecl mDecl = (MethodDecl)n;
            MethodDef mDef = mDecl.methodDef();       

            if(wts.isTargetProcedure(mDef)){
                if(debugLevel > 3){
                    System.out.println("[WS_INFO] Start transforming target method: " + mDef.name());
                }
                
                if(mDef.name().toString().equals("main")){
                    WSMainMethodClassGen mainClassGen = (WSMainMethodClassGen) wts.getInnerClass(mDef);
                    mainClassGen.setMethodDecl(mDecl);
                    mainClassGen.genClass((X10Context) context);

                    n = mainClassGen.getNewMainMethod();
                    if(debugLevel > 3){
                        System.out.println(mainClassGen.getFrameStructureDesc(4));
                    }
                }
                else{
                    WSMethodFrameClassGen methodGen = wts.getInnerClass(mDef);
                    methodGen.setMethodDecl(mDecl);
                    methodGen.genClass((X10Context) context);
                    n = null;
                    if(debugLevel > 3){
                        System.out.println(methodGen.getFrameStructureDesc(4));
                    }
                }
            }
            
            return n;

        }

        if (n instanceof X10ClassDecl) {           
            X10ClassDecl cDecl = (X10ClassDecl)n;
            
            List<X10ClassDecl> innerClasses = wts.getInnerClasses(cDecl.classDef());
            if (innerClasses.isEmpty()) {
                return result; //No WS transformation
            }
            else{
                if(debugLevel > 3){
                    System.out.println();
                    System.out.println("[WS_INFO] Add new methods/inner-classes to class:" + n);
                }
                cDecl = Synthesizer.addInnerClasses(cDecl, innerClasses);
                cDecl = Synthesizer.addMethods(cDecl, wts.getGeneratedMethods(cDecl.classDef()));
                return cDecl;
            }
        }
        return result;
    }
    
    /**
     * If the rActivity's place is not here, just throw error.
     * @param rActivity
     * @throws SemanticException
     */
    protected void checkRemoteActivityPlace(RemoteActivityInvocation rActivity) throws SemanticException{
        if(!(rActivity.place() instanceof Here)){
            throw new SemanticException("Work-Stealing Compiling only supports single place applications: " + rActivity, ((Node)rActivity).position());
        }
    }
}
