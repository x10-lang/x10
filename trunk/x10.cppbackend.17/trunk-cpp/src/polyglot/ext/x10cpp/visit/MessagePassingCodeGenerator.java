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
import x10c.util.WriterStreams;
import x10c.util.WriterStreams.StreamClass;

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
	private final ClassifiedStream w; // This is the current stream.
	private final WriterStreams ws;
	private final Translator tr;
	private XCDProcessor xcdProcessor;

	Emitter emitter;
	ASTQuery query;
	public MessagePassingCodeGenerator(StreamWrapper sw, Translator tr) {
		this.sw = sw;
		this.ws = sw.ws;
		this.tr = tr;
		this.w = sw.cs;
		this.emitter=new Emitter(sw, tr);
		this.xcdProcessor = new XCDProcessor(sw, tr);
		this.query = new ASTQuery(sw, tr);
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
				emitter.printHeader(fd, w, tr, false);
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
				emitter.printHeader(md, w, tr, false);
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

	private void extractGenericStaticInits(X10ClassDef cd, ClassifiedStream w) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		ArrayList<FieldDecl_c> inits = new ArrayList<FieldDecl_c>();
		String container = translate_mangled_FQN(cd.fullName().toString())+voidTemplateInstantiation(cd.typeParameters().size());
		for (ClassMember dec : context.pendingStaticDecls) {
			if (dec instanceof FieldDecl_c) {
				FieldDecl_c fd = (FieldDecl_c) dec;
				((X10CPPTranslator)tr).setContext(fd.enterScope(context)); // FIXME
				emitter.printType(fd.type().type(), w);
				w.allowBreak(2, " ");
				w.write(container+"::");
				w.write(mangled_field_name(fd.name().id().toString()));
				if (fd.init() != null) {
				    // [DC] want these to occur in the static initialiser instead
                    // [IP] except for the ones that use a literal init - otherwise switch is broken
					if (fd.flags().flags().isStatic() && fd.flags().flags().isFinal() &&
                            (fd.init() instanceof NumLit_c || fd.init() instanceof BooleanLit_c))
					{
						w.write(" =");
						w.allowBreak(2, " ");
						sw.pushCurrentStream(w);
						fd.print(fd.init(), sw, tr);
						sw.popCurrentStream();
					} else
						inits.add(fd);
				}
				w.write(";");
				((X10CPPTranslator)tr).setContext(context); // FIXME
			} else if (dec instanceof MethodDecl_c) {
				MethodDecl_c md = (MethodDecl_c) dec;
				((X10CPPTranslator)tr).setContext(md.enterScope(context)); // FIXME
				emitter.printTemplateSignature(((X10ClassType)md.methodDef().container().get()).typeArguments(), w);
				emitter.printType(md.returnType().type(), w);
				w.allowBreak(2, " ");
				w.write(container+"::");
				w.write(mangled_method_name(md.name().id().toString()) + "(");
				w.begin(0);
				boolean first = true;
				for (Formal f : md.formals()) {
					if (first) {
						first = false;
					} else {
						w.write(",");
						w.allowBreak(0, " ");
					}
					sw.pushCurrentStream(w);
					md.print(f, sw, tr);
					sw.popCurrentStream();
				}
				w.end();
				w.write(")");
				if (md.body() != null) {
					w.allowBreak(0, " ");
					sw.pushCurrentStream(w);
					md.printBlock(md.body(), sw, tr);
					sw.popCurrentStream();
				}
				((X10CPPTranslator)tr).setContext(context); // FIXME
			} else if (dec instanceof X10ClassDecl_c) {
				X10ClassDecl_c cdecl = (X10ClassDecl_c) dec;
				((X10CPPTranslator)tr).setContext(cdecl.enterScope(context)); // FIXME
				X10ClassDef def = (X10ClassDef) cdecl.classDef();
				if (getCppRep(def, tr) != null) {
					// emit no c++ code as this is a native rep class
					continue;
				}
				ClassifiedStream h = ws.getCurStream(WriterStreams.StreamClass.Header);
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

				sw.pushCurrentStream(w);
				cdecl.print(cdecl.body(), sw, tr);
				sw.popCurrentStream();

				processNestedClasses(cdecl);

				if (extractGenericStaticDecls((X10ClassDef)cdecl.classDef(), h)) {
					extractGenericStaticInits((X10ClassDef)cdecl.classDef(), w);
				}

				context.pendingStaticDecls = opsd;

				h.newline();

				ArrayList asyncs = context.closures.asyncs;
				if (context.classesWithAsyncSwitches.size() != 0) {
					emitter.printSwitchMethod(cdecl.classDef().asType(), ASYNC_SWITCH, VOID,
							ASYNC_PREFIX, asyncs, context.closures.asyncsParameters,
							context.closures.asyncContainers,
							"int niter",
							"for (int i = 0; i < niter; i++,_arg++) {", "}",
							context.classesWithAsyncSwitches, ws.getCurStream(WriterStreams.StreamClass.Closures));
					emitter.printAsyncsRegistration(cdecl.classDef().asType(), asyncs,
							ws.getCurStream(WriterStreams.StreamClass.Closures));
				}
				if (context.closures.arrayCopyClosures.size() != 0)
					emitter.printSwitchMethod(cdecl.classDef().asType(), ARRAY_COPY_SWITCH, VOID_PTR,
							ARRAY_COPY_PREFIX, asyncs, context.closures.asyncsParameters,
							context.closures.arrayCopyClosures,
							null,
							null, null,
							context.classesWithArrayCopySwitches, ws.getCurStream(WriterStreams.StreamClass.Closures));

				((X10CPPTranslator)tr).setContext(context); // FIXME
			}
			w.newline();
		}
		if (inits.size() > 0) {
			w.write(VOID + " " + container + "::" + STATIC_INIT + "() {");
			w.newline(4); w.begin(0);
			w.write("_I_(\"Doing static initialisation for class: "+container+"\");"); w.newline();
			for (FieldDecl_c fd : inits) {
				assert (fd.init() != null);
				w.write(mangled_field_name(fd.name().id().toString())+" =");
				w.allowBreak(2, " ");
				sw.pushCurrentStream(w);
				fd.print(fd.init(), sw, tr);
				sw.popCurrentStream();
				w.write(";");
				w.newline();
			}
            //w.write("return NULL;");
			w.end(); w.newline();
			w.write("}"); w.newline();
			w.write("static " + VOID_PTR + " __init__"+getUniqueId_() +" = x10aux::InitDispatcher::addInitializer(" + container + "::" + STATIC_INIT + ")"+ ";");
			w.newline(); w.forceNewline(0);
		}
	}

	private boolean extractInits(X10ClassType currentClass, String methodName,
			String retType, List members,
			ClassifiedStream w, boolean staticInits)
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
				emitter.printTemplateSignature(currentClass.typeArguments(), w);
				w.write(retType + " " + className + "::" + methodName + "() {");
				w.newline(4);
				w.begin(0);
				w.write("_I_(\"Doing "+(staticInits?"static ":"")+"initialisation for class: "+className+"\");"); w.newline();
				sawInit = true;
			}
			if (member instanceof Initializer_c) {
				Initializer_c init = (Initializer_c) member;
				sw.pushCurrentStream(w);
				init.printBlock(init.body(), sw, tr);
				sw.popCurrentStream();
				w.newline(0);
			} else if (member instanceof FieldDecl_c) {
				FieldDecl_c dec = (FieldDecl_c) member;
				Term_c init = (Term_c) dec.init();
				assert (init != null);
				w.write(mangled_field_name(dec.name().id().toString()));
				w.write(" = ");
				sw.pushCurrentStream(w);
				dec.print(init, sw, tr);
				sw.popCurrentStream();
				w.write(";");
				w.newline();
			}
		}
		if (sawInit) {
			if (!retType.equals(VOID))
				w.write("return ("+retType+")0;");
			w.end(); w.newline();
			w.write("}");
			w.newline(); w.forceNewline(0);
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


	private void extractAllClassTypes(Type t, Set<ClassType> types) {
        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        t = xts.expandMacros(t);
		if (!t.isClass())
			return;
		X10ClassType ct = (X10ClassType) t.toClass();
		types.add(ct);
		for (Type pt : ct.typeArguments())
			extractAllClassTypes(pt, types);
		if (ct.isNested())
			extractAllClassTypes(ct.outer(), types);
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

    /** Write the header for a source file. */
    public void writeHeader(X10ClassDecl cd, ClassifiedStream w, ClassifiedStream h, X10CPPTranslator tr) {
        DelegateTargetFactory tf = tr.targetFactory();
        X10CPPContext_c context = (X10CPPContext_c) tr.context();

        String header = getHeader(cd.classDef().asType());
        String guard = getHeaderGuard(header);
        h.write("#ifndef __"+guard); h.newline();
        h.write("#define __"+guard); h.newline();
        h.forceNewline(0);
        h.write("#include <x10rt17.h>"); h.newline();
        h.forceNewline(0);

        w.write("#include <"+header+">"); w.newline();
        w.forceNewline(0);
        //w.write("using namespace x10;"); w.newline();
        //w.write("using namespace x10::lang;"); w.newline();
        //w.write("using namespace x10::core::fun;"); w.newline();
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
        String incfile = tf.integratedOutputName(pkg, cd.name().toString(), WriterStreams.StreamClass.Closures.toString());
        w.write("#include \""+incfile+"\""); w.newline();
        w.forceNewline(0);
    }

    /** Write the footer for a source file. */
    public void writeFooter(X10ClassDecl cd, ClassifiedStream w, ClassifiedStream h, X10CPPTranslator tr) {
        X10CPPContext_c context = (X10CPPContext_c) tr.context();

        h.newline(0);

        String guard = getHeaderGuard(getHeader(cd.classDef().asType()));
        h.write("#endif // __"+guard); h.newline();
        h.forceNewline(0);

        // The declarations below are intentionally outside of the guard
        if (context.package_() != null) {
            QName qn = context.package_().fullName();
            Emitter.openNamespaces(h, qn);
            h.newline(0);
        }
        if (cd.typeParameters().size() > 0) {
            h.write("template <");
            String sep = "";
            for (TypeParamNode tn : cd.typeParameters()) {
                h.write(sep);
                sep = ", ";
                h.write("class ");
                h.write(tn.name().toString());
            }
            h.write("> ");
        }
        h.write("class "+Emitter.mangled_non_method_name(cd.name().toString())+";");
        h.newline();
        if (context.package_() != null) {
            h.newline(0);
            QName qn = context.package_().fullName();
            Emitter.closeNamespaces(h, qn);
            h.newline(0);
        }
    }

	void processClass(X10ClassDecl_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		// PV-IPA
		if (context.outermostContext().harvest == null) {  // TODO: Fix to handle separate compilation.
			// Presently this test's a hack to ensure that summaries pass occurs only for the outermost class
			X10SummarizingPass v = new X10SummarizingPass(tr);
			v.makeSummariesPass(n);
		}

		X10ClassDef def = (X10ClassDef) n.classDef();

		if (getCppRep(def, tr) != null) {
			// emit no c++ code as this is a native rep class
			return;
		}
		context.setinsideClosure(false);
		context.hasInits = false;

        ClassifiedStream w = this.w;
        ClassifiedStream h = context.inLocalClass() ? w : ws.getCurStream(WriterStreams.StreamClass.Header);

        if (!context.inLocalClass())
            writeHeader(n, w, h, (X10CPPTranslator) tr);

		if (!def.isNested() && hasExternMethods(n.body().members())) {
			// FIXME: extern methods in nested classes
			w.write("#include <" + X10ClassBodyExt_c.wrapperFileName(def.asType().toReference()) + ">");
			w.newline();
		}

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		ArrayList<Type> allIncludes = new ArrayList<Type>();
		if (def.isNested()) {
            assert (false) : ("Nested class alert!");
            if (!def.flags().isStatic())
                throw new InternalCompilerError("Instance Inner classes not supported");
        } else {
			if (n.superClass() != null) {
				ClassType ct = n.superClass().type().toClass();
				String cpp = getCppRep((X10ClassDef) ct.def(), tr);
				if (cpp == null) {
					String header = getHeader(ct);
                    String guard = getHeaderGuard(header)+"_NODEPS";
					h.write("#define "+guard); h.newline();
					h.write("#include <" + header + ">");
					h.newline();
					h.write("#undef "+guard); h.newline();
                    allIncludes.add(ct);
				}
                Set<ClassType> types = new HashSet<ClassType>();
                extractAllClassTypes(ct, types);
                types.remove(ct);
                for (ClassType t : types) {
                    X10ClassDef cd = ((X10ClassType)t).x10Def();
                    if (getCppRep(cd, tr) == null) {
                        declareClass(cd, h);
                        allIncludes.add(t);
                    }
                }
			}
			for (TypeNode i : n.interfaces()) {
				ClassType ct = i.type().toClass();
				String cpp = getCppRep((X10ClassDef) ct.def(), tr);
				if (cpp == null) {
					while (ct.isNested())
						ct = (ClassType) ct.container();
					String header = getHeader(ct);
					String guard = getHeaderGuard(header)+"_NODEPS";
					h.write("#define "+guard); h.newline();
					h.write("#include <" + header + ">");
					h.newline();
					h.write("#undef "+guard); h.newline();
                    allIncludes.add(ct);
				}
                Set<ClassType> types = new HashSet<ClassType>();
                extractAllClassTypes(ct, types);
                types.remove(ct);
                for (ClassType t : types) {
                    X10ClassDef cd = ((X10ClassType)t).x10Def();
                    if (getCppRep(cd, tr) == null) {
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

			// [DC] static final field initialisers should be in the cc file,
			// with everything else that needs a full definition (lookups,
			// construction, etc)

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
				Set<ClassType> types = new HashSet<ClassType>();
				for (int i = 0; i < typeNodes.size(); i++) {
					X10CanonicalTypeNode_c t = (X10CanonicalTypeNode_c) typeNodes.get(i);
					Type type = t.type();
					extractAllClassTypes(type, types);
				}
				for (ClassType ct : types) {
					if (xts.typeEquals(ct, def.asType()))
						continue;
					X10ClassDef cd = (X10ClassDef) ct.def();
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

		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();

        ((X10CPPTranslator)tr).setContext(n.enterChildScope(n.body(), context)); // FIXME
        /*
         * TODO: [IP] Add comment about dependences between the method calls.
         */
        processNestedClasses(n);

        if (extractGenericStaticDecls(def, h)) {
            extractGenericStaticInits(def, w);
        }
        ((X10CPPTranslator)tr).setContext(context); // FIXME

		context.pendingStaticDecls = opsd;

		h.newline();

		ArrayList asyncs = context.closures.asyncs;
		if (context.classesWithAsyncSwitches.size() != 0) {
			emitter.printSwitchMethod(def.asType(), ASYNC_SWITCH, VOID,
					ASYNC_PREFIX, asyncs, context.closures.asyncsParameters,
					context.closures.asyncContainers,
					"int niter",
					"for (int i = 0; i < niter; i++,_arg++) {", "}",
					context.classesWithAsyncSwitches, ws.getCurStream(WriterStreams.StreamClass.Closures));
			emitter.printAsyncsRegistration(def.asType(), asyncs,
					ws.getCurStream(WriterStreams.StreamClass.Closures));
		}
		if (context.closures.arrayCopyClosures.size() != 0)
			emitter.printSwitchMethod(def.asType(), ARRAY_COPY_SWITCH, VOID_PTR,
					ARRAY_COPY_PREFIX, asyncs, context.closures.asyncsParameters,
					context.closures.arrayCopyClosures,
					null,
					null, null,
					context.classesWithArrayCopySwitches, ws.getCurStream(WriterStreams.StreamClass.Closures));

		if (!def.isNested()) {
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
            h.write("#endif // HACK: close the main guard");
			// [IP] Ok to include here, since the class is already defined
			h.forceNewline();
			String guard = getHeaderGuard(getHeader(def.asType()))+"_NODEPS";
			h.write("#ifndef "+guard); h.newline();
            h.write("#define "+guard); h.newline();

			for (Type t : allIncludes) {
				ClassType ct = t.toClass();
				String cpp = getCppRep((X10ClassDef) ct.def(), tr);
				assert (cpp == null);
				assert (!ct.isNested());
                String header = getHeader(ct);
				h.write("#include <" + header + ">");
				h.newline();
			}

			h.write("//#endif // HACK: the main guard will be closed later: "+guard); h.newline();
		}
		w.newline(0);

        if (!context.inLocalClass())
            writeFooter(n, w, h, (X10CPPTranslator) tr);
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
		emitter.printHeader((ClassDecl_c)n.decl(), w, tr, false);
		w.allowBreak(0, " ");
		sw.pushCurrentStream(w);
		n.print(n.decl().body(), sw, tr);
		sw.popCurrentStream();
		context.popInLocalClass();

	}

    // Get the list of methods of name "name" that ought to be accessible from class c
    // due to being locally defined or inherited
    List<MethodInstance> getOROLMeths(Name name, X10ClassType c) {
        assert(name!=null);
        assert(c!=null);
        return getOROLMeths(name, c, new HashSet<List<Type>>());
    }

    List<MethodInstance> getOROLMeths(Name name, X10ClassType c, HashSet<List<Type>> shadowed) {
        assert(name!=null);
        assert(c!=null);
        assert(shadowed!=null);

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

		ClassifiedStream h;
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);

		h.write("{");
		h.newline(4);
		h.begin(0);
		emitter.printRTT(currentClass, h);
		w.begin(0);
		for (Iterator pi = context.classProperties.iterator(); pi.hasNext();) {
			PropertyDecl_c p = (PropertyDecl_c) pi.next();
			sw.pushCurrentStream(w);
			n.print(p, sw, tr);
			sw.popCurrentStream();
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

			if (extractInits(currentClass, RUN_INITIALIZERS, VOID, members, w, false)) {
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
					w.newline(0);
				}
				prev = member;
				sw.pushCurrentStream(w);
				n.printBlock(member, sw, tr);
				sw.popCurrentStream();
            }

            // generate proxy methods for an overridden method's superclass overloads
            if (superClass != null) {
                // first gather a set of all the method names in the current class
                Set<Name> mnames = new HashSet<Name>();
                for (ClassMember member : members) {
                    if (!(member instanceof X10MethodDecl)) continue;
                    X10MethodDecl mdcl = (X10MethodDecl) member;
                    Name mname = mdcl.name().id();
                    if (mnames.contains(mname)) continue;
                    MethodDef md = mdcl.methodDef();
                    MethodInstance mi = md.asInstance();
                    if (mi.flags().isStatic()) continue;
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

			if (extractInits(currentClass, STATIC_INIT, VOID, members, w, true)) {
                // declare static init function in header
				h.write("public : static " + VOID + " " + STATIC_INIT + "();");
				h.newline();
                // define field that triggers initalisation-time registration of
                // static init function
				w.write("static " + VOID_PTR + " __init__"+getUniqueId_() +
						" = x10aux::InitDispatcher::addInitializer(" +
						className+"::"+STATIC_INIT + ")" + ";");
				w.newline(); w.forceNewline(0);
			}


			if (!currentClass.isNested() && !context.inLocalClass())

			if (((X10TypeSystem) tr.typeSystem()).isValueType(currentClass)) {
				emitter.generateSerializationMethods(currentClass, w, h);
			}

			context.resetMainMethod();
		}

		h.end();
		w.end();
		w.newline();
		h.newline(0);
		h.write("};");
		h.newline();

        // FIXME: just use a different stream for the body
		if (currentClass.x10Def().typeParameters().isEmpty()) {
			emitter.printRTTDefn(currentClass, w);
		} else {
			emitter.printRTTDefn(currentClass, h);
		}

	}

	String defaultValue(Type type) {
		return type.isPrimitive() ? "0" : "x10aux::null";
	}




	public void visit(PackageNode_c n) {
		w.write(mangled_non_method_name(translateFQN(n.package_().get().fullName().toString())));
	}

	public void visit(Import_c n) {
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
		ClassifiedStream h;
		X10Flags flags = X10Flags.toX10Flags(dec.flags().flags());
		if (flags.isNative())
			return;
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);
		if (query.isMainMethod(dec))
		{
			Type container = dec.methodDef().asInstance().container();
		    if (container.isClass() && !((X10ClassType)container).typeArguments().isEmpty()) {
		    	List<Type> args = Arrays.asList(new Type[] { xts.Void() });
		    	container = ((X10ClassType)container).typeArguments(args);
		    }
			xcdProcessor.new Template("MainMP", emitter.translateType(container)).expand();
			h.newline();
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
		emitter.printHeader(dec, h, tr, methodName, ret_type, false);

		if (dec.body() != null) {
			if (!flags.isStatic()) {
				VarInstance ti = xts.localDef(Position.COMPILER_GENERATED, Flags.FINAL,
						new Ref_c<StructType>(dec.methodDef().asInstance().container()), Name.make(THIS)).asInstance();
				context.addVariable(ti);
			}
			if (!context.inLocalClass()) {
				h.write(";");
				h.newline();
				emitter.printHeader(dec, w, tr, methodName, ret_type, true);
				w.newline();
			} else {
				w.allowBreak(0, " ");
			}
			sw.pushCurrentStream(w);
			dec.printSubStmt(dec.body(), sw, tr);
			sw.popCurrentStream();
			w.newline();
		} else {
			// Check for properties accessed using method syntax.  They may have @Native annotations too.
			if (flags.isProperty() && flags.isAbstract() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
				X10FieldInstance fi = (X10FieldInstance) mi.container().fieldNamed(mi.name());
				if (fi != null) {
					// This is a property method in an interface.  Give it a body.
					h.write("{");
					h.allowBreak(0, " ");
					h.write("return "+mangled_field_name(fi.name().toString())+";");
					h.allowBreak(0, " ");
					h.write("}");
				}
			} else {
				h.write(";");
				h.newline();
			}
		}
	}



	public void visit(ConstructorDecl_c dec) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (dec.flags().flags().isNative())
			return;
		ClassifiedStream h = ws.getCurStream(WriterStreams.StreamClass.Header);
		if ((!context.inLocalClass()))
			emitter.printHeader(dec, h, tr, false);
		emitter.printHeader(dec, w, tr, true);
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
					w.allowBreak(4, " ");
					w.write(":");
					w.allowBreak(2, " ");
					w.write(emitter.translateType(container.superClass()) + "(");
					if (call.arguments().size() > 0) {
						w.allowBreak(2, 2, "", 0); // miser mode
						w.begin(0);
                        int counter = 0;
						for(Expr e : (List<Expr>) call.arguments() ) {
							if (counter==0) {
							} else {
								w.write(",");
								w.allowBreak(0, " ");
							}
                            ConstructorInstance calli = call.constructorInstance();
                            String type = emitter.translateType(calli.formalTypes().get(counter),true);
                            w.write("("+type+")(");
							sw.pushCurrentStream(w);
							dec.print(e, sw, tr);
							sw.popCurrentStream();
                            w.write(")");
                            counter++;
						}
						w.end();
					}
					w.write(")");
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
				w.newline();
			else
				w.allowBreak(0, " ");
			w.write("{"); w.newline(4); w.begin(0);
			if (context.hasInits) {
				w.write("this->"+RUN_INITIALIZERS+"();"); w.newline();
			}
			w.write("this->"+INIT+"(");
			w.begin(0);
			boolean first = true;
			for (Formal f : dec.formals()) {
				if (first) {
					first = false;
				} else {
					w.write(",");
					w.allowBreak(0, " ");
				}
				w.write(mangled_non_method_name(f.name().toString()));
			}
			w.end();
			w.write(");");
			w.end(); w.newline(); w.write("}");
			w.newline();

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
				sw.pushCurrentStream(h);
				dec.print(f, sw, tr);
				sw.popCurrentStream();
			}
			h.end();
			h.write(");");
			h.newline();
			if (!context.inLocalClass())
				emitter.printTemplateSignature(((X10ClassType)dec.constructorDef().container().get()).typeArguments(), w);
			w.write(VOID+" "+emitter.translateType(container)+"::"+INIT+"(");
			w.begin(0);
			first = true;
			for (Formal f : dec.formals()) {
				if (first) {
					first = false;
				} else {
					w.write(",");
					w.allowBreak(0, " ");
				}
				sw.pushCurrentStream(w);
				dec.print(f, sw, tr);
				sw.popCurrentStream();
			}
			w.end();
			w.write(")");
			w.allowBreak(0, " ");
			if (newStatements.size() > 0) {
				w.write("{"); w.newline(4); w.begin(0);
				for (Stmt s : newStatements) {
					if (s instanceof ConstructorCall) {
						ConstructorCall call = (ConstructorCall)s;
						assert (call.kind() == ConstructorCall.THIS);
						w.write("this->"+INIT+"(");
						if (call.arguments().size() > 0) {
							w.allowBreak(2, 2, "", 0); // miser mode
							w.begin(0);
							first = true;
							for(Expr e : (List<Expr>) call.arguments() ) {
								if (first) {
									first = false;
								} else {
									w.write(",");
									w.allowBreak(0, " ");
								}
								sw.pushCurrentStream(w);
								dec.print(e, sw, tr);
								sw.popCurrentStream();
							}
							w.end();
						}
						w.write(");");
					} else {
						sw.pushCurrentStream(w);
						dec.printBlock(s, sw, tr);
						sw.popCurrentStream();
					}
					w.newline();
				}
				w.end(); w.newline(); w.write("}");
			} else
				w.write("{ }");
		} else {
			w.write(" { }");
		}
		w.newline();
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
		ClassifiedStream h;
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);
		emitter.printHeader(dec, h, tr, false);
		if (dec.flags().flags().isStatic()) {
			emitter.printHeader(dec, w, tr, true);
			// [DC] disabled because I want this done through the static initialisation framework
            // [IP] re-enabled for a very limited set of cases, namely literal inits
			if (dec.init() != null && dec.flags().flags().isFinal() &&
			        (dec.init() instanceof NumLit_c || dec.init() instanceof BooleanLit_c))
            {
				w.write(" =");
				w.allowBreak(2, " ");
				sw.pushCurrentStream(w);
				dec.print(dec.init(), sw, tr);
				sw.popCurrentStream();
			}
			w.write(";");
			w.newline();
			return ;
		}
		// Ignore the initializer -- this will have been done in extractInits/extractStaticInits
		// FIXME: the above breaks switch constants!
		h.write(";");
		h.newline();
	}

	public void visit(PropertyDecl_c n) {
		super.visit(n);
	}

	public void visit(Initializer_c n) {
		if (n.flags().flags().isStatic()) {
			// Ignore -- this will have been processed earlier
		}
	}

	public void visit(Assert_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		if (!tr.job().extensionInfo().getOptions().assertions)
			return;
		w.write("x10aux::x10__assert(");
		sw.pushCurrentStream(w);
		n.print(n.cond(), sw, tr);
		sw.popCurrentStream();
		if (n.errorMessage() != null) {
			w.write(",");
			w.allowBreak(4, " ");
                        sw.pushCurrentStream(w);
			n.print(n.errorMessage(), sw, tr);
                        sw.popCurrentStream();
		}
		w.write(");");
		w.newline();
	}


	public void visit(Switch_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		{
			w.write("switch (");
			sw.pushCurrentStream(w);
			n.print(n.expr(), sw, tr);
			sw.popCurrentStream();
			w.write(")");
		}
		w.allowBreak(0, " ");
		w.write("{");
		w.newline();
		if (n.elements().size() > 0) {
			w.newline(4);
			w.begin(0);
			for (Iterator i = n.elements().iterator(); i.hasNext(); ) {
				Node s = (Node) i.next();
				sw.pushCurrentStream(w);
				n.print(s, sw, tr);
				sw.popCurrentStream();
			}
			w.end();
			w.newline(0);
		}
		else
			w.write(" ");

		w.write("}");
	}



	public void visit (AssignPropertyBody_c n) {
		sw.pushCurrentStream(w);
		n.translate(sw, tr);
		sw.popCurrentStream();
	}

	public void visit(SwitchBlock_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		w.write("{");
		if (n.statements().size() > 0) {
			w.newline(4);
			w.begin(0);
			for (Iterator i = n.statements().iterator(); i.hasNext(); ) {
				Node s = (Node) i.next();
				sw.pushCurrentStream(w);
				n.print(s, sw, tr);
				sw.popCurrentStream();
				if (i.hasNext())
					w.newline();
			}
			w.end();
			w.newline(0);
		}
		else
			w.write(" ");

		w.write("}");
	}



	public void visit(Case_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		w.newline();
		if (n.expr() == null) {
			w.write("default :");
		}
		else {
			w.write("case ");
			// FIXME: [IP] HACK HACK HACK! Substitute the actual constant if any
			if (n.expr() instanceof Field_c && n.expr().isConstant()) {
				w.write(""+n.expr().constantValue());
				w.write("/"+"*");
				sw.pushCurrentStream(w);
				n.print(n.expr(), sw, tr);
				sw.popCurrentStream();
				w.write("*"+"/");
			} else {
				sw.pushCurrentStream(w);
				n.print(n.expr(), sw, tr);
				sw.popCurrentStream();
			}
			w.write(":");
		}
		w.newline();
	}

	public void visit(Branch_c br) {
		// Note: The break statements inside a switch statement are always
		// non-labeled.
		// FIXME: [IP] The above assumption is incorrect!!!
		if (br.labelNode() != null) {
			if (br.kind().toString() == "continue")
				w.write("goto " + br.labelNode().id().toString() + "_next_");
			else
				w.write("goto " + br.labelNode().id().toString() + "_end_");
		} else
			w.write(br.kind().toString());
		w.write(";");
		w.newline();
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

		w.write(label.labelNode() + " : ");
		w.newline();
		((X10CPPContext_c) tr.context()).setLabel(label.labelNode().id().toString(), label.statement());
		sw.pushCurrentStream(w);
		label.print(label.statement(), sw, tr);
		sw.popCurrentStream();
		w.newline();
	}

	public void visit(Assign_c asgn) {
		X10CPPContext_c context = (X10CPPContext_c)tr.context();

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
			w.write("("+emitter.translateType(asgn.type())+")(");
			w.write("(("+emitter.makeUnsignedType(lhs.type())+"&)");
		}
		sw.pushCurrentStream(w);
		asgn.printSubExpr(lhs, false, sw, tr);
		sw.popCurrentStream();
		if (unsigned_op)
			 w.write(")");
		w.write(" ");
		// [IP] Are all the operators the same?
		w.write(opString);
		w.allowBreak(2, 2, " ", 1);
 		if (unsigned_op)
			 w.write("(("+emitter.makeUnsignedType(rhs.type())+")");
		sw.pushCurrentStream(w);
		asgn.printSubExpr(rhs, true, sw, tr);
		sw.popCurrentStream();
		if (unsigned_op)
			 w.write("))");
	}


	public void visit(Return_c ret) {
		X10CPPContext_c context = (X10CPPContext_c)tr.context();

		Expr e = ret.expr();

		w.write("return");
		if (e != null) {
			w.write(" ");
			sw.pushCurrentStream(w);
			ret.print(e, sw, tr);
			sw.popCurrentStream();
		}
		w.write(";");
		w.newline();
	}




	public void visit(Formal_c n) {
		emitter.printHeader(n, w, tr, true);
	}


	public void visit(LocalDecl_c dec) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

		Expr initexpr = dec.init();
		Type base_type = null;
		emitter.printHeader(dec, w, tr, true);

		// FIXME: Need to handle nullable cast in this situation.
		if (initexpr != null) {
			w.write(" =");
			w.allowBreak(2, " ");
			{
				sw.pushCurrentStream(w);
				dec.print(initexpr, sw, tr);
				sw.popCurrentStream();
			}
		}

		if (tr.appendSemicolon()) {
			w.write(";");
			w.newline(0);
		}
	}


	public void visit(Block_c b) {

		w.write("{");
		w.newline();
		if (b.statements().size() > 0) {
			w.newline(4);
//			w.unifiedBreak(4, 1, " ", 1);
			w.begin(0);
			for (Iterator i = b.statements().iterator(); i.hasNext(); ) {
				Stmt n = (Stmt) i.next();
				sw.pushCurrentStream(w);
				b.printBlock(n, sw, tr);
				sw.popCurrentStream();
				if (i.hasNext())
					w.newline();
			}
			w.end();
//			w.unifiedBreak(0, 1, " ", 1);
			w.newline(0);
		}
		else
			w.write(" ");
		w.newline();
		w.write("}");
	}


	public void visit(StmtSeq_c b) {
		assert (false); // FIXME. It is not clear if when StmtSeq_c nodes are generated.  If they indeed are, remove this assert and continue with the method below.

		//w.write("{");
		w.newline();
		if (b.statements().size() > 0) {
			w.newline(4);
//			w.unifiedBreak(4, 1, " ", 1);
			w.begin(0);
			for (Iterator i = b.statements().iterator(); i.hasNext(); ) {
				Stmt n = (Stmt) i.next();
				sw.pushCurrentStream(w);
				b.printBlock(n, sw, tr);
				sw.popCurrentStream();
				if (i.hasNext())
					w.newline();
			}
			w.end();
//			w.unifiedBreak(0, 1, " ", 1);
			w.newline(0);
		}
		else
			w.write(" ");
		w.newline();
		//w.write("}");
	}

	/*
	public void visit (NullableNode_c n) {
		tr.job().compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR,
				"NullableNode_c's visitor in SPMDCppCodeGenerator should not be reached. ", n.position());
	}
	*/




	public void visit(X10Binary_c n) {
		visit((Binary_c)n);
	}






	private void handleLabeledLoop(Loop_c n) {
		String label = null;
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (context.getLabeledStatement() == n) {
			label = context.getLabel();
			context.setLabel(null, null);
		}
		w.allowBreak(0, " ");
		if (label != null) {
			w.write("{");
			w.newline(0); w.begin(0);
			w.newline();
		}
		w.newline();
		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();
		w.newline();
		if (label != null) {
			w.newline(0);
			w.write(label + "_next_ : ;");
			w.end(); w.newline();
			w.write("}");
			w.newline(0);
			w.write(label + "_end_ : ; ");
		}
	}

	public void visit(For_c n) {

		// FIXME: Generate normal for-loop code, without
		// separating out the inits. [Krishna]
		X10CPPContext_c context = (X10CPPContext_c) tr.context();


		w.write("{");
		w.newline(4); w.begin(0);

		if (n.inits() != null) {
			for (Iterator i = n.inits().iterator(); i.hasNext(); ) {
				ForInit s = (ForInit) i.next();
				if (s instanceof LocalDecl_c) {
					LocalDecl_c dec = (LocalDecl_c)s;
					emitter.printHeader(dec, w, tr, true);
					w.write(";");
					w.newline(0);
				}
			}
		}


		w.newline(0);
		w.write("for (");
		w.begin(0);

		if (n.inits() != null) {
			boolean first = true;
			for (Iterator i = n.inits().iterator(); i.hasNext(); ) {
				ForInit s = (ForInit) i.next();
				boolean oldSemiColon = tr.appendSemicolon(false);
				boolean oldPrintType = tr.printType(false);
				sw.pushCurrentStream(w);
				n.printBlock(s, sw, tr);
				sw.popCurrentStream();
				tr.printType(oldPrintType);
				tr.appendSemicolon(oldSemiColon);
				first = false;

				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(2, " ");
				}
			}
		}

		w.write(";");
		w.allowBreak(0);

		if (n.cond() != null) {
			sw.pushCurrentStream(w);
			n.printBlock(n.cond(), sw, tr);
			sw.popCurrentStream();
		}

		w.write(";");
		w.allowBreak(0);

		if (n.iters() != null) {
			for (Iterator i = n.iters().iterator(); i.hasNext(); ) {
				ForUpdate s = (ForUpdate) i.next();
				boolean oldSemiColon = tr.appendSemicolon(false);
				sw.pushCurrentStream(w);
				n.printBlock(s, sw, tr);
				sw.popCurrentStream();
				tr.appendSemicolon(oldSemiColon);

				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(2, " ");
				}
			}
		}

		w.end();
		w.write(")");
		w.write("{");
		w.newline(0);


		handleLabeledLoop(n);


		w.end(); w.newline(0);
		w.write("}");
		w.newline(0);


		w.write("}");
		w.newline(0);
	}


	public void visit(Do_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		{
			w.write("do {");
			handleLabeledLoop(n);
			w.write("} while (");
			sw.pushCurrentStream(w);
			n.printBlock(n.cond(), sw, tr);
			sw.popCurrentStream();
			w.write(");");
		}
	}


	public void visit(While_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		{
			w.write("while (");
			sw.pushCurrentStream(w);
			n.printBlock(n.cond(), sw, tr);
			sw.popCurrentStream();
			w.write(")");
			handleLabeledLoop(n);
		}
	}


	public void visit(If_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		context.incrCurCond();
		context.incrCondDepth();
		int current_cond_indx = context.curCond();

		boolean bodyForOnlyPlaceZero = !query.hasCodeForAllPlaces(n);



		{
			w.write("if (");
			sw.pushCurrentStream(w);
			n.printBlock(n.cond(), sw, tr);
			sw.popCurrentStream();
			w.write(")");
			w.allowBreak(0, " ");
		}

		w.allowBreak(0, "");
		sw.pushCurrentStream(w);
		n.print(n.consequent(), sw, tr);
		sw.popCurrentStream();
		if (n.alternative() != null) {
			{
				w.allowBreak(0, " ");
				w.write("else");
				w.allowBreak(0, " ");
			}
			// [IP] Semi-HACK: handle "else if" specially
			Stmt alternative = n.alternative();
			if (alternative instanceof Block_c) {
				Block_c block = (Block_c) alternative;
				if (block.statements().size() == 1 && block.statements().get(0) instanceof If_c)
					alternative = (Stmt) block.statements().get(0);
			}
			sw.pushCurrentStream(w);
			n.print(alternative, sw, tr);
			sw.popCurrentStream();
		}

		w.newline(0);
		//context.decrCurCond();
		context.decrCondDepth();
		// You do not decrease the curCond. Always have a new one.
	}


	public void visit(Empty_c n) {
		w.write(";");
	}

	public void visit(Eval_c n) {
		// TODO: check for assignment x = a.sum()
		boolean semi = tr.appendSemicolon(true);
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		sw.pushCurrentStream(w);
		n.print(n.expr(), sw, tr);
		sw.popCurrentStream();
		if (semi)
			w.write(";");
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
		// TODO: only emit static_cast where necessary


        Type ret_type = emitter.findRootMethodReturnType(md, null, mi);

		boolean needsCast = !xts.typeDeepBaseEquals(mi.returnType(),ret_type);
		if (needsCast) {
			w.write("static_cast<");
			emitter.printType(mi.returnType(), w);
			w.write(" >(");
		}
		w.begin(0);
		if (!n.isTargetImplicit()) {
			// explicit target.

			emitter.printExplicitTarget(n, target, context, w);

			if ((mi.flags().isStatic() && !(target instanceof Expr)) ||
					(target instanceof X10Special_c &&
							((X10Special_c)target).kind().equals(X10Special_c.SUPER))) {
				w.write("::");
			} else {
				w.write("->");
			}
		
		}

        if (context.inTemplate() && mi.typeParameters().size() != 0) {
            w.write("template ");
        }
		w.write(mangled_method_name(n.name().id().toString()));
		emitter.printTemplateInstantiation(mi, w);
		w.write("(");
		if (n.arguments().size() > 0) {
			w.allowBreak(2, 2, "", 0); // miser mode
			w.begin(0);
            int counter = 0;
			for (Iterator i = n.arguments().iterator(); i.hasNext(); ) {
				Expr e = (Expr) i.next();
                String type = emitter.translateType(mi.formalTypes().get(counter),true);
                w.write("("+type+")(");
				sw.pushCurrentStream(w);
				n.print(e, sw, tr);
				sw.popCurrentStream();
                w.write(")");
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
                counter++;
			}
			w.end();
		}
		w.write(")");
		w.end();
		if (needsCast) {
			w.write(")");
		}
	}




	public void visit(RegionMaker_c n) {
//		X10CPPContext_c context = (X10CPPContext_c) tr.context();
//
//		assert (n.arguments().size() == 2);
//		String region_type = "_region<1>";
//		w.write("("+make_ref(region_type)+")(");
//		w.begin(0);
//		w.write("new (x10aux::alloc<"+region_type+" >()) "+region_type+"(");
//		w.allowBreak(2, 2, "", 0); // miser mode
//		w.begin(0);
//		for (Iterator i = n.arguments().iterator(); i.hasNext(); ) {
//			sw.pushCurrentStream(w);
//			n.print((Expr) i.next(), sw, tr);
//			sw.popCurrentStream();
//			if (i.hasNext()) {
//				w.write(",");
//				w.allowBreak(0, " ");
//			}
//		}
//		w.end();
//		w.write(")");
//		w.end();
//		w.write(")");
//		return;
		super.visit(n);
	}


	public void visit(ConstantDistMaker_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		assert (n.arguments().size() == 2);
		String dist_type = "_dist_local";
		w.write("("+make_ref(dist_type)+")(");
		w.begin(0);
		w.write("new (x10aux::alloc<"+dist_type+" >()) "+dist_type+"(");
		w.allowBreak(2, 2, "", 0); // miser mode
		w.begin(0);
		Expr r = (Expr) n.arguments().get(0);
		Expr p = (Expr) n.arguments().get(1);
		sw.pushCurrentStream(w);
		n.printSubExpr(r, true, sw, tr);
		sw.popCurrentStream();
		w.write(",");
		w.allowBreak(0, " ");
		sw.pushCurrentStream(w);
		n.print(p, sw, tr);
		sw.popCurrentStream();
		w.end();
		w.write(")");
		w.end();
		w.write(")");
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
		    emitter.dumpRegex("Native", components, tr, pat, w);
		    return;
		}


		w.begin(0);
		if (!n.isTargetImplicit()) {
			// explicit target.
			if (target instanceof Expr) {
                if (fi.flags().isStatic()) {
                    w.write("((void)");
                    sw.pushCurrentStream(w);
                    n.printSubExpr((Expr) target, false, sw, tr);
                    sw.popCurrentStream();
                    w.write(",");
                    w.write(emitter.translateType(target.type()));
                    w.write("::");
                    w.allowBreak(2, 3, "", 0);
                    w.write(mangled_field_name(n.name().id().toString()));
                    w.write(")");
                    w.end();
                    return;
                } else {
                    boolean assoc =
                        !(target instanceof New_c ||
                            target instanceof Binary_c);
                    sw.pushCurrentStream(w);
                    n.printSubExpr((Expr) target, assoc, sw, tr);
                    sw.popCurrentStream();
                }
			}
			else if (target instanceof TypeNode || target instanceof AmbReceiver) {
				sw.pushCurrentStream(w);
				n.print(target, sw, tr);
				sw.popCurrentStream();
			}
			if (n.fieldInstance().flags().isStatic())
				w.write("::");
			else
				w.write("->");
			w.allowBreak(2, 3, "", 0);
		} else {
			// TODO: capture constant fields as variables
			if (!n.flags().isStatic()) {
				X10CPPContext_c c = (X10CPPContext_c) tr.context();
				if (target instanceof X10Special_c && ((X10Special_c)target).isSelf()) {
					w.write((context.Self() == null)? "self":context.Self());
					w.write("->");
					// FIXME: Do we need to save the
					// context.Self() in the env?
					// [Krishna]
				} else
				if (c.insideClosure) {
					w.write(SAVED_THIS+"->");
					if (c.insideClosure)
						c.saveEnvVariableInfo(THIS);
				}
			} else {
				w.write(emitter.translateType(n.fieldInstance().container()) + "::");
			}
		}
		w.write(mangled_field_name(n.name().id().toString()));
		w.end();
	}

	public void visit(Local_c n) {
		X10CPPContext_c c = (X10CPPContext_c) tr.context();
		LocalInstance var = n.localInstance();
		if (c.insideClosure) {
			c.saveEnvVariableInfo(n.name().toString());
		}
		w.write(c.getCurrentName(var));
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
		w.begin(0);
		w.write("("+make_ref(type)+")( ");
		w.allowBreak(4, "");
		w.write("new (x10aux::alloc<"+type+(type.endsWith(">")?" ":"")+">())");
		w.allowBreak(4, "");
		w.write(type+"(");
		w.begin(0);
		for (Iterator i = n.arguments().iterator(); i.hasNext(); ) {
			Expr e = (Expr) i.next();
			sw.pushCurrentStream(w);
			n.print(e, sw, tr);
			sw.popCurrentStream();
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		// FIXME: [IP] Temporary hack until we have full stack traces
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (ts.isSubtype(n.objectType().type(), ts.Throwable()) && n.arguments().size() == 0) {
			String stringType = emitter.translateType(ts.String());
			w.write("String::Lit(__FILELINE__)");
		}
		w.end();
		w.write(")");
		w.write(" )");
		w.end();
	}


	public void visit(FloatLit_c n) {
		String val;
		if (n.kind() == FloatLit_c.FLOAT)
			val = Float.toString((float) n.value()) + "f";
		else if (n.kind() == FloatLit_c.DOUBLE)
			val = Double.toString(n.value());
		else
			throw new InternalCompilerError("Unrecognized FloatLit kind " + n.kind());
		w.write(val);
	}

	public void visit(IntLit_c n) {
		String val;
		if (n.kind() == IntLit_c.LONG)
			val = Long.toString(n.value()) + "ll";
		else if (n.kind() == IntLit_c.INT)
			val = Long.toString((int) n.value());
		else
			throw new InternalCompilerError("Unrecognized IntLit kind " + n.kind());
		w.write("("); w.begin(0);
		w.write("(" + emitter.translateType(n.type(),true) + ")");
		w.write(val);
		w.end(); w.write(")");
	}

	public void visit(NullLit_c n) {
		w.write("x10aux::null");
	}

	public void visit(StringLit_c n) {
		w.write("String::Lit(\"");
		w.write(StringUtil.escape(n.stringValue()));
		w.write("\")");
	}

	public void visit(CharLit_c lit) {
		w.write("'"+StringUtil.escape(lit.charValue())+"'");
	}

	public void visit(BooleanLit_c lit) {
		w.write(lit.toString());
	}

	public void visit(Id_c n) {
		w.write(mangled_non_method_name(n.id().toString()));
	}

	public void visit(X10Cast_c c) {
		// Original code picked up from
		// x10.compiler.p3/.../X10PrettyPrinter.java and mouled
		// onto the cppbackend requirements. [Krishna]
		// [DC] now commented out to use a c++ function
		String castVar = null;
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
					sw.pushCurrentStream(w);
					c.printSubExpr(c.expr(), true, sw, tr);
					sw.popCurrentStream();
				} else {
					w.write("x10aux::class_cast<");
					emitter.printType(t, w);
					w.write(" >(");
					sw.pushCurrentStream(w);
					c.printSubExpr(c.expr(), true, sw, tr);
					sw.popCurrentStream();
					w.write(")");
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
			w.write("!!dynamic_cast<");
			w.write(emitter.translateType(n.compareType().type(), true));
			w.write(" >(");
			w.begin(0);
			sw.pushCurrentStream(w);
			n.printSubExpr(n.expr(), sw, tr);
			sw.popCurrentStream();
			w.end();
			w.write(")");
			return;
		}
		// equivalent of (x instanceof B) -> (!!dynamic_cast<B>(x))
		// but the above doesn't work for refs
		w.write("INSTANCEOF(");
		w.begin(0);
		sw.pushCurrentStream(w);
		n.printSubExpr(n.expr(), false, sw, tr);
		sw.popCurrentStream();
		w.write(",");
		w.allowBreak(0, " ");
		w.write(emitter.translateType(n.compareType().type(), true));
		w.end();
		w.write(")");
	}

	public void visit(Throw_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		//((X10Context_c) tr.context()).resetInlinableAsyncsOnly();
		w.write("x10aux::throwException(");
		sw.pushCurrentStream(w);
		n.print(n.expr(), sw, tr);
		sw.popCurrentStream();
		w.write(");");
	}

	public void visit(Try_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (n.finallyBlock() != null){
			w.write("{");
			// Create a class and use the finally block as the
			// destructor. The object is created at the
			// beginning. Use local classes.
			w.newline(4); w.begin(0);
			String tempClass = getId();
			String tempClassDef = tempClass+"def";
			w.write("struct " + tempClassDef);
			w.write("{");
			w.newline(4); w.begin(0);

			w.write(tempClassDef+ "(){}");
			w.newline();
			w.write("~" + tempClassDef + "()");
			sw.pushCurrentStream(w);
			n.print(n.finallyBlock(), sw, tr);
			sw.popCurrentStream();

			w.end(); w.newline();
			w.write("} ");
		 	w.write(tempClass + ";");
			w.newline();
		}
		w.write("try {");
		w.newline(0);
		assert (n.tryBlock() instanceof Block_c);
		sw.pushCurrentStream(w);
		n.printSubStmt(n.tryBlock(), sw, tr);
		sw.popCurrentStream();
		w.newline(0);
		w.write("}");
		w.newline(0);

		// [IP] C++ will not catch ref types properly, as there is no hierarchy.
		// So, we have to do the dispatching ourselves.
		w.newline();
		String refVar = "__ref__" + getUniqueId_();
		w.write("catch (x10aux::__ref& " + refVar + ") {");
		w.newline(4); w.begin(0);
		String excVar = "__exc" + refVar;
		// Note that the following c-style cast only works because Throwable is
		// *not* an interface and thus is not virtually inheritted.  If it
		// were, we would have to static_cast the exception to Throwable on
		// throw (otherwise we would need to offset by an unknown quantity).
		String exception_ref = make_ref("Throwable");
		w.write(exception_ref+"& " + excVar + " = ("+exception_ref+"&)" + refVar + ";");
		((X10CPPContext_c)tr.context()).setExceptionVar(excVar);
		for (Iterator it = n.catchBlocks().iterator(); it.hasNext(); ) {
			Catch cb = (Catch) it.next();
			w.newline(0);
			sw.pushCurrentStream(w);
			n.printBlock(cb, sw, tr);
			sw.popCurrentStream();
		}
		w.newline(4);
		w.write("throw;");
		w.end(); w.newline();
		w.write("}");
		if (n.finallyBlock() != null){
			w.end(); w.newline();
			w.write("}");
		}

	}

	public void visit(Catch_c n) {
		String excVar = ((X10CPPContext_c)tr.context()).getExceptionVar();
		w.newline();
		w.write("if (");
		String type = emitter.translateType(n.formal().type().type(), true);
		if (refsAsPointers) {
			w.write("!!dynamic_cast<" + type + " >(" + excVar + ")");
		} else {
			w.write("INSTANCEOF(" + excVar + ",");
			w.allowBreak(0, " ");
			w.write(type);
			w.write(")");
		}
		w.write(") {");
		w.newline(4); w.begin(0);
		sw.pushCurrentStream(w);
		n.printBlock(n.formal(), sw, tr);
		sw.popCurrentStream();
		w.write(" =");
		w.allowBreak(2, " ");
		w.write("static_cast<" + type + " >(" + excVar + ");");
		w.newline(0);
		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();
		w.end(); w.newline();
		w.write("} else");
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

			w.write("{");
			w.newline(4); w.begin(0);

			String domain = getId();
			LocalDef[] lis = form.localInstances();
			List<Formal> vars = form.vars();
			int rank = lis.length;
			String[] limit = new String[rank];
			emitter.printType(n.domain().type(), w);
			w.write(" " + domain + ";");
			w.newline();
			for (int i = 0; i < rank; i++) {
				LocalInstance f = lis[i].asInstance();
				assert (f.type().isInt());
				limit[i] = getId();
				emitter.printType(f.type(), w);
				w.write(" " + limit[i] + ";");
				w.newline();
				emitter.printType(f.type(), w);
				w.write(" ");
				w.write(mangled_non_method_name(f.name().toString()));
				w.write(";");
				w.newline();
			}

			w.write(domain + " = ");
			sw.pushCurrentStream(w);
			n.print(n.domain(), sw, tr);
			sw.popCurrentStream();
			w.write(";");
			w.newline();
			for (int i = 0; i < rank; i++) {
				LocalInstance f = lis[i].asInstance();
				assert (f.type().isInt());
				w.write(limit[i] + " = " + domain + "->rank(" + i + ")->high();");
				w.newline();
				w.write("for (");
				w.write(mangled_non_method_name(f.name().toString()));
				w.write(" = " + domain + "->rank(" + i + ")->low(); ");
				w.write(mangled_non_method_name(f.name().toString()));
				w.write(" <= " + limit[i] + "; ");
				w.write(mangled_non_method_name(f.name().toString()));
				w.write("++) {");
				w.newline(4); w.begin(0);
			}

			form.addDecls(tr.context());
			sw.pushCurrentStream(w);
			n.print(n.body(), sw, tr);
			sw.popCurrentStream();

			for (int i = 0; i < rank; i++) {
				w.end(); w.newline();
				w.write("}");
			}

			w.end(); w.newline(0);
			w.write("}");
			w.newline(0);
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

        w.write("{");
		w.newline(4); w.begin(0);

		String name = "__i" + form.name();
		w.write("Iterator<");
		String fType = emitter.translateType(form.type().type(), true);
		w.write(fType + (fType.endsWith(">") ? " " : ""));
		w.write(">* " + name + ";");
		w.newline();
		w.write(name + " = &*"); // FIXME
        if (!xts.typeDeepBaseEquals(form.type().type(), itType))
            w.write("x10aux::convert_iterator<"+fType+","+emitter.translateType(itType, true)+" >");
        w.write("((");
		sw.pushCurrentStream(w);
		n.print(n.domain(), sw, tr);
		sw.popCurrentStream();
		w.write(")->iterator());");
		w.newline();

		w.write("for (");
		w.begin(0);

		w.write(";"); w.allowBreak(2, " ");
		w.write(name + "->hasNext();");
		w.allowBreak(2, " ");

		w.end();
		w.write(") {");
		w.newline(4); w.begin(0);

		sw.pushCurrentStream(w);
		n.print(form, sw, tr);
		sw.popCurrentStream();
		w.write(";");
		w.newline();
		w.write(mangled_non_method_name(form.name().id().toString()));
		w.write(" = " + name + "->next();");
		w.newline();
		for (Iterator li = n.locals().iterator(); li.hasNext(); ) {
			Stmt l = (Stmt) li.next();
			sw.pushCurrentStream(w);
			n.print(l, sw, tr);
			sw.popCurrentStream();
		}

		handleLabeledLoop(n);

		w.end(); w.newline(0);
		w.write("}");
		w.newline(0);

		// [IP] It's always safe to free the iterator because it can't escape
		w.write("x10aux::dealloc(" + name + ");");
		w.newline();

		w.end(); w.newline(0);
		w.write("}");
		w.newline(0);
	}



	public void visit(ForEach_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10Formal form = (X10Formal) n.formal();
		// FIXME: Handle clocks. [Krishna]
		assert (n.clocks() == null || n.clocks().size() == 0);

		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars() && form.isUnnamed()) {
			X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
			assert (xts.isPoint(form.type().type()));
			assert (xts.isDistribution(n.domain().type()) || xts.isRegion(n.domain().type()));

			w.write("{");
			w.newline(4); w.begin(0);

			String domain = getId();
			emitter.printType(n.domain().type(), w);
			w.write(" " + domain + " = ");
			sw.pushCurrentStream(w);
			n.print(n.domain(), sw, tr);
			sw.popCurrentStream();
			w.write(";");
			w.newline();

			LocalDef [] lis = form.localInstances();
			List<Formal> vars = form.vars();
			int rank = lis.length;
			for (int i = 0; i < rank; i++) {
				LocalInstance f = lis[i].asInstance();
				assert (f.type().isInt());
				String limit = getId();
				emitter.printType(f.type(), w);
				w.write(" " + limit + " = " + domain + "->rank(" + i + ")->high();");
				w.newline();
				w.write("for (");
				emitter.printType(f.type(), w);
				w.write(" ");
				w.write(mangled_non_method_name(f.name().toString()));
				w.write(" = " + domain + "->rank(" + i + ")->low(); ");
				w.write(mangled_non_method_name(f.name().toString()));
				w.write(" <= " + limit + "; ");
				w.write(mangled_non_method_name(f.name().toString()));
				w.write("++) {");
				w.newline(4); w.begin(0);
			}

			form.addDecls(tr.context());
			sw.pushCurrentStream(w);
			n.print(n.body(), sw, tr);
			sw.popCurrentStream();

			for (int i = 0; i < rank; i++) {
				w.end(); w.newline();
				w.write("}");
			}

			w.end(); w.newline(0);
			w.write("}");
			w.newline(0);
			return;
		}

		w.write("{");
		w.newline(4); w.begin(0);

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		assert (xts.isPoint(form.type().type()));
		assert (xts.isDistribution(n.domain().type()) || xts.isRegion(n.domain().type()));
		String name = "__i" + form.name();
		w.write("Iterator<point>* " + name + " = &(");
		sw.pushCurrentStream(w);
		n.print(n.domain(), sw, tr);
		sw.popCurrentStream();
		w.write(")->iterator();");
		w.newline();

		w.write("for (");
		w.begin(0);

		w.write(";"); w.allowBreak(2, " ");
		w.write(name + "->hasNext();");
		w.allowBreak(2, " ");

		w.end();
		w.write(") {");
		w.newline(4); w.begin(0);

		sw.pushCurrentStream(w);
		n.print(form, sw, tr);
		sw.popCurrentStream();
		w.write(" = &" + name + "->next();");
		w.newline();
		for (Iterator li = n.locals().iterator(); li.hasNext(); ) {
			Stmt l = (Stmt) li.next();
			sw.pushCurrentStream(w);
			n.print(l, sw, tr);
			sw.popCurrentStream();
		}

		handleLabeledLoop(n);

		w.end(); w.newline(0);
		w.write("}");
		w.newline(0);

		w.write("x10aux::dealloc(" + name + ");");
		w.newline();

		w.end(); w.newline(0);
		w.write("}");
		w.newline(0);
	}


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

		w.write("x10::lang::dist " + dist + " = ");
		sw.pushCurrentStream(w);
		n.print(n.domain(), sw, tr);
		sw.popCurrentStream();
		w.write(";");
		w.newline();

		String itr = getId();
		w.write("for (x10::lang::Iterator<x10::lang::point> " + itr);
		w.write(" = " + dist + ".iterator() ; " +
				itr + ".hasNext();) ");
		w.write("{"); w.newline(4); w.begin(0);


		sw.pushCurrentStream(w);
		n.print(n.formal(), sw, tr);
		sw.popCurrentStream();
		w.write (" = ");


		w.write ("(");
		sw.pushCurrentStream(w);
		n.print(n.formal().type(), sw, tr);
		sw.popCurrentStream();
		w.write (")");
		w.write (itr + ".next();");
		w.newline();

		context.addVar(n.formal().name().id().toString());

		for (Iterator li = n.locals().iterator(); li.hasNext(); ) {
			Stmt l = (Stmt) li.next();
			sw.pushCurrentStream(w);
			n.print(l, sw, tr);
			sw.popCurrentStream();
		}

		emitter.processAsync(n,
		   mangled_non_method_name(n.formal().name().toString()),
		   n.body(), context, ws, w);

		w.end(); w.newline(); w.write("}");
		w.newline();
	}



	public void visit(Finish_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		w.write("/* TODO: implement finish */");
		w.newline();
/*

		emitter.emit_general_finish_start(w);
		w.newline();
		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();
		w.newline();
		emitter.emit_general_finish_end(w);
		w.newline();
*/

	}



	public void visit(ArrayAccess_c n) {
		assert (false);
	}


	public void visit(ParExpr_c n) {
		//w.write(" ( ");
		sw.pushCurrentStream(w);
		n.print(n.expr(), sw, tr);
		sw.popCurrentStream();
		//w.write(" ) ");
	}

	public void visit(Conditional_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		sw.pushCurrentStream(w);
		n.printSubExpr(n.cond(), false, sw, tr);
		sw.popCurrentStream();
		w.unifiedBreak(2);
		w.write("? ");
        w.write("("+emitter.translateType(n.type(), true)+")(");
        w.begin(0);
		sw.pushCurrentStream(w);
		n.printSubExpr(n.consequent(), true, sw, tr);
		sw.popCurrentStream();
        w.end();
        w.write(")");
		w.unifiedBreak(2);
		w.write(": ");
        w.write("("+emitter.translateType(n.type(), true)+")(");
        w.begin(0);
		sw.pushCurrentStream(w);
		n.printSubExpr(n.alternative(), true, sw, tr);
		sw.popCurrentStream();
        w.end();
        w.write(")");
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
                w.write(SAVED_THIS);
                context.saveEnvVariableInfo(THIS);
            } else {
                w.write("(("+emitter.translateType(n.type(),true)+")"+n.kind()+")");
            }
        } else if (n.kind().equals(X10Special_c.SUPER)) {
            w.write(emitter.translateType(context.currentClass().superClass()));
        } else if (n.isSelf()) {
            // FIXME: Why are we printing the string "self"?
            // Confirm with Igor. [Krishna]
            w.write((context.Self() == null)? "self":context.Self());
        } else assert (false) : n.kind();

    }

	public String getClosureName(String className, int id) {
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


		// create closure and packed arguments

		// Prepend this stream to closures.  Closures are created from the outside in.
		// Thus, later closures can be used by earlier ones, but not vice versa.
		ClassifiedStream inc = ws.getNewStream(WriterStreams.StreamClass.Closures, true);


		Type retType = n.returnType().type();
		//String className = emitter.translateType(c.currentClass());
		String superType = n.returnType().type().isVoid() ?
				"x10::lang::" + mangled_non_method_name("VoidFun_0_" + n.formals().size()) :
					"x10::lang::" + mangled_non_method_name("Fun_0_" + n.formals().size());
		String prefix = "<";
		for (Formal formal : n.formals()) {
			superType = superType + prefix + emitter.translateType(formal.type().typeRef().get(), true);
			prefix = ", ";
		}
		if (!n.returnType().type().isVoid()) {
			superType = superType + prefix + emitter.translateType(retType, true);
			prefix = ", ";
		}
		if (!prefix.equals("<")) superType = superType +" >"; // don't emit " >" for void->void case

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
			sw.pushCurrentStream(inc);
			n.print(formal, sw, tr);
			sw.popCurrentStream();
			prefix = ", ";
		}
		inc.write(") ");
		sw.pushCurrentStream(inc);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();
		inc.newline(); inc.forceNewline();

		inc.write("// captured environment"); inc.newline();
		emitter.printDeclarationList(inc, c, c.variables);
		inc.forceNewline();

		inc.write("void _serialize_fields("+SERIALIZATION_BUFFER+" &buf, ");
        inc.write("x10aux::addr_map& m) {");
		inc.newline(4); inc.begin(0);
        inc.write("x10aux::AnyClosure::_serialize_fields(buf,m);"); inc.newline();
        for (int i=0 ; i<c.variables.size() ; i++) {
			if (i>0) inc.newline();
            VarInstance var = (VarInstance)c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name=mangled_non_method_name(name);
            inc.write("buf.write(" + name + ");");
        }
		inc.end(); inc.newline();
		inc.write("}"); inc.newline(); inc.forceNewline();

		inc.write("void _deserialize_fields("+SERIALIZATION_BUFFER+" &buf) {");
		inc.newline(4); inc.begin(0);
        inc.write("x10aux::AnyClosure::_deserialize_fields(buf);"); inc.newline();
        for (int i=0 ; i<c.variables.size() ; i++) {
			if (i>0) inc.newline();
            VarInstance var = (VarInstance)c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name=mangled_non_method_name(name);
            inc.write(name+" = buf.read<"+emitter.translateType(var.type(), true)+" >();");
        }
		inc.end(); inc.newline();
		inc.write("}"); inc.newline(); inc.forceNewline();


		inc.write(cname+"("+SERIALIZATION_MARKER+") : x10aux::AnyClosure("+id+") { }");
		inc.newline(); inc.forceNewline();

		inc.write(cname+"(");
        for (int i=0 ; i<c.variables.size() ; i++) {
            if (i > 0) inc.write(", ");
            VarInstance var = (VarInstance)c.variables.get(i);
            String name = var.name().toString();
            if (name.equals(THIS))
                name = SAVED_THIS;
            else name=mangled_non_method_name(name);
            inc.write(emitter.translateType(var.type(),true) + " " + name);
        }
		inc.write(") : x10aux::AnyClosure("+id+") {");
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

		inc.write("const x10aux::RuntimeType *_type() const {"+
			 	  " return x10aux::getRTT<"+superType+" >(); }");
		inc.newline(); inc.forceNewline();

		inc.write("x10aux::ref<x10::lang::String> toString() { return String(\""+
                StringUtil.escape(n.position().nameAndLineString())+"\"); }");
		inc.end(); inc.newline(); inc.forceNewline();

		inc.write("};"); inc.newline(); inc.forceNewline();


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

		w.write("x10aux::ref<"+superType+" >");
        w.write("(new (x10aux::alloc<"+superType+" >(sizeof("+cname+templateArgs+")))");
		w.write(cname+templateArgs+"(");
        for (int i=0 ; i<c.variables.size() ; i++) {
            if (i > 0) w.write(", ");
            VarInstance var = (VarInstance)c.variables.get(i);
            String name = var.name().toString();
            if (!name.equals(THIS))
				name=mangled_non_method_name(name);
            w.write(name);
        }
		w.write("))");

		c.finalizeClosureInstance();
		emitter.exitClosure(a);
	}


	public void visit(ClosureCall_c c) {
		Expr target = c.target();
		Type t = target.type();
		boolean base = false;

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

		X10MethodInstance mi = c.closureInstance();

		sw.pushCurrentStream(w);
		c.printSubExpr(target, sw, tr);
		sw.popCurrentStream();
		w.write("->");
		w.write("apply");
		w.write("(");
		w.begin(0);
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
			sw.pushCurrentStream(w);
			c.print(e, sw, tr);
			sw.popCurrentStream();
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");
	}



	public void visit(X10CanonicalTypeNode_c n) {
//		System.out.println("Pretty-printing canonical type node for "+n);
		Type t = n.type();
		if (t == null)
			throw new InternalCompilerError("Unknown type");
		w.write(emitter.translateType(t));
	}

	public void visit(Unary_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		Unary_c.Operator operator = n.operator();
		Expr expr = n.expr();
		Type t = expr.type();
		if (operator == Unary_c.NEG && expr instanceof IntLit && ((IntLit) expr).boundary()) {
			w.write(operator.toString());
			w.write(((IntLit) expr).positiveToString());
		}
		else if ((operator == Unary_c.NEG || operator == Unary_c.POS) &&
				t instanceof X10Type &&
				((X10TypeSystem) ((X10Type) t).typeSystem()).isPoint(t)) {
			sw.pushCurrentStream(w);
			n.printSubExpr(expr, true, sw, tr);
			sw.popCurrentStream();
			if (operator == Unary_c.NEG) {
				w.write("->neg()");
			}
		}
		else if (operator.isPrefix()) {
			w.write(operator.toString());
			sw.pushCurrentStream(w);
			n.printSubExpr(expr, false, sw, tr);
			sw.popCurrentStream();
		}
		else {
			sw.pushCurrentStream(w);
			n.printSubExpr(expr, false, sw, tr);
			sw.popCurrentStream();
			w.write(operator.toString());
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
			w.write("("+emitter.translateType(n.type())+")(");
			w.write("(("+emitter.makeUnsignedType(n.left().type())+")");
		}
		sw.pushCurrentStream(w);
		n.printSubExpr(n.left(), true, sw, tr);
		sw.popCurrentStream();
		if (unsigned_op)
			w.write(")");
		w.write(" ");
		w.write(opString);
		w.allowBreak(n.type() == null || n.type().isPrimitive() ? 2 : 0, " ");
		if (unsigned_op)
			w.write("(("+emitter.makeUnsignedType(n.left().type())+")");
		sw.pushCurrentStream(w);
		n.printSubExpr(n.right(), false, sw, tr);
		sw.popCurrentStream();
		if (unsigned_op)
			w.write("))");
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
	        w.write("(");
	        w.begin(0);
	        sw.pushCurrentStream(w);
	        tr.print(n, array, sw);
	        sw.popCurrentStream();
	        w.end();
	        w.write(").set(");
	        w.begin(0);
	        tr.print(n, n.right(), sw);
	        for (Expr e: index) {
	            w.write(",");
	            w.allowBreak(0, " ");
	            sw.pushCurrentStream(w);
	            n.printSubExpr(e, false, sw, tr);
	            sw.popCurrentStream();
	        }
	        w.end();
	        w.write(")");
	    }
	    else {
	        // R target = x; T right = e;
	        // target.f = target.f.add(right);
	        // new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
	        Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	        Name methodName = X10Binary_c.binaryMethodName(op);
	        w.write("{ ");
	        emitter.printType(n.type(), w);
	        String retVar=getId();
	        w.write(" " + retVar + ";");
	        w.newline();

	        emitter.printType(array.type(), w);
	        String target = getId();
	        w.write(" " + target + " = ");
	        sw.pushCurrentStream(w);
	        tr.print(n, array, sw);
	        sw.popCurrentStream();
	        w.newline();

	        String eArr [] = new String[index.size()];
	        {
	            int i = 0;
	            for (Expr e : index) {
	                emitter.printType(e.type(), w);
	                w.write(" ");
			eArr[i] = getId();
	                w.write(eArr[i]);
			w.write(" = ");
			sw.pushCurrentStream(w);
			n.printSubExpr(e, false, sw, tr);
			sw.popCurrentStream();
			w.write(";"); w.newline();
	                i++;
	            }
	        }
	        emitter.printType(n.right().type(), w);
	        String right = getId();
	        w.write(" " + right + " = ");
	        sw.pushCurrentStream(w);
	        tr.print(n, n.right(), sw);
	        sw.popCurrentStream();
	        w.write(";"); w.newline();

	        if (! n.type().isVoid()) {
	            w.write(retVar + " = " );
	        }
	        w.write("array.set(");
	        w.write(" array.apply(");
	        {
	            int i = 0;
	            for (Expr e : index) {
	                if (i != 0)
	                    w.write(", ");
	                w.write(eArr[i]);
	                i++;
	            }
	        }
	        w.write(")");
	        if (nativeop) {
	            w.write(" ");
	            w.write(op.toString());
	            w.write(right);
	        }
	        else {
	            w.write(".");
	            w.write(methodName.toString());
	            w.write("(" + right +")");
	        }
	        if (index.size() > 0)
	            w.write(", ");
	        {
	            int i = 0;
	            for (Expr e : index) {
	                if (i != 0)
	                    w.write(", ");
	                w.write(eArr[i]);
	                i++;
	            }
	        }
	        w.write(");");
	        w.newline();
	        if (! n.type().isVoid()) {
	            w.write(retVar + ";" );
		    w.newline();
	        }
	        w.write("}");
	    }
	}
	String getCppImplForDef(X10Def o) {
	    X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
	    try {
	        Type java = (Type) xts.systemResolver().find(QName.make("x10.compiler.Native"));
	        List<Type> as = o.annotationsMatching(java);
	        for (Type at : as) {
	            assertNumberOfInitializers(at, 2, tr);
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
	Object getBoxType(Type type) {
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
		 emitter.dumpRegex("Native", components, tr, pat, w);
	}

	public void visit(Tuple_c c) {
		// Handles Rails initializer.
		Type T = X10TypeMixin.getParameterType(c.type(), 0);
		String type = emitter.translateType(c.type());
		// [DC] this cast is needed to ensure everything has a ref type
		// otherwise overloads don't seem to work properly
		w.write("("+make_ref(type)+")");
		w.write("x10aux::alloc_rail<");
		emitter.printType(T, w);
		w.write(",");
		w.allowBreak(0, " ");
		w.write(emitter.translateType(c.type()));
		w.write(" >("+c.arguments().size());
		for (Expr e:c.arguments()) {
			w.write(",");
			sw.pushCurrentStream(w);
			c.printSubExpr(e, false, sw, tr);
			sw.popCurrentStream();
		}
		w.write(")");
	}

	void newJavaArray(Term_c n, Type base, List dims, int additionalDims, ArrayInit_c init) {
		// TODO: check that all of the initializer fragments are less than MAX_OBJECT_ARRAY_INIT in size
//		if (init != null && !base.isPrimitive() && init.elements().size() > MAX_OBJECT_ARRAY_INIT) {
//		tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
//		"Non-primitive array initializers with more than "+MAX_OBJECT_ARRAY_INIT+" elements not supported",
//		init.position());
//		// TODO: generate an init method for the whole initializer
//		}
		newJavaArray(n, base, dims, 0, additionalDims, init);
	}

	private void newJavaArray(Term_c n, Type base, List dims, int dim, int additionalDims, ArrayInit_c init) {
		if (init != null && !base.isPrimitive() && init.elements().size() > MAX_OBJECT_ARRAY_INIT) {
			tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
					"Non-primitive array initializers with more than "+MAX_OBJECT_ARRAY_INIT+" elements not supported",
					init.position());
			// TODO: generate an init method
		}
		w.write("x10aux::alloc_array<");
		String base_type = emitter.translateType(base, true);
		w.write(base_type);
		w.write(" >(");
		if (dims.size() > 0) {
			sw.pushCurrentStream(w);
			n.printBlock((Expr) dims.get(dim), sw, tr);
			sw.popCurrentStream();
		} else if (init != null) {
			w.write(""+init.elements().size());
		} else {
			tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
					"Unknown array size",
					n.position());
		}
		if (init != null) {
			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			for (Iterator i = init.elements().iterator(); i.hasNext(); ) {
				w.write(",");
				w.allowBreak(4, " ");
				Expr init_i = (Expr) i.next();
				if (init_i instanceof ArrayInit_c) {
					assert (base.isArray());
					newJavaArray((Term_c) init_i, base.toArray().base(), dims, dim+1, additionalDims, (ArrayInit_c) init_i);
				} else {
					boolean needsCast = !ts.typeBaseEquals((X10Type)base, (X10Type) init_i.type());
					if (needsCast)
						w.write("("+base_type+")(");
					sw.pushCurrentStream(w);
					init.print(init_i, sw, tr);
					sw.popCurrentStream();
					if (needsCast)
						w.write(")");
				}
			}
		}
		w.write(")");
	}

	public void visit(When_c n) {
        // FIXME: [IP] this code is likely wrong.  Consolidate with Await_c.
		assert (n.exprs() == null || n.exprs().size() == 0);
		assert (n.stmts() == null || n.stmts().size() == 0);
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		context.canInline = false;

		w.write("while (!(");
		w.begin(0);
		sw.pushCurrentStream(w);
		n.print(n.expr(), sw, tr);
		sw.popCurrentStream();
		w.end();
		w.write("))");
        w.newline(4); w.begin(0);
        w.write("x10aux::async_poll();");
        w.end(); w.newline();
		sw.pushCurrentStream(w);
		n.printSubStmt(n.stmt(), sw, tr);
		sw.popCurrentStream();
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
