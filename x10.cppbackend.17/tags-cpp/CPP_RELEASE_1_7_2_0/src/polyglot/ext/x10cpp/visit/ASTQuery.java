package polyglot.ext.x10cpp.visit;

import static polyglot.ext.x10cpp.visit.SharedVarsMethods.*;

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
import polyglot.ast.StringLit;
import polyglot.ast.Throw_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10cpp.Configuration;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.types.ClassType;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
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

	private final Translator tr;
	public ASTQuery(Translator tr) {
		this.tr = tr;
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

    boolean isSyntheticField(String name) {
		if (name.startsWith("jlc$")) return true;
		return false;
	}

    private boolean seenMain = false;
    boolean isMainMethod(MethodDecl dec) {
        final X10TypeSystem ts = (X10TypeSystem) dec.returnType().type().typeSystem();
        X10ClassType container = (X10ClassType) dec.methodDef().asInstance().container();
        assert (container.isClass());
        boolean result =
            (Configuration.MAIN_CLASS == null ||
                    container.fullName().toString().equals(Configuration.MAIN_CLASS)) &&
            dec.name().toString().equals("main") &&
            dec.flags().flags().isPublic() &&
            dec.flags().flags().isStatic() &&
            dec.returnType().type().isVoid() &&
            (dec.formals().size() == 1) &&
            ((Formal)dec.formals().get(0)).type().type().typeEquals(ts.Rail(ts.String()));
        if (result) {
            if (seenMain && Configuration.MAIN_CLASS == null)
                tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
                                                         "Multiple main() methods encountered.  " +
                                                         "Please specify MAIN_CLASS.");
            seenMain = true;
        }
        return result;
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
			X10ClassType baseType = (X10ClassType) ts.systemResolver().find(QName.make(name));
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
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
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

	static final ArrayList knownAsyncArrayCopyMethods = new ArrayList();

	/* -- SPMD compilation --
	boolean isAsyncArrayCopy(Call_c n) {
		X10TypeSystem_c ts = (X10TypeSystem_c) tr.typeSystem();
		if (knownAsyncArrayCopyMethods.size() == 0) {
			X10CPPContext_c context = (X10CPPContext_c) tr.context();
			try {
				Type x_l_Runtime = (Type) ts.Runtime();
				Type Int = ts.Int();
				Type[] OA_I_P_I_I_B = { ts.array(ts.Object()), Int, ts.Place(), Int, Int, ts.Boolean() };
				knownAsyncArrayCopyMethods.add(ts.findMethod(x_l_Runtime, ts.MethodMatcher(x_l_Runtime, Name.make("asyncDoubleArrayCopy"), Arrays.asList(OA_I_P_I_I_B)), context.currentClassDef()));
			} catch (SemanticException e) { assert (false); }
		}
		if (!(n.target() instanceof X10CanonicalTypeNode_c))
			return false;
		X10CanonicalTypeNode_c target = (X10CanonicalTypeNode_c) n.target();
		if (!ts.typeEquals(target.type(), ts.Runtime()))
			return false;
		if (!knownAsyncArrayCopyMethods.contains(n.methodInstance()))
			return false;
		return true;
	}
	*/


	static final ArrayList knownArrayCopyMethods = new ArrayList();

	boolean isAsyncArrayCopy(Async_c n) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (knownArrayCopyMethods.size() == 0) {
			X10CPPContext_c context = (X10CPPContext_c) tr.context();
			try {
				Type x_l_Runtime = (Type) ts.Runtime();
				Type array = ts.Array();
				Type Int = ts.Int();
				Type[] A_I_A_I_I = { array, Int, array, Int, Int };
				knownArrayCopyMethods.add(ts.findMethod(x_l_Runtime, ts.MethodMatcher(x_l_Runtime, Name.make("arrayCopy"), Arrays.asList(A_I_A_I_I)), context.currentClassDef()));
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
		if (!ts.typeEquals(target.type(), ts.Runtime()))
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


	public static String getCppRep(X10ClassDef def) {
		return getCppRepParam(def, 1);
	}
	public static String getCppBoxRep(X10ClassDef def) {
		return getCppRepParam(def, 2);
	}
	public static String getCppRTTRep(X10ClassDef def) {
		return getCppRepParam(def, 3);
	}
	public static String getCppRepParam(X10ClassDef def, int i) {
		try {
			X10TypeSystem xts = (X10TypeSystem) def.typeSystem();
			Type rep = (Type) xts.systemResolver().find(QName.make("x10.compiler.NativeRep"));
			List<Type> as = def.annotationsMatching(rep);
			for (Type at : as) {
				assertNumberOfInitializers(at, 4);
				String lang = getPropertyInit(at, 0);
				if (lang != null && lang.equals(NATIVE_STRING)) {
					return getPropertyInit(at, i);
				}
			}
		}
		catch (SemanticException e) {}
		return null;
	}

	public static void assertNumberOfInitializers(Type at, int len) {
	    at = X10TypeMixin.baseType(at);
	    if (at instanceof X10ClassType) {
	        X10ClassType act = (X10ClassType) at;
	        assert len == act.propertyInitializers().size();
	    }
	}

	public static String getPropertyInit(Type at, int index) {
	    at = X10TypeMixin.baseType(at);
	    if (at instanceof X10ClassType) {
		X10ClassType act = (X10ClassType) at;
		if (index < act.propertyInitializers().size()) {
		    Expr e = act.propertyInitializer(index);
		    if (e instanceof StringLit) {
			StringLit lit = (StringLit) e;
			String s = lit.value();
			return s;
		    }
		}
	    }
	    return null;
	}
}
