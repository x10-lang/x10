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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.visit.NodeVisitor;
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
	public static final String WS_TOPIC = "workstealing";
	public static final void wsReport(int level, String message){
		if(Report.should_report(WS_TOPIC, level)){
			Report.report(level, message);
		}
	}
	
    
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
    
    //Store WALA analysis result - Need refactoring
    //        Map<className : Map<methodSig: methodType>  methodType == false: derived target
    Map<String, Map<String, Boolean>> walaClass2MethodMap;
    
    public void setWALAResult(List<String> result){
    	walaClass2MethodMap = new HashMap<String, Map<String, Boolean>>();
    	for(String str : result){
    		//format: [C]Fib.fib(Lx10/lang/Int;)Lx10/lang/Int;
    		boolean type = str.charAt(1) == 'C'; 
    		int dotIndex = str.lastIndexOf('.');
    		String className = str.substring(3, dotIndex);
    		String methodSig = str.substring(dotIndex + 1);
    		
    		//insert into the map;
    		Map<String, Boolean> methodMap;
    		if(walaClass2MethodMap.containsKey(className)){
    			methodMap = walaClass2MethodMap.get(className);
    		}
    		else{
    			methodMap = new HashMap<String, Boolean>();
    			walaClass2MethodMap.put(className, methodMap);
    		}
    		methodMap.put(methodSig, type);
    	}
    	
    	//final: show found result;
    	if(Report.should_report("workstealing", 5)){
    		Report.report(5, "Results set from WALA");
    		for(String className : walaClass2MethodMap.keySet()){
    			Report.report(5, "   Class: " + className);
    			Map<String, Boolean> methodMap = walaClass2MethodMap.get(className);
    			for(String methodSig : methodMap.keySet()){
    				String typeStr = methodMap.get(methodSig) ? "[C]" : "[D]";
    				Report.report(5, "     "+typeStr + methodSig);
    			}
    		}
    	}
    	
    }
    
    protected Map<ProcedureDef, WSCallGraphNode> def2NodeMap;
    
    //These methods has parallel constructs in them;
    protected List<WSCallGraphNode> initialParallelMethods;
    
    public WSCallGraph(){
        def2NodeMap = new HashMap<ProcedureDef, WSCallGraphNode>();
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
        
        if(walaClass2MethodMap != null){
        	//use wala result to do mark
        	markNodeFromWALAResult(node);
        	return;
        }
        
        //otherwise depend on the current one

        if(!node.isCallgraphBuild()){ //prevent add one method again
            //now check whether this method has parallel;
            if(WSCodeGenUtility.containsConcurrentConstruct(procedure)){
                node.setContainsConcurrent(true);
                initialParallelMethods.add(node);
            }
                    
            //now get all callees from this node
            node.addCallTo(WSCodeGenUtility.scanForCallees(procedure));
            
//            //debug info
//            System.out.println("scan for callto:"+pDef);
//            for(ProcedureDef pcDef : WSCodeGenUtility.scanForCallees(procedure)){
//                System.out.println(" "+pcDef);
//            }
        }
    }
    
    private void markNodeFromWALAResult(WSCallGraphNode node) {
    	ProcedureDef pDef = node.getMethodDef();
    	String fullSig = getJavaSignature(pDef);
    	wsReport(5, "Input pDef Sig: " + pDef.signature());
    	wsReport(5, "Output pDef Java Sig: " +fullSig);    	
    	
    	
    	int dInd= fullSig.indexOf('.');
    	if(dInd < 0){
    		return; //no class name, should be closure. not support now
    	}
    	String className = fullSig.substring(0, dInd);
    	String methedSig = fullSig.substring(dInd + 1);

    	wsReport(5, "  Class: " +className);   
    	wsReport(5, "  Method: " + methedSig);
    	
        if(walaClass2MethodMap.containsKey(className)){
        	Map<String, Boolean> map = walaClass2MethodMap.get(className);
        	if(map.containsKey(methedSig)){
        		node.setParallel(true); //always parallel
        		if(map.get(methedSig)){ //this method contains concurrent
        			node.setContainsConcurrent(true);
        			initialParallelMethods.add(node);
        		}
        	}	
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
     * Input format: n:x10.lang.Int args:x10.array.Array[x10.lang.String]{self.rank==1}
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
    	if(walaClass2MethodMap != null){
    		//we use wala DFS result instead of the simple one
    		return;
    	}
    	
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
