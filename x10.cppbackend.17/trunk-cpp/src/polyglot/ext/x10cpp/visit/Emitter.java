package polyglot.ext.x10cpp.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import polyglot.ast.Block_c;
import polyglot.ast.Call_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.If_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Stmt_c;
import polyglot.ast.Stmt;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.While_c;
import polyglot.types.QName;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.X10CanonicalTypeNode;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ConstructorDecl_c;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10Special_c;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.Translator;
import static polyglot.ext.x10cpp.visit.SharedVarsMethods.*;
import static polyglot.ext.x10cpp.visit.ASTQuery.*;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;

public class Emitter {

    private final Translator tr;
    private ASTQuery query;
    public Emitter(Translator tr) {
        this.tr = tr;
        query = new ASTQuery(tr);
    }
    private static final String[] CPP_KEYWORDS = { // Some are also X10 keywords
        "asm", "auto", "bool", "break", "case", "catch", "char", "class",
        "const", "const_cast", "continue", "default", "delete", "do", "double",
        "dynamic_cast", "else", "enum", "explicit", "export", "extern",
        "false", "float", "for", "friend", "goto", "if", "inline", "int",
        "long", "mutable", "namespace", "new", "operator", "private",
        "protected", "public", "register", "reinterpret_cast", "return",
        "restrict", // Yes, stupid xlC has a "restrict" keyword -- who knew?
        "short", "signed", "sizeof", "static", "static_cast", "struct",
        "switch", "template", "this", "throw", "true", "try", "typedef",
        "typeid", "typename", "union", "unsigned", "using", "virtual", "void",
        "volatile", "wchar_t", "while"
    };
    private static boolean isCPPKeyword(String name) {
        for (int i = 0; i < CPP_KEYWORDS.length; i++) {
            if (CPP_KEYWORDS[i].equals(name))
                return true;
        }
        return false;
    }
    private static String mangle_to_cpp(String str) {
        if (isCPPKeyword(str))
            str = "_kwd__" + str;
        return str.replace("$", "__");
    }
    public static String mangled_method_name(String str) {
        return mangle_to_cpp(str);
    }
    public static String mangled_non_method_name(String str) {
        return mangle_to_cpp(str);
    }
    public static String mangled_field_name(String str) {
        //return "__"+mangle_to_cpp(str);
        //return "x10__"+mangle_to_cpp(str);
        return "FMGL("+mangle_to_cpp(str)+")";
    }

	void printStaticAsyncDeclarations(X10CPPContext_c context, CodeWriter w) {
		printAsyncDeclarations("static ", "", context.closures.asyncs,
				context.closures.asyncsParameters, context.closures.arrayCopyClosures,
				context.closures.asyncContainers, w);
	}

	void printFriendAsyncDeclarations(X10CPPContext_c context, CodeWriter h) {
		printAsyncDeclarations("friend ", translateType(getOuterClass(context))+"::",
				context.closures.asyncs,
				context.closures.asyncsParameters, context.closures.arrayCopyClosures,
				context.closures.asyncContainers, h);
	}

	void printAsyncDeclarations(String prefix, String name_pfx, 
			ArrayList asyncs, ArrayList asyncsParameters, 
			HashMap arrayCopyClosures, HashMap asyncContainers, CodeWriter w) {
		X10CPPContext_c c = (X10CPPContext_c) tr.context();

		// Declare the switch functions here
		w.write("public : " + prefix + VOID_PTR + " " + name_pfx + ARRAY_COPY_SWITCH + "(x10_async_handler_t h, " + VOID_PTR + " __arg);");
		w.newline();
		w.write("public : " + prefix + VOID + " " + name_pfx + ASYNC_SWITCH + "(x10_async_handler_t h, " + VOID_PTR + " arg, int niter);");
		w.newline();

		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		for (int i = 0; i < asyncs.size(); i++) {
			ClassType aClass = null;
			if (arrayCopyClosures.containsKey(asyncs.get(i))) {
				aClass = (ClassType) arrayCopyClosures.get(asyncs.get(i));
			} else {
				aClass = (ClassType) asyncContainers.get(asyncs.get(i));
			}
			if (! ts.typeEquals(aClass, c.currentClass()))
				continue;
			if (arrayCopyClosures.containsKey(asyncs.get(i))) {
				w.write("public : " + prefix + VOID_PTR + " " + name_pfx + closure_name(ARRAY_COPY_PREFIX, i));
			} else {
				w.write("public : " + prefix + VOID + " " + name_pfx + closure_name(ASYNC_PREFIX, i));
			}
			w.write("(");
			w.begin(0);
			if (useStructsForAsyncArgs) {
				w.write(VOID_PTR);
			}
			else {
				ArrayList parameters = (ArrayList) asyncsParameters.get(i);
				for (int j = 0; j < parameters.size(); j++) {
					if (j > 0) {
						w.write(",");
						w.allowBreak(2, " ");
					}
					VarInstance var = (VarInstance) parameters.get(j);
					Type t = var.type();
					if (c.isSPMDVar(var)) {
						t = query.getX10ArrayElementType(t);
					}
					w.write(translateType(t, true));
				}
			}
			w.write(");");
			w.end(); w.newline();
		}
	}

	/**
	 * Same as n.printBlock(child, w, tr); but without the enterScope call.
	 */
	void printBlock(Node n, Expr child, StreamWrapper w, Translator tr) {
		w.begin(0);
		child.del().translate(w, tr);
		w.end();
	}

	void printStaticClosureDeclarations(X10CPPContext_c context, CodeWriter h) {
		printClosureDeclarations("static ", "",
				context.closures.arrayInitializers, context.closures.arrayInitializerParameters, h);
	}
	void printClosureDeclarations(String prefix, String name_pfx, ArrayList closures, 
			ArrayList closuresParameters, CodeWriter w) {
		X10CPPContext_c c = (X10CPPContext_c) tr.context();
		for (int i = 0; i < closures.size(); i++) {
			Closure_c n = (Closure_c) closures.get(i);
			Type r = n.returnType().type();
			w.write("public : " + prefix + translateType(r, true) + " " + name_pfx + closure_name(INIT_PREFIX, i) + "(");
			w.begin(0);
			if (useStructsForArrayInitArgs) {
				w.write(VOID_PTR+", ");
			}
			for (Iterator p = n.formals().iterator(); p.hasNext(); ) {
				Formal f = (Formal) p.next();
				w.write(translateType(f.type().type(), true));
				if (p.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
			boolean hasFormals = !n.formals().isEmpty();
			if (!useStructsForArrayInitArgs) {
				if (hasFormals) {
					w.write(",");
					w.allowBreak(2, " ");
				}
				ArrayList parameters = (ArrayList) closuresParameters.get(i);
				for (int j = 0; j < parameters.size(); j++) {
					if (j > 0) {
						w.write(",");
						w.allowBreak(2, " ");
					}
					VarInstance var = (VarInstance) parameters.get(j);
					Type t = var.type();
					if (c.isSPMDVar(var)) {
						t = query.getX10ArrayElementType(t);
					}
					w.write(translateType(t, true));
				}
			}
			w.write(");");
			w.end(); w.newline();
		}
	}
	/**
	 * Translate the string representation of a fully-qualified type name.
	 * TODO: rewrite to use QName instead
	 */
	public static String translateFQN(String name) {
		return translateFQN(name, "::");
	}
	public static String translateFQN(String name, String delim) {
		return name.replace("$","__").replaceAll("\\.", delim);
	}
	public static String translate_mangled_FQN(String name) {
		return translate_mangled_FQN(name, "::");
	}
	public static String translate_mangled_FQN(String name, String delim) {
		String src = name;
		String dest = "";
		int fromIndex = 0;
		while (true) {
			boolean finished=false;
			int toIndex = src.indexOf('.', fromIndex);
			if (toIndex < 0){
				finished=true;
				toIndex=src.length();
			}
			String s = src.substring(fromIndex, toIndex);
			dest += mangled_non_method_name(s);
			if (finished) break;
			dest += ".";
			fromIndex = toIndex+1;
		}

		return translateFQN(dest, delim);
	}
	public static String translate_mangled_NSFQN(String name) { // namespace FQN
		String src = name;
		String dest = "";
		int fromIndex = 0;
		while (true) {
			boolean finished=false;
			int toIndex = src.indexOf('.', fromIndex);
			if (fromIndex != 0 && toIndex >= 0)
				dest += ".";
			if (toIndex < 0)
				break;
			String s = src.substring(fromIndex, toIndex);
			dest += mangled_non_method_name(s);
			fromIndex = toIndex+1;
		}

		return translateFQN(dest);
	}

	/**
	 * Print a type as a reference.
	 */
	void printType(Type type, CodeWriter w) {
		w.write(translateType(type, true));
	}

	/**
	 * Translate a type name.
	 */
	String translateType(Type type) {
		return translateType(type, false);
	}


	/**
	 * Translate a type.
	 *
	 * @param type type to translate
	 * @param asRef whether to make a reference
	 * @return a string representation of the type
	 */
	String translateType(Type type, boolean asRef) {
		assert (type != null);
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		type = xts.expandMacros(type);
		Context context = tr.context();
		if (type.isVoid()) {
			return "void";
		}
		// TODO: handle closures
//		if (((X10TypeSystem) type.typeSystem()).isClosure(type))
//			return translateType(((X10Type) type).toClosure().base(), asRef);
		String name = null;
		if (type.isClass()) {
			X10ClassType ct = (X10ClassType) type.toClass();
			if (ct.isAnonymous())  // FIXME: [IP] Is this ever true?
				name = "__anonymous__"+getId();
			else {
                type = X10TypeMixin.baseType(type);
				X10ClassDef cd = ((X10ClassType) type).x10Def();
				String pat = null;
				if (!asRef)
					pat = getCppBoxRep(cd, tr);
				else
					pat = getCppRep(cd, tr);
				if (pat != null) { 
					List<Type> typeArguments = ct.typeArguments();
					Object[] o = new Object[typeArguments.size()+1];
					int i = 0;
					o[i++] = type;
					for (Type a : typeArguments) {
					    o[i++] = a;
					}
					// FIXME: Clean up this code!
					return dumpRegex("NativeRep", o, tr, pat);
				}
				else {
                    context = tr.typeSystem().createContext(); // Always fully qualify
					if (ct.def().isNested()) {
						X10ClassDef cdef = (X10ClassDef) ct.container().toClass().def();
						assert (false) : ("Nested class alert!");
						if (cdef.typeParameters().size() != 0) {
							name = ct.container().translate(context)+
								voidTemplateInstantiation(cdef.typeParameters().size())+
								"::"+ct.name().toString();
						} else {
							context = tr.typeSystem().createContext();
							name = type.translate(context);
						}
					} else
						name = type.translate(context);
				}
			}
			if (ct.typeArguments().size() != 0) {
				name += "<";
				int s = ct.typeArguments().size();
				for (Type t: ct.typeArguments()) {
					name += translateType(t, true); // type arguments are always translated as refs
					s--;
					if (s > 0)
						name +=", ";
				}
				if (name.endsWith(">"))
					name += " ";
				name += ">";
			}
		} else if (type instanceof ParameterType) {
			return type.toString(); // parameter types shouldn't be refs
		} else if (type.isNull()) {
			return "x10aux::NullType"; // typedef to something sensible
		} else 
			assert false : type; // unhandled type.
		assert (name != null);
		name = translateFQN(name);
		if (!asRef)
			return name;
		return make_ref(name);
	}

	void printArgumentList(CodeWriter w, X10CPPContext_c c) {
		printArgumentList(w, c, false, true);
	}
	
	void printArgumentList(CodeWriter w, X10CPPContext_c c, boolean omitType) {
		printArgumentList(w, c, omitType, true);
	}
	
	void printArgumentList(CodeWriter w, X10CPPContext_c c, boolean omitType, boolean saved_this_mechanism) {
		for (int i = 0; i < c.variables.size(); i++) {
			if (i > 0) {
				w.write(",");
				w.allowBreak(2, " ");
			}
			VarInstance var = (VarInstance)c.variables.get(i);
			if (!omitType) {
				Type t = var.type();
				if (c.isSPMDVar(var)) {
					t = query.getX10ArrayElementType(t);
				}
				String type = translateType(t, true);
				w.write(type + " ");
			}
			String name = var.name().toString();
			if (saved_this_mechanism && name.equals(THIS))
				name = SAVED_THIS;
			else
				name = mangled_non_method_name(name) ;

			w.write(name);
		}
	}
	void printAsyncsRegistration(ClassType currentClass, ArrayList asyncs, CodeWriter w) {
		if (asyncs.size() == 0) return;
		String className = translateType(currentClass);
		w.newline();
		w.write("func_t handlers[] = {");
		w.newline(4); w.begin(0);
		for (int i = 0; i < asyncs.size(); i++) {
			if (i > 0) {
				w.write(",");
				w.newline();
			}
			w.write("(void_func_t) "+className+"::"+closure_name(ASYNC_PREFIX, i));
		}
		w.end(); w.newline();
		w.write("};");
		w.newline();
	}



	void printFriendClosureDeclarations(X10CPPContext_c context, CodeWriter h) {
		printClosureDeclarations("friend ", translateType(getOuterClass(context))+"::",
				context.closures.arrayInitializers, context.closures.arrayInitializerParameters, h);
	}


	void printTemplateSignature(List<Type> list, CodeWriter h) {
		int size = list.size();
		if (size != 0){
			h.write("template <class ");
			for (Type t: list) {
				assert (t instanceof ParameterType);
				h.write(translateType(t));
				size--;
				if (size > 0)
					h.write(", class ");
			}
			h.write("> ");
		}
	}
	
	List<Type> toTypeList(List<TypeParamNode> list) {
		ArrayList<Type> res = new ArrayList<Type>();
		for (TypeParamNode n : list)
			res.add(n.type());
		return res;
	}
	
	void printTemplateInstantiation(X10MethodInstance mi, CodeWriter w) {
		if (mi.typeParameters().size() == 0)
			return;
		w.write("<");
		for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
		    final Type at = i.next();
		    w.write(translateType(at, true));
		    if (i.hasNext()) {
		        w.write(",");
		        w.allowBreak(0, " ");
		    }
		}
		w.write(" >");
	}

	public static String voidTemplateInstantiation(int num) {
		StringBuffer b = new StringBuffer();
		b.append("<");
		for (int i = 0; i < num; i++) {
			if (i > 0)
				b.append(", ");
			b.append("void");
		}
		b.append(">");
		return b.toString();
	}

	static MethodInstance getOverridingMethod(X10TypeSystem xts, ClassType localClass, MethodInstance mi, ClassType original) {
		try {
			return xts.findMethod(localClass,xts.MethodMatcher(localClass,mi.name(),mi.formalTypes()),original.def());
		} catch (SemanticException e) {
			return null;
		}
	}

	Type findRootMethodReturnType(X10MethodDef n, Position pos, MethodInstance from) {
		assert from != null;
		// [IP] Optimizations
		X10Flags flags = X10Flags.toX10Flags(from.flags());
		if (flags.isStatic() || flags.isPrivate() || flags.isProperty() || from.returnType().isVoid())
			return from.returnType();

		// [DC] c++ doesn't allow covariant smartptr return types so
		// we are forced to use the same type everywhere and cast
		// on call.  This function gets the one type that we use for
		// all return types, from the root of the class hierarchy.

		// TODO: currently we cannot handle the following code:
		// A new approach is required.
		/*
		interface Cloneable {
			def clone() : Cloneable;
		}

		interface Cloneable2 {
			def clone() : Cloneable2;
		}

		class A implements Cloneable, Cloneable2 {
			public def clone() { return this; }
		}
		*/

		// [DC] TODO: There has to be a better way!
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		X10ClassType original = (X10ClassType) n.container().get();

		X10ClassType classType = (X10ClassType) from.container();
		X10ClassType superClass = (X10ClassType) classType.superClass();
		List<Type> interfaces = classType.interfaces();
		Type returnType = null;

		if (superClass != null) {
			MethodInstance superMeth = getOverridingMethod(xts,superClass,from,original);
			if (superMeth != null) {
				//System.out.println(from+" overrides "+superMeth);
				returnType = findRootMethodReturnType(n, pos, superMeth);
			}
		}

		for (Type itf : interfaces) {
			X10ClassType itf_ = (X10ClassType) itf;
			// same thing again for interfaces
			MethodInstance superMeth = getOverridingMethod(xts,itf_,from,original);
			if (superMeth != null) {
				//System.out.println(from+" implements "+superMeth);
				Type newReturnType = findRootMethodReturnType(n, pos, superMeth);

				// check -- 
				if (returnType != null && !xts.typeDeepBaseEquals(returnType, newReturnType)) {
					String msg = "Two supertypes declare " + from + " with "
						+ "different return types: " + returnType + " != " + newReturnType;
					tr.job().compiler().errorQueue().enqueue(ErrorInfo.WARNING, msg, pos);
				}
				returnType = newReturnType;
			}
		}

		// if we couldn't find an overridden method, just use the local return type
		return returnType != null ? returnType : from.returnType();
	}

	void printHeader(MethodDecl_c n, CodeWriter h, Translator tr, boolean qualify) {
		printHeader(n, h, tr, n.name().id().toString(), n.returnType().type(), qualify);
	}
	void printHeader(MethodDecl_c n, CodeWriter h, Translator tr, String name, Type ret, boolean qualify) {
		X10Flags flags = X10Flags.toX10Flags(n.flags().flags());

		if (!qualify) {
			printFlags(h, flags);
		}
		h.begin(0);

		if (qualify) {
			printTemplateSignature(((X10ClassType)n.methodDef().container().get()).typeArguments(), h);
		}

		X10MethodDecl_c xn = (X10MethodDecl_c) n;
		printTemplateSignature(toTypeList(xn.typeParameters()), h);

		if (!qualify) {
			if (flags.isStatic())
				h.write(flags.retain(Flags.STATIC).translate());
			else if (xn.typeParameters().size() != 0) {
			    if (!flags.isFinal()) {
			        // FIXME: [IP] for now just make non-virtual.
			        // In the future, will need to have some sort of dispatch object, e.g. the following:
			        // class Foo { def m[T](a: X): Y { ... } }; class Bar extends Foo { def m[T](a: X): Y { ... } }
			        // translates to
			        // template<class T> class Foo_m {
			        //    Y _(Foo* f, X a) {
			        //       if (typeid(f)==typeid(Foo)) { return f->Foo::m_impl<T>(a); }
			        //       else if (typeid(f)==typeid(Bar)) { return f->Bar::m_impl<T>(a); }
			        //    }
			        // };
			        // class Foo {
			        //    public: template<class T> Y m(X a) { Foo_m<T>::_(this, a); }
			        //    public: template<class T> Y m_impl(X a);
			        // };
			        // class Bar : public Foo {
			        //    public: template<class T> Y m_impl(X a);
			        // };
			        String msg = n.methodDef()+" is both generic and virtual";
			        tr.job().compiler().errorQueue().enqueue(ErrorInfo.WARNING, msg, n.position());
			    }
			}
			else if (!flags.isProperty() && !flags.isPrivate() /*&& !flags.isFinal()*/) // [IP] TODO: find out if this is ok
				h.write("virtual ");
		}
		printType(ret, h);
		h.allowBreak(2, 2, " ", 1);
		if (qualify)
			h.write(translateType(n.methodDef().asInstance().container()) + "::"); 
		//n.print(n.id(), h, tr);
		h.write(mangled_method_name(name)); 
		h.write("(");
		h.allowBreak(2, 2, "", 0);
		h.begin(0);
		for (Iterator i = n.formals().iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			n.print(f, h, tr);
			if (i.hasNext()) {
				h.write(",");
				h.allowBreak(0, " ");
			}
		}
		h.end();
		h.write(")");
		h.end();
		if (!qualify) {
			assert (!flags.isNative());
			if (n.body() == null && !flags.isExtern() && !flags.isProperty())
				h.write(" = 0");
		}
	}

	void printHeader(Formal_c n, CodeWriter h, Translator tr, boolean qualify) {
//		Flags flags = n.flags().flags();
		h.begin(0);
//		if (flags.isFinal())
//		h.write("const ");
		printType(n.type().type(), h);
		h.write(" ");
		h.write(mangled_non_method_name(n.name().id().toString()));
		h.end();
	}

	void printFlags(CodeWriter h, Flags flags) {
//		if (flags.isPublic() || flags.isProtected() || flags.isPrivate())
//		h.write(flags.retain(Flags.PUBLIC.Private().Protected()).translate()+": ");
//		else // Java's "package access" should be the same as public
//		h.write(Flags.PUBLIC.translate()+": ");
		// [IP] HACK: with inlining, everything is public
		// [DC] I got tired of seeing the comment everywhere
		//h.write("/"+"*"+flags.retain(Flags.PUBLIC.Private().Protected()).translate()+"*"+"/ ");
		h.write("public: ");
	}

	private void printAllTemplateSignatures(ClassDef cd, CodeWriter h) {
		if (cd.isNested()) {
			assert (false) : ("Nested class alert!");
			printAllTemplateSignatures(cd.outer().get(), h);
		}
		printTemplateSignature(((X10ClassType)cd.asType()).typeArguments(), h);
	}

	void printRTT(X10ClassType ct, CodeWriter h) {
		String x10name = ct.fullName().toString();
		int num_parents = 1 + ct.interfaces().size();
		//
		h.write("public: class RTT : public x10aux::RuntimeType {"); h.newline(4); h.begin(0);
			h.write("public:"); h.newline();
			h.write("static RTT * const it;"); h.newline();
			h.write("virtual void init() {"); h.newline(4); h.begin(0);
				h.write("initParents("+num_parents);
				h.write(", x10aux::getRTT<" + (ct.superClass()==null ? "x10::lang::Ref" : translateType(ct.superClass())) + " >()");
				for (Type iface : ct.interfaces()) {
					h.write(", x10aux::getRTT<"+translateType(iface)+" >()");
				}
				h.write(");"); h.end(); h.newline();
			h.write("}"); h.newline();
			h.write("virtual std::string name() const {"); h.newline(4); h.begin(0);
				//TODO: type parameters
				if (ct.typeArguments().isEmpty()) {
					h.write("return \""+x10name+"\";"); h.end(); h.newline();
				} else {
					h.write("std::stringstream ss;"); h.newline();
					h.write("ss << \""+x10name+"[\"");
					String comma = "";
					for (Type param : ct.typeArguments()) {
						h.write(comma+" << x10aux::getRTT<"+translateType(param)+">()->name()");
						comma = " << \",\"";
					}
					h.write(" << \"]\";") ; h.newline();
					h.write("return ss.str();"); h.end(); h.newline();
				}
			h.write("}"); h.newline();
			h.end(); h.newline();
		h.write("};"); h.newline();
		h.newline();
		h.write("public: virtual const x10aux::RuntimeType *_type () const {"); h.newline(4); h.begin(0);
			h.write("return x10aux::getRTT<"+translateType(ct)+" >();"); h.end(); h.newline();
		h.write("}"); h.newline();
	}
	void printRTTDefn(X10ClassType ct, CodeWriter h) {
		if (ct.typeArguments().isEmpty()) {
			h.write("DEFINE_RTT("+translateType(ct)+");");
		} else {
    		printTemplateSignature(ct.typeArguments(), h);
			h.write("typename "+translateType(ct)+"::RTT * const "+translateType(ct)+"::RTT::it = ");
			h.newline(4);
			h.write("new (x10aux::alloc<typename "+translateType(ct)+"::RTT>()) "+
					"typename "+translateType(ct)+"::RTT();");
		}
		h.newline();
	}

	void printInheritance(ClassDecl_c n, CodeWriter h, Translator tr) {
		String extends_ = n.superClass()==null ? null : translateType(n.superClass().type());
		ArrayList<String> implements_ = new ArrayList<String>();
		for (TypeNode tn : n.interfaces()) {
			implements_.add(translateType(tn.type()));
		}

		// [DC] FIXME: the following is a hack, probably this should happen in
		// the front end but I doubt that will happen any time soon.  It ought
		// to use type objects instead of strings, too.  But I couldn't work
		// out how to do that.

		// it seems extends_==null implies that we are dealing with an
		// interface, since otherwise extends_ is Ref, Value, or some
		// user-defined type.

		if (extends_ == null && implements_.isEmpty()) {
			//Interfaces must always extend something in c++
			implements_.add("x10::lang::Object");
		} else if (extends_ != null && implements_.contains("x10::lang::Object")) {
			//Cosmetic: No point implementing Object if we're already extending something
			implements_.remove("x10::lang::Object");
		}

		String prefix = ":";
		if (extends_ != null) {
			h.write(" "+prefix+" public "+extends_);
			prefix = ",";
		}

		h.allowBreak(2);
		h.begin(0);
		for (String iface : implements_) {
			h.write(" "+prefix+" public virtual "+iface);
			prefix = ",";
		}
		h.end();
	}

	void printHeader(ClassDecl_c n, CodeWriter h, Translator tr, boolean qualify) {
		h.begin(0);
		// Handle generics
		// If it involves Parameter Types then generate C++
		// templates.

		printAllTemplateSignatures(n.classDef(), h);

		h.write("class ");
		assert(!n.classDef().isLocal());
		if (n.classDef().isNested() && !n.classDef().isLocal()) { // FIXME: handle local classes
			assert (false) : ("Nested class alert!");
			h.write(translateType(n.classDef().outer().get().asType()) + "::");
		}
		h.write(mangled_non_method_name(n.name().id().toString())); 

		printInheritance(n, h, tr);

		h.unifiedBreak(0);
		h.end();
	}

	void printHeader(ConstructorDecl_c n, CodeWriter h, Translator tr, boolean qualify) {
        Flags flags = n.flags().flags();

		X10ClassType container = (X10ClassType) n.constructorDef().container().get();
		if (qualify){
			printTemplateSignature((container).typeArguments(), h);
		}

		X10ConstructorDecl_c xn = (X10ConstructorDecl_c) n;
		printTemplateSignature(toTypeList(xn.typeParameters()), h);

		if (!qualify) {
			printFlags(h, flags);
		}
		h.begin(0);
		String typeName = translateType(container.def().asType());
		if (qualify)
			h.write(typeName + "::");
		// Use just the name for the constructor identifier -- no template params, etc
		h.write(mangled_non_method_name(container.def().asType().name().toString()));
		h.write("(");
		h.allowBreak(2, 2, "", 0);
		h.begin(0);
		for (Iterator i = n.formals().iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			n.print(f, h, tr);
			if (i.hasNext()) {
				h.write(",");
				h.allowBreak(0, " ");
			}
		}
		h.end();
		h.write(")");
		h.end();
		if (!qualify) {
			h.write(";");
			h.newline();
		}
	}

	void printHeader(FieldDecl_c n, CodeWriter h, Translator tr, boolean qualify) {
		Flags flags = n.flags().flags();
		if (!qualify) {
			printFlags(h, flags);
		}
		h.begin(0);
		if (!qualify) {
			if (flags.isStatic())
				h.write(flags.retain(Flags.STATIC).translate());
		}

		if (query.hasAnnotation(n, "x10.lang.shared")) {
			h.write("volatile ");
		}

		printType(n.type().type(), h);
		h.allowBreak(2, 2, " ", 1);
		if (qualify)
			h.write(translateType(n.fieldDef().asInstance().container()) + "::");
		h.write(mangled_field_name(n.name().id().toString())); 
		h.end();
	}

	void printHeader(LocalDecl_c n, CodeWriter h, Translator tr, boolean qualify) {
		//Flags flags = n.flags().flags();
		h.begin(0);
		// Let us not generate constants - We will have problem in
		// initializing away from the place where it is declared.
		//if (flags.isFinal())
		//	h.write("const ");
		if (tr.printType()) {
			assert (n != null);
			assert (n.type() != null);
			assert (n.type().type() != null);
			printType(n.type().type(), h);
			h.write(" ");
		}
		h.write(mangled_non_method_name(n.name().id().toString())); 
		h.end();
	}
	
	void createPackedArgumentsStruct(CodeWriter w, X10CPPContext_c c, int id, String prefix) {
		createPackedArgumentsStruct(w, c, id, prefix, true);
	}
	void createPackedArgumentsStruct(CodeWriter w, X10CPPContext_c c, int id, String prefix, boolean saved_this_mechanism) {
		w.write("struct "+args_name(prefix, id)+" : public x10aux::closure_args {");
		w.newline(4); w.begin(0);
		w.write(args_name(prefix, id));
		w.write("(");
		printArgumentList(w, c, false, saved_this_mechanism);
		w.write(")");
		w.allowBreak(0, " ");
		// [IP] This used to output the normal C++ field initialization syntax,
		//      but the commas played havoc with the macro invocation
		w.write("{"); w.newline(4); w.begin(0);
		for (int i = 0; i < c.variables.size(); i++) {
			VarInstance var = (VarInstance)c.variables.get(i);
			String name = var.name().toString();
			if (saved_this_mechanism && name.equals(THIS))
				name = SAVED_THIS;
			else name=mangled_non_method_name(name);
			if (i > 0) w.newline();
			// FIXME: pack references properly
			w.write("this->" + name + " = " + name + ";");
		}
		w.end(); w.newline();
		w.write("}");
		w.newline();
		if (c.variables.size() > 0) {
			// Generate a no-arg constructor as well
			w.write(args_name(prefix, id));
			w.write("() { }");
			w.newline();
		}
		printDeclarationList(w, c, c.variables, saved_this_mechanism);
		w.end(); w.newline();
		w.write("};");
	}

	void enterAsync(X10CPPContext_c.Closures a, Node n) {
		assert (n != null);
//		assert (!a.asyncs.contains(n));
		// if present, need to add again, because captured variables may change
		a.asyncs.add(n);
		a.asyncsParameters.add(null); // actual parameters will be defined later
		a.nesting++;
	}

	void enterClosure(X10CPPContext_c.Closures a, Closure_c n) {
		assert (n != null);
//		assert (!a.arrayInitializers.contains(n));
		// if present, need to add again, because captured variables may change
		a.arrayInitializers.add(n);
		a.arrayInitializerParameters.add(null); // actual parameters will be defined later
		a.nesting++;
	}
	void exitClosure(X10CPPContext_c.Closures a) {
		a.nesting--;
	}

	public void processAsync(Stmt_c n, String placeVar, Stmt asyncStmt, 
									X10CPPContext_c c, StreamWrapper sw) {
		ClassifiedStream decl = sw.getNewStream(StreamWrapper.StreamClass.Closures);
        ClassifiedStream body = sw.getNewStream(StreamWrapper.StreamClass.Closures, false);

		boolean insideClosure = c.insideClosure;
		c.setinsideClosure(true);

		X10CPPContext_c.Closures a = c.closures;
		enterAsync(a, n);
		// Async argument struct declaration.
		int id = async_id(a, n);

		decl.write("struct "+args_name(ASYNC_PREFIX, id) + ";\n"); 
		decl.newline();

        sw.pushCurrentStream(body); // Now writing into the body

		c.closures.asyncContainers.put(n, c.currentClass());
		// Declare async function
		String className = translateType(c.currentClass());
		sw.write("async_closure(" + className + ",");
		sw.allowBreak(0, " ");
		sw.write(id + ",");
		sw.allowBreak(0, " ");
		if (useStructsForAsyncArgs)
			sw.write("async_unpacked_body(");
		n.print(asyncStmt, sw, tr);
		sw.write(",");
        sw.allowBreak(0, " ");
		if (useStructsForAsyncArgs) {
			unpackArgs(sw, c, c.variables, id, ASYNC_PREFIX);
            sw.write(")");
            sw.write(",");
            sw.allowBreak(0, " ");
            sw.write("(" + VOID_PTR + " args)");
		}
		else {
            sw.write("(");
			printArgumentList(sw, c);
            sw.write(")");
		}
        sw.write(");");
        sw.newline();

		// Async argument struct definition.
		createPackedArgumentsStruct(decl, c, id, ASYNC_PREFIX);
		decl.newline(0);

		body.forceNewline(0);

		sw.popCurrentStream();

		c.setinsideClosure(insideClosure);

		if (query.hasAnnotation(n, "x10.lang.aggregate"))
			sw.write("agg_");
		sw.write("async_invocation(" + translateType(c.currentClass()) + ", " + id + ", ");
		sw.write("(");
		sw.write(placeVar);
		sw.write(")");
		//sw.write(")->x10__id");
		sw.write(", ");
		instantiateArguments(sw, c, n.position());
		sw.write(");");
		a.asyncsParameters.set(id, c.variables);

		c.finalizeClosureInstance();
		exitClosure(a);
	}

	/**
	 * TODO: consolidate with
	 * {@link #printAsyncArrayCopyInvocation(Call_c, X10CPPContext_c, Type, Expr,
	 *                                       Expr, Expr, Expr, Expr, Expr, Expr,
	 *                                       boolean)}.
	 */
	void printAsyncArrayCopyInvocation(Async_c n, X10CPPContext_c context, Type baseType,
			Expr target, Expr src, Expr srcOffset, Expr dest, Expr destOffset, Expr len,
            StreamWrapper sw)
	{
        assert (false);
		String ARRAY_ACCESSOR = useIndicesForArrayCopy ? RAW_ADJUSTED : RAW_ARRAY;
		sw.write("async_array_copy_invocation(");
		sw.begin(0);
		sw.write("(");
		n.printBlock(src, sw, tr);
		sw.write(")->"+ARRAY_ACCESSOR+"()");
		sw.allowBreak(0, " "); sw.write("+"); sw.allowBreak(0, " ");
		sw.write("(");
		n.printBlock(srcOffset, sw, tr);
		sw.write(")");
		sw.write(","); sw.allowBreak(0, " ");

		// Represent dest and destOffset as a closure to be evaluated at the target
		boolean inClosure = context.inClosure;
		boolean insideClosure = context.insideClosure;
		context.setinClosure(true);
		context.setinsideClosure(true);

		X10CPPContext_c.Closures a = context.closures;
		enterAsync(a, n);
		a.arrayCopyClosures.put(n, context.currentClass());

        ClassifiedStream inc = sw.getNewStream(StreamWrapper.StreamClass.Closures);
        sw.pushCurrentStream(inc);

		// Array copy argument struct declaration.
		int id = async_id(a, n);

		// Declare array copy function
		String className = translateType(context.currentClass());
		sw.write("array_copy_closure_and_args_struct(" + className + ",");
		sw.allowBreak(0, " ");
		sw.write(id + ",");
		sw.allowBreak(0, " ");
		sw.write(VOID_PTR+",");
		sw.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs)
			sw.write("async_unpacked_body(");
		sw.write("{ ");
		sw.write("return (");
		printBlock(n, dest, sw, tr);
		sw.write(")->"+ARRAY_ACCESSOR+"()");
		sw.allowBreak(0, " "); sw.write("+"); sw.allowBreak(0, " ");
		sw.write("(");
		printBlock(n, destOffset, sw, tr);
		sw.write("); },");
		sw.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs) {
			unpackArgs(sw, context, context.variables, id, ARRAY_COPY_PREFIX);
			sw.write(")");
			sw.write(",");
			sw.allowBreak(0, " ");
			sw.write("(" + VOID_PTR + " args)");
		}
		else {
			sw.write("(");
			printArgumentList(sw, context);
			sw.write(")");
		}
		sw.write(",");
		sw.allowBreak(0, " ");
		// Array copy argument struct definition.
		createPackedArgumentsStruct(sw, context, id, ARRAY_COPY_PREFIX);
		sw.newline();
		sw.write(");");
		sw.newline(0);

		sw.write("struct "+closure_name(CLOSURE_WRAPPER_PREFIX, id)+" : public x10lib::Closure {");
		sw.newline(4); sw.begin(0);
		sw.write(closure_name(CLOSURE_WRAPPER_PREFIX, id));
		sw.write("(");
		printArgumentList(sw, context);
		sw.write(") :");
		sw.allowBreak(0, " ");
		sw.write("x10lib::Closure(sizeof(" + args_name(ARRAY_COPY_PREFIX, id) + "),");
		sw.allowBreak(0, " ");
        sw.write(id + "),");
        sw.allowBreak(0, " ");
        sw.write("args(");
		printArgumentList(sw, context, true);  // print args with no types
        sw.write(") { }");
        sw.newline();
		if (context.variables.size() > 0) {
            sw.write(closure_name(CLOSURE_WRAPPER_PREFIX, id) + "() { }"); sw.newline();
		}
        sw.write(args_name(ARRAY_COPY_PREFIX, id) + " args;");
        sw.end(); sw.newline();
        sw.write("};");
        sw.newline(0);

        sw.forceNewline(0);

		sw.popCurrentStream();

		context.setinClosure(inClosure);
		context.setinsideClosure(insideClosure);

		sw.write("array_copy_closure_invocation(");
		sw.begin(0);
		sw.write(id + ","); sw.allowBreak(0, " ");
		instantiateArguments(sw, context, n.position());
		sw.end();
		sw.write(")");
		a.asyncsParameters.set(id, context.variables);

		context.finalizeClosureInstance();
		exitClosure(a);

		sw.write(","); sw.allowBreak(0, " ");
		sw.write("(");
		n.printBlock(len, sw, tr);
		sw.write(")*sizeof(" + translateType(baseType, true) + ")");
		sw.write(","); sw.allowBreak(0, " ");
		sw.write("(");
		n.printBlock(target, sw, tr);
		sw.write(")->x10__id");
		// FIXME: handle clocks
//		if (clock != null) {
//		w.write(","); w.allowBreak(0, " ");
//		n.printBlock(clock, w, tr);
//		}
		sw.end(); sw.write(");");
	}

	/**
	 * TODO: consolidate with
	 * {@link #printAsyncArrayCopyInvocation(Async_c, X10CPPContext_c, Type, Expr,
	 *                                       Expr, Expr, Expr, Expr, Expr)}.
	 */
	void printAsyncArrayCopyInvocation(Call_c n, X10CPPContext_c context, Type baseType,
			Expr target, Expr src, Expr srcOffset, Expr dest, Expr destOffset, Expr len,
			Expr array, boolean shouldNotify, StreamWrapper sw)
	{
        assert (false);
		String ARRAY_ACCESSOR = useIndicesForArrayCopy ? RAW_ADJUSTED : RAW_ARRAY;
		assert (eagerArrayCopyNotification); // TODO
		//w.write("x10lib::AsyncArrayCopyRawNotify("); // TODO
		sw.write("async_array_copy_invocation(");
		sw.begin(0);
		sw.write("(");
		n.printBlock(src, sw, tr);
		sw.write(")->"+ARRAY_ACCESSOR+"()");
		sw.allowBreak(0, " "); sw.write("+"); sw.allowBreak(0, " ");
		sw.write("(");
		n.printBlock(srcOffset, sw, tr);
		sw.write(")");
		sw.write(","); sw.allowBreak(0, " ");

		// Represent dest and destOffset as a closure to be evaluated at the target
		boolean inClosure = context.inClosure;
		boolean insideClosure = context.insideClosure;
		context.setinClosure(true);
		context.setinsideClosure(true);

		X10CPPContext_c.Closures a = context.closures;
		enterAsync(a, n);
		a.arrayCopyClosures.put(n, context.currentClass());

		ClassifiedStream inc = sw.getNewStream(StreamWrapper.StreamClass.Closures);
		sw.pushCurrentStream(inc);

		// Array copy argument struct declaration.
		int id = async_id(a, n);

		// Declare array copy function
		String className = translateType(context.currentClass());
		sw.write("array_copy_closure_and_args_struct(" + className + ",");
		sw.allowBreak(0, " ");
		sw.write(id + ",");
		sw.allowBreak(0, " ");
		sw.write(VOID_PTR+",");
		sw.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs)
			sw.write("async_unpacked_body(");
		sw.write("{ ");
		String destoff_id = getId();
		sw.write(translateType(destOffset.type(), true)+" "+destoff_id+" = ");
		printBlock(n, destOffset, sw, tr);
		sw.write(";"); sw.allowBreak(0, " ");
		if (eagerArrayCopyNotification && shouldNotify) {
			sw.write("(");
			n.printBlock(array, sw, tr);
			sw.write(")->"+POST_COPY_RUN+"("+destoff_id+");");
			sw.allowBreak(0, " ");
		}
		sw.write("return (");
		printBlock(n, dest, sw, tr);
		sw.write(")->"+ARRAY_ACCESSOR+"()");
		sw.allowBreak(0, " "); sw.write("+"); sw.allowBreak(0, " ");
		sw.write("(");
		sw.write(destoff_id);
		sw.write("); },");
		sw.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs) {
			unpackArgs(sw, context, context.variables, id, ARRAY_COPY_PREFIX);
			sw.write(")");
			sw.write(",");
			sw.allowBreak(0, " ");
			sw.write("(" + VOID_PTR + " args)");
		}
		else {
			sw.write("(");
			printArgumentList(sw, context);
			sw.write(")");
		}
		sw.write(",");
		sw.allowBreak(0, " ");
		// Array copy argument struct definition.
		createPackedArgumentsStruct(sw, context, id, ARRAY_COPY_PREFIX);
		sw.newline();
		sw.write(");");
		sw.newline(0);

		sw.write("struct "+closure_name(CLOSURE_WRAPPER_PREFIX, id)+" : public x10lib::Closure {");
		sw.newline(4); sw.begin(0);
		sw.write(closure_name(CLOSURE_WRAPPER_PREFIX, id));
		sw.write("(");
		printArgumentList(sw, context);
		sw.write(") :");
		sw.allowBreak(0, " ");
		sw.write("x10lib::Closure(sizeof(" + args_name(ARRAY_COPY_PREFIX, id) + "),");
		sw.allowBreak(0, " ");
		sw.write(id + "),");
		sw.allowBreak(0, " ");
		sw.write("args(");
		printArgumentList(sw, context, true);  // print args with no types
		sw.write(") { }");
		sw.newline();
		if (context.variables.size() > 0) {
			sw.write(closure_name(CLOSURE_WRAPPER_PREFIX, id) + "() { }"); sw.newline();
		}
		sw.write(args_name(ARRAY_COPY_PREFIX, id) + " args;");
		sw.end(); sw.newline();
		sw.write("};");
		sw.newline(0);

		sw.forceNewline(0);

        sw.popCurrentStream();

		context.setinClosure(inClosure);
		context.setinsideClosure(insideClosure);

		sw.write("array_copy_closure_invocation(");
		sw.begin(0);
		sw.write(id + ","); sw.allowBreak(0, " ");
		instantiateArguments(sw, context, n.position());
		sw.end();
		sw.write(")");
		a.asyncsParameters.set(id, context.variables);

		context.finalizeClosureInstance();
		exitClosure(a);

		sw.write(","); sw.allowBreak(0, " ");
		sw.write("(");
		n.printBlock(len, sw, tr);
		sw.write(")*sizeof(" + translateType(baseType, true) + ")");
		sw.write(","); sw.allowBreak(0, " ");
		sw.write("(");
		n.printBlock(target, sw, tr);
		sw.write(")->x10__id");
		// FIXME: handle clocks
//		if (clock != null) {
//		w.write(","); w.allowBreak(0, " ");
//		n.printBlock(clock, w, tr);
//		}

		// TODO
//		if (!eagerArrayCopyNotification && shouldNotify) {
//		w.write(","); w.allowBreak(0, " ");
//		// Represent argument 4 as a closure to be evaluated at the target
//		}

		sw.end(); sw.write(");");
	}

    /**
     * TODO: emit to separate file to make work on Windows
     */
    void printSwitchMethod(ClassType currentClass, String methodName,
            String retType, String prefix, ArrayList asyncs,
            ArrayList asyncsParameters, HashMap include,
            String extraArgs,
            String preCode, String postCode, HashMap classToAsyncs, ClassifiedStream w)
    {
        String className = translateType(currentClass);
        w.newline(0);

        printTemplateSignature(((X10ClassType)currentClass).typeArguments(), w);

        String arg = "arg";
        w.write(retType+" "+className+"::"+methodName+"(x10_async_handler_t h, "+VOID_PTR+" "+arg);
        if (extraArgs != null)
            w.write(", " + extraArgs);
        w.write(") {");
        w.newline(4); w.begin(0);
        w.write("switch (h) {"); w.newline();

        ArrayList relevant_asyncs = new ArrayList();

        X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
        for (int i = 0; i < asyncs.size(); i++) {
            if (!include.containsKey(asyncs.get(i)))
                continue;
            ClassType container = (ClassType) include.get(asyncs.get(i));
            if (!ts.typeEquals(container, currentClass))
                continue;
            className = translateType(container);

            relevant_asyncs.add(new Integer(i));

            w.write("case " + i + ":");
            w.newline(4); w.begin(0);
            w.write("{"); w.newline();
            w.begin(0);
            String args_struct = args_name(prefix, i);
            w.write(args_struct+"* _arg = ("+args_struct+"*) "+arg+";"); w.newline();
            if (preCode != null) {
                w.write(preCode);
                w.newline();
                if (postCode != null)
                    w.begin(0);
            }
            if (!retType.equals(VOID))
                w.write("return ");
            w.write(className + "::" + closure_name(prefix, i) + "(");
            ArrayList parameters = (ArrayList) asyncsParameters.get(i);
            // TODO: unpackArgs(w, c, parameters, i, prefix);
            for (int j = 0; j < parameters.size(); j++) {
                VarInstance p = (VarInstance) parameters.get(j);
                String name = p.name().toString();
                if (name.equals(THIS))
                    name = SAVED_THIS;
                else
                    name = mangled_non_method_name(name); 
                // FIXME: unpack refs
                w.write(((j>0) ? ", " : "") + "_arg->" + name);
            }
            w.write(");");
            w.end(); w.newline();
            if (postCode != null) {
                w.write(postCode);
                if (preCode != null)
                    w.end();
                w.newline();
            }
            w.write("}"); w.newline();
            if (retType.equals(VOID))
                w.write("break;");
            w.end(); w.newline();
        }

        if (relevant_asyncs.size() > 0) {
            int[] async_ids = new int[relevant_asyncs.size()];
            for (int i = 0; i < async_ids.length; i++)
                async_ids[i] = ((Integer) relevant_asyncs.get(i)).intValue();
            classToAsyncs.put(currentClass, async_ids);
        }

        w.write("}"); w.newline(0);
        if (!retType.equals(VOID))
            w.write("return (" + retType + ")0;");
        w.end(); w.newline();
        w.write("}"); w.newline(0); w.forceNewline(0);
    }

    void printExplicitTarget(Call_c n, Receiver target, X10CPPContext_c context, CodeWriter w) {
		if (target instanceof X10Special_c &&
				((X10Special_c)target).kind().equals(X10Special_c.THIS) &&
				(context.inlining || context.insideClosure))
		{
			w.write(SAVED_THIS);
			if (context.insideClosure)
				context.saveEnvVariableInfo(THIS);
		}
		else if (target instanceof Expr) {
			boolean assoc = !(target instanceof New_c);
			n.printSubExpr((Expr) target, assoc, w, tr);
		}
		else if (target != null) {
			n.print(target, w, tr);
		}

		return;
	}
	
	void printDeclarationList(CodeWriter w, X10CPPContext_c c, ArrayList vars) {
		printDeclarationList(w, c, vars, true);
	}
	
	void printDeclarationList(CodeWriter w, X10CPPContext_c c, ArrayList vars, boolean saved_this_mechanism) {
		for (int i = 0; i < vars.size(); i++) {
			VarInstance var = (VarInstance)vars.get(i);
			Type t = var.type();
			if (c.isSPMDVar(var)) {
				t = query.getX10ArrayElementType(t);
			}
			String type = translateType(t, true);
			List names = c.getRenameMapping(var);
			if (names == null) {
				String name = var.name().toString();
				if (saved_this_mechanism && name.equals(THIS)) {
					if (c.inlining || c.insideClosure) // FIXME: Krishna, why did you add this test?
						name = SAVED_THIS;
				}
				else {
					if (c.isGlobalVar(var) && c.getDuplicateId(var) != null)
						name += c.getDuplicateId(var);
					name = mangled_non_method_name(name);
				}
				w.write(type + " " + name + "; ");
				w.newline();
			} else {
				for (Iterator n = names.iterator(); n.hasNext(); ) {
					String name = (String) n.next();
					w.write(type + " " + name + "; ");
					w.newline();
				}
			}
		}
	}

	void unpackArgs(CodeWriter w, X10CPPContext_c c, ArrayList vars, int id, String prefix) {
		for (int i = 0; i < vars.size(); i++) {
			VarInstance var = (VarInstance)vars.get(i);
			Type t = var.type();
			if (c.isSPMDVar(var)) {
				t = query.getX10ArrayElementType(t);
			}
			String type = translateType(t, true);
			String name = var.name().toString();
			if (name.equals(THIS))
				name = SAVED_THIS;
			else
				name = mangled_non_method_name(name);
			// FIXME: unpack references properly
			w.write(type + " " + name + " = " +
					"((" + args_name(prefix, id)+"*) args)->" + name
					+ ";");
			w.newline();
		}
	}
	
	void unpackClosureArgs(CodeWriter w, X10CPPContext_c c, ArrayList vars, int id, String prefix) {
		for (int i = 0; i < vars.size(); i++) {
			VarInstance var = (VarInstance)vars.get(i);
			Type t = var.type();
			if (c.isSPMDVar(var)) {
				t = query.getX10ArrayElementType(t);
			}
			String type = translateType(t, true);
			String name = var.name().toString();
			name = mangled_non_method_name(name);
			// FIXME: unpack references properly
			w.write(type + " " + name + " = " +
					"((" + args_name(prefix, id)+"*) args)->" + name
					+ ";");
			w.newline();
		}
	}
	
	void instantiateArguments(CodeWriter w, X10CPPContext_c c, Position p) {
		instantiateArguments(w, c, p, true);
	}
	
	void instantiateArguments(CodeWriter w, X10CPPContext_c c, Position p, boolean saved_this_mechanism) {
		w.write("(");
		w.begin(0);
		for (int i = 0; i < c.variables.size(); i++) {
			if (i > 0) {
				w.write(",");
				w.allowBreak(2, " ");
			}
			VarInstance v = (VarInstance)c.variables.get(i);
			String name = v.name().toString();
			if (saved_this_mechanism && name.equals(THIS)) {
				if (c.inlining || c.insideClosure)
					name = SAVED_THIS;
			}
			else
				name = mangled_non_method_name(name);
			if (v.type().isPrimitive()) {
				w.write(name);
			} else if (((X10TypeSystem) tr.typeSystem()).isValueType(v.type())) {
				// FIXME: allocate a buffer and pack things properly (translate references, etc)
/*
				if (!c.isUnbroadcastable(v))
					tr.job().compiler().errorQueue().enqueue(ErrorInfo.WARNING,
							"Warning: attempt to serialize non-primitive value "+name+" -- IGNORING", p);
*/
				w.write("/"+"*"+" Serialization of value "+name+" should go here "+"*"+"/");
				w.write(name);
				// FIXME: [IP] cannot invoke addUnbroadcastable from non-main methods
				//c.addUnbroadcastable(v);
			} else {
				// FIXME: translate references
/*
				if (!c.isUnbroadcastable(v))
					tr.job().compiler().errorQueue().enqueue(ErrorInfo.WARNING,
							"Warning: attempt to serialize reference "+name+" -- IGNORING", p);
*/
				w.write("/"+"*"+" Serialization of reference "+name+" should go here "+"*"+"/");
				w.write(name);
				// FIXME: [IP] cannot invoke addUnbroadcastable from non-main methods
				//c.addUnbroadcastable(v);
			}
		}
		w.end();
		w.write(")");
	}
	
	void generateSerializationMethods(ClassType type, StreamWrapper sw) {
		// FIXME: Has a lot of string constants. Refactor them
		// into final variables.
		// -Krishna.
		X10ClassType ct = (X10ClassType) type.toClass();
		X10TypeSystem ts = (X10TypeSystem) type.typeSystem();
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
        ClassifiedStream w = sw.body();
		ClassifiedStream h = sw.header();
		h.forceNewline();
		h.write("// Serialization"); h.newline();
		String klass = translateType(type);
		int serializationId = getUniqueId_().intValue();
		h.write("public: static const int "+SERIALIZATION_ID_FIELD+" = "+serializationId+";");
		h.newline();

		// constructor (FIXME: public because "friend" below doesn't work)
		h.write("public: explicit "+mangled_non_method_name(type.name().toString())+"("+SERIALIZATION_MARKER+" m) ");
		Type parent = type.superClass();
		if (parent !=null && ts.isValueType(parent))
			h.write(": "+ translateType(parent)+"(m)");
		h.write("{ (void) m; }");
		h.newline();
		// FIXME: this doesn't work
		//// Make sure the reference serializer can access the above
		//h.write("template<> friend struct x10aux::_reference_serializer<"+type.name()+">;");
		//h.newline();

		// _serialize()
		h.write("public: ");
		if (!type.flags().isFinal())
			h.write("virtual ");
		h.write("void "+SERIALIZE_METHOD+"("+SERIALIZATION_BUFFER+"& buf, x10aux::addr_map& m) "+
		"{ x10aux::_serialize_ref(this, buf, m); }");
		h.newline(0);

		// _serialize_fields()
		h.write("public: ");
		if (!type.flags().isFinal())
			h.write("virtual ");
		h.write("void "+SERIALIZE_FIELDS_METHOD+"("+SERIALIZATION_BUFFER+"& buf, x10aux::addr_map& m);"); h.newline(0);

		printTemplateSignature(ct.typeArguments(), w);
		w.write("void "+klass+"::"+SERIALIZE_FIELDS_METHOD+"("+SERIALIZATION_BUFFER+"& buf, x10aux::addr_map& m) {");
		w.newline(4); w.begin(0);
		if (parent != null && ts.isValueType(parent)) {
			w.write(translateType(parent)+"::"+SERIALIZE_FIELDS_METHOD+"(buf, m);");
			w.newline();
		}
		for (int i = 0; i < type.fields().size(); i++) {
			if (i != 0)
				w.newline();
			FieldInstance f = (FieldInstance) type.fields().get(i);
			if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
				continue;
			String fieldName = mangled_field_name(f.name().toString());
			if (f.type().isBoolean() || f.type().isNumeric()) {
				w.write("buf.write(this->"+fieldName+");"); w.newline();
				w.write("_S_(\"Written \" << this->"+fieldName+");");
			} else if (ts.isValueType(f.type())) {
				w.write("if (!m.ensure_unique(this->"+fieldName+")) assert (false);"); w.newline();
				w.write("this->"+fieldName+"->"+SERIALIZE_METHOD+"(buf, m);"); w.newline();
				w.write("_S_(\"Serialized "+fieldName+"\");");
			} else {
				w.write("buf.write(this->"+fieldName+"); "+"/"+"*"+" non-value "+"*"+"/"); w.newline();
				w.write("_S_(\"Written reference "+fieldName+"\");");
			}
		}
		w.end(); w.newline();
		w.write("}");
		w.newline();
		w.forceNewline();

		// _deserialize()
		h.write("public: ");
		if (!type.flags().isFinal())
			h.write("virtual ");
		h.write("void "+DESERIALIZE_FIELDS_METHOD+"("+SERIALIZATION_BUFFER+"& buf);"); h.newline(0);
		printTemplateSignature(ct.typeArguments(), w);
		w.write("void "+klass+"::"+DESERIALIZE_FIELDS_METHOD+"("+SERIALIZATION_BUFFER+"& buf) {");
		w.newline(4); w.begin(0);
		for (int i = 0; i < type.fields().size(); i++) {
			if (i != 0)
				w.newline();
			FieldInstance f = (FieldInstance) type.fields().get(i);
			if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
				continue;
			String fieldName = mangled_field_name(f.name().toString());
			if (f.type().isBoolean() || f.type().isNumeric()) {
				w.write("this->"+fieldName+" = buf.read<"+translateType(f.type())+" >();");
			} else if (ts.isValueType(f.type())) {
				w.write("this->"+fieldName+" = x10aux::_deserialize_value_ref<"+translateType(f.type())+" >(buf);");
			} else {
				w.write("this->"+fieldName+" = buf.read<"+translateType(f.type(), true)+" >(); "+"/"+"*"+" non-value "+"*"+"/");
			}
		}
		w.end(); w.newline();
		w.write("}");
		w.newline();
		w.forceNewline();
	}

	public void emitUniqueNS(QName name, ArrayList<String> history, CodeWriter w) {
		if (name == null) return;
		if  (!history.contains(name.toString())) {
			openNamespaces(w, name);
			closeNamespaces(w, name);
			w.newline();
			w.write("using namespace "+translateFQN(name.toString())+";");
			history.add(name.toString());
		}
		return;
	}

    public static void openNamespaces(CodeWriter h, QName name) {
        if (name == null) return;
        openNamespaces(h, name.qualifier());
        h.write("namespace "+name.name()+" { ");
    }       
                
    public static void closeNamespaces(CodeWriter h, QName name) {
        if (name == null) return;
        h.write("} ");
        closeNamespaces(h, name.qualifier());
    }
     
	public String makeUnsignedType(Type t) {
		// FIXME: HACK!
		if (t.isInt())
			return "uint32_t";
		if (t.isLong())
			return "uint64_t";
		return "unsigned "+translateType(t);
	}

	private String dumpRegex(String id, Object[] components, Translator tr, String regex) {
	    String retVal = "";
	    for (int i = 0; i < components.length; i++) {
	        assert ! (components[i] instanceof Object[]);
	    }
	    int len = regex.length();
	    int pos = 0;
	    int start = 0;
	    while (pos < len) {
	    	if (regex.charAt(pos) == '\n') {
	    		retVal +=regex.substring(start, pos);
			retVal += "\n";
	    		start = pos+1;
	    	}
	    	else
	    	if (regex.charAt(pos) == '#') {
	    		retVal += regex.substring(start, pos); //translateFQN(regex.substring(start, pos));
	    		Integer idx = new Integer(regex.substring(pos+1,pos+2));
	    		pos++;
	    		start = pos+1;
	    		if (idx.intValue() >= components.length){
	    			throw new InternalCompilerError("Template '"+id+"' '"+regex+"' uses #"+idx+" (max is "+(components.length-1)+")");
                }
                Object o = components[idx.intValue()];
                if (o instanceof Type) {
                    retVal += translateType((Type)o, true);
                } else if (o != null) {
                    retVal += o.toString();
                }
	    	}
	    	pos++;
	    }
	    retVal += regex.substring(start); //translateFQN(regex.substring(start));
	    return retVal;
	}
	public void dumpRegex(String id, Object[] components, Translator tr, String regex, CodeWriter w) {
		for (int i = 0; i < components.length; i++) {
			assert ! (components[i] instanceof Object[]);
		}
		int len = regex.length();
		int pos = 0;
		int start = 0;
		while (pos < len) {
			if (regex.charAt(pos) == '\n') {
				w.write(regex.substring(start, pos));
				w.newline(0);
				start = pos+1;
			}
			else
			if (regex.charAt(pos) == '#') {
				w.write(regex.substring(start, pos) /*translateFQN(regex.substring(start, pos))*/);
				Integer idx = new Integer(regex.substring(pos+1,pos+2));
				pos++;
				start = pos+1;
				if (idx.intValue() >= components.length) {
					throw new InternalCompilerError("Template '"+id+"' '"+regex+"' uses #"+idx+" (max is "+(components.length-1)+")");
				}
				prettyPrint(components[idx.intValue()], tr, w);
			}
			pos++;
		}
		w.write(regex.substring(start) /*translateFQN(regex.substring(start))*/);
	}
    private void prettyPrint(Object o, Translator tr, CodeWriter w) {
        if (o instanceof Node) {
            Node n = (Node) o;
            X10CPPContext_c context = (X10CPPContext_c) tr.context();
            ((X10CPPTranslator) tr).setContext(n.del().enterScope(context));
            n.del().translate(w, tr);
            ((X10CPPTranslator) tr).setContext(context);
        } else if (o instanceof Type) {
            w.write(translateType((Type)o, true));
        } else if (o != null) {
            w.write(o.toString());
        }
    }
}
// vim:tabstop=4:shiftwidth=4:expandtab
