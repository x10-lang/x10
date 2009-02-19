package polyglot.ext.x10cpp.visit;

import static polyglot.ext.x10cpp.visit.SharedVarsMethods.knownInlinableMethods;
import static polyglot.ext.x10cpp.visit.SharedVarsMethods.populateIninableMethodsIfEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import polyglot.ast.Block_c;
import polyglot.ast.Call_c;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Throw_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.types.ClassType;
import polyglot.types.NoClassException;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.visit.Translator;

/**
 * Stateless Queries of various kinds against the AST. Extracted from X10PrettyPrinterVisitor.
 * 
 * @author vj
 *
 */

public class ASTQuery {

	private final CodeWriter w;
	private final Translator tr;
	public ASTQuery(CodeWriter w, Translator tr) {
		this.w=w;
		this.tr=tr;
	}
	
	 static final HashMap<String,String> arrayRuntimeNameToType = new HashMap<String,String>();
		static {
			arrayRuntimeNameToType.put("booleanArray", "boolean");
			arrayRuntimeNameToType.put("BooleanReferenceArray", "boolean");
			arrayRuntimeNameToType.put("byteArray", "byte");
			arrayRuntimeNameToType.put("ByteReferenceArray", "byte");
			arrayRuntimeNameToType.put("charArray", "char");
			arrayRuntimeNameToType.put("CharReferenceArray", "char");
			arrayRuntimeNameToType.put("shortArray", "short");
			arrayRuntimeNameToType.put("ShortReferenceArray", "short");
			arrayRuntimeNameToType.put("intArray", "int");
			arrayRuntimeNameToType.put("IntReferenceArray", "int");
			arrayRuntimeNameToType.put("longArray", "long");
			arrayRuntimeNameToType.put("LongReferenceArray", "long");
			arrayRuntimeNameToType.put("floatArray", "float");
			arrayRuntimeNameToType.put("FloatReferenceArray", "float");
			arrayRuntimeNameToType.put("doubleArray", "double");
			arrayRuntimeNameToType.put("DoubleReferenceArray", "double");
		}
	boolean isMainMethod(X10CPPContext_c context) {
		return context.isMainMethod() && !context.insideClosure;
	}
	boolean isInlinable(Async_c async) {
		populateIninableMethodsIfEmpty(tr);
		// An inlinable async should not have any of the
		// following in it's body unless they are known good

		X10SearchVisitor xAsync = new X10SearchVisitor(Async_c.class, Throw_c.class, Call_c.class);
		async.body().visit(xAsync);
		return canBePartOfInlinableAsync(xAsync);
	}
	boolean isSyntheticField(String name) {
		if (name.startsWith("jlc$")) return true;
		if (name.endsWith("$")) return true;
		return false;
	}

	boolean containsCodeForAll(X10SearchVisitor xCode, Node body) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (!xCode.found())
			return false;
		ArrayList matches = xCode.getMatches();
		for (int j = 0; j < matches.size(); j++) {
			Node match = (Node) matches.get(j);
			if (match instanceof Call_c) {
				Call_c call = (Call_c) match;
				if (knownInlinableMethods.contains(call.methodInstance()))
					continue;

				X10SummarizingRules.Collection harvest = context.outermostContext().harvest;
				MethodDecl_c decl = harvest.getDecl(call);
				if (decl!= null && decl.body()!= null && decl.body() != body && hasCodeForAllPlaces(decl.body()))
					return true;
			} else if (match instanceof Finish_c || match instanceof AtEach_c) {
				return true;
			}
		}
		return false;
	}
	
	boolean isMainMethod(MethodDecl dec) {
		final X10TypeSystem ts = (X10TypeSystem) dec.returnType().type().typeSystem();
		String currentFileName = tr.job().source().name(); 
		//FIXME. The above should refer to path().  Path() however gives full path only 
		// for the file the x10c++ command is invoked on, and not the other files pulled in for compilation by imports.  
		// Clearly path() works if we demand that the specified main file be the one to be specified explicitly to x10c++, but that's unclean.

		boolean answer =   
			dec.name().equals("main") &&
			dec.flags().flags().isPublic() &&
			dec.flags().flags().isStatic() &&
			dec.returnType().type().isVoid() &&
			(dec.formals().size() == 1) &&
			((Formal)dec.formals().get(0)).type().type().equals(ts.arrayOf(ts.String()));
		if (answer) {
			if (polyglot.ext.x10cpp.Configuration.MAIN_FILE.equals(currentFileName)) { return true;}
			
			if (polyglot.ext.x10cpp.Configuration.MAIN_FILE.equals("")) {
				polyglot.ext.x10cpp.Configuration.MAIN_FILE = "__internal_processing_value__";
				return true;
			}
			else if (polyglot.ext.x10cpp.Configuration.MAIN_FILE.equals("__internal_processing_value__")){ 
			       tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
					"Encountered multiple mains. Choice of main file not specified in compiler options.");}
					
		}
		
		return false;
	}

	boolean hasAnnotation(Node dec, String name) {
		try {
			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			if (annotationNamed(ts, dec, name) != null)
				return true;
		} catch (NoClassException e) {
			if (!e.getClassName().equals(name))
				throw new InternalCompilerError("Something went terribly wrong", e);
		} catch (SemanticException e) {
			throw new InternalCompilerError("Something is terribly wrong", e);
		}
		return false;
	}
	X10ClassType annotationNamed(TypeSystem ts, Node o, String name) throws SemanticException {
		// Nate's code. This one.
		if (o.ext() instanceof X10Ext) {
			X10Ext ext = (X10Ext) o.ext();
			X10ClassType baseType = (X10ClassType) ts.systemResolver().find(name);
			List<X10ClassType> ats = ext.annotationMatching(baseType);
			if (ats.size() > 1) {
				throw new SemanticException("Expression has more than one " + name + " annotation.", o.position());
			}
			if (!ats.isEmpty()) {
				X10ClassType at = ats.get(0);
				return at;
			}
		}
		return null;
	}

	boolean hasCodeForAllPlaces(Node n) {
		populateIninableMethodsIfEmpty(tr);
		X10SearchVisitor xCode = new X10SearchVisitor(Finish_c.class, AtEach_c.class, Call_c.class);
		n.visit(xCode);
		return containsCodeForAll(xCode, n);
	}

	boolean isInlinableFunc(Call_c c) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10SummarizingRules.Collection harvest = context.outermostContext().harvest;
		MethodDecl_c decl = harvest.getDecl(c);
		// Note that this is different from the inlining code, so there's no reason why
		// we cannot actually examine the body of the function to figure out if it's
		// inlinable - we're only going to call it anyway, not inline.
		if (decl != null && decl.body() != null && !harvest.isCallRecursiveCached(c)) {
			X10SearchVisitor xAsync = new X10SearchVisitor(Async_c.class, Throw_c.class, Call_c.class);
			decl.body().visit(xAsync);
			return canBePartOfInlinableAsync(xAsync);
		}

		return false;
	}

	boolean canBePartOfInlinableAsync(X10SearchVisitor xAsync) {
		if (!xAsync.found())
			return true;
		ArrayList matches = xAsync.getMatches();
		for (int j = 0; j < matches.size(); j++) {
			Node match = (Node) matches.get(j);
			if (match instanceof Async_c) {
				if (!(((Async_c) match).place() instanceof Here_c))
					return false;
			}
			else if (match instanceof Call_c) {
				Call_c call = (Call_c) match;
				if (!knownInlinableMethods.contains(call.methodInstance()) 
						&& !isInlinableFunc(call))
					return false;
			}
			// TODO: check for statically bounded loops
			else
				return false;
		}
		return true;
	}

	boolean isClockMaker(Call_c n) {
		if (n.isTargetImplicit())
			return false;
		Receiver target = n.target();
		if (!(target instanceof Field_c))
			return false;
		Field_c f = (Field_c) target;
		if (!(f.target() instanceof X10CanonicalTypeNode_c))
			return false;
		X10CanonicalTypeNode_c t = (X10CanonicalTypeNode_c) f.target();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		if (!xts.isClock(t.type()) || !f.name().equals("factory"))
			return false;
		if (n.arguments().size() != 0)
			return false;
		if (!n.name().equals("clock"))
			return false;
		return true;
	}

	boolean isBlockDistMaker(Call_c n) {
		if (n.isTargetImplicit())
			return false;
		Receiver target = n.target();
		if (!(target instanceof Field_c))
			return false;
		Field_c f = (Field_c) target;
		if (!(f.target() instanceof X10CanonicalTypeNode_c))
			return false;
		X10CanonicalTypeNode_c t = (X10CanonicalTypeNode_c) f.target();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		if (!xts.isDistribution(t.type()) || !f.name().equals("factory"))
			return false;
		// TODO: detect other distribution constructors
		if (n.arguments().size() != 1)
			return false;
		if (!n.name().equals("block"))
			return false;
		return true;
	}

	boolean isPointMaker(Call_c n) {
		if (n.isTargetImplicit())
			return false;
		Receiver target = n.target();
		if (!(target instanceof Field_c))
			return false;
		Field_c f = (Field_c) target;
		if (!(f.target() instanceof X10CanonicalTypeNode_c))
			return false;
		X10CanonicalTypeNode_c t = (X10CanonicalTypeNode_c) f.target();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		if (!xts.isPoint(t.type()) || !f.name().equals("factory"))
			return false;
		if (n.arguments().size() < 1)
			return false;
		if (!n.name().equals("point"))
			return false;
		return true;
	}

	boolean isSPMDArrayReduction(Expr expr) {
		if (!(expr instanceof Call_c))
			return false;
		Call_c n = (Call_c) expr;
		if (n.isTargetImplicit())
			return false;
		Receiver target = n.target();
		if (!(target instanceof Local_c))
			return false;
		Local_c l = (Local_c) target;
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (!context.isSPMDVar(l.localInstance()))
			return false;
		// FIXME: detect reductions other than "sum"
		if (!(n.arguments().size() == 0 && n.name().equals("sum")))
			return false;
		return true;
	}

	static boolean isPrintf(Call_c n) {
		return n.name().equals("printf") &&
		n.methodInstance().container().isClass() &&
		n.methodInstance().container().toClass().fullName().equals("java.io.PrintStream");
	}

	static final ArrayList knownAsyncArrayCopyMethods = new ArrayList();

	boolean isAsyncArrayCopy(Call_c n) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (knownAsyncArrayCopyMethods.size() == 0) {
			X10CPPContext_c context = (X10CPPContext_c) tr.context();
			try {
				ReferenceType x_l_Runtime = (ReferenceType) ts.Runtime();
				Type Int = ts.Int();
				Type[] OA_I_P_I_I_B = { ts.array(ts.Object()), Int, ts.place(), Int, Int, ts.Boolean() };
				knownAsyncArrayCopyMethods.add(ts.findMethod(x_l_Runtime, "asyncDoubleArrayCopy", Arrays.asList(OA_I_P_I_I_B), context.currentClass()));
			} catch (SemanticException e) { assert (false); }
		}
		if (!(n.target() instanceof X10CanonicalTypeNode_c))
			return false;
		X10CanonicalTypeNode_c target = (X10CanonicalTypeNode_c) n.target();
		if (!ts.equals(target.type(), ts.Runtime()))
			return false;
		if (!knownAsyncArrayCopyMethods.contains(n.methodInstance()))
			return false;
		return true;
	}


	static final ArrayList knownArrayCopyMethods = new ArrayList();

	boolean isAsyncArrayCopy(Async_c n) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (knownArrayCopyMethods.size() == 0) {
			X10CPPContext_c context = (X10CPPContext_c) tr.context();
			try {
				ReferenceType x_l_Runtime = (ReferenceType) ts.Runtime();
				Type array = ts.array();
				Type Int = ts.Int();
				Type[] A_I_A_I_I = { array, Int, array, Int, Int };
				knownArrayCopyMethods.add(ts.findMethod(x_l_Runtime, "arrayCopy", Arrays.asList(A_I_A_I_I), context.currentClass()));
				// TODO
//				Type[] A_A = { array, array };
//				knownArrayCopyMethods.add(ts.findMethod(x_l_Runtime, "arrayCopy", Arrays.asList(A_A), context.currentClass()));
				// TODO
//				ReferenceType x_l_region = ts.region();
//				Type[] A_R_A_R = { array, x_l_region, array, x_l_region };
//				knownArrayCopyMethods.add(ts.findMethod(x_l_Runtime, "arrayCopy", Arrays.asList(A_R_A_R), context.currentClass()));
			} catch (SemanticException e) { assert (false); }
		}
		List clocks = n.clocks();
		if (clocks != null && clocks.size() > 1)
			return false;
		Stmt body = n.body();
		if (body instanceof Block_c) {
			Block_c block = (Block_c) body;
			if (block.statements().size() != 1)
				return false;
			body = (Stmt) block.statements().get(0);
		}
		if (!(body instanceof Eval_c) || !(((Eval_c) body).expr() instanceof Call_c))
			return false;
		Call_c call = (Call_c) ((Eval_c) body).expr();
		if (!(call.target() instanceof X10CanonicalTypeNode_c))
			return false;
		X10CanonicalTypeNode_c target = (X10CanonicalTypeNode_c) call.target();
		if (!ts.equals(target.type(), ts.Runtime()))
			return false;
		if (!knownArrayCopyMethods.contains(call.methodInstance()))
			return false;
		return true;
	}
	static int async_id(X10CPPContext_c.Closures a, Node n) {
		return a.asyncs.lastIndexOf(n);
	}


	static int getConstructorId(X10CPPContext_c.Closures c) {
		return c.arrayInitializers.size()-1;
	}

	static boolean outerClosure(X10CPPContext_c.Closures a) {
		return a.nesting == 0;
	}
	static ClassType getOuterClass(X10CPPContext_c c) {
		ClassType currentClass = c.currentClass();
		while (currentClass.isNested()) {
			currentClass = (ClassType) currentClass.container();
		}
		return currentClass;
	}


	// FIXME: [IP] KLUDGE! KLUDGE! KLUDGE!
	boolean isX10Array(Type type) {
		// TODO: move this method to X10TypeSystem_c
		String name = type.translate(tr.typeSystem().createContext());
		return type.isClass() && name.startsWith("x10.lang.") && name.endsWith("Array");
	}

	// FIXME: [IP] KLUDGE! KLUDGE! KLUDGE!
	Type getX10ArrayElementType(Type type) {
		// TODO: move this method to X10TypeSystem_c
		assert (isX10Array(type));
		TypeSystem ts = tr.typeSystem();
		String name = type.translate(ts.createContext());
		name = (String) arrayRuntimeNameToType.get(name.substring(9));
		if (name == null) {
			X10Type x10type = (X10Type) type;
			assert (x10type.typeParameters().size() == 1);
			return (Type) x10type.typeParameters().get(0);
		} else {
			try { type = ts.primitiveForName(name); } catch (SemanticException e) { }
		}
		return type;
	}

}
