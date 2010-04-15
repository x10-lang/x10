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

package x10.emitter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.CharLit;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Instanceof;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.MemberInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.visit.Translator;
import x10.Configuration;
import x10.ast.Clocked;
import x10.ast.ClosureCall;
import x10.ast.ParExpr_c;
import x10.ast.TypeParamNode;
import x10.ast.X10Cast;
import x10.ast.X10Cast_c;
import x10.ast.X10ClockedLoop;
import x10.ast.X10MethodDecl_c;
import x10.constraint.XAnd_c;
import x10.constraint.XEQV_c;
import x10.constraint.XEquals_c;
import x10.constraint.XField_c;
import x10.constraint.XFormula_c;
import x10.constraint.XLit_c;
import x10.constraint.XLocal_c;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XNot_c;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.extension.X10Ext;
import x10.types.ConstrainedType_c;
import x10.types.FunctionType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10Def;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator.XTypeLit_c;
import x10.types.checker.Converter;
import x10.types.constraints.CConstraint;
import x10.visit.X10PrettyPrinterVisitor;
import x10.visit.X10Translator;
import x10.visit.X10PrettyPrinterVisitor.CircularList;

public class Emitter {

	CodeWriter w;
	Translator tr;
	X10PrettyPrinterVisitor ppv;
        
        private static final Set<String> JAVA_KEYWORDS = new HashSet<String>(
            Arrays.asList(new String[]{
                "abstract", "default",  "if",         "private",    "this",
                "boolean",  "do",       "implements", "protected",  "throw",
                "break",    "double",   "import",     "public",     "throws",
                "byte",     "else",     "instanceof", "return",     "transient",
                "case",     "extends",  "int",        "short",      "try",
                "catch",    "final",    "interface",  "static",     "void",
                "char",     "finally",  "long",       "strictfp",   "volatile",
                "class",    "float",    "native",     "super",      "while",
                "const",    "for",      "new",        "switch",
                "continue", "goto",     "package",    "synchronized",
                "null",     "true",     "false"
                }
            )
        );
        
	public Emitter(CodeWriter w, Translator tr) {
		this.w=w;
		this.tr=tr;
	}

	public String mangle(QName name) {
		String mangle = name.toString().replace(".", "$");
		return mangle;
	}

	public static String mangleIdentifier(String n) {
		// Workaround an assertion failure in Name.make.
		if (! StringUtil.isNameShort(n))
			return n;
		return mangleIdentifier(Name.make(n)).toString();
	}

	public static Name mangleIdentifier(Name n) {
		Map<Name,Name> map = new HashMap<Name,Name>();
		map.put(Converter.operator_as, Name.make("$convert"));
		map.put(Converter.implicit_operator_as, Name.make("$implicit_convert"));
		map.put(Name.make("set"), Name.make("set"));
		map.put(Name.make("apply"), Name.make("apply"));
		map.put(Name.make("operator+"), Name.make("$plus"));
		map.put(Name.make("operator-"), Name.make("$minus"));
		map.put(Name.make("operator*"), Name.make("$times"));
		map.put(Name.make("operator/"), Name.make("$over"));
		map.put(Name.make("operator%"), Name.make("$percent"));
		map.put(Name.make("operator<"), Name.make("$lt"));
		map.put(Name.make("operator>"), Name.make("$gt"));
		map.put(Name.make("operator<="), Name.make("$le"));
		map.put(Name.make("operator>="), Name.make("$ge"));
		map.put(Name.make("operator<<"), Name.make("$left"));
		map.put(Name.make("operator>>"), Name.make("$right"));
		map.put(Name.make("operator>>>"), Name.make("$unsigned_right"));
		map.put(Name.make("operator&"), Name.make("$ampersand"));
		map.put(Name.make("operator|"), Name.make("$bar"));
		map.put(Name.make("operator^"), Name.make("$caret"));
		map.put(Name.make("operator~"), Name.make("$tilde"));
		map.put(Name.make("operator&&"), Name.make("$and"));
		map.put(Name.make("operator||"), Name.make("$or"));
		map.put(Name.make("operator!"), Name.make("$not"));
		map.put(Name.make("operator=="), Name.make("$equalsequals"));
		map.put(Name.make("operator!="), Name.make("$ne"));
		map.put(Name.make("inverse_operator+"), Name.make("$inv_plus"));
		map.put(Name.make("inverse_operator-"), Name.make("$inv_minus"));
		map.put(Name.make("inverse_operator*"), Name.make("$inv_times"));
		map.put(Name.make("inverse_operator/"), Name.make("$inv_over"));
		map.put(Name.make("inverse_operator%"), Name.make("$inv_percent"));
		map.put(Name.make("inverse_operator<"), Name.make("$inv_lt"));
		map.put(Name.make("inverse_operator>"), Name.make("$inv_gt"));
		map.put(Name.make("inverse_operator<="), Name.make("$inv_le"));
		map.put(Name.make("inverse_operator>="), Name.make("$inv_ge"));
		map.put(Name.make("inverse_operator<<"), Name.make("$inv_left"));
		map.put(Name.make("inverse_operator>>"), Name.make("$inv_right"));
		map.put(Name.make("inverse_operator>>>"), Name.make("$inv_unsigned_right"));
		map.put(Name.make("inverse_operator&"), Name.make("$inv_ampersand"));
		map.put(Name.make("inverse_operator|"), Name.make("$inv_bar"));
		map.put(Name.make("inverse_operator^"), Name.make("$inv_caret"));
		map.put(Name.make("inverse_operator~"), Name.make("$inv_tilde"));
		map.put(Name.make("inverse_operator&&"), Name.make("$inv_and"));
		map.put(Name.make("inverse_operator||"), Name.make("$inv_or"));
		map.put(Name.make("inverse_operator!"), Name.make("$inv_not"));
		map.put(Name.make("inverse_operator=="), Name.make("$inv_equalsequals"));
		map.put(Name.make("inverse_operator!="), Name.make("$inv_ne"));

		Name o = map.get(n);
		if (o != null)
			return o;
		return n;
	}

	public static String mangleToJava(Name name) {
	        String str = mangleIdentifier(name).toString();
	        String prefix = "kwd_";
	        if (str.startsWith(prefix)) {
	            str = "_" + str;
	        }
	        if (JAVA_KEYWORDS.contains(str)) {
	            str = prefix + str;
	        }
	        return str;
	}
	
	static HashMap<String, String> translationCache_ = new HashMap<String, String>();

	public static String translate(String id) {
		String cached = (String) translationCache_.get(id);
		if (cached != null)
			return cached;
		try {
			String rname = Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY + id
					+ ".xcd"; // xcd = x10 compiler data/definition
			InputStream is = Emitter.class.getClassLoader()
					.getResourceAsStream(rname);
			if (is == null)
				throw new IOException("Cannot find resource '" + rname + "'");
			byte[] b = new byte[is.available()];
			for (int off = 0; off < b.length;) {
				int read = is.read(b, off, b.length - off);
				off += read;
			}
			String trans = new String(b, "UTF-8");
			// Skip initial lines that start with "// SYNOPSIS: "
			// (spaces matter!)
			while (trans.indexOf("// SYNOPSIS: ") == 0)
				trans = trans.substring(trans.indexOf('\n') + 1);
			// Remove one trailing newline (if any)
			if (trans.lastIndexOf('\n') == trans.length() - 1)
				trans = trans.substring(0, trans.length() - 1);
			boolean newline = trans.lastIndexOf('\n') == trans.length() - 1;
			trans = "/* template:" + id + " { */" + trans + "/* } */";
			// If the template ends in a newline, add it after the footer
			if (newline)
				trans = trans + "\n";
			translationCache_.put(id, trans);
			is.close();
			return trans;
		} catch (IOException io) {
			throw new InternalCompilerError("No translation for " + id
					+ " found!", io);
		}
	}

	/**
	 * Expand a given template with given parameters.
	 * 
	 * @param id
	 *            xcd filename for the template
	 * @param components
	 *            arguments to the template.
	 */
	public void dump(String id, Object[] components, Translator tr) {
		String regex = translate(id);
		dumpRegex(id, components, tr, regex);
	}

	/**
	 * Support "inline" .xcd so that you dont have to create a separate xcd file
	 * for a short code fragment.
	 * 
	 * @param components
	 * @param regex
	 */
	public void dumpCodeString(String regex, Object... components) {
		dumpRegex("internal", components, tr, regex);
	}

	public void dumpRegex(String id, Object[] components, Translator tr,
			String regex) {
		for (int i = 0; i < components.length; i++) {
			assert !(components[i] instanceof Object[]);
		}
		int len = regex.length();
		int pos = 0;
		int start = 0;
		while (pos < len) {
			if (regex.charAt(pos) == '\n') {
				w.write(regex.substring(start, pos));
				w.newline(0);
				start = pos + 1;
			} else if (regex.charAt(pos) == '#') {
				w.write(regex.substring(start, pos));
				Integer idx = new Integer(regex.substring(pos + 1, pos + 2));
				pos++;
				start = pos + 1;
				if (idx.intValue() >= components.length)
					throw new InternalCompilerError("Template '" + id
							+ "' uses #" + idx);
				
				Object component = components[idx.intValue()];
				if (component instanceof Expr && !isNoArgumentType((Expr)component)) {
                                    component = new CastExpander(w, this, (Node) component).castTo(((Expr)component).type(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
				}
				prettyPrint(component, tr);
			}
			pos++;
		}
		w.write(regex.substring(start));
	}

	/**
	 * Pretty-print a given object.
	 * 
	 * @param o
	 *            object to print
	 */
	public void prettyPrint(Object o, Translator tr) {
		if (o instanceof Expander) {
			((Expander) o).expand(tr);
		} else if (o instanceof Node) {
			((Node) o).del().translate(w, tr);
		} else if (o instanceof Type) {
			throw new InternalCompilerError(
					"Should not attempt to pretty-print a type");
		} else if (o != null) {
			w.write(o.toString());
		}
	}

	public String getJavaImplForStmt(Stmt n, X10TypeSystem xts) {
		if (n.ext() instanceof X10Ext) {
			X10Ext ext = (X10Ext) n.ext();
			try {
				Type java = (Type) xts.systemResolver().find(
						QName.make("x10.compiler.Native"));
				List<X10ClassType> as = ext.annotationMatching(java);
				for (Type at : as) {
					assertNumberOfInitializers(at, 2);
					String lang = getPropertyInit(at, 0);
					if (lang != null && lang.equals("java")) {
						String lit = getPropertyInit(at, 1);
						return lit;
					}
				}
			} catch (SemanticException e) {
			}
		}
		return null;
	}

	public String getJavaImplForDef(X10Def o) {
		X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
		try {
			Type java = (Type) xts.systemResolver().find(
					QName.make("x10.compiler.Native"));
			List<Type> as = o.annotationsMatching(java);
			for (Type at : as) {
				assertNumberOfInitializers(at, 2);
				String lang = getPropertyInit(at, 0);
				if (lang != null && lang.equals("java")) {
					String lit = getPropertyInit(at, 1);
					return lit;
				}
			}
		} catch (SemanticException e) {
		}
		return null;
	}
	
	public boolean isPrimitiveJavaRep(X10ClassDef def) {
		String pat = getJavaRepParam(def, 1);
		if (pat == null) {
			return false;
		}
		String[] s = new String[] { "boolean", "byte", "char",
				"short", "int", "long", "float", "double" };
		for (int i = 0; i < s.length; i++) {
			if (pat.equals(s[i])) {
				return true;
			}
		}
		return false;
	}

	public String getJavaRep(X10ClassDef def, boolean boxPrimitives) {
		String pat = getJavaRepParam(def, 1);
		if (pat != null && boxPrimitives) {
			String[] s = new String[] { "boolean", "byte", "char",
					"short", "int", "long", "float", "double" };
			String[] w = new String[] { "java.lang.Boolean",
					"java.lang.Byte", "java.lang.Character",
					"java.lang.Short", "java.lang.Integer",
					"java.lang.Long", "java.lang.Float",
					"java.lang.Double" };
			for (int i = 0; i < s.length; i++) {
				if (pat.equals(s[i])) {
					pat = w[i];
					break;
				}
			}
		}
		return pat;
	}
	
	public String getJavaRep(X10ClassDef def) {
		return getJavaRepParam(def, 1);
	}

	public String getJavaRTTRep(X10ClassDef def) {
		return getJavaRepParam(def, 3);
	}

	public String getJavaRepParam(X10ClassDef def, int i) {
		try {
			X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
			Type rep = (Type) xts.systemResolver().find(
					QName.make("x10.compiler.NativeRep"));
			List<Type> as = def.annotationsMatching(rep);
			for (Type at : as) {
				assertNumberOfInitializers(at, 4);
				String lang = getPropertyInit(at, 0);
				if (lang != null && lang.equals("java")) {
					return getPropertyInit(at, i);
				}
			}
		} catch (SemanticException e) {
		}
		return null;
	}

	public String getPropertyInit(Type at, int index) {
		at = X10TypeMixin.baseType(at);
		if (at instanceof X10ClassType) {
			X10ClassType act = (X10ClassType) at;
			if (index < act.propertyInitializers().size()) {
				Expr e = act.propertyInitializer(index);
                                if (e != null && e.isConstant()) {
					Object v = e.constantValue();
					if (v instanceof String) {
						return (String) v;
					}
				}
			}
		}
		return null;
	}

	public void assertNumberOfInitializers(Type at, int len) {
		at = X10TypeMixin.baseType(at);
		if (at instanceof X10ClassType) {
			X10ClassType act = (X10ClassType) at;
			assert len == act.propertyInitializers().size();
		}
	}

	boolean printRepType(Type type, boolean printGenerics,
			boolean boxPrimitives, boolean inSuper) {
		if (type.isVoid()) {
			w.write("void");
			return true;
		}

		// If the type has a native representation, use that.
		if (type instanceof X10ClassType) {
			X10ClassDef cd = ((X10ClassType) type).x10Def();
			String pat = getJavaRep(cd, boxPrimitives);
			if (pat != null) {
				List<Type> typeArguments = ((X10ClassType) type)
						.typeArguments();
				Object[] o = new Object[typeArguments.size() + 1];
				int i = 0;
				NodeFactory nf = tr.nodeFactory();
				o[i++] = new TypeExpander(this, type, printGenerics,
						boxPrimitives, inSuper);
				for (Type a : typeArguments) {
					o[i++] = new TypeExpander(this, a, printGenerics, true,
							inSuper);
				}
				if (!printGenerics) {
					pat = pat.replaceAll("<.*>", "");
				}
				dumpRegex("NativeRep", o, tr, pat);
				return true;
			}
		}

		return false;
	}

	public String rttShortName(X10ClassDef cd) {
		if (cd.isMember() || cd.isLocal())
			return cd.name() + "$RTT";
		else
			return "RTT";
	}

	public String rttName(X10ClassDef cd) {
		if (cd.isTopLevel())
			return cd.fullName() + "." + rttShortName(cd);
		if (cd.isLocal())
			return rttShortName(cd);
		if (cd.isMember()) {
			X10ClassType container = (X10ClassType) Types.get(cd.container());
			return rttName(container.x10Def()) + "." + rttShortName(cd);
		}
		assert false : "unexpected class " + cd;
		return "";
	}

	public void printType(Type type, int flags) {
		boolean printTypeParams = (flags & X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS) != 0;
		boolean boxPrimitives = (flags & X10PrettyPrinterVisitor.BOX_PRIMITIVES) != 0;
		boolean inSuper = (flags & X10PrettyPrinterVisitor.NO_VARIANCE) != 0;
		boolean ignoreQual = (flags & X10PrettyPrinterVisitor.NO_QUALIFIER) != 0;

		X10TypeSystem xts = (X10TypeSystem) type.typeSystem();

		type = X10TypeMixin.baseType(type);

		if (type instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) type;
			if (ct.isAnonymous()) {
				if (ct.interfaces().size() > 0) {
					printType(ct.interfaces().get(0), flags);
					return;
				} else if (ct.superClass() != null) {
					printType(ct.superClass(), flags);
					return;
				} else {
					assert false;
					printType(xts.Object(), flags);
					return;
				}
			}
		}

		if (printRepType(type, printTypeParams, boxPrimitives, inSuper))
			return;

		if (type instanceof ParameterType) {
			w.write(((ParameterType) type).name().toString());
			return;
		}

		if (type instanceof FunctionType) {
			FunctionType ct = (FunctionType) type;
			List<Type> args = ct.argumentTypes();
			Type ret = ct.returnType();
			if (ret.isVoid()) {
				w.write("x10.core.fun.VoidFun");
			} else {
				w.write("x10.core.fun.Fun");
			}
			w.write("_" + ct.typeParameters().size());
			w.write("_" + args.size());
			if (printTypeParams && args.size() + (ret.isVoid() ? 0 : 1) > 0) {
				w.write("<");
				String sep = "";
				for (Type a : args) {
					w.write(sep);
					sep = ",";
					printType(
							a,
							(printTypeParams ? X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
									: 0)
									| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
				}
				if (!ret.isVoid()) {
					w.write(sep);
					printType(
							ret,
							(printTypeParams ? X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
									: 0)
									| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
				}
				w.write(">");
			}
			return;
		}

		// Shouldn't get here.
		if (type instanceof MacroType) {
			MacroType mt = (MacroType) type;
			printType(mt.definedType(),
					X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);
			return;
		}

		// Print the class name
		if (ignoreQual) {
			if (type instanceof X10ClassType) {
				w.write(((X10ClassType) type).name().toString());
			} else {
				type.print(w);
			}
		} else if (type.isNull()) {
		        w.write("java.lang.Object");
		} else {
			type.print(w);
		}

		if (printTypeParams) {
			if (type instanceof X10ClassType) {
				X10ClassType ct = (X10ClassType) type;
				String sep = "<";
				for (int i = 0; i < ct.typeArguments().size(); i++) {
					w.write(sep);
					sep = ", ";

					Type a = ct.typeArguments().get(i);

					final boolean variance = false;
					if (!inSuper && variance) {
						ParameterType.Variance v = ct.x10Def().variances().get(
								i);
						switch (v) {
						case CONTRAVARIANT:
							w.write("? super ");
							break;
						case COVARIANT:
							w.write("? extends ");
							break;
						case INVARIANT:
							break;
						}
					}
					printType(
							a,
							(printTypeParams ? X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
									: 0)
									| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
				}
				if (ct.typeArguments().size() > 0)
					w.write(">");
			}
		}
	}
	public void serializeConstraint(CConstraint constraint) {
		//       String serializedConstraint = serializedForm(constraint);
		//       StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, serializedConstraint);
		//       tr.print(null, lit, w);
		w.write("new x10.constraint.CConstraint_c() {{");
		w.newline(4);
		w.begin(0);
		w.write("try {");
		w.newline(4);
		w.begin(0);
		List<XTerm> terms = constraint.constraints();
		for (XTerm term : terms) {
			w.write("addTerm(");
			w.begin(0);
			serializeTerm(term, constraint);
			w.end();
			w.write(");");
			w.newline();
			// XConstraint_c c = new XConstraint_c();
			// c.addTerm(XTerms.makeEquals(XTerms.makeField(c.self(),
			// XTerms.makeName("p")), XTerms.makeLit(2)))
		}
		w.end();
		w.newline();
		w.write("} catch (x10.constraint.XFailure f) {");
		w.newline(4);
		w.begin(0);
		w.write("setInconsistent();");
		w.end();
		w.newline();
		w.write("}");
		w.end();
		w.newline();
		w.write("}}");
	}

	private static final String XTERMS = "x10.constraint.XTerms";
	private void serializeTerm(XTerm term, CConstraint parent) {
		if (term.equals(parent.self())) {
			w.write("self()");
		} else if (term == XTerms.OPERATOR) {
			w.write(XTERMS + ".OPERATOR");
		} else if (term instanceof XAnd_c) {
			w.write(XTERMS + ".makeAnd(");
			w.begin(0);
			serializeTerm(((XAnd_c) term).left(), parent);
			w.write(",");
			w.allowBreak(0, " ");
			serializeTerm(((XAnd_c) term).right(), parent);
			w.end();
			w.write(")");
		} else if (term instanceof XEquals_c) {
			w.write(XTERMS + ".makeEquals(");
			w.begin(0);
			serializeTerm(((XEquals_c) term).left(), parent);
			w.write(",");
			w.allowBreak(0, " ");
			serializeTerm(((XEquals_c) term).right(), parent);
			w.end();
			w.write(")");
		} else if (term instanceof XNot_c) {
			w.write(XTERMS + ".makeNot(");
			w.begin(0);
			serializeTerm(((XNot_c) term).unaryArg(), parent);
			w.end();
			w.write(")");
		} else if (term instanceof XFormula_c) {
			if (!((XFormula_c) term).isAtomicFormula())
				throw new RuntimeException("Non-atomic formula encountered: "
						+ term);
			w.write(XTERMS + ".makeAtom(");
			w.begin(0);
			serializeName(((XFormula_c) term).operator());
			List<XTerm> arguments = ((XFormula_c) term).arguments();
			for (XTerm arg : arguments) {
				w.write(",");
				w.allowBreak(0, " ");
				serializeTerm(arg, parent);
			}
			w.end();
			w.write(")");
		} else if (term instanceof XTypeLit_c) {
			w.write("new x10.rtt.ConstrainedType.XTypeLit_c(");
			w.begin(0);
			new RuntimeTypeExpander(this, ((XTypeLit_c) term).type())
					.expand(tr);
			w.end();
			w.write(")");
		} else if (term instanceof XLit_c) {
			Object val = ((XLit_c) term).val();
			w.write(XTERMS + ".makeLit(");
			w.begin(0);
			if (val == null) {
				w.write("null");
			} else if (val instanceof Boolean || val instanceof Number) {
				w.write(val.toString());
			} else if (val instanceof Character) {
				CharLit lit = tr.nodeFactory().CharLit(
						Position.COMPILER_GENERATED,
						((Character) val).charValue());
				tr.print(null, lit, w);
			} else if (val instanceof String) {
				StringLit lit = tr.nodeFactory().StringLit(
						Position.COMPILER_GENERATED, (String) val);
				tr.print(null, lit, w);
			} else if (val instanceof QName) {
				StringLit lit = tr.nodeFactory().StringLit(
						Position.COMPILER_GENERATED, ((QName) val).toString());
				tr.print(null, lit, w);
			} else if (val instanceof XName) {
				serializeName((XName) val);
			} else if (val.getClass() == Object.class) {
				StringLit lit = tr.nodeFactory().StringLit(
						Position.COMPILER_GENERATED, val.toString());
				tr.print(null, lit, w);
			} else {
				throw new RuntimeException("Unknown value type "
						+ val.getClass());
			}
			w.end();
			w.write(")");
		} else if (term instanceof XField_c) {
			w.write(XTERMS + ".makeField((x10.constraint.XVar)");
			w.begin(0);
			serializeTerm(((XField_c) term).receiver(), parent);
			w.write(",");
			w.allowBreak(0, " ");
			serializeName(((XField_c) term).field());
			w.end();
			w.write(")");
		} else if (term instanceof XEQV_c) {
			w.write("genEQV(");
			w.begin(0);
			serializeName(((XEQV_c) term).name());
			w.write(",");
			w.allowBreak(0, " ");
			w.write("" + ((XEQV_c) term).isEQV());
			w.end();
			w.write(")");
		} else if (term instanceof XLocal_c) {
			w.write(XTERMS + ".makeLocal(");
			w.begin(0);
			serializeName(((XLocal_c) term).name());
			w.end();
			w.write(")");
		} else {
			throw new RuntimeException("Unknown term type " + term.getClass()
					+ ": " + term);
		}
	}

	private void serializeName(XName n) {
		assert (n instanceof XNameWrapper);
		XNameWrapper<?> name = (XNameWrapper<?>) n;
		w.write(XTERMS + ".makeName(");
		w.begin(0);
		Object val = name.val();
		if (val == null) {
			w.write("null");
		} else if (val instanceof Def) {
			StringLit lit = tr.nodeFactory().StringLit(
					Position.COMPILER_GENERATED, val.toString());
			tr.print(null, lit, w);
		} else if (val instanceof ParameterType) {
			StringLit lit = tr.nodeFactory().StringLit(
					Position.COMPILER_GENERATED, val.toString());
			tr.print(null, lit, w);
		} else if (val instanceof Binary.Operator) {
			StringLit lit = tr.nodeFactory().StringLit(
					Position.COMPILER_GENERATED, val.toString());
			tr.print(null, lit, w);
		} else if (val instanceof Unary.Operator) {
			StringLit lit = tr.nodeFactory().StringLit(
					Position.COMPILER_GENERATED, val.toString());
			tr.print(null, lit, w);
		} else if (val instanceof String) {
			StringLit lit = tr.nodeFactory().StringLit(
					Position.COMPILER_GENERATED, val.toString());
			tr.print(null, lit, w);
		} else if (val.getClass() == Object.class) {
			StringLit lit = tr.nodeFactory().StringLit(
					Position.COMPILER_GENERATED, val.toString());
			tr.print(null, lit, w);
		} else {
			throw new RuntimeException("Unknown value type " + val.getClass());
		}
		w.write(",");
		w.allowBreak(0, " ");
		StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED,
				name.toString());
		tr.print(null, lit, w);
		w.end();
		w.write(")");
	}

	public void generateDispatchers(X10ClassDef cd) {
		if (true)
			return;
		if (cd.flags().isInterface() || cd.flags().isAbstract()) {
			return;
		}

		X10ClassType ct = (X10ClassType) cd.asType();

		Collection<MethodInstance> seen = new ArrayList<MethodInstance>();

		// Remove methods where we generate the dispatcher directly.
		for (MethodDef md : cd.methods()) {
			MethodInstance mi = md.asInstance();
			mi = (MethodInstance) mi.container(ct);
			if (methodUsesClassParameter(mi.def()) && !md.flags().isStatic())
				seen.add(mi);
		}

		List<MethodInstance> methods = new ArrayList<MethodInstance>();
		getInheritedVirtualMethods(ct, methods);

		// Remove abstract or overridden methods.
		for (ListIterator<MethodInstance> i = methods.listIterator(); i
				.hasNext();) {
			MethodInstance mi = i.next();
			if (mi.flags().isAbstract()) {
				i.remove();
				continue;
			}
			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			MethodInstance mj = ts.findImplementingMethod(cd.asType(), mi, tr
					.context());
			if (mj != null && mj.def() != mi.def())
				i.remove();
		}

		// Remove methods that don't need dispatchers
		// and reset containers.
		for (ListIterator<MethodInstance> i = methods.listIterator(); i
				.hasNext();) {
			MethodInstance mi = i.next();
			mi = (MethodInstance) mi.container(ct);
			if (!overridesMethodThatUsesClassParameter(mi))
				i.remove();
			else if (methodUsesClassParameter(mi.def()))
				i.remove();
			else
				i.set(mi);
		}

		// Remove seen methods.
		for (ListIterator<MethodInstance> i = methods.listIterator(); i
				.hasNext();) {
			MethodInstance mi = i.next();
			if (seen(seen, mi))
				i.remove();
		}

		for (MethodInstance mi : methods) {
			generateDispatcher((X10MethodInstance) mi,
					methodUsesClassParameter(mi.def()));
		}
	}

	private boolean seen(Collection<MethodInstance> seen, MethodInstance mi) {
		for (MethodInstance mj : seen) {
			if (mi.name().equals(mj.name())
					&& mj.hasFormals(mi.formalTypes(), tr.context()))
				return true;
		}
		seen.add(mi);
		return false;
	}

	public void getInheritedVirtualMethods(X10ClassType ct,
			List<MethodInstance> methods) {
		for (MethodInstance mi : ct.methods()) {
			if (!mi.flags().isStatic())
				methods.add(mi);
		}
		Type sup = ct.superClass();
		if (sup instanceof X10ClassType) {
			getInheritedVirtualMethods((X10ClassType) sup, methods);
		}
		for (Type t : ct.interfaces()) {
			if (t instanceof X10ClassType) {
				getInheritedVirtualMethods((X10ClassType) t, methods);
			}
		}
	}

	public void generateDispatcher(X10MethodInstance md, boolean usesClassParam) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

		Flags flags = md.flags();
		flags = X10Flags.toX10Flags(flags).clearExtern();
		flags = flags.clearNative();

		// Hack to ensure that X10Flags are not printed out .. javac will
		// not know what to do with them.
		flags = X10Flags.toX10Flags(flags);

		w.begin(0);
		w.write(flags.translate());

		String sep = "<";
		for (int i = 0; i < md.typeParameters().size(); i++) {
			w.write(sep);
			sep = ", ";
			printType(md.typeParameters().get(i),
					X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
							| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
		}
		if (md.typeParameters().size() > 0)
			w.write("> ");

		printType(md.returnType(), X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
				| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
		w.allowBreak(2, 2, " ", 1);
		w.write(mangleToJava(md.name()));

		w.write("(");

		w.allowBreak(2, 2, "", 0);
		w.begin(0);
		boolean first = true;

		// Add a formal parameter of type Type for each type parameters.
		for (Type p : md.typeParameters()) {
			if (!first) {
				w.write(",");
				w.allowBreak(0, " ");
			}
			first = false;
			w.write("final ");
			w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
			w.write(" ");
			Type pt = p;
			assert pt instanceof ParameterType;
			Name name = ((ParameterType) pt).name();
                        w.write(mangleToJava(name));
		}

		List<Expander> dispatchArgs = new ArrayList<Expander>();

		for (Type pt : md.typeParameters()) {
			dispatchArgs.add(new Inline(this, mangleToJava(((ParameterType) pt).name())));
		}

		for (int i = 0; i < md.formalTypes().size(); i++) {
			Type f = md.formalTypes().get(i);
			if (!first) {
				w.write(",");
				w.allowBreak(0, " ");
			}
			first = false;

			w.write(Flags.FINAL.translate());
			printType(f, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
					| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			w.write(" ");

			Name name = Name.make("a" + (i + 1));
			w.write(name.toString());

			dispatchArgs.add(new Join(this, "", CollectionUtil.list("(",
					new TypeExpander(this, f, true, true, false), ") ", name
							.toString())));
		}

		w.end();
		w.write(")");

		if (!md.throwTypes().isEmpty()) {
			w.allowBreak(6);
			w.write("throws ");

			for (Iterator<Type> i = md.throwTypes().iterator(); i.hasNext();) {
				Type t = i.next();
				printType(t, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(4, " ");
				}
			}
		}

		w.end();

		if (isAbstract(md)) {
			w.write(";");
			return;
		}

		w.write(" ");
		w.write("{");
		w.allowBreak(4);

		if (!md.returnType().isVoid()) {
			w.write(" return       ");
		}

		String pat = getJavaImplForDef(md.x10Def());
		if (pat != null) {
			String target = "this";
			emitNativeAnnotation(pat, target, md.typeParameters(), dispatchArgs, Collections.<Type>emptyList());
			w.write("; }");
			return;
		}

		w.write("this");
		w.write(".");

		sep = "<";
		for (int i = 0; i < md.typeParameters().size(); i++) {
			w.write(sep);
			sep = ", ";
			printType(md.typeParameters().get(i),
					X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
							| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
		}
		if (md.typeParameters().size() > 0)
			w.write("> ");

		w.write(mangleToJava(md.name()));
		w.write("(");
		w.begin(0);

		for (int i = 0; i < dispatchArgs.size(); i++) {
			if (i != 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
			dispatchArgs.get(i).expand(tr);
		}

		w.end();
		w.write(");");
		w.allowBreak(0, " ");
		w.write("}");
	}

	boolean isAbstract(MemberInstance<?> mi) {
		if (mi.flags().isAbstract())
			return true;
		Type t = X10TypeMixin.baseType(mi.container());
		if (t instanceof ClassType) {
			ClassType ct = (ClassType) t;
			if (ct.flags().isInterface())
				return true;
		}
		return false;
	}

	public void generateRTTMethods(X10ClassDef def) {
		generateRTTMethods(def, false);
	}

	public void generateRTTMethods(X10ClassDef def, boolean boxed) {
		Set<ClassDef> visited = new HashSet<ClassDef>();
		visited.add(def);

		// Generate RTTI methods, one for each parameter.
		for (int i = 0; i < def.typeParameters().size(); i++) {
			ParameterType pt = def.typeParameters().get(i);
			ParameterType.Variance var = def.variances().get(i);

			w.write("public x10.rtt.Type<?> " + "rtt_" + mangle(def.fullName())
					+ "_");
			w.write(mangleToJava(pt.name()));
			w.write("()");

			if (def.flags().isInterface()) {
				w.write(";");
			} else if (!boxed) {
				w.write(" { return this.");
				w.write(mangleToJava(pt.name()));
				w.write("; }");
			} else {
				w.write(" { throw new java.lang.RuntimeException(); }");
			}
			w.newline();
		}

		// Generate RTTI methods for each interface instantiation.
		if (!def.flags().isInterface()) {
			LinkedList<Type> worklist = new LinkedList<Type>();

			for (Type t : def.asType().interfaces()) {
				Type it = X10TypeMixin.baseType(t);
				worklist.add(it);
			}

			while (!worklist.isEmpty()) {
				Type it = worklist.removeFirst();

				if (it instanceof X10ClassType) {
					X10ClassType ct = (X10ClassType) it;
					X10ClassDef idef = ct.x10Def();

					if (visited.contains(idef))
						continue;
					visited.add(idef);

					for (Type t : ct.interfaces()) {
						Type it2 = X10TypeMixin.baseType(t);
						worklist.add(it2);
					}

					for (int i = 0; i < idef.typeParameters().size(); i++) {
						ParameterType pt = idef.typeParameters().get(i);
						w.write("public x10.rtt.Type<?> " + "rtt_"
								+ mangle(idef.fullName()) + "_");
						w.write(mangleToJava(pt.name()));
						w.write("() { ");

						if (!boxed) {
							w.write("return ");
							Type at = ct.typeArguments().get(i);
							new RuntimeTypeExpander(this, at).expand();
						} else {
							w.write("throw new java.lang.RuntimeException()");
						}

						w.write("; }");
						w.newline();
					}
				}
			}
		}
	}
	
	/*
	 * For "java" annotations:
	 * 
	 * Given a method with signature: def m[X, Y](x, y); and a call o.m[A, B](a,
	 * b); #0 = o #1 = A #2 = boxed representation of A #3 = run-time Type
	 * object for A #4 = B #5 = boxed representation of B #6 = run-time Type
	 * object for B #7 = a #8 = b
	 */
	public void emitNativeAnnotation(String pat, Object target,
			List<Type> types, List<? extends Object> args, List<Type> typeArguments) {
		Object[] components = new Object[1 + types.size() * 3 + args.size() + typeArguments.size() * 3];
		int i = 0;
		components[i++] = target;
		for (Type at : types) {
			components[i++] = new TypeExpander(this, at, true, false, false);
			components[i++] = new TypeExpander(this, at, true, true, false);
			components[i++] = new RuntimeTypeExpander(this, at);
		}
		for (Object e : args) {
			components[i++] = e;
		}
        for (Type at : typeArguments) {
            components[i++] = new TypeExpander(this, at, true, false, false);
            components[i++] = new TypeExpander(this, at, true, true, false);
            components[i++] = new RuntimeTypeExpander(this, at);
        }
		this.dumpRegex("Native", components, tr, pat);
	}

	private boolean overridesMethodThatUsesClassParameter(MethodInstance mi) {
		if (methodUsesClassParameter(mi.def()))
			return true;

		for (MethodInstance mj : mi.implemented(tr.context())) {
			boolean usesParameter = methodUsesClassParameter(mj.def());
			if (usesParameter)
				return true;
		}

		return false;
	}

	private boolean methodUsesClassParameter(MethodDef md) {
		Type container = Types.get(md.container());
		container = X10TypeMixin.baseType(container);
		if (container instanceof X10ClassType) {
			X10ClassDef cd = ((X10ClassType) container).x10Def();

			if (cd.typeParameters().size() == 0)
				return false;

			if (getJavaRep(cd) != null)
				return false;

			for (Ref<? extends Type> tref : md.formalTypes()) {
				Type t = Types.get(tref);
				if (t instanceof ParameterType) {
					ParameterType pt = (ParameterType) t;
					Def d = Types.get(pt.def());
					if (d == cd) {
						return true;
					}
				}
			}
			Type t = Types.get(md.returnType());
			if (t instanceof ParameterType) {
				ParameterType pt = (ParameterType) t;
				Def d = Types.get(pt.def());
				if (d == cd) {
					return true;
				}
			}
		}

		return false;
	}

	public void generateMethodDecl(X10MethodDecl_c n, boolean boxPrimitives) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

		Flags flags = n.flags().flags();

		Context c = tr.context();

		if (c.currentClass().flags().isInterface()) {
			flags = flags.clearPublic();
			flags = flags.clearAbstract();
		}

		// Hack to ensure that X10Flags are not printed out .. javac will
		// not know what to do with them.
		flags = X10Flags.toX10Flags(flags);

		w.begin(0);
		w.write(flags.translate());

		String sep = "<";
		for (int i = 0; i < n.typeParameters().size(); i++) {
			w.write(sep);
			sep = ", ";
			printType(n.typeParameters().get(i).type(),
					X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
							| X10PrettyPrinterVisitor.BOX_PRIMITIVES);
		}
		if (n.typeParameters().size() > 0)
			w.write("> ");

		printType(
				n.returnType().type(),
				X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
						| (boxPrimitives ? X10PrettyPrinterVisitor.BOX_PRIMITIVES
								: 0));
		w.allowBreak(2, 2, " ", 1);
		tr.print(n, n.name(), w);

		w.write("(");

		w.allowBreak(2, 2, "", 0);
		w.begin(0);
		boolean first = true;

		// Add a formal parameter of type Type for each type parameters.
		for (TypeParamNode p : n.typeParameters()) {
			if (!first) {
				w.write(",");
				w.allowBreak(0, " ");
			}
			first = false;

			w.write("final ");
			w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
			w.write(" ");
			w.write(Emitter.mangleToJava(p.name().id()));
		}

		for (int i = 0; i < n.formals().size(); i++) {
			Formal f = n.formals().get(i);
			if (!first) {
				w.write(",");
				w.allowBreak(0, " ");
			}
			first = false;

			tr.print(n, f.flags(), w);
			printType(
					f.type().type(),
					X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
							| (boxPrimitives ? X10PrettyPrinterVisitor.BOX_PRIMITIVES
									: 0));
			w.write(" ");

			Name name = f.name().id();
			if (name.toString().equals("")) {
				name = Name.makeFresh("a");
			}
			tr.print(n, f.name().id(name), w);
		}

		w.end();
		w.write(")");

		if (!n.throwTypes().isEmpty()) {
			w.allowBreak(6);
			w.write("throws ");

			for (Iterator<TypeNode> i = n.throwTypes().iterator(); i.hasNext();) {
				TypeNode tn = (TypeNode) i.next();
				// vj 09/26/08: Changed to print out translated version of throw
				// type
				// tr.print(n, tn, w);
				// TODO: Nate to check.
				printType(tn.type(), X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);

				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(4, " ");
				}
			}
		}

		w.end();

		if (n.body() != null) {

			// if (! unboxFormals.isEmpty()) {
			// for (Map.Entry<Name,Formal> e : unboxFormals.entrySet()) {
			// Name newName = e.getKey();
			// Formal f = e.getValue();
			// tr.print(n, f.flags(), w);
			// printType(f.type().type(), true, false, false); // don't box
			// w.write(" ");
			// tr.print(n, f.name(), w);
			// w.write(" = ");
			// w.write(newName.toString());
			// w.write(";");
			// }
			// w.allowBreak(0, " ");

			tr.print(n, n.body(), w);
		} else {
			w.write(";");
		}
	}

	public void generateRTType(X10ClassDef def) {
		w.newline();

		String mangle = mangle(def.fullName());

		boolean isConstrained = def.classInvariant() != null
				&& !def.classInvariant().get().valid();

		String superClass = "x10.rtt.RuntimeType";
		if (X10PrettyPrinterVisitor.serialize_runtime_constraints
				&& isConstrained) { // constrained type; treat specially
			superClass = "x10.rtt.ConstrainedType";
		}

		if (def.asType().isGloballyAccessible()) {
			w.write("public static ");
		}
		w.write("class ");

		Expander rttShortName;

		if (def.typeParameters().size() == 0) {
			rttShortName = new Join(this, "", rttShortName(def), "");
		} else {
			X10ClassType ct = (X10ClassType) def.asType();
			rttShortName = new Join(this, ", ", ct.typeArguments());
			List<TypeExpander> args = new ArrayList<TypeExpander>();
			for (Type a : ct.typeArguments()) {
				args.add(new TypeExpander(this, a,
						X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
								| X10PrettyPrinterVisitor.BOX_PRIMITIVES));
			}
			rttShortName = new Join(this, "", rttShortName(def), "<", new Join(
					this, ", ", args), ">");
		}

		rttShortName.expand(tr);

		w.write(" extends ");
		w.write(superClass);
		w.write("<");
		printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES
				| X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
				| X10PrettyPrinterVisitor.NO_VARIANCE);
		w.write("> {");
		w.newline();
		w.begin(4);
		if (def.asType().isGloballyAccessible()
				&& def.typeParameters().size() == 0) {
			w.write("public static final ");
			rttShortName.expand(tr);
			w.write(" it = new ");
			rttShortName.expand(tr);
			w.write("();");
			w.newline();
			w.newline();
		}
		for (int i = 0; i < def.typeParameters().size(); i++) {
			ParameterType pt = def.typeParameters().get(i);
			w.write("public final x10.rtt.Type ");
			w.write(Emitter.mangleToJava(pt.name()));
			w.write(";");
			w.newline();
		}
		w.newline();
		w.write("public " + this.rttShortName(def) + "(");

		for (int i = 0; i < def.typeParameters().size(); i++) {
			ParameterType pt = def.typeParameters().get(i);
			if (i != 0)
				w.write(", ");
			w.write("final x10.rtt.Type ");
			w.write(Emitter.mangleToJava(pt.name()));
		}

		w.write(") {");
		w.begin(4);
		w.write("super(");

		if (X10PrettyPrinterVisitor.serialize_runtime_constraints
				&& isConstrained) { // constrained type; treat specially
			w.write("new x10.rtt.RuntimeType<");
			printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			w.write(">(");
			printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			w.write(".class");
			w.write(")");
			// new RuntimeTypeExpander(def.asType()).expand(tr); // Cannot do
			// this, because we are *defining* T.it here
			w.write(", ");
			w.write("null, "); // TODO
			CConstraint constraint = def.classInvariant().get();
			assert (constraint != null);
			serializeConstraint(constraint);
		} else {
			printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			w.write(".class");
		}
		w.write(");");
		w.newline();
		for (int i = 0; i < def.typeParameters().size(); i++) {
			ParameterType pt = def.typeParameters().get(i);
			w.write("this.");
			w.write(Emitter.mangleToJava(pt.name()));
			w.write(" = ");
			w.write(Emitter.mangleToJava(pt.name()));
			w.write(";");
			w.newline();
		}
		w.end();
		w.write("}");
		w.newline();
		w.write("public boolean instanceof$(java.lang.Object o) {");
		w.newline();
		w.begin(4);
		if (def.asType().isInnerClass()) {
			w.write("try { ");
			printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			w.write(" zzz = (");
			printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			w
					.write(") o; } catch (ClassCastException xyzzy) { return false; }");
		} else {
			w.write("if (! (o instanceof ");
			printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			w.write(")) return false;");
		}
		w.newline();
		// w.write("RTT ro = (RTT) new RTT(this, o);");
		// w.newline();
		// w.write("if (ro == null) return false;");
		// w.newline();
		for (int i = 0; i < def.typeParameters().size(); i++) {
			ParameterType pt = def.typeParameters().get(i);
			ParameterType.Variance var = def.variances().get(i);
			w.write("if (! ");
			switch (var) {
			case INVARIANT:
				w.write("this.");
				w.write(Emitter.mangleToJava(pt.name()));
				w.write(".equals(");
				javacast(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES,
						"o");
				w.write("." + "rtt_" + mangle(def.fullName()) + "_");
				w.write(Emitter.mangleToJava(pt.name()));
				w.write("()");
				w.write(")");
				break;
			case COVARIANT:
				w.write("this.");
				w.write(Emitter.mangleToJava(pt.name()));
				w.write(".isSubtype(");
				javacast(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES,
						"o");
				w.write("." + "rtt_" + mangle(def.fullName()) + "_");
				w.write(Emitter.mangleToJava(pt.name()));
				w.write("()");
				w.write(")");
				break;
			case CONTRAVARIANT:
				javacast(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES,
						"o");
				w.write("." + "rtt_" + mangle(def.fullName()) + "_");
				w.write(Emitter.mangleToJava(pt.name()));
				w.write("()");
				w.write(".isSubtype(");
				w.write("this.");
				w.write(Emitter.mangleToJava(pt.name()));
				w.write(")");
				break;
			}
			w.write(") return false;");
			w.newline();
		}
		w.write("return true;");
		w.end();
		w.newline();
		w.write("}");
		w.newline();

		for (Ref<? extends ClassType> mtref : def.memberClasses()) {
			X10ClassType mt = (X10ClassType) Types.get(mtref);
			generateRTType(mt.x10Def());
		}

		w.write("public java.util.List<x10.rtt.Type<?>> getTypeParameters() {");
		w.newline();
		w.begin(4);
		if (def.typeParameters().isEmpty())
			w.write("return null;");
		else {
			w.write("return java.util.Arrays.asList(new x10.rtt.Type<?>[] { ");
			w.newline();
			w.begin(4);
			for (int i = 0; i < def.typeParameters().size(); i++) {
				ParameterType pt = def.typeParameters().get(i);
				if (i != 0)
					w.write(", ");
				w.write(Emitter.mangleToJava(pt.name()));
			}
			w.end();
			w.newline();
			w.write(" });");
		}
		w.end();
		w.newline();
		w.write("}");
		w.end();
		w.newline();

		w.write("}");
		w.newline();
	}

	private void javacast(Node parent, Type t, int flags, Expr e) {
		if (X10PrettyPrinterVisitor.reduce_generic_cast && 
				(flags & X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS) != 0) {
			flags -= X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS;
		}
		w.write("((");
		printType(t, flags);
		w.write(") ");
		tr.print(parent, e, w);
		w.write(")");

		// w.write("x10.rtt.Types.<");
		// printType(t, flags | BOX_PRIMITIVES);
		// w.write(">javacast(");
		// tr.print(parent, e, w);
		// w.write(")");
	}

	private void javacast(Type t, int flags, Expander e) {
		if (X10PrettyPrinterVisitor.reduce_generic_cast &&
				(flags & X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS) != 0) {
			flags -= X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS;
		}
		w.write("((");
		printType(t, flags);
		w.write(") ");
		e.expand(tr);
		w.write(")");
		// w.write("x10.rtt.Types.<");
		// printType(t, flags | BOX_PRIMITIVES);
		// w.write(">javacast(");
		// e.expand(tr);
		// w.write(")");
	}

	private void javacast(Type t, int flags, String e) {
		if (X10PrettyPrinterVisitor.reduce_generic_cast &&
				(flags & X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS) != 0) {
			flags -= X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS;
		}
		w.write("((");
		printType(t, flags);
		w.write(") ");
		w.write(e);
		w.write(")");
		// w.write("x10.rtt.Types.<");
		// printType(t, flags | BOX_PRIMITIVES);
		// w.write(">javacast(");
		// w.write(e);
		// w.write(")");
	}

	/**
	 * @param pos
	 * @param left
	 *            TODO
	 * @param name
	 * @param right
	 *            TODO
	 */
	public void generateStaticOrInstanceCall(Position pos, Expr left,
			Name name, Expr... right) {
		List<Expr> sargs = new ArrayList<Expr>();
		List<Type> stypes = new ArrayList<Type>();
		sargs.add(left);
		stypes.add(left.type());
		for (Expr e : right) {
			sargs.add(e);
			stypes.add(e.type());
		}
		List<Type> types = stypes.subList(1, stypes.size());
		List<Expr> args = sargs.subList(1, sargs.size());

		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		try {
			MethodInstance mi = xts.findMethod(left.type(), xts.MethodMatcher(
					left.type(), name, types, tr.context()));
			tr.print(null, nf.Call(pos, left, nf.Id(pos, name), args)
					.methodInstance(mi).type(mi.returnType()), w);
		} catch (SemanticException e) {
			throw new InternalCompilerError(e.getMessage(), pos, e);
		}
	}

	public void arrayPrint(Node n, Node array, CodeWriter w, Template tmp) {
		if (null == tmp) {
			tr.print(n, array, w);
		} else {
			w.write("(");
			tmp.expand();
			w.write(")");
		}

	}

	public void printFormal(Translator tr, Node n, Formal f, boolean mustBox) {
		tr.print(n, f.flags(), w);
		printType(f.type().type(), X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
				| (mustBox ? X10PrettyPrinterVisitor.BOX_PRIMITIVES : 0));
		w.write(" ");

		Name name = f.name().id();
		if (name.toString().equals(""))
			tr.print(n, f.name().id(Name.makeFresh("a")), w);
		else {
			tr.print(n, f.name(), w);
		}
	}
	
	public boolean isNoArgumentType(Expr e) {
		while (e instanceof ParExpr_c) {
			e = ((ParExpr_c) e).expr();
			if (e == null) {
			    return true;
			}
		}
		
		Type exprTgtType = null;
		if (e instanceof Field_c) {
			exprTgtType = ((Field_c) e).target().type();
		} else if (e instanceof Call) {
			exprTgtType = ((Call) e).target().type();
		}
		
		if (exprTgtType != null) {
			if (exprTgtType instanceof X10ParsedClassType_c && !((X10ParsedClassType_c) exprTgtType).typeArguments().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void coerce(Node parent, Expr e, Type expected) {
		Type actual = e.type();
		
		if (expected instanceof ConstrainedType_c) {
			expected = ((ConstrainedType_c) expected).baseType().get();
		}
		if (actual instanceof ConstrainedType_c) {
			actual = ((ConstrainedType_c) actual).baseType().get();
		}
		
		boolean parameterExpected = expected instanceof ParameterType;

		CastExpander expander = new CastExpander(w, this, e);
		if (actual.isNull() || e.isConstant() && !parameterExpected) {
		} else if (actual != expected 
					&& (actual.isBoolean() || actual.isNumeric() || actual.isByte())) {
			//when the type of e has parameters, cast to actual boxed primitive. 
			if (!isNoArgumentType(e)) {
				expander = expander.castTo(actual, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			}
			//cast to actual primitive to expected primitive to expected boxed primitive.
			expander = expander.castTo(actual).castTo(expected).castTo(expected, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
		} else if (actual.isBoolean() || actual.isNumeric() || actual.isByte()){
			if (!isNoArgumentType(e)) {
				expander = expander.castTo(expected, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
			}
		} else {
			//cast eagerly
			expander = expander.castTo(expected, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
		}
		expander.expand(tr);
	}

	public static X10ClassType annotationNamed(TypeSystem ts, Node o, QName name)
			throws SemanticException {
		// Nate's code. This one.
		if (o.ext() instanceof X10Ext) {
			X10Ext ext = (X10Ext) o.ext();
			X10ClassType baseType = (X10ClassType) ts.systemResolver().find(
					name);
			List<X10ClassType> ats = ext.annotationMatching(baseType);
			if (ats.size() > 1) {
				throw new SemanticException("Expression has more than one "
						+ name + " annotation.", o.position());
			}
			if (!ats.isEmpty()) {
				X10ClassType at = ats.get(0);
				return at;
			}
		}
		return null;
	}

	public boolean hasAnnotation(Node dec, QName name) {
	    return hasAnnotation((X10TypeSystem) tr.typeSystem(), dec, name);
	}

	public static boolean hasAnnotation(X10TypeSystem ts, Node dec, QName name) {
		try {
			if (annotationNamed(ts, dec, name) != null)
				return true;
		} catch (NoClassException e) {
			if (!e.getClassName().equals(name.toString()))
				throw new InternalCompilerError(
						"Something went terribly wrong", e);
		} catch (SemanticException e) {
			throw new InternalCompilerError("Something is terribly wrong", e);
		}
		return false;
	}

	private TypeNode getParameterType(Type at) {
		NodeFactory nf = tr.nodeFactory();
		Type parameterType = X10TypeMixin.getParameterType(at, 0);
		if (parameterType != null)
			return nf.CanonicalTypeNode(Position.COMPILER_GENERATED,
					parameterType);
		return null;
	}

	public boolean hasEffects(Receiver e) {
		if (e instanceof TypeNode)
			return false;
		if (e instanceof Local)
			return false;
		if (e instanceof Lit)
			return false;
		if (e instanceof Field) {
			Field f = (Field) e;
			return hasEffects(f.target());
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			if (u.operator() == Unary.BIT_NOT || u.operator() == Unary.NOT
					|| u.operator() == Unary.POS || u.operator() == Unary.NEG)
				return hasEffects(u.expr());
		}
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			return hasEffects(b.left()) || hasEffects(b.right());
		}
		if (e instanceof Cast) {
			Cast c = (Cast) e;
			return hasEffects(c.expr());
		}
		if (e instanceof Instanceof) {
			Instanceof i = (Instanceof) e;
			return hasEffects(i.expr());
		}
		// HACK: Rail.apply has no effects
		if (e instanceof ClosureCall) {
			ClosureCall c = (ClosureCall) e;
			Expr target = c.target();
			if (hasEffects(target))
				return true;
			for (Expr a : c.arguments()) {
				if (hasEffects(a))
					return true;
			}
			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			if (ts.isRail(target.type()) || ts.isValRail(target.type()))
				return false;
		}
		if (e instanceof Call) {
			Call c = (Call) e;
			Receiver target = c.target();
			if (hasEffects(target))
				return true;
			for (Expr a : c.arguments()) {
				if (hasEffects(a))
					return true;
			}
			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			if (c.name().id().equals(Name.make("apply")))
				if (ts.isRail(target.type()) || ts.isValRail(target.type()))
					return false;
		}
		return true;
	}

	public boolean needsHereCheck(Receiver target, X10Context context) {
		return false;
		/*
		 * boolean needsHereCheck = true; // calls on new objects needsHereCheck
		 * &= ! (target instanceof New); // others... needsHereCheck &=
		 * QueryEngine.INSTANCE().needsHereCheck(target.type(), context);
		 * 
		 * if (needsHereCheck) { if (target instanceof X10Cast) { X10Cast c =
		 * (X10Cast) target; if (c.conversionType() ==
		 * X10Cast.ConversionType.CHECKED) { return needsHereCheck(c.expr(),
		 * context); } } } return needsHereCheck;
		 */
	}

	private Template processClocks(Clocked c) {
		assert (null != c.clocks());
		Template clocks = null;
		// if (c.clocks().isEmpty())
		// clocks = null;
		// else if (c.clocks().size() == 1)
		// clocks = new Template("clock", c.clocks().get(0));
		// else {
		Integer id = X10PrettyPrinterVisitor.getUniqueId_();
		clocks = new Template(this, "clocked", new Loop(this, "clocked-loop", c
				.clocks(), new CircularList<Integer>(id)), id);
		// }
		return clocks;
	}

	public void processClockedLoop(String template, X10ClockedLoop l) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template(this, template,
		/* #0 */l.formal().flags(),
		/* #1 */l.formal().type(),
		/* #2 */l.formal().name(),
		/* #3 */l.domain(),
				/* #4 */new Join(this, "\n", new Join(this, "\n", l.locals()),
						l.body()),
				/* #5 */processClocks(l),
				/* #6 */new Join(this, "\n", l.locals()),
				/* #7 */new TypeExpander(this, l.formal().type().type(),
						X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
								| X10PrettyPrinterVisitor.BOX_PRIMITIVES),
				/* #8 */l.position().nameAndLineString().replace("\\", "\\\\"))
				.expand(tr2);
	}

	public String convertToString(Object[] a) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < a.length; ++i) {
			s.append(a[i].toString());
			if (i + 1 < a.length) {
				s.append(", ");
			}
		}
		s.append("]");
		return s.toString();
	}

	public String convertToString(List<?> a) {
		StringBuffer s = new StringBuffer("[");
		final int size = a.size();
		for (int i = 0; i < size; ++i) {
			s.append(a.get(i).toString());
			if (i + 1 < size) {
				s.append(", ");
			}
		}
		s.append("]");
		return s.toString();
	}
}
