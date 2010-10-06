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
import java.util.HashMap;
import java.util.List;

import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.MethodDef;
import polyglot.types.ProcedureDef;
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
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.compiler.ws.codegen.AbstractWSClassGen;
import x10.compiler.ws.codegen.WSMainMethodClassGen;
import x10.compiler.ws.codegen.WSMethodFrameClassGen;
import x10.compiler.ws.util.WSCallGraph;
import x10.compiler.ws.util.WSCallGraphNode;
import x10.types.ClosureDef;
import x10.types.X10Context;
import x10.types.X10TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.util.Synthesizer;
import x10.util.synthesizer.MethodSynth;
import x10.visit.X10PrettyPrinterVisitor;


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


    //map from target method to synthesized method
    final HashMap<MethodDef, MethodSynth> methodToWSMethodMap;

    /** 
     * @param job
     * @param ts
     * @param nf
     */
    public WSCodeGenerator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        methodToWSMethodMap = new HashMap<MethodDef, MethodSynth>();
        methodToInnerClassTreeMap = new HashMap<MethodDef, WSMethodFrameClassGen>();
    }
    

    //map from target method to synthesized inner class
    public final HashMap<MethodDef, WSMethodFrameClassGen> methodToInnerClassTreeMap;

    
    public static void buildCallGraph(X10TypeSystem xts, X10NodeFactory xnf, String theLanguage) {
        wts = new WSTransformState(xts, xnf, theLanguage);
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
        //Need check whether some constructs are concurrent
        if(n instanceof ConstructorDecl){
            ConstructorDecl cDecl = (ConstructorDecl)n;
            ConstructorDef cDef = cDecl.constructorDef();
            if(wts.isTargetProcedure(cDef)){
                throw new SemanticException("Work Stealing doesn't support concurrent constructor: " + cDef,n.position());
            }
        }
        
        if(n instanceof RemoteActivityInvocation){
            RemoteActivityInvocation r = (RemoteActivityInvocation)n;
            if(!(r.place() instanceof Here)){
                throw new SemanticException("Work-Stealing doesn't support at: " + r, n.position());
            }
        }

        if(n instanceof Closure){
            Closure closure = (Closure)n;           
            ClosureDef cDef = closure.closureDef();
            if(wts.isTargetProcedure(cDef)){
                throw new SemanticException("Work Stealing doesn't support concurrent closure: " + cDef,n.position());
            }
        }
        
        if(n instanceof AtEach){
            throw new SemanticException("Work Stealing doesn't support ateach: " + n,n.position());
        }
        
        if(n instanceof Offer){
            throw new SemanticException("Work Stealing doesn't support collecting finish: " + n,n.position());
        }
        
        if(n instanceof MethodDecl) {
            MethodDecl mDecl = (MethodDecl)n;
            MethodDef mDef = mDecl.methodDef();

            if(wts.isTargetProcedure(mDef)){
                if(debugLevel > 3){
                    System.out.println("[WS_INFO] Start transforming target method: " + mDef.name());
                }
                
                WSMethodFrameClassGen methodGen;
                Job job = (((ClassType) mDef.container().get()).def()).job();
                
                if (X10PrettyPrinterVisitor.isMainMethodInstance(mDef.asInstance(), context)) {
                    WSMainMethodClassGen mainClassGen = new WSMainMethodClassGen(job, (X10NodeFactory) nf, (X10Context) context, mDef, wts);
                    mainClassGen.setMethodDecl(mDecl);
                    mainClassGen.genClass((X10Context) context);
                    n = mainClassGen.getNewMainMethod();
                    methodGen = mainClassGen;
                }
                else {
                    methodGen = new WSMethodFrameClassGen(job, (X10NodeFactory) nf, (X10Context) context, mDef, wts);
                    methodGen.setMethodDecl(mDecl);
                    methodGen.genClass((X10Context) context);
                    n = null;
                }
                methodToInnerClassTreeMap.put(mDef, methodGen); 
                methodToWSMethodMap.put(mDef, methodGen.getWraperMethodSynths());
                if(debugLevel > 3){
                    System.out.println(methodGen.getFrameStructureDesc(4));
                }
            }

            return n;
        }

        if (n instanceof X10ClassDecl) {
            X10ClassDecl cDecl = (X10ClassDecl)n;
            
            List<X10ClassDecl> innerClasses = getInnerClasses(cDecl.classDef());
            if (innerClasses.isEmpty()) {
                return n; //No WS transformation
            }
            else{
                if(debugLevel > 3){
                    System.out.println();
                    System.out.println("[WS_INFO] Add new methods/inner-classes to class:" + n);
                }
                cDecl = Synthesizer.addInnerClasses(cDecl, innerClasses);
                cDecl = Synthesizer.addMethods(cDecl, getGeneratedMethods(cDecl.classDef()));
                return cDecl;
            }
        }
        return n;
    }
    
    /**
     * @return newly generated methods from the WS code transformation
     * @throws SemanticException 
     */
    public List<X10MethodDecl> getGeneratedMethods(ClassDef cDef) throws SemanticException {
        List<X10MethodDecl> mDecls = new ArrayList<X10MethodDecl>();
        
        for(MethodDef mDef : methodToWSMethodMap.keySet()){
            ClassDef containerDef = ((ClassType) mDef.container().get()).def();  
            if(containerDef == cDef){
                MethodSynth pair = methodToWSMethodMap.get(mDef);
                mDecls.add(pair.close());
            }
        }
        return mDecls;
    }
    
    public List<X10ClassDecl> getInnerClasses(ClassDef cDef) throws SemanticException {
        ArrayList<X10ClassDecl> cDecls = new ArrayList<X10ClassDecl>();
        
        for(MethodDef mDef : methodToInnerClassTreeMap.keySet()){
            
            ClassDef containerDef = ((ClassType) mDef.container().get()).def();   
            if(containerDef == cDef){ //only add those methods here
                AbstractWSClassGen innerClass = methodToInnerClassTreeMap.get(mDef);
                AbstractWSClassGen[]  innerClasses = innerClass.genAllOffString();
                for(int i = 0; i <innerClasses.length; i++){
                    cDecls.add(innerClasses[i].getGenClassDecl());
                }
            }
        }
        return cDecls;
    }
}
