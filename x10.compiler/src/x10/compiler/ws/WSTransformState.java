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

import polyglot.ast.Call;
import polyglot.ast.CodeBlock;
import polyglot.ast.MethodDecl;

import x10.ExtensionInfo;
import x10.X10CompilerOptions;

/**
 * Record the WS transformation intermediate results and context.
 * 
 * The WSState is globally existed. However, some types need be refreshed
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
public abstract class WSTransformState {

    public enum CallSiteType {
        NORMAL,               //Nothing to be changed to the call
        CONCURRENT_CALL,     //Need use worker, parent frame as parent
        MATCHED_CALL        //The target's def is changed, but not the body
    }

    public enum MethodType {
        NORMAL,   //nothing to be changed
        BODYDEF_TRANSFORMATION, //both body and def need to be transformed
        DEFONLY_TRANSFORMATION, //only def need to be transformed
    }

    // true: __CPP__ macro is defined
    public boolean __CPP__;
    
    // 1: no pc field for FinishFrame and AsyncFrame
    public boolean OPT_PC_FIELD = true;
    
    // 2: no try catch block in FinishFrame and AsyncFrame
    public boolean DISABLE_EXCEPTION_HANDLE = false;
    
    // 3: optimize simple for-async by transform it into divide-and-conquer style
    public boolean OPT_FOR_ASYNC = true;

    protected WSTransformState(ExtensionInfo extensionInfo) {
        X10CompilerOptions options = extensionInfo.getOptions();

        __CPP__ = options.macros.contains("__CPP__");
        DISABLE_EXCEPTION_HANDLE = options.x10_config.WS_DISABLE_EXCEPTION_HANDLE;

        if(DISABLE_EXCEPTION_HANDLE){
            System.out.println("[WS_INFO] Not Generate fast path's exception handling code");
        }
    }

    public boolean isConcurrentCallSite(Call call){ 
        return getCallSiteType(call) == CallSiteType.CONCURRENT_CALL;
    }

    public abstract CallSiteType getCallSiteType(Call call);

    public abstract MethodType getMethodType(CodeBlock codeBlock);
    
    public abstract void addSynthesizedConcurrentMethod(MethodDecl mDecl);
}
