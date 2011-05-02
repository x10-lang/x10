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
 * the translation state for the CUDA backend.
 *
 * @author Dave Cunningham
 */

import java.util.ArrayList;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.frontend.Job;
import x10.ast.Closure_c;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.types.X10CPPContext_c;
import x10cuda.ast.CUDAKernel;
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
    
    private ArrayList<VarInstance<?>> kernelParams; public ArrayList<VarInstance<?>> kernelParams() { return kernelParams; }
    public void initKernelParams() {
        this.kernelParams = variables();
        if (cudaKernel.autoBlocks!=null)
            this.kernelParams.add(cudaKernel.autoBlocks.localDef().asInstance());
        if (cudaKernel.autoThreads!=null)
            this.kernelParams.add(cudaKernel.autoThreads.localDef().asInstance());
    }
    public boolean isKernelParam(Name n) {
        for (VarInstance<?> i : kernelParams) {
            if (i.name()==n) return true;
        }
        return false;
    }
	


    private ClassifiedStream cudaStream = null;

    public ClassifiedStream cudaStream (StreamWrapper sw, Job j) {
    	if (firstKernel()) {
    		ClassifiedStream cudaStream = sw.getNewStream("cu" );
        	firstKernel(false);
            j.compiler().addOutputFile(wrappingClass()+".x10", wrappingClass()+".cu");
            ((X10CPPCompilerOptions)j.extensionInfo().getOptions()).compilationUnits().add(wrappingClass()+".cu");
            cudaStream.write("#include <x10aux/config.h>"); cudaStream.newline();
            cudaStream.write("#include <x10aux/cuda_kernel.cuh>"); cudaStream.newline();
            cudaStream.forceNewline();
            // shm type must not be char[], this causes a bug in nvcc
            cudaStream.write("extern __shared__ x10_int __shm[];"); cudaStream.newline();
            cudaStream.write("__constant__ x10_int __cmem[64*1024/4];"); cudaStream.newline();
            cudaStream.forceNewline();
    	}
        if (cudaStream==null) {
            cudaStream = sw.getNewStream("cu", false);
        }
        return cudaStream;
    }
    
    // The idea here is that when visiting the inner statement, we modify the context so that the closure visit (earlier in the stack) can receive the CUDAData object
    // and the kernelParams stuff
    // This is for generateClosureSerializationFunctions, where this info is needed to generate code in the inc / cc files
    X10CUDAContext_c established;
    public void establishClosure() { established = this; }
    public X10CUDAContext_c established() { return established; }

    // This var is used to iterate over indexes in shm arrays in the shm initialisation codegen
    Formal shmIterationVar;
    public void shmIterationVar(Formal v) { shmIterationVar = v; }
    public Formal shmIterationVar() { return shmIterationVar; }

    // The first kernel emitted in a class will have some #include codegen output before it
    boolean firstKernel[] = new boolean[]{false};
	public void firstKernel(boolean b) { firstKernel[0] = b; }
	public boolean firstKernel() { return firstKernel[0]; }
	
	private CUDAKernel cudaKernel;
	public CUDAKernel cudaKernel() { return cudaKernel; } 
	public void cudaKernel(CUDAKernel cudaKernel) { this.cudaKernel = cudaKernel; }
	
}

//vim:tabstop=4:shiftwidth=4:expandtab
