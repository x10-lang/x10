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

package x10cuda.visit;

import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.util.ErrorInfo;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Closure;
import x10.ast.Finish;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10Formal;
import x10.ast.X10Loop;
import x10.ast.X10New;
import x10.ast.X10New_c;
import x10.extension.X10Ext;
import x10.types.MethodInstance;
import x10.types.TypeParamSubst;
import x10.types.X10ClassType;
import x10.visit.NodeTransformingVisitor;
import x10.visit.Reinstantiator;
import x10cpp.visit.Emitter;
import x10cuda.ast.CUDAKernel;
import x10cuda.types.CUDAData;
import x10cuda.types.SharedMem;

public class CUDAPatternMatcher extends ContextVisitor {
	
	private static final String ANN_KERNEL = "x10.compiler.CUDA";
	private static final String ANN_DIRECT_PARAMS = "x10.compiler.CUDADirectParams";
	
	public CUDAPatternMatcher(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	private TypeSystem_c xts() {
		return (TypeSystem_c)ts;
	}

	// Type from name
	private Type getType(String name) throws SemanticException {
		return xts().systemResolver().findOne(QName.make(name));
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
	
	private static class Complaint extends RuntimeException {
	}

	private void complainIfNot(boolean cond, String exp, Node n, boolean except) {
		complainIfNot2(cond, "@CUDA Expected: " + exp, n, except);
	}

	private void complainIfNot2(boolean cond, String exp, Node n, boolean except) {
		if (!cond) {
			job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, exp, n.position());
			if (except)
				throw new Complaint();
		}
	}

	private void complainIfNot(boolean cond, String exp, Node n) {
		complainIfNot(cond, exp, n, true);
	}

	private void complainIfNot2(boolean cond, String exp, Node n) {
		complainIfNot2(cond, exp, n, true);
	}

	// If type is (Global|CUDAConstant)?Rail[T] then return T else return null
	private Type arrayCargo(Type typ) {
		if (!xts().isUnknown(typ) && (xts().isRail(typ) || xts().isGlobalRail(typ) || xts().isCUDAConstantRail(typ))) {
			typ = typ.toClass();
			X10ClassType ctyp = (X10ClassType) typ;
			assert ctyp.typeArguments() != null && ctyp.typeArguments().size() == 1 : typ; // Rail[T]
			return ctyp.typeArguments().get(0);
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

	// Java cannot return multiple values from a function
	class MultipleValues {
		public Expr max;

		public Formal var;

		public Block body;
	}

	protected MultipleValues processLoop(Block b) {
		Node loop_ = b.statements().get(0);
		complainIfNot(loop_ instanceof X10Loop,
				"A 1-dimensional iteration of the form 0..", loop_);
		X10Loop loop = (X10Loop) loop_;

		MultipleValues r = new MultipleValues();
		Formal loop_formal = loop.formal();
		complainIfNot(loop_formal instanceof X10Formal,
				"named loop formal", loop);
		X10Formal loop_x10_formal = (X10Formal) loop_formal;
		r.var = loop_x10_formal;
		Expr domain = loop.domain();
		complainIfNot(domain instanceof Binary,
				"An iteration over a int range literal of the form 0..", domain);
		Binary region = (Binary) domain;
		complainIfNot(region.operator() == Binary.DOT_DOT,
				"An iteration over an int range literal of the form 0..", domain);
		MethodInstance mi = region.methodInstance();
		ClassType target_type = mi.container().toClass();
		complainIfNot(target_type.isInt(),
				"An iteration over an int range literal of the form 0..", domain);
		Expr from_ = region.left();
		Expr to_ = region.right();
		complainIfNot(from_ instanceof IntLit,
				"An iteration over an int range literal of the form 0..", from_);
		IntLit from = (IntLit) from_;
		complainIfNot(from.value() == 0,
				"An iteration over an int range literal of the form 0..", from_);
		r.max = to_;
		r.body = (Block) loop.body();
		return r;
	}

	public static boolean checkStaticCall(Expr e, String class_name, String method_name, int args) {
		try {
			Call call = (Call) e;
			if (!call.name().toString().equals(method_name))
				return false;
			CanonicalTypeNode async_target_type_node = (CanonicalTypeNode) (call
					.target());
			if (!async_target_type_node.nameString().equals(class_name))
				return false;
			if (call.arguments().size() != args)
				return false;
		} catch (ClassCastException exc) {
			return false;
		}
		return true;
	}

	public static boolean checkStaticCall(Stmt s, String class_name, String method_name, int args) {
		try {
			Expr e = ((Eval) s).expr();
			return checkStaticCall(e, class_name, method_name, args);
		} catch (ClassCastException e) {
			return false;
		}
	}

	public static Stmt checkFinish(Stmt s, boolean clocked) {
		try {
			Finish s1 = (Finish) s;
			if (s1.clocked() != clocked) return null;
			return s1.body();
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Block checkAsync(Stmt s, boolean clocked) {
		try {
			Async s1 = (Async) s;
			if (s1.clocked() != clocked) return null;
			return (Block) s1.body();
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	public Node leaveCall(Node parent, Node old, Node child, NodeVisitor visitor) {
		if (child instanceof Block) {
			Block b = (Block) child;
			if (blockIsKernel(b)) {
				try {
					//System.out.println("Got kernel: ");
					//parent.prettyPrint(System.out);
					//System.out.println();
					Reinstantiator reinstantiator = new Reinstantiator(TypeParamSubst.IDENTITY);
					Block kernel_block = (Block) b.visit(new NodeTransformingVisitor(job, ts, nf, reinstantiator).context(context()));
					boolean direct = kernelWantsDirectParams(b);
					complainIfNot2(parent instanceof AtStmt, "@CUDA annotation must be on an at body", kernel_block);
					
	
					// if there are no autoblocks/threads statemnets, this will be 1
					complainIfNot(kernel_block.statements().size() >= 1, "A block containing at least one statement.", kernel_block);
	
					LocalDecl autoBlocks = null, autoThreads = null;
					
					// handle autoblocks/autothreads and constant memory
					// declarations
					SharedMem cmem = new SharedMem();
					for (int i = 0; i < kernel_block.statements().size() - 1; ++i) {
						Stmt ld_ = kernel_block.statements().get(i);
						complainIfNot(ld_ instanceof LocalDecl, "val <something> = CUDAUtilities.autoBlocks/Threads() or CUDAConstantRail definition", ld_);
						LocalDecl ld = (LocalDecl) ld_;
						
						Type decl_type = ld.type().type();

						// [DC] probably because of a type error in the input
						if (xts().isUnknown(decl_type)) continue;
						
						Expr init_expr = ld.init();

						if (xts().isCUDAConstantRail(decl_type)) {
							complainIfNot(init_expr instanceof X10New_c, "val <something> = CUDAConstantRail(arr)", ld);
							X10New_c init_call = (X10New_c)init_expr;
							Type constr_type = Types.baseType(init_call.type());
							complainIfNot(xts().isCUDAConstantRail(constr_type), "val <something> = CUDAConstantRail(arr)", ld);
							complainIfNot(init_call.arguments().size() == 1, "val <something> = CUDAConstantRail(arr)", ld);
							Expr constr_arg = init_call.arguments().get(0);
							complainIfNot(constr_arg instanceof Local, "val <something> = CUDAConstantRail(arr)", ld);
							Local arr = (Local) constr_arg;
							Type cargo = arrayCargo(decl_type);
							
							cmem.addArrayInitArray((LocalDecl) setReachable(ld), arr,  Emitter.translateType(cargo, true));
							
						} else if (decl_type.typeEquals(xts().Int(), context())) {
							complainIfNot(init_expr instanceof X10Call, "val <something> = CUDAUtilities.autoBlocks/Threads()", ld);

							//blocks/threads
							
							X10Call init_call = (X10Call) init_expr;							
							Receiver init_call_target = init_call.target();
							if (init_call_target instanceof CanonicalTypeNode) {
								CanonicalTypeNode init_call_target_node = (CanonicalTypeNode) init_call_target;
								
								String classname = init_call_target_node.nameString();
								int targs = init_call.typeArguments().size();
								int args = init_call.arguments().size();
								String methodname = init_call.name().toString();
			
								if (classname.equals("CUDAUtilities") && targs == 0 && args == 0 && methodname.equals("autoBlocks")) {
									complainIfNot2(autoBlocks == null, "@CUDA: Already have autoBlocks", init_call);
									autoBlocks = ld.init(null);
								} else if (classname.equals("CUDAUtilities") && targs == 0 && args == 0 && methodname.equals("autoThreads")) {
									complainIfNot2(autoThreads == null, "@CUDA: Already have autoThreads", init_call);
									autoThreads = ld.init(null);
								} else {
									complainIfNot(false, "A call to CUDAUtilities.autoBlocks/autoThreads", init_call);
								}
							}
							
						} else {
							complainIfNot(
									false,
									"val <something> = CUDAUtilities.autoBlocks/Threads() or CUDAConstantRail definition",
									ld);
						}
					}
	
					Stmt finish = kernel_block.statements().get(kernel_block.statements().size() - 1);
					Stmt finish_body = checkFinish(finish, false);
					complainIfNot(finish_body != null, "A finish statement", finish);
					complainIfNot(finish_body instanceof Block,
							"A single loop at the root of the kernel", finish_body);
					Block finish_body_block = (Block) finish_body;
					complainIfNot(finish_body_block.statements().size()==1,
							"A single loop at the root of the CUDA kernel", finish_body_block);
	
					MultipleValues outer = processLoop(finish_body_block);
					Block outer_b = (Block) outer.body;
	
					outer_b = (Block) checkAsync(outer_b.statements().get(0), false);
					complainIfNot(outer_b != null, "An async for the block", outer.body);
	
					Stmt last = outer_b.statements().get(outer_b.statements().size() - 1);
	
					SharedMem shm = new SharedMem();
					// look at all but the last statement to find shm decls
					for (Stmt st : outer_b.statements()) {
						if (st == last)
							continue;
						complainIfNot(st instanceof LocalDecl,
								"Shared memory definition", st);
						LocalDecl ld = (LocalDecl) st;
						Expr init_expr = ld.init();
						// TODO: primitive vals and shared vars
						complainIfNot(init_expr instanceof X10New,
								"val <var> = new Rail[T](...)", init_expr);
						X10New init_new = (X10New) init_expr;
						Type instantiatedType = init_new.objectType().type();
						complainIfNot(xts().isRail(instantiatedType),
								"Initialisation expression to have Rail[T] type.",
								init_new);
						TypeNode rail_type_arg_node = init_new.typeArguments().get(
								0);
	
						Type rail_type_arg = rail_type_arg_node.type();
						String rail_type_arg_ = Emitter.translateType(rail_type_arg, true);
						// TODO: support other types
						if (init_new.arguments().size() == 2) {
							Expr num_elements = init_new.arguments().get(0);
							Expr rail_init_closure = init_new.arguments().get(1);
							shm.addArrayInitClosure((LocalDecl) setReachable(ld), (Expr) setReachable(num_elements),
									(Expr) setReachable(rail_init_closure), rail_type_arg_);
						} else {
							complainIfNot(init_new.arguments().size() == 1,
									"val <var> = new Rail[T](other_array)",
									init_new);
							Expr src_array = init_new.arguments().get(0);
							complainIfNot(
									xts().isRail(src_array.type())
											|| xts().isGlobalRail(src_array.type()),
									"SHM to be initialised from array or remote array type",
									src_array);
							shm.addArrayInitArray((LocalDecl) setReachable(ld), (Expr) setReachable(src_array), rail_type_arg_);
						}
					}
	
					Stmt for_block2_ = checkFinish(last, true);
					complainIfNot(for_block2_ != null,
							"A clocked finish statement", last);
					complainIfNot(for_block2_ instanceof Block,
							"A loop over CUDA threads", for_block2_);
					Block for_block2 = (Block) for_block2_;
					MultipleValues inner = processLoop(for_block2);
					Block inner_b = inner.body;
	
					complainIfNot(inner_b.statements().size() == 1,
							"A block with a single statement", inner_b);
					Stmt async = inner_b.statements().get(0);
					Block async_body = (Block) checkAsync(async, true);
	
					CUDAKernel ck = nf.CUDAKernel(b.position(), b.statements(), (Block) setReachable(async_body));
					ck.autoBlocks = (LocalDecl) setReachable(autoBlocks);
					ck.autoThreads = (LocalDecl) setReachable(autoThreads);
					ck.blocks = (Expr) setReachable(outer.max);
					ck.blocksVar = (Formal) setReachable(outer.var);
					ck.threads = (Expr) setReachable(inner.max);
					ck.threadsVar = (Formal) setReachable(inner.var);
					ck.shm = shm;
					ck.cmem = cmem;
					ck.directParams = direct;
					return ck;
				} catch (Complaint e) {
		            job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, e.toString(), b.position());
					//e.printStackTrace();
				}
			}
		}
		
		return child;
	}	
	
	private static Node setReachable (Term x) {
		if (x==null) return null;
		return x.visit(new NodeVisitor() {
			@Override
			public Node leave(Node parent, Node old, Node child, NodeVisitor v) {
				if (child instanceof Term) {
					Term child_term = (Term) child;
					return child_term.reachable(true);
				}
				return child;
			}
		});
		
	}
}
