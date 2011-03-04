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
import java.util.HashSet;
import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import x10.ast.X10ClassDecl;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.compiler.ws.codegen.AbstractWSClassGen;
import x10.compiler.ws.codegen.WSMainMethodClassGen;
import x10.compiler.ws.codegen.WSMethodFrameClassGen;
import x10.compiler.ws.util.WSCallGraph;
import x10.compiler.ws.util.WSCallGraphNode;
import x10.types.X10Context;
import x10.types.X10TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.util.synthesizer.MethodSynth;
import x10.visit.X10PrettyPrinterVisitor;

/**
 * Record the WS transformation intermediate results and context.
 * 
 * The WSState is globally exisits, however, some types need be refreshed
 * each time a WSCodeGenerator begins
 * 
 * Globally table: target methodDef -> Fast/Slow MethodSynth
 *    used to identify whether one method is target or not, and used for other places to generate right call
 * 
 * State Related:
 *    all types
 * 
 * 
 * And provide interfaces for WS code gen to query a method is a target method or not
 * 
 * Every target method and the corresponding inner class(es) will be stored here.
 * In other procedures, it need query the map.
 * If it is a target method, it need transform method call to
 *  (1) A new innerclass instance
 *  (2) invoke the fast method on the instance
 *   
 * @author Haichuan
 *
 */
public class WSTransformState {
    public final ClassType frameType;
    public final ClassType finishFrameType;
    public final ClassType rootFinishType;
    public final ClassType mainFrameType;
    public final ClassType regularFrameType;
    public final ClassType asyncFrameType;
    public final ClassType boxedBooleanType;
    public final ClassType workerType;
    public final ClassType stackAllocateType; //annotation type
    public final ClassType inlineType; //annotation type
    public final ClassType transientType; //annotation type
    public final ClassType headerType; //annotation type
    public final ClassType uninitializedType; //annotation type
    public final Boolean realloc; // whether or not to generate code for frame migration

    private final WSCallGraph callGraph;

    public WSTransformState(X10TypeSystem xts, X10NodeFactory xnf, String theLanguage){
        if (theLanguage.equals("c++")) {
            frameType = xts.load("x10.compiler.ws.Frame");
            finishFrameType = xts.load("x10.compiler.ws.FinishFrame");
            rootFinishType = xts.load("x10.compiler.ws.RootFinish");
            mainFrameType = xts.load("x10.compiler.ws.MainFrame");
            regularFrameType = xts.load("x10.compiler.ws.RegularFrame");
            asyncFrameType = xts.load("x10.compiler.ws.AsyncFrame");
            boxedBooleanType = xts.load("x10.compiler.ws.BoxedBoolean");
            workerType = xts.load("x10.compiler.ws.Worker");
            realloc = true;
        } else {
            frameType = xts.load("x10.compiler.ws.java.Frame");
            finishFrameType = xts.load("x10.compiler.ws.java.FinishFrame");
            rootFinishType = xts.load("x10.compiler.ws.java.RootFinish");
            mainFrameType = xts.load("x10.compiler.ws.java.MainFrame");
            regularFrameType = xts.load("x10.compiler.ws.java.RegularFrame");
            asyncFrameType = xts.load("x10.compiler.ws.java.AsyncFrame");
            boxedBooleanType = xts.load("x10.compiler.ws.java.BoxedBoolean");
            workerType = xts.load("x10.compiler.ws.java.Worker");
            realloc = false;
        }
        stackAllocateType = xts.load("x10.compiler.StackAllocate");
        inlineType = xts.load("x10.compiler.InlineOnly");
        transientType = xts.load("x10.compiler.Ephemeral");
        headerType = xts.load("x10.compiler.Header");
        uninitializedType = xts.load("x10.compiler.Uninitialized");
        
        callGraph = new WSCallGraph();
        
        //start to iterate the ast in jobs and build all;
        for(Job job : xts.extensionInfo().scheduler().jobs()){
            if(job == null){
                System.err.println("[WS_ERR] CallGraphBuilding: Find one job is empty!");
                continue;
            }
            Node node = job.ast();
            if(node != null && node instanceof SourceFile){
                for(TopLevelDecl tld : ((SourceFile)node).decls()){
                    if(tld instanceof ClassDecl){
                        boolean result = callGraph.addClass((ClassDecl) tld);
                        if(result){
                            System.out.println("[WS_INFO] CallGraphBuilding: Add one classDecl to graph: " + tld.toString());
                        }
                        
                    }
                }
            }
            else{
                if(node == null){
                    System.err.println("[WS_ERR] CallGraphBuilding: AST node == null for job: " + job.source().toString());
                    continue;
                }
                if(! (node instanceof SourceFile)){
                    System.err.println("[WS_ERR] CallGraphBuilding: AST node is not SourceFile for job: " + job.source().toString());
                    continue;
                } 
            }
        }
        
        //now do search
        
        callGraph.doDFSMark();
        
        //debug print
        List<WSCallGraphNode> methods = callGraph.getAllParallelMethods();
        
        System.out.println("[WS_INFO] Found Parallel Methods:");
        
        for(WSCallGraphNode node : methods){
            ProcedureDef md = node.getMethodDef();
            System.out.printf("    [%s] %s\n",  node.isContainsConcurrent() ? "C" : "D",
                        md.toString());
            
            for(WSCallGraphNode callerNode : node.getCallers()){
                ProcedureDef cmd = callerNode.getMethodDef();
                System.out.printf("      <-[%s] %s\n",  callerNode.isContainsConcurrent() ? "C" : "D",
                        cmd.toString());     
            }
        }
    }

    public boolean isTargetProcedure(ProcedureDef procedureDef) {
       return callGraph.isParallel(procedureDef);
    }
}
