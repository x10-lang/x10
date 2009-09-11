/*
 *
 * (C) Copyright IBM Corporation 2006, 2007, 2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 7, 2004
 */
package x10cpp.visit;

import static x10cpp.visit.ASTQuery.assertNumberOfInitializers;
import static x10cpp.visit.ASTQuery.getConstructorId;
import static x10cpp.visit.ASTQuery.getCppRep;
import static x10cpp.visit.ASTQuery.getPropertyInit;
import static x10cpp.visit.Emitter.mangled_field_name;
import static x10cpp.visit.Emitter.mangled_method_name;
import static x10cpp.visit.Emitter.mangled_non_method_name;
import static x10cpp.visit.Emitter.toTypeList;
import static x10cpp.visit.Emitter.translateFQN;
import static x10cpp.visit.Emitter.translate_mangled_FQN;
import static x10cpp.visit.Emitter.voidTemplateInstantiation;
import static x10cpp.visit.SharedVarsMethods.CONSTRUCTOR;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZATION_BUFFER;
import static x10cpp.visit.SharedVarsMethods.DESERIALIZE_METHOD;
import static x10cpp.visit.SharedVarsMethods.INSTANCE_INIT;
import static x10cpp.visit.SharedVarsMethods.MAKE;
import static x10cpp.visit.SharedVarsMethods.CPP_NATIVE_STRING;
import static x10cpp.visit.SharedVarsMethods.SAVED_THIS;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_BUFFER;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_ID_FIELD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZATION_MARKER;
import static x10cpp.visit.SharedVarsMethods.SERIALIZE_BODY_METHOD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZE_ID_METHOD;
import static x10cpp.visit.SharedVarsMethods.SERIALIZE_METHOD;
import static x10cpp.visit.SharedVarsMethods.STATIC_INIT;
import static x10cpp.visit.SharedVarsMethods.STRUCT_EQUALS;
import static x10cpp.visit.SharedVarsMethods.STRUCT_EQUALS_METHOD;
import static x10cpp.visit.SharedVarsMethods.THIS;
import static x10cpp.visit.SharedVarsMethods.VOID;
import static x10cpp.visit.SharedVarsMethods.VOID_PTR;
import static x10cpp.visit.SharedVarsMethods.chevrons;
import static x10cpp.visit.SharedVarsMethods.getId;
import static x10cpp.visit.SharedVarsMethods.getUniqueId_;
import static x10cpp.visit.SharedVarsMethods.make_ref;
import static x10cpp.visit.SharedVarsMethods.refsAsPointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import polyglot.ast.AmbReceiver;
import polyglot.ast.ArrayAccess_c;
import polyglot.ast.ArrayInit_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Branch_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Case_c;
import polyglot.ast.Catch;
import polyglot.ast.Catch_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.ClassBody_c;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassMember;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
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
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.Loop_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
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
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.util.TypedList;
import polyglot.visit.Translator;
import x10.Configuration;
import x10.ast.AssignPropertyBody_c;
import x10.ast.Async_c;
import x10.ast.AtEach_c;
import x10.ast.AtExpr_c;
import x10.ast.AtStmt_c;
import x10.ast.Atomic_c;
import x10.ast.Await_c;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.ConstantDistMaker_c;
import x10.ast.Finish_c;
import x10.ast.ForEach_c;
import x10.ast.ForLoop_c;
import x10.ast.Future_c;
import x10.ast.Here_c;
import x10.ast.LocalTypeDef_c;
import x10.ast.Next_c;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl;
import x10.ast.PropertyDecl_c;
import x10.ast.RegionMaker_c;
import x10.ast.SettableAssign_c;
import x10.ast.StmtSeq_c;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.When_c;
import x10.ast.X10Binary_c;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Cast;
import x10.ast.X10Cast_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10Formal;
import x10.ast.X10Instanceof_c;
import x10.ast.X10IntLit_c;
import x10.ast.X10Local_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.ast.X10Special_c;
import x10.ast.X10Unary_c;
import x10.types.ClosureDef;
import x10.types.ClosureInstance;
import x10.types.ParameterType;
import x10.types.ParameterType_c;
import x10.types.X10ArraysMixin;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10Def;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.visit.StaticNestedClassRemover;
import x10.visit.X10DelegatingVisitor;
import x10.util.ClassifiedStream;
import x10.util.StreamWrapper;
import x10cpp.extension.X10ClassBodyExt_c;
import x10cpp.types.X10CPPContext_c;
import x10cpp.visit.X10CPPTranslator.DelegateTargetFactory;

/**
 * Visitor on the AST nodes that for some X10 nodes triggers the template
 * based dumping mechanism (and for all others just defaults to the normal
 * pretty printing).
 *
 * A new code generator to generate message passing style code. [Krishna]
 *
 * @author Igor Peshansky
 * @author V. Krishna Nandivada
 * @author Pradeep Varma
 * @author vj
 * @author Dave Cunningham
 */

/* Design:
 * Each place (excluding the place 0) starts of by running an async that
 * runs the "loop_main" that waits for any events from place 0.
 * Currently loop_main is an infinite loop, that does nothing, but waits for
 * the termination signal.
 * Place 0, runs the program main and sends the required communication
 * across, whenever it requires.
 *
 * All the final variables that are visible in the async are send as
 * arguments.
 *
 * [TODO]
 * The code that is common to multiple files will goto the ".inc" file
 * [Krishna]
 */
public class MessagePassingCodeGenerator extends X10DelegatingVisitor {

	protected final StreamWrapper sw;
	protected final Translator tr;
	protected XCDProcessor xcdProcessor;

	protected Emitter emitter;
	protected ASTQuery query;
	public MessagePassingCodeGenerator(StreamWrapper sw, Translator tr) {
		this.sw = sw;
		this.tr = tr;
		this.emitter = new Emitter(tr);
		this.xcdProcessor = new XCDProcessor(sw, tr);
		this.query = new ASTQuery(tr);
	}

	public void visit(Node n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
				"Unhandled node type: "+n.getClass(), n.position());
		//n.translate(w, tr);
	}



    public void visit(TypeDecl_c n) {
        // do nothing
        sw.write(" /* " + n + " *" + "/ ");
        sw.newline();
    }

    public void visit(LocalTypeDef_c n) {
        // do nothing
        sw.write(" /* " + n + " *" + "/ ");
        sw.newline();
    }

	public void visit(X10ClassDecl_c n) {
		processClass(n);
	}

	/**
	 * Returns an all-void instantiation of a given class type ct.
	 */
	public static X10ClassType getStaticMemberContainer(X10ClassType ct) {
	    if (ct.typeArguments().size() == 0)
	        return ct;
	    X10TypeSystem_c xts = (X10TypeSystem_c) ct.typeSystem();
	    List<Type> args = new TypedList(new ArrayList<Type>(), Type.class, false);
	    for (int i = 0; i < ct.typeArguments().size(); i++) {
	        args.add(xts.Void());
	    }
	    return ct.typeArguments(args);
	}

	/**
	 * Is the given expression constant and not a native field.
	 */
	private boolean isConstant(Expr e) {
	    // [IP] HACK for XTENLANG-486.  The only way a native expression would be marked a constant
	    // is if it's a field access.
	    String pat = e instanceof Field_c ? getCppImplForDef((X10FieldDef) ((Field_c) e).fieldInstance().def()) : null;
	    return e.isConstant() && pat == null;
	}

	private boolean isGlobalInit(FieldDecl_c fd) {
	    // [DC] want these to occur in the static initialiser
	    // [IP] except for the ones that use a literal init - otherwise switch is broken
	    return (fd.init() != null &&
	            fd.flags().flags().isStatic() && fd.flags().flags().isFinal() &&
	            isConstant(fd.init()) &&
	            (fd.init().type().isNumeric() || fd.init().type().isBoolean() ||
	             fd.init().type().isChar() || fd.init().type().isNull()));
	}

	// FIXME: Consolidate with extractInits()
	private boolean extractGenericStaticDecls(X10ClassDef cd, ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (cd.typeParameters().size() == 0)
			return false;
		w.write("template <> class ");
		w.write(mangled_non_method_name(cd.name().toString()));
		w.write(voidTemplateInstantiation(cd.typeParameters().size()));
		if (cd.superType() != null) {
		    w.write(" : public ");
		    X10ClassDef sdef = ((X10ClassType) cd.superType().get()).x10Def();
		    X10ClassType stype = (X10ClassType) sdef.asType();
		    w.write(Emitter.translateType(getStaticMemberContainer(stype), false));
		}
		w.allowBreak(0, " ");
		w.write("{");
		w.newline(4); w.begin(0);
        w.write("public:"); w.newline();
        w.write("static x10aux::RuntimeType rtt;"); w.newline();
        w.write("static const x10aux::RuntimeType* getRTT() { return & rtt; }"); w.newline();
		// First process all classes
		for (ClassMember dec : context.pendingStaticDecls()) {
			if (dec instanceof ClassDecl_c) {
                assert false : "nested class alert! "+cd.position();
				ClassDecl_c cdecl = (ClassDecl_c) dec;
				((X10CPPTranslator)tr).setContext(cdecl.enterScope(context)); // FIXME
				X10ClassDef def = (X10ClassDef) cdecl.classDef();
				if (getCppRep(def) != null) {
					// emit no c++ code as this is a native rep class
					continue;
				}
				emitter.printTemplateSignature(((X10ClassType)def.asType()).typeArguments(), w);
				w.write("class ");
				w.write(Emitter.mangled_non_method_name(cdecl.name().id().toString()));
				w.write(";");
				((X10CPPTranslator)tr).setContext(context); // FIXME
				w.newline();
			}
		}
		// Then process all fields and methods
		for (ClassMember dec : context.pendingStaticDecls()) {
			if (dec instanceof FieldDecl_c) {
				FieldDecl_c fd = (FieldDecl_c) dec;
				((X10CPPTranslator)tr).setContext(fd.enterScope(context)); // FIXME
				sw.pushCurrentStream(w);
				emitter.printHeader(fd, w, tr, false);
				sw.popCurrentStream();
				w.write(";");
				((X10CPPTranslator)tr).setContext(context); // FIXME
			} else if (dec instanceof MethodDecl_c) {
				MethodDecl_c md = (MethodDecl_c) dec;
				((X10CPPTranslator)tr).setContext(md.enterScope(context)); // FIXME
				sw.pushCurrentStream(w);
				emitter.printHeader(md, sw, tr, false);
				sw.popCurrentStream();
				w.write(";");
				((X10CPPTranslator)tr).setContext(context); // FIXME
			}
			w.newline(); w.forceNewline();
		}
		w.write("static " + VOID + " " + STATIC_INIT + "();");
		w.newline(); w.forceNewline();
		w.end(); w.newline();
		w.write("};");
		w.newline();
		return true;
	}

	// FIXME: Consolidate with extractInits()
	private void extractGenericStaticInits(X10ClassDef cd) {
	    // Always write non-template static decls into the implementation file
	    ClassifiedStream save_w = sw.currentStream();
	    ClassifiedStream w = sw.getNewStream(StreamWrapper.CC, false);
	    sw.pushCurrentStream(w);
	    String header = getHeader(cd.asType());
	    w.write("#include <"+header+">"); w.newline();
	    w.forceNewline(0);

	    X10CPPContext_c context = (X10CPPContext_c) tr.context();
	    ArrayList<FieldDecl_c> inits = new ArrayList<FieldDecl_c>();
	    String container = translate_mangled_FQN(cd.fullName().toString())+voidTemplateInstantiation(cd.typeParameters().size());
	    sw.write("x10aux::RuntimeType "+container+"::rtt;");
	    for (ClassMember dec : context.pendingStaticDecls()) {
	        if (dec instanceof FieldDecl_c) {
	            FieldDecl_c fd = (FieldDecl_c) dec;
	            ((X10CPPTranslator)tr).setContext(fd.enterScope(context)); // FIXME
	            emitter.printType(fd.type().type(), sw);
	            sw.allowBreak(2, " ");
	            sw.write(container+"::");
	            sw.write(mangled_field_name(fd.name().id().toString()));
	            // [DC] want these to occur in the static initialiser instead
	            // [IP] except for the ones that use a literal init - otherwise switch is broken
	            if (isGlobalInit(fd)) {
	                sw.write(" =");
	                sw.allowBreak(2, " ");
	                fd.print(fd.init(), sw, tr);
	            } else if (fd.init() != null) {
	                inits.add(fd);
	            }
	            sw.write(";");
	            sw.newline();
	            ((X10CPPTranslator)tr).setContext(context); // FIXME
	        } else if (dec instanceof MethodDecl_c) {
	            MethodDecl_c md = (MethodDecl_c) dec;
	            X10MethodDef def = (X10MethodDef)md.methodDef();
	            boolean templateMethod = def.typeParameters().size() != 0;
	            if (templateMethod)
	                sw.pushCurrentStream(save_w);
	            ((X10CPPTranslator)tr).setContext(md.enterScope(context)); // FIXME
	            if (query.isMainMethod(md))
	                processMain((X10ClassType) cd.asType());
	            emitter.printTemplateSignature(toTypeList(def.typeParameters()), sw);
	            emitter.printType(md.returnType().type(), sw);
	            sw.allowBreak(2, " ");
	            sw.write(container+"::");
	            sw.write(mangled_method_name(md.name().id().toString()) + "(");
	            sw.begin(0);
	            boolean first = true;
	            for (Formal f : md.formals()) {
	                if (first) {
	                    first = false;
	                } else {
	                    sw.write(",");
	                    sw.allowBreak(0, " ");
	                }
	                md.print(f, sw, tr);
	            }
	            sw.end();
	            sw.write(")");
	            if (md.body() != null) {
	                sw.allowBreak(0, " ");
	                md.printBlock(md.body(), sw, tr);
	            }
	            sw.newline();
	            ((X10CPPTranslator)tr).setContext(context); // FIXME
	            if (templateMethod)
	                sw.popCurrentStream();
	        } else if (dec instanceof X10ClassDecl_c) {
	            assert (false) : ("Nested class alert!");

	        }
	    }
	    sw.write(VOID + " " + container + "::" + STATIC_INIT + "() {");
	    sw.newline(4); sw.begin(0);
	    sw.write("static bool done = false;"); sw.newline();
	    sw.write("if (done) return;"); sw.newline();
	    sw.write("done = true;"); sw.newline();
	    sw.write("_I_(\"Doing static initialisation for class: "+container+"\");"); sw.newline();
	    if (cd.superType() != null) {
	        X10ClassDef superDef = ((X10ClassType) X10TypeMixin.baseType(cd.superType().get())).x10Def();
	        String superContainer = translate_mangled_FQN(superDef.fullName().toString())+voidTemplateInstantiation(superDef.typeParameters().size());
	        sw.write(superContainer + "::" + STATIC_INIT + "();");
	        sw.newline();
	    }
	    for (FieldDecl_c fd : inits) {
	        assert (fd.init() != null);
	        sw.write(mangled_field_name(fd.name().id().toString())+" =");
	        sw.allowBreak(2, " ");
	        fd.print(fd.init(), sw, tr);
	        sw.write(";");
	        sw.newline();
	    }
	    //w.write("return NULL;");
	    sw.end(); sw.newline();
	    sw.write("}"); sw.newline();
	    sw.write("static " + VOID_PTR + " __init__"+getUniqueId_() +" __attribute__((__unused__)) = x10aux::InitDispatcher::addInitializer(" + container + "::" + STATIC_INIT + ")"+ ";");
	    sw.newline(); sw.forceNewline(0);

	    sw.popCurrentStream();
	}

	private boolean extractInits(X10ClassType currentClass, String methodName,
			String retType, List members, boolean staticInits)
	{
	    String className = emitter.translateType(currentClass);
	    boolean sawInit = false;
	    emitter.printTemplateSignature(currentClass.typeArguments(), sw);
	    sw.write(retType + " " + className + "::" + methodName + "() {");
	    sw.newline(4); sw.begin(0);
	    if (staticInits) {
	        sw.write("static bool done = false;"); sw.newline();
	        sw.write("if (done) return;"); sw.newline();
	        sw.write("done = true;"); sw.newline();
	        sw.write("_I_(\"Doing static initialisation for class: "+className+"\");"); sw.newline();
	        if (currentClass.superClass() != null) {
	            X10ClassDef superDef = ((X10ClassType) X10TypeMixin.baseType(currentClass.superClass())).x10Def();
	            String superContainer = translate_mangled_FQN(superDef.fullName().toString())+voidTemplateInstantiation(superDef.typeParameters().size());
	            sw.write(superContainer + "::" + methodName + "();");
	            sw.newline();
	        }
	    } else {
	        sw.write("_I_(\"Doing initialisation for class: "+className+"\");"); sw.newline();
	    }
	    for (Iterator i = members.iterator(); i.hasNext(); ) {
	        ClassMember member = (ClassMember) i.next();
	        if (!(member instanceof Initializer_c) && !(member instanceof FieldDecl_c))
	            continue;
	        if (member.memberDef().flags().isStatic() != staticInits)
	            continue;
	        if (member instanceof FieldDecl_c &&
	                (((FieldDecl_c)member).init() == null ||
	                        query.isSyntheticField(((FieldDecl_c)member).name().id().toString())))
	            continue;
	        if (member instanceof FieldDecl_c) {
	            FieldDecl_c dec = (FieldDecl_c) member;
	            if (dec.flags().flags().isStatic()) {
	                X10ClassType container = (X10ClassType)dec.fieldDef().asInstance().container();
	                if (((X10ClassDef)container.def()).typeParameters().size() != 0)
	                    continue;
	                if (isGlobalInit(dec))
	                    continue;
	            }
	        }
	        sawInit = true;
	        if (member instanceof Initializer_c) {
	            Initializer_c init = (Initializer_c) member;
	            init.printBlock(init.body(), sw, tr);
	            sw.newline(0);
	        } else if (member instanceof FieldDecl_c) {
	            FieldDecl_c dec = (FieldDecl_c) member;
	            X10CPPContext_c context = (X10CPPContext_c) tr.context();
	            ((X10CPPTranslator)tr).setContext(dec.enterScope(context)); // FIXME
	            X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
	            if (!staticInits) {
	                VarInstance ti = xts.localDef(Position.COMPILER_GENERATED, Flags.FINAL,
	                        Types.ref(currentClass), Name.make(THIS)).asInstance();
	                context.addVariable(ti);
	            }
	            Expr init = (Expr) dec.init();
	            assert (init != null);
	            sw.write(mangled_field_name(dec.name().id().toString()));
	            sw.write(" = ");
	            Type aType = dec.type().type();
	            boolean rhsNeedsCast = !xts.typeDeepBaseEquals(aType, init.type(), context);
	            if (rhsNeedsCast) {
	                // FIXME: this cast would not be needed if not for a frontend bug
	                sw.write("x10aux::class_cast<");
	                emitter.printType(aType, sw);
	                sw.write(" >(");
	            }
	            dec.print(init, sw, tr);
	            if (rhsNeedsCast)
	                sw.write(")");
	            sw.write(";");
	            sw.newline();
	            ((X10CPPTranslator)tr).setContext(context); // FIXME
	        }
	    }
	    if (!retType.equals(VOID))
	        sw.write("return ("+retType+")0;");
	    sw.end(); sw.newline();
	    sw.write("}");
	    sw.newline(); sw.forceNewline(0);
	    return sawInit;
	}

	private boolean hasExternMethods(List members) {
		for (Iterator i = members.iterator(); i.hasNext(); ) {
			ClassMember member = (ClassMember) i.next();
			if (member instanceof MethodDecl_c) {
				MethodDecl_c init = (MethodDecl_c) member;
				if (X10Flags.isExtern(init.flags().flags()))
					return true;
			}
		}
		return false;
	}

	private void extractAllClassTypes(Type t, List<ClassType> types, Set<ClassType> dupes) {
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        t = xts.expandMacros(t);
		if (!t.isClass())
			return;
		X10ClassType ct = (X10ClassType) t.toClass();
        if (!dupes.contains(ct)) {
            dupes.add(ct);
            types.add(ct);
        }
		for (Type pt : ct.typeArguments())
			extractAllClassTypes(pt, types, dupes);
		if (ct.isNested())
			extractAllClassTypes(ct.outer(), types, dupes);
	}

    private void declareClass(X10ClassDef cd, ClassifiedStream h) {
        assert (cd != null);
        QName pkg = null;
        if (cd.package_() != null)
            pkg = cd.package_().get().fullName();
        if (pkg != null) {
            Emitter.openNamespaces(h, pkg);
            h.newline(0);
        }
        emitter.printTemplateSignature((List<Type>)(List)cd.typeParameters(), h);
        String name = StaticNestedClassRemover.mangleName(cd).toString();
        h.write("class "+Emitter.mangled_non_method_name(name)+";");
        h.newline();
        if (pkg != null) {
            h.newline(0);
            Emitter.closeNamespaces(h, pkg);
            h.newline(0);
        }
    }

    private String getHeader(ClassType ct) {
        DelegateTargetFactory tf = ((X10CPPTranslator) tr).targetFactory();
        String pkg = "";
        if (ct.package_() != null)
            pkg = ct.package_().fullName().toString();
        Name name = StaticNestedClassRemover.mangleName(ct.def());
        String header = tf.outputHeaderName(pkg, name.toString());
        return header;
    }

    private String getHeaderGuard(String header) {
        return header.replace('/','_').replace('.','_').replace('$','_').toUpperCase();
    }

    void processClass(X10ClassDecl_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10ClassDef def = (X10ClassDef) n.classDef();

		if (getCppRep(def) != null) {
			// emit no c++ code as this is a native rep class
			return;
		}

        assert (!def.isNested()) : ("Nested class alert!");

        ClassifiedStream save_body = sw.body();
        ClassifiedStream save_header = sw.header();
        ClassifiedStream save_generic = context.templateFunctions;
        // Header stream
        ClassifiedStream h = sw.getNewStream(StreamWrapper.Header, false);
        // Stream for generic functions (always in the header, may be empty)
        ClassifiedStream g = sw.getNewStream(StreamWrapper.Header, false);
        context.templateFunctions = g;
        String impl = StreamWrapper.CC;
        if (def.typeParameters().size() != 0)
            impl = StreamWrapper.Header;
        // Implementation stream (may be after the header)
        ClassifiedStream w = sw.getNewStream(impl, false);
        // Dependences guard closing stream (comes at the end of the header)
        ClassifiedStream z = sw.getNewStream(StreamWrapper.Header, false);
        sw.set(h, w);

        context.setInsideClosure(false);
        context.hasInits = false;

        // Write the header for the class
        DelegateTargetFactory tf = ((X10CPPTranslator) tr).targetFactory();
        String cheader = getHeader(def.asType());
        String cguard = getHeaderGuard(cheader);
        h.write("#ifndef __"+cguard); h.newline();
        h.write("#define __"+cguard); h.newline();
        h.forceNewline(0);
        h.write("#include <x10rt17.h>"); h.newline();
        h.forceNewline(0);

        g.write("#ifndef "+cguard+"_GENERICS"); g.newline();
        g.write("#define "+cguard+"_GENERICS"); g.newline();

        boolean inTemplate = def.typeParameters().size() != 0;
        if (inTemplate) {
            w.write("#ifndef "+cguard+"_IMPLEMENTATION"); w.newline();
            w.write("#define "+cguard+"_IMPLEMENTATION"); w.newline();
        }

        w.write("#include <"+cheader+">"); w.newline();

        w.forceNewline(0);
        w.forceNewline(0);

        String pkg = "";
        if (context.package_() != null)
            pkg = context.package_().fullName().toString();
        String incfile = tf.integratedOutputName(pkg, n.name().toString(), StreamWrapper.Closures);
        w.write("#include \""+incfile+"\""); w.newline();
        w.forceNewline(0);

		if (hasExternMethods(n.body().members())) {
			w.write("#include <" + X10ClassBodyExt_c.wrapperFileName(def.asType().toReference()) + ">");
			w.newline();
		}

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		ArrayList<Type> allIncludes = new ArrayList<Type>();
		if (n.superClass() != null) {
		    ClassType ct = n.superClass().type().toClass();
		    X10ClassDef scd = (X10ClassDef) ct.def();
		    if (scd != def) {
		        String header = getHeader(ct);
		        String guard = getHeaderGuard(header);
		        h.write("#define "+guard+"_NODEPS"); h.newline();
		        h.write("#include <" + header + ">");
		        h.newline();
		        h.write("#undef "+guard+"_NODEPS"); h.newline();
		        allIncludes.add(ct);
		    }
		    ArrayList<ClassType> types = new ArrayList<ClassType>();
		    Set<ClassType> dupes = new HashSet<ClassType>();
		    dupes.add(ct);
		    extractAllClassTypes(ct, types, dupes);
		    for (ClassType t : types) {
		        X10ClassDef cd = ((X10ClassType)t).x10Def();
		        if (cd != def && getCppRep(cd) == null) {
		            declareClass(cd, h);
		            allIncludes.add(t);
		        }
		    }
		}
		for (TypeNode i : n.interfaces()) {
		    ClassType ct = i.type().toClass();
		    X10ClassDef icd = (X10ClassDef) ct.def();
		    if (icd != def) {
		        String header = getHeader(ct);
		        String guard = getHeaderGuard(header);
		        h.write("#define "+guard+"_NODEPS"); h.newline();
		        h.write("#include <" + header + ">");
		        h.newline();
		        h.write("#undef "+guard+"_NODEPS"); h.newline();
		        allIncludes.add(ct);
		    }
		    ArrayList<ClassType> types = new ArrayList<ClassType>();
		    Set<ClassType> dupes = new HashSet<ClassType>();
		    dupes.add(ct);
		    extractAllClassTypes(ct, types, dupes);
		    for (ClassType t : types) {
		        X10ClassDef cd = ((X10ClassType)t).x10Def();
		        if (cd != def && getCppRep(cd) == null) {
		            declareClass(cd, h);
		            allIncludes.add(t);
		        }
		    }
		}

		// FIXME: [IP] There is a problem with include ordering.
		// We cannot just blindly include a header for every type used
		// because of recursive dependences.  So we need to do partial
		// ordering.  For that, we need to include only those headers
		// that define classes for which the code needs a full definition.
		// Otherwise, just declare a class and include the header in the
		// implementation file instead.
		// As I can see, the only uses that need a full definition are
		// field reads (in static final field initializers) and
		// inheritance.  So find all class declarations and field
		// declarations, and do #include for those headers.
		//
		// [DC] static final field initialisers should be in the cc file,
		// with everything else that needs a full definition (lookups,
		// construction, etc)
		//
		// [DC] generic classes might cause a problem though, as their
		// function bodies are in the header.  We can still get cycles
		// through this approach.  We may need two layers of headers or
		// something for generic classes, in a manner that reflects the
		// (h,cc) pairing for non-generic classes.

		// TODO: sort by namespace and combine things in the same namespace
		X10SearchVisitor xTypes = new X10SearchVisitor(X10CanonicalTypeNode_c.class, Closure_c.class);
		n.visit(xTypes);
		ArrayList<ClassType> types = new ArrayList<ClassType>();
		Set<ClassType> dupes = new HashSet<ClassType>();
		dupes.add(def.asType());
		if (xTypes.found()) {
		    ArrayList typeNodesAndClosures = xTypes.getMatches();
		    for (int i = 0; i < typeNodesAndClosures.size(); i++) {
		        Node tn = (Node) typeNodesAndClosures.get(i);
		        if (tn instanceof X10CanonicalTypeNode_c) {
		            X10CanonicalTypeNode_c t = (X10CanonicalTypeNode_c) tn;
		            extractAllClassTypes(t.type(), types, dupes);
		        } else if (tn instanceof Closure_c) {
                    Closure_c t = (Closure_c) tn;
		            ClassType c = t.type().toClass();
		            assert (c.interfaces().size() == 1);
                    extractAllClassTypes(c.interfaces().get(0), types, dupes);
		        }
		    }
        }
        X10ClassType superClass = (X10ClassType) X10TypeMixin.baseType(def.asType().superClass());
        if (superClass != null) {
            for (Name mname : getMethodNames(n.body().members())) {
                List<MethodInstance> overriddenOverloads = getOROLMeths(mname, superClass);
                for (MethodInstance mi : overriddenOverloads) {
                    extractAllClassTypes(mi.returnType(), types, dupes);
                    for (Type t : mi.formalTypes())
                        extractAllClassTypes(t, types, dupes);
                    for (Type t : mi.throwTypes())
                        extractAllClassTypes(t, types, dupes);
                }
            }
        }

        for (ClassType ct : types) {
            X10ClassDef cd = (X10ClassDef) ct.def();
            if (cd == def)
                continue;
            if (!allIncludes.contains(ct)) {
                declareClass(cd, h);
                allIncludes.add(ct);
            }
        }

		if (def.package_() != null) {
		    QName pkgName = def.package_().get().fullName();
		    Emitter.openNamespaces(h, pkgName);
		    h.newline(0);
		    h.forceNewline(0);
		}

        /* [DC] since we don't have nested classes anymore, processClass is never called from
         * inside processClass, so this stack device is no-longer needed.
		ArrayList<ClassMember> opsd = context.pendingStaticDecls;
        assert opsd.isEmpty() : "Should not occur without nested classes";
         */

        /*
         * Ideally, classProperties would be added to the child context
         * that will be created for processing the classBody, but there
         * is no way to do that.  So instead we add and remove the properties
         * in the global context, for each class.
         */
		context.resetStateForClass(n.properties());

		if (def.typeParameters().size() != 0) {
			// Pre-declare the void specialization for statics
			emitter.printTemplateSignature(((X10ClassType)def.asType()).typeArguments(), h);
			h.write("class ");
			h.write(mangled_non_method_name(def.name().toString()));
			h.write(";");
			h.newline();
			h.write("template <> class ");
			h.write(mangled_non_method_name(def.name().toString()));
			h.write(voidTemplateInstantiation(def.typeParameters().size()));
			h.write(";");
			h.newline();
		}

		emitter.printHeader(n, h, tr, false);
		h.allowBreak(0, " ");

		n.print(n.body(), sw, tr);

        ((X10CPPTranslator)tr).setContext(n.enterChildScope(n.body(), context)); // FIXME
        /*
         * TODO: [IP] Add comment about dependences between the method calls.
         */

        if (extractGenericStaticDecls(def, h)) {
            extractGenericStaticInits(def);
        }

        /*
         * See comment about resetStateForClass() above.
         */
        context.clearStateForClass();

        ((X10CPPTranslator)tr).setContext(context); // FIXME

		// [DC] disabled, see (commented out) definition of opsd above for details
        // context.pendingStaticDecls = opsd;

		h.newline();

        // Write the footer for the class
		if (def.package_() != null) {
		    QName pkgName = def.package_().get().fullName();
		    h.newline(0);
		    h.forceNewline(0);
		    Emitter.closeNamespaces(h, pkgName);
		    h.newline(0);
		}
		h.write("#endif // " + cguard); h.newline(0);
        h.forceNewline(0);

        g.write("#endif // "+cguard+"_GENERICS"); g.newline();

        if (inTemplate) {
            w.write("#endif // "+cguard+"_IMPLEMENTATION"); w.newline();
        }

        // The declarations below are intentionally outside of the guard
        if (context.package_() != null) {
            QName qn = context.package_().fullName();
            Emitter.openNamespaces(h, qn);
            h.newline(0);
        }
        emitter.printTemplateSignature((List<Type>)(List)def.typeParameters(), h);
        h.write("class "+Emitter.mangled_non_method_name(n.name().toString())+";");
        h.newline(0);
        if (context.package_() != null) {
            h.newline(0);
            QName qn = context.package_().fullName();
            Emitter.closeNamespaces(h, qn);
            h.newline(0);
        }
        h.forceNewline(0);

        // [IP] Ok to include here, since the class is already defined
        h.write("#ifndef "+cguard+"_NODEPS"); h.newline();
        h.write("#define "+cguard+"_NODEPS"); h.newline();

        for (Type t : allIncludes) {
            ClassType ct = t.toClass();
            String header = getHeader(ct);
            h.write("#include <" + header + ">");
            h.newline();
        }

        z.write("#endif // __"+cguard+"_NODEPS"); h.newline();
        z.forceNewline(0);

        context.templateFunctions = save_generic;
        sw.set(save_header, save_body);
	}



	public void visit(LocalClassDecl_c n) {
		assert (false) : ("Local classes should have been removed by a separate pass");
	}

    // Get the list of methods of name "name" that ought to be accessible from class c
    // due to being locally defined or inherited
    List<MethodInstance> getOROLMeths(Name name, X10ClassType c) {
        return getOROLMeths(name, c, new HashSet<List<Type>>());
    }

    List<MethodInstance> getOROLMeths(Name name, X10ClassType c, HashSet<List<Type>> shadowed) {
        assert (name != null);
        assert (c != null);
        assert (shadowed != null);

        List<MethodInstance> meths = new ArrayList<MethodInstance>();

        List<MethodInstance> cmeths = c.methodsNamed(name);

        for (MethodInstance cmi : cmeths) {
            if (cmi.flags().isAbstract()) continue;
            if (shadowed.contains(cmi.formalTypes())) continue;
            shadowed.add(cmi.formalTypes());
            if (cmi.flags().isPrivate()) continue;
            meths.add(cmi);
        }

        // no need to look in interfaces because they only contain abstract methods
        X10ClassType superClass = (X10ClassType) X10TypeMixin.baseType(c.superClass());
        if (superClass!=null) {
            List<MethodInstance> moreMeths = getOROLMeths(name, superClass, shadowed);
            meths.addAll(moreMeths);
        }

        return meths;
    }


    private Type replaceType(Type type, HashMap<Type, Type> typeMap) {
        Type mapped = typeMap.get(type);
        if (mapped != null)
            return mapped;
        if (!type.isClass())
            return type;
        X10ClassType ct = (X10ClassType) type.toClass();
        if (ct.typeArguments().size() == 0)
            return ct;
        ArrayList<Type> newArgs = new ArrayList<Type>();
        boolean dirty = false;
        for (Type t : ct.typeArguments()) {
            Type n = replaceType(t, typeMap);
            if (n != t) dirty = true;
            newArgs.add(n);
        }
        if (dirty)
            ct = ct.typeArguments(newArgs);
        return ct;
    }

    private ArrayList<Name> getMethodNames(List<ClassMember> members) {
        ArrayList<Name> mnames = new ArrayList<Name>();
        Set<Name> dupes = new HashSet<Name>();
        for (ClassMember member : members) {
            if (!(member instanceof X10MethodDecl)) continue;
            X10MethodDecl mdcl = (X10MethodDecl) member;
            Name mname = mdcl.name().id();
            if (dupes.contains(mname)) continue;
            MethodDef md = mdcl.methodDef();
            MethodInstance mi = md.asInstance();
            if (mi.flags().isStatic()) continue;
            dupes.add(mname);
            mnames.add(mname);
        }
        return mnames;
    }

    public void visit(ClassBody_c n) {
        X10CPPContext_c context = (X10CPPContext_c) tr.context();
        X10ClassType currentClass = (X10ClassType) context.currentClass();
        X10ClassType superClass = (X10ClassType) X10TypeMixin.baseType(currentClass.superClass());
        X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();

        if (currentClass.flags().isInterface()) {
            visitInterfaceBody(n, context, currentClass, superClass, xts);
        } else if (xts.isValueType(currentClass, context) || currentClass.isX10Struct()) { // HACK: treating sturcts as values.
            visitValueBody(n, context, currentClass, superClass, xts);
        } else if (currentClass.isX10Struct()) {
            assert false : "unreachable code due to structs as values hack";
            context.setGeneratingSturct(true);
            visitStructBody(n, context, currentClass, superClass, xts);
            context.setGeneratingSturct(false);
        } else {
            visitClassBody(n, context, currentClass, superClass, xts);
        }
    }

    private void visitInterfaceBody(ClassBody_c n, X10CPPContext_c context,
                                X10ClassType currentClass, X10ClassType superClass,
                                X10TypeSystem xts) {
        ClassifiedStream h = sw.header();
        List<ClassMember> members = n.members();
        ITable itable = ITable.getITable(currentClass);

        h.write("{"); h.newline(4); h.begin(0);
        h.write("public:"); h.newline();
        h.write("RTT_H_DECLS_INTERFACE");
        h.newline(); h.forceNewline();
        sw.begin(0);
        for (PropertyDecl p : context.classProperties()) {
            n.print(p, sw, tr);
        }

        h.write("template <class I> struct itable {"); h.newline(4); h.begin(0);
        h.write("itable(");
        boolean firstMethod = true;
        for (MethodInstance meth : itable.getMethods()) {
            if (!firstMethod) {
                h.write(", ");
            }
            firstMethod = false;
            itable.emitFunctionPointerDecl(h, emitter, meth, "I", true);
        }
        h.write(") ");
        firstMethod = true;
        for (MethodInstance meth : itable.getMethods()) {
            if (firstMethod) {
                h.write(": ");
                firstMethod = false;
            } else {
                h.write(", ");
            }
            String name = itable.mangledName(meth);
            h.write(name+"("+name+")");
        }
        h.write(" {}");
        h.newline();

        firstMethod = true;
        for (MethodInstance meth : itable.getMethods()) {
            if (!firstMethod) h.newline();
            firstMethod = false;
            itable.emitFunctionPointerDecl(h, emitter, meth, "I", true);
            h.write(";");
        }
        h.end(); h.newline();
        h.write("};"); h.newline();
        h.forceNewline();

        /* Serialization redirection methods */
        h.write("static void "+SERIALIZE_METHOD+"("); h.begin(0);
        h.write(emitter.translateType(currentClass, true)+" this_,"); h.newline();
        h.write(SERIALIZATION_BUFFER+"& buf,"); h.newline();
        h.write("x10aux::addr_map& m) {"); h.end(); h.newline(4); h.begin(0);
        h.write("x10::lang::Object::"+SERIALIZE_METHOD+"(this_, buf, m);"); h.end(); h.newline();
        h.write("}"); h.newline(); h.forceNewline();

        h.write("public: template<class __T> static ");
        h.write(make_ref("__T")+" "+DESERIALIZE_METHOD+"("+DESERIALIZATION_BUFFER+"& buf) {"); h.newline(4) ; h.begin(0);
        h.write("return x10::lang::Object::"+DESERIALIZE_METHOD+"<__T>(buf);"); h.end(); h.newline();
        h.write("}"); h.newline(); h.forceNewline();

        if (!members.isEmpty()) {
            String className = emitter.translateType(currentClass);

            h.write(VOID + " " + INSTANCE_INIT + "();");
            h.newline(); h.forceNewline();
            if (extractInits(currentClass, INSTANCE_INIT, VOID, members, false)) {
                context.hasInits = true;
            }

            for (ClassMember member : members) {
                if (! (member instanceof polyglot.ast.MethodDecl)){
                    n.printBlock(member, sw, tr);
                }
            }

            generateStaticInitForClass(currentClass, h, members, className);
        }

        h.end();
        sw.end();
        sw.newline();
        h.newline(0);
        h.write("};");
        h.newline();

        emitter.printRTTDefn(currentClass, sw);
    }    

    private void visitClassBody(ClassBody_c n, X10CPPContext_c context,
                                X10ClassType currentClass, X10ClassType superClass,
                                X10TypeSystem xts) {
        ClassifiedStream h = sw.header();

        h.write("{");
        h.newline(4);
        h.begin(0);
        h.write("public:");
        h.newline();
        h.write("RTT_H_DECLS_CLASS");    		
        h.newline(); h.forceNewline();
        sw.begin(0);
        for (PropertyDecl p : context.classProperties()) {
            n.print(p, sw, tr);
        }

        List<ClassMember> members = n.members();

        generateITablesForClass(currentClass, xts, "virtual ", h);

        if (!members.isEmpty()) {
            String className = emitter.translateType(currentClass);

            h.write(VOID + " " + INSTANCE_INIT + "();");
            h.newline();
            h.forceNewline();
            if (extractInits(currentClass, INSTANCE_INIT, VOID, members, false)) {
                context.hasInits = true;
            }

            ClassMember prev = null;
            for (ClassMember member : members) {
                if ((member instanceof polyglot.ast.CodeDecl)
                        || (prev instanceof polyglot.ast.CodeDecl)) {
                    h.newline(0);
                    sw.newline(0);
                }
                prev = member;
                n.printBlock(member, sw, tr);
            }

            if (superClass != null) {
                generateProxiesForOverriddenMethods(context, currentClass,
                                                    superClass, xts, "virtual ", h, members);
            }

            generateStaticInitForClass(currentClass, h, members, className);

            if (xts.isValueType(currentClass, context)) {
                emitter.generateSerializationMethods(currentClass, sw);
            }
        }

        h.end();
        sw.end();
        sw.newline();
        h.newline(0);
        h.write("};");
        h.newline();

        emitter.printRTTDefn(currentClass, sw);
    }
    
    private void visitValueBody(ClassBody_c n, X10CPPContext_c context,
                                X10ClassType currentClass, X10ClassType superClass,
                                X10TypeSystem xts) {
        ClassifiedStream h = sw.header();
        List<ClassMember> members = n.members();

        h.write("{");
        h.newline(4);
        h.begin(0);
        h.write("public:");
        h.newline();
        h.write("RTT_H_DECLS_CLASS");           
        h.newline(); h.forceNewline();
        sw.begin(0);
        for (PropertyDecl p : context.classProperties()) {
            n.print(p, sw, tr);
        }

        generateITablesForClass(currentClass, xts, "virtual ", h);

        if (!members.isEmpty()) {
            String className = emitter.translateType(currentClass);

            h.write(VOID + " " + INSTANCE_INIT + "();");
            h.newline();
            h.forceNewline();
            if (extractInits(currentClass, INSTANCE_INIT, VOID, members, false)) {
                context.hasInits = true;
            }

            ClassMember prev = null;
            for (ClassMember member : members) {
                if ((member instanceof polyglot.ast.CodeDecl)
                        || (prev instanceof polyglot.ast.CodeDecl)) {
                    h.newline(0);
                    sw.newline(0);
                }
                prev = member;
                n.printBlock(member, sw, tr);
            }

            if (superClass != null) {
                generateProxiesForOverriddenMethods(context, currentClass,
                        superClass, xts, "virtual ", h, members);
            }

            // Generate structEquals for values
            h.write("public: ");
            h.write("virtual ");
            emitter.printType(xts.Boolean(), h);
            h.write(" " + mangled_method_name(STRUCT_EQUALS_METHOD) + "(");
            emitter.printType(xts.Object(), h);
            h.write(" p0");
            h.write(");");
            h.newline();

            emitter.printTemplateSignature(currentClass.typeArguments(), sw);
            emitter.printType(xts.Boolean(), sw);
            sw.write(" " + emitter.translateType(currentClass, false)
                     + "::" + mangled_method_name(STRUCT_EQUALS_METHOD)
                     + "(");
            emitter.printType(xts.Object(), sw);
            sw.write(" p0");
            sw.write(") {");
            sw.newline(4);
            sw.begin(0);
            sw.write("if (p0.operator->() == this) return true; // short-circuit trivial equality");
            sw.newline();
            if (superClass != null) { // HACK: treating structs as values.
                sw.write("if (!this->" + emitter.translateType(superClass)
                         + "::" + mangled_method_name(STRUCT_EQUALS_METHOD)
                         + "(p0))");
                sw.newline(4);
                sw.begin(0);
                sw.write("return false;");
                sw.end();
                sw.newline();
            }
            emitter.printType(currentClass, sw);
            sw.write(" that =");
            sw.allowBreak(4, " ");
            sw.write("(");
            sw.begin(0);
            emitter.printType(currentClass, sw);
            sw.end();
            sw.write(") p0;");
            sw.newline();
            for (FieldInstance fi : currentClass.fields()) {
                if (!fi.flags().isStatic()) {
                    String name = fi.name().toString();
                    sw.write("if (!" + STRUCT_EQUALS + "(this->"
                             + mangled_field_name(name) + ", that->"
                             + mangled_field_name(name) + "))");
                    sw.newline(4);
                    sw.begin(0);
                    sw.write("return false;");
                    sw.end();
                    sw.newline();
                }
            }
            sw.write("return true;");
            sw.end();
            sw.newline();
            sw.write("}");
            sw.newline();

            generateStaticInitForClass(currentClass, h, members, className);

            emitter.generateSerializationMethods(currentClass, sw);
        }

        h.end();
        sw.end();
        sw.newline();
        h.newline(0);
        h.write("};");
        h.newline();

        emitter.printRTTDefn(currentClass, sw);
    }
    
    private void visitStructBody(ClassBody_c n, X10CPPContext_c context,
                                X10ClassType currentClass, X10ClassType superClass,
                                X10TypeSystem xts) {
        ClassifiedStream h = sw.header();

        h.write("{");
        h.newline(4);
        h.begin(0);
        h.write("public:");
        h.newline();
        h.write("RTT_H_DECLS_STRUCT");          
        h.newline(); h.forceNewline();
        h.write(Emitter.translateType(currentClass, false)+"* operator->() { return this; }");
        h.newline();
        
        sw.begin(0);
        for (PropertyDecl p : context.classProperties()) {
            n.print(p, sw, tr);
        }

        List<ClassMember> members = n.members();

        generateITablesForClass(currentClass, xts, "", h);

        if (!members.isEmpty()) {
            String className = emitter.translateType(currentClass);

            h.write(VOID + " " + INSTANCE_INIT + "();");
            h.newline();
            h.forceNewline();
            if (extractInits(currentClass, INSTANCE_INIT, VOID, members, false)) {
                context.hasInits = true;
            }

            ClassMember prev = null;
            for (ClassMember member : members) {
                if ((member instanceof polyglot.ast.CodeDecl)
                        || (prev instanceof polyglot.ast.CodeDecl)) {
                    h.newline(0);
                    sw.newline(0);
                }
                prev = member;
                n.printBlock(member, sw, tr);
            }

            if (superClass != null) {
                generateProxiesForOverriddenMethods(context, currentClass,
                                                    superClass, xts, "", h, members);
            }

            // Generate structEquals for structs
            h.write("public: ");
            emitter.printType(xts.Boolean(), h);
            h.write(" " + mangled_method_name(STRUCT_EQUALS_METHOD) + "(");
            h.write(Emitter.translateType(currentClass, false) + " *that");
            h.write(");");
            h.newline();

            emitter.printTemplateSignature(currentClass.typeArguments(), sw);
            emitter.printType(xts.Boolean(), sw);
            sw.write(" " + Emitter.translateType(currentClass, false)
                     + "::" + mangled_method_name(STRUCT_EQUALS_METHOD)+ "(");
            sw.write(Emitter.translateType(currentClass, false) + " *that");
            sw.write(") {");
            sw.newline(4);
            sw.begin(0);
            if (superClass != null) {
                sw.write("if (!this->" + Emitter.translateType(superClass)
                         + "::" + mangled_method_name(STRUCT_EQUALS_METHOD)
                         + "((" + Emitter.translateType(superClass)+ "*)(that)))");
                sw.newline(4);
                sw.begin(0);
                sw.write("return false;");
                sw.end();
                sw.newline();
            }
            for (FieldInstance fi : currentClass.fields()) {
                if (!fi.flags().isStatic()) {
                    String name = fi.name().toString();
                    sw.write("if (!" + STRUCT_EQUALS + "(this->"+ mangled_field_name(name) 
                             + ", that->"+ mangled_field_name(name) + "))");
                    sw.newline(4);
                    sw.begin(0);
                    sw.write("return false;");
                    sw.end();
                    sw.newline();
                }
            }
            sw.write("return true;");
            sw.end();
            sw.newline();
            sw.write("}");
            sw.newline();

            generateStaticInitForClass(currentClass, h, members, className);
        }

        h.end();
        sw.end();
        sw.newline();
        h.newline(0);
        h.write("};");
        h.newline();

        emitter.printRTTDefn(currentClass, sw);
    }

	private void generateITablesForClass(X10ClassType currentClass,
			X10TypeSystem xts, String maybeVirtual, ClassifiedStream h) {
		List<X10ClassType> allInterfaces = xts.allImplementedInterfaces(currentClass);
		int numInterfaces = allInterfaces.size();
		if (numInterfaces > 0 && !currentClass.flags().isAbstract()) {
			/* ITables declarations */
			h.write("static x10aux::itable_entry _itables["+(numInterfaces+1)+"];"); h.newline(); h.forceNewline();
			h.write(maybeVirtual+"x10aux::itable_entry* _getITables() { return _itables; }"); h.newline(); h.forceNewline();
			int itableNum = 0;
			for (Type interfaceType : allInterfaces) {
				ITable itable = ITable.getITable((X10ClassType) X10TypeMixin.baseType(interfaceType));
				itable.emitITableDecl(currentClass, itableNum, emitter, h);
				itableNum += 1;
				h.forceNewline();
			}

			/* ITables initialization */
			itableNum = 0;
			for (Type interfaceType : allInterfaces) {
				ITable itable = ITable.getITable((X10ClassType) X10TypeMixin.baseType(interfaceType));
				itable.emitITableInitialization(currentClass, itableNum, emitter, h, sw);
				itableNum += 1;
			}

			if (!currentClass.typeArguments().isEmpty()) {
		        emitter.printTemplateSignature(currentClass.typeArguments(), sw);
			}
			sw.write("x10aux::itable_entry "+emitter.translateType(currentClass)+"::_itables["+(numInterfaces+1)+"] = {");
			itableNum = 0;
			for (Type interfaceType : allInterfaces) {
				sw.write("x10aux::itable_entry(x10aux::getRTT"+chevrons(emitter.translateType(interfaceType, false))+"(), &_itable_"+itableNum+"), ");
				itableNum += 1;
			}
			sw.write("x10aux::itable_entry(NULL, (void*)x10aux::getRTT"+chevrons(emitter.translateType(currentClass, false))+"())};"); sw.newline();
		}
	}

    private void generateProxiesForOverriddenMethods(X10CPPContext_c context,
    		X10ClassType currentClass, X10ClassType superClass,
    		X10TypeSystem xts, String maybeVirtual, ClassifiedStream h,
    		List<ClassMember> members) {
		// first gather a set of all the method names in the current class
		ArrayList<Name> mnames = getMethodNames(members);

		// then, for each one
		for (Name mname : mnames) {
			// get the list of overloads that this class should expose
			// (but doesn't because c++ doesn't work that way)
			List<MethodInstance> overriddenOverloads = getOROLMeths(mname,superClass);
			// for each one...
			for (MethodInstance dropzone_ : overriddenOverloads) {
				X10MethodInstance dropzone = (X10MethodInstance) dropzone_;
				List<Type> formals = dropzone.formalTypes();
				// do we have a matching method? (i.e. one the x10 programmer has written)
				if (currentClass.methods(mname, formals, context).size() > 0) continue;
				// otherwise we need to add a proxy.
				//System.out.println("Not found: "+dropzone);
				assert (!dropzone.flags().isStatic());
				//assert(!dropzone.flags().isFinal());
				assert (!dropzone.flags().isPrivate());
				h.write("public: ");
				List<Type> typeParameters = dropzone.typeParameters();
				List<Type> newTypeParameters = new ArrayList<Type>();
				HashMap<Type, Type> typeMap = new HashMap<Type, Type>();
				for (Type t : typeParameters) {
					assert (t instanceof ParameterType);
					Type dummy = new ParameterType_c(xts, t.position(), Name.makeFresh("T"), null);
					newTypeParameters.add(dummy);
					typeMap.put(t, dummy);
				}
				emitter.printTemplateSignature(newTypeParameters, h);
				if (newTypeParameters.isEmpty()) {
					h.write(maybeVirtual);
				}
				emitter.printType(replaceType(X10TypeMixin.baseType(dropzone.returnType()), typeMap), h);
				h.write(" "+mangled_method_name(mname.toString())+"(");
				h.begin(0);
				int counter = 0;
				for (Type formal : formals) {
					h.write(counter == 0 ? "" : ", ");
					emitter.printType(replaceType(X10TypeMixin.baseType(formal), typeMap), h);
					h.write(" p"+counter++);
				}
				h.end();
				h.write(");"); h.newline();

				if (newTypeParameters.size() != 0) {
					sw.pushCurrentStream(context.templateFunctions);
				}

				emitter.printTemplateSignature(currentClass.typeArguments(), sw);
				emitter.printTemplateSignature(newTypeParameters, sw);
				emitter.printType(replaceType(X10TypeMixin.baseType(dropzone.returnType()), typeMap), sw);
				sw.write(" " + emitter.translateType(currentClass, false) +
						"::" + mangled_method_name(mname.toString()) + "(");
				sw.begin(0);
				counter = 0;
				for (Type formal : formals) {
					sw.write(counter == 0 ? "" : ", ");
					emitter.printType(replaceType(X10TypeMixin.baseType(formal), typeMap), sw);
					sw.write(" p"+counter++);
				}
				sw.end();
				sw.write(") {"); sw.newline(4); sw.begin(0);
				if (!dropzone.returnType().isVoid())
					sw.write("return ");
				String pat = getCppImplForDef(dropzone.x10Def());
				if (pat != null) { // TODO: merge with emitNativeAnnotation
					// FIXME: casts!
					Object[] components = new Object[1+3*newTypeParameters.size() + formals.size()];
					components[0] = "this";
					int i = 1;
					for (Type at : newTypeParameters) {
						components[i++] = at;
						components[i++] = "/"+"*"+" UNUSED "+"*"+"/";
						components[i++] = "/"+"*"+" UNUSED "+"*"+"/";
					}
					counter = 0;
					for (Type formal : formals) {
						components[i++] = "p" + (counter++);
					}
					emitter.dumpRegex("Native", components, tr, pat, sw);
				} else {
					sw.write(emitter.translateType(superClass, false) +
							"::" + mangled_method_name(mname.toString()));
					if (newTypeParameters.size() != 0) {
						String prefix = "<";
						for (Type t : newTypeParameters) {
							sw.write(prefix);
							sw.write(emitter.translateType(t));
							prefix = ",";
						}
						sw.write(">");
					}
					sw.write("(");
					sw.begin(0);
					counter = 0;
					for (Type formal : formals) {
						sw.write(counter == 0 ? "" : ", ");
						sw.write("p" + (counter++));
					}
					sw.end();
					sw.write(")");
				}
				sw.write(";"); sw.end(); sw.newline();

				sw.write("}"); sw.newline();

				if (newTypeParameters.size() != 0) {
					sw.popCurrentStream();
				}
			}
		}
	}

	private void generateStaticInitForClass(X10ClassType currentClass,
			ClassifiedStream h, List<ClassMember> members, String className) {
		// declare static init function in header
		h.write("public : static " + VOID + " " + STATIC_INIT + "();");
		h.newline();
		if (extractInits(currentClass, STATIC_INIT, VOID, members, true)) {
		    // define field that triggers initalisation-time registration of
		    // static init function
		    sw.write("static " + VOID_PTR + " __init__"+getUniqueId_() +
		            " __attribute__((__unused__)) = x10aux::InitDispatcher::addInitializer(" +
		            className+"::"+STATIC_INIT + ")" + ";");
		    sw.newline(); sw.forceNewline(0);
		}
	}

    String defaultValue(Type type) {
		return type.isPrimitive() ? "0" : "x10aux::null";
	}



    public void visit(PackageNode_c n) {
        assert (false);
        sw.write(mangled_non_method_name(translateFQN(n.package_().get().fullName().toString())));
    }

    public void visit(Import_c n) {
        assert (false);
    }


    protected void processMain(X10ClassType container) {
        X10TypeSystem_c xts = (X10TypeSystem_c) container.typeSystem();
        if (container.isClass())
            container = getStaticMemberContainer(container);
        sw.write("#include <x10/runtime/Runtime.h>");
        sw.newline();
        sw.write("#include <x10aux/bootstrap.h>");
        sw.newline();
        String mainTypeArgs = "x10::runtime::Runtime," + emitter.translateType(container);
        sw.write("extern \"C\" { int main(int ac, char **av) { return x10aux::template_main"+chevrons(mainTypeArgs)+"(ac,av); } }");
        sw.newline();
        sw.forceNewline(0);
    }

	public void visit(MethodDecl_c dec) {
		// TODO: if method overrides another method with generic
		// types, check if C++ does the right thing.
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		X10Flags flags = X10Flags.toX10Flags(dec.flags().flags());
		if (flags.isNative())
			return;
		ClassifiedStream h = sw.header();
		X10MethodDef def = (X10MethodDef) dec.methodDef();
		X10MethodInstance mi = (X10MethodInstance) def.asInstance();
		X10ClassType container = (X10ClassType) mi.container();
		if ((container.x10Def().typeParameters().size() != 0) && flags.isStatic()) {
			context.pendingStaticDecls().add(dec);
			return;
		}
		if (query.isMainMethod(dec))
		    processMain(container);
		int mid = getUniqueId_().intValue();
		if (def.typeParameters().size() != 0) {
		    sw.pushCurrentStream(context.templateFunctions);
		    String guard = getHeaderGuard(getHeader(mi.container().toClass()));
		    sw.write("#ifndef "+guard+"_"+mi.name().toString()+"_"+mid); sw.newline();
		    sw.write("#define "+guard+"_"+mi.name().toString()+"_"+mid); sw.newline();
		}
//        X10ClassType container = (X10ClassType) mi.container().toClass();
//        // TODO: [IP] Add an extra apply to something that's both Settable and Indexable
//        try {
//        if (mi.name().toString().equals("apply") &&
//            xts.isImplicitCastValid(container, (Type) xts.forName(QName.make("x10.lang.Indexable"))) &&
//            xts.isImplicitCastValid(container, (Type) xts.forName(QName.make("x10.lang.Settable"))))
//        {
//
//        }
//        } catch (SemanticException e) { assert (false) : ("Huh?  No Indexable or Settable?"); }
		// we sometimes need to use a more general return type as c++ does not support covariant smartptr return types
		Type ret_type = emitter.findRootMethodReturnType(def, dec.position(), mi);
		String methodName = mi.name().toString();
        sw.pushCurrentStream(h);
        emitter.printHeader(dec, sw, tr, methodName, ret_type, false);
        sw.popCurrentStream();
        h.write(";");
        h.newline();

		if (dec.body() != null) {
			if (!flags.isStatic()) {
				VarInstance ti = xts.localDef(Position.COMPILER_GENERATED, Flags.FINAL,
						Types.ref(container), Name.make(THIS)).asInstance();
				context.addVariable(ti);
			}
			emitter.printHeader(dec, sw, tr, methodName, ret_type, true);
			dec.printSubStmt(dec.body(), sw, tr);
			sw.newline();
		} else {
			// Define property getter methods.
			if (flags.isProperty() && flags.isAbstract() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
				X10FieldInstance fi = (X10FieldInstance) container.fieldNamed(mi.name());
				if (fi != null) {
					//assert (X10Flags.toX10Flags(fi.flags()).isProperty()); // FIXME: property fields don't seem to have the property flag set
					// This is a property method in an interface.  Give it a body.
					emitter.printHeader(dec, sw, tr, methodName, ret_type, true);
					sw.write(" {");
					sw.allowBreak(0, " ");
					sw.write("return "+mangled_field_name(fi.name().toString())+";");
					sw.allowBreak(0, " ");
					sw.write("}");
				}
			}
		}
		if (def.typeParameters().size() != 0) {
		    String guard = getHeaderGuard(getHeader(mi.container().toClass()));
		    sw.write("#endif // "+guard+"_"+mi.name().toString()+"_"+mid); sw.newline();
		    sw.popCurrentStream();
		}
	}


	public void visit(ConstructorDecl_c dec) {

        // [DC] here we generate a pair of functions to form a fake
        // constructor.   Real constructors are no use to us because they
        // cannot invoke virtual functions with the semantics we need.  The two
        // functions are _make which behaves like a constructor by creating an
        // uninitialised object and calling _constructor (the body of the X10
        // constructor).  Calls to super() and this() are translated into calls
        // to the appropriate OtherClass::_constructor (hence the separate
        // function).

		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (dec.flags().flags().isNative())
			return;

		ClassifiedStream h = sw.header();

        X10ClassType container = (X10ClassType) dec.constructorDef().container().get();
        String typeName = emitter.translateType(container.def().asType());

        if (!container.flags().isAbstract()) {
            // emit _make method
            h.write("static ");
            sw.pushCurrentStream(h);
            emitter.printHeader(dec, sw, tr, false, MAKE, context.generatingStruct() ? typeName : make_ref(typeName));
            sw.popCurrentStream();
            h.allowBreak(0, " "); h.write("{"); h.newline(4); h.begin(0);
            if (context.generatingStruct()) {
            	h.write(typeName+" this_; "); h.newline();
            	h.write("this_."+CONSTRUCTOR+"(");
            } else {
                h.write(make_ref(typeName)+" this_ = "+
                		"new (x10aux::alloc"+chevrons(typeName)+"()) "+typeName+"();"); h.newline();
                h.write("this_->"+CONSTRUCTOR+"(");
            }
            for (Iterator<Formal> i = dec.formals().iterator(); i.hasNext(); ) {
                Formal f = i.next();
                h.write(mangled_non_method_name(f.name().id().toString()));
                if (i.hasNext()) {
                    h.write(",");
                    h.allowBreak(0, " ");
                }
            }
            h.write(");"); h.newline();
            h.write("return this_;");
            h.end(); h.newline();
            h.write("}"); h.newline(); // no gap between _make and _constructor
        }

        sw.pushCurrentStream(h);
		emitter.printHeader(dec, sw, tr, false, CONSTRUCTOR, "void");
        sw.popCurrentStream();
        h.write(";") ; h.newline();
        h.forceNewline();
		emitter.printHeader(dec, sw, tr, true, CONSTRUCTOR, "void");
		X10ConstructorInstance ci = (X10ConstructorInstance) dec.constructorDef().asInstance();
		if (dec.body() == null) {
            assert false : dec.position().toString();
            sw.write("{ }"); sw.newline(); sw.forceNewline();
        }

        assert (!dec.flags().flags().isStatic());

        TypeSystem ts = tr.typeSystem();

        VarInstance ti = ts.localDef(Position.COMPILER_GENERATED, Flags.FINAL,
                                     Types.ref(container), Name.make(THIS)).asInstance();
        context.addVariable(ti);


        sw.allowBreak(0, " "); sw.write("{"); sw.newline(4); sw.begin(0);

        Block_c body = (Block_c) dec.body();

        // Synthetic fields must be initialized before everything else
        for (Stmt s : body.statements()) {
            if (query.isSyntheticOuterAccessor(s)) {
                dec.print(s, sw, tr); sw.newline();
            }
        }

        if (context.hasInits) {
            // then, run x10 instance field initialisers
            sw.write("this->"+INSTANCE_INIT+"();"); sw.newline();
        }

        for (Stmt s : body.statements()) {
            // FIXME: constructor calls won't get line number information
            if (s instanceof ConstructorCall) {
                ConstructorCall call = (ConstructorCall)s;
                if (call.kind() == ConstructorCall.SUPER) {
                    String superClass = emitter.translateType(container.superClass());
                    sw.write("this->"+superClass+"::"+CONSTRUCTOR+"(");
                } else if (call.kind() == ConstructorCall.THIS) {
                    sw.write("this->"+CONSTRUCTOR+"(");
                }
                if (call.arguments().size() > 0) {
                    sw.allowBreak(2, 2, "", 0); // miser mode
                    sw.begin(0);
                    boolean first = true;
                    for(Expr e : (List<Expr>) call.arguments() ) {
                        if (!first) {
                            sw.write(",");
                            sw.allowBreak(0, " ");
                        }
                        dec.print(e, sw, tr);
                        first = false;
                    }
                    sw.end();
                }
                sw.write(");");
                sw.newline();
            } else if (query.isSyntheticOuterAccessor(s)) {
                // we did synthetic field initialisation earlier
            } else {
                dec.printBlock(s, sw, tr);
                sw.newline();
            }
        }
        sw.end(); sw.newline();
        sw.write("}");

		sw.newline(); sw.forceNewline();
	}


	public void visit(FieldDecl_c dec) {
	    // FIXME: HACK: skip synthetic serialization fields
	    if (query.isSyntheticField(dec.name().id().toString()))
	        return;

	    X10CPPContext_c context = (X10CPPContext_c) tr.context();
	    if ((((X10ClassDef)((X10ClassType)dec.fieldDef().asInstance().container()).def()).typeParameters().size() != 0) &&
	            dec.flags().flags().isStatic())
	    {
	        context.pendingStaticDecls().add(dec);
	        return;
	    }

	    ClassifiedStream h = sw.header();
	    sw.pushCurrentStream(h);
	    emitter.printHeader(dec, sw, tr, false);
	    sw.popCurrentStream();
	    // Ignore the initializer -- this will have been done in extractInits/extractStaticInits
	    // FIXME: the above breaks switch constants!
	    h.write(";");
	    h.newline(); h.forceNewline();
	    if (dec.flags().flags().isStatic()) {
	        emitter.printHeader(dec, sw, tr, true);
	        // [DC] disabled because I want this done through the static initialisation framework
	        // [IP] re-enabled for a very limited set of cases, namely literal inits
	        if (isGlobalInit(dec)) {
	            sw.write(" =");
	            sw.allowBreak(2, " ");
	            dec.print(dec.init(), sw, tr);
	        }
	        sw.write(";");
	        sw.newline(); sw.forceNewline();
	    }
	}

	public void visit(PropertyDecl_c n) {
		super.visit(n);
	}

	public void visit(Initializer_c n) {
		if (n.flags().flags().isStatic()) {
			// Ignore -- this will have been processed earlier
		}
	}


    public void visit(AssignPropertyBody_c n) {
        n.translate(sw, tr);
    }


	public void visit(Assert_c n) {
		if (!tr.job().extensionInfo().getOptions().assertions)
			return;

		sw.write("x10aux::x10__assert(");
		n.print(n.cond(), sw, tr);
		if (n.errorMessage() != null) {
			sw.write(",");
			sw.allowBreak(4, " ");
			n.print(n.errorMessage(), sw, tr);
		}
		sw.write(");");
		sw.newline();
	}


	public void visit(Switch_c n) {
	    sw.write("switch (");
	    n.print(n.expr(), sw, tr);
	    sw.write(")");
	    sw.allowBreak(0, " ");
	    sw.write("{");
	    sw.newline();
	    if (n.elements().size() > 0) {
	        sw.newline(4);
	        sw.begin(0);
	        for (Iterator i = n.elements().iterator(); i.hasNext(); ) {
	            Node s = (Node) i.next();
	            n.print(s, sw, tr);
	        }
	        sw.end();
	        sw.newline(0);
	    }
	    else
	        sw.write(" ");

	    sw.write("}");
	}

	public void visit(SwitchBlock_c n) {
		sw.write("{");
		if (n.statements().size() > 0) {
			sw.newline(4);
			sw.begin(0);
			for (Iterator i = n.statements().iterator(); i.hasNext(); ) {
				Node s = (Node) i.next();
				n.print(s, sw, tr);
				if (i.hasNext())
					sw.newline();
			}
			sw.end();
			sw.newline(0);
		}
		else
			sw.write(" ");

		sw.write("}");
	}

	public void visit(Case_c n) {
		sw.newline();
		if (n.expr() == null) {
			sw.write("default :");
		}
		else {
			sw.write("case ");
			// FIXME: [IP] HACK HACK HACK! Substitute the actual constant if any
			if (n.expr() instanceof Field_c && isConstant(n.expr())) {
				sw.write(""+n.expr().constantValue());
				sw.write("/"+"*");
				n.print(n.expr(), sw, tr);
				sw.write("*"+"/");
			} else {
				n.print(n.expr(), sw, tr);
			}
			sw.write(":");
		}
		sw.newline();
	}

	public void visit(Branch_c br) {
		if (br.labelNode() != null) {
			if (br.kind() == Branch_c.CONTINUE)
				sw.write("goto " + br.labelNode().id().toString() + "_next_");
			else
				sw.write("goto " + br.labelNode().id().toString() + "_end_");
		} else
			sw.write(br.kind().toString());
		sw.write(";");
		sw.newline();
	}


	public void visit(Labeled_c label) {
		// For every labeled statement, generate one/three labels->
		// L: S --> L :
		//          S
		// If S is a for / while / do-while loop then after the
		// generated C++ for-loop, have a label L_end_ and before
		// end-parenthesis of the loop, have one more label L_next_:
		// L : for (...) { ... } -->
		// L :
		// for (...) {... L_next_: ; }
		// L_end_: ;

        X10CPPContext_c context = (X10CPPContext_c) tr.context();

		sw.write(label.labelNode() + " : ");
		sw.newline();
		context.setLabel(label.labelNode().id().toString(), label.statement());
		label.print(label.statement(), sw, tr);
		sw.newline();
	}


	public void visit(Assign_c asgn) {
	    boolean unsigned_op = false;
	    String opString = asgn.operator().toString();

	    if (opString.equals(">>>=")) {
	        unsigned_op = true;
	        opString = opString.substring(1);
	    }

	    NodeFactory nf = tr.nodeFactory();
	    Expr lhs = asgn.left(nf);
	    Expr rhs = asgn.right();
	    if (unsigned_op) {
	        sw.write("("+emitter.translateType(asgn.type())+")(");
	        sw.write("(("+emitter.makeUnsignedType(lhs.type())+"&)");
	    }
	    asgn.printSubExpr(lhs, false, sw, tr);
	    if (unsigned_op)
	        sw.write(")");
	    sw.write(" ");
	    // [IP] Are all the operators the same?
	    sw.write(opString);
        sw.allowBreak(2, 2, " ", 1);

	    X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
	    Context context = tr.context();
	    Type aType = lhs.type();
	    boolean rhsNeedsCast = !xts.typeDeepBaseEquals(aType, rhs.type(), context);
	    if (rhsNeedsCast) {
	        // FIXME: this cast would not be needed if not for a frontend bug
	        sw.write("x10aux::class_cast<");
	        emitter.printType(aType, sw);
	        sw.write(" >(");
	    }
	    if (unsigned_op)
	        sw.write("(("+emitter.makeUnsignedType(rhs.type())+")");
	    asgn.printSubExpr(rhs, true, sw, tr);
	    if (unsigned_op)
	        sw.write("))");
        if (rhsNeedsCast)
            sw.write(")");
	}


	public void visit(Return_c ret) {
		Expr e = ret.expr();
		if (e == null) {
			sw.write("return;");
		} else {
			sw.write("return ");
			ret.print(e, sw, tr);
			sw.write(";"); sw.newline();
		}
	}


	public void visit(Formal_c n) {
		emitter.printHeader(n, sw, tr, true);
	}


	public void visit(LocalDecl_c dec) {
	    emitter.printHeader(dec, sw, tr, true);

	    Expr initexpr = dec.init();
	    if (initexpr != null) {
	        sw.write(" =");
	        sw.allowBreak(2, " ");
	        X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
	        Context context = tr.context();
	        Type aType = dec.type().type();
	        boolean rhsNeedsCast = !xts.typeDeepBaseEquals(aType, initexpr.type(), context);
	        if (rhsNeedsCast) {
	            // FIXME: this cast would not be needed if not for a frontend bug
	            sw.write("x10aux::class_cast<");
	            emitter.printType(aType, sw);
	            sw.write(" >(");
	        }
	        dec.print(initexpr, sw, tr);
	        if (rhsNeedsCast)
	            sw.write(")");
	    }

	    if (tr.appendSemicolon()) {
	        sw.write(";");
	        sw.newline(0);
	    }
	}

	public void visit(Block_c b) {
		sw.write("{");
		sw.newline();
		if (b.statements().size() > 0) {
			sw.newline(4);
			sw.begin(0);
			for (Iterator i = b.statements().iterator(); i.hasNext(); ) {
				Stmt n = (Stmt) i.next();
				b.printBlock(n, sw, tr);
				if (i.hasNext())
					sw.newline();
			}
			sw.end();
			sw.newline(0);
		}
		else
			sw.write(" ");
		sw.newline();
		sw.write("}");
	}


	public void visit(StmtSeq_c s) {
		assert (false); // FIXME. It is not clear if when StmtSeq_c nodes are generated.  If they indeed are, remove this assert and continue with the method below.

		sw.newline();
		if (s.statements().size() > 0) {
			sw.newline(4);
			sw.begin(0);
			for (Iterator i = s.statements().iterator(); i.hasNext(); ) {
				Stmt n = (Stmt) i.next();
				s.printBlock(n, sw, tr);
				if (i.hasNext())
					sw.newline();
			}
			sw.end();
			sw.newline(0);
		}
		else
			sw.write(" ");
		sw.newline();
	}


	private void handleLabeledLoop(Loop_c n) {
		String label = null;
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (context.getLabeledStatement() == n) {
			label = context.getLabel();
			context.setLabel(null, null);
		}
		sw.allowBreak(0, " ");
		if (label != null) {
			sw.write("{");
			sw.newline(0); sw.begin(0);
			sw.newline();
		}
		sw.newline();
		n.print(n.body(), sw, tr);
		sw.newline();
		if (label != null) {
			sw.newline(0);
			sw.write(label + "_next_ : ;");
			sw.end(); sw.newline();
			sw.write("}");
			sw.newline(0);
			sw.write(label + "_end_ : ; ");
		}
	}

	public void visit(For_c n) {
		// FIXME: Generate normal for-loop code, without
		// separating out the inits. [Krishna]

		sw.write("{");
		sw.newline(4); sw.begin(0);
		if (n.inits() != null) {
			for (Iterator i = n.inits().iterator(); i.hasNext(); ) {
				ForInit s = (ForInit) i.next();
				if (s instanceof LocalDecl_c) {
					LocalDecl_c dec = (LocalDecl_c) s;
					emitter.printHeader(dec, sw, tr, true);
					sw.write(";");
					sw.newline(0);
				}
			}
		}

		sw.newline(0);
		sw.write("for (");
		sw.begin(0);

		if (n.inits() != null) {
			for (Iterator i = n.inits().iterator(); i.hasNext(); ) {
				ForInit s = (ForInit) i.next();
				boolean oldSemiColon = tr.appendSemicolon(false);
				boolean oldPrintType = tr.printType(false);
				n.printBlock(s, sw, tr);
				tr.printType(oldPrintType);
				tr.appendSemicolon(oldSemiColon);

				if (i.hasNext()) {
					sw.write(",");
					sw.allowBreak(2, " ");
				}
			}
		}

		sw.write(";");
		sw.allowBreak(0);

		if (n.cond() != null) {
			n.printBlock(n.cond(), sw, tr);
		}

		sw.write(";");
		sw.allowBreak(0);

		if (n.iters() != null) {
			for (Iterator i = n.iters().iterator(); i.hasNext(); ) {
				ForUpdate s = (ForUpdate) i.next();
				boolean oldSemiColon = tr.appendSemicolon(false);
				n.printBlock(s, sw, tr);
				tr.appendSemicolon(oldSemiColon);

				if (i.hasNext()) {
					sw.write(",");
					sw.allowBreak(2, " ");
				}
			}
		}

		sw.end();
		sw.write(")");
		sw.write("{");
    	sw.newline(4);
        sw.begin(0);
        
		handleLabeledLoop(n);

		sw.end(); sw.newline(0);
		sw.write("}");
		sw.end(); sw.newline(0);

		sw.write("}");
		sw.newline(0);
	}

	public void visit(Do_c n) {
	    sw.write("do {");
	    handleLabeledLoop(n);
	    sw.write("} while (");
	    n.printBlock(n.cond(), sw, tr);
	    sw.write(");");
	}

	public void visit(While_c n) {
	    sw.write("while (");
	    n.printBlock(n.cond(), sw, tr);
	    sw.write(")");
	    handleLabeledLoop(n);
	}


	public void visit(If_c n) {
		sw.write("if (");
		n.printBlock(n.cond(), sw, tr);
		sw.write(")");
		sw.allowBreak(0, " ");
		n.print(n.consequent(), sw, tr);
		if (n.alternative() != null) {
		    sw.allowBreak(0, " ");
		    sw.write("else");
		    sw.allowBreak(0, " ");
			// [IP] Semi-HACK: handle "else if" specially
			Stmt alternative = n.alternative();
			if (alternative instanceof Block_c) {
				Block_c block = (Block_c) alternative;
				if (block.statements().size() == 1 && block.statements().get(0) instanceof If_c)
					alternative = (Stmt) block.statements().get(0);
			}
			n.print(alternative, sw, tr);
		}
        sw.newline(0);
	}


	public void visit(Empty_c n) {
		sw.write(";");
	}


	public void visit(Eval_c n) {
		boolean semi = tr.appendSemicolon(true);
		n.print(n.expr(), sw, tr);
		if (semi)
			sw.write(";");
		tr.appendSemicolon(semi);
	}

	private Expr cast(Expr a, Type fType) {
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tr.nodeFactory();
		Context context = tr.context();
		if (!xts.typeDeepBaseEquals(fType, a.type(), context)) {
			Position pos = a.position();
			a = nf.X10Cast(pos, nf.CanonicalTypeNode(pos, fType), a,
					X10Cast.ConversionType.CHECKED).type(fType);
        }
		return a;
	}

	public void visit(X10Call_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		X10MethodInstance mi = (X10MethodInstance) n.methodInstance();
		Receiver target = n.target();
		Type t = target.type();

		X10MethodDef md = mi.x10Def();
		if (mi.flags().isStatic()) {
		    TypeNode tn =
		        target instanceof TypeNode ?
		                (TypeNode) target :
		                tr.nodeFactory().CanonicalTypeNode(target.position(), t);
		    if (t instanceof ParameterType) {
		        // Rewrite to the class declaring the field.
		        target = tn.typeRef(md.container());
		        n = (X10Call_c) n.target(target);
		    }
		    if (t.isClass()) {
		        X10ClassType ct = (X10ClassType)t.toClass();
		        X10ClassDef cd = (X10ClassDef)ct.def();
		        target = tn.typeRef(Types.ref(getStaticMemberContainer((X10ClassType)cd.asType())));
		    }
		}

		X10Flags flags = X10Flags.toX10Flags(mi.flags());
		// Check for properties accessed using method syntax.  They may have @Native annotations too.
		if (flags.isProperty() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
		    X10FieldInstance fi = (X10FieldInstance) md.container().get().fieldNamed(mi.name());
		    if (fi != null) {
		        //assert (X10Flags.toX10Flags(fi.flags()).isProperty()); // FIXME: property fields don't seem to have the property flag set
		        // This is a property getter method.  Translate as a field access.
		        String pat = getCppImplForDef(fi.x10Def());
		        if (pat != null) {
		            Object[] components = new Object[] { target };
		            emitter.dumpRegex("Native", components, tr, pat, sw);
		            return;
		        }
		    }
		}

		X10NodeFactory nf = (X10NodeFactory) tr.nodeFactory();
		List<Expr> args = new ArrayList<Expr>();
		int counter = 0;
		for (Expr a : n.arguments()) {
		    Type fType = mi.formalTypes().get(counter);
		    if (!xts.typeEquals(fType, a.type(), context) && !(xts.isParameterType(fType) && a.type().isNull()))
		        a = cast(a, fType);
		    args.add(a);
		    counter++;
		}

		// [IP] FIXME: accommodate the frontend hack for the equals method
		if (mi.name() == Name.make("equals") && args.size() == 1 && mi.typeParameters().size() == 0 &&
                xts.isParameterType(mi.formalTypes().get(0)))
		{
		    args.set(0, cast(args.get(0), xts.Object()));
		}

		String pat = getCppImplForDef(md);
		if (pat != null) {
			emitNativeAnnotation(pat, mi.typeParameters(), target, args);
			return;
		}

		// the cast is because our generated member function may use a more general
		// return type because c++ does not support covariant smartptr returns
        Type ret_type = emitter.findRootMethodReturnType(md, null, mi);
		boolean needsCast = !xts.typeDeepBaseEquals(mi.returnType(), ret_type, context);
		if (needsCast) {
			sw.write("static_cast<");
			emitter.printType(mi.returnType(), sw);
			sw.write(" >(");
		}
		sw.begin(0);
        String dangling = "";
        boolean already_static = false;
        String targetMethodName = mangled_method_name(n.name().id().toString());
        boolean isInterfaceInvoke = false;
		if (!n.isTargetImplicit()) {
			// explicit target.

            if (target instanceof X10Special_c &&
                ((X10Special_c)target).kind().equals(X10Special_c.SUPER))
            {
                sw.write(emitter.translateType(t));
                sw.write("::");
                already_static = true;
            } else if (target instanceof Expr) {
                if (mi.flags().isStatic()) {
                    sw.write("((void)");
                    n.printSubExpr((Expr) target, false, sw, tr);
                    sw.write(",");
                    sw.write(emitter.translateType(t));
                    sw.write("::");
                    dangling = ")";
                    already_static = true;
                } else {
                    boolean assoc = !(target instanceof New_c || target instanceof Binary_c);
                    if (t.isClass()) {
                    	X10ClassType clsType = (X10ClassType)t.toClass();
                    	if (clsType.flags().isInterface()) {
                    		ITable itable= ITable.getITable(clsType);
                    		targetMethodName = itable.mangledName(mi);
                    		isInterfaceInvoke = true;
                    		if (ret_type.isVoid()) {
                    			sw.write("(__extension__ ({ x10aux::ref<x10::lang::Object> _ = x10aux::placeCheck(x10aux::nullCheck(");
                        		n.printSubExpr((Expr) target, assoc, sw, tr);
                        		sw.write(")); (_.operator->()->*(x10aux::findITable"+chevrons(emitter.translateType(clsType, false))+"(_->_getITables())->"+itable.mangledName(mi)+"))");
                        		dangling = "; }))";
                    		} else {
                    			sw.write("(__extension__ ({ x10aux::ref<x10::lang::Object> _ = x10aux::placeCheck(x10aux::nullCheck(");
                    			n.printSubExpr((Expr) target, assoc, sw, tr);
                    			sw.write(")); x10aux::GXX_ICE_Workaround"+chevrons(emitter.translateType(ret_type, true))+"::_((_.operator->()->*(x10aux::findITable"+chevrons(emitter.translateType(clsType, false))+"(_->_getITables())->"+itable.mangledName(mi)+"))");
                    			dangling = "); }))";
                    		}
                    	}
                   }

                    if (!isInterfaceInvoke) {
                    	boolean needsCheck = true || !((X10Type)t).isX10Struct(); // HACK: treating sturcts as values.
                    	sw.write(needsCheck ? "x10aux::placeCheck(x10aux::nullCheck(" : "(");
                  		n.printSubExpr((Expr) target, assoc, sw, tr);
                  		sw.write((needsCheck ? "))" : ")")+"->");
                    }
                }
            } else if (target instanceof TypeNode || target instanceof AmbReceiver) {
                n.print(target, sw, tr);
                sw.write("::");
                already_static = true;
            }
		}

        boolean virtual_dispatch = true;
        if (t.isClass()) {
            X10ClassType ct = (X10ClassType)t.toClass();
            X10ClassDef cd = ct.x10Def();
            if (cd.flags().isFinal()) {
                virtual_dispatch = false;
            }
        }
        if (mi.flags().isFinal()) {
            virtual_dispatch = false;
        }
        if (!virtual_dispatch && !already_static ) {
            // disable virtual dispatch
            sw.write(emitter.translateType(t));
            sw.write("::");
        }
        // [IP] FIXME: virtual_dispatch test is temporary, until xlC is upgraded to v10
        if (context.inTemplate() && mi.typeParameters().size() != 0 && virtual_dispatch) {
            sw.write("template ");
        }
		if (!isInterfaceInvoke) {
			sw.write(targetMethodName);
			emitter.printTemplateInstantiation(mi, sw);
		}
		sw.write("(");
		if (args.size() > 0) {
			sw.allowBreak(2, 2, "", 0); // miser mode
			sw.begin(0);
			counter = 0;
			for (Iterator i = args.iterator(); i.hasNext(); ) {
				Expr e = (Expr) i.next();
				Type fType = mi.formalTypes().get(counter);
				assert (xts.typeDeepBaseEquals(fType, e.type(), context)) : ("Casts should have been inserted");
				n.print(e, sw, tr);
				if (i.hasNext()) {
					sw.write(",");
					sw.allowBreak(0, " ");
				}
				counter++;
			}
			sw.end();
		}
		sw.write(")");
		sw.write(dangling);
		sw.end();
		if (needsCast) {
			sw.write(")");
		}
	}

	public void visit(RegionMaker_c n) {
		super.visit(n);
	}

	public void visit(ConstantDistMaker_c n) {
        super.visit(n);
	}


	public void visit(Field_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		Receiver target = n.target();
		Type t = target.type();

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		X10FieldInstance fi = (X10FieldInstance) n.fieldInstance();

		X10FieldDef fd = fi.x10Def();
		if (target instanceof TypeNode) {
		    assert (fi.flags().isStatic());
		    TypeNode tn = (TypeNode) target;
		    if (t instanceof ParameterType) {
		        // Rewrite to the class declaring the field.
		        target = tn.typeRef(fd.container());
		        n = (Field_c) n.target(target);
		    }
		    if (t.isClass()) {
		        X10ClassType ct = (X10ClassType)t.toClass();
		        X10ClassDef cd = (X10ClassDef)ct.def();
		        target = tn.typeRef(Types.ref(getStaticMemberContainer((X10ClassType)cd.asType())));
		    }
		}

		String pat = getCppImplForDef(fd);
		if (pat != null) {
			Object[] components = new Object[] { target };
			emitter.dumpRegex("Native", components, tr, pat, sw);
			return;
		}

		sw.begin(0);
		// TODO: capture constant fields as variables
		if (!n.flags().isStatic()) {
			X10CPPContext_c c = (X10CPPContext_c) tr.context();
			if (target instanceof X10Special_c && ((X10Special_c)target).isSelf()) {
				assert (false) : ("Loki knows why we got here...");
				//sw.write((context.Self() == null) ? "self" : context.Self());
				//sw.write("->");
			}
		}
		assert (target != null);
		if (target instanceof Expr) {
			if (fi.flags().isStatic()) {
				sw.write("((void)");
				n.printSubExpr((Expr) target, false, sw, tr);
				sw.write(",");
				sw.write(emitter.translateType(target.type()));
				sw.write("::");
				sw.allowBreak(2, 3, "", 0);
				sw.write(mangled_field_name(n.name().id().toString()));
				sw.write(")");
				sw.end();
				return;
			} else {
				boolean needsChecks = true || !((X10Type)t).isX10Struct(); // HACK: treating sturcts as values.
				boolean assoc = !(target instanceof New_c || target instanceof Binary_c);
				if (needsChecks) sw.write("x10aux::placeCheck(x10aux::nullCheck(");
				n.printSubExpr((Expr) target, assoc, sw, tr);
				if (needsChecks) sw.write("))");
			}
		}
		else if (target instanceof TypeNode || target instanceof AmbReceiver) {
			n.print(target, sw, tr);
		}
		if (n.fieldInstance().flags().isStatic()) {
			sw.write("::");
		} else {
			sw.write("->");
		}
		sw.allowBreak(2, 3, "", 0);
		sw.write(mangled_field_name(n.name().id().toString()));
		sw.end();
	}


	public void visit(Local_c n) {
		X10CPPContext_c c = (X10CPPContext_c) tr.context();
		LocalInstance var = n.localInstance();
		if (c.isInsideClosure())
			c.saveEnvVariableInfo(n.name().toString());
		sw.write(mangled_non_method_name(var.name().toString()));
	}

	public void visit(New_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem xts = (X10TypeSystem)context.typeSystem();
		ConstructorInstance constructor = n.constructorInstance();
		
		if (n.qualifier() != null)
			throw new InternalCompilerError("Qualified new not supported");
		if (n.body() != null)
			throw new InternalCompilerError("Anonymous innner classes should have been removed.");
		
		List<Expr> coercedArgs = new ArrayList<Expr>();
		int counter = 0;
		for (Expr a : n.arguments()) {
		    Type fType = constructor.formalTypes().get(counter);
		    if (!xts.typeEquals(fType, a.type(), context) && !(xts.isParameterType(fType) && a.type().isNull())) {
		        a = cast(a, fType);
		    }
		    coercedArgs.add(a);
		    counter++;
		}
		
		sw.write(Emitter.translateType(n.objectType().type())+"::"+MAKE+"(");
		sw.begin(0);
		for (Iterator<Expr> i = coercedArgs.iterator(); i.hasNext(); ) {
			Expr e = i.next();
			n.print(e, sw, tr);
			if (i.hasNext()) {
				sw.write(",");
				sw.allowBreak(0, " ");
			}
		}
		sw.write(")");
		sw.end();
	}


	public void visit(FloatLit_c n) {
		String val;
		if (n.kind() == FloatLit_c.FLOAT)
			val = Float.toString((float) n.value()) + "f";
		else if (n.kind() == FloatLit_c.DOUBLE)
			val = Double.toString(n.value());
		else
			throw new InternalCompilerError("Unrecognized FloatLit kind " + n.kind());
		sw.write(val);
	}

	public void visit(IntLit_c n) {
	    String val;
	    if (n.kind() == X10IntLit_c.ULONG) {
	        if (n.boundary())
	            val = "0x" + Long.toHexString(n.value()).toUpperCase() + "llu";
	        else if (n.value() < 0)
	            val = "0x" + Long.toHexString(n.value()).toUpperCase() + "llu";
	        else
	            val = Long.toString(n.value()) + "ull";
	    } else if (n.kind() == X10IntLit_c.UINT) {
	        if (n.value() >= 0x80000000L)
	            val = "0x" + Long.toHexString(n.value()).toUpperCase() + "u";
	        else if (n.boundary())
	            val = "0x" + Long.toHexString(-n.value()).toUpperCase() + "u";
	        else
	            val = Long.toString((int) n.value()) + "u";
	    } else if (n.kind() == IntLit_c.LONG) {
	        if (n.boundary())
	            val = "0x" + Long.toHexString(n.value()).toUpperCase() + "llu";
	        else
	            val = Long.toString(n.value()) + "ll";
	    } else if (n.kind() == IntLit_c.INT) {
	        if (n.value() >= 0x80000000L)
	            val = "0x" + Long.toHexString(n.value()).toUpperCase() + "u";
	        else if (n.boundary())
	            val = "0x" + Long.toHexString(-n.value()).toUpperCase() + "u";
	        else
	            val = Long.toString((int) n.value());
	    } else
	        throw new InternalCompilerError("Unrecognized IntLit kind " + n.kind());
	    sw.write("("); sw.begin(0);
	    sw.write("(" + emitter.translateType(n.type(), true) + ")");
	    sw.write(val);
	    sw.end(); sw.write(")");
	}

	public void visit(NullLit_c n) {
		sw.write("x10aux::null");
	}

	public void visit(StringLit_c n) {
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		sw.write(emitter.translateType(xts.String())+"::Lit(\"");
		sw.write(StringUtil.escape(n.stringValue()));
		sw.write("\")");
	}

	public void visit(CharLit_c lit) {
		sw.write("((x10_char)'"+StringUtil.escape(lit.charValue())+"')");
	}

	public void visit(BooleanLit_c lit) {
		sw.write(lit.toString());
	}


	public void visit(Id_c n) {
		sw.write(mangled_non_method_name(n.id().toString()));
	}


	public void visit(X10Cast_c c) {
		TypeNode tn = c.castType();
		assert tn instanceof CanonicalTypeNode;

		switch (c.conversionType()) {
		case CHECKED:
		case PRIMITIVE:
		case SUBTYPE:

			if (tn instanceof X10CanonicalTypeNode) {
				X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;

                Type t = X10TypeMixin.baseType(xtn.type());
                Type f = X10TypeMixin.baseType(c.expr().type());

                Type t_ = X10TypeMixin.stripConstraints(t);
                Type f_ = X10TypeMixin.stripConstraints(f);

                X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
                X10Context context = (X10Context) tr.context();

                if (xts.typeEquals(f_, t_, context)) {
                    c.printSubExpr(c.expr(), true, sw, tr);
                } else if (c.conversionType()==X10Cast_c.ConversionType.SUBTYPE && xts.isSubtype(f_, t_, context)) {
					c.printSubExpr(c.expr(), true, sw, tr);
				} else {
					sw.write("x10aux::class_cast<");
					emitter.printType(t_, sw);
					sw.write(" >(");
					c.printSubExpr(c.expr(), true, sw, tr);
					sw.write(")");
				}
			} else {
				throw new InternalCompilerError("Ambiguous TypeNode survived type-checking.", tn.position());
			}
			break;

		case UNBOXING:
			throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
		case UNKNOWN_IMPLICIT_CONVERSION:
			throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
		case UNKNOWN_CONVERSION:
			throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
		case BOXING:
			throw new InternalCompilerError("Boxing conversion should have been rewritten.", c.position());
		}
	}

	public void visit(SubtypeTest_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
        if (n.equals()) {
            sw.write("x10aux::sametype");
            sw.write(chevrons(
                emitter.translateType(n.subtype().type(), true) + "," +
                emitter.translateType(n.supertype().type(), true)
            ));
        } else {
            sw.write("x10aux::subtypeof");
            sw.write(chevrons(
                emitter.translateType(n.subtype().type(), true) + "," +
                emitter.translateType(n.supertype().type(), true)
            ));
		}
		sw.write("()");
	}

	public void visit(X10Instanceof_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		if (refsAsPointers) {
			sw.write("!!dynamic_cast");
			sw.write(chevrons(emitter.translateType(n.compareType().type(), true)));
			sw.write("(");
			sw.begin(0);
			n.printSubExpr(n.expr(), sw, tr);
			sw.end();
			sw.write(")");
			return;
		}
		sw.write("x10aux::instanceof");
		sw.write(chevrons(emitter.translateType(n.compareType().type(), true)));
		sw.write("(");
		sw.begin(0);
		n.printSubExpr(n.expr(), false, sw, tr);
		sw.end();
		sw.write(")");
	}

	public void visit(Throw_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		sw.write("x10aux::throwException(x10aux::nullCheck(");
		n.print(n.expr(), sw, tr);
		sw.write("));");
	}

	public void visit(Try_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		if (n.finallyBlock() != null) {
			sw.write("try {");
			sw.newline(0); sw.begin(0);
		}
		sw.write("try");
		assert (n.tryBlock() instanceof Block_c);
		n.printSubStmt(n.tryBlock(), sw, tr);
		sw.newline(0);

		// [IP] C++ will not catch ref types properly, as there is no hierarchy.
		// So, we have to do the dispatching ourselves.
		sw.newline();
		String refVar = "__ref__" + getUniqueId_();
		sw.write("catch (x10aux::__ref& " + refVar + ") {");
		sw.newline(4); sw.begin(0);
		if (n.catchBlocks().size() > 0) {
		    String excVar = "__exc" + refVar;
		    // Note that the following c-style cast only works because Throwable is
		    // *not* an interface and thus is not virtually inheritted.  If it
		    // were, we would have to static_cast the exception to Throwable on
		    // throw (otherwise we would need to offset by an unknown quantity).
		    String exception_ref = emitter.translateType(xts.Throwable(), true);
		    sw.write(exception_ref+"& " + excVar + " = ("+exception_ref+"&)" + refVar + ";");
		    context.setExceptionVar(excVar);
		    for (Iterator it = n.catchBlocks().iterator(); it.hasNext(); ) {
		        Catch cb = (Catch) it.next();
		        sw.newline(0);
		        n.printBlock(cb, sw, tr);
		    }
		}
		sw.newline();
		sw.write("throw;");
		sw.end(); sw.newline();
		sw.write("}");
		if (n.finallyBlock() != null) {
			sw.end(); sw.newline();
			sw.write("} catch (...) {");
			sw.newline(4); sw.begin(0);
			n.printBlock(n.finallyBlock(), sw, tr);
			sw.newline();
			sw.write("throw;");
			sw.end(); sw.newline();
			sw.write("}");
			sw.newline();
			n.printBlock(n.finallyBlock(), sw, tr);
		}
	}

	public void visit(Catch_c n) {
        X10CPPContext_c context = (X10CPPContext_c) tr.context();
		String excVar = context.getExceptionVar();
		sw.newline();
		sw.write("if (");
		String type = emitter.translateType(n.formal().type().type(), true);
		if (refsAsPointers) {
			sw.write("!!dynamic_cast" + chevrons(type) + "(" + excVar + ")");
		} else {
			sw.write("x10aux::instanceof" + chevrons(type) + "(" + excVar + ")");
		}
		sw.write(") {");
		sw.newline(4); sw.begin(0);
		n.printBlock(n.formal(), sw, tr);
		sw.write(" =");
		sw.allowBreak(2, " ");
		sw.write("static_cast<" + type + " >(" + excVar + ");");
		sw.newline(0);
		n.print(n.body(), sw, tr);
		sw.end(); sw.newline();
		sw.write("} else");
	}


	public void visit(Atomic_c a) {
        assert (false) : ("Atomic should have been desugared earlier");
	}

	public void visit(Await_c n) {
        assert (false) : ("Await should have been desugared earlier");
	}

	public void visit(Next_c n) {
        assert (false) : ("Next should have been desugared earlier");
	}


	public void visit(ForLoop_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10Formal form = (X10Formal) n.formal();

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		Expr domain = n.domain();
		Type dType = domain.type();
		if (Configuration.LOOP_OPTIMIZATIONS &&
		        form.hasExplodedVars() && form.isUnnamed() && xts.isPoint(form.type().type()) &&
		        (X10ArraysMixin.isRect(dType, context)))
		{
		    assert (xts.isPoint(form.type().type()));
		    assert (xts.isX10Array(dType) || xts.isDistribution(dType) || xts.isRegion(dType));

		    // TODO: move this to the Desugarer
		    X10NodeFactory xnf = (X10NodeFactory) tr.nodeFactory();
		    if (xts.isX10Array(dType)) {
		        Position pos = domain.position();
		        FieldInstance fDist = dType.toClass().fieldNamed(Name.make("dist"));
		        dType = fDist.type();
		        domain = xnf.Field(pos, domain, xnf.Id(pos, Name.make("dist"))).fieldInstance(fDist).type(dType);
		    }
		    if (xts.isDistribution(dType)) {
		        Position pos = domain.position();
		        FieldInstance fRegion = dType.toClass().fieldNamed(Name.make("region"));
		        dType = fRegion.type();
		        domain = xnf.Field(pos, domain, xnf.Id(pos, Name.make("region"))).fieldInstance(fRegion).type(dType);
		    }

		    sw.write("{");
		    sw.newline(4); sw.begin(0);

		    String dom = getId();
		    emitter.printType(dType, sw);
		    sw.write(" " + dom + " = ");
		    n.print(domain, sw, tr);
		    sw.write(";");
		    sw.newline();

		    LocalDef[] lis = form.localInstances();
		    List<Formal> vars = form.vars();
		    int rank = lis.length;
		    String[] limit = new String[rank];
		    for (int i = 0; i < rank; i++) {
		        LocalInstance f = lis[i].asInstance();
		        assert (f.type().isInt());
		        limit[i] = getId();
		        emitter.printType(f.type(), sw);
		        sw.write(" " + limit[i] + ";");
		        sw.newline();
		        emitter.printType(f.type(), sw);
		        sw.write(" ");
		        sw.write(mangled_non_method_name(f.name().toString()));
		        sw.write(";");
		        sw.newline();
		    }
		    for (int i = 0; i < rank; i++) {
		        LocalInstance f = lis[i].asInstance();
		        assert (f.type().isInt());
		        sw.write(limit[i] + " = " + dom + "->max(" + i + ");");
		        sw.newline();
		        sw.write("for (");
		        sw.write(mangled_non_method_name(f.name().toString()));
		        sw.write(" = " + dom + "->min(" + i + "); ");
		        sw.write(mangled_non_method_name(f.name().toString()));
		        sw.write(" <= " + limit[i] + "; ");
		        sw.write(mangled_non_method_name(f.name().toString()));
		        sw.write("++) {");
		        sw.newline(4); sw.begin(0);
		    }

		    form.addDecls(tr.context());
		    n.print(n.body(), sw, tr);

		    for (int i = 0; i < rank; i++) {
		        sw.end(); sw.newline();
		        sw.write("}");
		    }

		    sw.end(); sw.newline(0);
		    sw.write("}");
		    sw.newline(0);
		    return;
		}

        Type itType = null;
        assert (dType.isClass());
        X10ClassType domainType = (X10ClassType)dType.toClass();
        try {
            X10MethodInstance mi = xts.findMethod(domainType,
                                xts.MethodMatcher(domainType, Name.make("iterator"), Collections.EMPTY_LIST, context));
            assert (mi != null);
            assert (mi.returnType().isClass());
            List<Type> typeArgs = ((X10ClassType)mi.returnType()).typeArguments();
            assert (typeArgs.size() == 1);
            itType = typeArgs.get(0);
        } catch (SemanticException e) {
            assert (false) : e.getMessage();
        }

        sw.write("{");
		sw.newline(4); sw.begin(0);

		String name = "__i" + form.name();
		String itableName = name+"_itable";
		String iteratorType = emitter.translateType(xts.Iterator(form.type().type()), false);
		String iterableType = emitter.translateType(xts.Iterable(form.type().type()), false);
		String iterableTypeRef = emitter.translateType(xts.Iterable(form.type().type()), true);
		String iteratorTypeRef = emitter.translateType(xts.Iterator(form.type().type()), true);
		boolean doubleTemplate = ((X10ClassType)context.currentClass()).typeArguments().size() > 0;

		sw.write("x10aux::ref<x10::lang::Object> " + name + " = "+iteratorTypeRef);
		sw.write("(__extension__ ({ x10aux::ref<x10::lang::Object> _1 = x10aux::placeCheck(x10aux::nullCheck(");
		n.print(domain, sw, tr);
		sw.write(")); x10aux::GXX_ICE_Workaround"+chevrons(iteratorTypeRef)+"::_((_1.operator->()->*(x10aux::findITable"+chevrons(iterableType)+"(_1->_getITables())->iterator))()); }));"); sw.newline();
		sw.write((doubleTemplate ? "typename " : "")+iteratorType+"::"+(doubleTemplate ? "template ":"")+"itable<x10::lang::Object> *"+itableName+" = x10aux::findITable"+chevrons(iteratorType)+"("+name+"->_getITables());"); sw.newline();

		sw.write("for (");
		sw.begin(0);

		sw.write(";"); sw.allowBreak(2, " ");
		sw.write("("+name+".operator->()->*("+itableName+"->hasNext))();");
		sw.allowBreak(2, " ");

		sw.end();
		sw.write(") {");
		sw.newline(4); sw.begin(0);

		n.print(form, sw, tr);
		sw.write(";");
		sw.newline();
		sw.write(mangled_non_method_name(form.name().id().toString()));
        sw.write(" = ("+name+".operator->()->*("+itableName+"->next))();");
		sw.newline();
		for (Iterator li = n.locals().iterator(); li.hasNext(); ) {
			Stmt l = (Stmt) li.next();
			n.print(l, sw, tr);
		}

		handleLabeledLoop(n);

		sw.end(); sw.newline(0);
		sw.write("}");
		sw.newline(0);

		// [IP] It's always safe to free the iterator because it can't escape
		// [DC] It's not safe to free the iterator because it has been cast to an interface
		// FIXME: change the type of 'name' to be some kind of non-interface type
		//sw.write("x10aux::dealloc(" + name + ");");
		sw.newline();

		sw.end(); sw.newline(0);
		sw.write("}");
		sw.newline(0);
	}


    public void visit(ForEach_c n) {
        assert (false) : ("ForEach should have been desugared earlier");
    }

    public void visit(AtEach_c n) {
        assert (false) : ("AtEach should have been desugared earlier");
    }

    public void visit(Finish_c n) {
        assert (false) : ("Finish should have been desugared earlier");
    }


    public void visit(ArrayAccess_c n) {
        assert (false);
    }


    public void visit(ParExpr_c n) {
        n.print(n.expr(), sw, tr);
    }

    public void visit(Conditional_c n) {
        X10CPPContext_c context = (X10CPPContext_c) tr.context();
        n.printSubExpr(n.cond(), false, sw, tr);
        sw.unifiedBreak(2);
        sw.write("? ");
        sw.write("("+emitter.translateType(n.type(), true)+")(");
        sw.begin(0);
        n.printSubExpr(n.consequent(), true, sw, tr);
        sw.end();
        sw.write(")");
        sw.unifiedBreak(2);
        sw.write(": ");
        sw.write("("+emitter.translateType(n.type(), true)+")(");
        sw.begin(0);
        n.printSubExpr(n.alternative(), true, sw, tr);
        sw.end();
        sw.write(")");
    }


    public void visit(Here_c n) {
        assert (false) : ("Here should have been desugared earlier");
    }

    public void visit(Async_c n) {
        assert (false) : ("Async should have been desugared earlier");
    }


    public void visit(X10Special_c n) {
        X10CPPContext_c context = (X10CPPContext_c) tr.context();

        // inner classes have been removed
        // [NN] but qualifier may still be nonnull (== currentClass)
//        assert (n.qualifier() == null) :
//               n.qualifier()+" "+n.kind()+" "+n.position().nameAndLineString();

        if (n.kind().equals(X10Special_c.THIS)) {
            if (context.isInsideClosure()) {
                sw.write(SAVED_THIS);
                context.saveEnvVariableInfo(THIS);
            } else {
            	if (false && ((X10Type)n.type()).isX10Struct()) { // HACK: treating sturcts as values.
                    sw.write("("+n.kind()+")");
            	} else {
            		sw.write("(("+emitter.translateType(n.type(),true)+")"+n.kind()+")");
            	}
            }
        } else if (n.kind().equals(X10Special_c.SUPER)) {
            sw.write(emitter.translateType(context.currentClass().superClass()));
        } else if (n.isSelf()) {
            assert false: "I do not believe we ever visit over constraints.";
            // FIXME: Why are we printing the string "self"?
            // Confirm with Igor. [Krishna]
            //sw.write((context.Self() == null)? "self":context.Self());
        } else assert (false) : n.kind();

    }


    public static String getClosureName(String className, int id) {
        // TODO: factor out into a constant
        return className+"__closure__"+id;
    }

    public void visit(Closure_c n) {
        X10CPPContext_c c = (X10CPPContext_c) tr.context();

        emitter.enterClosure(c);

        ClosureDef closureDef = n.closureDef();
        CodeInstance ci = closureDef.methodContainer().get();
        X10ClassType hostClassType = (X10ClassType) closureDef.typeContainer().get();
        X10ClassDef hostClassDef = hostClassType.x10Def();
        X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();

        List<Type> freeTypeParams = new ArrayList<Type>();
        while (ci instanceof ClosureInstance)
            ci = ((ClosureDef) ci.def()).methodContainer().get();
        if (ci instanceof X10MethodInstance) {
            X10MethodInstance xmi = (X10MethodInstance) ci;
            // in X10, static methods do not inherit the template params of their classes
            if (!xmi.flags().isStatic())
                freeTypeParams.addAll(hostClassDef.typeParameters());
            freeTypeParams.addAll(xmi.typeParameters());
        } else if (ci instanceof InitializerInstance) {
            InitializerInstance ii = (InitializerInstance) ci;
            if (!ii.def().flags().isStatic())
                freeTypeParams.addAll(hostClassDef.typeParameters());
        } else {
            // could be a constructor or other non-static thing
            freeTypeParams.addAll(hostClassDef.typeParameters());
        }

        String hostClassName = emitter.translate_mangled_FQN(hostClassType.fullName().toString(), "_");

        c.setInsideClosure(true);

        int id = getConstructorId(c);

        String cname = getClosureName(hostClassName, id);

        boolean in_template_closure = false;

        StringBuffer cnamet_ = new StringBuffer(cname);
        String prefix = "<";
        for (Type t : freeTypeParams) {
            in_template_closure = true;
            cnamet_.append(prefix + emitter.translateType(t));
            prefix = ",";
        }
        if (in_template_closure) cnamet_.append(" >");
        String cnamet = cnamet_.toString();


        // create closure and packed arguments

        // Prepend this stream to closures.  Closures are created from the outside in.
        // Thus, later closures can be used by earlier ones, but not vice versa.
        ClassifiedStream inc_s = in_template_closure ?
                sw.getNewStream(StreamWrapper.Header, sw.header(), false) :
                sw.getNewStream(StreamWrapper.Closures, true);
        sw.pushCurrentStream(inc_s);

        StreamWrapper inc = sw;

        if (in_template_closure) {
            String guard = getHeaderGuard(cname);
            inc.write("#ifndef "+guard+"_CLOSURE"); inc.newline();
            inc.write("#define "+guard+"_CLOSURE"); inc.newline();
        }

        Type retType = n.returnType().type();
        //String className = emitter.translateType(c.currentClass());
        X10ClassType sup = (X10ClassType) xts.closureBaseInterfaceDef(0, n.formals().size(), retType.isVoid()).asType();
        List<Type> supArgs = new ArrayList<Type>();
        for (Formal formal : n.formals())
            supArgs.add(formal.type().typeRef().get());
        if (!retType.isVoid())
            supArgs.add(retType);
        String superType = emitter.translateType(sup.typeArguments(supArgs));
        String superTypeRef = emitter.translateType(sup.typeArguments(supArgs), true);

        // class header
        if (!freeTypeParams.isEmpty())
            emitter.printTemplateSignature(freeTypeParams, inc);
        inc.write("class "+cname+" : "); inc.begin(0);
        inc.write("public "+emitter.translateType(xts.Value()));
        inc.write("{") ; inc.end() ; inc.newline(4); inc.begin(0);
        inc.write("public:") ; inc.newline(); inc.forceNewline();

		/* ITables declarations */
        inc.write("static "+(in_template_closure ? "typename " : "")+superType+(in_template_closure ? "::template itable " : "::itable")+chevrons(cnamet)+" _itable;"); inc.newline();
		inc.write("static x10aux::itable_entry _itables[2];"); inc.newline(); inc.forceNewline();
		inc.write("virtual x10aux::itable_entry* _getITables() { return _itables; }"); inc.newline(); inc.forceNewline();

		inc.write("// closure body"); inc.newline();
        inc.write(emitter.translateType(retType, true)+" apply(");
        prefix = "";
        for (Formal formal : n.formals()) {
            inc.write(prefix);
            n.print(formal, inc, tr);
            prefix = ", ";
        }
        inc.write(") ");
        n.print(n.body(), inc, tr);
        inc.newline(); inc.forceNewline();

        inc.write("// captured environment"); inc.newline();
        emitter.printDeclarationList(inc, c, c.variables);
        inc.forceNewline();

        inc.write("x10aux::serialization_id_t "+SERIALIZE_ID_METHOD+"() {");
        inc.newline(4); inc.begin(0);
        inc.write("return "+SERIALIZATION_ID_FIELD+";"); inc.end(); inc.newline();
        inc.write("}"); inc.newline(); inc.forceNewline();

        inc.write("void "+SERIALIZE_BODY_METHOD+"("+SERIALIZATION_BUFFER+" &buf, x10aux::addr_map& m) {");
        inc.newline(4); inc.begin(0);
        // FIXME: factor out this loop
        for (int i = 0; i < c.variables.size(); i++) {
            if (i > 0) inc.newline();
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name = mangled_non_method_name(name);
            inc.write("buf.write(this->" + name + ", m);");
        }
        inc.end(); inc.newline();
        inc.write("}"); inc.newline(); inc.forceNewline();

        inc.write("template<class __T> static "+make_ref("__T")+" "+DESERIALIZE_METHOD+"("+DESERIALIZATION_BUFFER+" &buf) {");
        inc.newline(4); inc.begin(0);
        inc.write(make_ref(cnamet)+" this_ = new (x10aux::alloc"+chevrons(cnamet)+"()) "+
                  cnamet+"("+SERIALIZATION_MARKER+"());");
        inc.newline();
        // FIXME: factor out this loop
        for (int i = 0; i < c.variables.size(); i++) {
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name = mangled_non_method_name(name);
            inc.write("this_->"+name+" = buf.read<"+emitter.translateType(var.type(), true)+" >();");
            inc.newline();
        }
        inc.write("return this_;"); inc.end(); inc.newline();
        inc.write("}"); inc.newline(); inc.forceNewline();

        inc.write(cname+"("+SERIALIZATION_MARKER+") { }");
        inc.newline(); inc.forceNewline();

        inc.write(cname+"(");
        for (int i = 0; i < c.variables.size(); i++) {
            if (i > 0) inc.write(", ");
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name = mangled_non_method_name(name);
            inc.write(emitter.translateType(var.type(), true) + " " + name);
        }
        inc.write(") {");
        inc.newline(4); inc.begin(0);
        // FIXME: factor out this loop
        for (int i = 0 ; i < c.variables.size() ; i++) {
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name = mangled_non_method_name(name);
            if (i > 0) inc.newline();
            inc.write("this->" + name + " = " + name + ";");
        }
        inc.end(); inc.newline();
        inc.write("}"); inc.newline(); inc.forceNewline();

        inc.write("static const x10aux::serialization_id_t "+SERIALIZATION_ID_FIELD+";");
        inc.newline(); inc.forceNewline();

        inc.write("const x10aux::RuntimeType *_type() const {"+
                  " return x10aux::getRTT<"+superType+" >(); }");
        /*
        inc.newline();
        inc.write("static x10aux::RuntimeType * rtt;");
        */
        inc.newline(); inc.forceNewline();

        inc.write(emitter.translateType(xts.String(), true)+" toString() {");
        inc.newline(4); inc.begin(0);
        inc.write("return "+emitter.translateType(xts.String())+"::Lit(\""+StringUtil.escape(n.position().nameAndLineString())+"\");");
        inc.end(); inc.newline();
        inc.write("}");
        inc.end(); inc.newline(); inc.forceNewline();

        inc.write("};"); inc.newline(); inc.forceNewline();

        if (in_template_closure)
            emitter.printTemplateSignature(freeTypeParams, inc);
        inc.write((in_template_closure ? "typename ": "")+superType+(in_template_closure ? "::template itable ": "::itable")+chevrons(cnamet)+
        			cnamet+"::_itable(&"+cnamet+"::apply);");

        if (in_template_closure)
            emitter.printTemplateSignature(freeTypeParams, inc);
		inc.write("x10aux::itable_entry "+cnamet+"::_itables[2] = {");
		inc.write("x10aux::itable_entry(&"+superType+"::rtt, &"+cnamet+"::_itable),");
		inc.write("x10aux::itable_entry(NULL, NULL)};"); inc.newline();

        /*
        if (in_template_closure)
            emitter.printTemplateSignature(freeTypeParams, inc);
        inc.write("x10aux::RuntimeType * "+cnamet+"::rtt = const_cast<x10aux::RuntimeType *>(x10aux::getRTT<"+superType+" >());");
        inc.newline(); inc.forceNewline();
        */

        if (in_template_closure)
            emitter.printTemplateSignature(freeTypeParams, inc);
        inc.write("const x10aux::serialization_id_t "+cnamet+"::"+SERIALIZATION_ID_FIELD+" = ");
        inc.newline(4);
        String template = in_template_closure ? "template " : "";
        inc.write("x10aux::DeserializationDispatcher::addDeserializer("+
                  cnamet+"::"+template+DESERIALIZE_METHOD+
                  chevrons(emitter.translateType(xts.Object()))+");");
        inc.newline(); inc.forceNewline();

        if (in_template_closure) {
            String guard = getHeaderGuard(cname);
            inc.write("#endif // "+guard+"_CLOSURE"); inc.newline();
        }

        sw.popCurrentStream();

        // create closure instantiation (not in inc but where the closure was defined)
        // note that we alloc using the typeof the superType but we pass in the correct size
        // this is because otherwise alloc may (when debugging is on) try to examine the
        // RTT of the closure (which doesn't exist)

        // first get the template arguments (if any)
        prefix="<";
        StringBuffer sb = new StringBuffer();
        for (Type t : freeTypeParams) {
            sb.append(prefix+emitter.translateType(t, true));
            prefix = ",";
        }
        if (prefix.equals(",")) sb.append(">");
        String templateArgs = sb.toString();

        sw.write(make_ref(superType)+"("+make_ref(cnamet));
        sw.write("(new (x10aux::alloc"+chevrons(superType)+"(sizeof("+cname+templateArgs+")))");
        sw.write(cname+templateArgs+"(");
        for (int i = 0; i < c.variables.size(); i++) {
            if (i > 0) sw.write(", ");
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (!name.equals(THIS))
                name = mangled_non_method_name(name);
            else if (((X10CPPContext_c)c.pop()).isInsideClosure())  // FIXME: hack
                name = SAVED_THIS;
            sw.write(name);
        }
        sw.write(")))");

        c.finalizeClosureInstance();
        emitter.exitClosure(c);
    }


	private void printInlinedClosureStmt(Stmt s, StreamWrapper w, String retLabel, String retVar, Closure_c closure) {
	    if (s instanceof Block_c) {
	        w.write("{");
	        w.newline(4); w.begin(0);
	        for (Stmt b : ((Block_c)s).statements())
	            printInlinedClosureStmt(b, w, retLabel, retVar, closure);
	        w.end(); w.newline();
	        w.write("}");
	        return;
	    }
	    if (s instanceof Return_c) { // TODO
	        w.write("/"+"*"+" return from closure should go here "+"*"+"/");

	        Return_c r = (Return_c) s;
	        Expr e = r.expr();
	        e = cast(e, closure.returnType().type());
	        tr.print(null, e, w);
	        return;
	    }
	    tr.print(null, s, w);
	}

	private boolean inlineClosureCall(ClosureCall_c c, Closure_c closure, List<Expr> args) {
	    Type retType = closure.returnType().type();
	    List<Stmt> body = closure.body().statements();
	    // Special case: a single return statement
	    if (body.size() != 1 || !(body.get(0) instanceof Return_c)) // TODO
	        return false;

	    List<Formal> formals = closure.formals();
	    boolean clashes = false;
	    for (Expr a : c.arguments()) {
	        X10SearchVisitor xLocals = new X10SearchVisitor(X10Local_c.class);
	        a.visit(xLocals);
	        if (!xLocals.found())
	            continue;
	        ArrayList locals = xLocals.getMatches();
	        for (int i = 0; i < locals.size(); i++) {
	            X10Local_c t = (X10Local_c) locals.get(i);
	            Name name = t.localInstance().name();
	            for (Formal f : formals) {
	                if (f.name().id().equals(name))
	                    clashes = true;
	            }
	        }
	    }

	    {
	    	List<Expr> newArgs = new ArrayList<Expr>();
	    	int i =0;
	    	for (Expr a : args) {
	    		Type fType = formals.get(i).type().type();
	    		newArgs.add(cast(a, fType));
	    		i++;
	    	}
	    	args = newArgs;
	    }

	    sw.write("__extension__ ({");
	    sw.newline(4); sw.begin(0);
	    String[] alt = null;
	    if (clashes) {
	        alt = new String[args.size()];
	        int i = 0;
	        for (Expr a : args) {
	            alt[i] = getId();
	            Type fType = formals.get(i).type().type();
	            sw.write(emitter.translateType(fType, true)+" "+alt[i]+" =");
	            sw.allowBreak(2, " ");
	            c.print(a, sw, tr);
	            sw.write(";");
	            sw.newline();
	            i++;
	        }
	    }
	    // Enter the context of the body
	    X10CPPContext_c context = (X10CPPContext_c) tr.context();
	    X10CPPContext_c ctx = (X10CPPContext_c) closure.del().enterScope(context);
	    ((X10CPPTranslator)tr).setContext(ctx); // FIXME
	    int i = 0;
	    for (Expr a : args) {
	        Formal f = closure.formals().get(i);
	        c.print(f, sw, tr);
	        sw.write(" =");
	        sw.allowBreak(2, " ");
	        if (clashes)
	            sw.write(alt[i]);
	        else {
	            // Arguments are evaluated in the outside context
	            ((X10CPPTranslator)tr).setContext(context); // FIXME
	            c.print(a, sw, tr);
	            ((X10CPPTranslator)tr).setContext(ctx); // FIXME
	        }
	        sw.write(";");
	        sw.newline();
	        i++;
	    }
	    String ret = null;
	    if (!retType.isVoid()) {
	        ret = getId();
	        sw.begin(0);
	        emitter.printType(retType, sw);
	        sw.write(" ");
	        sw.write(ret);
	        sw.end();
	        sw.write(";");
	        sw.newline();
	    }
	    // Special case: a single return statement
	    if (body.size() == 1 && body.get(0) instanceof Return_c) {
	    	Return_c r = (Return_c) body.get(0);
	    	Expr e = r.expr();
	    	if (e != null) {
	    	    e = cast(e, retType);
	    	    sw.write(ret + " = ");
	    	    c.print(e, sw, tr);
	    	    sw.write(";");
	    	}
	    } else { // TODO
	        tr.job().compiler().errorQueue().enqueue(ErrorInfo.WARNING, "Inlining a multi-statement closure", c.position());
	        for (Stmt s : body) {
	            sw.newline();
	            printInlinedClosureStmt(s, sw, null, ret, closure);
	        }
	    }
	    if (!retType.isVoid()) {
	        sw.newline();
	        sw.write(ret);
	        X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
	        // Workaround for a g++ ICE (XTENLANG-461)
	        if (retType.isClass() &&
	            (xts.isSubtype(retType, xts.Ref(), tr.context()) ||
	             retType.toClass().flags().isInterface()))
	        {
	            sw.write(".operator->()");
	        }
	        sw.write(";");
	    }
	    ctx.finalizeClosureInstance();
	    ((X10CPPTranslator)tr).setContext(context); // FIXME
	    sw.end(); sw.newline();
	    sw.write("})"); sw.newline();
	    return true;
	}

	private Closure_c getClosureLiteral(Expr target) {
	    if (target instanceof Closure_c)
	        return (Closure_c) target;
	    if (target instanceof ParExpr_c)
	        return getClosureLiteral(((ParExpr_c)target).expr());
	    return null;
	}

	public void visit(ClosureCall_c c) {
		Expr target = c.target();

		X10MethodInstance mi = c.closureInstance();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tr.nodeFactory();
		List<Expr> args = new ArrayList<Expr>();
		int counter = 0;
		for (Expr a : c.arguments()) {
		    Type fType = mi.formalTypes().get(counter);
		    a = cast(a, fType);
		    args.add(a);
		    counter++;
		}

		// Optimization: if the target is a closure literal, inline the body
		Closure_c lit = getClosureLiteral(target);
		if (lit != null && inlineClosureCall(c, lit, args)) {
		    return;
		}

		// Can be a non-interface dispatch for classes like Future, so we have to check.
		Type t = target.type();
		String terminate = "";
		if (lit != null || (t.isClass() && t.toClass().flags().isInterface())) {
			sw.write("(__extension__ ({ x10aux::ref<x10::lang::Object> _ = x10aux::placeCheck(x10aux::nullCheck(");
			c.printSubExpr(target, sw, tr);
			sw.write(")); "+(mi.returnType().isVoid() ? "" : "x10aux::GXX_ICE_Workaround"+chevrons(emitter.translateType(mi.returnType(), true))+"::_")+"((_.operator->()->*(x10aux::findITable"+chevrons(emitter.translateType(target.type(), false))+"(_->_getITables())->apply))(");;
			terminate = ");}))";
		} else {
			sw.write("x10aux::placeCheck(x10aux::nullCheck(");
			c.printSubExpr(target, sw, tr);
			sw.write("))->apply(");
		}
		
		sw.begin(0);
		List l = args;
		boolean first = true;
		for (Iterator i = l.iterator(); i.hasNext(); ) {
			Expr e = (Expr) i.next();
			if (!first) sw.write(",");
			sw.allowBreak(0, " ");
			c.print(e, sw, tr);
			first = false;
		}
		sw.end();
		sw.write(")"+terminate);
	}


	public void visit(X10CanonicalTypeNode_c n) {
		Type t = n.type();
		if (t == null)
			throw new InternalCompilerError("Unknown type");
		sw.write(emitter.translateType(t));
	}


	public void visit(X10Unary_c n) {
	    X10CPPContext_c context = (X10CPPContext_c) tr.context();

	    Expr left = n.expr();
	    Type l = left.type();
	    X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
	    NodeFactory nf = tr.nodeFactory();
	    Unary.Operator op = n.operator();

	    if (op == Unary.POST_DEC || op == Unary.POST_INC || op == Unary.PRE_DEC || op == Unary.PRE_INC) { // TODO
	        visit((Unary_c)n);
	        return;
	    }
	    if (l.isNumeric()) { // TODO: get rid of this special case by defining native operators
	        visit((Unary_c)n);
	        return;
	    }
	    if (l.isBoolean()) { // TODO: get rid of this special case by defining native operators
	        visit((Unary_c)n);
	        return;
	    }
	    assert (false) : ("User-defined unary operators should have been desugared earier");

	    // FIXME: move this to the Desugarer
	    Name methodName = X10Unary_c.unaryMethodName(op);
	    Expr receiver = left;

	    if (methodName == null)
	        throw new InternalCompilerError("No method to implement " + n, n.position());

	    try {
	        List<Type> types = Arrays.asList(new Type[] { });
	        MethodInstance mi = xts.findMethod(receiver.type(),
	                xts.MethodMatcher(receiver.type(), methodName, types, context));
	        List<Expr> args = Arrays.asList(new Expr[] { });
	        n.print(nf.Call(n.position(), receiver, nf.Id(n.position(), methodName),
	                args).methodInstance(mi).type(mi.returnType()), sw, tr);
	    } catch (SemanticException e) { }
	}

	public void visit(Unary_c n) {
		Unary_c.Operator operator = n.operator();
		Expr expr = n.expr();
		if (operator == Unary_c.NEG && expr instanceof IntLit) {
		    IntLit_c lit = (IntLit_c) expr;
		    IntLit.Kind kind = (lit.kind() == X10IntLit_c.UINT) ? IntLit.INT : ((lit.kind() == X10IntLit_c.ULONG) ? IntLit.LONG : lit.kind());
		    n.printSubExpr(lit.value(-lit.longValue()).kind(kind), true, sw, tr);
		}
		else if (operator.isPrefix()) {
			sw.write(operator.toString());
			n.printSubExpr(expr, false, sw, tr);
		}
		else {
			n.printSubExpr(expr, false, sw, tr);
			sw.write(operator.toString());
		}
	}


	public void visit(X10Binary_c n) {
	    X10CPPContext_c context = (X10CPPContext_c) tr.context();

	    Expr left = n.left();
	    Type l = left.type();
	    Expr right = n.right();
	    Type r = right.type();
	    X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
	    NodeFactory nf = tr.nodeFactory();
	    Binary.Operator op = n.operator();

	    if (op == Binary.EQ || op == Binary.NE) { // FIXME: get rid of this special case
	        sw.write("("); sw.begin(0);
	        Type c = null;
	        if (l.isNumeric() && r.isNumeric()) {
	            try {
	                c = xts.promote(l, r);
	            } catch (SemanticException e) { assert (false); }
	        }
	        if (op == Binary.NE)
	            sw.write("!");
	        sw.write(STRUCT_EQUALS+"("); sw.begin(0);
	        if (c != null && !xts.isParameterType(c) && !xts.typeEquals(c, l, context))
	            sw.write("(" + emitter.translateType(c, true) + ")");
	        n.print(left, sw, tr);
	        sw.write(",");
	        sw.allowBreak(0, " ");
	        if (c != null && !xts.isParameterType(c) && !xts.typeEquals(c, r, context))
	            sw.write("(" + emitter.translateType(c, true) + ")");
	        n.print(right, sw, tr);
	        sw.end(); sw.write(")");
	        sw.end(); sw.write(")");
	        return;
	    }
	    if (l.isNumeric() && r.isNumeric()) { // TODO: get rid of this special case by defining native operators
	        visit((Binary_c)n);
	        return;
	    }
	    if (l.isBoolean() && r.isBoolean()) { // TODO: get rid of this special case by defining native operators
	        visit((Binary_c)n);
	        return;
	    }
	    if (op == Binary.ADD && (l.isSubtype(xts.String(), context) || r.isSubtype(xts.String(), context))) { // TODO: get rid of this special case by defining native operators
	        visit((Binary_c)n);
	        return;
	    }
	    assert (false) : ("User-defined binary operators should have been desugared earier");

	    // FIXME: move this to the Desugarer
	    boolean inv = n.invert();
	    Name methodName = inv ? X10Binary_c.invBinaryMethodName(op) : X10Binary_c.binaryMethodName(op);
	    Expr receiver = inv ? right : left;
	    Expr arg = inv ? left : right;

	    if (methodName == null)
	        throw new InternalCompilerError("No method to implement " + n, n.position());

	    try {
	        List<Type> types = Arrays.asList(new Type[] { arg.type() });
	        MethodInstance mi = xts.findMethod(receiver.type(),
	                xts.MethodMatcher(receiver.type(), methodName, types, context));
	        List<Expr> args = Arrays.asList(new Expr[] { arg });
	        n.print(nf.Call(n.position(), receiver, nf.Id(n.position(), methodName),
	                args).methodInstance(mi).type(mi.returnType()), sw, tr);
	    } catch (SemanticException e) { }
	}

	public void visit(Binary_c n) {
		// FIXME Check if there needs to be explicit handling of operators polyglot.ast.Binary.EQ and polyglot.ast.Binary.NE
		// for Reference Type arguments here as in X10PrettyPrinter.java

		boolean unsigned_op = false;
		String opString = n.operator().toString();

		if (opString.equals(">>>")) {
			unsigned_op = true;
			opString = opString.substring(1);
		}
		if (opString.equals("%") && (n.type().isFloat() || n.type().isDouble())) {
		    // [IP] Float and double modulus have to be treated specially in C++
		    assert (!unsigned_op);
		    sw.write("x10aux::mod("); sw.begin(0);
		    n.printSubExpr(n.left(), false, sw, tr);
		    sw.write(",");
		    sw.allowBreak(0, " ");
		    n.printSubExpr(n.right(), false, sw, tr);
		    sw.end(); sw.write(")");
		    return;
		}

		if (unsigned_op) {
			sw.write("("+emitter.translateType(n.type())+")(");
			sw.write("(("+emitter.makeUnsignedType(n.left().type())+")");
		}
		n.printSubExpr(n.left(), true, sw, tr);
		if (unsigned_op)
			sw.write(")");
		sw.write(" ");
		sw.write(opString);
		sw.allowBreak(n.type() == null || n.type().isPrimitive() ? 2 : 0, " ");
		if (unsigned_op)
			sw.write("(("+emitter.makeUnsignedType(n.left().type())+")");
		n.printSubExpr(n.right(), false, sw, tr);
		if (unsigned_op)
			sw.write("))");
	}



	public void visit(ArrayInit_c n) {
		throw new InternalCompilerError("Should not be invoked");
	}


	public void visit(SettableAssign_c n) {
	    assert (false) : ("Function assign should have been desugared earlier");
	}

    // allow overriding in subclasses
    // [DC] FIXME: ASTQuery.getCppRepParam still uses CPP_NATIVE_STRING directly
    protected String[] getCurrentNativeStrings() { return new String[] {CPP_NATIVE_STRING}; }

	private String getCppImplForDef(X10Def o) {
	    X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
	    try {
	        Type annotation = (Type) xts.systemResolver().find(QName.make("x10.compiler.Native"));
            String[] our_langs = getCurrentNativeStrings();
            for (String our_lang : our_langs) {
    	        List<Type> as = o.annotationsMatching(annotation);
    	        for (Type at : as) {
    	            assertNumberOfInitializers(at, 2);
    	            String lang = getPropertyInit(at, 0);
    	            if (lang != null && lang.equals(our_lang)) {
    	                String lit = getPropertyInit(at, 1);
    	                return lit;
    	            }
    	        }
            }
	    }
	    catch (SemanticException e) {}
	    return null;
	}


    // FIXME: generic native methods will break
	private void emitNativeAnnotation(String pat, List<Type> types, Receiver receiver, List<Expr> args) {
		 Object[] components = new Object[1+3*types.size() + args.size()];
		 assert (receiver != null);
         components[0] = receiver;
         if (receiver instanceof X10Special_c && ((X10Special_c)receiver).kind() == X10Special_c.SUPER)
             pat = pat.replaceAll("\\(#0\\)->", "#0::"); // FIXME: HACK

         int i = 1;
		 for (Type at : types) {
			 components[i++] = at;
			 components[i++] = "/"+"*"+" UNUSED "+"*"+"/";
			 components[i++] = "/"+"*"+" UNUSED "+"*"+"/";
		 }
		 for (Expr e : args) {
			 components[i++] = e;
		 }
		 emitter.dumpRegex("Native", components, tr, pat, sw);
	}

	public void visit(Tuple_c c) {
		// Handles Rails initializer.
		Type T = X10TypeMixin.getParameterType(c.type(), 0);
		String type = emitter.translateType(c.type());
		// [DC] this cast is needed to ensure everything has a ref type
		// otherwise overloads don't seem to work properly
		sw.write("("+make_ref(type)+")");
		sw.write("x10aux::alloc_rail<");
		emitter.printType(T, sw);
		sw.write(",");
		sw.allowBreak(0, " ");
		sw.write(emitter.translateType(c.type()));
		sw.write(" >("+c.arguments().size());
		for (Expr e : c.arguments()) {
			sw.write(",");
			c.printSubExpr(e, false, sw, tr);
		}
		sw.write(")");
	}

	public void visit(When_c n) {
        assert (false) : ("When should have been desugared earlier");
	}

    public void visit(Future_c n) {
        assert (false) : ("Future should have been desugared earlier");
    }

    public void visit(AtStmt_c n) {
        assert (false) : ("At statements are deprecated");
    }

    public void visit(AtExpr_c n) {
        assert (false) : ("At expression should have been desugared earlier");
    }

} // end of MessagePassingCodeGenerator
// vim:tabstop=4:shiftwidth=4:expandtab
