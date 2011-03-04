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

import polyglot.ast.Formal;
import polyglot.types.TypeSystem;
import x10.ast.Closure_c;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;
import x10cpp.types.X10CPPContext_c;

public class X10CUDAContext_c extends X10CPPContext_c {

    public X10CUDAContext_c(TypeSystem ts) {
        super(ts);
    }
        
    private Closure_c wrappingClosure;
    public Closure_c wrappingClosure() { return wrappingClosure; }
    public void wrappingClosure(Closure_c v) { wrappingClosure = v; }

    private boolean generatingCuda;
    public boolean generatingCuda() { return generatingCuda; }
    public void generatingCuda(boolean v) { generatingCuda = v; }
    
    private long blocks; public long blocks() { return blocks; }
    private long threads; public long threads() { return threads; }
    private Formal blocksVar; public Formal blocksVar() { return blocksVar; }
    private Formal threadsVar; public Formal threadsVar() { return threadsVar; }
    public void setCudaKernelCFG(long blocks, Formal blocksVar, long threads, Formal threadsVar) {
        this.blocks = blocks;
        this.blocksVar = blocksVar;
        this.threads = threads;
        this.threadsVar = threadsVar;
    }

    private SharedMem shm;
    public SharedMem shm() { return shm; }
    public void shm(SharedMem v) { shm = v; }

    private ClassifiedStream cudaStream = null;

    public ClassifiedStream cudaStream (StreamWrapper sw) {
        if (cudaStream==null) {
            cudaStream = sw.getNewStream("cu");
        }
        return cudaStream;
    }

    
}

//vim:tabstop=4:shiftwidth=4:expandtab
