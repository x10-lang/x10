package dims.plugin;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Unary;
import polyglot.ext.x10.ast.AnnotationNode;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.ParExpr;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10Instanceof;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.plugin.SimpleTypeAnnotationPlugin;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeObject;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class DimensionTypePlugin extends SimpleTypeAnnotationPlugin {
	public DimensionTypePlugin() {
		super();
	}
	
	X10ClassType annotationNamed(X10TypeObject o, String name) throws SemanticException {
		X10ClassType baseType = (X10ClassType) o.typeSystem().systemResolver().find(name);
		List<X10ClassType> ats = o.annotationMatching(baseType);

		if (ats.size() > 1) {
			throw new SemanticException("Type has more than one " + name + " annotation.", o.position());
		}

		if (! ats.isEmpty()) {
			X10ClassType at = ats.get(0);
			return at;
		}
		
		return null;
	}

	X10ClassType annotationNamed(TypeSystem ts, Node o, String name) throws SemanticException {
		if (o.ext() instanceof X10Ext) {
			X10Ext ext = (X10Ext) o.ext();
			
			X10ClassType baseType = (X10ClassType) ts.systemResolver().find(name);
			List<X10ClassType> ats = ext.annotationMatching(baseType);
			
			if (ats.size() > 1) {
				throw new SemanticException("Expression has more than one " + name + " annotation.", o.position());
			}
			
			if (! ats.isEmpty()) {
				X10ClassType at = ats.get(0);
				return at;
			}
		}
		return null;
	}
		
	public X10Type unaryPromote(Unary u, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10Type etype = (X10Type) u.expr().type();
		X10ClassType edim = annotationNamed(etype, "dims.Unit");
		
		Expr x = propertyExpr(edim, 0);
		
		if (x != null) {
			X10Type t = (X10Type) u.type();
			if (u.operator() == Unary.POS || u.operator() == Unary.NEG) {
				t = setUnitAnnotation(t, x, nf);
				return t;
			}
		}
		
		return (X10Type) u.type();
	}
	
	public X10Type binaryPromote(Binary b, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10Type t = (X10Type) b.type();
		
		X10Type ltype = (X10Type) b.left().type();
		X10Type rtype = (X10Type) b.right().type();
		
		// Ignore string +.
		if (! ltype.isNumeric() || ! rtype.isNumeric())
			return t;
		
		X10ClassType ldim = annotationNamed(ltype, "dims.Unit");
		X10ClassType rdim = annotationNamed(rtype, "dims.Unit");
		
		Expr lx = propertyExpr(ldim, 0);
		Expr rx = propertyExpr(rdim, 0);
		
		if (lx == null && rx == null) {
			return t;
		}
		
		// These should have been checked before.
		assert lx == null || lx.type().isNumeric();
		assert rx == null || rx.type().isNumeric();
		
		if (lx == null || rx == null) {
			Expr x = lx != null ? lx : rx;
			String lu = lx == null ? "none" : lx.toString();
			String ru = rx == null ? "none" : rx.toString();
			String u = x.toString();
			
			if (b.operator() == Binary.ADD || b.operator() == Binary.SUB) {
				// 1m + 3
				throw new SemanticException("Cannot add or subtract values with incompatible units: " + lu + " and " + ru + ".");
			}
			
			if (b.operator() == Binary.MOD && rx != null) {
				// 7 % 2m
				throw new SemanticException("Cannot take remainder of value with unit " + u);
			}
			
			if (b.operator() == Binary.MUL) {
				// 2 * 4m = 8m
				t = setUnitAnnotation(t, x, nf);
				return t;
			}
			
			if ((b.operator() == Binary.DIV || b.operator() == Binary.MOD) && lx != null) {
				// 7m / 2 = 3.5m
				// 7m % 2 = 1m
				t = setUnitAnnotation(t, x, nf);
				return t;
			}
			
			if (b.operator() == Binary.DIV && rx != null) {
				// 2 / 3m == (2/3) (1/m)
				Expr e = nf.Binary(b.position(), one(nf, ts), Binary.DIV, rx);
				e = e.type(ts.promote(one(nf, ts).type(), rx.type()));
				t = setUnitAnnotation(t, e, nf);
				return t;
			}
			
			if (b.operator() == Binary.SHL ||
					b.operator() == Binary.SHR ||
					b.operator() == Binary.USHR) {
				
				if (rx != null)
					throw new SemanticException("Cannot shift using value with unit " + u);
				
				// Same as scalar multiplication.
				// 4m >> 2 = 2m
				
				t = setUnitAnnotation(t, lx, nf);
				return t;
			}
		}
		
		if (lx != null && rx != null) {
			Exp lu = getUnit(lx, ts, nf);
			Exp ru = getUnit(rx, ts, nf);
			
			if (b.operator() == Binary.SHL ||
					b.operator() == Binary.SHR ||
					b.operator() == Binary.USHR) {
				
				throw new SemanticException("Cannot shift value with units.");
			}
			
			if (b.operator() == Binary.EQ ||
					b.operator() == Binary.NE ||
					b.operator() == Binary.LT ||
					b.operator() == Binary.GT ||
					b.operator() == Binary.LE ||
					b.operator() == Binary.GE) {
				
				if (! lu.equals(ru))
					throw new SemanticException("Cannot compare values with incompatible units: " + lx + " and " + rx + ".");
			}
			
			if (b.operator() == Binary.MUL) {
				Exp u = mul(lu, ru, nf);
				Expr e = u.toExpr(nf, ts);
				t = setUnitAnnotation(t, e, nf);
				return t;
			}
			
			if (b.operator() == Binary.DIV) {
				Exp u = mul(lu, inv(ru, nf), nf);
				Expr e = u.toExpr(nf, ts);
				t = setUnitAnnotation(t, e, nf);
				return t;
			}
			
			if (b.operator() == Binary.ADD || b.operator() == Binary.SUB) {
				if (! lu.equals(ru))
					throw new SemanticException("Cannot add or subtract values with incompatible units: " + lx + " and " + rx + ".");
				
				t = setUnitAnnotation(t, lx, nf);
				return t;
			}
		}
		
		return t;
	}
	
	@Override
	public boolean checkImplicitCoercion(X10Type toType, X10Type fromType,
			X10Context context, X10TypeSystem ts, X10NodeFactory nf)
	throws SemanticException {
		X10Type lhsType = toType;
		
		X10ClassType toDim = annotationNamed(toType, "dims.Unit");
		X10ClassType fromDim = annotationNamed(fromType, "dims.Unit");
		
		// Can always strip units.
		if (toDim == null) {
			return true;
		}
		
		// Can always add units.
		if (fromDim == null) {
			return true;
		}
		
		Expr toX = toDim.propertyExprs().get(0);
		Expr fromX = fromDim.propertyExprs().get(0);
		
		Exp toM = getUnit(toX, ts, nf);
		Exp fromM = getUnit(fromX, ts, nf);
		
		if (! toM.equals(fromM)) {
			throw new SemanticException("Cannot coerce units " + fromX + " (= " + fromM + ") to " + toX + " (= " + toM + ").");
		}
		
		return true;
	}
	
	static abstract class Term implements Comparable<Term> { }
	
	static class Exp {
		// scale * terms + offset
		Expr scale;
		Expr offset;
		List<Term> terms;
		
		Exp(Expr scale, Expr offset) {
			this.scale = scale;
			this.offset = offset;
			this.terms = Collections.EMPTY_LIST;
		}
		
		public Expr toExpr(X10NodeFactory nf, X10TypeSystem ts) throws SemanticException {
			Expr e = scale;
			
			for (Iterator<Term> i = terms.iterator(); i.hasNext(); ) {
				Term t = i.next();
				if (t instanceof Unit) {
					Expr u = ((Unit) t).unit;
					if (isOne(e)) {
						e = u;
					}
					else {
						e = mul(e, u, ts, nf);
					}
				}
				else {
					Expr u = ((Inv) t).u.unit;
					Type ty = ts.promote(e.type(), u.type());
					e = nf.Binary(Position.compilerGenerated(), e, Binary.DIV, u).type(ty);
				}
			}
			
			if (! isZero(offset)) {
				e = add(e, offset, ts, nf);
			}
			
			return e;
		}
		
		Exp(Expr scale, Expr offset, Term t) {
			this.scale = scale;
			this.offset = offset;
			this.terms = new ArrayList<Term>(1);
			this.terms.add(t);
		}
		
		Exp(Exp e1, Exp e2, X10NodeFactory nf) {
			X10TypeSystem ts = (X10TypeSystem) e1.scale.type().typeSystem();
			
			// Make sure we don't multiply an offset with a unit
			assert isZero(e1.offset) || isZero(e2.scale) || e2.terms.isEmpty();
			assert isZero(e2.offset) || isZero(e1.scale) || e1.terms.isEmpty();
			
			// multiply the scales and offsets
			if (e1.isConstant() && e2.isConstant()) {
				// k = scale * 1 + offset
				Expr k1 = add(e1.scale, e1.offset, ts, nf);
				Expr k2 = add(e2.scale, e2.offset, ts, nf);
				this.scale = mul(k1, k2, ts, nf);
				this.offset = nf.IntLit(Position.compilerGenerated(), IntLit.INT, 0).type(ts.Int());
			}
			else if (e1.isConstant()) {
				// k = scale * 1 + offset
				Expr k = add(e1.scale, e1.offset, ts, nf);
				this.scale = mul(k, e2.scale, ts, nf);
				this.offset = mul(k, e2.offset, ts, nf);
			}
			else if (e2.isConstant()) {
				// k = scale * 1 + offset
				Expr k = add(e2.scale, e2.offset, ts, nf);
				this.scale = mul(k, e1.scale, ts, nf);
				this.offset = mul(k, e1.offset, ts, nf);
			}
			else {
				this.scale = mul(e1.scale, e2.scale, ts, nf);
				this.offset = mul(e1.offset, e2.offset, ts, nf);
			}
			
			List<Term> l = new ArrayList<Term>(e1.terms.size() + e2.terms.size());
			l.addAll(e1.terms);
			l.addAll(e2.terms);
			init(l);
		}
		
		Exp(Expr scale, Expr offset, List<Term> l) {
			this.scale = scale;
			this.offset = offset;
			init(l);
		}
		
		void init(List<Term> l) {
			this.terms = new ArrayList<Term>(l.size());
			
			Collections.<Term>sort(l);
			
			// Check if both ti and 1/ti are in the list.
			BitSet delete = new BitSet();
			
			for (int i = 0; i < l.size(); i++) {
				Term ti = l.get(i);
				if (ti instanceof Unit) {
					for (int j = i+1; j < l.size(); j++) {
						Term tj = l.get(j);
						if (tj instanceof Inv) {
							Inv v = (Inv) tj;
							if (ti.equals(v.u)) {
								delete.set(i);
								delete.set(j);
								break;
							}
						}
					}
				}
			}
			
			for (int i = 0; i < l.size(); i++) {
				Term ti = l.get(i);
				if (delete.get(i))
					continue;
				this.terms.add(ti);
			}
		}
		
		
		public boolean equals(Object o) {
			return o instanceof Exp &&
			((Exp) o).scale.constantValue().equals(this.scale.constantValue()) &&
			((Exp) o).offset.constantValue().equals(this.offset.constantValue()) &&
			((Exp) o).terms.equals(this.terms);
		}
		
		public String toString() {
			String s = "";
			if (! isOne(scale)) {
				s = "" + scale;
				if (! terms.isEmpty())
					s += " * ";
			}
			for (Iterator<Term> i = terms.iterator(); i.hasNext(); ) {
				Term t = i.next();
				s += t;
				if (i.hasNext())
					s += " * ";
			}
			if (! isZero(offset))
				s += " + " + offset;
			return s;
		}
		
		public boolean isConstant() {
			return terms.isEmpty();
		}
	}
	
	public static Expr neg(Expr e, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			if (u.operator() == Unary.POS) {
				return neg(u.expr(), ts, nf);
			}
			if (u.operator() == Unary.NEG) {
				return u.expr();
			}
		}
		if (e instanceof FloatLit) {
			FloatLit x = (FloatLit) e;
			return x.value(-x.value());
		}
		if (e instanceof IntLit) {
			IntLit x = (IntLit) e;
			return x.value(-x.value());
		}
		return nf.Unary(Position.compilerGenerated(), Unary.NEG, e).type(ts.promote(e.type()));
	}
	
	public static Expr add(Expr e1, Expr e2, X10TypeSystem ts, X10NodeFactory nf) {
		if (isZero(e1)) return e2;
		if (isZero(e2)) return e1;
		
		Binary.Operator op = Binary.ADD;
		if (e2 instanceof Unary && ((Unary) e2).operator() == Unary.NEG) {
			op = Binary.SUB;
			e2 = ((Unary) e2).expr();
		}
		
		try {
			return nf.Binary(Position.compilerGenerated(), e1, op, e2).type(ts.promote(e1.type(), e2.type()));
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e);
		}
	}
	
	public static Expr mul(Expr e1, Expr e2, X10TypeSystem ts, X10NodeFactory nf) {
		if (isZero(e1) || isOne(e2)) return e1;
		if (isZero(e2) || isOne(e1)) return e2;
		
		try {
			return nf.Binary(Position.compilerGenerated(), e1, Binary.MUL, e2).type(ts.promote(e1.type(), e2.type()));
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e);
		}
	}
	
	static class Unit extends Term {
		Field unit;
		
		public Unit(Field unit) {
			this.unit = unit;
		}
		
		public boolean equals(Object o) {
			return o instanceof Unit && unit.fieldInstance().orig().equals(((Unit) o).unit.fieldInstance().orig());
		}
		
		public String toString() {
			return unit.toString();
		}
		
		public int compareTo(Term o) {
			if (o instanceof Inv) return -1;
			if (o instanceof Unit) return unit.fieldInstance().orig().toString().compareTo(((Unit) o).unit.fieldInstance().orig().toString());
			return 0;
		}
	}
	
	static class Inv extends Term {
		Unit u;
		
		public Inv(Unit u) {
			this.u = u;
		}
		
		public boolean equals(Object o) {
			return o instanceof Inv && u.equals(((Inv) o).u);
		}
		
		public String toString() {
			return "(1/" + u + ")";
		}
		
		public int compareTo(Term o) {
			if (o instanceof Unit) return 1;
			if (o instanceof Inv) return u.compareTo(((Inv) o).u);
			return 0;
		}
	}
	
	private Exp getUnit(Expr e, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		Exp u = getUnit2(e, ts, nf);
		System.out.println("[[" + e + "]] = " + u);
		return u;
	}
	
	private Exp getUnit2(Expr e, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		if (e == null) {
			// No Unit annotation.  Dimensionless!
			return new Exp(one(nf, ts), zero(nf, ts));
		}
		if (e instanceof FloatLit) {
			// @Unit(1.0).  Dimensionless!
			FloatLit x = (FloatLit) e;
			return new Exp(x, zero(nf, ts));
		}
		if (e instanceof IntLit) {
			// @Unit(1).  Dimensionless!
			IntLit x = (IntLit) e;
			return new Exp(x, zero(nf, ts));
		}
		if (e instanceof Field) {
			Field f = (Field) e;
			X10FieldInstance fi = (X10FieldInstance) f.fieldInstance();
			
			if (! fi.isConstant())
				throw new SemanticException("Bad unit " + e + "; not constant");
			Object o = fi.constantValue();
			if (! (fi.constantValue() instanceof Number))
				throw new SemanticException("Unit declaration " + fi + " cannot be a non-numeric constant");
			
			X10ClassType baseUnit = annotationNamed(fi, "dims.BaseUnit");
			X10ClassType derivedUnit = annotationNamed(fi, "dims.DerivedUnit");
			
			if (derivedUnit != null && baseUnit != null)
				throw new SemanticException("Unit declaration " + fi + " cannot be both a base and derived unit.");
			
			// No unit annotation on the field; must be a constant.
			if (derivedUnit == null && baseUnit == null) {
				X10ClassType fUnit = annotationNamed(ts, f, "dims.Unit");
				if (fUnit == null) {
					return new Exp(f, zero(nf, ts));
				}
				else {
					// A constant with units, e.g., 9.8 m / (s*s)
					Expr x = propertyExpr(fUnit, 0);
					Exp k = new Exp(f, zero(nf, ts));
					Exp u = getUnit(x, ts, nf);
					return mul(k, u, nf);
				}
			}
			
			if (baseUnit != null) {
				return new Exp(one(nf, ts), zero(nf, ts), new Unit(f));
			}
			
			if (derivedUnit != null) {
				if (derivedUnit.propertyExprs().isEmpty()) {
					// a dimensionless unit
					return new Exp(one(nf, ts), zero(nf, ts));
				}
				else {
					// derived
					Expr x = (Expr) propertyExpr(derivedUnit, 0);
					Exp u = getUnit(x, ts, nf);
					return u;
				}
			}
		}
		if (e instanceof ParExpr) {
			ParExpr p = (ParExpr) e;
			return getUnit(p.expr(), ts, nf);
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			if (u.operator() == Unary.NEG) {
				return sub(new Exp(one(nf, ts), zero(nf, ts)),
						getUnit(u.expr(), ts, nf), nf);
			}
			if (u.operator() == Unary.POS) {
				return getUnit(u.expr(), ts, nf);
			}
		}
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			Exp u1 = getUnit(b.left(), ts, nf);
			Exp u2 = getUnit(b.right(), ts, nf);
			Exp u = null;
			if (b.operator() == Binary.ADD) {
				u = add(u1, u2, nf);
			}
			if (b.operator() == Binary.SUB) {
				u = sub(u1, u2, nf);
			}
			if (b.operator() == Binary.MUL) {
				u = mul(u1, u2, nf);
			}
			if (b.operator() == Binary.DIV) {
				u = mul(u1, inv(u2, nf), nf);
			}
			if (u != null) {
				return u;
			}
		}
		throw new SemanticException("Invalid unit operation " + e);
	}
	
	private Exp inv(Exp e, X10NodeFactory nf) throws SemanticException {
		if (! isZero(e.offset)) {
			throw new SemanticException("Cannot invert unit with offset.");
		}
		
		X10TypeSystem ts = (X10TypeSystem) e.scale.type().typeSystem();
		
		// flip the terms
		List<Term> l = new ArrayList<Term>(e.terms.size());
		for (Iterator<Term> i = e.terms.iterator(); i.hasNext(); ) {
			Term ti = i.next();
			if (ti instanceof Unit)
				l.add(new Inv((Unit) ti));
			if (ti instanceof Inv)
				l.add(((Inv) ti).u);
		}
		if (isOne(e.scale)) {
			return new Exp(e.scale, zero(nf, ts), l);
		}
		else if (e.scale instanceof IntLit) {
			// flip the scale
			IntLit n = (IntLit) e.scale;
			Expr scale = nf.FloatLit(Position.compilerGenerated(), FloatLit.DOUBLE, 1.0 / n.value()).type(ts.Double());
			return new Exp(scale, zero(nf, ts), l);
		}
		else if (e.scale instanceof FloatLit) {
			// flip the scale
			FloatLit n = (FloatLit) e.scale;
			Expr scale = nf.FloatLit(Position.compilerGenerated(), FloatLit.DOUBLE, 1.0 / n.value()).type(ts.Double());
			return new Exp(scale, zero(nf, ts), l);
		}
		else {
			// flip the scale
			Expr scale = nf.Binary(Position.compilerGenerated(), one(nf, ts), Binary.DIV, e.scale).type(e.scale.type());
			return new Exp(scale, zero(nf, ts), l);
		}
	}
	
	private static boolean is(Expr e, int v) {
		if (e instanceof IntLit) {
			IntLit n = (IntLit) e;
			return n.value() == v;
		}
		
		if (e instanceof FloatLit) {
			FloatLit n = (FloatLit) e;
			return n.value() == v;
		}
		
		return false;
	}
	
	private static boolean isZero(Expr e) {
		return is(e, 0);
	}
	
	private static boolean isOne(Expr e) {
		return is(e, 1);
	}
	
	private Expr zero;
	private Expr one;
	
	private Expr zero(X10NodeFactory nf, X10TypeSystem ts) {
		if (zero == null) {
			zero = nf.IntLit(Position.compilerGenerated(), IntLit.INT, 0).type(ts.Int());
		}
		return zero;
	}
	
	private Expr one(X10NodeFactory nf, X10TypeSystem ts) {
		if (one == null) {
			one = nf.IntLit(Position.compilerGenerated(), IntLit.INT, 1).type(ts.Int());
		}
		return one;
	}
	
	private Exp add(Exp u1, Exp u2, X10NodeFactory nf, boolean neg) throws SemanticException {
		// (a1 x + b1) + (a2 x + b2)
		// =
		// (a1+a2) x + (b1+b2)
		
		assert u1.terms.isEmpty() || u2.terms.isEmpty() || u1.terms.equals(u2.terms);
		
		X10TypeSystem ts = (X10TypeSystem) u1.offset.type().typeSystem();
		
		Expr off1;
		Expr off2;
		Expr scale1;
		Expr scale2;
		
		if (u1.terms.isEmpty()) {
			off1 = add(u1.scale, u1.offset, ts, nf);
			scale1 = zero(nf, ts);
		}
		else {
			off1 = u1.offset;
			scale1 = u1.scale;
		}
		
		if (u2.terms.isEmpty()) {
			off2 = add(u2.scale, u2.offset, ts, nf);
			scale2 = zero(nf, ts);
		}
		else {
			off2 = u2.offset;
			scale2 = u2.scale;
		}
		
		if (neg) {
			scale2 = neg(scale2, ts, nf);
			off2 = neg(off2, ts, nf);
		}
		
		Expr scale = add(scale1, scale2, ts, nf);
		Expr offset = add(off1, off2, ts, nf);
		List<Term> terms = u1.terms.isEmpty() ? u2.terms : u1.terms;
		
		return new Exp(scale, offset, terms);
	}
	
	private Exp add(Exp u1, Exp u2, X10NodeFactory nf) throws SemanticException {
		return add(u1, u2, nf, false);
	}
	
	private Exp sub(Exp u1, Exp u2, X10NodeFactory nf) throws SemanticException {
		return add(u1, u2, nf, true);
	}
	
	private Exp mul(Exp u1, Exp u2, X10NodeFactory nf) throws SemanticException {
		return new Exp(u1, u2, nf);
	}
	
	private X10Type setUnitAnnotation(X10Type t, Expr x, X10NodeFactory nf) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
		X10ClassType Unit = (X10ClassType) ts.systemResolver().find("dims.Unit");
		List<Expr> args = Collections.singletonList(x);
		X10ClassType depUnit = ts.instantiateType(Unit, args, nf);
		return (X10Type) ts.addAnnotation(t, depUnit, true);
	}
	
	public Expr propertyExpr(X10ClassType ct, int i) {
		if (ct == null)
			return null;
		return ct.propertyExpr(i);
	}

	protected Expr annotationCast(Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		Expr e2 = annotationCast2(e, context, ts, nf);
		if (e == e2)
			return e2;
		if (e != e2 && ! ts.typeBaseEquals(e.type(), e2.type())) {
			X10Type t = (X10Type) e.type();
			t = (X10Type) t.annotations(((X10Type) e2.type()).annotations());
			t = (X10Type) t.dep(t.dep());
			e2 = nf.Cast(e.position(), nf.CanonicalTypeNode(t.position(), t), e2);
			e2 = e2.type(t);
		}
		return e2;
	}
	
	protected Expr annotationCast2(Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		if (e == null || ! (e.ext() instanceof X10Ext)) {
			return e;
		}
		
		X10ClassType toDim = annotationNamed(ts, e, "dims.Unit");
		X10ClassType fromDim = annotationNamed((X10Type) e.type(), "dims.Unit");
		
		Expr toX = propertyExpr(toDim, 0);
		Expr fromX = propertyExpr(fromDim, 0);
		
		if (toX == null)
			return e;
		
		// just copy the to-annotation into the type
		X10Type t = (X10Type) e.type();
		t = setUnitAnnotation(t, toX, nf);
		
		
		if (fromX != null) {
			if (! getUnit(fromX, ts, nf).terms.equals(getUnit(toX, ts, nf).terms)) {
				throw new SemanticException("Cannot cast from " + fromX + " to incompatible unit " + toX + ".");
			}
			
			Expr invert = invert(e, fromX, ts, nf);
			Expr unopt = apply(invert, toX, ts, nf);
			
//			System.out.println("coercing " + e + " to " + unopt);
			
			e = unopt;
			
			Type Unit = (Type) ts.systemResolver().find("dims.Unit");
			
			// Remove the expression annotation so rerunning the plugin doesn't cause us to reapply the coercion
			List<AnnotationNode> l = new ArrayList<AnnotationNode>(((X10Ext) e.ext()).annotations());
			for (Iterator<AnnotationNode> i = l.iterator(); i.hasNext(); ) {
				AnnotationNode an = i.next();
				if (an.annotationInterface().isSubtype(Unit)) {
					i.remove();
				}
			}
			e = (Expr) ((X10Ext) e.ext()).annotations(l);
		}
		
		return e.type(t);
	}
	
	private Expr optimize(Expr e, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return e;
	}
	
	private Expr apply(Expr e, Expr x, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		Exp u = getUnit(x, ts, nf);
		Expr e1;
		Expr e2;
		if (isOne(u.scale)) {
			e1 = e;
		}
		else {
			e1 = mul(e, u.scale, ts, nf);
		}
		if (isZero(u.offset)) {
			e2 = e1;
		}
		else {
			e2 = add(e1, u.offset, ts, nf);
		}
		return e2;
	}
	
	private Expr invert(Expr e, Expr x, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		Exp u = getUnit(x, ts, nf);
		Type t1 = ts.promote(e.type(), u.offset.type());
		Type t2 = ts.promote(t1, u.scale.type());
		Expr e1;
		Expr e2;
		if (isZero(u.offset)) {
			e1 = e;
		}
		else {
			e1 = nf.Binary(Position.compilerGenerated(), e, Binary.SUB, u.offset).type(t1);
		}
		if (isOne(u.scale)) {
			e2 = e1;
		}
		else {
			e2 = nf.Binary(Position.compilerGenerated(), e1, Binary.DIV, u.scale).type(t2);
		}
		return e2;
	}
	
	protected Expr rewriteCast(X10Cast e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return e;
	}
	
	protected Expr rewriteInstanceof(X10Instanceof e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return e;
	}
}
