package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import polyglot.ast.*;
import polyglot.ext.x10.ast.*;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.Package;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class X10Dom {
	public <T> T get(AbsLens<T> lens, Element e, String tag) {
		return lens.fromXML(getChild(e, tag));
	}

	public <T> void gen(DomGenerator v, String tag, T n) {
		toXML(v.tag(tag), n);
	}
	
	public Element getChild(Element e, String tag) {
		NodeList l = e.getElementsByTagName(tag);
		return (Element) l.item(0);
	}

	public interface Lens<T extends org.w3c.dom.Node, U> {
		public U fromXML(T e);
		public void toXML(DomGenerator v, U n);
	}

	public interface AbsLens<T> extends Lens<Element,T> { }

	public class StringLens implements AbsLens<String> {
		public String fromXML(Element e) {
			return e.getTextContent();
		}
		public void toXML(DomGenerator v, String s) {
			// v = v.tag("String");
			v.createText(v.parent, s);
		}
	}

	public class ListLens<T> implements AbsLens<List<T>> {
                AbsLens<T> lens;

                ListLens(AbsLens<T> lens) {
                        this.lens = lens;
                }

		public List<T> fromXML(Element e) {
			if (! e.hasChildNodes()) {
				return Collections.EMPTY_LIST;
			}
			NodeList l = e.getChildNodes();
			ArrayList r = new ArrayList(l.getLength());
			for (int i = 0; i < l.getLength(); i++) {
				r.add(lens.fromXML((Element) l.item(i)));
			}
			return r;
		}

		public void toXML(DomGenerator v, List<T> l) {
			for (Iterator<T> i = l.iterator(); i.hasNext(); ) {
				T o = i.next();
				lens.toXML(v, o);
			}
		}
	}


	public <T> T getChild(Element e, String tag, AbsLens<T> lens) {
		NodeList l = e.getElementsByTagName(tag);
		if (l.getLength() == 0)
			return null;
		if (l.getLength() != 1)
			throw new InternalCompilerError("More children with tag " + tag + " than expected.");
		return lens.fromXML((Element) l.item(0));
	}

	public class BooleanLens implements AbsLens<Boolean> {
		public Boolean fromXML(Element e) {
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

	public class AssignOpLens implements AbsLens<Assign.Operator> {
		public Assign.Operator fromXML(Element e) {
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
		public Binary.Operator fromXML(Element e) {
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
		public Unary.Operator fromXML(Element e) {
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
	
	public class CharLens implements AbsLens<Character> {
		public Character fromXML(Element e) {
			String s = e.getTextContent();
			if (s.length() == 0) return null;
			return s.charAt(0);
		}
		
		public void toXML(DomGenerator v, Character n) {
			// v = v.tag("Char");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class IntLens implements AbsLens<Integer> {
		public Integer fromXML(Element e) {
			String s = e.getTextContent();
			try {
				return Integer.parseInt(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad int " + s);
			}
		}
		
		public void toXML(DomGenerator v, Integer n) {
			// v = v.tag("Int");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class LongLens implements AbsLens<Long> {
		public Long fromXML(Element e) {
			String s = e.getTextContent();
			try {
				return Long.parseLong(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad long " + s);
			}
		}
		
		public void toXML(DomGenerator v, Long n) {
			// v = v.tag("Long");
			v.createText(v.parent, n.toString());
		}
	}
	
	public class DoubleLens implements AbsLens<Double> {
		public Double fromXML(Element e) {
			String s = e.getTextContent();
			try {
				return Double.parseDouble(s);
			}
			catch (NumberFormatException ex) {
				throw new InternalCompilerError("Bad long " + s);
			}
		}
		
		public void toXML(DomGenerator v, Double n) {
			// v = v.tag("Double");
			v.createText(v.parent, n.toString());
		}
	}
	public class FlagsLens implements AbsLens<Flags> {
		public Flags fromXML(Element e) {
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
			// v.tag("Flags");
			Set names = n.flags();
			for (Iterator i = names.iterator(); i.hasNext(); ) {
				String fname = (String) i.next();
				gen(v, "flag", fname);
			}
		}
	}


	public class PositionLens implements AbsLens<Position> {
		public Position fromXML(Element e) {
			String path = get(new StringLens(), e, "path");
			String file = get(new StringLens(), e, "file");
			int line = get(new IntLens(), e, "line");
			int column = get(new IntLens(), e, "column");
			int offset = get(new IntLens(), e, "offset");
			int endLine = get(new IntLens(), e, "endLine");
			int endColumn = get(new IntLens(), e, "endColumn");
			int endOffset = get(new IntLens(), e, "endOffset");
			return new Position(path, file, line, column, endLine, endColumn, offset, endOffset);
		}

		public void toXML(DomGenerator v, Position n) {
			// v = v.tag("Position");
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

	public class ClassTypeLens implements AbsLens<ClassType> {
		public ClassType fromXML(Element e) {
			Package p = get(new PackageLens(), e, "package");
			String name = get(new StringLens(), e, "name");
			Resolver r = ts.systemResolver();
			if (p != null) {
				r = ts.packageContextResolver(p);
			}
			try {
				Named n = r.find(name);
				if (n instanceof ClassType) {
					return (ClassType) n;
				}
				throw new InternalCompilerError("Not a class " + name);
			}
			catch (SemanticException ex) {
				// not found
//				throw new InternalCompilerError(ex);
				ParsedClassType ct = ts.createClassType();
				try {
				ts.systemResolver().addNamed(name, ct);
				}
				catch (SemanticException ex2) {
					throw new InternalCompilerError(ex2);
				}
				Type container = get(new TypeRefLens(), e, "container");
				Flags flags = get(new FlagsLens(), e, "flags");
				Type superclass = get(new TypeRefLens(), e, "superclass");
				List<Type> interfaces = get(new ListLens<Type>(new TypeRefLens()), e, "interfaces");
				List<FieldInstance> fis = get(new ListLens<FieldInstance>(new FieldInstanceLens()), e, "fields");
				List<MethodInstance> mis = get(new ListLens<MethodInstance>(new MethodInstanceLens()), e, "methods");
				List<ConstructorInstance> cis = get(new ListLens<ConstructorInstance>(new ConstructorInstanceLens()), e, "constructors");
				List<ClassType> ts = get(new ListLens<ClassType>(new ClassTypeLens()), e, "memberClasses");
				ct.setContainer((ReferenceType) container);
				ct.setFlags(flags);
				ct.package_(p);
				ct.name(name);
				ct.superType(superclass);
				ct.setInterfaces(interfaces);
				ct.setFields(fis);
				ct.setMethods(mis);
				ct.setConstructors(cis);
				ct.setMemberClasses(ts);
				return ct;
			}
		}

		public void toXML(DomGenerator v, ClassType n) {
			v = v.tag("ClassType");
			gen(v, "package", n.package_());
			gen(v, "name", n.name());
			gen(v, "flags", n.flags());
			gen(v, "superclass", n.superType());
			gen(v, "interfaces", n.interfaces());
			gen(v, "fields", n.fields());
			gen(v, "methods", n.methods());
			gen(v, "constructors", n.constructors());
			gen(v, "memberClasses", n.memberClasses());
		}
	}

	public class ConstructorInstanceLens implements AbsLens<ConstructorInstance> {
		public ConstructorInstance fromXML(Element e) {
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
		public MethodInstance fromXML(Element e) {
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

	public class FieldInstanceLens implements AbsLens<FieldInstance> {
		public FieldInstance fromXML(Element e) {
			return null;
		}

		public void toXML(DomGenerator v, FieldInstance n) {
			v = v.tag("FieldInstance");
			gen(v, "flags", n.flags());
			gen(v, "name", n.name());
			if (n.isConstant()) {
				gen(v, "constant", n.constantValue().toString());
			}
		}
	}

	public class LocalInstanceLens implements AbsLens<LocalInstance> {
		public LocalInstance fromXML(Element e) {
			return null;
		}

		public void toXML(DomGenerator v, LocalInstance n) {
			v = v.tag("LocalInstance");
			gen(v, "flags", n.flags());
			gen(v, "name", n.name());
			if (n.isConstant()) {
				gen(v, "constant", n.constantValue().toString());
			}
		}
	}

	public class InstanceofLens implements AbsLens<Instanceof> {
		public Instanceof fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type");
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
		public Async fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr place = (Expr) get(new NodeLens(), e, "place");
			List<Node> clocks = get(new ListLens<Node>(new NodeLens()), e, "clocks");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
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
		public Atomic fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr place = (Expr) get(new NodeLens(), e, "place");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
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
		public Future fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr place = (Expr) get(new NodeLens(), e, "place");
			Expr body = (Expr) get(new NodeLens(), e, "body");
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
		public FutureNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode tn = get(new CanonicalTypeNodeLens(), e, "type");
			return nf.FutureNode(position, tn);
		}
		
		public void toXML(DomGenerator v, FutureNode n) {
//			v = v.tag("FutureNode");
			gen(v, "position", n.position());
			gen(v, "type", n.type());
		}
	}
	
	public class NullableNodeLens implements AbsLens<NullableNode> {
		public NullableNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode tn = get(new CanonicalTypeNodeLens(), e, "type");
			return nf.Nullable(position, tn);
		}
		
		public void toXML(DomGenerator v, NullableNode n) {
//			v = v.tag("NullableNode");
			gen(v, "position", n.position());
			gen(v, "type", n.type());
		}
	}
	
	public class RangeLens implements AbsLens<Range> {
		public Range fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr lb = (Expr) get(new NodeLens(), e, "lowerBound");
			Expr ub = (Expr) get(new NodeLens(), e, "upperBound");
			Expr stride = (Expr) get(new NodeLens(), e, "stride");
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
		public Region fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List l = get(new ListLens<Node>(new NodeLens()), e, "ranges");
			return new Region_c(position, l);
		}
		
		public void toXML(DomGenerator v, Region n) {
			// v = v.tag("Region");
			gen(v, "position", n.position());
			gen(v, "ranges", n.ranges());
		}
	}

	public class HereLens implements AbsLens<Here> {
		public Here fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			return nf.Here(position);
		}

		public void toXML(DomGenerator v, Here n) {
			// v = v.tag("Here");
			gen(v, "position", n.position());
		}
	}

	public class WhenLens implements AbsLens<When> {
		public When fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			Stmt statement = (Stmt) get(new NodeLens(), e, "statement");
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
		public Next fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			return nf.Next(position);
		}

		public void toXML(DomGenerator v, Next n) {
			// v = v.tag("Next");
			gen(v, "position", n.position());
		}
	}

	public class NowLens implements AbsLens<Now> {
		public Now fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr clock = (Expr) get(new NodeLens(), e, "clock");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
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
		public X10ClassDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			Id name = (Id) get(new NodeLens(), e, "name");
			List properties = get(new ListLens<Node>(new NodeLens()), e, "properties");
			Expr ci = (Expr) get(new NodeLens(), e, "ci");
			TypeNode superClass = (TypeNode) get(new NodeLens(), e, "superClass");
			List interfaces = get(new ListLens<Node>(new NodeLens()), e, "interfaces");
			ClassBody body = (ClassBody) get(new NodeLens(), e, "body");
			return (X10ClassDecl) nf.ClassDecl(position, flags, name, properties, ci, superClass, interfaces, body);
		}

		public void toXML(DomGenerator v, X10ClassDecl n) {
			// v = v.tag("ClassDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "name", n.id());
                        // XXX
//			gen(v, "properties", n.properties());
//			gen(v, "ci", n.ci());
			gen(v, "superClass", n.superClass());
			gen(v, "interfaces", n.interfaces());
			gen(v, "body", n.body());
		}
	}

	public class ValueClassDeclLens implements AbsLens<ValueClassDecl> {
		public ValueClassDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			Id name = (Id) get(new NodeLens(), e, "name");
			List properties = get(new ListLens<Node>(new NodeLens()), e, "properties");
			Expr ci = (Expr) get(new NodeLens(), e, "ci");
			TypeNode superClass = (TypeNode) get(new NodeLens(), e, "superClass");
			List interfaces = get(new ListLens<Node>(new NodeLens()), e, "interfaces");
			ClassBody body = (ClassBody) get(new NodeLens(), e, "body");
			return nf.ValueClassDecl(position, flags, name, properties, ci, superClass, interfaces, body);
		}

		public void toXML(DomGenerator v, ValueClassDecl n) {
			// v = v.tag("ValueClassDecl");
			gen(v, "position", n.position());
			gen(v, "flags", n.flags());
			gen(v, "name", n.id());
                        // XXX
//			gen(v, "properties", n.properties());
//			gen(v, "ci", n.ci());
			gen(v, "superClass", n.superClass());
			gen(v, "interfaces", n.interfaces());
			gen(v, "body", n.body());
		}
	}

	public class AwaitLens implements AbsLens<Await> {
		public Await fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			return nf.Await(position, expression);
		}

		public void toXML(DomGenerator v, Await n) {
			// v = v.tag("Await");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class X10ArrayAccess1Lens implements AbsLens<X10ArrayAccess1> {
		public X10ArrayAccess1 fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr array = (Expr) get(new NodeLens(), e, "array");
			Expr index = (Expr) get(new NodeLens(), e, "index");
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
		public X10ArrayAccess fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr array = (Expr) get(new NodeLens(), e, "array");
			List index = get(new ListLens<Node>(new NodeLens()), e, "index");
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
		public ArrayConstructor fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base");
			boolean isSafe = get(new BooleanLens(), e, "isSafe");
			boolean isValue = get(new BooleanLens(), e, "isValue");
			Expr d = (Expr) get(new NodeLens(), e, "distribution");
			Expr i = (Expr) get(new NodeLens(), e, "initializer");
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
		public Point fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List values = get(new ListLens<Node>(new NodeLens()), e, "values");
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
		public RemoteCall fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Receiver target = (Receiver) get(new NodeLens(), e, "target");
			Id name = (Id) get(new NodeLens(), e, "name");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
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
		public Call fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Receiver target = (Receiver) get(new NodeLens(), e, "target");
			Id name = (Id) get(new NodeLens(), e, "name");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
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
		public ConstantDistMaker fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
			return nf.ConstantDistMaker(position, (Expr) arguments.get(0), (Expr) arguments.get(1));
		}

		public void toXML(DomGenerator v, ConstantDistMaker n) {
			// v = v.tag("ConstantDistMaker");
			gen(v, "position", n.position());
			gen(v, "arguments", n.arguments());
		}
	}

	public class NewLens implements AbsLens<New> {
		public New fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr qualifier = (Expr) get(new NodeLens(), e, "qualifier");
			TypeNode objectType = (TypeNode) get(new NodeLens(), e, "objectType");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
			ClassBody body = (ClassBody) get(new NodeLens(), e, "body");
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
		public For fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List inits = get(new ListLens<Node>(new NodeLens()), e, "inits");
			Expr condition = (Expr) get(new NodeLens(), e, "condition");
			List iters = get(new ListLens<Node>(new NodeLens()), e, "iters");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
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
		public Finish fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
			return nf.Finish(position, body);
		}

		public void toXML(DomGenerator v, Finish n) {
			// v = v.tag("Finish");
			gen(v, "position", n.position());
			gen(v, "body", n.body());
		}
	}

	public class DepParameterExprLens implements AbsLens<DepParameterExpr> {
		public DepParameterExpr fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List l = get(new ListLens<Node>(new NodeLens()), e, "arguments");
			return nf.DepParameterExpr(position, l);
		}

		public void toXML(DomGenerator v, DepParameterExpr n) {
			// v = v.tag("DepParameterExpr");
			gen(v, "position", n.position());
			gen(v, "arguments", n.args());
		}
	}

	public class GenParameterExprLens implements AbsLens<GenParameterExpr> {
		public GenParameterExpr fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List l = get(new ListLens<Node>(new NodeLens()), e, "arguments");
			return nf.GenParameterExpr(position, l);
		}

		public void toXML(DomGenerator v, GenParameterExpr n) {
			// v = v.tag("GenParameterExpr");
			gen(v, "position", n.position());
			gen(v, "arguments", n.args());
		}
	}

	public class X10ArrayTypeNodeLens implements AbsLens<X10ArrayTypeNode> {
		public X10ArrayTypeNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base");
			boolean isValueType = get(new BooleanLens(), e, "isValueType");
			DepParameterExpr indexedSet = (DepParameterExpr) get(new NodeLens(), e, "indexedSet");
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
		public Assign fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr left = (Expr) get(new NodeLens(), e, "left");
			Assign.Operator operator = new AssignOpLens().fromXML(getChild(e, "operator"));
			Expr right = (Expr) get(new NodeLens(), e, "right");
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
		public X10ArrayAccessAssign fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			X10ArrayAccess left = (X10ArrayAccess) get(new NodeLens(), e, "left");
			Assign.Operator operator = get(new AssignOpLens(), e, "operator");
			Expr right = (Expr) get(new NodeLens(), e, "right");
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
		public X10ArrayAccess1Assign fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			X10ArrayAccess1 left = (X10ArrayAccess1) get(new NodeLens(), e, "left");
			Assign.Operator operator = get(new AssignOpLens(), e, "operator");
			Expr right = (Expr) get(new NodeLens(), e, "right");
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
		public Binary fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr left = (Expr) get(new NodeLens(), e, "left");
			Binary.Operator operator = get(new BinaryOpLens(), e, "operator");
			Expr right = (Expr) get(new NodeLens(), e, "right");
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
		public Unary fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Unary.Operator operator = get(new UnaryOpLens(), e, "operator");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
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
		public X10ArrayAccessUnary fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Unary.Operator operator = get(new UnaryOpLens(), e, "operator");
			X10ArrayAccess expression = (X10ArrayAccess) get(new NodeLens(), e, "expression");
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
		public X10ArrayAccess1Unary fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Unary.Operator operator = get(new UnaryOpLens(), e, "operator");
			X10ArrayAccess1 expression = (X10ArrayAccess1) get(new NodeLens(), e, "expression");
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
		public Tuple fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Receiver p = (Receiver) get(new NodeLens(), e, "pointReceiver");
			Receiver r = (Receiver) get(new NodeLens(), e, "regionReceiver");
			List a = get(new ListLens<Node>(new NodeLens()), e, "arguments");
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
		public X10Formal fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type");
			Id name = (Id) get(new NodeLens(), e, "name");
			List vars = get(new ListLens<Node>(new NodeLens()), e, "vars");
			boolean isUnnamed = get(new BooleanLens(), e, "isUnnamed");
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
		public Formal fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type");
			Id name = (Id) get(new NodeLens(), e, "name");
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
		public ParExpr fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			return nf.ParExpr(position, expression);
		}

		public void toXML(DomGenerator v, ParExpr n) {
			// v = v.tag("ParExpr");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class PlaceCastLens implements AbsLens<PlaceCast> {
		public PlaceCast fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr place = (Expr) get(new NodeLens(), e, "place");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
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
		public polyglot.ast.Field fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Receiver target = (Receiver) get(new NodeLens(), e, "target");
			Id name = (Id) get(new NodeLens(), e, "name");
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
		public X10FieldDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			DepParameterExpr thisClause = (DepParameterExpr) get(new NodeLens(), e, "thisClause");
			Flags flags = get(new FlagsLens(), e, "flags");
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type");
			Id name = (Id) get(new NodeLens(), e, "name");
			Expr init = (Expr) get(new NodeLens(), e, "init");
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
		public Cast fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			return nf.Cast(position, type, expression);
		}

		public void toXML(DomGenerator v, Cast n) {
			// v = v.tag("Cast");
			gen(v, "position", n.position());
			gen(v, "type", n.castType());
			gen(v, "expression", n.expr());
		}
	}
	
	public class X10MethodDeclLens implements AbsLens<X10MethodDecl> {
		public X10MethodDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			DepParameterExpr thisClause = (DepParameterExpr) get(new NodeLens(), e, "thisClause");
			Flags flags = get(new FlagsLens(), e, "flags");
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType");
			Id name = (Id) get(new NodeLens(), e, "name");
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals");
			Expr whereClause = (Expr) get(new NodeLens(), e, "whereClause");
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws");
			Block body = (Block) get(new NodeLens(), e, "body");
			return (X10MethodDecl) nf.MethodDecl(position, thisClause, flags, returnType, name, formals, whereClause, throwTypes, body);
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
		public MethodDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType");
			Id name = (Id) get(new NodeLens(), e, "name");
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals");
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public LocalDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type");
			Id name = (Id) get(new NodeLens(), e, "name");
			Expr init = (Expr) get(new NodeLens(), e, "init");
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
		public X10ConstructorDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			Id name = (Id) get(new NodeLens(), e, "name");
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType");
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals");
			Expr argWhereClause = (Expr) get(new NodeLens(), e, "argWhereClause");
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public Package fromXML(Element e) {
			String s = e.getTextContent();
			try {
				return ts.packageForName(s);
			}
			catch (SemanticException ex) {
				throw new InternalCompilerError(ex);
			}
		}
		
		public void toXML(DomGenerator v, polyglot.types.Package t) {
			// v = v.tag("Package");
			v.createText(v.parent, ((Package) t).fullName());
		}
	}
	
	public class TypeRefLens implements AbsLens<Type> {
		public Type fromXML(Element e) {
			String tag = e.getTagName();
			if (tag.equals("array")) {
				org.w3c.dom.Node n = e.getFirstChild();
				Type type = new TypeRefLens().fromXML((Element) n);
				return ts.arrayOf(type);
			}
			if (tag.equals("class")) {
				try {
					Named n = ts.systemResolver().find(e.getTextContent());
					if (n instanceof Type) {
						return (Type) n;
					}
				}
				catch (SemanticException ex) {
				}
				return null;
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
		
		public void toXML(DomGenerator v, polyglot.types.Type t) {
			// v = v.tag("Type");
			if (t instanceof ArrayType) {
				gen(v, "array", ((ArrayType) t).base());
			}
			else if (t instanceof ClassType) {
				v = v.tag("class");
				v.createText(v.parent, ((ClassType) t).fullName());
			}
			else if (t instanceof PrimitiveType) {
				v = v.tag(((PrimitiveType) t).name());
			}
		}
	}

	public class CanonicalTypeNodeLens implements AbsLens<CanonicalTypeNode> {
		public CanonicalTypeNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Type type = (Type) get(new TypeRefLens(), e, "type");
			return nf.CanonicalTypeNode(position, type);
		}
		
		public void toXML(DomGenerator v, CanonicalTypeNode n) {
			// v = v.tag("CanonicalTypeNode");
			gen(v, "position", n.position());
			gen(v, "type", n.type());
		}
	}
	
	public class X10CanonicalTypeNodeLens implements AbsLens<X10CanonicalTypeNode> {
		public X10CanonicalTypeNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Type type = (Type) get(new TypeRefLens(), e, "type");
			GenParameterExpr gen = (GenParameterExpr) get(new NodeLens(), e, "gen");
			DepParameterExpr dep = (DepParameterExpr) get(new NodeLens(), e, "dep");
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
		public PropertyDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			TypeNode type = (TypeNode) get(new NodeLens(), e, "type");
			Id name = (Id) get(new NodeLens(), e, "name");
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
		public Special fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Special.Kind kind = (Special.Kind) get(new EnumLens("SpecialKind", new Special.Kind[] { Special.SUPER, Special.THIS }), e, "kind");
			TypeNode outer = (TypeNode) get(new NodeLens(), e, "qualifier");
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
		public Local fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Id name = (Id) get(new NodeLens(), e, "name");
			return nf.Local(position, name);
		}

		public void toXML(DomGenerator v, Local n) {
			// v = v.tag("Local");
			gen(v, "position", n.position());
			gen(v, "name", n.id());
		}
	}

	public class BooleanLitLens implements AbsLens<BooleanLit> {
		public BooleanLit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			boolean value = get(new BooleanLens(), e, "value");
			return nf.BooleanLit(position, value);
		}

		public void toXML(DomGenerator v, BooleanLit n) {
			// v = v.tag("BooleanLit");
			gen(v, "position", n.position());
			gen(v, "value", n.value());
		}
	}

	public class StmtSeqLens implements AbsLens<StmtSeq> {
		public StmtSeq fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List statements = get(new ListLens<Node>(new NodeLens()), e, "statements");
			return nf.StmtSeq(position, statements);
		}

		public void toXML(DomGenerator v, StmtSeq n) {
			// v = v.tag("StmtSeq");
			gen(v, "position", n.position());
			gen(v, "statements", n.statements());
		}
	}

	public class IfLens implements AbsLens<If> {
		public If fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr condition = (Expr) get(new NodeLens(), e, "condition");
			Stmt consequent = (Stmt) get(new NodeLens(), e, "then");
			Stmt alternative = (Stmt) get(new NodeLens(), e, "else");
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
		public RegionMaker fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
			return nf.RegionMaker(position, (Expr) arguments.get(0), (Expr) arguments.get(1));
		}

		public void toXML(DomGenerator v, RegionMaker n) {
			// v = v.tag("RegionMaker");
			gen(v, "position", n.position());
			gen(v, "arguments", n.arguments());
		}
	}

	public class RectRegionMakerLens implements AbsLens<RectRegionMaker> {
		public RectRegionMaker fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Receiver receiver = (Receiver) get(new NodeLens(), e, "receiver");
			Id name = (Id) get(new NodeLens(), e, "name");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
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
		public While fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr condition = (Expr) get(new NodeLens(), e, "condition");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
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

		public polyglot.util.Enum fromXML(Element e) {
			String kindStr = new StringLens().fromXML(e);
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
		public IntLit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			IntLit.Kind kind = (IntLit.Kind) get(new EnumLens("IntLitKind", new IntLit.Kind[] { IntLit.INT, IntLit.LONG }), e, "kind");
			long value = get(new LongLens(), e, "value");
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
		public StringLit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			String value = get(new StringLens(), e, "value");
			return nf.StringLit(position, value);
		}

		public void toXML(DomGenerator v, StringLit n) {
			// v = v.tag("StringLit");
			gen(v, "position", n.position());
			gen(v, "value", n.value());
		}
	}

	public class FloatLitLens implements AbsLens<FloatLit> {
		public FloatLit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			FloatLit.Kind kind = (FloatLit.Kind) get(new EnumLens("FloatLitKind", new FloatLit.Kind[] {
					FloatLit.FLOAT, FloatLit.DOUBLE}), e, "kind");
			double value = get(new DoubleLens(), e, "value");
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
		public CharLit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			char value = get(new CharLens(), e, "value");
			return nf.CharLit(position, value);
		}

		public void toXML(DomGenerator v, CharLit n) {
			// v = v.tag("CharLit");
			gen(v, "position", n.position());
			gen(v, "value", n.value());
		}
	}

	public class AssignPropertyCallLens implements AbsLens<AssignPropertyCall> {
		public AssignPropertyCall fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
			return nf.AssignPropertyCall(position, arguments);
		}

		public void toXML(DomGenerator v, AssignPropertyCall n) {
			// v = v.tag("AssignPropertyCall");
			gen(v, "position", n.position());
			gen(v, "arguments", n.args());
		}
	}

	public class ConditionalLens implements AbsLens<Conditional> {
		public Conditional fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr condition = (Expr) get(new NodeLens(), e, "condition");
			Expr consequent = (Expr) get(new NodeLens(), e, "then");
			Expr alternative = (Expr) get(new NodeLens(), e, "else");
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
		public ConstructorCall fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			ConstructorCall.Kind kind = (ConstructorCall.Kind) get(new EnumLens("ConstructorCallKind", new ConstructorCall.Kind[] { ConstructorCall.THIS, ConstructorCall.SUPER }), e, "kind");
			Expr outer = (Expr) get(new NodeLens(), e, "outer");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
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
		public Closure fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals");
			TypeNode returnType = (TypeNode) get(new NodeLens(), e, "returnType");
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public ClosureCall fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr target = (Expr) get(new NodeLens(), e, "target");
			List arguments = get(new ListLens<Node>(new NodeLens()), e, "arguments");
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
		public AnnotationNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode tn = (TypeNode) get(new NodeLens(), e, "type");
			return nf.AnnotationNode(position, tn);
		}

		public void toXML(DomGenerator v, AnnotationNode n) {
			// v = v.tag("AnnotationNode");
			gen(v, "position", n.position());
			gen(v, "type", n.annotationType());
		}
	}

	public class IdLens implements AbsLens<Id> {
		public Id fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			String name = get(new StringLens(), e, "name");
			return nf.Id(position, name);
		}

		public void toXML(DomGenerator v, Id n) {
			// v = v.tag("Id");
			gen(v, "position", n.position());
			gen(v, "name", n.id());
		}
	}


	public class ArrayAccessLens implements AbsLens<ArrayAccess> {
		public ArrayAccess fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr array = (Expr) get(new NodeLens(), e, "array");
			Expr index = (Expr) get(new NodeLens(), e, "index");
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
		public ArrayInit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List elements = get(new ListLens<Node>(new NodeLens()), e, "elements");
			return nf.ArrayInit(position, elements);
		}

		public void toXML(DomGenerator v, ArrayInit n) {
			// v = v.tag("ArrayInit");
			gen(v, "position", n.position());
			gen(v, "elements", n.elements());
		}
	}

	public class AssertLens implements AbsLens<Assert> {
		public Assert fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr condition = (Expr) get(new NodeLens(), e, "condition");
			Expr errorMessage = (Expr) get(new NodeLens(), e, "errorMessage");
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
		public LocalAssign fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Local left = (Local) get(new NodeLens(), e, "left");
			Assign.Operator operator = get(new AssignOpLens(), e, "operator");
			Expr right = (Expr) get(new NodeLens(), e, "right");
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
		public FieldAssign fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Field left = (Field) get(new NodeLens(), e, "left");
			Assign.Operator operator = get(new AssignOpLens(), e, "operator");
			Expr right = (Expr) get(new NodeLens(), e, "right");
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
		public ArrayAccessAssign fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			ArrayAccess left = (ArrayAccess) get(new NodeLens(), e, "left");
			Assign.Operator operator = get(new AssignOpLens(), e, "operator");
			Expr right = (Expr) get(new NodeLens(), e, "right");
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
		public Block fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List statements = get(new ListLens<Node>(new NodeLens()), e, "statements");
			return nf.Block(position, statements);
		}

		public void toXML(DomGenerator v, Block n) {
			// v = v.tag("Block");
			gen(v, "position", n.position());
			gen(v, "statements", n.statements());
		}
	}

	public class SwitchBlockLens implements AbsLens<SwitchBlock> {
		public SwitchBlock fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List statements = get(new ListLens<Node>(new NodeLens()), e, "statements");
			return nf.SwitchBlock(position, statements);
		}

		public void toXML(DomGenerator v, SwitchBlock n) {
			// v = v.tag("SwitchBlock");
			gen(v, "position", n.position());
			gen(v, "statements", n.statements());
		}
	}

	public <T extends polyglot.util.Enum> T getEnum(Element e, String tag, T[] options) {
		String kindStr = get(new StringLens(), e, tag);
		for (int i = 0; i < options.length; i++) {
			if (kindStr.equals(options[i].toString())) {
				return options[i];
			}
		}
		throw new InternalCompilerError("Bad kind " + kindStr);
	}

	public class BranchLens implements AbsLens<Branch> {
		public Branch fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Branch.Kind kind = (Branch.Kind) get(new EnumLens("BranchKind", new Branch.Kind[] { Branch.BREAK, Branch.CONTINUE }), e, "kind");
			Id label = (Id) get(new NodeLens(), e, "label");
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
		public Case fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			return nf.Case(position, expression);
		}

		public void toXML(DomGenerator v, Case n) {
			// v = v.tag("Case");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class CatchLens implements AbsLens<Catch> {
		public Catch fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Formal formal = (Formal) get(new NodeLens(), e, "formal");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public ClassBody fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List members = get(new ListLens<Node>(new NodeLens()), e, "members");
			return nf.ClassBody(position, members);
		}

		public void toXML(DomGenerator v, ClassBody n) {
			// v = v.tag("ClassBody");
			gen(v, "position", n.position());
			gen(v, "members", n.members());
		}
	}

	public class ClassLitLens implements AbsLens<ClassLit> {
		public ClassLit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode typeNode = (TypeNode) get(new NodeLens(), e, "typeNode");
			return nf.ClassLit(position, typeNode);
		}

		public void toXML(DomGenerator v, ClassLit n) {
			// v = v.tag("ClassLit");
			gen(v, "position", n.position());
			gen(v, "typeNode", n.typeNode());
		}
	}

	public class ConstructorDeclLens implements AbsLens<ConstructorDecl> {
		public ConstructorDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			Id name = (Id) get(new NodeLens(), e, "name");
			List formals = get(new ListLens<Node>(new NodeLens()), e, "formals");
			List throwTypes = get(new ListLens<Node>(new NodeLens()), e, "throws");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public AtEach fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Formal formal = (Formal) get(new NodeLens(), e, "formal");
			Expr domain = (Expr) get(new NodeLens(), e, "domain");
			List clocks = get(new ListLens<Node>(new NodeLens()), e, "clocks");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public ForEach fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Formal formal = (Formal) get(new NodeLens(), e, "formal");
			Expr domain = (Expr) get(new NodeLens(), e, "domain");
			List clocks = get(new ListLens<Node>(new NodeLens()), e, "clocks");
			Block body = (Block) get(new NodeLens(), e, "body");
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

	public class DoLens implements AbsLens<Do> {
		public Do fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
			Expr condition = (Expr) get(new NodeLens(), e, "condition");
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
		public Empty fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			return nf.Empty(position);
		}

		public void toXML(DomGenerator v, Empty n) {
			// v = v.tag("Empty");
			gen(v, "position", n.position());
		}
	}

	public class EvalLens implements AbsLens<Eval> {
		public Eval fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			return nf.Eval(position, expression);
		}

		public void toXML(DomGenerator v, Eval n) {
			// v = v.tag("Eval");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class ImportLens implements AbsLens<Import> {
		public Import fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Import.Kind kind = (Import.Kind) get(new EnumLens("ImportKind", new Import.Kind[] { Import.PACKAGE, Import.CLASS }), e, "kind");
			String name = get(new StringLens(), e, "name");
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
		public Initializer fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Flags flags = get(new FlagsLens(), e, "flags");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public Labeled fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Id label = (Id) get(new NodeLens(), e, "label");
			Stmt body = (Stmt) get(new NodeLens(), e, "body");
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
		public LocalClassDecl fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			ClassDecl decl = (ClassDecl) get(new NodeLens(), e, "decl");
			return nf.LocalClassDecl(position, decl);
		}

		public void toXML(DomGenerator v, LocalClassDecl n) {
			// v = v.tag("LocalClassDecl");
			gen(v, "position", n.position());
			gen(v, "decl", n.decl());
		}
	}

	public class NewArrayLens implements AbsLens<NewArray> {
		public NewArray fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base");
			List dims = get(new ListLens<Node>(new NodeLens()), e, "dims");
			int addDims = get(new IntLens(), e, "addDims");
			ArrayInit init = (ArrayInit) get(new NodeLens(), e, "init");
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
		public NullLit fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			return nf.NullLit(position);
		}

		public void toXML(DomGenerator v, NullLit n) {
			// v = v.tag("NullLit");
			gen(v, "position", n.position());
		}
	}

	public class ReturnLens implements AbsLens<Return> {
		public Return fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			return nf.Return(position, expression);
		}

		public void toXML(DomGenerator v, Return n) {
			// v = v.tag("Return");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class SourceCollectionLens implements AbsLens<SourceCollection> {
		public SourceCollection fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			List sources = get(new ListLens<Node>(new NodeLens()), e, "sources");
			return nf.SourceCollection(position, sources);
		}

		public void toXML(DomGenerator v, SourceCollection n) {
			// v = v.tag("SourceCollection");
			gen(v, "position", n.position());
			gen(v, "sources", n.sources());
		}
	}

	public class SourceFileLens implements AbsLens<SourceFile> {
		public SourceFile fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			PackageNode packageName = (PackageNode) get(new NodeLens(), e, "package");
			List imports = get(new ListLens<Node>(new NodeLens()), e, "imports");
			List decls = get(new ListLens<Node>(new NodeLens()), e, "decls");
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
		public Switch fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			List elements = get(new ListLens<Node>(new NodeLens()), e, "elements");
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
		public Synchronized fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			Block body = (Block) get(new NodeLens(), e, "body");
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
		public Throw fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Expr expression = (Expr) get(new NodeLens(), e, "expression");
			return nf.Throw(position, expression);
		}

		public void toXML(DomGenerator v, Throw n) {
			// v = v.tag("Throw");
			gen(v, "position", n.position());
			gen(v, "expression", n.expr());
		}
	}

	public class TryLens implements AbsLens<Try> {
		public Try fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			Block tryBlock = (Block) get(new NodeLens(), e, "tryBlock");
			List catchBlocks = get(new ListLens<Node>(new NodeLens()), e, "catchBlocks");
			Block finallyBlock = (Block) get(new NodeLens(), e, "finallyBlock");
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
		public ArrayTypeNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			TypeNode base = (TypeNode) get(new NodeLens(), e, "base");
			return nf.ArrayTypeNode(position, base);
		}

		public void toXML(DomGenerator v, ArrayTypeNode n) {
			// v = v.tag("ArrayTypeNode");
			gen(v, "position", n.position());
			gen(v, "base", n.base());
		}
	}

	public class PackageNodeLens implements AbsLens<PackageNode> {
		public PackageNode fromXML(Element e) {
			Position position = get(new PositionLens(), e, "position");
			polyglot.types.Package p = get(new PackageLens(), e, "package");
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

	public Object fromXMLJunk(Element e) {
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

		return new NodeLens().fromXML(e);
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
			new ListLens<Node>(new NodeLens()).toXML(v, (List) o);
		}
		else if (o instanceof Position) {
			new PositionLens().toXML(v, (Position) o);
		}
		else if (o instanceof Flags) {
			new FlagsLens().toXML(v, (Flags) o);
		}
		else if (o instanceof LocalInstance) {
			new LocalInstanceLens().toXML(v, (LocalInstance) o);
		}
		else if (o instanceof MethodInstance) {
			new MethodInstanceLens().toXML(v, (MethodInstance) o);
		}
		else if (o instanceof ConstructorInstance) {
			new ConstructorInstanceLens().toXML(v, (ConstructorInstance) o);
		}
		else if (o instanceof FieldInstance) {
			new FieldInstanceLens().toXML(v, (FieldInstance) o);
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
		else if (o == null) {
			return;
		}
		else {
			throw new InternalCompilerError("unimplemented lens for " + o + ": " + o.getClass().getName());
		}
	}
	
	public class NodeLens implements AbsLens<Node> {
		public void toXML(DomGenerator v, Node n) {
			String tag = n.getClass().getName();
			if (tag.endsWith("_c"))
				tag = tag.substring(0,tag.length()-2);
			v = v.tag(tag);
                        gen(v, "position", n.position());
			LensFactory f = new LensFactory(v);
			f.visitAppropriate(n);
			if (n instanceof Expr) {
				gen(v, "type", ((Expr) n).type());
			}
		}

		public Node fromXML(Element e) {
			Node n = fromXML2(e);
			if (n instanceof Expr) {
				Type t = get(new TypeRefLens(), e, "type");
				n = ((Expr) n).type(t);
			}
			return n;
		}
		
		public Node fromXML2(Element e) {
			String tag = e.getTagName();
			if (tag.equals("Instanceof")) return new InstanceofLens().fromXML(e);
			if (tag.equals("Async")) return new AsyncLens().fromXML(e);
			if (tag.equals("Atomic")) return new AtomicLens().fromXML(e);
			if (tag.equals("Future")) return new FutureLens().fromXML(e);
			if (tag.equals("Here")) return new HereLens().fromXML(e);
			if (tag.equals("When")) return new WhenLens().fromXML(e);
			if (tag.equals("Next")) return new NextLens().fromXML(e);
			if (tag.equals("Now")) return new NowLens().fromXML(e);
			if (tag.equals("ClassDecl")) return new ClassDeclLens().fromXML(e);
			if (tag.equals("ValueClassDecl")) return new ValueClassDeclLens().fromXML(e);
			if (tag.equals("Await")) return new AwaitLens().fromXML(e);
			if (tag.equals("X10ArrayAccess1")) return new X10ArrayAccess1Lens().fromXML(e);
			if (tag.equals("X10ArrayAccess")) return new X10ArrayAccessLens().fromXML(e);
			if (tag.equals("ArrayAccessAssign")) return new ArrayAccessAssignLens().fromXML(e);
			if (tag.equals("ArrayConstructor")) return new ArrayConstructorLens().fromXML(e);
			if (tag.equals("Point")) return new PointLens().fromXML(e);
			if (tag.equals("RemoteCall")) return new RemoteCallLens().fromXML(e);
			if (tag.equals("Call")) return new CallLens().fromXML(e);
			if (tag.equals("ConstantDistMaker")) return new ConstantDistMakerLens().fromXML(e);
			if (tag.equals("New")) return new NewLens().fromXML(e);
			if (tag.equals("For")) return new ForLens().fromXML(e);
			if (tag.equals("Finish")) return new FinishLens().fromXML(e);
			if (tag.equals("DepParameterExpr")) return new DepParameterExprLens().fromXML(e);
			if (tag.equals("GenParameterExpr")) return new GenParameterExprLens().fromXML(e);
			if (tag.equals("X10ArrayTypeNode")) return new X10ArrayTypeNodeLens().fromXML(e);
			if (tag.equals("Assign")) return new AssignLens().fromXML(e);
			if (tag.equals("X10ArrayAccessAssign")) return new X10ArrayAccessAssignLens().fromXML(e);
			if (tag.equals("X10ArrayAccess1Assign")) return new X10ArrayAccess1AssignLens().fromXML(e);
			if (tag.equals("Binary")) return new BinaryLens().fromXML(e);
			if (tag.equals("Unary")) return new UnaryLens().fromXML(e);
			if (tag.equals("X10ArrayAccessUnary")) return new X10ArrayAccessUnaryLens().fromXML(e);
			if (tag.equals("X10ArrayAccess1Unary")) return new X10ArrayAccess1UnaryLens().fromXML(e);
			if (tag.equals("Tuple")) return new TupleLens().fromXML(e);
			if (tag.equals("Formal")) return new FormalLens().fromXML(e);
			if (tag.equals("Formal")) return new FormalLens().fromXML(e);
			if (tag.equals("ParExpr")) return new ParExprLens().fromXML(e);
			if (tag.equals("PlaceCast")) return new PlaceCastLens().fromXML(e);
			if (tag.equals("Field")) return new FieldLens().fromXML(e);
			if (tag.equals("FieldDecl")) return new FieldDeclLens().fromXML(e);
			if (tag.equals("Cast")) return new CastLens().fromXML(e);
			if (tag.equals("MethodDecl")) return new X10MethodDeclLens().fromXML(e);
			if (tag.equals("LocalDecl")) return new LocalDeclLens().fromXML(e);
			if (tag.equals("ConstructorDecl")) return new ConstructorDeclLens().fromXML(e);
			if (tag.equals("CanonicalTypeNode")) return new CanonicalTypeNodeLens().fromXML(e);
			if (tag.equals("PropertyDecl")) return new PropertyDeclLens().fromXML(e);
			if (tag.equals("Special")) return new SpecialLens().fromXML(e);
			if (tag.equals("Local")) return new LocalLens().fromXML(e);
			if (tag.equals("BooleanLit")) return new BooleanLitLens().fromXML(e);
			if (tag.equals("StmtSeq")) return new StmtSeqLens().fromXML(e);
			if (tag.equals("If")) return new IfLens().fromXML(e);
			if (tag.equals("RegionMaker")) return new RegionMakerLens().fromXML(e);
			if (tag.equals("RectRegionMaker")) return new RectRegionMakerLens().fromXML(e);
			if (tag.equals("While")) return new WhileLens().fromXML(e);
			if (tag.equals("IntLit")) return new IntLitLens().fromXML(e);
			if (tag.equals("StringLit")) return new StringLitLens().fromXML(e);
			if (tag.equals("FloatLit")) return new FloatLitLens().fromXML(e);
			if (tag.equals("CharLit")) return new CharLitLens().fromXML(e);
			if (tag.equals("AssignPropertyCall")) return new AssignPropertyCallLens().fromXML(e);
			if (tag.equals("Conditional")) return new ConditionalLens().fromXML(e);
			if (tag.equals("ConstructorCall")) return new ConstructorCallLens().fromXML(e);
			if (tag.equals("Closure")) return new ClosureLens().fromXML(e);
			if (tag.equals("ClosureCall")) return new ClosureCallLens().fromXML(e);
			if (tag.equals("AnnotationNode")) return new AnnotationNodeLens().fromXML(e);
			if (tag.equals("Id")) return new IdLens().fromXML(e);
			if (tag.equals("ArrayAccess")) return new ArrayAccessLens().fromXML(e);
			if (tag.equals("ArrayInit")) return new ArrayInitLens().fromXML(e);
			if (tag.equals("Assert")) return new AssertLens().fromXML(e);
			if (tag.equals("Assign")) return new AssignLens().fromXML(e);
			if (tag.equals("LocalAssign")) return new LocalAssignLens().fromXML(e);
			if (tag.equals("FieldAssign")) return new FieldAssignLens().fromXML(e);
			if (tag.equals("ArrayAccessAssign")) return new ArrayAccessAssignLens().fromXML(e);
			if (tag.equals("Binary")) return new BinaryLens().fromXML(e);
			if (tag.equals("Block")) return new BlockLens().fromXML(e);
			if (tag.equals("SwitchBlock")) return new SwitchBlockLens().fromXML(e);
			if (tag.equals("BooleanLit")) return new BooleanLitLens().fromXML(e);
			if (tag.equals("Branch")) return new BranchLens().fromXML(e);
			if (tag.equals("Call")) return new CallLens().fromXML(e);
			if (tag.equals("Case")) return new CaseLens().fromXML(e);
			if (tag.equals("Cast")) return new CastLens().fromXML(e);
			if (tag.equals("Catch")) return new CatchLens().fromXML(e);
			if (tag.equals("CharLit")) return new CharLitLens().fromXML(e);
			if (tag.equals("ClassBody")) return new ClassBodyLens().fromXML(e);
			if (tag.equals("ClassDecl")) return new ClassDeclLens().fromXML(e);
			if (tag.equals("ClassLit")) return new ClassLitLens().fromXML(e);
			if (tag.equals("Conditional")) return new ConditionalLens().fromXML(e);
			if (tag.equals("ConstructorCall")) return new ConstructorCallLens().fromXML(e);
			if (tag.equals("ConstructorDecl")) return new ConstructorDeclLens().fromXML(e);
			if (tag.equals("FieldDecl")) return new FieldDeclLens().fromXML(e);
			if (tag.equals("Do")) return new DoLens().fromXML(e);
			if (tag.equals("Empty")) return new EmptyLens().fromXML(e);
			if (tag.equals("Eval")) return new EvalLens().fromXML(e);
			if (tag.equals("Field")) return new FieldLens().fromXML(e);
			if (tag.equals("FloatLit")) return new FloatLitLens().fromXML(e);
			if (tag.equals("For")) return new ForLens().fromXML(e);
			if (tag.equals("Formal")) return new FormalLens().fromXML(e);
			if (tag.equals("If")) return new IfLens().fromXML(e);
			if (tag.equals("Import")) return new ImportLens().fromXML(e);
			if (tag.equals("Initializer")) return new InitializerLens().fromXML(e);
			if (tag.equals("Instanceof")) return new InstanceofLens().fromXML(e);
			if (tag.equals("IntLit")) return new IntLitLens().fromXML(e);
			if (tag.equals("Labeled")) return new LabeledLens().fromXML(e);
			if (tag.equals("Local")) return new LocalLens().fromXML(e);
			if (tag.equals("LocalClassDecl")) return new LocalClassDeclLens().fromXML(e);
			if (tag.equals("LocalDecl")) return new LocalDeclLens().fromXML(e);
			if (tag.equals("MethodDecl")) return new X10MethodDeclLens().fromXML(e);
			if (tag.equals("New")) return new NewLens().fromXML(e);
			if (tag.equals("NewArray")) return new NewArrayLens().fromXML(e);
			if (tag.equals("NullLit")) return new NullLitLens().fromXML(e);
			if (tag.equals("Return")) return new ReturnLens().fromXML(e);
			if (tag.equals("SourceCollection")) return new SourceCollectionLens().fromXML(e);
			if (tag.equals("SourceFile")) return new SourceFileLens().fromXML(e);
			if (tag.equals("Special")) return new SpecialLens().fromXML(e);
			if (tag.equals("StringLit")) return new StringLitLens().fromXML(e);
			if (tag.equals("Switch")) return new SwitchLens().fromXML(e);
			if (tag.equals("Synchronized")) return new SynchronizedLens().fromXML(e);
			if (tag.equals("Throw")) return new ThrowLens().fromXML(e);
			if (tag.equals("Try")) return new TryLens().fromXML(e);
			if (tag.equals("ArrayTypeNode")) return new ArrayTypeNodeLens().fromXML(e);
			if (tag.equals("CanonicalTypeNode")) return new CanonicalTypeNodeLens().fromXML(e);
			if (tag.equals("PackageNode")) return new PackageNodeLens().fromXML(e);
			if (tag.equals("Unary")) return new UnaryLens().fromXML(e);
			if (tag.equals("While")) return new WhileLens().fromXML(e);
			throw new InternalCompilerError("Could not construct node from tag " + tag + ".");
		}
	}

	public class LensFactory extends X10DelegatingVisitor {
		DomGenerator v;

		LensFactory(DomGenerator v) { this.v = v; }
		
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
//		public void visit(FieldDecl_c n) { new FieldDeclLens().toXML(v, n); }
		public void visit(X10FieldDecl_c n) { new FieldDeclLens().toXML(v, n); }
		public void visit(PropertyDecl_c n) { new PropertyDeclLens().toXML(v, n); }
//		public void visit(X10PropertyDecl_c n) { new PropertyDeclLens().toXML(v, n); }
		public void visit(Formal_c n) { new FormalLens().toXML(v, n); }
		public void visit(X10Formal_c n) { new FormalLens().toXML(v, n); }
		public void visit(Initializer_c n) { new InitializerLens().toXML(v, n); }
		public void visit(MethodDecl_c n) { new MethodDeclLens().toXML(v, n); }
		public void visit(X10MethodDecl_c n) { new X10MethodDeclLens().toXML(v, n); }
//		public void visit(AtomicMethodDecl_c n) { new AtomicMethodDeclLens().toXML(v, n); }
		public void visit(Block_c n) { new BlockLens().toXML(v, n); }
		public void visit(StmtSeq_c n) { new StmtSeqLens().toXML(v, n); }
		public void visit(SwitchBlock_c n) { new SwitchBlockLens().toXML(v, n); }
		public void visit(Assert_c n) { new AssertLens().toXML(v, n); }
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
