/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10cuda.types;

/**
 * This class extends the X10 notion of Context to keep track of
 * the translation state for the C++ backend.
 *
 * @author Dave Cunningham
 */

import java.util.ArrayList;

import polyglot.ast.Formal;
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
        
    private Closure_c wrappingClosure;
    public Closure_c wrappingClosure() { return wrappingClosure; }
    public void wrappingClosure(Closure_c v) { wrappingClosure = v; }

    private boolean generatingKernel;
    public boolean generatingKernel() { return generatingKernel; }
    public void generatingKernel(boolean v) { generatingKernel = v; }
    
    private long blocks; public long blocks() { return blocks; }
    private long threads; public long threads() { return threads; }
    private Name blocksVar; public Name blocksVar() { return blocksVar; }
    private Name threadsVar; public Name threadsVar() { return threadsVar; }
    private SharedMem shm; public SharedMem shm() { return shm; }
    private ArrayList<VarInstance> kernelParams; public ArrayList<VarInstance> kernelParams() { return kernelParams; }
    public void setCudaKernelCFG(long blocks, Name blocksVar, long threads, Name threadsVar, SharedMem shm) {
        this.blocks = blocks;
        this.blocksVar = blocksVar;
        this.threads = threads;
        this.threadsVar = threadsVar;
        this.shm = shm;
        this.kernelParams = variables();
    }
    public boolean isKernelParam(Name n) {
        for (VarInstance i : kernelParams) {
            if (i.name()==n) return true;
        }
        return false;
    }


    private ClassifiedStream cudaStream = null;

    public ClassifiedStream cudaStream (StreamWrapper sw) {
        if (cudaStream==null) {
            cudaStream = sw.getNewStream("cu");
            cudaStream.write("#include <x10aux/config.h>");
            cudaStream.newline();
            cudaStream.write("#include <cfloat>");
            cudaStream.newline();
            cudaStream.forceNewline();
        }
        return cudaStream;
    }
    
}

//vim:tabstop=4:shiftwidth=4:expandtab
