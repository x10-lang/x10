/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.visit;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Assert_c;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Case_c;
import polyglot.ast.Catch;
import polyglot.ast.Catch_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FlagsNode_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id;
import polyglot.ast.Id_c;
import polyglot.ast.If_c;
import polyglot.ast.Import_c;
import polyglot.ast.IntLit_c;
import polyglot.ast.Labeled_c;
import polyglot.ast.Lit;
import polyglot.ast.Lit_c;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Local_c;
import polyglot.ast.Loop_c;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Receiver;
import polyglot.ast.Return_c;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Throw_c;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.Try;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.frontend.Source;
import polyglot.types.ArrayType;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.MethodInstance_c;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.Translator;
import x10.Configuration;
import x10.ast.AssignPropertyCall_c;
import x10.ast.Async_c;
import x10.ast.AtEach_c;
import x10.ast.AtExpr_c;
import x10.ast.AtStmt_c;
import x10.ast.Atomic_c;
import x10.ast.Await_c;
import x10.ast.ClosureCall;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.Contains_c;
import x10.ast.Finish_c;
import x10.ast.ForLoop_c;
import x10.ast.Future_c;
import x10.ast.Here_c;
import x10.ast.LocalTypeDef_c;
import x10.ast.Next_c;
import x10.ast.ParExpr;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl;
import x10.ast.SettableAssign;
import x10.ast.SettableAssign_c;
import x10.ast.StmtExpr_c;
import x10.ast.StmtSeq_c;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.TypeParamNode;
import x10.ast.TypeParamNode_c;
import x10.ast.When_c;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Cast_c;
import x10.ast.X10ClassBody_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10Formal;
import x10.ast.X10Instanceof_c;
import x10.ast.X10IntLit_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10Return_c;
import x10.ast.X10Special;
import x10.ast.X10Unary_c;
import x10.emitter.CastExpander;
import x10.emitter.Emitter;
import x10.emitter.Expander;
import x10.emitter.Inline;
import x10.emitter.Join;
import x10.emitter.Loop;
import x10.emitter.RuntimeTypeExpander;
import x10.emitter.Template;
import x10.emitter.TryCatchExpander;
import x10.emitter.TypeExpander;
import x10.types.FunctionType;
import x10.types.ParameterType;
import x10.types.ParameterType.Variance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10c.ast.X10CBackingArrayAccessAssign_c;
import x10c.ast.X10CBackingArrayAccess_c;
import x10c.ast.X10CBackingArrayNewArray_c;
import x10c.ast.X10CBackingArray_c;
import x10c.types.X10CContext_c;


/**
 * Visitor on the AST nodes that for some X10 nodes triggers the template
 * based dumping mechanism (and for all others just defaults to the normal
 * pretty printing).
 *
 * @author Christian Grothoff
 * @author Igor Peshansky (template classes)
 * @author Rajkishore Barik 26th Aug 2006 (added loop optimizations)
 * @author vj Refactored Emitter out.
 */
public class X10PrettyPrinterVisitor extends X10DelegatingVisitor {
	public static final String X10_RUNTIME_TYPE_CLASS = "x10.rtt.Type";
	public static final String X10_FUN_CLASS_PREFIX = "x10.core.fun.Fun";
	public static final String X10_RUNTIME_CLASS = "x10.runtime.impl.java.Runtime";

	public static final int PRINT_TYPE_PARAMS = 1;
	public static final int BOX_PRIMITIVES = 2;
	public static final int NO_VARIANCE = 4;
	public static final int NO_QUALIFIER = 8;
	public static final boolean reduce_generic_cast = true;

	public static final String RETURN_PARAMETER_TYPE_SUFFIX = "$G";

	final public CodeWriter w;
	final public Translator tr;
	final public Emitter er;

	public final Type imcType;
    public static final String JAVA_LANG_OBJECT = "java.lang.Object";
    public static final boolean isSelfDispatch = true;
    public static final boolean isGenericOverloading = true;
	
    private static final String X10_RTT_TYPES = "x10.rtt.Types";

	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	public static Integer getUniqueId_() {
		return new Integer(nextId_++);
	}

	public static Name getId() {
		return Name.make("__var" + getUniqueId_() + "__");
	}

	public X10PrettyPrinterVisitor(CodeWriter w, Translator tr) {
		this.w = w;
		this.tr = tr;
		this.er = new Emitter(w,tr);
		try {
		    imcType = tr.typeSystem().typeForName(QName.make("x10.util.IndexedMemoryChunk"));
		} catch (SemanticException e1) {
		    throw new InternalCompilerError("Something is terribly wrong");
		}
	}

	public void visit(Node n) {
	    
	    // invoke appropriate visit method for Java backend's specific nodes
	    if (n instanceof X10CBackingArray_c) {visit((X10CBackingArray_c) n);return;}
	    if (n instanceof X10CBackingArrayAccess_c) {visit((X10CBackingArrayAccess_c) n); return;} 
	    if (n instanceof X10CBackingArrayAccessAssign_c) {visit((X10CBackingArrayAccessAssign_c)n); return;}
        if (n instanceof X10CBackingArrayNewArray_c) {visit((X10CBackingArrayNewArray_c)n); return;}
	    
	    if (n instanceof FlagsNode_c) {visit((FlagsNode_c)n); return;}
	    if (n instanceof TypeParamNode_c) {visit((TypeParamNode_c)n); return;}
	    
	    // already known unhandled node type
	    if (
	        n instanceof Async_c || n instanceof AtStmt_c || n instanceof Atomic_c || n instanceof Here_c 
	        || n instanceof Await_c || n instanceof Next_c || n instanceof Future_c || n instanceof AtExpr_c
	        || n instanceof AtEach_c || n instanceof When_c
	        || n instanceof Finish_c || n instanceof Contains_c
	    ) {
	        tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
	            "Unhandled node type: "+n.getClass(), n.position());
	        return;
	    }
	    
	    System.err.println(n.position() + ": Unhandled node type: " + n.getClass());
	    // FIXME
//    	    tr.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
// 	        "Unhandled node type: "+n.getClass(), n.position());

	    // Don't call through del; that would be recursive.
	    n.translate(w, tr);
    	}

	public void visit(FlagsNode_c n) {
	    n.translate(w, tr);
	}
	public void visit(TypeParamNode_c n) {
	    n.translate(w, tr);
	}
	public void visit(X10ClassBody_c n) {
	    n.translate(w, tr);
	}
	public void visit(PackageNode_c n) {
	    n.translate(w, tr);
	}
	public void visit(Loop_c n) {
	    n.translate(w, tr);
	}
	public void visit(Return_c n) {
	    n.translate(w, tr);
	}
	public void visit(Eval_c n) {
	    n.translate(w, tr);
	}
	public void visit(Local_c n) {
	    n.translate(w, tr);
	}
	public void visit(Lit_c n) {
	    n.translate(w, tr);
	}
	public void visit(If_c n) {
	    n.translate(w, tr);
	}
	public void visit(Conditional_c n) {
        n.translate(w, tr);
	}
	public void visit(ParExpr_c n) {
	    n.translate(w, tr);
	}
	public void visit(Unary_c n) {
	    n.translate(w, tr);
	}
	public void visit(StmtSeq_c n) {
	    n.translate(w, tr);
	}
	public void visit(Branch_c n) {
	    n.translate(w, tr);
	}
	public void visit(Throw_c n) {
	    n.translate(w, tr);
	}
	public void visit(Catch_c n) {
	    n.translate(w, tr);
	}
	public void visit(Empty_c n) {
	    n.translate(w, tr);
	}
	public void visit(Assert_c n) {
	    n.translate(w, tr);
	}
	public void visit(Switch_c n) {
	    n.translate(w, tr);
	}
	public void visit(Case_c n) {
	    n.translate(w, tr);
	}
	public void visit(SwitchBlock_c n) {
	    n.translate(w, tr);
	}
	public void visit(LocalTypeDef_c n) {
	    n.translate(w, tr);
	}

	public void visit(AssignPropertyCall_c n) {
	    // TODO: initialize properties in the Java constructor
	    Context ctx = tr.context();
	    List<X10FieldInstance> definedProperties = n.properties();
	    List<Expr> arguments = n.arguments();
	    int aSize = arguments.size();
	    assert (definedProperties.size() == aSize);

	    for (int i = 0; i < aSize; i++) {
	        Expr arg = arguments.get(i);
	        X10FieldInstance fi = definedProperties.get(i);
	        w.write("this.");
	        w.write(Emitter.mangleToJava(fi.name()));
	        w.write(" = ");
	        er.coerce(n, arg, fi.type());
	        w.write(";");
	        w.newline();
	    }
	}

	public void visit(Block_c n) {
	    String s = er.getJavaImplForStmt(n, (X10TypeSystem) tr.typeSystem());
	      if (s != null) {
	          w.write(s);
	      } else {
	          n.translate(w, tr);
	      }
	}

	public void visit(StmtExpr_c n) {
	    final Context c = tr.context();
	    final ArrayList<LocalInstance> capturedVars = new ArrayList<LocalInstance>();
	    // This visitor (a) finds all captured local variables,
	    // (b) adds a qualifier to every "this", and
	    // (c) rewrites fields and calls to use an explicit "this" target.
	    n = (StmtExpr_c) n.visit(new ContextVisitor(tr.job(), tr.typeSystem(), tr.nodeFactory()) {
	        public Node leaveCall(Node n) {
	            if (n instanceof Local) {
	                Local l = (Local) n;
	                assert (l.name() != null) : l.position().toString();
	                VarInstance<?> found = c.findVariableSilent(l.name().id());
	                if (found != null) {
	                    VarInstance<?> local = context().findVariableSilent(l.name().id());
	                    if (found.equals(local)) {
	                        assert (found.equals(l.localInstance())) : l.toString();
	                        capturedVars.add(l.localInstance());
	                    }
	                }
	            }
	            if (n instanceof Field_c) {
	                Field_c f = (Field_c) n;
	                return f.target((Receiver) f.target()).targetImplicit(false);
	            }
	            if (n instanceof X10Call_c) {
	                X10Call_c l = (X10Call_c) n;
	                return l.target((Receiver) l.target()).targetImplicit(false);
	            }
	            if (n instanceof Special_c) {
	                NodeFactory nf = nodeFactory();
	                Special_c s = (Special_c) n;
	                if (s.qualifier() == null) {
	                    return s.qualifier(nf.CanonicalTypeNode(n.position(), s.type()));
	                }
	            }
	            return n;
	        }
	    }.context(c.pushBlock()));
	    for (LocalInstance li : capturedVars) {
	        if (!li.flags().isFinal()) {
	            System.err.println("Bad statement expression: " +n+ " at " +n.position()); // DEBUG
	            n.dump(System.err);                                                        // DEBUG
	            throw new InternalCompilerError("Statement expression uses non-final variable " +li+ "(at " +li.position()+ ") from the outer scope", n.position());
	        }
	    }
	    w.write("(new " + JAVA_LANG_OBJECT + "() { ");
	    er.printType(n.type(), PRINT_TYPE_PARAMS);
	    w.write(" eval() {");
	    w.newline(4); w.begin(0);
	    Translator tr = this.tr.context(c.pushBlock());
	    List<Stmt> statements = n.statements();
	    for (Stmt stmt : statements) {
	        tr.print(n, stmt, w);
	        w.newline();
	    }
	    w.write("return ");
	    tr.print(n, n.result(), w);
	    w.write(";");
	    w.end(); w.newline();
	    w.write("} }.eval())");
	}

	public void visit(LocalAssign_c n) {
		Local l = n.local();
		TypeSystem ts = tr.typeSystem();
		if (n.operator() == Assign.ASSIGN || l.type().isNumeric() || l.type().isBoolean() || l.type().isSubtype(ts.String(), tr.context())) {
			tr.print(n, l, w);
			w.write(" ");
			w.write(n.operator().toString());
			w.write(" ");
			er.coerce(n, n.right(), l.type());
		}
		else {
			Binary.Operator op = n.operator().binaryOperator();
			Name methodName = X10Binary_c.binaryMethodName(op);
			tr.print(n, l, w);
			w.write(" = ");
			tr.print(n, l, w);
			w.write(".");
			w.write(Emitter.mangleToJava(methodName));
			w.write("(");
			tr.print(n, n.right(), w);
			w.write(")");
		}
	}

	public void visit(FieldAssign_c n) {
		Type t = n.fieldInstance().type();

		TypeSystem ts = tr.typeSystem();
		if (n.operator() == Assign.ASSIGN || t.isNumeric() || t.isBoolean() || t.isSubtype(ts.String(), tr.context())) {
			tr.print(n, n.target(), w);
			w.write(".");
			w.write(Emitter.mangleToJava(n.name().id()));
			w.write(" ");
			w.write(n.operator().toString());
			w.write(" ");
			er.coerce(n, n.right(), n.fieldInstance().type());
		}
		else if (n.target() instanceof TypeNode || n.target() instanceof Local || n.target() instanceof Lit) {
			// target has no side effects--evaluate it more than once
			Binary.Operator op = n.operator().binaryOperator();
			Name methodName = X10Binary_c.binaryMethodName(op);
			tr.print(n, n.target(), w);
			w.write(".");
			w.write(Emitter.mangleToJava(n.name().id()));
			w.write(" ");
			w.write(" = ");
			tr.print(n, n.target(), w);
			w.write(".");
			w.write(Emitter.mangleToJava(n.name().id()));
			w.write(".");
			w.write(Emitter.mangleToJava(methodName));
			w.write("(");
			tr.print(n, n.right(), w);
			w.write(")");
		}
		else {
			// x.f += e
			// -->
			// new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
			Binary.Operator op = n.operator().binaryOperator();
			Name methodName = X10Binary_c.binaryMethodName(op);
			w.write("new " + JAVA_LANG_OBJECT + "() {");
			w.allowBreak(0, " ");
			w.write("final ");
			er.printType(n.type(), PRINT_TYPE_PARAMS);
			w.write(" eval(");
			er.printType(n.target().type(), PRINT_TYPE_PARAMS);
			w.write(" target, ");
			er.printType(n.right().type(), PRINT_TYPE_PARAMS);
			w.write(" right) {");
			w.allowBreak(0, " ");
			w.write("return (target.");
			w.write(Emitter.mangleToJava(n.name().id()));
			w.write(" = ");
			w.write("target.");
			w.write(Emitter.mangleToJava(n.name().id()));
			w.write(".");
			w.write(Emitter.mangleToJava(methodName));
			w.write("(right));");
			w.allowBreak(0, " ");
			w.write("} }.eval(");
			tr.print(n, n.target(), w);
			w.write(", ");
			tr.print(n, n.right(), w);
			w.write(")");
		}
	}


	public void visit(X10MethodDecl_c n) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

		Flags flags = n.flags().flags();

		if (n.formals().size() == 1 && isMainMethod(ts, flags, n.name(), n.returnType().type(), n.formals().get(0).declType())){
			/*Expander throwsClause = new Inline(er, "");
			if (n.throwTypes().size() > 0) {
				List<Expander> l = new ArrayList<Expander>();
				for (TypeNode tn : n.throwTypes()) {
					l.add(new TypeExpander(er, tn.type(), PRINT_TYPE_PARAMS));
				}
				throwsClause = new Join(er, "", "throws ", new Join(er, ", ", l));
			}*/

			// SYNOPSIS: main(#0) #3 #1    #0=args #1=body #2=class name 
			String regex = "public static class Main extends x10.runtime.impl.java.Runtime {\n" +
			    "public static void main(java.lang.String[] args) {\n" +
			        "// start native runtime\n" +
			        "new Main().start(args);\n" +
			    "}\n" +
			    "\n" +
			    "// called by native runtime inside main x10 thread\n" +
			    "public void main(final x10.array.Array<java.lang.String> args) {\n" +
			        "try {\n" +
			            "// start xrx\n" +
			            "x10.lang.Runtime.start(\n" +
			                "// static init activity\n" +
			                "new x10.core.fun.VoidFun_0_0() {\n" +
			                    "public void apply() {\n" +
			                        "// preload classes\n" +
			                        "if (Boolean.getBoolean(\"x10.PRELOAD_CLASSES\")) {\n" +
			                            "x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean(\"x10.PRELOAD_STRINGS\"));\n" +
			                        "}\n" +
			                    "}\n" +
			                    "public x10.rtt.RuntimeType<?> getRTT() {\n" +
			                        "return _RTT;\n" +
			                    "}\n" +
			                    "public x10.rtt.Type<?> getParam(int i) {\n" +
			                        "return null;\n" +
			                    "}\n" +
			                "},\n" +
			                "// body of main activity\n" +
			                "new x10.core.fun.VoidFun_0_0() {\n" +
			                    "public void apply() {\n" +
			                        "// catch and rethrow checked exceptions\n" +
                                    "// (closures cannot throw checked exceptions)\n" +
			                        "try {\n" +
			                            "// call the original app-main method\n" +
			                            "#2.main(args);\n" +
			                        "} catch (java.lang.RuntimeException e) {\n" +
			                            "throw e;\n" +
			                        "} catch (java.lang.Error e) {\n" +
			                            "throw e;\n" +
			                        "} catch (java.lang.Throwable t) {\n" +
			                            "throw new x10.runtime.impl.java.X10WrappedThrowable(t);\n" +
			                        "}\n" +
			                    "}\n" +
                                "public x10.rtt.RuntimeType<?> getRTT() {\n" +
                                    "return _RTT;\n" +
                                "}\n" +
                                "public x10.rtt.Type<?> getParam(int i) {\n" +
                                    "return null;\n" +
                                "}\n" +
			                "});\n" +
			        "} catch (java.lang.Throwable t) {\n" +
			            "t.printStackTrace();\n" +
			        "}\n" +
			    "}\n" +
			"}\n" +
            "\n" +
			"// the original app-main method\n" +
			"public static void main(#0)  #1";
			er.dumpRegex("Main", new Object[] { n.formals().get(0), n.body(), tr.context().currentClass().name() }, tr, regex);

			return;
		}

		X10MethodInstance mi = (X10MethodInstance) n.methodDef().asInstance();
		er.generateMethodDecl(n, false);
	}

    private boolean isMainMethod(X10TypeSystem ts, Flags flags, Id name, Type returnType, Type argType) {
        return isMainMethod(ts, flags, name.id(), returnType, argType, tr.context());
    }

    public static boolean isMainMethod(X10TypeSystem ts, Flags flags, Name name, Type returnType, Type argType, Context context) {
        return name.toString().equals("main") &&
                flags.isPublic() &&
                flags.isStatic() &&
                returnType.isVoid() &&
                argType.isSubtype(ts.Array(ts.String()), context);
    }

    public static boolean isMainMethodInstance(MethodInstance mi, Context context) {
        return mi.formalTypes().size() == 1 && isMainMethod((X10TypeSystem) mi.typeSystem(), mi.flags(), mi.name(), mi.returnType(), mi.formalTypes().get(0), context);
    }

	public void visit(Id_c n) {
                w.write(Emitter.mangleToJava(n.id()));
	}


	public void visit(X10ConstructorDecl_c n) {
		w.begin(0);

		tr.print(n, tr.nodeFactory().FlagsNode(n.flags().position(), n.flags().flags().clearPrivate().clearProtected().Public()), w);
		tr.print(n, n.name(), w);
		w.write("(");

		w.begin(0);

		X10ConstructorDef ci = (X10ConstructorDef) n.constructorDef();
		X10ClassType ct = (X10ClassType) Types.get(ci.container());
		List<String> typeAssignments = new ArrayList<String>();

		for (Iterator<ParameterType> i = ct.x10Def().typeParameters().iterator(); i.hasNext(); ) {
			ParameterType p = (ParameterType) i.next();
			w.write("final ");
			w.write(X10_RUNTIME_TYPE_CLASS);
			w.write(" ");
			w.write(Emitter.mangleToJava(p.name()));

			typeAssignments.add("this." + p.name() + " = " + p.name() + ";");

			if (i.hasNext() || n.formals().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		for (Iterator<Formal> i = n.formals().iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			n.print(f, w, tr);

			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		w.end();
		w.write(")");
/*
		if (! n.throwTypes().isEmpty()) {
			w.allowBreak(6);
			w.write("throws ");

			for (Iterator<TypeNode> i = n.throwTypes().iterator(); i.hasNext(); ) {
				TypeNode tn = (TypeNode) i.next();
				er.printType(tn.type(), PRINT_TYPE_PARAMS);

				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(4, " ");
				}
			}
		}
*/
		w.end();

		if (n.body() != null) {
			if (typeAssignments.size() > 0) {
				w.write(" {");
				w.begin(4);
				if (n.body().statements().size() > 0) {
					if (n.body().statements().get(0) instanceof ConstructorCall) {
						ConstructorCall cc = (ConstructorCall) n.body().statements().get(0);
						n.printSubStmt(cc, w, tr);
						w.allowBreak(0, " ");
						if (cc.kind() == ConstructorCall.THIS)
							typeAssignments.clear();
					}
				}
				for (String s : typeAssignments) {
					w.write(s);
					w.allowBreak(0, " ");
				}
				if (n.body().statements().size() > 0) {
					if (n.body().statements().get(0) instanceof ConstructorCall)
						n.printSubStmt(n.body().statements(n.body().statements().subList(1, n.body().statements().size())), w, tr);
					// vj: the main body was not being written. Added next two lines.
					else 
						n.printSubStmt(n.body(), w, tr);
				}
				else
					n.printSubStmt(n.body(), w, tr);
				w.write("}");
				w.end();
			}
			else {
				n.printSubStmt(n.body(), w, tr);
			}
		}
		else {
			w.write(";");
		}
	}

	public void visit(TypeDecl_c n) {
		// Do not write anything.
		return;
	}


	private boolean hasCustomSerializer(X10ClassDecl_c n) {
        boolean hasCustomSerializer = false;
        for (TypeNode tn: n.interfaces()) {
            if ("x10.io.CustomSerialization".equals(tn.type().toString())) {
                hasCustomSerializer = true;
            }
        }
        return hasCustomSerializer;
	}

	public void visit(X10ClassDecl_c n) {
	    String className = n.classDef().name().toString();
	    X10CContext_c context = (X10CContext_c) tr.context();
	    if (n.classDef().isTopLevel() && !n.classDef().sourceFile().name().equals(className + ".x10") && !context.isContainsGeneratedClasses(n.classDef())) {
	        context.addGeneratedClasses(n.classDef());
	        // not include import
	        SourceFile sf = tr.nodeFactory().SourceFile(n.position(), Collections.<TopLevelDecl>singletonList(n));
	        if (n.classDef().package_() != null) {
	            sf = sf.package_(tr.nodeFactory().PackageNode(n.position(), n.classDef().package_()));
	        }
	        sf = sf.source(new Source(n.classDef().name().toString() + ".x10", n.position().path(), null));
	        tr.translate(sf);
	        return;
	    }
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();

		X10ClassDef def = (X10ClassDef) n.classDef();

		// Do not generate code if the class is represented natively.
		if (er.getJavaRep(def) != null) {
			w.write(";");
			w.newline();
			return;
		}

		//	    StringBuilder sb = new StringBuilder();
		//	    for (TypeParamNode t : n.typeParameters()) {
		//		if (sb.length() > 0) {
		//		    sb.append(", ");
		//		}
		//		sb.append("\"");
		//		sb.append(t.name().id());
		//		sb.append("\"");
		//	    }

		//	    if (sb.length() > 0) {
		//		w.write("@x10.generics.Parameters({");
		//		w.write(sb.toString());
		//		w.write("})");
		//		w.allowBreak(0);
		//	    }

		//	    @Parameters({"T"})
		//	    public class List implements Runnable {
		//	    	public class T {};
		//	    	public List() { }
		//	    	@Synthetic public List(Class T) { this(); }
		//	    	@Synthetic public static boolean instanceof$(Object o, String constraint) { assert(false); return true; }
		//	    	public static boolean instanceof$(Object o, String constraint, boolean b) { /*check constraint*/; return b; }
		//	    	public static Object cast$(Object o, String constraint) { /*check constraint*/; return (List)o; }
		//	    	T x;

		X10Flags flags = X10Flags.toX10Flags( n.flags().flags());

		w.begin(0);
		if (flags.isInterface()) {
			w.write(X10Flags.toX10Flags(flags.clearInterface().clearAbstract()).translate());
		}
		else {
			w.write(X10Flags.toX10Flags(flags).translate());
		}

		if (flags.isInterface()) {
			w.write("interface ");
		}
		else {
			w.write("class ");
		}

		tr.print(n, n.name(), w);

		List<TypeParamNode> typeParameters = n.typeParameters();
        if (typeParameters.size() > 0) {
			er.printTypeParams(n, context, typeParameters);
		}

        final TypeNode superClassNode = n.superClass();
        if (superClassNode!= null || flags.isStruct()) {
			w.allowBreak(0);
			w.write("extends ");
            if (flags.isStruct()) {
                assert superClassNode==null : superClassNode;
                w.write("x10.core.Struct");
            } else {
                assert superClassNode!=null;
			    Type superType = superClassNode.type();
                // FIXME: HACK! If a class extends x10.lang.Object, swipe in x10.core.Ref
                if (!xts.typeEquals(superType, xts.Object(), context))
                    er.printType(superType, PRINT_TYPE_PARAMS | BOX_PRIMITIVES | NO_VARIANCE);
                else
                    w.write("x10.core.Ref");
            }
		}

		// Filter out x10.lang.Any from the interfaces.
		List<TypeNode> interfaces = new ArrayList<TypeNode>();

		for (TypeNode tn: n.interfaces()) {
		    if (!xts.isAny(tn.type())) {
		        interfaces.add(tn);
		    }
		}
	/* Interfaces automatically extend Any
	  	if (n.flags().flags().isInterface() && interfaces.isEmpty()) {
	
		    X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		    interfaces.add(tr.nodeFactory().CanonicalTypeNode(n.position(), ts.Any()));
		}
*/
		List<Type> alreadyPrintedTypes = new ArrayList<Type>();
		if (!interfaces.isEmpty()) {
			w.allowBreak(2);
			if (flags.isInterface()) {
				w.write("extends ");
			}
			else {
				w.write("implements ");
			}

			w.begin(0);
			for (Iterator<TypeNode> i = interfaces.iterator(); i.hasNext(); ) {
				TypeNode tn = (TypeNode) i.next();
				if (!isSelfDispatch || (isSelfDispatch && !alreadyPrinted(alreadyPrintedTypes, tn))) {
				    if (alreadyPrintedTypes.size() != 0) {
				        w.write(",");
				        w.allowBreak(0);
				    }
				    alreadyPrintedTypes.add(tn.type());
				    er.printType(tn.type(), (isSelfDispatch ? 0 : PRINT_TYPE_PARAMS) | BOX_PRIMITIVES | NO_VARIANCE);
				}
			}
			w.end();
		}
		w.unifiedBreak(0);
		w.end();
		w.write("{");
		w.newline(4); w.begin(0);

		// Generate the run-time type.  We have to wrap in a class since n might be an interface
		// and interfaces can't have static methods.

//		er.generateRTTMethods(def);

//		boolean isValueType = xts.isValueType(def.asType(), (X10Context) tr.context());
//		if (def.isTopLevel()) {
//			er.generateRTType(def);
//		}
		
		// XTENLANG-1102
		er.generateRTTInstance(def);
		
		if (hasCustomSerializer(n)) {
            er.generateCustomSerializer(def);
        }

		// Generate dispatcher methods.
//		er.generateDispatchers(def);

		if (isSelfDispatch) {
		    er.generateDispatchMethods(def);
		}
		er.generateBridgeMethods(def);

		if (typeParameters.size() > 0) {
			w.newline(4);
			w.begin(0);
			if (! flags.isInterface()) {
				for (TypeParamNode tp : typeParameters) {
					w.write("private ");
					w.write(X10_RUNTIME_TYPE_CLASS);
//					w.write("<"); n.print(tp, w, tr); w.write(">");  // TODO
					w.write(" ");
					n.print(tp.name(), w, tr);
					w.write(";");
					w.newline();
				}
			}
			w.end();
		}

		if (! flags.isInterface()) {
			if (n.properties().size() > 0) {
				w.newline(4);
				w.begin(0);
				for (PropertyDecl pd : n.properties()) {
					n.print(pd, w, tr);
					w.newline();
				}
				w.end();
			}
		}

		n.print(n.body(), w, tr);

		w.end(); w.newline();
		w.write("}");
		w.newline(0);

//		if (def.isLocal()) {
//			er.generateRTType(def);
//		}
	}

    private boolean alreadyPrinted(List<Type> alreadyPrintedTypes, TypeNode tn) {
        boolean alreadyPrinted = false;
        Type type = tn.type();
        if (type instanceof FunctionType) {
            if (((FunctionType) type).returnType().isVoid()) {
                for (Type apt : alreadyPrintedTypes) {
                    if (apt instanceof FunctionType && ((FunctionType) apt).returnType().isVoid()) {
                        if (((FunctionType) apt).typeArguments().size() == ((FunctionType) type).typeArguments().size()) {
                            alreadyPrinted = true;
                            break;
                        }
                    }
                }
            } else {
                for (Type apt : alreadyPrintedTypes) {
                    if (apt instanceof FunctionType && !((FunctionType) apt).returnType().isVoid()) {
                        if (((FunctionType) apt).typeArguments().size() == ((FunctionType) type).typeArguments().size()) {
                            alreadyPrinted = true;
                            break;
                        }
                    }
                }
            }
        }
        else if (type instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) type;
            if (ct.typeArguments().size() > 0) {
                for (Type apt : alreadyPrintedTypes) {
                    if (apt instanceof X10ClassType && !(apt instanceof FunctionType)) {
                        if (((X10ClassType) apt).name().toString().equals(((X10ClassType) type).name().toString())) {
                            alreadyPrinted = true;
                            break;
                        }
                    }
                }
            }
        }
        return alreadyPrinted;
    }




        public void visit(X10Cast_c c) {
            TypeNode tn = c.castType();
            assert tn instanceof CanonicalTypeNode;
            
            Expr expr = c.expr();
            Type type = expr.type();


            switch (c.conversionType()) {
            case CHECKED:
            case PRIMITIVE:
            case SUBTYPE:
            case UNCHECKED:
                    if (tn instanceof X10CanonicalTypeNode) {
                            X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;

                            Type t = X10TypeMixin.baseType(xtn.type());
                            Expander ex = new TypeExpander(er, t, PRINT_TYPE_PARAMS);

                            Expander rt = new RuntimeTypeExpander(er, t);

                            // Note: constraint checking should be desugared when compiling without NO_CHECKS flag

                            // e.g. any as Int (any:Any), t as Int (t:T)
                            if (
                                    (X10TypeMixin.baseType(type) instanceof ParameterType || ((X10TypeSystem) type.typeSystem()).isAny(X10TypeMixin.baseType(type)))
                                    && (t.isBoolean() || t.isByte() || t.isShort() || t.isInt() || t.isLong() || t.isFloat() || t.isDouble() || t.isChar())
                            ) { 
                                w.write(X10_RTT_TYPES + ".as");
                                new TypeExpander(er, t, NO_QUALIFIER).expand(tr);
                                w.write("(");
                                c.printSubExpr(expr, w, tr);
                                w.write(")");
                            }
                            // all unsigned types come here
                            else if (t.isBoolean() || t.isNumeric() || t.isChar() /*|| type.isSubtype(t, tr.context())*/) {
                                    w.begin(0);
                                    w.write("("); // put "(Type) expr" in parentheses.
                                    w.write("(");
                                    ex.expand(tr);
                                    w.write(")");
                                    // e.g. d as Int (d:Double) -> (int)(double)(Double) d 
                                    if (type.isBoolean() || type.isNumeric() || type.isChar()) {
                                            w.write(" ");
                                            w.write("(");
                                            new TypeExpander(er, type, 0).expand(tr);
                                            w.write(")");
                                            w.write(" ");
                                            if (!(expr instanceof Unary || expr instanceof Lit) && (expr instanceof X10Call)) {
                                                w.write("(");
                                                new TypeExpander(er, type, BOX_PRIMITIVES).expand(tr);
                                                w.write(")");
                                            }
                                    }
                                    w.allowBreak(2, " ");
                                    // HACK: (java.lang.Integer) -1
                                    //       doesn't parse correctly, but
                                    //       (java.lang.Integer) (-1)
                                    //       does
                                    boolean needParan = expr instanceof Unary || expr instanceof Lit || expr instanceof Conditional_c;
                                    if (needParan)
                                            w.write("(");
                                    c.printSubExpr(expr, w, tr);
                                    if (needParan)
                                            w.write(")");
                                    w.write(")");
                                    w.end();
                            }
                            else if (type.isSubtype(t, tr.context())) {
                                w.begin(0);
                                w.write("("); // put "(Type) expr" in parentheses.
                                w.write("(");
                                ex.expand(tr);
                                w.write(")");
                                
                                if (t instanceof X10ClassType) {
                                    X10ClassType ct = (X10ClassType) t;
                                    if (ct.hasParams()) {
                                        boolean castToRawType = false;
                                        for (Variance variance : ct.x10Def().variances()) {
                                            if (variance != Variance.INVARIANT) {
                                                castToRawType = true;
                                                break;
                                            }
                                        }
                                        if (castToRawType) {
                                            // cast to raw type
                                            // e.g. for covariant class C[+T]{} and C[Object] v = new C[String](),
                                            // it generates class C<T>{} and C<Object> v = (C<Object>) (C) (new C<String>()).
                                            w.write("(");
                                            (new TypeExpander(er, t, 0)).expand(tr);
                                            w.write(")");
                                        }
                                    }
                                }
                                
                                w.allowBreak(2, " ");
                                X10TypeSystem xts = ((X10TypeSystem)tr.typeSystem());
                                boolean needParen = expr instanceof Unary || expr instanceof Lit || expr instanceof Conditional_c || (expr instanceof X10Call && !(X10TypeMixin.baseType(expr.type()) instanceof ParameterType) && xts.isRail(((X10Call) expr).target().type()));
                                if (needParen)
                                    w.write("(");
                                c.printSubExpr(expr, w, tr);
                                if (needParen)
                                    w.write(")");
                                w.write(")");
                                w.end();
                            }
                            // e.g. i as T (T is parameterType)
                            else if (t instanceof ParameterType) {
                                // SYNOPSIS: (#0) #1 #0=param type #1=primitive or object #2=runtime type
                                String regex =
                                    "(new " + JAVA_LANG_OBJECT + "() {" +
                                        "final #0 cast(final Object self) {" +
                                            "x10.rtt.Type rtt = #2;" +
                                            "#0 dep = (#0) x10.rtt.Types.conversion(rtt,self);" +
                                            "if (self==null) return null;" +
                                            "if (rtt != null && ! rtt.instanceof$(dep)) throw new x10.lang.ClassCastException();" +
                                            "return dep;" +
                                        "}" +
                                    "}.cast(#1))";
                                er.dumpRegex("cast_deptype_primitive_param", new Object[] { ex, expr, rt }, tr, regex);
                            }
                            else {
                                assert !(t.isBoolean() || t.isNumeric() || t.isChar());
                                // SYNOPSIS: (#0) #1 #0=type #1=object #2=runtime type
                                String regex =
                                    "(new " + JAVA_LANG_OBJECT + "() {" +
                                        "final #0 cast(final #0 self) {" +
                                            "if (self==null) return null;" +
                                            "x10.rtt.Type rtt = #2;" +
                                            "if (rtt != null && ! rtt.instanceof$(self)) throw new x10.lang.ClassCastException();" +
                                            "return self;" +
                                        "}" +
                                    "}.cast((#0) #1))";
                                er.dumpRegex("cast_deptype", new Object[] { ex, expr, rt }, tr, regex);
                            }
                    }
                    else {
                            throw new InternalCompilerError("Ambiguous TypeNode survived type-checking.", tn.position());
                    }
                    break;
            case UNBOXING:
                    throw new InternalCompilerError("Unboxing conversions not yet supported.", tn.position());
            case UNKNOWN_IMPLICIT_CONVERSION:
                    throw new InternalCompilerError("Unknown implicit conversion type after type-checking.", c.position());
            case UNKNOWN_CONVERSION:
                    throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
            case BOXING:
                    throw new InternalCompilerError("Boxing conversion should have been rewritten.", c.position());
            }
    }

	public void visit(X10Instanceof_c c) {
		TypeNode tn = c.compareType();

        // Note: constraint checking should be desugared when compiling without NO_CHECKS flag

		Type t = tn.type();
		
		// Fix for XTENLANG-1099
                X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
                if (xts.typeEquals(xts.Object(), t, tr.context())) {
		    /*
		     * Because @NativeRep of x10.lang.Object is java.lang.Object,
		     * we cannot compile "instanceof x10.lang.Object" as "instanceof @NativeRep".
		     */

                    w.write("(!");
                    w.write("(");

                    w.write("(null == ");
                    w.write("(");
                    tr.print(c, c.expr(), w);
                    w.write(")");
                    w.write(")");
                    
                    w.write(" || ");
                    w.write("x10.rtt.Types.isStruct(");
                    tr.print(c, c.expr(), w);
                    w.write(")");
                    /*
                    w.write(" || ");
                    w.write("x10.rtt.Types.runtimeType(x10.core.Struct.class)"); // new RuntimeTypeExpander(er, xts.Struct()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Byte()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Short()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");
                    
                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Int()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Long()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Float()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Double()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Char()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.Boolean()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");
                    */

                    // Java representation of unsigned types are same as signed ones.
                    /*
                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.UByte()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.UShort()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");
                    
                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.UInt()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");

                    w.write(" || ");
                    new RuntimeTypeExpander(er, xts.ULong()).expand(tr);
                    w.write(".");
                    w.write("instanceof$(");
                    tr.print(c, c.expr(), w);
                    w.write(")");
                    */
                    
                    w.write(")");
                    w.write(")");
		    return;
		}
                
                // XTENLANG-1102
                if (t instanceof X10ClassType) {
                    X10ClassType ct = (X10ClassType) t;
                    X10ClassDef cd = ct.x10Def();
                    String pat = er.getJavaRTTRep(cd);

                    if (t instanceof FunctionType) {
                        FunctionType ft = (FunctionType) t;
                        List<Type> args = ft.argumentTypes();
                        Type ret = ft.returnType();
                        if (ret.isVoid()) {
                            w.write("x10.core.fun.VoidFun");
                        } else {
                            w.write("x10.core.fun.Fun");
                        }
                        w.write("_" + ft.typeParameters().size());
                        w.write("_" + args.size());
                        w.write("._RTT");
                    }
                    else if (pat == null && er.getJavaRep(cd) == null && ct.isGloballyAccessible() && ct.typeArguments().size() != 0) {
                        w.write(cd.fullName().toString() + "." + "_RTT");
                    }
                    else {
                        new RuntimeTypeExpander(er, t).expand(tr);
                    }
                } else {
                    new RuntimeTypeExpander(er, t).expand(tr);
                }

                w.write(".");
		w.write("instanceof$(");
		tr.print(c, c.expr(), w);
		
		if (t instanceof X10ClassType) {
		    X10ClassType ct = (X10ClassType) t;
		    X10ClassDef cd = ct.x10Def();
		    String pat = er.getJavaRTTRep(cd);

		    if (pat == null && er.getJavaRep(cd) == null) {
		        for (int i = 0; i < ct.typeArguments().size(); i++) {
		            w.write(", ");
		            new RuntimeTypeExpander(er, ct.typeArguments().get(i)).expand(tr);
		        }
		    }
		}
		w.write(")");
	}


	public void visit(Special_c n) {
		X10Context c = (X10Context) tr.context();
		/*
		 * The InnerClassRemover will have replaced the
		 */
		if (c.inAnonObjectScope() && n.kind() == Special.THIS
				&& c.inStaticContext()) {
			w.write(InnerClassRemover.OUTER_FIELD_NAME.toString());
			return;
		}
		if ((((X10Translator) tr).inInnerClass() || c.inAnonObjectScope() )
				&& n.qualifier() == null && n.kind() != X10Special.SELF && n.kind() != Special.SUPER) {
			er.printType(n.type(), 0);
			w.write(".");
		}
		else if (n.qualifier() != null && n.kind() != X10Special.SELF && n.kind() != Special.SUPER) {
			er.printType(n.qualifier().type(), 0);
			w.write(".");
		}

		w.write(n.kind().toString());
	}



	public void visit(X10ConstructorCall_c c) {
		X10ConstructorCall_c n = c;

		if (n.qualifier() != null) {
			tr.print(c, n.qualifier(), w);
			w.write(".");
		}

		w.write(c.kind() == ConstructorCall.THIS ? "this" : "super");

		w.write("(");
		w.begin(0);

		X10ConstructorInstance mi = (X10ConstructorInstance) n.constructorInstance();
		X10ClassType ct = (X10ClassType) mi.container();

		for (Iterator<Type> i = ct.typeArguments().iterator(); i.hasNext(); ) {
			final Type at = i.next();
			new RuntimeTypeExpander(er, at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		List<Expr> l = c.arguments();
		for (Iterator<Expr> i = l.iterator(); i.hasNext(); ) {
			Expr e = (Expr) i.next();
			c.print(e, w, tr);
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		w.end();
		w.write(");");	
	}
	public void visit(X10New_c c) {
		X10New_c n = c;
		
		X10ConstructorInstance mi = (X10ConstructorInstance) n.constructorInstance();
		
		String pat = er.getJavaImplForDef(mi.x10Def());
		if (pat != null) {
		    List<Type> typeArguments  = Collections.<Type>emptyList();
		    if (mi.container().isClass() && !mi.flags().isStatic()) {
		        X10ClassType ct = (X10ClassType) mi.container().toClass();
		        typeArguments = ct.typeArguments();
		    }
		    List<CastExpander> args = new ArrayList<CastExpander>();
		    List<Expr> arguments = n.arguments();
		    for (int i = 0; i < arguments.size(); ++ i) {
		        Type ft = n.constructorInstance().def().formalTypes().get(i).get();
		        Type at = arguments.get(i).type();
		        if ((at.isBoolean() || at.isNumeric() || at.isChar()) && X10TypeMixin.baseType(ft) instanceof ParameterType) {
		            args.add(new CastExpander(w, er, arguments.get(i)).castTo(at, BOX_PRIMITIVES));
		        }
		        else if ((at.isBoolean() || at.isNumeric() || at.isChar())) {
		            args.add(new CastExpander(w, er, arguments.get(i)).castTo(at, 0));
		        }
		        else {
		            args.add(new CastExpander(w, er, arguments.get(i)));                                    
		        }
		    }
		    er.emitNativeAnnotation(pat, null, Collections.<Type>emptyList(), args, typeArguments);
		    return;
		}
		
		if (n.qualifier() != null) {

			tr.print(c, n.qualifier(), w);
			w.write(".");
		}

		w.write("new ");

		if (n.qualifier() == null) {
		        er.printType(n.objectType().type(), PRINT_TYPE_PARAMS | NO_VARIANCE);
		}
		else {
			er.printType(n.objectType().type(), PRINT_TYPE_PARAMS | NO_VARIANCE | NO_QUALIFIER);
		}

		w.write("(");
		w.begin(0);

		X10ClassType ct = (X10ClassType) mi.container();
		
		List<Type> ta = ct.typeArguments();
                if (ta.size() > 0 && !isJavaNative(n)) {
                    for (Iterator<Type> i = ta.iterator(); i.hasNext(); ) {
                        final Type at = i.next();
                        new RuntimeTypeExpander(er, at).expand(tr);
                        if (i.hasNext() || c.arguments().size() > 0) {
                                w.write(",");
                                w.allowBreak(0, " ");
                        }
                    }        
                }     

		List<Expr> l = c.arguments();
		for (Iterator<Expr> i = l.iterator(); i.hasNext(); ) {
			Expr e = (Expr) i.next();
			c.print(e, w, tr);
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");

		if (c.body() != null) {
			w.write("{");
			tr.print(c, c.body(), w);
			w.write("}");
		}
	}

        private boolean isJavaNative(X10New_c n) {
                Type type = n.objectType().type();
                if (type instanceof X10ClassType) {
                    X10ClassDef cd = ((X10ClassType) type).x10Def();
                    String pat = er.getJavaRep(cd);
                    if (pat != null && pat.startsWith("java.")) {
                        return true;
                    }
                }
                return false;
        }

	public void visit(Import_c c) {
		// don't generate any code at all--we should fully qualify all type names
	}

	public void visit(ClosureCall_c c) {
		Expr target = c.target();
		Type t = target.type();
		boolean base = false;

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

		X10MethodInstance mi = c.closureInstance();

		Expr expr = target;
		if (target instanceof ParExpr) {
		    expr = ((ParExpr) target).expr();
		}
		boolean newClosure = expr instanceof Closure_c;

		if (isSelfDispatch && !newClosure && !c.type().isVoid()) {
		    w.write("(");
		    w.write("(");
		    er.printType(c.type(), BOX_PRIMITIVES);
		    w.write(")");
		}

		c.printSubExpr(target, w, tr);
		w.write(".");
		w.write("apply");
        if (isSelfDispatch && (!newClosure && !mi.returnType().isVoid() && mi.formalTypes().size() == 0)) {
            w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
		}
        else if (
                !isSelfDispatch
                && (!(expr instanceof Closure_c) && !mi.returnType().isVoid())
                || (expr instanceof Closure_c && X10TypeMixin.baseType(mi.returnType()) instanceof ParameterType)) {
                w.write(RETURN_PARAMETER_TYPE_SUFFIX);
        }

		w.write("(");
		w.begin(0);
		
		for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
			final Type at = i.next();
			new RuntimeTypeExpander(er, at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		List<Expr> l = c.arguments();
		for (int i = 0; i < l.size(); i++) {
			Expr e = l.get(i);
			c.print(e, w, tr);
			if (isSelfDispatch && !newClosure) {
			    w.write(",");
			    new RuntimeTypeExpander(er, mi.formalTypes().get(i)).expand();
			}
			if (i != l.size() - 1) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");
		
		if (isSelfDispatch && !newClosure && !c.type().isVoid()) {
		    w.write(")");
		}
	}
	
	public void visit(Try_c c) {
		if (isFinish(c)) {
		    List<Catch> ncatches = new ArrayList<Catch>(c.catchBlocks().size());
		    for (Catch catch1 : c.catchBlocks()) {
		        Block body = catch1.body();
		        X10NodeFactory xnf = (X10NodeFactory) tr.nodeFactory();
		        Position pos = Position.COMPILER_GENERATED;
		        X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		        Type re = xts.RuntimeException();
		        New new1 = xnf.New(Position.COMPILER_GENERATED, xnf.CanonicalTypeNode(Position.COMPILER_GENERATED, re), Collections.<Expr>emptyList());
		        X10ConstructorInstance ci;
		        try {
		            ci = xts.findConstructor(re, xts.ConstructorMatcher(re, Collections.<Type>emptyList(), tr.context()));
		        } catch (SemanticException e) {
		            e.printStackTrace();
		            throw new InternalCompilerError("");
		        }
		        new1 = new1.constructorInstance(ci);
		        body = body.append(xnf.Throw(pos, new1));
                        ncatches.add(catch1.body(body));
		    }
		    c = (Try_c) c.catchBlocks(ncatches);
		}
		TryCatchExpander expander = new TryCatchExpander(w, er, c.tryBlock(), c.finallyBlock());
		final List<Catch> catchBlocks = c.catchBlocks();

		boolean isJavaCheckedExceptionCaught = false;
        for (int i = 0; i < catchBlocks.size(); ++i) {
            Type type = catchBlocks.get(i).catchType();
            if (type.toString().startsWith("java") && !type.isUncheckedException()) {
                // found Java checked exceptions caught here!!
                isJavaCheckedExceptionCaught = true;
            }
        }
		if (isJavaCheckedExceptionCaught) {
		    final String temp = "__$generated_wrappedex$__";
		    expander.addCatchBlock("x10.runtime.impl.java.X10WrappedThrowable", temp, new Expander(er) {
		        public void expand(Translator tr) {
                    w.newline();

                    for (int i = 0; i < catchBlocks.size(); ++i) {
		                Catch cb = catchBlocks.get(i);
		                Type type = cb.catchType();
		                if (!type.toString().startsWith("java") || type.isUncheckedException())
//		                if (type.isSubtype(tr.typeSystem().Error(), tr.context()) || 
//		                    type.isSubtype(tr.typeSystem().RuntimeException(), tr.context()))
		                    // nothing to do, since X10WrappedThrowable wrap only Java checked exceptions
		                    continue;

		                if (i > 0) {
		                    w.write("else ");
		                }
		                w.write("if (" + temp + ".getCause() instanceof ");
		                new TypeExpander(er, cb.catchType(), false, false, false).expand(tr);
		                w.write(") {");
		                w.newline();
	                        
		                cb.formal().translate(w, tr);
		                w.write(" = (");
		                new TypeExpander(er, cb.catchType(), false, false, false).expand(tr);
		                w.write(") " + temp + ".getCause();");
		                w.newline();
		                
		                cb.body().translate(w, tr);
		                w.newline();
		                
		                w.write("}");
                        w.newline();
		            }
                    w.write("else {");
                    w.newline();
		            w.write("throw " + temp + ";");
		            w.newline();
		            w.write("}");
                    w.newline();
		        }
		    });
        }
		    
		for (int i = 0; i < catchBlocks.size(); ++i) {
		    expander.addCatchBlock(catchBlocks.get(i));
		}

		expander.expand(tr);
	}

	private boolean isFinish(Try c) {
	    Block block = c.finallyBlock();
	    if (block == null) return false;
	    List<Stmt> statements = block.statements();
	    if (statements.size() > 0) {
	        Stmt stmt = statements.get(0);
	        if (stmt instanceof Eval) {
	            Expr expr = ((Eval) stmt).expr();
	            if (expr instanceof Call) {
	                Call call = (Call) expr;
	                Receiver target = call.target();
	                if (target instanceof X10CanonicalTypeNode) {
	                    if (target.type().typeEquals(((X10TypeSystem) tr.typeSystem()).Runtime(), tr.context())) {
	                        if (call.methodInstance().name().toString().equals("stopFinish")) {
	                            return true;
	                        }
	                    }
	                }
	            }
                    if (expr instanceof LocalAssign) {
                        Expr right = ((LocalAssign) expr).right();
                        if (right instanceof Call) {
                            Call call = (Call) right;
                            if (call.target().type().toString().startsWith("x10.lang.Runtime.CollectingFinish") && call.methodInstance().name().toString().equals("stopFinishExpr")) {
                                return true;
                            }
                        }
                    }
	        }
	    }
	    return false;
	}
	
	public void visit(Tuple_c c) {
	    Type t = X10TypeMixin.getParameterType(c.type(), 0);

	    w.write("x10.core.RailFactory.<");
            new TypeExpander(er, t, true, true, false).expand();
            w.write(">");
            w.write("makeArrayFromJavaArray(");
            new RuntimeTypeExpander(er, t).expand();
            w.write(", ");
            if (X10TypeMixin.baseType(t) instanceof ParameterType) {
                new RuntimeTypeExpander(er, t).expand();
                w.write(".makeArray(");
                new Join(er, ", ", c.arguments()).expand();
                w.write(")");
            } else {
                w.write("new ");
                new TypeExpander(er, t, false, false, false).expand();
                w.write("[] {");
                new Join(er, ", ", c.arguments()).expand();
                w.write("}");
            }
            w.write(")");
            
	}

	public void visit(X10Call_c c) {
	    Type type = X10TypeMixin.baseType(c.type());
	    X10TypeSystem xts = (X10TypeSystem) type.typeSystem();
	    
	    boolean isParameterType = false;
	    Type ptype = type;
	    Type ttype = X10TypeMixin.baseType(c.target().type());
	    if (ttype instanceof X10ClassType) {
	        if (((X10ClassType) ttype).typeArguments().size() > 0) {
	            ptype = ((X10ClassType) ttype).typeArguments().get(0);
	            if (ptype instanceof ParameterType) {
	                isParameterType = true;
	            }
	        }
	    }
	    
            if (!isParameterType && (xts.isRail(c.target().type()) || xts.isValRail(c.target().type()) || isIMC(ttype))) {
	        Name methodName = c.methodInstance().name();
	        // e.g. rail.set(a,i) -> ((Object[]) rail.value)[i] = a or ((int[]/* primitive array */)rail.value)[i] = a
	        if (methodName==SettableAssign.SET) {
	            w.write("(");
	            w.write("(");
	            new TypeExpander(er, ptype, 0).expand();
	            w.write("[]");
	            w.write(")");
	            c.print(c.target(), w, tr);
	            w.write(".value");
	            w.write(")");

	            w.write("[");
	            c.print(c.arguments().get(1), w, tr);
	            w.write("]");

	            w.write(" = ");
	            c.print(c.arguments().get(0), w, tr);
	            return;
	        }
	        // e.g. rail.apply(i) -> ((String)((Object[])rail.value)[i]) or ((int[])rail.value)[i]
	        if (methodName==ClosureCall.APPLY) {
	            if (!(ptype.isBoolean() || ptype.isNumeric() || ptype.isChar())) {
	                    w.write("(");
	                    w.write("(");
	                    new TypeExpander(er, ptype, true, false, false).expand();
	                    w.write(")");
	            }
	            
	            w.write("(");
	            w.write("(");
	            new TypeExpander(er, ptype, 0).expand();
	            w.write("[]");
	            w.write(")");
	            c.print(c.target(), w, tr);
	            w.write(".value");
	            w.write(")");

	            w.write("[");
	            c.print(c.arguments().get(0), w, tr);
	            w.write("]");

	            if (!(ptype.isBoolean() || ptype.isNumeric() || ptype.isChar())) {
	                w.write(")");
	            }
	            return;
	        }
	    }
	    
	    X10Context context = (X10Context) tr.context();
	    
	    if (xts.isRail(c.target().type()) || xts.isValRail(c.target().type())) {
	        String methodName = c.methodInstance().name().toString();
	        if (methodName.equals("make")) {
	            Type rt = X10TypeMixin.baseType(c.type());
	            if (rt instanceof X10ClassType) {
	                Type pt = ((X10ClassType) rt).typeArguments().get(0);
	                if (!(X10TypeMixin.baseType(pt) instanceof ParameterType)) {
	                    // for makeVaxRail(type,length,init);
	                    if (c.arguments().size() == 2 && c.arguments().get(0).type().isNumeric()) {
	                        Expr expr = c.arguments().get(1);
	                        if (expr instanceof Closure_c) {
	                            Closure_c closure = (Closure_c) expr;
	                            final List<Stmt> statements = closure.body().statements();
	                            if (!throwException(statements)) {
	                                
	                                Translator tr2 = ((X10Translator) tr).inInnerClass(true);
	                                tr2 = tr2.context(expr.enterScope(tr2.context()));
	                                
	                                final Expander ex;
	                                ex = new TypeExpander(er, pt, false, false, false);
	                                final Node n = c;
	                                final Id id = closure.formals().get(0).name();
	                                Expander ex1 = new Expander(er) {
                                            @Override
                                            public void expand(Translator tr2) {
                                                for (Stmt stmt : statements) {
                                                    if (stmt instanceof X10Return_c) {
                                                        w.write("array");
                                                        w.write("[");
                                                        w.write(id.toString());
                                                        w.write("] = ");
                                                        er.prettyPrint(((X10Return_c) stmt).expr(), tr2);
                                                        w.write(";");
                                                    }
                                                    else {
                                                        er.prettyPrint(stmt, tr2);
                                                    }
                                                }
                                            }
                                        };

                                        Expander ex2 = new Expander(er) {
                                            @Override
                                            public void expand(Translator tr2) {
                                                ex.expand();
                                                w.write("[] ");
                                                w.write("array = new ");
                                                ex.expand();
                                                w.write("[length];");
                                            }
                                        };
                                        
	                                Object[] components = {
                                            new TypeExpander(er, c.target().type(), false, true, false),
                                            new TypeExpander(er, pt, true, true, false),
                                            new RuntimeTypeExpander(er, pt),
                                            c.arguments().get(0),
                                            ex1,
                                            id,
                                            ex2
	                                };
	                                er.dumpRegex("rail-make", components, tr2, 
	                                        "(new " + JAVA_LANG_OBJECT + "() {" +
	                                    	    "final #0<#1> apply(int length) {" +
	                                    	        "#6" + 
	                                    	        "for (int #5$ = 0; #5$ < length; #5$++) {" +
	                                    		    "final int #5 = #5$;" +
	                                    		    "#4" +
	                                    		"}" +
	                                    		"return new #0<#1>(#2, #3, array);" +
	                                             "}" +
	                                         "}.apply(#3))");
	                                
	                                return;
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }

		Receiver target = c.target();
		Type t = target.type();
		boolean base = false;

		X10MethodInstance mi = (X10MethodInstance) c.methodInstance();

		String pat = er.getJavaImplForDef(mi.x10Def());
		if (pat != null) {
			
			CastExpander targetArg;
			boolean cast = xts.isParameterType(t);
			targetArg = new CastExpander(w, er, target);
			if (cast) {
			    targetArg = targetArg.castTo(mi.container(), BOX_PRIMITIVES);
			}
			List<Type> typeArguments  = Collections.<Type>emptyList();
			if (mi.container().isClass() && !mi.flags().isStatic()) {
			    X10ClassType ct = (X10ClassType) mi.container().toClass();
			    typeArguments = ct.typeArguments();
			}
			
			List<CastExpander> args = new ArrayList<CastExpander>();
			List<Expr> arguments = c.arguments();
			for (int i = 0; i < arguments.size(); ++ i) {
			    Type ft = c.methodInstance().def().formalTypes().get(i).get();
			    Type at = arguments.get(i).type();
			    if ((at.isBoolean() || at.isNumeric() || at.isChar()) && X10TypeMixin.baseType(ft) instanceof ParameterType) {
			        args.add(new CastExpander(w, er, arguments.get(i)).castTo(at, BOX_PRIMITIVES));
			    }
			    else if ((at.isBoolean() || at.isNumeric() || at.isChar())) {
			        args.add(new CastExpander(w, er, arguments.get(i)).castTo(at, 0));
			    }
			    else {
			        args.add(new CastExpander(w, er, arguments.get(i)));                                    
			    }
			}
			er.emitNativeAnnotation(pat, targetArg, mi.typeParameters(), args, typeArguments);
			return;
		}


		// Check for properties accessed using method syntax.  They may have @Native annotations too.
		if (X10Flags.toX10Flags(mi.flags()).isProperty() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
			X10FieldInstance fi = (X10FieldInstance) mi.container().fieldNamed(mi.name());
			if (fi != null) {
				String pat2 = er.getJavaImplForDef(fi.x10Def());
				if (pat2 != null) {
					Object[] components = new Object[] { target };
					er.dumpRegex("Native", components, tr, pat2);
					return;
				}
			}
		}
		
		boolean runAsync = false;
		if (mi.container().isClass() && ((X10ClassType) mi.container().toClass()).fullName().toString().equals("x10.lang.Runtime")) {
			if (mi.signature().startsWith("runAsync")) {
				runAsync = true;
			}
		}
		
		// When the target class is a generics , print a cast operation explicitly.
		if (target instanceof TypeNode) {
			er.printType(t, 0);
		}
		else {
			CastExpander miContainer;
			// add a check that verifies if the target of the call is in place 'here'
			// This is not needed for:

			if (! (target instanceof Special || target instanceof New)) {

			    miContainer = new CastExpander(w, er, target);

				if (xts.isParameterType(t)) {
					miContainer = new CastExpander(w, er, new TypeExpander(er,  mi.container(), false, false, false), miContainer);
				} else if (isSelfDispatch && (mi.typeParameters().size() > 0 || (mi.container() instanceof X10ClassType && ((X10ClassType) mi.container()).typeArguments().size() > 0))) {
                    miContainer = new CastExpander(w, er, new TypeExpander(er,  mi.container(), true, false, false), miContainer);
				}
				miContainer = new CastExpander(w, er, miContainer);
			}
			else {
				miContainer = new CastExpander(w, er, target);
			}
			miContainer.expand();
		}
		
		w.write(".");

		if (mi.typeParameters().size() > 0) {
			w.write("<");
			for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
				final Type at = i.next();
				new TypeExpander(er, at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES).expand(tr);
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
			w.write(">");
		}
		
		boolean fromInterface = false;
		StructType st = mi.def().container().get();
		Type bst = X10TypeMixin.baseType(st);
		if (bst instanceof X10ClassType) {
		    if (xts.isInterfaceType(bst) || (xts.isFunctionType(bst) && ((X10ClassType) bst).isAnonymous())) {
		        fromInterface = true;
		    }
		}
		
		if (isGenericOverloading && mi.formalTypes().size() == 1 && isMainMethod(xts, mi.flags(), c.name(), mi.returnType(), mi.formalTypes().get(0)))
		{
		    w.write(Emitter.mangleToJava(c.name().id()));
		}
		else if (isGenericOverloading && !fromInterface) {
            w.write(Emitter.mangleMethodName(mi.def(), true));
		}
		else if (isGenericOverloading) {
            w.write(Emitter.mangleMethodName(mi.def(), false));
		}
		else {
		    w.write(Emitter.mangleToJava(c.name().id()));
		}

		boolean isInstantiateReturn = false;
		List<MethodInstance> list = mi.implemented(tr.context());
		for (MethodInstance mj : list) {
		    if (
		        mj.container().typeEquals(mi.container(), context) &&
		        X10TypeMixin.baseType(mj.def().returnType().get()) instanceof ParameterType) {
		        isInstantiateReturn = true;
		        break;
		    }
		}
		
		Type returnType = X10TypeMixin.baseType(mi.def().returnType().get());
		if (returnType instanceof ParameterType || isInstantiateReturn) {
		    if (isSelfDispatch) {
		        Type tt = X10TypeMixin.baseType(target.type());
		        
		        if (tt instanceof X10ClassType && ((X10ClassType) tt).flags().isInterface()) {
		            if (!containsTypeParam(mi.def().formalTypes())) {
		                w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
		            }
		        }
		        else if (target instanceof ParExpr && ((ParExpr) target).expr() instanceof Closure_c) {
		            if (mi.formalTypes().size() == 0) {
		                w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
		            }
		        }
		        else {
		            w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
		        }
		    } else {
                w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
		    }
		}
		w.write("(");
		w.begin(0);

		for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
			final Type at = i.next();
			new RuntimeTypeExpander(er, at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		List<Expr> exprs = c.arguments();
		MethodDef def = c.methodInstance().def();
		for (int i = 0; i < exprs.size(); ++i) {
			Expr e = exprs.get(i);
			Type defType = def.formalTypes().get(i).get();
			if (runAsync && e instanceof Closure_c) {
				c.print(((Closure_c)e).methodContainer(mi), w, tr);
			}
//			else if (!er.isNoArgumentType(e)) {
//				new CastExpander(w, er, e).castTo(e.type(), BOX_PRIMITIVES).expand();
//			}
			else {
			        if (e.type().isBoolean() || e.type().isNumeric() || e.type().isChar()) {
			            // e.g) m((Integer) a) for m(T a)
                        if (X10TypeMixin.baseType(defType) instanceof ParameterType) {
			                w.write("(");
			                er.printType(e.type(), BOX_PRIMITIVES);
			                w.write(")");
			            // e.g) m((int) a) for m(int a)
			            } else {
			                w.write("(");
			                er.printType(e.type(), 0);
			                w.write(")");
			                if (e instanceof X10Call) {
                                            Type targetType = ((X10Call) e).target().type();
                                            if (
                                                !(
                                                    ((X10TypeSystem) tr.typeSystem()).isRail(targetType)
                                                    || ((X10TypeSystem) tr.typeSystem()).isValRail(targetType)
                                                    && !(X10TypeMixin.baseType(e.type()) instanceof ParameterType)
                                                )
                                                && X10TypeMixin.baseType(((X10Call) e).methodInstance().def().returnType().get()) instanceof ParameterType
                                            ) {
			                        w.write("(");
			                        er.printType(e.type(), BOX_PRIMITIVES);
			                        w.write(")");
			                    }
			                }
			                else if (e instanceof ClosureCall) {
			                    ClosureCall cl = (ClosureCall) e;
			                    Expr expr = cl.target();
			                    if (expr instanceof ParExpr) {
			                        expr = (ParExpr) expr;
			                    }
			                    if (!(expr instanceof Closure_c) && X10TypeMixin.baseType(cl.closureInstance().def().returnType().get()) instanceof ParameterType) {
                                                w.write("(");
                                                er.printType(e.type(), BOX_PRIMITIVES);
                                                w.write(")");
			                    }
			                }
			            }
			            w.write("(");
			            c.print(e, w, tr);
                                    w.write(")");
			        }
			        // XTENLANG-1704
			        else {
			            w.write("(");
			            w.write("(");
			            er.printType(mi.formalTypes().get(i), 0);
			            w.write(")");
			            w.write("(");
			            c.print(e, w, tr);
			            w.write(")");
			            w.write(")");
			        }
			}
			
			// if I is an interface and val i:I , t = type of the type of the formal of method instance
			// i.m(a) => i.m(a,t)
			if (isSelfDispatch && X10TypeMixin.baseType(t) instanceof X10ClassType ) {
			    X10ClassType ct = (X10ClassType) X10TypeMixin.baseType(t);
			    if ((ct.flags().isInterface() || (xts.isFunctionType(ct) && ct.isAnonymous())) && Emitter.containsTypeParam(defType)){
			        w.write(",");
			        new RuntimeTypeExpander(er, c.methodInstance().formalTypes().get(i)).expand();
			    }
			}
			if (i != exprs.size() - 1) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");
	}
	
    public static boolean containsTypeParam(List<Ref<? extends Type>> list) {
        for (Ref<? extends Type> ref : list) {
            if (Emitter.containsTypeParam(ref.get())) {
                return true;
            }
        }
        return false;
    }
    
	private boolean isIMC(Type type) {
	    X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
	    Type tbase = X10TypeMixin.baseType(type);
	    return tbase instanceof X10ParsedClassType_c && ((X10ParsedClassType_c) tbase).def().asType().typeEquals(imcType, tr.context());
	}

	public void visit(final Closure_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		tr2 = tr2.context(n.enterScope(tr2.context()));

		List<Expander> typeArgs = new ArrayList<Expander>();
		for (final Formal f : n.formals()) {
			TypeExpander ft = new TypeExpander(er, f.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
			typeArgs.add(ft); // must box formals
		}

		boolean runAsync = false;
		MethodInstance_c mi = (MethodInstance_c) n.methodContainer();
		if (mi != null 
				&& mi.container().isClass() 
				&& ((X10ClassType) mi.container().toClass()).fullName().toString().equals("x10.lang.Runtime") 
				&& mi.signature().startsWith("runAsync")) {
			runAsync = true;
		}
		
		TypeExpander ret = new TypeExpander(er, n.returnType().type(), true, true, false);
		if (!n.returnType().type().isVoid()) {
			typeArgs.add(ret);
			w.write("new x10.core.fun.Fun_0_" + n.formals().size());
		}
		else {		
			w.write("new x10.core.fun.VoidFun_0_" + n.formals().size());
		}

		if (typeArgs.size() > 0) {
			w.write("<");
			new Join(er, ", ", typeArgs).expand(tr2);
			w.write(">");
		}

		w.write("() {");
		
		List<Formal> formals = n.formals();
		// bridge
		boolean bridge = containsPrimitive(n) || !n.returnType().type().isVoid() && !(X10TypeMixin.baseType(n.returnType().type()) instanceof ParameterType);
        if (bridge) {
		    w.write("public final ");
		    if (isSelfDispatch && n.returnType().type().isVoid() && n.formals().size() != 0) {
		        w.write(JAVA_LANG_OBJECT);
		    }
		    else {
		        ret.expand(tr2);
		    }
		    w.write(" apply");
		    if (!n.returnType().type().isVoid() && (!isSelfDispatch || (isSelfDispatch && n.formals().size() == 0))) {
		        w.write(RETURN_PARAMETER_TYPE_SUFFIX);
		    }
		    w.write("(");
		    for (int i = 0; i < formals.size(); ++i) {
		        if (i != 0) w.write(",");
		        er.printFormal(tr2, n, formals.get(i), true);
		        
		        if (isSelfDispatch) {
		            w.write(", ");
		            w.write(X10_RUNTIME_TYPE_CLASS);
		            w.write(" ");
		            w.write("t" + (i + 1));
		        }
		    }
		    w.write(") { ");
		    if (!n.returnType().type().isVoid()) {
		        w.write("return ");
		    }
		    w.write("apply");
		    if (X10TypeMixin.baseType(n.returnType().type()) instanceof ParameterType && (!isSelfDispatch || (isSelfDispatch && n.formals().size() == 0))) {
		        w.write(RETURN_PARAMETER_TYPE_SUFFIX);
		    }
		    w.write("(");
		    String delim = "";
		    for (Formal f: formals) {
		        w.write(delim);
		        delim = ",";
		        if (f.type().type().isBoolean() || f.type().type().isNumeric() || f.type().type().isChar()) {
		            w.write("(");
		            er.printType(f.type().type(), 0);
		            w.write(")");
		        }
		        w.write(f.name().toString());
		    }
		    w.write(");");
		    if (isSelfDispatch && n.returnType().type().isVoid() && n.formals().size() != 0) {
                w.write("return null;");
            }
		    w.write("}");
		    w.newline();
		}
		
		w.write("public final ");
		if (isSelfDispatch && !bridge && n.returnType().type().isVoid() && n.formals().size() != 0) {
		    w.write(JAVA_LANG_OBJECT);
		}
		else {
		    new TypeExpander(er, n.returnType().type(), true, false, false).expand(tr2);
		}
		w.write(" apply");
		if (X10TypeMixin.baseType(n.returnType().type()) instanceof ParameterType && (!isSelfDispatch || (isSelfDispatch && n.formals().size() == 0))) {
		    w.write(RETURN_PARAMETER_TYPE_SUFFIX);
		}
		w.write("(");
		for (int i = 0; i < formals.size(); i++) {
		    if (i != 0) w.write(", ");
		    er.printFormal(tr2, n, formals.get(i), false);
		    if (isSelfDispatch && !bridge) {
		        w.write(", ");
		        w.write(X10_RUNTIME_TYPE_CLASS);
		        w.write(" ");
		        w.write("t" + (i + 1));
		    }
		}
		
		w.write(") { ");
		
                List<Stmt> statements = n.body().statements();
                boolean throwException = false;
                boolean throwThrowable = false;
                for (Stmt stmt : statements) {
                    final List<Type> throwables = getThrowables(stmt);
                    if (throwables == null) {
                        continue;
                    }
                    for (Type type : throwables) {
                        if (type != null) {
                            if (type.isSubtype(tr.typeSystem().Exception(), tr.context()) && !type.isSubtype(tr.typeSystem().RuntimeException(), tr.context())) {
                                throwException = true;
                            } else if (!type.isSubtype(tr.typeSystem().Exception(), tr.context()) && !type.isSubtype(tr.typeSystem().Error(), tr.context())) {
                                throwThrowable = true;
                            }
                        }
                    }
                }
                
                TryCatchExpander tryCatchExpander = new TryCatchExpander(w, er, n.body(), null);
                if (runAsync) {
                    tryCatchExpander.addCatchBlock("x10.runtime.impl.java.X10WrappedThrowable", "ex", new Expander(er) {
                        public void expand(Translator tr) {
                            w.write("x10.lang.Runtime.pushException(ex);");
                        }
                    });
                }
                if (throwThrowable) {
                    tryCatchExpander.addCatchBlock("java.lang.RuntimeException", "ex", new Expander(er) {
                        public void expand(Translator tr) {
                            w.write("throw ex;");
                        }
                    });
                    tryCatchExpander.addCatchBlock("java.lang.Error", "er", new Expander(er) {
                        public void expand(Translator tr) {
                            w.write("throw er;");
                        }
                    });                    
                    if (runAsync) {
                        tryCatchExpander.addCatchBlock("java.lang.Throwable", "t", new Expander(er) {
                            public void expand(Translator tr) {
                                w.write("x10.lang.Runtime.pushException(new x10.runtime.impl.java.X10WrappedThrowable(t));");
                            }
                        });
                    } else {
                        tryCatchExpander.addCatchBlock("java.lang.Throwable", "t", new Expander(er) {
                            public void expand(Translator tr) {
                                w.write("throw new x10.runtime.impl.java.X10WrappedThrowable(t);");
                            }
                        });
                    }
                    tryCatchExpander.expand(tr2); 
                }
                else if (throwException) {
                    tryCatchExpander.addCatchBlock("java.lang.RuntimeException", "ex", new Expander(er) {
                        public void expand(Translator tr) {
                            w.write("throw ex;");
                        }
                    });
                    
                    if (runAsync) {
                        tryCatchExpander.addCatchBlock("java.lang.Exception", "ex", new Expander(er) {
                            public void expand(Translator tr) {
                                w.write("x10.lang.Runtime.pushException(new x10.runtime.impl.java.X10WrappedThrowable(ex));");
                            }
                        });
                    } else {
                        tryCatchExpander.addCatchBlock("java.lang.Exception", "ex", new Expander(er) {
                            public void expand(Translator tr) {
                                w.write("throw new x10.runtime.impl.java.X10WrappedThrowable(ex);");
                            }
                        });
                    }
                    tryCatchExpander.expand(tr2);
                } else if (runAsync) {
                    tryCatchExpander.expand(tr2);
                } else {
                    er.prettyPrint(n.body(), tr2);
                }
                
                if (isSelfDispatch && !bridge && n.returnType().type().isVoid() && n.formals().size() != 0) {
                    w.write("return null;");
                }
                
                w.write("}");
                w.newline();

                Type type = n.type();
                type = X10TypeMixin.baseType(type);
                if (type instanceof X10ClassType) {
                        X10ClassType ct = (X10ClassType) type;
                        X10ClassDef def = ct.x10Def();
                        
                        // XTENLANG-1102
                        Set<ClassDef> visited = new HashSet<ClassDef>();
                        
                        visited = new HashSet<ClassDef>();
                        visited.add(def);
                        if (!def.flags().isInterface()) {
                                List<Type> types = new ArrayList<Type>();
                                LinkedList<Type> worklist = new LinkedList<Type>();
                                for (Type t : def.asType().interfaces()) {
                                        Type it = X10TypeMixin.baseType(t);
                                        worklist.add(it);
                                }
                                while (!worklist.isEmpty()) {
                                        Type it = worklist.removeFirst();
                                        if (it instanceof X10ClassType) {
                                                X10ClassType ct2 = (X10ClassType) it;
                                                X10ClassDef idef = ct2.x10Def();

                                                if (visited.contains(idef))
                                                        continue;
                                                visited.add(idef);

                                                for (Type t : ct2.interfaces()) {
                                                        Type it2 = X10TypeMixin.baseType(t);
                                                        worklist.add(it2);
                                                }
                                                
                                                types.addAll(ct2.typeArguments());
                                        }
                                }
                                // To extend Any, the type requires getRTT even if it has no type params (e.g. VoidFun_0_0).  
//                                if (types.size() > 0) {
                                    w.write("public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}");
                                    w.write("public x10.rtt.Type<?> getParam(int i) {");
                                    for (int i = 0; i < types.size(); i++) {
                                        w.write("if (i ==" + i + ")");
                                        Type t = types.get(i);
                                        w.write(" return ");
                                        new RuntimeTypeExpander(er, t).expand();
                                        w.write(";");
                                    }
                                    w.write("return null;");
                                    w.newline();
                                    w.write("}");
                                    w.newline();
//                                }
                        }
                }

                w.write("}");
        }

        private boolean throwException(List<Stmt> statements) {
            for (Stmt stmt : statements) {
                final List<Type> exceptions = getThrowables(stmt);
                if (exceptions == null) {
                    continue;
                }
                for (Type type : exceptions) {
                    if (type != null) {
                        if (type.isSubtype(tr.typeSystem().Exception(), tr.context()) && !type.isSubtype(tr.typeSystem().RuntimeException(), tr.context())) {
                            return true;
                        } else if (!type.isSubtype(tr.typeSystem().Exception(), tr.context()) && !type.isSubtype(tr.typeSystem().Error(), tr.context())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private static List<Type> getThrowables(Stmt stmt) {
            final List<Type> throwables = new ArrayList<Type>();
            stmt.visit(
                new NodeVisitor() {
                    @Override
                    public Node leave(Node old, Node n, NodeVisitor v) {
                     /*   if (n instanceof X10Call_c) {
                            List<Type> throwTypes = ((X10Call_c) n).methodInstance().throwTypes();
                            if (throwTypes != null) throwables.addAll(throwTypes);
                        }
                        if (n instanceof Throw) {
                            throwables.add(((Throw) n).expr().type());
                        }
                        if (n instanceof X10New_c) {
                            List<Type> throwTypes = ((X10New_c) n).procedureInstance().throwTypes();
                            if (throwTypes != null) throwables.addAll(throwTypes);
                        }
                        */
                        return n;
                    }
            });
            return throwables;
        }

	private boolean containsPrimitive(Closure_c n) {
	    Type t = n.returnType().type();
	    if (t.isBoolean() || t.isNumeric() || t.isChar()) {
	        return true;
            }
	    for (Formal f:n.formals()) {
	        Type type = f.type().type();
	        if (type.isBoolean() || type.isNumeric() || type.isChar()) {
	            return true;
	        }
	    }
	    return false;
	}
	    
	public void visit(FieldDecl_c n) {
		if (er.hasAnnotation(n, QName.make("x10.lang.shared"))) {
			w.write ("volatile ");
		}
		
		// Hack to ensure that X10Flags are not printed out .. javac will
		// not know what to do with them.
		Flags flags;
		if (!n.flags().flags().isStatic()) {
		    flags = X10Flags.toX10Flags(n.flags().flags().clearFinal());
		} else {
            flags = X10Flags.toX10Flags(n.flags().flags());
		}

		FieldDecl_c javaNode = (FieldDecl_c) n.flags(n.flags().flags(flags));

		//same with FiledDecl_c#prettyPrint(CodeWriter w, PrettyPrinter tr)
		FieldDef fieldDef = javaNode.fieldDef();
        boolean isInterface = fieldDef != null && fieldDef.container() != null &&
        fieldDef.container().get().toClass().flags().isInterface();

        Flags f = javaNode.flags().flags();

        if (isInterface) {
            f = f.clearPublic();
            f = f.clearStatic();
            f = f.clearFinal();
        }

        w.write(f.translate());
        tr.print(javaNode, javaNode.type(), w);
        w.allowBreak(2, 2, " ", 1);
        tr.print(javaNode, javaNode.name(), w);

        if (javaNode.init() != null) {
            w.write(" =");
            w.allowBreak(2, " ");
            
            //X10 unique
            er.coerce(javaNode, javaNode.init(), javaNode.type().type());
        }

        w.write(";");
	}

	public void visit(X10LocalDecl_c n) {
		if (!X10PrettyPrinterVisitor.reduce_generic_cast) {
			n.prettyPrint(w, tr);
			return;
		}
		
		//same with FieldDecl_c#prettyPrint(CodeWriter w, PrettyPrinter tr)
        boolean printSemi = tr.appendSemicolon(true);
        boolean printType = tr.printType(true);

        tr.print(n, n.flags(), w);
        if (printType) {
        	tr.print(n, n.type(), w);
            w.write(" ");
        }
        tr.print(n, n.name(), w);

        if (n.init() != null) {
            w.write(" =");
            w.allowBreak(2, " ");
            
            //X10 unique
            er.coerce(n, n.init(), n.type().type());
        }
        // assign default value for access vars in at or async
        else if (!n.flags().flags().isFinal()) {
            Type type = X10TypeMixin.baseType(n.type().type());
            X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();

            w.write(" =");
            w.allowBreak(2, " ");

            if (xts.isBoolean(type)) {
                w.write(" false");
            }
            else if (!xts.isUnsigned(type) && (xts.isChar(type) || xts.isNumeric(type))) {
                w.write(" 0");
            }
            else {
                w.write(" null");
            }
        }

        if (printSemi) {
            w.write(";");
        }

        tr.printType(printType);
        tr.appendSemicolon(printSemi);
	}


	public void visit(Formal_c f) {
		if (f.name().id().toString().equals(""))
			f = (Formal_c) f.name(f.name().id(Name.makeFresh("a")));
		f.translate(w, tr);
	}

	public void visit(ForLoop_c f) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

		X10Formal form = (X10Formal) f.formal();

		X10Context context = (X10Context) tr.context();

		/* TODO: case: for (point p:D) -- discuss with vj */
		/* handled cases: exploded syntax like: for (point p[i,j]:D) and for (point [i,j]:D) */
		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars() && (ts.isSubtype(f.domain().type(), ts.Region(), context) || ts.isSubtype(f.domain().type(), ts.Dist(), context)) && X10TypeMixin.isRect(f.domain().type(), context)) {
			String regVar = getId().toString();
			List<Name> idxs = new ArrayList<Name>();
			List<Name> lims = new ArrayList<Name>();
			List<Name> idx_vars = new ArrayList<Name>();
			List<Object> vals = new ArrayList<Object>();
			LocalDef[] lis = form.localInstances();
			int rank = lis.length;

			for (int i = 0; i < rank; i++) {
				idxs.add(getId());
				lims.add(getId());
				idx_vars.add(lis[i].name());
				vals.add(new Integer(i));
			}

			Object body = f.body();

			if (!form.isUnnamed()) {
			    // SYNOPSIS: #0=modifiers #1=type #2=var #3=idxs_list
			    String regex = "#0 #1 #2 = x10.array.Point.make(#3);";
                Template template = Template.createTemplateFromRegex(er, "point-create", regex,
                                                                     form.flags(),
                                                                     form.type(),
                                                                     form.name(),
                                                                     new Join(er, ",", idxs)
                    ); 
			    body = new Join(er, "\n", template, body);
			}

			// SYNOPSIS: #0=type #1=final_var #2=value_var
			String regex1 = "final #0 #1 = #2;";
            Loop loop1 = new Loop(er, "final-var-assign", regex1, new CircularList<String>("int"), idx_vars, idxs);
			body = new Join(er, "\n", loop1, body);

			// SYNOPSIS: #0=generated_index_var #1=region_var #2=index #3=limit_var
			String regex2 = "for (int #0 = #1.min(#2), #3 = #1.max(#2); #0 <= #3; #0++)";
            Loop loop2 = new Loop(er, "forloop-mult-each", regex2, idxs, new CircularList<String>(regVar), vals, lims);
			// SYNOPSIS: #0=region_expr #1=region_var #2=rect_for_header #3=rect_for_body #4=regular_for_iterator
            String regex = "{ x10.array.Region #1 = (#0).region(); if (#1.rect()) { #2 { #3 } } else { #4 }	}";
			er.dumpRegex("forloop-mult", new Object[] {
			        f.domain(),
                    regVar,
                    loop2,
                    body,
                    //                           new Template("forloop",
                    //                                   form.flags(),
                    //                                   form.type(),
                    //                                   form.name(),
                    //                                   regVar,
                    //                                   new Join("\n", new Join("\n", f.locals()), f.body()),
                    //                                   new TypeExpander(form.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES)
                    //                               )
                    new Inline(er, "assert false;")
			}, tr, regex);
		} else {
			// SYNOPSIS: for (#0 #2: #1 in #3) #4    #5=unboxed type
			String regex = "for (x10.lang.Iterator #2__ = (#3).iterator(); #2__.hasNext(); ) { #0 #1 #2 = (#5) #2__.next$G(); #4 }";
			er.dumpRegex("forloop", new Object[] {
                    form.flags(),
                    form.type(),
                    form.name(),
                    f.domain(),
                    new Join(er, "\n", new Join(er, "\n", f.locals()), f.body()),
                    new TypeExpander(er, form.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES)
			        }, tr, regex);
		}
	}

	public void visit(Labeled_c n) {
	    Stmt statement = n.statement();
	    if (statement instanceof Block_c) {
	        w.write("{");
	        Block_c block = (Block_c) statement;
	        for (Stmt s : block.statements()) {
	            if (s instanceof Loop_c) {
	                w.write(n.labelNode() + ": ");
	            }
	            tr.print(n, s, w);
	        }
            w.write("}");
	    } else {
	        w.write(n.labelNode() + ": ");
	        tr.print(n, statement, w);
	    }
	}


	/*
	 * Field access -- this includes FieldAssign (because the left node of
	 * FieldAssign is a Field node!
	 */
	public void visit(Field_c n) {
		Receiver target = n.target();
		Type t = target.type();

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		X10Context context = (X10Context) tr.context();
		X10FieldInstance fi = (X10FieldInstance) n.fieldInstance();

		if (target instanceof TypeNode) {
			TypeNode tn = (TypeNode) target;
			if (t instanceof ParameterType) {
				// Rewrite to the class declaring the field.
				FieldDef fd = fi.def();
				t = Types.get(fd.container());
				target = tn.typeRef(fd.container());
				n = (Field_c) n.target(target);
			}
		}
		
		String pat = er.getJavaImplForDef(fi.x10Def());
		if (pat != null) {
			Object[] components = new Object[] { target };
			er.dumpRegex("Native", components, tr, pat);
			return;
		}

		if (target instanceof TypeNode) {
			er.printType(t, 0);
			w.write(".");
			w.write(Emitter.mangleToJava(n.name().id()));
		}
		else {

			boolean is_location_access = xts.isObjectOrInterfaceType(fi.container(), context) && fi.name().equals(xts.homeName());
			// WARNING: it's important to delegate to the appropriate visit() here!
			n.translate(w, tr);

		}

		// Fix XTENLANG-945 (Java backend only fix)
	        // Change field access to method access
	        if (X10Field_c.isInterfaceProperty(target.type(), fi)) {
	            w.write("()");
	        }
	}

	public void visit(IntLit_c n) {
		String val;
		if (n.kind() == X10IntLit_c.ULONG) {
			val = Long.toString(n.value()) + "L";
		    val = "new x10.lang.ULong("+val+")";
		}
		else if (n.kind() == IntLit_c.LONG) {
			val = Long.toString(n.value()) + "L";
		}
		else if (n.kind() == X10IntLit_c.UINT) {
			if (n.value() >= 0x80000000L)
				val = "0x" + Long.toHexString(n.value() & 0xffffffffL);
			else
				val = Long.toString(n.value() & 0xffffffffL);
		    val = "new x10.lang.UInt("+val+")";
		}
		else if (n.kind() == IntLit_c.INT) {
			if (n.value() >= 0x80000000L)
				val = "0x" + Long.toHexString(n.value());
			else
				val = Long.toString((int) n.value());
		} else
			throw new InternalCompilerError("Unrecognized IntLit kind " + n.kind());
		w.write(val);
	}

	//	private Stmt optionalBreak(Stmt s) {
	//		NodeFactory nf = tr.nodeFactory();
	//		if (s.reachable())
	//			return nf.Break(s.position());
	//		// [IP] Heh, Empty cannot be unreachable either.  Who knew?
	////		return nf.Empty(s.position());
	//		return null;
	//	}

	public void visit(SettableAssign_c n) {
		SettableAssign_c a = n;
		Expr array = a.array();
		List<Expr> index = a.index();

		boolean effects = er.hasEffects(array);
		for (Expr e : index) {
			if (effects)
				break;
			if (er.hasEffects(e))
				effects = true;
		}

		TypeSystem ts = tr.typeSystem();
		X10Context context = (X10Context) tr.context();
		Type t = n.leftType();

//		boolean needsHereCheck = er.needsHereCheck(array, context);
		Template tmp = null; 
//		if (needsHereCheck) {
//            // SYNOPSIS: (#0)((#1)!here) #0=type #1=object -- wrap in Object to help javac
//            String regex = "((#0) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), #1))";
//			tmp = Template.createTemplateFromRegex(er, "place-check", regex, new TypeExpander(er, array.type(), true, false, false), array);
//		}

		boolean nativeop = false;
		if (t.isNumeric() || t.isBoolean() || t.isChar() || t.isSubtype(ts.String(), context)) {
			nativeop = true;
		}

		X10MethodInstance mi = (X10MethodInstance) n.methodInstance();
		boolean superUsesClassParameter = ! mi.flags().isStatic() ; // && overridesMethodThatUsesClassParameter(mi);

		if (n.operator() == Assign.ASSIGN) {
			// Look for the appropriate set method on the array and emit native code if there is an
			// @Native annotation on it.
			List<Expr> args = new ArrayList<Expr>(index.size()+1);
			//args.add(array);
			args.add(n.right());
			for (Expr e : index) args.add(e);
			String pat = er.getJavaImplForDef(mi.x10Def());

			if (pat != null) {
				er.emitNativeAnnotation(pat, null == tmp ? array : tmp, mi.typeParameters(), args, Collections.<Type>emptyList());
				return;
			} else {
				// otherwise emit the hardwired code.
				er.arrayPrint(n, array, w, tmp);
				w.write(".set");
				w.write("(");
				tr.print(n, n.right(), w);
				if (index.size() > 0)
					w.write(", ");
				new Join(er, ", ", index).expand(tr);
				w.write(")");
			}
		}
		else if (! effects) {
			Binary.Operator op = n.operator().binaryOperator();
			Name methodName = X10Binary_c.binaryMethodName(op);
			X10TypeSystem xts = (X10TypeSystem) ts;
			if ((t.isBoolean() || t.isNumeric()) && (xts.isRail(array.type()) || xts.isValRail(array.type()) || isIMC(array.type()))) {
			    w.write("(");
			    w.write("(");
			    new TypeExpander(er, t, 0).expand();
			    w.write("[])");
			    er.arrayPrint(n, array, w, tmp);
			    w.write(".value");
			    w.write(")");
			    w.write("[");
			    new Join(er, ", ", index).expand(tr);
			    w.write("]");
			    w.write(" ");
			    w.write(op.toString());
			    w.write("=");
			    w.write(" ");
			    tr.print(n, n.right(), w);
			    return;
			}
			
			er.arrayPrint(n, array, w, tmp);
			w.write(".set");
			w.write("((");
			tr.print(n, array, w);
			w.write(").apply(");
			new Join(er, ", ", index).expand(tr);
			w.write(")");
			if (nativeop) {
				w.write(" ");
				w.write(op.toString());
				tr.print(n, n.right(), w);
			}
			else {
				w.write(".");
				w.write(Emitter.mangleToJava(methodName));
				w.write("(");
				tr.print(n, n.right(), w);
				w.write(")");
			}
			if (index.size() > 0)
				w.write(", ");
			new Join(er, ", ", index).expand(tr);
			w.write(")");
		}
		else {
			// new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
			Binary.Operator op = n.operator().binaryOperator();
			Name methodName = X10Binary_c.binaryMethodName(op);
			X10TypeSystem xts = (X10TypeSystem) ts;
			if ((t.isBoolean() || t.isNumeric()) && (xts.isRail(array.type()) || xts.isValRail(array.type()) || isIMC(array.type()))) {
			    w.write("(");
			    w.write("(");
			    new TypeExpander(er, t, 0).expand();
			    w.write("[])");
			    er.arrayPrint(n, array, w, tmp);
			    w.write(".value");
			    w.write(")");
			    w.write("[");
			    new Join(er, ", ", index).expand(tr);
			    w.write("]");
			    w.write(" ");
			    w.write(op.toString());
			    w.write("=");
			    w.write(" ");
			    tr.print(n, n.right(), w);
			    return;
			}
			
			w.write("new " + JAVA_LANG_OBJECT + "() {");
			w.allowBreak(0, " ");
			w.write("final ");
			er.printType(n.type(), PRINT_TYPE_PARAMS);
			w.write(" eval(");
			er.printType(array.type(), PRINT_TYPE_PARAMS);
			w.write(" array");
			{
				int i = 0;
				for (Expr e : index) {
					w.write(", ");
					er.printType(e.type(), PRINT_TYPE_PARAMS);
					w.write(" ");
					w.write("i" + i);
					i++;
				}
			}
			w.write(", ");
			er.printType(n.right().type(), PRINT_TYPE_PARAMS);
			w.write(" right) {");
			w.allowBreak(0, " ");
			if (! n.type().isVoid()) {
				w.write("return ");
			}
			w.write("array.set");
			w.write("(");

			w.write(" array.apply(");
			{
				int i = 0;
				for (Expr e : index) {
					if (i != 0)
						w.write(", ");
					w.write("i" + i);
					i++;
				}
			}
			w.write(")");
			if (nativeop) {
				w.write(" ");
				w.write(op.toString());
				w.write(" right");
			}
			else {
				w.write(".");
				w.write(Emitter.mangleToJava(methodName));
				w.write("(right)");
			}
			if (index.size() > 0)
				w.write(", ");
			{
				int i = 0;
				for (Expr e : index) {
					if (i != 0)
						w.write(", ");
					w.write("i" + i);
					i++;
				}
			}
			w.write(");");
			w.allowBreak(0, " ");
			w.write("} }.eval(");
			er.arrayPrint(n, array, w, tmp);
			if (index.size() > 0)
				w.write(", ");
			new Join(er, ", ", index).expand();
			w.write(", ");
			tr.print(n, n.right(), w);
			w.write(")");
		}
	}

	public void visit(CanonicalTypeNode_c n) {
		Type t = n.type();
		if (t != null)
			er.printType(t, PRINT_TYPE_PARAMS);
		else
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)n);
	}

	public void visit(X10Unary_c n) {
		Expr left = n.expr();
		Type l =  left.type();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		Unary.Operator op = n.operator();

		if (op == Unary.POST_DEC || op == Unary.POST_INC || op == Unary.PRE_DEC || op == Unary.PRE_INC) {
			Expr expr = left;
			Type t = left.type();

			Expr target = null;
			List<Expr> args = null;
			List<TypeNode> typeArgs = null;
			X10MethodInstance mi = null;

			// Handle a(i)++ and a.apply(i)++
			if (expr instanceof ClosureCall) {
				ClosureCall e = (ClosureCall) expr;
				target = e.target();
				args = e.arguments();
				typeArgs = Collections.<TypeNode>emptyList(); // e.typeArgs();
				mi = e.closureInstance();
			}
			else if (expr instanceof X10Call) {
				X10Call e = (X10Call) expr;
				if (e.target() instanceof Expr && e.name().id() == ClosureCall.APPLY) {
					target = (Expr) e.target();
					args = e.arguments();
					typeArgs = e.typeArguments();
					mi = (X10MethodInstance) e.methodInstance();
				}
			}

			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			if (mi != null) {
				MethodInstance setter = null;

				List<Type> setArgTypes = new ArrayList<Type>();
				List<Type> setTypeArgs = new ArrayList<Type>();
				for (Expr e : args) {
					setArgTypes.add(e.type());
				}
				setArgTypes.add(expr.type());
				for (TypeNode tn : typeArgs) {
					setTypeArgs.add(tn.type());
				}
				try {
					setter = ts.findMethod(target.type(), ts.MethodMatcher(t, Name.make("set"), setTypeArgs, setArgTypes, tr.context()));
				}
				catch (SemanticException e) {
				}

				// TODO: handle type args
				// TODO: handle setter method

				w.write("new " + JAVA_LANG_OBJECT + "() {");
				w.allowBreak(0, " ");
				w.write("final ");
				er.printType(t, PRINT_TYPE_PARAMS);
				w.write(" eval(");
				er.printType(target.type(), PRINT_TYPE_PARAMS);
				w.write(" target");
				{int i = 0;
				for (Expr e : args) {
					w.write(", ");
					er.printType(e.type(), PRINT_TYPE_PARAMS);
					w.write(" a" + (i+1));
					i++;
				}}
				w.write(") {");
				w.allowBreak(0, " ");
				er.printType(left.type(), PRINT_TYPE_PARAMS);
				w.write(" old = ");
				String pat = er.getJavaImplForDef(mi.x10Def());
				if (pat != null) {
					Object[] components = new Object[args.size()+1];
					int j = 0;
					components[j++] = "target";
					{int i = 0;
					for (Expr e : args) {
						components[j++] = "a" + (i+1);
						i++;
					}}
					er.dumpRegex("Native", components, tr, pat);
				}
				else {
					w.write("target.apply(");
					{int i = 0;
					for (Expr e : args) {
						if (i > 0) w.write(", ");
						w.write("a" + (i+1));
						i++;
					}}
					w.write(")");
				}
				w.write(";");
				w.allowBreak(0, " ");
				er.printType(left.type(), PRINT_TYPE_PARAMS);
				w.write(" neu = (");
				er.printType(left.type(), PRINT_TYPE_PARAMS);
				w.write(") old");
				w.write((op == Unary.POST_INC || op == Unary.PRE_INC ? "+" : "-") + "1");
				w.write(";");
				w.allowBreak(0, " ");
				w.write("target.set(neu");
				{int i = 0;
				for (Expr e : args) {
					w.write(", ");
					w.write("a" + (i+1));
					i++;
				}}
				w.write(");");
				w.allowBreak(0, " ");
				w.write("return ");
				w.write((op == Unary.PRE_DEC || op == Unary.PRE_INC ? "neu" : "old"));
				w.write(";");
				w.allowBreak(0, " ");
				w.write("}");
				w.allowBreak(0, " ");
				w.write("}.eval(");
				tr.print(n, target, w);
				w.write(", ");
				new Join(er, ", ", args).expand(tr);
				w.write(")");

				return;
			}
		}

		if (l.isNumeric() || l.isBoolean()) {
			visit((Unary_c)n);
			return;
		}

		Name methodName = X10Unary_c.unaryMethodName(op);
		if (methodName != null)
			er.generateStaticOrInstanceCall(n.position(), left, methodName);
		else
			throw new InternalCompilerError("No method to implement " + n, n.position());
		return;
	}

	public void visit(SubtypeTest_c n) {
		TypeNode sub = n.subtype();
		TypeNode sup = n.supertype();

		w.write("((");
		new RuntimeTypeExpander(er, sub.type()).expand(tr);
		w.write(")");
		if (n.equals()) {
			w.write(".equals(");
		}
		else {
			w.write(".isSubtype(");
		}
		new RuntimeTypeExpander(er, sup.type()).expand(tr);
		w.write("))");
	}

	// This is an enhanced version of Binary_c#prettyPrint(CodeWriter, PrettyPrinter)
    private void prettyPrint(X10Binary_c n) {
        Expr left = n.left();
        Type l = left.type();
        Expr right = n.right();
        Type r = right.type();
        Binary.Operator op = n.operator();

        boolean asPrimitive = false;
        boolean asUnsignedPrimitive = false;
        if (op == Binary.EQ || op == Binary.NE) {
            if (l.isNumeric() && r.isNumeric() || l.isBoolean() && r.isBoolean() || l.isChar() && r.isChar()) {
                asPrimitive = true;

                if (l.isNumeric() && !(l.isByte() || l.isShort() || l.isInt() || l.isLong() || l.isFloat() || l.isDouble())) {
                    asUnsignedPrimitive = true;
                }
            }
        }

        if (asPrimitive) {
            if (asUnsignedPrimitive && (op == Binary.NE)) w.write("!");
            w.write("((");
            er.printType(l, 0);
            w.write(") ");
        }
        n.printSubExpr(left, true, w, tr);
        if (asPrimitive) w.write(")");
        if (asUnsignedPrimitive) {
            w.write(".equals(");
        } else {
            w.write(" ");
            w.write(op.toString());
            w.allowBreak(n.type() == null || n.type().isPrimitive() ? 2 : 0, " ");
        }
        if (asPrimitive) {
            w.write("((");
            er.printType(r, 0);
            w.write(") ");
        }
        n.printSubExpr(right, false, w, tr);
        if (asPrimitive) w.write(")");
        if (asUnsignedPrimitive) w.write(")");
    }

    public void visit(X10Binary_c n) {
		Expr left = n.left();
		Type l = left.type();
		Expr right = n.right();
		Type r =  right.type();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		Binary.Operator op = n.operator();


        if (l.isNumeric() && r.isNumeric() || l.isBoolean() && r.isBoolean() || l.isChar() && r.isChar()) {
            prettyPrint(n);
            return;
        }

		if (op == Binary.EQ) {
			// SYNOPSIS: #0 == #1 (for values, also works for reference == operations)
			String regex = "x10.rtt.Equality.equalsequals(#0,#1)";
			er.dumpRegex("equalsequals", new Object[] { left, right }, tr, regex);
			return;
		}

		if (op == Binary.NE) {
			// SYNOPSIS: #0 != #1 (for values, also works for reference != operations)
			String regex = "(!x10.rtt.Equality.equalsequals(#0,#1))";
            er.dumpRegex("notequalsequals", new Object[] { left, right }, tr, regex);
			return;
		}

		if (op == Binary.ADD && (l.isSubtype(xts.String(), tr.context()) || r.isSubtype(xts.String(), tr.context()))) {
            prettyPrint(n);
			return;
		}
		if (n.invert()) {
			Name methodName = X10Binary_c.invBinaryMethodName(op);
			if (methodName != null) {
				er.generateStaticOrInstanceCall(n.position(), right, methodName, left);
				return;
			}
		}
		else {
			Name methodName = X10Binary_c.binaryMethodName(op);
			if (methodName != null) {
				er.generateStaticOrInstanceCall(n.position(), left, methodName, right);
				return;
			}
		}
		throw new InternalCompilerError("No method to implement " + n, n.position());
	}

        public void visit(X10CBackingArray_c n) {
            w.write("(");
            w.write("(");
            ArrayType arrayType = (ArrayType) n.type();
            er.printType(arrayType.base(), 0);
            w.write("[]");
            w.write(")");
            er.prettyPrint(n.container(), tr);
            w.write(".");
            w.write("value");
            w.write(")");
        }
        
        public void visit(X10CBackingArrayAccess_c n) {
            w.write("(");
            w.write("(");
            er.printType(n.type(), PRINT_TYPE_PARAMS);
            w.write(")");
            
            er.prettyPrint(n.array(), tr);
            w.write("[");
            er.prettyPrint(n.index(), tr);
            w.write("]");
            w.write(")");
        }

        public void visit(X10CBackingArrayAccessAssign_c n) {
            er.prettyPrint(n.array(), tr);
            w.write("[");
            er.prettyPrint(n.index(), tr);
            w.write("]");
            
            w.write(n.operator().toString());
            
            er.prettyPrint(n.right(), tr);
        }

        public void visit(X10CBackingArrayNewArray_c n) {
            w.write("new ");
            er.printType(((ArrayType)n.type()).base(), 0);
            for (Expr dim : n.dims()) {
                w.write("[");
                er.prettyPrint(dim, tr);
                w.write("]");
            }
            for (int i = 0; i < n.additionalDims(); i++) w.write("[]");
        }

	/**
	 * A list of one object that has an infinite circular iterator.
	 */
	public static class CircularList<T> extends AbstractList<T> {
		private T o;
		public CircularList(T o) { this.o = o; }
		public Iterator<T> iterator() {
			return new Iterator<T>() {
				public boolean hasNext() { return true; }
				public T next() { return o; }
				public void remove() { return; }
			};
		}
		public T get(int i) { return o; }
		public int size() { return -1; }
	}

} // end of X10PrettyPrinterVisitor

