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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.FunctionDef;
import polyglot.types.MemberDef;
import polyglot.types.Package;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.visit.NodeVisitor;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10.ast.Closure;
import x10.ast.PlacedClosure;

/**
 * A call graph to record WS transformation call graph and do DFS search
 * It contains method, constructor, and closure
 * 
 * 
 * @author Haichuan
 *
 */
public class WSCallGraph {
    
    static final String ignorePackages[] = {
        "x10",
    };
    
    static final String ignoreClasses[] ={
        "AsyncFrame",
        "BoxedBoolean",
        "Continuation",
        "FinishFrame",
        "Frame",
        "MainFrame",
        "RegularFrame",
        "WhenFrame",
        "Worker",
        "Deque",
    };
    
    static public boolean ignoreClass(ClassDef classDef){
        
        Ref<? extends Package> pRef = classDef.package_();
        if(pRef != null){
            Package p = pRef.get();
            String pName = p.toString();
            
            for(String s : ignorePackages){
                if(pName.startsWith(s)){
                    return true;
                }
            }
        }
        
        String className = classDef.toString();
        
        for(String s : ignoreClasses){
            if(className.equals(s)){
                return true;
            }
        }
        return false;
    }
    
    static final int debugLevel = 0;
    
    protected Map<ProcedureDef, WSCallGraphNode> def2NodeMap;
    
    //These methods has parallel constructs in them;
    protected List<WSCallGraphNode> initialParallelMethods;
    
    public WSCallGraph(){
        def2NodeMap = CollectionFactory.newHashMap();
        initialParallelMethods = new ArrayList<WSCallGraphNode>();
    }
    
    public WSCallGraphNode findOrCreateNode(ProcedureDef methodDef){
        if(def2NodeMap.containsKey(methodDef)){
            return def2NodeMap.get(methodDef);
        }
        
        WSCallGraphNode node = new WSCallGraphNode(this, methodDef);
        def2NodeMap.put(methodDef, node);
        return node;
    }
    
    
    public boolean addClass(ClassDecl classDecl){
        
        if(ignoreClass(classDecl.classDef())){
            if(debugLevel > 3){
                System.out.println("[WS_INFO]WSCallGraph: Ingore Class: " + classDecl);
            }
            return false;
        }
        
        //typically, it only analyze top level class, inner class is combined into its container to analysis
        NodeVisitor v = new NodeVisitor(){
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if(n instanceof MethodDecl
                        || n instanceof ConstructorDecl
                        || (n instanceof Closure && !(n instanceof PlacedClosure))){
                    //Note, PlacedClosure are not treated as normal closure, not build node
                    addProcedure((Term)n);                    
                }
                return n;
            }
        };
        classDecl.visit(v);
        return true;
    }
    
    /**
     * used only by WSCodePreprocessor.
     * In the pass, it may generate some concurrent method.
     * @param mDecl
     */
    public void addSynthesizedConcurrentMethod(MethodDecl mDecl) {
        WSCallGraphNode node = findOrCreateNode(mDecl.methodDef());
        node.setParallel(true);
    }
    
    /**
     * Add a method into the node, and mark the parallel status
     * And build the call graph, too
     * 
     * @param method
     */
    public void addProcedure(Term procedure){

        ProcedureDef pDef = null;
        if(procedure instanceof MethodDecl){
            pDef = ((MethodDecl)procedure).methodDef();
        }
        else if(procedure instanceof ConstructorDecl){
            pDef = ((ConstructorDecl)procedure).constructorDef();
        }
        else if(procedure instanceof Closure){
            pDef = ((Closure)procedure).closureDef();
        }

        WSCallGraphNode node = findOrCreateNode(pDef);
        
        //otherwise depend on the current one

        if(!node.isCallgraphBuild()){ //prevent add one method again
            //now check whether this method has parallel;
            if(WSUtil.containsConcurrentConstruct(procedure)){
                node.setContainsConcurrent(true);
                initialParallelMethods.add(node);
            }
                    
            //now get all callees from this node
            node.addCallTo(WSUtil.scanForCallees(procedure));
            
//            //debug info
//            System.out.println("scan for callto:"+pDef);
//            for(ProcedureDef pcDef : WSCodeGenUtility.scanForCallees(procedure)){
//                System.out.println(" "+pcDef);
//            }
        }
    }

	protected String getJavaSignature(ProcedureDef pDef){
    	
    	StringBuffer sb = new StringBuffer();
    	if(pDef instanceof MemberDef){
    		MemberDef mDef = (MemberDef)pDef;
    		String containerStr = mDef.container().get().toString().replace('.', '/');
    		sb.append(containerStr).append('.');
    	}
    	else{
    		//closure type is not well supported yet
    	}

    	//process signature
    	String sig = pDef.signature();
		int b1Index = sig.indexOf('(');
		int b2Index = sig.indexOf(')');
    	//method name
    	sb.append(sig.substring(0, b1Index)).append('(');
    	
    	
    	String formalTypes[] = sig.substring(b1Index+1, b2Index).split(",");
    	for(String fStr : formalTypes){
    		String ftStr = fStr.trim(); 
    		if(ftStr.length() == 0){
    			continue;
    		}
    		sb.append(type2JavaSignature(ftStr));
    	}
		sb.append(')');
		
    	//process return types
		if(pDef instanceof FunctionDef){
			FunctionDef fDef = (FunctionDef)pDef;
			String rStr = fDef.returnType().get().toString();
			if(rStr == null || rStr.length() == 0 || rStr.equalsIgnoreCase("void") ){
				sb.append('V');
			}
			else{
				sb.append('L').append(rStr.replace('.', '/')).append(';');
			}
		}
		return sb.toString();
    }
    
    /**
     * Input format: n:x10.lang.Int args:x10.regionarray.Array[x10.lang.String]{self.rank==1}
     * or no name, just type
     * @param type
     * @return Lx10/lang/Int;
     */
    protected String type2JavaSignature(String typeStr){
    	int sIndex = Math.max(0, typeStr.indexOf(':') + 1);
    	
    	//search template '['
    	int tIndex = typeStr.indexOf('[');
    	tIndex = tIndex > 0 ? tIndex : Integer.MAX_VALUE;
    	//search constraint '{'
    	int cIndex = typeStr.indexOf('{');
    	cIndex = cIndex > 0 ? cIndex : Integer.MAX_VALUE;
    	int eIndex = Math.min(typeStr.length(), Math.min(tIndex, cIndex));

    	StringBuffer sb = new StringBuffer();
    	sb.append('L').append(typeStr.substring(sIndex, eIndex).replace('.', '/')).append(';');
    	return sb.toString();    	
    }
    
    
    
    /**
     * Mark all possible methods as target methods.
     * Start from those contains concurrent constructs, and do DFS search to mark.
     */
    public void doDFSMark(){    	
        for(WSCallGraphNode node : initialParallelMethods){
            doDFSMark(node);
        }
    }
    
    protected void doDFSMark(WSCallGraphNode node){
        
        node.setParallel(true); //in case future clean;
        //then depths first;
        for(WSCallGraphNode parent :node.getCallers()){
            if(!parent.isParallel()){
                doDFSMark(parent);
            }
            //if parent is indeed, someone else is marked, no need mark again.
        }
    }
    
    
    /**
     * Return all parallel method from this call graph, including those from DFS search
     * @return
     */
    public List<WSCallGraphNode> getAllParallelMethods(){
        List<WSCallGraphNode> methods = new ArrayList<WSCallGraphNode>();
        for(WSCallGraphNode node : def2NodeMap.values()){
            if(node.isParallel()){
                methods.add(node);
            }
        }
        return methods;
    }
    
    public boolean isParallel(ProcedureDef m) {
        WSCallGraphNode n = def2NodeMap.get(m);
        if(n == null){
        	if(m instanceof MemberDef){
        		Type type = ((MemberDef)m).container().get();
        		if(type.toString().startsWith("x10.")){
        			return false; //not report x10 pacakges;
        		}
        	}
        	
            System.out.println("[WS_WARNING]ProcedureDef:" + m + " was not added to call graph. Suppose it is not a parallel procedure.");
            return false;
        }
        else{
            return n.parallel;
        }
    }

}
