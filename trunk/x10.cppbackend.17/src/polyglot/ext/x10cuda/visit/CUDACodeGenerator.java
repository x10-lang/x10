/*
 *
 * (C) Copyright IBM Corporation 2006, 2007, 2008, 2009
 *
 *  This file is part of X10 Language.
 *
 */

/*
 * Created on Feb 24 2009
 *
 */

package polyglot.ext.x10cuda.visit;

import polyglot.ast.Node;
import polyglot.ast.Block_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10cpp.visit.MessagePassingCodeGenerator;
import polyglot.ext.x10cuda.types.X10CUDAContext_c;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.visit.Translator;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;

/**
 * Visitor that prettyprints an X10 AST to the CUDA subset of c++.
 *
 * @author Dave Cunningham
 */

public class CUDACodeGenerator extends MessagePassingCodeGenerator {
	
	private static final String CUDA_ANNOTATION = "x10.compiler.Cuda";

    public CUDACodeGenerator(StreamWrapper sw, Translator tr) {
        super(sw,tr);
    }

    
    private X10CUDAContext_c context() {
        return (X10CUDAContext_c) tr.context();
    }
    
    // defer to CUDAContext.cudaStream()
    private ClassifiedStream cudaStream() {
        return context().cudaStream(sw);
    }

    
    private boolean nodeHasCudaAnnotation(Node n) {
        X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
        X10Ext ext = (X10Ext) n.ext();
        try {
            Type cudable = (Type) xts.systemResolver().find(QName.make(CUDA_ANNOTATION));
            return !ext.annotationMatching(cudable).isEmpty();
        } catch (SemanticException e) {
            assert false : e;
        	return false; // in case asserts are off
        }
    }
    
    void handleCuda(Block_c b) {
        ClassifiedStream out = cudaStream();
        String kernel_name = "my_happy_kernel";
        out.write("__global__ void "+kernel_name+"() {"); out.newline(4); out.begin(0);
        sw.pushCurrentStream(out);
        super.visit(b);
        sw.popCurrentStream();
        out.end() ; out.newline();
        out.write("}"); out.newline();
        out.forceNewline();
        out.forceNewline();
    }

    public void visit(Closure_c n) {
        Closure_c last = context().getWrappingClosure();
        context().setWrappingClosure(n);
        super.visit(n);
        context().setWrappingClosure(last);
    }
    
    public void visit(Block_c b) {
        if (nodeHasCudaAnnotation(b)) {
            sw.write("/*CUDA block*/ ");
            handleCuda(b);
        }
        super.visit(b);
    }


} // end of CUDACodeGenerator

// vim:tabstop=4:shiftwidth=4:expandtab
