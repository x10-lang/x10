package x10.compiler.ws.util;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.MethodDef;
import polyglot.types.ProcedureDef;

/**
 * @author Haichuan
 * 
 * Represent one node in the WS Call Graph
 * It has the method def, a flag to indicate whether it is a parallel (translation target)
 * And two list to record the call to and call from
 *
 */
public class WSCallGraphNode {

    protected WSCallGraph callGraph;
    protected ProcedureDef methodDef;
    protected boolean containsConcurrent;
    protected boolean parallel;
    protected boolean callgraphBuild;
    protected List<WSCallGraphNode> callFrom;
    protected List<WSCallGraphNode> callTo;
    
    
    public WSCallGraphNode(WSCallGraph callGraph, ProcedureDef methodDef){
        this.callGraph = callGraph;
        this.methodDef = methodDef;
        callFrom = new ArrayList<WSCallGraphNode>();
        callTo = new ArrayList<WSCallGraphNode>();
    }

    public boolean isParallel() {
        return parallel;
    }


    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }
    
    public void addCallTo(List<ProcedureDef> callees){
        for(ProcedureDef md : callees){
            addCallTo(md);
        }
        
    }
    
    public void addCallTo(ProcedureDef calleeMethodDef){
        WSCallGraphNode calleeNode = callGraph.findOrCreateNode(calleeMethodDef);
        callTo.add(calleeNode);
        calleeNode.addCallFrom(this);
    }
    
    /**
     * Only be called from a WSCallGraphNode
     * @param callerNode
     */
    protected void addCallFrom(WSCallGraphNode callerNode){
        callFrom.add(callerNode);
    }
    
    public WSCallGraphNode[] getCallers(){
        return callFrom.toArray(new WSCallGraphNode[0]);
    }

    public ProcedureDef getMethodDef() {
        return methodDef;
    }

    protected boolean isCallgraphBuild() {
        return callgraphBuild;
    }

    protected void setCallgraphBuild(boolean callgraphBuild) {
        this.callgraphBuild = callgraphBuild;
    }

    public boolean isContainsConcurrent() {
        return containsConcurrent;
    }

    protected void setContainsConcurrent(boolean containsConcurrent) {
        this.containsConcurrent = containsConcurrent;
    }
    
}

