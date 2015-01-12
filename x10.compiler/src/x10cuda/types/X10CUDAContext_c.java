/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10cuda.types;

/**
 * This class extends the X10 notion of Context to keep track of
 * the translation state for the CUDA backend.
 *
 * @author Dave Cunningham
 */

import static x10cpp.visit.Emitter.mangled_non_method_name;

import java.util.ArrayList;
import java.util.HashMap;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.frontend.Job;
import x10.ast.Closure_c;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.types.X10CPPContext_c;
import x10cpp.visit.Emitter;
import x10cpp.visit.X10CPPTranslator;
import x10cuda.ast.CUDAKernel;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;

public class X10CUDAContext_c extends X10CPPContext_c {

    public X10CUDAContext_c(TypeSystem ts) {
        super(ts);
    }
        
    private String wrappingClosure;
    public String wrappingClosure() { return wrappingClosure; }
    public void wrappingClosure(String v) { wrappingClosure = v; }

    private X10ClassDef wrappingClass;
    public X10ClassDef wrappingClass() { return wrappingClass; }
    public void wrappingClass(X10ClassDef v) { wrappingClass = v; }
    
    private boolean generatingCUDACode;
    public boolean generatingCUDACode() { return generatingCUDACode; }
    public void generatingCUDACode(boolean v) { generatingCUDACode = v; }
    
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
	


    private HashMap<String,ClassifiedStream> cudaStreams = new HashMap<String,ClassifiedStream>();
    boolean[] classEmitted = new boolean[]{false};

    private void initStreams (StreamWrapper sw, Job j) {
        j.compiler().addOutputFile(wrappingClass()+".x10", wrappingClass()+".cu");

        ClassifiedStream fileHeader = sw.getNewStream("cu" );
        cudaStreams.put("fileHeader", fileHeader);
        cudaStreams.put("fileHeader", fileHeader);
        cudaStreams.put("classHeader", sw.getNewStream("cu",false));
        cudaStreams.put("classBody", sw.getNewStream("cu",false));
        cudaStreams.put("classFooter", sw.getNewStream("cu",false));
        cudaStreams.put("kernels", sw.getNewStream("cu",false));
                
        ((X10CPPCompilerOptions)j.extensionInfo().getOptions()).compilationUnits().add(sw.getStreamName("cu"));
        
        fileHeader.write("#include <x10aux/config.h>"); fileHeader.newline();
        fileHeader.write("#include <x10aux/cuda_kernel.cuh>"); fileHeader.newline();
        fileHeader.forceNewline();

        // shm type must not be char[], this causes a bug in nvcc
        fileHeader.write("extern __shared__ x10_int __shm[];"); fileHeader.newline();
        fileHeader.write("__constant__ x10_int __cmem[64*1024/4];"); fileHeader.newline();
        fileHeader.forceNewline();
    }

	public ClassifiedStream cudaKernelStream (StreamWrapper sw, Job j) {
    	ClassifiedStream s = cudaStreams.get("kernels");
    	if (s == null) {
    		initStreams(sw, j);
        	// now it will work
    		s = cudaStreams.get("kernels");
    	}
    	return s;
    }
    
	public ClassifiedStream cudaClassBodyStream (StreamWrapper sw, Job j) {
    	ClassifiedStream b = cudaStreams.get("classBody");
    	if (b == null) {
        	initStreams(sw, j);
    		b = cudaStreams.get("classBody");
    	}
    	ClassifiedStream h = cudaStreams.get("classHeader");
    	ClassifiedStream f = cudaStreams.get("classFooter");
    	if (!classEmitted[0]) {
    		classEmitted[0] = true;
    		if (wrappingClass.package_()!=null) {
    			QName pkg_name = wrappingClass.package_().get().fullName();
    			Emitter.openNamespaces(h, pkg_name); h.newline();
    		}
		    String class_name = mangled_non_method_name(wrappingClass().name().toString());
		    h.writeln("class "+class_name+" {");
		    h.writeln("    public:");
		    f.writeln("};");
    		if (wrappingClass.package_()!=null) {
    			QName pkg_name = wrappingClass.package_().get().fullName();
		    	Emitter.closeNamespaces(f, pkg_name); f.newline();
    		}
    		f.forceNewline();
    	}
		return b;
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
	
	boolean inCUDAFunction = false;
	public void inCUDAFunction (boolean v) { inCUDAFunction = v; }
	public boolean inCUDAFunction() { return inCUDAFunction; }
	
}

//vim:tabstop=4:shiftwidth=4:expandtab
