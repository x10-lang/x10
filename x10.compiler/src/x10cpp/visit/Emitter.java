package x10cpp.visit;

import static x10cpp.visit.ASTQuery.getCppBoxRep;
import static x10cpp.visit.ASTQuery.getCppRep;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZATION_BUFFER;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZER_METHOD;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZE_BODY_METHOD;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZE_METHOD;
import static x10cpp.visit.SharedVarsMethods.SAVED_THIS;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_BUFFER;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_ID_FIELD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZE_BODY_METHOD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZE_ID_METHOD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZE_METHOD;
import static x10cpp.visit.SharedVarsMethods.THIS;
import static x10cpp.visit.SharedVarsMethods.chevrons;
import static x10cpp.visit.SharedVarsMethods.make_ref;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Call_c;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.Translator;
import x10.ast.X10Special;
import x10.ast.X10Special_c;
import x10.types.FunctionType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.visit.StaticNestedClassRemover;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;
import x10cpp.types.X10CPPContext_c;

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
        "volatile", "wchar_t", "while",
        // operator names
        "and", "and_eq", "not", "not_eq", "or", "or_eq", "xor", "xor_eq",
        "bitand", "bitor", "compl",
        // X10 types
        "x10_boolean", "x10_byte", "x10_char", "x10_short", "x10_int",
        "x10_long", "x10_float", "x10_double",
        // X10 implementation names
        "FMGL", "TYPENAME", "getRTT", "rtt", "RTT_H_DECLS", "RTT_CC_DECLS1",
        // Additionally, anything starting with a '_' is reserved, and may clash
    };
    private static boolean isCPPKeyword(String name) {
        for (int i = 0; i < CPP_KEYWORDS.length; i++) {
            if (CPP_KEYWORDS[i].equals(name))
                return true;
        }
        return false;
    }
    private static String mangle_to_cpp(String str) {
        str = x10.emitter.Emitter.mangleIdentifier(str);
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
    public static String mangled_parameter_type_name(String str) {
        return "FMGL("+mangle_to_cpp(str)+")";
    }


	/**
	 * Same as n.printBlock(child, w, tr); but without the enterScope call.
	 */
	void printBlock(Node n, Expr child, StreamWrapper w, Translator tr) {
		w.begin(0);
		child.del().translate(w, tr);
		w.end();
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
	public static String translateType(Type type) {
		return translateType(type, false);
	}


	/**
	 * Translate a type.
	 *
	 * @param type type to translate
	 * @param asRef whether to make a reference
	 * @return a string representation of the type
	 */
	public static String translateType(Type type, boolean asRef) {
		assert (type != null);
		X10TypeSystem_c xts = (X10TypeSystem_c) type.typeSystem();
		type = xts.expandMacros(type);
		if (type.isVoid()) {
			return "void";
		}
		// TODO: handle closures
//		if (((X10TypeSystem) type.typeSystem()).isClosure(type))
//			return translateType(((X10Type) type).toClosure().base(), asRef);
		type = X10TypeMixin.baseType(type);
		String name = null;
		// TODO
//		if (type instanceof ClosureType) {
//		    ClosureType ct = (ClosureType) type;
//		    assert (ct.typeArguments().size() != 0);
//		    name = "x10aux::Fun";
//		    String args = "";
//		    if (ct.returnType().isVoid())
//		        args += translateType(ct.returnType(), true) + ", ";
//		    int s = ct.typeArguments().size();
//		    for (Type t: ct.typeArguments()) {
//		        args += translateType(t, true); // type arguments are always translated as refs
//		        if (--s > 0)
//		            args +=", ";
//		    }
//		    name += chevrons(args);
//		} else
		if (type.isClass()) {
			X10ClassType ct = (X10ClassType) type.toClass();
			if (ct.isX10Struct()) {
				// Struct types are not boxed up as Refs.  They are always generated as just C++ class types
				// (Note: not pointer to Class, but the actual class).
				asRef = false;
			}

		    if (ct.isAnonymous()) {
		        if (ct.interfaces().size() == 1 && ct.interfaces().get(0) instanceof FunctionType) {
		            return translateType(ct.interfaces().get(0), asRef);
		        } else {
		            assert false : "unexpected anonymous type " + ct;
		            assert ct.superClass() != null;
		       	    return translateType(ct.superClass(), asRef);
		       	}
		    } else {
				X10ClassDef cd = ((X10ClassType) type).x10Def();
				String pat = null;
				if (!asRef)
					pat = getCppBoxRep(cd);
				else
					pat = getCppRep(cd);
				if (pat != null) {
					List<Type> typeArguments = ct.typeArguments();
					Object[] o = new Object[typeArguments.size()+1];
					int i = 0;
					o[i++] = type;
					for (Type a : typeArguments) {
					    o[i++] = a;
					}
					// FIXME: [IP] Clean up this code!
					return dumpRegex("NativeRep", o, pat);
				}
				else {
					if (ct.def().isNested()) {
						// This is a legitimate case.  We do not invoke StaticNestedClassRemover on
						// classes for which we don't generate code.  Thus, while we cannot see a
						// definition of a nested class, we may well see references to such classes.
						// At the moment, we cannot make sure that StaticNestedClassRemover runs on
						// all classes - when we can, we should re-enable this assert.
						//assert false : "Nested class: "+ct;
						Name mangled = StaticNestedClassRemover.mangleName(ct.def());
						QName pkg = ct.package_() != null ? ct.package_().fullName() : null;
						QName full = QName.make(pkg, mangled);
						name = full.toString();
					} else
						name = ct.fullName().toString();
				}
			}
			if (ct.typeArguments().size() != 0) {
				String args = "";
				int s = ct.typeArguments().size();
				for (Type t: ct.typeArguments()) {
					args += translateType(t, true); // type arguments are always translated as refs
					if (--s > 0)
						args +=", ";
				}
				name += chevrons(args);
			}
		} else if (type instanceof ParameterType) {
			name = ((ParameterType)type).name().toString();
			return mangled_parameter_type_name(name); // parameter types shouldn't be refs
		} else if (type.isNull()) {
			return "x10aux::NullType"; // typedef to something sensible
		} else
			assert false : type; // unhandled type.
		assert (name != null);
		name = translate_mangled_FQN(name);
		if (!asRef)
			return name;
		return make_ref(name);
	}
	
	public static String structMethodClass(ClassType ct, boolean fqn, boolean chevrons) {
	    X10ClassType classType = (X10ClassType)ct;
	    QName qname = classType.fullName();
	    String name = fqn ? qname.toString() : qname.name().toString();
	    name += "_methods";
        if (chevrons && classType.typeArguments().size() != 0) {
            String args = "";
            int s = classType.typeArguments().size();
            for (Type t: classType.typeArguments()) {
                args += translateType(t, true); // type arguments are always translated as refs
                if (--s > 0)
                    args +=", ";
            }
            name += chevrons(args);
        }
	    name = translate_mangled_FQN(name);
	    return name;
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


	public void printTemplateSignature(List<Type> list, CodeWriter h) {
		int size = list.size();
		if (size != 0) {
			h.write("template<class ");
			for (Type t: list) {
				assert (t instanceof ParameterType);
				h.write(translateType(t));
				size--;
				if (size > 0)
					h.write(", class ");
			}
			h.write(">");
			h.allowBreak(0, " ");
		}
	}

	static List<Type> toTypeList(List<Ref<? extends Type>> list) {
		ArrayList<Type> res = new ArrayList<Type>();
		for (Ref<? extends Type> r : list)
			res.add(r.get());
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
		if (num <= 0)
		    return "";
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

	static MethodInstance getOverridingMethod(X10TypeSystem xts, ClassType localClass, MethodInstance mi, Context context) {
	    try {
	        List<Type> params = ((X10MethodInstance) mi).typeParameters();
	        List<MethodInstance> overrides = xts.findAcceptableMethods(localClass, xts.MethodMatcher(localClass, mi.name(), ((X10MethodInstance) mi).typeParameters(), mi.formalTypes(), context));
	        for (MethodInstance smi : overrides) {
	            List<Type> sparams = ((X10MethodInstance) smi).typeParameters();
	            if (params == null && sparams == null)
	                return smi;
	            if (params != null && params.equals(sparams))
	                return smi;
	        }
	        return null;
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
		Context context = tr.context();

		X10ClassType classType = (X10ClassType) from.container();
		X10ClassType superClass = (X10ClassType) X10TypeMixin.baseType(classType.superClass());
		List<Type> interfaces = classType.interfaces();
		Type returnType = null;

		if (superClass != null) {
			MethodInstance superMeth = getOverridingMethod(xts,superClass,from,context);
			if (superMeth != null) {
				//System.out.println(from+" overrides "+superMeth);
				returnType = findRootMethodReturnType(n, pos, superMeth);
			}
		}

		for (Type itf : interfaces) {
			X10ClassType itf_ = (X10ClassType) itf;
			// same thing again for interfaces
			MethodInstance superMeth = getOverridingMethod(xts,itf_,from,context);
			if (superMeth != null) {
				//System.out.println(from+" implements "+superMeth);
				Type newReturnType = findRootMethodReturnType(n, pos, superMeth);

				// check --
				if (returnType != null && !xts.typeDeepBaseEquals(returnType, newReturnType, context)) {
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
		X10MethodDef def = (X10MethodDef) n.methodDef();
		X10MethodInstance mi = (X10MethodInstance) def.asInstance();
		X10ClassType container = (X10ClassType) mi.container();
		boolean isStruct = container.isX10Struct();

		h.begin(0);

		if (qualify) {
			printTemplateSignature(((X10ClassType)n.methodDef().container().get()).typeArguments(), h);
		}

		printTemplateSignature(toTypeList(def.typeParameters()), h);

		if (!qualify) {
			if (flags.isStatic())
				h.write(flags.retain(Flags.STATIC).translate());
			else if (def.typeParameters().size() != 0) {
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
			        String msg = n.methodDef()+" is generic and non-final, disabling virtual binding for this method";
			        tr.job().compiler().errorQueue().enqueue(ErrorInfo.WARNING, msg, n.position());
			    }
			}
            // [DC] there is no benefit to omitting the virtual keyword as we can
            // statically bind CALLS to final methods and methods that are members of final classes
			else if (!isStruct && !flags.isProperty() && !flags.isPrivate()) {
			    h.write("virtual ");
			}
		}
		if (!qualify && !flags.isStatic() && container.isX10Struct()) h.write("static ");
		printType(ret, h);
		h.allowBreak(2, 2, " ", 1);
		if (qualify) {
		    h.write((container.isX10Struct() ? structMethodClass(container, true, true) : translateType(container))+ "::");
		}
		h.write(mangled_method_name(name));
		h.write("(");
		h.allowBreak(2, 2, "", 0);
		h.begin(0);
		if (isStruct && !flags.isStatic()) {
		    h.write(translateType(container) +" this_");
		    if (!n.formals().isEmpty()) h.write(", ");
		}
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
			if (n.body() == null && !flags.isExtern() && !flags.isProperty() && !isStruct)
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

	private void printAllTemplateSignatures(ClassDef cd, CodeWriter h) {
		if (cd.isNested()) {
			assert (false) : ("Nested class alert!");
			printAllTemplateSignatures(cd.outer().get(), h);
		}
		printTemplateSignature(((X10ClassType)cd.asType()).typeArguments(), h);
	}

    void printRTTDefn(X10ClassType ct, CodeWriter h) {
        X10TypeSystem_c xts = (X10TypeSystem_c) ct.typeSystem();
		String x10name = ct.fullName().toString();
		int numParents = 0;
		if (ct.superClass() != null && !xts.isAny(ct.superClass())) {
		    numParents++;
		}
		for (Type iface: ct.interfaces()) {
		    if (!xts.isAny(iface)) numParents++;
		}
		
		if (ct.typeArguments().isEmpty()) {
		  boolean first = true;
          h.write("x10aux::RuntimeType "+translateType(ct)+"::rtt;"); h.newline();
          h.write("void "+translateType(ct)+"::_initRTT() {"); h.newline(4); h.begin(0);
          h.write("rtt.canonical = &rtt;"); h.newline();
          if (numParents > 0) { 
              h.write("const x10aux::RuntimeType* parents["+numParents+"] = { ");
              if (ct.superClass() != null && !xts.isAny(ct.superClass())) {
                  h.write("x10aux::getRTT" + chevrons(translateType(ct.superClass())) + "()");
                  first = false;
              }
              for (Type iface : ct.interfaces()) {
                  if (xts.isAny(iface)) continue; // IGNORE ANY
                  if (!first) h.write(", ");
                  h.write("x10aux::getRTT"+chevrons(translateType(iface))+"()");
                  first = false;
              }
              h.write("};"); h.newline();
          } else {
              h.write("const x10aux::RuntimeType** parents = NULL; "); h.newline();
          }
          h.write("rtt.init(&rtt, \""+ct.fullName()+"\", "+numParents+ ", parents, 0, NULL, NULL);");
          if (ct.isX10Struct() && isPointerless(ct)) {
              h.newline(); h.write("rtt.containsPtrs = false;");
          }
          h.end(); h.newline();
          h.write("}"); h.newline();
		} else {
		    boolean first = true;
		    int numTypeParams = ct.typeArguments().size();
            printTemplateSignature(ct.typeArguments(), h);
            h.write("x10aux::RuntimeType "+translateType(ct)+"::rtt;"); h.newline();

            printTemplateSignature(ct.typeArguments(), h);
            h.write("void "+translateType(ct)+"::_initRTT() {"); h.newline(4); h.begin(0);
            h.write("rtt.canonical = &rtt;"); h.newline();
            if (numParents > 0) {
                h.write("const x10aux::RuntimeType* parents["+numParents+"] = { ");
                if (ct.superClass() != null && !xts.isAny(ct.superClass())) {
                    h.write("x10aux::getRTT" + chevrons(translateType(ct.superClass())) + "()");
                    first = false;
                }
                for (Type iface : ct.interfaces()) {
                    if (xts.isAny(iface)) continue; // IGNORE ANY
                    if (!first) h.write(", ");
                    h.write("x10aux::getRTT"+chevrons(translateType(iface))+"()");
                    first = false;
                }
                h.write("};"); h.newline();
            } else {
                h.write("const x10aux::RuntimeType** parents = NULL; "); h.newline();                
            }
            h.write("const x10aux::RuntimeType* params["+numTypeParams+"] = { ");
            first = true;
            for (Type param : ct.typeArguments()) {
            	if (!first) h.write(", ");
                h.write("x10aux::getRTT"+chevrons(translateType(param))+"()");
                first = false;
            }
            h.write("};"); h.newline();
            
            h.write("x10aux::RuntimeType::Variance variances["+numTypeParams+"] = { ");
            first = true;
            for (ParameterType.Variance v : ct.x10Def().variances()) {
            	if (!first) h.write(", ");
            	switch(v) {
            	case COVARIANT: h.write("x10aux::RuntimeType::covariant"); break;
            	case CONTRAVARIANT: h.write("x10aux::RuntimeType::contravariant"); break;
            	case INVARIANT: h.write("x10aux::RuntimeType::invariant"); break;
            	default: assert false : "Unexpected Variance";
            	}
                first = false;
            }
            h.write("};"); h.newline();
            
            h.write("const x10aux::RuntimeType *canonical = x10aux::getRTT"+chevrons(translateType(MessagePassingCodeGenerator.getStaticMemberContainer(ct),false))+"();");
            h.newline();
            
            h.write("const char *name = "); h.newline(4);
            h.write("x10aux::alloc_printf("); h.begin(0);
            h.write("\""+x10name+"[");
            for (int i=0; i<numTypeParams; i++) {
                h.write(i > 0 ? ",%s" : "%s");
            }
            h.write("]\"");
            for (int i=0; i<numTypeParams; i++) {
                h.write(", params["+i+"]->name()"); h.newline();
            }
            h.write(");") ; h.end(); h.newline();
            h.write("rtt.init(canonical, name, "+numParents+", parents, "+numTypeParams+", params, variances);"); h.end(); h.newline();
            h.write("}"); h.newline();
		}
		h.newline();
	}

    // Helper method to recursively examine the fields of a struct and determine if they
    // are pointers.  Used to mark the RTT of the struct as pointerless, thus enabling
    // Rails/ValRails of pointerless structs to be allocated with GC_MALLOC_ATOMIC
    private boolean isPointerless(X10ClassType ct) {
        assert ct.isX10Struct() : "Only structs should be checked to see if they are pointerless";
        
        for (FieldInstance fi : ct.fields()) {
            if (fi.flags().isStatic()) continue; // ignore static fields; only care about instance fields
            if (!fi.type().isNumeric()) {
                if (fi.type().isClass() && ((X10ClassType)fi.type().toClass()).isX10Struct()) {
                    // recursively check fields of struct type
                    if (!isPointerless(((X10ClassType)fi.type().toClass()))) return false;
                } else {
                    // if fi.type() isn't numeric and isn't a struct, it must be a pointer.
                    return false;
                }
            }
        }
        
        return true;
    }
                                                    
                                                    
    
    void printHeader(ClassDecl_c n, CodeWriter h, Translator tr) {
		h.begin(0);
		
		// Handle generics
		// If it involves Parameter Types then generate C++ templates.
		printAllTemplateSignatures(n.classDef(), h);

		h.write("class ");
		assert (!n.classDef().isLocal());
		if (n.classDef().isNested() && !n.classDef().isLocal()) { // FIXME: handle local classes
			assert (false) : ("Nested class alert!");
			h.write(translateType(n.classDef().outer().get().asType()) + "::");
		}
		h.write(mangled_non_method_name(n.name().id().toString()));

		if (!n.flags().flags().isInterface() && n.superClass() != null) {
			String parent = translateType(n.superClass().type());
			h.write(" : public "+parent);
		}

		h.unifiedBreak(0);
		h.end();
	}
    
    void printHeaderForStructMethods(ClassDecl_c n, CodeWriter h, Translator tr) {
        h.begin(0);
        
        // Handle generics
        // If it involves Parameter Types then generate C++ templates.
        printAllTemplateSignatures(n.classDef(), h);

        h.write("class ");
        h.write(structMethodClass(n.classDef().asType(), false, false));

        h.unifiedBreak(0);
        h.end();
    }
    
	void printHeader(ConstructorDecl_c n, CodeWriter h, Translator tr,
                     boolean define, boolean isMakeMethod, String rType) {
        Flags flags = n.flags().flags();

		X10ClassType container = (X10ClassType) n.constructorDef().container().get();
		if (define){
			printTemplateSignature((container).typeArguments(), h);
		}

		X10ConstructorDef def = (X10ConstructorDef) n.constructorDef();
		printTemplateSignature(toTypeList(def.typeParameters()), h);

		h.begin(0);
		if (!define && container.isX10Struct()) h.write("static ");
		String typeName = translateType(container.def().asType());
        // not a virtual method, this function is called only when the static type is precise
        h.write(rType + " ");
		if (define) {
		    h.write((container.isX10Struct() ? structMethodClass(container, true, true) : typeName) + "::"); 
		}
		h.write((isMakeMethod ? SharedVarsMethods.MAKE : SharedVarsMethods.CONSTRUCTOR) + "(");
		h.allowBreak(2, 2, "", 0);
		h.begin(0);
		if (container.isX10Struct() && !isMakeMethod) {
		    h.write(typeName + " *this_");
		    if (!n.formals().isEmpty()) h.write(", ");
		}
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
        // TODO: caller should emit this
	}

	void printHeader(FieldDecl_c n, CodeWriter h, Translator tr, boolean qualify) {
		Flags flags = n.flags().flags();
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
		if (qualify) {
		    X10ClassType declClass = (X10ClassType)n.fieldDef().asInstance().container().toClass();
			h.write((declClass.isX10Struct() ? structMethodClass(declClass, true, true) : translateType(declClass)) + "::");
		}
		h.write(mangled_field_name(n.name().id().toString()));
		h.end();
	}

	void printHeader(LocalDecl_c n, CodeWriter h, Translator tr, boolean qualify) {
		//Flags flags = n.flags().flags();
		h.begin(0);
		// Let us not generate constants - We will have problem in
		// initializing away from the place where it is declared.
		//if (flags.isFinal())            }
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

	void enterClosure(X10CPPContext_c c) {
		c.advanceClosureId();
	}
	void exitClosure(X10CPPContext_c c) {
	}

    void printExplicitTarget(Call_c n, Receiver target, X10CPPContext_c context, CodeWriter w) {
		if (target instanceof X10Special_c &&
				((X10Special_c)target).kind().equals(X10Special_c.THIS) &&
				context.isInsideClosure())
		{
			w.write(SAVED_THIS);
			if (context.isInsideClosure())
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

	public void printDeclarationList(CodeWriter w, X10CPPContext_c c, ArrayList<VarInstance<?>> vars) {
		printDeclarationList(w, c, vars, true, false);
	}

	void printDeclarationList(CodeWriter w, X10CPPContext_c c, ArrayList<VarInstance<?>> vars, boolean saved_this_mechanism, boolean writable) {
		for (int i = 0; i < vars.size(); i++) {
			VarInstance<?> var = vars.get(i);
			Type t = var.type();
			String type = translateType(t, true);
			if (writable && !var.name().toString().equals(THIS)) // this is a temporary ref
			    type = type + "&"; // FIXME: Hack to get writable args in finally closures
			String name = var.name().toString();
			if (saved_this_mechanism && name.equals(THIS)) {
				assert (c.isInsideClosure()); // FIXME: Krishna, why did you add this test?
				name = SAVED_THIS;
			}
			else {
				name = mangled_non_method_name(name);
			}
			w.write(type + " " + name + ";");
			w.newline();
		}
	}

    void generateClassSerializationMethods(ClassType type, StreamWrapper sw) {
        X10ClassType ct = (X10ClassType) type.toClass();
        X10TypeSystem ts = (X10TypeSystem) type.typeSystem();
        X10CPPContext_c context = (X10CPPContext_c) tr.context();
        ClassifiedStream w = sw.body();
        ClassifiedStream h = sw.header();
        h.forceNewline();

        h.write("// Serialization"); h.newline();
        String klass = translateType(type);

        String template = context.inTemplate() ? "template " : "";

        if (!type.flags().isAbstract()) {
            // _serialization_id
            h.write("public: static const x10aux::serialization_id_t "+SERIALIZATION_ID_FIELD+";"); h.newline();
            h.forceNewline();
            printTemplateSignature(ct.typeArguments(), w);
            w.write("const x10aux::serialization_id_t "+klass+"::"+SERIALIZATION_ID_FIELD+" = ");
            w.newline(4);
            w.write("x10aux::DeserializationDispatcher::addDeserializer(");
            w.write(klass+"::"+template+DESERIALIZER_METHOD+chevrons(translateType(ts.Object()))+");");
            w.newline(); w.forceNewline();
        }

        if (type.flags().isFinal()) {
            // _serialize()
            h.write("public: ");
            h.write("static void "+SERIALIZE_METHOD+"("); h.begin(0);
            h.write(make_ref(klass)+" this_,"); h.newline();
            h.write(SERIALIZATION_BUFFER+"& buf);"); h.end();
            h.newline(); h.forceNewline();
            printTemplateSignature(ct.typeArguments(), w);
            w.write("void "+klass+"::"+SERIALIZE_METHOD+"("); w.begin(0);
            w.write(make_ref(klass)+" this_,"); w.newline();
            w.write(SERIALIZATION_BUFFER+"& buf) {"); w.end(); w.newline(4); w.begin(0);
            w.write(    "_serialize_reference(this_, buf);"); w.newline();
            w.write(    "if (this_ != x10aux::null) {"); w.newline(4); w.begin(0);
            w.write(        "this_->_serialize_body(buf);"); w.end(); w.newline();
            w.write(    "}"); w.end(); w.newline();
            w.write("}"); w.newline();
            w.forceNewline();
        }

        // _serialize_id()
        if (!type.flags().isAbstract()) {
            h.write("public: ");
            if (!type.flags().isFinal())
                h.write("virtual ");
            h.write("x10aux::serialization_id_t "+SERIALIZE_ID_METHOD+"() {");
            h.newline(4); h.begin(0);
            h.write(" return "+SERIALIZATION_ID_FIELD+";"); h.end(); h.newline();
            h.write("}"); h.newline();
            h.forceNewline();
        }


        // _serialize_body()
        h.write("public: ");
        if (!type.flags().isFinal())
            h.write("virtual ");
        h.write("void "+SERIALIZE_BODY_METHOD+"("+SERIALIZATION_BUFFER+"& buf);");
        h.newline(0); h.forceNewline();

        printTemplateSignature(ct.typeArguments(), w);
        w.write("void "+klass+"::"+SERIALIZE_BODY_METHOD+
                    "("+SERIALIZATION_BUFFER+"& buf) {");
        w.newline(4); w.begin(0);
        Type parent = type.superClass();
        if (parent != null && parent.isClass()) {
            w.write(translateType(parent)+"::"+SERIALIZE_BODY_METHOD+"(buf);");
            w.newline();
        }
        for (int i = 0; i < type.fields().size(); i++) {
            if (i != 0)
                w.newline();
            FieldInstance f = (FieldInstance) type.fields().get(i);
            if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
                continue;
            if (!X10Flags.toX10Flags(f.flags()).isGlobal()) // only serialize global fields of classes
                continue;
            String fieldName = mangled_field_name(f.name().toString());
            w.write("buf.write(this->"+fieldName+");"); w.newline();
        }
        w.end(); w.newline();
        w.write("}");
        w.newline(); w.forceNewline();

        if (!type.flags().isAbstract()) {
            // _deserializer()
            h.write("public: template<class __T> static ");
            h.write(make_ref("__T")+" "+DESERIALIZER_METHOD+"("+DESERIALIZATION_BUFFER+"& buf);");
            h.newline(); h.forceNewline();
            sw.pushCurrentStream(context.templateFunctions);
            printTemplateSignature(ct.typeArguments(), sw);
            sw.write("template<class __T> ");
            sw.write(make_ref("__T")+" "+klass+"::"+DESERIALIZER_METHOD+"("+DESERIALIZATION_BUFFER+"& buf) {");
            sw.newline(4); sw.begin(0);
            sw.writeln(make_ref(klass)+" this_ = "+
                       "new (x10aux::alloc_remote"+chevrons(klass)+"()) "+klass+"();");
            sw.writeln("this_->"+DESERIALIZE_BODY_METHOD+"(buf);");
            sw.write("return this_;");
            sw.end(); sw.newline();
            sw.writeln("}"); sw.forceNewline();
            sw.popCurrentStream();
        }

        if (type.flags().isFinal()) {
            // _deserialize()
            h.write("public: template<class __T> static ");
            h.write(make_ref("__T")+" "+DESERIALIZE_METHOD+"("+DESERIALIZATION_BUFFER+"& buf);");
            h.newline(); h.forceNewline();
            sw.pushCurrentStream(context.templateFunctions);
            printTemplateSignature(ct.typeArguments(), sw);
            sw.write("template<class __T> ");
            sw.write(make_ref("__T")+" "+klass+"::"+DESERIALIZE_METHOD+"("+DESERIALIZATION_BUFFER+"& buf) {");
            sw.newline(4); sw.begin(0);
            sw.writeln("x10::lang::Ref::_reference_state rr = " +
                    "x10::lang::Ref::_deserialize_reference_state(buf);");
            sw.writeln(make_ref(klass)+" this_;");
            sw.write("if (rr.ref != 0) {");
            sw.newline(4); sw.begin(0);
            sw.write("this_ = "+klass+"::"+template+DESERIALIZER_METHOD+chevrons(klass)+"(buf);");
            sw.end(); sw.newline();
            sw.writeln("}");
            sw.write("return x10::lang::Ref::_finalize_reference"+chevrons("__T")+"(this_, rr);");
            sw.end(); sw.newline();
            sw.writeln("}"); sw.forceNewline();
            sw.popCurrentStream();
        }

        // _deserialize_body()
        h.write("public: ");
        h.write("void "+DESERIALIZE_BODY_METHOD+"("+DESERIALIZATION_BUFFER+"& buf);"); h.newline(0);
        printTemplateSignature(ct.typeArguments(), w);
        w.write("void "+klass+"::"+DESERIALIZE_BODY_METHOD+"("+DESERIALIZATION_BUFFER+"& buf) {");
        w.newline(4); w.begin(0);
        if (parent != null && parent.isClass()) {
            w.write(translateType(parent)+"::"+DESERIALIZE_BODY_METHOD+"(buf);");
            w.newline();
        }
        for (int i = 0; i < type.fields().size(); i++) {
            if (i != 0)
                w.newline();
            FieldInstance f = (FieldInstance) type.fields().get(i);
            if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
                continue;
            if (!X10Flags.toX10Flags(f.flags()).isGlobal()) // only serialize global fields of classes
                continue;
            String fieldName = mangled_field_name(f.name().toString());
            w.write(fieldName+" = buf.read"+chevrons(translateType(f.type(),true))+"();");
        }
        w.end(); w.newline();
        w.write("}");
        w.newline();
        w.forceNewline();
    }

    void generateStructSerializationMethods(ClassType type, StreamWrapper sw) {
        X10ClassType ct = (X10ClassType) type.toClass();
        X10TypeSystem ts = (X10TypeSystem) type.typeSystem();
        X10CPPContext_c context = (X10CPPContext_c) tr.context();
        ClassifiedStream w = sw.body();
        ClassifiedStream sh = context.structHeader;
        String klass = translateType(type);
 
        sh.forceNewline();
        
        // _serialize()
        sh.write("static void "+SERIALIZE_METHOD+"("+klass+" this_, "+SERIALIZATION_BUFFER+"& buf);");
        sh.newline(0); sh.forceNewline();

        printTemplateSignature(ct.typeArguments(), w);
        w.write("void "+klass+"::"+SERIALIZE_METHOD+"("+klass+" this_, "+SERIALIZATION_BUFFER+"& buf) {");
        w.newline(4); w.begin(0);
        Type parent = type.superClass();
        if (parent != null) {
            w.write(translateType(parent)+"::"+SERIALIZE_METHOD+"(this_, buf);");
            w.newline();
        }
        for (int i = 0; i < type.fields().size(); i++) {
            if (i != 0)
                w.newline();
            FieldInstance f = (FieldInstance) type.fields().get(i);
            if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
                continue;
            String fieldName = mangled_field_name(f.name().toString());
            w.write("buf.write(this_->"+fieldName+");"); w.newline();
        }
        w.end(); w.newline();
        w.write("}");
        w.newline(); w.forceNewline();

        // _deserialize()
        sh.write("static "+klass+" "+DESERIALIZE_METHOD+"("+DESERIALIZATION_BUFFER+"& buf) {");
        sh.newline(4) ; sh.begin(0);
        sh.writeln(klass+" this_;");
        sh.write("this_->"+DESERIALIZE_BODY_METHOD+"(buf);"); sh.newline();
        sh.write("return this_;");
        sh.end(); sh.newline();
        sh.write("}"); sh.newline(); sh.forceNewline();

        // _deserialize_body()
        sh.write("void "+DESERIALIZE_BODY_METHOD+"("+DESERIALIZATION_BUFFER+"& buf);"); sh.newline(0);
        printTemplateSignature(ct.typeArguments(), w);
        w.write("void "+klass+"::"+DESERIALIZE_BODY_METHOD+"("+DESERIALIZATION_BUFFER+"& buf) {");
        w.newline(4); w.begin(0);
        if (parent != null) {
            w.write(translateType(parent)+"::"+DESERIALIZE_BODY_METHOD+"(buf);");
            w.newline();
        }
        for (int i = 0; i < type.fields().size(); i++) {
            if (i != 0)
                w.newline();
            FieldInstance f = (FieldInstance) type.fields().get(i);
            if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
                continue;
            String fieldName = mangled_field_name(f.name().toString());
            w.write(fieldName+" = buf.read"+chevrons(translateType(f.type(),true))+"();");
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
        h.write("namespace "+mangle_to_cpp(name.name().toString())+" { ");
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

	public static void dumpString(String str, CodeWriter w) {
	    int pos = 0;
	    int nl = 0;
	    while ((nl = str.indexOf('\n', pos)) != -1) {
	        w.write(str.substring(pos, nl));
	        w.newline(0);
	        pos = nl+1;
	    }
	    w.write(str.substring(pos));
	}

	private static String dumpRegex(String id, Object[] components, String regex) {
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
            tr.print(null, n, w);
        } else if (o instanceof Type) {
            w.write(translateType((Type)o, true));
        } else if (o != null) {
            w.write(o.toString());
        }
    }
}
// vim:tabstop=4:shiftwidth=4:expandtab
