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
import java.util.Map.Entry;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Instanceof;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Node_c;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
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
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.visit.Translator;
import x10.ast.ClosureCall;
import x10.ast.ParExpr_c;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10MethodDecl_c;
import x10.extension.X10Ext;
import x10.types.ConstrainedType_c;
import x10.types.FunctionType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;
import x10.visit.X10PrettyPrinterVisitor;
import x10c.types.BackingArrayType;

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
                "null",     "true",     "false",
                
                // X10 implementation names        
                "getRTT", "_RTT", "getParam"
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

	private static Name mangleIdentifier(Name n) {
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
			    Integer idx;
			    if (pos<len-2 && Character.isDigit(regex.charAt(pos+2))) {
			        idx = new Integer(regex.substring(pos + 1, pos + 3));
			        pos += 2;			    
			    } else {
			        idx = new Integer(regex.substring(pos + 1, pos + 2));
			        pos++;				    
			    }
			    start = pos + 1;
				if (idx.intValue() >= components.length)
					throw new InternalCompilerError("Template '" + id
							+ "' uses #" + idx);
				
				Object component = components[idx.intValue()];
				if (component instanceof Expr && !isNoArgumentType((Expr)component)) {
                                    component = new CastExpander(w, this, (Node) component).castTo(((Expr)component).type(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
				}
				prettyPrint(component, tr);
			} else if (regex.charAt(pos) == '`') {
			    w.write(regex.substring(start, pos));
			    int endpos = pos;
			    while (regex.charAt(++endpos) != '`') { }
			    String optionName = regex.substring(pos + 1, endpos);
                Object optionValue = null;
			    try {
			        Class<?> configClass = Class.forName("x10.Configuration");
			        java.lang.reflect.Field optionField = configClass.getField(optionName);
			        optionValue = optionField.get(null);
                } catch (Exception e) {
                    throw new InternalCompilerError("Template '" + id + "' uses `" + optionName + "`");
                }
                w.write(optionValue.toString());
                pos = endpos;
                start = pos + 1;
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
	
	private boolean isPrimitiveJavaRep(X10ClassDef def) {
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

	private String getJavaRep(X10ClassDef def, boolean boxPrimitives) {
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

	private String getJavaRepParam(X10ClassDef def, int i) {
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

	private String getPropertyInit(Type at, int index) {
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

	private void assertNumberOfInitializers(Type at, int len) {
		at = X10TypeMixin.baseType(at);
		if (at instanceof X10ClassType) {
			X10ClassType act = (X10ClassType) at;
			assert len == act.propertyInitializers().size();
		}
	}

	private boolean printRepType(Type type, boolean printGenerics,
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

	private String rttShortName(X10ClassDef cd) {
		if (cd.isMember() || cd.isLocal())
			return cd.name() + "$RTT";
		else
			return "RTT";
	}

	@Deprecated
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

		if (type instanceof BackingArrayType) {
		    Type base = ((BackingArrayType) type).base();
		    printType(base, 0);
		    w.write("[]");
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
		        w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
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

	@Deprecated
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

	private void getInheritedVirtualMethods(X10ClassType ct,
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

	private void generateDispatcher(X10MethodInstance md, boolean usesClassParam) {
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
/* Remove throw types support
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
*/
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

	@Deprecated
	public void generateRTTMethods(X10ClassDef def) {
		generateRTTMethods(def, false);
	}

	@Deprecated
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
				w.write(" { throw new x10.lang.RuntimeException(); }");
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
							w.write("throw new x10.lang.RuntimeException()");
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

		List<TypeParamNode> typeParameters = n.typeParameters();
        if (typeParameters.size() > 0)
			printTypeParams(n, c, typeParameters);
		
		boolean isDispatch = false;
		if (X10PrettyPrinterVisitor.isSelfDispatch && c.currentClass().flags().isInterface()) {
		    for (int i = 0; i < n.formals().size(); i++) {
		        Type type = n.formals().get(i).type().type();
		        if (containsTypeParam(type)) {
		            isDispatch = true;
		            break;
		        }
		    }
		}
		
        if (isDispatch) {
            w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
	    } else {
            printType(
                      n.returnType().type(),
                      X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS
            );	        
	    }
		w.allowBreak(2, 2, " ", 1);
		if (X10PrettyPrinterVisitor.isGenericOverloading) {
		    w.write(mangleMethodName(n.methodDef(), c.currentClass().flags().isInterface() ? false : true));
		}
		else {
		    tr.print(n, n.name(), w);
		}

		if (!isDispatch && X10TypeMixin.baseType(n.returnType().type()) instanceof ParameterType) {
		    w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
		}
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
		int formalNum = 1;
		for (int i = 0; i < n.formals().size(); i++) {
			Formal f = n.formals().get(i);
			if (!first) {
				w.write(",");
				w.allowBreak(0, " ");
			}
			first = false;

			tr.print(n, f.flags(), w);
			Type type = f.type().type();
			
			if (X10PrettyPrinterVisitor.isSelfDispatch && c.currentClass().flags().isInterface() && containsTypeParam(type)) {
			    printType(type, 0);
			    w.write(" ");
			    Name name = f.name().id();
			    if (name.toString().equals("")) {
			        name = Name.makeFresh("a");
			    }
			    tr.print(n, f.name().id(name), w);

			    w.write(",");
			    w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
			    w.write(" ");
			    Name name1 = Name.make("t" + formalNum++);
			    tr.print(n, f.name().id(name1), w);
			}
			else {
			    printType(
			              type,
			              (n.flags().flags().isStatic() ? X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS : 0) |
			              (boxPrimitives ? X10PrettyPrinterVisitor.BOX_PRIMITIVES: 0)
			    );
			    w.write(" ");
			    Name name = f.name().id();
			    if (name.toString().equals("")) {
			        name = Name.makeFresh("a");
			    }
			    tr.print(n, f.name().id(name), w);
			}
		}

		w.end();
		w.write(")");

		/* Removed throw types.
		 * if (!n.throwTypes().isEmpty()) {
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
*/
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

    public void printTypeParams(Node_c n, Context context, List<TypeParamNode> typeParameters) {
        w.write("<");
        w.begin(0);
        String sep = "";
        for (TypeParamNode tp : typeParameters) {
            w.write(sep);
            n.print(tp, w, tr);
            List<Type> sups = new LinkedList<Type>(tp.upperBounds());
                            
            Type supClassType = null;
            for (Iterator<Type> it = sups.iterator(); it.hasNext();) {
                Type type = X10TypeMixin.baseType(it.next());
                if (type instanceof ParameterType) {
                    it.remove();
                }
                if (type instanceof X10ClassType) {
                    if (!((X10ClassType) type).flags().isInterface()) {
                        if (supClassType != null ) {
                            if (type.isSubtype(supClassType, context)) {
                                supClassType = type;
                            }
                        } else {
                            supClassType = type;
                        }
                        it.remove();
                    }
                }
            }
            if (supClassType != null) {
                sups.add(0, supClassType);
            }
            
            // FIXME
            if (sups.size() > 0) {
                w.write(" extends ");
                for (int i = 0; i < sups.size(); ++i) {
                    if (i != 0) w.write(" & ");
                    printType(sups.get(i), X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS | X10PrettyPrinterVisitor.NO_VARIANCE | X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                }
            }
            sep = ", ";
        }
        w.end();
        w.write(">");
    }
    
    public static String mangleMethodName(MethodDef md, boolean printIncludingGeneric) {
        StringBuilder sb = new StringBuilder(mangleToJava(md.name()));
        List<Ref<? extends Type>> formalTypes = md.formalTypes();
        for (int i = 0; i < formalTypes.size(); ++i) {
            Type type = formalTypes.get(i).get();
            mangleMethodName((ClassType) md.container().get(), sb, i, type, printIncludingGeneric);
        }
        return sb.toString();
    }
    
    public static String mangleMethodName(ClassType ct, MethodInstance mi, boolean printIncludingGeneric) {
        StringBuilder sb = new StringBuilder(mangleToJava(mi.name()));
        List<Type> formalTypes = mi.formalTypes();
        for (int i = 0; i < formalTypes.size(); ++i) {
            Type type = formalTypes.get(i);
            mangleMethodName(ct, sb, i, type, printIncludingGeneric);
        }
        return sb.toString();
    }

    public static void mangleMethodName(ClassType ct, StringBuilder sb, int i, Type type, boolean printIncludingGeneric) {
        Type t = X10TypeMixin.baseType(type);
        if (t instanceof X10ClassType && (printIncludingGeneric || (!printIncludingGeneric && !containsTypeParam(t)))) {
            X10ClassType x10t = (X10ClassType) t;
            if (x10t.typeArguments().size() > 0) {
                sb.append("_");
                sb.append(i);
                sb.append("_");
                List<Type> ts = x10t.typeArguments();
                String delim = null;
                for (Type t1 : ts) {
                    if (delim != null) sb.append(delim);
                    delim = "_";
                    appendParameterizedType(ct, sb, X10TypeMixin.baseType(t1));
                }
            }
        }
        else if (printIncludingGeneric && t instanceof ParameterType) {
            sb.append("_");
            sb.append(i);
            sb.append("_");
            sb.append("$$");
            sb.append(ct.fullName().toString().replace(".", "$"));
            sb.append("_");
            sb.append(((ParameterType) t).name().toString());
        }
    }
    
    private static void appendParameterizedType(ClassType ct, StringBuilder sb, Type t) {
        sb.append("$_");
        if (t instanceof X10ClassType) {
            X10ClassType x10t = (X10ClassType) t;
            sb.append(x10t.fullName().toString().replace(".", "$"));
            if (x10t.typeArguments().size() > 0) {
                List<Type> ts = x10t.typeArguments();
                for (Type t1 : ts) {
                    appendParameterizedType(ct, sb, X10TypeMixin.baseType(t1));
                }
            }
        }
        else if (t instanceof ParameterType) {
            sb.append(ct.fullName().toString().replace(".", "$"));
            sb.append("_");
            sb.append(((ParameterType) t).name().toString());
        }
        else {
            sb.append(t.toString().replace(".", "$"));
        }
        sb.append("_$");
    }

	public static boolean containsTypeParam(Type type) {
	    if (type instanceof ParameterType) {
	        return true;
	    }
	    else if (type instanceof X10ClassType) {
	        List<Type> tas = ((X10ClassType) type).typeArguments();
	        if (tas != null) {
	            for (Type type1 : tas) {
	                if (containsTypeParam(type1)) {
	                    return true;
	                }
	            }
	        }
	    }
        return false;
    }

    public void generateBridgeMethods(X10ClassDef cd) {
	    if (cd.flags().isInterface()) {
	        return;
	    }

	    X10ClassType ct = (X10ClassType) cd.asType();
	    List<MethodInstance> methods;
	    for (MethodDef md : cd.methods()) {
	        if (mangleToJava(md.name()).startsWith("$")) continue;
	        
	        methods = getInstantiatedMethods(ct, md.asInstance());
	        for (MethodInstance mi : methods) {
	            printBridgeMethod(ct, md.asInstance(), mi.def());
	        }
	    }
	    
	    List<MethodInstance> inheriteds = new ArrayList<MethodInstance>();
        List<MethodInstance> overrides = new ArrayList<MethodInstance>();
	    getInheritedMethods(ct, inheriteds, overrides);
	    for (MethodInstance mi : inheriteds) {
	        if (isInstantiate(mi.def().returnType().get(), mi.returnType())) {
	            printInheritedMethodBridge(ct, mi);
	            continue;
	        }
	        for (int i = 0; i < mi.formalTypes().size(); ++ i) {
	            if (
	                    isPrimitive(mi.formalTypes().get(i)) &&
	                    isInstantiate(mi.def().formalTypes().get(i).get(), mi.formalTypes().get(i))
	            ) {
	                printInheritedMethodBridge(ct, mi);
	                break;
	            }
	        }
	        List<MethodInstance> implMethods = new ArrayList<MethodInstance>();
	        List<Type> interfaces = ct.interfaces();
	        getImplMethods(mi, implMethods, interfaces);
	        for (MethodInstance mi2 : implMethods) {
	            printBridgeMethod(ct, mi, mi2.def());
	        }
	    }
	}

    private void getImplMethods(MethodInstance mi, List<MethodInstance> implMethods, List<Type> interfaces) {
            for (Type type : interfaces) {
                if (type instanceof X10ClassType) {
                    List<MethodInstance> imis = ((X10ClassType) type).methods();
                    for (MethodInstance imi : imis) {
                        if (!(imi.name().equals(mi.name()) && imi.formalTypes().size() == mi.formalTypes().size())) continue;

                        if (isContainInstantiateSamePlace(implMethods, imi)) continue;
                        
                        Type returnType = mi.returnType();
                        if (X10PrettyPrinterVisitor.isGenericOverloading) {
                            if (
                                    returnType.typeEquals(imi.returnType() , tr.context())
                                    && isInstantiate(imi.def().returnType().get(), returnType)
                            ) {
                                boolean isContains = false;
                                List<Ref<? extends Type>> types = imi.def().formalTypes();
                                for (int i = 0;i < types.size(); ++i) {
                                    if (
                                            mi.formalTypes().get(i).typeEquals(imi.formalTypes().get(i), tr.context())
                                            && containsTypeParam(imi.def().formalTypes().get(i).get())
                                    ) {
                                        isContains = true;
                                        break;
                                    }
                                }
                                if (!isContains) {
                                    implMethods.add(imi);
                                    break;
                                }
                            }
                        } else {
                            if (
                                    returnType.typeEquals(imi.returnType() , tr.context())
                                    && isInstantiate(imi.def().returnType().get(), returnType)
                            ) {
                                implMethods.add(imi);
                                break;
                            }
                            List<Ref<? extends Type>> types = imi.def().formalTypes();
                            for (int i = 0;i < types.size(); ++i) {
                                if (
                                        mi.formalTypes().get(i).typeEquals(imi.formalTypes().get(i), tr.context())
                                        && isPrimitive(mi.formalTypes().get(i))
                                        && isInstantiate(types.get(i).get(), mi.formalTypes().get(i))
                                ) {
                                    implMethods.add(imi);
                                    break;
                                }
                            }
                        }
                    }
                    getImplMethods(mi, implMethods, ((X10ClassType) type).interfaces());
                }
            }
        }

    private List<MethodInstance> getInstantiatedMethods(X10ClassType ct, MethodInstance mi) {
        	    List<MethodInstance> methods = new ArrayList<MethodInstance>();
        	    for (MethodInstance impled : mi.implemented(tr.context())) {
        	        if (mi.container().typeEquals(impled.container(), tr.context())) continue;

        	        if (X10PrettyPrinterVisitor.isGenericOverloading) {
        	            boolean isContain = false;
        	            for (MethodInstance mi1 : methods) {
        	                if (mi1.def().equals(impled.def())) {
        	                    isContain = true;
        	                }
        	            }
        	            if (isContain) continue;
        	        }
        	        else {
        	            if (isContainInstantiateSamePlace(methods, impled)) continue;
        	        }
        
        	        Type ti = impled.container();
        	        ti = X10TypeMixin.baseType(ti);
        	        
        	        if (ti instanceof X10ClassType && !((X10ClassType) ti).flags().isInterface()) {
        	            if (
        	                    X10PrettyPrinterVisitor.isGenericOverloading
        	                    || (ti.typeEquals(ct.superClass(), tr.context()) || (ct.isMember() && ti.typeEquals(ct.container(), tr.context())))
        	            ) {
        	                Type returnType = mi.returnType();
        	                // instantiate return type
        	                if (
        	                    isInstantiate(impled.def().returnType().get(), returnType)
        	                ) {
        	                    methods.add(impled);
        	                    continue;
        	                }
        	                else {
        	                    List<Ref<? extends Type>> types = impled.def().formalTypes();
        	                    for (int i = 0;i < types.size(); ++i) {
        	                        if (
        	                            (
        	                                    X10PrettyPrinterVisitor.isGenericOverloading
        	                                    && containsTypeParam(types.get(i).get())
        	                            ) 
        	                            || (
        	                                    !X10PrettyPrinterVisitor.isGenericOverloading
        	                                    && isPrimitive(mi.formalTypes().get(i))
        	                                    && isInstantiate(types.get(i).get(), mi.formalTypes().get(i))
        	                            )
        	                        ) {
        	                            methods.add(impled);
        	                            break;
        	                        }
        	                    }
        	                }
        	            }
        	        }
        	        else {
        	            for (Type t:ct.interfaces()) {
        	                if (existMethodInterfaces(t, ti, impled, mi)) {
        	                    methods.add(impled);
        	                    continue;
        	                }
        	            }
        	        }
        	    }
        	    return methods;
        	}

    private static boolean isInstantiate(Type sup, Type t) {
        return X10TypeMixin.baseType(sup) instanceof ParameterType && !(X10TypeMixin.baseType(t) instanceof ParameterType);
    }

    private boolean existMethodInterfaces(Type t, Type type, MethodInstance mi, MethodInstance mdi) {
	    if (t.typeEquals(type, tr.context())) {
	        Type returnType = mdi.returnType();
	        if (
	                isInstantiate(mi.def().returnType().get(), returnType)
	        ) {
	            if (X10PrettyPrinterVisitor.isGenericOverloading) {
	                boolean containsTypeParam = false;
	                List<Ref<? extends Type>> types = mi.def().formalTypes();
	                for (int i = 0;i < types.size(); ++i) {
	                    if (containsTypeParam(types.get(i).get())) {
	                        containsTypeParam = true;
	                        break;
	                    }
	                }
	                if (!containsTypeParam) return true;
	            } else {
	                return true;
	            }
	        }
	        if (!X10PrettyPrinterVisitor.isGenericOverloading) {
	            List<Ref<? extends Type>> types = mi.def().formalTypes();
	            for (int i = 0;i < types.size(); ++i) {
	                if (containsTypeParam(types.get(i).get())) return false;
	                
	                if (
	                        isPrimitive(mdi.formalTypes().get(i))
	                        && isInstantiate(types.get(i).get(), mdi.formalTypes().get(i))
	                ) {
	                    return true;
	                }
	            }
	        }
	    }
	    t = X10TypeMixin.baseType(t);
	    if (t instanceof X10ClassType) {
	        for (Type ti : ((X10ClassType) t).interfaces()) {
	            if (existMethodInterfaces(ti, type, mi, mdi)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}

	private static boolean isPrimitive(Type type) {
	    return type.isBoolean() || type.isNumeric() || type.isChar();
	}

	private void printBridgeMethod(ClassType ct, MethodInstance impl, MethodDef def) {
	    w.write("// bridge for " + def);
	    w.newline();

	    Flags flags = X10Flags.toX10Flags(impl.flags());

	    w.begin(0);
	    w.write(flags.clearAbstract().clearProtected().Public()
	        .clear(X10Flags.SAFE)
	        .clear(X10Flags.NATIVE)
	        .translate()
	    );
        
	    StructType st = def.container().get();
	    
	    if (def instanceof X10MethodDef) {
	        List<ParameterType> tps = ((X10MethodDef) def).typeParameters();
	        if (tps.size() > 0) {
	            w.write("<");
	            String delim = "";
	            for (ParameterType pt : tps) {
	                w.write(delim);
	                delim = ",";
	                w.write(pt.name().toString());
	            }
	            w.write(">");
	            w.write(" ");
	        }
	    }
	    
	    // e.g int m() overrides or implements T m()
	    boolean instantiateReturnType = X10TypeMixin.baseType(def.returnType().get()) instanceof ParameterType;
	    if (instantiateReturnType) {
            printType(impl.returnType(), (X10PrettyPrinterVisitor.isGenericOverloading ? 0 : X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS) | X10PrettyPrinterVisitor.BOX_PRIMITIVES);
	    }
	    else {
            printType(impl.returnType(), (X10PrettyPrinterVisitor.isGenericOverloading ? 0 : X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS) );
	    }

	    boolean isInterface = false;
        if (st instanceof X10ClassType && ((X10ClassType) st).flags().isInterface()) {
            isInterface = true;
	    }
	    
	    w.allowBreak(2, 2, " ", 1);
	    if (X10PrettyPrinterVisitor.isGenericOverloading && !isInterface) {
	        w.write(mangleMethodName(def, true));
	    }
	    else if (X10PrettyPrinterVisitor.isGenericOverloading) {
            w.write(mangleMethodName(def, false));
	    }
	    else {
	        w.write(mangleToJava(impl.name()));
	    }

	    if (instantiateReturnType) {
	        w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
	    }
	    
	    w.write("(");
        boolean first = true;
        
        if (X10PrettyPrinterVisitor.isGenericOverloading && def instanceof X10MethodDef && !isInterface) {
            X10MethodDef x10def = (X10MethodDef) def;
            for (ParameterType p : x10def.typeParameters()) {
                if (!first) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
                first = false;
                
                w.write("final ");
                w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                w.write(Emitter.mangleToJava(p.name()));
            }
        }

	    for (int i = 0; i < def.formalTypes().size(); i++) {
	        Type f = impl.formalTypes().get(i);
	        if (!first || i != 0) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        if (X10TypeMixin.baseType(def.formalTypes().get(i).get()) instanceof ParameterType) {
                printType(f, (X10PrettyPrinterVisitor.isGenericOverloading ? 0 : X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS) | X10PrettyPrinterVisitor.BOX_PRIMITIVES);

	        } else {
                printType(f, X10PrettyPrinterVisitor.isGenericOverloading ? 0 : X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);

	        }
	        w.write(" ");

	        Name name = Name.make("a" + (i + 1));
	        w.write(name.toString());
	    }

	    w.end();
	    w.write(")");

	    /* Remove throw types support
	    if (!impl.throwTypes().isEmpty()) {
	        w.allowBreak(6);
	        w.write("throws ");
	        for (Iterator<Type> i = impl.throwTypes().iterator(); i.hasNext();) {
	            Type t = i.next();
	            printType(t, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);
	            if (i.hasNext()) {
	                w.write(",");
	                w.allowBreak(4, " ");
	            }
	        }
	    }
*/
	    w.write("{");
	    if (!impl.returnType().isVoid()) {
	        w.write("return ");
	    }

	    X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
	    
	    boolean isInterface2 = false;
	    StructType st2 = impl.container();
	    Type bst = X10TypeMixin.baseType(st2);
        if (st2 instanceof X10ClassType) {
	        if (xts.isInterfaceType(bst) || (xts.isFunctionType(bst) && ((X10ClassType) bst).isAnonymous())) {
	            isInterface2 = true;
	        }
	    }
	    
        if (X10PrettyPrinterVisitor.isGenericOverloading && !isInterface2) {
            w.write(mangleMethodName(impl.def(), true));
        }
        else if (X10PrettyPrinterVisitor.isGenericOverloading) {
            w.write(mangleMethodName(ct, impl, false));
        }
        else {
            w.write(mangleToJava(impl.name()));
        }

	    if (X10TypeMixin.baseType(impl.returnType()) instanceof ParameterType) {
	        w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
	    }

	    w.write("(");
	    
	    boolean first2 = true;
	    MethodInstance dmi = def.asInstance();
	    if (dmi instanceof X10MethodInstance) {
	        X10MethodInstance x10mi = (X10MethodInstance) dmi;
	        for (Iterator<Type> i = x10mi.typeParameters().iterator(); i.hasNext(); ) {
	            final Type at = i.next();
	            first2 = false;
	            new RuntimeTypeExpander(this, at).expand(tr);
	            if (i.hasNext()) {
	                w.write(",");
	                w.allowBreak(0, " ");
	            }
	        }
	    }
	    
	    for (int i = 0; i < impl.formalTypes().size(); i++) {
	        Type f = impl.formalTypes().get(i);
	        if (!first2 || i != 0) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        if (isPrimitive(f)) {
	            w.write("(");
	            printType(f, 0);
	            w.write(")");
	        }
	        w.write(" ");

	        Name name = Name.make("a" + (i + 1));
	        w.write(name.toString());
	    }
	    w.write(")");

	    w.write(";");
	    w.write("}");
	    w.newline();
	}

	private void getInheritedMethods(X10ClassType ct, List<MethodInstance> results, List<MethodInstance> overrides) {
            ArrayList<MethodInstance> list = new ArrayList<MethodInstance>(ct.methods());
            list.addAll(results);
            for (MethodInstance mi : list) {
                for (MethodInstance mi2 : mi.overrides(tr.context())) {
                    if (X10PrettyPrinterVisitor.isGenericOverloading || (ct.superClass() != null && mi2.container().typeEquals(ct.superClass(), tr.context()))) {
                        overrides.add(mi2);
                    }
                }
            }
            Type sup = ct.superClass();
            if (sup instanceof X10ClassType) {
                for (MethodInstance mi : ((X10ClassType)(sup)).methods()) {
                    if (!mi.flags().isStatic()) {
                        boolean contains = false;
                        for (MethodInstance mi2 : overrides) {
                            if (mi2.isSameMethod(mi, tr.context())) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            results.add(mi);
                        }
                    }
                }
                getInheritedMethods((X10ClassType) sup, results, overrides);
            }       
        }

    private void printInheritedMethodBridge(ClassType ct, MethodInstance mi) {
    	    MethodDef def = mi.def();
    	    w.write("// bridge for " + def);
    	    w.newline();
    
    	    Flags flags = X10Flags.toX10Flags(mi.flags());
    
    	    w.begin(0);
    	    w.write(flags.clearAbstract()
    	        .clear(X10Flags.SAFE)
    	        .clear(X10Flags.NATIVE)
    	        .translate()
    	    );
    
    	    printType(mi.returnType(), X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);
    
    	    w.allowBreak(2, 2, " ", 1);
    	    if (X10PrettyPrinterVisitor.isGenericOverloading) {
    	        w.write(mangleMethodName(ct, mi, true));
    	    } else {
    	        w.write(mangleToJava(mi.name()));
    	    }
    
    	    if (X10TypeMixin.baseType(mi.returnType()) instanceof ParameterType) {
    	        w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
    	    }
    
    	    w.write("(");
    	    for (int i = 0; i < def.formalTypes().size(); i++) {
    	        Type f = mi.formalTypes().get(i);
    	        if (i != 0) {
    	            w.write(",");
    	            w.allowBreak(0, " ");
    	        }
                printType(f, (X10PrettyPrinterVisitor.isGenericOverloading ? 0 : X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS));
    	        w.write(" ");
    
    	        Name name = Name.make("a" + (i + 1));
    	        w.write(name.toString());
    	    }
    
    	    w.end();
    	    w.write(")");
    
    	    /** Remove throw types support.
    	    if (!mi.throwTypes().isEmpty()) {
    	        w.allowBreak(6);
    	        w.write("throws ");
    	        for (Iterator<Type> i = mi.throwTypes().iterator(); i.hasNext();) {
    	            Type t = i.next();
    	            printType(t, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);
    	            if (i.hasNext()) {
    	                w.write(",");
    	                w.allowBreak(4, " ");
    	            }
    	        }
    	    }
    */
    	    w.write("{");
    	    if (!mi.returnType().isVoid()) {
    	        w.write("return ");
    	    }
    
    	    w.write("super.");
            if (X10PrettyPrinterVisitor.isGenericOverloading) {
                w.write(mangleMethodName(mi.def(), true));
            } else {
                w.write(mangleToJava(mi.name()));
            }
    
    	    if (X10TypeMixin.baseType(def.returnType().get()) instanceof ParameterType) {
    	        w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
    	    }
    
    	    w.write("(");
    	    for (int i = 0; i < mi.formalTypes().size(); i++) {
    	        Type f = mi.formalTypes().get(i);
    	        if (i != 0) {
    	            w.write(",");
    	            w.allowBreak(0, " ");
    	        }
    	        if (isPrimitive(f) && isInstantiate(def.formalTypes().get(i).get(), f)) {
    	            w.write("(");
    	            printType(f, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
    	            w.write(")");
    	        }
    	        w.write(" ");
    
    	        Name name = Name.make("a" + (i + 1));
    	        w.write(name.toString());
    	    }
    	    w.write(")");
    
    	    w.write(";");
    	    w.write("}");
    	    w.newline();
    	}

    private boolean isContainInstantiateSamePlace(List<MethodInstance> methods, MethodInstance impled) {
	    for (MethodInstance mi : methods) {
	        if (
	            !(
	                (X10TypeMixin.baseType(mi.def().returnType().get()) instanceof ParameterType && X10TypeMixin.baseType(impled.def().returnType().get()) instanceof ParameterType)
	                || (!(X10TypeMixin.baseType(mi.def().returnType().get()) instanceof ParameterType) && !(X10TypeMixin.baseType(impled.def().returnType().get()) instanceof ParameterType))
	            )
	        ) {
	            return false;
	        }
	        List<Ref<? extends Type>> types = mi.def().formalTypes();
	        for (int i = 0;i < types.size(); ++i) {
	            if (
	                !(
	                    (X10TypeMixin.baseType(types.get(i).get()) instanceof ParameterType && X10TypeMixin.baseType(impled.def().formalTypes().get(i).get()) instanceof ParameterType))
	                    || (!(X10TypeMixin.baseType(types.get(i).get()) instanceof ParameterType) && !(X10TypeMixin.baseType(impled.def().formalTypes().get(i).get()) instanceof ParameterType))
	            ) {
	                return false;
	            }
	        }
	        return true;
	    }
	    return false;
	}

	@Deprecated
	public void generateRTType(X10ClassDef def) {
		w.newline();

		String mangle = mangle(def.fullName());

		boolean isConstrained = def.classInvariant() != null
				&& !def.classInvariant().get().valid();

		String superClass = "x10.rtt.RuntimeType";

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

		printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES);
		w.write(".class");
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
	
	private boolean isNoArgumentType(Expr e) {
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

            Type expectedBase = expected;
            if (expectedBase instanceof ConstrainedType_c) {
                expectedBase = ((ConstrainedType_c) expectedBase).baseType().get();
            }
            if (actual instanceof ConstrainedType_c) {
                actual = ((ConstrainedType_c) actual).baseType().get();
            }
            CastExpander expander = new CastExpander(w, this, e);
            if (actual.isNull() || e.isConstant() && !(expectedBase instanceof ParameterType) && !(actual instanceof ParameterType)) {
                prettyPrint(e, tr);
            }
            // for primitive
            else if (actual.isBoolean() || actual.isNumeric() || actual.isByte()) {
                if (actual.typeEquals(expectedBase, tr.context())) {
                    if (!isNoArgumentType(e)) {
                        expander = expander.castTo(expectedBase, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                        expander.expand(tr);

                    } else if (e instanceof X10Call && X10TypeMixin.baseType(((X10Call) e).methodInstance().def().returnType().get()) instanceof ParameterType) {
                        expander = expander.castTo(expectedBase, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                        expander.expand(tr);
                    }
                    else {
                        prettyPrint(e, tr);
                    }
                } else {

                    //when the type of e has parameters, cast to actual boxed primitive. 
                    if (!isNoArgumentType(e) || expected instanceof ConstrainedType_c) {
                        expander = expander.castTo(actual, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                        expander = expander.castTo(actual).castTo(expectedBase).castTo(expectedBase, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                        expander.expand(tr);
                    }
                    else {
                        //cast to actual primitive to expected primitive to expected boxed primitive.
                        expander = expander.castTo(actual).castTo(expectedBase);
                        expander.expand(tr);
                    }
                }
            }
            else {
                if (actual.typeEquals(expected, tr.context()) && !(expected instanceof ConstrainedType_c) && !(expectedBase instanceof ParameterType) && !(actual instanceof ParameterType)) {
                    prettyPrint(e, tr);
                }
                else {
                    //cast eagerly
                    expander = expander.castTo(expectedBase, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                    expander.expand(tr);
                }
            }
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
				throw new SemanticException("Expression has more than one "+ name + " annotation.", o.position());
			}
			if (!ats.isEmpty()) {
				X10ClassType at = ats.get(0);
				return at;
			}
		}
		return null;
	}

    public static List<X10ClassType> annotationsNamed(TypeSystem ts, Node o, QName fullName) {
        if (o.ext() instanceof X10Ext) {
            X10Ext ext = (X10Ext) o.ext();
            return ext.annotationNamed(fullName);
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
			if (c.name().id().equals(ClosureCall.APPLY))
				if (ts.isRail(target.type()) || ts.isValRail(target.type()))
					return false;
		}
		return true;
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

	private boolean isUnsignedClassType(Type type) {
	    return type.isNumeric() && !(type.isChar() || type.isByte() || type.isShort() ||
	            type.isInt() || type.isLong() || type.isFloat() || type.isDouble());
	}

	public void generateRTTInstance(X10ClassDef def) {

	    boolean isUnsignedType = isUnsignedClassType(def.asType());
        String unsignedClassName = null;

	    if (isUnsignedType) {
            unsignedClassName = def.asType().name().toString();
	    }
	    
//        if (isUnsignedType) {
//            w.write("public static final x10.rtt."+unsignedClassName+"Type");
//        } else {
            w.write("public static final x10.rtt.RuntimeType");
//        }
        w.write("<");
        printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES | X10PrettyPrinterVisitor.NO_QUALIFIER);
        w.write(">");
        if (isUnsignedType) {
            w.write(" _RTT = new x10.rtt."+unsignedClassName+"Type");
        } else {
            w.write(" _RTT = new x10.rtt.RuntimeType");            
        }
        w.write("<");
        printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES | X10PrettyPrinterVisitor.NO_QUALIFIER);
        w.write(">");
        w.write("(");
        w.newline();
        w.write("/* base class */");
        printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES | X10PrettyPrinterVisitor.NO_QUALIFIER);
        w.write(".class");
        
        for (int i = 0; i < def.variances().size(); i ++) {
            w.write(", ");
            w.newline();
            if (i == 0) w.write("/* variances */ new x10.rtt.RuntimeType.Variance[] {");
            ParameterType.Variance v = def.variances().get(i);
            switch (v) {
            case INVARIANT:
                w.write("x10.rtt.RuntimeType.Variance.INVARIANT");
                break;
            case COVARIANT:
                w.write("x10.rtt.RuntimeType.Variance.COVARIANT");
                break;
            case CONTRAVARIANT:
                w.write("x10.rtt.RuntimeType.Variance.CONTRAVARIANT");
                break;
            }
            if (i == def.variances().size() - 1) w.write("}");
        }
        w.newline();
        
        if (def.interfaces().size() > 0 || def.superType() != null) {
            w.write(", ");
            w.write("/* parents */ new x10.rtt.Type[] {");
            for (int i = 0 ; i < def.interfaces().size(); i ++) {
                if (i != 0) w.write(", ");
                Type type = def.interfaces().get(i).get();
                printParents(def, type);
            }
            if (def.superType() != null) {
                if (def.interfaces().size() != 0) w.write(", ");
                printParents(def, def.superType().get());
            }
            w.write("}");
        }
        w.newline();
        w.write(") {");
        w.write("public String typeName() {");
        w.write("return \"" + def.asType() + "\";");
        w.write("}");
        w.write("};");
        w.newline();
        
        if (!def.flags().isInterface()) {
//            if (isUnsignedType) {
//                w.write("public x10.rtt."+unsignedClassName+"Type<"+unsignedClassName+"> getRTT() {");
//            } else {
                w.write("public x10.rtt.RuntimeType<?> getRTT() {");
//            }
            w.write("return _RTT;");
            w.write("}");
            w.newline();
            w.newline();
            
            if (!def.typeParameters().isEmpty()) {
              w.write("public x10.rtt.Type<?> getParam(int i) {");
              for (int i = 0; i < def.typeParameters().size(); i++) {
                  ParameterType pt = def.typeParameters().get(i);
                  w.write("if (i ==" + i + ")");
                  w.write("return ");
                  w.write(Emitter.mangleToJava(pt.name()));
                  w.write(";");
              }
                w.write("return null;");
                w.write("}");
            }
            w.newline();
        }
	}

	public void generateCustomSerializer(X10ClassDef def) {
	    String fieldName = "__serialized__";
	    w.write("// custom serializer");
	    w.newline();
	    w.write("private transient Object " + fieldName + ";");
        w.newline();
        w.write("private Object writeReplace() { " + fieldName + " = serialize(); return this; }");
        w.newline();
	    w.write("private Object readResolve() { return new ");
        printType(def.asType(), X10PrettyPrinterVisitor.BOX_PRIMITIVES | X10PrettyPrinterVisitor.NO_QUALIFIER);
        w.write("(");
        for (ParameterType type : def.typeParameters()) {
            w.write(type.name().toString() + ", ");
        }
        w.write(fieldName + "); }");
        w.newline();
        w.write("private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {");
        w.newline();
        for (ParameterType type : def.typeParameters()) {
            w.write("oos.writeObject(" + type.name().toString() + ");");
            w.newline();
        }
        w.write("oos.writeObject(" + fieldName + "); }");
        w.newline();
        w.write("private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {");
        w.newline();
        for (ParameterType type : def.typeParameters()) {
            w.write(type.name().toString() + " = (" + X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS + ") ois.readObject();");
            w.newline();
        }
        w.write(fieldName + " = ois.readObject(); }");
        w.newline();
	}

    private void printParents(X10ClassDef def, Type type) {
        if (type instanceof ConstrainedType_c) {
            type = ((ConstrainedType_c) type).baseType().get();
        }
        if (type instanceof X10ClassType) {
            X10ClassType x10Type = (X10ClassType) type;
            if (x10Type.typeArguments().size() > 0) {
                for (int i = 0; i < x10Type.typeArguments().size(); i++) {
                    if (i == 0) { 
                        w.write("new x10.rtt.ParameterizedType(");
                        if (x10Type instanceof FunctionType) {
                            FunctionType ft = (FunctionType) x10Type;
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
                        else {
                            X10ClassDef cd = x10Type.x10Def();
                            if (getJavaRep(cd) != null && getJavaRTTRep(cd) == null) {
                                w.write("new x10.rtt.RuntimeType(");
                                printType(x10Type, 0);
                                w.write(".class");
                                w.write(")");
                            }
                            else {
                                printType(x10Type, 0);
                                w.write("._RTT");
                            }
                        }
                    }
                    w.write(", ");
                    Type ta = x10Type.typeArguments().get(i);
                    if (ta.typeEquals(def.asType(), tr.context())) {
                        w.write("new x10.rtt.UnresolvedType(");
                        w.write("-1");
                        w.write(")");
                    }
                    else if (ta instanceof ParameterType) {
                        w.write("new x10.rtt.UnresolvedType(");
                        w.write("" + getIndex(def.typeParameters(), (ParameterType) ta));
                        w.write(")");
                    } else {
                        printParents(def, ta);
                    }
                    if (i == x10Type.typeArguments().size() - 1) w.write(")");
                }
            } else {
                new RuntimeTypeExpander(this, x10Type).expand(tr);
            }
        }
    }
    
    private int getIndex(List<ParameterType> pts, ParameterType t) {
        for (int i = 0; i < pts.size(); i ++) {
            if (pts.get(i).name().equals(t.name())) {
                return i;
            }
        }
        throw new InternalCompilerError(""); // TODO
    }

    public void generateDispatchMethods(X10ClassDef cd) {
        if (cd.flags().isInterface()) {
            return;
        }
        
        X10ClassType ct = (X10ClassType) cd.asType();
        
        List<MethodInstance> methods = ct.methods();
        Map<MethodInstance, List<MethodInstance>> dispatcherToMyMethods = new HashMap<MethodInstance,List<MethodInstance>>();
        for (MethodInstance myMethod : methods) {
            List<MethodInstance> implementeds = myMethod.implemented(tr.context());
            List<MethodInstance> targets = new ArrayList<MethodInstance>();
            for (MethodInstance implemented : implementeds) {
                if (implemented.def().equals(myMethod.def())) continue;
                
                // only interface
                StructType st = implemented.def().container().get();
                if (st instanceof X10ClassType) {
                    if (!((X10ClassType) st).flags().isInterface()) {
                        continue;
                    }
                }
                
                boolean isContainsTypeParams = false;
                List<Ref<? extends Type>> formalTypes = implemented.def().formalTypes();
                for (Ref<? extends Type> ref : formalTypes) {
                    Type type = ref.get();
                    if (containsTypeParam(type)) {
                        isContainsTypeParams = true;
                        break;
                    }
                }
                if (!isContainsTypeParams) continue;
                
                // only implements by itself not super class's
                List<Type> allInterfaces = new ArrayList<Type>();
                getAllInterfaces(ct.interfaces(), allInterfaces);
                boolean isContainInterfaces = false;
                for (Type type : allInterfaces) {
                    if (type.typeEquals(implemented.container(), tr.context())) {
                        isContainInterfaces = true;
                        break;
                    }
                }
                if (!isContainInterfaces) continue;
                
                if (!isContainSameSignature(targets, implemented)) {
                    targets.add(implemented);
                }
            }
            add(dispatcherToMyMethods, myMethod, targets);
        }
        
        List<MethodInstance> inheriteds = new ArrayList<MethodInstance>();
        List<MethodInstance> overrides = new ArrayList<MethodInstance>();
        getInheritedMethods(ct, inheriteds, overrides);
        for (MethodInstance mi : inheriteds) {
            List<MethodInstance> implMethods = new ArrayList<MethodInstance>();
            List<Type> interfaces = ct.interfaces();
            getImplMethodsForDispatch(mi, implMethods, interfaces);
            add(dispatcherToMyMethods, mi, implMethods);
        }

        Set<Entry<MethodInstance, List<MethodInstance>>> entrySet = dispatcherToMyMethods.entrySet();
        for (Entry<MethodInstance, List<MethodInstance>> entry : entrySet) {
            printDispatchMethod(entry.getKey(), entry.getValue());
        }
    }

    private void add(Map<MethodInstance, List<MethodInstance>> dispatcherToMyMethods, MethodInstance myMethod,
                      List<MethodInstance> targets) {
        for (MethodInstance target : targets) {
            boolean isContainsSameSignature = false;
            Set<Entry<MethodInstance, List<MethodInstance>>> entrySet = dispatcherToMyMethods.entrySet();
            for (Entry<MethodInstance, List<MethodInstance>> entry : entrySet) {
                
                MethodDef md = entry.getKey().def();
                MethodDef td = target.def();
                if (md.name().equals(td.name()) && md.formalTypes().size() == td.formalTypes().size()) {
                    List<Ref<? extends Type>> formalTypes = md.formalTypes();
                    isContainsSameSignature = true;
                    for (int i = 0; i < formalTypes.size(); ++i) {
                        Type ft = formalTypes.get(i).get();
                        Type tt = td.formalTypes().get(i).get();
                        if ((ft instanceof ParameterType && td.formalTypes().get(i).get() instanceof ParameterType)) {}
                        else if (ft instanceof X10ClassType && tt instanceof X10ClassType && ((X10ClassType) ft).name().toString().equals(((X10ClassType) tt).name().toString())) {}
                        else {
                            isContainsSameSignature = false;
                            break;
                        }
                    }
                    if (isContainsSameSignature) {
                        entry.getValue().add(myMethod);
                    }
                }
            }
            if (isContainsSameSignature) break;
            
            ArrayList<MethodInstance> mis = new ArrayList<MethodInstance>();
            mis.add(myMethod);
            dispatcherToMyMethods.put(target, mis);
        }
    }

    private void getImplMethodsForDispatch(MethodInstance mi, List<MethodInstance> implMethods, List<Type> interfaces) {
        for (Type type : interfaces) {
            if (type instanceof X10ClassType) {
                List<MethodInstance> imis = ((X10ClassType) type).methods();
                for (MethodInstance imi : imis) {
                    if (!(imi.name().equals(mi.name()) && imi.formalTypes().size() == mi.formalTypes().size())) continue;
                    if (isContainSameSignature(implMethods, imi)) continue;
                    List<Ref<? extends Type>> types = imi.def().formalTypes();
                    for (int i = 0;i < types.size(); ++i) {
                        if (containsTypeParam(types.get(i).get()) ) {
                            implMethods.add(imi);
                            break;
                        }
                    }
                }
                getImplMethodsForDispatch(mi, implMethods, ((X10ClassType) type).interfaces());
            }
        }
    }
    
    private void getAllInterfaces(List<Type> interfaces, List<Type> allInterfaces) {
        allInterfaces.addAll(interfaces);
        for (Type type : interfaces) {
            if (type instanceof X10ClassType) {
                List<Type> interfaces1 = ((X10ClassType) type).interfaces();
                getAllInterfaces(interfaces1, allInterfaces);
            }
        }
    }

    private boolean isContainSameSignature(List<MethodInstance> targets, MethodInstance mi1) {
        boolean isContain = false;
        for (MethodInstance mi2 : targets) {
            if (mi2.name().equals(mi1.name())) {
                List<Type> formalTypes1 = mi1.formalTypes();
                List<Type> formalTypes2 = mi2.formalTypes();
                if (formalTypes1.size() == formalTypes2.size()) {
                    for (int i = 0; i < formalTypes1.size(); ++i) {
                        Type type1 = formalTypes1.get(i);
                        Type type2 = formalTypes2.get(i);
                        if (type1.typeEquals(type2, tr.context()) || (type1 instanceof ParameterType && type2 instanceof ParameterType)) {
                            isContain = true;
                            break;
                        }
                    }
                }
            }
        }
        return isContain;
    }

    private void printDispatchMethod(MethodInstance dispatch, List<MethodInstance> mis) {
        MethodDef def = dispatch.def();
        w.write("// dispatcher for " + def);
        w.newline();

        Flags flags = X10Flags.toX10Flags(dispatch.flags());

        w.begin(0);
        w.write(flags.clearAbstract()
            .clear(X10Flags.SAFE)
            .clear(X10Flags.NATIVE)
            .translate()
        );
        
        // e.g int m() overrides or implements T m()
        boolean instantiateReturnType = X10TypeMixin.baseType(def.returnType().get()) instanceof ParameterType;
        w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
        
        w.allowBreak(2, 2, " ", 1);
        if (X10PrettyPrinterVisitor.isGenericOverloading) {
            w.write(mangleMethodName(dispatch.def(), false));
        }
        else {
            w.write(mangleToJava(dispatch.name()));
        }
        
        w.write("(");
        
        boolean first = true;
        X10MethodDef x10def = (X10MethodDef) def;
        for (ParameterType p : x10def.typeParameters()) {
            if (!first) {
                w.write(",");
                w.allowBreak(0, " ");
            }
            first = false;
            w.write("final ");
            w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
            w.write(" ");
            w.write(Emitter.mangleToJava(p.name()));
        }
        
        Name[] names = new Name[def.formalTypes().size()];
        for (int i = 0; i < def.formalTypes().size(); i++) {
            Type f = dispatch.formalTypes().get(i);
            if (!first || i != 0) {
                w.write(",");
                w.allowBreak(0, " ");
            }
            Type type = def.formalTypes().get(i).get();
            if (containsTypeParam(type)) {
                w.write("final ");
                if (type instanceof ParameterType) {
                    w.write(X10PrettyPrinterVisitor.JAVA_LANG_OBJECT);
                } else {
                    printType(type, 0);
                }
                
                w.write(" ");

                Name name = Name.make("a" + (i + 1));
                w.write(name.toString());
                
                w.write(",");
                w.write("final ");
                w.write(X10PrettyPrinterVisitor.X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                Name name1 = Name.make("t" + (i + 1));
                w.write(name1.toString());
                
                names[i] = name1;
            } else {
                w.write("final ");
                printType(f, 0);
                
                w.write(" ");

                Name name = Name.make("a" + (i + 1));
                w.write(name.toString());
            }
        }

        w.end();
        w.write(")");
/* Remove throw types support
        if (!dispatch.throwTypes().isEmpty()) {
            w.allowBreak(6);
            w.write("throws ");
            for (Iterator<Type> i = dispatch.throwTypes().iterator(); i.hasNext();) {
                Type t = i.next();
                printType(t, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);
                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(4, " ");
                }
            }
        }
*/
        w.write("{");
        
        for (MethodInstance mi : mis) {
            if (mis.size() != 1) {
                boolean first3 = true;
                for (int i = 0; i < names.length; i++) {
                    Name name = names[i];
                    if (name == null) continue;
                    if (first3) {
                        w.write("if (");
                        first3 = false;
                    }
                    else {
                        w.write(" && ");
                    }

                    w.write(name.toString());
                    w.write(".equals(");
                    new RuntimeTypeExpander(this, mi.formalTypes().get(i)).expand();
                    w.write(")");
                }
                w.write(") {");
            }
            
            if (!mi.returnType().isVoid()) {
                w.write("return ");
            }
            
            if (X10PrettyPrinterVisitor.isGenericOverloading) {
                w.write(mangleMethodName(mi.def(), true));
            }
            else {
                w.write(mangleToJava(mi.name()));
            }
            
            if (X10TypeMixin.baseType(dispatch.returnType()) instanceof ParameterType) {
                w.write(X10PrettyPrinterVisitor.RETURN_PARAMETER_TYPE_SUFFIX);
            }
            
            w.write("(");

            boolean first2 = true;
            X10MethodInstance x10mi = (X10MethodInstance) mi;
            assert (x10mi.typeParameters().size() == x10def.typeParameters().size());
            for (Type t : x10def.typeParameters()) {
                if (!first2) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
                first2 = false;
                new RuntimeTypeExpander(this, t).expand(tr);
            }

            for (int i = 0; i < mi.formalTypes().size(); i++) {
                Type f = mi.formalTypes().get(i);
                if (!first2 || i != 0) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
                if (def.formalTypes().get(i).get() instanceof ParameterType) {
                    Type bf = X10TypeMixin.baseType(f);
                    if (f.isBoolean() || f.isNumeric()) {
                        w.write("(");
                        printType(f, 0);
                        w.write(")");
                        w.write("(");
                        printType(f, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                        w.write(")");
                    } else if (!(bf instanceof ParameterType && ((ParameterType) bf).def().get() instanceof MethodDef)) {
                        w.write("(");
                        printType(f, X10PrettyPrinterVisitor.BOX_PRIMITIVES);
                        w.write(")");
                    }
                }
                
                Name name = Name.make("a" + (i + 1));
                w.write(name.toString());
            }
            w.write(")");
            w.write(";");
            if (mi.returnType().isVoid()) {
                w.write("return null;");
            }
            if (mis.size() != 1) {
                w.write("}");
            }
        }

        if (mis.size() != 1) {
            w.write("throw new x10.lang.Error(\"not implemented dispatch mechanism based on contra-variant type completely\");");
        }
        
        w.write("}");
        w.newline();
    }
}
