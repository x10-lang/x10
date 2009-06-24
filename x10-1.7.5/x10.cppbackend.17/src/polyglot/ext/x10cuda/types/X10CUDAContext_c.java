/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10cuda.types;

/**
 * This class extends the X10 notion of Context to keep track of
 * the translation state for the C++ backend.
 *
 * @author Dave Cunningham
 */
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.types.TypeSystem;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;

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

    private ClassifiedStream cudaStream = null;

    public ClassifiedStream cudaStream (StreamWrapper sw) {
        if (cudaStream==null) {
            cudaStream = sw.getNewStream("cu");
        }
        return cudaStream;
    }

    
}

//vim:tabstop=4:shiftwidth=4:expandtab