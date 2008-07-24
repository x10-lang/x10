package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.DerefTransform;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

public class FunctionTypeNode_c extends TypeNode_c implements FunctionTypeNode {

	List<TypeParamNode> typeParams;
	List<Formal> formals;
	DepParameterExpr whereClause;
	List<TypeNode> throwTypes;
	TypeNode returnType;

	public FunctionTypeNode_c(Position pos, List<TypeParamNode> typeParams, List<Formal> formals, TypeNode returnType, DepParameterExpr where,
			List<TypeNode> throwTypes) {
		super(pos);
		this.typeParams = TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
		this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
		this.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
		this.returnType = returnType;
		this.whereClause = where;
	}
	
	@Override
	public Node disambiguate(TypeChecker ar) throws SemanticException {
	    X10NodeFactory nf = (X10NodeFactory) ar.nodeFactory();
	    X10TypeSystem ts = (X10TypeSystem) ar.typeSystem();
	    FunctionTypeNode_c n = this;
	    List<Ref<? extends Type>> typeParams = new ArrayList<Ref<? extends Type>>(n.typeParameters().size());
	    for (TypeParamNode p : n.typeParameters()) {
		typeParams.add(p.typeRef());
	    }
	    List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>(n.formals().size());
	    for (Formal f : n.formals()) {
		formalTypes.add(f.type().typeRef());
	    }

	    List<Ref<? extends Type>> throwTypes = new ArrayList<Ref<? extends Type>>(n.throwTypes().size());
	    for (TypeNode tn : n.throwTypes()) {
		throwTypes.add(tn.typeRef());
	    }

	    Type t = ts.closure(position(), returnType.typeRef(), typeParams, formalTypes, whereClause != null ? whereClause.xconstraint() : null, throwTypes);
	    ((LazyRef<Type>) typeRef()).update(t);
	    return nf.CanonicalTypeNode(position(), t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see polyglot.ext.x10.ast.FunctionTypeNode#returnType()
	 */
	public TypeNode returnType() {
		return this.returnType;
	}

	/** Set the return type of the method. */
	public FunctionTypeNode returnType(TypeNode returnType) {
		FunctionTypeNode_c n = (FunctionTypeNode_c) copy();
		n.returnType = returnType;
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.FunctionTypeNode#typeParameters()
	 */
	public List<TypeParamNode> typeParameters() {
		return Collections.<TypeParamNode> unmodifiableList(this.typeParams);
	}

	/** Set the formals of the method. */
	public FunctionTypeNode typeParameters(List<TypeParamNode> typeParams) {
		FunctionTypeNode_c n = (FunctionTypeNode_c) copy();
		n.typeParams = TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.FunctionTypeNode#formals()
	 */
	public List<Formal> formals() {
		return Collections.<Formal> unmodifiableList(this.formals);
	}

	/** Set the formals of the method. */
	public FunctionTypeNode formals(List<Formal> formals) {
		FunctionTypeNode_c n = (FunctionTypeNode_c) copy();
		n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.FunctionTypeNode#whereClause()
	 */
	public DepParameterExpr whereClause() {
		return whereClause;
	}

	public FunctionTypeNode whereClause(DepParameterExpr where) {
		FunctionTypeNode_c n = (FunctionTypeNode_c) copy();
		this.whereClause = where;
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.FunctionTypeNode#throwTypes()
	 */
	public List<TypeNode> throwTypes() {
		return Collections.<TypeNode> unmodifiableList(this.throwTypes);
	}

	/** Set the exception types of the method. */
	public FunctionTypeNode throwTypes(List<TypeNode> throwTypes) {
		FunctionTypeNode_c n = (FunctionTypeNode_c) copy();
		n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
		return n;
	}

	/** Visit the children of the method. */
	public Node visitChildren(NodeVisitor v) {
		List<TypeParamNode> typeParams = this.visitList(this.typeParams, v);
		List<Formal> formals = this.visitList(this.formals, v);
		DepParameterExpr where = (DepParameterExpr) this.visitChild(this.whereClause, v);
		TypeNode returnType = (TypeNode) this.visitChild(this.returnType, v);
		List<TypeNode> throwTypes = this.visitList(this.throwTypes, v);
		return reconstruct(typeParams, formals, where, returnType, throwTypes);
	}

	protected Node reconstruct(List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr where, TypeNode returnType, List<TypeNode> throwTypes) {

		FunctionTypeNode_c n = this;
		n = (FunctionTypeNode_c) n.typeParameters(typeParams);
		n = (FunctionTypeNode_c) n.formals(formals);
		n = (FunctionTypeNode_c) n.whereClause(where);
		n = (FunctionTypeNode_c) n.returnType(returnType);
		n = (FunctionTypeNode_c) n.throwTypes(throwTypes);
		return n;
	}

	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    if (typeParams.size() > 0) {
		sb.append("[");
		String sep = "";
		for (TypeParamNode p : typeParams) {
		    sb.append(sep);
		    sb.append(p);
		    sep = ", ";
		}
		sb.append("]");
	    }
	    sb.append("(");
		String sep = "";
		for (Formal p : formals) {
		    sb.append(sep);
		    sb.append(p);
		    sep = ", ";
		}
	    sb.append(")");
	    if (whereClause != null)
		sb.append(whereClause);
	    sb.append(" => ");
	    sb.append(returnType);
	    return sb.toString();
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.begin(0);
		
		if (typeParams.size() > 0) {
			w.write("[");
		
			w.allowBreak(2, 2, "", 0);
			w.begin(0);
		
			for (Iterator<Formal> i = formals.iterator(); i.hasNext();) {
				Formal f = i.next();
		
				print(f, w, tr);
		
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
		
			w.end();
			w.write("]");
			w.allowBreak(2, 2, " ", 1);
		}
		
		w.write("(");
		
		w.allowBreak(2, 2, "", 0);
		w.begin(0);
		
		for (Iterator<Formal> i = formals.iterator(); i.hasNext();) {
			Formal f = i.next();
		
			print(f, w, tr);
		
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		
		w.end();
		w.write(")");
		w.allowBreak(2, 2, " ", 1);
		
		if (whereClause != null)
			print(whereClause, w, tr);
		
		if (!throwTypes().isEmpty()) {
			w.allowBreak(6);
			w.write("throws ");
		
			for (Iterator i = throwTypes().iterator(); i.hasNext();) {
				TypeNode tn = (TypeNode) i.next();
				print(tn, w, tr);
		
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(4, " ");
				}
			}
		}
		
		w.write(" => ");
		print(returnType, w, tr);
		
		w.end();
	}

}
