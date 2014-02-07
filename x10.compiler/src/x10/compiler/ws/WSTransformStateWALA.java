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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.CodeBlock;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.MethodDef;

import polyglot.types.ProcedureDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.X10CompilerOptions;
import x10.ast.Closure;
import x10.ast.X10ClassDecl;
import x10.ast.X10MethodDecl;
import x10.compiler.ws.util.WSCallGraph;
import x10.compiler.ws.util.WSCallGraphNode;
import x10.compiler.ws.util.WSTransformationContent;
import x10.compiler.ws.util.WSUtil;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
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
public class WSTransformStateWALA extends WSTransformState {
    protected WSTransformationContent transTarget;

    /**
     * Used by WALA call graph builder.
     * @param ts
     * @param nf
     * @param transTarget
     */
    public WSTransformStateWALA(ExtensionInfo extensionInfo, WSTransformationContent transTarget){
        super(extensionInfo);
        this.transTarget = transTarget;
    }

    public CallSiteType getCallSiteType(Call call){
        return transTarget.getCallSiteType(call);
    }

    public MethodType getMethodType(CodeBlock codeBlock){
        return transTarget.getMethodType(codeBlock);
    }


    public void addSynthesizedConcurrentMethod(MethodDecl mDecl) {
        try{
            //FIXME: fix here
            WSUtil.err("WALA Call Graph Builder doesn't support the function yet", mDecl);
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
