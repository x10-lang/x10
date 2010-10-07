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

package x10cuda.visit;

import static x10cpp.visit.Emitter.mangled_non_method_name;
import static x10cpp.visit.SharedVarsMethods.CUDA_NATIVE_STRING;
import static x10cpp.visit.SharedVarsMethods.CPP_NATIVE_STRING;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZATION_BUFFER;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZE_METHOD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_BUFFER;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_ID_FIELD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_MARKER;
import static x10cpp.visit.SharedVarsMethods.SERIALIZE_BODY_METHOD;
import static x10cpp.visit.SharedVarsMethods.THIS;
import static x10cpp.visit.SharedVarsMethods.SAVED_THIS;
import static x10cpp.visit.SharedVarsMethods.chevrons;
import static x10cpp.visit.SharedVarsMethods.make_ref;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.ArrayInit_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Case_c;
import polyglot.ast.Catch_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.ClassBody_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.For_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id_c;
import polyglot.ast.If_c;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Labeled_c;
import polyglot.ast.Local;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.LocalDecl;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NullLit_c;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Receiver;
import polyglot.ast.Return_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.frontend.Compiler;
import x10.ast.AssignPropertyCall_c;
import x10.ast.Await_c;
import x10.ast.Closure;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.ConstantDistMaker_c;
import x10.ast.ForLoop;
import x10.ast.ForLoop_c;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl_c;
import x10.ast.RegionMaker;
import x10.ast.RegionMaker_c;
import x10.ast.StmtSeq;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Cast_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10Formal;
import x10.ast.X10Instanceof_c;
import x10.ast.X10Loop;
import x10.ast.X10Loop_c;
import x10.ast.X10New_c;
import x10.ast.X10Special_c;
import x10.ast.X10Unary_c;
import x10.extension.X10Ext;
import x10.types.ConstrainedType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.postcompiler.CXXCommandBuilder;
import x10cpp.types.X10CPPContext_c;
import x10cpp.visit.Emitter;
import x10cpp.visit.MessagePassingCodeGenerator;
import x10cpp.visit.SharedVarsMethods;
import x10cpp.visit.X10CPPTranslator;
import x10cuda.types.SharedMem;
import x10cuda.types.X10CUDAContext_c;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.VarInstance;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.SimpleCodeWriter;
import polyglot.visit.Translator;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;

/**
 * Visitor that prettyprints an X10 AST to the CUDA subset of c++.
 * 
 * @author Dave Cunningham
 */

public class CUDACodeGenerator extends MessagePassingCodeGenerator {

	private static final String ANN_KERNEL = "x10.compiler.CUDA";
	private static final String ANN_DIRECT_PARAMS = "x10.compiler.CUDADirectParams";

	public CUDACodeGenerator(StreamWrapper sw, Translator tr) {
		super(sw, tr);
	}

	protected String[] getCurrentNativeStrings() {
		if (!generatingKernel())
			return new String[] { CPP_NATIVE_STRING };
		return new String[] { CUDA_NATIVE_STRING, CPP_NATIVE_STRING };
	}

	private X10CUDAContext_c context() {
		return (X10CUDAContext_c) tr.context();
	}

	private X10TypeSystem_c xts() {
		return (X10TypeSystem_c) tr.typeSystem();
	}

	// defer to CUDAContext.cudaStream()
	private ClassifiedStream cudaStream() {
		return context().cudaStream(sw, tr.job());
	}

	private boolean generatingKernel() {
		return context().generatingKernel();
	}

	private void generatingKernel(boolean v) {
		context().generatingKernel(v);
	}

	private Type getType(String name) throws SemanticException {
		return (Type) xts().systemResolver().find(QName.make(name));
	}

	// does the block have the annotation that denotes that it should be
	// split-compiled to cuda?
	private boolean blockIsKernel(Node n) {
		return nodeHasAnnotation(n, ANN_KERNEL);
	}

	// does the block have the annotation that denotes that it should be
	// compiled to use conventional cuda kernel params
	private boolean kernelWantsDirectParams(Node n) {
		return nodeHasAnnotation(n, ANN_DIRECT_PARAMS);
	}

	// does the block have the given annotation
	private boolean nodeHasAnnotation(Node n, String ann) {
		X10Ext ext = (X10Ext) n.ext();
		try {
			return !ext.annotationMatching(getType(ann)).isEmpty();
		} catch (SemanticException e) {
			assert false : e;
			return false; // in case asserts are off
		}
	}

	private String env = "__env";
	
	private static class Complaint extends RuntimeException { }
	
	private void complainIfNot (boolean cond, String exp, Node n, boolean except) {
		complainIfNot2(cond, "Expected: "+exp, n, except);
	}
	private void complainIfNot2 (boolean cond, String exp, Node n, boolean except) {
		if (!cond) {
			tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, exp, n.position());
			if (except) throw new Complaint();
		}
	}
	private void complainIfNot (boolean cond, String exp, Node n) { complainIfNot(cond,exp, n, true); }
	private void complainIfNot2 (boolean cond, String exp, Node n) { complainIfNot2(cond,exp, n, true); }

	private Type arrayCargo(Type typ) {
		if (xts().isArray(typ)) {
			typ = typ.toClass();
			X10ClassType ctyp = (X10ClassType) typ;
			assert ctyp.typeArguments().size() == 1; // Array[T]
			return ctyp.typeArguments().get(0);
		}
		if (xts().isRemoteArray(typ)) {
			typ = typ.toClass();
			X10ClassType ctyp = (X10ClassType) typ;
			assert ctyp.typeArguments().size() == 1; // RemoteRef[Array[T]]
			Type type2 = ctyp.typeArguments().get(0);
			X10ClassType ctyp2 = (X10ClassType) typ;
			assert ctyp2.typeArguments().size() == 1;  // Array[T]
			return ctyp2.typeArguments().get(0);
		}
		return null;

	}

	private boolean isFloatArray(Type typ) {
		Type cargo = arrayCargo(typ);
		return cargo != null && cargo.isFloat();
	}

	private boolean isIntArray(Type typ) {
		Type cargo = arrayCargo(typ);
		return cargo != null && cargo.isInt();
	}

	String prependCUDAType(Type t, String rest) {
		String type = Emitter.translateType(t, true);

		if (isIntArray(t)) {
			type = "x10aux::cuda_array<x10_int> ";
		} else if (isFloatArray(t)) {
			type = "x10aux::cuda_array<x10_float> ";
		} else {
			type = type + " ";
		}

		return type + rest;
	}

	void handleKernel(Block_c b) {
		String kernel_name = context().wrappingClosure();
		sw.write("/* block split-compiled to cuda as " + kernel_name + " */ ");
		System.out.println("Kernel: " + kernel_name);

		ClassifiedStream out = cudaStream();

		// environment (passed into kernel via pointer)
		generateStruct(kernel_name, out, context().kernelParams());

		out.forceNewline();

		boolean ptr = !context().directParams();
		// kernel (extern "C" to disable name-mangling which seems to be
		// inconsistent across cuda versions)
		out.write("extern \"C\" __global__ void " + kernel_name + "("
				+ kernel_name + "_env " + (ptr ? "*" : "") + env + ") {");
		out.newline(4);
		out.begin(0);

		if (ptr) {
			for (VarInstance<?> var : context().kernelParams()) {
				String name = var.name().toString();
				if (name.equals(THIS)) {
					name = SAVED_THIS;
				} else {
					name = Emitter.mangled_non_method_name(name);
				}
				out.write("__shared__ " + prependCUDAType(var.type(), name)
						+ ";");
				out.newline();
			}
			out.write("if (threadIdx.x==0) {");
			out.newline(4);
			out.begin(0);
			for (VarInstance<?> var : context().kernelParams()) {
				String name = var.name().toString();
				if (name.equals(THIS)) {
					name = SAVED_THIS;
				} else {
					name = Emitter.mangled_non_method_name(name);
				}
				out.write(name + " = " + env + "->" + name + ";");
				out.newline();
			}
			out.end();
			out.newline();
			out.write("}");
			out.newline();
			out.write("__syncthreads();");
			out.newline();
			out.forceNewline();
		}

		sw.pushCurrentStream(out);
		context().shm().generateCode(sw, tr);
		sw.popCurrentStream();

		// body
		sw.pushCurrentStream(out);
		super.visit(b);
		sw.popCurrentStream();

		// end
		out.end();
		out.newline();
		out.write("} // " + kernel_name);
		out.newline();

		out.forceNewline();
	}

	private void generateStruct(String kernel_name, SimpleCodeWriter out,
			ArrayList<VarInstance<?>> vars) {
		out.write("struct " + kernel_name + "_env {");
		out.newline(4);
		out.begin(0);
		// emitter.printDeclarationList(out, context(),
		// context().kernelParams());
		for (VarInstance<?> var : vars) {
			String name = var.name().toString();
			if (name.equals(THIS)) {
				name = SAVED_THIS;
			} else {
				name = Emitter.mangled_non_method_name(name);
			}
			out.write(prependCUDAType(var.type(), name) + ";");
			out.newline();
		}
		out.end();
		out.newline();
		out.write("};");
		out.newline();
	}

	// Java cannot return multiple values from a function
	class MultipleValues {
		public Expr max;

		public Name var;

		public Block body;
	}

	protected MultipleValues processLoop(X10Loop loop) {
		MultipleValues r = new MultipleValues();
		Formal loop_formal = loop.formal();
		complainIfNot(loop_formal instanceof X10Formal, "Exploded point syntax", loop);
		X10Formal loop_x10_formal = (X10Formal) loop_formal;
		complainIfNot(loop_x10_formal.hasExplodedVars(), "Exploded point syntax", loop_formal);
		complainIfNot(loop_x10_formal.vars().size() == 1, "A 1 dimensional iteration", loop_formal);
		r.var = loop_x10_formal.vars().get(0).name().id();
		Expr domain = loop.domain();
		complainIfNot(domain instanceof RegionMaker, "An iteration over a region literal of the form 0..", domain);
		RegionMaker region = (RegionMaker) domain;
		complainIfNot(region.name().toString().equals("makeRectangular"), "An iteration over a region literal of the form 0..", domain);
		Receiver target = region.target();
		complainIfNot(target instanceof CanonicalTypeNode, "An iteration over a region literal of the form 0..", target);
		CanonicalTypeNode target_type_node = (CanonicalTypeNode) target;
		complainIfNot(target_type_node.nameString().equals("Region"), "An iteration over a region literal of the form 0..", target);
		complainIfNot(region.arguments().size() == 2, "An iteration over a region literal of the form 0..", region);
		Expr from_ = region.arguments().get(0);
		Expr to_ = region.arguments().get(1);
		complainIfNot(from_ instanceof IntLit, "An iteration over a region literal of the form 0..", from_);
		IntLit from = (IntLit) from_;
		complainIfNot(from.value() == 0, "An iteration over a region literal of the form 0..", from_);
		r.max = to_;
		r.body = (Block) loop.body();
		return r;
	}

	protected MultipleValues processLoop(Block for_block) {

		complainIfNot(for_block.statements().size() == 3, "A 1-dimensional iteration of the form 0..", for_block);

		// test that it is of the form for (blah in region)
		Stmt i_ = for_block.statements().get(0);
		Stmt j_ = for_block.statements().get(1);
		Stmt for_block2 = for_block.statements().get(2);
		complainIfNot(for_block2 instanceof For, "A 1-dimensional iteration of the form 0..", for_block2);

		// error
		For loop = (For) for_block2;
		MultipleValues r = new MultipleValues();
		complainIfNot(loop.inits().size() == 1, "A 1-dimensional iteration of the form 0..", loop);
		// loop inits are not actually used
		complainIfNot(i_ instanceof LocalDecl, "A 1-dimensional iteration of the form 0..", i_);
		LocalDecl i = (LocalDecl) i_;
		complainIfNot(j_ instanceof LocalDecl, "A 1-dimensional iteration of the form 0..", j_);
		LocalDecl j = (LocalDecl) j_;
		complainIfNot(loop.cond() instanceof X10Call, "A 1-dimensional iteration of the form 0..", loop.cond());
		X10Call cond = (X10Call) loop.cond();
		complainIfNot(cond.name().id() == X10Binary_c.binaryMethodName(Binary.LE), "The <= operator", cond);
		List<Expr> args = cond.arguments();
		complainIfNot(args.size() == 2, "The <= operator", cond);
		complainIfNot(args.get(0) instanceof Local, "The LHS of <= to be the loop variable", args.get(0));
		Local cond_left = (Local) args.get(0);
		complainIfNot(args.get(1) instanceof Local, "The RHS of <= to be the max variable", args.get(1));
		Local cond_right = (Local) args.get(1);
		complainIfNot(cond_right.name().id() == j.name().id(), "The RHS of <= to be the max variable", cond_right);
		Expr from_ = i.init();
		Expr to_ = j.init();
		complainIfNot(from_ instanceof IntLit, "An iteration that begins from zero", from_);
															// error
		IntLit from = (IntLit) from_;
		complainIfNot(from.value() == 0, "An iteration that begins from zero", from_);
		r.max = to_;
		complainIfNot(loop.body() instanceof Block, "Body of the loop to be a block",  loop.body());
		Block block = (Block) loop.body();
		complainIfNot(block.statements().size() == 2, "Body of the loop to be a block containing 2 statements",  block);
		Stmt first = block.statements().get(0);
		complainIfNot(first instanceof LocalDecl, "First statement to be a local variable declaration",  first);
		LocalDecl real_var = (LocalDecl) first;
		Stmt second = block.statements().get(1);
		complainIfNot(second instanceof Block, "Second statement to be a block",  second);
		r.body = (Block) second;
		r.var = real_var.name().id();
		return r;
	}
	
	public static boolean checkStaticCall(Stmt s, String class_name, String method_name, int args)
	{
		try {
	        Call async_call = (Call) ((Eval) s).expr();
			if (!async_call.name().toString().equals(method_name)) return false;
	        CanonicalTypeNode async_target_type_node = (CanonicalTypeNode) (async_call.target());
			if (!async_target_type_node.nameString().equals(class_name)) return false;
			if (async_call.arguments().size() != args) return false;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	public void visit(Block_c b) {
        super.visit(b);
        try {
	        if (blockIsKernel(b)) {
	        	Block_c closure_body = b;
	        	//System.out.println(b); // useful for finding out what the frontend is actually giving us
	
				complainIfNot2(!generatingKernel(), "CUDA kernels may not be nested.",  b);
	            // TODO: assert the block is the body of an async
	
				complainIfNot(b.statements().size()>=1, "A block containing at least one statement.",  b);
	            
	            // handle autoblocks/autothreads and constant memory declarations
	            for (int i=0 ; i<b.statements().size()-1 ; ++i) {
	            	Stmt ld_ = b.statements().get(i);
	        		complainIfNot(ld_ instanceof LocalDecl, "val <something> = CUDAUtilities.autoBlocks/autoThreads()",  ld_);
	                LocalDecl s_ = (LocalDecl)ld_;
	                
	                Expr init_expr = s_.init();
	        		complainIfNot(init_expr instanceof X10Call, "val <something> = CUDAUtilities.autoBlocks/autoThreads",  init_expr);
	                X10Call_c init_call = (X10Call_c) init_expr;
	                
	                Receiver init_call_target = init_call.target();
	        		complainIfNot(init_call_target instanceof CanonicalTypeNode, "val <something> = CUDAUtilities.autoBlocks/Vars()",  init_call_target);
	                CanonicalTypeNode init_call_target_node = (CanonicalTypeNode) init_call_target;
	                
	                String classname = init_call_target_node.nameString();
	                int targs = init_call.typeArguments().size();
	                int args = init_call.arguments().size();
	                String methodname = init_call.name().toString();
	                
	                if (classname.equals("CUDAUtilities") && targs==0 && args==0 && methodname.equals("autoBlocks")) {
	        			complainIfNot2(context().autoBlocks() == null, "Already have autoBlocks",  init_call);
	                    context().autoBlocks(s_);
	                    context().established().autoBlocks(s_);
	                } else if (classname.equals("CUDAUtilities") && targs==0 && args==0 && methodname.equals("autoThreads")) {
	        			complainIfNot2(context().autoThreads() == null, "Already have autoThreads",  init_call);
	                    context().autoThreads(s_);
	                    context().established().autoThreads(s_);
	                } else if (classname.equals("Rail") && targs==2 && args==2 && methodname.equals("make")) {
	        			complainIfNot(false, "A call to CUDAUtilities.autoBlocks/autoThreads",  init_call);
	                } else {
	        			complainIfNot(false, "A call to CUDAUtilities.autoBlocks/autoThreads",  init_call);
	                }
	            }
	
				Stmt for_block_ = b.statements().get(b.statements().size()-1);
				complainIfNot(for_block_ instanceof Block, "A loop over CUDA blocks",  for_block_);
	            // error
	            Block for_block = (Block) for_block_;
	
	            MultipleValues outer = processLoop(for_block);
	            // System.out.println("outer loop: "+outer.var+"
	            // "+outer.iterations);
	            b = (Block_c) outer.body;
	
	            Stmt last = b.statements().get(b.statements().size() - 1);
	            //System.out.println(last);
				complainIfNot(last instanceof Block, "A loop over CUDA blocks",  last);
	            Block for_block2 = (Block) last;
	
	            SharedMem shm = new SharedMem();
	            // look at all but the last statement to find shm decls
	            for (Stmt st : b.statements()) {
	                if (st == last)
	                    continue;
	        		complainIfNot(st instanceof LocalDecl, "Shared memory definition",  st);
	                LocalDecl ld = (LocalDecl) st;
	                Expr init_expr = ld.init();
	                // TODO: primitive vals and shared vars
	        		complainIfNot(init_expr instanceof X10New_c, "val <var> = new Array[T](...)",  init_expr);
	                X10New_c init_new = (X10New_c) init_expr;
	                Type instantiatedType = init_new.objectType().type();
	        		complainIfNot(xts().isArray(instantiatedType), "Initialisation expression to have Array[T] type.",  init_new);
	                TypeNode rail_type_arg_node = init_new.typeArguments().get(0);
	                
	                Type rail_type_arg = rail_type_arg_node.type();
	                // TODO: support other types
	        		complainIfNot(rail_type_arg.isFloat(), "An array of type float",  rail_type_arg_node);
	                if (init_new.arguments().size()==2) {
		                Expr num_elements = init_new.arguments().get(0);
		                Expr rail_init_closure = init_new.arguments().get(1);
		                shm.addArrayInitClosure(ld, num_elements, rail_init_closure);
	                } else {
	            		complainIfNot(init_new.arguments().size() == 1, "val <var> = new Array[T](other_array)",  init_new);
		                Expr src_array = init_new.arguments().get(0);
	            		complainIfNot(xts().isArray(src_array.type()) || xts().isRemoteArray(src_array.type()), "SHM to be initialised from array or remote array type",  src_array);
		                shm.addArrayInitArray(ld, src_array);
	                }
	            }
	
	            MultipleValues inner = processLoop(for_block2);
	            // System.out.println("outer loop: "+outer.var+"
	            // "+outer.iterations);
	            b = (Block_c) inner.body;
	
	    		complainIfNot(b.statements().size() == 1, "A block with a single statement",  b);
	    		Stmt async = b.statements().get(0);
	            complainIfNot(checkStaticCall(async,"Runtime","runAsync",1), "An async block",  async);
	            Call async_call = (Call) (((Eval) async).expr());
	            Expr async_arg = async_call.arguments().get(0);
	    		complainIfNot(async_arg instanceof Closure, "An async block",  async_arg);
	            Closure async_closure = (Closure) async_arg;
	    		complainIfNot(async_closure.formals().size() == 0, "An async block",  async_closure);
	            Block async_body = async_closure.body();
	
	            //b = (Block_c) async_body;
	            context().setCUDAKernelCFG(outer.max, outer.var,inner.max, inner.var, shm, kernelWantsDirectParams(closure_body));
	            context().established().setCUDAKernelCFG(outer.max, outer.var,inner.max, inner.var, shm, kernelWantsDirectParams(closure_body));
	            generatingKernel(true);
	            try {
	            	handleKernel((Block_c)async_body);
	            } finally {
	            	generatingKernel(false);
	            }
	        }
        } catch (Complaint e) {
        	// don't bother doing anything more with this kernel,
        	// just try and continue with the code after
        	// (note that we've already done the regular CPU code) 
        }
    }

	public void visit(Closure_c n) {
		context().establishClosure();
		String last = context().wrappingClosure();
		String lastHostClassName = context().wrappingClass();
		X10ClassType hostClassType = (X10ClassType) n.closureDef().typeContainer().get();
		String nextHostClassName = Emitter.translate_mangled_FQN(hostClassType.fullName().toString(), "_");
		String next = getClosureName(nextHostClassName, context().closureId() + 1);
		// System.out.println(last+" goes to "+next);
		context().wrappingClosure(next);
		context().wrappingClass(nextHostClassName);
		super.visit(n);
		context().wrappingClosure(last);
		context().wrappingClass(lastHostClassName);
		// System.out.println("back to "+last);
	}

	protected void generateClosureDeserializationIdDef(ClassifiedStream defn_s,
			String cnamet, List<Type> freeTypeParams, String hostClassName,
			Block block) {
		if (blockIsKernel(block)) {

			X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
			boolean in_template_closure = freeTypeParams.size() > 0;
			if (in_template_closure)
				emitter.printTemplateSignature(freeTypeParams, defn_s);
			defn_s.write("const x10aux::serialization_id_t " + cnamet + "::"
					+ SharedVarsMethods.SERIALIZATION_ID_FIELD + " = ");
			defn_s.newline(4);
			String template = in_template_closure ? "template " : "";
			defn_s.write("x10aux::DeserializationDispatcher::addDeserializer("
					+ cnamet + "::" + template
					+ SharedVarsMethods.DESERIALIZE_METHOD
					+ chevrons("x10::lang::Reference") + ", true, "
					+ cnamet + "::" + template + SharedVarsMethods.DESERIALIZE_CUDA_METHOD + ", "
					+ cnamet + "::" + template + SharedVarsMethods.POST_CUDA_METHOD + ", "
					+ "\"" + hostClassName + ".cubin\", \"" + cnamet + "\");");
			defn_s.newline();
			defn_s.forceNewline();
		} else {
			super.generateClosureDeserializationIdDef(defn_s, cnamet,
					freeTypeParams, hostClassName, block);
		}
	}

	protected void generateClosureSerializationFunctions(X10CPPContext_c c,
			String cnamet, StreamWrapper inc, Block block, List<VarInstance<?>> refs) {
		super.generateClosureSerializationFunctions(c, cnamet, inc, block, refs);

		if (blockIsKernel(block)) {

			ArrayList<VarInstance<?>> env = context().kernelParams();

			generateStruct("__cuda", inc, env);

			inc.write("static void "
					  + SharedVarsMethods.POST_CUDA_METHOD
					  + "("
					  + DESERIALIZATION_BUFFER
					  + " &__buf, x10aux::place __gpu, size_t __blocks, size_t __threads, size_t __shm, size_t argc, char *argv, size_t cmemc, char *cmemv) {");
			inc.newline(4);
			inc.begin(0);

			inc.write("__cuda_env __env;");
			inc.newline();

			if (!kernelWantsDirectParams(block)) {
				inc.write("x10_ulong __remote_env;");
				inc.newline();
				inc.write("::memcpy(&__remote_env, argv, sizeof (void*));");
				inc.newline();
				inc.write("x10aux::remote_free(__gpu, __remote_env);");
				inc.newline();
				//FIXME: any arrays referenced from the env are being leaked here.
				// we need some way to record a copy of the contents of the __env on the host
				// so that we do not have to fetch __remote_env back onto the host
				// then we can free those arrays like in the else branch below
			} else {
				inc.write("::memcpy(&__env, argv, argc);");
				inc.newline();
				for (VarInstance<?> var : env) {
					Type t = var.type();
					String name = var.name().toString();
					if (isIntArray(t) || isFloatArray(t)) {
						if (!xts().isRemoteArray(t)) {
							inc.write("x10aux::remote_free(__gpu, (x10_ulong)(size_t)__env."+name+".raw);");
						}
					}
					inc.newline();
				}
				
			}
			
			inc.end(); inc.newline();
			inc.write("}"); inc.newline();
			
			inc.forceNewline();
			
			inc.write("static void "
					  + SharedVarsMethods.DESERIALIZE_CUDA_METHOD
					  + "("
					  + DESERIALIZATION_BUFFER
					  + " &__buf, x10aux::place __gpu, size_t &__blocks, size_t &__threads, size_t &__shm, size_t &argc, char *&argv, size_t &cmemc, char *&cmemv) {");
			inc.newline(4);
			inc.begin(0);

			inc.write(make_ref(cnamet) + " __this = " + cnamet + "::"
					+ DESERIALIZE_METHOD + "<" + cnamet + ">(__buf);");
			inc.newline();

			for (VarInstance<?> var : env) {
				Type t = var.type();
				String name = var.name().toString();
				inc.write(Emitter.translateType(t, true) + " " + name);
				if (context().autoBlocks() != null && var == context().autoBlocks().localDef().asInstance()) {
					inc.write(";");
				} else if (context().autoThreads() != null && var == context().autoThreads().localDef().asInstance()) {
					inc.write(";");
				} else {
					inc.write(" = __this->" + name + ";");
				}
				inc.newline();
			}

			inc.write("__shm = ");
			inc.begin(0);
			context().shm().generateSize(inc, tr);
			inc.write(";");
			inc.end();
			inc.newline();

			// this is probably broken when only one is given. 
			if (context().autoBlocks()!=null && context().autoThreads()!=null) {
				String bname = context().autoBlocks().name().id().toString();
				String tname = context().autoThreads().name().id().toString();
				inc.write("x10aux::blocks_threads(__gpu, x10aux::DeserializationDispatcher::getMsgType(_serialization_id), __shm, "
                          + bname + ", " + tname + ");");
				inc.newline();
			}
			inc.write("__blocks = (");
			inc.begin(0);
			tr.print(null, context().blocks(), inc);
			inc.write(")+1;");
			inc.end();
			inc.newline();

			inc.write("__threads = (");
			inc.begin(0);
			tr.print(null, context().threads(), inc);
			inc.write(")+1;");
			inc.end();
			inc.newline();
			

			inc.write("__cuda_env __env;");
			inc.newline();

			for (VarInstance<?> var : env) {
				Type t = var.type();
				String name = var.name().toString();

				// String addr = "&(*"+name+")[0]"; // old way for rails
				String addr = "&" + name + "->FMGL(raw).raw()[0]";
				// String rr =
				// "x10aux::get_remote_ref_maybe_null("+name+".operator->())";
				// // old object model
				String rr = "&" + name + "->FMGL(rawData).raw()[0]";

				String ts = null;
				if (isIntArray(t)) {
					ts = "x10_int";
				} else if (isFloatArray(t)) {
					ts = "x10_float";
				}
				
				if (isIntArray(t) || isFloatArray(t)) {
					if (xts().isRemoteArray(t)) {
						inc.write("__env." + name + ".raw = ("+ts+"*)(size_t)"+rr+";");
						inc.newline();
						inc.write("__env." + name + ".size = "+name + "->FMGL(size);");
						inc.newline();
					} else {
						String len = name + "->FMGL(rawLength)";
						String sz = "sizeof("+ts+")*" + len;
						inc.write("__env." + name + ".raw = ("+ts+"*)(size_t)x10aux::remote_alloc(__gpu, "+sz+");");
						inc.newline();
						inc.write("__env." + name + ".size = "+len+";");
						inc.newline();
						inc.write("x10aux::cuda_put(__gpu, (x10_ulong) __env." + name + ".raw, " + addr + ", " + sz + ");");
					}
				} else {
					inc.write("__env." + name + " = " + name + ";");
				}
				inc.newline();
			}

			if (env.isEmpty()) {
				inc.write("argc = 0;");
				inc.end();
				inc.newline();
			} else {
				if (kernelWantsDirectParams(block)) {
					inc.write("memcpy(argv, &__env, sizeof(__env));");
					inc.newline();
					inc.write("argc = sizeof(__env);");
					inc.end();
					inc.newline();
				} else {
					inc.write("x10_ulong __remote_env = x10aux::remote_alloc(__gpu, sizeof(__env));");
					inc.newline();
					inc.write("x10aux::cuda_put(__gpu, __remote_env, &__env, sizeof(__env));");
					inc.newline();
					inc.write("::memcpy(argv, &__remote_env, sizeof (void*));");
					inc.newline();
					inc.write("argc = sizeof(void*);");
					inc.end();
					inc.newline();
				}
			}
			inc.write("}");
			inc.newline();
			inc.forceNewline();
		}
	}

	public void visit(New_c n) {
		complainIfNot2(!generatingKernel(),"New not allowed in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(Assert_c n) {
		complainIfNot2(!generatingKernel(),"Throwing exceptions not allowed in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(Assign_c asgn) {
		// TODO Auto-generated method stub
		super.visit(asgn);
	}

	@Override
	public void visit(AssignPropertyCall_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Await_c n) {
		complainIfNot2(!generatingKernel(),"Await not allowed in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(Binary_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(BooleanLit_c lit) {
		// TODO Auto-generated method stub
		super.visit(lit);
	}

	@Override
	public void visit(Branch_c br) {
		// TODO Auto-generated method stub
		super.visit(br);
	}

	@Override
	public void visit(Case_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Catch_c n) {
		complainIfNot2(!generatingKernel(),"Catching exceptions not allowed in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(CharLit_c lit) {
		// TODO Auto-generated method stub
		super.visit(lit);
	}

	@Override
	public void visit(ClosureCall_c n) {
		complainIfNot2(!generatingKernel(),"Closure calls not allowed in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(Conditional_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Do_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Empty_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Eval_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Field_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(FloatLit_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(For_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(ForLoop_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Formal_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Id_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(If_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Initializer_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(IntLit_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Labeled_c label) {
		// TODO Auto-generated method stub
		super.visit(label);
	}

	@Override
	public void visit(Local_c n) {
		if (generatingKernel()) {
			ClassifiedStream out = cudaStream();
			Name ln = n.name().id();
			if (ln == context().blocksVar()) {
				out.write("blockIdx.x");
			} else if (ln == context().threadsVar()) {
				out.write("threadIdx.x");
			} else if (context().shm().has(ln)) {
				out.write(ln.toString());
			} else if (context().isKernelParam(ln)) {
				// it seems the post-compiler is not good at hoisting these
				// accesses so we do it ourselves
				if (context().directParams()) {
					out.write(env + "." + ln);
				} else {
					out.write(ln.toString());
				}
			} else {
				super.visit(n);
			}
		} else {
			// we end up here in the _deserialize_cuda function because
			// generatingKernel() is false
			Name ln = n.name().id();
			if (context().autoBlocks() != null
					&& ln == context().autoBlocks().name().id()) {
				sw.write(context().autoBlocks().name().id().toString());
			} else if (context().autoThreads() != null
					&& ln == context().autoThreads().name().id()) {
				sw.write(context().autoThreads().name().id().toString());
			} else {
				super.visit(n);
			}
		}
	}

	@Override
	public void visit(NullLit_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Return_c ret) {
		// TODO Auto-generated method stub
		super.visit(ret);
	}

	@Override
	public void visit(StringLit_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(SubtypeTest_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Switch_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(SwitchBlock_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Throw_c n) {
		complainIfNot2(!generatingKernel(),"Throwing exceptions not allowed in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(Try_c n) {
		complainIfNot2(!generatingKernel(),"Catching exceptions not allowed in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(Tuple_c c) {
		// TODO Auto-generated method stub
		super.visit(c);
	}

	@Override
	public void visit(TypeDecl_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(Unary_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(While_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(X10Binary_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(X10Call_c n) {
		// In fact they are allowed, as long as they are implemented with
		// @Native
		super.visit(n);
	}

	@Override
	public void visit(X10CanonicalTypeNode_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(X10Cast_c c) {
		// TODO Auto-generated method stub
		super.visit(c);
	}

	@Override
	public void visit(X10ClassDecl_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(X10Instanceof_c n) {
		complainIfNot2(!generatingKernel(),"Runtime types not available in @CUDA code.", n, false);
		super.visit(n);
	}

	@Override
	public void visit(X10Special_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	@Override
	public void visit(X10Unary_c n) {
		// TODO Auto-generated method stub
		super.visit(n);
	}

	public static boolean postCompile(X10CPPCompilerOptions options,
			Compiler compiler, ErrorQueue eq) {
		// TODO Auto-generated method stub
		if (options.post_compiler != null && !options.output_stdout) {
			Collection<String> compilationUnits = options.compilationUnits();
			String[] nvccCmd = { "nvcc", "--cubin",
					"-I" + CXXCommandBuilder.X10_DIST + "/include", null };
			for (String f : compilationUnits) {
				if (f.endsWith(".cu")) {
					nvccCmd[3] = f;
					if (!X10CPPTranslator.doPostCompile(options, eq,
							compilationUnits, nvccCmd, true)) {
						eq.enqueue(ErrorInfo.WARNING,
		        				   "Found @CUDA annotation, but not compiling for GPU because nvcc could not be run (check your $PATH).");
						return true;
					}
				}
			}

		}

		return true;
	}

} // end of CUDACodeGenerator

// vim:tabstop=4:shiftwidth=4:expandtab
