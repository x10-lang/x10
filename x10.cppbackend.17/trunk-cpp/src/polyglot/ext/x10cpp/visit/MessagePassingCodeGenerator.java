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

import static polyglot.ext.x10cpp.visit.ASTQuery.getConstructorId;
import static polyglot.ext.x10cpp.visit.ASTQuery.getOuterClass;
import static polyglot.ext.x10cpp.visit.ASTQuery.isPrintf;
import static polyglot.ext.x10cpp.visit.ASTQuery.outerClosure;
import static polyglot.ext.x10cpp.visit.Emitter.mangled_method_name;
import static polyglot.ext.x10cpp.visit.Emitter.mangled_non_method_name;
import static polyglot.ext.x10cpp.visit.Emitter.translateFQN;
import static polyglot.ext.x10cpp.visit.Emitter.translate_mangled_NSFQN;
import static polyglot.ext.x10cpp.visit.SharedVarsMethods.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
import polyglot.ast.Call_c;
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
import polyglot.ast.PackageNode_c;
import polyglot.ast.Receiver;
import polyglot.ast.Return_c;
import polyglot.ast.Stmt;
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
import polyglot.ext.x10.ast.ArrayConstructor_c;
import polyglot.ext.x10.ast.AssignPropertyBody_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.ClosureCall_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.ConstantDistMaker_c;
import polyglot.ext.x10.ast.DepCast_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.ParExpr_c;
import polyglot.ext.x10.ast.RectRegionMaker_c;
import polyglot.ext.x10.ast.RegionMaker_c;
import polyglot.ext.x10.ast.StmtSeq_c;
import polyglot.ext.x10.ast.X10ArrayAccess1_c;
import polyglot.ext.x10.ast.X10ArrayAccess_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode_c;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClockedLoop_c;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10Special_c;
import polyglot.ext.x10.extension.X10Ext_c;
import polyglot.ext.x10.query.QueryEngine;
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
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.NoMemberException;
import polyglot.types.Package;
import polyglot.types.Package_c;
import polyglot.types.ParsedClassType;
import polyglot.types.QName;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
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
 *
 * This code is invoked with -SPMD_COMPILATION=false switch
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


	


	public void visit(ClassDecl_c n) {
		processClass(n);
	}

	private boolean extractInits(String className, String methodName,
			String retType, List members,
			ClassifiedStream w, boolean staticInits)
	{
		boolean sawInit = false;
		for (Iterator i = members.iterator(); i.hasNext(); ) {
			ClassMember member = (ClassMember) i.next();
			if (!(member instanceof Initializer_c) && !(member instanceof FieldDecl_c))
				continue;
			if (member.memberDef().flags().isStatic() != staticInits)
				continue;
			if (member instanceof FieldDecl_c &&
					(((FieldDecl_c)member).init() == null ||
							query.isSyntheticField(((FieldDecl_c)member).name().toString())))
				continue;
			if (!sawInit) {
				w.write(retType + " " + className + "::" + methodName + "() {");
				w.newline(4);
				w.begin(0);
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

				w.write(mangled_non_method_name(dec.name().id().toString()));
				w.write(" = ");
				if (init instanceof ArrayInit_c) {
					assert (dec.type().type().isArray());
					ArrayType type = dec.type().type().toArray();
					newJavaArray(init, type.base(), Collections.EMPTY_LIST, type.dims(), (ArrayInit_c) init);
				} else {
					sw.pushCurrentStream(w);
					dec.print(init, sw, tr);
					sw.popCurrentStream();
				}
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
	boolean hasNativeMethods(List members) {
		for (Iterator i = members.iterator(); i.hasNext(); ) {
			ClassMember member = (ClassMember) i.next();
			if (member instanceof MethodDecl_c) {
				MethodDecl_c init = (MethodDecl_c) member;
				if (init.flags().flags().isNative())
					return true;
			}
		}
		return false;
	}

	// private void processNestedClasses(ClassDecl_c n, SimpleDualCodeWriter w, Translator tr)  
	// -- FIXME: Igor, any reason, why we should keep the second and third arguments?
	private void processNestedClasses(ClassDecl_c n) {
		for (Iterator i = n.body().members().iterator(); i.hasNext(); ) {
			ClassMember member = (ClassMember) i.next();
			if (member instanceof ClassDecl_c)
				processClass((ClassDecl_c) member);
		}
	}


	void processClass(ClassDecl_c n) {

		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		// PV-IPA
		if (context.outermostContext().harvest == null) {  // TODO: Fix to handle separate compilation.
			// Presently this test's a hack to ensure that summaries pass occurs only for the outermost class
			X10SummarizingPass v = new X10SummarizingPass(tr);
			v.makeSummariesPass(n);
		}

		context.setinsideClosure(false);
		context.hasInits = false;

		if (hasNativeMethods(n.body().members())) {
			w.write("#include \"" + X10ClassBodyExt_c.wrapperFileName(n.classDef().fullName().toString()) + "\"");
			w.newline();
		}
		ClassifiedStream h;
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);
		if (!n.classDef().isNested()) {
			// SPMD fields
			X10SearchVisitor xTypes = new X10SearchVisitor(X10CanonicalTypeNode_c.class);
			n.visit(xTypes);
			if (xTypes.found()) {
				X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
				DelegateTargetFactory tf = ((X10CPPTranslator) tr).getTargetFactory();
				populateKnownSpecialPackages(xts);

				ArrayList typeNodes = xTypes.getMatches();
				HashSet types = new HashSet();
				for (int i = 0; i < typeNodes.size(); i++) {
					X10CanonicalTypeNode_c t = (X10CanonicalTypeNode_c) typeNodes.get(i);
					if (!t.type().isClass() || xts.equals(t.type(), n.classDef()))
						continue;
					ClassType ct = t.type().toClass();
					if (ct.isNested() || knownSpecialPackages.contains(ct.package_()))
						continue;
					types.add(ct);
				}
				for (Iterator is = types.iterator(); is.hasNext(); ) {
					ClassType ct = (ClassType) is.next();
					String pkg = "";
					if (ct.package_() != null)
						pkg = ct.package_().fullName().toString();
					String header = tf.outputHeaderName(pkg, ct.name().toString());
					h.write("#include \"" + header + "\"");
					h.newline();
				}
				for (Iterator is = context.pendingImports.iterator(); is.hasNext();) {
					Import_c in = (Import_c) is.next();
					Package_c rt = null;
					try {
					if (xts.forName(in.name()) instanceof Package_c){
						h.write("using namespace "+translateFQN(in.name().toString())+";");
					} else if (knownSpecialPackages.contains(xts.packageForName(in.name())) ) { // library class import.

						h.write("using namespace "+translateFQN(in.name().toString().substring(0,in.name().toString().lastIndexOf('.')))+";");
					}
					else {// import user defined class
						h.write("using namespace " + translate_mangled_NSFQN(in.name().toString()) + ";");
					}
					h.newline();
					} catch (SemanticException e) { assert (false); }

				}
				context.pendingImports.clear();  // Just processed all imports - clean up
			}
			h.forceNewline(0);
			if (n.classDef().package_() != null) {
				h.write("namespace ");
				h.write(mangled_non_method_name(translateFQN(n.classDef().package_().fullName().toString())));
				h.write(" {");
				h.newline(0);
			}
		}

		if (n.classDef().isNested() && !n.classDef().flags().isStatic())
			throw new InternalCompilerError("Instance Inner classes not supported");

		emitter.printHeader(n, h, tr, false);

		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();

		/*
		 * TODO: [IP] Add comment about dependences between the method calls.
		 */
		processNestedClasses(n);

		ArrayList asyncs = context.closures.asyncs;
		if (asyncSwitchRequired)
			emitter.printSwitchMethod(n.classDef().asType(), ASYNC_SWITCH, VOID,
					ASYNC_PREFIX, asyncs, context.closures.asyncsParameters,
					context.closures.asyncContainers,
					"int niter",
					"for (int i = 0; i < niter; i++,_arg++) {", "}",
					context.classesWithAsyncSwitches, ws.getCurStream(WriterStreams.StreamClass.Closures));
		if (asyncRegistrationRequired)
			emitter.printAsyncsRegistration(n.classDef().asType(), asyncs, 
					ws.getCurStream(WriterStreams.StreamClass.Closures));
		if (arrayCopySwitchRequired)
			emitter.printSwitchMethod(n.classDef().asType(), ARRAY_COPY_SWITCH, VOID_PTR,
					ARRAY_COPY_PREFIX, asyncs, context.closures.asyncsParameters,
					context.closures.arrayCopyClosures, 
					null,
					null, null,
					context.classesWithArrayCopySwitches, ws.getCurStream(WriterStreams.StreamClass.Closures));

		if (!n.classDef().isNested()) {
			if (n.classDef().package_() != null) {
				h.newline(0);
				h.write("} // namespace ");
				h.write(mangled_non_method_name(translateFQN(n.classDef().package_().fullName().toString())));
				h.newline(0);
			}
		}
	}



	public void visit(LocalClassDecl_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		// FIXME: [IP] Local classes cannot have static members (thus asyncs)
		//
		// [Krishna] Is it not fixed already? -- The asyncs in the
		// nested class will be added to the outmost toplevel
		// class. Would that be a problem? Probably not.
		assert(false); // We are removing all the inner + local classes using a separate pass.
		context.pushInLocalClass();
		emitter.printHeader((ClassDecl_c)n.decl(), w, tr, false);
		sw.pushCurrentStream(w);
		n.print(n.decl().body(), sw, tr);
		sw.popCurrentStream();
		context.popInLocalClass();

	}

	public void visit(ClassBody_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		ClassifiedStream h;
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);
		h.write("{");
		List members = n.members();
		if (!members.isEmpty()) {
			ClassType currentClass = context.currentClass();
			String className = emitter.translateType(currentClass);

			h.newline(4);
			h.begin(0);
			w.begin(0);

			for (Iterator i = members.iterator(); i.hasNext(); ) {
				ClassMember member = (ClassMember) i.next();
				if (!(member instanceof ClassDecl_c))
					continue;
				ClassDecl_c dec = (ClassDecl_c)member;
				emitter.printFlags(h, dec.flags().flags());
				h.write("class ");
				h.write(mangled_non_method_name(dec.name().id().toString()));
				h.write(";");
				h.newline();
			}

			if (extractInits(className, RUN_INITIALIZERS, VOID, members, w, false)) {
				h.write("private : " + VOID + " " + RUN_INITIALIZERS + "();");
				h.newline();
				context.hasInits = true;
			}

			ClassMember prev = null;
			for (Iterator i = members.iterator(); i.hasNext(); ) {
				ClassMember member = (ClassMember) i.next();
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

			if (extractInits(className, STATIC_INIT, VOID_PTR, members, w, true)) {
				h.write("public : static " + VOID_PTR + " " + STATIC_INIT + "();");
				h.newline();
				w.write("static " + VOID_PTR + " __init__"+getUniqueId_() +" = " + className + "::" + STATIC_INIT + "()"+ ";");
				w.newline(); w.forceNewline(0);
			}


			if (!context.inLocalClass()){
				// FIXME: Do something for local classes
				// as well. [Krishna]
				emitter.printStaticAsyncDeclarations(context,  h);
				emitter.printStaticClosureDeclarations(context, h);
			}

			if (!currentClass.isNested() && !context.inLocalClass())

			if (((X10TypeSystem) tr.typeSystem()).isValueType(currentClass)) {
				emitter.generateSerializationMethods(currentClass, w, h);
			}

			context.resetMainMethod();

			w.end();
			h.end();
			h.newline(0);
		}

		h.write("};");
		h.newline();
	}

	String defaultValue(Type type) {
		return type.isPrimitive() ? "0" : "NULL";
	}




	public void visit(PackageNode_c n) {
		w.write(mangled_non_method_name(translateFQN(n.package_().fullName().toString())));
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
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		ClassifiedStream h;
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);
		emitter.printHeader(dec, h, tr, false);
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

		assert (!polyglot.ext.x10cpp.Configuration.SPMD_COMPILATION);
		if (query.isMainMethod(dec))
		{
			Type container = dec.methodDef().asInstance().container();
			if (ignoreExceptions)
				xcdProcessor.new Template("MainMP_noexc", emitter.translateType(container)).expand();
			else 
				xcdProcessor.new Template("MainMP", emitter.translateType(container)).expand();
			h.write("public : static void main_np0();");
			h.newline();
		}

		if (dec.body() != null) {
			if (!dec.flags().flags().isStatic()) {
				VarInstance ti = ts.localInstance(Position.COMPILER_GENERATED, Flags.FINAL,
						dec.methodDef().asInstance().container(), THIS);
				context.addVariable(ti);
			}
			if (!context.inLocalClass())
				emitter.printHeader(dec, w, tr, true);
			w.newline();
			w.write("{");
			w.newline();
			sw.pushCurrentStream(w);
			dec.printSubStmt(dec.body(), sw, tr);
			sw.popCurrentStream();
			w.newline();
			w.write("}");
			w.newline();
		} else if (dec.flags().flags().isNative()) {
			if (!context.inLocalClass())
				emitter.printHeader(dec, w, tr, true);
			w.newline(0); w.begin(4); w.write("{"); w.newline();
			if (!dec.methodDef().asInstance().returnType().isVoid())
				w.write("return ");

			String jniName = X10ClassBodyExt_c.generateX10NativeName(dec);
			w.write(jniName + "(");
			w.begin(0);

			for (ListIterator i = dec.formals().listIterator(); i.hasNext(); ) {
				Formal_c parameter = (Formal_c) i.next();

				Type type = parameter.declType();
				if (type.isPrimitive()) {
					w.write(mangled_non_method_name(parameter.name().id().toString()));
				} else if (type.isArray()) {
					assert (false);
					w.write(mangled_non_method_name(parameter.name().id().toString()));
					w.write("->"+RAW_ARRAY+"()");
				} else {
					assert (ts.isX10Array(type));
					Type base = X10TypeMixin.baseType(type);
					assert (base.isPrimitive());
					w.write(mangled_non_method_name(parameter.name().id().toString()));
					w.write("->"+RAW_ARRAY+"()");
					w.write(", NULL"); // for the descriptor
				}
				if (i.hasNext())
					w.write(", ");
			}

			w.end(); w.write(");");
			w.end(); w.newline(0); w.write("}"); w.newline();
		}
	}



	public void visit(ConstructorDecl_c dec) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		ClassifiedStream h;
		if (!context.inLocalClass())
			emitter.printHeader(dec, ws.getCurStream(WriterStreams.StreamClass.Header), tr, false);
		emitter.printHeader(dec, w, tr, true);
		StructType container = dec.constructorDef().asInstance().container();
		boolean hasInits = false;
		// Extract initializers from the body
		Block_c body = (Block_c) dec.body();
		List<Stmt> statements = body.statements();
//		HashMap inited = new HashMap();
		List<Stmt> newStatements = new TypedList(new ArrayList(), Stmt.class, false);
		for (Stmt n : statements) {
			// Iterator i = statements.iterator(); i.hasNext(); ) {
			// Stmt n = (Stmt) i.next();
			if (n instanceof ConstructorCall) {
				ConstructorCall call = (ConstructorCall) n;
				if (call.kind() == ConstructorCall.SUPER) {
					if (!hasInits) {
						w.allowBreak(4, " ");
						w.write(":");
						hasInits = true;
					} else {
						w.write(",");
					}
					w.allowBreak(2, " ");
					w.write(emitter.translateType(container.superType()) + "(");
					if (call.arguments().size() > 0) {
						w.allowBreak(2, 2, "", 0); // miser mode
						w.begin(0);
						boolean first = true;
						for(Expr e : (List<Expr>) call.arguments() ) {
							if (first) {
								first=false;
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
					w.write(")");
				}
				else
					throw new InternalCompilerError("this() calls not supported", dec.position());
			}
			/* The code below translated assignments of the form "a = val" to field
			 * initializers like "a(val)".  The problem is that assignments are expected
			 * to happen in a certain order, and C++ initialzers don't provide such
			 * guarantees.  We used to have to do this because of final fields, but
			 * since we already strip away the "const" from final fields in FieldDecl_c,
			 * we can do this.  Even if we didn't strip away the "const", we could
			 * add const_cast's to assignments.

			else
			if (n instanceof Eval_c && ((Eval_c)n).expr() instanceof FieldAssign_c) {
				FieldAssign_c init = (FieldAssign_c) ((Eval_c)n).expr();
				Field f = (Field) init.left();
				String name = f.name().id().toString();
				if (f.fieldInstance().container() == container && inited.get(name) == null) {
					inited.put(name, init.right());
					if (!hasInits) {
						w.write(":");
						hasInits = true;
					} else {
						w.write(",");
					}
					w.allowBreak(2, " ");
					w.write(name + "(");
					w.allowBreak(2, 2, "", 0); // miser mode
					w.begin(0);
					dec.print(init.right(), w, tr);
					w.end();
					w.write(")");
				}
				else
					newStatements.add(n);
			}
			 */
			else
				newStatements.add(n);
		}
		if (!dec.flags().flags().isStatic()) {
			TypeSystem ts = tr.typeSystem();
			VarInstance ti = ts.localInstance(Position.COMPILER_GENERATED, Flags.FINAL,
					container, THIS);
			context.addVariable(ti);
		}
		if (hasInits)
			w.newline();
		else
			w.allowBreak(0, " ");
		w.write("{"); w.newline(4); w.begin(0);
		if (context.hasInits) {
			w.write("this->"+RUN_INITIALIZERS+"();"); w.newline();
		}
		for (Stmt s : newStatements) {
			sw.pushCurrentStream(w);
			dec.printBlock(s, sw, tr);
			sw.popCurrentStream();
			w.newline();
		}
		w.end(); w.newline(); w.write("}");
		w.newline();
	}



	public void visit(FieldDecl_c dec) {
		// FIXME: HACK: skip synthetic serialization fields and x10 auxiliary fields
		if (query.isSyntheticField(dec.name().toString()))
			return;
		ClassifiedStream h;
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (context.inLocalClass())
			h = w;
		else
			h = ws.getCurStream(WriterStreams.StreamClass.Header);
		emitter.printHeader(dec, h, tr, false);
		if (dec.flags().flags().isStatic()) {
			emitter.printHeader(dec, w, tr, true);
			w.write(";");
			w.newline();
			return ;
		}
		// Ignore the initializer -- this will have been done in extractInits/extractStaticInits
		// FIXME: the above breaks switch constants!
		h.write(";");
		h.newline();
	}

	public void visit(Initializer_c n) {
		if (n.flags().flags().isStatic()) {
			// Ignore -- this will have been processed earlier
//			tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
//			"Static initializers not supported",
//			n.position());
		}
	}

	public void visit(Assert_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		if (!tr.job().extensionInfo().getOptions().assertions)
			return;
		// TODO: implement this
		w.write("x10::x10__assert(");
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
		final boolean bodyForOnlyPlaceZero = !query.hasCodeForAllPlaces(n);

		
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
		final boolean bodyForOnlyPlaceZero = !query.hasCodeForAllPlaces(n);
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
		((X10CPPContext_c) tr.context()).setLabel(label.labelNode(), label.statement());
		sw.pushCurrentStream(w);
		label.print(label.statement(), sw, tr);
		sw.popCurrentStream();
		w.newline();
	}

	public void visit(Assign_c asgn) {
		X10CPPContext_c context = (X10CPPContext_c)tr.context();

		NodeFactory nf = tr.nodeFactory();
		Expr lhs = asgn.left(nf);
		Expr rhs = asgn.right();
		// Be conservative for now (because of evaluation order)
		// TODO: [IP] use a non-const reference for lhs

		sw.pushCurrentStream(w);
		asgn.printSubExpr(lhs, false, sw, tr);
		sw.popCurrentStream();
		w.write(" ");
		// [IP] Are all the operators the same?
		w.write(asgn.operator().toString());
		w.allowBreak(2, 2, " ", 1);
		sw.pushCurrentStream(w);
		asgn.printSubExpr(rhs, true, sw, tr);
		sw.popCurrentStream();
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

		Expr initcasts = dec.init();
		Expr initexpr = initcasts;
		boolean isDistributed = false;
		// FIXME: [IP] This will ignore casts on SPMD array initializers.
		// However, most times the casts are for dependent types, and can probably be ignored.
		// TODO: investigate the above
		while (initexpr instanceof X10Cast_c)
			initexpr = ((X10Cast_c)initexpr).expr();
		ArrayConstructor_c init = null;
		Type base_type = null;
		if (initexpr instanceof ArrayConstructor_c) {
			init = (ArrayConstructor_c) initexpr;
			assert(ts.isDistribution(init.distribution().type()));
			assert (query.isX10Array(dec.type().type()));
			base_type = init.arrayBaseType().type();
			isDistributed = !isLocal(init.distribution());
		}
		if (isDistributed) {
			emitter.emit_general_finish_start(w);
			w.newline();
		}
		emitter.printHeader(dec, w, tr, true);
		
		// FIXME: [IP] KLUDGE! KLUDGE! KLUDGE!
		// xlC, for some reason, really doesn't like v1 = v2 = v3.  v1 gets the wrong value.
		// So work around this by turning the above into v1 = v3; v2 = v1.
		// FIXME: Need to handle nullable cast in this situation.
		X10Cast_c castExpr = null;
		if (initexpr != null) {
			w.write(" =");
			w.allowBreak(2, " ");
			Expr e = initcasts;
			while (e instanceof X10Cast_c) {
				X10Cast_c n = (X10Cast_c) e;
				castExpr = n;
				w.write("("+emitter.translateType(n.castType().type(), true)+")");
				w.allowBreak(2, " ");
				e = n.expr();
			}
			assert (e == initexpr);
			if (initexpr instanceof ArrayInit_c) {
				assert (dec.type().type().isArray());
				ArrayType type = dec.type().type().toArray();
				newJavaArray((Term_c) initexpr, type.base(), Collections.EMPTY_LIST, type.dims(), (ArrayInit_c) dec.init());
			} else
			// TODO: [IP] Combine finish calls for consecutive reductions
			{
				sw.pushCurrentStream(w);
				dec.print(initexpr, sw, tr);
				sw.popCurrentStream();
			}
		}

		if (tr.appendSemicolon()) {
			w.write(";");
			w.newline(0);
			// Note: Our handling of the cast, first assigns
			// the value to the lhs and then it might throw
			// the error. But there is no way to actually
			// refer to this variable (it is a local
			// declaration). So this translation is
			// semantically correct. [Krishna]
			emitter.handleX10Cast(castExpr, mangled_non_method_name(dec.name().id().toString()), w);

		}
		// See above note about xlC.
		if (isDistributed) {
			emitter.emit_general_finish_end(w);
			w.newline();
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
		// Assumption: A StmtSeq will not do any leave/enterSPMD
		// work. It is upto the each individual construct to
		// handle things right.
		
		assert(false); // FIXME. It is not clear if when StmtSeq_c nodes are generated.  If they indeed are, remove this assert and continue with the method below.
		
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





	/**
	 * Asynchronous array copies are treated in the following way:
	 * The first argument has to be an SPMD global.
	 * The method call is converted to a call to the push-style
	 * runtime routine, asyncArrayCopy.  The source is a sum of
	 * the first argument indexed with "here.id" and the second
	 * argument, the destination is a closure that yields the sum
	 * of the first argument indexed with "here.id" (remote!) and
	 * the fourth argument, the destination place is left
	 * unchanged.  The clock argument is always null.  Finally, if
	 * the post-copy notification (sixth argument) is true,
	 * another closure is created, this one invoking the
	 * postCopyRun() method on the first argument indexed with
	 * "here.id" (again remote).
	 */
	private void processAsyncArrayCopy(Call_c n) {
		List args = n.arguments();
		assert (args.size() == 6);
		Expr array = (Expr) args.get(0);
		Expr srcOffset = (Expr) args.get(1);
		Expr target = (Expr) args.get(2);
		Expr destOffset = (Expr) args.get(3);
		Expr len = (Expr) args.get(4);
		Expr notify = (Expr) args.get(5);
		// TODO
//		Expr clock = null;

		if (!(array instanceof Local_c || array instanceof Field_c)) {
			tr.job().compiler().errorQueue().enqueue(ErrorInfo.WARNING,
					"Warning: array argument to " + n.name() + " is neither a local nor a field",
					array.position());
			w.write("// AsyncArrayCopy call would have been here");
			w.newline();
			return;
		}

		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		VarInstance a_var =
			(array instanceof Local_c) ? ((Local_c)array).localInstance()
					: ((Field_c)array).fieldInstance();
		boolean isSPMDTarget = context.isSPMDVar(a_var);
		Type x_l_RemoteDoubleArrayCopier = null;
		try {
			x_l_RemoteDoubleArrayCopier = (Type) xts.forName(QName.make("x10.lang.RemoteDoubleArrayCopier"));
			assert (xts.isX10Array(array.type()) &&
					xts.isSubtype(X10TypeMixin.baseType(array.type()),
							x_l_RemoteDoubleArrayCopier));
			// TODO: assert that the distribution of the array is unique
		} catch (SemanticException e) { assert (false); }

		boolean notifyConstant = notify.isConstant();
		boolean shouldNotify = false;
		if (notify instanceof BooleanLit_c) {
			BooleanLit_c lit = (BooleanLit_c) notify;
			shouldNotify = lit.booleanValue();
		}

		w.write("{");
		w.newline(4); w.begin(0);

		// FIXME: make more general
		Type baseType = xts.Double();

		// TODO: Evaluate arguments in the right order
		X10NodeFactory xnf = (X10NodeFactory)tr.nodeFactory();
		Expr src = null;
		Expr dest = null;
		try {
			FieldInstance fi = xts.findField(xts.Place(), "id", context.currentClassDef());
			array = xnf.X10ArrayAccess1(array.position(), array,
					xnf.Field(array.position(),
							xnf.Here(Position.COMPILER_GENERATED).type(xts.Place()),
							xnf.Id(Position.COMPILER_GENERATED, "id"))
						.fieldInstance(fi).type(xts.Int()))
					.type(x_l_RemoteDoubleArrayCopier);
			Type aType = xts.array(baseType);
			MethodInstance smi = xts.findMethod(x_l_RemoteDoubleArrayCopier, xts.MethodMatcher(x_l_RemoteDoubleArrayCopier, GET_SOURCE_ARRAY, (List<Type>)Collections.EMPTY_LIST), context.currentClassDef());
			src = xnf.Call(Position.COMPILER_GENERATED, array,
						  xnf.Id(Position.COMPILER_GENERATED, GET_SOURCE_ARRAY))
				  .methodDef().asInstance(smi).type(aType);
			MethodInstance dmi = xts.findMethod(x_l_RemoteDoubleArrayCopier, xts.MethodMatcher(x_l_RemoteDoubleArrayCopier, GET_DEST_ARRAY, (List<Type>)Collections.EMPTY_LIST), context.currentClassDef());
			dest = xnf.Call(Position.COMPILER_GENERATED, array,
						   xnf.Id(Position.COMPILER_GENERATED, GET_DEST_ARRAY))
				   .methodDef().asInstance(dmi).type(aType);
		} catch (SemanticException e) { assert (false); }

		if (notifyConstant) {
			emitter.printAsyncArrayCopyInvocation(n, context, baseType, target,
					src, srcOffset, dest, destOffset, len, array, shouldNotify, ws, w);
		} else {
			// TODO: store notify in a variable and send that into the closure
			w.write("if (");
			sw.pushCurrentStream(w);
			n.print(notify, sw, tr);
			sw.popCurrentStream();
			w.write(") {");
			w.newline(4); w.begin(0);
			emitter.printAsyncArrayCopyInvocation(n, context, baseType, target,
					src, srcOffset, dest, destOffset, len, array, true, ws, w);
			w.end(); w.newline(0);
			w.write("} else {");
			w.newline(4); w.begin(0);
			emitter.printAsyncArrayCopyInvocation(n, context, baseType, target,
					src, srcOffset, dest, destOffset, len, array, false, ws, w);
			w.end(); w.newline(0);
			w.write("}");
			w.newline();
		}

		w.end(); w.newline(0);
		w.write("}");
	}
	public void visit(Call_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		// -- K assert (!query.isSPMDArrayReduction(n));
		if (query.isClockMaker(n)) {
			w.begin(0);
			String clock_type = "x10::lang::clock";
			w.write("("+make_ref(clock_type)+")(");
			w.begin(0);
			w.write("new (x10::alloc<"+clock_type+" >()) "+clock_type+"(");
			w.write(")");
			w.end();
			w.write(")");
			// TODO: refactor
			w.end();
			return;
		} else
		if (query.isBlockDistMaker(n)) {
			assert (n.arguments().size() == 1);

			w.begin(0);
			String dist_type = "_dist_block";
			w.write("("+make_ref(dist_type)+")(");
			w.begin(0);
			w.write("new (x10::alloc<"+dist_type+" >()) "+dist_type+"(");
			w.allowBreak(2, 2, "", 0); // miser mode
			w.begin(0);
			Expr r = (Expr) n.arguments().get(0);
			sw.pushCurrentStream(w);
			n.printSubExpr(r, true, sw, tr);
			sw.popCurrentStream();
			w.end();
			w.write(")");
			w.end();
			w.write(")");
			// TODO: refactor
			w.end();
			return;
		} else
		if (query.isPointMaker(n)) {

			w.begin(0);
			List arguments = n.arguments();
			int dim = arguments.size();
			String point_type = "x10::lang::_point<"+dim+">";
			w.write("("+make_ref(point_type)+")(");
			w.begin(0);
			w.write("new (x10::alloc<"+point_type+" >()) "+point_type+"(");
			w.allowBreak(2, 2, "", 0); // miser mode
			w.begin(0);
			for(Iterator i = arguments.iterator(); i.hasNext(); ) {
				sw.pushCurrentStream(w);
				n.print((Expr) i.next(), sw, tr);
				sw.popCurrentStream();
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
			w.end();
			w.write(")");
			w.end();
			w.write(")");
			// TODO: refactor
			w.end();
			return;
		} else
		if (query.isAsyncArrayCopy(n)) {
			processAsyncArrayCopy(n);
			return;
		}


		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		MethodInstance mi = n.methodInstance();

		w.begin(0);
		boolean requireMangling = true;
		if (!n.isTargetImplicit()) {
			// explicit target.
			Receiver target = n.target();

///////////////////////////////////////////////////
// The following code can be enabled if we require methods with guards.
// Vijay says, it is not required anymore. Also enable the code in
// inlineMethod. -Krishna.
//			X10SummarizingRules.Collection harvest = context.outermostContext().harvest;
//			X10MethodDecl_c decl = (X10MethodDecl_c) harvest.getDecl(n);
//			if (decl != null && decl.body() != null) {
//				if (decl.whereClause() != null) {
//					printType(target.type(), w);
//					w.write(" ");
//					String tVar = getId();
//					w.write(tVar + " = ");
//					printExplicitTarget(n, target, context);
//					w.write(" ,");
//					String wcVar = getId();
//					w.write("boolean " + wcVar + " = ");
//					w.write(tVar + ".");
//					n.printSubExpr(decl.whereClause(), true, w, tr);
//					w.write(" , ");
//					w.write(tVar);
//				}
//			} else {
//				printExplicitTarget(n, target, context);
//			}
///////////////////////////////////////////////////
			emitter.printExplicitTarget(n, target, context, w);

			if (mi.flags().isStatic() ||
					(target instanceof X10Special_c &&
							((X10Special_c)target).kind().equals(X10Special_c.SUPER))) {
				w.write("::");
			} else {
				w.write("->");
			}
			if (!target.type().isClass()) {
				requireMangling = false;
			} else {
				ClassType ct = target.type().toClass();
				if (knownSpecialPackages.contains(ct.package_()))
					requireMangling = false;
				// FIXME: The target name is mangled. What about
				// the method name? Currently it is not! -- Issue
				// with x10lang.
				// It is waiting to break. For example,
				// System.out.println should get translated to
				// System::__x10__out.println, not
				// System::__x10__out.x10__println.
				// -Krishna.
			}
		}
		else if (context.inlining || context.insideClosure) {
			w.write(SAVED_THIS+"->");
			if (context.insideClosure)
				context.saveEnvVariableInfo(THIS);
			// FIXME: [IP] Why are we returning here?

			assert (false); // Want to check, where it is used.
			                // Do not see any obvious use of
			                // this part of the code.
			                // Seems wrong. -Krishna.
			return;
		}

		if (requireMangling)
                        w.write(mangled_method_name(n.name().id().toString()));
                else
                        w.write(n.name().id().toString());
		w.write("(");
		if (isPrintf(n)) {
			assert (n.arguments().size() > 0);
			sw.pushCurrentStream(w);
			n.print((Expr) n.arguments().get(0), sw, tr);
			sw.popCurrentStream();
			if (n.arguments().size() > 1) {
				Expr firstArg = (Expr) n.arguments().get(1);
				w.write(",");
				w.allowBreak(0, " ");
				NodeFactory nf = tr.nodeFactory();
				ClassType objectType = xts.X10Object();
				ArrayList initArgs = new ArrayList();
				for (int i = 1; i < n.arguments().size(); i++) {
					Expr arg_i = (Expr) n.arguments().get(i);
					// FIXME: [IP] HACK.  Box the primitives here!
					if (arg_i.type().isPrimitive()) {
						arg_i = nf.Cast(arg_i.position(), nf.CanonicalTypeNode(arg_i.position(), objectType), arg_i).type(objectType);
						arg_i = (Expr) ((X10Ext_c) arg_i.ext()).rewrite(xts, nf, tr.job().extensionInfo());
					}
					initArgs.add(arg_i);
				}
				newJavaArray(n, objectType, Collections.EMPTY_LIST, 0,
						(ArrayInit_c) nf.ArrayInit(firstArg.position(), initArgs).type(xts.array(objectType)));
			}
		} else
		if (n.arguments().size() > 0) {
			w.allowBreak(2, 2, "", 0); // miser mode
			w.begin(0);
			for(Iterator i = n.arguments().iterator(); i.hasNext(); ) {
				Expr e = (Expr) i.next();
				sw.pushCurrentStream(w);
				n.print(e, sw, tr);
				sw.popCurrentStream();
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
			w.end();
		}
		w.write(")");
		w.end();
	}




	public void visit(RegionMaker_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		assert (n.arguments().size() == 2);
		String region_type = "_region<1>";
		w.write("("+make_ref(region_type)+")(");
		w.begin(0);
		w.write("new (x10::alloc<"+region_type+" >()) "+region_type+"(");
		w.allowBreak(2, 2, "", 0); // miser mode
		w.begin(0);
		for (Iterator i = n.arguments().iterator(); i.hasNext(); ) {
			sw.pushCurrentStream(w);
			n.print((Expr) i.next(), sw, tr);
			sw.popCurrentStream();
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");
		w.end();
		w.write(")");
		return;
	}


	public void visit(RectRegionMaker_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		List arguments = n.arguments();
		int dims = arguments.size();
		if (dims == 1) { // Special case - unbox
			sw.pushCurrentStream(w);
			n.print((RegionMaker_c) arguments.get(0), sw, tr);
			sw.popCurrentStream();
			return;
		}
		String rect_region_type = "_region<"+ dims + ">";
		w.write("("+make_ref(rect_region_type)+")(");
		w.begin(0);
		w.write("new (x10::alloc<"+rect_region_type+" >()) "+rect_region_type+"(");
		w.allowBreak(2, 2, "", 0); // miser mode
		w.begin(0);
		for (Iterator i = arguments.iterator(); i.hasNext(); ) {
			sw.pushCurrentStream(w);
			n.print((Expr) i.next(), sw, tr);
			sw.popCurrentStream();
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");
		w.end();
		w.write(")");
	}


	public void visit(ConstantDistMaker_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		assert (n.arguments().size() == 2);
		String dist_type = "_dist_local";
		w.write("("+make_ref(dist_type)+")(");
		w.begin(0);
		w.write("new (x10::alloc<"+dist_type+" >()) "+dist_type+"(");
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

		w.begin(0);
		if (!n.isTargetImplicit()) {
			// explicit target.
			Receiver target = n.target();
			if (target instanceof Expr) {
				boolean assoc =
					!(target instanceof New_c ||
						target instanceof RectRegionMaker_c ||
						target instanceof Binary_c);
				sw.pushCurrentStream(w);
				n.printSubExpr((Expr) target, assoc, sw, tr);
				sw.popCurrentStream();
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
			Receiver target = n.target();
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
				if (c.insideClosure || c.inlining) {
					w.write(SAVED_THIS+"->");
					if (c.insideClosure)
						c.saveEnvVariableInfo(THIS);
				}
			} else {
				w.write(emitter.translateType(n.fieldInstance().container()) + "::");
			}
		}
                w.write(mangled_non_method_name(n.name().id().toString()));
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
			throw new InternalCompilerError("Anonymous innner classes not supported");
		String type = emitter.translateType(n.objectType().type());
		w.write("("+make_ref(type)+")(");
		w.begin(0);
		w.write("new");
		w.allowBreak(0, " ");
		w.write("(x10::alloc<"+type+(type.endsWith(">")?" ":"")+">())");
		w.allowBreak(0, " ");
		sw.pushCurrentStream(w);
		n.print(n.objectType(), sw, tr);
		sw.popCurrentStream();
		w.write("(");
		w.allowBreak(2, 2, "", 0);
		w.begin(0);
		for (Iterator i = n.arguments().iterator(); i.hasNext(); ) {
			Expr e = (Expr) i.next();
			sw.pushCurrentStream(w);
			n.print(e, sw, tr);
			sw.popCurrentStream();
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(2, " ");
			}
		}
		// FIXME: [IP] Temporary hack until we have full stack traces
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		if (ts.isSubtype(n.objectType().type(), ts.Throwable()) && n.arguments().size() == 0) {
			String stringType = emitter.translateType(ts.String());
			w.write(stringType+"(__FILE__ \":\")+"+stringType+"((x10_int)__LINE__)");
		}
		w.end();
		w.write(")");
		w.end();
		w.write(")");
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
		w.write("(" + emitter.translateType(n.type()) + ")");
		w.write(val);
		w.end(); w.write(")");
	}

	public void visit(NullLit_c n) {
		w.write("NULL");
	}

	public void visit(StringLit_c n) {
		w.write("String(\"");
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
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (context.requireMangling())
			w.write("x10__" + n.id().toString());
		else
			w.write(n.id().toString());
	}


	public void visit(X10Cast_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		w.begin(0);
		String castVar = null;
		DepParameterExpr dpe = null;
		if (n instanceof DepCast_c && n.isDepTypeCheckingNeeded()) {
			dpe = (((DepCast_c) n).dep());
		}
		if (dpe != null || (n.notNullRequired() && !n.isToTypeNullable())) {
			w.write("({");
			w.newline(4);
			w.begin(0);
			emitter.printType(n.castType().type(), w); 
			w.write(" ");
			castVar = getId();
			w.write(castVar);
			w.write (" = ");

		} else {
			w.write("(");
			emitter.printType(n.castType().type(), w);
			w.write(")");
		}
		w.allowBreak(2, " ");
		sw.pushCurrentStream(w);
		n.printSubExpr(n.expr(), true, sw, tr);
		sw.popCurrentStream();
		if (dpe != null || (n.notNullRequired() && !n.isToTypeNullable())) {
			w.write(";");
			emitter.handleX10Cast(n, castVar, w);
			w.write( castVar + "; ");
			w.write("})");
		}
		w.end();

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
		w.write("throw ");
		sw.pushCurrentStream(w);
		n.print(n.expr(), sw, tr);
		sw.popCurrentStream();
		w.write(";");
		return;
	}

	public void visit(Try_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (n.finallyBlock() != null)
			throw new InternalCompilerError("finally blocks not supported");
		// TODO: [IP] create a local class here with the finally block as the destructor


		// TODO: All the places enter the try block. This should
		// be fixed. But needs fix from two other places: variable
		// declaration and return statements of functions.
		//boolean bodyForOnlyPlaceZero = !hasCodeForAllPlaces(n);
		//if (bodyForOnlyPlaceZero) {
		//	w.write("try ");
		//	w.newline(0);
		//	n.printSubStmt(n.tryBlock(), w, tr);
		//} else {
			w.write("try {");
			w.newline(0);
			assert (n.tryBlock() instanceof Block_c);
			sw.pushCurrentStream(w);
			n.printSubStmt(n.tryBlock(), sw, tr);
			sw.popCurrentStream();
			w.newline(0);
			w.write("}");
			w.newline(0);
		//}

		// [IP] C++ will not catch ref types properly, as there is no hierarchy.
		// So, we have to do the dispatching ourselves.
		w.newline();
		String refVar = "__ref__" + getUniqueId_();
		w.write("catch (x10::__ref& " + refVar + ") {");
		w.newline(4); w.begin(0);
		String excVar = "__exc" + refVar;
		String exception_ref = make_ref("Exception");
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

	}

	public void visit(Catch_c n) {
		String excVar = ((X10CPPContext_c)tr.context()).getExceptionVar();
		w.newline();
		w.write("if (");
		String type = emitter.translateType(n.formal().type().type(), true);
		if (type.equals("x10::ref<ClassCastException>")) { // C++ does not have ClassCastException. 
			                                           // Instead it will throw std::bad_cast
			type = "ref<std::bad_cast>&";
		}
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
		w.write(type); w.write (" ");
		w.write(mangled_non_method_name(n.formal().name().id().toString()));
		w.write(" =");
		w.allowBreak(2, " ");
		w.write("(" + type + ") " + excVar + ";");
		w.newline(0);
		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();
		w.end(); w.newline();
		w.write("} else");
	}

	public void visit(Atomic_c a) {
		if (!(a.place() instanceof Here_c)) // TODO: [IP] Ask Vijay what those mean
			throw new InternalCompilerError("placed atomics not supported");
		sw.pushCurrentStream(w);
		a.print(a.body(), sw, tr);
		sw.popCurrentStream();
	}


	public void visit(Await_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		w.write("while (!(");
		w.begin(0);
		sw.pushCurrentStream(w);
		n.print(n.expr(), sw, tr);
		sw.popCurrentStream();
		w.end();
		w.write(")) x10::async_poll();");
		w.newline();
	}

	public void visit(Next_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		w.write("x10::clock_next();");
		w.newline();
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

		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars() && form.isUnnamed()) {
			X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
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

		w.write("{");  
		w.newline(4); w.begin(0);

		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		assert (xts.isPoint(form.type().type()));
		assert (xts.isDistribution(n.domain().type()) || xts.isRegion(n.domain().type()));
		String name = "__i" + form.name();
		w.write("Iterator<point>* " + name + ";");
		w.newline();
		w.write(name + " = &(");
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
		w.write(";");
		w.newline();
		w.write(mangled_non_method_name(form.name().id().toString()));
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

		w.write("x10::dealloc(" + name + ");");
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

		w.write("x10::dealloc(" + name + ");");
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
		
		w.write ("x10::lang::dist " + dist + " = ");
		sw.pushCurrentStream(w);
		n.print(n.domain(), sw, tr);
		sw.popCurrentStream();
		w.write (";");
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

		emitter.emit_general_finish_start(w);
		w.newline();
		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();
		w.newline();
		emitter.emit_general_finish_end(w);
		w.newline();

	}



	public void visit(ArrayAccess_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		sw.pushCurrentStream(w);
		n.printSubExpr(n.array(), sw, tr);
		sw.popCurrentStream();
		// FIXME: [IP] HACK! Inline array accesses
		if (inlineArrayAccesses)
			w.write("->_data[");
		else
		if (arraysAsRefs)
			w.write("->operator[](");
		else
			w.write("[");
		sw.pushCurrentStream(w);
		n.printBlock(n.index(), sw, tr);
		sw.popCurrentStream();
		if (inlineArrayAccesses)
			w.write("]");
		else
		if (arraysAsRefs)
			w.write(")");
		else
			w.write("]");
	}



	public void visit(X10ArrayAccess1_c a) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10TypeSystem_c ts = (X10TypeSystem_c) tr.typeSystem();
		Expr arr = a.array();
		
		// FIXME: [IP] HACK! Inline unique distribution accesses
		if (ts.isDistribution(arr.type()) && isUnique(arr)) {
			w.write("((x10::lang::place)");
			sw.pushCurrentStream(w);
			a.printSubExpr(a.index(), true, sw, tr);
			sw.popCurrentStream();
			if (ts.isPoint(a.index().type())) {
				// Unique distributions are always rank 1
				w.write("->operator[](0)");
			}
			w.write(")");
			return;
		}
		// FIXME: [IP] HACK! Inline array accesses
		if (inlineArrayAccesses && query.isX10Array(arr.type()) &&
				QueryEngine.INSTANCE().isRectangularRankOneLowZero(a) &&
				a.index().type().isPrimitive())
		{
			sw.pushCurrentStream(w);
			a.printSubExpr(arr, sw, tr);
			sw.popCurrentStream();
			w.write("->"+RAW_ADJUSTED+"()[");
			sw.pushCurrentStream(w);
			a.printBlock(a.index(), sw, tr);
			sw.popCurrentStream();
			w.write("]");
			return;
		}
		sw.pushCurrentStream(w);
		a.printSubExpr(arr, sw, tr);
		sw.popCurrentStream();
		if (arraysAsRefs)
			w.write("->operator[](");
		else
			w.write("[");
		sw.pushCurrentStream(w);
		a.printBlock(a.index(), sw, tr);
		sw.popCurrentStream();
		if (arraysAsRefs)
			w.write(")");
		else
			w.write("]");
//		X10Type at = (X10Type)a.type();
//		if (at.isParametric()) {
//		w.write("((");
//		printType((Type)at.typeParameters().get(0), w);
//		w.write(")");
//		}
//		boolean assoc = !(a.array() instanceof New_c);
//		a.printSubExpr(a.array(), assoc, w, tr);
//		w.write("->get(");
//		a.printBlock(a.index(), w, tr);
//		w.write(")");
//		if (at.isParametric()) {
//		w.write(")");
//		}
	}


	public void visit(X10ArrayAccess_c a) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		Expr arr = a.array();
		List index = a.index();
		// FIXME: [IP] HACK! Inline array accesses
		sw.pushCurrentStream(w);
		a.printSubExpr(arr, sw, tr);
		sw.popCurrentStream();
		if (arraysAsRefs)
			w.write("->operator[](");
		else
			w.write("[");
		w.write(("_point<"+index.size()+">")+"(");
		w.begin(0);
		for (Iterator is = index.iterator(); is.hasNext(); ) {
			Expr i = (Expr) is.next();
			sw.pushCurrentStream(w);
			a.printBlock(i, sw, tr);
			sw.popCurrentStream();
			if (is.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");
		if (arraysAsRefs)
			w.write(")");
		else
			w.write("]");
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
		sw.pushCurrentStream(w);
		n.printSubExpr(n.consequent(), true, sw, tr);
		sw.popCurrentStream();
		w.unifiedBreak(2);
		w.write(": ");
		sw.pushCurrentStream(w);
		n.printSubExpr(n.alternative(), true, sw, tr);
		sw.popCurrentStream();
	}


	public void visit(Here_c n) {
		w.write("__here__");
	}

	/**
	 * Asynchronous array copies are treated in the following way:
	 * The async is converted to a call to the push-style runtime routine.
	 * The first two arguments are combined into the source address.
	 * The third and fourth arguments are converted into a closure that
	 * yields the destination address, to be invoked on the target.
	 * The rest of the arguments are the length, the target id, and the
	 * clock from the async (at most one).
	 */
	void processAsyncArrayCopy(Async_c n, X10CPPContext_c context) {
		Stmt body = n.body();
		if (body instanceof Block_c)
			body = (Stmt) ((Block_c) body).statements().get(0);
		Call_c call = (Call_c) ((Eval_c) body).expr();
		Expr target = n.place();
		Expr clock = null;
		// FIXME: handle more than one clock
		if (n.clocks() != null && !n.clocks().isEmpty()) {
			assert (n.clocks().size() == 1);
			clock = (Expr) n.clocks().get(0);
		}
		List args = call.arguments();
		assert (args.size() == 5);
		Expr src = (Expr) args.get(0);
		Expr srcOffset = (Expr) args.get(1);
		Expr dest = (Expr) args.get(2);
		Expr destOffset = (Expr) args.get(3);
		Expr len = (Expr) args.get(4);
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		assert (xts.isX10Array(src.type())&& xts.isX10Array(dest.type()) &&
				xts.equals(X10TypeMixin.baseType(src.type()), X10TypeMixin.baseType(dest.type())));
		Type baseType = X10TypeMixin.baseType(src.type());
		emitter.printAsyncArrayCopyInvocation(n, context, baseType, target, src, srcOffset, 
				dest, destOffset, len,ws, w);
	}

	public void visit(Async_c n) {
		X10CPPContext_c c = (X10CPPContext_c) tr.context();

		//if (!n.clocks().isEmpty())
		//throw new InternalCompilerError("clocked asyncs not supported");

		// TODO: [IP] Switching to a different mechanism
		if (query.isAsyncArrayCopy(n)) { // Special case -- async(...) { Runtime.arrayCopy(...) }
			processAsyncArrayCopy(n, c);
			return;
		}

		// FIXME: Why not merge the below code in the body of
		// processAsync? Suggested by Igor. [Krishna]
		
		String placeVar = getId();
		// FIXME: This should be x10::lang::place [Krishna]
		w.write ("x10_place_t " + placeVar + " = "); 

		if (n.place instanceof Field_c) {
			Field_c n1 = (Field_c)(n.place);
			if (!n1.isTargetImplicit() && (!(n1.target() instanceof X10Special_c)) ) {
				// explicit target.
				Receiver target = n1.target();
				w.write("x10_get_loc (");
				w.write("(x10_addr_t )");
				sw.pushCurrentStream(w);
				n1.print(target, sw, tr);
				sw.popCurrentStream();
				w.write(".operator->())");
			} else{
				w.write("__here__");
				
			}
		} else { // It is array place expression 
				sw.pushCurrentStream(w);
				n.print(n.place, sw, tr);
				sw.popCurrentStream();
		}
		
		w.write(";");
		w.newline();

		emitter.processAsync(n, placeVar, n.body, c, ws, w);
	}
	


	public void visit(ArrayConstructor_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		Type base_type = n.arrayBaseType().type();
		String base = emitter.translateType(base_type, true);
		Expr init = n.initializer();
		if (init == null) {
			w.write("x10::x10newArray<"+base+(base.endsWith(">")?" ":"")+">(");
			w.begin(0);
			sw.pushCurrentStream(w);
			n.printSubExpr(n.distribution(), sw, tr);
			sw.popCurrentStream();
			w.end();
			w.write(")");
			return;
		}

		X10CPPContext_c.Closures a = context.closures;
		boolean outer = outerClosure(a);
		emitter.enterClosure(a, (Closure_c) init);
		int constructor_id = getConstructorId(a);

		w.write("array_init_closure_invocation(" + constructor_id + ",");
		w.allowBreak(0, " ");
		sw.pushCurrentStream(w);
		n.printSubExpr(n.distribution(), sw, tr);
		sw.popCurrentStream();
		w.write(",");
		w.allowBreak(0, " ");

		w.write(base);
		w.write(",");
		w.allowBreak(0, " ");

		sw.pushCurrentStream(w);
		n.printSubExpr(init, sw, tr);
		sw.popCurrentStream();

		w.write(")");
		emitter.exitClosure(a);
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
		w.write("x10::alloc_array<");
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
					boolean needsCast = !ts.equalsWithoutClause((X10Type)base, (X10Type) init_i.type());
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


	public void visit(NewArray_c n) {
		X10CPPContext_c context = (X10CPPContext_c) tr.context();

		newJavaArray(n, n.baseType().type(), n.dims(), n.additionalDims(), (ArrayInit_c) n.init());
	}

	public void visit(X10Special_c n) {
		if (n.qualifier() != null) {
			sw.pushCurrentStream(w);
			n.print(n.qualifier(), sw, tr);
			sw.popCurrentStream();
			w.write(".");
			throw new InternalCompilerError("Qualified "+n.kind()+" not supported");
		}

		X10CPPContext_c context = (X10CPPContext_c) tr.context();
		if (n.kind().equals(X10Special_c.THIS) && (context.inlining || context.insideClosure)) {
			w.write(SAVED_THIS);
			if (context.insideClosure)
				context.saveEnvVariableInfo(THIS);
			return;
		}
		if (n.kind().equals(X10Special_c.SUPER)) {
			w.write(emitter.translateType(context.currentClass().superType()));
			return;
		}
		if (n.isSelf()) {
			// FIXME: Why are we printing the string "self"?
			// Confirm with Igor. [Krishna]
			w.write((context.Self() == null)? "self":context.Self());
		}
		else w.write(n.kind().toString());
	}


	public void visit(Closure_c n) {
		X10CPPContext_c c = (X10CPPContext_c) tr.context();

		c.setinsideClosure(true);

		X10CPPContext_c.Closures a = c.closures;
		int constructor_id = getConstructorId(a);
		boolean outer = a.nesting == 1;  // TODO: clean up this hack

		// create closure and packed arguments
		ClassifiedStream w = this.w;
		if (outer) w = ws.getNewStream(WriterStreams.StreamClass.Closures);
		else w.newline();


		Type base_type = n.returnType().type();
		String base = emitter.translateType(base_type, true);
		String className = emitter.translateType(c.currentClass());
		w.write("array_init_closure_and_args_struct(" + className + ",");
		w.allowBreak(0, " ");
		w.write(constructor_id + ",");
		w.allowBreak(0, " ");
		w.write(base + ",");
		w.allowBreak(0, " ");

		// need to invoke this macro to make sure we visit the args before the body
		w.write("array_init_unpacked_body(");
		// arguments of the initializer function
		w.write("(");
//		w.begin(0);
//		w.allowBreak(2, 2, "", 0);
		assert (n.formals().size() == 1);
		X10TypeSystem_c xts = (X10TypeSystem_c) tr.typeSystem();
		for (Iterator i = n.formals().iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			assert (xts.isPoint(f.type().type()));
			sw.pushCurrentStream(w);
			n.print(f, sw, tr);
			sw.popCurrentStream();
//			w.write("const point& " + f.name());
//			f.addDecls(c);
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
//		w.end(); w.allowBreak(0, " ");
		w.write(")");
		w.write(","); w.allowBreak(0, " ");

		sw.pushCurrentStream(w);
		n.print(n.body(), sw, tr);
		sw.popCurrentStream();
		w.write(",");
		w.allowBreak(0, " ");

		if (useStructsForArrayInitArgs) {
			emitter.unpackArgs(w, c, c.variables, constructor_id, INIT_PREFIX);
		}

		w.write(")");
		w.write(","); w.allowBreak(0, " ");

		w.write("(");
		w.begin(0);
		w.allowBreak(2, 2, "", 0);

		if (useStructsForArrayInitArgs) { w.write(VOID_PTR+" args, "); }

		assert (n.formals().size() == 1);
		for (Iterator i = n.formals().iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			assert (xts.isPoint(f.type().type()));
			sw.pushCurrentStream(w);
			n.print(f, sw, tr);
			sw.popCurrentStream();
//			w.write("const point& " + f.name());
//			f.addDecls(c);
			if (i.hasNext()) {
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
			emitter.printArgumentList(w, c);
		}
		w.end();
		w.allowBreak(2, 2, "", 0);
		w.write(")");
		w.write(",");
		w.newline();

		emitter.createPackedArgumentsStruct(w, c, constructor_id, INIT_PREFIX);
		w.newline();

		w.write(");");
		w.newline(0);
		w.forceNewline(0);

		if (outer) w = this.w;

		c.setinsideClosure(false);

		// create closure invocation
		emitter.instantiateArguments(w, c, n.position());
		int id = getConstructorId(a);
		a.arrayInitializerParameters.set(id, c.variables);

		c.finalizeClosureInstance();
	}



	public void visit(ClosureCall_c n) { // TODO: [IP] Remove
		throw new InternalCompilerError("Closure calls not supported");
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

		if (unsigned_op)
			w.write("((unsigned)");
		sw.pushCurrentStream(w);
		n.printSubExpr(n.left(), true, sw, tr);
		sw.popCurrentStream();
		if (unsigned_op)
			w.write(")");
		w.write(" ");
		w.write(opString);
		w.allowBreak(n.type() == null || n.type().isPrimitive() ? 2 : 0, " ");
		if (unsigned_op)
			w.write("((unsigned)");
		sw.pushCurrentStream(w);
		n.printSubExpr(n.right(), false, sw, tr);
		sw.popCurrentStream();
		if (unsigned_op)
			w.write(")");
	}


	void declarePackedArgumentsStruct(ClassifiedStream w, X10CPPContext_c c, int id) {
		w.write("struct "+args_name(INIT_PREFIX, id)+";"); w.newline();
	}

	public void visit(ArrayInit_c n) {
		throw new InternalCompilerError("Should not be invoked");
	}




} // end of MessagePassingCodeGenerator
