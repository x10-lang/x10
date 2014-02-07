/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.dom;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import polyglot.ast.ArrayInit;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Branch;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Conditional;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Empty;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FloatLit;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.MethodDecl;
import polyglot.ast.NewArray;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.Precedence;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import x10.ast.AnnotationNode;
import x10.ast.AssignPropertyBody;
import x10.ast.AssignPropertyCall;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.Closure;
import x10.ast.DepParameterExpr;
import x10.ast.Finish;
import x10.ast.ForEach;
import x10.ast.ForLoop;
import x10.ast.FutureNode;
import x10.ast.NullableNode;
import x10.ast.X10DelFactory_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.dom.DomReader.LazyTypeObject;
import x10.extension.X10Del;
import x10.extension.X10Ext;
import x10.types.ClosureInstance;
import x10.types.ClosureInstance_c;
import x10.types.ClosureType;
import x10.types.FutureType;
import x10.types.NullableType;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10LocalInstance;
import x10.types.X10MethodInstance;
import x10.types.X10NamedType;
import x10.types.X10ParsedClassType;
import x10.types.X10Type;
import x10.types.X10TypeSystem;
import x10.types.constr.C_BinaryTerm;
import x10.types.constr.C_BinaryTerm_c;
import x10.types.constr.C_EQV;
import x10.types.constr.C_EQV_c;
import x10.types.constr.C_Field;
import x10.types.constr.C_Field_c;
import x10.types.constr.C_Here_c;
import x10.types.constr.C_Lit;
import x10.types.constr.C_Lit_c;
import x10.types.constr.C_Local;
import x10.types.constr.C_Local_c;
import x10.types.constr.C_Special;
import x10.types.constr.C_Special_c;
import x10.types.constr.C_Term;
import x10.types.constr.C_Term_c;
import x10.types.constr.C_Type;
import x10.types.constr.C_Type_c;
import x10.types.constr.C_UnaryTerm;
import x10.types.constr.C_UnaryTerm_c;
import x10.types.constr.C_Var;
import x10.types.constr.Constraint;
import x10.types.constr.Constraint_c;
import x10.types.constr.Promise;
import x10.types.constr.Promise_c;
import x10.types.constraints.CConstraint;
import polyglot.frontend.Source;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.ImportTable;
import polyglot.types.InitializerInstance;
import polyglot.types.LazyInitializer;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NullType;
import polyglot.types.Package;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.IdentityKey;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.util.SubtypeSet;
import polyglot.util.UniqueID;
import polyglot.visit.InitImportsVisitor;

public class X10Dom {

	X10TypeSystem ts;
	X10NodeFactory nf;
	Map<String,Lens> tagMap;

	public X10Dom(X10TypeSystem ts, X10NodeFactory nf) {
		this.ts = ts;
		this.nf = nf;
	}

		
	public Map<String,Lens> tagMap() {
		if (tagMap != null) {
			return tagMap;
		}
		
		tagMap = CollectionFactory.newHashMap();

		tagMap.put("Boolean", new BooleanLens());
		tagMap.put("Byte", new ByteLens());
		tagMap.put("Short", new ShortLens());
		tagMap.put("Char", new CharLens());
		tagMap.put("Int", new IntLens());
		tagMap.put("Long", new LongLens());
		tagMap.put("Float", new FloatLens());
		tagMap.put("Double", new DoubleLens());
		tagMap.put("String", new StringLens());
		tagMap.put("List", new ListLens<Object>(new ObjectLens()));
//		tagMap.put("Map", new MapLens<Object,Object>(new ObjectLens(), new ObjectLens()));
//		tagMap.put("Entry", new EntryLens<Object,Object>(new ObjectLens(), new ObjectLens()));
		
		tagMap.put("StringArray", new StringArrayLens());
		tagMap.put("SubtypeSet", new SubtypeSetLens());
		
		tagMap.put("Constraint", new ConstraintLens());
		tagMap.put("CTerm", new CTermLens());
		tagMap.put("CVar", new CVarLens());
		tagMap.put("Promise", new PromiseRefLens());

		tagMap.put("Package", new PackageLens());
		tagMap.put("TypeRef", new TypeRefLens());

		tagMap.put("ConstructorInstanceRef", new ConstructorInstanceRefLens());
		tagMap.put("FieldInstanceRef", new FieldInstanceRefLens());
		tagMap.put("ClosureInstanceRef", new ClosureInstanceRefLens());
		tagMap.put("InitializerInstanceRef", new InitializerInstanceRefLens());
		tagMap.put("LocalInstanceRef", new LocalInstanceRefLens());
		tagMap.put("MethodInstanceRef", new MethodInstanceRefLens());

		tagMap.put("SourceFile", new SourceFileLens());
		tagMap.put("NewArray", new NewArrayLens());
		
		tagMap.put("Flags", new FlagsLens());
		tagMap.put("Position", new PositionLens());
		
		tagMap.put("AssignOp", new AssignOpLens());
		tagMap.put("BinaryOp", new BinaryOpLens());
		tagMap.put("UnaryOp", new UnaryOpLens());

		tagMap.put("ClassTypeKind", new EnumLens("ClassTypeKind",
				new ClassType.Kind[] { ClassType.ANONYMOUS,
				ClassType.LOCAL, ClassType.MEMBER,
				ClassType.TOP_LEVEL }));
		tagMap.put("IntLitKind", new EnumLens("IntLitKind",
				new IntLit.Kind[] { IntLit.INT, IntLit.LONG }));
		tagMap.put("FloatLitKind", new EnumLens("FloatLitKind",
				new FloatLit.Kind[] { FloatLit.FLOAT, FloatLit.DOUBLE }));
		tagMap.put("ImportKind", new EnumLens("ImportKind",
				new Import.Kind[] { Import.CLASS, Import.PACKAGE }));
		tagMap.put("SpecialKind", new EnumLens("SpecialKind",
				new Special.Kind[] { Special.THIS, Special.SUPER }));
		tagMap.put("X10SpecialKind", new EnumLens("X10SpecialKind",
				new X10Special.Kind[] { X10Special.THIS, X10Special.SUPER,
				X10Special.SELF }));
		tagMap.put("ConstructorCallKind", new EnumLens("ConstructorCallKind",
				new ConstructorCall.Kind[] {
						ConstructorCall.THIS, ConstructorCall.SUPER }));
		tagMap.put("BranchKind", new EnumLens("BranchKind",
				new Branch.Kind[] { Branch.BREAK, Branch.CONTINUE }));
		
		return tagMap;
	}
		
	public <T> T get(Lens<T> lens, Element e, String tag, DomReader v) {
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

	protected <T> T getImpl(Lens<T> lens, Element e, String tag, DomReader v) {
		if (e == null) {
			return null;
		}
		Element field = getChild(e, tag);
		if (field == null) {
			return null;
		}
		Element child = getFirstElement(field);
		if (child == null) {
			return null;
		}
		return lens.fromXML(v, child);
	}

	public <T> void gen(DomGenerator v, String tag, T n) {
		if (n == null) return;
		new ObjectLens().toXML(v.tag(tag), n);
	}
	
	public Element getFirstElement(Element e) {
		for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof Element) {
				return (Element) child;
			}
		}
		return null;
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

	public interface Lens<T> { 
		public T fromXML(DomReader v, Element e);
		public void toXML(DomGenerator v, T n);
	}

	public interface RefLens<T> extends Lens<T> { }
	
	public class ListLens<T> implements Lens<List<T>> {
		Lens<T> lens;
		
		ListLens(Lens<T> lens) {
			this.lens = lens;
		}
		
		public List<T> fromXML(DomReader v, Element e) {
			if (e == null) return Collections.<T>emptyList();
			ArrayList<T> r = new ArrayList<T>();
			for (org.w3c.dom.Node child = e.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child instanceof Element) {
					assert ((Element) child).getTagName().equals("item");
//					Element x = getFirstElement((Element) child);
					Element x = (Element) child;
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
//				DomGenerator v2 = v.tag("item");
				DomGenerator v2 = v;
				lens.toXML(v2, o);
			}
		}
	}
	
	public class StringArrayLens implements Lens<String[]> {
		public String[] fromXML(DomReader v, Element e) {
			List l = get(new ListLens<Object>(new ObjectLens()), e, "list", v);
			return (String[]) new ArrayList(l).toArray(new String[0]);
		}
		
		public void toXML(DomGenerator v, String[] a) {
			v = v.tag("StringArray");
			gen(v, "list", Arrays.asList(a));
		}
	}
	
	public class SubtypeSetLens implements Lens<SubtypeSet> {
		public SubtypeSet fromXML(DomReader v, Element e) {
			List l = get(new ListLens<Object>(new ObjectLens()), e, "list", v);
			SubtypeSet s = new SubtypeSet(ts);
			s.addAll(l);
			return s;
		}
		
		public void toXML(DomGenerator v, SubtypeSet a) {
			v = v.tag("SubtypeSet");
			gen(v, "list", new ArrayList(a));
		}
	}

	protected static class Entry<K,V> {
		K k; V v;
		Entry(K k, V v) { this.k = k ; this.v = v; }
	}
	
	public class EntryLens<K,V> implements Lens<Entry<K,V>> {
		Lens<K> klens;
		Lens<V> vlens;
		
		EntryLens(Lens<K> klens, Lens<V> vlens) {
			this.klens = klens;
			this.vlens = vlens;
		}
		
		public Entry<K,V> fromXML(DomReader r, Element e) {
			K k = get(klens, e, "key", r);
			V v = get(vlens, e, "value", r);
			return new Entry<K,V>(k,v);
		}
		
		public void toXML(DomGenerator v, Entry<K,V> e) {
			v = v.tag("Entry");
			klens.toXML(v.tag("key"), e.k);
			vlens.toXML(v.tag("value"), e.v);
		}
	}
	
	public class MapLens<K,V> implements Lens<Map<K,V>> {
		Lens<K> klens;
		Lens<V> vlens;
		
		MapLens(Lens<K> klens, Lens<V> vlens) {
			this.klens = klens;
			this.vlens = vlens;
		}

		public Map<K,V> fromXML(DomReader v, Element e) {
			if (e == null)
				return Collections.<K,V>emptyMap();
			Map<K,V> r = CollectionFactory.newHashMap();
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
				new EntryLens<K,V>(klens, vlens).toXML(vi, new Entry(e.getKey(), e.getValue()));
			}
		}
	}
	
	public class X10ExtLens implements Lens<X10Ext> {
		public X10Ext fromXML(DomReader v, Element e) {
			return (X10Ext) new DefaultLens().fromXML(v, e);
		}

		public void toXML(DomGenerator v, X10Ext n) {
			new DefaultLens() {
				public boolean shouldFollow(Object obj, String field, Object fieldValue) {
					return ! (field.equals("node") && fieldValue instanceof Node);
				}
			}.toXML(v, n);
		}
	}
	
	public class AssignOpLens implements Lens<Assign.Operator> {
		public Assign.Operator fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
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
			v = v.tag("AssignOp");
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
			v.createText(v.parent, tag);
		}
	}

	public class BinaryOpLens implements Lens<Binary.Operator> {
		public Binary.Operator fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
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
			v = v.tag("BinaryOp");
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
			v.createText(v.parent, tag);
		}
	}

	public class UnaryOpLens implements Lens<Unary.Operator> {
		public Unary.Operator fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
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
			v = v.tag("UnaryOp");
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
			v.createText(v.parent, tag);
		}
	}

	public class StringLens implements Lens<String> {
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

	public class BooleanLens implements Lens<Boolean> {
		public Boolean fromXML(DomReader v, Element e) {
			String s = e.getTextContent();
			if (s.equals("true")) return Boolean.TRUE;
			if (s.equals("false")) return Boolean.FALSE;
			return null;
		}

		public void toXML(DomGenerator v, Boolean s) {
			v = v.tag("Boolean");
			v.createText(v.parent, s.toString());
		}
	}
	
	public class CharLens implements Lens<Character> {
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
	
	public class ByteLens implements Lens<Byte> {
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
	
	
	public class ShortLens implements Lens<Short> {
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
	

	public class IntLens implements Lens<Integer> {
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
	
	public class LongLens implements Lens<Long> {
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
	
	public class FloatLens implements Lens<Float> {
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
	
	public class DoubleLens implements Lens<Double> {
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
	
	public class FlagsLens implements Lens<Flags> {
		public Flags fromXML(DomReader v, Element e) {
			Set fs = CollectionFactory.newHashSet;
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
			List l = get(new ListLens<Object>(new ObjectLens()), e, "list", v);
			for (int i = 0; i < l.size(); i++) {
				String s = (String) l.get(i);
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
			return f;
		}

		public void toXML(DomGenerator v, Flags n) {
			v = v.tag("Flags");
			List names = new ArrayList(n.flags());
			gen(v, "list", names);
		}
	}


	public class PositionLens implements Lens<Position> {
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
				return Position.COMPILER_GENERATED;
			return new Position(path, file, line, column, endLine, endColumn, offset, endOffset);
		}

		public void toXML(DomGenerator v, Position n) {
			v = v.tag("Position");
			if (n == Position.COMPILER_GENERATED)
				return;
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

	public class ClassTypeLens extends LTOLens<X10ClassType> {
		public ClassTypeLens(LazyTypeObject lto) {
			super(lto);
		}

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
			}
			
			if (fullName != null) {
				Named n = ts.systemResolver().check(fullName);
				if (n instanceof X10ClassType) {
					return (X10ClassType) n;
				}
			}
			
			X10ParsedClassType ct = (X10ParsedClassType) ts.createClassType(new X10MLClassInitializer(ts, X10Dom.this, v, e));
			lto.o = ct;

			ct.package_(pkg);
			ct.setJob(ts.extensionInfo().scheduler().currentJob());
			ct.kind(kind);
			if (kind != ClassType.TOP_LEVEL) {
				ct.outer(outer);
			}
			
			if (name != null) {
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
			
			Flags flags = get(new FlagsLens(), e, "flags", v);
			ct.setFlags(flags);

			Constraint realClause = get(new ConstraintLens(), e, "realClause", v);
			ct.setRealClause(realClause);
			List annotations = get(new ListLens<Type>(new TypeRefLens()), e, "annotations", v);
			ct.setAnnotations(annotations);
			List classAnnotations = get(new ListLens<Type>(new TypeRefLens()), e, "classAnnotations", v);
			ct.setClassAnnotations(classAnnotations);
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
			if (n.annotationsSet())
				gen(v, "annotations", n.annotations());
			if (n instanceof X10ParsedClassType && ((X10ParsedClassType) n).classAnnotationsSet())
				gen(v, "classAnnotations", ((X10ParsedClassType) n).classAnnotations());
			gen(v, "realClause", n.realClause());
		}
	}

	public class ClosureInstanceLens extends LTOLens<ClosureInstance> {
		public ClosureInstanceLens(LazyTypeObject lto) {
			super(lto);
		}

		public ClosureInstance fromXML(DomReader v, Element e) {
			Position pos = Position.COMPILER_GENERATED;
			ClosureInstance ci = ts.closureInstance(pos, null, null, ts.unknownType(pos), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			lto.o = ci;
			ClosureInstance proto = (ClosureInstance) new DefaultTypeObjectLens().fromXML(v, e);
			proto.setDeclaration(proto);
			ci.setMethodContainer(proto.methodContainer());
			ci.setReturnType(proto.returnType());
			ci.setFormalTypes(proto.formalTypes());
			ci.setThrowTypes(proto.throwTypes());
			((ClosureInstance_c) ci).setContainer(((ClosureInstance_c) proto).container());
			return ci;
		}
		
		public void toXML(DomGenerator v, ClosureInstance n) {
			new DefaultTypeObjectLens().toXML(v, n);
		}
	}
	
	public class InitializerInstanceLens extends LTOLens<InitializerInstance> {
		public InitializerInstanceLens(LazyTypeObject lto) {
			super(lto);
		}

		public InitializerInstance fromXML(DomReader v, Element e) {
			InitializerInstance ii = ts.initializerInstance(Position.COMPILER_GENERATED, ts.X10Object(), Flags.NONE);
			lto.o = ii;
			InitializerInstance proto = (InitializerInstance) new DefaultTypeObjectLens().fromXML(v, e);
			ii.setContainer(proto.container());
			ii.setFlags(proto.flags());
			return ii;
		}
		
		public void toXML(DomGenerator v, InitializerInstance n) {
			new DefaultTypeObjectLens().toXML(v, n);
		}
	}
	
	
	protected class DefaultTypeObjectLens extends DefaultLens {
		@Override
		public boolean shouldFollow(Object obj, String name, Object fieldValue) {
			return ! (name.equals("ts") && fieldValue instanceof TypeSystem)
			    && ! (name.equals("decl") && fieldValue == obj);
		}
		
		@Override
		public Object overrideField(Object obj, java.lang.reflect.Field f, Object fieldValue) {
			if (f.getName().equals("decl") && f.getType().isAssignableFrom(obj.getClass()) && fieldValue == null) {
				return obj;
			}
			if (TypeSystem.class.isAssignableFrom(f.getType()) && f.getType().isAssignableFrom(ts.getClass())) {
				return ts;
			}
			return fieldValue;
		}
	}

	public class ConstructorInstanceLens extends LTOLens<ConstructorInstance> {
		public ConstructorInstanceLens(LazyTypeObject lto) {
			super(lto);
		}

		public ConstructorInstance fromXML(DomReader v, Element e) {
			Position pos = Position.COMPILER_GENERATED;
			X10ConstructorInstance ci = (X10ConstructorInstance) ts.constructorDef(pos, null, Flags.NONE, (X10Type) ts.X10Object(), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			lto.o = ci;
			
			X10ConstructorInstance proto = (X10ConstructorInstance) new DefaultTypeObjectLens().fromXML(v, e);
			proto.setDeclaration(proto);
			ci.setContainer(proto.container());
			ci.setFlags(proto.flags());
			ci.setReturnType(((X10ConstructorInstance) proto).returnType());
			ci.setFormalTypes(proto.formalTypes());
			ci.setThrowTypes(proto.throwTypes());
			if (proto.annotationsSet())
				ci.setAnnotations(proto.annotations());
			else
				ci.setAnnotations(Collections.EMPTY_LIST);
			ci.setSupClause(proto.supClause());
			return ci;
		}

		public void toXML(DomGenerator v, ConstructorInstance n) {
			new DefaultTypeObjectLens().toXML(v, n);
		}
	}

	public class MethodInstanceLens extends LTOLens<MethodInstance> {
		public MethodInstanceLens(LazyTypeObject lto) {
			super(lto);
		}

		public MethodInstance fromXML(DomReader v, Element e) {
			Position pos = Position.COMPILER_GENERATED;
			X10MethodInstance mi = (X10MethodInstance) ts.methodInstance(pos, null, Flags.NONE, ts.unknownType(pos), "-", Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			lto.o = mi;
			X10MethodInstance proto = (X10MethodInstance) new DefaultTypeObjectLens().fromXML(v, e);
			proto.setDeclaration(proto);
			mi.setContainer(proto.container());
			mi.setFlags(proto.flags());
			mi.setReturnType(proto.returnType());
			mi.setName(proto.name());
			mi.setFormalTypes(proto.formalTypes());
			mi.setThrowTypes(proto.throwTypes());
			if (proto.annotationsSet())
				mi.setAnnotations(proto.annotations());
			else
				mi.setAnnotations(Collections.EMPTY_LIST);
			return mi;
		}

		public void toXML(DomGenerator v, MethodInstance n) {
			new DefaultTypeObjectLens().toXML(v, n);
		}
	}
	
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
			addType(v, n, new ConstructorInstanceLens(null));
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
			addType(v, n, new MethodInstanceLens(null));
		}
	}
	
	public class InitializerInstanceRefLens implements RefLens<InitializerInstance> {
		public InitializerInstance fromXML(DomReader v, Element e) {
			String name = get(new StringLens(), e, "key", v);
			InitializerInstance t = (InitializerInstance) v.typeMap.get(name).force(X10Dom.this);
			if (t != null) {
				return t;
			}
			return null;
		}
		
		public void toXML(DomGenerator v, InitializerInstance n) {
			v = v.tag("InitializerInstanceRef");
			addType(v, n, new InitializerInstanceLens(null));
		}
	}
	
	public class ClosureInstanceRefLens implements RefLens<ClosureInstance> {
		public ClosureInstance fromXML(DomReader v, Element e) {
			String name = get(new StringLens(), e, "key", v);
			ClosureInstance t = (ClosureInstance) v.typeMap.get(name).force(X10Dom.this);
			if (t != null) {
				return t;
			}
			return null;
		}
		
		public void toXML(DomGenerator v, ClosureInstance n) {
			v = v.tag("ClosureInstanceRef");
			addType(v, n, new ClosureInstanceLens(null));
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
			addType(v, n, new FieldInstanceLens(null));
		}
	}
	
	public class FieldInstanceLens extends LTOLens<FieldInstance> {
		public FieldInstanceLens(LazyTypeObject lto) {
			super(lto);
		}

		public FieldInstance fromXML(DomReader v, Element e) {
			Position pos = Position.COMPILER_GENERATED;
			X10FieldInstance fi = (X10FieldInstance) ts.fieldInstance(pos, ts.X10Object(), Flags.NONE, ts.unknownType(pos), "-");
			lto.o = fi;
			X10FieldInstance proto = (X10FieldInstance) new DefaultTypeObjectLens().fromXML(v, e);
			proto.setDeclaration(proto);
			fi.setContainer(proto.container());
			fi.setFlags(proto.flags());
			fi.setType(proto.type());
			fi.setName(proto.name());
			if (proto.annotationsSet())
				fi.setAnnotations(proto.annotations());
			else
				fi.setAnnotations(Collections.EMPTY_LIST);
			if (proto.depTypeSet())
				fi.setDepType(proto.type());
			if (proto.isPropertyInitialized())
				if (proto.isProperty())
					fi.setProperty();
			if (proto.constantValueSet() && proto.isConstant()) 
				fi.setConstantValue(proto.constantValue());
			else
				fi.setNotConstant();
			return fi;
		}

		public void toXML(DomGenerator v, FieldInstance n) {
			new DefaultTypeObjectLens().toXML(v, n);
		}
	}

	public class LocalInstanceLens extends LTOLens<LocalInstance> {
		public LocalInstanceLens(LazyTypeObject lto) {
			super(lto);
		}

		public LocalInstance fromXML(DomReader v, Element e) {
			Position pos = Position.COMPILER_GENERATED;
			X10LocalInstance li = (X10LocalInstance) ts.localDef(pos, Flags.NONE, ts.unknownType(pos), "-");
			lto.o = li;
			X10LocalInstance proto = (X10LocalInstance) new DefaultTypeObjectLens().fromXML(v, e);
			proto.setDeclaration(proto);
			li.setFlags(proto.flags());
			li.setType(proto.type());
			li.setName(proto.name());
			if (proto.annotationsSet())
				li.setAnnotations(proto.annotations());
			else
				li.setAnnotations(Collections.EMPTY_LIST);
			if (proto.constantValueSet() && proto.isConstant())
				li.setConstantValue(proto.constantValue());
			else
				li.setNotConstant();
			li.setPositionInArgList(proto.positionInArgList());
			return li;
		}
		
		public void toXML(DomGenerator v, LocalInstance n) {
			new DefaultTypeObjectLens().toXML(v, n);
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
			addType(v, n, new LocalInstanceLens(null));
		}
	}

	public class PackageLens implements Lens<Package> {
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
	
	public class TypeObjectLens implements Lens<Object> {
		LazyTypeObject lto;
		
		public Object fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("MethodInstance")) {
				return new MethodInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("X10MethodInstance")) {
				return new MethodInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("FieldInstance")) {
				return new FieldInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("X10FieldInstance")) {
				return new FieldInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("ConstructorInstance")) {
				return new ConstructorInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("X10ConstructorInstance")) {
				return new ConstructorInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("LocalInstance")) {
				return new LocalInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("X10LocalInstance")) {
				return new LocalInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("InitializerInstance")) {
				return new InitializerInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("ClosureInstance")) {
				return new ClosureInstanceLens(lto).fromXML(v, e);
			}
			if (tag.equals("ClassType")) {
				return new ClassTypeLens(lto).fromXML(v, e);
			}
			if (tag.equals("Promise")) {
				return new PromiseLens(lto).fromXML(v, e);
			}
			throw new InternalCompilerError("No lens found for " + tag);
		}
		
		public void toXML(DomGenerator v, Object n) {
			assert false;
		}
	}

	public class PrecedenceLens implements Lens<Precedence> {
		public Precedence fromXML(DomReader v, Element e) {
			Precedence p = (Precedence)	new DefaultLens().fromXML(v, e);
			return (Precedence) p.intern();
		}
		
		public void toXML(DomGenerator v, Precedence n) {
			new DefaultLens().toXML(v, n);
		}
	}
	
	public class ConstraintLens implements Lens<Constraint> {
		public Constraint fromXML(DomReader v, Element e) {
			Boolean consistent = get(new BooleanLens(), e, "consistent", v);
			Boolean placeIsHere = get(new BooleanLens(), e, "placeIsHere", v);
			Boolean placePossiblyNull = get(new BooleanLens(), e, "placePossiblyNull", v);
			Boolean valid = get(new BooleanLens(), e, "valid", v);
			Integer eqvCount = get(new IntLens(), e, "eqvCount", v);
			C_Var selfVar = get(new CVarLens(), e, "selfVar", v);
			C_Term_c placeTerm = (C_Term_c) get(new CTermLens(), e, "placeTerm", v);
			Map<C_Var,Promise> roots = (Map<C_Var,Promise>) get(new MapLens<C_Var,Promise>(new CVarLens(), new PromiseRefLens()), e, "roots", v);
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
	
	public abstract class LTOLens<T> implements Lens<T> {
		LazyTypeObject lto;
		
		LTOLens(LazyTypeObject lto) {
			this.lto = lto;
		}
	}
	
	public class PromiseLens extends LTOLens<Promise> {
		public PromiseLens(LazyTypeObject lto) {
			super(lto);
		}

		public Promise fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("Promise")) {
				C_Var var = get(new CVarLens(), e, "var", v);
				Promise value = get(new PromiseRefLens(), e, "value", v);
				Map<String,Promise> fields = get(new MapLens<String,Promise>(new StringLens(), new PromiseRefLens()), e, "var", v);
				Promise_c o = new Promise_c(var, value, fields);
				lto.o = o;
				return o;
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
	
	public class PromiseRefLens implements Lens<Promise> {
		public Promise fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("C_Here")) return new CHereLens().fromXML(v, e);
			if (tag.equals("C_Type")) return new CTypeLens().fromXML(v, e);
			if (tag.equals("C_Lit")) return new CLitLens().fromXML(v, e);
			if (tag.equals("PromiseRef")) {
				String name = get(new StringLens(), e, "key", v);
				Promise t = (Promise) v.typeMap.get(name).force(X10Dom.this);
				if (t != null) {
					return t;
				}
				return null;
			}
			assert false;
			return null;
		}
		
		public void toXML(DomGenerator v, Promise n) {
			v = v.tag("PromiseRef");
			addType(v, n, new PromiseLens(null));
		}
	}
	
	public class CVarLens implements Lens<C_Var> {
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
	
	public class CFieldLens implements Lens<C_Field> {
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
	
	public class CLocalLens implements Lens<C_Local> {
		public C_Local fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			if (tag.equals("C_EQV")) return new CEQVLens().fromXML(v, e);
			LocalInstance li = get(new LocalInstanceRefLens(), e, "local", v);
			Boolean isSelf = get(new BooleanLens(), e, "isSelf", v);
			return new C_Local_c(li, isSelf);
		}
		
		public void toXML(DomGenerator v, C_Local n) {
			v = v.tag("C_Local");
			gen(v, "local", n.localInstance());
			gen(v, "isSelf", n.isSelfVar());
		}
	}
	
	public class CSpecialLens implements Lens<C_Special> {
		public C_Special fromXML(DomReader v, Element e) {
			Type type = get(new TypeRefLens(), e, "type", v);
			Type qualifier = get(new TypeRefLens(), e, "qualifier", v);
			Boolean isThis = get(new BooleanLens(), e, "this", v);
			Boolean isSuper = get(new BooleanLens(), e, "super", v);
			Boolean isSelf = get(new BooleanLens(), e, CConstraint.SELF_VAR_PREFIX, v);
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
			gen(v, CConstraint.SELF_VAR_PREFIX, n.kind() == C_Special.SELF);
		}
	}
	
	public class CEQVLens implements Lens<C_EQV> {
		public C_EQV fromXML(DomReader v, Element e) {
			LocalInstance li = get(new LocalInstanceRefLens(), e, "local", v);
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
	
	public class CUnaryLens implements Lens<C_UnaryTerm> {
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
	
	public class CBinaryLens implements Lens<C_BinaryTerm> {
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
	
	public class CTermLens implements Lens<C_Term> {
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
	
	public class CTypeLens implements Lens<C_Type> {
		public C_Type fromXML(DomReader v, Element e) {
			Type t = get(new TypeRefLens(), e, "type", v);
			return new C_Type_c(t);
		}
		
		public void toXML(DomGenerator v, C_Type n) {
			v = v.tag("C_Type");
			gen(v, "type", n.type());
		}
	}
	
	
	public class CLitLens implements Lens<C_Lit> {
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
	
	public class CHereLens implements Lens<C_Here_c> {
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
			
			e = getFirstElement(e);
			
			String tag = e.getTagName();
			if (tag.equals("ArrayType")) {
				Type type = get(new TypeRefLens(), e, "base", v);
				return ts.arrayOf(type);
			}
			if (tag.equals("FutureType")) {
				X10NamedType type =(X10NamedType) get(new TypeRefLens(), e, "base", v);
				return ts.createFutureType(Position.COMPILER_GENERATED, type);
			}
			if (tag.equals("NullableType")) {
				X10NamedType type = (X10NamedType) get(new TypeRefLens(), e, "base", v);
				return ts.createNullableType(Position.COMPILER_GENERATED, type);
			}
			if (tag.equals("ClosureType")) {
				Type returnType = get(new TypeRefLens(), e, "return", v);
				List<Type> throwsTypes = get(new ListLens<Type>(new TypeRefLens()), e, "throws", v);
				List<Type> argTypes = get(new ListLens<Type>(new TypeRefLens()), e, "args", v);
				return ts.closure(Position.COMPILER_GENERATED, returnType, argTypes, throwsTypes);
			}
			if (tag.equals("NullType")) {
				return ts.Null();
			}
			if (tag.equals("ClassType")) {
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
			if (tag.equals("ExternalClassType")) {
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
			if (tag.equals("DependentType")) {
				X10Type t = (X10Type) get(new TypeRefLens(), e, "root", v);
				DepParameterExpr dep = (DepParameterExpr) get(new NodeLens(), e, "dep", v);
				// XXX
//				Constraint c = (Constraint) get(new ConstraintLens(), e, "clause");
				return t.dep(dep);
			}
			if (tag.equals("PrimitiveType")) {
				String t = e.getTextContent();
				if (t.equals("void")) {
					return ts.Void();
				}
				if (t.equals("boolean")) {
					return ts.Boolean();
				}
				if (t.equals("byte")) {
					return ts.Byte();
				}
				if (t.equals("short")) {
					return ts.Short();
				}
				if (t.equals("char")) {
					return ts.Char();
				}
				if (t.equals("int")) {
					return ts.Int();
				}
				if (t.equals("long")) {
					return ts.Long();
				}
				if (t.equals("float")) {
					return ts.Float();
				}
				if (t.equals("double")) {
					return ts.Double();
				}
			}
			return null;
		}
		
		public void toXMLRoot(DomGenerator v, polyglot.types.Type t) {
			if (t instanceof ArrayType) {
				v = v.tag("ArrayType");
				gen(v, "base", ((ArrayType) t).base());
			}
			else if (t instanceof X10ClassType) {
				if (t instanceof ParsedClassType) {
					ParsedClassType pct = (ParsedClassType) t;
					if (pct.job() == null) {
						v = v.tag("ExternalClassType");
						gen(v, "name", ts.getTransformedClassName(pct));
						return;
					}
				}
				v = v.tag("ClassType");
				addType(v, (X10ClassType) t, new ClassTypeLens(null));
			}
			else if (t instanceof PrimitiveType) {
				v = v.tag("PrimitiveType");
				v.createText(v.parent, ((PrimitiveType) t).name());
			}
			else if (t instanceof FutureType) {
				v = v.tag("FutureType");
				gen(v, "base", ((FutureType) t).base());
			}
			else if (t instanceof NullableType) {
				v = v.tag("NullableType");
				gen(v, "base", ((NullableType) t).base());
			}
			else if (t instanceof ClosureType) {
				v = v.tag("ClosureType");
				gen(v, "return", ((ClosureType) t).throwTypes());
				gen(v, "throws", ((ClosureType) t).returnType());
				gen(v, "args", ((ClosureType) t).argumentTypes());
			}
			else if (t instanceof NullType) {
				v = v.tag("NullType");
			}
			else {
				assert false;
			}
		}
		
		public void toXML(DomGenerator v, polyglot.types.Type t) {
			v = v.tag("TypeRef");
			if (t instanceof X10Type) {
				X10Type xt = (X10Type) t;
				if (xt.isRootType()) {
					toXMLRoot(v, xt);
				}
				else {
					v = v.tag("DependentType");
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

	public class EnumLens implements Lens<polyglot.util.Enum> {
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

	public <T extends polyglot.util.Enum> T getEnum(Element e, String tag, T[] options, DomReader v) {
		String kindStr = get(new StringLens(), e, tag, v);
		for (int i = 0; i < options.length; i++) {
			if (kindStr.equals(options[i].toString())) {
				return options[i];
			}
		}
		throw new InternalCompilerError("Bad kind " + kindStr);
	}

	public class NewArrayLens implements Lens<NewArray> {
		public NewArray fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base", v);
			List dims = get(new ListLens<Node>(new NodeLens()), e, "dims", v);
			int addDims = get(new IntLens(), e, "addDims", v);
			ArrayInit init = (ArrayInit) get(new NodeLens(), e, "init", v);
			Type t = (Type) get(new TypeRefLens(), e, "type", v);
			return (NewArray) nf.NewArray(position, base, dims, addDims, init).type(t);
		}

		public void toXML(DomGenerator v, NewArray n) {
			 v = v.tag("NewArray");
			gen(v, "position", n.position());
			gen(v, "base", n.baseType());
			gen(v, "dims", n.dims());
			gen(v, "addDims", n.additionalDims());
			gen(v, "init", n.init());
			gen(v, "type", n.type());
		}
	}

	public class SourceFileLens implements Lens<SourceFile> {
		public SourceFile fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			PackageNode packageName = (PackageNode) get(new NodeLens(), e, "package", v);
			List imports = get(new ListLens<Node>(new NodeLens()), e, "imports", v);
			List decls = get(new ListLens<Node>(new NodeLens()), e, "decls", v);
			
			SourceFile n = nf.X10MLSourceFile(position, packageName, imports, decls);
			n = n.source(v.source());
			
			// Create the import table
			n = (SourceFile) n.visit(new InitImportsVisitor(ts.extensionInfo().scheduler().currentJob(), ts, nf));
			
			return n;
		}

		public void toXML(DomGenerator v, SourceFile n) {
			v = v.tag("SourceFile");
			gen(v, "position", n.position());
			gen(v, "package", n.package_());
			gen(v, "imports", n.imports());
			gen(v, "decls", n.decls());
		}
	}

	public class PackageNodeLens implements Lens<PackageNode> {
		public PackageNode fromXML(DomReader v, Element e) {
			Position position = get(new PositionLens(), e, "position", v);
			polyglot.types.Package p = get(new PackageLens(), e, "package", v);
			return nf.PackageNode(position, p);
		}

		public void toXML(DomGenerator v, PackageNode n) {
			// v = v.tag("PackageNode");
//			gen(v, "position", n.position());
			gen(v, "package", n.package_());
		}
	}

	protected final class DefaultNodeLens extends DefaultLens {
		public Object fromXML(DomReader v, Element e) {
			Object o = super.fromXML(v, e);
			
			if (o instanceof Node) {
				return postprocessNode((Node) o);
			}
			
			return o;
		}
		
		protected Node postprocessNode(Node n) {
			if (n.ext() != null)
				n.ext().init(n);
			
			if (n instanceof Closure)
				n = n.del(new X10DelFactory_c().delClosureImpl());
			else if (n instanceof Finish)
				n = n.del(new X10DelFactory_c().delFinishImpl());
			else if (n instanceof ForEach)
				n = n.del(new X10DelFactory_c().delForEachImpl());
			else if (n instanceof ForLoop)
				n = n.del(new X10DelFactory_c().delForLoopImpl());
			else if (n instanceof Async)
				n = n.del(new X10DelFactory_c().delAsyncImpl());
			else if (n instanceof AtEach)
				n = n.del(new X10DelFactory_c().delAtEachImpl());
			else if (n instanceof NullableNode)
				n = n.del(new X10DelFactory_c().delNullableNodeImpl());
			else if (n instanceof FutureNode)
				n = n.del(new X10DelFactory_c().delFutureNodeImpl());
			else if (n instanceof CanonicalTypeNode)
				n = n.del(new X10DelFactory_c().delCanonicalTypeNode());
			else if (n instanceof CanonicalTypeNode)
				n = n.del(new X10DelFactory_c().delCanonicalTypeNode());
			else if (n instanceof Conditional)
				n = n.del(new X10DelFactory_c().delConditional());
			else if (n instanceof FieldDecl)
				n = n.del(new X10DelFactory_c().delFieldDecl());
			else if (n instanceof MethodDecl)
				n = n.del(new X10DelFactory_c().delMethodDecl());
			else if (n instanceof Term)
				n = n.del(new X10DelFactory_c().delTerm());
			else if (n instanceof Id)
				n = n.del(new X10DelFactory_c().delNode());
			else
				n = n.del(new X10DelFactory_c().delNode());

			return n;
		}
	}
	
	public class ObjectLens implements Lens<Object> {
		public Object fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			Lens lens = tagMap().get(tag);
			
			if (lens == null) {
				lens = new DefaultNodeLens();
			}
			
			return lens.fromXML(v, e);
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
			else if (o instanceof Map) {
				new MapLens<Object,Object>(new ObjectLens(), new ObjectLens()).toXML(v, (Map) o);
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
			else if (o instanceof ClosureInstance) {
				new ClosureInstanceRefLens().toXML(v, (ClosureInstance) o);
			}
			else if (o instanceof InitializerInstance) {
				new InitializerInstanceRefLens().toXML(v, (InitializerInstance) o);
			}
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
				new EnumLens("ClassTypeKind", new ClassType.Kind[] {
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
			else if (o instanceof Precedence) {
				new PrecedenceLens().toXML(v, (Precedence) o);
			}
			else if (o instanceof Constraint) {
				new ConstraintLens().toXML(v, (Constraint) o);
			}
			else if (o instanceof C_BinaryTerm) {
				new CBinaryLens().toXML(v, (C_BinaryTerm) o);
			}
			else if (o instanceof C_UnaryTerm) {
				new CUnaryLens().toXML(v, (C_UnaryTerm) o);
			}
			else if (o instanceof C_Here_c) {
				new CHereLens().toXML(v, (C_Here_c) o);
			}
			else if (o instanceof C_Lit) {
				new CLitLens().toXML(v, (C_Lit) o);
			}
			else if (o instanceof C_Special) {
				new CSpecialLens().toXML(v, (C_Special) o);
			}
			else if (o instanceof C_EQV) {
				new CEQVLens().toXML(v, (C_EQV) o);
			}
			else if (o instanceof C_Local) {
				new CLocalLens().toXML(v, (C_Local) o);
			}
			else if (o instanceof C_Field) {
				new CFieldLens().toXML(v, (C_Field) o);
			}
			else if (o instanceof C_Var) {
				new CVarLens().toXML(v, (C_Var) o);
			}
			else if (o instanceof C_Type) {
				new CTypeLens().toXML(v, (C_Type) o);
			}
			else if (o instanceof Promise) {
				new PromiseRefLens().toXML(v, (Promise) o);
			}
			else if (o instanceof ImportTable) {
				return;
			}
			else if (o instanceof X10Del) {
				return;
			}
			else if (o instanceof X10Ext) {
				new X10ExtLens().toXML(v, (X10Ext) o);
			}
			else if (o instanceof Source) {
				return;
			}
			else if (o instanceof String[]) {
				new StringArrayLens().toXML(v, (String[]) o);
			}
			else if (o instanceof SubtypeSet) {
				new SubtypeSetLens().toXML(v, (SubtypeSet) o);
			}
			else if (o == null) {
				return;
			}
			else {
				throw new InternalCompilerError("unimplemented lens for " + o + ": " + o.getClass().getName());
			}
		}
	}

	public <T> void addType(DomGenerator v, T t, Lens<T> lens) {
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
		
		new ObjectLens().toXML(v.tag("key"), key);
	}
	
	public String keyString(Object t) {
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
	
	/**
	 * Default lens that uses reflection.
	 * 
	 * Because the lens calls constructors via reflection with the possibly
	 * incorrect arguments, this lens should only be used for classes whose
	 * constructors have no side effects. It should not be used for type
	 * objects.
	 */
	public class DefaultLens implements X10Dom.Lens<Object> {
		X10Dom dom = X10Dom.this;
		
		public Object fromXML(DomReader v, Element e) {
			String tag = e.getTagName();
			Map<String,Object> m = CollectionFactory.newHashMap();
			
			for (org.w3c.dom.Node x = e.getFirstChild(); x != null; x = x.getNextSibling()) {
				if (x instanceof Element) {
					Element field = (Element) x;
					if (field != null) {
						Element child = dom.getFirstElement(field);
						if (child != null) {
							Object o = dom.new ObjectLens().fromXML(v, child);
							m.put(field.getTagName(), o);
						}
					}
				}
			}
			
			String[] names = new String[] {
					"x10.ast." + tag + "_c",
					"x10.ast." + tag,
					"x10.extension." + tag + "_c",
					"x10.extension." + tag,
					"polyglot.ast." + tag + "_c",
					"polyglot.ast." + tag,
					"x10.types." + tag + "_c",
					"x10.types." + tag,
					"polyglot.types." + tag + "_c",
					"polyglot.types." + tag,
			};
			
			Class c = null;
			
			if (tag.equals("Position")) {
				c = Position.class;
			}
			
			if (c == null) {
				for (String name : names) {
					try {
						c = Class.forName(name);
						break;
					}
					catch (ClassNotFoundException exn) {
					}
				}
			}
			
			if (c == null) {
				throw new InternalCompilerError("Could not find class for " + tag);
			}
			
			Object o = alloc(c, m);
			init(v, tag, e, o, m);
			return o;
		}
		
		void init(DomReader v, String tag, Element e, Object o, Map<String,Object> fieldMap) {
			try {
				for (Class c = o.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
					java.lang.reflect.Field[] fields = c.getDeclaredFields();
					java.lang.reflect.AccessibleObject.setAccessible(fields, true);
					for (int i = 0; i < fields.length; i++) {
						java.lang.reflect.Field field = fields[i];
						if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0)
							continue;
						Object x = fieldMap.get(field.getName());
						x = overrideField(o, field, x);
						if ((field.getModifiers() & java.lang.reflect.Modifier.TRANSIENT) != 0)
							assert x != null; // should override
						try {
							field.set(o, x);
						}
						catch (IllegalAccessException exn) {
						}
					}
				}
			}
			catch (SecurityException exn) {
			}
		}
		
		protected Object overrideField(Object obj, java.lang.reflect.Field field, Object fieldValue) {
			return fieldValue;
		}

		/**
		 * Allocate an instance of class c. We need to invoke a constructor for
		 * c, but do not know how the fields map to the constructor arguments.
		 * So, we just use objects of the appropriate type in the fields map, or
		 * null. A later step (init) will assign the fields correctly.
		 * 
		 */
		Object alloc(Class c, Map<String,Object> fields) {
			if (c.isInterface()) {
				throw new InternalCompilerError("Attempting to instantiate interface " + c.getName());
			}
			
			if ((c.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) != 0) {
				throw new InternalCompilerError("Attempting to instantiate abstract class " + c.getName());
			}
			
			try {
				java.lang.reflect.Constructor[] ctors = c.getDeclaredConstructors();
				java.lang.reflect.AccessibleObject.setAccessible(ctors, true);
				if (ctors.length == 0) {
					throw new InternalCompilerError("No constructor found for " + c);
				}
				else {
					Map m = fields;
					
					Class[] formals = ctors[0].getParameterTypes();
					Object[] actuals = new Object[formals.length];
					FORMALS:
					for (int i = 0; i < formals.length; i++) {
						actuals[i] = null;
						
						// First try the field map.
						for (Iterator j = m.values().iterator(); j.hasNext(); ) {
							Object oj = j.next();
							if (formals[i].isInstance(oj)) {
								actuals[i] = oj;
								continue FORMALS;
							}
						}
						
						// Now, manufacture a value.  This will get overridden when the
						// fields are assigned to.
						if (formals[i].isPrimitive()) {
							if (formals[i] == boolean.class) actuals[i] = Boolean.valueOf(false);
							if (formals[i] == byte.class) actuals[i] = Byte.valueOf((byte) 0);
							if (formals[i] == char.class) actuals[i] = Character.valueOf((char) 0);
							if (formals[i] == short.class) actuals[i] = Short.valueOf((short) 0);
							if (formals[i] == int.class) actuals[i] = Integer.valueOf(0);
							if (formals[i] == long.class) actuals[i] = Long.valueOf(0);
							if (formals[i] == float.class) actuals[i] = Float.valueOf(0);
							if (formals[i] == double.class) actuals[i] = Double.valueOf(0);
						}
						if (formals[i] == Position.class) actuals[i] = Position.COMPILER_GENERATED;
						if (formals[i] == List.class) actuals[i] = Collections.EMPTY_LIST;
						if (formals[i] == Flags.class) actuals[i] = Flags.NONE;
						if (formals[i] == String.class) actuals[i] = "$$$";
						
						if (actuals[i] != null) {
							continue FORMALS;
						}
						
						// Manufacture a value.  Null can cause assertion failures in the constructor body.
//						if (Node.class.isAssignableFrom(formals[i])) {
//							actuals[i] = alloc(formals[i], Collections.EMPTY_MAP);
//						}
					}
					
					return (Object) ctors[0].newInstance(actuals);
				}
			}
			catch (IllegalArgumentException exn) {
				throw new InternalCompilerError("No constructor found for " + c + ".", exn);
			}
			catch (IllegalAccessException exn) {
				throw new InternalCompilerError("No constructor found for " + c + ".", exn);
			}
			catch (InstantiationException exn) {
				throw new InternalCompilerError("No constructor found for " + c + ".", exn);
			}
			catch (InvocationTargetException exn) {
				throw new InternalCompilerError("No constructor found for " + c + ".", exn);
			}
			catch (SecurityException exn) {
				throw new InternalCompilerError("No constructor found for " + c + ".", exn);
			}
		}
		
		String nodeTag(Object n) {
			String tag = n.getClass().getName();
			tag = StringUtil.getShortNameComponent(tag);
			if (tag.endsWith("_c"))
				tag = tag.substring(0,tag.length()-2);
			return tag;
		}
		
		public void toXML(DomGenerator v, Object n) {
			v = v.tag(nodeTag(n));
			
			try {
				for (Class c = n.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
					java.lang.reflect.Field[] fields = c.getDeclaredFields();
					java.lang.reflect.AccessibleObject.setAccessible(fields, true);
					for (int i = 0; i < fields.length; i++) {
						java.lang.reflect.Field field = fields[i];
						if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0)
							continue;
						if ((field.getModifiers() & java.lang.reflect.Modifier.TRANSIENT) != 0)
							continue;
						
						try {
							Object o = field.get(n);
							if (o != null && shouldFollow(n, field.getName(), o)) {
								dom.gen(v, field.getName(), o);
							}
						}
						catch (IllegalAccessException e) {
							throw new InternalCompilerError(e);
						}
					}
				}
			}
			catch (SecurityException e) {
				throw new InternalCompilerError(e);
			}
		}
		
		public boolean shouldFollow(Object obj, String name, Object fieldValue) {
			return true;
		}
	}
	
	public class NodeLens implements Lens<Node> {
		public void toXML(DomGenerator v, Node n) {
			if (n instanceof SourceFile) {
				new SourceFileLens().toXML(v, (SourceFile) n);
				return;
			}
			
			if (n instanceof NewArray) {
				new NewArrayLens().toXML(v, (NewArray) n);
				return;
			}
			
			new DefaultLens().toXML(v, n);
		}

		public Node fromXML(DomReader v, Element e) {
			if (e == null) {
				return null;
			}
			
			if (e.getTagName().equals("SourceFile")) {
				// Need to override to handle import table and source.
				Node n = new SourceFileLens().fromXML(v, e);
				return n;
			}
			
			if (e.getTagName().equals("NewArray")) {
				// Need to override because constructor assertion is too picky.
				Node n = new NewArrayLens().fromXML(v, e);
				return n;
			}
			
			Node n = (Node) new DefaultNodeLens().fromXML(v, e);
			return n;
		}
	}
	
}
