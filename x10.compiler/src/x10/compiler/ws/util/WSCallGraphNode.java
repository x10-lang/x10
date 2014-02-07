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
    protected boolean containsConcurrent; //contains concurrent constructs
    protected boolean parallel; //contains concurrent, or directly/indirectly call concurrent methods
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

