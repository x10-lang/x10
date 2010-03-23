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

package x10cuda.types;

/**
 * This class extends the X10 notion of Context to keep track of
 * the translation state for the C++ backend.
 *
 * @author Dave Cunningham
 */

import java.util.ArrayList;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.frontend.Job;
import x10.ast.Closure_c;
import x10cpp.types.X10CPPContext_c;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;

public class X10CUDAContext_c extends X10CPPContext_c {

    public X10CUDAContext_c(TypeSystem ts) {
        super(ts);
    }
        
    private String wrappingClosure;
    public String wrappingClosure() { return wrappingClosure; }
    public void wrappingClosure(String v) { wrappingClosure = v; }

    private String wrappingClass;
    public String wrappingClass() { return wrappingClass; }
    public void wrappingClass(String v) { wrappingClass = v; }
    
    private boolean generatingKernel;
    public boolean generatingKernel() { return generatingKernel; }
    public void generatingKernel(boolean v) { generatingKernel = v; }
    
    private Expr blocks; public Expr blocks() { return blocks; }
    private Expr threads; public Expr threads() { return threads; }
    private Name blocksVar; public Name blocksVar() { return blocksVar; }
    private Name threadsVar; public Name threadsVar() { return threadsVar; }
    private SharedMem shm; public SharedMem shm() { return shm; }
    private boolean directParams; public boolean directParams() { return directParams; }
    private ArrayList<VarInstance> kernelParams; public ArrayList<VarInstance> kernelParams() { return kernelParams; }
    public void setCUDAKernelCFG(Expr blocks, Name blocksVar, Expr threads, Name threadsVar, SharedMem shm, boolean directParams) {
        this.blocks = blocks;
        this.blocksVar = blocksVar;
        this.threads = threads;
        this.threadsVar = threadsVar;
        this.shm = shm;
        this.kernelParams = variables();
        this.directParams = directParams;
        if (autoBlocks!=null)
            this.kernelParams.add(autoBlocks.localDef().asInstance());
        if (autoThreads!=null)
            this.kernelParams.add(autoThreads.localDef().asInstance());
    }
    public boolean isKernelParam(Name n) {
        for (VarInstance i : kernelParams) {
            if (i.name()==n) return true;
        }
        return false;
    }


    private ClassifiedStream cudaStream = null;

    public ClassifiedStream cudaStream (StreamWrapper sw, Job j) {
        if (cudaStream==null) {
            cudaStream = sw.getNewStream("cu");
            j.compiler().outputFiles().add(wrappingClass()+".cu");
            cudaStream.write("#include <x10aux/config.h>"); cudaStream.newline();
            cudaStream.write("#include <cfloat>"); cudaStream.newline();
            cudaStream.forceNewline();
            cudaStream.write("extern __shared__ char __shm[];"); cudaStream.newline();
            cudaStream.write("extern __constant__ char __cmem[64*1024];"); cudaStream.newline();
            cudaStream.forceNewline();
        }
        return cudaStream;
    }
    
    X10CUDAContext_c established;
    public void establishClosure() { established = this; }
    public X10CUDAContext_c established() { return established; }

    LocalDecl autoBlocks;
    public void autoBlocks(LocalDecl v) { this.autoBlocks = v; }
    public LocalDecl autoBlocks() { return this.autoBlocks; }

    LocalDecl autoThreads;
    public void autoThreads(LocalDecl v) { this.autoThreads = v; }
    public LocalDecl autoThreads() { return this.autoThreads; }

}

//vim:tabstop=4:shiftwidth=4:expandtab
