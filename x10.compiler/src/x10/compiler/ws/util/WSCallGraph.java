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
import polyglot.types.Package;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
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
    
    static public boolean ignoreClass(ClassDecl classDecl){
        
        Ref<? extends Package> pRef = classDecl.classDef().package_();
        if(pRef != null){
            Package p = pRef.get();
            String pName = p.toString();
            
            for(String s : ignorePackages){
                if(pName.startsWith(s)){
                    return true;
                }
            }
        }
        
        
        String className = classDecl.name().id().toString();
        
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
        
        if(ignoreClass(classDecl)){
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
        return def2NodeMap.get(m).parallel;
    }
}
