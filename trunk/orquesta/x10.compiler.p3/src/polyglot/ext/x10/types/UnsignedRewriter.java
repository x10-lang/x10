package polyglot.ext.x10.types;

import polyglot.ast.Binary;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.visit.NodeVisitor;

public class UnsignedRewriter extends NodeVisitor {
    X10TypeSystem ts;
    X10NodeFactory nf;
    
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
	if (n instanceof TypeNode) {
	    TypeNode tn = (TypeNode) n;
	    Type t = tn.type();
	    if (ts.isSubtype(t, ts.UByte())) {
		return nf.CanonicalTypeNode(tn.position(), Types.ref(ts.Byte()));
	    }
	    if (ts.isSubtype(t, ts.UShort())) {
		return nf.CanonicalTypeNode(tn.position(), Types.ref(ts.Short()));
	    }
	    if (ts.isSubtype(t, ts.UInt())) {
		return nf.CanonicalTypeNode(tn.position(), Types.ref(ts.Int()));
	    }
	    if (ts.isSubtype(t, ts.ULong())) {
		return nf.CanonicalTypeNode(tn.position(), Types.ref(ts.Long()));
	    }
	}

	if (n instanceof Cast) {
	    Cast c = (Cast) n;
	    Type t = c.type();
	    Type etype = c.expr().type();
	    if (etype.isNumeric() && t.isNumeric())
		return convert(n, c.expr(), etype, t);
	}

	if (n instanceof Binary) {
	    Binary b = (Binary) n;
	    Expr l = b.left();
	    Expr r = b.right();
	    if (b.operator() == Binary.LT || b.operator() == Binary.GT || b.operator() == Binary.LE || b.operator() == Binary.GE) {
		if (ts.isSubtype(l.type(), ts.UByte()))
		    return b.left(add(l, Byte.MIN_VALUE)).right(add(r, Byte.MIN_VALUE));
		if (ts.isSubtype(l.type(), ts.UShort()))
		    return b.left(add(l, Short.MIN_VALUE)).right(add(r, Short.MIN_VALUE));
		if (ts.isSubtype(l.type(), ts.UInt()))
		    return b.left(add(l, Integer.MIN_VALUE)).right(add(r, Integer.MIN_VALUE));
		if (ts.isSubtype(l.type(), ts.ULong()))
		    return b.left(add(l, Long.MIN_VALUE)).right(add(r, Long.MIN_VALUE));
	    }
	}

	return n;
    }
    
    public Expr add(Expr l, byte b) {
	return nf.Binary(l.position(), l, Binary.ADD, nf.IntLit(l.position(), IntLit.INT, b));
    }
    public Expr add(Expr l, short b) {
	return nf.Binary(l.position(), l, Binary.ADD, nf.IntLit(l.position(), IntLit.INT, b));
    }
    public Expr add(Expr l, int b) {
	return nf.Binary(l.position(), l, Binary.ADD, nf.IntLit(l.position(), IntLit.INT, b));
    }
    public Expr add(Expr l, long b) {
	return nf.Binary(l.position(), l, Binary.ADD, nf.IntLit(l.position(), IntLit.LONG, b));
    }

    private Node convert(Node n, Expr e, Type fromType, Type toType) {
	if (ts.isSubtype(fromType, ts.Byte()) && ts.isSubtype(toType, ts.Byte()))
	    return e;
	if (ts.isSubtype(fromType, ts.UByte()) && ts.isSubtype(toType, ts.Byte()))
	    return e;
	if (ts.isSubtype(fromType, ts.UByte()) && ts.isSubtype(toType, ts.UByte()))
	    return e;
	if (ts.isSubtype(fromType, ts.Byte()) && ts.isSubtype(toType, ts.UByte()))
	    return e;

	if (ts.isSubtype(fromType, ts.Short()) && ts.isSubtype(toType, ts.Short()))
	    return e;
	if (ts.isSubtype(fromType, ts.UShort()) && ts.isSubtype(toType, ts.Short()))
	    return e;
	if (ts.isSubtype(fromType, ts.UShort()) && ts.isSubtype(toType, ts.UShort()))
	    return e;
	if (ts.isSubtype(fromType, ts.Short()) && ts.isSubtype(toType, ts.UShort()))
	    return e;
	
	if (ts.isSubtype(fromType, ts.Int()) && ts.isSubtype(toType, ts.Int()))
	    return e;
	if (ts.isSubtype(fromType, ts.UInt()) && ts.isSubtype(toType, ts.Int()))
	    return e;
	if (ts.isSubtype(fromType, ts.UInt()) && ts.isSubtype(toType, ts.UInt()))
	    return e;
	if (ts.isSubtype(fromType, ts.Int()) && ts.isSubtype(toType, ts.UInt()))
	    return e;
	
	if (ts.isSubtype(fromType, ts.Long()) && ts.isSubtype(toType, ts.Long()))
	    return e;
	if (ts.isSubtype(fromType, ts.ULong()) && ts.isSubtype(toType, ts.Long()))
	    return e;
	if (ts.isSubtype(fromType, ts.ULong()) && ts.isSubtype(toType, ts.ULong()))
	    return e;
	if (ts.isSubtype(fromType, ts.Long()) && ts.isSubtype(toType, ts.ULong()))
	    return e;

	// do nothing
	return n;
    }
}
