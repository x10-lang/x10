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
import java.util.HashSet;
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
 * @author tardieu
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
    public static final int debugLevel = 5; //0: no; 3: little; 5: median; 7: heave; 9: verbose
    
    // Single static WSTransformState shared by all visitors (FIXME)
    public static WSTransformState wts; 

    private final HashSet<X10MethodDecl> genMethodDecls;
    private final HashSet<X10ClassDecl> genClassDecls;

    /** 
     * @param job
     * @param ts
     * @param nf
     */
    public WSCodeGenerator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        genMethodDecls = new HashSet<X10MethodDecl>();
        genClassDecls = new HashSet<X10ClassDecl>();
    }

    public static void buildCallGraph(X10TypeSystem xts, X10NodeFactory xnf, String theLanguage) {
        wts = new WSTransformState(xts, xnf, theLanguage);
    }

    /** 
     * WS codegen
     * MethodDecl --> if it is a target method, transform it into an inner class
     * X10ClassDecl --> add generated inner classes and methods if any
     */
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        // reject unsupported patterns
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

        // transform methods
        if(n instanceof MethodDecl) {
            MethodDecl mDecl = (MethodDecl)n;
            MethodDef mDef = mDecl.methodDef();
            if(wts.isTargetProcedure(mDef)){
                if(debugLevel > 3){
                    System.out.println("[WS_INFO] Start transforming target method: " + mDef.name());
                }
                
                WSMethodFrameClassGen mFrame;
                Job job = ((ClassType) mDef.container().get()).def().job();
                if (X10PrettyPrinterVisitor.isMainMethodInstance(mDef.asInstance(), context)) {
                    WSMainMethodClassGen mainFrame = new WSMainMethodClassGen(job, (X10NodeFactory) nf, (X10Context) context, mDef, mDecl, wts);
                    mainFrame.genClass();
                    n = mainFrame.getNewMainMethod();
                    mFrame = mainFrame;
                }
                else {
                    mFrame = new WSMethodFrameClassGen(job, (X10NodeFactory) nf, (X10Context) context, mDef, mDecl, wts);
                    mFrame.genClass();
                    n = null;
                }
                genClassDecls.addAll(mFrame.close()); 
                genMethodDecls.add(mFrame.getWraperMethod());
                if(debugLevel > 3){
                    System.out.println(mFrame.getFrameStructureDesc(4));
                }
            }
            return n;
        }

        // transform classes
        if (n instanceof X10ClassDecl) {
            X10ClassDecl cDecl = (X10ClassDecl)n;
            ClassDef cDef = cDecl.classDef();
            
            List<X10ClassDecl> classes = getClassDecls(cDef);
            if (classes.isEmpty()) {
                return n; //no change
            }
            else{
                if(debugLevel > 3){
                    System.out.println();
                    System.out.println("[WS_INFO] Add new methods and nested classes to class: " + n);
                }
                cDecl = Synthesizer.addNestedClasses(cDecl, classes);
                cDecl = Synthesizer.addMethods(cDecl, getMethodDecls(cDef));
                return cDecl;
            }
        }
        return n;
    }
    
    protected List<X10MethodDecl> getMethodDecls(ClassDef cDef) throws SemanticException {
        List<X10MethodDecl> mDecls = new ArrayList<X10MethodDecl>();
        
        for(X10MethodDecl mDecl : genMethodDecls){
            ClassDef containerDef = ((ClassType) mDecl.methodDef().container().get()).def();
            if(containerDef == cDef){
                mDecls.add(mDecl);
            }
        }
        return mDecls;
    }
    
    protected List<X10ClassDecl> getClassDecls(ClassDef cDef) throws SemanticException {
        ArrayList<X10ClassDecl> cDecls = new ArrayList<X10ClassDecl>();
        
        for(X10ClassDecl cDecl : genClassDecls){
            ClassDef containerDef = cDecl.classDef().outer().get();
            if(containerDef == cDef){
                cDecls.add(cDecl);
            }
        }
        return cDecls;
    }
}
