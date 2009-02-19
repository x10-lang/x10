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
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Stmt_c;
import polyglot.ast.Stmt;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.While_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.RectRegionMaker_c;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ConstructorDecl_c;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10Special_c;
import polyglot.ext.x10.types.ConstrainedType_c;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.ParameterType_c;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.types.ClassType;
import polyglot.types.ClassType_c;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.types.VarInstance;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.Translator;
import static polyglot.ext.x10cpp.visit.SharedVarsMethods.*;
import static polyglot.ext.x10cpp.visit.ASTQuery.*;
import polyglot.ext.x10cpp.visit.*;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;
import x10c.util.WriterStreams;

public class Emitter {

	private final StreamWrapper sw;
	//private final WriterStreams ws;
	private final Translator tr;
	private ASTQuery query;
	public Emitter(StreamWrapper sw, Translator tr) {
		this.sw=sw;
		//	this.ws=sw.ws; 
		this.tr=tr;
		query = new ASTQuery(sw, tr);
	}
	public static String mangled_method_name(String str) {
		return str.replace("$","__"); // FIXME: Add appropriate mangling. [Krishna]
	}
	public static String mangled_non_method_name(String str) {
		if (str.equals(THIS)) return str;  // "this" is passed by closures code for graceful fitting with 1.5 closures code.  
		// "this" mangles to "this". FIXME Import Igor's changes
        return str.replace("$","__");
		//return ("x10__" + str.replace("$","__"));
	}

	void emit_cond_global_finish_start(String cs, String comment, ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		// FIXME: [IP] These assertions fail for me on AWE.
//		assert(context.inplace0 == false && context.finish_depth == 0);
		w.write(cs + " = x10::finish_start(" + cs + ");" + comment);
	}

	void emit_global_finish_start(ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		// FIXME: [IP] These assertions fail for me on AWE.
//		assert(context.inplace0 == false && context.finish_depth == 0);
		w.write("x10::finish_start(-1);");
	}

	void emit_global_finish_end(ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		// FIXME: [IP] These assertions fail for me on AWE.
//		assert(context.inplace0 == false && context.finish_depth == 0);
		w.write("x10::finish_end(EXCEPTION);");
	}

	void emit_cond_global_finish_end(String comment,ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		// FIXME: [IP] These assertions fail for me on AWE.
//		assert(context.inplace0 == false && context.finish_depth == 0);
		w.write("x10::finish_end(EXCEPTION);" + comment);
	}

	void emit_general_finish_start(ClassifiedStream w) {
		w.write("x10::lang::general_finish_start();");  
		// keep it in sync with data/*.xcd files
		w.newline();
	}

	void emit_general_finish_end(ClassifiedStream w) {
		w.write("x10::lang::general_finish_end();");
		// keep it in sync with data/*.xcd files
		// FIXME: Handle Exceptions. [Krishna]
		w.newline();
	}

	void printStaticAsyncDeclarations(X10CPPContext_c context, ClassifiedStream w) {
		printAsyncDeclarations("static ", "", context.closures.asyncs,
				context.closures.asyncsParameters, context.closures.arrayCopyClosures,
				context.closures.asyncContainers, w);
	}

	void printFriendAsyncDeclarations(X10CPPContext_c context, ClassifiedStream h) {
		printAsyncDeclarations("friend ", translateType(getOuterClass(context))+"::",
				context.closures.asyncs,
				context.closures.asyncsParameters, context.closures.arrayCopyClosures,
				context.closures.asyncContainers, h);
	}

	void printAsyncDeclarations(String prefix, String name_pfx, 
			ArrayList asyncs, ArrayList asyncsParameters, 
			HashMap arrayCopyClosures, HashMap asyncContainers, ClassifiedStream w) {
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

	void printStaticClosureDeclarations(X10CPPContext_c context, ClassifiedStream h) {
		printClosureDeclarations("static ", "",
				context.closures.arrayInitializers, context.closures.arrayInitializerParameters, h);
	}
	/**
	 * Same as n.printBlock(child, w, tr); but without the enterScope call.
	 */
	void printBlock(Node n, Expr child, ClassifiedStream w, Translator tr) {
		w.begin(0);
		sw.pushCurrentStream(w);
		child.del().translate(sw, tr);
		sw.popCurrentStream();
		w.end();
	}
	void printClosureDeclarations(String prefix, String name_pfx, ArrayList closures, 
			ArrayList closuresParameters, ClassifiedStream w) {
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
	void printType(Type type, ClassifiedStream w) {
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
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		type = xts.expandMacros(type);
		if (xts.isRail(type) || xts.isValRail(type) || type.isArray()) {
		//if (type.isArray()) {
			String base;
			if (type.isArray()) {
				base = translateType(type.toArray().base(), true);
			} else {
				Type T = X10TypeMixin.getParameterType((X10Type) type, 0);
				if (T == null) {
				    base="x10::lang::Object";
				}
				else {
				    base=translateType(T, asRef);
				}
			}
			String name = "x10::array<"+base+(base.endsWith(">")?" ":"")+">";
			if (!arraysAsRefs || !asRef)
				return name;
			return make_ref(name);
		}
		Context context = tr.context();
		if (type.isVoid()) {
			return "void";
		}
		if ((type.isPrimitive() || type.isNumeric() || type.isBoolean())&& !type.isClass() && !type.isVoid())
			return "x10_"+type.translate(context);
		// FIXME: is ignoring nullable correct?
//		if (((X10TypeSystem) type.typeSystem()).isNullable(type))
//			return translateType(((X10Type) type).toNullable().base(), asRef);
		// TODO: handle closures
//		if (((X10TypeSystem) type.typeSystem()).isClosure(type))
//			return translateType(((X10Type) type).toClosure().base(), asRef);
		String name = null;
		boolean nativeTranslated = false;
		if (type.isClass()){
			if (type.toClass().isAnonymous())
				name = "__anonymous__"+getId();
			else {
				if (type instanceof ConstrainedType_c)
					type = ((ConstrainedType_c)type).baseType().get();
				X10ClassDef cd = ((X10ClassType) type).x10Def();
				String pat = null;
				//if (type.isBoolean() || type.isNumeric())
				//	pat = getCppBoxRep(cd, tr);
				//else
					pat = getCppRep(cd, tr);
				if (pat != null){ 
					List<Type> typeArguments = ((X10ClassType) type).typeArguments();
					Object[] o = new Object[typeArguments.size()+1];
					int i = 0;
					NodeFactory nf = tr.nodeFactory();
					o[i++] = type;
					for (Type a : typeArguments) {
					    o[i++] = a;
					}
					String pi = translate_mangled_NSFQN(pat);
					if (!pi.contains("#")){
					    X10CPPContext_c c = (X10CPPContext_c) tr.context();
					    c.pendingImplicitImports.add(pi);
					    c.pendingImplicitImports.add(translate_mangled_FQN(pat));
					}
					// FIXME: Clean up this code!
					return dumpRegex("NativeRep", o, tr, pat);
				}
				else{
					if (type.toClass().isNested())
						context = tr.typeSystem().createContext();
					name = type.translate(context);
				}
			}
			X10ClassType ct = (X10ClassType)type.toClass();
			if (!knownSpecialPackages.contains(ct.package_())) {

				if (!nativeTranslated && ct.typeArguments().size() != 0) {

					name += " < ";
					int s = ct.typeArguments().size();
					for (Type t: ct.typeArguments()) {
						name +=translateType(t, asRef);
						s--;
						if (s > 0)
							name +=", ";
					}
					name +=" > ";
				}

				//name = "x10__" + name;
				name=name.replaceAll("\\.", ".x10__");
				//name=name.replaceAll("\\::", "::x10__");
			}
			// FIXME: [IP] KLUDGE! KLUDGE! KLUDGE!
			if (query.isX10Array(type)) {
				String base = translateType(query.getX10ArrayElementType(type), true);
				name = "x10::x10array<"+base+(base.endsWith(">")?" ":"")+">";
				if (!arraysAsRefs || !asRef)
					return name;
				return make_ref(name);
			}
		} else if (type instanceof ParameterType){
			name = type.toString(); 
		} else 
			assert false; // unhandled type.
		name = translateFQN(name);
		if (!asRef)
			return name;
		return make_ref(name);
	}
	void printGlobalStateStruct(X10CPPContext_c c, ArrayList globals, 
			WriterStreams ws, ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		ClassifiedStream h;
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);
		h.forceNewline();
		h.write("public: ");
		h.write("static struct _"+GLOBAL_STATE+" {"); h.newline(0);
		h.begin(4); h.newline();
		printDeclarationList(h, c, globals);
		h.end(); h.newline();
		h.write("} "+GLOBAL_STATE+";");
		h.newline();
		String klass = translateType(c.currentClass());
		w.write("struct "+klass+"::_"+GLOBAL_STATE+" "+klass+"::"+GLOBAL_STATE+";");
		w.newline();
		w.forceNewline();
	}
	void printArgumentList(ClassifiedStream w, X10CPPContext_c c) {
		printArgumentList(w, c, false, true);
	}
	
	void printArgumentList(ClassifiedStream w, X10CPPContext_c c, boolean omitType) {
		printArgumentList(w, c, omitType, true);
	}
	
	void printArgumentList(ClassifiedStream w, X10CPPContext_c c, boolean omitType, boolean saved_this_mechanism) {
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
	void printAsyncsRegistration(ClassType currentClass, ArrayList asyncs, ClassifiedStream w) {
		assert w.sClass==WriterStreams.StreamClass.Closures;
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



	void printFriendClosureDeclarations(X10CPPContext_c context, ClassifiedStream h) {
		printClosureDeclarations("friend ", translateType(getOuterClass(context))+"::",
				context.closures.arrayInitializers, context.closures.arrayInitializerParameters, h);
	}


	void printAsyncFlush(int last_async, int numAsyncs, ClassifiedStream w) {
		for (int i = last_async; i < numAsyncs; i++) {
			w.newline();
			w.write("async_flush(" + i + ");");
		}
		w.newline();
	}

	void printTemplateSignature(List<TypeParamNode> list, ClassifiedStream h) {
		int size = list.size();
		if (size != 0){
			h.write("template <class ");
			for (TypeParamNode p: list) {
				h.write(p.name().id().toString());
				size --;
				if (size > 0)
					h.write(", class ");
			}

			h.write("> ");
		}

	}
	void printTemplateInstantiation(X10MethodInstance mi, ClassifiedStream w) {

		if (mi.typeParameters().size() == 0)
			return;
		w.write("<");
		for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
		    final Type at = i.next();
		    w.write(translateType(at));
		    if (i.hasNext()) {
		        w.write(",");
		        w.allowBreak(0, " ");
		    }
		}
		w.write(">");
	}
	void printHeader(MethodDecl_c n, ClassifiedStream h, Translator tr, boolean qualify) {
		Flags flags = n.flags().flags();

		if (!qualify) {
			printFlags(h, flags);
		}
		h.begin(0);

		if (qualify){
			X10CPPContext_c context = (X10CPPContext_c) tr.context();
			printTemplateSignature(context.classTypeParams, h);
		}

		X10MethodDecl_c xn = (X10MethodDecl_c) n;
		printTemplateSignature(xn.typeParameters(), h);

		if (!qualify) {
			if (flags.isStatic())
				h.write(flags.retain(Flags.STATIC).translate());
			else if (xn.typeParameters().size() == 0)
				h.write("virtual ");
		}
		printType(n.returnType().type(), h);
		h.allowBreak(2, 2, " ", 1);
		if (qualify)
			h.write(translateType(n.methodDef().asInstance().container()) + "::"); 
		//n.print(n.id(), h, tr);
		h.write(mangled_method_name(n.name().id().toString())); 
		h.write("(");
		h.allowBreak(2, 2, "", 0);
		h.begin(0);
		for (Iterator i = n.formals().iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			sw.pushCurrentStream(h);
			n.print(f, sw, tr);
			sw.popCurrentStream();
			if (i.hasNext()) {
				h.write(",");
				h.allowBreak(0, " ");
			}
		}
		h.end();
		h.write(")");
		h.end();
		if (!qualify) {
			if (n.body() == null && !n.flags().flags().isNative())
				h.write(" = 0");
			// [IP] I don't like this.  There has to be a better way.
			if (h.sClass == WriterStreams.StreamClass.Header){
				h.write(";");
				h.newline();
			}
		}
	}

	void printHeader(Formal_c n, ClassifiedStream h, Translator tr, boolean qualify) {
		Flags flags = n.flags().flags();
		h.begin(0);
//		if (flags.isFinal())
//		h.write("const ");
		printType(n.type().type(), h);
		h.write(" ");
		h.write(mangled_non_method_name(n.name().id().toString()));
		h.end();
	}

	void printFlags(ClassifiedStream h, Flags flags) {
//		if (flags.isPublic() || flags.isProtected() || flags.isPrivate())
//		h.write(flags.retain(Flags.PUBLIC.Private().Protected()).translate()+": ");
//		else // Java's "package access" should be the same as public
//		h.write(Flags.PUBLIC.translate()+": ");
		// [IP] HACK: with inlining, everything is public
		h.write("/"+"*"+flags.retain(Flags.PUBLIC.Private().Protected()).translate()+"*"+"/ ");
		h.write(Flags.PUBLIC.translate()+": ");
	}

	void printHeader(ClassDecl_c n, ClassifiedStream h, Translator tr, boolean qualify) {
		h.begin(0);
		// Handle generics
		// If it involves Parameter Types then generate C++
		// templates.

		X10ClassDecl_c xn = (X10ClassDecl_c) n;
		printTemplateSignature(xn.typeParameters(), h);

		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		for (TypeParamNode p: xn.typeParameters()) {
			context.classTypeParams.add(p);
		}

		h.write("class ");
		assert(!n.classDef().isLocal());
		if (n.classDef().isNested() && !n.classDef().isLocal()) // FIXME: handle local classes
			h.write(translateType(n.classDef().outer().get().asType()) + "::");
		h.write(mangled_non_method_name(n.name().id().toString())); 

		boolean hasSuper = false;
		if (n.superClass() != null) {
			h.allowBreak(0);
			if (!hasSuper) {
				h.write(":");
				hasSuper = true;
			} else {
				h.write(",");
			}
			h.allowBreak(0, " ");
			h.write("public ");
			sw.pushCurrentStream(h);
			n.print(n.superClass(), sw, tr);
			sw.popCurrentStream();
		}

		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		// FIXME: HACK! [IP] Ignore the ValueType tag interface
		if (!n.interfaces().isEmpty()
				&& (!ts.isValueType((Type)n.classDef().asType()) || n.interfaces().size() > 1))
		{
			h.allowBreak(2);
			h.begin(0);
			if (!hasSuper) {
				h.write(":");
				hasSuper = true;
			} else {
				h.write(",");
			}
			h.allowBreak(0, " ");
			for (Iterator i = n.interfaces().iterator(); i.hasNext(); ) {
				TypeNode tn = (TypeNode) i.next();
				// FIXME: HACK! [IP] Ignore the ValueType tag interface
				if (tn.type().typeEquals(ts.Value()))
					continue;
				h.write("public ");
				sw.pushCurrentStream(h);
				n.print(tn, sw, tr);
				sw.popCurrentStream();
				if (i.hasNext()) {
					h.write(",");
					h.allowBreak(0, " ");
				}
			}
			h.end();
		}
		h.unifiedBreak(0);
		h.end();
	}

	void printHeader(ConstructorDecl_c n, ClassifiedStream h, Translator tr, boolean qualify) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		Flags flags = n.flags().flags();

		if (qualify){
			printTemplateSignature(context.classTypeParams, h);
		}

		X10ConstructorDecl_c xn = (X10ConstructorDecl_c) n;
		printTemplateSignature(xn.typeParameters(), h);

		if (!qualify) {
			printFlags(h, flags);
		}
		h.begin(0);
		if (qualify && !context.inLocalClass())
			h.write(translateType(n.constructorDef().asInstance().container()) + "::");
		h.write(mangled_non_method_name(n.name().id().toString())); 
		h.write("(");
		h.allowBreak(2, 2, "", 0);
		h.begin(0);
		for (Iterator i = n.formals().iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			sw.pushCurrentStream(h);
			n.print(f, sw, tr);
			sw.popCurrentStream();
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
	void printHeader(FieldDecl_c n, ClassifiedStream h, Translator tr, boolean qualify) {
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
		//h.write("__");  // In Java a field and a method can have
		//		// the same name. This takes care of it.
		h.write(mangled_non_method_name(n.name().id().toString())); 
		h.end();

		// TODO: Handle initialization of instance fields.
		// While doing it also handle initializer blocks.
		// [Krishna]
		if (!qualify && flags.isStatic()) {
			h.write(";");
			h.newline();
		}
	}
	void printHeader(LocalDecl_c n, ClassifiedStream h, Translator tr, boolean qualify) {
		Flags flags = n.flags().flags();
		h.begin(0);
		// Let us not generate constants - We will have problem in
		// initializing away from the place where it is declared.
		//if (flags.isFinal())
		//	h.write("const ");
		if (tr.printType()) {
			printType(n.type().type(), h);
			h.write(" ");
		}
		h.write(mangled_non_method_name(n.name().id().toString())); 
		h.end();
	}
	
	void createPackedArgumentsStruct(ClassifiedStream w, X10CPPContext_c c, int id, String prefix) {
		createPackedArgumentsStruct(w, c, id, prefix, true);
	}
	void createPackedArgumentsStruct(ClassifiedStream w, X10CPPContext_c c, int id, String prefix, boolean saved_this_mechanism) {
		w.write("struct "+args_name(prefix, id)+" : public x10::closure_args {");
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
	public void processAsync (Stmt_c n, String placeVar, Stmt asyncStmt, 
									X10CPPContext_c c, WriterStreams ws, ClassifiedStream w1){
		ClassifiedStream fw = ws.getFirstStream(WriterStreams.StreamClass.Closures);
		boolean insideClosure = c.insideClosure;
		c.setinsideClosure(true);

		X10CPPContext_c.Closures a = c.closures;
		enterAsync(a, n);
		ClassifiedStream w = ws.getNewStream(WriterStreams.StreamClass.Closures);
		// Async argument struct declaration.
		int id = async_id(a, n);

		fw.write("struct "+args_name(ASYNC_PREFIX, id) + ";\n"); 
		fw.newline();

		c.closures.asyncContainers.put(n, c.currentClass());
		// Declare async function
		String className = translateType(c.currentClass());
		w.write("async_closure(" + className + ",");
		w.allowBreak(0, " ");
		w.write(id + ",");
		w.allowBreak(0, " ");
		if (useStructsForAsyncArgs)
			w.write("async_unpacked_body(");
		sw.pushCurrentStream(w);
		n.print(asyncStmt, sw, tr);
		sw.popCurrentStream();
		w.write(",");
		w.allowBreak(0, " ");
		if (useStructsForAsyncArgs) {
			unpackArgs(w, c, c.variables, id, ASYNC_PREFIX);
			w.write(")");
			w.write(",");
			w.allowBreak(0, " ");
			w.write("(" + VOID_PTR + " args)");
		}
		else {
			w.write("(");
			printArgumentList(w, c);
			w.write(")");
		}
		w.write(");");
		w.newline();

		// Async argument struct definition.
		createPackedArgumentsStruct(fw, c, id, ASYNC_PREFIX);
		fw.newline(0);

		w.forceNewline(0);

		w = w1;

		c.setinsideClosure(insideClosure);

		if (query.hasAnnotation(n, "x10.lang.aggregate")) {
			w.write("agg_");
		}

		w.write("async_invocation(" + translateType(c.currentClass()) + ", " + id + ", ");
		w.write("(");
		w.write(placeVar);
		w.write(")");
		//w.write(")->x10__id");
		w.write(", ");
		instantiateArguments(w, c, n.position());
		w.write(");");
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
			WriterStreams ws, ClassifiedStream w)
	{
		String ARRAY_ACCESSOR = useIndicesForArrayCopy ? RAW_ADJUSTED : RAW_ARRAY;
		w.write("async_array_copy_invocation(");
		w.begin(0);
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(src, sw, tr);
		sw.popCurrentStream();
		w.write(")->"+ARRAY_ACCESSOR+"()");
		w.allowBreak(0, " "); w.write("+"); w.allowBreak(0, " ");
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(srcOffset, sw, tr);
		sw.popCurrentStream();
		w.write(")");
		w.write(","); w.allowBreak(0, " ");

		// Represent dest and destOffset as a closure to be evaluated at the target
		boolean inClosure = context.inClosure;
		boolean insideClosure = context.insideClosure;
		context.setinClosure(true);
		context.setinsideClosure(true);

		X10CPPContext_c.Closures a = context.closures;
		boolean outer = outerClosure(a);
		enterAsync(a, n);
		a.arrayCopyClosures.put(n, context.currentClass());
		// ClassifiedStream w = this.w;
		ClassifiedStream oldW = w;
		if (polyglot.ext.x10cpp.Configuration.SPMD_COMPILATION){
			if (outer) w = ws.getNewStream(WriterStreams.StreamClass.Closures);
			else w.newline(0);
		} else {
			w = ws.getNewStream(WriterStreams.StreamClass.Closures);
		}

		// Array copy argument struct declaration.
		int id = async_id(a, n);

		// Declare array copy function
		String className = translateType(context.currentClass());
		w.write("array_copy_closure_and_args_struct(" + className + ",");
		w.allowBreak(0, " ");
		w.write(id + ",");
		w.allowBreak(0, " ");
		w.write(VOID_PTR+",");
		w.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs)
			w.write("async_unpacked_body(");
		w.write("{ ");
		w.write("return (");
		printBlock(n, dest, w, tr);
		w.write(")->"+ARRAY_ACCESSOR+"()");
		w.allowBreak(0, " "); w.write("+"); w.allowBreak(0, " ");
		w.write("(");
		printBlock(n, destOffset, w, tr);
		w.write("); },");
		w.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs) {
			unpackArgs(w, context, context.variables, id, ARRAY_COPY_PREFIX);
			w.write(")");
			w.write(",");
			w.allowBreak(0, " ");
			w.write("(" + VOID_PTR + " args)");
		}
		else {
			w.write("(");
			printArgumentList(w, context);
			w.write(")");
		}
		w.write(",");
		w.allowBreak(0, " ");
		// Array copy argument struct definition.
		createPackedArgumentsStruct(w, context, id, ARRAY_COPY_PREFIX);
		w.newline();
		w.write(");");
		w.newline(0);

		w.write("struct "+closure_name(CLOSURE_WRAPPER_PREFIX, id)+" : public x10lib::Closure {");
		w.newline(4); w.begin(0);
		w.write(closure_name(CLOSURE_WRAPPER_PREFIX, id));
		w.write("(");
		printArgumentList(w, context);
		w.write(") :");
		w.allowBreak(0, " ");
		w.write("x10lib::Closure(sizeof(" + args_name(ARRAY_COPY_PREFIX, id) + "),");
		w.allowBreak(0, " ");
		w.write(id + "),");
		w.allowBreak(0, " ");
		w.write("args(");
		printArgumentList(w, context, true);  // print args with no types
		w.write(") { }");
		w.newline();
		if (context.variables.size() > 0) {
			w.write(closure_name(CLOSURE_WRAPPER_PREFIX, id) + "() { }"); w.newline();
		}
		w.write(args_name(ARRAY_COPY_PREFIX, id) + " args;");
		w.end(); w.newline();
		w.write("};");
		w.newline(0);

		w.forceNewline(0);

		if (polyglot.ext.x10cpp.Configuration.SPMD_COMPILATION){
			if (outer) w = oldW;
		} else {
			w = oldW;
		}

		context.setinClosure(inClosure);
		context.setinsideClosure(insideClosure);

		w.write("array_copy_closure_invocation(");
		w.begin(0);
		w.write(id + ","); w.allowBreak(0, " ");
		instantiateArguments(w, context, n.position());
		w.end();
		w.write(")");
		a.asyncsParameters.set(id, context.variables);

		context.finalizeClosureInstance();
		exitClosure(a);

		w.write(","); w.allowBreak(0, " ");
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(len, sw, tr);
		sw.popCurrentStream();
		w.write(")*sizeof(" + translateType(baseType, true) + ")");
		w.write(","); w.allowBreak(0, " ");
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(target, sw, tr);
		sw.popCurrentStream();
		w.write(")->x10__id");
		// FIXME: handle clocks
//		if (clock != null) {
//		w.write(","); w.allowBreak(0, " ");
//		n.printBlock(clock, w, tr);
//		}
		w.end(); w.write(");");
	}

	/**
	 * TODO: consolidate with
	 * {@link #printAsyncArrayCopyInvocation(Async_c, X10CPPContext_c, Type, Expr,
	 *                                       Expr, Expr, Expr, Expr, Expr)}.
	 */
	void printAsyncArrayCopyInvocation(Call_c n, X10CPPContext_c context, Type baseType,
			Expr target, Expr src, Expr srcOffset, Expr dest, Expr destOffset, Expr len,
			Expr array, boolean shouldNotify, WriterStreams ws, ClassifiedStream w)
	{
		String ARRAY_ACCESSOR = useIndicesForArrayCopy ? RAW_ADJUSTED : RAW_ARRAY;
		assert (eagerArrayCopyNotification); // TODO
		//w.write("x10lib::AsyncArrayCopyRawNotify("); // TODO
		w.write("async_array_copy_invocation(");
		w.begin(0);
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(src, sw, tr);
		sw.popCurrentStream();
		w.write(")->"+ARRAY_ACCESSOR+"()");
		w.allowBreak(0, " "); w.write("+"); w.allowBreak(0, " ");
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(srcOffset, sw, tr);
		sw.popCurrentStream();
		w.write(")");
		w.write(","); w.allowBreak(0, " ");

		// Represent dest and destOffset as a closure to be evaluated at the target
		boolean inClosure = context.inClosure;
		boolean insideClosure = context.insideClosure;
		context.setinClosure(true);
		context.setinsideClosure(true);

		X10CPPContext_c.Closures a = context.closures;
		boolean outer = outerClosure(a);
		enterAsync(a, n);
		a.arrayCopyClosures.put(n, context.currentClass());
		ClassifiedStream oldW = w;
		if (polyglot.ext.x10cpp.Configuration.SPMD_COMPILATION){
			if (outer) w = ws.getNewStream(WriterStreams.StreamClass.Closures);
			else w.newline(0);
		} else {
			w = ws.getNewStream(WriterStreams.StreamClass.Closures);
		}

		// Array copy argument struct declaration.
		int id = async_id(a, n);

		// Declare array copy function
		String className = translateType(context.currentClass());
		w.write("array_copy_closure_and_args_struct(" + className + ",");
		w.allowBreak(0, " ");
		w.write(id + ",");
		w.allowBreak(0, " ");
		w.write(VOID_PTR+",");
		w.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs)
			w.write("async_unpacked_body(");
		w.write("{ ");
		String destoff_id = getId();
		w.write(translateType(destOffset.type(), true)+" "+destoff_id+" = ");
		printBlock(n, destOffset, w, tr);
		w.write(";"); w.allowBreak(0, " ");
		if (eagerArrayCopyNotification && shouldNotify) {
			w.write("(");
			sw.pushCurrentStream(w);
			n.printBlock(array, sw, tr);
			sw.popCurrentStream();
			w.write(")->"+POST_COPY_RUN+"("+destoff_id+");");
			w.allowBreak(0, " ");
		}
		w.write("return (");
		printBlock(n, dest, w, tr);
		w.write(")->"+ARRAY_ACCESSOR+"()");
		w.allowBreak(0, " "); w.write("+"); w.allowBreak(0, " ");
		w.write("(");
		w.write(destoff_id);
		w.write("); },");
		w.allowBreak(0, " ");
		if (useStructsForArrayCopyArgs) {
			unpackArgs(w, context, context.variables, id, ARRAY_COPY_PREFIX);
			w.write(")");
			w.write(",");
			w.allowBreak(0, " ");
			w.write("(" + VOID_PTR + " args)");
		}
		else {
			w.write("(");
			printArgumentList(w, context);
			w.write(")");
		}
		w.write(",");
		w.allowBreak(0, " ");
		// Array copy argument struct definition.
		createPackedArgumentsStruct(w, context, id, ARRAY_COPY_PREFIX);
		w.newline();
		w.write(");");
		w.newline(0);

		w.write("struct "+closure_name(CLOSURE_WRAPPER_PREFIX, id)+" : public x10lib::Closure {");
		w.newline(4); w.begin(0);
		w.write(closure_name(CLOSURE_WRAPPER_PREFIX, id));
		w.write("(");
		printArgumentList(w, context);
		w.write(") :");
		w.allowBreak(0, " ");
		w.write("x10lib::Closure(sizeof(" + args_name(ARRAY_COPY_PREFIX, id) + "),");
		w.allowBreak(0, " ");
		w.write(id + "),");
		w.allowBreak(0, " ");
		w.write("args(");
		printArgumentList(w, context, true);  // print args with no types
		w.write(") { }");
		w.newline();
		if (context.variables.size() > 0) {
			w.write(closure_name(CLOSURE_WRAPPER_PREFIX, id) + "() { }"); w.newline();
		}
		w.write(args_name(ARRAY_COPY_PREFIX, id) + " args;");
		w.end(); w.newline();
		w.write("};");
		w.newline(0);

		w.forceNewline(0);


		if (polyglot.ext.x10cpp.Configuration.SPMD_COMPILATION){
			if (outer) w = oldW;
		} else {
			w = oldW;
		}

		context.setinClosure(inClosure);
		context.setinsideClosure(insideClosure);

		w.write("array_copy_closure_invocation(");
		w.begin(0);
		w.write(id + ","); w.allowBreak(0, " ");
		instantiateArguments(w, context, n.position());
		w.end();
		w.write(")");
		a.asyncsParameters.set(id, context.variables);

		context.finalizeClosureInstance();
		exitClosure(a);

		w.write(","); w.allowBreak(0, " ");
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(len, sw, tr);
		sw.popCurrentStream();
		w.write(")*sizeof(" + translateType(baseType, true) + ")");
		w.write(","); w.allowBreak(0, " ");
		w.write("(");
		sw.pushCurrentStream(w);
		n.printBlock(target, sw, tr);
		sw.popCurrentStream();
		w.write(")->x10__id");
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

		w.end(); w.write(");");
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

        X10CPPContext_c context = (X10CPPContext_c) tr.context();
        printTemplateSignature(context.classTypeParams, w);

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

    void printExplicitTarget(Call_c n, Receiver target, X10CPPContext_c context, ClassifiedStream w) {
		if (target instanceof X10Special_c &&
				((X10Special_c)target).kind().equals(X10Special_c.THIS) &&
				(context.inlining || context.insideClosure))
		{
			w.write(SAVED_THIS);
			if (context.insideClosure)
				context.saveEnvVariableInfo(THIS);
		}
		else if (target instanceof Expr) {
			boolean assoc = !(target instanceof New_c || target instanceof RectRegionMaker_c);
			sw.pushCurrentStream(w);
			n.printSubExpr((Expr) target, assoc, sw, tr);
			sw.popCurrentStream();
		}
		else if (target != null) {
			sw.pushCurrentStream(w);
			n.print(target, sw, tr);
			sw.popCurrentStream();
		}

		return;
	}
	
	void printDeclarationList(ClassifiedStream w, X10CPPContext_c c, ArrayList vars) {
		printDeclarationList(w, c, vars, true);
	}
	
	void printDeclarationList(ClassifiedStream w, X10CPPContext_c c, ArrayList vars, boolean saved_this_mechanism) {
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

	void unpackArgs(ClassifiedStream w, X10CPPContext_c c, ArrayList vars, int id, String prefix) {
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
	
	void unpackClosureArgs(ClassifiedStream w, X10CPPContext_c c, ArrayList vars, int id, String prefix) {
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
	
	void instantiateArguments(ClassifiedStream w, X10CPPContext_c c, Position p) {
		instantiateArguments(w, c, p, true);
	}
	
	void instantiateArguments(ClassifiedStream w, X10CPPContext_c c, Position p, boolean saved_this_mechanism) {
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
	
	void generateSerializationMethods(ClassType type, ClassifiedStream w, ClassifiedStream h) {
		// FIXME: Has a lot of string constants. Refactor them
		// into final variables.
		// -Krishna.
		X10TypeSystem ts = (X10TypeSystem) type.typeSystem();
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		assert(!context.inLocalClass()); // Do not ask for header stream in LocalClass
		//ClassifiedStream h = ws.getCurStream(WriterStreams.StreamClass.Header);
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
		//h.write("template<> friend struct x10aux::_reference_serializer<"+"x10__"+type.name()+">;");
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
			String fieldName = mangled_non_method_name(f.name().toString());
			if (f.type().isPrimitive()) {
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
		w.write("void "+klass+"::"+DESERIALIZE_FIELDS_METHOD+"("+SERIALIZATION_BUFFER+"& buf) {");
		w.newline(4); w.begin(0);
		for (int i = 0; i < type.fields().size(); i++) {
			if (i != 0)
				w.newline();
			FieldInstance f = (FieldInstance) type.fields().get(i);
			if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
				continue;
			String fieldName = mangled_non_method_name(f.name().toString());
			if (f.type().isPrimitive()) {
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
	void handleX10Cast(X10Cast_c c, String castVar, ClassifiedStream w) {

		if (c==null) return;

		X10CPPContext_c context = (X10CPPContext_c) tr.context();
	        TypeNode tn = c.castType();

	        assert tn instanceof CanonicalTypeNode;

	        switch (c.conversionType()) {
	        case COERCION:
	        case PRIMITIVE:
	        case TRUNCATION:
	            if (tn instanceof X10CanonicalTypeNode) {
	                X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;

	                Type t = X10TypeMixin.baseType(xtn.type());
	                DepParameterExpr dep = xtn.constraintExpr();

			if (dep != null) {
				// FIXME: handle RTT
				context.setSelf(castVar);
				w.write("if (! ");
				sw.pushCurrentStream(w);
				c.printSubExpr(dep.condition(), true, sw, tr);
				sw.popCurrentStream();
				context.resetSelf();
				w.write(")");
			} else if (t.isBoolean() || t.isNumeric() || c.expr().type().isSubtype(t)) {
			    w.write("if (" + castVar + "== NULL)");
	                } else {
				// FIXME: RTT
				w.write("if (false)");
			}
			w.newline(2);
			w.begin(0);
			w.write("throw (x10aux::ref<x10::lang::ClassCastException>) new (x10aux::alloc<x10::lang::ClassCastException>()) x10::lang::ClassCastException() ; " );
			w.end();
			w.newline();
		}
	        	break;

	        case BOXING:
	        case UNBOXING:
			// FIXME: Handle boxing and unboxing. Need generics?
			assert (false);
			break;
		case UNKNOWN_CONVERSION:
	            throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
	        case CALL:
	            throw new InternalCompilerError("Conversion call should have been rewritten.", c.position());
		}
	}
	public void emitUniqueNS(String name, ArrayList<String> history, ClassifiedStream w) {
		if (name == null) return;
		if  (!history.contains(name)){
			w.write("using namespace "+name+";");
			history.add(name);
		}
		return;
	}
	public void emitUniqueUS(String header, ArrayList<String> history, ClassifiedStream w) {
		// emit unique using statement
		if  (!history.contains(header)){
			w.write("using \"" + header + "\";");
			history.add(header);
		}
		return;
	}
	public void emitUniqueIF(String header, ArrayList<String> history, ClassifiedStream w) {
		if  (!history.contains(header)){
			w.write("#include <" + header + ">");
			history.add(header);
		}
		return;
	}
	 public String makeUnsignedType(Type t) {
	 		 // FIXME: HACK!
	 		 if (t.isInt())
	 		 		 return "uint32_t";
	 		 if (t.isLong())
	 		 		 return "uint64_t";
	 		 return "unsigned "+translateType(t);
	 }
	Node enterSPMD(Node n, ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		boolean mainMethod = query.isMainMethod(context);
		boolean toplevel0 = mainMethod && context.finish_depth == 0;
		boolean toplevel1 = mainMethod && context.finish_depth == 1;
		boolean toplevel2 = mainMethod && context.ateach_depth == 0;
		// Add isMainMethod() to toplevel2 as well -> we do not emit
		// SKIP statements in non-Main methods.

		if (n == null && mainMethod ||
				((n instanceof Finish_c) && toplevel0) ||
				//((n instanceof Eval_c) && (toplevel0 || toplevel1)) ||
				((n instanceof Call_c) && mainMethod) ||
				((n instanceof AtEach_c) && toplevel1) ||
				((n instanceof LocalDecl_c) && toplevel0) ||
				((n instanceof If_c) && (toplevel0/* || context.ateach_depth == 0*/)) ||
				((n instanceof For_c) && (toplevel0 || toplevel2)) ||
				((n instanceof Do_c) && (toplevel0 || toplevel2)) ||
				((n instanceof While_c) && (toplevel0 || toplevel2)) ||
				((n instanceof ForLoop_c) && (toplevel0 || toplevel2)) ||
				((n instanceof Switch_c) && (toplevel0/* || context.ateach_depth == 0*/)) ||
				((n instanceof SwitchBlock_c) && (toplevel0/* || context.ateach_depth == 0*/)) ||
				((n instanceof Block_c) && mainMethod && toplevel2) ||
				((n instanceof Next_c) && (toplevel0)) ||
				((n instanceof Try_c) && (toplevel0 || toplevel2)))
		{
			if (context.inplace0) {
				w.newline(0);
				w.write("SKIP_s" + context.nextLabel + ": ;");
				w.newline(0);
				context.inplace0 = false;
			}
		}
		return null;
	}
	Node leaveSPMD(Node n, ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		boolean mainMethod = query.isMainMethod(context);
		boolean toplevel0 = mainMethod && context.finish_depth == 0;
		boolean toplevel1 = mainMethod && context.finish_depth == 1;
		boolean toplevel2 = mainMethod && context.ateach_depth == 0;
		// Add isMainMethod to toplevel2 as well -> we do not emit
		// SKIP statements in non-Main methods.
		if (n == null && mainMethod ||
				((n instanceof Finish_c) && toplevel0) ||
				//((n instanceof Eval_c) && (toplevel0 || toplevel1)) ||
				((n instanceof Call_c) && mainMethod) ||
				((n instanceof AtEach_c) && toplevel1) ||
				((n instanceof LocalDecl_c) && toplevel0) ||
				((n instanceof If_c) && (toplevel0)) ||
				((n instanceof For_c) && (toplevel0 || toplevel2)) ||
				((n instanceof While_c) && (toplevel0 || toplevel2)) ||
				((n instanceof Do_c) && (toplevel0 || toplevel2)) ||
				((n instanceof ForLoop_c) && (toplevel0 || toplevel2)) ||
				((n instanceof Switch_c) && (toplevel0/* || context.ateach_depth == 0*/)) ||
				((n instanceof SwitchBlock_c) && (toplevel0/* || context.ateach_depth == 0*/)) ||
				((n instanceof Block_c) && mainMethod && toplevel2) ||
				((n instanceof Next_c) && (toplevel0)) ||
				((n instanceof Try_c) && (toplevel0 || toplevel2)))
		{
			if (!context.inplace0) {
				context.nextLabel++;
				w.newline(0);
				w.write("if (__here__ != 0) goto SKIP_s"+context.nextLabel+";");
				w.newline(0);
				context.inplace0 = true;
			}
		}
		return null;
	}
	public static void closeNameSpace(String ns, ClassifiedStream w) {
		int start = 0;
		while (true) {
			start = ns.indexOf('.', start);
			if (start < 0) break;
			w.write("}");
			start ++;
		}
		w.write("} // namespace ");
		w.write(translate_mangled_FQN(ns));
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
	    			throw new InternalCompilerError("Template '"+id+"' uses #"+idx);
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
	public void dumpRegex(String id, Object[] components, Translator tr, String regex, ClassifiedStream w) {
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
	    		if (idx.intValue() >= components.length){
	    			throw new InternalCompilerError("Template '"+id+"' uses #"+idx);
			}
	    		prettyPrint(components[idx.intValue()], tr, w);
	    	}
	    	pos++;
	    }
	    w.write(regex.substring(start) /*translateFQN(regex.substring(start))*/);
	}
	private void prettyPrint(Object o, Translator tr, ClassifiedStream w) {
		if (o instanceof Node) {
			sw.pushCurrentStream(w);
			((Node) o).del().translate(sw, tr);
			sw.popCurrentStream();
		} else if (o instanceof Type) {
			w.write(translateType((Type)o));
		} else if (o != null) {
			w.write(o.toString());
		}
	}
	public String getStaticFieldName(X10ClassDef cd, String fldName) {
		return translate_mangled_FQN(cd.fullName().toString()) + "<void>::" + fldName;
	}
}
