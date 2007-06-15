package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import polyglot.ast.*;
import polyglot.ext.x10.ast.*;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.FutureType;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_BinaryTerm;
import polyglot.ext.x10.types.constr.C_BinaryTerm_c;
import polyglot.ext.x10.types.constr.C_EQV;
import polyglot.ext.x10.types.constr.C_EQV_c;
import polyglot.ext.x10.types.constr.C_Field;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Local;
import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Term_c;
import polyglot.ext.x10.types.constr.C_Type;
import polyglot.ext.x10.types.constr.C_Type_c;
import polyglot.ext.x10.types.constr.C_UnaryTerm;
import polyglot.ext.x10.types.constr.C_UnaryTerm_c;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.ext.x10.types.constr.Promise_c;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NullType;
import polyglot.types.Package;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.util.IdentityKey;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.util.UniqueID;

public class X10Dom {
	public <T> T get(AbsLens<T> lens, Element e, String tag, DomReader v) {
		T x = getImpl(lens, e, tag, v);
		if (x == null) {
			if (lens instanceof ListLens) {
				return (T) Collections.EMPTY_LIST;
			}
			if (lens instanceof MapLens) {
				return (T) Collections.EMPTY_MAP;
			}
		}
		return x;
	}

	private <T> T getImpl(AbsLens<T> lens, Element e, String tag, DomReader v) {
		if (e == null) {
			return null;
		}
		Element field = getChild(e, tag);
		if (field == null) {
			return null;
		}
//		if (lens instanceof ListLens) {
//			return lens.fromXML(v, field);
//		}
//		if (lens instanceof MapLens) {
//			return lens.fromXML(v, field);
//		}
		Element child = getFirstElement(field);
		if (child == null) {
			return null;
		}
		return lens.fromXML(v, child);
	}

	public <T> void gen(DomGenerator v, String tag, T n) {
		if (n == null) return;
		toXML(v.tag(tag), n);
	}
	
	public Element getFirstElement(Element e) {
		for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof Element) {
				return (Element) child;
			}
		}
		return null;
//		return (Element) e.getFirstChild();
	}
	
	public Element getChild(Element e, String tag) {
		for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof Element) {
				Element x = (Element) child;
				if (x.getTagName().equals(tag)) {
					return x;
				}
			}
		}
		return null;
	}

	public interface Lens<T extends org.w3c.dom.Node, U> {
		public U fromXML(DomReader v, T e);
		public void toXML(DomGenerator v, U n);
	}

	public interface AbsLens<T> extends Lens<Element,T> { }
	
	public class ListLens<T> implements AbsLens<List<T>> {
		AbsLens<T> lens;
		
		ListLens(AbsLens<T> lens) {
			this.lens = lens;
		}
		
		public List<T> fromXML(DomReader v, Element e) {
			if (e == null) return Collections.<T>emptyList();
			ArrayList<T> r = new ArrayList<T>();
			for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child instanceof Element) {
					Element x = getFirstElement((Element) child);
					T o = lens.fromXML(v, x);
					r.add(o);
				}
			}
			return r;
		}
		
		public void toXML(DomGenerator v, List<T> l) {
			v = v.tag("List");
			for (Iterator<T> i = l.iterator(); i.hasNext(); ) {
				T o = i.next();
				lens.toXML(v.tag("item"), o);
			}
		}
	}

	protected static class Entry<K,V> {
		K k; V v;
		Entry(K k, V v) { this.k = k ; this.v = v; }
	}
	
	public class EntryLens<K,V> implements AbsLens<Entry<K,V>> {
		AbsLens<K> klens;
		AbsLens<V> vlens;
		
		EntryLens(AbsLens<K> klens, AbsLens<V> vlens) {
			this.klens = klens;
			this.vlens = vlens;
		}
		
		public Entry<K,V> fromXML(DomReader r, Element e) {
			K k = get(klens, e, "key", r);
			V v = get(vlens, e, "value", r);
			return new Entry<K,V>(k,v);
		}
		
		public void toXML(DomGenerator v, Entry<K,V> e) {
			klens.toXML(v.tag("key"), e.k);
			vlens.toXML(v.tag("value"), e.v);
		}
	}
	
	public class MapLens<K,V> implements AbsLens<Map<K,V>> {
		AbsLens<K> klens;
		AbsLens<V> vlens;
		
		MapLens(AbsLens<K> klens, AbsLens<V> vlens) {
			this.klens = klens;
			this.vlens = vlens;
		}

		public Map<K,V> fromXML(DomReader v, Element e) {
			if (e == null)
				return Collections.<K,V>emptyMap();
			Map<K,V> r = new HashMap<K,V>();
			for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child instanceof Element) {
					Entry<K,V> x = new EntryLens<K,V>(klens, vlens).fromXML(v, (Element) child);
					r.put(x.k, x.v);
				}
			}
			return r;
		}

		public void toXML(DomGenerator v, Map<K,V> l) {
			for (Iterator<Map.Entry<K,V>> i = l.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry<K,V> e = i.next();
				DomGenerator vi = v.tag("entry");
				new EntryLens<K,V>(klens, vlens).toXML(v, new Entry(e.getKey(), e.getValue()));
			}
		}
	}

	public <T> T getChild(DomReader v, Element e, String tag, AbsLens<T> lens) {
		NodeList l = e.getElementsByTagName(tag);
		if (l.getLength() == 0)
			return null;
		if (l.getLength() != 1)
			throw new InternalCompilerError("More children with tag " + tag + " than expected.");
		return lens.fromXML(v, (Element) l.item(0));
	}

	public class AssignOpLens implements AbsLens<Assign.Operator> {
		public Assign.Operator fromXML(DomReader v, Element e) {
			String s = e.getTagName();
			if (s.equals("assign")) return Assign.ASSIGN;
			if (s.equals("add")) return Assign.ADD_ASSIGN;
			if (s.equals("sub")) return Assign.SUB_ASSIGN;
			if (s.equals("mul")) return Assign.MUL_ASSIGN;
			if (s.equals("div")) return Assign.DIV_ASSIGN;
			if (s.equals("mod")) return Assign.MOD_ASSIGN;
			if (s.equals("bor")) return Assign.BIT_OR_ASSIGN;
			if (s.equals("bxor")) return Assign.BIT_XOR_ASSIGN;
			if (s.equals("band")) return Assign.BIT_AND_ASSIGN;
			if (s.equals("shl")) return Assign.SHL_ASSIGN;
			if (s.equals("shr")) return Assign.SHR_ASSIGN;
			if (s.equals("ushr")) return Assign.USHR_ASSIGN;
			throw new InternalCompilerError("Bad assign operator " + s);
		}

		public void toXML(DomGenerator v, Assign.Operator n) {
			String tag = null;
			if (n == Assign.ASSIGN) tag = "assign";
			if (n == Assign.ADD_ASSIGN) tag = "add";
			if (n == Assign.SUB_ASSIGN) tag = "sub";
			if (n == Assign.MUL_ASSIGN) tag = "mul";
			if (n == Assign.DIV_ASSIGN) tag = "div";
			if (n == Assign.MOD_ASSIGN) tag = "mod";
			if (n == Assign.BIT_OR_ASSIGN) tag = "bor";
			if (n == Assign.BIT_XOR_ASSIGN) tag = "bxor";
			if (n == Assign.BIT_AND_ASSIGN) tag = "band";
			if (n == Assign.SHL_ASSIGN) tag = "shl";
			if (n == Assign.SHR_ASSIGN) tag = "shr";
			if (n == Assign.USHR_ASSIGN) tag = "ushr";
			if (tag == null)
				throw new InternalCompilerError("Bad assign operator " + n);
			v.tag(tag);
		}
	}

	public class BinaryOpLens implements AbsLens<Binary.Operator> {
		public Binary.Operator fromXML(DomReader v, Element e) {
			String s = e.getTagName();
			if (s.equals("add")) return Binary.ADD;
			if (s.equals("sub")) return Binary.SUB;
			if (s.equals("mul")) return Binary.MUL;
			if (s.equals("div")) return Binary.DIV;
			if (s.equals("mod")) return Binary.MOD;
			if (s.equals("cor")) return Binary.COND_OR;
			if (s.equals("cand")) return Binary.COND_AND;
			if (s.equals("bor")) return Binary.BIT_OR;
			if (s.equals("bxor")) return Binary.BIT_XOR;
			if (s.equals("band")) return Binary.BIT_AND;
			if (s.equals("shl")) return Binary.SHL;
			if (s.equals("shr")) return Binary.SHR;
			if (s.equals("ushr")) return Binary.USHR;
			if (s.equals("eq")) return Binary.EQ;
			if (s.equals("ne")) return Binary.NE;
			if (s.equals("gt")) return Binary.GT;
			if (s.equals("ge")) return Binary.GE;
			if (s.equals("lt")) return Binary.LT;
			if (s.equals("le")) return Binary.LE;
			throw new InternalCompilerError("Bad binary operator " + s);
		}

		public void toXML(DomGenerator v, Binary.Operator n) {
			String tag = null;
			if (n == Binary.ADD) tag = "add";
			if (n == Binary.SUB) tag = "sub";
			if (n == Binary.MUL) tag = "mul";
			if (n == Binary.DIV) tag = "div";
			if (n == Binary.MOD) tag = "mod";
			if (n == Binary.EQ) tag = "eq";
			if (n == Binary.NE) tag = "ne";
			if (n == Binary.LT) tag = "lt";
			if (n == Binary.LE) tag = "le";
			if (n == Binary.GT) tag = "gt";
			if (n == Binary.GE) tag = "ge";
			if (n == Binary.BIT_OR) tag = "bor";
			if (n == Binary.BIT_XOR) tag = "bxor";
			if (n == Binary.BIT_AND) tag = "band";
			if (n == Binary.SHL) tag = "shl";
			if (n == Binary.SHR) tag = "shr";
			if (n == Binary.USHR) tag = "ushr";
			if (n == Binary.COND_OR) tag = "cor";
			if (n == Binary.COND_AND) tag = "cand";
			if (tag == null)
				throw new InternalCompilerError("Bad binary operator " + n);
			v.tag(tag);
		}
	}

	public class UnaryOpLens implements AbsLens<Unary.Operator> {
		public Unary.Operator fromXML(DomReader v, Element e) {
			String s = e.getTagName();
			if (s.equals("neg")) return Unary.NEG;
			if (s.equals("not")) return Unary.NOT;
			if (s.equals("comp")) return Unary.BIT_NOT;
			if (s.equals("preinc")) return Unary.PRE_INC;
			if (s.equals("predec")) return Unary.PRE_DEC;
			if (s.equals("postinc")) return Unary.POST_INC;
			if (s.equals("postdec")) return Unary.POST_DEC;
			throw new InternalCompilerError("Bad unary operator " + s);
		}

		public void toXML(DomGenerator v, Unary.Operator n) {
			String tag = null;
			if (n == Unary.NEG) tag = "neg";
			if (n == Unary.NOT) tag = "not";
			if (n == Unary.BIT_NOT) tag = "comp";
			if (n == Unary.PRE_INC) tag = "preinc";
			if (n == Unary.PRE_DEC) tag = "predec";
			if (n == Unary.POST_INC) tag = "postinc";
			if (n == Unary.POST_DEC) tag = "postdec";
			if (tag == null)
				throw new InternalCompilerError("Bad unary operator " + n);
			v.tag(tag);
		}
	}

	public class StringLens implements AbsLens<String> {
		boolean trim;
		
		StringLens() {
			this(true);
		}
		
		StringLens(boolean trim) {
			this.trim = trim;
		}
		
		public String fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			if (trim) {
				s = s.trim();
			}
			return s;
		}
		public void toXML(DomGenerator v, String s) {
			v = v.tag("String");
			v.createText(v.parent, s);
		}
	}

	public class BooleanLens implements AbsLens<Boolean> {
		public Boolean fromXML(DomReader v, Element e) {
			if (e.getTagName().equals("true")) return Boolean.TRUE;
			if (e.getTagName().equals("false")) return Boolean.FALSE;
			return null;
		}

		public void toXML(DomGenerator v, Boolean s) {
			if (s.booleanValue()) {
				v.tag("true");
			}
			else {
				v.tag("false");
			}
		}
	}
	
	public class CharLens implements AbsLens<Character> {
		public Character fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			if (s.length() == 0) return null;
			return s.charAt(0);
		}
		
		public void toXML(DomGenerator v, Character n) {
			v = v.tag("Char");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class ByteLens implements AbsLens<Byte> {
		public Byte fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			try {
				return (byte) Integer.parseInt(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad int " + s);
			}
		}
		
		public void toXML(DomGenerator v, Byte n) {
			v = v.tag("Byte");
			v.createText(v.parent, n.toString());
		}
	}
	
	
	public class ShortLens implements AbsLens<Short> {
		public Short fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			try {
				return (short) Integer.parseInt(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad int " + s);
			}
		}
		
		public void toXML(DomGenerator v, Short n) {
			v = v.tag("Short");
			v.createText(v.parent, n.toString());
		}
	}
	

	public class IntLens implements AbsLens<Integer> {
		public Integer fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			try {
				return Integer.parseInt(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad int " + s);
			}
		}
		
		public void toXML(DomGenerator v, Integer n) {
			v = v.tag("Int");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class LongLens implements AbsLens<Long> {
		public Long fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			try {
				return Long.parseLong(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad long " + s);
			}
		}
		
		public void toXML(DomGenerator v, Long n) {
			v = v.tag("Long");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class FloatLens implements AbsLens<Float> {
		public Float fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			try {
				return Float.parseFloat(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad long " + s);
			}
		}
		
		public void toXML(DomGenerator v, Float n) {
			v = v.tag("Float");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class DoubleLens implements AbsLens<Double> {
		public Double fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			try {
				return Double.parseDouble(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad long " + s);
			}
		}
		
		public void toXML(DomGenerator v, Double n) {
			v = v.tag("Double");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class FlagsLens implements AbsLens<Flags> {
		public Flags fromXML(DomReader v, Element e) {
			Set fs = new HashSet();
			fs.add(Flags.PUBLIC);
			fs.add(Flags.PRIVATE);
			fs.add(Flags.PROTECTED);
			fs.add(Flags.ABSTRACT);
			fs.add(Flags.FINAL);
			fs.add(Flags.INTERFACE);
			fs.add(Flags.NATIVE);
			fs.add(Flags.STATIC);
			fs.add(Flags.STRICTFP);
			fs.add(Flags.SYNCHRONIZED);
			fs.add(Flags.TRANSIENT);
			fs.add(Flags.VOLATILE);
			fs.add(X10Flags.VALUE);
			fs.add(X10Flags.REFERENCE);
			fs.add(X10Flags.ATOMIC);
			fs.add(X10Flags.PURE);
			fs.add(X10Flags.MUTABLE);
			fs.add(X10Flags.SAFE);
			fs.add(X10Flags.LOCAL);
			fs.add(X10Flags.NON_BLOCKING);
			fs.add(X10Flags.SEQUENTIAL);
			Flags f = Flags.NONE;
			NodeList l = e.getElementsByTagName("flag");
			for (int i = 0; i < l.getLength(); i++) {
				org.w3c.dom.Node ni = l.item(i);
				if (ni instanceof org.w3c.dom.Text) {
					String s = ni.getTextContent();
					for (Iterator j = fs.iterator(); j.hasNext(); ) {
						Flags fj = (Flags) j.next();
						if (fj.flags().contains(s)) {
							if (fj instanceof X10Flags) {
								f = ((X10Flags) fj).setX(f);
							}
							else if (f instanceof X10Flags) {
								f = ((X10Flags) f).setX(fj);
							}
							else {
								f = f.set(fj);
							}
							break;
						}
					}
				}
			}
			return f;
		}

		public void toXML(DomGenerator v, Flags n) {
			v.tag("Flags");
			Set names = n.flags();
			for (Iterator i = names.iterator(); i.hasNext(); ) {
				String fname = (String) i.next();
				gen(v, "flag", fname);
			}
		}
	}


	public class PositionLens implements AbsLens<Position> {
		public Position fromXML(DomReader v, Element e) {
			String path = get(new StringLens(), e, "path", v);
			String file = get(new StringLens(), e, "file", v);
			Integer line = get(new IntLens(), e, "line", v);
			Integer column = get(new IntLens(), e, "column", v);
			Integer offset = get(new IntLens(), e, "offset", v);
			Integer endLine = get(new IntLens(), e, "endLine", v);
			Integer endColumn = get(new IntLens(), e, "endColumn", v);
			Integer endOffset = get(new IntLens(), e, "endOffset", v);
			if (line == null)
				line = 0;
			return new Position(path, file, line, column, endLine, endColumn, offset, endOffset);
		}

		public void toXML(DomGenerator v, Position n) {
			v = v.tag("Position");
			gen(v, "path", n.path());
			gen(v, "file", n.file());
			gen(v, "line", n.line());
			gen(v, "column", n.column());
			gen(v, "offset", n.offset());
			gen(v, "endLine", n.endLine());
			gen(v, "endColumn", n.endColumn());
			gen(v, "endOffset", n.endOffset());
		}
	}

	public class ClassTypeLens implements AbsLens<X10ClassType> {
		public X10ClassType fromXML(DomReader v, Element e) {
			Package pkg = get(new PackageLens(), e, "package", v);
			ClassType.Kind kind = (ClassType.Kind) get(new EnumLens("ClassTypeKind", new ClassType.Kind[] {
					ClassType.ANONYMOUS,
					ClassType.LOCAL,
					ClassType.MEMBER,
					ClassType.TOP_LEVEL }), e, "kind", v);
			String name = null;
			if (kind != ClassType.ANONYMOUS) {
				name = get(new StringLens(), e, "name", v);
			}
			if (name != null) {
				Resolver r = ts.systemResolver();
				if (pkg != null) {
					r = ts.packageContextResolver(pkg);
				}
				try {
					Named n = r.find(name);
					if (n instanceof X10ClassType) {
						return (X10ClassType) n;
					}
					throw new InternalCompilerError("Not a class " + name);
				}
				catch (SemanticException ex) {
					// not found
//					throw new InternalCompilerError(ex);
				}
			}
			
			X10ParsedClassType ct = (X10ParsedClassType) ts.createClassType();
			ClassType outer = null;
			if (kind != ClassType.TOP_LEVEL) {
				outer = (ClassType) get(new TypeRefLens(), e, "outer", v);
			}
			String fullName = null;
			
			if (name != null) {
				if (kind == ClassType.TOP_LEVEL) {
					if (pkg != null) {
						fullName = pkg.fullName() + "." + name;
					}
					else {
						fullName = name;
					}
				}
				else if (kind == ClassType.MEMBER && outer != null) {
					fullName = outer.fullName() + "." + name;
				}
				
				ct.name(name);
			}
			
			if (fullName != null) {
				try {
					ts.systemResolver().addNamed(fullName, ct);
				}
				catch (SemanticException ex2) {
					throw new InternalCompilerError(ex2);
				}
			}
			ct.kind(kind);
			
			Flags flags = get(new FlagsLens(), e, "flags", v);
			Type superclass = get(new TypeRefLens(), e, "superclass", v);
			List<Type> interfaces = get(new ListLens<Type>(new TypeRefLens()), e, "interfaces", v);
			List<FieldInstance> fields = get(new ListLens<FieldInstance>(new FieldInstanceLens()), e, "fields", v);
			List<MethodInstance> methods = get(new ListLens<MethodInstance>(new MethodInstanceLens()), e, "methods", v);
			List<ConstructorInstance> constructors = get(new ListLens<ConstructorInstance>(new ConstructorInstanceLens()), e, "constructors", v);
			List<X10ClassType> memberClasses = get(new ListLens<X10ClassType>(new ClassTypeLens()), e, "memberClasses", v);
//			XXX
//			Constraint realClause = get(new ConstraintLens(), e, "realClause");
			ct.outer(outer);
			ct.setFlags(flags);
			ct.package_(pkg);
			ct.superType(superclass);
			ct.setInterfaces(interfaces);
			ct.setFields(fields);
			ct.setMethods(methods);
			ct.setConstructors(constructors);
			ct.setMemberClasses(memberClasses);
			// XXX
//			ct.setRealClause(realClause);
			return ct;
		}

		public void toXML(DomGenerator v, X10ClassType n) {
			v = v.tag("ClassType");
			gen(v, "package", n.package_());
			gen(v, "kind", n.kind());
			if (! n.isAnonymous()) {
				if (n.name() != null) {
					if (! StringUtil.isNameShort(n.name()))
						gen(v, "name", StringUtil.getShortNameComponent(n.name()));
					else
						gen(v, "name", n.name());
				}
			}
			if (n.isNested()) {
				gen(v, "outer", n.outer());
			}
			gen(v, "flags", n.flags());
			gen(v, "superclass", n.superType());
			gen(v, "interfaces", n.interfaces());
			gen(v, "fields", n.fields());
			gen(v, "methods", n.methods());
			gen(v, "constructors", n.constructors());
			gen(v, "memberClasses", n.memberClasses());
			gen(v, "realClause", n.realClause());
		}
	}

	public class ConstructorInstanceLens implements AbsLens<ConstructorInstance> {
		public ConstructorInstance fromXML(DomReader v, Element e) {
			return null;
		}

		public void toXML(DomGenerator v, ConstructorInstance n) {
			v = v.tag("ConstructorInstance");
			gen(v, "flags", n.flags());
			gen(v, "formals", n.formalTypes());
			gen(v, "throws", n.throwTypes());
		}
	}

	public class MethodInstanceLens implements AbsLens<MethodInstance> {
		public MethodInstance fromXML(DomReader v, Element e) {
			return null;
		}

		public void toXML(DomGenerator v, MethodInstance n) {
			v = v.tag("MethodInstance");
			gen(v, "flags", n.flags());
			gen(v, "returnType", n.returnType());
			gen(v, "name", n.name());
			gen(v, "formals", n.formalTypes());
			gen(v, "throws", n.throwTypes());
		}
	}
	
	public interface RefLens<T> extends AbsLens<T> { }
	
	public class ConstructorInstanceRefLens implements RefLens<ConstructorInstance> {
		public ConstructorInstance fromXML(DomReader v, Element e) {
			String name = get(new StringLens(), e, "key", v);
			ConstructorInstance t = (ConstructorInstance) v.typeMap.get(name).force(X10Dom.this);
			if (t != null) {
				return t;
			}
			return null;
		}
		
		public void toXML(DomGenerator v, ConstructorInstance n) {
			v = v.tag("ConstructorInstanceRef");
			addType(v, n, new ConstructorInstanceLens());
		}
	}
	
	
	public class MethodInstanceRefLens implements RefLens<MethodInstance> {
		public MethodInstance fromXML(DomReader v, Element e) {
			String name = get(new StringLens(), e, "key", v);
			MethodInstance t = (MethodInstance) v.typeMap.get(name).force(X10Dom.this);
			if (t != null) {
				return t;
			}
			return null;
		}
		
		public void toXML(DomGenerator v, MethodInstance n) {
			v = v.tag("MethodInstanceRef");
			addType(v, n, new MethodInstanceLens());
		}
	}
	
	public class FieldInstanceRefLens implements RefLens<FieldInstance> {
		public FieldInstance fromXML(DomReader v, Element e) {
			String name = get(new StringLens(), e, "key", v);
			FieldInstance t = (FieldInstance) v.typeMap.get(name).force(X10Dom.this);
			if (t != null) {
				return t;
			}
			return null;
		}
		
		public void toXML(DomGenerator v, FieldInstance n) {
			v = v.tag("FieldInstanceRef");
			addType(v, n, new FieldInstanceLens());
		}
	}
	
	public class PrimitiveLens implements AbsLens<Object> {
		public Object fromXML(DomReader v, Element e) {
			if (e == null) {
				return null;
			}
			
			String tag = e.getTagName();
			if (tag.equals("Boolean")) {
				return new BooleanLens().fromXML(v, e);
			}
			else if (tag.equals("Char")) {
				return new CharLens().fromXML(v, e);
			}
			else if (tag.equals("Byte")) {
				return new ByteLens().fromXML(v, e);
			}
			else if (tag.equals("Short")) {
				return new ShortLens().fromXML(v, e);
			}
			else if (tag.equals("Int")) {
				return new IntLens().fromXML(v, e);
			}
			else if (tag.equals("Long")) {
				return new LongLens().fromXML(v, e);
			}
			else if (tag.equals("Float")) {
				return new FloatLens().fromXML(v, e);
			}
			else if (tag.equals("Double")) {
				return new DoubleLens().fromXML(v, e);
			}
			else if (tag.equals("String")) {
				return new StringLens().fromXML(v, e);
			}
			return null;
		}
		
		public void toXML(DomGenerator v, Object o) {
			if (o instanceof Boolean) {
				new BooleanLens().toXML(v, (Boolean) o);
			}
			else if (o instanceof Character) {
				new CharLens().toXML(v, (Character) o);
			}
			else if (o instanceof Byte) {
				new ByteLens().toXML(v, (Byte) o);
			}
			else if (o instanceof Short) {
				new ShortLens().toXML(v, (Short) o);
			}
			else if (o instanceof Integer) {
				new IntLens().toXML(v, (Integer) o);
			}
			else if (o instanceof Long) {
				new LongLens().toXML(v, (Long) o);
			}
			else if (o instanceof Float) {
				new FloatLens().toXML(v, (Float) o);
			}
			else if (o instanceof Double) {
				new DoubleLens().toXML(v, (Double) o);
			}
			else if (o instanceof String) {
				new StringLens().toXML(v, (String) o);
			}
			else {
				throw new InternalCompilerError("unimplemented lens for " + o + ": " + o.getClass().getName());
			}
		}
	}

	public class FieldInstanceLens implements AbsLens<FieldInstance> {
		public FieldInstance fromXML(DomReader v, Element e) {
			Flags flags = get(new FlagsLens(), e, "flags", v);
			ReferenceType container = (ReferenceType) get(new TypeRefLens(), e, "container", v);
			Type type = get(new TypeRefLens(), e, "type", v);
			String name = get(new StringLens(), e, "name", v);
			Object value = get(new PrimitiveLens(), e, "constant", v);
			FieldInstance fi = ts.fieldInstance(Position.COMPILER_GENERATED, container, flags, type, name);
			if (value != null) {
				fi = fi.constantValue(value);
			}
			return fi;
		}

		public void toXML(DomGenerator v, FieldInstance n) {
			v = v.tag("FieldInstance");
			gen(v, "flags", n.flags());
			gen(v, "container", n.container());
			gen(v, "type", n.type());
			gen(v, "name", n.name());
			if (n.isConstant()) {
				gen(v, "constant", n.constantValue().toString());
			}
		}
	}

	public class LocalInstanceLens implements AbsLens<LocalInstance> {
		public LocalInstance fromXML(DomReader v, Element e) {
			Flags flags = get(new FlagsLens(), e, "flags", v);
			Type type = get(new TypeRefLens(), e, "type", v);
			String name = get(new StringLens(), e, "name", v);
			Object value = get(new PrimitiveLens(), e, "constant", v);
			LocalInstance fi = ts.localInstance(Position.COMPILER_GENERATED, flags, type, name);
			fi = fi.constantValue(value);
			return fi;
		}
		
		public void toXML(DomGenerator v, LocalInstance n) {
			v = v.tag("LocalInstance");
			gen(v, "flags", n.flags());
			gen(v, "type", n.type());
			gen(v, "name", n.name());
			if (n.isConstant()) {
				gen(v, "constant", n.constantValue().toString());
			}
		}
	}
	
	public class LocalInstanceRefLens implements RefLens<LocalInstance> {
		public LocalInstance fromXML(DomReader v, Element e) {
			String name = get(new StringLens(), e, "key", v);
			LocalInstance t = (LocalInstance) v.typeMap.get(name).force(X10Dom.this);
			if (t != null) {
				return t;
			}
			return null;
		}
		
		public void toXML(DomGenerator v, LocalInstance n) {
			v = v.tag("LocalInstanceRef");
			addType(v, n, new LocalInstanceLens());
		}
	}

	public class InstanceofLens implements AbsLens<Instanceof> {
		public Instanceof fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type", v);
			return nf.Instanceof(position, expression, type);
		}

		public void toXML(DomGenerator v, Instanceof n) {
//			v = v.tag("Instanceof");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
			gen(v, "type", n.compareType());
		}
	}

	public class AsyncLens implements AbsLens<Async> {
		public Async fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr place = (Expr) get(new NodeLens(), e, "place", v);
			List<Node> clocks = get(new ListLens<Node>(new NodeLens()), e, "clocks", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			return nf.Async(position, place, clocks, body);
		}

		public void toXML(DomGenerator v, Async n) {
//			v = v.tag("Async");
			gen(v, "position", n.position());
			gen(v, "place", n.place());
			gen(v, "clocks", n.clocks());
			gen(v, "body", n.body());
		}
	}

	public class AtomicLens implements AbsLens<Atomic> {
		public Atomic fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr place = (Expr) get(new NodeLens(), e, "place", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			return nf.Atomic(position, place, body);
		}

		public void toXML(DomGenerator v, Atomic n) {
//			v = v.tag("Atomic");
			gen(v, "position", n.position());
			gen(v, "place", n.place());
			gen(v, "body", n.body());
		}
	}

	public class FutureLens implements AbsLens<Future> {
		public Future fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr place = (Expr) get(new NodeLens(), e, "place", v);
			Expr body = (Expr) get(new NodeLens(), e, "body", v);
			return nf.Future(position, place, body);
		}

		public void toXML(DomGenerator v, Future n) {
//			v = v.tag("Future");
			gen(v, "position", n.position());
			gen(v, "place", n.place());
			gen(v, "body", n.body());
		}
	}
	
	public class FutureNodeLens implements AbsLens<FutureNode> {
		public FutureNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode tn = (TypeNode) get(new NodeLens(), e, "base", v);
			Type t = get(new TypeRefLens(), e, "type", v);
			return (FutureNode) nf.FutureNode(position, tn).type(t);
		}
		
		public void toXML(DomGenerator v, FutureNode n) {
//			v = v.tag("FutureNode");
			gen(v, "position", n.position());
			gen(v, "base", n.base());
			gen(v, "type", n.type());
		}
	}
	
	public class NullableNodeLens implements AbsLens<NullableNode> {
		public NullableNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode tn = (TypeNode) get(new NodeLens(), e, "base", v);
			Type t = get(new TypeRefLens(), e, "type", v);
			return (NullableNode) nf.Nullable(position, tn).type(t);
		}
		
		public void toXML(DomGenerator v, NullableNode n) {
//			v = v.tag("NullableNode");
			gen(v, "position", n.position());
			gen(v, "base", n.base());
			gen(v, "type", n.type());
		}
	}
	
	public class RangeLens implements AbsLens<Range> {
		public Range fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr lb = (Expr) get(new NodeLens(), e, "lowerBound", v);
			Expr ub = (Expr) get(new NodeLens(), e, "upperBound", v);
			Expr stride = (Expr) get(new NodeLens(), e, "stride", v);
			return new Range_c(position, lb, ub, stride);
		}
		
		public void toXML(DomGenerator v, Range n) {
			// v = v.tag("Range");
			gen(v, "position", n.position());
			gen(v, "lowerBound", n.lowerBound());
			gen(v, "upperBound", n.upperBound());
			gen(v, "stride", n.stride());
		}
	}
	
	public class RegionLens implements AbsLens<Region> {
		public Region fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List l = get(new ListLens<Node>(new NodeLens()), e, "ranges", v);
			return new Region_c(position, l);
		}
		
		public void toXML(DomGenerator v, Region n) {
			// v = v.tag("Region");
			gen(v, "position", n.position());
			gen(v, "ranges", n.ranges());
		}
	}

	public class HereLens implements AbsLens<Here> {
		public Here fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			return nf.Here(position);
		}

		public void toXML(DomGenerator v, Here n) {
			// v = v.tag("Here");
			gen(v, "position", n.position());
		}
	}

	public class WhenLens implements AbsLens<When> {
		public When fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			Stmt statement = (Stmt) get(new NodeLens(), e, "statement", v);
			return nf.When(position, expression, statement);
		}

		public void toXML(DomGenerator v, When n) {
			// v = v.tag("When");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
			gen(v, "statement", n.stmt());
		}
	}

	public class NextLens implements AbsLens<Next> {
		public Next fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			return nf.Next(position);
		}

		public void toXML(DomGenerator v, Next n) {
			// v = v.tag("Next");
			gen(v, "position", n.position());
		}
	}

	public class NowLens implements AbsLens<Now> {
		public Now fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr clock = (Expr) get(new NodeLens(), e, "clock", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			return nf.Now(position, clock, body);
		}

		public void toXML(DomGenerator v, Now n) {
			// v = v.tag("Now");
			gen(v, "position", n.position());
			gen(v, "clock", n.clock());
			gen(v, "body", n.body());
		}
	}

	public class ClassDeclLens implements AbsLens<X10ClassDecl> {
		public X10ClassDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			TypeNode superClass = (TypeNode) get(new NodeLens(), e, "superClass", v);
			List interfaces = get(new ListLens<Node>(new NodeLens()), e, "interfaces", v);
			ClassBody body = (ClassBody) get(new NodeLens(), e, "body", v);
			return (X10ClassDecl) nf.ClassDecl(position, flags, name, Collections.EMPTY_LIST, null, superClass, interfaces, body);
		}

		public void toXML(DomGenerator v, X10ClassDecl n) {
			// v = v.tag("ClassDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "name", n.id());
                        // XXX
//			gen(v, "properties", n.properties());
			gen(v, "superClass", n.superClass());
			gen(v, "interfaces", n.interfaces());
			gen(v, "body", n.body());
		}
	}

	public class ValueClassDeclLens implements AbsLens<ValueClassDecl> {
		public ValueClassDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			TypeNode superClass = (TypeNode) get(new NodeLens(), e, "superClass", v);
			List interfaces = get(new ListLens<Node>(new NodeLens()), e, "interfaces", v);
			ClassBody body = (ClassBody) get(new NodeLens(), e, "body", v);
			return nf.ValueClassDecl(position, flags, name, Collections.EMPTY_LIST, null, superClass, interfaces, body);
		}

		public void toXML(DomGenerator v, ValueClassDecl n) {
			// v = v.tag("ValueClassDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "name", n.id());
			gen(v, "superClass", n.superClass());
			gen(v, "interfaces", n.interfaces());
			gen(v, "body", n.body());
		}
	}

	public class AwaitLens implements AbsLens<Await> {
		public Await fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.Await(position, expression);
		}

		public void toXML(DomGenerator v, Await n) {
			// v = v.tag("Await");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class X10ArrayAccess1Lens implements AbsLens<X10ArrayAccess1> {
		public X10ArrayAccess1 fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr array = (Expr) get(new NodeLens(), e, "array", v);
			Expr index = (Expr) get(new NodeLens(), e, "index", v);
			return nf.X10ArrayAccess1(position, array, index);
		}

		public void toXML(DomGenerator v, X10ArrayAccess1 n) {
			// v = v.tag("X10ArrayAccess1");
			gen(v, "position", n.position());
			gen(v, "array", n.array());
			gen(v, "index", n.index());
		}
	}

	public class X10ArrayAccessLens implements AbsLens<X10ArrayAccess> {
		public X10ArrayAccess fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr array = (Expr) get(new NodeLens(), e, "array", v);
			List index = get(new ListLens<Node>(new NodeLens()), e, "index", v);
			return nf.X10ArrayAccess(position, array, index);
		}

		public void toXML(DomGenerator v, X10ArrayAccess n) {
			// v = v.tag("X10ArrayAccess");
			gen(v, "position", n.position());
			gen(v, "array", n.array());
			gen(v, "index", n.index());
		}
	}

	public class ArrayConstructorLens implements AbsLens<ArrayConstructor> {
		public ArrayConstructor fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base", v);
			boolean isSafe = get(new BooleanLens(), e, "isSafe", v);
			boolean isValue = get(new BooleanLens(), e, "isValue", v);
			Expr d = (Expr) get(new NodeLens(), e, "distribution", v);
			Expr i = (Expr) get(new NodeLens(), e, "initializer", v);
			return nf.ArrayConstructor(position, base, ! isSafe, isValue, d, i);
		}

		public void toXML(DomGenerator v, ArrayConstructor n) {
			// v = v.tag("ArrayConstructor");
			gen(v, "position", n.position());
			gen(v, "base", n.arrayBaseType());
			gen(v, "isSafe", n.isSafe());
			gen(v, "isValue", n.isValue());
			gen(v, "distribution", n.distribution());
			gen(v, "initializer", n.initializer());
		}
	}

	public class PointLens implements AbsLens<Point> {
		public Point fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List values = get(new ListLens<Node>(new NodeLens()), e, "values", v);
			return nf.Point(position, values);
		}

		public void toXML(DomGenerator v, Point n) {
			// v = v.tag("Point");
			gen(v, "position", n.position());
			List l = new ArrayList(n.rank());
			for (int i = 0; i < n.rank(); i++) {
				l.add(n.valueAt(i));
			}
			gen(v, "values", l);
		}
	}

	public class RemoteCallLens implements AbsLens<RemoteCall> {
		public RemoteCall fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Receiver target = (Receiver) get(new NodeLens(), e, "target", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.RemoteCall(position, target, name, arguments);
		}

		public void toXML(DomGenerator v, RemoteCall n) {
			// v = v.tag("RemoteCall");
			gen(v, "position", n.position());
			gen(v, "target", n.target());
			gen(v, "name", n.id());
			gen(v, "arguments", n.arguments());
		}
	}

	public class CallLens implements AbsLens<Call> {
		public Call fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Receiver target = (Receiver) get(new NodeLens(), e, "target", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.Call(position, target, name, arguments);
		}

		public void toXML(DomGenerator v, Call n) {
			// v = v.tag("Call");
			gen(v, "position", n.position());
			gen(v, "target", n.target());
			gen(v, "name", n.id());
			gen(v, "arguments", n.arguments());
		}
	}

	public class ConstantDistMakerLens implements AbsLens<ConstantDistMaker> {
		public ConstantDistMaker fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.ConstantDistMaker(position, (Expr) arguments.get(0), (Expr) arguments.get(1));
		}

		public void toXML(DomGenerator v, ConstantDistMaker n) {
			// v = v.tag("ConstantDistMaker");
			gen(v, "position", n.position());
			gen(v, "arguments", n.arguments());
		}
	}

	public class NewLens implements AbsLens<New> {
		public New fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr qualifier = (Expr) get(new NodeLens(), e, "qualifier", v);
			TypeNode objectType = (TypeNode) get(new NodeLens(), e, "objectType", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			ClassBody body = (ClassBody) get(new NodeLens(), e, "body", v);
			return nf.New(position, qualifier, objectType, arguments, body);
		}

		public void toXML(DomGenerator v, New n) {
			// v = v.tag("New");
			gen(v, "position", n.position());
			gen(v, "qualifier", n.qualifier());
			gen(v, "objectType", n.objectType());
			gen(v, "arguments", n.arguments());
			gen(v, "body", n.body());
		}
	}

	public class ForLens implements AbsLens<For> {
		public For fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List inits = get(new ListLens<Node>(new NodeLens()), e, "inits", v);
			Expr condition = (Expr) get(new NodeLens(), e, "condition", v);
			List iters = get(new ListLens<Node>(new NodeLens()), e, "iters", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			return nf.For(position, inits, condition, iters, body);
		}

		public void toXML(DomGenerator v, For n) {
			// v = v.tag("For");
			gen(v, "position", n.position());
			gen(v, "inits", n.inits());
			gen(v, "condition", n.cond());
			gen(v, "iters", n.iters());
			gen(v, "body", n.body());
		}
	}

	public class FinishLens implements AbsLens<Finish> {
		public Finish fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			return nf.Finish(position, body);
		}

		public void toXML(DomGenerator v, Finish n) {
			// v = v.tag("Finish");
			gen(v, "position", n.position());
			gen(v, "body", n.body());
		}
	}
	
	public class DepParameterExprLens implements AbsLens<DepParameterExpr> {
		public DepParameterExpr fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List l = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.DepParameterExpr(position, l);
		}
		
		public void toXML(DomGenerator v, DepParameterExpr n) {
			// v = v.tag("DepParameterExpr");
			gen(v, "position", n.position());
			gen(v, "arguments", n.args());
		}
	}
	
	public class DepCastLens implements AbsLens<DepCast> {
		public DepCast fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode tn = (TypeNode) get(new NodeLens(), e, "castType", v);
			DepParameterExpr dep = (DepParameterExpr) get(new NodeLens(), e, "dep", v);
			Expr expr = (Expr) get(new NodeLens(), e, "expr", v);
			return (DepCast) nf.DepCast(position, tn, dep, expr);
		}
		
		public void toXML(DomGenerator v, DepCast n) {
			// v = v.tag("DepCast");
			gen(v, "position", n.position());
			gen(v, "castType", n.castType());
			gen(v, "dep", n.dep());
			gen(v, "expr", n.expr());
		}
	}

	public class DepInstanceofLens implements AbsLens<DepInstanceof> {
		public DepInstanceof fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode tn = (TypeNode) get(new NodeLens(), e, "compareType", v);
			DepParameterExpr dep = (DepParameterExpr) get(new NodeLens(), e, "dep", v);
			Expr expr = (Expr) get(new NodeLens(), e, "expr", v);
			return (DepInstanceof) nf.DepInstanceof(position, tn, dep, expr);
		}

		public void toXML(DomGenerator v, DepInstanceof n) {
			// v = v.tag("DepInstance");
			gen(v, "position", n.position());
			gen(v, "compareType", n.compareType());
			gen(v, "dep", n.dep());
			gen(v, "expr", n.expr());
		}
	}

	public class GenParameterExprLens implements AbsLens<GenParameterExpr> {
		public GenParameterExpr fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List l = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.GenParameterExpr(position, l);
		}

		public void toXML(DomGenerator v, GenParameterExpr n) {
			// v = v.tag("GenParameterExpr");
			gen(v, "position", n.position());
			gen(v, "arguments", n.args());
		}
	}

	public class X10ArrayTypeNodeLens implements AbsLens<X10ArrayTypeNode> {
		public X10ArrayTypeNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base", v);
			boolean isValueType = get(new BooleanLens(), e, "isValueType", v);
			DepParameterExpr indexedSet = (DepParameterExpr) get(new NodeLens(), e, "indexedSet", v);
			return nf.X10ArrayTypeNode(position, base, isValueType, indexedSet);
		}

		public void toXML(DomGenerator v, X10ArrayTypeNode n) {
			// v = v.tag("X10ArrayTypeNode");
			gen(v, "position", n.position());
			gen(v, "base", n.base());
			gen(v, "isValueType", n.isValueType());
			gen(v, "indexedSet", n.indexedSet());
		}
	}

	public class AssignLens implements AbsLens<Assign> {
		public Assign fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr left = (Expr) get(new NodeLens(), e, "left", v);
			Assign.Operator operator = get(new AssignOpLens(), e, "operator", v);
			Expr right = (Expr) get(new NodeLens(), e, "right", v);
			return nf.Assign(position, left, operator, right);
		}

		public void toXML(DomGenerator v, Assign n) {
			// v = v.tag("Assign");
			gen(v, "position", n.position());
			gen(v, "left", n.left());
			gen(v, "operator", n.operator());
			gen(v, "right", n.right());
		}
	}

	public class X10ArrayAccessAssignLens implements AbsLens<X10ArrayAccessAssign> {
		public X10ArrayAccessAssign fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			X10ArrayAccess left = (X10ArrayAccess) get(new NodeLens(), e, "left", v);
			Assign.Operator operator = get(new AssignOpLens(), e, "operator", v);
			Expr right = (Expr) get(new NodeLens(), e, "right", v);
			return ((X10NodeFactory_c) nf).X10ArrayAccessAssign(position, left, operator, right);
		}

		public void toXML(DomGenerator v, X10ArrayAccessAssign n) {
			// v = v.tag("X10ArrayAccessAssign");
			gen(v, "position", n.position());
			gen(v, "left", n.left());
			gen(v, "operator", n.operator());
			gen(v, "right", n.right());
		}
	}

	public class X10ArrayAccess1AssignLens implements AbsLens<X10ArrayAccess1Assign> {
		public X10ArrayAccess1Assign fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			X10ArrayAccess1 left = (X10ArrayAccess1) get(new NodeLens(), e, "left", v);
			Assign.Operator operator = get(new AssignOpLens(), e, "operator", v);
			Expr right = (Expr) get(new NodeLens(), e, "right", v);
			return ((X10NodeFactory_c) nf).X10ArrayAccess1Assign(position, left, operator, right);
		}

		public void toXML(DomGenerator v, X10ArrayAccess1Assign n) {
			// v = v.tag("X10ArrayAccess1Assign");
			gen(v, "position", n.position());
			gen(v, "left", n.left());
			gen(v, "operator", n.operator());
			gen(v, "right", n.right());
		}
	}

	public class BinaryLens implements AbsLens<Binary> {
		public Binary fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr left = (Expr) get(new NodeLens(), e, "left", v);
			Binary.Operator operator = get(new BinaryOpLens(), e, "operator", v);
			Expr right = (Expr) get(new NodeLens(), e, "right", v);
			return nf.Binary(position, left, operator, right);
		}

		public void toXML(DomGenerator v, Binary n) {
			// v = v.tag("Binary");
			gen(v, "position", n.position());
			gen(v, "left", n.left());
			gen(v, "operator", n.operator());
			gen(v, "right", n.right());
		}
	}

	public class UnaryLens implements AbsLens<Unary> {
		public Unary fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Unary.Operator operator = get(new UnaryOpLens(), e, "operator", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.Unary(position, operator, expression);
		}

		public void toXML(DomGenerator v, Unary n) {
			// v = v.tag("Unary");
			gen(v, "position", n.position());
			gen(v, "operator", n.operator());
			gen(v, "expression", n.expr());
		}
	}

	public class X10ArrayAccessUnaryLens implements AbsLens<X10ArrayAccessUnary> {
		public X10ArrayAccessUnary fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Unary.Operator operator = get(new UnaryOpLens(), e, "operator", v);
			X10ArrayAccess expression = (X10ArrayAccess) get(new NodeLens(), e, "expression", v);
			return ((X10NodeFactory_c) nf).X10ArrayAccessUnary(position, operator, expression);
		}

		public void toXML(DomGenerator v, X10ArrayAccessUnary n) {
			// v = v.tag("X10ArrayAccessUnary");
			gen(v, "position", n.position());
			gen(v, "operator", n.operator());
			gen(v, "expression", n.expr());
		}
	}

	public class X10ArrayAccess1UnaryLens implements AbsLens<X10ArrayAccess1Unary> {
		public X10ArrayAccess1Unary fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Unary.Operator operator = get(new UnaryOpLens(), e, "operator", v);
			X10ArrayAccess1 expression = (X10ArrayAccess1) get(new NodeLens(), e, "expression", v);
			return ((X10NodeFactory_c) nf).X10ArrayAccess1Unary(position, operator, expression);
		}

		public void toXML(DomGenerator v, X10ArrayAccess1Unary n) {
			// v = v.tag("X10ArrayAccess1Unary");
			gen(v, "position", n.position());
			gen(v, "operator", n.operator());
			gen(v, "expression", n.expr());
		}
	}

	public class TupleLens implements AbsLens<Tuple> {
		public Tuple fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Receiver p = (Receiver) get(new NodeLens(), e, "pointReceiver", v);
			Receiver r = (Receiver) get(new NodeLens(), e, "regionReceiver", v);
			List a = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.Tuple(position, p, r, a);
		}

		public void toXML(DomGenerator v, Tuple n) {
			// v = v.tag("Tuple");
			gen(v, "position", n.position());
			gen(v, "pointReceiver", n.pointReceiver());
			gen(v, "regionReceiver", n.regionReceiver());
			gen(v, "arguments", n.arguments());
		}
	}

	public class X10FormalLens implements AbsLens<X10Formal> {
		public X10Formal fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List vars = get(new ListLens<Node>(new NodeLens()), e, "vars", v);
			boolean isUnnamed = get(new BooleanLens(), e, "isUnnamed", v);
			if (isUnnamed) name = null;
			return (X10Formal) nf.X10Formal(position, flags, type, name, vars);
		}

		public void toXML(DomGenerator v, X10Formal n) {
			// v = v.tag("X10Formal");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "type", n.type());
			gen(v, "name", n.id());
			gen(v, "vars", n.vars());
			gen(v, "isUnnamed", n.isUnnamed());
		}
	}

	public class FormalLens implements AbsLens<Formal> {
		public Formal fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			return nf.Formal(position, flags, type, name);
		}

		public void toXML(DomGenerator v, Formal n) {
			// v = v.tag("Formal");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "type", n.type());
			gen(v, "name", n.id());
		}
	}
	
	public class ParExprLens implements AbsLens<ParExpr> {
		public ParExpr fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.ParExpr(position, expression);
		}

		public void toXML(DomGenerator v, ParExpr n) {
			// v = v.tag("ParExpr");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class PlaceCastLens implements AbsLens<PlaceCast> {
		public PlaceCast fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr place = (Expr) get(new NodeLens(), e, "place", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.PlaceCast(position, place, expression);
		}

		public void toXML(DomGenerator v, PlaceCast n) {
			// v = v.tag("PlaceCast");
			gen(v, "position", n.position());
			gen(v, "place", n.placeCastType());
			gen(v, "expression", n.expr());
		}
	}

	public class FieldLens implements AbsLens<polyglot.ast.Field> {
		public polyglot.ast.Field fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Receiver target = (Receiver) get(new NodeLens(), e, "target", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			return nf.Field(position, target, name);
		}

		public void toXML(DomGenerator v, polyglot.ast.Field n) {
			// v = v.tag("Field");
			gen(v, "position", n.position());
			gen(v, "target", n.target());
			gen(v, "name", n.id());
		}
	}

	public class FieldDeclLens implements AbsLens<X10FieldDecl> {
		public X10FieldDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			DepParameterExpr thisClause = (DepParameterExpr) get(new NodeLens(), e, "thisClause", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			Expr init = (Expr) get(new NodeLens(), e, "init", v);
			return (X10FieldDecl) nf.FieldDecl(position, thisClause, flags, type, name, init);
		}

		public void toXML(DomGenerator v, X10FieldDecl n) {
			// v = v.tag("FieldDecl");
			gen(v, "position", n.position());
			gen(v, "thisClause", n.thisClause());
			gen(v, "flags", n.flags());
			gen(v, "type", n.type());
			gen(v, "name", n.id());
			gen(v, "init", n.init());
		}
	}

	public class CastLens implements AbsLens<Cast> {
		public Cast fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.Cast(position, type, expression);
		}

		public void toXML(DomGenerator v, Cast n) {
			// v = v.tag("Cast");
			gen(v, "position", n.position());
			gen(v, "type", n.castType());
			gen(v, "expression", n.expr());
		}
	}
	
	public class AtomicMethodDeclLens implements AbsLens<X10MethodDecl> {
		public X10MethodDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			DepParameterExpr thisClause = (DepParameterExpr) get(new NodeLens(), e, "thisClause", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals", v);
			Expr whereClause = (Expr) get(new NodeLens(), e, "whereClause", v);
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return (X10MethodDecl) nf.AtomicX10MethodDecl(position, thisClause, flags, returnType, name, formals, whereClause, throwTypes, body);
		}
		
		public void toXML(DomGenerator v, X10MethodDecl n) {
			// v = v.tag("MethodDecl");
			gen(v, "position", n.position());
			gen(v, "thisClause", n.thisClause());
			gen(v, "flags", n.flags());
			gen(v, "returnType", n.returnType());
			gen(v, "name", n.id());
			gen(v, "formals", n.formals());
			gen(v, "whereClause", n.whereClause());
			gen(v, "throws", n.throwTypes());
			gen(v, "body", n.body());
		}
	}
	public class X10MethodDeclLens implements AbsLens<X10MethodDecl> {
		public X10MethodDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			DepParameterExpr thisClause = (DepParameterExpr) get(new NodeLens(), e, "thisClause", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals", v);
			Expr whereClause = (Expr) get(new NodeLens(), e, "whereClause", v);
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return (X10MethodDecl) nf.X10MethodDecl(position, thisClause, flags, returnType, name, formals, whereClause, throwTypes, body);
		}
		
		public void toXML(DomGenerator v, X10MethodDecl n) {
			// v = v.tag("MethodDecl");
			gen(v, "position", n.position());
			gen(v, "thisClause", n.thisClause());
			gen(v, "flags", n.flags());
			gen(v, "returnType", n.returnType());
			gen(v, "name", n.id());
			gen(v, "formals", n.formals());
			gen(v, "whereClause", n.whereClause());
			gen(v, "throws", n.throwTypes());
			gen(v, "body", n.body());
		}
	}

	public class MethodDeclLens implements AbsLens<MethodDecl> {
		public MethodDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals", v);
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return nf.MethodDecl(position, flags, returnType, name, formals, throwTypes, body);
		}

		public void toXML(DomGenerator v, MethodDecl n) {
			// v = v.tag("MethodDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "returnType", n.returnType());
			gen(v, "name", n.id());
			gen(v, "formals", n.formals());
			gen(v, "throws", n.throwTypes());
			gen(v, "body", n.body());
		}
	}

	public class LocalDeclLens implements AbsLens<LocalDecl> {
		public LocalDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			Expr init = (Expr) get(new NodeLens(), e, "init", v);
			return nf.LocalDecl(position, flags, type, name, init);
		}

		public void toXML(DomGenerator v, LocalDecl n) {
			// v = v.tag("LocalDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "type", n.type());
			gen(v, "name", n.id());
			gen(v, "init", n.init());
		}
	}

//	public class ConstructorDeclLens implements AbsLens<ConstructorDecl> {
//	public ConstructorDecl fromXML(Element e) {
//	Position position = get(new PositionLens(), e, "position");
//	Flags flags = get(new FlagsLens(), e, "flags");
//	Id name = (Id) get(new NodeLens(), e, "name");
//	TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType");
//	List formals = get(new ListLens<Node>(new NodeLens()), e, "formals");
//	List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws");
//	Block body = (Block) get(new NodeLens(), e, "body");
//	return nf.ConstructorDecl(position, flags, name, returnType, formals, throwTypes, body);
//	}
//
//	public void toXML(DomGenerator v, ConstructorDecl n) {
//	v = v.tag("ConstructorDecl");
//	gen(v, "position", n.position());
//	gen(v, "flags", n.flags());
//	gen(v, "name", n.id());
//	gen(v, "returnType", n.returnType());
//	gen(v, "formals", n.formals());
//	gen(v, "throws", n.throwTypes());
//	gen(v, "body", n.body());
//	}
//	}

	public class X10ConstructorDeclLens implements AbsLens<X10ConstructorDecl> {
		public X10ConstructorDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType", v);
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals", v);
			Expr argWhereClause = (Expr) get(new NodeLens(), e, "argWhereClause", v);
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return (X10ConstructorDecl) nf.ConstructorDecl(position, flags, name, returnType, formals, argWhereClause, throwTypes, body);
		}

		public void toXML(DomGenerator v, X10ConstructorDecl n) {
			// v = v.tag("ConstructorDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "name", n.id());
			gen(v, "returnType", n.returnType());
			gen(v, "formals", n.formals());
//			gen(v, "argWhereClause", n.argWhereClause());
			gen(v, "throws", n.throwTypes());
			gen(v, "body", n.body());
		}
	}
	
	public class PackageLens implements AbsLens<Package> {
		public Package fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			try {
				return ts.packageForName(s);
			}
			catch (SemanticException ex) {
				throw new InternalCompilerError(ex);
			}
		}
		
		public void toXML(DomGenerator v, polyglot.types.Package t) {
			v = v.tag("Package");
			v.createText(v.parent, ((Package) t).fullName());
		}
	}
	
	public class TypeObjectLens implements AbsLens<TypeObject> {
		public TypeObject fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("MethodInstance")) {
				return new MethodInstanceLens().fromXML(v, e);
			}
			if (tag.equals("FieldInstance")) {
				return new FieldInstanceLens().fromXML(v, e);
			}
			if (tag.equals("ConstructorInstance")) {
				return new ConstructorInstanceLens().fromXML(v, e);
			}
			if (tag.equals("LocalInstance")) {
				return new LocalInstanceLens().fromXML(v, e);
			}
			if (tag.equals("ClassType")) {
				return new ClassTypeLens().fromXML(v, e);
			}
			throw new InternalCompilerError("No lens found for " + tag);
		}
		
		public void toXML(DomGenerator v, TypeObject n) {
			assert false;
		}
	}
	
	public class ConstraintLens implements AbsLens<Constraint> {
		public Constraint fromXML(DomReader v, Element e) {
			Boolean consistent = get(new BooleanLens(), e, "consistent", v);
			Boolean placeIsHere = get(new BooleanLens(), e, "placeIsHere", v);
			Boolean placePossiblyNull = get(new BooleanLens(), e, "placePossiblyNull", v);
			Boolean valid = get(new BooleanLens(), e, "valid", v);
			Integer eqvCount = get(new IntLens(), e, "eqvCount", v);
			C_Var selfVar = get(new CVarLens(), e, "selfVar", v);
			C_Term_c placeTerm = (C_Term_c) get(new CTermLens(), e, "placeTerm", v);
			Map<C_Var,Promise> roots = (Map<C_Var,Promise>) get(new MapLens<C_Var,Promise>(new CVarLens(), new PromiseLens()), e, "roots", v);
			Constraint_c n = new Constraint_c(ts, eqvCount, consistent, placeIsHere, placeTerm, placePossiblyNull, roots, valid, selfVar);
			return n;
		}
		
		public void toXML(DomGenerator v, Constraint n) {
			v = v.tag("Constraint");
			gen(v, "eqvCount", n.eqvCount());
			gen(v, "consistent", n.consistent());
			gen(v, "placeIsHere", n.placeIsHere());
			gen(v, "placeTerm", n.placeTerm());
			gen(v, "placePossiblyNull", n.placePossiblyNull());
			gen(v, "roots", n.roots());
			gen(v, "valid", n.valid());
			gen(v, "selfVar", n.selfVar());
		}
	}
	
	public class PromiseLens implements AbsLens<Promise> {
		public Promise fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("C_Here")) return new CHereLens().fromXML(v, e);
			if (tag.equals("C_Type")) return new CTypeLens().fromXML(v, e);
			if (tag.equals("C_Lit")) return new CLitLens().fromXML(v, e);
			if (tag.equals("Promise")) {
				// unimplemented
				C_Var var = get(new CVarLens(), e, "var", v);
				Promise value = get(new PromiseLens(), e, "value", v);
				Map<String,Promise> fields = get(new MapLens<String,Promise>(new StringLens(), new PromiseLens()), e, "var", v);
				return new Promise_c(var, value, fields);
			}
			assert false;
			return null;
		}
		
		public void toXML(DomGenerator v, Promise n) {
			v = v.tag("Promise");
			gen(v, "var", n.term());
			gen(v, "value", n.value());
			gen(v, "fields", n.fields());
		}
	}
	
	public class CVarLens implements AbsLens<C_Var> {
		public C_Var fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("C_EQV")) return new CEQVLens().fromXML(v, e);
			if (tag.equals("C_Field")) return new CFieldLens().fromXML(v, e);
			if (tag.equals("C_Local")) return new CLocalLens().fromXML(v, e);
			if (tag.equals("C_Special")) return new CSpecialLens().fromXML(v, e);
			if (tag.equals("C_Lit")) return new CLitLens().fromXML(v, e);
			assert false;
			return null;
		}
		
		public void toXML(DomGenerator v, C_Var n) {
			assert false;
		}
	}
	
	public class CFieldLens implements AbsLens<C_Field> {
		public C_Field fromXML(DomReader v, Element e) {
			FieldInstance fi = get(new FieldInstanceRefLens(), e, "field", v);
			C_Var receiver = get(new CVarLens(), e, "receiver", v);
			return new C_Field_c(fi,  receiver);
		}
		
		public void toXML(DomGenerator v, C_Field n) {
			v = v.tag("C_Field");
			gen(v, "field", n.fieldInstance());
			gen(v, "receiver", n.receiver());
		}
	}
	
	public class CLocalLens implements AbsLens<C_Local> {
		public C_Local fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("C_EQV")) return new CEQVLens().fromXML(v, e);
			LocalInstance li = get(new LocalInstanceLens(), e, "local", v);
			Boolean isSelf = get(new BooleanLens(), e, "isSelf", v);
			return new C_Local_c(li, isSelf);
		}
		
		public void toXML(DomGenerator v, C_Local n) {
			v = v.tag("C_Local");
			gen(v, "local", n.localInstance());
			gen(v, "isSelf", n.isSelfVar());
		}
	}
	
	public class CSpecialLens implements AbsLens<C_Special> {
		public C_Special fromXML(DomReader v, Element e) {
			Type type = get(new TypeRefLens(), e, "type", v);
			Type qualifier = get(new TypeRefLens(), e, "qualifier", v);
			Boolean isThis = get(new BooleanLens(), e, "this", v);
			Boolean isSuper = get(new BooleanLens(), e, "super", v);
			Boolean isSelf = get(new BooleanLens(), e, "self", v);
			C_Special.C_Kind kind = isThis ? C_Special.THIS :
				(isSuper ? C_Special.SUPER : C_Special.SELF);
			return new C_Special_c(type, qualifier, kind);
		}
		
		public void toXML(DomGenerator v, C_Special n) {
			v = v.tag("C_Special");
			gen(v, "type", n.type());
			gen(v, "qualifier", n.qualifier());
			gen(v, "this", n.kind() == C_Special.THIS);
			gen(v, "super", n.kind() == C_Special.SUPER);
			gen(v, "self", n.kind() == C_Special.SELF);
			assert false;
		}
	}
	
	public class CEQVLens implements AbsLens<C_EQV> {
		public C_EQV fromXML(DomReader v, Element e) {
			LocalInstance li = get(new LocalInstanceLens(), e, "local", v);
			Boolean isSelf = get(new BooleanLens(), e, "isSelf", v);
			Boolean hidden = get(new BooleanLens(), e, "isEQV", v);
			return new C_EQV_c(li, isSelf, hidden);
		}
		
		public void toXML(DomGenerator v, C_EQV n) {
			v = v.tag("C_EQV");
			gen(v, "local", n.localInstance());
			gen(v, "isSelf", n.isSelfVar());
			gen(v, "isEQV", n.isEQV());
		}
	}
	
	public class CUnaryLens implements AbsLens<C_UnaryTerm> {
		public C_UnaryTerm fromXML(DomReader v, Element e) {
			String op = get(new StringLens(), e, "op", v);
			C_Term arg = get(new CTermLens(), e, "arg", v);
			Type type = get(new TypeRefLens(), e, "type", v);
			return new C_UnaryTerm_c(op, arg, type);
		}
		
		public void toXML(DomGenerator v, C_UnaryTerm n) {
			v = v.tag("C_Unary");
			gen(v, "op", n.op());
			gen(v, "arg", n.arg());
			gen(v, "type", n.type());
		}
	}
	
	public class CBinaryLens implements AbsLens<C_BinaryTerm> {
		public C_BinaryTerm fromXML(DomReader v, Element e) {
			String op = get(new StringLens(), e, "op", v);
			C_Term left = get(new CTermLens(), e, "left", v);
			C_Term right = get(new CTermLens(), e, "right", v);
			Type type = get(new TypeRefLens(), e, "type", v);
			return new C_BinaryTerm_c(op, left, right, type);
		}
		
		public void toXML(DomGenerator v, C_BinaryTerm n) {
			v = v.tag("C_Binary");
			gen(v, "op", n.op());
			gen(v, "left", n.left());
			gen(v, "right", n.right());
			gen(v, "type", n.type());
		}
	}
	
	public class CTermLens implements AbsLens<C_Term> {
		public C_Term fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("C_EQV")) return new CEQVLens().fromXML(v, e);
			if (tag.equals("C_Field")) return new CFieldLens().fromXML(v, e);
			if (tag.equals("C_Local")) return new CLocalLens().fromXML(v, e);
			if (tag.equals("C_Special")) return new CSpecialLens().fromXML(v, e);
			if (tag.equals("C_Lit")) return new CLitLens().fromXML(v, e);
			if (tag.equals("C_UnaryTerm")) return new CUnaryLens().fromXML(v, e);
			if (tag.equals("C_BinaryTerm")) return new CBinaryLens().fromXML(v, e);
			if (tag.equals("C_Type")) return new CTypeLens().fromXML(v, e);
			assert false;
			return null;
		}
		
		public void toXML(DomGenerator v, C_Term n) {
			assert false;
		}
	}
	
	public class CTypeLens implements AbsLens<C_Type> {
		public C_Type fromXML(DomReader v, Element e) {
			Type t = get(new TypeRefLens(), e, "type", v);
			return new C_Type_c(t);
		}
		
		public void toXML(DomGenerator v, C_Type n) {
			v = v.tag("C_Type");
			gen(v, "type", n.type());
		}
	}
	
	
	public class CLitLens implements AbsLens<C_Lit> {
		public C_Lit fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("C_Here")) return new CHereLens().fromXML(v, e);
			Object o = get(new ObjectLens(), e, "value", v);
			Type t = get(new TypeRefLens(), e, "type", v);
			return new C_Lit_c(o, t);
		}
		
		public void toXML(DomGenerator v, C_Lit n) {
			if (n instanceof C_Here_c) new CHereLens().toXML(v, (C_Here_c) n);
			v = v.tag("C_Lit");
			gen(v, "value", n.val());
			gen(v, "type", n.type());
		}
	}
	
	public class CHereLens implements AbsLens<C_Here_c> {
		public C_Here_c fromXML(DomReader v, Element e) {
			return new C_Here_c(ts);
		}
		
		public void toXML(DomGenerator v, C_Here_c n) {
			v = v.tag("C_Here");
		}
	}
	
	public class TypeRefLens implements RefLens<Type> {
		public Type fromXML(DomReader v, Element e) {
			if (e == null) {
				return null;
			}
			String tag = e.getTagName();
			if (tag.equals("array")) {
				Type type = get(new TypeRefLens(), e, "base", v);
				return ts.arrayOf(type);
			}
			if (tag.equals("future")) {
				X10NamedType type =(X10NamedType) get(new TypeRefLens(), e, "base", v);
				return ts.createFutureType(Position.COMPILER_GENERATED, type);
			}
			if (tag.equals("nullable")) {
				X10NamedType type = (X10NamedType) get(new TypeRefLens(), e, "base", v);
				return ts.createNullableType(Position.COMPILER_GENERATED, type);
			}
			if (tag.equals("closure")) {
				Type returnType = get(new TypeRefLens(), e, "return", v);
				List<Type> throwsTypes = get(new ListLens<Type>(new TypeRefLens()), e, "throws", v);
				List<Type> argTypes = get(new ListLens<Type>(new TypeRefLens()), e, "args", v);
				return ts.closure(Position.COMPILER_GENERATED, returnType, argTypes, throwsTypes);
			}
			if (tag.equals("null")) {
				return ts.Null();
			}
			if (tag.equals("class")) {
				String name = get(new StringLens(), e, "key", v);
				
				ClassType t = (ClassType) v.typeMap.get(name).force(X10Dom.this);
				if (t != null) {
					return t;
				}
				
//				try {
//					Named n = ts.systemResolver().find(name);
//					if (n instanceof ClassType) {
//						return (ClassType) n;
//					}
//				}
//				catch (SemanticException ex) {
//				}
				
				return null;
			}
			if (tag.equals("externalClass")) {
				String name = get(new StringLens(), e, "name", v);
				try {
					Named n = ts.systemResolver().find(name);
					if (n instanceof ClassType) {
						return (ClassType) n;
					}
				}
				catch (SemanticException ex) {
				}
				return null;
			}
			if (tag.equals("deptype")) {
				X10Type t = (X10Type) get(new TypeRefLens(), e, "root", v);
				DepParameterExpr dep = (DepParameterExpr) get(new NodeLens(), e, "dep", v);
				// XXX
//				Constraint c = (Constraint) get(new ConstraintLens(), e, "clause");
				return t.dep(dep);
			}
			if (tag.equals("void")) {
				return ts.Void();
			}
			if (tag.equals("boolean")) {
				return ts.Boolean();
			}
			if (tag.equals("byte")) {
				return ts.Byte();
			}
			if (tag.equals("short")) {
				return ts.Short();
			}
			if (tag.equals("char")) {
				return ts.Char();
			}
			if (tag.equals("int")) {
				return ts.Int();
			}
			if (tag.equals("long")) {
				return ts.Long();
			}
			if (tag.equals("float")) {
				return ts.Float();
			}
			if (tag.equals("double")) {
				return ts.Double();
			}
			return null;
		}
		
		public void toXMLRoot(DomGenerator v, polyglot.types.Type t) {
			if (t instanceof ArrayType) {
				v = v.tag("array");
				gen(v, "base", ((ArrayType) t).base());
			}
			else if (t instanceof X10ClassType) {
				if (t instanceof ParsedClassType) {
					ParsedClassType pct = (ParsedClassType) t;
					if (pct.job() == null) {
						v = v.tag("externalClass");
						gen(v, "name", ts.getTransformedClassName(pct));
						return;
					}
				}
				v = v.tag("class");
				addType(v, (X10ClassType) t, new ClassTypeLens());
			}
			else if (t instanceof PrimitiveType) {
				v = v.tag(((PrimitiveType) t).name());
			}
			else if (t instanceof FutureType) {
				v = v.tag("future");
				gen(v, "base", ((FutureType) t).base());
			}
			else if (t instanceof NullableType) {
				v = v.tag("nullable");
				gen(v, "base", ((NullableType) t).base());
			}
			else if (t instanceof ClosureType) {
				v = v.tag("closure");
				gen(v, "return", ((ClosureType) t).throwTypes());
				gen(v, "throws", ((ClosureType) t).returnType());
				gen(v, "args", ((ClosureType) t).argumentTypes());
			}
			else if (t instanceof NullType) {
				v = v.tag("null");
			}
			else {
				assert false;
			}
		}
		
		public void toXML(DomGenerator v, polyglot.types.Type t) {
			// v = v.tag("Type");
			if (t instanceof X10Type) {
				X10Type xt = (X10Type) t;
				if (xt.isRootType()) {
					toXMLRoot(v, xt);
				}
				else {
					v = v.tag("deptype");
					gen(v, "root", xt.rootType());
					gen(v, "dep", xt.dep());
					// XXX
//					gen(v, "clause", xt.depClause());
				}
			}
			else {
				toXMLRoot(v, t);
			}
		}
	}

	public class CanonicalTypeNodeLens implements AbsLens<CanonicalTypeNode> {
		public CanonicalTypeNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Type type = (Type) get(new TypeRefLens(), e, "type", v);
			return nf.CanonicalTypeNode(position, type);
		}
		
		public void toXML(DomGenerator v, CanonicalTypeNode n) {
			// v = v.tag("CanonicalTypeNode");
			gen(v, "position", n.position());
			gen(v, "type", n.type());
		}
	}
	
	public class X10CanonicalTypeNodeLens implements AbsLens<X10CanonicalTypeNode> {
		public X10CanonicalTypeNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Type type = (Type) get(new TypeRefLens(), e, "type", v);
			GenParameterExpr gen = (GenParameterExpr) get(new NodeLens(), e, "gen", v);
			DepParameterExpr dep = (DepParameterExpr) get(new NodeLens(), e, "dep", v);
			return nf.X10CanonicalTypeNode(position, type, gen, dep);
		}

		public void toXML(DomGenerator v, X10CanonicalTypeNode n) {
			// v = v.tag("X10CanonicalTypeNode");
			gen(v, "position", n.position());
			gen(v, "type", n.type());
			gen(v, "gen", n.gen());
			gen(v, "dep", n.dep());
		}
	}

	public class PropertyDeclLens implements AbsLens<PropertyDecl> {
		public PropertyDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			return nf.PropertyDecl(position, flags, type, name);
		}

		public void toXML(DomGenerator v, PropertyDecl n) {
			// v = v.tag("PropertyDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "type", n.type());
			gen(v, "name", n.id());
		}
	}

	public class SpecialLens implements AbsLens<Special> {
		public Special fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Special.Kind kind = (Special.Kind) get(new EnumLens("SpecialKind", new Special.Kind[] { Special.SUPER, Special.THIS }), e, "kind", v);
			TypeNode outer = (TypeNode) get(new NodeLens(), e, "qualifier", v);
			return nf.Special(position, kind, outer);
		}

		public void toXML(DomGenerator v, Special n) {
			// v = v.tag("Special");
			gen(v, "position", n.position());
			gen(v, "kind", n.kind().toString());
			gen(v, "qualifier", n.qualifier());
		}
	}

	public class LocalLens implements AbsLens<Local> {
		public Local fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			return nf.Local(position, name);
		}

		public void toXML(DomGenerator v, Local n) {
			// v = v.tag("Local");
			gen(v, "position", n.position());
			gen(v, "name", n.id());
		}
	}

	public class BooleanLitLens implements AbsLens<BooleanLit> {
		public BooleanLit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Boolean value = get(new BooleanLens(), e, "value", v);
			return nf.BooleanLit(position, value);
		}

		public void toXML(DomGenerator v, BooleanLit n) {
			// v = v.tag("BooleanLit");
			gen(v, "position", n.position());
			gen(v, "value", n.value());
		}
	}

	public class StmtSeqLens implements AbsLens<StmtSeq> {
		public StmtSeq fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List statements = get(new ListLens<Node>(new NodeLens()), e, "statements", v);
			return nf.StmtSeq(position, statements);
		}

		public void toXML(DomGenerator v, StmtSeq n) {
			// v = v.tag("StmtSeq");
			gen(v, "position", n.position());
			gen(v, "statements", n.statements());
		}
	}

	public class IfLens implements AbsLens<If> {
		public If fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr condition = (Expr) get(new NodeLens(), e, "condition", v);
			Stmt consequent = (Stmt) get(new NodeLens(), e, "then", v);
			Stmt alternative = (Stmt) get(new NodeLens(), e, "else", v);
			return nf.If(position, condition, consequent, alternative);
		}

		public void toXML(DomGenerator v, If n) {
			// v = v.tag("If");
			gen(v, "position", n.position());
			gen(v, "condition", n.cond());
			gen(v, "then", n.consequent());
			gen(v, "else", n.alternative());
		}
	}

	public class RegionMakerLens implements AbsLens<RegionMaker> {
		public RegionMaker fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.RegionMaker(position, (Expr) arguments.get(0), (Expr) arguments.get(1));
		}

		public void toXML(DomGenerator v, RegionMaker n) {
			// v = v.tag("RegionMaker");
			gen(v, "position", n.position());
			gen(v, "arguments", n.arguments());
		}
	}

	public class RectRegionMakerLens implements AbsLens<RectRegionMaker> {
		public RectRegionMaker fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Receiver receiver = (Receiver) get(new NodeLens(), e, "receiver", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.RectRegionMaker(position, receiver, name, arguments);
		}

		public void toXML(DomGenerator v, RectRegionMaker n) {
			// v = v.tag("RectRegionMaker");
			gen(v, "position", n.position());
			gen(v, "receiver", n.target());
			gen(v, "name", n.id());
			gen(v, "arguments", n.arguments());
		}
	}

	public class WhileLens implements AbsLens<While> {
		public While fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr condition = (Expr) get(new NodeLens(), e, "condition", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			return nf.While(position, condition, body);
		}

		public void toXML(DomGenerator v, While n) {
			// v = v.tag("While");
			gen(v, "position", n.position());
			gen(v, "condition", n.cond());
			gen(v, "body", n.body());
		}
	}

	public class EnumLens implements AbsLens<polyglot.util.Enum> {
                String tag;
		polyglot.util.Enum[] options;

		EnumLens(String tag, polyglot.util.Enum[] options) {
                        this.tag = tag;
			this.options = options;
		}

		public polyglot.util.Enum fromXML(DomReader v, Element e) {
			String kindStr = new StringLens().fromXML(v, e);
			for (int i = 0; i < options.length; i++) {
				if (kindStr.equals(options[i].toString())) {
					return options[i];
				}
			}
			throw new InternalCompilerError("Bad kind " + kindStr);
		}

		public void toXML(DomGenerator v, polyglot.util.Enum n) {
			new StringLens().toXML(v.tag(tag), n.toString());
		}
	}

	public class IntLitLens implements AbsLens<IntLit> {
		public IntLit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			IntLit.Kind kind = (IntLit.Kind) get(new EnumLens("IntLitKind", new IntLit.Kind[] { IntLit.INT, IntLit.LONG }), e, "kind", v);
			long value = get(new LongLens(), e, "value", v);
			return nf.IntLit(position, kind, value);
		}

		public void toXML(DomGenerator v, IntLit n) {
			// v = v.tag("IntLit");
			gen(v, "position", n.position());
			gen(v, "kind", n.kind());
			gen(v, "value", n.value());
		}
	}

	public class StringLitLens implements AbsLens<StringLit> {
		public StringLit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			String value = get(new StringLens(false), e, "value", v);
			return nf.StringLit(position, value);
		}

		public void toXML(DomGenerator v, StringLit n) {
			// v = v.tag("StringLit");
			gen(v, "position", n.position());
			gen(v, "value", n.value());
		}
	}

	public class FloatLitLens implements AbsLens<FloatLit> {
		public FloatLit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			FloatLit.Kind kind = (FloatLit.Kind) get(new EnumLens("FloatLitKind", new FloatLit.Kind[] {
					FloatLit.FLOAT, FloatLit.DOUBLE}), e, "kind", v);
			double value = get(new DoubleLens(), e, "value", v);
			return nf.FloatLit(position, kind, value);
		}

		public void toXML(DomGenerator v, FloatLit n) {
			// v = v.tag("FloatLit");
			gen(v, "position", n.position());
			gen(v, "kind", n.kind());
			gen(v, "value", n.value());
		}
	}

	public class CharLitLens implements AbsLens<CharLit> {
		public CharLit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			char value = get(new CharLens(), e, "value", v);
			return nf.CharLit(position, value);
		}

		public void toXML(DomGenerator v, CharLit n) {
			// v = v.tag("CharLit");
			gen(v, "position", n.position());
			gen(v, "value", n.value());
		}
	}

	public class AssignPropertyCallLens implements AbsLens<AssignPropertyCall> {
		public AssignPropertyCall fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.AssignPropertyCall(position, arguments);
		}

		public void toXML(DomGenerator v, AssignPropertyCall n) {
			// v = v.tag("AssignPropertyCall");
			gen(v, "position", n.position());
			gen(v, "arguments", n.args());
		}
	}
	
	public class AssignPropertyBodyLens implements AbsLens<AssignPropertyBody> {
		public AssignPropertyBody fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List statements = get(new ListLens<Node>(new NodeLens()), e, "statements", v);
			X10ConstructorInstance ci = (X10ConstructorInstance) get(new ConstructorInstanceRefLens(), e, "ci", v);
			List fi = get(new ListLens<FieldInstance>(new FieldInstanceRefLens()), e, "fi", v);
			return nf.AssignPropertyBody(position, statements, ci, fi);
		}
		
		public void toXML(DomGenerator v, AssignPropertyBody n) {
			// v = v.tag("AssignPropertyCall");
			gen(v, "position", n.position());
			gen(v, "statements", n.statements());
			gen(v, "fi", n.fieldInstances());
			gen(v, "ci", n.constructorInstance());
		}
	}

	public class ConditionalLens implements AbsLens<Conditional> {
		public Conditional fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr condition = (Expr) get(new NodeLens(), e, "condition", v);
			Expr consequent = (Expr) get(new NodeLens(), e, "then", v);
			Expr alternative = (Expr) get(new NodeLens(), e, "else", v);
			return nf.Conditional(position, condition, consequent, alternative);
		}

		public void toXML(DomGenerator v, Conditional n) {
			// v = v.tag("Conditional");
			gen(v, "position", n.position());
			gen(v, "condition", n.cond());
			gen(v, "then", n.consequent());
			gen(v, "else", n.alternative());
		}
	}

	public class ConstructorCallLens implements AbsLens<ConstructorCall> {
		public ConstructorCall fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			ConstructorCall.Kind kind = (ConstructorCall.Kind) get(new EnumLens("ConstructorCallKind", new ConstructorCall.Kind[] { ConstructorCall.THIS, ConstructorCall.SUPER }), e, "kind", v);
			Expr outer = (Expr) get(new NodeLens(), e, "outer", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.ConstructorCall(position, kind, outer, arguments);
		}

		public void toXML(DomGenerator v, ConstructorCall n) {
			// v = v.tag("ConstructorCall");
			gen(v, "position", n.position());
			gen(v, "kind", n.kind().toString());
			gen(v, "outer", n.qualifier());
			gen(v, "arguments", n.arguments());
		}
	}

	public class ClosureLens implements AbsLens<Closure> {
		public Closure fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals", v);
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType", v);
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return nf.Closure(position, formals, returnType, throwTypes, body);
		}

		public void toXML(DomGenerator v, Closure n) {
			// v = v.tag("Closure");
			gen(v, "position", n.position());
			gen(v, "formals", n.formals());
			gen(v, "returnType", n.returnType());
			gen(v, "throws", n.throwTypes());
			gen(v, "body", n.body());
		}
	}

	public class ClosureCallLens implements AbsLens<ClosureCall> {
		public ClosureCall fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr target = (Expr) get(new NodeLens(), e, "target", v);
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments", v);
			return nf.ClosureCall(position, target, arguments);
		}

		public void toXML(DomGenerator v, ClosureCall n) {
			// v = v.tag("ClosureCall");
			gen(v, "position", n.position());
			gen(v, "target", n.target());
			gen(v, "arguments", n.arguments());
		}
	}

	public class AnnotationNodeLens implements AbsLens<AnnotationNode> {
		public AnnotationNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode tn = (TypeNode) get(new NodeLens(), e, "type", v);
			return nf.AnnotationNode(position, tn);
		}

		public void toXML(DomGenerator v, AnnotationNode n) {
			// v = v.tag("AnnotationNode");
			gen(v, "position", n.position());
			gen(v, "type", n.annotationType());
		}
	}

	public class IdLens implements AbsLens<Id> {
		public Id fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			String name = get(new StringLens(), e, "name", v);
			return nf.Id(position, name);
		}

		public void toXML(DomGenerator v, Id n) {
			// v = v.tag("Id");
			gen(v, "position", n.position());
			gen(v, "name", n.id());
		}
	}


	public class ArrayAccessLens implements AbsLens<ArrayAccess> {
		public ArrayAccess fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr array = (Expr) get(new NodeLens(), e, "array", v);
			Expr index = (Expr) get(new NodeLens(), e, "index", v);
			return nf.ArrayAccess(position, array, index);
		}

		public void toXML(DomGenerator v, ArrayAccess n) {
			// v = v.tag("ArrayAccess");
			gen(v, "position", n.position());
			gen(v, "array", n.array());
			gen(v, "index", n.index());
		}
	}

	public class ArrayInitLens implements AbsLens<ArrayInit> {
		public ArrayInit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List elements = get(new ListLens<Node>(new NodeLens()), e, "elements", v);
			return nf.ArrayInit(position, elements);
		}

		public void toXML(DomGenerator v, ArrayInit n) {
			// v = v.tag("ArrayInit");
			gen(v, "position", n.position());
			gen(v, "elements", n.elements());
		}
	}

	public class AssertLens implements AbsLens<Assert> {
		public Assert fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr condition = (Expr) get(new NodeLens(), e, "condition", v);
			Expr errorMessage = (Expr) get(new NodeLens(), e, "errorMessage", v);
			return nf.Assert(position, condition, errorMessage);
		}

		public void toXML(DomGenerator v, Assert n) {
			// v = v.tag("Assert");
			gen(v, "position", n.position());
			gen(v, "condition", n.cond());
			gen(v, "errorMessage", n.errorMessage());
		}
	}

	public class LocalAssignLens implements AbsLens<LocalAssign> {
		public LocalAssign fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Local left = (Local) get(new NodeLens(), e, "left", v);
			Assign.Operator operator = get(new AssignOpLens(), e, "operator", v);
			Expr right = (Expr) get(new NodeLens(), e, "right", v);
			return nf.LocalAssign(position, left, operator, right);
		}

		public void toXML(DomGenerator v, LocalAssign n) {
			// v = v.tag("LocalAssign");
			gen(v, "position", n.position());
			gen(v, "left", n.left());
			gen(v, "operator", n.operator());
			gen(v, "right", n.right());
		}
	}

	public class FieldAssignLens implements AbsLens<FieldAssign> {
		public FieldAssign fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Field left = (Field) get(new NodeLens(), e, "left", v);
			Assign.Operator operator = get(new AssignOpLens(), e, "operator", v);
			Expr right = (Expr) get(new NodeLens(), e, "right", v);
			return nf.FieldAssign(position, left, operator, right);
		}

		public void toXML(DomGenerator v, FieldAssign n) {
			// v = v.tag("FieldAssign");
			gen(v, "position", n.position());
			gen(v, "left", n.left());
			gen(v, "operator", n.operator());
			gen(v, "right", n.right());
		}
	}

	public class ArrayAccessAssignLens implements AbsLens<ArrayAccessAssign> {
		public ArrayAccessAssign fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			ArrayAccess left = (ArrayAccess) get(new NodeLens(), e, "left", v);
			Assign.Operator operator = get(new AssignOpLens(), e, "operator", v);
			Expr right = (Expr) get(new NodeLens(), e, "right", v);
			return nf.ArrayAccessAssign(position, left, operator, right);
		}

		public void toXML(DomGenerator v, ArrayAccessAssign n) {
			// v = v.tag("ArrayAccessAssign");
			gen(v, "position", n.position());
			gen(v, "left", n.left());
			gen(v, "operator", n.operator());
			gen(v, "right", n.right());
		}
	}

	public class BlockLens implements AbsLens<Block> {
		public Block fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List statements = get(new ListLens<Node>(new NodeLens()), e, "statements", v);
			return nf.Block(position, statements);
		}

		public void toXML(DomGenerator v, Block n) {
			// v = v.tag("Block");
			gen(v, "position", n.position());
			gen(v, "statements", n.statements());
		}
	}

	public class SwitchBlockLens implements AbsLens<SwitchBlock> {
		public SwitchBlock fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List statements = get(new ListLens<Node>(new NodeLens()), e, "statements", v);
			return nf.SwitchBlock(position, statements);
		}

		public void toXML(DomGenerator v, SwitchBlock n) {
			// v = v.tag("SwitchBlock");
			gen(v, "position", n.position());
			gen(v, "statements", n.statements());
		}
	}

	public <T extends polyglot.util.Enum> T getEnum(Element e, String tag, T[] options, DomReader v) {
		String kindStr = get(new StringLens(), e, tag, v);
		for (int i = 0; i < options.length; i++) {
			if (kindStr.equals(options[i].toString())) {
				return options[i];
			}
		}
		throw new InternalCompilerError("Bad kind " + kindStr);
	}

	public class BranchLens implements AbsLens<Branch> {
		public Branch fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Branch.Kind kind = (Branch.Kind) get(new EnumLens("BranchKind", new Branch.Kind[] { Branch.BREAK, Branch.CONTINUE }), e, "kind", v);
			Id label = (Id) get(new NodeLens(), e, "label", v);
			return nf.Branch(position, kind, label);
		}

		public void toXML(DomGenerator v, Branch n) {
			// v = v.tag("Branch");
			gen(v, "position", n.position());
			gen(v, "kind", n.kind().toString());
			gen(v, "label", n.labelNode());
		}
	}

	public class CaseLens implements AbsLens<Case> {
		public Case fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.Case(position, expression);
		}

		public void toXML(DomGenerator v, Case n) {
			// v = v.tag("Case");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class CatchLens implements AbsLens<Catch> {
		public Catch fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Formal formal = (Formal) get(new NodeLens(), e, "formal", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return nf.Catch(position, formal, body);
		}

		public void toXML(DomGenerator v, Catch n) {
			// v = v.tag("Catch");
			gen(v, "position", n.position());
			gen(v, "formal", n.formal());
			gen(v, "body", n.body());
		}
	}

	public class ClassBodyLens implements AbsLens<ClassBody> {
		public ClassBody fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List members = get(new ListLens<Node>(new NodeLens()), e, "members", v);
			return nf.ClassBody(position, members);
		}

		public void toXML(DomGenerator v, ClassBody n) {
			// v = v.tag("ClassBody");
			gen(v, "position", n.position());
			gen(v, "members", n.members());
		}
	}

	public class ClassLitLens implements AbsLens<ClassLit> {
		public ClassLit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode typeNode = (TypeNode) get(new NodeLens(), e, "typeNode", v);
			return nf.ClassLit(position, typeNode);
		}

		public void toXML(DomGenerator v, ClassLit n) {
			// v = v.tag("ClassLit");
			gen(v, "position", n.position());
			gen(v, "typeNode", n.typeNode());
		}
	}

	public class ConstructorDeclLens implements AbsLens<ConstructorDecl> {
		public ConstructorDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			Id name = (Id) get(new NodeLens(), e, "name", v);
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals", v);
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return nf.ConstructorDecl(position, flags, name, formals, throwTypes, body);
		}

		public void toXML(DomGenerator v, ConstructorDecl n) {
			// v = v.tag("ConstructorDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "name", n.id());
			gen(v, "formals", n.formals());
			gen(v, "throws", n.throwTypes());
			gen(v, "body", n.body());
		}
	}
	
	public class AtEachLens implements AbsLens<AtEach> {
		public AtEach fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Formal formal = (Formal) get(new NodeLens(), e, "formal", v);
			Expr domain = (Expr) get(new NodeLens(), e, "domain", v);
			List clocks = get(new ListLens<Node>(new NodeLens()), e, "clocks", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return (AtEach) nf.AtEach(position, formal, domain, clocks, body);
		}
		
		public void toXML(DomGenerator v, AtEach n) {
			// v = v.tag("AtEach");
			gen(v, "position", n.position());
			gen(v, "formal", n.formal());
			gen(v, "domain", n.domain());
			gen(v, "clocks", n.clocks());
			gen(v, "body", n.body());
		}
	}
	
	public class ForEachLens implements AbsLens<ForEach> {
		public ForEach fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Formal formal = (Formal) get(new NodeLens(), e, "formal", v);
			Expr domain = (Expr) get(new NodeLens(), e, "domain", v);
			List clocks = get(new ListLens<Node>(new NodeLens()), e, "clocks", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return (ForEach) nf.ForEach(position, formal, domain, clocks, body);
		}
		
		public void toXML(DomGenerator v, ForEach n) {
			// v = v.tag("ForEach");
			gen(v, "position", n.position());
			gen(v, "formal", n.formal());
			gen(v, "domain", n.domain());
			gen(v, "clocks", n.clocks());
			gen(v, "body", n.body());
		}
	}
	
	public class ForLoopLens implements AbsLens<ForLoop> {
		public ForLoop fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Formal formal = (Formal) get(new NodeLens(), e, "formal", v);
			Expr domain = (Expr) get(new NodeLens(), e, "domain", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return (ForLoop) nf.ForLoop(position, formal, domain, body);
		}
		
		public void toXML(DomGenerator v, ForLoop n) {
			// v = v.tag("ForLoop");
			gen(v, "position", n.position());
			gen(v, "formal", n.formal());
			gen(v, "domain", n.domain());
			gen(v, "body", n.body());
		}
	}

	public class DoLens implements AbsLens<Do> {
		public Do fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			Expr condition = (Expr) get(new NodeLens(), e, "condition", v);
			return nf.Do(position, body, condition);
		}

		public void toXML(DomGenerator v, Do n) {
			// v = v.tag("Do");
			gen(v, "position", n.position());
			gen(v, "body", n.body());
			gen(v, "condition", n.cond());
		}
	}

	public class EmptyLens implements AbsLens<Empty> {
		public Empty fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			return nf.Empty(position);
		}

		public void toXML(DomGenerator v, Empty n) {
			// v = v.tag("Empty");
			gen(v, "position", n.position());
		}
	}

	public class EvalLens implements AbsLens<Eval> {
		public Eval fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.Eval(position, expression);
		}

		public void toXML(DomGenerator v, Eval n) {
			// v = v.tag("Eval");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class ImportLens implements AbsLens<Import> {
		public Import fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Import.Kind kind = (Import.Kind) get(new EnumLens("ImportKind", new Import.Kind[] { Import.PACKAGE, Import.CLASS }), e, "kind", v);
			String name = get(new StringLens(), e, "name", v);
			return nf.Import(position, kind, name);
		}

		public void toXML(DomGenerator v, Import n) {
			// v = v.tag("Import");
			gen(v, "position", n.position());
			gen(v, "kind", n.kind());
			gen(v, "name", n.name());
		}
	}

	public class InitializerLens implements AbsLens<Initializer> {
		public Initializer fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Flags flags = get(new FlagsLens(), e, "flags", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return nf.Initializer(position, flags, body);
		}

		public void toXML(DomGenerator v, Initializer n) {
			// v = v.tag("Initializer");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "body", n.body());
		}
	}

	public class LabeledLens implements AbsLens<Labeled> {
		public Labeled fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Id label = (Id) get(new NodeLens(), e, "label", v);
			Stmt body = (Stmt) get(new NodeLens(), e, "body", v);
			return nf.Labeled(position, label, body);
		}

		public void toXML(DomGenerator v, Labeled n) {
			// v = v.tag("Labeled");
			gen(v, "position", n.position());
			gen(v, "label", n.labelNode());
			gen(v, "body", n.statement());
		}
	}

	public class LocalClassDeclLens implements AbsLens<LocalClassDecl> {
		public LocalClassDecl fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			ClassDecl decl = (ClassDecl) get(new NodeLens(), e, "decl", v);
			return nf.LocalClassDecl(position, decl);
		}

		public void toXML(DomGenerator v, LocalClassDecl n) {
			// v = v.tag("LocalClassDecl");
			gen(v, "position", n.position());
			gen(v, "decl", n.decl());
		}
	}

	public class NewArrayLens implements AbsLens<NewArray> {
		public NewArray fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base", v);
			List dims = get(new ListLens<Node>(new NodeLens()), e, "dims", v);
			int addDims = get(new IntLens(), e, "addDims", v);
			ArrayInit init = (ArrayInit) get(new NodeLens(), e, "init", v);
			return nf.NewArray(position, base, dims, addDims, init);
		}

		public void toXML(DomGenerator v, NewArray n) {
			// v = v.tag("NewArray");
			gen(v, "position", n.position());
			gen(v, "base", n.baseType());
			gen(v, "dims", n.dims());
			gen(v, "addDims", n.additionalDims());
			gen(v, "init", n.init());
		}
	}

	public class NullLitLens implements AbsLens<NullLit> {
		public NullLit fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			return nf.NullLit(position);
		}

		public void toXML(DomGenerator v, NullLit n) {
			// v = v.tag("NullLit");
			gen(v, "position", n.position());
		}
	}

	public class ReturnLens implements AbsLens<Return> {
		public Return fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.Return(position, expression);
		}

		public void toXML(DomGenerator v, Return n) {
			// v = v.tag("Return");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class SourceCollectionLens implements AbsLens<SourceCollection> {
		public SourceCollection fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			List sources = get(new ListLens<Node>(new NodeLens()), e, "sources", v);
			return nf.SourceCollection(position, sources);
		}

		public void toXML(DomGenerator v, SourceCollection n) {
			// v = v.tag("SourceCollection");
			gen(v, "position", n.position());
			gen(v, "sources", n.sources());
		}
	}

	public class SourceFileLens implements AbsLens<SourceFile> {
		public SourceFile fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			PackageNode packageName = (PackageNode) get(new NodeLens(), e, "package", v);
			List imports = get(new ListLens<Node>(new NodeLens()), e, "imports", v);
			List decls = get(new ListLens<Node>(new NodeLens()), e, "decls", v);
			return nf.SourceFile(position, packageName, imports, decls);
		}

		public void toXML(DomGenerator v, SourceFile n) {
			// v = v.tag("SourceFile");
			gen(v, "position", n.position());
			gen(v, "package", n.package_());
			gen(v, "imports", n.imports());
			gen(v, "decls", n.decls());
		}
	}

	public class SwitchLens implements AbsLens<Switch> {
		public Switch fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			List elements = get(new ListLens<Node>(new NodeLens()), e, "elements", v);
			return nf.Switch(position, expression, elements);
		}

		public void toXML(DomGenerator v, Switch n) {
			// v = v.tag("Switch");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
			gen(v, "elements", n.elements());
		}
	}

	public class SynchronizedLens implements AbsLens<Synchronized> {
		public Synchronized fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			Block body = (Block) get(new NodeLens(), e, "body", v);
			return nf.Synchronized(position, expression, body);
		}

		public void toXML(DomGenerator v, Synchronized n) {
			// v = v.tag("Synchronized");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
			gen(v, "body", n.body());
		}
	}

	public class ThrowLens implements AbsLens<Throw> {
		public Throw fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Expr expression = (Expr) get(new NodeLens(), e, "expression", v);
			return nf.Throw(position, expression);
		}

		public void toXML(DomGenerator v, Throw n) {
			// v = v.tag("Throw");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class TryLens implements AbsLens<Try> {
		public Try fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			Block tryBlock = (Block) get(new NodeLens(), e, "tryBlock", v);
			List catchBlocks = get(new ListLens<Node>(new NodeLens()), e, "catchBlocks", v);
			Block finallyBlock = (Block) get(new NodeLens(), e, "finallyBlock", v);
			return nf.Try(position, tryBlock, catchBlocks, finallyBlock);
		}

		public void toXML(DomGenerator v, Try n) {
			// v = v.tag("Try");
			gen(v, "position", n.position());
			gen(v, "tryBlock", n.tryBlock());
			gen(v, "catchBlocks", n.catchBlocks());
			gen(v, "finallyBlock", n.finallyBlock());
		}
	}

	public class ArrayTypeNodeLens implements AbsLens<ArrayTypeNode> {
		public ArrayTypeNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base", v);
			return nf.ArrayTypeNode(position, base);
		}

		public void toXML(DomGenerator v, ArrayTypeNode n) {
			// v = v.tag("ArrayTypeNode");
			gen(v, "position", n.position());
			gen(v, "base", n.base());
		}
	}

	public class PackageNodeLens implements AbsLens<PackageNode> {
		public PackageNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			polyglot.types.Package p = get(new PackageLens(), e, "package", v);
			return nf.PackageNode(position, p);
		}

		public void toXML(DomGenerator v, PackageNode n) {
			// v = v.tag("PackageNode");
			gen(v, "position", n.position());
			gen(v, "package", n.package_());
		}
	}

	X10TypeSystem ts;
	X10NodeFactory nf;

	public X10Dom(X10TypeSystem ts, X10NodeFactory nf) {
		this.ts = ts;
		this.nf = nf;
	}

	public Object fromXMLJunk(DomReader v, Element e) {
		String tag = e.getTagName();
                if (false) {
                }
                /*
                else if (tag.equals("Boolean")) {
			return new BooleanLens().fromXML(e);
		}
		else if (tag.equals("Char")) {
			return new CharLens().fromXML(e);
		}
		else if (tag.equals("Int")) {
			return new IntLens().fromXML(e);
		}
		else if (tag.equals("Long")) {
			return new LongLens().fromXML(e);
		}
		else if (tag.equals("Double")) {
			return new DoubleLens().fromXML(e);
		}
		else if (tag.equals("String")) {
			return new StringLens().fromXML(e);
		}
		else if (tag.equals("List")) {
			return new ListLens<Node>(new NodeLens()).fromXML(e);
		}
		else if (tag.equals("Position")) {
			return new PositionLens().fromXML(e);
		}
		else if (tag.equals("Flags")) {
			return new FlagsLens().fromXML(e);
		}
		else if (tag.equals("LocalInstance")) {
			return new LocalInstanceLens().fromXML(e);
		}
		else if (tag.equals("MethodInstance")) {
			return new MethodInstanceLens().fromXML(e);
		}
		else if (tag.equals("ConstructorInstance")) {
			return new ConstructorInstanceLens().fromXML(e);
		}
		else if (tag.equals("FieldInstance")) {
			return new FieldInstanceLens().fromXML(e);
		}
//		else if (tag.equals("InitializerInstance")) {
//			return new InitializerInstanceLens().fromXML(e);
//		}
//		else if (tag.equals("ClosureInstance")) {
//			return new ClosureInstanceLens().fromXML(e);
//		}
		else if (tag.equals("Type")) {
			return new TypeRefLens().fromXML(e);
		}
		else if (tag.equals("Package")) {
			return new PackageLens().fromXML(e);
		}
		else if (tag.equals("AssignOperator")) {
			return new AssignOpLens().fromXML(e);
		}
		else if (tag.equals("BinaryOperator")) {
			return new BinaryOpLens().fromXML(e);
		}
		else if (tag.equals("UnaryOperator")) {
			return new UnaryOpLens().fromXML(e);
		}
		else if (tag.equals("ImportKind")) {
			return new EnumLens("ImportKind", new Import.Kind[] {
					Import.CLASS,
					Import.PACKAGE
			}).fromXML(e);
		}
		else if (tag.equals("SpecialKind")) {
			return new EnumLens("SpecialKind", new Special.Kind[] {
					Special.THIS,
					Special.SUPER
			}).fromXML(e);
		}
		else if (tag.equals("X10SpecialKind")) {
			return new EnumLens("X10SpecialKind", new X10Special.Kind[] {
					X10Special.THIS,
					X10Special.SUPER,
					X10Special.SELF
			}).fromXML(e);
		}
		else if (tag.equals("ConstructorCallKind")) {
			return new EnumLens("ConstructorCallKind",
                                     new ConstructorCall.Kind[] {
					ConstructorCall.THIS,
					ConstructorCall.SUPER
			}).fromXML(e);
		}
		else if (tag.equals("BranchKind")) {
			return new EnumLens("BranchKind", new Branch.Kind[] {
					Branch.BREAK,
					Branch.CONTINUE
			}).fromXML(e);
		}
                */

		return new NodeLens().fromXML(v, e);
	}

	public void toXML(DomGenerator v, boolean o) {
		toXML(v, (Boolean) o);
	}
	public void toXML(DomGenerator v, byte o) {
		toXML(v, (int) o);
	}
	public void toXML(DomGenerator v, short o) {
		toXML(v, (int) o);
	}
	public void toXML(DomGenerator v, char o) {
		toXML(v, (Character) o);
	}
	public void toXML(DomGenerator v, int o) {
		toXML(v, (Integer) o);
	}
	public void toXML(DomGenerator v, long o) {
		toXML(v, (Long) o);
	}
	public void toXML(DomGenerator v, float o) {
		toXML(v, (double) o);
	}
	public void toXML(DomGenerator v, double o) {
		toXML(v, (Double) o);
	}
	

	public class ObjectLens implements AbsLens<Object> {
		public Object fromXML(DomReader v, Element e) {
			assert false;
			return null;
		}

		public void toXML(DomGenerator v, Object n) {
			X10Dom.this.toXML(v, n);
		}
	}

	
	public void toXML(DomGenerator v, Object o) {
		if (o instanceof Boolean) {
			new BooleanLens().toXML(v, (Boolean) o);
		}
		else if (o instanceof Character) {
			new CharLens().toXML(v, (Character) o);
		}
		else if (o instanceof Integer) {
			new IntLens().toXML(v, (Integer) o);
		}
		else if (o instanceof Long) {
			new LongLens().toXML(v, (Long) o);
		}
		else if (o instanceof Double) {
			new DoubleLens().toXML(v, (Double) o);
		}
		else if (o instanceof String) {
			new StringLens().toXML(v, (String) o);
		}
		else if (o instanceof List) {
			new ListLens<Object>(new ObjectLens()).toXML(v, (List) o);
		}
		else if (o instanceof Position) {
			new PositionLens().toXML(v, (Position) o);
		}
		else if (o instanceof Flags) {
			new FlagsLens().toXML(v, (Flags) o);
		}
		else if (o instanceof LocalInstance) {
			new LocalInstanceRefLens().toXML(v, (LocalInstance) o);
		}
		else if (o instanceof MethodInstance) {
			new MethodInstanceRefLens().toXML(v, (MethodInstance) o);
		}
		else if (o instanceof ConstructorInstance) {
			new ConstructorInstanceRefLens().toXML(v, (ConstructorInstance) o);
		}
		else if (o instanceof FieldInstance) {
			new FieldInstanceRefLens().toXML(v, (FieldInstance) o);
		}
//		else if (o instanceof InitializerInstance) {
//			new InitializerInstanceLens().toXML(v, (InitializerInstance) o);
//		}
//		else if (o instanceof ClosureInstance) {
//			new ClosureInstanceLens().toXML(v, (ClosureInstance) o);
//		}
		else if (o instanceof Type) {
			new TypeRefLens().toXML(v, (Type) o);
		}
		else if (o instanceof Package) {
			new PackageLens().toXML(v, (Package) o);
		}
		else if (o instanceof Node) {
			new NodeLens().toXML(v, (Node) o);
		}
		else if (o instanceof ClassType.Kind) {
			new EnumLens("ClassTypeLens", new ClassType.Kind[] {
					ClassType.ANONYMOUS,
					ClassType.LOCAL,
					ClassType.MEMBER,
					ClassType.TOP_LEVEL
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof Assign.Operator) {
			new AssignOpLens().toXML(v, (Assign.Operator) o);
		}
		else if (o instanceof Binary.Operator) {
			new BinaryOpLens().toXML(v, (Binary.Operator) o);
		}
		else if (o instanceof Unary.Operator) {
			new UnaryOpLens().toXML(v, (Unary.Operator) o);
		}
		else if (o instanceof IntLit.Kind) {
			new EnumLens("IntLitKind", new IntLit.Kind[] {
					IntLit.INT,
					IntLit.LONG
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof FloatLit.Kind) {
			new EnumLens("FloatLitKind", new FloatLit.Kind[] {
					FloatLit.FLOAT,
					FloatLit.DOUBLE
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof Import.Kind) {
			new EnumLens("ImportKind", new Import.Kind[] {
					Import.CLASS,
					Import.PACKAGE
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof Special.Kind) {
			new EnumLens("SpecialKind", new Special.Kind[] {
					Special.THIS,
					Special.SUPER
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof X10Special.Kind) {
			new EnumLens("X10SpecialKind", new X10Special.Kind[] {
					X10Special.THIS,
					X10Special.SUPER,
					X10Special.SELF
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof ConstructorCall.Kind) {
			new EnumLens("ConstructorCallKind", new ConstructorCall.Kind[] {
					ConstructorCall.THIS,
					ConstructorCall.SUPER
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof Branch.Kind) {
			new EnumLens("BranchKind", new Branch.Kind[] {
					Branch.BREAK,
					Branch.CONTINUE
			}).toXML(v, (polyglot.util.Enum) o);
		}
		else if (o instanceof Constraint) {
			new ConstraintLens().toXML(v, (Constraint) o);
		}
		else if (o instanceof C_Term) {
			new CTermLens().toXML(v, (C_Term) o);
		}
		else if (o instanceof C_Var) {
			new CVarLens().toXML(v, (C_Var) o);
		}
		else if (o instanceof Promise) {
			new PromiseLens().toXML(v, (Promise) o);
		}
		else if (o == null) {
			return;
		}
		else {
			throw new InternalCompilerError("unimplemented lens for " + o + ": " + o.getClass().getName());
		}
	}
	
	public <T extends TypeObject> void addType(DomGenerator v, T t, AbsLens<T> lens) {
		IdentityKey tkey = new IdentityKey(t);
		String key = v.typesMap.get(tkey);
		
		if (key == null) {
			key = keyString(t);
			v.typesMap.put(tkey, key);

			DomGenerator v1 = (DomGenerator) v.copy();
			v1.parent = v.createElement(v1.types, "item");
			gen(v1, "key", key);
			DomGenerator v2 = v1.tag("value");
			lens.toXML(v2, t);
		}
		
		v = v.tag("key");
		toXML(v, key);
	}
	
	public String keyString(TypeObject t) {
		String key = StringUtil.getShortNameComponent(t.getClass().getName());
		if (t instanceof Named) {
			key = ((Named) t).fullName().replace('.', '$');
		}
		else if (t instanceof FieldInstance) {
			key = keyString(((FieldInstance) t).container()) + "$" + ((FieldInstance) t).name();
		}
		else if (t instanceof MethodInstance) {
			key = keyString(((MethodInstance) t).container()) + "$" + ((MethodInstance) t).name();
		}
		else if (t instanceof ConstructorInstance) {
			key = keyString(((ConstructorInstance) t).container()) + "$" + ((ClassType) ((ConstructorInstance) t).container()).name();
		}
		else if (t instanceof LocalInstance) {
			key = ((LocalInstance) t).name();
		}
		return UniqueID.newID(key);
	}

	public class NodeLens implements AbsLens<Node> {
		public void toXML(DomGenerator v, Node n) {
			String tag = n.getClass().getName();
			tag = StringUtil.getShortNameComponent(tag);
			if (tag.endsWith("_c"))
				tag = tag.substring(0,tag.length()-2);
			v = v.tag(tag);
			gen(v, "position", n.position());
			LensFactory f = new LensFactory(v);
			f.visitAppropriate(n);
			if (n instanceof Expr) {
				Type t = ((Expr) n).type();
				if (t != null) {
					gen(v, "type", t);
				}
				else {
					gen(v, "type", t);
				}
			}
		}

		public Node fromXML(DomReader v, Element e) {
			if (e == null) {
				return null;
			}
			Node n = nodeFromXML(v, e);
			if (n instanceof Expr) {
				Type t = get(new TypeRefLens(), e, "type", v);
				n = ((Expr) n).type(t);
			}
			return n;
		}
		
		public Node nodeFromXML(DomReader v, Element e) {
			String tag = e.getTagName();
                    {

			if (tag.equals("AnnotationNode")) return new AnnotationNodeLens().fromXML(v, e);
			if (tag.equals("ArrayAccess")) return new ArrayAccessLens().fromXML(v, e);
			if (tag.equals("ArrayAccessAssign")) return new ArrayAccessAssignLens().fromXML(v, e);
			if (tag.equals("ArrayConstructor")) return new ArrayConstructorLens().fromXML(v, e);
			if (tag.equals("ArrayInit")) return new ArrayInitLens().fromXML(v, e);
			if (tag.equals("ArrayTypeNode")) return new ArrayTypeNodeLens().fromXML(v, e);
			if (tag.equals("Assert")) return new AssertLens().fromXML(v, e);
			if (tag.equals("Assign")) return new AssignLens().fromXML(v, e);
			if (tag.equals("AssignPropertyCall")) return new AssignPropertyCallLens().fromXML(v, e);
			if (tag.equals("AssignPropertyBody")) return new AssignPropertyBodyLens().fromXML(v, e);
			if (tag.equals("Async")) return new AsyncLens().fromXML(v, e);
			if (tag.equals("AtEach")) return new AtEachLens().fromXML(v, e);
			if (tag.equals("Atomic")) return new AtomicLens().fromXML(v, e);
			if (tag.equals("AtomicMethodDecl")) return new AtomicMethodDeclLens().fromXML(v, e);
			if (tag.equals("Await")) return new AwaitLens().fromXML(v, e);
			if (tag.equals("Binary")) return new BinaryLens().fromXML(v, e);
			if (tag.equals("Block")) return new BlockLens().fromXML(v, e);
			if (tag.equals("BooleanLit")) return new BooleanLitLens().fromXML(v, e);
			if (tag.equals("Branch")) return new BranchLens().fromXML(v, e);
			if (tag.equals("Call")) return new CallLens().fromXML(v, e);
			if (tag.equals("CanonicalTypeNode")) return new CanonicalTypeNodeLens().fromXML(v, e);
			if (tag.equals("Case")) return new CaseLens().fromXML(v, e);
			if (tag.equals("Cast")) return new CastLens().fromXML(v, e);
			if (tag.equals("Catch")) return new CatchLens().fromXML(v, e);
			if (tag.equals("CharLit")) return new CharLitLens().fromXML(v, e);
			if (tag.equals("ClassBody")) return new ClassBodyLens().fromXML(v, e);
			if (tag.equals("ClassDecl")) return new ClassDeclLens().fromXML(v, e);
			if (tag.equals("ClassLit")) return new ClassLitLens().fromXML(v, e);
			if (tag.equals("Closure")) return new ClosureLens().fromXML(v, e);
			if (tag.equals("ClosureCall")) return new ClosureCallLens().fromXML(v, e);
			if (tag.equals("Conditional")) return new ConditionalLens().fromXML(v, e);
			if (tag.equals("ConstantDistMaker")) return new ConstantDistMakerLens().fromXML(v, e);
			if (tag.equals("ConstructorCall")) return new ConstructorCallLens().fromXML(v, e);
			if (tag.equals("ConstructorDecl")) return new ConstructorDeclLens().fromXML(v, e);
			if (tag.equals("DepParameterExpr")) return new DepParameterExprLens().fromXML(v, e);
			if (tag.equals("DepCast")) return new DepCastLens().fromXML(v, e);
			if (tag.equals("DepInstanceof")) return new DepInstanceofLens().fromXML(v, e);
			if (tag.equals("Do")) return new DoLens().fromXML(v, e);
			if (tag.equals("Empty")) return new EmptyLens().fromXML(v, e);
			if (tag.equals("Eval")) return new EvalLens().fromXML(v, e);
			if (tag.equals("Field")) return new FieldLens().fromXML(v, e);
			if (tag.equals("FieldAssign")) return new FieldAssignLens().fromXML(v, e);
			if (tag.equals("FieldDecl")) return new FieldDeclLens().fromXML(v, e);
			if (tag.equals("Finish")) return new FinishLens().fromXML(v, e);
			if (tag.equals("FloatLit")) return new FloatLitLens().fromXML(v, e);
			if (tag.equals("For")) return new ForLens().fromXML(v, e);
			if (tag.equals("ForEach")) return new ForEachLens().fromXML(v, e);
			if (tag.equals("ForLoop")) return new ForLoopLens().fromXML(v, e);
			if (tag.equals("Formal")) return new FormalLens().fromXML(v, e);
			if (tag.equals("Future")) return new FutureLens().fromXML(v, e);
			if (tag.equals("FutureNode")) return new FutureNodeLens().fromXML(v, e);
			if (tag.equals("GenParameterExpr")) return new GenParameterExprLens().fromXML(v, e);
			if (tag.equals("Here")) return new HereLens().fromXML(v, e);
			if (tag.equals("Id")) return new IdLens().fromXML(v, e);
			if (tag.equals("If")) return new IfLens().fromXML(v, e);
			if (tag.equals("Import")) return new ImportLens().fromXML(v, e);
			if (tag.equals("Initializer")) return new InitializerLens().fromXML(v, e);
			if (tag.equals("Instanceof")) return new InstanceofLens().fromXML(v, e);
			if (tag.equals("IntLit")) return new IntLitLens().fromXML(v, e);
			if (tag.equals("Labeled")) return new LabeledLens().fromXML(v, e);
			if (tag.equals("Local")) return new LocalLens().fromXML(v, e);
			if (tag.equals("LocalAssign")) return new LocalAssignLens().fromXML(v, e);
			if (tag.equals("LocalClassDecl")) return new LocalClassDeclLens().fromXML(v, e);
			if (tag.equals("LocalDecl")) return new LocalDeclLens().fromXML(v, e);
			if (tag.equals("MethodDecl")) return new MethodDeclLens().fromXML(v, e);
			if (tag.equals("MethodDecl")) return new X10MethodDeclLens().fromXML(v, e);
			if (tag.equals("New")) return new NewLens().fromXML(v, e);
			if (tag.equals("NewArray")) return new NewArrayLens().fromXML(v, e);
			if (tag.equals("Next")) return new NextLens().fromXML(v, e);
			if (tag.equals("Now")) return new NowLens().fromXML(v, e);
			if (tag.equals("NullLit")) return new NullLitLens().fromXML(v, e);
			if (tag.equals("NullableNode")) return new NullableNodeLens().fromXML(v, e);
			if (tag.equals("PackageNode")) return new PackageNodeLens().fromXML(v, e);
			if (tag.equals("ParExpr")) return new ParExprLens().fromXML(v, e);
			if (tag.equals("PlaceCast")) return new PlaceCastLens().fromXML(v, e);
			if (tag.equals("Point")) return new PointLens().fromXML(v, e);
			if (tag.equals("PropertyDecl")) return new PropertyDeclLens().fromXML(v, e);
			if (tag.equals("Range")) return new RangeLens().fromXML(v, e);
			if (tag.equals("RectRegionMaker")) return new RectRegionMakerLens().fromXML(v, e);
			if (tag.equals("Region")) return new RegionLens().fromXML(v, e);
			if (tag.equals("RegionMaker")) return new RegionMakerLens().fromXML(v, e);
			if (tag.equals("RemoteCall")) return new RemoteCallLens().fromXML(v, e);
			if (tag.equals("Return")) return new ReturnLens().fromXML(v, e);
			if (tag.equals("SourceCollection")) return new SourceCollectionLens().fromXML(v, e);
			if (tag.equals("SourceFile")) return new SourceFileLens().fromXML(v, e);
			if (tag.equals("Special")) return new SpecialLens().fromXML(v, e);
			if (tag.equals("StmtSeq")) return new StmtSeqLens().fromXML(v, e);
			if (tag.equals("StringLit")) return new StringLitLens().fromXML(v, e);
			if (tag.equals("Switch")) return new SwitchLens().fromXML(v, e);
			if (tag.equals("SwitchBlock")) return new SwitchBlockLens().fromXML(v, e);
			if (tag.equals("Synchronized")) return new SynchronizedLens().fromXML(v, e);
			if (tag.equals("Throw")) return new ThrowLens().fromXML(v, e);
			if (tag.equals("Try")) return new TryLens().fromXML(v, e);
			if (tag.equals("Tuple")) return new TupleLens().fromXML(v, e);
			if (tag.equals("Unary")) return new UnaryLens().fromXML(v, e);
			if (tag.equals("ValueClassDecl")) return new ValueClassDeclLens().fromXML(v, e);
			if (tag.equals("When")) return new WhenLens().fromXML(v, e);
			if (tag.equals("While")) return new WhileLens().fromXML(v, e);
			if (tag.equals("X10ArrayAccess")) return new X10ArrayAccessLens().fromXML(v, e);
			if (tag.equals("X10ArrayAccess1")) return new X10ArrayAccess1Lens().fromXML(v, e);
//			if (tag.equals("X10ArrayAccess1Assign")) return new AssignLens().fromXML(v, e);
			if (tag.equals("X10ArrayAccess1Assign")) return new X10ArrayAccess1AssignLens().fromXML(v, e);
//			if (tag.equals("X10ArrayAccess1Unary")) return new UnaryLens().fromXML(v, e);
			if (tag.equals("X10ArrayAccess1Unary")) return new X10ArrayAccess1UnaryLens().fromXML(v, e);
//			if (tag.equals("X10ArrayAccessAssign")) return new ArrayAccessAssignLens().fromXML(v, e);
			if (tag.equals("X10ArrayAccessAssign")) return new X10ArrayAccessAssignLens().fromXML(v, e);
			if (tag.equals("X10ArrayAccessUnary")) return new X10ArrayAccessUnaryLens().fromXML(v, e);
			if (tag.equals("X10ArrayTypeNode")) return new X10ArrayTypeNodeLens().fromXML(v, e);
			if (tag.equals("X10Binary")) return new BinaryLens().fromXML(v, e);
			if (tag.equals("X10BooleanLit")) return new BooleanLitLens().fromXML(v, e);
			if (tag.equals("X10Call")) return new CallLens().fromXML(v, e);
			if (tag.equals("X10CanonicalTypeNode")) return new X10CanonicalTypeNodeLens().fromXML(v, e);
			if (tag.equals("X10Cast")) return new CastLens().fromXML(v, e);
			if (tag.equals("X10CharLit")) return new CharLitLens().fromXML(v, e);
			if (tag.equals("X10ClassBody")) return new ClassBodyLens().fromXML(v, e);
			if (tag.equals("X10ClassDecl")) return new ClassDeclLens().fromXML(v, e);
			if (tag.equals("X10Conditional")) return new ConditionalLens().fromXML(v, e);
			if (tag.equals("X10ConstructorDecl")) return new ConstructorDeclLens().fromXML(v, e);
			if (tag.equals("X10ConstructorCall")) return new ConstructorCallLens().fromXML(v, e);
			if (tag.equals("X10Field")) return new FieldLens().fromXML(v, e);
			if (tag.equals("X10FieldDecl")) return new FieldDeclLens().fromXML(v, e);
			if (tag.equals("X10FloatLit")) return new FloatLitLens().fromXML(v, e);
			if (tag.equals("X10Formal")) return new FormalLens().fromXML(v, e);
			if (tag.equals("X10Formal")) return new X10FormalLens().fromXML(v, e);
			if (tag.equals("X10If")) return new IfLens().fromXML(v, e);
			if (tag.equals("X10Instanceof")) return new InstanceofLens().fromXML(v, e);
			if (tag.equals("X10IntLit")) return new IntLitLens().fromXML(v, e);
			if (tag.equals("X10Local")) return new LocalLens().fromXML(v, e);
			if (tag.equals("X10LocalDecl")) return new LocalDeclLens().fromXML(v, e);
			if (tag.equals("X10MethodDecl")) return new X10MethodDeclLens().fromXML(v, e);
			if (tag.equals("X10New")) return new NewLens().fromXML(v, e);
			if (tag.equals("X10Special")) return new SpecialLens().fromXML(v, e);
			if (tag.equals("X10StringLit")) return new StringLitLens().fromXML(v, e);
			if (tag.equals("X10Unary")) return new UnaryLens().fromXML(v, e);
			if (tag.equals("X10While")) return new WhileLens().fromXML(v, e);

                    }

			throw new InternalCompilerError("Could not construct node from tag " + tag + ".");
		}
	}

	public class LensFactory extends X10DelegatingVisitor {
		DomGenerator v;

		LensFactory(DomGenerator v) { this.v = v; }
		
		public void visit(DepInstanceof_c n) { new DepInstanceofLens().toXML(v, n); }
		public void visit(DepCast_c n) { new DepCastLens().toXML(v, n); }

		public void visit(Node n) { throw new InternalCompilerError("No visit method found for " + n + ": " + n.getClass().getName()); }
		public void visit(Import_c n) { new ImportLens().toXML(v, n); }
		public void visit(PackageNode_c n) { new PackageNodeLens().toXML(v, n); }
		public void visit(SourceCollection_c n) { new SourceCollectionLens().toXML(v, n); }
		public void visit(SourceFile_c n) { new SourceFileLens().toXML(v, n); }
		public void visit(ClassBody_c n) { new ClassBodyLens().toXML(v, n); }
		public void visit(X10ClassBody_c n) { new ClassBodyLens().toXML(v, n); }
//		public void visit(ClassDecl_c n) { new ClassDeclLens().toXML(v, n); }
		public void visit(ValueClassDecl_c n) { new ValueClassDeclLens().toXML(v, n); }
		public void visit(X10ClassDecl_c n) { new ClassDeclLens().toXML(v, n); }
		public void visit(ConstructorDecl_c n) { new ConstructorDeclLens().toXML(v, n); }
		public void visit(X10ConstructorDecl_c n) { new ConstructorDeclLens().toXML(v, n); }
		public void visit(ArrayAccess_c n) { new ArrayAccessLens().toXML(v, n); }
		public void visit(ArrayConstructor_c n) { new ArrayConstructorLens().toXML(v, n); }
		public void visit(ArrayInit_c n) { new ArrayInitLens().toXML(v, n); }
		public void visit(Assign_c n) { new AssignLens().toXML(v, n); }
		public void visit(ArrayAccessAssign_c n) { new ArrayAccessAssignLens().toXML(v, n); }
		public void visit(FieldAssign_c n) { new FieldAssignLens().toXML(v, n); }
		public void visit(LocalAssign_c n) { new LocalAssignLens().toXML(v, n); }
		public void visit(X10ArrayAccess1Assign_c n) { new AssignLens().toXML(v, n); }
		public void visit(X10ArrayAccessAssign_c n) { new ArrayAccessAssignLens().toXML(v, n); }
		public void visit(Binary_c n) { new BinaryLens().toXML(v, n); }
		public void visit(X10Binary_c n) { new BinaryLens().toXML(v, n); }
		public void visit(Call_c n) { new CallLens().toXML(v, n); }
		public void visit(RemoteCall_c n) { new RemoteCallLens().toXML(v, n); }
		public void visit(X10Call_c n) { new CallLens().toXML(v, n); }
		public void visit(ConstantDistMaker_c n) { new ConstantDistMakerLens().toXML(v, n); }
		public void visit(RectRegionMaker_c n) { new RectRegionMakerLens().toXML(v, n); }
		public void visit(RegionMaker_c n) { new RegionMakerLens().toXML(v, n); }
		public void visit(Cast_c n) { new CastLens().toXML(v, n); }
		public void visit(X10Cast_c n) { new CastLens().toXML(v, n); }
		public void visit(Conditional_c n) { new ConditionalLens().toXML(v, n); }
		public void visit(X10Conditional_c n) { new ConditionalLens().toXML(v, n); }
		public void visit(DepParameterExpr_c n) { new DepParameterExprLens().toXML(v, n); }
		public void visit(Field_c n) { new FieldLens().toXML(v, n); }
		public void visit(X10Field_c n) { new FieldLens().toXML(v, n); }
		public void visit(Future_c n) { new FutureLens().toXML(v, n); }
		public void visit(GenParameterExpr_c n) { new GenParameterExprLens().toXML(v, n); }
		public void visit(Here_c n) { new HereLens().toXML(v, n); }
		public void visit(Instanceof_c n) { new InstanceofLens().toXML(v, n); }
		public void visit(X10Instanceof_c n) { new InstanceofLens().toXML(v, n); }
		public void visit(BooleanLit_c n) { new BooleanLitLens().toXML(v, n); }
		public void visit(X10BooleanLit_c n) { new BooleanLitLens().toXML(v, n); }
		public void visit(ClassLit_c n) { new ClassLitLens().toXML(v, n); }
		public void visit(FloatLit_c n) { new FloatLitLens().toXML(v, n); }
		public void visit(X10FloatLit_c n) { new FloatLitLens().toXML(v, n); }
		public void visit(NullLit_c n) { new NullLitLens().toXML(v, n); }
		public void visit(CharLit_c n) { new CharLitLens().toXML(v, n); }
		public void visit(X10CharLit_c n) { new CharLitLens().toXML(v, n); }
		public void visit(IntLit_c n) { new IntLitLens().toXML(v, n); }
		public void visit(X10IntLit_c n) { new IntLitLens().toXML(v, n); }
		public void visit(StringLit_c n) { new StringLitLens().toXML(v, n); }
		public void visit(X10StringLit_c n) { new StringLitLens().toXML(v, n); }
		public void visit(Local_c n) { new LocalLens().toXML(v, n); }
		public void visit(X10Local_c n) { new LocalLens().toXML(v, n); }
		public void visit(New_c n) { new NewLens().toXML(v, n); }
		public void visit(X10New_c n) { new NewLens().toXML(v, n); }
		public void visit(NewArray_c n) { new NewArrayLens().toXML(v, n); }
		public void visit(Now_c n) { new NowLens().toXML(v, n); }
		public void visit(ParExpr_c n) { new ParExprLens().toXML(v, n); }
		public void visit(PlaceCast_c n) { new PlaceCastLens().toXML(v, n); }
		public void visit(Point_c n) { new PointLens().toXML(v, n); }
		public void visit(Range_c n) { new RangeLens().toXML(v, n); }
		public void visit(Region_c n) { new RegionLens().toXML(v, n); }
		public void visit(Special_c n) { new SpecialLens().toXML(v, n); }
		public void visit(X10Special_c n) { new SpecialLens().toXML(v, n); }
		public void visit(Tuple_c n) { new TupleLens().toXML(v, n); }
		public void visit(Unary_c n) { new UnaryLens().toXML(v, n); }
		public void visit(X10ArrayAccess1Unary_c n) { new UnaryLens().toXML(v, n); }
		public void visit(X10ArrayAccessUnary_c n) { new X10ArrayAccessUnaryLens().toXML(v, n); }
		public void visit(X10Unary_c n) { new UnaryLens().toXML(v, n); }
		public void visit(X10ArrayAccess_c n) { new X10ArrayAccessLens().toXML(v, n); }
		public void visit(X10ArrayAccess1_c n) { new X10ArrayAccess1Lens().toXML(v, n); }
		public void visit(Closure_c n) { new ClosureLens().toXML(v, n); }
		public void visit(ClosureCall_c n) { new ClosureCallLens().toXML(v, n); }
//		public void visit(FieldDecl_c n) { new FieldDeclLens().toXML(v, n); }
		public void visit(X10FieldDecl_c n) { new FieldDeclLens().toXML(v, n); }
		public void visit(PropertyDecl_c n) { new PropertyDeclLens().toXML(v, n); }
//		public void visit(X10PropertyDecl_c n) { new PropertyDeclLens().toXML(v, n); }
		public void visit(Formal_c n) { new FormalLens().toXML(v, n); }
		public void visit(X10Formal_c n) { new FormalLens().toXML(v, n); }
		public void visit(Initializer_c n) { new InitializerLens().toXML(v, n); }
		public void visit(MethodDecl_c n) { new MethodDeclLens().toXML(v, n); }
		public void visit(X10MethodDecl_c n) { new X10MethodDeclLens().toXML(v, n); }
		public void visit(AtomicMethodDecl_c n) { new AtomicMethodDeclLens().toXML(v, n); }
		public void visit(Block_c n) { new BlockLens().toXML(v, n); }
		public void visit(StmtSeq_c n) { new StmtSeqLens().toXML(v, n); }
		public void visit(SwitchBlock_c n) { new SwitchBlockLens().toXML(v, n); }
		public void visit(Assert_c n) { new AssertLens().toXML(v, n); }
		public void visit(AssignPropertyBody_c n) { new AssignPropertyBodyLens().toXML(v, n); }
		public void visit(AssignPropertyCall_c n) { new AssignPropertyCallLens().toXML(v, n); }
		public void visit(Async_c n) { new AsyncLens().toXML(v, n); }
		public void visit(Atomic_c n) { new AtomicLens().toXML(v, n); }
		public void visit(Await_c n) { new AwaitLens().toXML(v, n); }
		public void visit(Branch_c n) { new BranchLens().toXML(v, n); }
		public void visit(Case_c n) { new CaseLens().toXML(v, n); }
		public void visit(Catch_c n) { new CatchLens().toXML(v, n); }
		public void visit(ConstructorCall_c n) { new ConstructorCallLens().toXML(v, n); }
		public void visit(Empty_c n) { new EmptyLens().toXML(v, n); }
		public void visit(Eval_c n) { new EvalLens().toXML(v, n); }
		public void visit(Finish_c n) { new FinishLens().toXML(v, n); }
		public void visit(If_c n) { new IfLens().toXML(v, n); }
		public void visit(X10If_c n) { new IfLens().toXML(v, n); }
		public void visit(Labeled_c n) { new LabeledLens().toXML(v, n); }
		public void visit(LocalClassDecl_c n) { new LocalClassDeclLens().toXML(v, n); }
		public void visit(LocalDecl_c n) { new LocalDeclLens().toXML(v, n); }
		public void visit(X10LocalDecl_c n) { new LocalDeclLens().toXML(v, n); }
		public void visit(Do_c n) { new DoLens().toXML(v, n); }
		public void visit(For_c n) { new ForLens().toXML(v, n); }
		public void visit(While_c n) { new WhileLens().toXML(v, n); }
		public void visit(X10While_c n) { new WhileLens().toXML(v, n); }
		public void visit(Next_c n) { new NextLens().toXML(v, n); }
		public void visit(Return_c n) { new ReturnLens().toXML(v, n); }
		public void visit(Switch_c n) { new SwitchLens().toXML(v, n); }
		public void visit(Synchronized_c n) { new SynchronizedLens().toXML(v, n); }
		public void visit(Throw_c n) { new ThrowLens().toXML(v, n); }
		public void visit(Try_c n) { new TryLens().toXML(v, n); }
		public void visit(When_c n) { new WhenLens().toXML(v, n); }
		public void visit(AtEach_c n) { new AtEachLens().toXML(v, n); }
		public void visit(ForEach_c n) { new ForEachLens().toXML(v, n); }
		public void visit(ForLoop_c n) { new ForLoopLens().toXML(v, n); }
		public void visit(ArrayTypeNode_c n) { new ArrayTypeNodeLens().toXML(v, n); }
		public void visit(CanonicalTypeNode_c n) { new CanonicalTypeNodeLens().toXML(v, n); }
		public void visit(X10CanonicalTypeNode_c n) { new X10CanonicalTypeNodeLens().toXML(v, n); }
		public void visit(FutureNode_c n) { new FutureNodeLens().toXML(v, n); }
		public void visit(NullableNode_c n) { new NullableNodeLens().toXML(v, n); }
		public void visit(X10ArrayTypeNode_c n) { new X10ArrayTypeNodeLens().toXML(v, n); }
		public void visit(AnnotationNode_c n) { new AnnotationNodeLens().toXML(v, n); }
		public void visit(Id_c n) { new IdLens().toXML(v, n); }
	}
}
