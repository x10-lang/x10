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
package polyglot.ext.x10cpp.visit;

import static polyglot.ext.x10cpp.visit.ASTQuery.assertNumberOfInitializers;
import static polyglot.ext.x10cpp.visit.ASTQuery.getConstructorId;
import static polyglot.ext.x10cpp.visit.ASTQuery.getCppRep;
import static polyglot.ext.x10cpp.visit.ASTQuery.getPropertyInit;
import static polyglot.ext.x10cpp.visit.Emitter.mangled_field_name;
import static polyglot.ext.x10cpp.visit.Emitter.mangled_method_name;
import static polyglot.ext.x10cpp.visit.Emitter.mangled_non_method_name;
import static polyglot.ext.x10cpp.visit.Emitter.translateFQN;
import static polyglot.ext.x10cpp.visit.Emitter.translate_mangled_FQN;
import static polyglot.ext.x10cpp.visit.Emitter.translate_mangled_NSFQN;
import static polyglot.ext.x10cpp.visit.Emitter.voidTemplateInstantiation;
import static polyglot.ext.x10cpp.visit.SharedVarsMethods.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import polyglot.ast.AmbReceiver;
import polyglot.ast.ArrayAccess_c;
import polyglot.ast.ArrayInit_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Call_c;
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
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.ConstructorDecl;
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
import polyglot.ast.Lit_c;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.Loop_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.NewArray_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit_c;
import polyglot.ast.NumLit_c;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Receiver;
import polyglot.ast.Return_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Term_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ast.AssignPropertyBody_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.AtExpr_c;
import polyglot.ext.x10.ast.AtStmt_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.ClosureCall_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.ConstantDistMaker_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Future_c;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.ParExpr_c;
import polyglot.ext.x10.ast.PropertyDecl_c;
import polyglot.ext.x10.ast.RegionMaker_c;
import polyglot.ext.x10.ast.SettableAssign_c;
import polyglot.ext.x10.ast.StmtSeq_c;
import polyglot.ext.x10.ast.Tuple_c;
import polyglot.ext.x10.ast.TypeDecl_c;
import polyglot.ext.x10.ast.TypeDecl;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.When_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode;
import polyglot.ext.x10.ast.X10CanonicalTypeNode_c;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClassDecl;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.ast.X10ClockedLoop_c;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10Special_c;
import polyglot.ext.x10.extension.X10Ext_c;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.ClosureDef;
import polyglot.ext.x10.types.ClosureInstance;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Def;
import polyglot.ext.x10.types.X10FieldDef;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;

import polyglot.ext.x10.visit.X10DelegatingVisitor;
import polyglot.ext.x10cpp.extension.X10ClassBodyExt_c;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.ext.x10cpp.visit.X10CPPTranslator.DelegateTargetFactory;
import polyglot.ext.x10cpp.visit.X10SummarizingRules.X10SummarizingPass;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.NoMemberException;
import polyglot.types.Package;
import polyglot.types.Package_c;
import polyglot.types.ParsedClassType;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Ref_c;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.util.TypedList;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;

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

	private final StreamWrapper sw;
	private final Translator tr;
	private XCDProcessor xcdProcessor;

	Emitter emitter;
	ASTQuery query;
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
        // FIXME: I think we need to put a typedef for a TypeDecl.
        // verify. [Krishna]
    }

	public void visit(X10ClassDecl_c n) {
		processClass(n);
	}

	private boolean extractGenericStaticDecls(X10ClassDef cd, ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (context.pendingStaticDecls.size() == 0)
			return false;
		boolean hasInits = false;
		w.write("template <> class ");
		w.write(mangled_non_method_name(cd.name().toString()));
		w.write(voidTemplateInstantiation(cd.typeParameters().size()));
		w.allowBreak(0, " ");
		w.write("{");
		w.newline(4); w.begin(0);
		// First process all classes
		for (ClassMember dec : context.pendingStaticDecls) {
			if (dec instanceof ClassDecl_c) {
				ClassDecl_c cdecl = (ClassDecl_c) dec;
				((X10CPPTranslator)tr).setContext(cdecl.enterScope(context)); // FIXME
				X10ClassDef def = (X10ClassDef) cdecl.classDef();
				if (getCppRep(def, tr) != null) {
					// emit no c++ code as this is a native rep class
					continue;
				}
				emitter.printFlags(w, cdecl.flags().flags());
				emitter.printTemplateSignature(((X10ClassType)def.asType()).typeArguments(), w);
				w.write("class ");
				w.write(Emitter.mangled_non_method_name(cdecl.name().id().toString()));
				w.write(";");
				((X10CPPTranslator)tr).setContext(context); // FIXME
				w.newline();
			}
		}
		// Then process all fields and methods
		for (ClassMember dec : context.pendingStaticDecls) {
			if (dec instanceof FieldDecl_c) {
				FieldDecl_c fd = (FieldDecl_c) dec;
				((X10CPPTranslator)tr).setContext(fd.enterScope(context)); // FIXME
				sw.pushCurrentStream(w);
				emitter.printHeader(fd, w, tr, false);
				sw.popCurrentStream();
				w.write(";");
                // [DC] want these to occur in the static initialiser
                // [IP] except for the ones that use a literal init - otherwise switch is broken
				if (fd.init() != null &&
						!(fd.flags().flags().isStatic() && fd.flags().flags().isFinal() &&
								(fd.init() instanceof NumLit_c || fd.init() instanceof BooleanLit_c)))
				{
					hasInits = true;
				}
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
			w.newline();
		}
		if (hasInits) {
			w.write("public : static " + VOID + " " + STATIC_INIT + "();");
			w.newline();
		}
		w.end(); w.newline();
		w.write("};");
		w.newline();
		return true;
	}

	private void extractGenericStaticInits(X10ClassDef cd) {
        // Always write non-template static decls into the implementation file
	    ClassifiedStream save_w = sw.currentStream();
        ClassifiedStream w = sw.getNewStream(StreamWrapper.StreamClass.CC, false);
        sw.pushCurrentStream(w);
        String header = getHeader(cd.asType());
        w.write("#include <"+header+">"); w.newline();
        w.forceNewline(0);

		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		ArrayList<FieldDecl_c> inits = new ArrayList<FieldDecl_c>();
		String container = translate_mangled_FQN(cd.fullName().toString())+voidTemplateInstantiation(cd.typeParameters().size());
		for (ClassMember dec : context.pendingStaticDecls) {
			if (dec instanceof FieldDecl_c) {
				FieldDecl_c fd = (FieldDecl_c) dec;
				((X10CPPTranslator)tr).setContext(fd.enterScope(context)); // FIXME
				emitter.printType(fd.type().type(), sw);
				sw.allowBreak(2, " ");
				sw.write(container+"::");
				sw.write(mangled_field_name(fd.name().id().toString()));
				if (fd.init() != null) {
				    // [DC] want these to occur in the static initialiser instead
                    // [IP] except for the ones that use a literal init - otherwise switch is broken
					if (fd.flags().flags().isStatic() && fd.flags().flags().isFinal() &&
                            (fd.init() instanceof NumLit_c || fd.init() instanceof BooleanLit_c))
					{
						sw.write(" =");
						sw.allowBreak(2, " ");
						fd.print(fd.init(), sw, tr);
					} else
						inits.add(fd);
				}
				sw.write(";");
                sw.newline();
				((X10CPPTranslator)tr).setContext(context); // FIXME
			} else if (dec instanceof MethodDecl_c) {
				MethodDecl_c md = (MethodDecl_c) dec;
                boolean templateMethod = ((X10MethodDef)md.methodDef()).typeParameters().size() != 0;
                if (templateMethod)
                    sw.pushCurrentStream(save_w);
				((X10CPPTranslator)tr).setContext(md.enterScope(context)); // FIXME

				emitter.printTemplateSignature(((X10ClassType)md.methodDef().container().get()).typeArguments(), sw);
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
				X10ClassDecl_c cdecl = (X10ClassDecl_c) dec;
				((X10CPPTranslator)tr).setContext(cdecl.enterScope(context)); // FIXME

				X10ClassDef def = (X10ClassDef) cdecl.classDef();
				if (getCppRep(def, tr) != null) {
					// emit no c++ code as this is a native rep class
					continue;
				}
				ClassifiedStream h = sw.header();
				emitter.printTemplateSignature(((X10ClassType)def.asType()).typeArguments(), h);
				h.write("class "+container+"::"+Emitter.mangled_non_method_name(def.name().toString()));
				h.allowBreak(0, " ");
				// [DC]: the following function adds the : public B, public C etc
				emitter.printInheritance(cdecl, h ,tr);
				h.allowBreak(0, " ");

				context.setinsideClosure(false);
				context.hasInits = false;

				assert (def.isNested());
				assert (false) : ("Nested class alert!");
				if (!def.flags().isStatic())
					throw new InternalCompilerError("Instance Inner classes not supported");

				ArrayList<ClassMember> opsd = context.pendingStaticDecls;
				context.pendingStaticDecls = new ArrayList<ClassMember>();

				cdecl.print(cdecl.body(), sw, tr);

				processNestedClasses(cdecl);

				if (extractGenericStaticDecls((X10ClassDef)cdecl.classDef(), h)) {
					extractGenericStaticInits((X10ClassDef)cdecl.classDef());
				}

				context.pendingStaticDecls = opsd;

				h.newline();

				ArrayList asyncs = context.closures.asyncs;
				if (context.classesWithAsyncSwitches.size() != 0) {
                    ClassifiedStream inc = sw.getNewStream(StreamWrapper.StreamClass.Closures, false);
					emitter.printSwitchMethod(cdecl.classDef().asType(), ASYNC_SWITCH, VOID,
							ASYNC_PREFIX, asyncs, context.closures.asyncsParameters,
							context.closures.asyncContainers,
							"int niter",
							"for (int i = 0; i < niter; i++,_arg++) {", "}",
							context.classesWithAsyncSwitches, inc);
					emitter.printAsyncsRegistration(cdecl.classDef().asType(), asyncs, inc);
				}
				if (context.closures.arrayCopyClosures.size() != 0) {
                    ClassifiedStream inc = sw.getNewStream(StreamWrapper.StreamClass.Closures, false);
					emitter.printSwitchMethod(cdecl.classDef().asType(), ARRAY_COPY_SWITCH, VOID_PTR,
							ARRAY_COPY_PREFIX, asyncs, context.closures.asyncsParameters,
							context.closures.arrayCopyClosures,
							null,
							null, null,
							context.classesWithArrayCopySwitches, inc);
                }
                sw.newline();

				((X10CPPTranslator)tr).setContext(context); // FIXME
			}
		}
		if (inits.size() > 0) {
			sw.write(VOID + " " + container + "::" + STATIC_INIT + "() {");
			sw.newline(4); sw.begin(0);
			sw.write("static bool done = false;"); sw.newline();
			sw.write("if (done) return;"); sw.newline();
			sw.write("done = true;"); sw.newline();
			sw.write("_I_(\"Doing static initialisation for class: "+container+"\");"); sw.newline();
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
			sw.write("static " + VOID_PTR + " __init__"+getUniqueId_() +" = x10aux::InitDispatcher::addInitializer(" + container + "::" + STATIC_INIT + ")"+ ";");
			sw.newline(); sw.forceNewline(0);
		}

		sw.popCurrentStream();
	}

	private boolean extractInits(X10ClassType currentClass, String methodName,
			String retType, List members, boolean staticInits)
	{
		String className = emitter.translateType(currentClass);
		boolean sawInit = false;
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
					if (dec.init() != null && dec.flags().flags().isFinal() &&
					        (dec.init() instanceof NumLit_c || dec.init() instanceof BooleanLit_c))
					    continue;
				}
			}
			if (!sawInit) {
				emitter.printTemplateSignature(currentClass.typeArguments(), sw);
				sw.write(retType + " " + className + "::" + methodName + "() {");
				sw.newline(4); sw.begin(0);
                if (staticInits) {
                    sw.write("static bool done = false;"); sw.newline();
                    sw.write("if (done) return;"); sw.newline();
                    sw.write("done = true;"); sw.newline();
                    sw.write("_I_(\"Doing static initialisation for class: "+className+"\");"); sw.newline();
                } else {
                    sw.write("_I_(\"Doing initialisation for class: "+className+"\");"); sw.newline();
                }
				sawInit = true;
			}
			if (member instanceof Initializer_c) {
				Initializer_c init = (Initializer_c) member;
				init.printBlock(init.body(), sw, tr);
				sw.newline(0);
			} else if (member instanceof FieldDecl_c) {
				FieldDecl_c dec = (FieldDecl_c) member;
				Term_c init = (Term_c) dec.init();
				assert (init != null);
				sw.write(mangled_field_name(dec.name().id().toString()));
				sw.write(" = ");
				dec.print(init, sw, tr);
				sw.write(";");
				sw.newline();
			}
		}
		if (sawInit) {
			if (!retType.equals(VOID))
				sw.write("return ("+retType+")0;");
			sw.end(); sw.newline();
			sw.write("}");
			sw.newline(); sw.forceNewline(0);
		}
		return sawInit;
	}
	boolean hasExternMethods(List members) {
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

	private void processNestedClasses(X10ClassDecl_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		boolean generic = ((X10ClassDef)n.classDef()).typeParameters().size() != 0;
		for (Iterator i = n.body().members().iterator(); i.hasNext(); ) {
			ClassMember member = (ClassMember) i.next();
			if (member instanceof ClassDecl_c) {
				X10ClassDecl_c cd = (X10ClassDecl_c) member;
				if (generic && cd.flags().flags().isStatic()) {
					context.pendingStaticDecls.add(cd);
					continue;
				}
				processClass(cd);
			}
		}
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
        if (cd.typeParameters().size() > 0) {
            h.write("template <");
            boolean first = true;
            for (ParameterType pt : cd.typeParameters()) {
                if (first)
                    first = false;
                else {
                    h.write(",");
                    h.allowBreak(4, " ");
                }
                h.write("class ");
                h.write(emitter.translateType(pt));
            }
            h.write(">");
            h.allowBreak(2, " ");
        }
        h.write("class "+Emitter.mangled_non_method_name(cd.name().toString())+";");
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
        String header = tf.outputHeaderName(pkg, ct.name().toString());
        return header;
    }

    private String getHeaderGuard(String header) {
        return header.replace('/','_').replace('.','_').replace('$','_').toUpperCase();
    }

    void processClass(X10ClassDecl_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

        assert (!context.inLocalClass()) : ("Local classes should have been removed earlier");

		X10ClassDef def = (X10ClassDef) n.classDef();

		if (getCppRep(def, tr) != null) {
			// emit no c++ code as this is a native rep class
			return;
		}

        assert (!def.isNested()) : ("Nested class alert!");

        ClassifiedStream save_body = sw.body();
        ClassifiedStream save_header = sw.header();
        // Header stream
        ClassifiedStream h = sw.getNewStream(StreamWrapper.StreamClass.Header, false);
        StreamWrapper.StreamClass impl = StreamWrapper.StreamClass.CC;
        if (def.typeParameters().size() != 0)
            impl = StreamWrapper.StreamClass.Header;
        // Implementation stream (may be after the header)
        ClassifiedStream w = sw.getNewStream(impl, false);
        // Dependences guard closing stream (comes at the end of the header)
        ClassifiedStream z = sw.getNewStream(StreamWrapper.StreamClass.Header, false);
        sw.set(h, w);

        context.setinsideClosure(false);
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
        
        boolean inTemplate = def.typeParameters().size() != 0;
        if (inTemplate) {
            w.write("#ifndef "+cguard+"_IMPLEMENTATION"); w.newline();
            w.write("#define "+cguard+"_IMPLEMENTATION"); w.newline();
        }
        
        w.write("#include <"+cheader+">"); w.newline();
        w.forceNewline(0);
        w.forceNewline(0);
        
        if (context.package_() != null) {
            w.write("using namespace ");
            w.write(Emitter.translateFQN(context.package_().fullName().toString()));
            w.write(";");
            w.newline();
        }
        
        String pkg = "";
        if (context.package_() != null)
            pkg = context.package_().fullName().toString();
        String incfile = tf.integratedOutputName(pkg, n.name().toString(), StreamWrapper.StreamClass.Closures.toString());
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
		    String cpp = getCppRep(scd, tr);
		    if (scd != def && cpp == null) {
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
		        if (cd != def && getCppRep(cd, tr) == null) {
		            declareClass(cd, h);
		            allIncludes.add(t);
		        }
		    }
		}
		for (TypeNode i : n.interfaces()) {
		    ClassType ct = i.type().toClass();
		    X10ClassDef icd = (X10ClassDef) ct.def();
		    String cpp = getCppRep(icd, tr);
		    if (icd != def && cpp == null) {
		        while (ct.isNested())
		            ct = (ClassType) ct.container();
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
		        if (cd != def && getCppRep(cd, tr) == null) {
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
		X10SearchVisitor xTypes = new X10SearchVisitor(X10CanonicalTypeNode_c.class);
		n.visit(xTypes);
		if (xTypes.found()) {
		    ArrayList typeNodes = xTypes.getMatches();
		    ArrayList<ClassType> types = new ArrayList<ClassType>();
		    Set<ClassType> dupes = new HashSet<ClassType>();
		    dupes.add(def.asType());
		    for (int i = 0; i < typeNodes.size(); i++) {
		        X10CanonicalTypeNode_c t = (X10CanonicalTypeNode_c) typeNodes.get(i);
		        Type type = t.type();
		        extractAllClassTypes(type, types, dupes);
		    }
		    for (ClassType ct : types) {
		        X10ClassDef cd = (X10ClassDef) ct.def();
		        if (cd == def)
		            continue;
		        String cpp = getCppRep(cd, tr);
		        if (cpp != null)
		            continue;
		        if (cd.isNested()) {
		            assert (false) : (n.position().nameAndLineString()+" Nested class alert!");
		            continue;
		        }
		        if (!allIncludes.contains(ct)) {
		            declareClass(cd, h);
		            allIncludes.add(ct);
		        }
		    }

		    ArrayList<String> nsHistory = new ArrayList<String>();
		    for (Iterator is = context.pendingImports.iterator(); is.hasNext();) {
		        Import_c in = (Import_c) is.next();
		        QName nsName = in.name();
		        if (in.kind() == Import_c.CLASS) {
		            // [DC] couldn't we define a local typedef to represent this x10 concept?
		            nsName = nsName.qualifier();
		        } else {
		            assert (in.kind() == Import_c.PACKAGE);
		        }
		        emitter.emitUniqueNS(nsName, nsHistory, h);
		        h.newline();
		    }
		    context.pendingImports.clear();  // Just processed all imports - clean up
		    QName x10_lang = xts.Object().toClass().fullName().qualifier();
		    emitter.emitUniqueNS(x10_lang, nsHistory, h);
		    h.newline();
		}
		h.forceNewline(0);
		if (def.package_() != null) {
		    QName pkgName = def.package_().get().fullName();
		    Emitter.openNamespaces(h, pkgName);
		    h.newline(0);
		    h.forceNewline(0);
		    Emitter.openNamespaces(w, pkgName);
		    w.newline(0);
		    w.forceNewline(0);
		}

		ArrayList<ClassMember> opsd = context.pendingStaticDecls;
		context.pendingStaticDecls = new ArrayList<ClassMember>();

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

		context.classProperties.addAll(n.properties());

		n.print(n.body(), sw, tr);

        ((X10CPPTranslator)tr).setContext(n.enterChildScope(n.body(), context)); // FIXME
        /*
         * TODO: [IP] Add comment about dependences between the method calls.
         */
        processNestedClasses(n);

        if (extractGenericStaticDecls(def, h)) {
            extractGenericStaticInits(def);
        }
        ((X10CPPTranslator)tr).setContext(context); // FIXME

		context.pendingStaticDecls = opsd;

		h.newline();

		ArrayList asyncs = context.closures.asyncs;
		if (context.classesWithAsyncSwitches.size() != 0) {
            ClassifiedStream inc = sw.getNewStream(StreamWrapper.StreamClass.Closures, false);
			emitter.printSwitchMethod(def.asType(), ASYNC_SWITCH, VOID,
					ASYNC_PREFIX, asyncs, context.closures.asyncsParameters,
					context.closures.asyncContainers,
					"int niter",
					"for (int i = 0; i < niter; i++,_arg++) {", "}",
					context.classesWithAsyncSwitches, inc);
			emitter.printAsyncsRegistration(def.asType(), asyncs, inc);
		}
		if (context.closures.arrayCopyClosures.size() != 0) {
            ClassifiedStream inc = sw.getNewStream(StreamWrapper.StreamClass.Closures, false);
			emitter.printSwitchMethod(def.asType(), ARRAY_COPY_SWITCH, VOID_PTR,
					ARRAY_COPY_PREFIX, asyncs, context.closures.asyncsParameters,
					context.closures.arrayCopyClosures,
					null,
					null, null,
					context.classesWithArrayCopySwitches, inc);
        }

        // Write the footer for the class
		if (def.package_() != null) {
		    QName pkgName = def.package_().get().fullName();
		    h.newline(0);
		    h.forceNewline(0);
		    Emitter.closeNamespaces(h, pkgName);
		    h.newline(0);
		    w.newline(0);
		    w.forceNewline(0);
		    Emitter.closeNamespaces(w, pkgName);
		    w.newline(0);
		}
		h.write("#endif // " + cguard); h.newline(0);
        h.forceNewline(0);

        if (inTemplate) {
            w.write("#endif // "+cguard+"_IMPLEMENTATION"); w.newline();
        }

        // The declarations below are intentionally outside of the guard
        if (context.package_() != null) {
            QName qn = context.package_().fullName();
            Emitter.openNamespaces(h, qn);
            h.newline(0);
        }
        if (n.typeParameters().size() > 0) {
            h.write("template <");
            String sep = "";
            for (TypeParamNode tn : n.typeParameters()) {
                h.write(sep);
                sep = ", ";
                h.write("class ");
                h.write(tn.name().toString());
            }
            h.write("> ");
        }
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
            String cpp = getCppRep((X10ClassDef) ct.def(), tr);
            assert (cpp == null);
            assert (!ct.isNested());
            String header = getHeader(ct);
            h.write("#include <" + header + ">");
            h.newline();
        }

        z.write("#endif // __"+cguard+"_NODEPS"); h.newline();
        z.forceNewline(0);
        
        sw.set(save_header, save_body);
	}



	public void visit(LocalClassDecl_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		// FIXME: [IP] Local classes cannot have static members (thus asyncs)
		//
		// [Krishna] Is it not fixed already? -- The asyncs in the
		// nested class will be added to the outmost toplevel
		// class. Would that be a problem? Probably not.
		assert (false); // We are removing all the inner + local classes using a separate pass.
		context.pushInLocalClass();
        ClassifiedStream save_header = sw.header();
        sw.setHeader(sw.body());
		emitter.printHeader((ClassDecl_c)n.decl(), sw, tr, false);
		sw.allowBreak(0, " ");
		n.print(n.decl().body(), sw, tr);
		context.popInLocalClass();
        sw.setHeader(save_header);
	}

    // Get the list of methods of name "name" that ought to be accessible from class c
    // due to being locally defined or inherited
    List<MethodInstance> getOROLMeths(Name name, X10ClassType c) {
        assert(name!=null);
        assert(c!=null);
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
        X10ClassType superClass = (X10ClassType) c.superClass();
        if (superClass!=null) {
            List<MethodInstance> moreMeths = getOROLMeths(name, superClass, shadowed);
            meths.addAll(moreMeths);
        }

        return meths;
    }

	public void visit(ClassBody_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10ClassType currentClass = (X10ClassType) context.currentClass();
        X10ClassType superClass = (X10ClassType) currentClass.superClass();

		ClassifiedStream h = sw.header();

		h.write("{");
		h.newline(4);
		h.begin(0);
		emitter.printRTT(currentClass, h);
		sw.begin(0);
		for (Iterator pi = context.classProperties.iterator(); pi.hasNext();) {
			PropertyDecl_c p = (PropertyDecl_c) pi.next();
			n.print(p, sw, tr);
		}
		context.classProperties = new ArrayList();

		List<ClassMember> members = n.members();
		if (!members.isEmpty()) {
			String className = emitter.translateType(currentClass);

			for (ClassMember member : members) {
				if (!(member instanceof X10ClassDecl_c))
					continue;
                assert (false) : "Nested class alert! "+member+" "+n.position().nameAndLineString();
				ClassDecl_c dec = (ClassDecl_c)member;
				X10ClassDef def = (X10ClassDef) dec.classDef();
				if (getCppRep(def, tr) != null)
					continue;
				if (def.flags().isStatic() && ((X10ClassDef)currentClass.def()).typeParameters().size() != 0)
					continue;
				emitter.printFlags(h, dec.flags().flags());
				emitter.printTemplateSignature(((X10ClassType)dec.classDef().asType()).typeArguments(), h);
				h.write("class ");
				h.write(mangled_non_method_name(dec.name().id().toString()));
				h.write(";");
				h.newline();
			}

			if (extractInits(currentClass, RUN_INITIALIZERS, VOID, members, false)) {
				h.write("private : " + VOID + " " + RUN_INITIALIZERS + "();");
				h.newline();
				context.hasInits = true;
			}

			ClassMember prev = null;
			for (ClassMember member : members) {
				if (member instanceof ClassDecl_c)  // Process nested classes separately
					continue;
				if ((member instanceof polyglot.ast.CodeDecl) ||
						(prev instanceof polyglot.ast.CodeDecl)) {
					h.newline(0);
					sw.newline(0);
				}
				prev = member;
				n.printBlock(member, sw, tr);
            }

            // generate proxy methods for an overridden method's superclass overloads
            if (superClass != null) {
                // first gather a set of all the method names in the current class
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
                        if (currentClass.methods(mname,formals).size()>0) continue;
                        // otherwise we need to add a proxy.
                        //System.out.println("Not found: "+dropzone);
                        assert(!dropzone.flags().isStatic());
                        //assert(!dropzone.flags().isFinal());
                        assert(!dropzone.flags().isPrivate());
                        h.write("public: ");
                        emitter.printTemplateSignature(dropzone.typeParameters(), h);
                        if (dropzone.typeParameters().isEmpty()) {
                            h.write("virtual ");
                        }
                        emitter.printType(dropzone.returnType(), h);
                        h.write(" "+mangled_method_name(mname.toString()));
                        int counter = 0;
                        for (Type formal : formals) {
                            h.write(counter==0?"(":", ");
                            emitter.printType(formal, h);
                            h.write(" p"+counter++);
                        }
                        h.write(") {"); h.newline(4); h.begin(0);

                        if (!dropzone.returnType().isVoid())
                            h.write("return ");
                        h.write(emitter.translateType(superClass,false)
                                +"::"+mangled_method_name(mname.toString()));
                        if (dropzone.typeParameters().size() != 0) {
                            String prefix = "<";
                            for (Type t : dropzone.typeParameters()) {
                                h.write(prefix);
                                h.write(emitter.translateType(t));
                                prefix = ",";
                            }
                            h.write(">");
                        }
                        counter = 0;
                        for (Type formal : formals) {
                            h.write(counter==0?"(":", ");
                            h.write("p"+counter++);
                        }
                        h.write(");"); h.end(); h.newline();

                        h.write("}"); h.newline();
                    }
                }
            }

			if (extractInits(currentClass, STATIC_INIT, VOID, members, true)) {
                // define field that triggers initalisation-time registration of
                // static init function
                sw.write("static " + VOID_PTR + " __init__"+getUniqueId_() +
                        " = x10aux::InitDispatcher::addInitializer(" +
                        className+"::"+STATIC_INIT + ")" + ";");
                sw.newline(); sw.forceNewline(0);
                // declare static init function in header
				h.write("public : static " + VOID + " " + STATIC_INIT + "();");
				h.newline();
			}

			if (((X10TypeSystem) tr.typeSystem()).isValueType(currentClass)) {
				emitter.generateSerializationMethods(currentClass, sw);
			}

			context.resetMainMethod();
		}

		h.end();
		sw.end();
		sw.newline();
		h.newline(0);
		h.write("};");
		h.newline();

        emitter.printRTTDefn(currentClass, sw);
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
		if (n.kind() == Import_c.CLASS || n.kind() == Import_c.PACKAGE) {
			X10CPPContext_c context = (X10CPPContext_c) tr.context();
			context.pendingImports.add(n);
		}
		else
			throw new InternalCompilerError("Unknown import kind");
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
		if (query.isMainMethod(dec)) {
			Type container = dec.methodDef().asInstance().container();
		    if (container.isClass() && !((X10ClassType)container).typeArguments().isEmpty()) {
		    	List<Type> args = Arrays.asList(new Type[] { xts.Void() });
		    	container = ((X10ClassType)container).typeArguments(args);
		    }
			xcdProcessor.new Template("MainMP", emitter.translateType(container)).expand();
		}
		if ((((X10ClassDef)((X10ClassType)dec.methodDef().asInstance().container()).def()).typeParameters().size() != 0)
				&& flags.isStatic())
		{
			context.pendingStaticDecls.add(dec);
			return;
		}
		X10MethodInstance mi = (X10MethodInstance) dec.methodDef().asInstance();
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
		Type ret_type = emitter.findRootMethodReturnType((X10MethodDef)dec.methodDef(), dec.position(), mi);
		String methodName = mi.name().toString();
        sw.pushCurrentStream(h);
        emitter.printHeader(dec, sw, tr, methodName, ret_type, false);
        sw.popCurrentStream();
        h.write(";");
        h.newline();

		if (dec.body() != null) {
			if (!flags.isStatic()) {
				VarInstance ti = xts.localDef(Position.COMPILER_GENERATED, Flags.FINAL,
						new Ref_c<StructType>(dec.methodDef().asInstance().container()), Name.make(THIS)).asInstance();
				context.addVariable(ti);
			}
			emitter.printHeader(dec, sw, tr, methodName, ret_type, true);
			sw.newline();
			dec.printSubStmt(dec.body(), sw, tr);
			sw.newline();
		} else {
			// Check for properties accessed using method syntax.  They may have @Native annotations too.
			if (flags.isProperty() && flags.isAbstract() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
				X10FieldInstance fi = (X10FieldInstance) mi.container().fieldNamed(mi.name());
				if (fi != null) {
					// This is a property method in an interface.  Give it a body.
					emitter.printHeader(dec, sw, tr, methodName, ret_type, true);
					sw.write("{");
					sw.allowBreak(0, " ");
					sw.write("return "+mangled_field_name(fi.name().toString())+";");
					sw.allowBreak(0, " ");
					sw.write("}");
				}
			}
		}
	}



	public void visit(ConstructorDecl_c dec) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (dec.flags().flags().isNative())
			return;

		ClassifiedStream h = sw.header();
        sw.pushCurrentStream(h);
		emitter.printHeader(dec, sw, tr, false);
        sw.popCurrentStream();
		emitter.printHeader(dec, sw, tr, true);
		ClassType container = (ClassType)dec.constructorDef().asInstance().container();
		boolean hasInits = false;
		X10ConstructorInstance ci = (X10ConstructorInstance) dec.constructorDef().asInstance();
		if (dec.body() != null) {
			// Extract initializers from the body
			Block_c body = (Block_c) dec.body();
			List<Stmt> statements = body.statements();
//			HashMap inited = new HashMap();
			List<Stmt> newStatements = new TypedList(new ArrayList(), Stmt.class, false);
			for (Stmt n : statements) {
				if (n instanceof ConstructorCall && ((ConstructorCall)n).kind() == ConstructorCall.SUPER) {
					ConstructorCall call = (ConstructorCall) n;
					sw.allowBreak(4, " ");
					sw.write(":");
					sw.allowBreak(2, " ");
					sw.write(emitter.translateType(container.superClass()) + "(");
					if (call.arguments().size() > 0) {
						sw.allowBreak(2, 2, "", 0); // miser mode
						sw.begin(0);
                        int counter = 0;
						for(Expr e : (List<Expr>) call.arguments() ) {
							if (counter==0) {
							} else {
								sw.write(",");
								sw.allowBreak(0, " ");
							}
                            ConstructorInstance calli = call.constructorInstance();
                            String type = emitter.translateType(calli.formalTypes().get(counter),true);
                            sw.write("("+type+")(");
							dec.print(e, sw, tr);
                            sw.write(")");
                            counter++;
						}
						sw.end();
					}
					sw.write(")");
					hasInits = true;
				} else
					newStatements.add(n);
			}
			assert (!dec.flags().flags().isStatic());
			TypeSystem ts = tr.typeSystem();
			VarInstance ti = ts.localDef(Position.COMPILER_GENERATED, Flags.FINAL,
					new Ref_c<StructType>(container), Name.make(THIS)).asInstance();
			context.addVariable(ti);
			if (hasInits)
				sw.newline();
			else
				sw.allowBreak(0, " ");
			sw.write("{"); sw.newline(4); sw.begin(0);
			if (context.hasInits) {
				sw.write("this->"+RUN_INITIALIZERS+"();"); sw.newline();
			}
			sw.write("this->"+INIT+"(");
			sw.begin(0);
			boolean first = true;
			for (Formal f : dec.formals()) {
				if (first) {
					first = false;
				} else {
					sw.write(",");
					sw.allowBreak(0, " ");
				}
				sw.write(mangled_non_method_name(f.name().toString()));
			}
			sw.end();
			sw.write(");");
			sw.end(); sw.newline(); sw.write("}");
			sw.newline();

            sw.pushCurrentStream(h);
			h.write("private: "+VOID+" "+INIT+"(");
			h.begin(0);
			first = true;
			for (Formal f : dec.formals()) {
				if (first) {
					first = false;
				} else {
					h.write(",");
					h.allowBreak(0, " ");
				}
				dec.print(f, sw, tr);
			}
			h.end();
			h.write(");");
			h.newline();
            sw.popCurrentStream();
			emitter.printTemplateSignature(((X10ClassType)dec.constructorDef().container().get()).typeArguments(), sw);
			sw.write(VOID+" "+emitter.translateType(container)+"::"+INIT+"(");
			sw.begin(0);
			first = true;
			for (Formal f : dec.formals()) {
				if (first) {
					first = false;
				} else {
					sw.write(",");
					sw.allowBreak(0, " ");
				}
				dec.print(f, sw, tr);
			}
			sw.end();
			sw.write(")");
			sw.allowBreak(0, " ");
			if (newStatements.size() > 0) {
				sw.write("{"); sw.newline(4); sw.begin(0);
				for (Stmt s : newStatements) {
					if (s instanceof ConstructorCall) {
						ConstructorCall call = (ConstructorCall)s;
						assert (call.kind() == ConstructorCall.THIS);
						sw.write("this->"+INIT+"(");
						if (call.arguments().size() > 0) {
							sw.allowBreak(2, 2, "", 0); // miser mode
							sw.begin(0);
							first = true;
							for(Expr e : (List<Expr>) call.arguments() ) {
								if (first) {
									first = false;
								} else {
									sw.write(",");
									sw.allowBreak(0, " ");
								}
								dec.print(e, sw, tr);
							}
							sw.end();
						}
						sw.write(");");
					} else {
						dec.printBlock(s, sw, tr);
					}
					sw.newline();
				}
				sw.end(); sw.newline(); sw.write("}");
			} else
				sw.write("{ }");
		} else {
			sw.write(" { }");
		}
		sw.newline();
	}


	public void visit(FieldDecl_c dec) {
		// FIXME: HACK: skip synthetic serialization fields
		if (query.isSyntheticField(dec.name().id().toString()))
			return;
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if ((((X10ClassDef)((X10ClassType)dec.fieldDef().asInstance().container()).def()).typeParameters().size() != 0) &&
				dec.flags().flags().isStatic())
		{
			context.pendingStaticDecls.add(dec);
			return;
		}

		ClassifiedStream h = sw.header();
		sw.pushCurrentStream(h);
		emitter.printHeader(dec, sw, tr, false);
		sw.popCurrentStream();
        // Ignore the initializer -- this will have been done in extractInits/extractStaticInits
        // FIXME: the above breaks switch constants!
        h.write(";");
        h.newline();
		if (dec.flags().flags().isStatic()) {
			emitter.printHeader(dec, sw, tr, true);
			// [DC] disabled because I want this done through the static initialisation framework
            // [IP] re-enabled for a very limited set of cases, namely literal inits
			if (dec.init() != null && dec.flags().flags().isFinal() &&
			        (dec.init() instanceof NumLit_c || dec.init() instanceof BooleanLit_c))
            {
				sw.write(" =");
				sw.allowBreak(2, " ");
				dec.print(dec.init(), sw, tr);
			}
			sw.write(";");
			sw.newline();
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
			if (n.expr() instanceof Field_c && n.expr().isConstant()) {
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
		// Note: The break statements inside a switch statement are always
		// non-labeled.
		// FIXME: [IP] The above assumption is incorrect!!!
		if (br.labelNode() != null) {
			if (br.kind().toString() == "continue")
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
		// end-paranthesis of the loop, have one more label L_next_:
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
 		if (unsigned_op)
			 sw.write("(("+emitter.makeUnsignedType(rhs.type())+")");
		asgn.printSubExpr(rhs, true, sw, tr);
		if (unsigned_op)
			 sw.write("))");
	}


	public void visit(Return_c ret) {
		Expr e = ret.expr();
		sw.write("return");
		if (e != null) {
			sw.write(" ");
			ret.print(e, sw, tr);
		}
		sw.write(";");
		sw.newline();
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
			dec.print(initexpr, sw, tr);
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
			boolean first = true;
			for (Iterator i = n.inits().iterator(); i.hasNext(); ) {
				ForInit s = (ForInit) i.next();
				boolean oldSemiColon = tr.appendSemicolon(false);
				boolean oldPrintType = tr.printType(false);
				n.printBlock(s, sw, tr);
				tr.printType(oldPrintType);
				tr.appendSemicolon(oldSemiColon);
				first = false;

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
		sw.newline(0);

		handleLabeledLoop(n);

		sw.end(); sw.newline(0);
		sw.write("}");
		sw.newline(0);

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


	public void visit(X10Call_c n) {

		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		X10MethodInstance mi = (X10MethodInstance)n.methodInstance();
		Receiver target = n.target();
		Type t = target.type();

		X10MethodDef md = mi.x10Def();
		if (target instanceof TypeNode) {
		    assert (mi.flags().isStatic());
		    TypeNode tn = (TypeNode) target;
		    if (t instanceof ParameterType) {
		        // Rewrite to the class declaring the field.
		        target = tn.typeRef(md.container());
		        n = (X10Call_c) n.target(target);
		    }
		    if (t.isClass()) {
		    	X10ClassType ct = (X10ClassType)t.toClass();
				if (!ct.typeArguments().isEmpty()) {
		    		List<Type> args = new TypedList(new ArrayList<Type>(), Type.class, false);
		    		for (int i = 0; i < ct.typeArguments().size(); i++)
		    			args.add(xts.Void());
		    		target = tn.typeRef(Types.ref(ct.typeArguments(args)));
		    	}
		    }
		}

		String pat = getCppImplForDef(md);
		if (pat != null) {
			emitNativeAnnotation(pat, mi.typeParameters(), target, n.arguments());
			return;
		}

		// the cast is because our generated member function may use a more general
		// return type because c++ does not support covariant smartptr returns
        Type ret_type = emitter.findRootMethodReturnType(md, null, mi);
		boolean needsCast = !xts.typeDeepBaseEquals(mi.returnType(), ret_type);
		if (needsCast) {
			sw.write("static_cast<");
			emitter.printType(mi.returnType(), sw);
			sw.write(" >(");
		}
		sw.begin(0);
        String dangling = "";
		if (!n.isTargetImplicit()) {
			// explicit target.

            if (target instanceof X10Special_c &&
                ((X10Special_c)target).kind().equals(X10Special_c.SUPER))
            {
                sw.write(emitter.translateType(t));
                sw.write("::");
            } else if (target instanceof Expr) {
                if (mi.flags().isStatic()) {
                    sw.write("((void)");
                    n.printSubExpr((Expr) target, false, sw, tr);
                    sw.write(",");
                    sw.write(emitter.translateType(t));
                    sw.write("::");
                    dangling = ")";
                } else {
                    boolean assoc = !(target instanceof New_c || target instanceof Binary_c);
                    n.printSubExpr((Expr) target, assoc, sw, tr);
                    sw.write("->");
                }
            } else if (target instanceof TypeNode || target instanceof AmbReceiver) {
                n.print(target, sw, tr);
                sw.write("::");
            }
		}

        if (context.inTemplate() && mi.typeParameters().size() != 0) {
            sw.write("template ");
        }
		sw.write(mangled_method_name(n.name().id().toString()));
		emitter.printTemplateInstantiation(mi, sw);
		sw.write("(");
		if (n.arguments().size() > 0) {
			sw.allowBreak(2, 2, "", 0); // miser mode
			sw.begin(0);
            int counter = 0;
			for (Iterator i = n.arguments().iterator(); i.hasNext(); ) {
				Expr e = (Expr) i.next();
                Type fType = mi.formalTypes().get(counter);
                boolean argNeedsCast = !xts.typeDeepBaseEquals(fType, e.type());
                if (argNeedsCast) {
                    sw.write("x10aux::class_cast<");
                    emitter.printType(fType, sw);
                    sw.write(" >(");
                }
				n.print(e, sw, tr);
                if (argNeedsCast)
                    sw.write(")");
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
                if (!ct.typeArguments().isEmpty()) {
                    List<Type> args = new TypedList(new ArrayList<Type>(), Type.class, false);
                    for (int i = 0; i < ct.typeArguments().size(); i++)
                        args.add(xts.Void());
                    target = tn.typeRef(Types.ref(ct.typeArguments(args)));
                }
            }
		}

		String pat = getCppImplForDef(fd);
		if (pat != null) {
		    String pi = translate_mangled_NSFQN(pat);
		    Object[] components = new Object[] { target };
		    emitter.dumpRegex("Native", components, tr, pat, sw);
		    return;
		}


		sw.begin(0);
		if (!n.isTargetImplicit()) {
			// explicit target.
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
                    boolean assoc =
                        !(target instanceof New_c ||
                            target instanceof Binary_c);
                    n.printSubExpr((Expr) target, assoc, sw, tr);
                }
			}
			else if (target instanceof TypeNode || target instanceof AmbReceiver) {
				n.print(target, sw, tr);
			}
			if (n.fieldInstance().flags().isStatic())
				sw.write("::");
			else
				sw.write("->");
			sw.allowBreak(2, 3, "", 0);
		} else {
			// TODO: capture constant fields as variables
			if (!n.flags().isStatic()) {
				X10CPPContext_c c = (X10CPPContext_c) tr.context();
				if (target instanceof X10Special_c && ((X10Special_c)target).isSelf()) {
					sw.write((context.Self() == null) ? "self" : context.Self());
					sw.write("->");
					// FIXME: Do we need to save the
					// context.Self() in the env?
					// [Krishna]
				} else
				if (c.insideClosure) {
					sw.write(SAVED_THIS+"->");
					if (c.insideClosure)
						c.saveEnvVariableInfo(THIS);
				}
			} else {
				sw.write(emitter.translateType(n.fieldInstance().container()) + "::");
			}
		}
		sw.write(mangled_field_name(n.name().id().toString()));
		sw.end();
	}


	public void visit(Local_c n) {
		X10CPPContext_c c = (X10CPPContext_c) tr.context();
		LocalInstance var = n.localInstance();
		if (c.insideClosure)
			c.saveEnvVariableInfo(n.name().toString());
		sw.write(c.getCurrentName(var));
	}


	public void visit(New_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		if (n.qualifier() != null)
			throw new InternalCompilerError("Qualified new not supported");
		if (n.body() != null)
			throw new InternalCompilerError("Anonymous innner classes should have been removed.");

		String type = emitter.translateType(n.objectType().type());

		// [DC] this cast is needed to ensure everything has a ref type
		// otherwise overloads don't seem to work properly
		sw.begin(0);
		sw.write("("+make_ref(type)+")( ");
		sw.allowBreak(4, "");
		sw.write("new (x10aux::alloc<"+type+(type.endsWith(">")?" ":"")+">())");
		sw.allowBreak(4, "");
		sw.write(type+"(");
		sw.begin(0);
		for (Iterator i = n.arguments().iterator(); i.hasNext(); ) {
			Expr e = (Expr) i.next();
			n.print(e, sw, tr);
			if (i.hasNext()) {
				sw.write(",");
				sw.allowBreak(0, " ");
			}
		}
		// FIXME: [IP] Temporary hack until we have full stack traces
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (ts.isSubtype(n.objectType().type(), ts.Throwable()) && n.arguments().size() == 0) {
			String stringType = emitter.translateType(ts.String());
			sw.write("String::Lit(__FILELINE__)");
		}
		sw.end();
		sw.write(")");
		sw.write(" )");
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
		if (n.kind() == IntLit_c.LONG)
			val = Long.toString(n.value()) + "ll";
		else if (n.kind() == IntLit_c.INT)
			val = Long.toString((int) n.value());
		else
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
		sw.write("String::Lit(\"");
		sw.write(StringUtil.escape(n.stringValue()));
		sw.write("\")");
	}

	public void visit(CharLit_c lit) {
		sw.write("'"+StringUtil.escape(lit.charValue())+"'");
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
		case PRIMITIVE:
		case COERCION:
		case TRUNCATION:
		case BOXING:
		case UNBOXING:

			if (tn instanceof X10CanonicalTypeNode) {
				X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;

				Type t = X10TypeMixin.baseType(xtn.type());

				X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();

				if (false || xts.typeDeepBaseEquals(t,c.expr().type())) {
					c.printSubExpr(c.expr(), true, sw, tr);
				} else {
					sw.write("x10aux::class_cast<");
					emitter.printType(t, sw);
					sw.write(" >(");
					c.printSubExpr(c.expr(), true, sw, tr);
					sw.write(")");
				}
			} else {
				throw new InternalCompilerError("Ambiguous TypeNode survived type-checking.", tn.position());
			}
			break;

		case UNKNOWN_CONVERSION:
			throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
		case CALL:
			throw new InternalCompilerError("Conversion call should have been rewritten.", c.position());
		}
	}

	public void visit(X10Instanceof_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		if (refsAsPointers) {
			sw.write("!!dynamic_cast<");
			sw.write(emitter.translateType(n.compareType().type(), true));
			sw.write(" >(");
			sw.begin(0);
			n.printSubExpr(n.expr(), sw, tr);
			sw.end();
			sw.write(")");
			return;
		}
		// equivalent of (x instanceof B) -> (!!dynamic_cast<B>(x))
		// but the above doesn't work for refs
		sw.write("INSTANCEOF(");
		sw.begin(0);
		n.printSubExpr(n.expr(), false, sw, tr);
		sw.write(",");
		sw.allowBreak(0, " ");
		sw.write(emitter.translateType(n.compareType().type(), true));
		sw.end();
		sw.write(")");
	}

	public void visit(Throw_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		sw.write("x10aux::throwException(");
		n.print(n.expr(), sw, tr);
		sw.write(");");
	}

	public void visit(Try_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
        if (n.finallyBlock() != null) {
            // FIXME: this doesn't work.  Use closures
			sw.write("{");
            sw.newline(0); sw.begin(0);
			// Create a class and use the finally block as the
			// destructor. The object is created at the
			// beginning. Use local classes.
			String tempClass = getId();
			String tempClassDef = tempClass+"def";
			sw.write("struct " + tempClassDef);
			sw.write("{");
			sw.newline(4); sw.begin(0);

			sw.write(tempClassDef+ "(){}");
			sw.newline();
			sw.write("~" + tempClassDef + "()");
			n.print(n.finallyBlock(), sw, tr);

			sw.end(); sw.newline();
			sw.write("} ");
		 	sw.write(tempClass + ";");
			sw.newline();
		}
		sw.write("try {");
		sw.newline(0);
		assert (n.tryBlock() instanceof Block_c);
		n.printSubStmt(n.tryBlock(), sw, tr);
		sw.newline(0);
		sw.write("}");
		sw.newline(0);

		// [IP] C++ will not catch ref types properly, as there is no hierarchy.
		// So, we have to do the dispatching ourselves.
		sw.newline();
		String refVar = "__ref__" + getUniqueId_();
		sw.write("catch (x10aux::__ref& " + refVar + ") {");
		sw.newline(4); sw.begin(0);
		String excVar = "__exc" + refVar;
		// Note that the following c-style cast only works because Throwable is
		// *not* an interface and thus is not virtually inheritted.  If it
		// were, we would have to static_cast the exception to Throwable on
		// throw (otherwise we would need to offset by an unknown quantity).
		String exception_ref = make_ref("Throwable");
		sw.write(exception_ref+"& " + excVar + " = ("+exception_ref+"&)" + refVar + ";");
		context.setExceptionVar(excVar);
		for (Iterator it = n.catchBlocks().iterator(); it.hasNext(); ) {
			Catch cb = (Catch) it.next();
			sw.newline(0);
			n.printBlock(cb, sw, tr);
		}
		sw.newline(4);
		sw.write("throw;");
		sw.end(); sw.newline();
		sw.write("}");
		if (n.finallyBlock() != null){
			sw.end(); sw.newline();
			sw.write("}");
		}
	}

	public void visit(Catch_c n) {
        X10CPPContext_c context = (X10CPPContext_c) tr.context();
		String excVar = context.getExceptionVar();
		sw.newline();
		sw.write("if (");
		String type = emitter.translateType(n.formal().type().type(), true);
		if (refsAsPointers) {
			sw.write("!!dynamic_cast<" + type + " >(" + excVar + ")");
		} else {
			sw.write("INSTANCEOF(" + excVar + ",");
			sw.allowBreak(0, " ");
			sw.write(type);
			sw.write(")");
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


	// FIXME: [IP] does this process ForEach?
	public void visit(X10ClockedLoop_c n) {
		assert (false);
		// Why have the call after an assert?
		// n.print(n.body(), w, tr);
	}

	public void visit(ForLoop_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10Formal form = (X10Formal) n.formal();

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars() && form.isUnnamed() && xts.isPoint(form.type().type())) {
			assert (xts.isPoint(form.type().type()));
			assert (xts.isDistribution(n.domain().type()) || xts.isRegion(n.domain().type()));

			sw.write("{");
			sw.newline(4); sw.begin(0);

			String domain = getId();
			LocalDef[] lis = form.localInstances();
			List<Formal> vars = form.vars();
			int rank = lis.length;
			String[] limit = new String[rank];
			emitter.printType(n.domain().type(), sw);
			sw.write(" " + domain + ";");
			sw.newline();
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

			sw.write(domain + " = ");
			n.print(n.domain(), sw, tr);
			sw.write(";");
			sw.newline();
			for (int i = 0; i < rank; i++) {
				LocalInstance f = lis[i].asInstance();
				assert (f.type().isInt());
				sw.write(limit[i] + " = " + domain + "->rank(" + i + ")->high();");
				sw.newline();
				sw.write("for (");
				sw.write(mangled_non_method_name(f.name().toString()));
				sw.write(" = " + domain + "->rank(" + i + ")->low(); ");
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
        assert (n.domain().type().isClass());
        X10ClassType domainType = (X10ClassType)n.domain().type().toClass();
        try {
            X10MethodInstance mi = xts.findMethod(domainType,
                                xts.MethodMatcher(domainType, Name.make("iterator"), Collections.EMPTY_LIST),
                                context.currentClassDef());
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
		sw.write("Iterator<");
		String fType = emitter.translateType(form.type().type(), true);
		sw.write(fType + (fType.endsWith(">") ? " " : ""));
		sw.write(">* " + name + ";");
		sw.newline();
		sw.write(name + " = &*"); // FIXME
        if (!xts.typeDeepBaseEquals(form.type().type(), itType))
            sw.write("x10aux::convert_iterator<"+fType+","+emitter.translateType(itType, true)+" >");
        sw.write("((");
		n.print(n.domain(), sw, tr);
		sw.write(")->iterator());");
		sw.newline();

		sw.write("for (");
		sw.begin(0);

		sw.write(";"); sw.allowBreak(2, " ");
		sw.write(name + "->hasNext();");
		sw.allowBreak(2, " ");

		sw.end();
		sw.write(") {");
		sw.newline(4); sw.begin(0);

		n.print(form, sw, tr);
		sw.write(";");
		sw.newline();
		sw.write(mangled_non_method_name(form.name().id().toString()));
		sw.write(" = " + name + "->next();");
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


    // FIXME: do this in the Desugarer
	public void visit(ForEach_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10Formal form = (X10Formal) n.formal();
		// FIXME: Handle clocks. [Krishna]
		assert (n.clocks() == null || n.clocks().size() == 0);

		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars() && form.isUnnamed()) {
			X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
			assert (xts.isPoint(form.type().type()));
			assert (xts.isDistribution(n.domain().type()) || xts.isRegion(n.domain().type()));

			sw.write("{");
			sw.newline(4); sw.begin(0);

			String domain = getId();
			emitter.printType(n.domain().type(), sw);
			sw.write(" " + domain + " = ");
			n.print(n.domain(), sw, tr);
			sw.write(";");
			sw.newline();

			LocalDef [] lis = form.localInstances();
			List<Formal> vars = form.vars();
			int rank = lis.length;
			for (int i = 0; i < rank; i++) {
				LocalInstance f = lis[i].asInstance();
				assert (f.type().isInt());
				String limit = getId();
				emitter.printType(f.type(), sw);
				sw.write(" " + limit + " = " + domain + "->rank(" + i + ")->high();");
				sw.newline();
				sw.write("for (");
				emitter.printType(f.type(), sw);
				sw.write(" ");
				sw.write(mangled_non_method_name(f.name().toString()));
				sw.write(" = " + domain + "->rank(" + i + ")->low(); ");
				sw.write(mangled_non_method_name(f.name().toString()));
				sw.write(" <= " + limit + "; ");
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

		sw.write("{");
		sw.newline(4); sw.begin(0);

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		assert (xts.isPoint(form.type().type()));
		assert (xts.isDistribution(n.domain().type()) || xts.isRegion(n.domain().type()));
		String name = "__i" + form.name();
		sw.write("Iterator<point>* " + name + " = &(");
		n.print(n.domain(), sw, tr);
		sw.write(")->iterator();");
		sw.newline();

		sw.write("for (");
		sw.begin(0);

		sw.write(";"); sw.allowBreak(2, " ");
		sw.write(name + "->hasNext();");
		sw.allowBreak(2, " ");

		sw.end();
		sw.write(") {");
		sw.newline(4); sw.begin(0);

		n.print(form, sw, tr);
		sw.write(" = &" + name + "->next();");
		sw.newline();
		for (Iterator li = n.locals().iterator(); li.hasNext(); ) {
			Stmt l = (Stmt) li.next();
			n.print(l, sw, tr);
		}

		handleLabeledLoop(n);

		sw.end(); sw.newline(0);
		sw.write("}");
		sw.newline(0);

		sw.write("x10aux::dealloc(" + name + ");");
		sw.newline();

		sw.end(); sw.newline(0);
		sw.write("}");
		sw.newline(0);
	}


    // FIXME: do this in the Desugarer
	public void visit(AtEach_c n) {
//		if (!n.clocks().isEmpty())
//			throw new InternalCompilerError("clocked loops not supported");
		// We need to translate the ateach header before printing the body.
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		// -- K context.ateach_depth++;
		//context.setAtEachCode();

		// FIXME:Krishna: Replace the ateach by a for + async.
		// FIXME: Handle clocks.
		assert (n.clocks() == null);
		// We need to translate the ateach header before printing the body.
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		assert (xts.isPoint(n.formal().type().type()));
		assert (xts.isDistribution(n.domain().type()));
		String dist = getId();

		// Evaluate the distribution expression only once
		// (put it outside the loop).

		sw.write("x10::lang::dist " + dist + " = ");
		n.print(n.domain(), sw, tr);
		sw.write(";");
		sw.newline();

		String itr = getId();
		sw.write("for (x10::lang::Iterator<x10::lang::point> " + itr);
		sw.write(" = " + dist + ".iterator() ; " +
				itr + ".hasNext();) ");
		sw.write("{"); sw.newline(4); sw.begin(0);

		n.print(n.formal(), sw, tr);
		sw.write (" = ");

		sw.write ("(");
		n.print(n.formal().type(), sw, tr);
		sw.write (")");
		sw.write (itr + ".next();");
		sw.newline();

		context.addVar(n.formal().name().id().toString());

		for (Iterator li = n.locals().iterator(); li.hasNext(); ) {
			Stmt l = (Stmt) li.next();
			n.print(l, sw, tr);
		}

		emitter.processAsync(n,
		   mangled_non_method_name(n.formal().name().toString()),
		   n.body(), context, sw);

		sw.end(); sw.newline(); sw.write("}");
		sw.newline();
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
        assert (n.qualifier() == null) :
               n.qualifier()+" "+n.kind()+" "+n.position().nameAndLineString();

        if (n.kind().equals(X10Special_c.THIS)) {
            if (context.insideClosure) {
                sw.write(SAVED_THIS);
                context.saveEnvVariableInfo(THIS);
            } else {
                sw.write("(("+emitter.translateType(n.type(),true)+")"+n.kind()+")");
            }
        } else if (n.kind().equals(X10Special_c.SUPER)) {
            sw.write(emitter.translateType(context.currentClass().superClass()));
        } else if (n.isSelf()) {
            // FIXME: Why are we printing the string "self"?
            // Confirm with Igor. [Krishna]
            sw.write((context.Self() == null)? "self":context.Self());
        } else assert (false) : n.kind();

    }


	public static String getClosureName(String className, int id) {
        // TODO: factor out into a constant
		return className+"__closure__"+id;
	}

	public void visit(Closure_c n) {

		X10CPPContext_c c = (X10CPPContext_c) tr.context();

		X10CPPContext_c.Closures a = c.closures;
		emitter.enterClosure(a, n);

		ClosureDef closureDef = n.closureDef();
		CodeInstance ci = closureDef.methodContainer().get();
		X10ClassType hostClassType = (X10ClassType)(closureDef.typeContainer().get());
        X10ClassDef hostClassDef = hostClassType.x10Def();

        List<Type> freeTypeParams = new ArrayList<Type>();
        // FIXME: handle static field initializers here
        while (ci instanceof ClosureInstance)
            ci = ((ClosureDef) ci.def()).methodContainer().get();
        if (ci instanceof X10MethodInstance) {
            X10MethodInstance xmi = (X10MethodInstance) ci;
            // in X10, static methods do not inherit the template params of their classes
            if (!xmi.flags().isStatic())
                freeTypeParams.addAll(hostClassDef.typeParameters());
            freeTypeParams.addAll(xmi.typeParameters());
        } else {
            // could be a constructor or other non-static thing
            freeTypeParams.addAll(hostClassDef.typeParameters());
        }

		String hostClassName = emitter.translate_mangled_FQN(hostClassType.fullName().toString(), "_");

		c.setinsideClosure(true);

		int id = getConstructorId(a);

		String cname = getClosureName(hostClassName,id);

        boolean in_template_closure = false;

        StringBuffer cnamet_ = new StringBuffer(cname);
        String prefix = "<";
        for (Type t : freeTypeParams) {
            in_template_closure = true;
            cnamet_.append(prefix + emitter.translateType(t));
            prefix = ",";
        }
        if (in_template_closure) cnamet_.append(">");
        String cnamet = cnamet_.toString();


		// create closure and packed arguments

		// Prepend this stream to closures.  Closures are created from the outside in.
		// Thus, later closures can be used by earlier ones, but not vice versa.
		ClassifiedStream inc_s = sw.getNewStream(StreamWrapper.StreamClass.Closures, true);
        sw.pushCurrentStream(inc_s);

        StreamWrapper inc = sw;
		Type retType = n.returnType().type();
		//String className = emitter.translateType(c.currentClass());
		String superType = n.returnType().type().isVoid() ?
				"x10::lang::" + mangled_non_method_name("VoidFun_0_" + n.formals().size()) :
					"x10::lang::" + mangled_non_method_name("Fun_0_" + n.formals().size());
		prefix = "<";
		for (Formal formal : n.formals()) {
			superType = superType + prefix + emitter.translateType(formal.type().typeRef().get(), true);
			prefix = ", ";
		}
		if (!n.returnType().type().isVoid()) {
			superType = superType + prefix + emitter.translateType(retType, true);
			prefix = ", ";
		}
		if (!prefix.equals("<")) superType = superType +" >"; // don't emit " >" for void->void case

        boolean generate_async_invoke = false;
        if (superType.equals("x10::lang::VoidFun_0_0")) generate_async_invoke = true;

		// have to work out what the formals are whilst visiting the closure body
		// but we need to know the formals before generating code for the body (function parameters)


		// class header
        if (!freeTypeParams.isEmpty()) {
            inc.write("template");
            prefix="<";
            for (Type t : freeTypeParams) {
                inc.write(prefix+"class "+t);
                prefix = ",";
            }
            inc.write("> ");
        }
		inc.write("class "+cname+" : "); inc.begin(0);
		inc.write("public x10aux::AnyClosure, "); inc.newline();
		//inc.write("public x10::lang::Value, "); inc.newline();
		inc.write("public virtual "+superType); inc.end(); inc.newline();
		inc.write("{") ; inc.newline(4); inc.begin(0);
		inc.write("public:") ; inc.newline(); inc.forceNewline();

		inc.write("// closure body"); inc.newline();
		inc.write(emitter.translateType(retType, true)+" apply (");
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

		inc.write("void _serialize_fields("+SERIALIZATION_BUFFER+" &buf, x10aux::addr_map& m) {");
		inc.newline(4); inc.begin(0);
        inc.write("buf.write(_serialization_id);"); inc.newline();
		for (int i = 0; i < c.variables.size(); i++) {
			if (i > 0) inc.newline();
			VarInstance var = (VarInstance) c.variables.get(i);
			String name = var.name().toString();
			if (name.equals(THIS))
				name = SAVED_THIS;
			else name = mangled_non_method_name(name);
			inc.write("buf.write(" + name + ");");
		}
		inc.end(); inc.newline();
		inc.write("}"); inc.newline(); inc.forceNewline();

		inc.write("void _deserialize_fields("+SERIALIZATION_BUFFER+" &buf) {");
		inc.newline(4); inc.begin(0);
        for (int i = 0; i < c.variables.size(); i++) {
			if (i > 0) inc.newline();
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name=mangled_non_method_name(name);
            inc.write(name+" = buf.read<"+emitter.translateType(var.type(), true)+" >();");
        }
		inc.end(); inc.newline();
		inc.write("}"); inc.newline(); inc.forceNewline();


        if (generate_async_invoke) {
            inc.write("static void _invoke("+SERIALIZATION_BUFFER+" &buf) {");
            inc.newline(4); inc.begin(0);
            inc.write(cnamet+" *this_ = new "+cnamet+"(x10aux::SERIALIZATION_MARKER());"); inc.newline();
            inc.write("this_->_deserialize_fields(buf);"); inc.newline();
            inc.write("this_->apply();");
            inc.end(); inc.newline();
            inc.write("}"); inc.newline(); inc.forceNewline();
        }


		inc.write(cname+"("+SERIALIZATION_MARKER+") { }");
		inc.newline(); inc.forceNewline();

		inc.write(cname+"(");
        for (int i = 0; i < c.variables.size(); i++) {
            if (i > 0) inc.write(", ");
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name=mangled_non_method_name(name);
            inc.write(emitter.translateType(var.type(),true) + " " + name);
        }
		inc.write(") {");
		inc.newline(4); inc.begin(0);
        for (int i=0 ; i<c.variables.size() ; i++) {
            VarInstance var = (VarInstance)c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name=mangled_non_method_name(name);
            if (i > 0) inc.newline();
            inc.write("this->" + name + " = " + name + ";");
        }
		inc.end(); inc.newline();
		inc.write("}"); inc.newline(); inc.forceNewline();

        inc.write("static x10_int _serialization_id;"); inc.newline(); inc.forceNewline();

		inc.write("const x10aux::RuntimeType *_type() const {"+
			 	  " return x10aux::getRTT<"+superType+" >(); }");
		inc.newline(); inc.forceNewline();

        inc.write("x10aux::ref<x10::lang::String> toString() {");
        inc.newline(4); inc.begin(0);
        inc.write("return String::Lit(\""+StringUtil.escape(n.position().nameAndLineString())+"\");");
        inc.end(); inc.newline();
        inc.write(" }");
		inc.end(); inc.newline(); inc.forceNewline();

		inc.write("};"); inc.newline(); inc.forceNewline();

        if (in_template_closure) {
            inc.write("template");
            prefix="<";
            for (Type t : freeTypeParams) {
                inc.write(prefix+"class "+t);
                prefix = ",";
            }
            inc.write("> ");
        }
        inc.write("x10_int "+cnamet+"::_serialization_id = ");

        if (generate_async_invoke) {
            inc.write("x10aux::AsyncSwitch::addInvoker("+cnamet+"::_invoke);");
        } else {
            // FIXME: should be unique etc
            inc.write("0;");
        }
        inc.newline(); inc.forceNewline();

        sw.popCurrentStream();

		// create closure instantiation (not in inc but where the closure was defined)
		// note that we alloc using the typeof the superType but we pass in the correct size
		// this is because otherwise alloc may (when debugging is on) try to examine the
		// RTT of the closure (which doesn't exist)

        // first get the template arguments (if any)
        prefix="<";
        StringBuffer sb = new StringBuffer();
        for (Type t : freeTypeParams) {
            sb.append(prefix+t);
            prefix = ",";
        }
        if (prefix.equals(",")) sb.append(">");
        String templateArgs = sb.toString();

		sw.write("x10aux::ref<"+superType+" >");
        sw.write("(new (x10aux::alloc<"+superType+" >(sizeof("+cname+templateArgs+")))");
		sw.write(cname+templateArgs+"(");
        for (int i = 0; i < c.variables.size(); i++) {
            if (i > 0) sw.write(", ");
            VarInstance var = (VarInstance) c.variables.get(i);
            String name = var.name().toString();
            if (!name.equals(THIS))
				name=mangled_non_method_name(name);
            sw.write(name);
        }
		sw.write("))");

		c.finalizeClosureInstance();
		emitter.exitClosure(a);
	}


	public void visit(ClosureCall_c c) {
		Expr target = c.target();
		Type t = target.type();
		boolean base = false;

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

		X10MethodInstance mi = c.closureInstance();

		c.printSubExpr(target, sw, tr);
		sw.write("->");
		sw.write("apply");
		sw.write("(");
		sw.begin(0);
		/* TODO: TYPE PARAMETERS
		for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
			final Type at = i.next();
			new RuntimeTypeExpander(at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		*/

		List l = c.arguments();
		for (Iterator i = l.iterator(); i.hasNext(); ) {
			Expr e = (Expr) i.next();
			c.print(e, sw, tr);
			if (i.hasNext()) {
				sw.write(",");
				sw.allowBreak(0, " ");
			}
		}
		sw.end();
		sw.write(")");
	}



	public void visit(X10CanonicalTypeNode_c n) {
//		System.out.println("Pretty-printing canonical type node for "+n);
		Type t = n.type();
		if (t == null)
			throw new InternalCompilerError("Unknown type");
		sw.write(emitter.translateType(t));
	}


	public void visit(Unary_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		Unary_c.Operator operator = n.operator();
		Expr expr = n.expr();
		Type t = expr.type();
		if (operator == Unary_c.NEG && expr instanceof IntLit && ((IntLit) expr).boundary()) {
			sw.write(operator.toString());
			sw.write(((IntLit) expr).positiveToString());
		}
		else if ((operator == Unary_c.NEG || operator == Unary_c.POS) &&
				t instanceof X10Type &&
				((X10TypeSystem) ((X10Type) t).typeSystem()).isPoint(t)) {
			n.printSubExpr(expr, true, sw, tr);
			if (operator == Unary_c.NEG) {
				sw.write("->neg()");
			}
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


	public void visit(Binary_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		// FIXME Check if there needs to be explicit handling of operators polyglot.ast.Binary.EQ and polyglot.ast.Binary.NE
		// for Reference Type arguments here as in X10PrettyPrinter.java

		boolean unsigned_op = false;
		String opString = n.operator().toString();

		if (opString.equals(">>>") || opString.equals(">>>=")) {
			unsigned_op = true;
			opString = opString.substring(1);
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


	void declarePackedArgumentsStruct(ClassifiedStream w, X10CPPContext_c c, int id) {
		w.write("struct "+args_name(INIT_PREFIX, id)+";"); w.newline();
	}

	public void visit(ArrayInit_c n) {
		throw new InternalCompilerError("Should not be invoked");
	}

	public void visit(SettableAssign_c n) {
	    // Code ported from X10PrettyPrinter.java (x10.compiler.p3) and
	    // ported to suit the needs. [Krishna]
	    SettableAssign_c a = n;
	    Expr array = a.array();
	    List<Expr> index = a.index();

	    TypeSystem ts = tr.typeSystem();
	    Type t = n.leftType();

	    boolean nativeop = false;
	    if (t.isNumeric() || t.isBoolean() || t.isSubtype(ts.String())) {
	        nativeop = true;
	    }

	    if (n.operator() == Assign.ASSIGN) {
	        // Look for the appropriate set method on the array and emit native code if there is an
	        // @Native annotation on it.
	        X10MethodInstance mi= (X10MethodInstance) n.methodInstance();
	        List<Expr> args = new ArrayList<Expr>(index.size()+1);
	        args.add(n.right());
	        for (Expr e : index) args.add(e);

	        String pat = getCppImplForDef(mi.x10Def());
	        if (pat != null) {
	            emitNativeAnnotation(pat, mi.typeParameters(), array, args);
	            return;
	        }
	        // otherwise emit the hardwired code.
	        sw.write("(");
	        sw.begin(0);
	        tr.print(n, array, sw);
	        sw.end();
	        sw.write(").set(");
	        sw.begin(0);
	        tr.print(n, n.right(), sw);
	        for (Expr e: index) {
	            sw.write(",");
	            sw.allowBreak(0, " ");
	            n.printSubExpr(e, false, sw, tr);
	        }
	        sw.end();
	        sw.write(")");
	    }
	    else {
	        // R target = x; T right = e;
	        // target.f = target.f.add(right);
	        // new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
	        Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	        Name methodName = X10Binary_c.binaryMethodName(op);
	        sw.write("{ ");
	        emitter.printType(n.type(), sw);
	        String retVar=getId();
	        sw.write(" " + retVar + ";");
	        sw.newline();

	        emitter.printType(array.type(), sw);
	        String target = getId();
	        sw.write(" " + target + " = ");
	        tr.print(n, array, sw);
	        sw.newline();

	        String eArr [] = new String[index.size()];
	        {
	            int i = 0;
	            for (Expr e : index) {
	                emitter.printType(e.type(), sw);
	                sw.write(" ");
	                eArr[i] = getId();
	                sw.write(eArr[i]);
	                sw.write(" = ");
	                n.printSubExpr(e, false, sw, tr);
	                sw.write(";"); sw.newline();
	                i++;
	            }
	        }
	        emitter.printType(n.right().type(), sw);
	        String right = getId();
	        sw.write(" " + right + " = ");
	        tr.print(n, n.right(), sw);
	        sw.write(";"); sw.newline();

	        if (! n.type().isVoid()) {
	            sw.write(retVar + " = " );
	        }
	        sw.write("array.set(");
	        sw.write(" array.apply(");
	        {
	            int i = 0;
	            for (Expr e : index) {
	                if (i != 0)
	                    sw.write(", ");
	                sw.write(eArr[i]);
	                i++;
	            }
	        }
	        sw.write(")");
	        if (nativeop) {
	            sw.write(" ");
	            sw.write(op.toString());
	            sw.write(right);
	        }
	        else {
	            sw.write(".");
	            sw.write(methodName.toString());
	            sw.write("(" + right +")");
	        }
	        if (index.size() > 0)
	            sw.write(", ");
	        {
	            int i = 0;
	            for (Expr e : index) {
	                if (i != 0)
	                    sw.write(", ");
	                sw.write(eArr[i]);
	                i++;
	            }
	        }
	        sw.write(");");
	        sw.newline();
	        if (! n.type().isVoid()) {
	            sw.write(retVar + ";" );
		    sw.newline();
	        }
	        sw.write("}");
	    }
	}


	private static String getCppImplForDef(X10Def o) {
	    X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
	    try {
	        Type java = (Type) xts.systemResolver().find(QName.make("x10.compiler.Native"));
	        List<Type> as = o.annotationsMatching(java);
	        for (Type at : as) {
	            assertNumberOfInitializers(at, 2);
	            String lang = getPropertyInit(at, 0);
	            if (lang != null && lang.equals(NATIVE_STRING)) {
	                String lit = getPropertyInit(at, 1);
	                return lit;
	            }
	        }
	    }
	    catch (SemanticException e) {}
	    return null;
	}

	/*
	 // In case there is a need to have native declarations part of the
	 // function declarations, use the following.
	 // -Krishna.
	private void emitNativeDecl(String pat, List<LocalInstance> names) {
		 Object[] components = new Object[names.size()+1];
		    int i = 0;
		    components[i++] = THIS;
		    for (LocalInstance li : names) {
			// FIXME: Handle typeParameters
			if (li.type() instanceof ParameterType){
				assert false;

				// components[i++] = new TypeExpander(at, true, false, false);
				// components[i++] = new TypeExpander(at, true, true, false);
				// components[i++] = new RuntimeTypeExpander(at);
			}
			if (li.type() instanceof ClosureType) {
				// TODO: Handle Closures.
				assert false;
			}
			components[i++] = mangled_non_method_name(li.name().toString());
		    }
		    String pi = translate_mangled_NSFQN(pat);
		    if (!pi.contains("#")){

			X10CPPContext_c c = (X10CPPContext_c) tr.context();
			c.pendingImplicitImports.add(pat);
		    }

		    dumpRegex("Native", components, tr, pat);
	}
	*/

	private static Object getBoxType(Type type) {
		if (type.isClass())
			return type;
		if (type.isBoolean() || type.isNumeric()) {
			String[] s = new String[] { "boolean", "byte", "char", "short", "int", "long", "float", "double" };
			String[] w = new String[] { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" };
			for (int i = 0; i < s.length; i++) {
				if (type.toString().equals(s[i])) {
					return w[i];
				}
			}
			// Should not reach.
			assert (false);
		}
		if (type instanceof ParameterType)
			return type;

		// FIXME: unhandled.
		assert (false);
		return null;
	}

	private void emitNativeAnnotation(String pat, List<Type> types, Receiver receiver, List<Expr> args) {
		 Object[] components = new Object[1+3*types.size() + args.size()];
		 assert (receiver != null);
		 components[0] = receiver;

         int i = 1;
		 for (Type at : types) {
			 components[i++] = at;
			 components[i++] = getBoxType(at);
			 // FIXME: Handle runtime types.
			 components[i++] = "/* UNUSED */";
		 }
		 for (Expr e : args) {
			 components[i++] = e;
		 }
		 String pi = pat;
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
