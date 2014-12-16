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

package x10.compiler.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.ExtensionInfo;
import x10.util.CollectionFactory;
import x10.util.HierarchyUtils;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AtEach;
import x10.ast.Closure;
import x10.ast.Here;
import x10.ast.Offer;
import x10.ast.PlacedClosure;
import x10.ast.RemoteActivityInvocation;
import x10.ast.X10Call;
import x10.ast.X10ClassDecl;
import x10.ast.X10MethodDecl;
import x10.compiler.ws.codegen.WSMethodFrameClassGen;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.WSUtil;
import x10.compiler.ws.util.WSTransformationContent;
import x10.compiler.ws.WSTransformState.MethodType;
import x10.types.MethodInstance;
import x10.types.X10MethodDef;
import x10.util.Synthesizer;
import x10.visit.Desugarer;
import x10.visit.X10InnerClassRemover;


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
    public static final boolean debug = true;
   

    public static WSTransformState wts; //Set by WSCodePreprocessor
    private final Set<X10MethodDecl> genMethodDecls;
    private final Set<X10ClassDecl> genClassDecls;

    /** 
     * @param job
     * @param ts
     * @param nf
     */
    public WSCodeGenerator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        genMethodDecls = CollectionFactory.newHashSet();
        genClassDecls = CollectionFactory.newHashSet();
    }

    /** 
     * WS codegen
     * MethodDecl --> if it is a target method, transform it into an inner class
     * X10ClassDecl --> add generated inner classes and methods if any
     */
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {

        // transform call site
        if(n instanceof Call){
            Call call = (Call)n;
            switch(wts.getCallSiteType(call)){
            case MATCHED_CALL: //change the target
                //two steps, create a new method def, and change the call
                MethodInstance mi = WSUtil.createWSMethodInstance(call.methodInstance(), ts);
                List<Expr> newArgs = new ArrayList<Expr>();
                newArgs.add(nf.NullLit(Position.COMPILER_GENERATED).type(ts.Worker()));
                newArgs.add(nf.NullLit(Position.COMPILER_GENERATED).type(ts.Frame()));
                newArgs.add(nf.NullLit(Position.COMPILER_GENERATED).type(ts.FinishFrame()));
                return WSUtil.replaceMethodCallWithWSMethodCall(nf, (X10Call) call, mi, newArgs);
            case CONCURRENT_CALL:  //do nothing, leave the transformation in method decl transformation
            case NORMAL:
            default:
            }
        }

        // transform methods
        if(n instanceof X10MethodDecl) {
            
            X10MethodDecl mDecl = (X10MethodDecl)n;
            X10MethodDef mDef = mDecl.methodDef();
            
            switch(wts.getMethodType(mDecl)){
            case BODYDEF_TRANSFORMATION:
                //traditional transform
                WSUtil.info("Start transforming target method: " + mDef.name());
                Job job = ((ClassType) mDef.container().get()).def().job();
                WSMethodFrameClassGen mFrame = new WSMethodFrameClassGen(job, (NodeFactory) nf, (Context) context, 
                                                                         mDef, mDecl, wts, HierarchyUtils.isMainMethod(mDef, context));
                try{
                    n = mFrame.transform();
                }
                catch(SemanticException e){
                    e.printStackTrace();
                    WSUtil.err(e.getMessage(), n);
                }
                genClassDecls.addAll(mFrame.close()); 
                genMethodDecls.add(mFrame.getWraperMethod());
                WSUtil.info(mDef.name() + " frame Structure" 
                            + System.getProperty("line.separator")
                            + mFrame.getFrameStructureDesc(4));
                break;
            case DEFONLY_TRANSFORMATION:
                //only change the method's interface
                n = changeMethodDefOnly(mDecl);
                break;
            case NORMAL:
            default:
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
                WSUtil.info("Add new methods and nested classes to class: " + n);
                List<X10MethodDecl> methods = getMethodDecls(cDef);
                
                cDecl = Synthesizer.addNestedClasses(cDecl, classes);
                cDecl = Synthesizer.addMethods(cDecl, methods);
                
                ClosureDefReinstantiator closureProcessor = new ClosureDefReinstantiator(job, ts, nf);
                closureProcessor.begin();
                closureProcessor = (ClosureDefReinstantiator) closureProcessor.context(context());
                
                Desugarer desugarer = ((x10.ExtensionInfo) job.extensionInfo()).makeDesugarer(job);
                desugarer.begin();
                desugarer = (Desugarer) desugarer.context(context()); //copy current context
                
                X10InnerClassRemover innerclassRemover = new X10InnerClassRemover(job, ts, nf);
                innerclassRemover.begin();
                innerclassRemover = (X10InnerClassRemover) innerclassRemover.context(context()); //copy current context
                
                cDecl = (X10ClassDecl) cDecl.visit(closureProcessor);
                cDecl = (X10ClassDecl) cDecl.visit(desugarer);
                cDecl = (X10ClassDecl) cDecl.visit(innerclassRemover);
                
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
    
    
    /**
     * Transform a method, add worker/upframe/parent frame/ as formals.
     * But no any other changes. Just for those methods that need match the WS interface change
     * @param methodDecl
     * @return
     */
    protected X10MethodDecl changeMethodDefOnly(X10MethodDecl methodDecl){
        //need change the def's formals definition and decl's formals
        //three new formals, worker/upframe/finishframe
        Position pos = Position.COMPILER_GENERATED;
        
        Name workerName = Name.make("worker");
        LocalDef workerLDef = ts.localDef(pos, Flags.FINAL, Types.ref(ts.Worker()), workerName);
        Formal workerF = nf.Formal(pos,
                              nf.FlagsNode(pos, Flags.FINAL), 
                              nf.CanonicalTypeNode(pos, ts.Worker()), 
                              nf.Id(pos, workerName)).localDef(workerLDef);

        Name upName = Name.make("up");
        LocalDef upLDef = ts.localDef(pos, Flags.FINAL, Types.ref(ts.Frame()), upName);
        Formal upF = nf.Formal(pos,
                              nf.FlagsNode(pos, Flags.FINAL), 
                              nf.CanonicalTypeNode(pos, ts.Frame()), 
                              nf.Id(pos, upName)).localDef(upLDef);
        
        Name ffName = Name.make("ff");
        LocalDef ffLDef = ts.localDef(pos, Flags.FINAL, Types.ref(ts.FinishFrame()), ffName);
        Formal ffF = nf.Formal(pos,
                              nf.FlagsNode(pos, Flags.FINAL), 
                              nf.CanonicalTypeNode(pos, ts.FinishFrame()), 
                              nf.Id(pos, ffName)).localDef(ffLDef);
        
        
        //now change the def
        X10MethodDef methodDef = methodDecl.methodDef();
        ArrayList<LocalDef> formalNames = new ArrayList<LocalDef>();
        ArrayList<Ref<? extends Type>> formalRefs = 
            new  ArrayList<Ref<? extends Type>>();
        ArrayList<Formal> formals = new ArrayList<Formal>();
        
        formalNames.add(workerLDef);
        formalRefs.add(workerF.type().typeRef());
        formals.add(workerF);

        formalNames.add(upLDef);
        formalRefs.add(upF.type().typeRef());
        formals.add(upF);
        
        formalNames.add(ffLDef);
        formalRefs.add(ffF.type().typeRef());
        formals.add(ffF);
        
        formalNames.addAll(methodDef.formalNames());
        formalRefs.addAll(methodDef.formalTypes());
        formals.addAll(methodDecl.formals());
        
        methodDef.setFormalNames(formalNames);
        methodDef.setFormalTypes(formalRefs);
        methodDecl = methodDecl.formals(formals);
        
        //finally change the name;
        Name name = Name.make(WSUtil.getMethodFastPathName(methodDef));
        methodDef.setName(name);
        methodDecl = methodDecl.name(nf.Id(pos, name));
        
        return methodDecl;
    }
    
    
}
