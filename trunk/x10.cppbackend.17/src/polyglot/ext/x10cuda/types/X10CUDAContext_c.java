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
    
    private ClassifiedStream cudaStream = null;
    
    private Closure_c wrappingClosure;

	public X10CUDAContext_c(TypeSystem ts) {
		super(ts);
	}
    
    public ClassifiedStream cudaStream (StreamWrapper sw) {
        if (cudaStream==null) {
            cudaStream = sw.getNewStream("cu");
        }
        return cudaStream;
    }

    public Closure_c getWrappingClosure() {
        return wrappingClosure;
    }

    public void setWrappingClosure(Closure_c wrappingClosure) {
        this.wrappingClosure = wrappingClosure;
    }

}

//vim:tabstop=4:shiftwidth=4:expandtab