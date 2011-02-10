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
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.main.Report;
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
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
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
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.compiler.ws.util.WSTransformationContent;
import x10.compiler.ws.util.WSTransformationContent.MethodType;
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
    public static final int debugLevel = 5; //0: no; 3: little; 5: median; 7: heave; 9: verbose
	public static final String WS_TOPIC = "workstealing";
	//public static final void wsReport(int level, String message){
	//	if(Report.should_report(WS_TOPIC, level)){
	//		Report.report(level, message);
	//	}
	//}
    
    // Single static WSTransformState shared by all visitors (FIXME)
    public static WSTransformState wts; 
    
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

    public static void setWALATransTarget(TypeSystem xts, NodeFactory xnf, String theLanguage, WSTransformationContent target){
    	//DEBUG
    	if(debugLevel > 3){
        	//wsReport(5, "Use WALA CallGraph Data...");    
    		System.out.println("[WS_INFO] Use WALA CallGraph Data...");
    	}
    	wts = new WSTransformState(xts, xnf, theLanguage, target);
    }
    
    public static void buildCallGraph(TypeSystem xts, NodeFactory xnf, String theLanguage) {
    	//DEBUG
    	if(debugLevel > 3){
        	//wsReport(5, "Build Simple Graph Graph..."); 
    		System.out.println("[WS_INFO] Build Simple Graph Graph...");
    	}
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
            if(wts.getMethodType(cDecl) != MethodType.NORMAL){
                throw new SemanticException("Work Stealing doesn't support concurrent constructor: " + cDecl, n.position());
            }
        }
        if(n instanceof RemoteActivityInvocation){
            RemoteActivityInvocation r = (RemoteActivityInvocation)n;
            if(!(r.place() instanceof Here)){
                throw new SemanticException("Work-Stealing doesn't support at: " + r, n.position());
            }
        }
        if(n instanceof Closure && !(n instanceof PlacedClosure)){
            //match with WSCallGraph, not handle PlacedClosure
            Closure closure = (Closure)n;           
            if(wts.getMethodType(closure) != MethodType.NORMAL){
                throw new SemanticException("Work Stealing doesn't support concurrent closure: " + closure, n.position());
            }
        }
        if(n instanceof AtEach){
            throw new SemanticException("Work Stealing doesn't support ateach: " + n,n.position());
        }
        if(n instanceof Offer){
            throw new SemanticException("Work Stealing doesn't support collecting finish: " + n,n.position());
        }
        
        // transform call site
        if(n instanceof Call){
        	Call call = (Call)n;
        	switch(wts.getCallSiteType(call)){
        	case MATCHED_CALL: //change the target
        		//two steps, create a new method def, and change the call
        		X10MethodDef mDef = WSCodeGenUtility.createWSCallMethodDef(call.methodInstance().def(), wts);
        		List<Expr> newArgs = new ArrayList<Expr>();
        		newArgs.add(nf.NullLit(Position.COMPILER_GENERATED).type(wts.workerType));
        		newArgs.add(nf.NullLit(Position.COMPILER_GENERATED).type(wts.frameType));
        		newArgs.add(nf.NullLit(Position.COMPILER_GENERATED).type(wts.finishFrameType));
        		return WSCodeGenUtility.replaceMethodCallWithWSMethodCall(nf, (X10Call) call, mDef, newArgs);
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
                if(debugLevel > 3){
                    System.out.println("[WS_INFO] Start transforming target method: " + mDef.name());
                }
                Job job = ((ClassType) mDef.container().get()).def().job();
                WSMethodFrameClassGen mFrame = new WSMethodFrameClassGen(job, (NodeFactory) nf, (Context) context, mDef, mDecl, wts);
                try{
                    n = mFrame.transform();
                }
                catch(SemanticException e){
                    System.err.println("==========>" + e.getMessage());
                    e.printStackTrace();
                    System.exit(-1);
                }
                genClassDecls.addAll(mFrame.close()); 
                genMethodDecls.add(mFrame.getWraperMethod());
                if(debugLevel > 3){
                    System.out.println(mFrame.getFrameStructureDesc(4));
                }
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
                if(debugLevel > 3){
                    System.out.println();
                    System.out.println("[WS_INFO] Add new methods and nested classes to class: " + n);
                }
                List<X10MethodDecl> methods = getMethodDecls(cDef);
                
                cDecl = Synthesizer.addNestedClasses(cDecl, classes);
                cDecl = Synthesizer.addMethods(cDecl, methods);
                
                //Here we need use desugarer and inner class remover to visit the class again.
                //do final processing, run desugarer and inner class remover again
                //get the right desuguar
                Desugarer desugarer;
                if(wts.getTheLanguage().equals("java")){
                	desugarer = new x10c.visit.Desugarer(job, ts, nf);
                }
                else{
                	desugarer = new x10.visit.Desugarer(job, ts, nf);
                }
                desugarer.begin();
                desugarer.context(context()); //copy current context
                
                X10InnerClassRemover innerclassRemover = new X10InnerClassRemover(job, ts, nf);
                innerclassRemover.begin();
                innerclassRemover.context(context()); //copy current context
                
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
        LocalDef workerLDef = ts.localDef(pos, Flags.FINAL, Types.ref(wts.workerType), workerName);
        Formal workerF = nf.Formal(pos,
                              nf.FlagsNode(pos, Flags.FINAL), 
                              nf.CanonicalTypeNode(pos, wts.workerType), 
                              nf.Id(pos, workerName)).localDef(workerLDef);

    	Name upName = Name.make("up");
        LocalDef upLDef = ts.localDef(pos, Flags.FINAL, Types.ref(wts.frameType), upName);
        Formal upF = nf.Formal(pos,
                              nf.FlagsNode(pos, Flags.FINAL), 
                              nf.CanonicalTypeNode(pos, wts.frameType), 
                              nf.Id(pos, upName)).localDef(upLDef);
    	
    	Name ffName = Name.make("ff");
        LocalDef ffLDef = ts.localDef(pos, Flags.FINAL, Types.ref(wts.finishFrameType), ffName);
        Formal ffF = nf.Formal(pos,
                              nf.FlagsNode(pos, Flags.FINAL), 
                              nf.CanonicalTypeNode(pos, wts.finishFrameType), 
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
        Name name = Name.make(WSCodeGenUtility.getMethodFastPathName(methodDef));
        methodDef.setName(name);
        methodDecl = methodDecl.name(nf.Id(pos, name));
        
    	return methodDecl;
    }
    
    
}
